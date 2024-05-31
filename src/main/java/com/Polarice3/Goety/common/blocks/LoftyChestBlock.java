package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.LoftyChestBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

public class LoftyChestBlock extends ModChestBlock {
    public LoftyChestBlock(Properties p_51490_) {
        super(p_51490_, ModBlockEntities.LOFTY_CHEST::get);
    }

    public DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(BlockState p_53149_, Level p_53150_, BlockPos p_53151_, boolean p_53152_) {
        return DoubleBlockCombiner.Combiner::acceptNone;
    }

    public BlockState updateShape(BlockState p_51555_, Direction p_51556_, BlockState p_51557_, LevelAccessor p_51558_, BlockPos p_51559_, BlockPos p_51560_) {
        if (p_51555_.getValue(WATERLOGGED)) {
            p_51558_.scheduleTick(p_51559_, Fluids.WATER, Fluids.WATER.getTickDelay(p_51558_));
        }

        return super.updateShape(p_51555_, p_51556_, p_51557_, p_51558_, p_51559_, p_51560_);
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_53128_) {
        FluidState fluidstate = p_53128_.getLevel().getFluidState(p_53128_.getClickedPos());
        return this.defaultBlockState().setValue(FACING, p_53128_.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
    }

    public InteractionResult use(BlockState p_51531_, Level p_51532_, BlockPos p_51533_, Player p_51534_, InteractionHand p_51535_, BlockHitResult p_51536_) {
        if (p_51532_.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = p_51532_.getBlockEntity(p_51533_);
            if (blockentity instanceof LoftyChestBlockEntity blockEntity) {
                p_51534_.openMenu(blockEntity);
                p_51534_.awardStat(this.getOpenChestStat());
                PiglinAi.angerNearbyPiglins(p_51534_, true);
            }

            return InteractionResult.CONSUME;
        }
    }

    public BlockState mirror(BlockState p_53154_, Mirror p_53155_) {
        return p_53154_.rotate(p_53155_.getRotation(p_53154_.getValue(FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_153064_, BlockState p_153065_) {
        return new LoftyChestBlockEntity(p_153064_, p_153065_);
    }
}
