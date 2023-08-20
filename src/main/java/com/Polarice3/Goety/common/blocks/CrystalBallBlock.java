package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.hostile.cultists.Crone;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class CrystalBallBlock extends Block {
    public static final VoxelShape SHAPE_BASE = Block.box(6.0D, 0.0D, 6.0D,
            10.0D, 1.0D, 10.0D);
    public static final VoxelShape SHAPE_BASE_2 = Block.box(6.5D, 1.0D, 6.5D,
            9.5D, 2.0D, 9.5D);
    public static final VoxelShape SHAPE_CRYSTAL = Block.box(6.0D, 2.0D, 6.0D,
            10.0D, 6.0D, 10.0D);
    public static final VoxelShape SHAPE = Shapes.or(SHAPE_BASE, SHAPE_BASE_2, SHAPE_CRYSTAL);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public CrystalBallBlock() {
        super(Properties.of(Material.METAL, MaterialColor.GOLD)
                .strength(3.0F, 6.0F)
                .sound(SoundType.GLASS)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.TRUE));
    }

    public float getDestroyProgress(BlockState pState, Player pPlayer, BlockGetter pLevel, BlockPos pPos) {
        return !pState.getValue(POWERED) ? 3.0F : -1.0F;
    }

    public InteractionResult use(BlockState p_49722_, Level p_49723_, BlockPos p_49724_, Player p_49725_, InteractionHand p_49726_, BlockHitResult p_49727_) {
        if (!p_49723_.isClientSide && p_49723_.getDifficulty() != Difficulty.PEACEFUL) {
            if (p_49723_ instanceof ServerLevel serverLevel) {
                if (p_49722_.getValue(POWERED)) {
                    Crone crone = new Crone(ModEntityType.CRONE.get(), p_49723_);
                    BlockPos blockPos = BlockFinder.SummonFurtherRadius(p_49725_, p_49723_);
                    if (!serverLevel.getBlockState(blockPos.below()).isSolidRender(p_49723_, blockPos.below())) {
                        blockPos = BlockFinder.SummonRadius(p_49725_, p_49723_);
                    }
                    crone.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    if (!CuriosFinder.hasWitchSet(p_49725_) && MobUtil.validEntity(p_49725_)){
                        crone.setTarget(p_49725_);
                    }
                    crone.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(p_49724_), MobSpawnType.MOB_SUMMONED, null, null);
                    crone.setPersistenceRequired();
                    if (p_49723_.addFreshEntity(crone)) {
                        p_49723_.playSound(null, crone.blockPosition(), ModSounds.CRONE_LAUGH.get(), SoundSource.HOSTILE, 2.0F, 1.0F);
                        p_49723_.playSound(null, p_49724_, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 0.75F);
                        ServerParticleUtil.smokeParticles(ParticleTypes.SMOKE, p_49724_.getX() + 0.5F, p_49724_.getY() + 0.5F, p_49724_.getZ() + 0.5F, serverLevel);
                        p_49723_.setBlockAndUpdate(p_49724_, ModBlocks.CRYSTAL_BALL.get().defaultBlockState().setValue(POWERED, false));
                    }
                }
            }
        }
        return InteractionResult.sidedSuccess(p_49723_.isClientSide);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return SHAPE;
    }

    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(POWERED);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(POWERED, false);
    }

    public boolean isPathfindable(BlockState p_48799_, BlockGetter p_48800_, BlockPos p_48801_, PathComputationType p_48802_) {
        return false;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRand) {
        double d0 = (double)pPos.getX() + pRand.nextDouble();
        double d2 = (double)pPos.getZ() + pRand.nextDouble();
        if (pState.getValue(POWERED)) {
            pLevel.addParticle(ParticleTypes.WITCH, d0, pPos.getY(), d2, 0, 0, 0);
        }
    }
}
