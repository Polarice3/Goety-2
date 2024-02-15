package com.Polarice3.Goety.common.items.block;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;

public class EnchantableBlockItem extends BlockItemBase {

    public EnchantableBlockItem(Block blockIn) {
        super(blockIn);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (stack.getItem() == ModBlocks.SCULK_DEVOURER.get().asItem()){
            return stack.getCount() == 1
                    && (enchantment == ModEnchantments.SOUL_EATER.get()
                    || enchantment == ModEnchantments.RADIUS.get());
        }
        if (stack.getItem() == ModBlocks.SCULK_CONVERTER.get().asItem()){
            return stack.getCount() == 1 && enchantment == ModEnchantments.POTENCY.get();
        }
        if (stack.getItem() == ModBlocks.SCULK_GROWER.get().asItem()){
            if (MainConfig.SculkGrowerPotency.get()){
                return stack.getCount() == 1 && (enchantment == ModEnchantments.POTENCY.get() || enchantment == ModEnchantments.RADIUS.get());
            } else {
                return stack.getCount() == 1 && enchantment == ModEnchantments.RADIUS.get();
            }
        }
        return stack.getCount() == 1;
    }

    public int getMaxStackSize(ItemStack itemStack){
        return itemStack.isEnchanted() ? 1 : super.getMaxStackSize(itemStack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 25;
    }
}
