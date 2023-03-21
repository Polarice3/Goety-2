package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.TallSkullBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Wearable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TallSkullBlock extends BaseEntityBlock implements Wearable {
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 10.0D, 12.0D);

    public TallSkullBlock() {
        super(Properties.of(Material.DECORATION)
                .strength(1.0F)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(ROTATION, 0));
    }

    public VoxelShape getShape(BlockState p_56331_, BlockGetter p_56332_, BlockPos p_56333_, CollisionContext p_56334_) {
        return SHAPE;
    }

    public VoxelShape getOcclusionShape(BlockState p_56336_, BlockGetter p_56337_, BlockPos p_56338_) {
        return Shapes.empty();
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_56321_) {
        return this.defaultBlockState().setValue(ROTATION, Integer.valueOf(Mth.floor((double)(p_56321_.getRotation() * 16.0F / 360.0F) + 0.5D) & 15));
    }

    public BlockState rotate(BlockState p_56326_, Rotation p_56327_) {
        return p_56326_.setValue(ROTATION, Integer.valueOf(p_56327_.rotate(p_56326_.getValue(ROTATION), 16)));
    }

    public BlockState mirror(BlockState p_56323_, Mirror p_56324_) {
        return p_56323_.setValue(ROTATION, Integer.valueOf(p_56324_.mirror(p_56323_.getValue(ROTATION), 16)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(ROTATION);
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    public BlockEntity newBlockEntity(BlockPos p_151996_, BlockState p_151997_) {
        return new TallSkullBlockEntity(p_151996_, p_151997_);
    }
}
