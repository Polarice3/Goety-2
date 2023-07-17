package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class RavagerArmorItem extends Item {
   private final int protection;
   private final ResourceLocation texture;

   public RavagerArmorItem(int protection, String material) {
      this(protection, Goety.location("textures/entity/servants/ravager/armor/ravager_armor_" + material + ".png"), new Item.Properties().stacksTo(1).tab(Goety.TAB));
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
}
