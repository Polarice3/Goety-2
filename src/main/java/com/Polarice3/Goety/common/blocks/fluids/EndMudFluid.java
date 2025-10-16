package com.Polarice3.Goety.common.blocks.fluids;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class EndMudFluid extends FlowingFluid {

    public Fluid getFlowing() {
        return ModFluids.END_MUD_FLUID_FLOWING.get();
    }

    public Fluid getSource() {
        return ModFluids.END_MUD_FLUID_SOURCE.get();
    }

    public Item getBucket() {
        return ModItems.END_MUD_BUCKET.get();
    }

    public int getSlopeFindDistance(LevelReader p_76244_) {
        return 2;
    }

    public BlockState createLegacyBlock(FluidState p_76249_) {
        return ModBlocks.END_MUD_FLUID.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(p_76249_));
    }

    public boolean isSame(Fluid p_76231_) {
        return p_76231_ == ModFluids.END_MUD_FLUID_SOURCE.get() || p_76231_ == ModFluids.END_MUD_FLUID_FLOWING.get();
    }

    public int getDropOff(LevelReader p_76252_) {
        return 3;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluidIn, Direction direction) {
        return direction == Direction.DOWN && !isSame(fluidIn);
    }

    public int getTickDelay(LevelReader p_76226_) {
        return 20;
    }

    protected float getExplosionResistance() {
        return 100.0F;
    }

    @NotNull
    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

    protected boolean canConvertToSource(Level p_256295_) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
        Block.dropResources(state, worldIn, pos, blockEntity);
    }

    public void animateTick(Level p_230567_, BlockPos p_230568_, FluidState p_230569_, RandomSource p_230570_) {
        BlockPos blockpos = p_230568_.above();
        if (p_230569_.isSource() && p_230567_.getBlockState(blockpos).isAir() && !p_230567_.getBlockState(blockpos).isSolidRender(p_230567_, blockpos)) {
            if (p_230570_.nextInt(10) == 0) {
                Vec3 vec3 = blockpos.getCenter();
                double d0 = vec3.x() + (p_230570_.nextDouble() * p_230570_.nextInt(-1, 1));
                double d1 = vec3.y() + (p_230570_.nextDouble() / 2.0D);
                double d2 = vec3.z() + (p_230570_.nextDouble() * p_230570_.nextInt(-1, 1));
                p_230567_.addParticle(ModParticleTypes.MUD_GAS.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }

    }

    @Override
    public @NotNull FluidType getFluidType() {
        return ModFluids.END_MUD_FLUID_TYPE.get();
    }

    public static class Flowing extends EndMudFluid {
        public Flowing() {
            super();
            this.registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

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

    public static class Source extends EndMudFluid {
        public int getAmount(FluidState state) {
            return 8;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
