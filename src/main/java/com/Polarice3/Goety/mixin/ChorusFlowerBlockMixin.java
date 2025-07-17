package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChorusFlowerBlock.class)
public abstract class ChorusFlowerBlockMixin extends Block {

    @Shadow @Final private ChorusPlantBlock plant;

    public ChorusFlowerBlockMixin(Properties p_49795_) {
        super(p_49795_);
    }

    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    private void canSurvive(BlockState state, LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        BlockState blockState = world.getBlockState(pos.below());
        if (blockState.is(ModTags.Blocks.CHORUS_GROW)) {
            info.setReturnValue(true);
            info.cancel();
        }
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo info) {
        BlockState blockState = world.getBlockState(pos.below());
        if (blockState.is(ModTags.Blocks.CHORUS_GROW)) {
            BlockPos up = pos.above();
            if (world.isEmptyBlock(up) && up.getY() < world.getMaxBuildHeight()) {
                int i = state.getValue(ChorusFlowerBlock.AGE);
                if (i < 5) {
                    this.placeGrownFlower(world, up, i + 1);
                    world.setBlock(pos, this.plant.defaultBlockState()
                            .setValue(ChorusPlantBlock.UP, true)
                            .setValue(ChorusPlantBlock.DOWN, true), 2);
                    info.cancel();
                }
            }
        }
    }

    @Shadow
    private void placeGrownFlower(Level world, BlockPos pos, int age) {
    }
}
