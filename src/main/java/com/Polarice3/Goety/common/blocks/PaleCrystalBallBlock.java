package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.common.items.magic.TaglockKit;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PaleCrystalBallBlock extends Block {
    public static final VoxelShape SHAPE_BASE = Block.box(6.0D, 0.0D, 6.0D,
            10.0D, 1.0D, 10.0D);
    public static final VoxelShape SHAPE_BASE_2 = Block.box(6.5D, 1.0D, 6.5D,
            9.5D, 2.0D, 9.5D);
    public static final VoxelShape SHAPE_CRYSTAL = Block.box(6.0D, 2.0D, 6.0D,
            10.0D, 6.0D, 10.0D);
    public static final VoxelShape SHAPE = Shapes.or(SHAPE_BASE, SHAPE_BASE_2, SHAPE_CRYSTAL);

    public PaleCrystalBallBlock() {
        super(Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.0F, 6.0F)
                .sound(SoundType.GLASS)
                .noOcclusion());
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) {
            if (pPlayer.getItemInHand(pHand).getItem() instanceof TaglockKit && TaglockKit.hasEntity(pPlayer.getItemInHand(pHand))){
                ItemStack itemStack = pPlayer.getItemInHand(pHand);
                Entity entity = TaglockKit.getEntity(pPlayer.getItemInHand(pHand));
                if (entity != null && entity.distanceToSqr(Vec3.atCenterOf(pPos)) <= Mth.square(1024.0F) && TaglockKit.isSameDimension(pPlayer, itemStack) && pLevel.isLoaded(entity.blockPosition()) && (pLevel.getNearestPlayer(entity, 80.0D) != null || entity instanceof Player)) {
                    SEHelper.setCamera(pPlayer, TaglockKit.getEntity(pPlayer.getItemInHand(pHand)));
                    ModNetwork.sendTo(pPlayer, new SPlayPlayerSoundPacket(ModSounds.END_WALK.get(), 1.0F, 0.5F));
                    pLevel.playSound(pPlayer, pPlayer.blockPosition(), ModSounds.END_WALK.get(), SoundSource.PLAYERS, 1.0F, 0.5F);
                } else {
                    pPlayer.displayClientMessage(Component.translatable("info.goety.taglock.difDimension"), true);
                }
            } else if (pPlayer.getItemInHand(pHand).getItem() instanceof WaystoneItem && WaystoneItem.hasBlock(pPlayer.getItemInHand(pHand))){
                ItemStack itemStack = pPlayer.getItemInHand(pHand);
                GlobalPos globalPos = WaystoneItem.getPosition(pPlayer.getItemInHand(pHand));
                if (globalPos != null && globalPos.pos().distToCenterSqr(Vec3.atCenterOf(pPos)) <= Mth.square(1024.0F) && WaystoneItem.isSameDimension(pPlayer, itemStack) && pLevel.isLoaded(globalPos.pos())) {
                    SEHelper.setCamera(pPlayer, null, globalPos.pos());
                    ModNetwork.sendTo(pPlayer, new SPlayPlayerSoundPacket(ModSounds.END_WALK.get(), 1.0F, 0.5F));
                    pLevel.playSound(pPlayer, pPlayer.blockPosition(), ModSounds.END_WALK.get(), SoundSource.PLAYERS, 1.0F, 0.5F);
                } else {
                    pPlayer.displayClientMessage(Component.translatable("info.goety.waystone.difDimension"), true);
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

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return new ItemStack(ModBlocks.PALE_CRYSTAL_BALL.get());
    }

    public boolean isPathfindable(BlockState p_48799_, BlockGetter p_48800_, BlockPos p_48801_, PathComputationType p_48802_) {
        return false;
    }
}
