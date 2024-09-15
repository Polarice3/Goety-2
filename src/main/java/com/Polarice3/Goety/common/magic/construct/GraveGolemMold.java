package com.Polarice3.Goety.common.magic.construct;

import com.Polarice3.Goety.api.magic.IMold;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.undead.GraveGolem;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.SEHelper;
import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
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

import java.util.ArrayList;
import java.util.List;

public class GraveGolemMold implements IMold {
    private static final List<BlockPos> SOUL_SAND_LOCATIONS = ImmutableList.of(
            new BlockPos(1, -1, 1),
            new BlockPos(1, -1, -1),

            new BlockPos(-1, -1, 1),
            new BlockPos(-1, -1, -1)
    );
    private static final List<BlockPos> COARSE_DIRT_LOCATIONS = ImmutableList.of(
            new BlockPos(0, -1, 2),
            new BlockPos(1, -1, 2),
            new BlockPos(-1, -1, 2),

            new BlockPos(2, -1, -2),
            new BlockPos(2, -1, -1),
            new BlockPos(2, -1, 0),
            new BlockPos(2, -1, 1),
            new BlockPos(2, -1, 2),

            new BlockPos(-2, -1, -2),
            new BlockPos(-2, -1, -1),
            new BlockPos(-2, -1, 0),
            new BlockPos(-2, -1, 1),
            new BlockPos(-2, -1, 2),

            new BlockPos(0, -1, -2),
            new BlockPos(1, -1, -2),
            new BlockPos(-1, -1, -2)
    );
    private static final List<BlockPos> DARK_METAL_LOCATIONS = ImmutableList.of(
            new BlockPos(-3, 0, 3),
            new BlockPos(3, 0, 3),
            new BlockPos(3, 0, -3),
            new BlockPos(-3, 0, -3)
    );
    private static final List<BlockPos> SHADE_STONE_LOCATIONS = ImmutableList.of(
            new BlockPos(0, -1, 0),

            new BlockPos(0, -1, 1),
            new BlockPos(0, -1, -1),

            new BlockPos(1, -1, 0),
            new BlockPos(-1, -1, 0),

            new BlockPos(0, 0, 2),
            new BlockPos(1, 0, 2),
            new BlockPos(-1, 0, 2),

            new BlockPos(2, 0, -2),
            new BlockPos(2, 0, -1),
            new BlockPos(2, 0, 0),
            new BlockPos(2, 0, 1),
            new BlockPos(2, 0, 2),

            new BlockPos(-2, 0, -2),
            new BlockPos(-2, 0, -1),
            new BlockPos(-2, 0, 0),
            new BlockPos(-2, 0, 1),
            new BlockPos(-2, 0, 2),

            new BlockPos(0, 0, -2),
            new BlockPos(1, 0, -2),
            new BlockPos(-1, 0, -2)
    );
    private static final List<BlockPos> STONE_BRICKS_LOCATIONS = ImmutableList.of(
            new BlockPos(0, 0, 3),
            new BlockPos(1, 0, 3),
            new BlockPos(-1, 0, 3),

            new BlockPos(3, 0, -2),
            new BlockPos(3, 0, -1),
            new BlockPos(3, 0, 0),
            new BlockPos(3, 0, 1),
            new BlockPos(3, 0, 2),

            new BlockPos(-3, 0, -2),
            new BlockPos(-3, 0, -1),
            new BlockPos(-3, 0, 0),
            new BlockPos(-3, 0, 1),
            new BlockPos(-3, 0, 2),

            new BlockPos(0, 0, -3),
            new BlockPos(1, 0, -3),
            new BlockPos(-1, 0, -3)
    );
    private static final List<BlockPos> SKULL_PILE_LOCATIONS = ImmutableList.of(
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, -1),
            new BlockPos(0, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(1, 0, 0)
    );
    private static final List<BlockPos> BONE_LOCATIONS = ImmutableList.of(
            new BlockPos(1, 0, 1),
            new BlockPos(-1, 0, 1),

            new BlockPos(1, 0, -1),
            new BlockPos(-1, 0, -1)
    );

    private static List<BlockPos> checkSoulSand(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : SOUL_SAND_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(BlockTags.SOUL_FIRE_BASE_BLOCKS)){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkBones(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : BONE_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(Blocks.BONE_BLOCK)){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkCoarseDirt(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : COARSE_DIRT_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(Blocks.COARSE_DIRT)){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkDarkMetals(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : DARK_METAL_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(ModBlocks.DARK_METAL_BLOCK.get())){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkStones(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : STONE_BRICKS_LOCATIONS){
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

    private static List<BlockPos> checkShadeStone(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : SHADE_STONE_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(ModTags.Blocks.SHADE_STONE)){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkSkullPiles(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : SKULL_PILE_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(ModBlocks.SKULL_PILE.get())){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    public static boolean checkBlocks(Level level, BlockPos blockPos){
        return checkSoulSand(level, blockPos).isEmpty() && checkBones(level, blockPos).isEmpty() && checkCoarseDirt(level, blockPos).isEmpty()
                && checkDarkMetals(level, blockPos).isEmpty() && checkShadeStone(level, blockPos).isEmpty() && checkSkullPiles(level, blockPos).isEmpty()
                && checkStones(level, blockPos).isEmpty();
    }

    public static boolean conditionsMet(Level worldIn, LivingEntity entityLiving) {
        int count = 0;
        if (worldIn instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof GraveGolem servant) {
                    if (servant.getTrueOwner() == entityLiving && servant.isAlive()) {
                        ++count;
                    }
                }
            }
        }
        return count < SpellConfig.GraveGolemLimit.get();
    }

    @Override
    public boolean spawnServant(Player player, ItemStack stack, Level level, BlockPos blockPos){
        if (!level.isClientSide) {
            if (level.getBlockState(blockPos).is(ModBlocks.SKULL_PILE.get())) {
                if (checkBlocks(level, blockPos)) {
                    if (SEHelper.hasResearch(player, ResearchList.WARRED) && SEHelper.hasResearch(player, ResearchList.HAUNTING)) {
                        if (conditionsMet(level, player)) {
                            GraveGolem graveGolem = ModEntityType.GRAVE_GOLEM.get().create(level);
                            if (graveGolem != null) {
                                graveGolem.setTrueOwner(player);
                                graveGolem.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(graveGolem.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                                graveGolem.moveTo((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.05D, (double) blockPos.getZ() + 0.5D, 0.0F, 0.0F);
                                if (level.addFreshEntity(graveGolem)) {
                                    removeBlocks(level, blockPos);
                                    stack.shrink(1);
                                    if (player instanceof ServerPlayer serverPlayer) {
                                        CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, graveGolem);
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
                    player.displayClientMessage(Component.translatable("info.goety.block.fail"), true);
                }
            }
        }
        return false;
    }

    public static void removeBlocks(Level level, BlockPos blockPos){
        if (!level.isClientSide) {
            for (BlockPos blockPos1 : COARSE_DIRT_LOCATIONS) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(Blocks.COARSE_DIRT)) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.DIRT.defaultBlockState());
                }
            }
            for (BlockPos blockPos1 : SOUL_SAND_LOCATIONS) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.SAND.defaultBlockState());
                }
            }
            for (BlockPos blockPos1 : SHADE_STONE_LOCATIONS) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(ModTags.Blocks.SHADE_STONE)) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
            for (BlockPos blockPos1 : BONE_LOCATIONS) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(Blocks.BONE_BLOCK)) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
            for (BlockPos blockPos1 : SKULL_PILE_LOCATIONS) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(ModBlocks.SKULL_PILE.get())) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }
}
