package com.Polarice3.Goety.common.effects.brew;

import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPurifyEffectPacket;
import com.Polarice3.Goety.utils.EffectsUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class PurifyBrewEffect extends BrewEffect{
    public boolean removeDebuff;

    public PurifyBrewEffect(String effectID, int soulCost, int cap, MobEffectCategory mobEffectCategory, int color, boolean removeDebuff) {
        super(effectID, soulCost, cap, mobEffectCategory, color);
        this.removeDebuff = removeDebuff;
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public boolean canLinger() {
        return true;
    }

    public void applyEntityEffect(LivingEntity pTarget, @Nullable Entity pSource, @Nullable Entity pIndirectSource, int pAmplifier){
        if (!pTarget.level.isClientSide) {
            for (MobEffect mobEffect : ForgeRegistries.MOB_EFFECTS){
                boolean flag;
                if (this.removeDebuff) {
                    flag = !mobEffect.isBeneficial();
                } else {
                    flag = mobEffect.isBeneficial();
                }
                if (flag && !mobEffect.getCurativeItems().isEmpty()){
                    pTarget.removeEffect(mobEffect);
                }
            }
            ModNetwork.sentToTrackingEntity(pTarget, new SPurifyEffectPacket(pTarget.getId(), this.removeDebuff));
        }
    }
}
