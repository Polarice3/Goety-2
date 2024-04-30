package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.ModTiers;
import net.minecraft.world.item.*;

public class ModToolItems {

    public static class DarkSwordItem extends SwordItem{

        public DarkSwordItem() {
            super(ModTiers.DARK, 3, -2.4F, ModItems.baseProperities());
        }

        @Override
        public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
            return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
        }
    }

    public static class DarkShovelItem extends ShovelItem{

        public DarkShovelItem() {
            super(ModTiers.DARK, 1.5F, -3.0F, ModItems.baseProperities());
        }

        @Override
        public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
            return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
        }
    }

    public static class DarkPickaxeItem extends PickaxeItem{

        public DarkPickaxeItem() {
            super(ModTiers.DARK, 1, -2.8F, ModItems.baseProperities());
        }

        @Override
        public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
            return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
        }
    }

    public static class DarkAxeItem extends AxeItem{

        public DarkAxeItem() {
            super(ModTiers.DARK, 5.0F, -3.0F, ModItems.baseProperities());
        }

        @Override
        public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
            return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
        }
    }

    public static class DarkHoeItem extends HoeItem{

        public DarkHoeItem() {
            super(ModTiers.DARK, -3, 0.0F, ModItems.baseProperities());
        }

        @Override
        public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
            return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
        }
    }
}
