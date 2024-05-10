package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.google.common.collect.Sets;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class SpellLightningBolt extends LightningBolt {
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(SpellLightningBolt.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> OWNER_CLIENT_ID = SynchedEntityData.defineId(SpellLightningBolt.class, EntityDataSerializers.INT);
    private static final int START_LIFE = 2;
    private static final double DAMAGE_RADIUS = 3.0D;
    private static final double DETECTION_RADIUS = 15.0D;
    public int life;
    public long seed;
    public int flashes;
    public boolean visualOnly;
    public boolean setFire;
    public final Set<Entity> hitEntities = Sets.newHashSet();
    public int blocksSetOnFire;
    public float damage = 5.0F;

    public SpellLightningBolt(EntityType<? extends LightningBolt> p_20865_, Level p_20866_) {
        super(p_20865_, p_20866_);
        this.noCulling = true;
        this.life = 2;
        this.seed = this.random.nextLong();
        this.flashes = this.random.nextInt(3) + 1;
    }

    protected void defineSynchedData() {
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
        this.entityData.define(OWNER_CLIENT_ID, -1);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        UUID uuid;
        if (compound.hasUUID("Owner")) {
            uuid = compound.getUUID("Owner");
        } else {
            String s = compound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.setOwnerId(uuid);
            } catch (Throwable ignored) {
            }
        }

        if (compound.contains("OwnerClient")){
            this.setOwnerClientId(compound.getInt("OwnerClient"));
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        if (this.getOwnerId() != null) {
            compound.putUUID("Owner", this.getOwnerId());
        }
        if (this.getOwnerClientId() > -1) {
            compound.putInt("OwnerClient", this.getOwnerClientId());
        }
    }

    @Nullable
    public LivingEntity getOwner() {
        if (!this.level.isClientSide){
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
        } else {
            return this.level.getEntity(this.getOwnerClientId()) instanceof LivingEntity living ? living : null;
        }
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.getOwnerId();
    }

    @Nullable
    public UUID getOwnerId() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse((UUID)null);
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    public int getOwnerClientId(){
        return this.entityData.get(OWNER_CLIENT_ID);
    }

    public void setOwnerClientId(int id){
        this.entityData.set(OWNER_CLIENT_ID, id);
    }

    public void setVisualOnly(boolean p_20875_) {
        this.visualOnly = p_20875_;
    }

    public void setFire(boolean fire){
        this.setFire = fire;
    }

    public boolean isSetFire() {
        return this.setFire;
    }

    @Nullable
    public ServerPlayer getCause() {
        if (this.getOwner() instanceof ServerPlayer serverPlayer){
            return serverPlayer;
        }
        return null;
    }

    public void setCause(@Nullable ServerPlayer p_20880_) {
        this.setOwner(p_20880_);
    }

    public void setOwner(LivingEntity livingEntity){
        if (livingEntity != null) {
            this.setOwnerId(livingEntity.getUUID());
            this.setOwnerClientId(livingEntity.getId());
        }
    }

    private void powerLightningRod() {
        BlockPos blockpos = this.getStrikePosition();
        BlockState blockstate = this.level().getBlockState(blockpos);
        if (blockstate.is(Blocks.LIGHTNING_ROD)) {
            ((LightningRodBlock)blockstate.getBlock()).onLightningStrike(blockstate, this.level(), blockpos);
        }

    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return this.damage;
    }

    public void tick() {
        this.baseTick();
        if (this.life == 2) {
            if (this.level().isClientSide()) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F, false);
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F, false);
            } else {
                Difficulty difficulty = this.level().getDifficulty();
                if (difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD) {
                    this.spawnFire(4);
                }

                this.powerLightningRod();
                clearCopperOnLightningStrike(this.level(), this.getStrikePosition());
                this.gameEvent(GameEvent.LIGHTNING_STRIKE);
            }
        }

        --this.life;
        if (this.life < 0) {
            if (this.flashes == 0) {
                if (this.level() instanceof ServerLevel) {
                    List<Entity> list = this.level().getEntities(this, new AABB(this.getX() - 15.0D, this.getY() - 15.0D, this.getZ() - 15.0D, this.getX() + 15.0D, this.getY() + 6.0D + 15.0D, this.getZ() + 15.0D), (p_147140_) -> p_147140_.isAlive() && !this.hitEntities.contains(p_147140_));

                    for(ServerPlayer serverplayer : ((ServerLevel)this.level()).getPlayers((p_147157_) -> p_147157_.distanceTo(this) < 256.0F)) {
                        CriteriaTriggers.LIGHTNING_STRIKE.trigger(serverplayer, this, list);
                    }
                }

                this.discard();
            } else if (this.life < -this.random.nextInt(10)) {
                --this.flashes;
                this.life = 1;
                this.seed = this.random.nextLong();
                this.spawnFire(0);
            }
        }

        if (this.life >= 0) {
            if (!(this.level() instanceof ServerLevel)) {
                this.level().setSkyFlashTime(2);
            } else if (!this.visualOnly) {
                List<Entity> list1 = this.level().getEntities(this, new AABB(this.getX() - 3.0D, this.getY() - 3.0D, this.getZ() - 3.0D, this.getX() + 3.0D, this.getY() + 6.0D + 3.0D, this.getZ() + 3.0D), this::canHitEntity);

                for(Entity entity : list1) {
                    if (!net.minecraftforge.event.ForgeEventFactory.onEntityStruckByLightning(entity, this)) {
                        entity.thunderHit((ServerLevel) this.level(), this);
                    }
                }

                this.hitEntities.addAll(list1);
                if (this.getCause() != null) {
                    CriteriaTriggers.CHANNELED_LIGHTNING.trigger(this.getCause(), list1);
                }
            }
        }

    }

    private BlockPos getStrikePosition() {
        Vec3 vec3 = this.position();
        return BlockPos.containing(vec3.x, vec3.y - 1.0E-6D, vec3.z);
    }

    private void spawnFire(int p_20871_) {
        if (!this.visualOnly && !this.level().isClientSide && this.level().getGameRules().getBoolean(GameRules.RULE_DOFIRETICK) && this.isSetFire()) {
            BlockPos blockpos = this.blockPosition();
            BlockState blockstate = BaseFireBlock.getState(this.level(), blockpos);
            if (this.level().getBlockState(blockpos).isAir() && blockstate.canSurvive(this.level(), blockpos)) {
                this.level().setBlockAndUpdate(blockpos, blockstate);
                ++this.blocksSetOnFire;
            }

            for(int i = 0; i < p_20871_; ++i) {
                BlockPos blockpos1 = blockpos.offset(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
                blockstate = BaseFireBlock.getState(this.level(), blockpos1);
                if (this.level().getBlockState(blockpos1).isAir() && blockstate.canSurvive(this.level(), blockpos1)) {
                    this.level().setBlockAndUpdate(blockpos1, blockstate);
                    ++this.blocksSetOnFire;
                }
            }

        }
    }

    private static void clearCopperOnLightningStrike(Level p_147151_, BlockPos p_147152_) {
        BlockState blockstate = p_147151_.getBlockState(p_147152_);
        BlockPos blockpos;
        BlockState blockstate1;
        if (blockstate.is(Blocks.LIGHTNING_ROD)) {
            blockpos = p_147152_.relative(blockstate.getValue(LightningRodBlock.FACING).getOpposite());
            blockstate1 = p_147151_.getBlockState(blockpos);
        } else {
            blockpos = p_147152_;
            blockstate1 = blockstate;
        }

        if (blockstate1.getBlock() instanceof WeatheringCopper) {
            p_147151_.setBlockAndUpdate(blockpos, WeatheringCopper.getFirst(p_147151_.getBlockState(blockpos)));
            BlockPos.MutableBlockPos blockpos$mutableblockpos = p_147152_.mutable();
            int i = p_147151_.random.nextInt(3) + 3;

            for(int j = 0; j < i; ++j) {
                int k = p_147151_.random.nextInt(8) + 1;
                randomWalkCleaningCopper(p_147151_, blockpos, blockpos$mutableblockpos, k);
            }

        }
    }

    private static void randomWalkCleaningCopper(Level p_147146_, BlockPos p_147147_, BlockPos.MutableBlockPos p_147148_, int p_147149_) {
        p_147148_.set(p_147147_);

        for(int i = 0; i < p_147149_; ++i) {
            Optional<BlockPos> optional = randomStepCleaningCopper(p_147146_, p_147148_);
            if (optional.isEmpty()) {
                break;
            }

            p_147148_.set(optional.get());
        }

    }

    private static Optional<BlockPos> randomStepCleaningCopper(Level p_147154_, BlockPos p_147155_) {
        for(BlockPos blockpos : BlockPos.randomInCube(p_147154_.random, 10, p_147155_, 1)) {
            BlockState blockstate = p_147154_.getBlockState(blockpos);
            if (blockstate.getBlock() instanceof WeatheringCopper) {
                WeatheringCopper.getPrevious(blockstate).ifPresent((p_147144_) -> {
                    p_147154_.setBlockAndUpdate(blockpos, p_147144_);
                });
                p_147154_.levelEvent(3002, blockpos, -1);
                return Optional.of(blockpos);
            }
        }

        return Optional.empty();
    }

    public int getBlocksSetOnFire() {
        return this.blocksSetOnFire;
    }

    public @NotNull Stream<Entity> getHitEntities() {
        return this.hitEntities.stream().filter(Entity::isAlive);
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null){
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity){
                return pEntity.isAlive();
            } else {
                if(MobUtil.areAllies(this.getOwner(), pEntity)){
                    return false;
                }
                if (this.getOwner() instanceof Enemy && pEntity instanceof Enemy){
                    return false;
                }
                if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1){
                    return !MobUtil.ownerStack(owned0, owned1);
                }
            }
        }
        return pEntity.isAlive();
    }
}
