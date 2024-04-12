package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.enchantments.FocusEnchantments;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {

    @ModifyVariable(method = "run", at = @At("STORE"))
    private List<Enchantment> filterEnchants(List<Enchantment> enchantments) {
        enchantments.removeIf(enchantment -> enchantment instanceof FocusEnchantments);
        return enchantments;
    }
}
