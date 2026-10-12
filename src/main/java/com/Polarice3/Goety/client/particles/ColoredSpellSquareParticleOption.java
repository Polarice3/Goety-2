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

public class ColoredSpellSquareParticleOption implements ParticleOptions {
    public static final Codec<ColoredSpellSquareParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
            Codec.FLOAT.fieldOf("r").forGetter(d -> d.r),
            Codec.FLOAT.fieldOf("g").forGetter(d -> d.g),
            Codec.FLOAT.fieldOf("b").forGetter(d -> d.b),
            Codec.INT.fieldOf("life").forGetter(d -> d.life),
            Codec.BOOL.optionalFieldOf("flame", false).forGetter(d -> d.flame)
    ).apply(instance, ColoredSpellSquareParticleOption::new));
    public final float size;
    public final float r, g, b;
    public final int life;
    public final boolean flame;

    public ColoredSpellSquareParticleOption(float size, float r, float g, float b, int life) {
        this(size, r, g, b, life, false);
    }

    public ColoredSpellSquareParticleOption(float size, float r, float g, float b, int life, boolean flame) {
        this.size = size;
        this.r = r;
        this.g = g;
        this.b = b;
        this.life = life;
        this.flame = flame;
    }

    @NotNull
    @Override
    public ParticleType<ColoredSpellSquareParticleOption> getType() {
        return ModParticleTypes.COLORED_SPELL_SQUARE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(size);
        buf.writeFloat(r);
        buf.writeFloat(g);
        buf.writeFloat(b);
        buf.writeInt(life);
        buf.writeBoolean(flame);
    }

    @NotNull
    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %d %b",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.size, this.r, this.g, this.b, this.life, this.flame);
    }

    public static final Deserializer<ColoredSpellSquareParticleOption> DESERIALIZER = new Deserializer<>() {
        @NotNull
        @Override
        public ColoredSpellSquareParticleOption fromCommand(@NotNull ParticleType<ColoredSpellSquareParticleOption> type, @NotNull StringReader reader) throws CommandSyntaxException {
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
            boolean flame = false;
            if (reader.canRead() && reader.peek() == ' ') {
                reader.skip();
                flame = reader.readBoolean();
            }

            return new ColoredSpellSquareParticleOption(size, r, g, b, extraLife, flame);
        }

        @Override
        public ColoredSpellSquareParticleOption fromNetwork(@NotNull ParticleType<ColoredSpellSquareParticleOption> type, FriendlyByteBuf buf) {
            return new ColoredSpellSquareParticleOption(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt(), buf.readBoolean());
        }
    };
}
