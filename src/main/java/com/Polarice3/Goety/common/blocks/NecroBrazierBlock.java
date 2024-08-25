package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.NecroBrazierBlockEntity;
import com.Polarice3.Goety.utils.ConstantPaths;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.function.ToIntFunction;

public class NecroBrazierBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    protected static final VoxelShape SHAPE_BASE = Block.box(1.0D, 0.0D, 1.0D,
            15.0D, 1.0D, 15.0D);
    protected static final VoxelShape SHAPE_2 = Block.box(2.0D, 1.0D, 2.0D,
            14.0D, 11.0D, 14.0D);
    public static final VoxelShape SHAPE = Shapes.or(SHAPE_BASE, SHAPE_2);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public NecroBrazierBlock() {
        super(Properties.of(Material.METAL)
                .strength(3.5F)
                .sound(SoundType.CHAIN)
                .lightLevel(litBlockEmission())
                .noOcclusion()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE).setValue(LIT, Boolean.FALSE));
    }

    private static ToIntFunction<BlockState> litBlockEmission() {
        return (state) -> state.getValue(BlockStateProperties.LIT) ? 10 : 0;
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity tileentity = pLevel.getBlockEntity(pPos);
        if (tileentity instanceof NecroBrazierBlockEntity burnerTileEntity) {
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            if (itemstack.isEmpty() || MobUtil.isShifting(pPlayer)){
                burnerTileEntity.removeItem(pPlayer);
                return InteractionResult.SUCCESS;
            } else if (!pLevel.isClientSide && pState.getValue(LIT) && burnerTileEntity.addItem(pPlayer, itemstack)) {
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity tileentity = pLevel.getBlockEntity(pPos);
            if (tileentity instanceof NecroBrazierBlockEntity) {
                Containers.dropContents(pLevel, pPos, ((NecroBrazierBlockEntity)tileentity).getItems());
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    public RenderShape getRenderShape(BlockState p_222219_) {
        return RenderShape.MODEL;
    }

    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (pState.getValue(LIT)) {
            if (!pEntity.fireImmune()
                    && pEntity instanceof LivingEntity livingEntity
                    && livingEntity.getMobType() != MobType.UNDEAD
                    && !LichdomHelper.isInLichMode(livingEntity)
                    && !EnchantmentHelper.hasFrostWalker((LivingEntity) pEntity)
                    && pEntity.getY() >= pPos.getY() + 0.5F) {
                pEntity.hurt(DamageSource.IN_FIRE, 1.0F);
            }
            BlockEntity tileentity = pLevel.getBlockEntity(pPos);
            if (tileentity instanceof NecroBrazierBlockEntity blockEntity) {
                if (pEntity instanceof ItemEntity itemEntity) {
                    if (!pLevel.isClientSide) {
                        if (!itemEntity.getTags().contains(ConstantPaths.resultItem())) {
                            if (blockEntity.currentTime <= 0) {
                                blockEntity.addItem(null, itemEntity.getItem());
                                pLevel.playSound(null, pPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
                                itemEntity.discard();
                            }
                        }
                    }
                }
            }
        }

        super.entityInside(pState, pLevel, pPos, pEntity);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        LevelAccessor iworld = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        boolean flag = iworld.getFluidState(blockpos).getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(WATERLOGGED, flag);
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    public boolean placeLiquid(LevelAccessor pLevel, BlockPos pPos, BlockState pState, FluidState pFluidState) {
        if (!pState.getValue(BlockStateProperties.WATERLOGGED) && pFluidState.getType() == Fluids.WATER) {
            pLevel.setBlock(pPos, pState.setValue(WATERLOGGED, Boolean.TRUE), 3);
            pLevel.scheduleTick(pPos, pFluidState.getType(), pFluidState.getType().getTickDelay(pLevel));
            return true;
        } else {
            return false;
        }
    }

    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATERLOGGED, LIT);
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    public BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return state.getValue(LIT) ? BlockPathTypes.DAMAGE_FIRE : super.getBlockPathType(state, level, pos, mob);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new NecroBrazierBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152755_, BlockState p_152756_, BlockEntityType<T> p_152757_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof NecroBrazierBlockEntity brazierBlock)
                brazierBlock.tick();
        };
    }
}
