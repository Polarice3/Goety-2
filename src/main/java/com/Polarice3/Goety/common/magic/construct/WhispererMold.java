package com.Polarice3.Goety.common.magic.construct;

import com.Polarice3.Goety.api.magic.IMold;
import com.Polarice3.Goety.common.blocks.CorpseBlossomBlock;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Whisperer;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class WhispererMold implements IMold {
    private static final BlockPos LEAVES = new BlockPos(0, -1, 0);
    private static final BlockPos MOSS = new BlockPos(0, 0, 0);
    private static final BlockPos CORPSE_BLOSSOM = new BlockPos(0, 1, 0);

    @Override
    public boolean spawnServant(Player player, ItemStack stack, Level level, BlockPos blockPos) {
        if (!level.isClientSide) {
            if (level.getBlockState(blockPos).is(Blocks.MOSS_BLOCK)) {
                if (level.getBlockState(blockPos.offset(MOSS)).is(Blocks.MOSS_BLOCK)
                        && level.getBlockState(blockPos.offset(LEAVES)).is(ModBlocks.OVERGROWN_ROOTS.get())
                        && level.getBlockState(blockPos.offset(CORPSE_BLOSSOM)).is(ModBlocks.CORPSE_BLOSSOM.get())) {
                    if (conditionsMet(level, player)) {
                        if (SEHelper.hasResearch(player, ResearchList.FLORAL)) {
                            Whisperer whisperer = ModEntityType.WHISPERER.get().create(level);
                            if (level.getBlockState(blockPos.offset(CORPSE_BLOSSOM)).getValue(CorpseBlossomBlock.WATERLOGGED)) {
                                whisperer = ModEntityType.WAVEWHISPERER.get().create(level);
                            }
                            if (whisperer != null) {
                                whisperer.setTrueOwner(player);
                                whisperer.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(whisperer.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                                whisperer.moveTo((double) blockPos.getX() + 0.5D, (double) blockPos.below().getY() + 0.05D, (double) blockPos.getZ() + 0.5D, 0.0F, 0.0F);
                                if (level.addFreshEntity(whisperer)) {
                                    removeBlocks(level, blockPos);
                                    stack.shrink(1);
                                    if (player instanceof ServerPlayer serverPlayer) {
                                        CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, whisperer);
                                    }
                                    return true;
                                }
                            }
                        } else {
                            player.displayClientMessage(Component.translatable("info.goety.research.fail"), true);
                        }
                    } else {
                        player.displayClientMessage(Component.translatable("info.goety.summon.limit"), true);                    }
                } else {
                    player.displayClientMessage(Component.translatable("info.goety.block.fail"), true);
                }
            }
        }
        return false;
    }

    public static void removeBlocks(Level level, BlockPos blockPos){
        if (!level.isClientSide) {
            BlockPos blockPos0 = blockPos.offset(MOSS);
            if (level.getBlockState(blockPos0).is(Blocks.MOSS_BLOCK)) {
                level.levelEvent(2001, blockPos0, Block.getId(level.getBlockState(blockPos0)));
                level.setBlockAndUpdate(blockPos0, Blocks.AIR.defaultBlockState());
            }
            BlockPos blockPos1 = blockPos.offset(LEAVES);
            if (level.getBlockState(blockPos1).is(ModBlocks.OVERGROWN_ROOTS.get())) {
                level.levelEvent(2001, blockPos1, Block.getId(level.getBlockState(blockPos1)));
                level.setBlockAndUpdate(blockPos1, Blocks.AIR.defaultBlockState());
            }
            BlockPos blockPos2 = blockPos.offset(CORPSE_BLOSSOM);
            if (level.getBlockState(blockPos2).is(ModBlocks.CORPSE_BLOSSOM.get())) {
                level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
            }
        }
    }

    public boolean conditionsMet(Level worldIn, LivingEntity entityLiving) {
        int count = 0;
        if (worldIn instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof Whisperer servant) {
                    if (servant.getTrueOwner() == entityLiving && servant.isAlive()) {
                        ++count;
                    }
                }
            }
        }
        return count < SpellConfig.WhisperLimit.get();
    }
}
