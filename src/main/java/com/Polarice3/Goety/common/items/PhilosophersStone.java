package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeItem;

import javax.annotation.Nonnull;

public class PhilosophersStone extends Item implements IForgeItem {
    public PhilosophersStone(){
        super(new Properties().tab(Goety.TAB).durability(64));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (MainConfig.SoulRepair.get()) {
            if (entityIn instanceof Player player) {
                if (stack.isDamaged()) {
                    if (SEHelper.getSoulsContainer(player)){
                        if (SEHelper.getSoulsAmount(player, 1)){
                            if (player.tickCount % 20 == 0) {
                                stack.setDamageValue(stack.getDamageValue() - 1);
                                SEHelper.decreaseSouls(player, 1);
                            }
                        }
                    }
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack container = itemStack.copy();
        container.setDamageValue(itemStack.getDamageValue() + 1);
        return container;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
    }

    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.getItem() == Items.CRYING_OBSIDIAN || super.isValidRepairItem(pToRepair, pRepair);
    }

}
