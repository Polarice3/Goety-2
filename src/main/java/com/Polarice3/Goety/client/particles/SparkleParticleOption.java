/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package com.Polarice3.Goety.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class SparkleParticleOption implements ParticleOptions {
    public static final Codec<SparkleParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
            Codec.FLOAT.fieldOf("r").forGetter(d -> d.r),
            Codec.FLOAT.fieldOf("g").forGetter(d -> d.g),
            Codec.FLOAT.fieldOf("b").forGetter(d -> d.b),
            Codec.INT.fieldOf("extraLife").forGetter(d -> d.extraLife)
    ).apply(instance, SparkleParticleOption::new));
    public final float size;
    public final float r, g, b;
    public final int extraLife;

    public SparkleParticleOption(float size, float r, float g, float b, int extraLife) {
        this.size = size;
        this.r = r;
        this.g = g;
        this.b = b;
        this.extraLife = extraLife;
    }

    @NotNull
    @Override
    public ParticleType<SparkleParticleOption> getType() {
        return ModParticleTypes.SPARKLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(size);
        buf.writeFloat(r);
        buf.writeFloat(g);
        buf.writeFloat(b);
        buf.writeInt(extraLife);
    }

    @NotNull
    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %d %s %s %s",
                Registry.PARTICLE_TYPE.getKey(this.getType()), this.size, this.r, this.g, this.b, this.extraLife);
    }

    public static final Deserializer<SparkleParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @NotNull
        @Override
        public SparkleParticleOption fromCommand(@NotNull ParticleType<SparkleParticleOption> type, @NotNull StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float size = reader.readFloat();
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            int extraLife = reader.readInt();

            return new SparkleParticleOption(size, r, g, b, extraLife);
        }

        @Override
        public SparkleParticleOption fromNetwork(@NotNull ParticleType<SparkleParticleOption> type, FriendlyByteBuf buf) {
            return new SparkleParticleOption(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt());
        }
    };
}
