package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockPlaceContext.class)
public abstract class BlockPlaceContextMixin extends UseOnContext {
    public BlockPlaceContextMixin(Level level, @Nullable Player player, InteractionHand hand, ItemStack stack, BlockHitResult hitResult) {
        super(level, player, hand, stack, hitResult);
    }

    @Inject(
            method = {"canPlace()Z"},
            at = @At(value = "HEAD"),
            cancellable = true
    )
    public void goety_canPlace(CallbackInfoReturnable<Boolean> cir) {
        if (this.getPlayer() != null) {
            if (this.getPlayer().hasEffect(GoetyEffects.IMPAIRED.get())) {
                cir.setReturnValue(false);
            }
        }
    }
}
