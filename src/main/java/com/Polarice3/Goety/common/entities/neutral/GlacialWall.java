package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class GlacialWall extends AbstractMonolith{
    public GlacialWall(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (this.canSpawn(pLevel.getLevel())){
            this.playSound(ModSounds.WALL_SPAWN.get(), 1.0F, 2.0F);
            this.playSound(ModSounds.WALL_ERUPT.get(), 1.0F, 2.0F);
            ServerLevel serverLevel = pLevel.getLevel();
            for (int i = 0; i < serverLevel.random.nextInt(35) + 10; ++i) {
                ServerParticleUtil.smokeParticles(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), serverLevel);
            }
        }
        return pSpawnData;
    }

    @Override
    public BlockState getState() {
        return Blocks.BLUE_ICE.defaultBlockState();
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        int particles = 5;
        int efficiency = 0;
        boolean damage = false;
        if (!this.level.isClientSide && !this.isEmerging()) {
            if (ModDamageSource.physicalAttacks(pSource)) {
                if (pSource.getDirectEntity() instanceof LivingEntity living) {
                    if (living.getMainHandItem().isCorrectToolForDrops(this.getState())){
                        damage = true;
                        efficiency += EnchantmentHelper.getBlockEfficiency(living);
                    }
                }
            }
            if (damage){
                pAmount *= 2.0F + (efficiency / 2.0F);
                particles = 20;
            }
            if (pSource.is(DamageTypeTags.IS_FIRE)){
                pAmount *= 2.0F;
            }
            if (!pSource.is(DamageTypes.STARVE)) {
                if (this.level instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < particles; ++i) {
                        ServerParticleUtil.addParticlesAroundSelf(serverLevel, this.getParticles(), this);
                    }
                }
            }
        }
        return super.hurt(pSource, pAmount);
    }

    protected SoundEvent getHurtSound(DamageSource p_34154_) {
        if (p_34154_.is(DamageTypes.STARVE)){
            return SoundEvents.POWDER_SNOW_BREAK;
        }
        return SoundEvents.AMETHYST_BLOCK_BREAK;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.STONE_BREAK;
    }

    public void die(DamageSource cause) {
        if (this.level instanceof ServerLevel serverLevel){
            ServerParticleUtil.blockBreakParticles(this.getParticles(), BlockPos.containing(this.position()), Blocks.SNOW_BLOCK.defaultBlockState(), serverLevel);
            ServerParticleUtil.blockBreakParticles(this.getParticles(), BlockPos.containing(this.position()).above(), this.getState(), serverLevel);
            ServerParticleUtil.blockBreakParticles(this.getParticles(), BlockPos.containing(this.position()).above().above(), Blocks.SNOW_BLOCK.defaultBlockState(), serverLevel);
        }
        this.playSound(ModSounds.ICE_SPIKE_HIT.get(), 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 0.5F);
        if (cause.is(DamageTypes.STARVE)){
            if (this.level.random.nextFloat() <= 0.15F) {
                AreaEffectCloud areaeffectcloud = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
                areaeffectcloud.setParticle(ParticleTypes.POOF);
                areaeffectcloud.setRadius(2.0F);
                areaeffectcloud.setDuration(200);
                areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float) areaeffectcloud.getDuration());

                areaeffectcloud.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600));

                this.level.addFreshEntity(areaeffectcloud);
            }
        }
        this.remove(RemovalReason.KILLED);
        if (!cause.is(DamageTypes.STARVE)){
            super.die(cause);
        }
    }

    @Override
    public int getAgeSpeed() {
        return 5;
    }

    public void aiStep() {
        super.aiStep();
        if (!this.isEmerging()){
            if (!this.isActivate()){
                this.setActivate(true);
            } else if (this.random.nextFloat() <= 0.25F){
                float damage = 2.0F;
                if (this.level().getBiome(this.blockPosition()).is(BiomeTags.SNOW_GOLEM_MELTS)){
                    damage *= 2.0F;
                }
                this.hurt(this.damageSources().starve(), this.level.random.nextInt((int) damage));
            }
            if (!this.level.isClientSide) {
                if (this.tickCount >= MathHelper.secondsToTicks(this.getLifeSpan())){
                    if (this.random.nextFloat() <= 0.25F) {
                        this.die(this.damageSources().starve());
                    }
                }
            }
        }
    }
}
