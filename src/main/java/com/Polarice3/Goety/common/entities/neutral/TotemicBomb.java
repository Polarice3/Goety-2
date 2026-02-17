package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.SpellExplosion;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class TotemicBomb extends AbstractMonolith{
    public float explosionPower = 4.8F;

    public TotemicBomb(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public void addAdditionalSaveData(CompoundTag p_31485_) {
        super.addAdditionalSaveData(p_31485_);
        p_31485_.putFloat("ExplosionPower", this.getExplosionPower());
    }

    public void readAdditionalSaveData(CompoundTag p_31474_) {
        super.readAdditionalSaveData(p_31474_);
        if (p_31474_.contains("ExplosionPower")) {
            this.setExplosionPower(p_31474_.getFloat("ExplosionPower"));
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (this.canSpawn(pLevel.getLevel())){
            this.playSound(ModSounds.BOMB_SPAWN.get(), 2.0F, 1.0F);
            ServerLevel serverLevel = pLevel.getLevel();
            for (int i = 0; i < serverLevel.getRandom().nextInt(35) + 10; ++i) {
                ServerParticleUtil.smokeParticles(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), serverLevel);
            }
        }
        return pSpawnData;
    }

    @Override
    public BlockState getState() {
        return Blocks.STONE.defaultBlockState();
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if (!pSource.is(DamageTypeTags.BYPASSES_ARMOR)) {
            this.playSound(ModSounds.BOMB_SPARKLE.get());
        }
        return pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public int getAgeSpeed() {
        return 5;
    }

    @Override
    public int getLifeSpan() {
        return 5;
    }

    public float getExplosionPower() {
        return this.explosionPower;
    }

    public void setExplosionPower(float explosionPower) {
        this.explosionPower = explosionPower;
    }

    private void explode() {
        if (!this.level.isClientSide) {
            if (this.level instanceof ServerLevel serverLevel){
                ColorUtil colorUtil = new ColorUtil(0xff8905);
                serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, this.getExplosionPower(), 1), this.getX(), this.getY(), this.getZ(), 0, 0.0D, 0.0D, 0.0D, 0);
                serverLevel.sendParticles(new VerticalCircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, this.getExplosionPower(), 1), this.getX(), this.getY(), this.getZ(), 0, 0.0D, 0.0D, 0.0D, 0);
                for (int i = 0; i < 32; ++i) {
                    ColorUtil colorUtil1 = new ColorUtil(0xac9b8f);
                    serverLevel.sendParticles(ModParticleTypes.BIG_CULT_SPELL.get(), this.getRandomX(1.0F), this.getRandomY(), this.getRandomZ(1.0F), 0, colorUtil1.red, colorUtil1.green, colorUtil1.blue, 1.0F);
                    serverLevel.sendParticles(ModParticleTypes.BIG_FIRE_GROUND.get(), this.getRandomX(0.5F), this.getY(), this.getRandomZ(0.5F), 1, 0.0F, 0.0F, 0.0F, 0.0F);
                }
            }
            this.dead = true;
            new SpellExplosion(this.level, this.getTrueOwner() != null ? this.getTrueOwner() : this, this.damageSources().explosion(this, this.getTrueOwner() != null ? this.getTrueOwner() : this), this.getX(), this.getY(), this.getZ(), this.getExplosionPower(), 0);
            this.playSound(SoundEvents.GENERIC_EXPLODE, 4.0F, (1.0F + (this.level.getRandom().nextFloat() - this.level.getRandom().nextFloat()) * 0.2F) * 0.7F);
            this.discard();
        }
    }

    public void aiStep() {
        super.aiStep();
        if (!this.isEmerging()){
            if (!this.isActivate()){
                this.setActivate(true);
                this.playSound(ModSounds.BOMB_LOAD.get(), 2.0F, 1.0F);
            }
            if (this.tickCount == MathHelper.secondsToTicks(1)){
                this.playSound(ModSounds.BOMB_FUSE.get(), 2.0F, 1.0F);
            }
            if (this.tickCount == MathHelper.secondsToTicks(this.getLifeSpan() - 2)){
                this.playSound(ModSounds.BOMB_PULSE.get(), 2.0F, 1.0F);
            }
            if (this.tickCount >= MathHelper.secondsToTicks(this.getLifeSpan())){
                this.explode();
            }
        }
    }
}
