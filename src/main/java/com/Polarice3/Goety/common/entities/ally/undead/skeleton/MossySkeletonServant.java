package com.Polarice3.Goety.common.entities.ally.undead.skeleton;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MossySkeletonServant extends AbstractSkeletonServant {
    public MossySkeletonServant(EntityType<? extends AbstractSkeletonServant> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.MossySkeletonServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.MossySkeletonServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.MossySkeletonServantDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.MossySkeletonServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.MossySkeletonServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.MossySkeletonServantDamage.get());
    }

    public double getBaseRangeDamage(){
        return AttributesConfig.MossySkeletonServantRangeDamage.get();
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.MOSSY_SKELETON_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.MOSSY_SKELETON_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.MOSSY_SKELETON_DEATH.get();
    }

    protected SoundEvent getStepSound() {
        return ModSounds.MOSSY_SKELETON_STEP.get();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide){
            if (this.tickCount % 5 == 0 && this.level.random.nextBoolean()) {
                double[] colors = MathHelper.rgbParticle(2735172);
                this.level.addParticle(ModParticleTypes.BIG_CULT_SPELL.get(), this.getX(), this.getY() + 1.0D, this.getZ(),
                        colors[0],
                        colors[1],
                        colors[2]);
            }
        }
    }

    @Override
    public SoundEvent getShootSound() {
        return ModSounds.MOSSY_SKELETON_SHOOT.get();
    }

    protected AbstractArrow getMobArrow(ItemStack pArrowStack, float pDistanceFactor) {
        AbstractArrow abstractarrowentity = super.getMobArrow(pArrowStack, pDistanceFactor);
        if (abstractarrowentity instanceof Arrow arrow) {
            int amplifier = this.isUpgraded() ? 1 : 0;
            MobEffect mobEffect = MobEffects.POISON;
            if (CuriosFinder.hasWildRobe(this.getTrueOwner())){
                mobEffect = GoetyEffects.ACID_VENOM.get();
            }
            arrow.addEffect(new MobEffectInstance(mobEffect, MathHelper.secondsToTicks(3), amplifier));
        }

        return abstractarrowentity;
    }
}
