package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieVillagerServant;
import com.Polarice3.Goety.common.entities.hostile.BorderWraith;
import com.Polarice3.Goety.common.entities.hostile.Wraith;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;

public class ServantUtil {

    public static void convertZombies(Entity target, LivingEntity owner, boolean permanent){
        if (target instanceof Zombie zombieEntity && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(zombieEntity, ModEntityType.ZOMBIE_SERVANT.get(), (timer) -> {})) {
            EntityType<? extends Mob> entityType = ModEntityType.ZOMBIE_SERVANT.get();
            if (zombieEntity instanceof ZombieVillager){
                entityType = ModEntityType.ZOMBIE_VILLAGER_SERVANT.get();
            } else if (zombieEntity instanceof Husk){
                entityType = ModEntityType.HUSK_SERVANT.get();
            } else if (zombieEntity instanceof Drowned){
                entityType = ModEntityType.DROWNED_SERVANT.get();
            } else if (zombieEntity instanceof ZombifiedPiglin){
                entityType = ModEntityType.ZPIGLIN_SERVANT.get();
            }
            ZombieServant zombieServant = (ZombieServant) zombieEntity.convertTo(entityType, true);
            if (zombieServant != null) {
                if (owner != null) {
                    zombieServant.setTrueOwner(owner);
                }
                if (target.level instanceof ServerLevel serverLevel) {
                    zombieServant.finalizeSpawn(serverLevel, target.level.getCurrentDifficultyAt(zombieServant.blockPosition()), MobSpawnType.CONVERSION, null, null);
                }
                if (zombieEntity instanceof ZombieVillager villager && zombieServant instanceof ZombieVillagerServant servant){
                    servant.setVillagerData(villager.getVillagerData());
                    servant.setGossips(villager.gossips);
                    servant.setTradeOffers(villager.tradeOffers);
                    servant.setVillagerXp(villager.getVillagerXp());
                }
                if (!permanent) {
                    zombieServant.setLimitedLife(10 * (15 + target.level.random.nextInt(45)));
                }
                net.minecraftforge.event.ForgeEventFactory.onLivingConvert(zombieEntity, zombieServant);
                if (!zombieServant.isSilent()) {
                    zombieServant.level.levelEvent(null, 1026, zombieServant.blockPosition(), 0);
                }
            }
        }
    }

    public static void convertSkeletons(Entity target, LivingEntity owner, boolean wither, boolean permanent){
        if (target instanceof AbstractSkeleton skeleton && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(skeleton, ModEntityType.SKELETON_SERVANT.get(), (timer) -> {})) {
            EntityType<? extends Mob> entityType = ModEntityType.SKELETON_SERVANT.get();
            if (skeleton instanceof Stray){
                entityType = ModEntityType.STRAY_SERVANT.get();
            } else if (wither && skeleton instanceof WitherSkeleton){
                entityType = ModEntityType.WITHER_SKELETON_SERVANT.get();
            }
            AbstractSkeletonServant skeletonServant = (AbstractSkeletonServant) skeleton.convertTo(entityType, true);
            if (skeletonServant != null) {
                if (owner != null) {
                    skeletonServant.setTrueOwner(owner);
                }
                if (target.level instanceof ServerLevel serverLevel) {
                    skeletonServant.finalizeSpawn(serverLevel, target.level.getCurrentDifficultyAt(skeletonServant.blockPosition()), MobSpawnType.CONVERSION, null, null);
                }
                if (!permanent) {
                    skeletonServant.setLimitedLife(10 * (15 + target.level.random.nextInt(45)));
                }
                net.minecraftforge.event.ForgeEventFactory.onLivingConvert(skeleton, skeletonServant);
                if (!skeletonServant.isSilent()) {
                    skeletonServant.level.levelEvent(null, 1026, skeletonServant.blockPosition(), 0);
                }
            }
        }
    }

    public static void infect(Mob target, LivingEntity owner, boolean permanent, boolean keepLoot){
        Summoned summoned = null;
        if (target instanceof Wraith){
            summoned = target.convertTo(ModEntityType.WRAITH_SERVANT.get(), keepLoot);
        } else if (target instanceof BorderWraith){
            summoned = target.convertTo(ModEntityType.BORDER_WRAITH_SERVANT.get(), keepLoot);
        } else if (target instanceof PiglinBrute){
            summoned = target.convertTo(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), keepLoot);
        } else if (target instanceof AbstractPiglin){
            summoned = target.convertTo(ModEntityType.ZPIGLIN_SERVANT.get(), keepLoot);
        } else if (target instanceof Villager){
            summoned = target.convertTo(ModEntityType.ZOMBIE_VILLAGER_SERVANT.get(), keepLoot);
        } else if (target instanceof Vindicator){
            summoned = target.convertTo(ModEntityType.ZOMBIE_VINDICATOR_SERVANT.get(), keepLoot);
        } else if (target instanceof Pillager){
            summoned = target.convertTo(ModEntityType.SKELETON_PILLAGER_SERVANT.get(), keepLoot);
        }

        if (summoned != null) {
            EntityType<? extends LivingEntity> entityType = (EntityType<? extends LivingEntity>) summoned.getType();
            if (net.minecraftforge.event.ForgeEventFactory.canLivingConvert(target, entityType, (timer) -> {})) {
                if (target.level instanceof ServerLevel serverLevel) {
                    summoned.finalizeSpawn(serverLevel, target.level.getCurrentDifficultyAt(summoned.blockPosition()), MobSpawnType.CONVERSION, null, null);
                }
                if (!permanent) {
                    summoned.setLimitedLife(10 * (15 + target.level.random.nextInt(45)));
                }
                if (owner != null) {
                    summoned.setTrueOwner(owner);
                }
                if (target instanceof Villager villager && summoned instanceof ZombieVillagerServant servant){
                    servant.setVillagerData(villager.getVillagerData());
                    servant.setGossips(villager.getGossips().store(NbtOps.INSTANCE));
                    servant.setTradeOffers(villager.getOffers().createTag());
                    servant.setVillagerXp(villager.getVillagerXp());
                }
                net.minecraftforge.event.ForgeEventFactory.onLivingConvert(target, summoned);
                if (!summoned.isSilent()) {
                    summoned.level.levelEvent(null, 1026, summoned.blockPosition(), 0);
                }
            }
        }
    }
}
