package com.Polarice3.Goety.common.items.armor;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.ItemConfig;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.function.Supplier;

public enum ModArmorMaterials implements ArmorMaterial {
    //{feet, legs, chest, head}

    CURSED_KNIGHT("cursed_knight", ItemConfig.CursedKnightDurability.get(),
            Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266652_) -> {
                p_266652_.put(ArmorItem.Type.BOOTS, ItemConfig.CursedKnightFeet.get());
                p_266652_.put(ArmorItem.Type.LEGGINGS, ItemConfig.CursedKnightLegs.get());
                p_266652_.put(ArmorItem.Type.CHESTPLATE, ItemConfig.CursedKnightChest.get());
                p_266652_.put(ArmorItem.Type.HELMET, ItemConfig.CursedKnightHead.get());
            }),
            ItemConfig.CursedKnightEnchantability.get(),
            SoundEvents.ARMOR_EQUIP_IRON,
            ItemConfig.CursedKnightToughness.get().floatValue(),
            ItemConfig.CursedKnightKnockResist.get().floatValue(), () -> {
        return Ingredient.of(ModItems.CURSED_METAL_INGOT.get());
    }),
    CURSED_PALADIN("cursed_paladin", ItemConfig.CursedPaladinDurability.get(),
            Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266652_) -> {
                p_266652_.put(ArmorItem.Type.BOOTS, ItemConfig.CursedPaladinFeet.get());
                p_266652_.put(ArmorItem.Type.LEGGINGS, ItemConfig.CursedPaladinLegs.get());
                p_266652_.put(ArmorItem.Type.CHESTPLATE, ItemConfig.CursedPaladinChest.get());
                p_266652_.put(ArmorItem.Type.HELMET, ItemConfig.CursedPaladinHead.get());
            }),
            ItemConfig.CursedPaladinEnchantability.get(),
            SoundEvents.ARMOR_EQUIP_IRON,
            ItemConfig.CursedPaladinToughness.get().floatValue(),
            ItemConfig.CursedPaladinKnockResist.get().floatValue(), () -> {
        return Ingredient.of(ModItems.CURSED_METAL_INGOT.get());
    }),
    BLACK_IRON("black_iron", ItemConfig.BlackIronDurability.get(),
            Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266652_) -> {
                p_266652_.put(ArmorItem.Type.BOOTS, ItemConfig.BlackIronFeet.get());
                p_266652_.put(ArmorItem.Type.LEGGINGS, ItemConfig.BlackIronLegs.get());
                p_266652_.put(ArmorItem.Type.CHESTPLATE, ItemConfig.BlackIronChest.get());
                p_266652_.put(ArmorItem.Type.HELMET, ItemConfig.BlackIronHead.get());
            }),
            ItemConfig.BlackIronEnchantability.get(),
            SoundEvents.ARMOR_EQUIP_IRON,
            ItemConfig.BlackIronToughness.get().floatValue(),
            ItemConfig.BlackIronKnockResist.get().floatValue(), () -> {
        return Ingredient.of(ModItems.CURSED_METAL_INGOT.get());
    }),
    DARK("dark", ItemConfig.DarkArmorDurability.get(),
            Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266652_) -> {
                p_266652_.put(ArmorItem.Type.BOOTS, ItemConfig.DarkArmorFeet.get());
                p_266652_.put(ArmorItem.Type.LEGGINGS, ItemConfig.DarkArmorLegs.get());
                p_266652_.put(ArmorItem.Type.CHESTPLATE, ItemConfig.DarkArmorChest.get());
                p_266652_.put(ArmorItem.Type.HELMET, ItemConfig.DarkArmorHead.get());
            }),
            ItemConfig.DarkArmorEnchantability.get(),
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            ItemConfig.DarkArmorToughness.get().floatValue(),
            ItemConfig.DarkArmorKnockResist.get().floatValue(), () -> {
        return Ingredient.of(ModItems.DARK_METAL_INGOT.get());
    });

    private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
    private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266653_) -> {
        p_266653_.put(ArmorItem.Type.BOOTS, 13);
        p_266653_.put(ArmorItem.Type.LEGGINGS, 15);
        p_266653_.put(ArmorItem.Type.CHESTPLATE, 16);
        p_266653_.put(ArmorItem.Type.HELMET, 11);
    });
    private final String name;
    private final int durabilityMultiplier;
    private final EnumMap<ArmorItem.Type, Integer> protectionFunctionForType;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    ModArmorMaterials(String name, int durabilityMultiplier, EnumMap<ArmorItem.Type, Integer> protectionFunctionForType, int enchantmentValue, SoundEvent soundEvent, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionFunctionForType = protectionFunctionForType;
        this.enchantmentValue = enchantmentValue;
        this.sound = soundEvent;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = new LazyLoadedValue<>(repairIngredient);
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type p_266745_) {
        return HEALTH_FUNCTION_FOR_TYPE.get(p_266745_) * this.durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type p_266752_) {
        return this.protectionFunctionForType.get(p_266752_);
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
