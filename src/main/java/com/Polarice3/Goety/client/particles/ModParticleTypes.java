package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.Goety;
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

    public static final RegistryObject<SimpleParticleType> HEAL_EFFECT = PARTICLE_TYPES.register("heal",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> BULLET_EFFECT = PARTICLE_TYPES.register("bullet_effect",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> SOUL_LIGHT_EFFECT = PARTICLE_TYPES.register("soul_light",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> GLOW_EFFECT = PARTICLE_TYPES.register("glow_trail",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> GLOW_LIGHT_EFFECT = PARTICLE_TYPES.register("glow_light",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> BURNING = PARTICLE_TYPES.register("burning",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> CULT_SPELL = PARTICLE_TYPES.register("cult_spell",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> CONFUSED = PARTICLE_TYPES.register("confused",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> WHITE_EFFECT = PARTICLE_TYPES.register("white_effect",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> WRAITH = PARTICLE_TYPES.register("wraith",
            () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> LEECH = PARTICLE_TYPES.register("leech",
            () -> new SimpleParticleType(false));
}
