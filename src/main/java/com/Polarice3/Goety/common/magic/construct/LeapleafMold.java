package com.Polarice3.Goety.common.magic.construct;

import com.Polarice3.Goety.api.magic.IMold;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Leapleaf;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.utils.SEHelper;
import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class LeapleafMold implements IMold {
    private static final List<BlockPos> ROOTS_LOCATIONS = ImmutableList.of(
            new BlockPos(0, 0, 0),
            new BlockPos(0, -1, 0),
            new BlockPos(1, -1, 0),
            new BlockPos(-1, -1, 0)
    );
    private static final List<BlockPos> ROOTS_LOCATIONS_Z = ImmutableList.of(
            new BlockPos(0, 0, 0),
            new BlockPos(0, -1, 0),
            new BlockPos(0, -1, 1),
            new BlockPos(0, -1, -1)
    );
    private static final List<BlockPos> LEAVES_LOCATIONS = ImmutableList.of(
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0)
    );
    private static final List<BlockPos> LEAVES_LOCATIONS_Z = ImmutableList.of(
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1)
    );
    private static final BlockPos CORPSE_BLOSSOM = new BlockPos(0, 1, 0);

    private static List<BlockPos> checkRootsX(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : ROOTS_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(ModBlocks.OVERGROWN_ROOTS.get())){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkRootsZ(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : ROOTS_LOCATIONS_Z){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(ModBlocks.OVERGROWN_ROOTS.get())){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkLeavesX(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : LEAVES_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(Blocks.JUNGLE_LEAVES)){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkLeavesZ(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : LEAVES_LOCATIONS_Z){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(Blocks.JUNGLE_LEAVES)){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    public static boolean canSpawn(Level level, BlockPos blockPos){
        if (checkRootsX(level, blockPos).isEmpty() && checkLeavesX(level, blockPos).isEmpty()){
            for (BlockPos blockPos1 : ROOTS_LOCATIONS) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(ModBlocks.OVERGROWN_ROOTS.get())) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
            for (BlockPos blockPos1 : LEAVES_LOCATIONS) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(Blocks.JUNGLE_LEAVES)) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
            return level.getBlockState(blockPos.offset(CORPSE_BLOSSOM)).is(ModBlocks.CORPSE_BLOSSOM.get());
        } else if (checkRootsZ(level, blockPos).isEmpty() && checkLeavesZ(level, blockPos).isEmpty()) {
            for (BlockPos blockPos1 : ROOTS_LOCATIONS_Z) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(ModBlocks.OVERGROWN_ROOTS.get())) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
            for (BlockPos blockPos1 : LEAVES_LOCATIONS_Z) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(Blocks.JUNGLE_LEAVES)) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
            return level.getBlockState(blockPos.offset(CORPSE_BLOSSOM)).is(ModBlocks.CORPSE_BLOSSOM.get());
        } else {
            return false;
        }
    }

    @Override
    public boolean spawnServant(Player player, ItemStack stack, Level level, BlockPos blockPos) {
        if (!level.isClientSide) {
            if (level.getBlockState(blockPos).is(ModBlocks.OVERGROWN_ROOTS.get())) {
                if (SEHelper.hasResearch(player, ResearchList.FLORAL)) {
                    if (canSpawn(level, blockPos)) {
                        Leapleaf leapleaf = ModEntityType.LEAPLEAF.get().create(level);
                        if (leapleaf != null) {
                            leapleaf.setTrueOwner(player);
                            leapleaf.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(leapleaf.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                            leapleaf.moveTo((double) blockPos.getX() + 0.5D, (double) blockPos.below().getY() + 0.05D, (double) blockPos.getZ() + 0.5D, 0.0F, 0.0F);
                            if (level.addFreshEntity(leapleaf)) {
                                removeBlocks(level, blockPos);
                                stack.shrink(1);
                                if (player instanceof ServerPlayer serverPlayer) {
                                    CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, leapleaf);
                                }
                                return true;
                            }
                        }
                    } else {
                        player.displayClientMessage(Component.translatable("info.goety.block.fail"), true);
                    }
                }
                else {
                    player.displayClientMessage(Component.translatable("info.goety.research.fail"), true);
                }
            }
        }
        return false;
    }

    public static void removeBlocks(Level level, BlockPos blockPos){
        if (!level.isClientSide) {
            BlockPos blockPos2 = blockPos.offset(CORPSE_BLOSSOM);
            if (level.getBlockState(blockPos2).is(ModBlocks.CORPSE_BLOSSOM.get())) {
                level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
            }
        }
    }
}
