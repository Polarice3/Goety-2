package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.capabilities.soulenergy.ISoulEnergy;
import com.Polarice3.Goety.common.capabilities.soulenergy.SEImp;
import com.Polarice3.Goety.common.capabilities.soulenergy.SEProvider;
import com.Polarice3.Goety.common.capabilities.soulenergy.SEUpdatePacket;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.neutral.IOwned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.events.ArcaTeleporter;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.armor.DarkArmor;
import com.Polarice3.Goety.common.items.armor.ModArmorMaterials;
import com.Polarice3.Goety.common.items.magic.TotemOfSouls;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.research.Research;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.compat.minecolonies.MinecoloniesLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SEHelper {

    public static ISoulEnergy getCapability(Player player) {
        return player.getCapability(SEProvider.CAPABILITY).orElse(new SEImp());
    }

    public static boolean getSEActive(Player player) {
        return getCapability(player).getSEActive();
    }

    public static void setSEActive(Player player, boolean active){
        getCapability(player).setSEActive(active);
    }

    public static int getSESouls(Player player){
        return getCapability(player).getSoulEnergy();
    }

    public static void setSESouls(Player player, int souls){
        getCapability(player).setSoulEnergy(souls);
    }

    public static void setSoulsAmount(Player player, int souls){
        if (SEHelper.getSEActive(player)){
            SEHelper.setSESouls(player, souls);
            SEHelper.sendSEUpdatePacket(player);
        } else if (!TotemFinder.FindTotem(player).isEmpty()){
            TotemOfSouls.setSoulsamount(TotemFinder.FindTotem(player), souls);
        }
    }

    public static BlockPos getArcaBlock(Player player){
        return getCapability(player).getArcaBlock();
    }

    public static ResourceKey<Level> getArcaDimension(Player player){
        return getCapability(player).getArcaBlockDimension();
    }

    public static boolean decreaseSESouls(Player player, int souls){
        return getCapability(player).decreaseSE(souls);
    }

    public static boolean increaseSESouls(Player player, int souls){
        return getCapability(player).increaseSE(souls);
    }

    public static boolean getSoulsContainer(Player player){
        if(SEHelper.getSEActive(player)){
            return true;
        } else return !TotemFinder.FindTotem(player).isEmpty();
    }

    public static boolean getSoulsAmount(Player player, int souls){
        if (SEHelper.getSEActive(player) && SEHelper.getSESouls(player) > souls){
            return true;
        } else {
            return !TotemFinder.FindTotem(player).isEmpty() && TotemOfSouls.currentSouls(TotemFinder.FindTotem(player)) > souls;
        }
    }

    public static int getSoulAmountInt(Player player){
        if (SEHelper.getSEActive(player)){
            return SEHelper.getSESouls(player);
        } else if (!TotemFinder.FindTotem(player).isEmpty()){
            return TotemOfSouls.currentSouls(TotemFinder.FindTotem(player));
        }
        return 0;
    }

    public static int getSoulGiven(LivingEntity victim){
        if (victim != null){
            boolean flag = victim instanceof Owned && !(victim instanceof Enemy);
            if (!flag) {
                if (victim.getMobType() == MobType.UNDEAD) {
                    return MainConfig.UndeadSouls.get();
                } else if (victim.getMobType() == MobType.ARTHROPOD) {
                    return MainConfig.AnthropodSouls.get();
                } else if (victim instanceof Raider) {
                    return MainConfig.IllagerSouls.get();
                } else if (victim instanceof Villager && !victim.isBaby()) {
                    return MainConfig.VillagerSouls.get();
                } else if (victim instanceof AbstractPiglin || victim instanceof TamableAnimal) {
                    return MainConfig.PiglinSouls.get();
                } else if (victim instanceof EnderDragon) {
                    return MainConfig.EnderDragonSouls.get();
                } else if (victim instanceof Warden){
                    return MainConfig.WardenSouls.get();
                } else if (victim instanceof Player) {
                    return MainConfig.PlayerSouls.get();
                } else if (MinecoloniesLoaded.MINECOLONIES.isLoaded()
                        && victim.getType().getDescriptionId().contains("minecolonies")
                        && victim.getType().getCategory() != MobCategory.MISC){
                    return MainConfig.VillagerSouls.get();
                } else {
                    return MainConfig.DefaultSouls.get();
                }
            }
        }
        return 0;
    }

    public static void rawHandleKill(LivingEntity killer, LivingEntity victim, int soulEater) {
        Player player = null;
        if (killer instanceof Player){
            player = (Player) killer;
        } else if (killer instanceof IOwned summonedEntity){
            if (summonedEntity.getTrueOwner() instanceof Player){
                player = (Player) summonedEntity.getTrueOwner();
            }
        }
        if (player != null) {
            if (killer.getMainHandItem().getItem() == ModItems.FANGED_DAGGER.get()){
                soulEater *= 1.5;
            }
            increaseSouls(player, getSoulGiven(victim) * soulEater);
        }
    }

    public static void handleKill(LivingEntity killer, LivingEntity victim) {
        SEHelper.rawHandleKill(killer, victim, SEHelper.SoulMultiply(killer));
    }

    public static void increaseSouls(Player player, int souls){
        if (getSEActive(player)) {
            increaseSESouls(player, souls);
            SEHelper.sendSEUpdatePacket(player);
        } else {
            ItemStack foundStack = TotemFinder.FindTotem(player);
            if (foundStack != null){
                TotemOfSouls.increaseSouls(foundStack, souls);
            }
        }
    }

    public static float soulDiscount(LivingEntity living){
        float init = 1.0F;
        for (ItemStack itemStack : living.getArmorSlots()){
            if (itemStack.getItem() instanceof ArmorItem armorItem){
                if (armorItem.getMaterial() == ModArmorMaterials.DARK){
                    init -= (DarkArmor.getSoulDiscount() / 100.0F);
                }
            }
        }

        return init;
    }

    public static void decreaseSouls(Player player, int souls){
        souls *= soulDiscount(player);
        if (getSEActive(player)) {
            decreaseSESouls(player, souls);
            SEHelper.sendSEUpdatePacket(player);
        } else {
            ItemStack foundStack = TotemFinder.FindTotem(player);
            if (foundStack != null){
                TotemOfSouls.decreaseSouls(foundStack, souls);
            }
        }
    }

    public static int SoulMultiply(LivingEntity livingEntity){
        ItemStack weapon= livingEntity.getMainHandItem();
        int multiply = 1;
        int i = weapon.getEnchantmentLevel(ModEnchantments.SOUL_EATER.get());
        if (i > 0) {
            multiply = Mth.clamp(i + 1, 1, 10);
        }
        return multiply;
    }

    public static boolean teleportToArca(Player player){
        ISoulEnergy soulEnergy = SEHelper.getCapability(player);
        BlockPos blockPos = SEHelper.getArcaBlock(player);
        BlockPos blockPos1 = new BlockPos(blockPos.getX() + 0.5F, blockPos.getY() + 0.5F, blockPos.getZ() + 0.5F);
        if (soulEnergy.getArcaBlockDimension() == player.level.dimension()) {
            Optional<Vec3> optional = RespawnAnchorBlock.findStandUpPosition(EntityType.PLAYER, player.level, blockPos1);
            if (optional.isPresent()) {
                player.teleportTo(optional.get().x, optional.get().y, optional.get().z);
                return true;
            }
        } else {
            if (soulEnergy.getArcaBlockDimension() != null) {
                if (player.getServer() != null) {
                    ServerLevel serverWorld = player.getServer().getLevel(soulEnergy.getArcaBlockDimension());
                    if (serverWorld != null) {
                        Optional<Vec3> optional = RespawnAnchorBlock.findStandUpPosition(EntityType.PLAYER, serverWorld, blockPos1);
                        if (optional.isPresent()) {
                            player.changeDimension(serverWorld, new ArcaTeleporter(optional.get()));
                            player.teleportTo(optional.get().x, optional.get().y, optional.get().z);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean addGrudgeEntity(Player owner, LivingEntity target){
        if (target != owner) {
            if (!getGrudgeEntities(owner).contains(target)) {
                getCapability(owner).addGrudge(target.getUUID());
                return true;
            }
        }
        return false;
    }

    public static boolean removeGrudgeEntity(Player owner, LivingEntity target){
        if (target != owner) {
            if (getGrudgeEntities(owner).contains(target)) {
                getCapability(owner).removeGrudge(target.getUUID());
                return true;
            }
        }
        return false;
    }

    public static List<LivingEntity> getGrudgeEntities(Player owner){
        List<LivingEntity> livingEntities = new ArrayList<>();
        if (!getCapability(owner).grudgeList().isEmpty()){
            for (UUID uuid : getCapability(owner).grudgeList()){
                Entity entity = EntityFinder.getLivingEntityByUuiD(uuid);
                if (entity instanceof LivingEntity target && !livingEntities.contains(target) && target != owner){
                    livingEntities.add(target);
                }
            }
        }
        return livingEntities;
    }

    public static boolean addGrudgeEntityType(Player owner, EntityType<?> target){
        if (!getGrudgeEntityTypes(owner).contains(target)) {
            getCapability(owner).addGrudgeType(target);
            return true;
        }
        return false;
    }

    public static boolean removeGrudgeEntityType(Player owner, EntityType<?> target){
        if (getGrudgeEntityTypes(owner).contains(target)) {
            getCapability(owner).removeGrudgeType(target);
            return true;
        }
        return false;
    }

    public static List<EntityType<?>> getGrudgeEntityTypes(Player owner){
        List<EntityType<?>> entityTypes = new ArrayList<>();
        if (!getCapability(owner).grudgeTypeList().isEmpty()){
            for (EntityType<?> entityType : getCapability(owner).grudgeTypeList()){
                if (!entityTypes.contains(entityType)){
                    entityTypes.add(entityType);
                }
            }
        }
        return entityTypes;
    }

    public static boolean addSummon(Player owner, LivingEntity target){
        if (target != owner) {
            if (!getSummons(owner).contains(target)) {
                getCapability(owner).addSummon(target.getUUID());
                return true;
            }
        }
        return false;
    }

    public static boolean removeSummon(Player owner, LivingEntity target){
        if (target != owner) {
            if (getSummons(owner).contains(target)) {
                getCapability(owner).removeSummon(target.getUUID());
                return true;
            }
        }
        return false;
    }

    public static List<LivingEntity> getSummons(Player owner){
        List<LivingEntity> livingEntities = new ArrayList<>();
        if (!getCapability(owner).summonList().isEmpty()){
            for (UUID uuid : getCapability(owner).summonList()){
                Entity entity = EntityFinder.getLivingEntityByUuiD(uuid);
                if (entity instanceof LivingEntity target && !livingEntities.contains(target) && target != owner){
                    livingEntities.add(target);
                }
            }
        }
        return livingEntities;
    }

    public static List<LivingEntity> getSpecificSummons(Player owner, EntityType<?> entityType){
        List<LivingEntity> livingEntities = new ArrayList<>();
        if (!getSummons(owner).isEmpty()){
            for (LivingEntity livingEntity : getSummons(owner)){
                if (livingEntity.getType() == entityType){
                    livingEntities.add(livingEntity);
                }
            }
        }
        return livingEntities;
    }

    public static List<LivingEntity> getSpecificSummons(Player owner, Class<?> aClass){
        List<LivingEntity> livingEntities = new ArrayList<>();
        if (!getSummons(owner).isEmpty()){
            for (LivingEntity livingEntity : getSummons(owner)){
                if (livingEntity.getClass() == aClass){
                    livingEntities.add(livingEntity);
                }
            }
        }
        return livingEntities;
    }

    public static boolean addResearch(Player player, Research research){
        if (!getResearch(player).contains(research)) {
            getCapability(player).addResearch(research);
            return true;
        }
        return false;
    }

    public static boolean removeResearch(Player player, Research research){
        if (getResearch(player).contains(research)) {
            getCapability(player).removeResearch(research);
            return true;
        }
        return false;
    }

    public static List<Research> getResearch(Player player){
        List<Research> research = new ArrayList<>();
        if (!getCapability(player).getResearch().isEmpty()){
            research.addAll(getCapability(player).getResearch());
        }
        return research;
    }

    public static boolean hasResearch(Player player, Research research){
        return getResearch(player).contains(research);
    }

    public static void sendSEUpdatePacket(Player player) {
        if (!player.level.isClientSide()) {
            ModNetwork.sendTo(player, new SEUpdatePacket(player));
        }
    }

    public static CompoundTag save(CompoundTag tag, ISoulEnergy soulEnergy) {
        tag.putBoolean("seActive", soulEnergy.getSEActive());
        tag.putInt("soulEnergy", soulEnergy.getSoulEnergy());
        if (soulEnergy.getArcaBlock() != null) {
            tag.putInt("arcax", soulEnergy.getArcaBlock().getX());
            tag.putInt("arcay", soulEnergy.getArcaBlock().getY());
            tag.putInt("arcaz", soulEnergy.getArcaBlock().getZ());
            ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, soulEnergy.getArcaBlockDimension().location()).resultOrPartial(Goety.LOGGER::error).ifPresent(
                    (p_241148_1_) -> tag.put("dimension", p_241148_1_));
        }

        if (soulEnergy.grudgeList() != null) {
            ListTag listTag = new ListTag();
            if (!soulEnergy.grudgeList().isEmpty()) {
                for (UUID uuid : soulEnergy.grudgeList()) {
                    listTag.add(NbtUtils.createUUID(uuid));
                }
                tag.put("grudgeList", listTag);
            }
        }

        if (soulEnergy.grudgeTypeList() != null){
            ListTag listTag = new ListTag();
            if (!soulEnergy.grudgeTypeList().isEmpty()) {
                for (EntityType<?> entityType : soulEnergy.grudgeTypeList()){
                    CompoundTag compoundTag = new CompoundTag();
                    compoundTag.putString("id", EntityType.getKey(entityType).toString());
                    listTag.add(compoundTag);
                }
                tag.put("grudgeTypeList", listTag);
            }
        }

        if (soulEnergy.getResearch() != null){
            ListTag listTag = new ListTag();
            if (!soulEnergy.getResearch().isEmpty()){
                for (Research research : soulEnergy.getResearch()){
                    CompoundTag compoundTag = new CompoundTag();
                    compoundTag.putString("research", research.getId());
                    listTag.add(compoundTag);
                }
                tag.put("researchList", listTag);
            }
        }

        if (soulEnergy.summonList() != null){
            ListTag listTag = new ListTag();
            if (!soulEnergy.summonList().isEmpty()) {
                for (UUID uuid : soulEnergy.summonList()) {
                    listTag.add(NbtUtils.createUUID(uuid));
                }
                tag.put("summonList", listTag);
            }
        }
        return tag;
    }

    public static ISoulEnergy load(CompoundTag tag, ISoulEnergy soulEnergy) {
        soulEnergy.setSEActive(tag.getBoolean("seActive"));
        soulEnergy.setArcaBlock(new BlockPos(tag.getInt("arcax"), tag.getInt("arcay"), tag.getInt("arcaz")));
        soulEnergy.setSoulEnergy(tag.getInt("soulEnergy"));
        soulEnergy.setArcaBlockDimension(Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, tag.get("dimension")).resultOrPartial(Goety.LOGGER::error).orElse(Level.OVERWORLD));
        if (tag.contains("grudgeList", 9)) {
            ListTag listtag = tag.getList("grudgeList", 11);
            for (net.minecraft.nbt.Tag value : listtag) {
                soulEnergy.addGrudge(NbtUtils.loadUUID(value));
            }
        }
        if (tag.contains("grudgeTypeList", Tag.TAG_LIST)) {
            ListTag listtag = tag.getList("grudgeTypeList", Tag.TAG_COMPOUND);
            for (int i = 0; i < listtag.size(); ++i) {
                String string = listtag.getCompound(i).getString("id");
                if (EntityType.byString(string).isPresent()){
                    soulEnergy.addGrudgeType(EntityType.byString(string).get());
                }
            }
        }
        if (tag.contains("researchList", Tag.TAG_LIST)) {
            ListTag listtag = tag.getList("researchList", Tag.TAG_COMPOUND);
            for (int i = 0; i < listtag.size(); ++i) {
                String string = listtag.getCompound(i).getString("research");
                if (ResearchList.getResearch(string) != null) {
                    soulEnergy.addResearch(ResearchList.getResearch(string));
                }
            }
        }
        if (tag.contains("summonList", Tag.TAG_LIST)) {
            ListTag listtag = tag.getList("summonList", Tag.TAG_INT_ARRAY);
            for (net.minecraft.nbt.Tag value : listtag) {
                soulEnergy.addSummon(NbtUtils.loadUUID(value));
            }
        }
        return soulEnergy;
    }
}
