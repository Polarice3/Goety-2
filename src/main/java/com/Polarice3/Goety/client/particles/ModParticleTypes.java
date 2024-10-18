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

    public static final RegistryObject<SimpleParticleType> FROST = PARTICLE_TYPES.register("frost",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> FROST_NOVA = PARTICLE_TYPES.register("frost_nova",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> FLY = PARTICLE_TYPES.register("fly",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> BONE = PARTICLE_TYPES.register("bone",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> LASER_POINT = PARTICLE_TYPES.register("laser_point",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> LEECH = PARTICLE_TYPES.register("leech",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> CHANT = PARTICLE_TYPES.register("chant",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> ELECTRIC = PARTICLE_TYPES.register("electric",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> BIG_ELECTRIC = PARTICLE_TYPES.register("big_electric",
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

    public static final RegistryObject<SimpleParticleType> SPELL_SQUARE = PARTICLE_TYPES.register("spell_square",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> TRAIL = PARTICLE_TYPES.register("trail",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> SUMMON_TRAIL = PARTICLE_TYPES.register("summon_trail",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> SPELL_CLOUD = PARTICLE_TYPES.register("spell_cloud",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> FANG_RAIN = PARTICLE_TYPES.register("fang_rain",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> MAGIC_BOLT = PARTICLE_TYPES.register("magic_bolt",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> RISING_ENCHANT = PARTICLE_TYPES.register("rising_enchant",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> ROLLING_ENCHANT = PARTICLE_TYPES.register("rolling_enchant",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> REDSTONE_EXPLODE = PARTICLE_TYPES.register("redstone_explode",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> ELECTRIC_EXPLODE = PARTICLE_TYPES.register("electric_explode",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> FAN_CLOUD = PARTICLE_TYPES.register("fan_cloud",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> REDSTONE_DEBRIS = PARTICLE_TYPES.register("redstone_debris",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> BLOSSOM_THORN_INDICATOR = PARTICLE_TYPES.register("blossom_thorn_indicator",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<ParticleType<SparkleParticleOption>> SPARKLE = PARTICLE_TYPES.register("sparkle",
            () -> new ParticleType<>(false, SparkleParticleOption.DESERIALIZER) {
                @Override
                public Codec codec() {
                    return SparkleParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<DustCloudParticleOption>> DUST_CLOUD = PARTICLE_TYPES.register("dust_cloud",
            () -> new ParticleType<>(false, DustCloudParticleOption.DESERIALIZER) {
                @Override
                public Codec codec() {
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
                public Codec codec() {
                    return ShockwaveParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<ShockwaveParticleOption>> REVERSE_SHOCKWAVE = PARTICLE_TYPES.register("reverse_shockwave",
            () -> new ParticleType<>(false, ShockwaveParticleOption.DESERIALIZER) {
                @Override
                public Codec codec() {
                    return ShockwaveParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<CircleExplodeParticleOption>> CIRCLE_EXPLODE = PARTICLE_TYPES.register("circle_explode",
            () -> new ParticleType<>(false, CircleExplodeParticleOption.DESERIALIZER) {
                @Override
                public Codec codec() {
                    return CircleExplodeParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<FoggyCloudParticleOption>> FOG_CLOUD = PARTICLE_TYPES.register("fog_cloud",
            () -> new ParticleType<>(false, FoggyCloudParticleOption.DESERIALIZER) {
                @Override
                public Codec codec() {
                    return FoggyCloudParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<PulsatingCircleParticleOption>> MINE_PULSE = PARTICLE_TYPES.register("mine_pulse",
            () -> new ParticleType(false, PulsatingCircleParticleOption.DESERIALIZER) {
                @Override
                public Codec codec() {
                    return PulsatingCircleParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<RisingCircleParticleOption>> SOUL_HEAL = PARTICLE_TYPES.register("soul_heal",
            () -> new ParticleType<>(false, RisingCircleParticleOption.DESERIALIZER) {
                @Override
                public Codec codec() {
                    return RisingCircleParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<SculkBubbleParticleOption>> SCULK_BUBBLE = PARTICLE_TYPES.register("sculk_bubble",
            () -> new ParticleType<>(false, SculkBubbleParticleOption.DESERIALIZER) {
                @Override
                public Codec codec() {
                    return SculkBubbleParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<WindParticleOption>> WIND = PARTICLE_TYPES.register("wind",
            () -> new ParticleType<>(false, WindParticleOption.DESERIALIZER) {
                @Override
                public Codec codec() {
                    return WindParticleOption.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<WindShockwaveParticle.Option>> WIND_SHOCKWAVE = PARTICLE_TYPES.register("wind_shockwave",
            () -> new ParticleType<>(false, WindShockwaveParticle.Option.DESERIALIZER) {
                @Override
                public Codec codec() {
                    return WindShockwaveParticle.Option.CODEC;
                }
            });

    public static final RegistryObject<ParticleType<GatherTrailParticle.Option>> GATHER_TRAIL = PARTICLE_TYPES.register("gather_trail",
            () -> new ParticleType<>(false, GatherTrailParticle.Option.DESERIALIZER) {
                @Override
                public Codec codec() {
                    return GatherTrailParticle.Option.CODEC;
                }
            });
}
