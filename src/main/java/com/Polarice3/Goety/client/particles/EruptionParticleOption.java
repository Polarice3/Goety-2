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

public class EruptionParticleOption implements ParticleOptions {
    private final ParticleType<EruptionParticleOption> type;
    public final float size;
    public final int life;
    public final float baseRed, baseGreen, baseBlue;
    public final float redAmp, greenAmp, blueAmp;

    public EruptionParticleOption(ParticleType<EruptionParticleOption> type, float size, int life, float baseRed, float baseGreen, float baseBlue, float redAmp, float greenAmp, float blueAmp) {
        this.type = type;
        this.size = size;
        this.life = life;
        this.baseRed = baseRed;
        this.baseGreen = baseGreen;
        this.baseBlue = baseBlue;
        this.redAmp = redAmp;
        this.greenAmp = greenAmp;
        this.blueAmp = blueAmp;
    }

    public static Codec<EruptionParticleOption> codec(ParticleType<EruptionParticleOption> type) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("size").forGetter(o -> o.size),
                Codec.INT.fieldOf("life").forGetter(o -> o.life),
                Codec.FLOAT.fieldOf("baseRed").forGetter(o -> o.baseRed),
                Codec.FLOAT.fieldOf("baseGreen").forGetter(o -> o.baseGreen),
                Codec.FLOAT.fieldOf("baseBlue").forGetter(o -> o.baseRed),
                Codec.FLOAT.fieldOf("redAmp").forGetter(o -> o.redAmp),
                Codec.FLOAT.fieldOf("greenAmp").forGetter(o -> o.greenAmp),
                Codec.FLOAT.fieldOf("blueAmp").forGetter(o -> o.blueAmp)
        ).apply(instance, (size, life, baseRed, baseGreen, baseBlue, redAmp, greenAmp, blueAmp) -> new EruptionParticleOption(type, size, life, baseRed, baseGreen, baseBlue, redAmp, greenAmp, blueAmp)));
    }

    public static ParticleType<EruptionParticleOption> createType() {
        return new ParticleType<>(true, DESERIALIZER) {
            private final Codec<EruptionParticleOption> codec = EruptionParticleOption.codec(this);

            @Override
            public @NotNull Codec<EruptionParticleOption> codec() {
                return this.codec;
            }
        };
    }

    @NotNull
    @Override
    public ParticleType<EruptionParticleOption> getType() {
        return this.type;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(this.size);
        buf.writeVarInt(this.life);
        buf.writeFloat(this.baseRed);
        buf.writeFloat(this.baseGreen);
        buf.writeFloat(this.baseBlue);
        buf.writeFloat(this.redAmp);
        buf.writeFloat(this.greenAmp);
        buf.writeFloat(this.blueAmp);
    }

    @NotNull
    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.3f %d %.3f %.3f %.3f %.3f %.3f %.3f",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.type), this.size, this.life, this.baseRed, this.baseGreen, this.baseBlue, this.redAmp, this.greenAmp, this.blueAmp);
    }

    @SuppressWarnings("deprecation")
    public static final Deserializer<EruptionParticleOption> DESERIALIZER = new Deserializer<>() {
        @NotNull
        @Override
        public EruptionParticleOption fromCommand(@NotNull ParticleType<EruptionParticleOption> type, @NotNull StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float size = reader.readFloat();
            reader.expect(' ');
            int life = reader.readInt();
            reader.expect(' ');
            float baseRed = reader.readFloat();
            reader.expect(' ');
            float baseGreen = reader.readFloat();
            reader.expect(' ');
            float baseBlue = reader.readFloat();
            reader.expect(' ');
            float redAmp = reader.readFloat();
            reader.expect(' ');
            float greenAmp = reader.readFloat();
            reader.expect(' ');
            float blueAmp = reader.readFloat();
            return new EruptionParticleOption(type, size, life, baseRed, baseGreen, baseBlue, redAmp, greenAmp, blueAmp);
        }

        @NotNull
        @Override
        public EruptionParticleOption fromNetwork(@NotNull ParticleType<EruptionParticleOption> type, FriendlyByteBuf buf) {
            return new EruptionParticleOption(type, buf.readFloat(), buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
        }
    };
}