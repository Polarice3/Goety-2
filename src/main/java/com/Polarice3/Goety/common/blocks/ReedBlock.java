package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.NetherVines;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ToolActions;

public class ReedBlock extends Block implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_25;
    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public ReedBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    public VoxelShape getShape(BlockState p_57193_, BlockGetter p_57194_, BlockPos p_57195_, CollisionContext p_57196_) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (state.is(ModBlocks.CHORUS_BLOSSOM_VINES.get())) {
            if (itemStack.canPerformAction(ToolActions.SHEARS_CARVE)) {
                if (!world.isClientSide) {
                    world.playSound(null, pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    world.setBlock(pos, ModBlocks.CHORUS_BLOSSOM_VINES_PRUNED.get().defaultBlockState().setValue(ReedBlock.AGE, 0), 11);
                    itemStack.hurtAndBreak(1, player, (p_55287_) -> {
                        p_55287_.broadcastBreakEvent(hand);
                    });
                    world.gameEvent(player, GameEvent.SHEAR, pos);
                    player.awardStat(Stats.ITEM_USED.get(Items.SHEARS));
                }
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    public void tick(BlockState p_222543_, ServerLevel p_222544_, BlockPos p_222545_, RandomSource p_222546_) {
        if (!p_222543_.canSurvive(p_222544_, p_222545_)) {
            p_222544_.destroyBlock(p_222545_, true);
        }

    }

    public BlockState getStateForPlacement(LevelAccessor p_53949_) {
        return this.defaultBlockState().setValue(AGE, p_53949_.getRandom().nextInt(25));
    }

    protected BlockState getGrowIntoState(BlockState p_221347_, RandomSource p_221348_) {
        return p_221347_.cycle(AGE);
    }

    public BlockState getMaxAgeState(BlockState p_187439_) {
        return p_187439_.setValue(AGE, 25);
    }

    public boolean isMaxAge(BlockState p_187441_) {
        return p_187441_.getValue(AGE) == 25;
    }

    public boolean isRandomlyTicking(BlockState p_53961_) {
        return p_53961_.getValue(AGE) < 25;
    }

    public void randomTick(BlockState p_221350_, ServerLevel p_221351_, BlockPos p_221352_, RandomSource p_221353_) {
        if (p_221350_.getValue(AGE) < 25 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_221351_, p_221352_.relative(Direction.UP), p_221351_.getBlockState(p_221352_.relative(Direction.UP)),p_221353_.nextDouble() < 0.1D)) {
            BlockPos blockpos = p_221352_.relative(Direction.UP);
            if (this.canGrowInto(p_221351_.getBlockState(blockpos))) {
                p_221351_.setBlockAndUpdate(blockpos, this.getGrowIntoState(p_221350_, p_221351_.random));
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_221351_, blockpos, p_221351_.getBlockState(blockpos));
            }
        }

    }

    protected int getBlocksToGrowWhenBonemealed(RandomSource p_222649_) {
        return NetherVines.getBlocksToGrowWhenBonemealed(p_222649_);
    }

    protected boolean canGrowInto(BlockState p_154869_) {
        return NetherVines.isValidGrowthState(p_154869_);
    }

    public boolean canSurvive(BlockState p_53876_, LevelReader p_53877_, BlockPos p_53878_) {
        BlockPos blockpos = p_53878_.relative(Direction.DOWN);
        BlockState blockstate = p_53877_.getBlockState(blockpos);
        if (!this.canAttachTo(blockstate)) {
            return false;
        } else {
            return this.isSameType(blockstate) || blockstate.isFaceSturdy(p_53877_, blockpos, Direction.UP) || blockstate.is(BlockTags.LEAVES);
        }
    }

    public BlockState updateShape(BlockState p_53951_, Direction p_53952_, BlockState p_53953_, LevelAccessor p_53954_, BlockPos p_53955_, BlockPos p_53956_) {
        if (p_53952_ == Direction.DOWN && !p_53951_.canSurvive(p_53954_, p_53955_)) {
            p_53954_.scheduleTick(p_53955_, this, 1);
        }

        if (p_53952_ != Direction.UP || !this.isSameType(p_53953_) && !this.isSameType(p_53953_)) {
            return super.updateShape(p_53951_, p_53952_, p_53953_, p_53954_, p_53955_, p_53956_);
        } else {
            return this.updateBodyAfterConvertedFromHead(p_53951_, this.defaultBlockState());
        }
    }

    public boolean isSameType(BlockState blockState) {
        if (blockState.is(ModBlocks.CHORUS_BLOSSOM_VINES.get()) || blockState.is(ModBlocks.CHORUS_BLOSSOM_VINES_PRUNED.get())) {
            return this == ModBlocks.CHORUS_BLOSSOM_VINES.get() || this == ModBlocks.CHORUS_BLOSSOM_VINES_PRUNED.get();
        }
        return blockState.is(this);
    }

    protected BlockState updateBodyAfterConvertedFromHead(BlockState p_153329_, BlockState p_153330_) {
        return p_153330_;
    }

    protected boolean canAttachTo(BlockState p_153321_) {
        return true;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53958_) {
        p_53958_.add(AGE);
    }

    public boolean isValidBonemealTarget(LevelReader p_255931_, BlockPos p_256046_, BlockState p_256550_, boolean p_256181_) {
        return this.canGrowInto(p_255931_.getBlockState(p_256046_.relative(Direction.UP)));
    }

    public boolean isBonemealSuccess(Level p_221343_, RandomSource p_221344_, BlockPos p_221345_, BlockState p_221346_) {
        return true;
    }

    public void performBonemeal(ServerLevel p_221337_, RandomSource p_221338_, BlockPos p_221339_, BlockState p_221340_) {
        BlockPos blockpos = p_221339_.relative(Direction.UP);
        int i = Math.min(p_221340_.getValue(AGE) + 1, 25);
        int j = this.getBlocksToGrowWhenBonemealed(p_221338_);

        for(int k = 0; k < j && this.canGrowInto(p_221337_.getBlockState(blockpos)); ++k) {
            p_221337_.setBlockAndUpdate(blockpos, p_221340_.setValue(AGE, Integer.valueOf(i)));
            blockpos = blockpos.relative(Direction.UP);
            i = Math.min(i + 1, 25);
        }

    }
}
