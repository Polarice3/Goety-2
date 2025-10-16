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

    public BlockPlaceContextMixin(Player p_43709_, InteractionHand p_43710_, BlockHitResult p_43711_) {
        super(p_43709_, p_43710_, p_43711_);
    }

    public BlockPlaceContextMixin(Level p_43713_, @Nullable Player p_43714_, InteractionHand p_43715_, ItemStack p_43716_, BlockHitResult p_43717_) {
        super(p_43713_, p_43714_, p_43715_, p_43716_, p_43717_);
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
