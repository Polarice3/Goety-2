package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.BooleanSupplier;

public class MagmaCubeServant extends SlimeServant{
    public MagmaCubeServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, (double)0.2F);
    }

    public void setSize(int p_32972_, boolean p_32973_) {
        super.setSize(p_32972_, p_32973_);
        this.getAttribute(Attributes.ARMOR).setBaseValue((double)(p_32972_ * 3));
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    protected ParticleOptions getParticleType() {
        return ParticleTypes.FLAME;
    }

    public boolean isOnFire() {
        return false;
    }

    protected int getJumpDelay() {
        return super.getJumpDelay() * 4;
    }

    protected void decreaseSquish() {
        this.targetSquish *= 0.9F;
    }

    protected void jumpFromGround() {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, (double)(this.getJumpPower() + (float)this.getSize() * 0.1F), vec3.z);
        this.hasImpulse = true;
        net.minecraftforge.common.ForgeHooks.onLivingJump(this);
    }

    @Override
    public void jumpInFluid(net.minecraftforge.fluids.FluidType type) {
        this.jumpInLiquidInternal(() -> type == net.minecraftforge.common.ForgeMod.LAVA_TYPE.get(), () -> super.jumpInFluid(type));
    }

    private void jumpInLiquidInternal(BooleanSupplier isLava, Runnable onSuper) {
        if (isLava.getAsBoolean()) {
            Vec3 vec3 = this.getDeltaMovement();
            this.setDeltaMovement(vec3.x, (double)(0.22F + (float)this.getSize() * 0.05F), vec3.z);
            this.hasImpulse = true;
        } else {
            onSuper.run();
        }
    }

    public boolean causeFallDamage(float p_149717_, float p_149718_, DamageSource p_149719_) {
        return false;
    }

    protected boolean isDealsDamage() {
        return this.isEffectiveAi();
    }

    protected float getAttackDamage() {
        return super.getAttackDamage() + 2.0F;
    }

    protected SoundEvent getHurtSound(DamageSource p_32992_) {
        return this.isTiny() ? SoundEvents.MAGMA_CUBE_HURT_SMALL : SoundEvents.MAGMA_CUBE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return this.isTiny() ? SoundEvents.MAGMA_CUBE_DEATH_SMALL : SoundEvents.MAGMA_CUBE_DEATH;
    }

    protected SoundEvent getSquishSound() {
        return this.isTiny() ? SoundEvents.MAGMA_CUBE_SQUISH_SMALL : SoundEvents.MAGMA_CUBE_SQUISH;
    }

    protected SoundEvent getJumpSound() {
        return SoundEvents.MAGMA_CUBE_JUMP;
    }

    public Item getIncreaseItem(){
        return Items.MAGMA_BLOCK;
    }

    public Item getHealItem(){
        return Items.MAGMA_CREAM;
    }
}
