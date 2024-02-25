package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.utils.EffectsUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ApplyBonusCount.class)
public class ApplyBonusCountMixin {

    @Shadow
    @Final
    Enchantment enchantment;

    @ModifyVariable(
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "net/minecraft/world/item/enchantment/EnchantmentHelper.getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I"),
            method = "run")
    private int goety$applyFortune(int enchantmentLevel, ItemStack stack, LootContext lootContext) {
        if (this.enchantment == Enchantments.BLOCK_FORTUNE) {
            return enchantmentLevel + EffectsUtil.getFortuneEffectLevel(lootContext);
        } else {
            return enchantmentLevel;
        }
    }
}
