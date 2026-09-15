package com.Polarice3.Goety.common.magic;

import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.common.magic.spells.*;
import com.Polarice3.Goety.common.magic.spells.abyss.*;
import com.Polarice3.Goety.common.magic.spells.frost.*;
import com.Polarice3.Goety.common.magic.spells.geomancy.*;
import com.Polarice3.Goety.common.magic.spells.necromancy.*;
import com.Polarice3.Goety.common.magic.spells.nether.*;
import com.Polarice3.Goety.common.magic.spells.storm.*;
import com.Polarice3.Goety.common.magic.spells.utility.*;
import com.Polarice3.Goety.common.magic.spells.void_spells.*;
import com.Polarice3.Goety.common.magic.spells.wild.*;
import com.Polarice3.Goety.common.magic.spells.wind.*;

public class ModSpells {
    ///Magic
    public static final ISpell VEXING = new VexSpell();
    public static final ISpell BITING = new FangSpell();
    public static final ISpell FEAST = new FeastSpell();
    public static final ISpell TEETH = new TeethSpell();
    public static final ISpell SHREDDING = new SpikeSpell();
    public static final ISpell ILLUSION = new IllusionSpell();
    public static final ISpell IGNITE = new IgniteSpell();
    public static final ISpell SOUL_BOLT = new SoulBoltSpell();
    public static final ISpell MAGIC_BOLT = new MagicBoltSpell();
    public static final ISpell SWORD = new SwordSpell();
    public static final ISpell SOUL_LIGHT = new SoulLightSpell();
    public static final ISpell GLOW_LIGHT = new GlowLightSpell();
    public static final ISpell ILLUMINATE = new IlluminateSpell();
    public static final ISpell CRAFTING = new CraftingSpell();
    public static final ISpell IRON_HIDE = new IronHideSpell();
    public static final ISpell BULWARK = new BulwarkSpell();
    public static final ISpell SOUL_HEAL = new SoulHealSpell();
    public static final ISpell SHOCKWAVE = new ShockwaveSpell();
    public static final ISpell WEAKENING = new WeakeningSpell();
    public static final ISpell ARROW_RAIN = new ArrowRainSpell();
    public static final ISpell TELEKINESIS = new TelekinesisSpell();
    public static final ISpell COMMAND = new CommandSpell();
    public static final ISpell ORDER = new CommandSpell();
    public static final ISpell SONIC_BOOM = new SonicBoomSpell();
    public static final ISpell CORRUPTION = new CorruptedBeamSpell();

    ///Necromancy
    public static final ISpell ROTTING = new ZombieSpell();
    public static final ISpell OSSEOUS = new SkeletonSpell();
    public static final ISpell GHOST_FIRE = new IceBouquetSpell();
    public static final ISpell REAPING = new ReaperSpell();
    public static final ISpell SPOOKY = new WraithSpell();
    public static final ISpell PHANTASM = new PhantomSpell();
    public static final ISpell VANGUARD = new VanguardSpell();
    public static final ISpell BLACKGUARD = new BlackguardSpell();
    public static final ISpell LEECHING = new LeechingSpell();
    public static final ISpell KILLING = new KillingSpell();
    public static final ISpell SKULL = new HauntedSkullSpell();

    ///Geomancy
    public static final ISpell BARRICADE = new BarricadeSpell();
    public static final ISpell QUAKING = new QuakingSpell();
    public static final ISpell EARTH_PUNCH = new EarthFistSpell();
    public static final ISpell SMACK_STONE = new SmackStoneSpell();
    public static final ISpell MINISTROUS = new MinistrousSpell();
    public static final ISpell PULVERIZE = new PulverizeSpell();
    public static final ISpell ROTATION = new RotationSpell();
    public static final ISpell BURROWING = new BurrowingSpell();
    public static final ISpell SENSING = new SensingSpell();
    public static final ISpell SCATTER = new ScatterSpell();
    public static final ISpell ERUPTION = new EruptionSpell();

    ///Frost
    public static final ISpell FROST_BREATH = new FrostBreathSpell();
    public static final ISpell ICE_SPIKE = new IceSpikeSpell();
    public static final ISpell ICE_STORM = new IceStormSpell();
    public static final ISpell HAIL = new HailSpell();
    public static final ISpell ICEOLOGY = new IceChunkSpell();
    public static final ISpell BLIZZARD = new BlizzardSpell();
    public static final ISpell CHILLING = new ChillHideSpell();
    public static final ISpell FROST_NOVA = new FrostNovaSpell();
    public static final ISpell FROSTBORN = new IceGolemSpell();

    ///Wild
    public static final ISpell SWARM = new SwarmSpell();
    public static final ISpell POISON_DART = new PoisonDartSpell();
    public static final ISpell BLOSSOMING = new BlossomSpell();
    public static final ISpell GRAPPLE = new GrappleSpell();
    public static final ISpell HUNTING = new HuntingSpell();
    public static final ISpell MAULING = new MaulingSpell();
    public static final ISpell SLIMY = new SlimySpell();
    public static final ISpell CARRION = new CarrionSpell();
    public static final ISpell OVERGROWTH = new OvergrowthSpell();
    public static final ISpell ENTANGLING = new EntanglingSpell();
    public static final ISpell WHISPERING = new WhisperSpell();
    public static final ISpell LEAPING = new LeapingSpell();

    ///Wind
    public static final ISpell LAUNCH = new LaunchSpell();
    public static final ISpell FLYING = new FlyingSpell();
    public static final ISpell CUSHION = new CushionSpell();
    public static final ISpell WHIRLWIND = new WhirlwindSpell();
    public static final ISpell CYCLONE = new CycloneSpell();
    public static final ISpell UPDRAFT = new UpdraftSpell();
    public static final ISpell WIND_BLAST = new WindBlastSpell();
    public static final ISpell RAZOR_WIND = new RazorWindSpell();
    public static final ISpell TREMBLING = new WindHornSpell();

    ///Storm
    public static final ISpell CHARGE = new ChargeSpell();
    public static final ISpell SHOCKING = new ShockingSpell();
    public static final ISpell THUNDERBOLT = new ThunderboltSpell();
    public static final ISpell ELECTROCUTE = new ElectroOrbSpell();
    public static final ISpell SURGING = new SurgingSpell();
    public static final ISpell SPRIGHTLY = new SpriteSpell();
    public static final ISpell MONSOON = new MonsoonSpell();
    public static final ISpell DISCHARGE = new DischargeSpell();
    public static final ISpell BOLTING = new BoltingSpell();
    public static final ISpell LIGHTNING = new LightningSpell();
    public static final ISpell THUNDERSTORM = new ThunderstormSpell();

    ///Abyss
    public static final ISpell WATER_JET = new WaterJetSpell();
    public static final ISpell BOUNCY_BUBBLE = new BouncyBubbleSpell();
    public static final ISpell STEAMING = new SteamSpell();
    public static final ISpell TRIDENT_STORM = new TridentStormSpell();
    public static final ISpell PRISMA_BEAM = new PrismaBeamSpell();
    public static final ISpell GUARDIAN = new GuardianSpell();
    public static final ISpell BIOMINE = new BioMineSpell();
    public static final ISpell WATER_WHIP = new GulfTentacleSpell();
    public static final ISpell TIDAL = new TidalSpell();

    ///Nether
    public static final ISpell FIRE_BREATH = new FireBreathSpell();
    public static final ISpell FIREBALL = new FireballSpell();
    public static final ISpell LAVABALL = new LavaballSpell();
    public static final ISpell BOMBARDMENT = new BombardmentSpell();
    public static final ISpell METEOR_SHOWER = new MeteorShowerSpell();
    public static final ISpell MAGMA_BOMB = new MagmaSpell();
    public static final ISpell FIRE_BLAST = new FireBlastSpell();
    public static final ISpell FLAME_STRIKE = new FlameStrikeSpell();
    public static final ISpell WITHER_SKULL = new WitherSkullSpell();
    public static final ISpell GHASTLY = new GhastSpell();
    public static final ISpell BLAZING = new BlazeSpell();
    public static final ISpell HOGGING = new HoggingSpell();

    ///Void
    public static final ISpell CALL = new CallSpell();
    public static final ISpell TROOP = new TroopSpell();
    public static final ISpell RECALL = new RecallSpell();
    public static final ISpell ENDER_CHEST = new EnderChestSpell();
    public static final ISpell END_WALK = new EndWalkSpell();
    public static final ISpell BLINK = new BlinkSpell();
    public static final ISpell BANISH = new BanishSpell();
    public static final ISpell TUNNEL = new TunnelSpell();
    public static final ISpell RUPTURE = new VoidRiftSpell();
    public static final ISpell STELLAR = new VoidShockSpell();
    public static final ISpell VOID_FLASH = new VoidBombSpell();
    public static final ISpell WATCHING = new WatchlingSpell();
    public static final ISpell BLASTING = new BlastlingSpell();
    public static final ISpell SNARING = new SnarelingSpell();
}
