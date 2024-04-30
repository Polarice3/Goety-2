package com.Polarice3.Goety.common.magic.construct;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.SlimeServant;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnFromBlock {

    public static boolean spawnServant(Player player, ItemStack stack, Level level, BlockPos blockPos) {
        BlockState blockState = level.getBlockState(blockPos);
        if (!level.isClientSide) {
            Summoned summoned = null;
            if (blockState.is(Blocks.SLIME_BLOCK)) {
                summoned = ModEntityType.SLIME_SERVANT.get().create(level);
            } else if (blockState.is(Blocks.MAGMA_BLOCK)){
                summoned = ModEntityType.MAGMA_CUBE_SERVANT.get().create(level);
            }
            if (summoned != null){
                summoned.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(summoned.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                summoned.setTrueOwner(player);
                summoned.moveTo((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.05D, (double) blockPos.getZ() + 0.5D, 0.0F, 0.0F);
                if (summoned instanceof SlimeServant slime) {
                    slime.setSize(2, true);
                }
                if (level.addFreshEntity(summoned)) {
                    level.levelEvent(2001, blockPos, Block.getId(level.getBlockState(blockPos)));
                    level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                    stack.shrink(1);
                    if (player instanceof ServerPlayer serverPlayer) {
                        CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, summoned);
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
