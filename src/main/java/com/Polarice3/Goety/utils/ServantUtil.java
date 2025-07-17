package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.illager.PillagerServant;
import com.Polarice3.Goety.common.entities.ally.illager.VindicatorServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieVillagerServant;
import com.Polarice3.Goety.common.entities.hostile.BorderWraith;
import com.Polarice3.Goety.common.entities.hostile.MuckWraith;
import com.Polarice3.Goety.common.entities.hostile.Wraith;
import com.Polarice3.Goety.common.entities.neutral.ender.AbstractEnderling;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

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
        } else if (target instanceof MuckWraith){
            summoned = target.convertTo(ModEntityType.MUCK_WRAITH_SERVANT.get(), keepLoot);
        } else if (target instanceof PiglinBrute){
            summoned = target.convertTo(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), keepLoot);
        } else if (target instanceof AbstractPiglin){
            summoned = target.convertTo(ModEntityType.ZPIGLIN_SERVANT.get(), keepLoot);
        } else if (target instanceof Villager){
            summoned = target.convertTo(ModEntityType.ZOMBIE_VILLAGER_SERVANT.get(), keepLoot);
        } else if (target instanceof Vindicator || target instanceof VindicatorServant){
            summoned = target.convertTo(ModEntityType.ZOMBIE_VINDICATOR_SERVANT.get(), keepLoot);
        } else if (target instanceof Pillager || target instanceof PillagerServant){
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

    public static boolean isFrostHeal(LivingEntity servant){
        MobType mobType = servant.getMobType();
        EntityType<?> entityType = servant.getType();
        return mobType == ModMobType.FROST || entityType.is(ModTags.EntityTypes.FROST_HEAL);
    }

    public static boolean isWildHeal(LivingEntity servant){
        MobType mobType = servant.getMobType();
        EntityType<?> entityType = servant.getType();
        return mobType == ModMobType.NATURAL || mobType == MobType.ARTHROPOD || entityType.is(ModTags.EntityTypes.WILD_HEAL);
    }

    public static boolean isNetherHeal(LivingEntity servant){
        MobType mobType = servant.getMobType();
        EntityType<?> entityType = servant.getType();
        return mobType == ModMobType.NETHER || entityType.is(ModTags.EntityTypes.NETHER_HEAL);
    }

    public static boolean isNecroHeal(LivingEntity servant){
        MobType mobType = servant.getMobType();
        EntityType<?> entityType = servant.getType();
        return mobType == MobType.UNDEAD || entityType.is(ModTags.EntityTypes.NECRO_HEAL);
    }

    public static boolean isAbyssHeal(LivingEntity servant){
        MobType mobType = servant.getMobType();
        EntityType<?> entityType = servant.getType();
        return mobType == MobType.WATER || entityType.is(ModTags.EntityTypes.ABYSS_HEAL);
    }

    public static boolean isVoidHeal(LivingEntity servant){
        EntityType<?> entityType = servant.getType();
        return servant instanceof AbstractEnderling || entityType.is(ModTags.EntityTypes.VOID_HEAL);
    }

    public static boolean isValidServantHeal(LivingEntity livingEntity){
        return isFrostHeal(livingEntity) || isWildHeal(livingEntity) || isNecroHeal(livingEntity) || isNetherHeal(livingEntity) || isAbyssHeal(livingEntity) || isVoidHeal(livingEntity);
    }

    public static boolean notServantButOwned(LivingEntity livingEntity){
        return livingEntity instanceof OwnableEntity && !(livingEntity instanceof IServant) && isValidServantHeal(livingEntity);
    }

    public static void healServant(LivingEntity owner, LivingEntity servant){
        if (owner instanceof Player player){
            healServant(player, servant);
        }
    }

    public static void healServant(Player owner, LivingEntity servant){
        if (!servant.level.isClientSide) {
            if (!servant.getType().is(ModTags.EntityTypes.NO_HEAL_SERVANTS)) {
                if (!servant.isOnFire() && !servant.isDeadOrDying()) {
                    if (servant.getHealth() < servant.getMaxHealth()) {
                        boolean curio = false;
                        int soulCost = 0;
                        int healRate = 0;
                        float healAmount = 0;
                        if (isNecroHeal(servant) && MobsConfig.UndeadMinionHeal.get()) {
                            curio = CuriosFinder.hasUndeadCape(owner);
                            soulCost = MobsConfig.UndeadMinionHealCost.get();
                            healRate = MobsConfig.UndeadMinionHealTime.get();
                            healAmount = MobsConfig.UndeadMinionHealAmount.get().floatValue();
                        }
                        if (isAbyssHeal(servant) && MobsConfig.WaterMinionHeal.get()) {
                            curio = CuriosFinder.hasAbyssRobes(owner);
                            soulCost = MobsConfig.WaterMinionHealCost.get();
                            healRate = MobsConfig.WaterMinionHealTime.get();
                            healAmount = MobsConfig.WaterMinionHealAmount.get().floatValue();
                        }
                        if (isWildHeal(servant) && MobsConfig.NaturalMinionHeal.get()) {
                            curio = CuriosFinder.hasWildRobe(owner);
                            soulCost = MobsConfig.NaturalMinionHealCost.get();
                            healRate = MobsConfig.NaturalMinionHealTime.get();
                            healAmount = MobsConfig.NaturalMinionHealAmount.get().floatValue();
                        }
                        if (isFrostHeal(servant) && MobsConfig.FrostMinionHeal.get()) {
                            curio = CuriosFinder.hasFrostRobes(owner);
                            soulCost = MobsConfig.FrostMinionHealCost.get();
                            healRate = MobsConfig.FrostMinionHealTime.get();
                            healAmount = MobsConfig.FrostMinionHealAmount.get().floatValue();
                        }
                        if (isNetherHeal(servant) && MobsConfig.NetherMinionHeal.get()) {
                            curio = CuriosFinder.hasNetherRobe(owner);
                            soulCost = MobsConfig.NetherMinionHealCost.get();
                            healRate = MobsConfig.NetherMinionHealTime.get();
                            healAmount = MobsConfig.NetherMinionHealAmount.get().floatValue();
                        }
                        if (isVoidHeal(servant) && MobsConfig.VoidMinionHeal.get()) {
                            curio = CuriosFinder.hasVoidRobe(owner);
                            soulCost = MobsConfig.VoidMinionHealCost.get();
                            healRate = MobsConfig.VoidMinionHealTime.get();
                            healAmount = MobsConfig.VoidMinionHealAmount.get().floatValue();
                        }
                        if (curio) {
                            if (SEHelper.getSoulsAmount(owner, soulCost)) {
                                if (servant.tickCount % (MathHelper.secondsToTicks(healRate) + 1) == 0) {
                                    servant.heal(healAmount);
                                    Vec3 vector3d = servant.getDeltaMovement();
                                    if (servant.level instanceof ServerLevel serverWorld) {
                                        SEHelper.decreaseSouls(owner, soulCost);
                                        serverWorld.sendParticles(ParticleTypes.SCULK_SOUL, servant.getRandomX(0.5D), servant.getRandomY(), servant.getRandomZ(0.5D), 0, vector3d.x * -0.2D, 0.1D, vector3d.z * -0.2D, 0.5F);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
