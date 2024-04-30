package com.Polarice3.Goety.common.items.armor;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.ItemConfig;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum ModArmorMaterials implements ArmorMaterial {
    //{feet, legs, chest, head}

    CURSED_KNIGHT("cursed_knight", ItemConfig.CursedKnightDurability.get(),
            new int[]{ItemConfig.CursedKnightFeet.get(),
                    ItemConfig.CursedKnightLegs.get(),
                    ItemConfig.CursedKnightChest.get(),
                    ItemConfig.CursedKnightHead.get()},
            ItemConfig.CursedKnightEnchantability.get(),
            SoundEvents.ARMOR_EQUIP_IRON,
            ItemConfig.CursedKnightToughness.get().floatValue(),
            ItemConfig.CursedKnightKnockResist.get().floatValue(), () -> {
        return Ingredient.of(ModItems.CURSED_METAL_INGOT.get());
    }),
    CURSED_PALADIN("cursed_paladin", ItemConfig.CursedPaladinDurability.get(),
            new int[]{ItemConfig.CursedPaladinFeet.get(),
                    ItemConfig.CursedPaladinLegs.get(),
                    ItemConfig.CursedPaladinChest.get(),
                    ItemConfig.CursedPaladinHead.get()},
            ItemConfig.CursedPaladinEnchantability.get(),
            SoundEvents.ARMOR_EQUIP_IRON,
            ItemConfig.CursedPaladinToughness.get().floatValue(),
            ItemConfig.CursedPaladinKnockResist.get().floatValue(), () -> {
        return Ingredient.of(ModItems.CURSED_METAL_INGOT.get());
    }),
    BLACK_IRON("black_iron", ItemConfig.BlackIronDurability.get(),
            new int[]{ItemConfig.BlackIronFeet.get(),
                    ItemConfig.BlackIronLegs.get(),
                    ItemConfig.BlackIronChest.get(),
                    ItemConfig.BlackIronHead.get()},
            ItemConfig.BlackIronEnchantability.get(),
            SoundEvents.ARMOR_EQUIP_IRON,
            ItemConfig.BlackIronToughness.get().floatValue(),
            ItemConfig.BlackIronKnockResist.get().floatValue(), () -> {
        return Ingredient.of(ModItems.CURSED_METAL_INGOT.get());
    }),
    DARK("dark", ItemConfig.DarkArmorDurability.get(),
            new int[]{ItemConfig.DarkArmorFeet.get(),
                    ItemConfig.DarkArmorLegs.get(),
                    ItemConfig.DarkArmorChest.get(),
                    ItemConfig.DarkArmorHead.get()},
            ItemConfig.DarkArmorEnchantability.get(),
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            ItemConfig.DarkArmorToughness.get().floatValue(),
            ItemConfig.DarkArmorKnockResist.get().floatValue(), () -> {
        return Ingredient.of(ModItems.DARK_METAL_INGOT.get());
    });

    private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
    private final String name;
    private final int durabilityMultiplier;
    private final int[] slotProtections;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    ModArmorMaterials(String name, int durabilityMultiplier, int[] slotProtections, int enchantmentValue, SoundEvent soundEvent, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.slotProtections = slotProtections;
        this.enchantmentValue = enchantmentValue;
        this.sound = soundEvent;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = new LazyLoadedValue<>(repairIngredient);
    }

    public int getDurabilityForSlot(EquipmentSlot p_40484_) {
        return HEALTH_PER_SLOT[p_40484_.getIndex()] * this.durabilityMultiplier;
    }

    public int getDefenseForSlot(EquipmentSlot p_40487_) {
        return this.slotProtections[p_40487_.getIndex()];
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public SoundEvent getEquipSound() {
        return this.sound;
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    public String getName() {
        return this.name;
    }

    public float getToughness() {
        return this.toughness;
    }

    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
