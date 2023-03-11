package com.Polarice3.Goety.common.enchantments;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.items.curios.RingItem;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEnchantments {
    public static DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Goety.MOD_ID);

    public static final EnchantmentCategory RINGS = EnchantmentCategory.create("rings", (item) -> (item instanceof RingItem));
    public static final EnchantmentCategory FOCUS = EnchantmentCategory.create("focus", (item) -> (item instanceof MagicFocus));

    public static final RegistryObject<Enchantment> SOUL_EATER = ENCHANTMENTS.register("soul_eater",
            () -> new SoulEaterEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.BREAKABLE, EquipmentSlot.MAINHAND));

    public static final RegistryObject<Enchantment> WANTING = ENCHANTMENTS.register("wanting",
            () -> new LootingEnchantment(Enchantment.Rarity.RARE, RINGS, EquipmentSlot.MAINHAND));

    public static final RegistryObject<Enchantment> POTENCY = ENCHANTMENTS.register("potency",
            () -> new PotencyEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));

    public static final RegistryObject<Enchantment> RADIUS = ENCHANTMENTS.register("radius",
            () -> new RadiusEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));

    public static final RegistryObject<Enchantment> BURNING = ENCHANTMENTS.register("burning",
            () -> new BurningEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND));

    public static final RegistryObject<Enchantment> RANGE = ENCHANTMENTS.register("range",
            () -> new RangeEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND));

    public static final RegistryObject<Enchantment> ABSORB = ENCHANTMENTS.register("absorb",
            () -> new AbsorbEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND));

    public static final RegistryObject<Enchantment> DURATION = ENCHANTMENTS.register("duration",
            () -> new DurationEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));

}
