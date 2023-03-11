package com.Polarice3.Goety.data;

import com.Polarice3.Goety.common.blocks.ModBlockFamilies;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.Collection;

public class ModBlockLootProvider extends ModBaseLootProvider{

    ModBlockLoot blockLoot = new ModBlockLoot();

    public ModBlockLootProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        this.blockLoot.addTables();
    }

    private class ModBlockLoot extends BlockLoot {

        @Override
        protected void addTables() {
            this.dropSelf(ModBlocks.ARCA_BLOCK.get());
            this.dropSelf(ModBlocks.CURSED_CAGE_BLOCK.get());
            this.dropSelf(ModBlocks.DARK_ALTAR.get());
            this.dropSelf(ModBlocks.ICE_BOUQUET_TRAP.get());
            this.dropSelf(ModBlocks.PEDESTAL.get());
            this.dropSelf(ModBlocks.SOUL_ABSORBER.get());
            this.dropSelf(ModBlocks.SCULK_DEVOURER.get());
            this.dropSelf(ModBlocks.CURSED_METAL_BLOCK.get());
            this.dropSelf(ModBlocks.HAUNTED_SAPLING.get());
            this.dropSelf(ModBlocks.CURSED_BARS_BLOCK.get());
            this.dropSelf(ModBlocks.TALL_SKULL_BLOCK.get());
            Collection<Block> blocks = new ArrayList<>();
            ModBlockFamilies.getAllFamilies().forEach((blockFamily -> blocks.addAll(blockFamily.getVariants().values())));
            ModBlockFamilies.getAllFamilies().forEach((blockFamily -> blocks.add(blockFamily.getBaseBlock())));
            for (Block block : blocks){
                if (block instanceof DoorBlock){
                    this.add(block, BlockLoot::createDoorTable);
                } else if (block instanceof SlabBlock){
                    this.add(block, BlockLoot::createSlabItemTable);
                } else {
                    this.dropSelf(block);
                }
            }
        }

        @Override
        protected void add(Block blockIn, LootTable.Builder table) {
            ModBlockLootProvider.this.blockLootTable.put(blockIn, table);
        }
    }
}
