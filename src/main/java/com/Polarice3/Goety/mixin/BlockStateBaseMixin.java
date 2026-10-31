package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.blocks.entities.SealerHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockStateBaseMixin {
    @Inject(method = "getDestroyProgress", at = @At("HEAD"), cancellable = true)
    private void goety$sealedCannotBeMined(Player player, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (level instanceof LevelAccessor accessor && SealerHooks.isSealed(accessor, pos)) {
            cir.setReturnValue(-1.0F);
        }
    }
}
