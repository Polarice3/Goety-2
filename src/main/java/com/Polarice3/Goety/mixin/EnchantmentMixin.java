package com.Polarice3.Goety.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

    @Shadow public abstract String getDescriptionId();

    @Inject(
            method = {"canEnchant(Lnet/minecraft/world/item/ItemStack;)Z"},
            at = @At(value = "HEAD"),
            cancellable = true
    )
    public void canEnchant(ItemStack p_44689_, CallbackInfoReturnable<Boolean> cir) {
        Enchantment enchantment = (Enchantment) (Object) this;
        Enchantment siphon = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation("vanillatweaks", "siphon"));
        if (siphon != null) {
            if (enchantment == siphon) {
                if (p_44689_.canApplyAtEnchantingTable(enchantment)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
