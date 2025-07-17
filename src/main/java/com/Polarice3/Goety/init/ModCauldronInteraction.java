package com.Polarice3.Goety.init;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.items.ModItems;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Map;

public interface ModCauldronInteraction {
    Map<Item, CauldronInteraction> VOID = newInteractionMap();

    static Object2ObjectOpenHashMap<Item, CauldronInteraction> newInteractionMap() {
        return Util.make(new Object2ObjectOpenHashMap<>(), (interaction) -> {
            interaction.defaultReturnValue((blockState, level, blockPos, player, hand, itemStack) -> InteractionResult.PASS);
        });
    }

    static void init() {
        VOID.put(Items.BUCKET, (blockState, level, blockPos, player, hand, itemStack) -> CauldronInteraction.fillBucket(blockState, level, blockPos, player, hand, itemStack, new ItemStack(ModItems.VOID_BUCKET.get()), (state) -> true, SoundEvents.BUCKET_FILL_LAVA));

        CauldronInteraction.EMPTY.put(ModItems.VOID_BUCKET.get(), (blockState, level, blockPos, player, hand, itemStack) -> CauldronInteraction.emptyBucket(level, blockPos, player, hand, itemStack, ModBlocks.VOID_CAULDRON.get().defaultBlockState(), SoundEvents.BUCKET_EMPTY_LAVA));
    }
}
