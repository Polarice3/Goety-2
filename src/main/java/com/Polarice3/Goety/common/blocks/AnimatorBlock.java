package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.AnimatorBlockEntity;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

public class AnimatorBlock extends BaseEntityBlock implements IForgeBlock {
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public AnimatorBlock() {
        super(Properties.of(Material.STONE)
                .strength(5.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .dynamicShape()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.FALSE).setValue(TRIGGERED, Boolean.FALSE));
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
        if (pHand == InteractionHand.MAIN_HAND){
            BlockEntity tileEntity = pLevel.getBlockEntity(pPos);
            if (tileEntity instanceof AnimatorBlockEntity animatorBlock) {
                if (pPlayer.getMainHandItem().isEmpty()) {
                    if (pPlayer.isCrouching() && animatorBlock.getPosition() != null){
                        animatorBlock.setShowBlock(!animatorBlock.isShowBlock());
                        pLevel.playSound(null, pPos, ModSounds.CAST_SPELL.get(), SoundSource.BLOCKS, 0.25F, 2.0F);
                    } else {
                        this.dropItem(pLevel, pPos);
                    }
                    return InteractionResult.sidedSuccess(pLevel.isClientSide);
                } else if (animatorBlock.getItem().isEmpty() && pPlayer.getMainHandItem().getItem() instanceof WaystoneItem){
                    if (WaystoneItem.hasBlock(pPlayer.getMainHandItem())){
                        this.setItem(pLevel, pPos, pState, pPlayer.getMainHandItem());
                        return InteractionResult.sidedSuccess(pLevel.isClientSide);
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    public void setItem(Level pLevel, BlockPos pPos, BlockState pState, ItemStack pStack) {
        BlockEntity tileentity = pLevel.getBlockEntity(pPos);
        if (tileentity instanceof AnimatorBlockEntity animatorBlock) {
            animatorBlock.setItem(pStack.split(1));
            pLevel.playSound(null, pPos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public void dropItem(Level pLevel, BlockPos pPos) {
        if (!pLevel.isClientSide) {
            BlockEntity tileentity = pLevel.getBlockEntity(pPos);
            if (tileentity instanceof AnimatorBlockEntity animatorBlock) {
                ItemStack itemstack = animatorBlock.getItem();
                if (!itemstack.isEmpty()) {
                    pLevel.levelEvent(1010, pPos, 0);
                    animatorBlock.clearContent();
                    float f = 0.7F;
                    double d0 = (double)(pLevel.random.nextFloat() * f) + (double)0.15F;
                    double d1 = (double)(pLevel.random.nextFloat() * f) + (double)0.060000002F + 0.6D;
                    double d2 = (double)(pLevel.random.nextFloat() * f) + (double)0.15F;
                    ItemStack itemstack1 = itemstack.copy();
                    ItemEntity itementity = new ItemEntity(pLevel, (double)pPos.getX() + d0, (double)pPos.getY() + d1, (double)pPos.getZ() + d2, itemstack1);
                    itementity.setDefaultPickUpDelay();
                    if (pLevel.addFreshEntity(itementity)){
                        pLevel.playSound(null, pPos, SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
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

    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        boolean flag = pLevel.hasNeighborSignal(pPos) || pLevel.hasNeighborSignal(pPos.above());
        boolean flag1 = pState.getValue(TRIGGERED);
        if (pState.getValue(POWERED)) {
            if (flag && !flag1) {
                BlockEntity tileentity = pLevel.getBlockEntity(pPos);
                if (tileentity instanceof AnimatorBlockEntity animatorBlock) {
                    animatorBlock.summonGolem();
                }
                pLevel.setBlock(pPos, pState.setValue(TRIGGERED, Boolean.TRUE), 4);
            } else if (!flag && flag1) {
                pLevel.setBlock(pPos, pState.setValue(TRIGGERED, Boolean.FALSE), 4);
            }
        }

    }

    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(POWERED, TRIGGERED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new AnimatorBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152755_, BlockState p_152756_, BlockEntityType<T> p_152757_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof AnimatorBlockEntity blockEntity1)
                blockEntity1.tick();
        };
    }
}
