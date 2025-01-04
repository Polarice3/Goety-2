package com.Polarice3.Goety.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

public record AbsorbTrailParticleOption(Vec3 target, int color, int duration) implements ParticleOptions {
    public static final Codec<AbsorbTrailParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Vec3.CODEC.fieldOf("target").forGetter(AbsorbTrailParticleOption::target),
            Codec.INT.fieldOf("color").forGetter(AbsorbTrailParticleOption::color),
            ExtraCodecs.POSITIVE_INT.fieldOf("duration").forGetter(AbsorbTrailParticleOption::duration)
    ).apply(instance, AbsorbTrailParticleOption::new));

    public static final ParticleOptions.Deserializer<AbsorbTrailParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        public AbsorbTrailParticleOption fromCommand(ParticleType<AbsorbTrailParticleOption> p_235961_, StringReader p_235962_) throws CommandSyntaxException {
            p_235962_.expect(' ');
            double x = p_235962_.readDouble();
            p_235962_.expect(' ');
            double y = p_235962_.readDouble();
            p_235962_.expect(' ');
            double z = p_235962_.readDouble();
            p_235962_.expect(' ');
            int color = p_235962_.readInt();
            p_235962_.expect(' ');
            int duration = p_235962_.readInt();
            return new AbsorbTrailParticleOption(new Vec3(x, y, z), color, duration);
        }

        public AbsorbTrailParticleOption fromNetwork(ParticleType<AbsorbTrailParticleOption> p_235964_, FriendlyByteBuf p_235965_) {
            return new AbsorbTrailParticleOption(new Vec3(p_235965_.readDouble(), p_235965_.readDouble(), p_235965_.readDouble()), p_235965_.readInt(), p_235965_.readInt());
        }
    };

    public ParticleType<AbsorbTrailParticleOption> getType() {
        return ModParticleTypes.ABSORB_TRAIL.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf p_123732_) {
        p_123732_.writeDouble(this.target().x);
        p_123732_.writeDouble(this.target().y);
        p_123732_.writeDouble(this.target().z);
        p_123732_.writeInt(this.color());
        p_123732_.writeInt(this.duration());
    }

    @Override
    public String writeToString() {
        Vec3 vec3 = this.target();
        double d0 = vec3.x();
        double d1 = vec3.y();
        double d2 = vec3.z();
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %s %s",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), d0, d1, d2, this.color, this.duration);
    }
}
