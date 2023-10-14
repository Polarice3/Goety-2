package com.Polarice3.Goety.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.event.village.VillagerTradesEvent;

import javax.annotation.Nullable;

public class ModTradeUtil {
    /**
     * From Here: Based of TeamAbnormals' TradeUtil: <a href="https://github.com/team-abnormals/blueprint/blob/1.19.x/src/main/java/com/teamabnormals/blueprint/core/util/TradeUtil.java">...</a>
     */
    public static final int NOVICE = 1;
    public static final int APPRENTICE = 2;
    public static final int JOURNEYMAN = 3;
    public static final int EXPERT = 4;
    public static final int MASTER = 5;

    public static void addVillagerTrades(VillagerTradesEvent event, int profLevel, VillagerTrades.ItemListing... trades) {
        for (VillagerTrades.ItemListing trade : trades) {
            event.getTrades().get(profLevel).add(trade);
        }
    }

    public static void addVillagerTrades(VillagerTradesEvent event, VillagerProfession profession, int profLevel, VillagerTrades.ItemListing... trades) {
        if (event.getType().equals(profession)) {
            addVillagerTrades(event, profLevel, trades);
        }
    }
    /**
     * To Here.
     */

    public static class ItemsForEmeralds implements VillagerTrades.ItemListing {
        private final Item item;
        private final int cost;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public ItemsForEmeralds(ItemLike item, int cost, int maxUses, int villagerXp) {
            this.item = item.asItem();
            this.cost = cost;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.priceMultiplier = 0.05F;
        }

        public MerchantOffer getOffer(Entity p_219682_, RandomSource p_219683_) {
            ItemStack itemstack = new ItemStack(this.item, this.cost);
            return new MerchantOffer(new ItemStack(Items.EMERALD), itemstack, this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    public static class TreasureMapForEmeralds implements VillagerTrades.ItemListing {
        private final int emeraldCost;
        private final TagKey<Structure> destination;
        private final String displayName;
        private final MapDecoration.Type destinationType;
        private final int maxUses;
        private final int villagerXp;

        public TreasureMapForEmeralds(int emeraldCost, TagKey<Structure> destination, String displayName, MapDecoration.Type mapMarker, int maxUses, int villagerXp) {
            this.emeraldCost = emeraldCost;
            this.destination = destination;
            this.displayName = displayName;
            this.destinationType = mapMarker;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
        }

        @Nullable
        public MerchantOffer getOffer(Entity entity, RandomSource randomSource) {
            if (!(entity.level instanceof ServerLevel serverlevel)) {
                return null;
            } else {
                BlockPos blockpos = serverlevel.findNearestMapStructure(this.destination, entity.blockPosition(), 100, true);
                if (blockpos != null) {
                    ItemStack itemstack = MapItem.create(serverlevel, blockpos.getX(), blockpos.getZ(), (byte)2, true, true);
                    MapItem.renderBiomePreviewMap(serverlevel, itemstack);
                    MapItemSavedData.addTargetDecoration(itemstack, blockpos, "+", this.destinationType);
                    itemstack.setHoverName(Component.translatable(this.displayName));
                    return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(Items.COMPASS), itemstack, this.maxUses, this.villagerXp, 0.2F);
                } else {
                    return null;
                }
            }
        }
    }
}
