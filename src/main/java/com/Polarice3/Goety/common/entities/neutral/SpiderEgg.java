package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.spider.SpiderServant;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SpiderEgg extends Owned {
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(SpiderEgg.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_HATCHING = SynchedEntityData.defineId(SpiderEgg.class, EntityDataSerializers.BOOLEAN);
    private int oldSwell;
    private int swell;
    private int maxSwell = 30;

    public SpiderEgg(EntityType<? extends Owned> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SWELL_DIR, -1);
        this.entityData.define(DATA_IS_HATCHING, false);
    }

    public void addAdditionalSaveData(CompoundTag p_32304_) {
        super.addAdditionalSaveData(p_32304_);
        p_32304_.putShort("Fuse", (short)this.maxSwell);
        p_32304_.putBoolean("hatch", this.isHatching());
    }

    public void readAdditionalSaveData(CompoundTag p_32296_) {
        super.readAdditionalSaveData(p_32296_);
        if (p_32296_.contains("Fuse", 99)) {
            this.maxSwell = p_32296_.getShort("Fuse");
        }

        if (p_32296_.getBoolean("hatch")) {
            this.hatch();
        }

    }

    @Override
    public void lifeSpanDamage() {
        this.hatch();
    }

    public int getSwellDir() {
        return this.entityData.get(DATA_SWELL_DIR);
    }

    public void setSwellDir(int p_32284_) {
        this.entityData.set(DATA_SWELL_DIR, p_32284_);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.SPIDER_NEST_START.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SPIDER_NEST_START.get();
    }

    @Override
    public void die(DamageSource p_21014_) {
        if (this.level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 8; ++i) {
                ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBWEB.defaultBlockState()), this);
            }
        }
        this.discard();
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean isPushable() {
        return false;
    }

    public void push(Entity entityIn) {
    }

    @Override
    public void makeStuckInBlock(BlockState p_20006_, Vec3 p_20007_) {
        super.makeStuckInBlock(p_20006_, Vec3.ZERO);
    }

    @Override
    public void tick() {
        super.tick();
        this.oldSwell = this.swell;
        if (this.isHatching()) {
            this.setSwellDir(1);
        }

        int i = this.getSwellDir();
        if (i > 0 && this.swell == 0) {
            this.playSound(ModSounds.SPIDER_NEST_START.get(), 0.5F, 0.75F);
        }
        this.swell += i;
        if (this.swell < 0) {
            this.swell = 0;
        }

        if (this.swell >= this.maxSwell) {
            this.swell = this.maxSwell;
            this.hatchEgg();
        }
        if (this.level instanceof ServerLevel serverLevel) {
            if (serverLevel.random.nextInt(10) == 0) {
                ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBWEB.defaultBlockState()), this);
            }
        }
    }

    public float getSwelling(float p_32321_) {
        return Mth.lerp(p_32321_, (float)this.oldSwell, (float)this.swell) / (float)(this.maxSwell - 2);
    }

    private void hatchEgg() {
        if (this.level instanceof ServerLevel serverLevel) {
            SpiderServant spiderServant = ModEntityType.SPIDER_SERVANT.get().create(serverLevel);
            if (spiderServant != null) {
                EntityType<?> entityType = spiderServant.getVariant(this.getOwner() instanceof Player player ? player : null, serverLevel, this.blockPosition());
                if (entityType != null) {
                    spiderServant = (SpiderServant) entityType.create(serverLevel);
                }
                if (spiderServant != null) {
                    spiderServant.setPos(this.position());
                    if (this.getTrueOwner() != null) {
                        spiderServant.setTrueOwner(this.getTrueOwner());
                    }
                    spiderServant.setHostile(this.isHostile());
                    spiderServant.setPersistenceRequired();
                    spiderServant.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    if (serverLevel.addFreshEntity(spiderServant)) {
                        this.playSound(ModSounds.SPIDER_NEST_SPAWN.get());
                        this.die(this.damageSources().starve());
                    }
                }
            }
        }

    }

    public boolean isHatching() {
        return this.entityData.get(DATA_IS_HATCHING);
    }

    public void hatch() {
        this.entityData.set(DATA_IS_HATCHING, true);
    }
}
