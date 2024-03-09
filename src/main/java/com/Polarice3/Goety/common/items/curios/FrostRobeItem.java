package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.ItemConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FrostRobeItem extends SingleStackItem{

    @SubscribeEvent
    public static void LivingEffects(LivingEvent.LivingTickEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity != null){
            if (CuriosFinder.hasCurio(livingEntity, item -> item.getItem() instanceof FrostRobeItem)){
                livingEntity.setTicksFrozen(0);
                livingEntity.setIsInPowderSnow(false);
                MobUtil.PowderedSnowMovement(livingEntity);
            }
        }
    }

    @SubscribeEvent
    public static void HurtEvent(LivingHurtEvent event){
        LivingEntity victim = event.getEntity();
        if (CuriosFinder.hasCurio(victim, item -> item.getItem() instanceof FrostRobeItem)){
            if (ModDamageSource.freezeAttacks(event.getSource()) || event.getSource() == DamageSource.FREEZE){
                float resistance = 1.0F - (ItemConfig.FrostRobeResistance.get() / 100.0F);
                event.setAmount(event.getAmount() * resistance);
            }
        }
    }
}
