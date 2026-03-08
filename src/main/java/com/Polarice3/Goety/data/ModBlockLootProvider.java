package com.Polarice3.Goety.data;

import com.Polarice3.Goety.common.blocks.*;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Based on @klikli-dev's Block Loot Generator
 */
public class ModBlockLootProvider extends BlockLootSubProvider {
    private static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
    private static final LootItemCondition.Builder HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS));
    private static final LootItemCondition.Builder HAS_SHEARS_OR_SILK_TOUCH = HAS_SHEARS.or(HAS_SILK_TOUCH);
    private static final LootItemCondition.Builder HAS_NO_SHEARS_OR_SILK_TOUCH = HAS_SHEARS_OR_SILK_TOUCH.invert();
    private static final float[] NORMAL_LEAVES_SAPLING_CHANCES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
    private static final float[] NORMAL_LEAVES_STICK_CHANCES = new float[]{0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F};

    public ModBlockLootProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
        this.generate();
        this.map.forEach(consumer::accept);
    }

    @Override
    protected void generate() {
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
                this.add(block, createDoorTable(block));
            } else if (block instanceof SlabBlock){
                this.add(block, createSlabItemTable(block));
            } else if (block instanceof WitchPoleBlock || block instanceof HauntedMirrorBlock || block instanceof DoublePlantBlock) {
                this.add(block, bl -> createSinglePropConditionTable(bl, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));
            } else if (block instanceof LampBlock) {
                this.add(block, bl -> createSinglePropConditionTable(bl, LampBlock.HALF, DoubleBlockHalf.LOWER));
            } else if (block instanceof PurpurLampBlock) {
                this.add(block, bl -> createSinglePropConditionTable(bl, PurpurLampBlock.HALF, DoubleBlockHalf.LOWER));
            } else {
                this.dropSelf(block);
            }
        }
        LootItemCondition.Builder lootbuilder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.SNAP_WARTS.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnapWartsBlock.AGE, 2));
        LootItemCondition.Builder lootbuilder1 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.SNAP_WARTS.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnapWartsBlock.AGE, 1));
        this.add(ModBlocks.SNAP_WARTS.get(), createSnapWartDrops(ModBlocks.SNAP_WARTS.get(), ModItems.SNAP_FUNGUS.get(), ModBlocks.SNAP_WARTS_ITEM.get(), lootbuilder, lootbuilder1));
        this.add(ModBlocks.FORBIDDEN_GRASS.get(), (p_124183_) -> createSingleItemTableWithSilkTouch(p_124183_, Blocks.DIRT));
        this.add(ModBlocks.SPIDER_NEST.get(), (p_124183_) -> createSingleItemTableWithSilkTouch(p_124183_, Items.STRING, UniformGenerator.between(4.0F, 8.0F)));
        this.add(ModBlocks.SMOOTH_SILTSTONE_BLOCK.get(), (p_124183_) -> createSingleItemTableWithSilkTouch(p_124183_, ModBlocks.COBBLED_SILTSTONE_BLOCK.get()));
        this.add(ModBlocks.END_STONE_SLATE_BLOCK.get(), (p_124183_) -> createSingleItemTableWithSilkTouch(p_124183_, ModBlocks.COBBLED_END_STONE_BLOCK.get()));
        this.dropWhenSilkTouch(ModBlocks.SCULK_RELAY.get());
        this.dropPottedContents(ModBlocks.POTTED_CHORUS_STALK.get());
        this.dropPottedContents(ModBlocks.POTTED_CHORUS_FERN.get());
        this.dropPottedContents(ModBlocks.POTTED_HAUNTED_SAPLING.get());
        this.dropPottedContents(ModBlocks.POTTED_ROTTEN_SAPLING.get());
        this.dropPottedContents(ModBlocks.POTTED_WINDSWEPT_SAPLING.get());
        this.dropPottedContents(ModBlocks.POTTED_PINE_SAPLING.get());
        this.dropPottedContents(ModBlocks.POTTED_CHORUS_SAPLING.get());
        this.add(ModBlocks.JADE_ORE.get(), (p_124076_) -> {
            return createOreDrop(p_124076_, ModItems.JADE.get());
        });
        this.add(ModBlocks.CRYSTAL_BALL.get(), (p_236253_) -> {
            return createSilkTouchDispatchTable(p_236253_, LootItem.lootTableItem(Items.GOLD_INGOT).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))));
        });
        this.add(ModBlocks.ROTTEN_LEAVES.get(), (p_124094_) -> {
            return createRottenLeavesDrops(p_124094_, ModBlocks.ROTTEN_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES);
        });
        this.add(ModBlocks.ROTTEN_BOOKSHELF.get(), (p_124233_) -> {
            return createSingleItemTableWithSilkTouch(p_124233_, Items.BOOK, ConstantValue.exactly(3.0F));
        });
        this.add(ModBlocks.WINDSWEPT_LEAVES.get(), (p_124094_) -> {
            return createLeavesDrops(p_124094_, ModBlocks.WINDSWEPT_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES);
        });
        this.add(ModBlocks.WINDSWEPT_BOOKSHELF.get(), (p_124233_) -> {
            return createSingleItemTableWithSilkTouch(p_124233_, Items.BOOK, ConstantValue.exactly(3.0F));
        });
        this.add(ModBlocks.PINE_LEAVES.get(), (p_124094_) -> {
            return createLeavesDrops(p_124094_, ModBlocks.PINE_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES);
        });
        this.add(ModBlocks.PINE_BOOKSHELF.get(), (p_124233_) -> {
            return createSingleItemTableWithSilkTouch(p_124233_, Items.BOOK, ConstantValue.exactly(3.0F));
        });
        this.add(ModBlocks.CHORUS_LEAVES.get(), (p_124094_) -> {
            return createChorusLeavesDrops(p_124094_, ModBlocks.CHORUS_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES);
        });
        this.add(ModBlocks.CHORUS_BLOSSOM_LEAVES.get(), (p_124094_) -> {
            return createLeavesDrops(p_124094_, ModBlocks.CHORUS_BLOSSOM_VINES.get(), NORMAL_LEAVES_STICK_CHANCES);
        });
        this.add(ModBlocks.CHORUS_BOOKSHELF.get(), (p_124233_) -> {
            return createSingleItemTableWithSilkTouch(p_124233_, Items.BOOK, ConstantValue.exactly(3.0F));
        });
        this.add(ModBlocks.CORRUPT_CHORUS_BOOKSHELF.get(), (p_124233_) -> {
            return createSingleItemTableWithSilkTouch(p_124233_, Items.BOOK, ConstantValue.exactly(3.0F));
        });
        this.add(ModBlocks.CRYPT_BOOKSHELF.get(), (p_124233_) -> {
            return createSingleItemTableWithSilkTouch(p_124233_, Items.BOOK, ConstantValue.exactly(3.0F));
        });
        this.add(ModBlocks.SKULL_PILE.get(), (p_124233_) -> {
            return createSingleItemTableWithSilkTouch(p_124233_, Items.BONE, UniformGenerator.between(3.0F, 6.0F));
        });
        this.add(ModBlocks.CRYPT_URN.get(), createSilkTouchOnlyTable(ModBlocks.CRYPT_URN.get()));
        this.add(ModBlocks.CRYPT_CHEST.get(), (p_124233_) -> {
            return createSingleItemTableWithSilkTouch(p_124233_, ModBlocks.CRYPT_STONE_BLOCK.get(), UniformGenerator.between(2.0F, 4.0F));
        });
        this.add(ModBlocks.LOFTY_CHEST.get(), (p_124233_) -> {
            return createSingleItemTableWithSilkTouch(p_124233_, Items.OBSIDIAN, UniformGenerator.between(2.0F, 4.0F));
        });
        this.add(ModBlocks.SPIDER_SAC.get(), (p_124233_) -> {
            return createSingleItemTableWithSilkTouch(p_124233_, Items.STRING, UniformGenerator.between(2.0F, 4.0F));
        });
        this.add(ModBlocks.STASH_URN.get(), createSilkTouchOnlyTable(ModBlocks.STASH_URN.get()));
        this.add(ModBlocks.CHORUS_VINE.get(), (p_124233_) -> {
            return createShearsOnlyDrop(ModBlocks.CHORUS_VINE.get());
        });
        this.add(ModBlocks.END_GRASS_SPROUT.get(), (p_124233_) -> {
            return createShearsOnlyDrop(ModBlocks.END_GRASS_SPROUT.get());
        });
        this.add(ModBlocks.END_GRASS.get(), (p_124233_) -> {
            return createShearsOnlyDrop(ModBlocks.END_GRASS.get());
        });
        this.add(ModBlocks.TALL_END_GRASS.get(), (p_124233_) -> {
            return createShearsOnlyDrop(ModBlocks.TALL_END_GRASS.get());
        });
        this.add(ModBlocks.CHORUS_TALL_GRASS.get(), (p_124233_) -> {
            return createShearsOnlyDrop(ModBlocks.CHORUS_TALL_GRASS.get());
        });
        this.add(ModBlocks.CHORUS_FERN_SPROUT.get(), (p_124233_) -> {
            return createShearsOnlyDrop(ModBlocks.CHORUS_FERN_SPROUT.get());
        });
        this.add(ModBlocks.CHORUS_FERN.get(), (p_124233_) -> {
            return createShearsOnlyDrop(ModBlocks.CHORUS_FERN.get());
        });
        this.add(ModBlocks.LARGE_CHORUS_FERN.get(), (p_124233_) -> {
            return createDoublePlantShearsDrop(ModBlocks.LARGE_CHORUS_FERN.get());
        });
        this.addNetherVinesDropTable(ModBlocks.END_GROWTH_VINES.get(), ModBlocks.END_GROWTH_VINES_PLANT.get());
        this.add(ModBlocks.VOID_BARREL.get(), this::createVoidBarrelConditionTable);
        this.dropOther(ModBlocks.VOID_CAULDRON.get(), Blocks.CAULDRON.asItem());
        this.dropOther(ModBlocks.END_MUD_CAULDRON.get(), Blocks.CAULDRON.asItem());
        this.add(ModBlocks.END_SOIL_DEBRIS.get(), (p_251108_) -> {
            return LootTable.lootTable().withPool(LootPool.lootPool().when(LootItemEntityPropertyCondition.entityPresent(LootContext.EntityTarget.THIS)).add(AlternativesEntry.alternatives(AlternativesEntry.alternatives(LayerBlock.LAYERS.getPossibleValues(), (p_252097_) -> {
                return LootItem.lootTableItem(ModBlocks.END_SOIL_DEBRIS.get()).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_251108_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(LayerBlock.LAYERS, p_252097_))).apply(SetItemCountFunction.setCount(ConstantValue.exactly((float)p_252097_.intValue())));
            }).when(HAS_NO_SILK_TOUCH), AlternativesEntry.alternatives(LayerBlock.LAYERS.getPossibleValues(), (p_251216_) -> {
                return p_251216_ == 8 ? LootItem.lootTableItem(ModBlocks.END_SOIL.get()) : LootItem.lootTableItem(ModBlocks.END_SOIL_DEBRIS.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly((float)p_251216_.intValue()))).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_251108_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(LayerBlock.LAYERS, p_251216_)));
            }))));
        });
    }

    protected LootTable.Builder createRottenLeavesDrops(Block p_124264_, Block p_124265_, float... p_124266_) {
        return createLeavesDrops(p_124264_, p_124265_, p_124266_).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(HAS_NO_SHEARS_OR_SILK_TOUCH).add(applyExplosionCondition(p_124264_, LootItem.lootTableItem(Items.ROTTEN_FLESH)).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
    }

    protected LootTable.Builder createChorusLeavesDrops(Block p_250088_, Block p_250731_, float... p_248949_) {
        return createSilkTouchOrShearsDispatchTable(p_250088_, this.applyExplosionCondition(p_250088_, LootItem.lootTableItem(p_250731_)).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, p_248949_))).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(HAS_NO_SHEARS_OR_SILK_TOUCH).add(this.applyExplosionDecay(p_250088_, LootItem.lootTableItem(ModBlocks.CHORUS_VINE.get().asItem()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, NORMAL_LEAVES_STICK_CHANCES))));
    }

    protected LootTable.Builder createSnapWartDrops(Block block, Item drop, Item drop1, LootItemCondition.Builder builder, LootItemCondition.Builder builder1) {
        return applyExplosionDecay(block, LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(drop).when(builder)).add(LootItem.lootTableItem(drop1).when(builder1))));
    }

    protected LootTable.Builder createOreDrop(Block p_124140_, Item p_124141_) {
        return createSilkTouchDispatchTable(p_124140_, applyExplosionDecay(p_124140_, LootItem.lootTableItem(p_124141_).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
    }

    protected <T extends Comparable<T> & StringRepresentable> LootTable.Builder createVoidBarrelConditionTable(Block p_252154_) {
        return LootTable.lootTable().withPool(this.applyExplosionCondition(p_252154_, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_252154_).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_252154_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER).hasProperty(BlockStateProperties.LIT, false))))));
    }
}
