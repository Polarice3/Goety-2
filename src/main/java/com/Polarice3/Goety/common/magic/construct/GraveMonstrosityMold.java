package com.Polarice3.Goety.common.magic.construct;

import com.Polarice3.Goety.api.entities.IMonstrosity;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.magic.IMold;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.undead.GraveMonstrosity;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.config.MobsConfig;
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
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class GraveMonstrosityMold implements IMold {
    private static final List<BlockPos> BOTTOM_STONE_LOCATIONS = ImmutableList.of(
            new BlockPos(0, -1, 0),
            new BlockPos(0, -1, 2),
            new BlockPos(0, -1, 3),
            new BlockPos(0, -1, 4),
            new BlockPos(0, -1, -2),
            new BlockPos(0, -1, -3),
            new BlockPos(0, -1, -4),

            new BlockPos(1, -1, 1),
            new BlockPos(1, -1, -1),
            new BlockPos(1, -1, 2),
            new BlockPos(1, -1, -2),
            new BlockPos(1, -1, 3),
            new BlockPos(1, -1, -3),
            new BlockPos(1, -1, 5),
            new BlockPos(1, -1, -5),

            new BlockPos(-1, -1, 1),
            new BlockPos(-1, -1, -1),
            new BlockPos(-1, -1, 2),
            new BlockPos(-1, -1, -2),
            new BlockPos(-1, -1, 3),
            new BlockPos(-1, -1, -3),
            new BlockPos(-1, -1, 5),
            new BlockPos(-1, -1, -5),

            new BlockPos(2, -1, 0),
            new BlockPos(2, -1, 1),
            new BlockPos(2, -1, -1),
            new BlockPos(2, -1, 2),
            new BlockPos(2, -1, -2),
            new BlockPos(2, -1, 3),
            new BlockPos(2, -1, -3),
            new BlockPos(2, -1, 4),
            new BlockPos(2, -1, -4),

            new BlockPos(-2, -1, 0),
            new BlockPos(-2, -1, 1),
            new BlockPos(-2, -1, -1),
            new BlockPos(-2, -1, 2),
            new BlockPos(-2, -1, -2),
            new BlockPos(-2, -1, 3),
            new BlockPos(-2, -1, -3),
            new BlockPos(-2, -1, 4),
            new BlockPos(-2, -1, -4),

            new BlockPos(3, -1, 0),
            new BlockPos(3, -1, 1),
            new BlockPos(3, -1, -1),
            new BlockPos(3, -1, 2),
            new BlockPos(3, -1, -2),
            new BlockPos(3, -1, 3),
            new BlockPos(3, -1, -3),
            new BlockPos(3, -1, 4),
            new BlockPos(3, -1, -4),

            new BlockPos(-3, -1, 0),
            new BlockPos(-3, -1, 1),
            new BlockPos(-3, -1, -1),
            new BlockPos(-3, -1, 2),
            new BlockPos(-3, -1, -2),
            new BlockPos(-3, -1, 3),
            new BlockPos(-3, -1, -3),
            new BlockPos(-3, -1, 4),
            new BlockPos(-3, -1, -4),

            new BlockPos(4, -1, 0),
            new BlockPos(4, -1, 2),
            new BlockPos(4, -1, -2),
            new BlockPos(4, -1, 3),
            new BlockPos(4, -1, -3),

            new BlockPos(-4, -1, 0),
            new BlockPos(-4, -1, 2),
            new BlockPos(-4, -1, -2),
            new BlockPos(-4, -1, 3),
            new BlockPos(-4, -1, -3),

            new BlockPos(5, -1, 1),
            new BlockPos(5, -1, -1),

            new BlockPos(-5, -1, 1),
            new BlockPos(-5, -1, -1)
    );
    private static final List<BlockPos> ABOVE_STONE_LOCATIONS = ImmutableList.of(
            new BlockPos(1, 0, 6),
            new BlockPos(1, 0, -6),

            new BlockPos(3, 0, 5),
            new BlockPos(3, 0, -5),

            new BlockPos(-3, 0, 5),
            new BlockPos(-3, 0, -5),

            new BlockPos(5, 0, 3),
            new BlockPos(5, 0, -3),

            new BlockPos(-5, 0, 3),
            new BlockPos(-5, 0, -3),

            new BlockPos(6, 0, 1),
            new BlockPos(6, 0, -1),

            new BlockPos(-6, 0, 1),
            new BlockPos(-6, 0, -1)
    );
    private static final List<BlockPos> STONE_LOCATIONS = Stream.of(BOTTOM_STONE_LOCATIONS, ABOVE_STONE_LOCATIONS).flatMap(Collection::stream).toList();
    private static final List<BlockPos> BOTTOM_MOLD_LOCATIONS = ImmutableList.of(
            new BlockPos(0, -1, 1),
            new BlockPos(0, -1, -1),
            new BlockPos(0, -1, 5),
            new BlockPos(0, -1, -5),

            new BlockPos(1, -1, 0),
            new BlockPos(1, -1, 4),
            new BlockPos(1, -1, -4),

            new BlockPos(-1, -1, 0),
            new BlockPos(-1, -1, 4),
            new BlockPos(-1, -1, -4),

            new BlockPos(2, -1, 5),
            new BlockPos(2, -1, -5),

            new BlockPos(-2, -1, 5),
            new BlockPos(-2, -1, -5),

            new BlockPos(4, -1, 1),
            new BlockPos(4, -1, -1),

            new BlockPos(-4, -1, 1),
            new BlockPos(-4, -1, -1),

            new BlockPos(5, -1, 0),
            new BlockPos(5, -1, 2),
            new BlockPos(5, -1, -2),

            new BlockPos(-5, -1, 0),
            new BlockPos(-5, -1, 2),
            new BlockPos(-5, -1, -2)
    );
    private static final List<BlockPos> ABOVE_MOLD_LOCATIONS = ImmutableList.of(
            new BlockPos(0, 0, 6),
            new BlockPos(0, 0, -6),

            new BlockPos(2, 0, 6),
            new BlockPos(2, 0, -6),

            new BlockPos(-2, 0, 6),
            new BlockPos(-2, 0, -6),

            new BlockPos(4, 0, 4),
            new BlockPos(4, 0, -4),

            new BlockPos(-4, 0, 4),
            new BlockPos(-4, 0, -4),

            new BlockPos(6, 0, 0),
            new BlockPos(6, 0, 2),
            new BlockPos(6, 0, -2),

            new BlockPos(-6, 0, 0),
            new BlockPos(-6, 0, 2),
            new BlockPos(-6, 0, -2)
    );
    private static final List<BlockPos> MOLD_LOCATIONS = Stream.of(BOTTOM_MOLD_LOCATIONS, ABOVE_MOLD_LOCATIONS).flatMap(Collection::stream).toList();
    private static final List<BlockPos> BOTTOM_SOUL_FIRE_LOCATIONS = ImmutableList.of(
            new BlockPos(0, -1, 6),
            new BlockPos(0, -1, -6),

            new BlockPos(1, -1, 6),
            new BlockPos(1, -1, -6),

            new BlockPos(-1, -1, 6),
            new BlockPos(-1, -1, -6),

            new BlockPos(2, -1, 6),
            new BlockPos(2, -1, -6),

            new BlockPos(-2, -1, 6),
            new BlockPos(-2, -1, -6),

            new BlockPos(3, -1, 5),
            new BlockPos(3, -1, -5),

            new BlockPos(-3, -1, 5),
            new BlockPos(-3, -1, -5),

            new BlockPos(4, -1, 4),
            new BlockPos(4, -1, -4),

            new BlockPos(-4, -1, 4),
            new BlockPos(-4, -1, -4),

            new BlockPos(5, -1, 3),
            new BlockPos(5, -1, -3),

            new BlockPos(-5, -1, 3),
            new BlockPos(-5, -1, -3),

            new BlockPos(6, -1, 0),
            new BlockPos(6, -1, 1),
            new BlockPos(6, -1, -1),
            new BlockPos(6, -1, 2),
            new BlockPos(6, -1, -2),

            new BlockPos(-6, -1, 0),
            new BlockPos(-6, -1, 1),
            new BlockPos(-6, -1, -1),
            new BlockPos(-6, -1, 2),
            new BlockPos(-6, -1, -2)
    );
    private static final List<BlockPos> ABOVE_SOUL_FIRE_LOCATIONS = ImmutableList.of(
            new BlockPos(0, 0, 7),
            new BlockPos(0, 0, -7),

            new BlockPos(1, 0, 7),
            new BlockPos(1, 0, -7),

            new BlockPos(-1, 0, 7),
            new BlockPos(-1, 0, -7),

            new BlockPos(2, 0, 7),
            new BlockPos(2, 0, -7),

            new BlockPos(-2, 0, 7),
            new BlockPos(-2, 0, -7),

            new BlockPos(3, 0, 6),
            new BlockPos(3, 0, -6),

            new BlockPos(-3, 0, 6),
            new BlockPos(-3, 0, -6),

            new BlockPos(4, 0, 5),
            new BlockPos(4, 0, -5),

            new BlockPos(-4, 0, 5),
            new BlockPos(-4, 0, -5),

            new BlockPos(5, 0, 4),
            new BlockPos(5, 0, -4),

            new BlockPos(-5, 0, 4),
            new BlockPos(-5, 0, -4),

            new BlockPos(6, 0, 3),
            new BlockPos(6, 0, -3),

            new BlockPos(-6, 0, 3),
            new BlockPos(-6, 0, -3),

            new BlockPos(7, 0, 0),
            new BlockPos(7, 0, 1),
            new BlockPos(7, 0, -1),
            new BlockPos(7, 0, 2),
            new BlockPos(7, 0, -2),

            new BlockPos(-7, 0, 0),
            new BlockPos(-7, 0, 1),
            new BlockPos(-7, 0, -1),
            new BlockPos(-7, 0, 2),
            new BlockPos(-7, 0, -2)
    );
    private static final List<BlockPos> SOUL_FIRE_LOCATIONS = Stream.of(BOTTOM_SOUL_FIRE_LOCATIONS, ABOVE_SOUL_FIRE_LOCATIONS).flatMap(Collection::stream).toList();
    private static final List<BlockPos> SKULL_PILE_LOCATIONS = ImmutableList.of(
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),

            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),

            new BlockPos(1, 0, 1),
            new BlockPos(1, 0, -1),
            new BlockPos(-1, 0, 1),
            new BlockPos(-1, 0, -1)
    );
    private static final List<BlockPos> SOUL_SAND_LOCATIONS = ImmutableList.of(
            new BlockPos(0, 0, 3),
            new BlockPos(0, 0, -3),
            new BlockPos(3, 0, 0),
            new BlockPos(-3, 0, 0),

            new BlockPos(1, 0, 2),
            new BlockPos(1, 0, -2),
            new BlockPos(-1, 0, 2),
            new BlockPos(-1, 0, -2),

            new BlockPos(2, 0, 1),
            new BlockPos(2, 0, -1),
            new BlockPos(-2, 0, 1),
            new BlockPos(-2, 0, -1)
    );
    private static final List<BlockPos> BONES_LOCATIONS = ImmutableList.of(
            new BlockPos(0, 0, 2),
            new BlockPos(0, 0, -2),
            new BlockPos(2, 0, 0),
            new BlockPos(-2, 0, 0)
    );
    private static final List<BlockPos> GRAVE_SOIL_LOCATIONS = ImmutableList.of(
            new BlockPos(0, 0, 4),
            new BlockPos(0, 0, -4),
            new BlockPos(4, 0, 0),
            new BlockPos(-4, 0, 0),

            new BlockPos(1, 0, 4),
            new BlockPos(1, 0, -4),
            new BlockPos(4, 0, 1),
            new BlockPos(-4, 0, 1),

            new BlockPos(-1, 0, 4),
            new BlockPos(-1, 0, -4),
            new BlockPos(4, 0, -1),
            new BlockPos(-4, 0, -1),

            new BlockPos(1, 0, 3),
            new BlockPos(1, 0, -3),
            new BlockPos(3, 0, 1),
            new BlockPos(-3, 0, 1),

            new BlockPos(-1, 0, 3),
            new BlockPos(-1, 0, -3),
            new BlockPos(3, 0, -1),
            new BlockPos(-3, 0, -1),

            new BlockPos(2, 0, 2),
            new BlockPos(2, 0, -2),
            new BlockPos(-2, 0, 2),
            new BlockPos(-2, 0, -2),

            new BlockPos(2, 0, 3),
            new BlockPos(2, 0, -3),
            new BlockPos(3, 0, 2),
            new BlockPos(-3, 0, 2),

            new BlockPos(-2, 0, 3),
            new BlockPos(-2, 0, -3),
            new BlockPos(3, 0, -2),
            new BlockPos(-3, 0, -2)
    );
    private static final List<BlockPos> SHADE_STONE_LOCATIONS = ImmutableList.of(
            new BlockPos(0, 0, 5),
            new BlockPos(0, 0, -5),
            new BlockPos(5, 0, 0),
            new BlockPos(-5, 0, 0),

            new BlockPos(1, 0, 5),
            new BlockPos(1, 0, -5),
            new BlockPos(5, 0, 1),
            new BlockPos(-5, 0, 1),

            new BlockPos(-1, 0, 5),
            new BlockPos(-1, 0, -5),
            new BlockPos(5, 0, -1),
            new BlockPos(-5, 0, -1),

            new BlockPos(2, 0, 5),
            new BlockPos(2, 0, -5),
            new BlockPos(5, 0, 2),
            new BlockPos(-5, 0, 2),

            new BlockPos(-2, 0, 5),
            new BlockPos(-2, 0, -5),
            new BlockPos(5, 0, -2),
            new BlockPos(-5, 0, -2),

            new BlockPos(2, 0, 4),
            new BlockPos(2, 0, -4),
            new BlockPos(4, 0, 2),
            new BlockPos(-4, 0, 2),

            new BlockPos(-2, 0, 4),
            new BlockPos(-2, 0, -4),
            new BlockPos(4, 0, -2),
            new BlockPos(-4, 0, -2),

            new BlockPos(3, 0, 4),
            new BlockPos(3, 0, -4),
            new BlockPos(4, 0, 3),
            new BlockPos(-4, 0, 3),

            new BlockPos(-3, 0, 4),
            new BlockPos(-3, 0, -4),
            new BlockPos(4, 0, -3),
            new BlockPos(-4, 0, -3),

            new BlockPos(3, 0, 3),
            new BlockPos(3, 0, -3),
            new BlockPos(-3, 0, 3),
            new BlockPos(-3, 0, -3)
    );
    private static final BlockPos CORE = new BlockPos(0, 0, 0);


    private static List<BlockPos> checkStones(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : STONE_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            BlockState blockState = level.getBlockState(blockPos2);
            if (!blockState.is(ModTags.Blocks.CRYPT_STONE)){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkMold(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : MOLD_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(ModBlocks.DARK_MOLD_BLOCK.get())){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkSoulFire(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : SOUL_FIRE_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(Blocks.SOUL_FIRE)){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkSkullPile(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : SKULL_PILE_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(ModBlocks.SKULL_PILE.get())){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

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
        for (BlockPos blockPos1 : BONES_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(Blocks.BONE_BLOCK)){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkGraveSoil(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : GRAVE_SOIL_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(ModBlocks.GRAVE_SOIL.get())){
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

    public static boolean checkBlocks(Level level, BlockPos blockPos){
        return checkStones(level, blockPos).isEmpty()
                && checkMold(level, blockPos).isEmpty()
                && checkSkullPile(level, blockPos).isEmpty()
                && checkSoulSand(level, blockPos).isEmpty()
                && checkBones(level, blockPos).isEmpty()
                && checkGraveSoil(level, blockPos).isEmpty()
                && checkShadeStone(level, blockPos).isEmpty();
    }

    @Override
    public boolean isStructureValid(Level level, BlockPos blockPos) {
        return checkBlocks(level, blockPos);
    }

    public boolean conditionsMet(Level worldIn, LivingEntity entityLiving) {
        int count = 0;
        int global = 0;
        if (worldIn instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof IMonstrosity monstrosity && monstrosity instanceof IOwned servant) {
                    if (servant.getTrueOwner() == entityLiving && entity.isAlive()) {
                        ++count;
                    }
                    ++global;
                }
            }
        }
        return count < SpellConfig.MonstrosityPlayerLimit.get() && global < SpellConfig.MonstrosityGlobalLimit.get();
    }

    @Override
    public boolean spawnServant(Player player, ItemStack stack, Level level, BlockPos blockPos){
        if (!level.isClientSide) {
            if (level.getBlockState(blockPos).is(ModBlocks.MONSTROUS_CORE.get())) {
                if (SEHelper.getSoulsAmount(player, MobsConfig.MonstrositySoulCost.get())) {
                    if (checkBlocks(level, blockPos)) {
                        if (SEHelper.hasResearch(player, ResearchList.WARRED) && SEHelper.hasResearch(player, ResearchList.TERMINUS)) {
                            if (conditionsMet(level, player)) {
                                GraveMonstrosity monstrosity = ModEntityType.GRAVE_MONSTROSITY.get().create(level);
                                if (monstrosity != null) {
                                    monstrosity.setTrueOwner(player);
                                    monstrosity.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(monstrosity.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                                    monstrosity.moveTo((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.05D, (double) blockPos.getZ() + 0.5D, 0.0F, 0.0F);
                                    if (level.addFreshEntity(monstrosity)) {
                                        if (level instanceof ServerLevel serverLevel) {
                                            serverLevel.sendParticles(ModParticleTypes.MAGIC_RUNES.get(), monstrosity.getX(), monstrosity.getY() + 1.0F, monstrosity.getZ(), 0, 0.24F, 0.0F, 1.0F, 1.0);
                                        }
                                        SEHelper.decreaseSoulsNoDiscount(player, MobsConfig.MonstrositySoulCost.get());
                                        removeBlocks(level, blockPos);
                                        stack.shrink(1);
                                        if (player instanceof ServerPlayer serverPlayer) {
                                            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, monstrosity);
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
                } else {
                    player.displayClientMessage(Component.translatable("info.goety.soul.cost.fail", MobsConfig.MonstrositySoulCost.get()), true);
                }
            }
        }
        return false;
    }

    public static void removeBlocks(Level level, BlockPos blockPos){
        if (!level.isClientSide) {
            for (BlockPos blockPos1 : SKULL_PILE_LOCATIONS) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(ModBlocks.SKULL_PILE.get())) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
            for (BlockPos blockPos1 : SOUL_SAND_LOCATIONS) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
            for (BlockPos blockPos1 : BONES_LOCATIONS) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(Blocks.BONE_BLOCK)) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
            for (BlockPos blockPos1 : GRAVE_SOIL_LOCATIONS) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(ModBlocks.GRAVE_SOIL.get())) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
            for (BlockPos blockPos1 : SHADE_STONE_LOCATIONS) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(ModTags.Blocks.SHADE_STONE)) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
            BlockPos blockPos2 = blockPos.offset(CORE);
            if (level.getBlockState(blockPos2).is(ModBlocks.MONSTROUS_CORE.get())) {
                level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
            }
        }
    }
}
