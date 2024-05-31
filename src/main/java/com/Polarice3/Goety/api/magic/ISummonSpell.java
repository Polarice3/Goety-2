package com.Polarice3.Goety.api.magic;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public interface ISummonSpell extends ISpell{
    int SummonDownDuration();

    void commonResult(ServerLevel worldIn, LivingEntity entityLiving);

    default void SummonSap(LivingEntity owner, LivingEntity summonedEntity){
        if (owner != null && summonedEntity != null) {
            if (owner.hasEffect(GoetyEffects.SUMMON_DOWN.get())) {
                MobEffectInstance effectinstance = owner.getEffect(GoetyEffects.SUMMON_DOWN.get());
                if (effectinstance != null) {
                    summonedEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, EffectsUtil.infiniteEffect(), effectinstance.getAmplifier()));
                    summonedEntity.addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), EffectsUtil.infiniteEffect(), effectinstance.getAmplifier()));
                }
            }
        }
    }

    default void SummonDown(LivingEntity entityLiving){
        MobEffectInstance effectinstance1 = entityLiving.getEffect(GoetyEffects.SUMMON_DOWN.get());
        int i = 1;
        if (effectinstance1 != null) {
            i += effectinstance1.getAmplifier();
            entityLiving.removeEffectNoUpdate(GoetyEffects.SUMMON_DOWN.get());
        } else {
            --i;
        }

        i = Mth.clamp(i, 0, 4);
        int s = SummonDownDuration();
        if (entityLiving instanceof Player player){
            if (WandUtil.enchantedFocus(player)){
                s = (int) (SummonDownDuration() * 1.5);
            }
        }
        MobEffectInstance effectinstance = new MobEffectInstance(GoetyEffects.SUMMON_DOWN.get(), s, i, false, false, true);
        entityLiving.addEffect(effectinstance);
    }

    default void setTarget(LivingEntity source, Mob summoned){
        LivingEntity target = this.getTarget(summoned);
        if (target != null){
            if (MobUtil.areNotFullAllies(source, target)) {
                summoned.setTarget(target);
            }
        }
    }
}
