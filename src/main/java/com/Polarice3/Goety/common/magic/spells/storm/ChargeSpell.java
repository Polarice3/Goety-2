package com.Polarice3.Goety.common.magic.spells.storm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.TouchSpell;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningPacket;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ChargeSpell extends TouchSpell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.ChargeCost.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.ZAP.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.ChargeCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.STORM;
    }

    @Override
    public void touchResult(ServerLevel worldIn, LivingEntity caster, LivingEntity target, ItemStack staff, SpellStat spellStat) {
        if (this.rightStaff(staff)) {
            this.chainCharge(worldIn, target, caster, 6.0D);
        } else {
            if (!target.hasEffect(GoetyEffects.CHARGED.get())) {
                this.playSound(worldIn, target, 1.0F, 0.5F);
                target.addEffect(new MobEffectInstance(GoetyEffects.CHARGED.get(), MathHelper.secondsToTicks(30), 0, false, false));
            } else {
                MobEffectInstance instance = target.getEffect(GoetyEffects.CHARGED.get());
                if (instance != null) {
                    if (instance.getAmplifier() >= 1) {
                        DamageSource damageSource = ModDamageSource.directShock(caster);
                        if (MobUtil.areAllies(target, caster)) {
                            damageSource = ModDamageSource.getDamageSource(worldIn, ModDamageSource.SHOCK);
                        }
                        target.hurt(damageSource, SpellConfig.ChargeDamage.get().floatValue());
                    } else {
                        this.playSound(worldIn, target, 1.0F, 0.75F);
                    }
                    EffectsUtil.amplifyEffect(target, GoetyEffects.CHARGED.get(), MathHelper.secondsToTicks(30), 3, false, false);
                }
            }
        }
    }

    public void chainCharge(ServerLevel serverLevel, LivingEntity pTarget, LivingEntity pAttacker, double range) {
        List<Entity> harmed = new ArrayList<>();
        Predicate<Entity> selector = entity -> entity instanceof LivingEntity livingEntity && !harmed.contains(livingEntity);
        if (pAttacker != null){
            selector = selector.and(entity -> entity instanceof LivingEntity livingEntity && livingEntity != pAttacker && pTarget instanceof OwnableEntity ownable && ownable.getOwner() == pAttacker);
        }
        Entity prevTarget = pTarget;

        int hops = serverLevel.isThundering() ? 8 : 4;

        for (int i = 0; i < hops; i++) {
            AABB aabb = new AABB(Vec3Util.subtract(prevTarget.position(), range), Vec3Util.add(prevTarget.position(), range));
            Entity finalPrevTarget = prevTarget;
            List<Entity> entities = serverLevel.getEntities(prevTarget, aabb, selector.and(entity -> MobUtil.hasLineOfSight(finalPrevTarget, entity)));
            if (!entities.isEmpty()) {
                Entity entity = entities.get(serverLevel.getRandom().nextInt(entities.size()));
                if (entity instanceof LivingEntity target) {
                    if (!target.hasEffect(GoetyEffects.CHARGED.get())) {
                        this.playSound(serverLevel, target, 1.0F, 0.5F);
                        target.addEffect(new MobEffectInstance(GoetyEffects.CHARGED.get(), MathHelper.secondsToTicks(30), 0, false, false));
                    } else {
                        MobEffectInstance instance = target.getEffect(GoetyEffects.CHARGED.get());
                        if (instance != null) {
                            if (instance.getAmplifier() >= 1) {
                                target.hurt(ModDamageSource.directShock(pAttacker), SpellConfig.ChargeDamage.get().floatValue());
                            } else {
                                this.playSound(serverLevel, target, 1.0F, 0.75F);
                            }
                            EffectsUtil.amplifyEffect(target, GoetyEffects.CHARGED.get(), MathHelper.secondsToTicks(30), 3, false, false);
                        }
                    }
                    if (prevTarget != target) {
                        Vec3 vec3 = prevTarget.getEyePosition();
                        Vec3 vec31 = target.getEyePosition();
                        ModNetwork.sendToALL(new SLightningPacket(vec3, vec31, 5));
                    }

                    harmed.add(target);
                    prevTarget = target;
                }
            }
        }
    }
}
