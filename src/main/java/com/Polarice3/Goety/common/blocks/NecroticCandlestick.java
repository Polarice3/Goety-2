package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.entities.NecroticCandlestickBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.SoulCandlestickBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
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

    public void animateTick(BlockState p_220697_, Level p_220698_, BlockPos p_220699_, RandomSource p_220700_) {
        if (p_220697_.getValue(LIT) && p_220697_.getValue(HALF) == DoubleBlockHalf.UPPER) {
            float f = p_220700_.nextFloat();
            if (f < 0.3F) {
                if (f < 0.17F) {
                    p_220698_.playLocalSound(p_220699_.getX() + 0.5D, p_220699_.getY() + 0.5D, p_220699_.getZ() + 0.5D, SoundEvents.CANDLE_AMBIENT, SoundSource.BLOCKS, 1.0F + p_220700_.nextFloat(), p_220700_.nextFloat() * 0.7F + 0.3F, false);
                }
            }

            p_220698_.addParticle(ModParticleTypes.SMALL_NECRO_FIRE.get(), p_220699_.getX() + 0.5D, p_220699_.getY() + (8 / 16.0D), p_220699_.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
            p_220698_.addParticle(ModParticleTypes.SMALL_NECRO_FIRE_DROP.get(), p_220699_.getX() + 0.5D, p_220699_.getY() + (8 / 16.0D), p_220699_.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
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
