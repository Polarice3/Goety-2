package com.Polarice3.Goety.common.ritual;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import com.Polarice3.Goety.common.entities.ally.undead.AbstractBoundIllager;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieServant;
import com.Polarice3.Goety.common.entities.neutral.AbstractWraith;
import com.Polarice3.Goety.common.magic.spells.necromancy.SkeletonSpell;
import com.Polarice3.Goety.common.magic.spells.necromancy.WraithSpell;
import com.Polarice3.Goety.common.magic.spells.necromancy.ZombieSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

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

    public static boolean canSummon(Level level, Player castingPlayer, EntityType<?> summonType){
        if (level instanceof ServerLevel serverLevel){
            Entity summon = summonType.create(level);
            if (summon instanceof ZombieServant){
                return new ZombieSpell().conditionsMet(serverLevel, castingPlayer);
            }
            if (summon instanceof AbstractSkeletonServant){
                return new SkeletonSpell().conditionsMet(serverLevel, castingPlayer);
            }
            if (summon instanceof AbstractWraith){
                return new WraithSpell().conditionsMet(serverLevel, castingPlayer);
            }
            if (summon instanceof AbstractBoundIllager){
                int count = 0;
                for (Entity entity : serverLevel.getAllEntities()) {
                    if (entity instanceof AbstractBoundIllager servant) {
                        if (servant.getTrueOwner() == castingPlayer) {
                            ++count;
                        }
                    }
                }
                if (count >= SpellConfig.BoundIllagerLimit.get()){
                    castingPlayer.displayClientMessage(Component.translatable("info.goety.summon.limit"), true);
                    return false;
                } else {
                    return true;
                }
            }
        }
        return true;
    }

    public static boolean getProperStructure(String craftType, RitualBlockEntity pTileEntity, BlockPos pPos, Level pLevel){
        return switch (craftType) {
            case "animation", "forge", "magic", "sabbath" -> RitualRequirements.getStructures(craftType, pPos, pLevel);
            case "geoturgy" -> geoturgyRitual(pPos, pLevel);
            case "necroturgy", "lich" -> RitualRequirements.getStructures(craftType, pPos, pLevel) && pLevel.isNight();
            case "adept_nether", "expert_nether" -> RitualRequirements.getStructures(craftType, pPos, pLevel) && pLevel.dimensionType().ultraWarm();
            case "frost" -> frostRitual(pPos, pLevel);
            case "sky" -> skyRitual(pTileEntity, pPos);
            case "storm" -> RitualRequirements.getStructures(craftType, pPos, pLevel) && skyRitual(pTileEntity, pPos) && pLevel.isThundering() && pLevel.canSeeSky(pPos);
            default -> false;
        };
    }

    public static boolean geoturgyRitual(BlockPos pPos, Level pLevel){
        return (!pLevel.canSeeSky(pPos) && pPos.getY() <= 32) || getStructures("geoturgy", pPos, pLevel);
    }

    public static boolean frostRitual(BlockPos pPos, Level pLevel){
        return pLevel.getBiome(pPos).get().coldEnoughToSnow(pPos) || getStructures("frost", pPos, pLevel);
    }

    public static boolean skyRitual(RitualBlockEntity pTileEntity, BlockPos pPos){
        return pPos.getY() >= 128 || getStructures("sky", pPos, pTileEntity.getLevel());
    }

    public static boolean getStructures(String craftType, BlockPos pPos, Level pLevel){
        int firstCount = 0;
        int secondCount = 0;
        int thirdCount = 0;

        int totalFirst = 0;
        int totalSecond = 0;
        int totalThird = 0;
        for (int i = -RANGE; i <= RANGE; ++i) {
            for (int j = -RANGE; j <= RANGE; ++j) {
                for (int k = -RANGE; k <= RANGE; ++k) {
                    BlockPos blockpos1 = pPos.offset(i, j, k);
                    BlockState blockstate = pLevel.getBlockState(blockpos1);
                    switch (craftType) {
                        case "animation" -> {
                            totalFirst = 15;
                            totalSecond = 15;
                            totalThird = 1;
                            if (blockstate.getBlock() instanceof LadderBlock || blockstate.getBlock().getDescriptionId().contains("ladder")) {
                                ++firstCount;
                            }
                            if (blockstate.getBlock() instanceof RailBlock) {
                                ++secondCount;
                            }
                            if (blockstate.getBlock() instanceof CarvedPumpkinBlock) {
                                ++thirdCount;
                            }
                        }
                        case "necroturgy", "lich" -> {
                            totalFirst = 16;
                            totalSecond = 16;
                            totalThird = 8;
                            if (blockstate.getBlock() instanceof SculkBlock) {
                                ++firstCount;
                            }
                            if (blockstate.getBlock() instanceof SlabBlock) {
                                ++secondCount;
                            }
                            if (blockstate.getBlock() instanceof FlowerPotBlock flowerPotBlock) {
                                if (flowerPotBlock.getContent() != Blocks.AIR) {
                                    ++thirdCount;
                                }
                            }
                        }
                        case "forge" -> {
                            totalFirst = 1;
                            totalSecond = 2;
                            totalThird = 1;
                            if (blockstate.getBlock() instanceof LavaCauldronBlock) {
                                ++firstCount;
                            }
                            if (blockstate.getBlock() instanceof FurnaceBlock || blockstate.getBlock() instanceof BlastFurnaceBlock) {
                                ++secondCount;
                            }
                            if (blockstate.getBlock() instanceof AnvilBlock) {
                                ++thirdCount;
                            }
                        }
                        case "geoturgy" -> {
                            totalFirst = 8;
                            totalSecond = 1;
                            totalThird = 16;
                            if (blockstate.getBlock() instanceof AmethystBlock) {
                                ++firstCount;
                            }
                            if (blockstate.getBlock() instanceof SmithingTableBlock) {
                                ++secondCount;
                            }
                            if (blockstate.getBlock().getDescriptionId().contains("deepslate") && blockstate.isSolidRender(pLevel, blockpos1)) {
                                ++thirdCount;
                            }
                        }
                        case "magic" -> {
                            totalFirst = 16;
                            totalSecond = 1;
                            totalThird = 1;
                            if (blockstate.getEnchantPowerBonus(pLevel, blockpos1) > 0) {
                                firstCount += (int) blockstate.getEnchantPowerBonus(pLevel, blockpos1);
                            }
                            if (blockstate.getBlock() instanceof LecternBlock) {
                                if (blockstate.hasBlockEntity() && pLevel.getBlockEntity(blockpos1) instanceof LecternBlockEntity lecternTileEntity) {
                                    if (!lecternTileEntity.getBook().isEmpty()) {
                                        ++secondCount;
                                    }
                                }
                            }
                            if (blockstate.getBlock() instanceof EnchantmentTableBlock) {
                                ++thirdCount;
                            }
                        }
                        case "sabbath" -> {
                            totalFirst = 8;
                            totalSecond = 16;
                            totalThird = 4;
                            if (blockstate.getBlock() == Blocks.CRYING_OBSIDIAN) {
                                ++firstCount;
                            }
                            if (blockstate.getBlock() == Blocks.OBSIDIAN) {
                                ++secondCount;
                            }
                            if (blockstate.getBlock() == Blocks.SOUL_FIRE) {
                                ++thirdCount;
                            }
                        }
                        case "adept_nether" -> {
                            totalFirst = 8;
                            totalSecond = 16;
                            totalThird = 4;
                            if (blockstate.getBlock().getDescriptionId().contains("basalt")) {
                                ++firstCount;
                            }
                            if (blockstate.getBlock().getDescriptionId().contains("blackstone")) {
                                ++secondCount;
                            }
                            if (blockstate.getBlock() == Blocks.GLOWSTONE) {
                                ++thirdCount;
                            }
                        }
                        case "expert_nether" -> {
                            totalFirst = 4;
                            totalSecond = 32;
                            totalThird = 8;
                            if (blockstate.getBlock() == Blocks.WITHER_SKELETON_SKULL || blockstate.getBlock() == Blocks.WITHER_SKELETON_WALL_SKULL) {
                                ++firstCount;
                            }
                            if (blockstate.getBlock() == Blocks.NETHER_BRICKS || blockstate.getBlock() == Blocks.RED_NETHER_BRICKS) {
                                ++secondCount;
                            }
                            if (blockstate.getBlock() == Blocks.NETHER_WART) {
                                ++thirdCount;
                            }
                        }
                        case "frost" -> {
                            totalFirst = 16;
                            totalSecond = 8;
                            totalThird = 4;
                            if (blockstate.is(BlockTags.ICE)) {
                                ++firstCount;
                            }
                            if (blockstate.is(BlockTags.SNOW)) {
                                ++secondCount;
                            }
                            if (blockstate.is(ModBlocks.FREEZING_LAMP.get())) {
                                ++thirdCount;
                            }
                        }
                        case "sky" ->{
                            totalFirst = 8;
                            totalSecond = 16;
                            totalThird = 4;
                            if (blockstate.is(ModTags.Blocks.MARBLE_BLOCKS)) {
                                ++firstCount;
                            }
                            if (blockstate.is(ModTags.Blocks.JADE_BLOCKS)) {
                                ++secondCount;
                            }
                            if (blockstate.is(ModTags.Blocks.INDENTED_GOLD_BLOCKS)) {
                                ++thirdCount;
                            }
                        }
                        case "storm" -> {
                            totalFirst = 12;
                            totalSecond = 4;
                            totalThird = 20;
                            if (blockstate.getBlock().getDescriptionId().contains("copper")) {
                                ++firstCount;
                            }
                            if (blockstate.getBlock() instanceof LightningRodBlock) {
                                ++secondCount;
                            }
                            if (blockstate.getBlock() instanceof ChainBlock) {
                                ++thirdCount;
                            }
                        }
                    }
                }
            }
        }
        return firstCount >= totalFirst
                && secondCount >= totalSecond
                && thirdCount >= totalThird;
    }
}
