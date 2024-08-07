package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Locale;

public class WindParticleOption implements ParticleOptions {
    public static final Codec<WindParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("red").forGetter(d -> d.red),
            Codec.FLOAT.fieldOf("green").forGetter(d -> d.green),
            Codec.FLOAT.fieldOf("blue").forGetter(d -> d.blue),
            Codec.FLOAT.fieldOf("width").forGetter(d -> d.width),
            Codec.FLOAT.fieldOf("height").forGetter(d -> d.height),
            Codec.INT.fieldOf("ownerId").forGetter(d -> d.ownerId)
    ).apply(instance, WindParticleOption::new));
    public static final Deserializer<WindParticleOption> DESERIALIZER = new Deserializer<WindParticleOption>() {
        public WindParticleOption fromCommand(ParticleType<WindParticleOption> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float red = reader.readFloat();
            reader.expect(' ');
            float green = reader.readFloat();
            reader.expect(' ');
            float blue = reader.readFloat();
            reader.expect(' ');
            float width = reader.readFloat();
            reader.expect(' ');
            float height = reader.readFloat();
            reader.expect(' ');
            int ownerId = reader.readInt();
            return new WindParticleOption(red, green, blue, width, height, ownerId);
        }

        public WindParticleOption fromNetwork(ParticleType<WindParticleOption> particleTypeIn, FriendlyByteBuf buffer) {
            return new WindParticleOption(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt());
        }
    };
    private final float red;
    private final float green;
    private final float blue;
    private final float width;
    private final float height;
    private final int ownerId;

    public WindParticleOption(ColorUtil color, float width, float height, int ownerId) {
        this.red = color.red();
        this.green = color.green();
        this.blue = color.blue();
        this.width = width;
        this.height = height;
        this.ownerId = ownerId;
    }

    public WindParticleOption(float red, float green, float blue, float width, float height, int ownerId) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.width = width;
        this.height = height;
        this.ownerId = ownerId;
    }

    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.red);
        buffer.writeFloat(this.green);
        buffer.writeFloat(this.blue);
        buffer.writeFloat(this.width);
        buffer.writeFloat(this.height);
        buffer.writeInt(this.ownerId);
    }

    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %d",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.width, this.height, this.ownerId);
    }

    public ParticleType<WindParticleOption> getType() {
        return ModParticleTypes.WIND.get();
    }

    public float getRed() {
        return this.red;
    }

    public float getGreen() {
        return this.green;
    }

    public float getBlue() {
        return this.blue;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public int getOwnerId() {
        return this.ownerId;
    }
}
