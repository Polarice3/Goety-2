package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.client.particles.AoEParticleOption;
import com.Polarice3.Goety.common.blocks.entities.VoidBarrelBlockEntity;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class VoidBarrelBlock extends BaseEntityBlock {
    protected static final VoxelShape TOP_SHAPE = Block.box(0.0D, 0.0D, 0.0D,
            16.0D, 11.0D, 16.0D);
    protected static final VoxelShape POST_SHAPE = Block.box(0.0D, 0.0D, 0.0D,
            16.0D, 1.0D, 16.0D);
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D,
            16.0D, 16.0D, 16.0D);
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public VoidBarrelBlock() {
        super(Properties.of()
                .emissiveRendering((state, world, pos) -> true)
                .lightLevel((state) -> 1)
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(3.0F, 1200.0F)
                .noOcclusion()
                .sound(SoundType.GLASS));
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(TRIGGERED, Boolean.valueOf(false)).setValue(LIT, Boolean.valueOf(false)));
    }

    public void onProjectileHit(Level p_49708_, BlockState p_49709_, BlockHitResult p_49710_, Projectile p_49711_) {
        this.onHit(p_49708_, p_49709_, p_49710_.getBlockPos());
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (this.onHit(pLevel, pState, pPos)) {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        pLevel.setBlock(pPos.above(), pState.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    public void playerWillDestroy(Level p_52755_, BlockPos p_52756_, BlockState p_52757_, Player p_52758_) {
        if (!p_52755_.isClientSide && p_52758_.isCreative()) {
            BlockFinder.preventCreativeDropFromBottomPart(p_52755_, p_52756_, p_52757_, p_52758_);
        }

        super.playerWillDestroy(p_52755_, p_52756_, p_52757_, p_52758_);
    }

    public boolean onHit(Level p_49702_, BlockState p_49703_, BlockPos p_49704_) {
        if (!p_49703_.getValue(TRIGGERED)) {
            if (p_49703_.getValue(HALF) == DoubleBlockHalf.UPPER) {
                p_49704_ = p_49704_.below();
            }
            BlockState blockState = p_49702_.getBlockState(p_49704_);
            p_49702_.playSound(null, p_49704_, ModSounds.VOID_BARREL_ACTIVATE.get(), SoundSource.BLOCKS, 2.0F, 1.0F);
            p_49702_.setBlockAndUpdate(p_49704_, blockState.setValue(TRIGGERED, true));
            Vec3 vec3 = p_49704_.getCenter().subtract(0.0F, 0.4F, 0.0F);
            ColorUtil colorUtil = ColorUtil.WHITE;
            if (p_49702_ instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new AoEParticleOption(6, 60), vec3.x, vec3.y, vec3.z, 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.0F);
            }
            return true;
        }
        return false;
    }

    public RenderShape getRenderShape(BlockState p_49753_) {
        return RenderShape.MODEL;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pState.getValue(HALF) == DoubleBlockHalf.UPPER) {
            if (pState.getValue(LIT)) {
                return POST_SHAPE;
            }
            return TOP_SHAPE;
        }
        return SHAPE;
    }

    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
        BlockPos blockpos = blockPos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        return blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? super.canSurvive(blockState, level, blockPos) : blockstate.is(this);
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        DoubleBlockHalf doubleblockhalf = pState.getValue(HALF);
        if (pFacing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (pFacing == Direction.UP)) {
            return pFacingState.is(this) && pFacingState.getValue(HALF) != doubleblockhalf ? pState.setValue(TRIGGERED, pFacingState.getValue(TRIGGERED)).setValue(LIT, pFacingState.getValue(LIT)) : Blocks.AIR.defaultBlockState();
        } else {
            return doubleblockhalf == DoubleBlockHalf.LOWER && pFacing == Direction.DOWN && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
        }
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!(block instanceof VoidBarrelBlock) && pLevel.hasNeighborSignal(pPos)) {
            this.onHit(pLevel, pState, pPos);
        }
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (pState.getValue(HALF) == DoubleBlockHalf.UPPER && pState.getValue(LIT)) {
            BlockFinder.voidedEffect(pLevel, pState, pEntity);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49751_) {
        p_49751_.add(TRIGGERED, LIT, HALF);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER).setValue(TRIGGERED, false).setValue(LIT, false);
    }

    public boolean isPathfindable(BlockState p_49717_, BlockGetter p_49718_, BlockPos p_49719_, PathComputationType p_49720_) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new VoidBarrelBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof VoidBarrelBlockEntity blockEntity1)
                blockEntity1.tick();
        };
    }
}
