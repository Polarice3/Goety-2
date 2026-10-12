package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin {

    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (state.is(Blocks.SOUL_CAMPFIRE)) {
            if (entity instanceof LivingEntity livingEntity && state.getValue(CampfireBlock.LIT) && !EnchantmentHelper.hasFrostWalker(livingEntity)) {
                if (MainConfig.SoulFireBurn.get()) {
                    livingEntity.addEffect(new MobEffectInstance(GoetyEffects.SOUL_BURN.get(), MathHelper.secondsToTicks(1), 0));
                    ci.cancel();
                }
            }
        }
    }
}
