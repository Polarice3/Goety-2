package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Rattle extends AbstractSkeleton {
    public Rattle(EntityType<? extends AbstractSkeleton> p_32133_, Level p_32134_) {
        super(p_32133_, p_32134_);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.StrayServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.StrayServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.StrayServantDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.StrayServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.StrayServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.StrayServantDamage.get());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setConfigurableAttributes();
    }

    public double getBaseRangeDamage(){
        return AttributesConfig.StrayServantRangeDamage.get();
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.STRAY_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.STRAY_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.STRAY_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.STRAY_STEP;
    }

    protected AbstractArrow getArrow(ItemStack pArrowStack, float pDistanceFactor) {
        AbstractArrow abstractarrowentity = super.getArrow(pArrowStack, pDistanceFactor);
        abstractarrowentity.setBaseDamage(abstractarrowentity.getBaseDamage() + this.getBaseRangeDamage());
        if (abstractarrowentity instanceof Arrow) {
            ((Arrow)abstractarrowentity).addEffect(new MobEffectInstance(GoetyEffects.SPASMS.get(), 600, 0));
        }

        return abstractarrowentity;
    }

    protected float getDamageAfterMagicAbsorb(DamageSource p_34149_, float p_34150_) {
        p_34150_ = super.getDamageAfterMagicAbsorb(p_34149_, p_34150_);
        if (p_34149_.getEntity() == this) {
            p_34150_ = 0.0F;
        }

        if (ModDamageSource.shockAttacks(p_34149_) || p_34149_ == DamageSource.LIGHTNING_BOLT) {
            p_34150_ *= 0.15F;
        }

        return p_34150_;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance pPotioneffect) {
        return super.canBeAffected(pPotioneffect)
                && pPotioneffect.getEffect() != GoetyEffects.SPASMS.get();
    }
}
