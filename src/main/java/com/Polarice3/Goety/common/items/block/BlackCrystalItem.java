package com.Polarice3.Goety.common.items.block;

import com.Polarice3.Goety.client.render.block.ModISTER;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class BlackCrystalItem extends BlockItemBase {

    public BlackCrystalItem() {
        super(ModBlocks.BLACK_CRYSTAL.get());
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return stack.getCount() == 1
                && (enchantment == ModEnchantments.SOUL_EATER.get()
                || enchantment == ModEnchantments.RADIUS.get());
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

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new ModISTER();
            }
        });
    }
}
