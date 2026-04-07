package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.HauntedJugBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class HauntedJugBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    protected static final VoxelShape SHAPE_BOTTOM = Block.box(2.0D, 0.0D, 2.0D,
            14.0D, 2.0D, 14.0D);
    protected static final VoxelShape SHAPE_BASE_1 = Block.box(2.0D, 2.0D, 2.0D,
            14.0D, 14.0D, 4.0D);
    protected static final VoxelShape SHAPE_BASE_2 = Block.box(12.0D, 2.0D, 4.0D,
            14.0D, 14.0D, 12.0D);
    protected static final VoxelShape SHAPE_BASE_3 = Block.box(2.0D, 2.0D, 12.0D,
            14.0D, 14.0D, 14.0D);
    protected static final VoxelShape SHAPE_BASE_4 = Block.box(2.0D, 2.0D, 4.0D,
            4.0D, 14.0D, 12.0D);
    protected static final VoxelShape SHAPE_TOP_1 = Block.box(6.0D, 14.0D, 4.0D,
            10.0D, 16.0D, 6.0D);
    protected static final VoxelShape SHAPE_TOP_2 = Block.box(10.0D, 14.0D, 4.0D,
            12.0D, 16.0D, 12.0D);
    protected static final VoxelShape SHAPE_TOP_3 = Block.box(6.0D, 14.0D, 10.0D,
            10.0D, 16.0D, 12.0D);
    protected static final VoxelShape SHAPE_TOP_4 = Block.box(4.0D, 14.0D, 4.0D,
            6.0D, 16.0D, 12.0D);
    public static final VoxelShape SHAPE_BASE = Shapes.or(SHAPE_BOTTOM, SHAPE_BASE_1, SHAPE_BASE_2, SHAPE_BASE_3, SHAPE_BASE_4);
    public static final VoxelShape SHAPE_TOP = Shapes.or(SHAPE_TOP_1, SHAPE_TOP_2, SHAPE_TOP_3, SHAPE_TOP_4);
    public static final VoxelShape SHAPE = Shapes.or(SHAPE_BASE, SHAPE_TOP);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    public HauntedJugBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BROWN)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F)
                .sound(SoundType.WOOD));
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE).setValue(ENABLED, Boolean.TRUE));
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        if (!pLevel.isClientSide){
            if (pLevel.getBlockEntity(pPos) instanceof HauntedJugBlockEntity jugBlockEntity){
                IFluidHandler handler = jugBlockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, pHit.getDirection()).orElse(null);
                if (handler != null) {
                    if (FluidUtil.interactWithFluidHandler(pPlayer, pHand, handler)) {
                        pLevel.playSound(null, pPos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    } else if (itemStack.getItem() == Items.GLASS_BOTTLE){
                        pPlayer.setItemInHand(pHand, ItemUtils.createFilledResult(itemStack, pPlayer, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
                        pLevel.playSound(null, pPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    } else if (itemStack.getItem() == Items.WATER_BUCKET){
                        pPlayer.setItemInHand(pHand, ItemUtils.createFilledResult(itemStack, pPlayer, new ItemStack(Items.BUCKET)));
                        pLevel.playSound(null, pPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }
            }
        }
        if (FluidUtil.getFluidHandler(itemStack).isPresent()) {
            return InteractionResult.SUCCESS;
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    public RenderShape getRenderShape(BlockState p_222219_) {
        return RenderShape.MODEL;
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
        pBuilder.add(WATERLOGGED, ENABLED);
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new HauntedJugBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152755_, BlockState p_152756_, BlockEntityType<T> p_152757_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof HauntedJugBlockEntity jugBlock)
                jugBlock.tick();
        };
    }
}
