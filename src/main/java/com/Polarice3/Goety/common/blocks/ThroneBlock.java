package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.api.blocks.IEnchanteableBlock;
import com.Polarice3.Goety.api.blocks.IEnchantedBlock;
import com.Polarice3.Goety.api.blocks.ISeat;
import com.Polarice3.Goety.common.blocks.entities.ThroneBlockEntity;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.block.EnchantableBlockItem;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.Map;

public class ThroneBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock, ISeat, IEnchanteableBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public ThroneBlock(Properties p_54120_) {
        super(p_54120_);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE).setValue(HALF, DoubleBlockHalf.LOWER).setValue(FACING, Direction.NORTH));
    }

    public RenderShape getRenderShape(BlockState p_222120_) {
        return RenderShape.MODEL;
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        pLevel.setBlock(pPos.above(), pState.setValue(HALF, DoubleBlockHalf.UPPER).setValue(WATERLOGGED, pLevel.getFluidState(pPos.above()).getType() == Fluids.WATER), 3);
        BlockEntity tileentity = pLevel.getBlockEntity(pPos);
        if (pPlacer instanceof Player){
            if (tileentity instanceof ThroneBlockEntity blockEntity){
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(pStack);
                enchantments.keySet().removeIf(enchantment -> !this.asItem().canApplyAtEnchantingTable(pStack, enchantment));
                blockEntity.getEnchantments().putAll(enchantments);
                if (!pLevel.isClientSide) {
                    if (!enchantments.isEmpty()) {
                        if (blockEntity.getTrueOwner() == null) {
                            blockEntity.setOwner(pPlacer);
                        } else if (pStack.getItem() instanceof EnchantableBlockItem) {
                            blockEntity.setOwnerUUID(EnchantableBlockItem.getOwnerID(pStack));
                        }
                    }
                }
            }
        }
    }

    public void setOwner(ItemStack itemStack, BlockEntity tileEntity){
        if (tileEntity instanceof ThroneBlockEntity blockEntity && !blockEntity.getEnchantments().isEmpty()){
            EnchantableBlockItem.setOwner(blockEntity.getPlayer(), itemStack);
        }
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        ItemStack itemStack = new ItemStack(this);
        if (player.isCrouching()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof ThroneBlockEntity blockEntity) {
                if (!blockEntity.getEnchantments().isEmpty()) {
                    this.setEnchantments(itemStack, blockEntity);
                    this.setOwner(itemStack, blockEntity);
                }
            }
        }
        return itemStack;
    }

    public void playerWillDestroy(Level p_52755_, BlockPos p_52756_, BlockState p_52757_, Player p_52758_) {
        if (!p_52755_.isClientSide && p_52758_.isCreative()) {
            BlockFinder.preventCreativeDropFromBottomPart(p_52755_, p_52756_, p_52757_, p_52758_);
        }
        if (!p_52755_.isClientSide && !p_52758_.isCreative()) {
            ItemStack itemStack = new ItemStack(this);
            BlockEntity blockEntity = p_52755_.getBlockEntity(p_52756_);
            if (p_52757_.hasProperty(HALF)) {
                if (p_52757_.getValue(HALF) == DoubleBlockHalf.UPPER) {
                    blockEntity = p_52755_.getBlockEntity(p_52756_.below());
                }
            }
            if (blockEntity instanceof ThroneBlockEntity throneBlock) {
                if (throneBlock.getTrueOwner() == p_52758_) {
                    this.setEnchantments(itemStack, blockEntity);
                    this.setOwner(itemStack, blockEntity);
                }
            }
            popResource(p_52755_, p_52756_, itemStack);
        }

        super.playerWillDestroy(p_52755_, p_52756_, p_52757_, p_52758_);
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity) {
        return BlockPathTypes.RAIL;
    }

    @Override
    public boolean hasLookAngle() {
        return true;
    }

    @Override
    public float seatLookAngle(Level world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.hasProperty(FACING)) {
            return blockState.getValue(FACING).toYRot();
        }
        return 0.0F;
    }

    @Override
    public boolean isThrone(Level world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof IEnchantedBlock enchantedBlock) {
            return enchantedBlock.getEnchantments().getOrDefault(ModEnchantments.ROYALTY.get(), 0) >= 1;
        }
        return true;
    }

    @Override
    @Nullable
    public LivingEntity getOwner(Level world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ThroneBlockEntity throneBlock) {
            return throneBlock.getTrueOwner();
        }
        return null;
    }

    @Override
    public InteractionResult use(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {
        if (p_60503_.getValue(HALF) == DoubleBlockHalf.UPPER) {
            p_60505_ = p_60505_.below();
        }
        return this.sitDown(p_60503_, p_60504_, p_60505_, p_60506_, p_60507_, p_60508_);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_51750_) {
        BlockState blockstate = this.defaultBlockState();
        LevelReader levelreader = p_51750_.getLevel();
        BlockPos blockpos = p_51750_.getClickedPos();
        FluidState fluidstate = levelreader.getFluidState(blockpos);
        boolean flag = fluidstate.getType() == Fluids.WATER;

        for(Direction direction : p_51750_.getNearestLookingDirections()) {
            if (direction.getAxis().isHorizontal()) {
                Direction direction1 = direction.getOpposite();
                blockstate = blockstate.setValue(FACING, direction1).setValue(HALF, DoubleBlockHalf.LOWER);
                if (blockstate.canSurvive(levelreader, blockpos) && blockpos.getY() < levelreader.getMaxBuildHeight() - 1 && levelreader.getBlockState(blockpos.above()).canBeReplaced(p_51750_)) {
                    return blockstate.setValue(WATERLOGGED, flag);
                }
            }
        }
        return null;
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

    public boolean canSurvive(BlockState p_52783_, LevelReader p_52784_, BlockPos p_52785_) {
        BlockPos blockpos = p_52785_.below();
        BlockState blockstate = p_52784_.getBlockState(blockpos);
        return p_52783_.getValue(HALF) == DoubleBlockHalf.LOWER ? blockstate.isFaceSturdy(p_52784_, blockpos, Direction.UP) : blockstate.is(this);
    }

    public BlockState updateShape(BlockState p_51771_, Direction p_51772_, BlockState p_51773_, LevelAccessor p_51774_, BlockPos p_51775_, BlockPos p_51776_) {
        DoubleBlockHalf doubleblockhalf = p_51771_.getValue(HALF);
        if (p_51772_.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (p_51772_ == Direction.UP)) {
            return p_51773_.is(this) && p_51773_.getValue(HALF) != doubleblockhalf ? p_51771_.setValue(FACING, p_51773_.getValue(FACING)) : Blocks.AIR.defaultBlockState();
        } else {
            return doubleblockhalf == DoubleBlockHalf.LOWER && p_51772_ == Direction.DOWN && p_51772_ == p_51771_.getValue(FACING) && !p_51771_.canSurvive(p_51774_, p_51775_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_51771_, p_51772_, p_51773_, p_51774_, p_51775_, p_51776_);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51778_) {
        p_51778_.add(FACING, WATERLOGGED, HALF);
    }

    public boolean isPathfindable(BlockState p_51762_, BlockGetter p_51763_, BlockPos p_51764_, PathComputationType p_51765_) {
        return false;
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        if (p_153216_.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return new ThroneBlockEntity(p_153215_, p_153216_);
        } else {
            return null;
        }
    }
}
