package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin extends Fluid {

    @Inject(method = "canHoldFluid", at = @At("HEAD"), cancellable = true)
    private void canHoldFluid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid, CallbackInfoReturnable<Boolean> callback) {
        if (state.is(ModBlocks.HOLE.get())) {
            callback.setReturnValue(false);
        }
    }
}
