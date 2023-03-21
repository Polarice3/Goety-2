package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.extensions.IForgeBlock;

import javax.annotation.Nullable;

public class CursedCageBlock extends BaseEntityBlock implements IForgeBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public CursedCageBlock() {
        super(Properties.of(Material.STONE)
                .strength(5.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
                .noOcclusion()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.FALSE));
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        CompoundTag compoundnbt = pStack.getOrCreateTag();
        if (compoundnbt.contains("BlockEntityTag")) {
            CompoundTag compoundnbt1 = compoundnbt.getCompound("BlockEntityTag");
            if (compoundnbt1.contains("item")) {
                pLevel.setBlock(pPos, pState.setValue(POWERED, Boolean.TRUE), 2);
            }
        }

    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pState.getValue(POWERED) && pPlayer.getItemInHand(pHand).isEmpty()) {
            this.dropItem(pLevel, pPos);
            pState = pState.setValue(POWERED, Boolean.FALSE);
            pLevel.setBlock(pPos, pState, 2);
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    public void setItem(Level pLevel, BlockPos pPos, BlockState pState, ItemStack pStack) {
        BlockEntity tileentity = pLevel.getBlockEntity(pPos);
        if (tileentity instanceof CursedCageBlockEntity) {
            ((CursedCageBlockEntity)tileentity).setItem(pStack.copy());
            pLevel.setBlock(pPos, pState.setValue(POWERED, Boolean.TRUE), 2);
        }
    }

    public void dropItem(Level pLevel, BlockPos pPos) {
        if (!pLevel.isClientSide) {
            BlockEntity tileentity = pLevel.getBlockEntity(pPos);
            if (tileentity instanceof CursedCageBlockEntity) {
                CursedCageBlockEntity cageTileEntity = (CursedCageBlockEntity)tileentity;
                ItemStack itemstack = cageTileEntity.getItem();
                if (!itemstack.isEmpty()) {
                    pLevel.levelEvent(1010, pPos, 0);
                    cageTileEntity.clearContent();
                    float f = 0.7F;
                    double d0 = (double)(pLevel.random.nextFloat() * 0.7F) + (double)0.15F;
                    double d1 = (double)(pLevel.random.nextFloat() * 0.7F) + (double)0.060000002F + 0.6D;
                    double d2 = (double)(pLevel.random.nextFloat() * 0.7F) + (double)0.15F;
                    ItemStack itemstack1 = itemstack.copy();
                    ItemEntity itementity = new ItemEntity(pLevel, (double)pPos.getX() + d0, (double)pPos.getY() + d1, (double)pPos.getZ() + d2, itemstack1);
                    itementity.setDefaultPickUpDelay();
                    pLevel.addFreshEntity(itementity);
                }
            }
        }
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            this.dropItem(pLevel, pPos);
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(POWERED);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new CursedCageBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152755_, BlockState p_152756_, BlockEntityType<T> p_152757_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof CursedCageBlockEntity blockEntity1)
                blockEntity1.tick();
        };
    }
}
