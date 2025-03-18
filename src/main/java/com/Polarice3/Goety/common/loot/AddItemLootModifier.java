package com.Polarice3.Goety.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AddItemLootModifier implements IGlobalLootModifier {

    public static final Supplier<Codec<AddItemLootModifier>> CODEC = () ->
            RecordCodecBuilder.create(inst ->
                    inst.group(
                                    Codec.floatRange(0, Float.MAX_VALUE).fieldOf("chance").forGetter((lm) -> lm.chance),
                                    Codec.BOOL.fieldOf("replace").forGetter((configuration) -> configuration.replace),
                                    LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(lm -> lm.conditions),
                                    ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(lm -> lm.addedItem),
                                    Codec.intRange(0, Integer.MAX_VALUE).fieldOf("count").forGetter((lm) -> lm.count)
                                    )
                            .apply(inst, AddItemLootModifier::new));

    private final float chance;
    private final boolean replace;
    private final Item addedItem;
    private final int count;
    private final LootItemCondition[] conditions;

    private final Predicate<LootContext> orConditions;

    protected AddItemLootModifier(float chance, boolean replace, LootItemCondition[] conditionsIn, Item addedItemIn, int count) {
        this.conditions = conditionsIn;
        this.orConditions = LootItemConditions.orConditions(conditionsIn);
        this.addedItem = addedItemIn;
        this.replace = replace;
        this.count = count;
        this.chance = chance;
    }

    @NotNull
    @Override
    public ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        return this.orConditions.test(context) ? this.doApply(generatedLoot, context) : generatedLoot;
    }

    @Nonnull
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextFloat() < this.chance) {
            if (this.replace) {
                generatedLoot.clear();
            }
            generatedLoot.add(new ItemStack(this.addedItem, this.count));
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
