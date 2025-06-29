package com.Polarice3.Goety.common.entities.ally.illager.train;

import com.Polarice3.Goety.api.entities.ally.illager.ITrainIllager;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.illager.Neollager;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
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
        } else if (entityType == ModEntityType.VINDICATOR_CHEF_SERVANT.get()) {
            return mob.getType() == ModEntityType.VINDICATOR_SERVANT.get();
        } else {
            return mob instanceof Neollager;
        }
    }

    @Override
    public EntityType<? extends Mob> getIllager(Level level, BlockPos blockPos, int range) {
        if (BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(Blocks.HAY_BLOCK) || blockState.is(Blocks.TARGET), range, 2)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock() instanceof CarvedPumpkinBlock, range, 2)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(Tags.Blocks.FENCES_WOODEN), range, 16)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(BlockTags.WOOL), range, 20)) {
            return ModEntityType.PILLAGER_SERVANT.get();
        } else if (BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(BlockTags.BANNERS), range, 1)
                    && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(BlockTags.PLANKS), range, 64)
                    && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock().getDescriptionId().contains("bricks"), range, 60)
                    && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(BlockTags.WALLS), range, 10)
                    && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(BlockTags.FENCES), range, 10)
                    && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock() instanceof FurnaceBlock || blockState.getBlock() instanceof BlastFurnaceBlock, range, 2)
                    && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock() instanceof CraftingTableBlock, range, 1)){
            return ModEntityType.VINDICATOR_SERVANT.get();
        } else if (BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock() instanceof CakeBlock, range, 2)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock() instanceof SmokerBlock || blockState.getBlock() instanceof FurnaceBlock, range, 4)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock() instanceof BarrelBlock, range, 16)){
            return ModEntityType.VINDICATOR_CHEF_SERVANT.get();
        } else if (BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(BlockTags.PLANKS), range, 64)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock().getDescriptionId().contains("bricks"), range, 64)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(BlockTags.FENCES), range, 8)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock() instanceof BlastFurnaceBlock, range, 8)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock() instanceof LavaCauldronBlock, range, 2)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(Blocks.WATER_CAULDRON), range, 2)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock() instanceof AnvilBlock, range, 4)){
            return ModEntityType.CRUSHER_SERVANT.get();
        } else if (BlockFinder.getNearbyEnchantPower(level, blockPos, range, 32)
                && BlockFinder.getNearbyLitCandles(level, blockPos, range, 16)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(Blocks.LECTERN), range, 1)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(ModBlocks.AWAKENED_EMERALD_BLOCK.get()), range, 4)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock() instanceof AbstractSkullBlock, range, 4)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock() instanceof AmethystClusterBlock, range, 4)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock() instanceof FlowerPotBlock flowerPotBlock && flowerPotBlock.getContent() != Blocks.AIR, range, 4)) {
            return ModEntityType.EVOKER_SERVANT.get();
        } else if (BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(Tags.Blocks.STORAGE_BLOCKS_AMETHYST), range, 16)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock().getDescriptionId().contains("deepslate"), range, 64)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(ModBlocks.CREEPER_TOTEM.get()), range, 16)) {
            return ModEntityType.GEOMANCER_SERVANT.get();
        } else if (BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(Blocks.BLUE_ICE), range, 16)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(BlockTags.SNOW), range, 64)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(ModBlocks.FREEZING_LAMP.get()), range, 4)) {
            return ModEntityType.ICEOLOGER_SERVANT.get();
        } else if (BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(ModTags.Blocks.MARBLE_BLOCKS), range, 64)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(ModTags.Blocks.JADE_BLOCKS), range, 32)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.is(ModTags.Blocks.INDENTED_GOLD_BLOCKS), range, 4)) {
            return ModEntityType.WIND_CALLER_SERVANT.get();
        } else if (BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock().getDescriptionId().contains("copper"), range, 32)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock().getDescriptionId().contains("bricks"), range, 64)
                && BlockFinder.getNearbyBlocks(level, blockPos, blockState -> blockState.getBlock() instanceof LightningRodBlock, range, 4)
                && level.isRainingAt(blockPos)
                && level.isThundering()) {
            return ModEntityType.STORM_CASTER_SERVANT.get();
        }
        return null;
    }
}
