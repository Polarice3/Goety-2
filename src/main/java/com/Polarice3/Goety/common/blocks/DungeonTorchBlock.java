package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DungeonTorchBlock extends Block {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    protected static final VoxelShape AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D);

    public DungeonTorchBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, Boolean.TRUE));
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        RandomSource randomsource = pLevel.getRandom();
        if (canLight(pState)){
            pLevel.playSound((Player)null, pPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F);
            pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, Boolean.valueOf(true)));
        } else {
            pLevel.playSound((Player)null, pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, Boolean.valueOf(false)));
        }
        return InteractionResult.SUCCESS;
    }

    public static boolean canLight(BlockState p_51322_) {
        return !p_51322_.getValue(LIT);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LIT);
    }

    public VoxelShape getShape(BlockState p_57510_, BlockGetter p_57511_, BlockPos p_57512_, CollisionContext p_57513_) {
        return AABB;
    }

    public BlockState updateShape(BlockState p_57503_, Direction p_57504_, BlockState p_57505_, LevelAccessor p_57506_, BlockPos p_57507_, BlockPos p_57508_) {
        return p_57504_ == Direction.DOWN && !this.canSurvive(p_57503_, p_57506_, p_57507_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_57503_, p_57504_, p_57505_, p_57506_, p_57507_, p_57508_);
    }

    public boolean canSurvive(BlockState p_57499_, LevelReader p_57500_, BlockPos p_57501_) {
        return canSupportCenter(p_57500_, p_57501_.below(), Direction.UP);
    }

    public void animateTick(BlockState p_222593_, Level p_222594_, BlockPos p_222595_, RandomSource p_222596_) {
        if (p_222593_.getValue(LIT)) {
            double d0 = (double)p_222595_.getX() + 0.5D;
            double d1 = (double)p_222595_.getY() + 1.0D;
            double d2 = (double)p_222595_.getZ() + 0.5D;
            p_222594_.addParticle(ModParticleTypes.BIG_FIRE.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
            p_222594_.addParticle(ModParticleTypes.BIG_FIRE_DROP.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }
}
