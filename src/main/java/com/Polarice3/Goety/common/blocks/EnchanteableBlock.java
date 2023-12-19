package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.api.blocks.IEnchantedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.Map;

public abstract class EnchanteableBlock extends BaseEntityBlock {

    protected EnchanteableBlock(Properties p_49224_) {
        super(p_49224_);
    }

    public void setEnchantments(ItemStack itemStack, BlockEntity tileEntity){
        IEnchantedBlock devourerBlockEntity = (IEnchantedBlock) tileEntity;
        Map<Enchantment, Integer> enchantments = devourerBlockEntity.getEnchantments();
        for(Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            Integer integer = entry.getValue();
            if (integer < 0){
                enchantments.remove(enchantment);
            } else {
                enchantments.put(enchantment, integer);
            }
        }
        EnchantmentHelper.setEnchantments(enchantments, itemStack);
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        ItemStack itemStack = new ItemStack(this);
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof IEnchantedBlock) {
            this.setEnchantments(itemStack, tileEntity);
        }
        return itemStack;
    }

    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pTe, ItemStack pStack) {
        ItemStack itemStack = new ItemStack(this);
        if (pTe instanceof IEnchantedBlock) {
            this.setEnchantments(itemStack, pTe);
        }
        popResource(pLevel, pPos, itemStack);
        super.playerDestroy(pLevel, pPlayer, pPos, pState, pTe, pStack);
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        BlockEntity tileentity = pLevel.getBlockEntity(pPos);
        if (pPlacer instanceof Player){
            if (tileentity instanceof IEnchantedBlock blockEntity){
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(pStack);
                enchantments.keySet().removeIf(enchantment -> !this.asItem().canApplyAtEnchantingTable(pStack, enchantment));
                blockEntity.getEnchantments().putAll(enchantments);
            }
        }
    }
}
