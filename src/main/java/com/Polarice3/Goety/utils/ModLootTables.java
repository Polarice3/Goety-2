package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.Goety;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ModLootTables {
    private static final Set<ResourceLocation> LOCATIONS = Sets.newHashSet();
    private static final Set<ResourceLocation> IMMUTABLE_LOCATIONS = Collections.unmodifiableSet(LOCATIONS);
    public static final ResourceLocation EMPTY = new ResourceLocation("empty");
    public static final ResourceLocation CRYPT_TOMB = register("chests/crypt_tomb");

    public static final ResourceLocation TALL_SKULL = register("entities/tall_skull_mobs");
    public static final ResourceLocation PLAYER_WITCH = register("entities/player_witch");
    public static final ResourceLocation CULTISTS = register("entities/cultist_extra");
    public static final ResourceLocation CRYPT_SLIME = register("entities/crypt_slime");
    public static final ResourceLocation INFERNO = register("entities/inferno_extra");
    public static final ResourceLocation APOSTLE_HARD = register("entities/apostle_2");

    public static final ResourceLocation WITCH_BARTER = register("gameplay/witch_bartering");
    public static final ResourceLocation WARLOCK_BARTER = register("gameplay/warlock_bartering");
    public static final ResourceLocation MAVERICK_BARTER = register("gameplay/maverick_bartering");
    public static final ResourceLocation HERETIC_BARTER = register("gameplay/heretic_bartering");
    public static final ResourceLocation CRONE_BARTER = register("gameplay/crone_bartering");

    public static final ResourceLocation TREASURE_POUCH = register("gameplay/treasure_pouch");

    private static ResourceLocation register(String pId) {
        return register(Goety.location(pId));
    }

    private static ResourceLocation register(ResourceLocation pId) {
        if (LOCATIONS.add(pId)) {
            return pId;
        } else {
            throw new IllegalArgumentException(pId + " is already a registered built-in loot table");
        }
    }

    public static LootContext.Builder createLootParams(LivingEntity target, boolean checkPlayerKill, DamageSource source) {
        LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel) target.level)).withParameter(LootContextParams.THIS_ENTITY, target).withParameter(LootContextParams.ORIGIN, target.position()).withParameter(LootContextParams.DAMAGE_SOURCE, source).withOptionalParameter(LootContextParams.KILLER_ENTITY, source.getEntity()).withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, source.getDirectEntity());
        if (checkPlayerKill && target.getKillCredit() instanceof Player player) {
            lootcontext$builder = lootcontext$builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player).withLuck(player.getLuck());
        }

        return lootcontext$builder;
    }

    public static void shuffleAndSplitItems(ObjectArrayList<ItemStack> p_230925_, int p_230926_, RandomSource p_230927_) {
        List<ItemStack> list = Lists.newArrayList();
        Iterator<ItemStack> iterator = p_230925_.iterator();

        while(iterator.hasNext()) {
            ItemStack itemstack = iterator.next();
            if (itemstack.isEmpty()) {
                iterator.remove();
            } else if (itemstack.getCount() > 1) {
                list.add(itemstack);
                iterator.remove();
            }
        }

        while(p_230926_ - p_230925_.size() - list.size() > 0 && !list.isEmpty()) {
            ItemStack itemstack2 = list.remove(Mth.nextInt(p_230927_, 0, list.size() - 1));
            int i = Mth.nextInt(p_230927_, 1, itemstack2.getCount() / 2);
            ItemStack itemstack1 = itemstack2.split(i);
            if (itemstack2.getCount() > 1 && p_230927_.nextBoolean()) {
                list.add(itemstack2);
            } else {
                p_230925_.add(itemstack2);
            }

            if (itemstack1.getCount() > 1 && p_230927_.nextBoolean()) {
                list.add(itemstack1);
            } else {
                p_230925_.add(itemstack1);
            }
        }

        p_230925_.addAll(list);
        Util.shuffle(p_230925_, p_230927_);
    }

    public static void createLootChest(LivingEntity target, BlockState blockState, BlockPos blockPos, DamageSource cause){
        if (target.level.getServer() != null) {
            target.level.setBlockAndUpdate(blockPos, blockState);
            LootContext lootParams = ModLootTables.createLootParams(target, true, cause).create(LootContextParamSets.ENTITY);
            LootTable table = target.level.getServer().getLootTables().get(target.getLootTable());
            ObjectArrayList<ItemStack> lootItems = table.getRandomItems(lootParams);
            List<Integer> availableSlots = getAvailableSlots(target.getRandom());
            ModLootTables.shuffleAndSplitItems(lootItems, availableSlots.size(), target.getRandom());
            NonNullList<ItemStack> finalLoot = NonNullList.withSize(27, ItemStack.EMPTY);
            for (ItemStack itemstack : lootItems) {
                if (!availableSlots.isEmpty()) {
                    if (itemstack.isEmpty()) {
                        finalLoot.set(availableSlots.remove(availableSlots.size() - 1), ItemStack.EMPTY);
                    } else {
                        finalLoot.set(availableSlots.remove(availableSlots.size() - 1), itemstack);
                    }
                }
            }
            if (target.level.getBlockEntity(blockPos) instanceof Container container) {
                for (int i = 0; i < container.getContainerSize(); i++) {
                    container.setItem(i, finalLoot.get(i));
                }
            }
        }
    }

    public static List<Integer> getAvailableSlots(RandomSource random) {
        ObjectArrayList<Integer> arrayList = new ObjectArrayList<>();
        for (int i = 0; i < 27; ++i) {
            arrayList.add(i);
        }
        Util.shuffle(arrayList, random);
        return arrayList;
    }

    public static Set<ResourceLocation> all() {
        return IMMUTABLE_LOCATIONS;
    }

}
