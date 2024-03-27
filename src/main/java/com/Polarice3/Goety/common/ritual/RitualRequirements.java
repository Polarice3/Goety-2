package com.Polarice3.Goety.common.ritual;

import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import com.Polarice3.Goety.init.ModTags;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.Tags;

import java.util.List;

public class RitualRequirements {

    public static final int RANGE = Ritual.RANGE;

    public static boolean noConvertEntity(TagKey<EntityType<?>> entityType, BlockPos pPos, Level pLevel){
        return getConvertEntity(entityType, pPos, pLevel) == null;
    }

    public static Mob getConvertEntity(TagKey<EntityType<?>> entityType, BlockPos pPos, Level pLevel){
        for (Mob mob : pLevel.getEntitiesOfClass(Mob.class, new AABB(pPos).inflate(RANGE))){
            if (mob.getType().is(entityType)){
                return mob;
            }
        }
        return null;
    }

    public static boolean getProperStructure(String craftType, RitualBlockEntity pTileEntity, BlockPos pPos, Level pLevel){
        return switch (craftType) {
            case "animation", "forge", "magic", "sabbath" -> RitualRequirements.getStructures(craftType, pPos, pLevel);
            case "geoturgy" -> RitualRequirements.getStructures(craftType, pPos, pLevel) && !pLevel.canSeeSky(pPos) && pPos.getY() <= 32;
            case "necroturgy", "lich" -> RitualRequirements.getStructures(craftType, pPos, pLevel) && pLevel.isNight();
            case "adept_nether", "expert_nether" -> RitualRequirements.getStructures(craftType, pPos, pLevel) && pLevel.dimensionType().ultraWarm();
            case "frost" -> RitualRequirements.getStructures(craftType, pPos, pLevel) && pLevel.getBiome(pPos).get().shouldSnow(pLevel, pPos);
            case "sky" -> skyRitual(pTileEntity, pPos);
            case "storm" -> RitualRequirements.getStructures(craftType, pPos, pLevel) && skyRitual(pTileEntity, pPos) && pLevel.isThundering() && pLevel.canSeeSky(pPos);
            default -> false;
        };
    }

    public static boolean skyRitual(RitualBlockEntity pTileEntity, BlockPos pPos){
        return pPos.getY() >= 128 || getStructures("sky", pPos, pTileEntity.getLevel());
    }

    public static boolean getStructures(String craftType, BlockPos pPos, Level pLevel){
        List<BlockPos> first = Lists.newArrayList();
        List<BlockPos> second = Lists.newArrayList();
        List<BlockPos> third = Lists.newArrayList();
        int firstSize = 0;
        int secondSize = 0;
        int thirdSize = 0;
        for (int i = -RANGE; i <= RANGE; ++i) {
            for (int j = -RANGE; j <= RANGE; ++j) {
                for (int k = -RANGE; k <= RANGE; ++k) {
                    BlockPos blockpos1 = pPos.offset(i, j, k);
                    BlockState blockstate = pLevel.getBlockState(blockpos1);
                    switch (craftType) {
                        case "animation" -> {
                            firstSize = 15;
                            secondSize = 15;
                            thirdSize = 1;
                            if (blockstate.getBlock() instanceof LadderBlock || blockstate.getBlock().getDescriptionId().contains("ladder")) {
                                first.add(blockpos1);
                            }
                            if (blockstate.getBlock() instanceof RailBlock) {
                                second.add(blockpos1);
                            }
                            if (blockstate.getBlock() instanceof CarvedPumpkinBlock) {
                                third.add(blockpos1);
                            }
                        }
                        case "necroturgy", "lich" -> {
                            firstSize = 16;
                            secondSize = 16;
                            thirdSize = 8;
                            if (blockstate.getBlock() instanceof SculkBlock) {
                                first.add(blockpos1);
                            }
                            if (blockstate.getBlock() instanceof SlabBlock) {
                                second.add(blockpos1);
                            }
                            if (blockstate.getBlock() instanceof FlowerPotBlock flowerPotBlock) {
                                if (flowerPotBlock.getContent() != Blocks.AIR) {
                                    third.add(blockpos1);
                                }
                            }
                        }
                        case "forge" -> {
                            firstSize = 1;
                            secondSize = 2;
                            thirdSize = 1;
                            if (blockstate.getBlock() instanceof LavaCauldronBlock) {
                                first.add(blockpos1);
                            }
                            if (blockstate.getBlock() instanceof FurnaceBlock || blockstate.getBlock() instanceof BlastFurnaceBlock) {
                                second.add(blockpos1);
                            }
                            if (blockstate.getBlock() instanceof AnvilBlock) {
                                third.add(blockpos1);
                            }
                        }
                        case "geoturgy" -> {
                            firstSize = 8;
                            secondSize = 1;
                            thirdSize = 16;
                            if (blockstate.getBlock() instanceof AmethystBlock) {
                                first.add(blockpos1);
                            }
                            if (blockstate.getBlock() instanceof SmithingTableBlock) {
                                second.add(blockpos1);
                            }
                            if (blockstate.getBlock().getDescriptionId().contains("deepslate") && blockstate.isSolidRender(pLevel, blockpos1)) {
                                third.add(blockpos1);
                            }
                        }
                        case "magic" -> {
                            firstSize = 16;
                            secondSize = 1;
                            thirdSize = 1;
                            if (blockstate.is(Tags.Blocks.BOOKSHELVES) || blockstate.getBlock().getDescriptionId().contains("bookshelf")) {
                                first.add(blockpos1);
                            }
                            if (blockstate.getBlock() instanceof LecternBlock) {
                                if (blockstate.hasBlockEntity() && pLevel.getBlockEntity(blockpos1) instanceof LecternBlockEntity lecternTileEntity) {
                                    if (!lecternTileEntity.getBook().isEmpty()) {
                                        second.add(blockpos1);
                                    }
                                }
                            }
                            if (blockstate.getBlock() instanceof EnchantmentTableBlock) {
                                third.add(blockpos1);
                            }
                        }
                        case "sabbath" -> {
                            firstSize = 8;
                            secondSize = 16;
                            thirdSize = 4;
                            if (blockstate.getBlock() == Blocks.CRYING_OBSIDIAN) {
                                first.add(blockpos1);
                            }
                            if (blockstate.getBlock() == Blocks.OBSIDIAN) {
                                second.add(blockpos1);
                            }
                            if (blockstate.getBlock() == Blocks.SOUL_FIRE) {
                                third.add(blockpos1);
                            }
                        }
                        case "adept_nether" -> {
                            firstSize = 8;
                            secondSize = 16;
                            thirdSize = 4;
                            if (blockstate.getBlock().getDescriptionId().contains("basalt")) {
                                first.add(blockpos1);
                            }
                            if (blockstate.getBlock().getDescriptionId().contains("blackstone")) {
                                second.add(blockpos1);
                            }
                            if (blockstate.getBlock() == Blocks.GLOWSTONE) {
                                third.add(blockpos1);
                            }
                        }
                        case "expert_nether" -> {
                            firstSize = 4;
                            secondSize = 32;
                            thirdSize = 8;
                            if (blockstate.getBlock() == Blocks.WITHER_SKELETON_SKULL || blockstate.getBlock() == Blocks.WITHER_SKELETON_WALL_SKULL) {
                                first.add(blockpos1);
                            }
                            if (blockstate.getBlock() == Blocks.NETHER_BRICKS || blockstate.getBlock() == Blocks.RED_NETHER_BRICKS) {
                                second.add(blockpos1);
                            }
                            if (blockstate.getBlock() == Blocks.NETHER_WART) {
                                third.add(blockpos1);
                            }
                        }
                        case "frost" -> {
                            firstSize = 16;
                            secondSize = 8;
                            thirdSize = 4;
                            if (blockstate.is(BlockTags.STONE_BRICKS)) {
                                first.add(blockpos1);
                            }
                            if (blockstate.is(BlockTags.SNOW)) {
                                second.add(blockpos1);
                            }
                            if (blockstate.is(BlockTags.ICE)) {
                                third.add(blockpos1);
                            }
                        }
                        case "sky" ->{
                            firstSize = 8;
                            secondSize = 16;
                            thirdSize = 4;
                            if (blockstate.is(ModTags.Blocks.MARBLE_BLOCKS)) {
                                if (!first.contains(blockpos1)){
                                    first.add(blockpos1);
                                }
                            }
                            if (blockstate.is(ModTags.Blocks.JADE_BLOCKS)) {
                                if (!second.contains(blockpos1)){
                                    second.add(blockpos1);
                                }
                            }
                            if (blockstate.is(ModTags.Blocks.INDENTED_GOLD_BLOCKS)) {
                                if (!third.contains(blockpos1)){
                                    third.add(blockpos1);
                                }
                            }
                        }
                        case "storm" -> {
                            firstSize = 12;
                            secondSize = 4;
                            thirdSize = 20;
                            if (blockstate.getBlock().getDescriptionId().contains("copper")) {
                                first.add(blockpos1);
                            }
                            if (blockstate.getBlock() instanceof LightningRodBlock) {
                                second.add(blockpos1);
                            }
                            if (blockstate.getBlock() instanceof ChainBlock) {
                                third.add(blockpos1);
                            }
                        }
                    }
                }
            }
        }
        return first.size() >= firstSize
                && second.size() >= secondSize
                && third.size() >= thirdSize;
    }
}
