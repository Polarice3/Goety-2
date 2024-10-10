package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.hostile.cultists.Crone;
import com.Polarice3.Goety.common.entities.util.SummonCircleBoss;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.common.items.magic.TaglockKit;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
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
        super(Properties.of()
                .mapColor(MapColor.GOLD)
                .strength(3.0F, 6.0F)
                .sound(SoundType.GLASS)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.TRUE));
    }

    public float getDestroyProgress(BlockState pState, Player pPlayer, BlockGetter pLevel, BlockPos pPos) {
        return !pState.getValue(POWERED) ? 3.0F : -1.0F;
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pLevel.getDifficulty() != Difficulty.PEACEFUL) {
            if (pLevel instanceof ServerLevel serverLevel) {
                if (pState.getValue(POWERED)) {
                    Crone crone = new Crone(ModEntityType.CRONE.get(), pLevel);
                    BlockPos blockPos = BlockFinder.SummonFurtherRadius(pPos, crone, pLevel);
                    if (!serverLevel.getBlockState(blockPos.below()).isSolidRender(pLevel, blockPos.below())) {
                        blockPos = BlockFinder.SummonRadius(pPos, crone, pLevel);
                    }
                    crone.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    if (!CuriosFinder.hasWitchSet(pPlayer) && MobUtil.validEntity(pPlayer)){
                        crone.setTarget(pPlayer);
                    }
                    crone.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pPos), MobSpawnType.MOB_SUMMONED, null, null);
                    crone.setPersistenceRequired();
                    SummonCircleBoss summonCircle = new SummonCircleBoss(serverLevel, blockPos, crone);
                    if (pLevel.addFreshEntity(summonCircle)) {
                        pLevel.playSound(null, summonCircle.blockPosition(), ModSounds.CRONE_LAUGH.get(), SoundSource.HOSTILE, 2.0F, 1.0F);
                        pLevel.playSound(null, pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 0.75F);
                        ServerParticleUtil.smokeParticles(ParticleTypes.SMOKE, pPos.getX() + 0.5F, pPos.getY() + 0.5F, pPos.getZ() + 0.5F, serverLevel);
                        pLevel.setBlockAndUpdate(pPos, ModBlocks.CRYSTAL_BALL.get().defaultBlockState().setValue(POWERED, false));
                    }
                } else if (pPlayer.getItemInHand(pHand).getItem() instanceof TaglockKit && TaglockKit.hasEntity(pPlayer.getItemInHand(pHand))){
                    ItemStack itemStack = pPlayer.getItemInHand(pHand);
                    if (TaglockKit.isSameDimension(pPlayer, itemStack)) {
                        SEHelper.setCamera(pPlayer, TaglockKit.getEntity(pPlayer.getItemInHand(pHand)));
                        ModNetwork.sendTo(pPlayer, new SPlayPlayerSoundPacket(ModSounds.END_WALK.get(), 1.0F, 0.5F));
                        pLevel.playSound(pPlayer, pPlayer.blockPosition(), ModSounds.END_WALK.get(), SoundSource.PLAYERS, 1.0F, 0.5F);
                    } else {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.taglock.difDimension"), true);
                    }
                } else if (pPlayer.getItemInHand(pHand).getItem() instanceof WaystoneItem && WaystoneItem.hasBlock(pPlayer.getItemInHand(pHand))){
                    ItemStack itemStack = pPlayer.getItemInHand(pHand);
                    if (WaystoneItem.isSameDimension(pPlayer, itemStack)) {
                        SEHelper.setCamera(pPlayer, null, WaystoneItem.getPosition(pPlayer.getItemInHand(pHand)).pos());
                        ModNetwork.sendTo(pPlayer, new SPlayPlayerSoundPacket(ModSounds.END_WALK.get(), 1.0F, 0.5F));
                        pLevel.playSound(pPlayer, pPlayer.blockPosition(), ModSounds.END_WALK.get(), SoundSource.PLAYERS, 1.0F, 0.5F);
                    } else {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.waystone.difDimension"), true);
                    }
                } else if (pPlayer.getItemInHand(pHand).is(ModTags.Items.RESPAWN_BOSS) && MainConfig.CrystalBallRespawn.get() && BlockFinder.findStructure(serverLevel, pPlayer, ModTags.Structures.CRONE_SPAWNS)) {
                    ItemStack itemStack = pPlayer.getItemInHand(pHand);
                    if (pPlayer instanceof ServerPlayer serverPlayer) {
                        CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pPos, itemStack);
                    }
                    pLevel.setBlockAndUpdate(pPos, ModBlocks.CRYSTAL_BALL.get().defaultBlockState().setValue(POWERED, Boolean.TRUE));
                    itemStack.shrink(1);
                    pLevel.playSound(null, pPos, SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
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

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return new ItemStack(ModBlocks.CRYSTAL_BALL.get());
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
