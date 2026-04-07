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

public class WindBlowParticleOption implements ParticleOptions {
    public static final Codec<WindBlowParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("red").forGetter(d -> d.red),
            Codec.FLOAT.fieldOf("green").forGetter(d -> d.green),
            Codec.FLOAT.fieldOf("blue").forGetter(d -> d.blue),
            Codec.INT.fieldOf("width").forGetter(d -> d.width),
            Codec.FLOAT.fieldOf("height").forGetter(d -> d.height),
            Codec.INT.fieldOf("life").forGetter(d -> d.life)
    ).apply(instance, WindBlowParticleOption::new));
    public static final Deserializer<WindBlowParticleOption> DESERIALIZER = new Deserializer<>() {
        public WindBlowParticleOption fromCommand(ParticleType<WindBlowParticleOption> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float red = reader.readFloat();
            reader.expect(' ');
            float green = reader.readFloat();
            reader.expect(' ');
            float blue = reader.readFloat();
            reader.expect(' ');
            int width = reader.readInt();
            reader.expect(' ');
            float height = reader.readFloat();
            reader.expect(' ');
            int life = reader.readInt();
            return new WindBlowParticleOption(red, green, blue, width, height, life);
        }

        public WindBlowParticleOption fromNetwork(ParticleType<WindBlowParticleOption> particleTypeIn, FriendlyByteBuf buffer) {
            return new WindBlowParticleOption(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt(), buffer.readFloat(), buffer.readInt());
        }
    };
    private final float red;
    private final float green;
    private final float blue;
    private final int width;
    private final float height;
    private final int life;

    public WindBlowParticleOption(ColorUtil color, int width, float height) {
        this.red = color.red();
        this.green = color.green();
        this.blue = color.blue();
        this.width = width;
        this.height = height;
        this.life = 0;
    }

    public WindBlowParticleOption(ColorUtil color, int width, float height, int life) {
        this.red = color.red();
        this.green = color.green();
        this.blue = color.blue();
        this.width = width;
        this.height = height;
        this.life = life;
    }

    public WindBlowParticleOption(float red, float green, float blue, int width, float height, int life) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.width = width;
        this.height = height;
        this.life = life;
    }

    public WindBlowParticleOption(float red, float green, float blue, int width, float height) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.width = width;
        this.height = height;
        this.life = 0;
    }

    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.red);
        buffer.writeFloat(this.green);
        buffer.writeFloat(this.blue);
        buffer.writeInt(this.width);
        buffer.writeFloat(this.height);
        buffer.writeInt(this.life);
    }

    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %d %.2f %d",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.width, this.height, this.life);
    }

    public ParticleType<WindBlowParticleOption> getType() {
        return ModParticleTypes.WIND_BLOW.get();
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

    public int getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public int getLife() {
        return this.life;
    }
}
