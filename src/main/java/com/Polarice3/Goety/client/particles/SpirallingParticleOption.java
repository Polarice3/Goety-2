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
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class SpirallingParticleOption implements ParticleOptions {
    public static final Codec<SpirallingParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
            Codec.FLOAT.fieldOf("r").forGetter(d -> d.r),
            Codec.FLOAT.fieldOf("g").forGetter(d -> d.g),
            Codec.FLOAT.fieldOf("b").forGetter(d -> d.b),
            Codec.INT.fieldOf("life").forGetter(d -> d.life)
    ).apply(instance, SpirallingParticleOption::new));
    public final float size;
    public final float r, g, b;
    public final int life;

    public SpirallingParticleOption(float size, float r, float g, float b, int life) {
        this.size = size;
        this.r = r;
        this.g = g;
        this.b = b;
        this.life = life;
    }

    @NotNull
    @Override
    public ParticleType<SpirallingParticleOption> getType() {
        return ModParticleTypes.SPIRALLING.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(size);
        buf.writeFloat(r);
        buf.writeFloat(g);
        buf.writeFloat(b);
        buf.writeInt(life);
    }

    @NotNull
    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %d",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.size, this.r, this.g, this.b, this.life);
    }

    public static final Deserializer<SpirallingParticleOption> DESERIALIZER = new Deserializer<>() {
        @NotNull
        @Override
        public SpirallingParticleOption fromCommand(@NotNull ParticleType<SpirallingParticleOption> type, @NotNull StringReader reader) throws CommandSyntaxException {
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

            return new SpirallingParticleOption(size, r, g, b, extraLife);
        }

        @Override
        public SpirallingParticleOption fromNetwork(@NotNull ParticleType<SpirallingParticleOption> type, FriendlyByteBuf buf) {
            return new SpirallingParticleOption(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt());
        }
    };
}
