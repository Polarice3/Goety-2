package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.IceBouquetTrapBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;

public class IceBouquetTrapBlock extends BaseEntityBlock {
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public IceBouquetTrapBlock() {
        super(ModBlocks.ShadeStoneProperties());
        this.registerDefaultState(this.stateDefinition.any().setValue(TRIGGERED, Boolean.FALSE).setValue(POWERED, Boolean.FALSE));
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        BlockEntity tileentity = pLevel.getBlockEntity(pPos);
        if (pPlacer instanceof Player){
            if (tileentity instanceof IceBouquetTrapBlockEntity ghostFireTrapTile){
                ghostFireTrapTile.setOwner(pPlacer);
            }
        }
    }

    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        boolean flag = pLevel.hasNeighborSignal(pPos) || pLevel.hasNeighborSignal(pPos.above());
        boolean flag1 = pState.getValue(TRIGGERED);
        if (flag && !flag1 && !pLevel.getBlockState(pPos.above()).isSolidRender(pLevel, pPos.above())) {
            BlockEntity tileentity = pLevel.getBlockEntity(pPos);
            if (tileentity instanceof IceBouquetTrapBlockEntity ghostFireTrapTile) {
                ghostFireTrapTile.fire();
            }
            pLevel.setBlock(pPos, pState.setValue(TRIGGERED, Boolean.TRUE), 4);
        } else if (!flag && flag1) {
            pLevel.setBlock(pPos, pState.setValue(TRIGGERED, Boolean.FALSE), 4);
        }

    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(TRIGGERED, POWERED);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new IceBouquetTrapBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152755_, BlockState p_152756_, BlockEntityType<T> p_152757_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof IceBouquetTrapBlockEntity blockEntity1)
                blockEntity1.tick();
        };
    }
}
