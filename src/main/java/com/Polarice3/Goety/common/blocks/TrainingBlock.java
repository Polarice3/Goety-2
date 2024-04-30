package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.TrainingBlockEntity;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public abstract class TrainingBlock extends BaseEntityBlock {
    public TrainingBlock(Properties p_49224_) {
        super(p_49224_);
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        BlockEntity tileentity = pLevel.getBlockEntity(pPos);
        if (pPlacer instanceof Player){
            if (tileentity instanceof TrainingBlockEntity blockEntity){
                blockEntity.setOwnerId(pPlacer.getUUID());
                blockEntity.setVariant(pStack, pLevel, pPos);
            }
        }
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity tileentity = pLevel.getBlockEntity(pPos);
        if (tileentity instanceof TrainingBlockEntity blockEntity) {
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            if (blockEntity.placeItem(itemstack)){
                return InteractionResult.SUCCESS;
            } else if (itemstack.isEmpty() && pPlayer.isCrouching()){
                blockEntity.setShowArea(!blockEntity.isShowArea());
                pLevel.playSound(null, pPos, ModSounds.CAST_SPELL.get(), SoundSource.BLOCKS, 0.25F, 2.0F);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Nullable
    public <T extends BlockEntity> GameEventListener getListener(ServerLevel p_222092_, T p_222093_) {
        return p_222093_ instanceof TrainingBlockEntity ? (TrainingBlockEntity)p_222093_ : null;
    }
}
