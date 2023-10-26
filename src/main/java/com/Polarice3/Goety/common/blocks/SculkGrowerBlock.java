package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.blocks.entities.SculkGrowerBlockEntity;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
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

import javax.annotation.Nullable;

public class SculkGrowerBlock extends EnchanteableBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty CHARGED = BooleanProperty.create("charged");

    public SculkGrowerBlock() {
        super(Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .strength(3.0F, 3.0F)
                .sound(SoundType.SCULK_CATALYST)
                .lightLevel((blockState) -> 6));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LIT, Boolean.valueOf(false))
                .setValue(POWERED, Boolean.valueOf(false))
                .setValue(CHARGED, Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_222115_) {
        p_222115_.add(LIT, POWERED, CHARGED);
    }

    public void neighborChanged(BlockState p_55666_, Level p_55667_, BlockPos p_55668_, Block p_55669_, BlockPos p_55670_, boolean p_55671_) {
        if (!p_55667_.isClientSide) {
            boolean flag = p_55666_.getValue(POWERED);
            boolean flag1 = p_55667_.hasNeighborSignal(p_55668_);
            if (flag1 != flag) {
                p_55667_.setBlock(p_55668_, p_55666_.setValue(POWERED, Boolean.valueOf(flag1)), 3);
            }

        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new SculkGrowerBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return (world, pos, state, blockEntity) -> {
            if (!world.isClientSide) {
                if (blockEntity instanceof SculkGrowerBlockEntity blockEntity1) {
                    if (MainConfig.SculkGrowerContinue.get()) {
                        if (state.getValue(POWERED) || blockEntity1.getGrowCharges() > 0) {
                            blockEntity1.tick();
                        }
                    } else {
                        if (state.getValue(POWERED)) {
                            blockEntity1.tick();
                        } else {
                            blockEntity1.decayCharges();
                        }
                    }
                    blockEntity1.commonTick();
                }
            }
        };
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRand) {
        if (pState.getValue(POWERED)){
            BlockFinder.spawnRedstoneParticles(pLevel, pPos);
        }
    }

    public RenderShape getRenderShape(BlockState p_222120_) {
        return RenderShape.MODEL;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_55659_) {
        return this.defaultBlockState().setValue(POWERED, Boolean.valueOf(p_55659_.getLevel().hasNeighborSignal(p_55659_.getClickedPos())));
    }
}
