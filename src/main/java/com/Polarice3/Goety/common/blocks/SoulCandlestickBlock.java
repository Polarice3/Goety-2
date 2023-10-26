package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.SoulCandlestickBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToIntFunction;

public class SoulCandlestickBlock extends BaseEntityBlock {
    protected static final VoxelShape SHAPE_BASE = Block.box(6.0D, 0.0D, 6.0D,
            10.0D, 1.0D, 10.0D);
    protected static final VoxelShape SHAPE_BASE_2 = Block.box(6.25D, 1.0D, 6.25D,
            9.75D, 2.0D, 9.75D);
    protected static final VoxelShape SHAPE_MID = Block.box(7.5D, 2.0D, 7.5D,
            8.5D, 6.0D, 8.5D);
    protected static final VoxelShape SHAPE_TOP = Block.box(7.0D, 6.0D, 7.0D,
            9.0D, 7.0D, 9.0D);
    protected static final VoxelShape SHAPE_CANDLE = Block.box(7.5D, 7.0D, 7.5D,
            8.5D, 11.0D, 8.5D);
    public static final VoxelShape SHAPE = Shapes.or(SHAPE_BASE, SHAPE_BASE_2, SHAPE_MID, SHAPE_TOP, SHAPE_CANDLE);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public SoulCandlestickBlock() {
        super(Properties.of()
                .mapColor(MapColor.METAL)
                .strength(0.1F)
                .sound(SoundType.CANDLE)
                .lightLevel(litBlockEmission())
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, Boolean.FALSE));
    }

    private static ToIntFunction<BlockState> litBlockEmission() {
        return (state) -> state.getValue(BlockStateProperties.LIT) ? 10 : 0;
    }

    public RenderShape getRenderShape(BlockState p_222219_) {
        return RenderShape.MODEL;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LIT);
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRand) {
        if (pState.getValue(LIT)) {
            float f = pRand.nextFloat();
            pLevel.addParticle(ParticleTypes.SOUL_FIRE_FLAME, pPos.getX() + 0.5F, pPos.getY() + 0.85F, pPos.getZ() + 0.5F, 0, 0, 0);
            if (f < 0.3F) {
                pLevel.addParticle(ParticleTypes.SMOKE, pPos.getX() + 0.5F, pPos.getY() + 0.75F, pPos.getZ() + 0.5F, 0.0D, 0.0D, 0.0D);
                if (f < 0.17F) {
                    pLevel.playLocalSound(pPos.getX() + 0.5D, pPos.getY() + 0.5D, pPos.getZ() + 0.5D, SoundEvents.CANDLE_AMBIENT, SoundSource.BLOCKS, 1.0F + pRand.nextFloat(), pRand.nextFloat() * 0.7F + 0.3F, false);
                }
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new SoulCandlestickBlockEntity(p_153215_, p_153216_);
    }

    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152755_, BlockState p_152756_, BlockEntityType<T> p_152757_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof SoulCandlestickBlockEntity arcaBlock)
                arcaBlock.tick();
        };
    }
}
