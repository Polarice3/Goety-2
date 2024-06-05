package com.Polarice3.Goety.compat.jei;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public class WitchBrewJeiRecipe{
    public ItemStack catalyst;
    public ItemStack output;

    public WitchBrewJeiRecipe(ItemStack catalyst, ItemStack output) {
        this.catalyst = catalyst;
        this.output = output;
    }

    public ItemStack getCatalyst() {
        return catalyst;
    }

    public ItemStack getOutput() {
        return output;
    }
}
