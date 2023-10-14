package com.Polarice3.Goety.common.entities;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.*;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.common.entities.deco.HauntedArmorStand;
import com.Polarice3.Goety.common.entities.hostile.*;
import com.Polarice3.Goety.common.entities.hostile.cultists.Crone;
import com.Polarice3.Goety.common.entities.hostile.cultists.Warlock;
import com.Polarice3.Goety.common.entities.hostile.illagers.*;
import com.Polarice3.Goety.common.entities.hostile.servants.Malghast;
import com.Polarice3.Goety.common.entities.hostile.servants.ObsidianMonolith;
import com.Polarice3.Goety.common.entities.hostile.servants.SkeletonVillagerServant;
import com.Polarice3.Goety.common.entities.hostile.servants.ZombieVillagerServant;
import com.Polarice3.Goety.common.entities.neutral.*;
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

    public static final RegistryObject<EntityType<ModFireball>> MOD_FIREBALL = register("fireball",
            EntityType.Builder.<ModFireball>of(ModFireball::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4));

    public static final RegistryObject<EntityType<Lavaball>> LAVABALL = register("lavaball",
            EntityType.Builder.<Lavaball>of(Lavaball::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4));

    public static final RegistryObject<EntityType<SwordProjectile>> SWORD = register("sword",
            EntityType.Builder.<SwordProjectile>of(SwordProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4));

    public static final RegistryObject<EntityType<IceSpike>> ICE_SPIKE = register("ice_spike",
            EntityType.Builder.<IceSpike>of(IceSpike::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4));

    public static final RegistryObject<EntityType<DeathArrow>> DEATH_ARROW = register("death_arrow",
            EntityType.Builder.<DeathArrow>of(DeathArrow::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20));

    public static final RegistryObject<EntityType<ThrownBrew>> BREW = register("brew",
            EntityType.Builder.<ThrownBrew>of(ThrownBrew::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10));

    public static final RegistryObject<EntityType<ScytheSlash>> SCYTHE = register("scythe",
            EntityType.Builder.<ScytheSlash>of(ScytheSlash::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4));

    public static final RegistryObject<EntityType<GrandLavaball>> GRAND_LAVABALL = register("grand_lavaball",
            EntityType.Builder.<GrandLavaball>of(GrandLavaball::new, MobCategory.MISC)
                    .sized(1.0f,1.0f)
                    .clientTrackingRange(4));

    public static final RegistryObject<EntityType<HauntedSkullProjectile>> HAUNTED_SKULL_SHOT = register("haunted_skull_shot",
            EntityType.Builder.<HauntedSkullProjectile>of(HauntedSkullProjectile::new, MobCategory.MISC)
                    .sized(0.5f,0.5f)
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

    public static final RegistryObject<EntityType<NecroBolt>> NECRO_BOLT = register("necro_bolt",
            EntityType.Builder.<NecroBolt>of(NecroBolt::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<MagicBolt>> MAGIC_BOLT = register("magic_bolt",
            EntityType.Builder.<MagicBolt>of(MagicBolt::new, MobCategory.MISC)
                    .sized(0.625F, 0.625F)
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

    public static final RegistryObject<EntityType<IllBomb>> ILL_BOMB = register("ill_bomb",
            EntityType.Builder.<IllBomb>of(IllBomb::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4));

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

    public static final RegistryObject<EntityType<ViciousTooth>> VICIOUS_TOOTH = register("vicious_tooth",
            EntityType.Builder.<ViciousTooth>of(ViciousTooth::new, MobCategory.MISC)
                    .sized(0.6F, 1.4F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<ViciousPike>> VICIOUS_PIKE = register("vicious_pike",
            EntityType.Builder.<ViciousPike>of(ViciousPike::new, MobCategory.MISC)
                    .sized(0.6F, 3.0F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<CorruptedBeam>> CORRUPTED_BEAM = register("corrupted_beam",
            EntityType.Builder.<CorruptedBeam>of(CorruptedBeam::new, MobCategory.MISC)
                    .fireImmune()
                    .setShouldReceiveVelocityUpdates(false)
                    .sized(2.0F, 1.0F)
                    .clientTrackingRange(6)
                    .updateInterval(2));

    public static final RegistryObject<EntityType<ScatterMine>> SCATTER_MINE = register("scatter_mine",
            EntityType.Builder.<ScatterMine>of(ScatterMine::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.0F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<SoulBomb>> SOUL_BOMB = register("soul_bomb",
            EntityType.Builder.<SoulBomb>of(SoulBomb::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<SnapFungus>> SNAP_FUNGUS = register("snap_fungus",
            EntityType.Builder.<SnapFungus>of(SnapFungus::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<BlastFungus>> BLAST_FUNGUS = register("blast_fungus",
            EntityType.Builder.<BlastFungus>of(BlastFungus::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<BerserkFungus>> BERSERK_FUNGUS = register("berserk_fungus",
            EntityType.Builder.<BerserkFungus>of(BerserkFungus::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<ObsidianMonolith>> OBSIDIAN_MONOLITH = register("obsidian_monolith",
            EntityType.Builder.of(ObsidianMonolith::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(1.0F, 3.1F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<TotemicWall>> TOTEMIC_WALL = register("totemic_wall",
            EntityType.Builder.of(TotemicWall::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(1.0F, 3.1F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<TotemicBomb>> TOTEMIC_BOMB = register("totemic_bomb",
            EntityType.Builder.of(TotemicBomb::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(1.0F, 3.1F)
                    .clientTrackingRange(8)
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

/*    public static final RegistryObject<EntityType<ModPainting>> MOD_PAINTING = register("mod_painting",
            EntityType.Builder.of(ModPainting::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));*/

    public static final RegistryObject<EntityType<HauntedArmorStand>> HAUNTED_ARMOR_STAND = register("haunted_armor_stand",
            EntityType.Builder.<HauntedArmorStand>of(HauntedArmorStand::new, MobCategory.MISC)
                    .sized(0.5F, 1.975F)
                    .fireImmune()
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<Apostle>> APOSTLE = register("apostle",
            EntityType.Builder.of(Apostle::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .fireImmune()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<Warlock>> WARLOCK = register("warlock",
            EntityType.Builder.of(Warlock::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<Wartling>> WARTLING = register("wartling",
            EntityType.Builder.of(Wartling::new, MobCategory.MONSTER)
                    .sized(0.4F, 0.2F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<Crone>> CRONE = register("crone",
            EntityType.Builder.of(Crone::new, MobCategory.MONSTER)
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

    public static final RegistryObject<EntityType<VampireBat>> VAMPIRE_BAT = register("vampire_bat",
            EntityType.Builder.of(VampireBat::new, MobCategory.MONSTER)
                    .sized(0.5F, 0.9F)
                    .clientTrackingRange(5));

    public static final RegistryObject<EntityType<Wraith>> WRAITH = register("wraith",
            EntityType.Builder.of(Wraith::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<BorderWraith>> BORDER_WRAITH = register("border_wraith",
            EntityType.Builder.of(BorderWraith::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<CairnNecromancer>> CAIRN_NECROMANCER = register("cairn_necromancer",
            EntityType.Builder.of(CairnNecromancer::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.4875F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<HauntedArmor>> HAUNTED_ARMOR = register("haunted_armor",
            EntityType.Builder.of(HauntedArmor::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<AllyVex>> ALLY_VEX = register("ally_vex",
            EntityType.Builder.of(AllyVex::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(0.4F, 0.8F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<AllyIrk>> ALLY_IRK = register("ally_irk",
            EntityType.Builder.of(AllyIrk::new, MobCategory.MONSTER)
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

    public static final RegistryObject<EntityType<NecromancerServant>> NECROMANCER_SERVANT = register("necromancer_servant",
            EntityType.Builder.of(NecromancerServant::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.4875F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<VanguardServant>> VANGUARD_SERVANT = register("vanguard_servant",
            EntityType.Builder.of(VanguardServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<WraithServant>> WRAITH_SERVANT = register("wraith_servant",
            EntityType.Builder.of(WraithServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<HauntedArmorServant>> HAUNTED_ARMOR_SERVANT = register("haunted_armor_servant",
            EntityType.Builder.of(HauntedArmorServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
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

    public static final RegistryObject<EntityType<Ravaged>> RAVAGED = register("ravaged",
            EntityType.Builder.of(Ravaged::new, MobCategory.MONSTER)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<ModRavager>> MOD_RAVAGER = register("ravager",
            EntityType.Builder.of(ModRavager::new, MobCategory.MONSTER)
                    .sized(1.95F, 2.2F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<ArmoredRavager>> ARMORED_RAVAGER = register("armored_ravager",
            EntityType.Builder.of(ArmoredRavager::new, MobCategory.MONSTER)
                    .sized(1.95F, 2.2F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<ZombieRavager>> ZOMBIE_RAVAGER = register("zombie_ravager",
            EntityType.Builder.of(ZombieRavager::new, MobCategory.MONSTER)
                    .sized(1.95F, 2.2F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<RedstoneGolem>> REDSTONE_GOLEM = register("redstone_golem",
            EntityType.Builder.of(RedstoneGolem::new, MobCategory.MONSTER)
                    .sized(2.7F, 3.9F)
                    .fireImmune()
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<GraveGolem>> GRAVE_GOLEM = register("grave_golem",
            EntityType.Builder.of(GraveGolem::new, MobCategory.MONSTER)
                    .sized(2.7F, 3.9F)
                    .fireImmune()
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<Haunt>> HAUNT = register("haunt",
            EntityType.Builder.of(Haunt::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.5F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<Envioker>> ENVIOKER = register("envioker",
            EntityType.Builder.of(Envioker::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<Tormentor>> TORMENTOR = register("tormentor",
            EntityType.Builder.of(Tormentor::new, MobCategory.MONSTER)
                    .fireImmune()
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<Inquillager>> INQUILLAGER = register("inquillager",
            EntityType.Builder.of(Inquillager::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<Conquillager>> CONQUILLAGER = register("conquillager",
            EntityType.Builder.of(Conquillager::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<Minister>> MINISTER = register("minister",
            EntityType.Builder.of(Minister::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<Vizier>> VIZIER = register("vizier",
            EntityType.Builder.of(Vizier::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<Irk>> IRK = register("irk",
            EntityType.Builder.of(Irk::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(0.4F, 0.8F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<SkullLord>> SKULL_LORD = register("skull_lord",
            EntityType.Builder.of(SkullLord::new, MobCategory.MONSTER)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<BoneLord>> BONE_LORD = register("bone_lord",
            EntityType.Builder.of(BoneLord::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
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

    public static final RegistryObject<EntityType<UpdraftBlast>> UPDRAFT_BLAST = register("updraft_blast",
            EntityType.Builder.<UpdraftBlast>of(UpdraftBlast::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final RegistryObject<EntityType<MagicGround>> MAGIC_GROUND = register("magic_ground",
            EntityType.Builder.<MagicGround>of(MagicGround::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.25F, 1.0F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final RegistryObject<EntityType<SummonCircle>> SUMMON_CIRCLE = register("summon_circle",
            EntityType.Builder.<SummonCircle>of(SummonCircle::new, MobCategory.MISC)
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

    public static final RegistryObject<EntityType<FrostSpellCloud>> FROST_CLOUD = register("frost_cloud",
            EntityType.Builder.<FrostSpellCloud>of(FrostSpellCloud::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<BrewEffectCloud>> BREW_EFFECT_CLOUD = register("brew_effect_cloud",
            EntityType.Builder.<BrewEffectCloud>of(BrewEffectCloud::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(6.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final RegistryObject<EntityType<BrewGas>> BREW_EFFECT_GAS = register("brew_effect_gas",
            EntityType.Builder.<BrewGas>of(BrewGas::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final RegistryObject<EntityType<SkullLaser>> LASER = register("laser",
            EntityType.Builder.of(SkullLaser::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<TunnelingFang>> TUNNELING_FANG = register("tunneling_fang",
            EntityType.Builder.of(TunnelingFang::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String p_20635_, EntityType.Builder<T> p_20636_) {
        return ENTITY_TYPE.register(p_20635_, () -> p_20636_.build(Goety.location(p_20635_).toString()));
    }
}
