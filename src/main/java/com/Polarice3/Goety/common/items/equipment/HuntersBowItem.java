package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HuntersBowItem extends BowItem {
    public HuntersBowItem() {
        super((new Item.Properties()).rarity(Rarity.UNCOMMON).defaultDurability(MainConfig.HuntersBowDurability.get()).tab(Goety.TAB));
    }

    @SubscribeEvent
    public static void HunterLoot(LootingLevelEvent event){
        if (event.getDamageSource() != null) {
            if (event.getEntity() != null) {
                if (!event.getEntity().level.isClientSide) {
                    if (event.getDamageSource().getEntity() != null && event.getDamageSource().getEntity() instanceof LivingEntity livingEntity) {
                        if (livingEntity.getMainHandItem().getItem() instanceof HuntersBowItem) {
                            if (event.getDamageSource().getDirectEntity() instanceof AbstractArrow) {
                                if (event.getEntity() instanceof Animal){
                                    event.setLootingLevel(4);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
