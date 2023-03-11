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
import com.Polarice3.Goety.common.items.magic.TotemOfSouls;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.compat.minecolonies.MinecoloniesLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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

    public static BlockPos getArcaBlock(Player player){
        return getCapability(player).getArcaBlock();
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

    public static void decreaseSouls(Player player, int souls){
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
        int i = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SOUL_EATER.get(), weapon);
        if (i > 0) {
            multiply = Mth.clamp(i + 1, 1, 10);
        }
        return multiply;
    }

    public static void teleportDeathArca(Player player){
        ISoulEnergy soulEnergy = SEHelper.getCapability(player);
        BlockPos blockPos = SEHelper.getArcaBlock(player);
        BlockPos blockPos1 = new BlockPos(blockPos.getX() + 0.5F, blockPos.getY() + 0.5F, blockPos.getZ() + 0.5F);
        Vec3 vector3d = new Vec3(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ());
        if (soulEnergy.getArcaBlockDimension() == player.level.dimension()) {
            player.teleportTo(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ());
        } else {
            if (soulEnergy.getArcaBlockDimension() != null) {
                if (player.getServer() != null) {
                    ServerLevel serverWorld = player.getServer().getLevel(soulEnergy.getArcaBlockDimension());
                    if (serverWorld != null) {
                        player.changeDimension(serverWorld, new ArcaTeleporter(vector3d));
                        player.teleportTo(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ());
                    }
                }
            }
        }
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
        return tag;
    }

    public static ISoulEnergy load(CompoundTag tag, ISoulEnergy soulEnergy) {
        soulEnergy.setSEActive(tag.getBoolean("seActive"));
        soulEnergy.setArcaBlock(new BlockPos(tag.getInt("arcax"), tag.getInt("arcay"), tag.getInt("arcaz")));
        soulEnergy.setSoulEnergy(tag.getInt("soulEnergy"));
        soulEnergy.setArcaBlockDimension(Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, tag.get("dimension")).resultOrPartial(Goety.LOGGER::error).orElse(Level.OVERWORLD));
        return soulEnergy;
    }
}
