package com.Polarice3.Goety.common.ritual;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BookItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

import java.util.List;
import java.util.Map;

public class EnchantItemRitual extends Ritual{

    public EnchantItemRitual(RitualRecipe recipe) {
        super(recipe);
    }

    public boolean isValid(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                           Player castingPlayer, ItemStack activationItem,
                           List<Ingredient> remainingAdditionalIngredients) {
        return this.recipe.getEnchantment() != null
                && (activationItem.canApplyAtEnchantingTable(this.recipe.getEnchantment())
                || this.recipe.getEnchantment().canEnchant(activationItem)
                || activationItem.getItem() instanceof EnchantedBookItem)
                && compatibleEnchant(activationItem)
                && this.areAdditionalIngredientsFulfilled(world, darkAltarPos, castingPlayer, remainingAdditionalIngredients);
    }

    public boolean identify(Level world, BlockPos darkAltarPos, Player player, ItemStack activationItem) {
        return this.recipe.getEnchantment() != null
                && this.areAdditionalIngredientsFulfilled(world, darkAltarPos, player, this.recipe.getIngredients());
    }

    public int getLevelCost(ItemStack activationItem){
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(activationItem);
        if (activationItem.isEnchanted()){
            if (map.containsKey(this.recipe.getEnchantment())){
                return this.recipe.getXPLevelCost() * (map.get(this.recipe.getEnchantment()) + 1);
            }
        }
        return this.recipe.getXPLevelCost();
    }

    public boolean compatibleEnchant(ItemStack activationItem){
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(activationItem);
        if (activationItem.isEnchanted()){
            return EnchantmentHelper.isEnchantmentCompatible(map.keySet(), this.recipe.getEnchantment())
                    || activationItem.getItem() instanceof BookItem
                    || activationItem.getItem() instanceof EnchantedBookItem
                    || map.containsKey(this.recipe.getEnchantment());
        } else {
            return true;
        }
    }

    @Override
    public void finish(Level world, BlockPos blockPos, DarkAltarBlockEntity tileEntity,
                       Player castingPlayer, ItemStack activationItem) {
        super.finish(world, blockPos, tileEntity, castingPlayer, activationItem);

        for(int i = 0; i < 20; ++i) {
            double d0 = (double)blockPos.getX() + world.random.nextDouble();
            double d1 = (double)blockPos.getY() + world.random.nextDouble();
            double d2 = (double)blockPos.getZ() + world.random.nextDouble();
            world.addParticle(ParticleTypes.POOF, d0, d1, d2, 0, 0, 0);
        }

        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(activationItem);
        ItemStack result = activationItem;
        if (result.getItem() instanceof BookItem){
            EnchantmentInstance enchantmentInstance = new EnchantmentInstance(this.recipe.getEnchantment(), 1);
            result = EnchantedBookItem.createForEnchantment(enchantmentInstance);
            activationItem.shrink(1);
        } else {
            if (map.containsKey(this.recipe.getEnchantment())) {
                for (Enchantment enchantment : map.keySet()) {
                    if (enchantment != null && this.recipe.getEnchantment() == enchantment) {
                        int j2 = map.get(enchantment) + 1;
                        if (j2 > enchantment.getMaxLevel()) {
                            j2 = enchantment.getMaxLevel();
                        }
                        map.put(enchantment, j2);
                        EnchantmentHelper.setEnchantments(map, result);
                    }
                }
            } else {
                result.enchant(this.recipe.getEnchantment(), 1);
            }
        }
        result.onCraftedBy(world, castingPlayer, 1);
        IItemHandler handler = tileEntity.itemStackHandler.orElseThrow(RuntimeException::new);
        handler.insertItem(0, result, false);
    }
}
