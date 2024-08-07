package com.Polarice3.Goety.common.crafting;

import com.Polarice3.Goety.common.items.magic.TaglockKit;
import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;

public class TaglockRecipe extends CustomRecipe {
    public TaglockRecipe(ResourceLocation p_252125_) {
        super(p_252125_);
    }

    @Override
    public boolean matches(CraftingContainer p_44002_, Level p_44003_) {
        List<ItemStack> list = Lists.newArrayList();

        for(int i = 0; i < p_44002_.getContainerSize(); ++i) {
            ItemStack itemstack = p_44002_.getItem(i);
            if (!itemstack.isEmpty()) {
                list.add(itemstack);
                if (list.size() > 1) {
                    ItemStack itemstack1 = list.get(0);
                    if (itemstack1.getCount() != 1 || itemstack.getCount() != 1) {
                        return false;
                    }
                }
            }
        }

        return list.size() == 2;
    }

    @Override
    public ItemStack assemble(CraftingContainer p_44001_) {
        List<ItemStack> list = Lists.newArrayList();
        for(int i = 0; i < p_44001_.getContainerSize(); ++i) {
            ItemStack itemstack = p_44001_.getItem(i);
            if (!itemstack.isEmpty()) {
                list.add(itemstack);
                if (list.size() > 1) {
                    ItemStack itemstack1 = list.get(0);
                    if (itemstack1.getCount() != 1 || itemstack.getCount() != 1) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        if (list.size() == 2) {
            ItemStack itemstack3 = list.get(0);
            ItemStack itemstack4 = list.get(1);
            if (itemstack3.getCount() == 1 && itemstack4.getCount() == 1) {
                if (itemstack3.getItem() instanceof TaglockKit
                && TaglockKit.hasEntity(itemstack3)){
                    LivingEntity livingEntity = TaglockKit.getEntity(itemstack3);
                    if (livingEntity != null){
                        ItemStack itemstack2 = new ItemStack(itemstack4.getItem());
                        TaglockKit.setEntity(itemstack2, livingEntity);
                        return itemstack2;
                    }
                } else if (itemstack4.getItem() instanceof TaglockKit
                        && TaglockKit.hasEntity(itemstack4)){
                    LivingEntity livingEntity = TaglockKit.getEntity(itemstack4);
                    if (livingEntity != null){
                        ItemStack itemstack2 = new ItemStack(itemstack3.getItem());
                        TaglockKit.setEntity(itemstack2, livingEntity);
                        return itemstack2;
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return p_43999_ * p_44000_ >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }
}
