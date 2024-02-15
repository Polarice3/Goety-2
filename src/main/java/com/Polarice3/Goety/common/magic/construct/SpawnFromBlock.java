package com.Polarice3.Goety.common.magic.construct;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.SlimeServant;
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

public class SpawnFromBlock {

    public static boolean spawnServant(Player player, ItemStack stack, Level level, BlockPos blockPos) {
        if (!level.isClientSide) {
            SlimeServant slime = null;
            if (level.getBlockState(blockPos).is(Blocks.SLIME_BLOCK)) {
                slime = ModEntityType.SLIME_SERVANT.get().create(level);
            } else if (level.getBlockState(blockPos).is(Blocks.MAGMA_BLOCK)){
                slime = ModEntityType.MAGMA_CUBE_SERVANT.get().create(level);
            }
            if (slime != null){
                slime.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(slime.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                slime.setTrueOwner(player);
                slime.moveTo((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.05D, (double) blockPos.getZ() + 0.5D, 0.0F, 0.0F);
                slime.setSize(2, true);
                if (level.addFreshEntity(slime)) {
                    level.levelEvent(2001, blockPos, Block.getId(level.getBlockState(blockPos)));
                    level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                    stack.shrink(1);
                    if (player instanceof ServerPlayer serverPlayer) {
                        CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, slime);
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
