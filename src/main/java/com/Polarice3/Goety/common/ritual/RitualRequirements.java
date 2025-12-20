package com.Polarice3.Goety.common.ritual;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.ritual.IRitualType;
import com.Polarice3.Goety.api.ritual.RitualType;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class RitualRequirements extends RitualTypes {

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
            if (summonType != null) {
                Entity summon = summonType.create(level);
                if (summon instanceof IOwned owned) {
                    int count = 0;
                    for (ServerLevel serverLevel1 : serverLevel.getServer().getAllLevels()) {
                        for (Entity entity : serverLevel1.getAllEntities()) {
                            if (entity instanceof IOwned servant && owned.summonPredicate().test(entity)) {
                                if (servant.getTrueOwner() == castingPlayer) {
                                    ++count;
                                }
                            }
                        }
                    }
                    if (count >= owned.getSummonLimit(castingPlayer)) {
                        castingPlayer.displayClientMessage(Component.translatable("info.goety.summon.limit"), true);
                        return false;
                    } else {
                        return true;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean getProperStructure(String craftType, RitualBlockEntity pTileEntity, BlockPos pPos, Level pLevel) {
        return getProperStructure(craftType, null, pTileEntity, pPos, pLevel);
    }

    public static boolean getProperStructure(String craftType, @Nullable Player player, RitualBlockEntity pTileEntity, BlockPos pPos, Level pLevel){
        IRitualType ritualType = RitualType.getRitualType(craftType);
        if (ritualType != null) {
            return ritualType.getRequirement(pTileEntity, player, pPos, pLevel);
        }
        return false;
    }

    public static boolean geoturgyRitual(@Nullable Player pPlayer, BlockPos pPos, Level pLevel){
        return (!pLevel.canSeeSky(pPos) && pPos.getY() <= 32) || getStructures(GEOTURGY, pPlayer, pPos, pLevel);
    }

    public static boolean frostRitual(@Nullable Player pPlayer, BlockPos pPos, Level pLevel){
        return pLevel.getBiome(pPos).get().coldEnoughToSnow(pPos) || getStructures(FROST, pPlayer, pPos, pLevel);
    }

    public static boolean skyRitual(@Nullable Player pPlayer, RitualBlockEntity pTileEntity, Level pLevel, BlockPos pPos){
        return pPos.getY() >= 128 || pLevel.dimension().location().toString().contains("aether") || getStructures(SKY, pPlayer, pPos, pTileEntity.getLevel());
    }

    public static boolean deepRitual(@Nullable Player pPlayer, RitualBlockEntity pTileEntity, Level pLevel, BlockPos pPos){
        if (!(pTileEntity.getBlockState().hasProperty(BlockStateProperties.WATERLOGGED) && pTileEntity.getBlockState().getValue(BlockStateProperties.WATERLOGGED))) {
            if (pPlayer != null) {
                pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.waterlogged"), true);
            }
            return false;
        }
        return ((pPos.getY() <= pLevel.getSeaLevel() && pLevel.getBiome(pPos).is(BiomeTags.IS_DEEP_OCEAN)) || getStructures(DEEP, pPlayer, pPos, pTileEntity.getLevel()));
    }

    public static boolean overgrownRitual(@Nullable Player pPlayer, BlockPos pPos, Level pLevel){
        return (pLevel.getBiome(pPos).is(BiomeTags.IS_JUNGLE)) || getStructures(OVERGROWN, pPlayer, pPos, pLevel);
    }

    @Deprecated
    public static boolean getStructures(String craftType, BlockPos pPos, Level pLevel) {
        return getStructures(craftType, null, pPos, pLevel);
    }

    public static boolean getStructures(String craftType, @Nullable Player pPlayer, BlockPos pPos, Level pLevel){
        RitualChecker finder = new RitualChecker(pLevel, pPos, blockState -> true, RANGE, 0);
        switch (craftType) {
            case ANIMATION -> {
                Predicate<BlockState> first = blockState -> blockState.getBlock() instanceof LadderBlock || blockState.getBlock().getDescriptionId().contains("ladder");
                Predicate<BlockState> second = blockState -> blockState.getBlock() instanceof BaseRailBlock;
                Predicate<BlockState> third = blockState -> blockState.getBlock() instanceof CarvedPumpkinBlock;
                if (!finder.hasBlocks(first, 15)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.LADDER.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(second, 15)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.RAIL.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(third, 1)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.CARVED_PUMPKIN.getName()), true);
                    }
                    return false;
                }
            }
            case NECROTURGY -> {
                Predicate<BlockState> first = blockState -> blockState.getBlock() instanceof SculkBlock;
                Predicate<BlockState> second = blockState -> blockState.getBlock() instanceof SlabBlock;
                Predicate<BlockState> third = blockState -> blockState.getBlock() instanceof FlowerPotBlock flowerPotBlock && flowerPotBlock.getContent() != Blocks.AIR;
                if (!finder.hasBlocks(first, 16)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.SCULK.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(second, 16)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noSlabs"), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(third, 8)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noFill", Blocks.FLOWER_POT.getName()), true);
                    }
                    return false;
                }
            }
            case FORGE -> {
                Predicate<BlockState> first = blockState -> blockState.getBlock() instanceof LavaCauldronBlock;
                Predicate<BlockState> second = blockState -> blockState.getBlock() instanceof FurnaceBlock || blockState.getBlock() instanceof BlastFurnaceBlock;
                Predicate<BlockState> third = blockState -> blockState.getBlock().getDescriptionId().contains("anvil");
                if (!finder.hasBlocks(first, 1)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.LAVA_CAULDRON.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(second, 2)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.FURNACE.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(third, 1)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.ANVIL.getName()), true);
                    }
                    return false;
                }
            }
            case GEOTURGY -> {
                Predicate<BlockState> first = blockState -> blockState.getBlock() instanceof AmethystBlock;
                Predicate<BlockState> second = blockState -> blockState.getBlock() instanceof SmithingTableBlock;
                Predicate<BlockState> third = blockState -> blockState.getBlock().getDescriptionId().contains("deepslate");
                if (!finder.hasBlocks(first, 8)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.AMETHYST_BLOCK.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(second, 1)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.SMITHING_TABLE.getName()), true);
                    }
                    return false;
                }
                RitualChecker finder1 = new RitualChecker(pLevel, pPos, third, RANGE, 16) {
                    @Override
                    public boolean isCorrectBlock(BlockState blockState, BlockPos blockPos) {
                        return super.isCorrectBlock(blockState, blockPos) && blockState.isSolidRender(this.level, blockPos);
                    }
                };
                if (!finder1.checkBlocks()) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noFullBlocks", Blocks.DEEPSLATE.getName()), true);
                    }
                    return false;
                }
            }
            case MAGIC -> {
                Predicate<BlockState> second = blockState -> blockState.getBlock() instanceof LecternBlock;
                Predicate<BlockState> third = blockState -> blockState.getBlock() instanceof EnchantmentTableBlock;
                if (!BlockFinder.getNearbyEnchantPower(pLevel, pPos, RANGE, 16)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noEnchant"), true);
                    }
                    return false;
                }
                RitualChecker finder1 = new RitualChecker(pLevel, pPos, second, RANGE, 1) {
                    @Override
                    public boolean isCorrectBlock(BlockState blockState, BlockPos blockPos) {
                        return super.isCorrectBlock(blockState, blockPos) && blockState.hasBlockEntity() && pLevel.getBlockEntity(blockPos) instanceof LecternBlockEntity lecternTileEntity && !lecternTileEntity.getBook().isEmpty();
                    }
                };
                if (!finder1.checkBlocks()) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noFill", Blocks.LECTERN.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(third, 1)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.ENCHANTING_TABLE.getName()), true);
                    }
                    return false;
                }
            }
            case SABBATH -> {
                Predicate<BlockState> first = blockState -> blockState.is(Blocks.CRYING_OBSIDIAN);
                Predicate<BlockState> second = blockState -> blockState.is(Blocks.OBSIDIAN);
                Predicate<BlockState> third = blockState -> blockState.is(Blocks.SOUL_FIRE);
                if (!finder.hasBlocks(first, 8)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.CRYING_OBSIDIAN.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(second, 16)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.OBSIDIAN.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(third, 4)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.SOUL_FIRE.getName()), true);
                    }
                    return false;
                }
            }
            case ADEPT_NETHER -> {
                Predicate<BlockState> first = blockState -> blockState.getBlock().getDescriptionId().contains("basalt");
                Predicate<BlockState> second = blockState -> blockState.getBlock().getDescriptionId().contains("blackstone");
                Predicate<BlockState> third = blockState -> blockState.is(Blocks.GLOWSTONE);
                if (!finder.hasBlocks(first, 8)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.BASALT.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(second, 16)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.BLACKSTONE.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(third, 4)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.GLOWSTONE.getName()), true);
                    }
                    return false;
                }
            }
            case EXPERT_NETHER -> {
                Predicate<BlockState> first = blockState -> blockState.is(Blocks.WITHER_SKELETON_SKULL) || blockState.is(Blocks.WITHER_SKELETON_WALL_SKULL);
                Predicate<BlockState> second = blockState -> blockState.is(Blocks.NETHER_BRICKS) || blockState.is(Blocks.RED_NETHER_BRICKS);
                Predicate<BlockState> third = blockState -> blockState.is(Blocks.NETHER_WART);
                if (!finder.hasBlocks(first, 4)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.WITHER_SKELETON_SKULL.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(second, 32)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.NETHER_BRICKS.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(third, 8)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.NETHER_WART.getName()), true);
                    }
                    return false;
                }
            }
            case END -> {
                Predicate<BlockState> first = blockState -> blockState.is(ModBlocks.VOID_BLOCK.get());
                Predicate<BlockState> second = blockState -> blockState.is(ModTags.Blocks.END_STONE) || blockState.is(Blocks.END_STONE_BRICKS);
                Predicate<BlockState> third = blockState -> blockState.getBlock().getDescriptionId().contains("purpur");
                if (!finder.hasBlocks(first, 16)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", ModBlocks.VOID_BLOCK.get().getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(second, 64)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.END_STONE.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(third, 32)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.PURPUR_BLOCK.getName()), true);
                    }
                    return false;
                }
            }
            case FROST -> {
                Predicate<BlockState> first = blockState -> blockState.is(BlockTags.ICE);
                Predicate<BlockState> second = blockState -> blockState.is(BlockTags.SNOW);
                Predicate<BlockState> third = blockState -> blockState.is(ModBlocks.FREEZING_LAMP.get());
                if (!finder.hasBlocks(first, 16)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.ICE.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(second, 8)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.SNOW_BLOCK.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(third, 4)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", ModBlocks.FREEZING_LAMP.get().getName()), true);
                    }
                    return false;
                }
            }
            case SKY ->{
                Predicate<BlockState> first = blockState -> blockState.is(ModTags.Blocks.MARBLE_BLOCKS);
                Predicate<BlockState> second = blockState -> blockState.is(ModTags.Blocks.JADE_BLOCKS);
                Predicate<BlockState> third = blockState -> blockState.is(ModTags.Blocks.INDENTED_GOLD_BLOCKS);
                if (!finder.hasBlocks(first, 8)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", ModBlocks.MARBLE_BLOCK.get().getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(second, 16)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", ModBlocks.JADE_TILES.get().getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(third, 4)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", ModBlocks.INDENTED_GOLD_BLOCK.get().getName()), true);
                    }
                    return false;
                }
            }
            case STORM -> {
                Predicate<BlockState> first = blockState -> blockState.getBlock().getDescriptionId().contains("copper");
                Predicate<BlockState> second = blockState -> blockState.getBlock() instanceof LightningRodBlock;
                Predicate<BlockState> third = blockState -> blockState.getBlock() instanceof ChainBlock;
                if (!finder.hasBlocks(first, 12)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.COPPER_BLOCK.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(second, 4)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.LIGHTNING_ROD.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(third, 20)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.CHAIN.getName()), true);
                    }
                    return false;
                }
            }
            case DEEP ->{
                Predicate<BlockState> first = blockState -> blockState.is(Blocks.SEA_LANTERN);
                Predicate<BlockState> second = blockState -> blockState.getBlock().getDescriptionId().contains("prismarine");
                Predicate<BlockState> third = blockState -> blockState.getBlock().getDescriptionId().contains("granite");
                if (!finder.hasBlocks(first, 4)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.SEA_LANTERN.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(second, 16)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.PRISMARINE.getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(third, 16)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.GRANITE.getName()), true);
                    }
                    return false;
                }
            }
            case OVERGROWN ->{
                Predicate<BlockState> first = blockState -> blockState.is(BlockTags.LEAVES) || blockState.getBlock().getDescriptionId().contains("leaves");
                Predicate<BlockState> second = blockState -> blockState.is(ModBlocks.OVERGROWN_ROOTS.get());
                Predicate<BlockState> third = blockState -> blockState.getBlock().getDescriptionId().contains("moss");
                if (!finder.hasBlocks(first, 32)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noLeaves"), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(second, 16)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", ModBlocks.OVERGROWN_ROOTS.get().getName()), true);
                    }
                    return false;
                }
                if (!finder.hasBlocks(third, 16)) {
                    if (pPlayer != null) {
                        pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.noBlocks", Blocks.MOSS_BLOCK.getName()), true);
                    }
                    return false;
                }
            }
        }
        return true;
    }
}
