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

public class WindShockwaveParticleOption implements ParticleOptions {
    public static final Codec<WindShockwaveParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("red").forGetter(d -> d.red),
            Codec.FLOAT.fieldOf("green").forGetter(d -> d.green),
            Codec.FLOAT.fieldOf("blue").forGetter(d -> d.blue),
            Codec.FLOAT.fieldOf("width").forGetter(d -> d.width),
            Codec.FLOAT.fieldOf("height").forGetter(d -> d.height),
            Codec.FLOAT.fieldOf("increase").forGetter(d -> d.increase),
            Codec.FLOAT.fieldOf("startYRot").forGetter(d -> d.startYRot),
            Codec.INT.fieldOf("life").forGetter(d -> d.life),
            Codec.INT.fieldOf("ownerId").forGetter(d -> d.ownerId)
    ).apply(instance, WindShockwaveParticleOption::new));
    public static final Deserializer<WindShockwaveParticleOption> DESERIALIZER = new Deserializer<WindShockwaveParticleOption>() {
        public WindShockwaveParticleOption fromCommand(ParticleType<WindShockwaveParticleOption> particleTypeIn, StringReader reader) throws CommandSyntaxException {
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
            float increase = reader.readFloat();
            reader.expect(' ');
            float startYRot = reader.readFloat();
            reader.expect(' ');
            int life = reader.readInt();
            reader.expect(' ');
            int ownerId = reader.readInt();
            return new WindShockwaveParticleOption(red, green, blue, width, height, increase, startYRot, life, ownerId);
        }

        public WindShockwaveParticleOption fromNetwork(ParticleType<WindShockwaveParticleOption> particleTypeIn, FriendlyByteBuf buffer) {
            return new WindShockwaveParticleOption(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt(), buffer.readInt());
        }
    };
    private final float red;
    private final float green;
    private final float blue;
    private final float width;
    private final float height;
    private final float increase;
    private final float startYRot;
    private final int life;
    private final int ownerId;

    public WindShockwaveParticleOption(ColorUtil color, float width, float height, float increase, float startYRot, int ownerId) {
        this.red = color.red();
        this.green = color.green();
        this.blue = color.blue();
        this.width = width;
        this.height = height;
        this.increase = increase;
        this.startYRot = startYRot;
        this.life = 0;
        this.ownerId = ownerId;
    }

    public WindShockwaveParticleOption(ColorUtil color, float width, float height, float increase, float startYRot, int life, int ownerId) {
        this.red = color.red();
        this.green = color.green();
        this.blue = color.blue();
        this.width = width;
        this.height = height;
        this.increase = increase;
        this.startYRot = startYRot;
        this.life = life;
        this.ownerId = ownerId;
    }

    public WindShockwaveParticleOption(float red, float green, float blue, float width, float height, float increase, float startYRot, int ownerId) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.width = width;
        this.height = height;
        this.increase = increase;
        this.startYRot = startYRot;
        this.life = 0;
        this.ownerId = ownerId;
    }

    public WindShockwaveParticleOption(float red, float green, float blue, float width, float height, float increase, float startYRot, int life, int ownerId) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.width = width;
        this.height = height;
        this.increase = increase;
        this.startYRot = startYRot;
        this.life = life;
        this.ownerId = ownerId;
    }

    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.red);
        buffer.writeFloat(this.green);
        buffer.writeFloat(this.blue);
        buffer.writeFloat(this.width);
        buffer.writeFloat(this.height);
        buffer.writeFloat(this.increase);
        buffer.writeFloat(this.startYRot);
        buffer.writeInt(this.life);
        buffer.writeInt(this.ownerId);
    }

    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f %d %d",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.width, this.height, this.increase, this.startYRot, this.life, this.ownerId);
    }

    public ParticleType<WindShockwaveParticleOption> getType() {
        return ModParticleTypes.WIND_SHOCKWAVE.get();
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

    public float getIncrease() {
        return this.increase;
    }

    public float getStartYRot() {
        return this.startYRot;
    }

    public int getLife() {
        return this.life;
    }

    public int getOwnerId() {
        return this.ownerId;
    }
}
