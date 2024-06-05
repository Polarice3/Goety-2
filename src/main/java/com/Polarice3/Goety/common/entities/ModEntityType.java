package com.Polarice3.Goety.common.entities;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.*;
import com.Polarice3.Goety.common.entities.ally.golem.*;
import com.Polarice3.Goety.common.entities.ally.spider.*;
import com.Polarice3.Goety.common.entities.ally.undead.*;
import com.Polarice3.Goety.common.entities.ally.undead.bound.BoundEvoker;
import com.Polarice3.Goety.common.entities.ally.undead.bound.BoundIceologer;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.*;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.*;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.common.entities.deco.HauntedArmorStand;
import com.Polarice3.Goety.common.entities.deco.HauntedPainting;
import com.Polarice3.Goety.common.entities.hostile.*;
import com.Polarice3.Goety.common.entities.hostile.cultists.Crone;
import com.Polarice3.Goety.common.entities.hostile.cultists.Warlock;
import com.Polarice3.Goety.common.entities.hostile.illagers.*;
import com.Polarice3.Goety.common.entities.hostile.servants.*;
import com.Polarice3.Goety.common.entities.neutral.*;
import com.Polarice3.Goety.common.entities.projectiles.*;
import com.Polarice3.Goety.common.entities.util.*;
import com.Polarice3.Goety.common.entities.vehicle.ModBoat;
import com.Polarice3.Goety.common.entities.vehicle.ModChestBoat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Blocks;
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

    public static final RegistryObject<EntityType<HellBolt>> HELL_BOLT = register("hell_bolt",
            EntityType.Builder.<HellBolt>of(HellBolt::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4));

    public static final RegistryObject<EntityType<HellBlast>> HELL_BLAST = register("hell_blast",
            EntityType.Builder.<HellBlast>of(HellBlast::new, MobCategory.MISC)
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

    public static final RegistryObject<EntityType<IceSpear>> ICE_SPEAR = register("ice_spear",
            EntityType.Builder.<IceSpear>of(IceSpear::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4));

    public static final RegistryObject<EntityType<IceStorm>> ICE_STORM = register("ice_storm",
            EntityType.Builder.<IceStorm>of(IceStorm::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<GhostArrow>> GHOST_ARROW = register("ghost_arrow",
            EntityType.Builder.<GhostArrow>of(GhostArrow::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20));

    public static final RegistryObject<EntityType<DeathArrow>> DEATH_ARROW = register("death_arrow",
            EntityType.Builder.<DeathArrow>of(DeathArrow::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20));

    public static final RegistryObject<EntityType<Harpoon>> HARPOON = register("harpoon",
            EntityType.Builder.<Harpoon>of(Harpoon::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4));

    public static final RegistryObject<EntityType<PoisonQuill>> POISON_QUILL = register("poison_quill",
            EntityType.Builder.<PoisonQuill>of(PoisonQuill::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20));

    public static final RegistryObject<EntityType<BoneShard>> BONE_SHARD = register("bone_shard",
            EntityType.Builder.<BoneShard>of(BoneShard::new, MobCategory.MISC)
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

    public static final RegistryObject<EntityType<ModDragonFireball>> MOD_DRAGON_FIREBALL = register("dragon_fireball",
            EntityType.Builder.<ModDragonFireball>of(ModDragonFireball::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4));

    public static final RegistryObject<EntityType<HauntedSkullProjectile>> HAUNTED_SKULL_SHOT = register("haunted_skull_shot",
            EntityType.Builder.<HauntedSkullProjectile>of(HauntedSkullProjectile::new, MobCategory.MISC)
                    .sized(0.5f,0.5f)
                    .clientTrackingRange(4));

    public static final RegistryObject<EntityType<ModWitherSkull>> MOD_WITHER_SKULL = register("wither_skull",
            EntityType.Builder.<ModWitherSkull>of(ModWitherSkull::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

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

    public static final RegistryObject<EntityType<SteamMissile>> STEAM_MISSILE = register("steam_missile",
            EntityType.Builder.<SteamMissile>of(SteamMissile::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<WitherBolt>> WITHER_BOLT = register("wither_bolt",
            EntityType.Builder.<WitherBolt>of(WitherBolt::new, MobCategory.MISC)
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

    public static final RegistryObject<EntityType<ElectroOrb>> ELECTRO_ORB = register("electro_orb",
            EntityType.Builder.<ElectroOrb>of(ElectroOrb::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<IceBouquet>> ICE_BOUQUET = register("ice_bouquet",
            EntityType.Builder.<IceBouquet>of(IceBouquet::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(0.8F, 1.0F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<Hellfire>> HELLFIRE = register("hellfire",
            EntityType.Builder.<Hellfire>of(Hellfire::new, MobCategory.MISC)
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

    public static final RegistryObject<EntityType<ScatterBomb>> SCATTER_BOMB = register("scatter_bomb",
            EntityType.Builder.<ScatterBomb>of(ScatterBomb::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
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

    public static final RegistryObject<EntityType<Pyroclast>> PYROCLAST = register("pyroclast",
            EntityType.Builder.<Pyroclast>of(Pyroclast::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<MagmaBomb>> MAGMA_BOMB = register("magma_bomb",
            EntityType.Builder.<MagmaBomb>of(MagmaBomb::new, MobCategory.MISC)
                    .sized(1.25F, 1.25F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<WebShot>> WEB_SHOT = register("web_shot",
            EntityType.Builder.<WebShot>of(WebShot::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<EntangleVines>> ENTANGLE_VINES = register("entangle_vines",
            EntityType.Builder.<EntangleVines>of(EntangleVines::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<SpiderWeb>> SPIDER_WEB = register("spider_web",
            EntityType.Builder.<SpiderWeb>of(SpiderWeb::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
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

    public static final RegistryObject<EntityType<GlacialWall>> GLACIAL_WALL = register("glacial_wall",
            EntityType.Builder.of(GlacialWall::new, MobCategory.MONSTER)
                    .sized(1.0F, 3.1F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<QuickGrowingVine>> QUICK_GROWING_VINE = register("quick_growing_vine",
            EntityType.Builder.of(QuickGrowingVine::new, MobCategory.MONSTER)
                    .sized(1.0F, 4.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<QuickGrowingKelp>> QUICK_GROWING_KELP = register("quick_growing_kelp",
            EntityType.Builder.of(QuickGrowingKelp::new, MobCategory.MONSTER)
                    .sized(1.0F, 4.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<PoisonQuillVine>> POISON_QUILL_VINE = register("poison_quill_vine",
            EntityType.Builder.of(PoisonQuillVine::new, MobCategory.MONSTER)
                    .sized(1.0F, 4.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<PoisonAnemone>> POISON_ANEMONE = register("poison_anemone",
            EntityType.Builder.of(PoisonAnemone::new, MobCategory.MONSTER)
                    .sized(1.0F, 4.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<InsectSwarm>> INSECT_SWARM = register("insect_swarm",
            EntityType.Builder.<InsectSwarm>of(InsectSwarm::new, MobCategory.MONSTER)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<Volcano>> VOLCANO = register("volcano",
            EntityType.Builder.of(Volcano::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 1.0F)
                    .clientTrackingRange(8)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<FireTornado>> FIRE_TORNADO = register("fire_tornado",
            EntityType.Builder.<FireTornado>of(FireTornado::new, MobCategory.MISC)
                    .sized(1.0F, 1.5F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<Cyclone>> CYCLONE = register("cyclone",
            EntityType.Builder.<Cyclone>of(Cyclone::new, MobCategory.MISC)
                    .sized(1.0F, 1.5F)
                    .clientTrackingRange(4)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<ModFallingBlock>> FALLING_BLOCK = register("falling_block",
            EntityType.Builder.<ModFallingBlock>of(ModFallingBlock::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
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

    public static final RegistryObject<EntityType<HauntedPainting>> MOD_PAINTING = register("haunted_painting",
            EntityType.Builder.<HauntedPainting>of(HauntedPainting::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

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

    public static final RegistryObject<EntityType<Inferno>> INFERNO = register("inferno",
            EntityType.Builder.of(Inferno::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F)
                    .fireImmune()
                    .clientTrackingRange(8));

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

    public static final RegistryObject<EntityType<CryptSlime>> CRYPT_SLIME = register("crypt_slime",
            EntityType.Builder.<CryptSlime>of(CryptSlime::new, MobCategory.MONSTER)
                    .sized(2.04F, 2.04F)
                    .clientTrackingRange(10));

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

    public static final RegistryObject<EntityType<FrozenZombieServant>> FROZEN_ZOMBIE_SERVANT = register("frozen_zombie_servant",
            EntityType.Builder.of(FrozenZombieServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<JungleZombieServant>> JUNGLE_ZOMBIE_SERVANT = register("jungle_zombie_servant",
            EntityType.Builder.of(JungleZombieServant::new, MobCategory.MONSTER)
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

    public static final RegistryObject<EntityType<WitherSkeletonServant>> WITHER_SKELETON_SERVANT = register("wither_skeleton_servant",
            EntityType.Builder.of(WitherSkeletonServant::new, MobCategory.MONSTER)
                    .fireImmune()
                    .immuneTo(Blocks.WITHER_ROSE)
                    .sized(0.7F, 2.4F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<MossySkeletonServant>> MOSSY_SKELETON_SERVANT = register("mossy_skeleton_servant",
            EntityType.Builder.of(MossySkeletonServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<SunkenSkeletonServant>> SUNKEN_SKELETON_SERVANT = register("sunken_skeleton_servant",
            EntityType.Builder.of(SunkenSkeletonServant::new, MobCategory.MONSTER)
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

    public static final RegistryObject<EntityType<BorderWraithServant>> BORDER_WRAITH_SERVANT = register("border_wraith_servant",
            EntityType.Builder.of(BorderWraithServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<SkeletonPillager>> SKELETON_PILLAGER_SERVANT = register("skeleton_pillager",
            EntityType.Builder.of(SkeletonPillager::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<ZombieVindicator>> ZOMBIE_VINDICATOR_SERVANT = register("zombie_vindicator",
            EntityType.Builder.of(ZombieVindicator::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<BoundEvoker>> BOUND_EVOKER = register("bound_evoker",
            EntityType.Builder.of(BoundEvoker::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<BoundIceologer>> BOUND_ICEOLOGER = register("bound_iceologer",
            EntityType.Builder.of(BoundIceologer::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
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

    public static final RegistryObject<EntityType<MiniGhast>> MINI_GHAST = register("mini_ghast",
            EntityType.Builder.of(MiniGhast::new, MobCategory.MONSTER)
                    .sized(1.2F, 1.2F)
                    .clientTrackingRange(10)
                    .fireImmune());

    public static final RegistryObject<EntityType<GhastServant>> GHAST_SERVANT = register("ghast_servant",
            EntityType.Builder.of(GhastServant::new, MobCategory.MONSTER)
                    .sized(4.0F, 4.0F)
                    .clientTrackingRange(10)
                    .fireImmune());

    public static final RegistryObject<EntityType<BlazeServant>> BLAZE_SERVANT = register("blaze_servant",
            EntityType.Builder.of(BlazeServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(8)
                    .fireImmune());

    public static final RegistryObject<EntityType<SlimeServant>> SLIME_SERVANT = register("slime_servant",
            EntityType.Builder.of(SlimeServant::new, MobCategory.MONSTER)
                    .sized(2.04F, 2.04F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<MagmaCubeServant>> MAGMA_CUBE_SERVANT = register("magma_cube_servant",
            EntityType.Builder.of(MagmaCubeServant::new, MobCategory.MONSTER)
                    .sized(2.04F, 2.04F)
                    .fireImmune()
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<SpiderServant>> SPIDER_SERVANT = register("spider_servant",
            EntityType.Builder.of(SpiderServant::new, MobCategory.MONSTER)
                    .sized(1.4F, 0.9F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<CaveSpiderServant>> CAVE_SPIDER_SERVANT = register("cave_spider_servant",
            EntityType.Builder.of(CaveSpiderServant::new, MobCategory.MONSTER)
                    .sized(0.7F, 0.5F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<WebSpiderServant>> WEB_SPIDER_SERVANT = register("web_spider_servant",
            EntityType.Builder.of(WebSpiderServant::new, MobCategory.MONSTER)
                    .sized(1.4F, 0.9F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<IcySpiderServant>> ICY_SPIDER_SERVANT = register("icy_spider_servant",
            EntityType.Builder.of(IcySpiderServant::new, MobCategory.MONSTER)
                    .sized(1.4F, 0.9F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<BoneSpiderServant>> BONE_SPIDER_SERVANT = register("bone_spider_servant",
            EntityType.Builder.of(BoneSpiderServant::new, MobCategory.MONSTER)
                    .sized(0.6F, 0.7F)
                    .clientTrackingRange(8));

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

    public static final RegistryObject<EntityType<Whisperer>> WHISPERER = register("whisperer",
            EntityType.Builder.of(Whisperer::new, MobCategory.MONSTER)
                    .sized(0.6F, 2.0F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<Wavewhisperer>> WAVEWHISPERER = register("wavewhisperer",
            EntityType.Builder.of(Wavewhisperer::new, MobCategory.MONSTER)
                    .sized(0.6F, 2.0F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<Leapleaf>> LEAPLEAF = register("leapleaf",
            EntityType.Builder.of(Leapleaf::new, MobCategory.MONSTER)
                    .sized(1.9F, 1.9F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<IceGolem>> ICE_GOLEM = register("ice_golem",
            EntityType.Builder.of(IceGolem::new, MobCategory.MONSTER)
                    .sized(2.0F, 2.5F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<SquallGolem>> SQUALL_GOLEM = register("squall_golem",
            EntityType.Builder.of(SquallGolem::new, MobCategory.MONSTER)
                    .sized(2.0F, 2.5F)
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
                    .sized(0.6F, 0.8F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<RedstoneMonstrosity>> REDSTONE_MONSTROSITY = register("redstone_monstrosity",
            EntityType.Builder.of(RedstoneMonstrosity::new, MobCategory.MONSTER)
                    .sized(4.0F, 5.4F)
                    .fireImmune()
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<RedstoneCube>> REDSTONE_CUBE = register("redstone_cube",
            EntityType.Builder.of(RedstoneCube::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(1.0F, 1.0F)
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

    public static final RegistryObject<EntityType<Piker>> PIKER = register("piker",
            EntityType.Builder.of(Piker::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<Ripper>> RIPPER = register("ripper",
            EntityType.Builder.of(Ripper::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 0.85F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<Trampler>> TRAMPLER = register("trampler",
            EntityType.Builder.of(Trampler::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(1.3964844F, 1.6F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<Crusher>> CRUSHER = register("crusher",
            EntityType.Builder.of(Crusher::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<StormCaster>> STORM_CASTER = register("storm_caster",
            EntityType.Builder.of(StormCaster::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<Cryologer>> CRYOLOGER = register("cryologer",
            EntityType.Builder.of(Cryologer::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<Preacher>> PREACHER = register("preacher",
            EntityType.Builder.of(Preacher::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<Minister>> MINISTER = register("minister",
            EntityType.Builder.of(Minister::new, MobCategory.MONSTER)
                    .canSpawnFarFromPlayer()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<HostileRedstoneGolem>> HOSTILE_REDSTONE_GOLEM = register("hostile_redstone_golem",
            EntityType.Builder.of(HostileRedstoneGolem::new, MobCategory.MONSTER)
                    .sized(2.7F, 3.9F)
                    .fireImmune()
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<Vizier>> VIZIER = register("vizier",
            EntityType.Builder.of(Vizier::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8));

    public static final RegistryObject<EntityType<VizierClone>> VIZIER_CLONE = register("vizier_clone",
            EntityType.Builder.of(VizierClone::new, MobCategory.MONSTER)
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

    public static final RegistryObject<EntityType<WitherNecromancer>> WITHER_NECROMANCER = register("wither_necromancer",
            EntityType.Builder.of(WitherNecromancer::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.8875F)
                    .fireImmune()
                    .immuneTo(Blocks.WITHER_ROSE)
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

    public static final RegistryObject<EntityType<Cushion>> CUSHION = register("cushion",
            EntityType.Builder.<Cushion>of(Cushion::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(6.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<MagicGround>> MAGIC_GROUND = register("magic_ground",
            EntityType.Builder.<MagicGround>of(MagicGround::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.25F, 1.0F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final RegistryObject<EntityType<AcidPool>> ACID_POOL = register("acid_pool",
            EntityType.Builder.of(AcidPool::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final RegistryObject<EntityType<FirePillar>> FIRE_PILLAR = register("fire_pillar",
            EntityType.Builder.<FirePillar>of(FirePillar::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.25F, 1.0F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final RegistryObject<EntityType<SummonCircle>> SUMMON_CIRCLE = register("summon_circle",
            EntityType.Builder.<SummonCircle>of(SummonCircle::new, MobCategory.MISC)
                    .fireImmune()
                    .noSummon()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<SummonCircleBoss>> SUMMON_CIRCLE_BOSS = register("summon_circle_boss",
            EntityType.Builder.<SummonCircleBoss>of(SummonCircleBoss::new, MobCategory.MISC)
                    .fireImmune()
                    .noSummon()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<SummonCircleVariant>> SUMMON_FIERY = register("summon_fiery",
            EntityType.Builder.<SummonCircleVariant>of(SummonCircleVariant::new, MobCategory.MISC)
                    .fireImmune()
                    .noSummon()
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

    public static final RegistryObject<EntityType<HailCloud>> HAIL_CLOUD = register("hail_cloud",
            EntityType.Builder.<HailCloud>of(HailCloud::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<MonsoonCloud>> MONSOON_CLOUD = register("monsoon_cloud",
            EntityType.Builder.<MonsoonCloud>of(MonsoonCloud::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<HellCloud>> HELL_CLOUD = register("hell_cloud",
            EntityType.Builder.<HellCloud>of(HellCloud::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(2.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1));

    public static final RegistryObject<EntityType<SpellLightningBolt>> SPELL_LIGHTNING_BOLT = register("spell_lightning_bolt",
            EntityType.Builder.of(SpellLightningBolt::new, MobCategory.MISC)
                    .sized(0.0F, 0.0F)
                    .clientTrackingRange(16)
                    .updateInterval(Integer.MAX_VALUE));

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
                    .noSummon()
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<VineHook>> VINE_HOOK = register("vine_hook",
            EntityType.Builder.<VineHook>of(VineHook::new, MobCategory.MISC)
                    .noSave()
                    .noSummon()
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(5));

    public static final RegistryObject<EntityType<SurveyEye>> SURVEY_EYE = register("survey_eye",
            EntityType.Builder.of(SurveyEye::new, MobCategory.MISC)
                    .fireImmune()
                    .noSummon()
                    .sized(0.0F, 0.0F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<CameraShake>> CAMERA_SHAKE = register("camera_shake",
            EntityType.Builder.<CameraShake>of(CameraShake::new, MobCategory.MISC)
                    .fireImmune()
                    .noSummon()
                    .sized(1.0F, 1.0F)
                    .updateInterval(Integer.MAX_VALUE));

    public static final RegistryObject<EntityType<TunnelingFang>> TUNNELING_FANG = register("tunneling_fang",
            EntityType.Builder.of(TunnelingFang::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(0.0F, 0.0F)
                    .clientTrackingRange(10));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String p_20635_, EntityType.Builder<T> p_20636_) {
        return ENTITY_TYPE.register(p_20635_, () -> p_20636_.build(Goety.location(p_20635_).toString()));
    }
}
