package com.Polarice3.Goety.data;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Based on @klikli-dev's Block Loot Generator
 */
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
            Collection<Block> blocks = new ArrayList<>();
            ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block ->
            {
                ModBlocks.BlockLootSetting setting = ModBlocks.BLOCK_LOOT.get(ForgeRegistries.BLOCKS.getKey(block));
                if (setting.lootTableType == ModBlocks.LootTableType.DROP){
                    blocks.add(block);
                }
            });
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
