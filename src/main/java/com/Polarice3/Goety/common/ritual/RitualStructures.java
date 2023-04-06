package com.Polarice3.Goety.common.ritual;

import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RitualStructures {

    public static final int RANGE = 8;

    public static boolean getProperStructure(String craftType, RitualBlockEntity pTileEntity, BlockPos pPos, Level pLevel){
        findStructures(craftType, pTileEntity, pPos, pLevel);
        return switch (craftType) {
            case "animation", "necroturgy", "lich", "forge", "magic", "sabbath" -> RitualStructures.checkRequirements(craftType, pTileEntity);
            case "adept_nether" -> pLevel.dimensionType().ultraWarm();
            case "expert_nether" -> RitualStructures.checkRequirements(craftType, pTileEntity) && pLevel.dimensionType().ultraWarm();
            case "air" -> pPos.getY() >= 128;
            case "storm" -> RitualStructures.checkRequirements(craftType, pTileEntity) && pPos.getY() >= 128 && pLevel.isThundering() && pLevel.canSeeSky(pPos);
            default -> false;
        };
    }

    public static void findStructures(String craftType, RitualBlockEntity pTileEntity, BlockPos pPos, Level pLevel) {
        pTileEntity.first.clear();
        pTileEntity.second.clear();
        pTileEntity.third.clear();
        for (int i = -RANGE; i <= RANGE; ++i) {
            for (int j = -RANGE; j <= RANGE; ++j) {
                for (int k = -RANGE; k <= RANGE; ++k) {
                    BlockPos blockpos1 = pPos.offset(i, j, k);
                    assert pLevel != null;
                    BlockState blockstate = pLevel.getBlockState(blockpos1);
                    getBlocks(craftType, pTileEntity, blockstate, blockpos1, pLevel);
                }
            }
        }
    }

    public static void getBlocks(String craftType, RitualBlockEntity pTileEntity, BlockState pState, BlockPos pPos, Level pLevel){
        switch (craftType){
            case "animation":
                if (pState.getBlock() == Blocks.LADDER) {
                    pTileEntity.first.add(pPos);
                }
                if (pState.getBlock() == Blocks.RAIL) {
                    pTileEntity.second.add(pPos);
                }
                if (pState.getBlock() == Blocks.CARVED_PUMPKIN) {
                    pTileEntity.third.add(pPos);
                }
            case "necroturgy":
            case "lich":
                if (pState.getBlock() instanceof SculkBlock) {
                    pTileEntity.first.add(pPos);
                }
                if (pState.getBlock() instanceof SlabBlock) {
                    pTileEntity.second.add(pPos);
                }
                if (pState.getBlock() instanceof FlowerPotBlock) {
                    if (((FlowerPotBlock) pState.getBlock()).getContent() != Blocks.AIR){
                        pTileEntity.third.add(pPos);
                    }
                }
            case "forge":
                if (pState.getBlock() == Blocks.SMITHING_TABLE) {
                    pTileEntity.first.add(pPos);
                }
                if (pState.getBlock() == Blocks.FURNACE || pState.getBlock() == Blocks.BLAST_FURNACE) {
                    pTileEntity.second.add(pPos);
                }
                if (pState.getBlock() == Blocks.ANVIL || pState.getBlock() == Blocks.CHIPPED_ANVIL || pState.getBlock() == Blocks.DAMAGED_ANVIL) {
                    pTileEntity.third.add(pPos);
                }
            case "magic":
                if (pState.getBlock() == Blocks.BOOKSHELF) {
                    pTileEntity.first.add(pPos);
                }
                if (pState.getBlock() instanceof LecternBlock) {
                    if (pState.hasBlockEntity() && pLevel.getBlockEntity(pPos) instanceof LecternBlockEntity lecternTileEntity){
                        if (lecternTileEntity.hasBook()){
                            pTileEntity.second.add(pPos);
                        }
                    }
                }
                if (pState.getBlock() instanceof EnchantmentTableBlock) {
                    pTileEntity.third.add(pPos);
                }
            case "sabbath":
                if (pState.getBlock() == Blocks.CRYING_OBSIDIAN) {
                    pTileEntity.first.add(pPos);
                }
                if (pState.getBlock() == Blocks.OBSIDIAN) {
                    pTileEntity.second.add(pPos);
                }
                if (pState.getBlock() == Blocks.SOUL_FIRE) {
                    pTileEntity.third.add(pPos);
                }
            case "adept_nether":
                if (pState.getBlock() == Blocks.BASALT
                        || pState.getBlock() == Blocks.POLISHED_BASALT
                        || pState.getBlock() == Blocks.SMOOTH_BASALT) {
                    pTileEntity.first.add(pPos);
                }
                if (pState.getBlock() == Blocks.BLACKSTONE
                        || pState.getBlock() == Blocks.POLISHED_BLACKSTONE
                        || pState.getBlock() == Blocks.POLISHED_BLACKSTONE_BRICKS
                        || pState.getBlock() == Blocks.CHISELED_POLISHED_BLACKSTONE) {
                    pTileEntity.second.add(pPos);
                }
                if (pState.getBlock() == Blocks.GLOWSTONE) {
                    pTileEntity.third.add(pPos);
                }
            case "expert_nether":
                if (pState.getBlock() == Blocks.WITHER_SKELETON_SKULL || pState.getBlock() == Blocks.WITHER_SKELETON_WALL_SKULL) {
                    pTileEntity.first.add(pPos);
                }
                if (pState.getBlock() == Blocks.NETHER_BRICKS || pState.getBlock() == Blocks.RED_NETHER_BRICKS) {
                    pTileEntity.second.add(pPos);
                }
                if (pState.getBlock() == Blocks.NETHER_WART) {
                    pTileEntity.third.add(pPos);
                }
            case "storm":
                if (pState.getBlock() == Blocks.COPPER_BLOCK) {
                    pTileEntity.first.add(pPos);
                }
                if (pState.getBlock() == Blocks.LIGHTNING_ROD) {
                    pTileEntity.second.add(pPos);
                }
                if (pState.getBlock() == Blocks.CHAIN) {
                    pTileEntity.third.add(pPos);
                }
        }
    }

    public static boolean checkRequirements(String craftType, RitualBlockEntity pTileEntity){
        int first = 0;
        int second = 0;
        int third = 0;
        switch (craftType){
            case "animation":
                first = 15;
                second = 15;
                third = 1;
                break;
            case "necroturgy":
            case "lich":
                first = 16;
                second = 16;
                third = 8;
                break;
            case "forge":
                first = 1;
                second = 3;
                third = 4;
                break;
            case "magic":
                first = 16;
                second = 1;
                third = 2;
                break;
            case "sabbath", "adept_nether":
                first = 8;
                second = 16;
                third = 4;
                break;
            case "expert_nether":
                first = 4;
                second = 32;
                third = 8;
                break;
            case "storm":
                first = 12;
                second = 4;
                third = 20;
        }
        return pTileEntity.first.size() >= first && pTileEntity.second.size() >= second && pTileEntity.third.size() >= third;
    }
}
