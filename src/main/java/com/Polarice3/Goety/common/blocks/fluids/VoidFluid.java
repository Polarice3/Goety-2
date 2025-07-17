package com.Polarice3.Goety.common.blocks.fluids;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class VoidFluid extends FlowingFluid {

    public Fluid getFlowing() {
        return ModFluids.VOID_FLUID_FLOWING.get();
    }

    public Fluid getSource() {
        return ModFluids.VOID_FLUID_SOURCE.get();
    }

    public Item getBucket() {
        return ModItems.VOID_BUCKET.get();
    }

    public int getSlopeFindDistance(LevelReader p_76244_) {
        return p_76244_.dimensionType().effectsLocation() == BuiltinDimensionTypes.END_EFFECTS ? 4 : 2;
    }

    public BlockState createLegacyBlock(FluidState p_76249_) {
        return ModBlocks.VOID_FLUID.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(p_76249_));
    }

    public boolean isSame(Fluid p_76231_) {
        return p_76231_ == ModFluids.VOID_FLUID_SOURCE.get() || p_76231_ == ModFluids.VOID_FLUID_FLOWING.get();
    }

    public int getDropOff(LevelReader p_76252_) {
        return p_76252_.dimensionType().effectsLocation() == BuiltinDimensionTypes.END_EFFECTS ? 1 : 2;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluidIn, Direction direction) {
        return direction == Direction.DOWN && !isSame(fluidIn);
    }

    public int getTickDelay(LevelReader p_76226_) {
        return p_76226_.dimensionType().effectsLocation() == BuiltinDimensionTypes.END_EFFECTS ? 10 : 30;
    }

    public int getSpreadDelay(Level p_76203_, BlockPos p_76204_, FluidState p_76205_, FluidState p_76206_) {
        int i = this.getTickDelay(p_76203_);
        if (!p_76205_.isEmpty() && !p_76206_.isEmpty() && !p_76205_.getValue(FALLING) && !p_76206_.getValue(FALLING) && p_76206_.getHeight(p_76203_, p_76204_) > p_76205_.getHeight(p_76203_, p_76204_) && p_76203_.getRandom().nextInt(4) != 0) {
            i *= 4;
        }

        return i;
    }

    protected float getExplosionResistance() {
        return 100.0F;
    }

    @NotNull
    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL_LAVA);
    }

    protected boolean canConvertToSource(Level p_256295_) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
        Block.dropResources(state, worldIn, pos, blockEntity);
    }

    @Override
    public @NotNull FluidType getFluidType() {
        return ModFluids.VOID_FLUID_TYPE.get();
    }

    public static class Flowing extends VoidFluid {
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends VoidFluid {
        public int getAmount(FluidState state) {
            return 8;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
