package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.entities.NecroticCandlestickBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;

public class NecroticCandlestick extends CandlestickBlock implements EntityBlock {
    public NecroticCandlestick(Properties p_49795_) {
        super(p_49795_, 6);
    }

    public void onPlace(BlockState p_55724_, Level p_55725_, BlockPos p_55726_, BlockState p_55727_, boolean p_55728_) {
        for(Direction direction : Direction.values()) {
            p_55725_.updateNeighborsAt(p_55726_.relative(direction), this);
        }

    }

    public void onRemove(BlockState p_54647_, Level p_54648_, BlockPos p_54649_, BlockState p_54650_, boolean p_54651_) {
        if (!p_54651_ && !p_54647_.is(p_54650_.getBlock())) {
            if (p_54647_.getValue(LIT)) {
                this.updateNeighbours(p_54647_, p_54648_, p_54649_);
            }

            super.onRemove(p_54647_, p_54648_, p_54649_, p_54650_, p_54651_);
        }
    }

    public boolean isSignalSource(BlockState p_55213_) {
        return p_55213_.hasProperty(LIT);
    }

    public int getSignal(BlockState p_54635_, BlockGetter p_54636_, BlockPos p_54637_, Direction p_54638_) {
        return p_54635_.getValue(LIT) ? 15 : 0;
    }

    public int getDirectSignal(BlockState p_54670_, BlockGetter p_54671_, BlockPos p_54672_, Direction p_54673_) {
        return p_54670_.getValue(LIT) && Direction.UP == p_54673_ ? 15 : 0;
    }

    private void updateNeighbours(BlockState p_54681_, Level p_54682_, BlockPos p_54683_) {
        p_54682_.updateNeighborsAt(p_54683_, this);
        p_54682_.updateNeighborsAt(p_54683_.relative(Direction.UP.getOpposite()), this);
    }

    public void animateTick(BlockState p_220697_, Level p_220698_, BlockPos p_220699_, RandomSource p_220700_) {
        if (p_220697_.getValue(LIT) && p_220697_.getValue(HALF) == DoubleBlockHalf.UPPER) {
            float f = p_220700_.nextFloat();
            if (f < 0.3F) {
                if (f < 0.17F) {
                    p_220698_.playLocalSound(p_220699_.getX() + 0.5D, p_220699_.getY() + 0.5D, p_220699_.getZ() + 0.5D, SoundEvents.CANDLE_AMBIENT, SoundSource.BLOCKS, 1.0F + p_220700_.nextFloat(), p_220700_.nextFloat() * 0.7F + 0.3F, false);
                }
            }

            ParticleOptions particleOptions = ModParticleTypes.SMALL_NECRO_FIRE.get();
            p_220698_.addParticle(particleOptions, p_220699_.getX() + 0.5D, p_220699_.getY() + (8 / 16.0D), p_220699_.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new NecroticCandlestickBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152755_, BlockState p_152756_, BlockEntityType<T> p_152757_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof NecroticCandlestickBlockEntity block)
                block.tick();
        };
    }
}
