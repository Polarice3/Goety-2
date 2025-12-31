package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.VoidFrameBlock;
import com.Polarice3.Goety.common.blocks.entities.VoidFrameBlockEntity;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.hostile.ender.Endersent;
import com.Polarice3.Goety.common.entities.util.SummonCircleBoss;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class VoidKeyItem extends ItemBase{

    public InteractionResult useOn(UseOnContext p_41182_) {
        Level level = p_41182_.getLevel();
        BlockPos blockPos = p_41182_.getClickedPos();
        BlockState blockstate = level.getBlockState(blockPos);
        if (blockstate.is(ModBlocks.VOID_FRAME.get()) && blockstate.getValue(VoidFrameBlock.LOCKED)) {
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            } else if (level instanceof ServerLevel serverLevel){
                BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
                VoidFrameBlockEntity blockEntity1 = null;
                if (blockEntity instanceof VoidFrameBlockEntity voidFrameBlock) {
                    blockEntity1 = voidFrameBlock;
                }
                BlockState blockState2 = blockstate.setValue(VoidFrameBlock.LOCKED, false);
                level.setBlock(blockPos, blockState2, 2);
                level.updateNeighbourForOutputSignal(blockPos, ModBlocks.VOID_FRAME.get());
                p_41182_.getItemInHand().shrink(1);
                serverLevel.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), ModSounds.VOID_FRAME_UNLOCK.get(), SoundSource.BLOCKS, 2.0F, 1.0F);
                Vec3 vec3 = blockPos.above().getCenter();
                Endersent endersent = new Endersent(ModEntityType.ENDERSENT.get(), serverLevel);
                //If Void Frame Block Entity is available and has saved custom EyeType, use that.
                if (blockEntity1 != null && blockEntity1.getEyeType() > 0) {
                    endersent.setEyeType(blockEntity1.getEyeType());
                } else {
                    //4 = Random Enchanted Endersent, 5 = Regular Endersent, 0-3 = Set Endersent
                    if (blockState2.getValue(VoidFrameBlock.TYPE) == 4) {
                        endersent.setEyeType(serverLevel.getRandom().nextIntBetweenInclusive(1, 4));
                    } else if (blockState2.getValue(VoidFrameBlock.TYPE) == 5) {
                        endersent.setEyeType(0);
                    } else {
                        endersent.setEyeType(blockState2.getValue(VoidFrameBlock.TYPE) + 1);
                    }
                }
                //If Void Frame Block Entity is available and has saved custom EyeEffects, use those.
                if (blockEntity1 != null && !blockEntity1.getEyeEffects().isEmpty()) {
                    endersent.setEyeEffects(blockEntity1.getEyeEffects());
                }
                endersent.setPos(vec3);
                endersent.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, null, null);
                endersent.setVoidFramePos(blockPos);
                endersent.setPersistenceRequired();
                SummonCircleBoss circleBoss = new SummonCircleBoss(level, vec3, endersent);
                serverLevel.addFreshEntity(circleBoss);

                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }
}
