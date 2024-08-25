package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HungryDaggerItem extends FangedDaggerItem{
    public HungryDaggerItem(){
        super(ModTiers.DARK);
    }

    @SubscribeEvent
    public static void FangedHurt(LivingHurtEvent event){
        Entity attacker = event.getSource().getEntity();
        if (ModDamageSource.physicalAttacks(event.getSource())){
            if (attacker instanceof LivingEntity livingAttacker){
                if (livingAttacker.getMainHandItem().getItem() == ModItems.HUNGRY_DAGGER.get()){
                    int soulEat = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.SOUL_EATER.get(), livingAttacker) + 1;
                    livingAttacker.heal(event.getAmount() * (0.05F * soulEat));
                }
            }
        }
    }
}
