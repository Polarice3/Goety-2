package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.illager.PillagerServant;
import com.Polarice3.Goety.common.entities.ally.illager.VindicatorServant;
import com.Polarice3.Goety.common.entities.ally.illager.raider.Prisoner;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieVillagerServant;
import com.Polarice3.Goety.common.entities.hostile.*;
import com.Polarice3.Goety.common.entities.neutral.ender.AbstractEnderling;
import com.Polarice3.Goety.common.events.ArcaTeleporter;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IExtensibleEnum;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

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
            } else if (zombieEntity instanceof Frayed){
                entityType = ModEntityType.FRAYED_SERVANT.get();
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
            } else if (skeleton instanceof Rattled){
                entityType = ModEntityType.RATTLED_SERVANT.get();
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

    public static void infect(LivingEntity target, LivingEntity owner, boolean permanent, boolean keepLoot) {
        if (target instanceof Mob mob) {
            infect(mob, owner, permanent, keepLoot);
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
        } else if (target instanceof Villager || target instanceof Prisoner || target.getType().getDescriptionId().contains("entity.guardvillagers.guard")){
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
                if (summoned instanceof ZombieVillagerServant servant) {
                    if (target instanceof Villager villager) {
                        servant.setVillagerData(villager.getVillagerData());
                        servant.setGossips(villager.getGossips().store(NbtOps.INSTANCE));
                        servant.setTradeOffers(villager.getOffers().createTag());
                        servant.setVillagerXp(villager.getVillagerXp());
                    } else if (target instanceof Prisoner prisoner) {
                        servant.setVillagerData(prisoner.getVillagerData());
                        if (prisoner.getGossips() != null) {
                            servant.setGossips(prisoner.getGossips());
                        }
                        if (prisoner.getOffers() != null) {
                            servant.setTradeOffers(prisoner.getOffers());
                        }
                        servant.setVillagerXp(prisoner.getVillagerXp());
                    }
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

    public static boolean isWindHeal(LivingEntity servant){
        EntityType<?> entityType = servant.getType();
        return entityType.is(ModTags.EntityTypes.WIND_HEAL);
    }

    public static boolean isStormHeal(LivingEntity servant){
        EntityType<?> entityType = servant.getType();
        return entityType.is(ModTags.EntityTypes.STORM_HEAL);
    }

    public static boolean isWildHeal(LivingEntity servant){
        MobType mobType = servant.getMobType();
        EntityType<?> entityType = servant.getType();
        return mobType == ModMobType.NATURAL || mobType == MobType.ARTHROPOD || entityType.is(ModTags.EntityTypes.WILD_HEAL);
    }

    public static boolean isGeoHeal(LivingEntity servant){
        EntityType<?> entityType = servant.getType();
        return entityType.is(ModTags.EntityTypes.GEO_HEAL);
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
        return isFrostHeal(livingEntity) || isWindHeal(livingEntity) || isStormHeal(livingEntity) || isWildHeal(livingEntity) || isGeoHeal(livingEntity) || isNecroHeal(livingEntity) || isNetherHeal(livingEntity) || isAbyssHeal(livingEntity) || isVoidHeal(livingEntity);
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
                        HealConfig config = HealType.getConfig(servant, owner);
                        if (config.curio) {
                            if (SEHelper.getSoulsAmount(owner, config.soulCost)) {
                                if (servant.tickCount % (MathHelper.secondsToTicks(config.healRate) + 1) == 0) {
                                    servant.heal(config.healAmount);
                                    Vec3 vector3d = servant.getDeltaMovement();
                                    if (servant.level instanceof ServerLevel serverWorld) {
                                        SEHelper.decreaseSouls(owner, config.soulCost);
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

    @Nullable
    public static Entity teleportToRevive(IOwned owned){
        if (owned instanceof LivingEntity livingOwned) {
            if (livingOwned.level instanceof ServerLevel serverLevel) {
                BlockPos blockPos = owned.getRevivePos();
                if (blockPos != null) {
                    if (owned.getReviveLevel() != null) {
                        Optional<Vec3> optional = RespawnAnchorBlock.findStandUpPosition(livingOwned.getType(), livingOwned.level, blockPos);
                        Vec3 vec3 = blockPos.getCenter();
                        if (optional.isPresent()) {
                            vec3 = optional.get();
                        }
                        if (serverLevel.dimension() != owned.getReviveLevel()) {
                            if (livingOwned.getServer() != null) {
                                ServerLevel newLevel = livingOwned.getServer().getLevel(owned.getReviveLevel());
                                if (newLevel != null) {
                                    Optional<Vec3> optional2 = RespawnAnchorBlock.findStandUpPosition(livingOwned.getType(), newLevel, blockPos);
                                    if (optional2.isPresent()) {
                                        Vec3 vec32 = optional2.get();
                                        Entity entity = livingOwned.changeDimension(newLevel, new ArcaTeleporter(vec32));
                                        if (entity != null) {
                                            if (newLevel.getWorldBorder().isWithinBounds(vec32.x, vec32.y, vec32.z)) {
                                                entity.teleportTo(vec32.x, vec32.y, vec32.z);
                                            } else {
                                                BlockPos blockPos2 = newLevel.getSharedSpawnPos();
                                                entity.teleportTo(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
                                            }
                                            return entity;
                                        }
                                    }
                                }
                            }
                        } else {
                            if (serverLevel.getWorldBorder().isWithinBounds(vec3.x, vec3.y, vec3.z)) {
                                livingOwned.teleportTo(vec3.x, vec3.y, vec3.z);
                            } else {
                                BlockPos blockPos2 = serverLevel.getSharedSpawnPos();
                                livingOwned.teleportTo(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
                            }
                            return livingOwned;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static InteractionResult equipServantArmor(Player player, Summoned summoned, ItemStack itemStack, InteractionResult failResult) {
        ItemStack helmet = summoned.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestplate = summoned.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legging = summoned.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = summoned.getItemBySlot(EquipmentSlot.FEET);
        if (itemStack.getItem() instanceof ArmorItem armor) {
            if (MobsConfig.RaiderServantWearArmor.get() || !(summoned instanceof RaiderServant)) {
                summoned.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                if (armor.getType() == ArmorItem.Type.HELMET) {
                    summoned.setItemSlot(EquipmentSlot.HEAD, itemStack.copyWithCount(1));
                    summoned.dropEquipment(EquipmentSlot.HEAD, helmet);
                    summoned.setGuaranteedDrop(EquipmentSlot.HEAD);
                }
                if (armor.getType() == ArmorItem.Type.CHESTPLATE) {
                    summoned.setItemSlot(EquipmentSlot.CHEST, itemStack.copyWithCount(1));
                    summoned.dropEquipment(EquipmentSlot.CHEST, chestplate);
                    summoned.setGuaranteedDrop(EquipmentSlot.CHEST);
                }
                if (armor.getType() == ArmorItem.Type.LEGGINGS) {
                    summoned.setItemSlot(EquipmentSlot.LEGS, itemStack.copyWithCount(1));
                    summoned.dropEquipment(EquipmentSlot.LEGS, legging);
                    summoned.setGuaranteedDrop(EquipmentSlot.LEGS);
                }
                if (armor.getType() == ArmorItem.Type.BOOTS) {
                    summoned.setItemSlot(EquipmentSlot.FEET, itemStack.copyWithCount(1));
                    summoned.dropEquipment(EquipmentSlot.FEET, boots);
                    summoned.setGuaranteedDrop(EquipmentSlot.FEET);
                }
                for (int i = 0; i < 7; ++i) {
                    double d0 = summoned.getRandom().nextGaussian() * 0.02D;
                    double d1 = summoned.getRandom().nextGaussian() * 0.02D;
                    double d2 = summoned.getRandom().nextGaussian() * 0.02D;
                    summoned.level.addParticle(ParticleTypes.HAPPY_VILLAGER, summoned.getRandomX(1.0D), summoned.getRandomY() + 0.5D, summoned.getRandomZ(1.0D), d0, d1, d2);
                }
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        } else if (itemStack.is(Items.CARVED_PUMPKIN) && MobsConfig.ServantsCanWearPumpkin.get()) {
            summoned.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
            summoned.setItemSlot(EquipmentSlot.HEAD, itemStack.copyWithCount(1));
            summoned.dropEquipment(EquipmentSlot.HEAD, helmet);
            summoned.setGuaranteedDrop(EquipmentSlot.HEAD);
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return failResult;
    }

    public static EquipmentSlot getClickedSlot(Mob mob, Vec3 vec3) {
        EquipmentSlot equipmentslot = EquipmentSlot.MAINHAND;
        boolean flag = mob.isBaby();
        double d0 = flag ? vec3.y * 2.0D : vec3.y;
        EquipmentSlot equipmentslot1 = EquipmentSlot.FEET;
        if (d0 >= 0.1D && d0 < 0.1D + (flag ? 0.8D : 0.45D) && mob.hasItemInSlot(equipmentslot1)) {
            equipmentslot = EquipmentSlot.FEET;
        } else if (d0 >= 0.9D + (flag ? 0.3D : 0.0D) && d0 < 0.9D + (flag ? 1.0D : 0.7D) && mob.hasItemInSlot(EquipmentSlot.CHEST)) {
            equipmentslot = EquipmentSlot.CHEST;
        } else if (d0 >= 0.4D && d0 < 0.4D + (flag ? 1.0D : 0.8D) && mob.hasItemInSlot(EquipmentSlot.LEGS)) {
            equipmentslot = EquipmentSlot.LEGS;
        } else if (d0 >= 1.6D && mob.hasItemInSlot(EquipmentSlot.HEAD)) {
            equipmentslot = EquipmentSlot.HEAD;
        } else if (!mob.hasItemInSlot(EquipmentSlot.MAINHAND) && mob.hasItemInSlot(EquipmentSlot.OFFHAND)) {
            equipmentslot = EquipmentSlot.OFFHAND;
        }

        return equipmentslot;
    }

    enum HealType implements IExtensibleEnum {
        ABYSS(
                ServantUtil::isAbyssHeal,
                CuriosFinder::hasAbyssRobes,
                MobsConfig.WaterMinionHeal.get(),
                MobsConfig.WaterMinionHealCost.get(),
                MobsConfig.WaterMinionHealTime.get(),
                MobsConfig.WaterMinionHealAmount.get()
        ),
        WILD(
                ServantUtil::isWildHeal,
                CuriosFinder::hasWildRobe,
                MobsConfig.NaturalMinionHeal.get(),
                MobsConfig.NaturalMinionHealCost.get(),
                MobsConfig.NaturalMinionHealTime.get(),
                MobsConfig.NaturalMinionHealAmount.get()
        ),
        GEO(
                ServantUtil::isGeoHeal,
                CuriosFinder::hasGeoRobe,
                MobsConfig.GeoMinionHeal.get(),
                MobsConfig.GeoMinionHealCost.get(),
                MobsConfig.GeoMinionHealTime.get(),
                MobsConfig.GeoMinionHealAmount.get()
        ),
        FROST(
                ServantUtil::isFrostHeal,
                CuriosFinder::hasFrostRobes,
                MobsConfig.FrostMinionHeal.get(),
                MobsConfig.FrostMinionHealCost.get(),
                MobsConfig.FrostMinionHealTime.get(),
                MobsConfig.FrostMinionHealAmount.get()
        ),
        WIND(
                ServantUtil::isWindHeal,
                CuriosFinder::hasWindRobes,
                MobsConfig.WindMinionHeal.get(),
                MobsConfig.WindMinionHealCost.get(),
                MobsConfig.WindMinionHealTime.get(),
                MobsConfig.WindMinionHealAmount.get()
        ),
        STORM(
                ServantUtil::isStormHeal,
                CuriosFinder::hasStormRobes,
                MobsConfig.StormMinionHeal.get(),
                MobsConfig.StormMinionHealCost.get(),
                MobsConfig.StormMinionHealTime.get(),
                MobsConfig.StormMinionHealAmount.get()
        ),
        NETHER(
                ServantUtil::isNetherHeal,
                CuriosFinder::hasNetherRobe,
                MobsConfig.NetherMinionHeal.get(),
                MobsConfig.NetherMinionHealCost.get(),
                MobsConfig.NetherMinionHealTime.get(),
                MobsConfig.NetherMinionHealAmount.get()
        ),
        VOID(
                ServantUtil::isVoidHeal,
                CuriosFinder::hasVoidRobe,
                MobsConfig.VoidMinionHeal.get(),
                MobsConfig.VoidMinionHealCost.get(),
                MobsConfig.VoidMinionHealTime.get(),
                MobsConfig.VoidMinionHealAmount.get()
        ),
        NECRO(
                ServantUtil::isNecroHeal,
                CuriosFinder::hasUndeadCape,
                MobsConfig.UndeadMinionHeal.get(),
                MobsConfig.UndeadMinionHealCost.get(),
                MobsConfig.UndeadMinionHealTime.get(),
                MobsConfig.UndeadMinionHealAmount.get()
        );

        private final Predicate<LivingEntity> healCheck;
        private final Predicate<LivingEntity> curioCheck;
        private final boolean healEnabled;
        private final int healCost;
        private final int healTime;
        private final double healAmount;

        HealType(Predicate<LivingEntity> healCheck, Predicate<LivingEntity> curioCheck,
                 boolean healEnabled, int healCost,
                 int healTime, double healAmount) {
            this.healCheck = healCheck;
            this.curioCheck = curioCheck;
            this.healEnabled = healEnabled;
            this.healCost = healCost;
            this.healTime = healTime;
            this.healAmount = healAmount;
        }

        public static HealType create(String name, Predicate<LivingEntity> healCheck, Predicate<LivingEntity> curioCheck,
                                      boolean healEnabled, int healCost,
                                      int healTime, double healAmount){
            throw new IllegalStateException("Enum not extended");
        }

        public static HealConfig getConfig(LivingEntity servant, LivingEntity owner) {
            for (HealType type : values()) {
                if (type.healCheck.test(servant) && type.healEnabled) {
                    if (type.curioCheck.test(owner)) {
                        return new HealConfig(
                                type.curioCheck.test(owner),
                                type.healCost,
                                type.healTime,
                                (float) type.healAmount
                        );
                    }
                }
            }
            return new HealConfig(false, 0, 0, 0.0F);
        }
    }

    public record HealConfig(boolean curio, int soulCost, int healRate, float healAmount) {}

}
