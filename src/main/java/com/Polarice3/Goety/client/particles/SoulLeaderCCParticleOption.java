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

public class SoulLeaderCCParticleOption implements ParticleOptions {
    private final ParticleType<SoulLeaderCCParticleOption> type;
    public final float size;
    public final int life;
    public final float cx, cz;
    public final int color;

    public SoulLeaderCCParticleOption(ParticleType<SoulLeaderCCParticleOption> type, float size, int life, float cx, float cz, int color) {
        this.type = type;
        this.size = size;
        this.life = life;
        this.cx = cx;
        this.cz = cz;
        this.color = color;
    }

    public static Codec<SoulLeaderCCParticleOption> codec(ParticleType<SoulLeaderCCParticleOption> type) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("size").forGetter(o -> o.size),
                Codec.INT.fieldOf("life").forGetter(o -> o.life),
                Codec.FLOAT.fieldOf("cx").forGetter(o -> o.cx),
                Codec.FLOAT.fieldOf("cz").forGetter(o -> o.cz),
                Codec.INT.fieldOf("color").forGetter(o -> o.color)
        ).apply(instance, (size, life, cx, cz, color) -> new SoulLeaderCCParticleOption(type, size, life, cx, cz, color)));
    }

    public static ParticleType<SoulLeaderCCParticleOption> createType() {
        return new ParticleType<>(true, DESERIALIZER) {
            private final Codec<SoulLeaderCCParticleOption> codec = SoulLeaderCCParticleOption.codec(this);

            @Override
            public @NotNull Codec<SoulLeaderCCParticleOption> codec() {
                return this.codec;
            }
        };
    }

    @NotNull
    @Override
    public ParticleType<SoulLeaderCCParticleOption> getType() {
        return this.type;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(this.size);
        buf.writeVarInt(this.life);
        buf.writeFloat(this.cx);
        buf.writeFloat(this.cz);
        buf.writeVarInt(this.color);
    }

    @NotNull
    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.3f %d %.3f %.3f %d",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.type), this.size, this.life, this.cx, this.cz, this.color);
    }

    @SuppressWarnings("deprecation")
    public static final Deserializer<SoulLeaderCCParticleOption> DESERIALIZER = new Deserializer<>() {
        @NotNull
        @Override
        public SoulLeaderCCParticleOption fromCommand(@NotNull ParticleType<SoulLeaderCCParticleOption> type, @NotNull StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float size = reader.readFloat();
            reader.expect(' ');
            int life = reader.readInt();
            reader.expect(' ');
            float cx = reader.readFloat();
            reader.expect(' ');
            float cz = reader.readFloat();
            reader.expect(' ');
            int color = reader.readInt();
            return new SoulLeaderCCParticleOption(type, size, life, cx, cz, color);
        }

        @NotNull
        @Override
        public SoulLeaderCCParticleOption fromNetwork(@NotNull ParticleType<SoulLeaderCCParticleOption> type, FriendlyByteBuf buf) {
            return new SoulLeaderCCParticleOption(type, buf.readFloat(), buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readVarInt());
        }
    };
}
