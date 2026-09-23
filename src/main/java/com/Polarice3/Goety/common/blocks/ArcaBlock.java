package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.ArcaBlockEntity;
import com.Polarice3.Goety.common.capabilities.soulenergy.ISoulEnergy;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.jetbrains.annotations.Nullable;

public class ArcaBlock extends BaseEntityBlock implements IForgeBlock {

    public ArcaBlock() {
        super(Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(75.0F, 2400.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
                .forceSolidOn()
                .noOcclusion()
        );
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        BlockEntity tileentity = pLevel.getBlockEntity(pPos);
        if (pPlacer instanceof Player player){
            ISoulEnergy soulEnergy = SEHelper.getCapability(player);
            soulEnergy.setArcaBlock(pPos);
            soulEnergy.setArcaBlockDimension(pLevel.dimension());
            soulEnergy.setSEActive(true);
            if (tileentity instanceof ArcaBlockEntity arcaTile){
                arcaTile.setOwner(pPlacer);
            }
            SEHelper.sendSEUpdatePacket(player);
        }
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pState.hasBlockEntity()) {
            BlockEntity tileEntity = pLevel.getBlockEntity(pPos);
            if (tileEntity instanceof ArcaBlockEntity arcaTileEntity) {
                if (arcaTileEntity.getPlayer() == pPlayer) {
                    ISoulEnergy soulEnergy = SEHelper.getCapability(arcaTileEntity.getPlayer());
                    ItemStack itemStack = pPlayer.getMainHandItem();
                    if (itemStack.isEmpty()) {
                        if (!pPlayer.isShiftKeyDown() && !pPlayer.isCrouching()){
                            if (soulEnergy.getArcaBlock() == null){
                                soulEnergy.setArcaBlock(arcaTileEntity.getBlockPos());
                                soulEnergy.setArcaBlockDimension(pLevel.dimension());
                                if (!soulEnergy.getSEActive()){
                                    soulEnergy.setSEActive(true);
                                }
                                SEHelper.sendSEUpdatePacket(arcaTileEntity.getPlayer());
                                return InteractionResult.SUCCESS;
                            }
                        } else {
                            pLevel.destroyBlock(pPos, true);
                            return InteractionResult.sidedSuccess(pLevel.isClientSide);
                        }
                    } else if (itemStack.is(ModItems.WAYSTONE.get())) {
                        if (WaystoneItem.isSameDimension(tileEntity, itemStack)){
                            BlockPos blockPos = WaystoneItem.getBlockPos(itemStack);
                            if (blockPos != null && soulEnergy.getArcaRevivePos() != blockPos) {
                                if (blockPos.distToCenterSqr(pPos.getCenter()) <= Mth.square(8)) {
                                    soulEnergy.setArcaRevivePos(blockPos);
                                    SEHelper.sendSEUpdatePacket(pPlayer);
                                    if (pLevel instanceof ServerLevel) {
                                        ModNetwork.sendTo(pPlayer, new SPlayPlayerSoundPacket(SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, 1.0F, 1.0F));
                                    }
                                    pPlayer.displayClientMessage(Component.translatable("info.goety.waystone.revivePos"), true);
                                    return InteractionResult.sidedSuccess(pLevel.isClientSide);
                                } else {
                                    pPlayer.displayClientMessage(Component.translatable("info.goety.waystone.difDimension"), true);
                                    return InteractionResult.FAIL;
                                }
                            }
                        } else {
                            pPlayer.displayClientMessage(Component.translatable("info.goety.waystone.difDimension"), true);
                            return InteractionResult.FAIL;
                        }
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }


    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity tileEntity = pLevel.getBlockEntity(pPos);
            if (tileEntity instanceof ArcaBlockEntity arcaTileEntity){
                if (arcaTileEntity.getPlayer() != null){
                    ISoulEnergy soulEnergy = SEHelper.getCapability(arcaTileEntity.getPlayer());
                    if (soulEnergy.getArcaBlock() == arcaTileEntity.getBlockPos()) {
                        soulEnergy.setSEActive(false);
                        soulEnergy.setArcaBlock(null);
                        soulEnergy.setArcaRevivePos(null);
                        soulEnergy.setArcaBlockDimension(null);
                        SEHelper.sendSEUpdatePacket(arcaTileEntity.getPlayer());
                    }
                }
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    public boolean isPathfindable(BlockState p_51264_, BlockGetter p_51265_, BlockPos p_51266_, PathComputationType p_51267_) {
        return false;
    }

    public RenderShape getRenderShape(BlockState p_51307_) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new ArcaBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152755_, BlockState p_152756_, BlockEntityType<T> p_152757_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof ArcaBlockEntity arcaBlock)
                arcaBlock.tick();
        };
    }
}
