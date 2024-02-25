package com.Polarice3.Goety.common.magic.construct;

import com.Polarice3.Goety.api.magic.IMold;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.SquallGolem;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.SEHelper;
import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class SquallGolemMold implements IMold {
    private static final List<BlockPos> HIGHROCK_LOCATIONS = ImmutableList.of(
            new BlockPos(0, -1, 0),

            new BlockPos(0, -1, 1),
            new BlockPos(0, -1, -1),

            new BlockPos(1, -1, 0),
            new BlockPos(-1, -1, 0),

            new BlockPos(1, -1, 1),
            new BlockPos(1, -1, -1),

            new BlockPos(-1, -1, 1),
            new BlockPos(-1, -1, -1),

            new BlockPos(2, 0, 0),
            new BlockPos(2, 0, 1),
            new BlockPos(2, 0, -1),

            new BlockPos(-2, 0, 0),
            new BlockPos(-2, 0, 1),
            new BlockPos(-2, 0, -1),

            new BlockPos(0, 0, 2),
            new BlockPos(1, 0, 2),
            new BlockPos(-1, 0, 2),

            new BlockPos(0, 0, -2),
            new BlockPos(1, 0, -2),
            new BlockPos(-1, 0, -2)
    );
    private static final List<BlockPos> STONE_BRICKS_LOCATIONS = ImmutableList.of(
            new BlockPos(1, 0, 1),
            new BlockPos(1, 0, -1),
            new BlockPos(-1, 0, 1),
            new BlockPos(-1, 0, -1)
    );
    private static final List<BlockPos> INDENTED_GOLD_LOCATIONS = ImmutableList.of(
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0)
    );
    private static final BlockPos JADE = new BlockPos(0, 0, 0);

    private static List<BlockPos> checkHighrock(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : HIGHROCK_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(ModBlocks.POLISHED_HIGHROCK_BLOCK.get())){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkStoneBricks(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : STONE_BRICKS_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(BlockTags.STONE_BRICKS)){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkIndentedGold(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : INDENTED_GOLD_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(ModTags.Blocks.INDENTED_GOLD_BLOCKS)){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    public static boolean checkBlocks(Level level, BlockPos blockPos){
        return checkHighrock(level, blockPos).isEmpty() && checkStoneBricks(level, blockPos).isEmpty()
                && checkIndentedGold(level, blockPos).isEmpty();
    }

    @Override
    public boolean spawnServant(Player player, ItemStack stack, Level level, BlockPos blockPos) {
        if (!level.isClientSide) {
            if (level.getBlockState(blockPos).is(ModBlocks.JADE_BLOCK.get())) {
                if (checkBlocks(level, blockPos)) {
                    if (SEHelper.hasResearch(player, ResearchList.MISTRAL)) {
                        SquallGolem squallGolem = ModEntityType.SQUALL_GOLEM.get().create(level);
                        if (squallGolem != null) {
                            squallGolem.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(squallGolem.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                            squallGolem.setTrueOwner(player);
                            squallGolem.moveTo((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.05D, (double) blockPos.getZ() + 0.5D, 0.0F, 0.0F);
                            if (level.addFreshEntity(squallGolem)) {
                                removeBlocks(level, blockPos);
                                stack.shrink(1);
                                if (player instanceof ServerPlayer serverPlayer) {
                                    CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, squallGolem);
                                }
                                return true;
                            }
                        }
                    } else {
                        player.displayClientMessage(Component.translatable("info.goety.research.fail"), true);
                    }
                } else {
                    player.displayClientMessage(Component.translatable("info.goety.block.fail"), true);
                }
            }
        }
        return false;
    }

    public static void removeBlocks(Level level, BlockPos blockPos){
        if (!level.isClientSide) {
            for (BlockPos blockPos1 : STONE_BRICKS_LOCATIONS) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(BlockTags.STONE_BRICKS)) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
            for (BlockPos blockPos1 : INDENTED_GOLD_LOCATIONS) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(ModTags.Blocks.INDENTED_GOLD_BLOCKS)) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
            BlockPos blockPos2 = blockPos.offset(JADE);
            if (level.getBlockState(blockPos2).is(ModBlocks.JADE_BLOCK.get())) {
                level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
            }
        }
    }
}
