package com.Polarice3.Goety.common.magic.construct;

import com.Polarice3.Goety.api.magic.IMold;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.golem.RedstoneCube;
import com.Polarice3.Goety.common.entities.ally.golem.RedstoneGolem;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.SEHelper;
import com.google.common.collect.ImmutableList;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class RedstoneGolemMold implements IMold {
    private static final List<BlockPos> BOTTOM_STONE_LOCATIONS = ImmutableList.of(
            new BlockPos(0, -1, 0),

            new BlockPos(0, -1, 1),
            new BlockPos(0, -1, -1),

            new BlockPos(1, -1, 0),
            new BlockPos(-1, -1, 0),

            new BlockPos(1, -1, 1),
            new BlockPos(1, -1, -1),

            new BlockPos(-1, -1, 1),
            new BlockPos(-1, -1, -1),

            new BlockPos(2, -1, 1),
            new BlockPos(2, -1, -1),

            new BlockPos(-2, -1, 1),
            new BlockPos(-2, -1, -1),

            new BlockPos(1, -1, 2),
            new BlockPos(-1, -1, 2),

            new BlockPos(1, -1, -2),
            new BlockPos(-1, -1, -2)
    );
    private static final List<BlockPos> ABOVE_STONE_LOCATIONS = ImmutableList.of(
            new BlockPos(1, 0, 3),
            new BlockPos(-1, 0, 3),

            new BlockPos(1, 0, -3),
            new BlockPos(-1, 0, -3),

            new BlockPos(3, 0, 1),
            new BlockPos(3, 0, -1),

            new BlockPos(-3, 0, 1),
            new BlockPos(-3, 0, -1)
    );
    private static final List<BlockPos> STONE_LOCATIONS = Stream.of(BOTTOM_STONE_LOCATIONS, ABOVE_STONE_LOCATIONS).flatMap(Collection::stream).toList();
    private static final List<BlockPos> BOTTOM_DIAMOND_LOCATIONS = ImmutableList.of(
            new BlockPos(2, -1, 0),

            new BlockPos(-2, -1, 0),

            new BlockPos(0, -1, 2),

            new BlockPos(0, -1, -2)
    );
    private static final List<BlockPos> ABOVE_DIAMOND_LOCATIONS = ImmutableList.of(
            new BlockPos(0, 0, 3),

            new BlockPos(0, 0, -3),

            new BlockPos(3, 0, 0),

            new BlockPos(-3, 0, 0),

            new BlockPos(-2, 0, 2),
            new BlockPos(2, 0, 2),
            new BlockPos(2, 0, -2),
            new BlockPos(-2, 0, -2)
    );
    private static final List<BlockPos> DIAMOND_LOCATIONS = Stream.of(BOTTOM_DIAMOND_LOCATIONS, ABOVE_DIAMOND_LOCATIONS).flatMap(Collection::stream).toList();
    private static final List<BlockPos> LAVA_LOCATIONS = ImmutableList.of(
            new BlockPos(3, -1, 1),
            new BlockPos(3, -1, 0),
            new BlockPos(3, -1, -1),

            new BlockPos(-3, -1, 1),
            new BlockPos(-3, -1, 0),
            new BlockPos(-3, -1, -1),

            new BlockPos(1, -1, 3),
            new BlockPos(0, -1, 3),
            new BlockPos(-1, -1, 3),

            new BlockPos(1, -1, -3),
            new BlockPos(0, -1, -3),
            new BlockPos(-1, -1, -3),

            new BlockPos(-2, -1, 2),
            new BlockPos(2, -1, 2),
            new BlockPos(2, -1, -2),
            new BlockPos(-2, -1, -2),

            new BlockPos(4, 0, 1),
            new BlockPos(4, 0, 0),
            new BlockPos(4, 0, -1),

            new BlockPos(1, 0, 4),
            new BlockPos(0, 0, 4),
            new BlockPos(-1, 0, 4),

            new BlockPos(-4, 0, 1),
            new BlockPos(-4, 0, 0),
            new BlockPos(-4, 0, -1),

            new BlockPos(1, 0, -4),
            new BlockPos(0, 0, -4),
            new BlockPos(-1, 0, -4),

            new BlockPos(3, 0, 2),
            new BlockPos(3, 0, -2),

            new BlockPos(-3, 0, 2),
            new BlockPos(-3, 0, -2),

            new BlockPos(2, 0, 3),
            new BlockPos(2, 0, -3),

            new BlockPos(-2, 0, 3),
            new BlockPos(-2, 0, -3)
    );
    private static final List<BlockPos> REDSTONE_LOCATIONS = ImmutableList.of(
            new BlockPos(0, 0, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, -1),

            new BlockPos(1, 0, 1),
            new BlockPos(-1, 0, 1),

            new BlockPos(1, 0, -1),
            new BlockPos(-1, 0, -1),

            new BlockPos(0, 0, 2),
            new BlockPos(1, 0, 2),
            new BlockPos(-1, 0, 2),

            new BlockPos(0, 0, -2),
            new BlockPos(1, 0, -2),
            new BlockPos(-1, 0, -2),

            new BlockPos(2, 0, 0),
            new BlockPos(2, 0, 1),
            new BlockPos(2, 0, -1),

            new BlockPos(-2, 0, 0),
            new BlockPos(-2, 0, 1),
            new BlockPos(-2, 0, -1)
    );

    private static List<BlockPos> checkStones(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : STONE_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            BlockState blockState = level.getBlockState(blockPos2);
            if (!blockState.getBlock().getDescriptionId().contains("bricks")){
                invalid.add(blockPos1);
            }
            if (!blockState.isCollisionShapeFullBlock(level, blockPos2)){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkDiamonds(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : DIAMOND_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(Tags.Blocks.STORAGE_BLOCKS_DIAMOND)){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkLava(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : LAVA_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(Blocks.LAVA)){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkRedstone(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : REDSTONE_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(Tags.Blocks.STORAGE_BLOCKS_REDSTONE)){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    public static boolean checkBlocks(Level level, BlockPos blockPos){
        return checkStones(level, blockPos).isEmpty() && checkDiamonds(level, blockPos).isEmpty()
                && checkLava(level, blockPos).isEmpty() && checkRedstone(level, blockPos).isEmpty();
    }

    public boolean conditionsMet(Level worldIn, LivingEntity entityLiving) {
        int count = 0;
        if (worldIn instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof RedstoneGolem servant) {
                    if (servant.getTrueOwner() == entityLiving) {
                        ++count;
                    }
                }
            }
        }
        return count < SpellConfig.RedstoneGolemLimit.get();
    }

    @Override
    public boolean spawnServant(Player player, ItemStack stack, Level level, BlockPos blockPos){
        if (!level.isClientSide) {
            if (level.getBlockState(blockPos).is(Tags.Blocks.STORAGE_BLOCKS_REDSTONE)) {
                if (checkBlocks(level, blockPos)) {
                    if (SEHelper.hasResearch(player, ResearchList.WARRED)) {
                        if (conditionsMet(level, player)) {
                            RedstoneGolem redstoneGolem1 = ModEntityType.REDSTONE_GOLEM.get().create(level);
                            if (redstoneGolem1 != null) {
                                redstoneGolem1.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(redstoneGolem1.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                                redstoneGolem1.setTrueOwner(player);
                                redstoneGolem1.moveTo((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.05D, (double) blockPos.getZ() + 0.5D, 0.0F, 0.0F);
                                if (level.addFreshEntity(redstoneGolem1)) {
                                    removeBlocks(level, blockPos);
                                    stack.shrink(1);
                                    if (player instanceof ServerPlayer serverPlayer) {
                                        CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, redstoneGolem1);
                                    }
                                    return true;
                                }
                            }
                        } else {
                            player.displayClientMessage(Component.translatable("info.goety.summon.limit"), true);
                        }
                    } else {
                        player.displayClientMessage(Component.translatable("info.goety.research.fail"), true);
                    }
                } else {
//                    player.displayClientMessage(Component.translatable("info.goety.block.fail"), true);
                    RedstoneCube redstoneCube = ModEntityType.REDSTONE_CUBE.get().create(level);
                    if (redstoneCube != null) {
                        redstoneCube.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(redstoneCube.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                        redstoneCube.setTrueOwner(player);
                        redstoneCube.moveTo((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.05D, (double) blockPos.getZ() + 0.5D, 0.0F, 0.0F);
                        if (level.addFreshEntity(redstoneCube)) {
                            level.levelEvent(2001, blockPos, Block.getId(level.getBlockState(blockPos)));
                            level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                            stack.shrink(1);
                            if (player instanceof ServerPlayer serverPlayer) {
                                CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, redstoneCube);
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void removeBlocks(Level level, BlockPos blockPos){
        if (!level.isClientSide) {
            if (MobsConfig.RedstoneGolemMold.get()) {
                for (BlockPos blockPos1 : DIAMOND_LOCATIONS) {
                    BlockPos blockPos2 = blockPos.offset(blockPos1);
                    if (level.getBlockState(blockPos2).is(Tags.Blocks.STORAGE_BLOCKS_DIAMOND)) {
                        if (level.random.nextFloat() < 0.12F) {
                            level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                            level.setBlockAndUpdate(blockPos2, Blocks.OBSIDIAN.defaultBlockState());
                        }
                    }
                }
            }
            for (BlockPos blockPos1 : LAVA_LOCATIONS) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(Blocks.LAVA)) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
            for (BlockPos blockPos1 : REDSTONE_LOCATIONS) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(Tags.Blocks.STORAGE_BLOCKS_REDSTONE)) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }
}
