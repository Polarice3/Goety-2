package com.Polarice3.Goety.mixin.compat;

import com.Polarice3.Goety.common.items.equipment.EeriePickaxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "io/github/strikerrocker/vt/enchantments/SiphonEnchantment", remap = false)
public abstract class SiphonEnchantmentMixin {
    @Inject(
            method = {"canEnchant", "m_6081_"},
            at = @At("RETURN"),
            remap = false,
            cancellable = true
    )
    protected void canEnchant(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Enchantment enchantment = (Enchantment) (Object) this;
        boolean allow = cir.getReturnValue();
        if (!allow && enchantment.canApplyAtEnchantingTable(stack)) {
            cir.setReturnValue(true);
        }
        //Siphon's redundant on Eerie Pickaxe.
        if (allow && stack.getItem() instanceof EeriePickaxeItem) {
            cir.setReturnValue(false);
        }
    }
}
