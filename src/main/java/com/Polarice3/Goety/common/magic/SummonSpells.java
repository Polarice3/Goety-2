package com.Polarice3.Goety.common.magic;

import com.Polarice3.Goety.common.effects.ModEffects;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

public abstract class SummonSpells extends Spells{
    private final TargetingConditions summonCountTargeting = TargetingConditions.DEFAULT.range(64.0D).ignoreInvisibilityTesting();
    public int enchantment = 0;
    public int duration = 1;

    public abstract int SummonDownDuration();

/*    public boolean NecroPower(LivingEntity entityLiving){
        return RobeArmorFinder.FindNecroArmor(entityLiving);
    }

    public boolean NecroMastery(LivingEntity entityLiving){
        return RobeArmorFinder.FindNecroHelm(entityLiving);
    }

    public boolean SummonMastery(LivingEntity entityLiving){
        return RobeArmorFinder.FindLeggings(entityLiving);
    }*/

    public abstract void commonResult(ServerLevel worldIn, LivingEntity entityLiving);

    public void SummonSap(LivingEntity owner, LivingEntity summonedEntity){
        if (owner != null && summonedEntity != null) {
            if (owner.hasEffect(ModEffects.SUMMON_DOWN.get())) {
                MobEffectInstance effectinstance = owner.getEffect(ModEffects.SUMMON_DOWN.get());
                if (effectinstance != null) {
                    summonedEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, Integer.MAX_VALUE, effectinstance.getAmplifier()));
                    summonedEntity.addEffect(new MobEffectInstance(ModEffects.SAPPED.get(), Integer.MAX_VALUE, effectinstance.getAmplifier()));
                }
            }
        }
    }

    public void SummonDown(LivingEntity entityLiving){
        MobEffectInstance effectinstance1 = entityLiving.getEffect(ModEffects.SUMMON_DOWN.get());
        int i = 1;
        if (effectinstance1 != null) {
            i += effectinstance1.getAmplifier();
            entityLiving.removeEffectNoUpdate(ModEffects.SUMMON_DOWN.get());
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
/*        if (SummonMastery(entityLiving)){
            if (entityLiving.level.random.nextBoolean()){
                s = SummonDownDuration()/2;
            }
        }*/
        MobEffectInstance effectinstance = new MobEffectInstance(ModEffects.SUMMON_DOWN.get(), s, i, false, false, true);
        entityLiving.addEffect(effectinstance);
    }

    public int SummonLimit(LivingEntity entityLiving){
        return entityLiving.level.getNearbyEntities(Owned.class, this.summonCountTargeting, entityLiving, entityLiving.getBoundingBox().inflate(64.0D)).size();
    }
}
