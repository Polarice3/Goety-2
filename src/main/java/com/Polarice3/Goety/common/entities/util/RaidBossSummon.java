package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.client.particles.TeleportInShockwaveParticleOption;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.hostile.illagers.HostileRedstoneMonstrosity;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import java.util.List;

public class RaidBossSummon extends Raider {
    private static final EntityDataAccessor<Boolean> NEARBY_ILLAGERS = SynchedEntityData.defineId(RaidBossSummon.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(RaidBossSummon.class, EntityDataSerializers.BOOLEAN);
    private int lifeTick = 0;

    public RaidBossSummon(EntityType<? extends Raider> p_37839_, Level p_37840_) {
        super(p_37839_, p_37840_);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(NEARBY_ILLAGERS, false);
        this.entityData.define(ACTIVE, false);
    }

    public void addAdditionalSaveData(CompoundTag p_213281_1_) {
        super.addAdditionalSaveData(p_213281_1_);
        if (this.isActive()) {
            p_213281_1_.putBoolean("active", true);
        }
        p_213281_1_.putInt("LifeTick", this.lifeTick);
    }

    public void readAdditionalSaveData(CompoundTag p_70037_1_) {
        super.readAdditionalSaveData(p_70037_1_);
        this.setActive(p_70037_1_.getBoolean("active"));
        this.lifeTick = p_70037_1_.getInt("LifeTick");
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public boolean isInvisibleTo(Player p_20178_) {
        return true;
    }

    @Override
    public boolean canBeSeenByAnyone() {
        return false;
    }

    @Override
    public boolean canBeLeader() {
        return false;
    }

    @Override
    public void push(Entity p_21294_) {
    }

    @Override
    protected void doPush(Entity p_20971_) {
    }

    @Override
    public boolean canCollideWith(Entity p_20303_) {
        return false;
    }

    public boolean isActive() {
        return this.entityData.get(ACTIVE);
    }

    public void setActive(boolean active) {
        this.entityData.set(ACTIVE, active);
    }

    public boolean areIllagersNearby() {
        return this.entityData.get(NEARBY_ILLAGERS) && !this.isActive();
    }

    public void setIllagersNearby(boolean illagersNearby) {
        this.entityData.set(NEARBY_ILLAGERS, illagersNearby);
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_) {

    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EMPTY;
    }

    public void tick() {
        this.baseTick();

        this.setDeltaMovement(Vec3.ZERO);

        if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        }

        List<Raider> list = this.level.getEntitiesOfClass(Raider.class, this.getBoundingBox().inflate(100.0),
                (raider) -> raider.hasActiveRaid()
                        && !(raider instanceof RaidBossSummon)
                        && !raider.getType().is(Tags.EntityTypes.BOSSES)
                        && !raider.getType().is(ModTags.EntityTypes.RAID_BOSS));
        if (this.hasActiveRaid()) {
            if (!this.level.isClientSide) {
                this.setIllagersNearby(!list.isEmpty());

                if (!this.isActive()) {
                    if (this.tickCount % 20 == 0) {
                        if (!this.areIllagersNearby()) {
                            Player player = this.level.getNearestPlayer(this, 100.0D);
                            if (player != null) {
                                BlockPos blockPos = BlockFinder.SummonRadius(player.blockPosition(), this, this.level, 16);
                                this.moveTo(Vec3.atBottomCenterOf(blockPos));
                            }
                            MobUtil.moveDownToGround(this);
                            this.setActive(true);
                        }
                    }
                } else {
                    if (this.level instanceof ServerLevel serverWorld) {
                        ++this.lifeTick;
                        HostileRedstoneMonstrosity redstoneMonstrosity = null;
                        if (this.lifeTick == 1) {
                            Player player = this.level.getNearestPlayer(this, 100.0D);
                            if (player != null) {
                                redstoneMonstrosity = new HostileRedstoneMonstrosity(ModEntityType.HOSTILE_REDSTONE_MONSTROSITY.get(), this.level);
                                BlockPos blockPos = BlockFinder.SummonRadius(player.blockPosition(), redstoneMonstrosity, this.level, 16);
                                this.moveTo(Vec3.atBottomCenterOf(blockPos));
                            }
                            MobUtil.moveDownToGround(this);
                            this.playSound(ModSounds.BOSS_SUMMON.get(), 16.0F, 1.0F);
                        }
                        if (this.lifeTick % 5 == 0 && this.lifeTick < 50){
                            serverWorld.sendParticles(new TeleportInShockwaveParticleOption(8, 2), this.getX(), this.getY() + 0.25F, this.getZ(), 0, 0, 0, 0, 0.5F);
                        }
                        if (this.lifeTick == 50) {
                            if (this.getCurrentRaid() != null) {
                                float f = 1.5F;
                                float f5 = (float) Math.PI * f * f;
                                for (int j1 = 0; j1 < 16; ++j1) {
                                    for (int k1 = 0; (float) k1 < f5; ++k1) {
                                        float f6 = this.random.nextFloat() * ((float) Math.PI * 2F);
                                        float f7 = Mth.sqrt(this.random.nextFloat()) * f;
                                        float f8 = Mth.cos(f6) * f7;
                                        float f9 = Mth.sin(f6) * f7;
                                        serverWorld.sendParticles(ParticleTypes.REVERSE_PORTAL, this.getX() + (double) f8, this.getY(), this.getZ() + (double) f9, 0, 0, 0.5D, 0, 0.5F);
                                    }
                                }
                                if (redstoneMonstrosity == null) {
                                    redstoneMonstrosity = new HostileRedstoneMonstrosity(ModEntityType.HOSTILE_REDSTONE_MONSTROSITY.get(), this.level);
                                }
                                redstoneMonstrosity.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                                redstoneMonstrosity.setCanJoinRaid(true);
                                redstoneMonstrosity.setCurrentRaid(this.getCurrentRaid());
                                redstoneMonstrosity.setWave(this.getCurrentRaid().getGroupsSpawned());
                                redstoneMonstrosity.setTicksOutsideRaid(0);
                                redstoneMonstrosity.setPos(this.position());
                                this.getCurrentRaid().addWaveMob(this.getCurrentRaid().getGroupsSpawned(), redstoneMonstrosity, true);
                                if (serverWorld.addFreshEntity(redstoneMonstrosity)) {
                                    this.getCurrentRaid().removeFromRaid(this, true);
                                    this.discard();
                                }
                            }
                        }
                        if (this.lifeTick >= 55) {
                            this.discard();
                        }
                    }
                }
            }
        }

        if (this.hasActiveRaid() && this.getCurrentRaid() != null && this.getCurrentRaid().isOver()) {
            this.getCurrentRaid().removeFromRaid(this, true);
            if (!this.level.isClientSide) {
                this.discard();
            }
        }
        if (this.getCurrentRaid() == null) {
            if (this.tickCount % 100 == 0) {
                if (!this.level.isClientSide) {
                    this.discard();
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource p_37849_, float p_37850_) {
        return p_37849_.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }
}
