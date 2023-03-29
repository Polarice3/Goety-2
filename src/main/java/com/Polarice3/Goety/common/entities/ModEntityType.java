package com.Polarice3.Goety.common.entities;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.*;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.common.entities.hostile.Irk;
import com.Polarice3.Goety.common.entities.hostile.Wraith;
import com.Polarice3.Goety.common.entities.hostile.servants.Malghast;
import com.Polarice3.Goety.common.entities.hostile.servants.SkeletonVillagerServant;
import com.Polarice3.Goety.common.entities.hostile.servants.ZombieVillagerServant;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinBruteServant;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.Polarice3.Goety.common.entities.projectiles.*;
import com.Polarice3.Goety.common.entities.util.*;
import com.Polarice3.Goety.common.entities.vehicle.ModBoat;
import com.Polarice3.Goety.common.entities.vehicle.ModChestBoat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityType {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Goety.MOD_ID);

    public static final RegistryObject<EntityType<NetherMeteor>> NETHER_METEOR = register("nether_meteor",
            EntityType.Builder.<NetherMeteor>of(NetherMeteor::new, MobCategory.MISC)
                    .sized(1.0f,1.0f)
                    .clientTrackingRange(4));

    public static final RegistryObject<EntityType<SwordProjectile>> SWORD = register("sword",
            EntityType.Builder.<SwordProjectile>of(SwordProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4));

    public static final RegistryObject<EntityType<ScytheSlash>> SCYTHE = register("scythe",
            EntityType.Builder.<ScytheSlash>of(ScytheSlash::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4));

    public static final RegistryObject<EntityType<GrandLavaball>> GRAND_LAVABALL = register("grand_lavaball",
            EntityType.Builder.<GrandLavaball>of(GrandLavaball::new, MobCategory.MISC)
                    .sized(1.0f,1.0f)
                    .clientTrackingRange(4));

    public static final RegistryObject<EntityType<SoulLight>> SOUL_LIGHT = register("soul_light",
            EntityType.Builder.<SoulLight>of(SoulLight::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<GlowLight>> GLOW_LIGHT = register("glow_light",
            EntityType.Builder.<GlowLight>of(GlowLight::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<SoulBullet>> SOUL_BULLET = register("soul_bullet",
            EntityType.Builder.<SoulBullet>of(SoulBullet::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<SoulBolt>> SOUL_BOLT = register("soul_bolt",
            EntityType.Builder.<SoulBolt>of(SoulBolt::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<Fangs>> FANG = register("fang",
            EntityType.Builder.<Fangs>of(Fangs::new, MobCategory.MISC)
                    .sized(0.5F, 0.8F)
                    .clientTrackingRange(6)
                    .updateInterval(2));

    public static final RegistryObject<EntityType<Spike>> SPIKE = register("spike",
            EntityType.Builder.<Spike>of(Spike::new, MobCategory.MISC)
                    .sized(0.5F, 0.8F)
                    .clientTrackingRange(6)
                    .updateInterval(2));

    public static final RegistryObject<EntityType<IceBouquet>> ICE_BOUQUET = register("ice_bouquet",
            EntityType.Builder.<IceBouquet>of(IceBouquet::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(0.8F, 1.0F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<IceChunk>> ICE_CHUNK = register("ice_chunk",
            EntityType.Builder.<IceChunk>of(IceChunk::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 1.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<FireTornado>> FIRE_TORNADO = register("fire_tornado",
            EntityType.Builder.<FireTornado>of(FireTornado::new, MobCategory.MISC)
                    .sized(2.0F, 2.0F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<ModBoat>> MOD_BOAT = register("boat",
            EntityType.Builder.<ModBoat>of(ModBoat::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<ModChestBoat>> MOD_CHEST_BOAT = register("chest_boat",
            EntityType.Builder.<ModChestBoat>of(ModChestBoat::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<Apostle>> APOSTLE = register("apostle",
            EntityType.Builder.of(Apostle::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .fireImmune()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<ZombieVillagerServant>> ZOMBIE_VILLAGER_SERVANT = register("zombie_villager_servant",
            EntityType.Builder.of(ZombieVillagerServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<SkeletonVillagerServant>> SKELETON_VILLAGER_SERVANT = register("skeleton_villager_servant",
            EntityType.Builder.of(SkeletonVillagerServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<ZPiglinServant>> ZPIGLIN_SERVANT = register("zpiglin_servant",
            EntityType.Builder.of(ZPiglinServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .fireImmune()
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<ZPiglinBruteServant>> ZPIGLIN_BRUTE_SERVANT = register("zpiglin_brute_servant",
            EntityType.Builder.of(ZPiglinBruteServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .fireImmune()
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<Malghast>> MALGHAST = register("malghast",
            EntityType.Builder.of(Malghast::new, MobCategory.MONSTER)
                    .sized(2.0F, 2.0F)
                    .fireImmune()
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<Wraith>> WRAITH = register("wraith",
            EntityType.Builder.of(Wraith::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<AllyVex>> ALLY_VEX = register("ally_vex",
            EntityType.Builder.of(AllyVex::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(0.4F, 0.8F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<ZombieServant>> ZOMBIE_SERVANT = register("zombie_servant",
            EntityType.Builder.of(ZombieServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<HuskServant>> HUSK_SERVANT = register("husk_servant",
            EntityType.Builder.of(HuskServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<DrownedServant>> DROWNED_SERVANT = register("drowned_servant",
            EntityType.Builder.of(DrownedServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<SkeletonServant>> SKELETON_SERVANT = register("skeleton_servant",
            EntityType.Builder.of(SkeletonServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<StrayServant>> STRAY_SERVANT = register("stray_servant",
            EntityType.Builder.of(StrayServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<WraithServant>> WRAITH_SERVANT = register("wraith_servant",
            EntityType.Builder.of(WraithServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<HauntedSkull>> HAUNTED_SKULL = register("haunted_skull",
            EntityType.Builder.of(HauntedSkull::new, MobCategory.MONSTER)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .fireImmune());

    public static final RegistryObject<EntityType<Doppelganger>> DOPPELGANGER = register("doppelganger",
            EntityType.Builder.of(Doppelganger::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .fireImmune());

    public static final RegistryObject<EntityType<Vizier>> VIZIER = register("vizier",
            EntityType.Builder.of(Vizier::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<Irk>> IRK = register("irk",
            EntityType.Builder.of(Irk::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(0.4F, 0.8F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<LightningTrap>> LIGHTNING_TRAP = register("lightning_trap",
            EntityType.Builder.<LightningTrap>of(LightningTrap::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final RegistryObject<EntityType<FireRainTrap>> FIRE_RAIN_TRAP = register("fire_rain_trap",
            EntityType.Builder.<FireRainTrap>of(FireRainTrap::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final RegistryObject<EntityType<ArrowRainTrap>> ARROW_RAIN_TRAP = register("arrow_rain_trap",
            EntityType.Builder.<ArrowRainTrap>of(ArrowRainTrap::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final RegistryObject<EntityType<FireTornadoTrap>> FIRE_TORNADO_TRAP = register("fire_tornado_trap",
            EntityType.Builder.<FireTornadoTrap>of(FireTornadoTrap::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final RegistryObject<EntityType<FireBlastTrap>> FIRE_BLAST_TRAP = register("fire_blast_trap",
            EntityType.Builder.<FireBlastTrap>of(FireBlastTrap::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final RegistryObject<EntityType<SummonCircleEntity>> SUMMON_CIRCLE = register("summon_circle",
            EntityType.Builder.<SummonCircleEntity>of(SummonCircleEntity::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<StormEntity>> STORM_UTIL = register("storm_util",
            EntityType.Builder.of(StormEntity::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final RegistryObject<EntityType<SummonApostle>> SUMMON_APOSTLE = register("summon_apostle",
            EntityType.Builder.<SummonApostle>of(SummonApostle::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String p_20635_, EntityType.Builder<T> p_20636_) {
        return ENTITY_TYPE.register(p_20635_, () -> p_20636_.build(Goety.location(p_20635_).toString()));
    }
}
