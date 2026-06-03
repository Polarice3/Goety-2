package com.Polarice3.Goety.api.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.extensions.IForgeBlock;

import java.util.Map;

public interface IEnchanteableBlock extends EntityBlock, IForgeBlock {

    default void setEnchantments(ItemStack itemStack, BlockEntity tileEntity){
        if (tileEntity instanceof IEnchantedBlock enchantedBlock) {
            Map<Enchantment, Integer> enchantments = enchantedBlock.getEnchantments();
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                Integer integer = entry.getValue();
                if (integer < 0) {
                    enchantments.remove(enchantment);
                } else {
                    enchantments.put(enchantment, integer);
                }
            }
            EnchantmentHelper.setEnchantments(enchantments, itemStack);
        }
    }

    default ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        ItemStack itemStack = ItemStack.EMPTY;
        if (this instanceof ItemLike like) {
            itemStack = new ItemStack(like);
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof IEnchantedBlock) {
                this.setEnchantments(itemStack, tileEntity);
            }
        }
        return itemStack;
    }


}
