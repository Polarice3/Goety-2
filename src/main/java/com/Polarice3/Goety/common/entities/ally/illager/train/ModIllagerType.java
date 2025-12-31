package com.Polarice3.Goety.common.entities.ally.illager.train;

import com.Polarice3.Goety.api.entities.ally.illager.ITrainIllager;
import com.Polarice3.Goety.common.blocks.*;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.illager.Neollager;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.common.ritual.RitualChecker;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraftforge.common.Tags;

public class ModIllagerType implements ITrainIllager {

    @Override
    public boolean canSpawn(Level level, BlockPos blockPos, int range) {
        return this.getIllager(level, blockPos, range) != null;
    }

    @Override
    public boolean mobCanTrainTo(Mob mob, Level level, BlockPos blockPos, int range) {
        EntityType<?> entityType = this.getIllager(level, blockPos, range);
        if (entityType == ModEntityType.STORM_CASTER_SERVANT.get()) {
            return mob.getType() == ModEntityType.GEOMANCER_SERVANT.get() || mob.getType() == ModEntityType.WIND_CALLER_SERVANT.get();
        } else if (entityType == ModEntityType.CRYOLOGER_SERVANT.get()) {
            return mob.getType() == ModEntityType.ICEOLOGER_SERVANT.get();
        } else if (entityType == ModEntityType.VINDICATOR_CHEF_SERVANT.get() || entityType == ModEntityType.MOUNTAINEER_SERVANT.get()) {
            return mob.getType() == ModEntityType.VINDICATOR_SERVANT.get();
        } else if (entityType == ModEntityType.PIKER_SERVANT.get()) {
            return mob instanceof Neollager neollager && neollager.getTrueOwner() instanceof Player player && SEHelper.hasResearch(player, ResearchList.FRONT);
        } else {
            return mob instanceof Neollager;
        }
    }

    @Override
    public EntityType<? extends Mob> getIllager(Level level, BlockPos blockPos, int range) {
        RitualChecker checker = new RitualChecker(level, blockPos, blockState -> true, range, 0);
        if (checker.hasBlocks(blockState -> blockState.is(Blocks.HAY_BLOCK) || blockState.is(Blocks.TARGET), 2)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof CarvedPumpkinBlock, 2)
                && checker.hasBlocks(blockState -> blockState.is(Tags.Blocks.FENCES_WOODEN), 16)
                && checker.hasBlocks(blockState -> blockState.is(BlockTags.WOOL), 20)) {
            return ModEntityType.PILLAGER_SERVANT.get();
        } else if (checker.hasBlocks(blockState -> blockState.getBlock() instanceof GrindstoneBlock, 2)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof SmithingTableBlock, 2)
                && checker.hasBlocks(blockState -> blockState.is(Tags.Blocks.FENCES_WOODEN), 16)
                && checker.hasBlocks(blockState -> blockState.is(BlockTags.LOGS), 32)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof BedBlock, 4)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof BarrelBlock, 4)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof AnvilBlock, 2)) {
            return ModEntityType.PIKER_SERVANT.get();
        } else if (checker.hasBlocks(blockState -> blockState.getBlock() instanceof CampfireBlock, 2)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof BedBlock, 4)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof BarrelBlock, 4)) {
            return ModEntityType.SIGNALER_SERVANT.get();
        } else if (checker.hasBlocks(blockState -> blockState.is(BlockTags.BANNERS), 1)
                && checker.hasBlocks(blockState -> blockState.is(BlockTags.PLANKS), 64)
                && checker.hasBlocks(blockState -> blockState.getBlock().getDescriptionId().contains("bricks"), 60)
                && checker.hasBlocks(blockState -> blockState.is(BlockTags.WALLS), 10)
                && checker.hasBlocks(blockState -> blockState.is(BlockTags.FENCES), 10)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof FurnaceBlock || blockState.getBlock() instanceof BlastFurnaceBlock, 2)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof CraftingTableBlock, 1)){
            return ModEntityType.VINDICATOR_SERVANT.get();
        } else if (checker.hasBlocks(blockState -> blockState.getBlock() instanceof CakeBlock, 2)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof SmokerBlock || blockState.getBlock() instanceof FurnaceBlock, 4)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof BarrelBlock, 16)){
            return ModEntityType.VINDICATOR_CHEF_SERVANT.get();
        } else if (checker.hasBlocks(blockState -> blockState.getBlock() instanceof WindBlowerBlock && blockState.getValue(WindBlowerBlock.POWERED), 4)
                && checker.hasBlocks(blockState -> blockState.is(Tags.Blocks.FENCES_WOODEN), 16)
                && checker.hasBlocks(blockState -> blockState.is(BlockTags.WOOL), 20)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof BarrelBlock, 8)
                && level.getBiome(blockPos).get().coldEnoughToSnow(blockPos)){
            return ModEntityType.MOUNTAINEER_SERVANT.get();
        } else if (checker.hasBlocks(blockState -> blockState.is(BlockTags.PLANKS), 64)
                && checker.hasBlocks(blockState -> blockState.getBlock().getDescriptionId().contains("bricks"), 64)
                && checker.hasBlocks(blockState -> blockState.is(BlockTags.FENCES), 8)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof BlastFurnaceBlock, 8)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof LavaCauldronBlock, 2)
                && checker.hasBlocks(blockState -> blockState.is(Blocks.WATER_CAULDRON), 2)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof AnvilBlock, 4)){
            return ModEntityType.CRUSHER_SERVANT.get();
        } else if (BlockFinder.getNearbyEnchantPower(level, blockPos, range, 32)
                && BlockFinder.getNearbyLitCandles(level, blockPos, range, 16)
                && checker.hasBlocks(blockState -> blockState.is(Blocks.LECTERN), 1)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof StashUrnBlock, 4)
                && checker.hasBlocks(blockState -> blockState.is(BlockTags.BANNERS), 2)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof AbstractSkullBlock || blockState.getBlock() instanceof TallSkullBlock || blockState.getBlock() instanceof WallTallSkullBlock, 4)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof FlowerPotBlock flowerPotBlock && flowerPotBlock.getContent() != Blocks.AIR, 4)) {
            return ModEntityType.EVOKER_SERVANT.get();
        } else if (checker.hasBlocks(blockState -> blockState.is(Tags.Blocks.STORAGE_BLOCKS_AMETHYST), 16)
                && checker.hasBlocks(blockState -> blockState.getBlock().getDescriptionId().contains("deepslate"), 64)
                && checker.hasBlocks(blockState -> blockState.is(ModBlocks.CREEPER_TOTEM.get()), 16)) {
            return ModEntityType.GEOMANCER_SERVANT.get();
        } else if (checker.hasBlocks(blockState -> blockState.is(Blocks.BLUE_ICE), 16)
                && checker.hasBlocks(blockState -> blockState.is(BlockTags.SNOW), 64)
                && checker.hasBlocks(blockState -> blockState.is(ModBlocks.FREEZING_LAMP.get()), 4)) {
            return ModEntityType.ICEOLOGER_SERVANT.get();
        } else if (checker.hasBlocks(blockState -> blockState.is(ModTags.Blocks.MARBLE_BLOCKS), 64)
                && checker.hasBlocks(blockState -> blockState.is(ModTags.Blocks.JADE_BLOCKS), 32)
                && checker.hasBlocks(blockState -> blockState.is(ModTags.Blocks.INDENTED_GOLD_BLOCKS), 4)) {
            return ModEntityType.WIND_CALLER_SERVANT.get();
        } else if (BlockFinder.getNearbyEnchantPower(level, blockPos, range, 16)
                && checker.hasBlocks(blockState -> blockState.is(Blocks.POWDER_SNOW), 32)
                && checker.hasBlocks(blockState -> blockState.is(ModBlocks.CREEPER_TOTEM.get()), 16)
                && level.getBiome(blockPos).get().coldEnoughToSnow(blockPos)) {
            return ModEntityType.CRYOLOGER_SERVANT.get();
        } else if (checker.hasBlocks(blockState -> blockState.getBlock().getDescriptionId().contains("copper"), 32)
                && checker.hasBlocks(blockState -> blockState.getBlock().getDescriptionId().contains("bricks"), 64)
                && checker.hasBlocks(blockState -> blockState.getBlock() instanceof LightningRodBlock, 4)
                && level.isRainingAt(blockPos.above())
                && level.isThundering()) {
            return ModEntityType.STORM_CASTER_SERVANT.get();
        }
        return null;
    }
}
