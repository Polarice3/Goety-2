package com.Polarice3.Goety.common.entities.ally.spider;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class IcySpiderServant extends SpiderServant{
    public IcySpiderServant(EntityType<? extends SpiderServant> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return SpiderServant.setCustomAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.IcySpiderServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.IcySpiderServantDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.IcySpiderServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.IcySpiderServantDamage.get());
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.ICY_SPIDER_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_33814_) {
        return ModSounds.ICY_SPIDER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.ICY_SPIDER_DEATH.get();
    }

    public boolean doHurtTarget(Entity target) {
        if (super.doHurtTarget(target)) {
            if (target instanceof LivingEntity livingEntity) {
                int amp = 0;
                int i = this.getMasterOwner() instanceof Player ? 7 : 0;
                if (this.level.getDifficulty() == Difficulty.NORMAL) {
                    i = 7;
                } else if (this.level.getDifficulty() == Difficulty.HARD) {
                    i = 15;
                }

                if (i > 0) {
                    MobEffect effect = MobEffects.MOVEMENT_SLOWDOWN;
                    if (CuriosFinder.hasFrostRobes(this.getMasterOwner())){
                        if (!target.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)) {
                            effect = GoetyEffects.FREEZING.get();
                        } else {
                            amp += 1;
                        }
                    }
                    livingEntity.addEffect(new MobEffectInstance(effect, i * 20, amp), this);
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
