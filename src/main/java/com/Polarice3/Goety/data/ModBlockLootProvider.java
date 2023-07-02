package com.Polarice3.Goety.data;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.SnapWartsBlock;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
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
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
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
        private static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
        private static final LootItemCondition.Builder HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS));
        private static final LootItemCondition.Builder HAS_SHEARS_OR_SILK_TOUCH = HAS_SHEARS.or(HAS_SILK_TOUCH);
        private static final LootItemCondition.Builder HAS_NO_SHEARS_OR_SILK_TOUCH = HAS_SHEARS_OR_SILK_TOUCH.invert();
        private static final float[] NORMAL_LEAVES_SAPLING_CHANCES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};

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
            this.dropWhenSilkTouch(ModBlocks.SCULK_RELAY.get());
            this.dropPottedContents(ModBlocks.POTTED_HAUNTED_SAPLING.get());
            this.dropPottedContents(ModBlocks.POTTED_ROTTEN_SAPLING.get());
            this.add(ModBlocks.JADE_ORE.get(), (p_124076_) -> {
                return createOreDrop(p_124076_, ModItems.JADE.get());
            });
            this.add(ModBlocks.CRYSTAL_BALL.get(), (p_236253_) -> {
                return createSilkTouchDispatchTable(p_236253_, LootItem.lootTableItem(Items.GOLD_INGOT).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))));
            });
            this.add(ModBlocks.ROTTEN_LEAVES.get(), (p_124094_) -> {
                return createRottenLeavesDrops(p_124094_, ModBlocks.ROTTEN_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES);
            });
        }

        protected static LootTable.Builder createRottenLeavesDrops(Block p_124264_, Block p_124265_, float... p_124266_) {
            return createLeavesDrops(p_124264_, p_124265_, p_124266_).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(HAS_NO_SHEARS_OR_SILK_TOUCH).add(applyExplosionCondition(p_124264_, LootItem.lootTableItem(Items.ROTTEN_FLESH)).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
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
