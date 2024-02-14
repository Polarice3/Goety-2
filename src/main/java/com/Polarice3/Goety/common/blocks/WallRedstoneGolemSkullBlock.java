package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.RedstoneGolemSkullBlockEntity;
import com.Polarice3.Goety.common.items.block.RedstoneGolemSkullItem;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;

public class WallRedstoneGolemSkullBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.of(
            Direction.NORTH, Block.box(0.0D, 4.0D, 6.0D, 16.0D, 20.0D, 16.0D),
            Direction.SOUTH, Block.box(0.0D, 4.0D, 0.0D, 16.0D, 20.0D, 10.0D),
            Direction.EAST, Block.box(0.0D, 4.0D, 0.0D, 10.0D, 20.0D, 16.0D),
            Direction.WEST, Block.box(6.0D, 4.0D, 0.0D, 16.0D, 20.0D, 16.0D)));

    public WallRedstoneGolemSkullBlock() {
        super(Properties.of(Material.DECORATION)
                .strength(1.0F)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        ItemStack itemStack = new ItemStack(this);
        if (player.isCrouching()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof RedstoneGolemSkullBlockEntity) {
                this.setOwner(itemStack, tileEntity);
                this.setModCustomName(itemStack, tileEntity);
            }
        }
        return itemStack;
    }

    public void setOwner(ItemStack itemStack, BlockEntity tileEntity){
        if (tileEntity instanceof RedstoneGolemSkullBlockEntity blockEntity){
            RedstoneGolemSkullItem.setOwner(blockEntity.getPlayer(), itemStack);
        }
    }

    public void setModCustomName(ItemStack itemStack, BlockEntity tileEntity){
        if (tileEntity instanceof RedstoneGolemSkullBlockEntity blockEntity){
            if (blockEntity.getCustomName() != null && !blockEntity.getCustomName().isEmpty()) {
                RedstoneGolemSkullItem.setCustomName(blockEntity.getCustomName(), itemStack);
            }
        }
    }

    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pTe, ItemStack pStack) {
        ModBlocks.REDSTONE_GOLEM_SKULL_BLOCK.get().playerDestroy(pLevel, pPlayer, pPos, pState, pTe, pStack);
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        ModBlocks.REDSTONE_GOLEM_SKULL_BLOCK.get().setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }

    public String getDescriptionId() {
        return this.asItem().getDescriptionId();
    }

    public VoxelShape getShape(BlockState p_58114_, BlockGetter p_58115_, BlockPos p_58116_, CollisionContext p_58117_) {
        return AABBS.get(p_58114_.getValue(FACING));
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_58104_) {
        BlockState blockstate = this.defaultBlockState();
        BlockGetter blockgetter = p_58104_.getLevel();
        BlockPos blockpos = p_58104_.getClickedPos();
        Direction[] adirection = p_58104_.getNearestLookingDirections();

        for(Direction direction : adirection) {
            if (direction.getAxis().isHorizontal()) {
                Direction direction1 = direction.getOpposite();
                blockstate = blockstate.setValue(FACING, direction1);
                if (!blockgetter.getBlockState(blockpos.relative(direction)).canBeReplaced(p_58104_)) {
                    return blockstate;
                }
            }
        }

        return null;
    }

    public BlockState rotate(BlockState p_58109_, Rotation p_58110_) {
        return p_58109_.setValue(FACING, p_58110_.rotate(p_58109_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_58106_, Mirror p_58107_) {
        return p_58106_.rotate(p_58107_.getRotation(p_58106_.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_58112_) {
        p_58112_.add(FACING);
    }

    public BlockEntity newBlockEntity(BlockPos p_151996_, BlockState p_151997_) {
        return new RedstoneGolemSkullBlockEntity(p_151996_, p_151997_);
    }
}
