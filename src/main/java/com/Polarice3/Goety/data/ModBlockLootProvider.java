package com.Polarice3.Goety.data;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.SnapWartsBlock;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
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
            LootItemCondition.Builder lootbuilder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.SNAP_WARTS.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnapWartsBlock.AGE, 2));
            LootItemCondition.Builder lootbuilder1 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.SNAP_WARTS.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnapWartsBlock.AGE, 1));
            this.add(ModBlocks.SNAP_WARTS.get(), createSnapWartDrops(ModBlocks.SNAP_WARTS.get(), ModItems.SNAP_FUNGUS.get(), ModBlocks.SNAP_WARTS_ITEM.get(), lootbuilder, lootbuilder1));
            this.add(ModBlocks.FORBIDDEN_GRASS.get(), (p_124183_) -> createSingleItemTableWithSilkTouch(p_124183_, Blocks.DIRT));
            this.add(ModBlocks.SPIDER_NEST.get(), (p_124183_) -> createSingleItemTableWithSilkTouch(p_124183_, Items.STRING, UniformGenerator.between(4.0F, 8.0F)));

            this.add(ModBlocks.JADE_ORE.get(), (p_124076_) -> {
                return createOreDrop(p_124076_, ModItems.JADE.get());
            });
        }

        protected static LootTable.Builder createSnapWartDrops(Block block, Item drop, Item drop1, LootItemCondition.Builder builder, LootItemCondition.Builder builder1) {
            return applyExplosionDecay(block, LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(drop).when(builder)).add(LootItem.lootTableItem(drop1).when(builder1))));
        }

        protected static LootTable.Builder createOreDrop(Block p_124140_, Item p_124141_) {
            return createSilkTouchDispatchTable(p_124140_, applyExplosionDecay(p_124140_, LootItem.lootTableItem(p_124141_).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
        }

        @Override
        protected void add(Block blockIn, LootTable.Builder table) {
            ModBlockLootProvider.this.blockLootTable.put(blockIn, table);
        }
    }
}
