package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.Polarice3.Goety.common.entities.neutral.ender.AbstractEnderling;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.brew.ThrowableBrewItem;
import com.Polarice3.Goety.common.items.curios.*;
import com.Polarice3.Goety.common.items.handler.BrewBagItemHandler;
import com.Polarice3.Goety.compat.curios.CuriosLoaded;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;
import java.util.function.Predicate;

public class CuriosFinder {

    public static ItemStack findCurio(LivingEntity livingEntity, Predicate<ItemStack> filter){
        ItemStack foundStack = ItemStack.EMPTY;
        if (livingEntity instanceof Player) {
            if (CuriosLoaded.CURIOS.isLoaded()) {
                Optional<SlotResult> slotResult = CuriosApi.getCuriosInventory(livingEntity).map(inv -> inv.findFirstCurio(filter))
                        .orElse(Optional.empty());
                if (slotResult.isPresent()) {
                    foundStack = slotResult.get().stack();
                }
            }
        }

        return foundStack;
    }

    public static boolean hasCurio(LivingEntity livingEntity, Predicate<ItemStack> filter){
        return !findCurio(livingEntity, filter).isEmpty();
    }

    public static boolean hasCurio(LivingEntity livingEntity, Item item){
        return !findCurio(livingEntity, item).isEmpty();
    }

    public static ItemStack findCurio(LivingEntity livingEntity, Item item){
        ItemStack foundStack = ItemStack.EMPTY;
        if (livingEntity instanceof Player) {
            if (CuriosLoaded.CURIOS.isLoaded()) {
                Optional<SlotResult> slotResult = CuriosApi.getCuriosInventory(livingEntity).map(inv -> inv.findFirstCurio(item))
                        .orElse(Optional.empty());
                if (slotResult.isPresent()) {
                    foundStack = slotResult.get().stack();
                }
            }
        }

        return foundStack;
    }

    public static ItemStack findCurioInAll(Player playerEntity, Item item){
        ItemStack foundStack = ItemStack.EMPTY;
        if (CuriosLoaded.CURIOS.isLoaded()) {
            Optional<SlotResult> slotResult = CuriosApi.getCuriosInventory(playerEntity).map(inv -> inv.findFirstCurio(item))
                    .orElse(Optional.empty());
            if (slotResult.isPresent()) {
                foundStack = slotResult.get().stack();
            }
        }

        if (playerEntity.getOffhandItem().is(item)){
            foundStack = playerEntity.getOffhandItem();
        } else {
            for (int i = 0; i <= 9; i++) {
                ItemStack itemStack = playerEntity.getInventory().getItem(i);
                if (!itemStack.isEmpty() && itemStack.is(item)) {
                    foundStack = itemStack;
                    break;
                }
            }
        }
        return foundStack;
    }

    public static boolean noHeadWear(LivingEntity livingEntity){
        if (livingEntity != null) {
            if (CuriosLoaded.CURIOS.isLoaded()) {
                return CuriosApi.getCuriosInventory(livingEntity).map(inv -> inv.findCurios("head").isEmpty()).orElse(false);
            }
        }
        return true;
    }

    public static boolean hasWanting(Entity entity){
        Player player = null;
        if (entity instanceof Player player1){
            player = player1;
        } else if (MobUtil.getOwner(entity) instanceof Player player1) {
            player = player1;
        }
        if (player != null) {
            if (CuriosFinder.findRing(player).getItem() == ModItems.RING_OF_WANT.get()){
                if (CuriosFinder.findRing(player).isEnchanted()){
                    float wanting = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.WANTING.get(), CuriosFinder.findRing(player));
                    return wanting > 0;
                }
            }
        }
        return false;
    }

    public static boolean hasCastTimeReduce(LivingEntity livingEntity){
        return hasMagicHat(livingEntity) || hasNamelessCrown(livingEntity);
    }

    public static boolean hasMagicHat(LivingEntity livingEntity){
        return hasCurio(livingEntity, (itemStack -> itemStack.getItem() instanceof MagicHatItem));
    }

    public static boolean hasDarkRobe(LivingEntity livingEntity){
        return hasCurio(livingEntity, (itemStack -> itemStack.getItem() instanceof MagicRobeItem));
    }

    public static boolean hasWildRobe(LivingEntity livingEntity){
        return hasCurio(livingEntity, (itemStack -> itemStack.getItem() instanceof WildRobeItem));
    }

    public static boolean hasWildCrown(LivingEntity livingEntity){
        return hasCurio(livingEntity, item -> item.getItem() instanceof MagicCrownItem crownItem && crownItem.spellType == SpellType.WILD);
    }

    public static boolean hasWildSet(LivingEntity livingEntity){
        return hasWildRobe(livingEntity)
                && hasWildCrown(livingEntity);
    }

    public static boolean neutralWildSet(LivingEntity livingEntity){
        return hasWildSet(livingEntity) && ItemConfig.WildSetMobNeutral.get();
    }

    public static boolean validWildMob(LivingEntity livingEntity){
        return (livingEntity.getMobType() == ModMobType.NATURAL
                || livingEntity.getMobType() == MobType.ARTHROPOD
                || livingEntity instanceof Animal
                || livingEntity.getType().is(ModTags.EntityTypes.WILD_SET_NEUTRAL))
                && livingEntity.getMaxHealth() <= ItemConfig.WildSetMobNeutralHealth.get()
                && !(livingEntity instanceof IOwned && !(livingEntity instanceof Enemy));
    }

    public static boolean hasGeoRobe(LivingEntity livingEntity){
        return hasCurio(livingEntity, ModItems.GEO_ROBE.get());
    }

    public static boolean hasAmethystNecklace(LivingEntity livingEntity) {
        return hasCurio(livingEntity, ModItems.AMETHYST_NECKLACE.get());
    }

    public static boolean hasGeoSet(LivingEntity livingEntity){
        return hasGeoRobe(livingEntity)
                && hasAmethystNecklace(livingEntity);
    }

    public static boolean neutralGeoSet(LivingEntity livingEntity){
        return hasGeoSet(livingEntity) && ItemConfig.GeoSetMobNeutral.get();
    }

    public static boolean validGeoMob(LivingEntity livingEntity){
        return livingEntity.getType().is(ModTags.EntityTypes.GEO_SET_NEUTRAL)
                && livingEntity.getMaxHealth() <= ItemConfig.GeoSetMobNeutralHealth.get()
                && !(livingEntity instanceof IOwned && !(livingEntity instanceof Enemy));
    }

    public static boolean hasVoidRobe(LivingEntity livingEntity){
        return hasCurio(livingEntity, (itemStack -> itemStack.getItem() instanceof VoidRobeItem));
    }

    public static boolean hasVoidCrown(LivingEntity livingEntity){
        return hasCurio(livingEntity, item -> item.getItem() instanceof MagicCrownItem crownItem && crownItem.spellType == SpellType.VOID);
    }

    public static boolean hasVoidSet(LivingEntity livingEntity){
        return hasVoidRobe(livingEntity)
                && hasVoidCrown(livingEntity);
    }

    public static boolean neutralVoidSet(LivingEntity livingEntity){
        return hasVoidSet(livingEntity) && ItemConfig.VoidSetMobNeutral.get();
    }

    public static boolean validVoidMob(LivingEntity livingEntity){
        return (livingEntity instanceof AbstractEnderling
                || livingEntity instanceof EnderMan
                || livingEntity.getType().is(ModTags.EntityTypes.VOID_SET_NEUTRAL))
                && livingEntity.getMaxHealth() <= ItemConfig.VoidSetMobNeutralHealth.get()
                && !(livingEntity instanceof IOwned && !(livingEntity instanceof Enemy));
    }

    public static boolean hasIllusionRobe(LivingEntity livingEntity){
        return hasCurio(livingEntity, (itemStack -> itemStack.getItem() instanceof IllusionRobeItem));
    }

    public static boolean hasWitchHat(LivingEntity livingEntity){
        return hasCurio(livingEntity, itemStack -> itemStack.getItem() instanceof WitchHatItem);
    }

    public static boolean hasWitchRobe(LivingEntity livingEntity){
        return hasCurio(livingEntity, itemStack -> itemStack.getItem() instanceof WitchRobeItem);
    }

    public static boolean hasWarlockRobe(LivingEntity livingEntity){
        return hasCurio(livingEntity, itemStack -> itemStack.getItem() instanceof WarlockRobeItem);
    }

    public static boolean hasNetherRobe(LivingEntity livingEntity){
        return hasCurio(livingEntity, (itemStack -> itemStack.getItem() instanceof NetherRobeItem))
                || hasUnholyRobe(livingEntity);
    }

    public static boolean hasNetherCrown(LivingEntity livingEntity){
        return hasCurio(livingEntity, item -> item.getItem() instanceof MagicCrownItem crownItem && crownItem.spellType == SpellType.NETHER);
    }

    public static boolean hasNetherSet(LivingEntity livingEntity){
        return hasNetherRobe(livingEntity)
                && hasNetherCrown(livingEntity);
    }

    public static boolean neutralNetherSet(LivingEntity livingEntity){
        return hasNetherSet(livingEntity) && ItemConfig.NetherSetMobNeutral.get();
    }

    public static boolean validNetherMob(LivingEntity livingEntity){
        return (livingEntity.getMobType() == ModMobType.NETHER
                || livingEntity instanceof Blaze
                || livingEntity instanceof Ghast
                || livingEntity instanceof MagmaCube
                || livingEntity.getType().is(ModTags.EntityTypes.NETHER_SET_NEUTRAL))
                && livingEntity.getMaxHealth() <= ItemConfig.NetherSetMobNeutralHealth.get()
                && !(livingEntity instanceof IOwned && !(livingEntity instanceof Enemy));
    }

    public static boolean hasUnholyRobe(LivingEntity livingEntity){
        return hasCurio(livingEntity, (itemStack -> itemStack.getItem() instanceof UnholyRobeItem));
    }

    public static boolean hasUnholyHat(LivingEntity livingEntity){
        return hasCurio(livingEntity, item -> item.getItem() instanceof UnholyHatItem);
    }

    public static boolean hasUnholySet(LivingEntity livingEntity){
        return (hasUnholyRobe(livingEntity) && hasUnholyHat(livingEntity)) || livingEntity instanceof Apostle;
    }

    public static boolean isWitchFriendly(LivingEntity livingEntity){
        return (hasWitchSet(livingEntity) && ItemConfig.WitchSetWitchNeutral.get())
                || (hasWarlockRobe(livingEntity) && ItemConfig.WarlockRobeWitchNeutral.get())
                || (hasNetherRobe(livingEntity) && ItemConfig.NetherRobeWitchNeutral.get())
                || (hasUnholyRobe(livingEntity) || hasUnholyHat(livingEntity));
    }

    public static boolean hasWitchSet(LivingEntity livingEntity){
        return hasWitchHat(livingEntity)
                && hasWitchRobe(livingEntity);
    }

    public static boolean hasNecroCrown(LivingEntity livingEntity){
        return CuriosFinder.hasCurio(livingEntity, itemStack -> itemStack.getItem() instanceof NecroGarbs.NecroCrownItem crownItem && !crownItem.isNameless);
    }

    public static boolean hasNecroCape(LivingEntity livingEntity){
        return CuriosFinder.hasCurio(livingEntity, itemStack -> itemStack.getItem() instanceof NecroGarbs.NecroCapeItem capeItem && !capeItem.isNameless);
    }

    public static boolean neutralNecroCrown(LivingEntity livingEntity){
        return hasNecroCrown(livingEntity) && ItemConfig.NecroSetUndeadNeutral.get();
    }

    public static boolean neutralNecroCape(LivingEntity livingEntity){
        return hasNecroCape(livingEntity) && ItemConfig.NecroSetUndeadNeutral.get();
    }

    public static boolean hasNecroSet(LivingEntity livingEntity){
        return hasNecroCrown(livingEntity) && hasNecroCape(livingEntity);
    }

    public static boolean neutralNecroSet(LivingEntity livingEntity){
        return hasNecroSet(livingEntity) && ItemConfig.NecroSetUndeadNeutral.get();
    }

    public static boolean validNecroUndead(LivingEntity livingEntity){
        return (livingEntity.getMobType() == MobType.UNDEAD || livingEntity.getType().is(ModTags.EntityTypes.NECRO_SET_NEUTRAL)) && livingEntity.getMaxHealth() <= ItemConfig.NecroSetUndeadNeutralHealth.get() && !(livingEntity instanceof IOwned && !(livingEntity instanceof Enemy));
    }

    public static boolean hasNamelessCrown(LivingEntity livingEntity){
        return CuriosFinder.hasCurio(livingEntity, itemStack -> itemStack.getItem() instanceof NecroGarbs.NecroCrownItem crownItem && crownItem.isNameless);
    }

    public static boolean hasNamelessCape(LivingEntity livingEntity){
        return CuriosFinder.hasCurio(livingEntity, itemStack -> itemStack.getItem() instanceof NecroGarbs.NecroCapeItem capeItem && capeItem.isNameless);
    }

    public static boolean neutralNamelessCrown(LivingEntity livingEntity){
        return hasNamelessCrown(livingEntity) && ItemConfig.NamelessSetUndeadNeutral.get();
    }

    public static boolean neutralNamelessCape(LivingEntity livingEntity){
        return hasNamelessCape(livingEntity) && ItemConfig.NamelessSetUndeadNeutral.get();
    }

    public static boolean hasNamelessSet(LivingEntity livingEntity){
        return hasNamelessCrown(livingEntity) && hasNamelessCape(livingEntity);
    }

    public static boolean neutralNamelessSet(LivingEntity livingEntity){
        return hasNamelessSet(livingEntity) && ItemConfig.NamelessSetUndeadNeutral.get();
    }

    public static boolean validNamelessUndead(LivingEntity livingEntity){
        return (livingEntity.getMobType() == MobType.UNDEAD || livingEntity.getType().is(ModTags.EntityTypes.NECRO_SET_NEUTRAL)) && livingEntity.getMaxHealth() <= ItemConfig.NamelessSetUndeadNeutralHealth.get() && !(livingEntity instanceof IOwned && !(livingEntity instanceof Enemy));
    }

    public static boolean hasUndeadCrown(LivingEntity livingEntity){
        return CuriosFinder.hasCurio(livingEntity, itemStack -> itemStack.getItem() instanceof NecroGarbs.NecroCrownItem)
                || (livingEntity instanceof AbstractNecromancer && MobsConfig.NecromancerSummonsLife.get());
    }

    public static boolean hasUndeadCape(LivingEntity livingEntity){
        return CuriosFinder.hasCurio(livingEntity, itemStack -> itemStack.getItem() instanceof NecroGarbs.NecroCapeItem)
                || livingEntity instanceof AbstractNecromancer;
    }

    public static boolean hasUndeadSet(LivingEntity livingEntity){
        return hasUndeadCrown(livingEntity) && hasUndeadCape(livingEntity);
    }

    public static boolean hasFrostRobes(LivingEntity livingEntity){
        return hasCurio(livingEntity, item -> item.getItem() instanceof FrostRobeItem);
    }

    public static boolean hasFrostCrown(LivingEntity livingEntity){
        return hasCurio(livingEntity, item -> item.getItem() instanceof MagicCrownItem crownItem && crownItem.spellType == SpellType.FROST);
    }

    public static boolean hasFrostSet(LivingEntity livingEntity){
        return hasFrostRobes(livingEntity)
                && hasFrostCrown(livingEntity);
    }

    public static boolean neutralFrostSet(LivingEntity livingEntity){
        return hasFrostSet(livingEntity) && ItemConfig.FrostSetMobNeutral.get();
    }

    public static boolean validFrostMob(LivingEntity livingEntity){
        return (livingEntity.getMobType() == ModMobType.FROST
                || livingEntity.getType().is(ModTags.EntityTypes.FROST_SET_NEUTRAL))
                && livingEntity.getMaxHealth() <= ItemConfig.FrostSetMobNeutralHealth.get()
                && !(livingEntity instanceof IOwned && !(livingEntity instanceof Enemy));
    }

    public static boolean hasWindyRobes(LivingEntity livingEntity){
        return hasCurio(livingEntity, item -> item.getItem() instanceof WindyRobeItem);
    }

    public static boolean hasWindRobes(LivingEntity livingEntity){
        return hasCurio(livingEntity, ModItems.WIND_ROBE.get());
    }

    public static boolean hasWindCrown(LivingEntity livingEntity){
        return hasCurio(livingEntity, item -> item.getItem() instanceof MagicCrownItem crownItem && crownItem.spellType == SpellType.WIND);
    }

    public static boolean hasWindSet(LivingEntity livingEntity){
        return hasWindRobes(livingEntity)
                && hasWindCrown(livingEntity);
    }

    public static boolean neutralWindSet(LivingEntity livingEntity){
        return hasWindSet(livingEntity) && ItemConfig.WindSetMobNeutral.get();
    }

    public static boolean validWindMob(LivingEntity livingEntity){
        return livingEntity.getType().is(ModTags.EntityTypes.WIND_SET_NEUTRAL)
                && livingEntity.getMaxHealth() <= ItemConfig.WindSetMobNeutralHealth.get()
                && !(livingEntity instanceof IOwned && !(livingEntity instanceof Enemy));
    }

    public static boolean hasStormRobes(LivingEntity livingEntity){
        return hasCurio(livingEntity, ModItems.STORM_ROBE.get());
    }

    public static boolean hasStormCrown(LivingEntity livingEntity){
        return hasCurio(livingEntity, item -> item.getItem() instanceof MagicCrownItem crownItem && crownItem.spellType == SpellType.STORM);
    }

    public static boolean hasStormSet(LivingEntity livingEntity){
        return hasStormRobes(livingEntity)
                && hasStormCrown(livingEntity);
    }

    public static boolean neutralStormSet(LivingEntity livingEntity){
        return hasStormSet(livingEntity) && ItemConfig.StormSetMobNeutral.get();
    }

    public static boolean validStormMob(LivingEntity livingEntity){
        return livingEntity.getType().is(ModTags.EntityTypes.STORM_SET_NEUTRAL)
                && livingEntity.getMaxHealth() <= ItemConfig.StormSetMobNeutralHealth.get()
                && !(livingEntity instanceof IOwned && !(livingEntity instanceof Enemy));
    }

    public static boolean hasAbyssCrown(LivingEntity livingEntity){
        return hasCurio(livingEntity, item -> item.getItem() instanceof MagicCrownItem crownItem && crownItem.spellType == SpellType.ABYSS);
    }

    public static boolean hasAbyssRobes(LivingEntity livingEntity){
        return hasCurio(livingEntity, item -> item.getItem() instanceof AbyssRobeItem);
    }

    public static boolean hasAbyssSet(LivingEntity livingEntity){
        return hasAbyssRobes(livingEntity)
                && hasAbyssCrown(livingEntity);
    }

    public static boolean neutralAbyssSet(LivingEntity livingEntity){
        return hasAbyssSet(livingEntity) && ItemConfig.AbyssSetMobNeutral.get();
    }

    public static boolean validAbyssMob(LivingEntity livingEntity){
        return (livingEntity.getMobType() == MobType.WATER
                || livingEntity.getType().is(ModTags.EntityTypes.ABYSS_SET_NEUTRAL))
                && livingEntity.getMaxHealth() <= ItemConfig.AbyssSetMobNeutralHealth.get()
                && !(livingEntity instanceof IOwned && !(livingEntity instanceof Enemy));
    }

    private static boolean isRing(ItemStack itemStack) {
        return itemStack.getItem() instanceof RingItem;
    }

    public static ItemStack findRing(Player playerEntity){
        ItemStack foundStack = ItemStack.EMPTY;
        if (CuriosLoaded.CURIOS.isLoaded()) {
            Optional<SlotResult> slotResult = CuriosApi.getCuriosInventory(playerEntity).map(inv -> inv.findFirstCurio(CuriosFinder::isRing))
                    .orElse(Optional.empty());
            if (slotResult.isPresent()) {
                foundStack = slotResult.get().stack();
            }
        } else {
            for (int i = 0; i < playerEntity.getInventory().getContainerSize(); i++) {
                ItemStack itemStack = playerEntity.getInventory().getItem(i);
                if (!itemStack.isEmpty() && isRing(itemStack)) {
                    foundStack = itemStack;
                    break;
                }
            }
        }

        return foundStack;
    }

    public static ItemStack findBrewInBag(Player player){
        ItemStack foundStack = ItemStack.EMPTY;
        if (!findBrewBag(player).isEmpty()){
            BrewBagItemHandler brewBagItemHandler = BrewBagItemHandler.get(findBrewBag(player));
            for (int i = 1; i < brewBagItemHandler.getSlots(); ++i){
                ItemStack itemStack = brewBagItemHandler.getStackInSlot(i);
                if (itemStack.getItem() instanceof ThrowableBrewItem){
                    foundStack = itemStack;
                }
            }
        }
        return foundStack;
    }

    public static int getBrewBagTotal(Player player){
        int num = 0;
        if (!findBrewBag(player).isEmpty()){
            BrewBagItemHandler brewBagItemHandler = BrewBagItemHandler.get(findBrewBag(player));
            for (int i = 1; i < brewBagItemHandler.getSlots(); ++i){
                ItemStack itemStack = brewBagItemHandler.getStackInSlot(i);
                if (itemStack.getItem() instanceof ThrowableBrewItem){
                    ++num;
                }
            }
        }
        return num;
    }

    public static boolean hasEmptyBrewBagSpace(Player player){
        return getBrewBagTotal(player) < 10;
    }

    public static boolean hasBrewInBag(Player player){
        return !findBrewInBag(player).isEmpty();
    }

    private static boolean isBrewBag(ItemStack itemStack) {
        return itemStack.getItem() == ModItems.BREW_BAG.get();
    }

    public static ItemStack findBrewBag(Player playerEntity) {
        ItemStack foundStack = ItemStack.EMPTY;
        if (CuriosLoaded.CURIOS.isLoaded()) {
            Optional<SlotResult> slotResult = CuriosApi.getCuriosInventory(playerEntity).map(inv -> inv.findFirstCurio(CuriosFinder::isBrewBag))
                    .orElse(Optional.empty());
            if (slotResult.isPresent()) {
                foundStack = slotResult.get().stack();
            }
        }
        for (int i = 0; i < playerEntity.getInventory().getContainerSize(); i++) {
            ItemStack itemStack = playerEntity.getInventory().getItem(i);
            if (!itemStack.isEmpty() && isBrewBag(itemStack)) {
                foundStack = itemStack;
                break;
            }
        }

        return foundStack;
    }
}
