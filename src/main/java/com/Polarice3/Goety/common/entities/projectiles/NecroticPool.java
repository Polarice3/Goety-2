package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;

public class NecroticPool extends AcidPool {

    public NecroticPool(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void poolTick() {
        for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox(), EntitySelector.NO_CREATIVE_OR_SPECTATOR)){
            boolean undead = livingEntity.getMobType() == MobType.UNDEAD;
            float damage = this.getDamage();
            if (this.getPercentDamage() > 0.0F) {
                damage = livingEntity.getMaxHealth() * this.getPercentDamage();
            }
            if (MobUtil.areAllies(this.getOwner(), livingEntity)) {
                if (undead) {
                    if (this.getPercentDamage() <= 0.0F) {
                        damage *= 0.5F;
                    }
                    if (livingEntity.tickCount % 10 == 0){
                        livingEntity.heal(damage);
                    }
                }
            } else if (!undead) {
                boolean flag;
                if (this.getOwner() != null) {
                    flag = livingEntity.hurt(ModDamageSource.rot(this, this.getOwner()), damage);
                } else {
                    flag = livingEntity.hurt(ModDamageSource.getDamageSource(this.level, ModDamageSource.ROT), damage);
                }
                if (flag) {
                    if (this.getInvTime() > -1) {
                        livingEntity.invulnerableTime = this.getInvTime();
                    }
                    if (!this.getEffects().isEmpty()) {
                        for (MobEffectInstance instance : this.getEffects()) {
                            if (instance.getEffect().isInstantenous()) {
                                instance.getEffect().applyInstantenousEffect(this, this.getOwner(), livingEntity, instance.getAmplifier(), 0.5D);
                            } else {
                                livingEntity.addEffect(new MobEffectInstance(instance), this);
                            }
                        }
                    }
                }
            }
        }
    }
}
