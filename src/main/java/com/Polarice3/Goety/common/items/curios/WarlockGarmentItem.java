package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.WartlingEggItem;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ItemHelper;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WarlockGarmentItem extends SingleStackItem{

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isClientSide) {
            if (entityIn instanceof Player player) {
                if (CuriosFinder.hasCurio(player, itemStack -> itemStack == stack)) {
                    if (player.tickCount % 60 == 0) {
                        if (!ItemHelper.findItem(player, ModItems.WARTFUL_EGG.get()).isEmpty()) {
                            ItemStack itemStack = ItemHelper.findItem(player, ModItems.WARTFUL_EGG.get());
                            player.getActiveEffects().stream().filter(mobEffect -> mobEffect.getEffect().getCategory() == MobEffectCategory.HARMFUL).findFirst().ifPresent(effect -> {
                                if (!effect.getCurativeItems().isEmpty()) {
                                    WartlingEggItem.warlockUse(worldIn, player, itemStack);
                                }
                            });
                        }
                    }
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }
}
