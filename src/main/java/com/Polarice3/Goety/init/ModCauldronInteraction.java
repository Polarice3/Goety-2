package com.Polarice3.Goety.init;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.items.ModItems;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Map;

public interface ModCauldronInteraction {
    Map<Item, CauldronInteraction> VOID = newInteractionMap();
    Map<Item, CauldronInteraction> MUD = newInteractionMap();

    static Object2ObjectOpenHashMap<Item, CauldronInteraction> newInteractionMap() {
        return Util.make(new Object2ObjectOpenHashMap<>(), (interaction) -> {
            interaction.defaultReturnValue((blockState, level, blockPos, player, hand, itemStack) -> InteractionResult.PASS);
        });
    }

    static void init() {
        VOID.put(Items.BUCKET, (p_175725_, p_175726_, p_175727_, p_175728_, p_175729_, p_175730_) -> {
            return CauldronInteraction.fillBucket(p_175725_, p_175726_, p_175727_, p_175728_, p_175729_, p_175730_, new ItemStack(ModItems.VOID_BUCKET.get()), (p_175660_) -> {
                return p_175660_.getValue(LayeredCauldronBlock.LEVEL) == 3;
            }, SoundEvents.BUCKET_FILL);
        });
        VOID.put(Items.GLASS_BOTTLE, (p_175718_, p_175719_, p_175720_, p_175721_, p_175722_, p_175723_) -> {
            if (!p_175719_.isClientSide) {
                Item item = p_175723_.getItem();
                p_175721_.setItemInHand(p_175722_, ItemUtils.createFilledResult(p_175723_, p_175721_, new ItemStack(ModItems.VOID_BOTTLE.get())));
                p_175721_.awardStat(Stats.USE_CAULDRON);
                p_175721_.awardStat(Stats.ITEM_USED.get(item));
                LayeredCauldronBlock.lowerFillLevel(p_175718_, p_175719_, p_175720_);
                p_175719_.playSound((Player)null, p_175720_, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                p_175719_.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, p_175720_);
            }

            return InteractionResult.sidedSuccess(p_175719_.isClientSide);
        });
        VOID.put(ModItems.VOID_BOTTLE.get(), (p_175704_, p_175705_, p_175706_, p_175707_, p_175708_, p_175709_) -> {
            if (p_175704_.getValue(LayeredCauldronBlock.LEVEL) != 3) {
                if (!p_175705_.isClientSide) {
                    p_175707_.setItemInHand(p_175708_, ItemUtils.createFilledResult(p_175709_, p_175707_, new ItemStack(Items.GLASS_BOTTLE)));
                    p_175707_.awardStat(Stats.USE_CAULDRON);
                    p_175707_.awardStat(Stats.ITEM_USED.get(p_175709_.getItem()));
                    p_175705_.setBlockAndUpdate(p_175706_, p_175704_.cycle(LayeredCauldronBlock.LEVEL));
                    p_175705_.playSound(null, p_175706_, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    p_175705_.gameEvent(null, GameEvent.FLUID_PLACE, p_175706_);
                }

                return InteractionResult.sidedSuccess(p_175705_.isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        });
        CauldronInteraction.EMPTY.put(ModItems.VOID_BOTTLE.get(), (p_175732_, p_175733_, p_175734_, p_175735_, p_175736_, p_175737_) -> {
            if (!p_175733_.isClientSide) {
                Item item = p_175737_.getItem();
                p_175735_.setItemInHand(p_175736_, ItemUtils.createFilledResult(p_175737_, p_175735_, new ItemStack(Items.GLASS_BOTTLE)));
                p_175735_.awardStat(Stats.USE_CAULDRON);
                p_175735_.awardStat(Stats.ITEM_USED.get(item));
                p_175733_.setBlockAndUpdate(p_175734_, ModBlocks.VOID_CAULDRON.get().defaultBlockState());
                p_175733_.playSound(null, p_175734_, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                p_175733_.gameEvent(null, GameEvent.FLUID_PLACE, p_175734_);
            }

            return InteractionResult.sidedSuccess(p_175733_.isClientSide);
        });
        CauldronInteraction.EMPTY.put(ModItems.VOID_BUCKET.get(), (blockState, level, blockPos, player, hand, itemStack) -> CauldronInteraction.emptyBucket(level, blockPos, player, hand, itemStack, ModBlocks.VOID_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY_LAVA));

        MUD.put(Items.BUCKET, (p_175725_, p_175726_, p_175727_, p_175728_, p_175729_, p_175730_) -> {
            return CauldronInteraction.fillBucket(p_175725_, p_175726_, p_175727_, p_175728_, p_175729_, p_175730_, new ItemStack(ModItems.END_MUD_BUCKET.get()), (p_175660_) -> {
                return p_175660_.getValue(LayeredCauldronBlock.LEVEL) == 3;
            }, SoundEvents.BUCKET_FILL);
        });
        MUD.put(Items.GLASS_BOTTLE, (p_175718_, p_175719_, p_175720_, p_175721_, p_175722_, p_175723_) -> {
            if (!p_175719_.isClientSide) {
                Item item = p_175723_.getItem();
                p_175721_.setItemInHand(p_175722_, ItemUtils.createFilledResult(p_175723_, p_175721_, new ItemStack(ModItems.END_MUD_BOTTLE.get())));
                p_175721_.awardStat(Stats.USE_CAULDRON);
                p_175721_.awardStat(Stats.ITEM_USED.get(item));
                LayeredCauldronBlock.lowerFillLevel(p_175718_, p_175719_, p_175720_);
                p_175719_.playSound((Player)null, p_175720_, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                p_175719_.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, p_175720_);
            }

            return InteractionResult.sidedSuccess(p_175719_.isClientSide);
        });
        MUD.put(ModItems.END_MUD_BOTTLE.get(), (p_175704_, p_175705_, p_175706_, p_175707_, p_175708_, p_175709_) -> {
            if (p_175704_.getValue(LayeredCauldronBlock.LEVEL) != 3) {
                if (!p_175705_.isClientSide) {
                    p_175707_.setItemInHand(p_175708_, ItemUtils.createFilledResult(p_175709_, p_175707_, new ItemStack(Items.GLASS_BOTTLE)));
                    p_175707_.awardStat(Stats.USE_CAULDRON);
                    p_175707_.awardStat(Stats.ITEM_USED.get(p_175709_.getItem()));
                    p_175705_.setBlockAndUpdate(p_175706_, p_175704_.cycle(LayeredCauldronBlock.LEVEL));
                    p_175705_.playSound(null, p_175706_, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    p_175705_.gameEvent(null, GameEvent.FLUID_PLACE, p_175706_);
                }

                return InteractionResult.sidedSuccess(p_175705_.isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        });
        CauldronInteraction.EMPTY.put(ModItems.END_MUD_BOTTLE.get(), (p_175732_, p_175733_, p_175734_, p_175735_, p_175736_, p_175737_) -> {
            if (!p_175733_.isClientSide) {
                Item item = p_175737_.getItem();
                p_175735_.setItemInHand(p_175736_, ItemUtils.createFilledResult(p_175737_, p_175735_, new ItemStack(Items.GLASS_BOTTLE)));
                p_175735_.awardStat(Stats.USE_CAULDRON);
                p_175735_.awardStat(Stats.ITEM_USED.get(item));
                p_175733_.setBlockAndUpdate(p_175734_, ModBlocks.END_MUD_CAULDRON.get().defaultBlockState());
                p_175733_.playSound(null, p_175734_, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                p_175733_.gameEvent(null, GameEvent.FLUID_PLACE, p_175734_);
            }

            return InteractionResult.sidedSuccess(p_175733_.isClientSide);
        });
        CauldronInteraction.EMPTY.put(ModItems.END_MUD_BUCKET.get(), (blockState, level, blockPos, player, hand, itemStack) -> CauldronInteraction.emptyBucket(level, blockPos, player, hand, itemStack, ModBlocks.END_MUD_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY));
    }
}
