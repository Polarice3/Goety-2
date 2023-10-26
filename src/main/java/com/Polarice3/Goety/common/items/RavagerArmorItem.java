package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class RavagerArmorItem extends Item {
   private final int protection;
   private final ResourceLocation texture;

   public RavagerArmorItem(int protection, String material) {
      this(protection, Goety.location("textures/entity/servants/ravager/armor/ravager_armor_" + material + ".png"), new Properties().stacksTo(1));
   }

   public RavagerArmorItem(int protection, String material, Properties properties) {
      this(protection, Goety.location("textures/entity/servants/ravager/armor/ravager_armor_" + material + ".png"), properties);
   }

   public RavagerArmorItem(int protection, ResourceLocation texture, Properties properties) {
      super(properties);
      this.protection = protection;
      this.texture = texture;
   }

   public ResourceLocation getTexture() {
      return texture;
   }

   public int getProtection() {
      return this.protection;
   }

   @Override
   public int getEnchantmentValue(ItemStack stack) {
      return 1;
   }

   @Override
   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return super.canApplyAtEnchantingTable(stack, enchantment) && enchantment.category == EnchantmentCategory.ARMOR;
   }
}
