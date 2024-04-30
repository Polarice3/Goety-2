package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.config.ItemConfig;
import com.google.common.base.Suppliers;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum ModTiers implements Tier {
    SPECIAL(ItemConfig.SpecialToolsMiningLevel.get(),
            ItemConfig.SpecialToolsDurability.get(),
            ItemConfig.SpecialToolsBreakSpeed.get().floatValue(),
            ItemConfig.SpecialToolsDamage.get().floatValue(),
            ItemConfig.SpecialToolsEnchantability.get(), () -> {
        return Ingredient.of(ModItems.CURSED_METAL_INGOT.get());
    }),
    DARK(ItemConfig.DarkToolsMiningLevel.get(),
            ItemConfig.DarkToolsDurability.get(),
            ItemConfig.DarkToolsBreakSpeed.get().floatValue(),
            ItemConfig.DarkToolsDamage.get().floatValue(),
            ItemConfig.DarkToolsEnchantability.get(), () -> {
        return Ingredient.of(ModItems.DARK_METAL_INGOT.get());
    }),
    DEATH(4,
            ItemConfig.DeathScytheDurability.get(),
            12.0F,
            ItemConfig.DeathScytheDamage.get().floatValue(),
            ItemConfig.DeathScytheEnchantability.get(), () -> {
        return Ingredient.of(Items.BONE);
    });

    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final Supplier<Ingredient> repairIngredient;

    ModTiers(int pLevel, int pUses, float pSpeed, float pDamage, int pEnchantmentValue, Supplier<Ingredient> pRepairIngredient) {
        this.level = pLevel;
        this.uses = pUses;
        this.speed = pSpeed;
        this.damage = pDamage;
        this.enchantmentValue = pEnchantmentValue;
        this.repairIngredient = Suppliers.memoize(pRepairIngredient::get);
    }

    public int getUses() {
        return this.uses;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getAttackDamageBonus() {
        return this.damage;
    }

    public int getLevel() {
        return this.level;
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

}
