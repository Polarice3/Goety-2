package com.Polarice3.Goety.compat.jei;

import net.minecraft.world.item.ItemStack;

public class WitchBrewJeiRecipe {
    public ItemStack catalyst;
    public ItemStack output;
    public int capacity;
    public int soulCost;

    public WitchBrewJeiRecipe(ItemStack catalyst, ItemStack output, int capacity, int soulCost) {
        this.catalyst = catalyst;
        this.output = output;
        this.capacity = capacity;
        this.soulCost = soulCost;
    }

    public ItemStack getCatalyst() {
        return catalyst;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getCapacity(){
        return this.capacity;
    }

    public int getSoulCost(){
        return this.soulCost;
    }
}
