package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.Goety;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticleTypes {
    public static DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Goety.MOD_ID);

    public static final RegistryObject<SimpleParticleType> NONE = PARTICLE_TYPES.register("none",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> TOTEM_EFFECT = PARTICLE_TYPES.register("totem_effect",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> PLAGUE_EFFECT = PARTICLE_TYPES.register("plague_effect",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> DOOM = PARTICLE_TYPES.register("doom",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> DOOM_DEATH = PARTICLE_TYPES.register("doom_death",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> HEAL_EFFECT = PARTICLE_TYPES.register("heal",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> HEAL_EFFECT_2 = PARTICLE_TYPES.register("heal2",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> BULLET_EFFECT = PARTICLE_TYPES.register("bullet_effect",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> NECRO_EFFECT = PARTICLE_TYPES.register("necro_effect",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> NECRO_BOLT = PARTICLE_TYPES.register("necro_bolt",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> STUN = PARTICLE_TYPES.register("stun",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> SOUL_LIGHT_EFFECT = PARTICLE_TYPES.register("soul_light",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> GLOW_EFFECT = PARTICLE_TYPES.register("glow_trail",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> GLOW_LIGHT_EFFECT = PARTICLE_TYPES.register("glow_light",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> SOUL_EXPLODE_BITS = PARTICLE_TYPES.register("soul_explode_bits",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> XP_TAKE = PARTICLE_TYPES.register("xp_take",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> LASER_GATHER = PARTICLE_TYPES.register("laser",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> RESONANCE_GATHER = PARTICLE_TYPES.register("resonance",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> BURNING = PARTICLE_TYPES.register("burning",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> FIERY_PILLAR = PARTICLE_TYPES.register("fiery_pillar",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> CULT_SPELL = PARTICLE_TYPES.register("cult_spell",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> BIG_CULT_SPELL = PARTICLE_TYPES.register("big_cult_spell",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> SMALL_STATION_CULT_SPELL = PARTICLE_TYPES.register("small_station_cult_spell",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> STATION_CULT_SPELL = PARTICLE_TYPES.register("station_cult_spell",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> MUD_GAS = PARTICLE_TYPES.register("mud_gas",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> LICH = PARTICLE_TYPES.register("lich",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> CONFUSED = PARTICLE_TYPES.register("confused",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> WHITE_EFFECT = PARTICLE_TYPES.register("white_effect",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> WRAITH = PARTICLE_TYPES.register("wraith",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> WRAITH_BURST = PARTICLE_TYPES.register("wraith_burst",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> WRAITH_FIRE = PARTICLE_TYPES.register("wraith_fire",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> BIG_FIRE = PARTICLE_TYPES.register("big_fire",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> BIG_FIRE_DROP = PARTICLE_TYPES.register("big_fire_drop",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> BIG_FIRE_GROUND = PARTICLE_TYPES.register("big_fire_ground",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> BIG_SOUL_FIRE = PARTICLE_TYPES.register("big_soul_fire",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> BIG_SOUL_FIRE_DROP = PARTICLE_TYPES.register("big_soul_fire_drop",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> BIG_SOUL_FIRE_GROUND = PARTICLE_TYPES.register("big_soul_fire_ground",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> NECRO_FIRE = PARTICLE_TYPES.register("necro_fire",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> NECRO_FIRE_DROP = PARTICLE_TYPES.register("necro_fire_drop",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> SMALL_NECRO_FIRE = PARTICLE_TYPES.register("small_necro_fire",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> NECRO_FLAME = PARTICLE_TYPES.register("necro_flame",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> DRAGON_FLAME = PARTICLE_TYPES.register("dragon_flame",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> DRAGON_FLAME_DROP = PARTICLE_TYPES.register("dragon_flame_drop",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> SMALL_DRAGON_FLAME = PARTICLE_TYPES.register("small_dragon_flame",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> SMALL_DRAGON_FLAME_GROUND = PARTICLE_TYPES.register("small_dragon_flame_ground",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> END_FIRE = PARTICLE_TYPES.register("end_fire",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> END_FIRE_DROP = PARTICLE_TYPES.register("end_fire_drop",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> SMALL_END_FIRE = PARTICLE_TYPES.register("small_end_fire",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> FROST = PARTICLE_TYPES.register("frost",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> FROST_NOVA = PARTICLE_TYPES.register("frost_nova",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> FLY = PARTICLE_TYPES.register("fly",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> BONE = PARTICLE_TYPES.register("bone",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> LEECH = PARTICLE_TYPES.register("leech",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> CHANT = PARTICLE_TYPES.register("chant",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> ELECTRIC = PARTICLE_TYPES.register("electric",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> BIG_ELECTRIC = PARTICLE_TYPES.register("big_electric",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> SPELL_ELECTRIC = PARTICLE_TYPES.register("spell_electric",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> BUBBLE_STREAM = PARTICLE_TYPES.register("bubble_stream",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> BREW_BUBBLE = PARTICLE_TYPES.register("brew_bubble",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> WIND_BLAST = PARTICLE_TYPES.register("wind_blast",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> WARLOCK = PARTICLE_TYPES.register("warlock",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> FUNGUS_EXPLOSION = PARTICLE_TYPES.register("fungus_explosion",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> FUNGUS_EXPLOSION_EMITTER = PARTICLE_TYPES.register("fungus_explosion_emitter",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> SOUL_EXPLODE = PARTICLE_TYPES.register("soul_explode",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> SUMMON = PARTICLE_TYPES.register("summon",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> VOID_SPAWNER_DETECTION = PARTICLE_TYPES.register("void_spawner_detection",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> VOID_VAULT_CONNECT = PARTICLE_TYPES.register("void_vault_connect",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> SPELL_SQUARE = PARTICLE_TYPES.register("spell_square",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> SMALL_SPELL_SQUARE = PARTICLE_TYPES.register("small_spell_square",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> TRAIL = PARTICLE_TYPES.register("trail",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> SUMMON_TRAIL = PARTICLE_TYPES.register("summon_trail",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> SPELL_CLOUD = PARTICLE_TYPES.register("spell_cloud",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> DROPLET = PARTICLE_TYPES.register("droplet",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> GO = PARTICLE_TYPES.register("go",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> STOP = PARTICLE_TYPES.register("stop",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> FANG_RAIN = PARTICLE_TYPES.register("fang_rain",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> MAGIC_BOLT = PARTICLE_TYPES.register("magic_bolt",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> RISING_SPIRAL = PARTICLE_TYPES.register("rising_spiral",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> RISING_ENCHANT = PARTICLE_TYPES.register("rising_enchant",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> ROLLING_SPIRAL = PARTICLE_TYPES.register("rolling_spiral",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> ROLLING_ENCHANT = PARTICLE_TYPES.register("rolling_enchant",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> ROLLING_TARGET = PARTICLE_TYPES.register("rolling_target",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> REDSTONE_EXPLODE = PARTICLE_TYPES.register("redstone_explode",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> ELECTRIC_EXPLODE = PARTICLE_TYPES.register("electric_explode",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> FAN_CLOUD = PARTICLE_TYPES.register("fan_cloud",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> REDSTONE_DEBRIS = PARTICLE_TYPES.register("redstone_debris",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> GOO_STAIN = PARTICLE_TYPES.register("goo_stain",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> CHORUS_LEAVES = PARTICLE_TYPES.register("chorus_leaves",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> CHORUS_BLOSSOM_LEAVES = PARTICLE_TYPES.register("chorus_blossom_leaves",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> GOD_RAY = PARTICLE_TYPES.register("god_ray",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> STRETCHED_GOD_RAY = PARTICLE_TYPES.register("stretched_god_ray",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> WATER_STREAM = PARTICLE_TYPES.register("water_stream",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> WATER_JET = PARTICLE_TYPES.register("water_jet",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> WATER_TRAIL = PARTICLE_TYPES.register("water_trail",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> BLOSSOM_THORN_INDICATOR = PARTICLE_TYPES.register("blossom_thorn_indicator",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<ParticleType<SparkleParticleOption>> SPARKLE = PARTICLE_TYPES.register("sparkle",
            () -> new ParticleType<>(false, SparkleParticleOption.DESERIALIZER) {
                @Override
                public Codec<SparkleParticleOption> codec() {
                    return SparkleParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<DustCloudParticleOption>> DUST_CLOUD = PARTICLE_TYPES.register("dust_cloud",
            () -> new ParticleType<>(false, DustCloudParticleOption.DESERIALIZER) {
                @Override
                public Codec<DustCloudParticleOption> codec() {
                    return DustCloudParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<BlockParticleOption>> FAST_DUST = PARTICLE_TYPES.register("fast_dust",
            () -> new ParticleType<>(false, BlockParticleOption.DESERIALIZER) {
                @Override
                public Codec<BlockParticleOption> codec() {
                    return BlockParticleOption.codec(this);
                }
            });

    public static final RegistryObject<ParticleType<ShockwaveParticleOption>> SHOCKWAVE = PARTICLE_TYPES.register("shockwave",
            () -> new ParticleType<>(false, ShockwaveParticleOption.DESERIALIZER) {
                @Override
                public Codec<ShockwaveParticleOption> codec() {
                    return ShockwaveParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<ShockwaveParticleOption>> REVERSE_SHOCKWAVE = PARTICLE_TYPES.register("reverse_shockwave",
            () -> new ParticleType<>(false, ShockwaveParticleOption.DESERIALIZER) {
                @Override
                public Codec<ShockwaveParticleOption> codec() {
                    return ShockwaveParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<ShockwaveParticleOption>> LICH_DEATH = PARTICLE_TYPES.register("lich_death",
            () -> new ParticleType<>(false, ShockwaveParticleOption.DESERIALIZER) {
                @Override
                public Codec<ShockwaveParticleOption> codec() {
                    return ShockwaveParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<CircleExplodeParticleOption>> CIRCLE_EXPLODE = PARTICLE_TYPES.register("circle_explode",
            () -> new ParticleType<>(false, CircleExplodeParticleOption.DESERIALIZER) {
                @Override
                public Codec<CircleExplodeParticleOption> codec() {
                    return CircleExplodeParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<AoEParticleOption>> AOE_INDICATOR = PARTICLE_TYPES.register("aoe_indicator",
            () -> new ParticleType<>(false, AoEParticleOption.DESERIALIZER) {
                @Override
                public Codec<AoEParticleOption> codec() {
                    return AoEParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<FoggyCloudParticleOption>> FOG_CLOUD = PARTICLE_TYPES.register("fog_cloud",
            () -> new ParticleType<>(false, FoggyCloudParticleOption.DESERIALIZER) {
                @Override
                public Codec<FoggyCloudParticleOption> codec() {
                    return FoggyCloudParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<PulsatingCircleParticleOption>> MINE_PULSE = PARTICLE_TYPES.register("mine_pulse",
            () -> new ParticleType<>(false, PulsatingCircleParticleOption.DESERIALIZER) {
                @Override
                public Codec<PulsatingCircleParticleOption> codec() {
                    return PulsatingCircleParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<RisingCircleParticleOption>> SOUL_HEAL = PARTICLE_TYPES.register("soul_heal",
            () -> new ParticleType<>(false, RisingCircleParticleOption.DESERIALIZER) {
                @Override
                public Codec<RisingCircleParticleOption> codec() {
                    return RisingCircleParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<ModShriekParticleOption>> MOD_SHRIEK = PARTICLE_TYPES.register("mod_shriek",
            () -> new ParticleType<>(false, ModShriekParticleOption.DESERIALIZER) {
                @Override
                public Codec<ModShriekParticleOption> codec() {
                    return ModShriekParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<SculkBubbleParticleOption>> SCULK_BUBBLE = PARTICLE_TYPES.register("sculk_bubble",
            () -> new ParticleType<>(false, SculkBubbleParticleOption.DESERIALIZER) {
                @Override
                public Codec<SculkBubbleParticleOption> codec() {
                    return SculkBubbleParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<WindParticleOption>> WIND = PARTICLE_TYPES.register("wind",
            () -> new ParticleType<>(false, WindParticleOption.DESERIALIZER) {
                @Override
                public Codec<WindParticleOption> codec() {
                    return WindParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<WindBlowParticle.Option>> WIND_BLOW = PARTICLE_TYPES.register("wind_blow",
            () -> new ParticleType<>(false, WindBlowParticle.Option.DESERIALIZER) {
                @Override
                public Codec<WindBlowParticle.Option> codec() {
                    return WindBlowParticle.Option.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<WindShockwaveParticle.Option>> WIND_SHOCKWAVE = PARTICLE_TYPES.register("wind_shockwave",
            () -> new ParticleType<>(false, WindShockwaveParticle.Option.DESERIALIZER) {
                @Override
                public Codec<WindShockwaveParticle.Option> codec() {
                    return WindShockwaveParticle.Option.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<WindGatherParticleOption>> WIND_GATHER = PARTICLE_TYPES.register("wind_gather",
            () -> new ParticleType<>(false, WindGatherParticleOption.DESERIALIZER) {
                @Override
                public Codec<WindGatherParticleOption> codec() {
                    return WindGatherParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<GatherTrailParticle.Option>> GATHER_TRAIL = PARTICLE_TYPES.register("gather_trail",
            () -> new ParticleType<>(false, GatherTrailParticle.Option.DESERIALIZER) {
                @Override
                public Codec<GatherTrailParticle.Option> codec() {
                    return GatherTrailParticle.Option.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<GatherFrostParticle.Option>> FROST_GATHER = PARTICLE_TYPES.register("frost_gather",
            () -> new ParticleType<>(false, GatherFrostParticle.Option.DESERIALIZER) {
                @Override
                public Codec<GatherFrostParticle.Option> codec() {
                    return GatherFrostParticle.Option.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<AuraParticle.Option>> AURA = PARTICLE_TYPES.register("aura",
            () -> new ParticleType<>(false, AuraParticle.Option.DESERIALIZER) {
                @Override
                public Codec<AuraParticle.Option> codec() {
                    return AuraParticle.Option.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<GroundAuraParticle.Option>> GROUND_AURA = PARTICLE_TYPES.register("ground_aura",
            () -> new ParticleType<>(false, GroundAuraParticle.Option.DESERIALIZER) {
                @Override
                public Codec<GroundAuraParticle.Option> codec() {
                    return GroundAuraParticle.Option.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<VerticalCircleExplodeParticleOption>> VERTICAL_CIRCLE_EXPLODE = PARTICLE_TYPES.register("vertical_circle_explode",
            () -> new ParticleType<>(false, VerticalCircleExplodeParticleOption.DESERIALIZER) {
                @Override
                public Codec<VerticalCircleExplodeParticleOption> codec() {
                    return VerticalCircleExplodeParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<AbsorbTrailParticleOption>> ABSORB_TRAIL = PARTICLE_TYPES.register("absorb_trail",
            () -> new ParticleType<>(false, AbsorbTrailParticleOption.DESERIALIZER) {
                @Override
                public Codec<AbsorbTrailParticleOption> codec() {
                    return AbsorbTrailParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<MagicSmokeParticle.Option>> MAGIC_SMOKE = PARTICLE_TYPES.register("magic_smoke",
            () -> new ParticleType<>(false, MagicSmokeParticle.Option.DESERIALIZER) {
                @Override
                public Codec<MagicSmokeParticle.Option> codec() {
                    return MagicSmokeParticle.Option.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<MagicAshSmokeParticle.Option>> MAGIC_ASH_SMOKE = PARTICLE_TYPES.register("magic_ash_smoke",
            () -> new ParticleType<>(false, MagicAshSmokeParticle.Option.DESERIALIZER) {
                @Override
                public Codec<MagicAshSmokeParticle.Option> codec() {
                    return MagicAshSmokeParticle.Option.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<FollowFireParticle.Option>> FOLLOW_CULT_SPELL = PARTICLE_TYPES.register("follow_cult_spell",
            () -> new ParticleType<>(false, FollowFireParticle.Option.DESERIALIZER) {
                @Override
                public Codec<FollowFireParticle.Option> codec() {
                    return FollowFireParticle.Option.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<SpirallingParticleOption>> SPIRALLING = PARTICLE_TYPES.register("spiralling",
            () -> new ParticleType<>(false, SpirallingParticleOption.DESERIALIZER) {
                @Override
                public Codec<SpirallingParticleOption> codec() {
                    return SpirallingParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<ShootIndicatorParticleOption>> SHOOT_INDICATOR = PARTICLE_TYPES.register("shoot_indicator",
            () -> new ParticleType<>(false, ShootIndicatorParticleOption.DESERIALIZER) {
                @Override
                public Codec<ShootIndicatorParticleOption> codec() {
                    return ShootIndicatorParticleOption.CODEC;
                }
            });
}
