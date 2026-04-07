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

public class SmashParticleOption implements ParticleOptions {
    public static final Codec<SmashParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("red").forGetter(d -> d.red),
            Codec.FLOAT.fieldOf("green").forGetter(d -> d.green),
            Codec.FLOAT.fieldOf("blue").forGetter(d -> d.blue),
            Codec.FLOAT.fieldOf("width").forGetter(d -> d.width),
            Codec.FLOAT.fieldOf("height").forGetter(d -> d.height),
            Codec.FLOAT.fieldOf("speed").forGetter(d -> d.speed),
            Codec.INT.fieldOf("lifespan").forGetter(d -> d.lifespan)
    ).apply(instance, SmashParticleOption::new));
    public static final Deserializer<SmashParticleOption> DESERIALIZER = new Deserializer<>() {
        public SmashParticleOption fromCommand(ParticleType<SmashParticleOption> particleType, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            float width = reader.readFloat();
            reader.expect(' ');
            float height = reader.readFloat();
            reader.expect(' ');
            float speed = reader.readFloat();
            reader.expect(' ');
            int lifespan = reader.readInt();
            return new SmashParticleOption(r, g, b, width, height, speed, lifespan);
        }

        public SmashParticleOption fromNetwork(ParticleType<SmashParticleOption> particleType, FriendlyByteBuf byteBuf) {
            return new SmashParticleOption(byteBuf.readFloat(), byteBuf.readFloat(), byteBuf.readFloat(), byteBuf.readFloat(), byteBuf.readFloat(), byteBuf.readFloat(), byteBuf.readInt());
        }
    };
    private final float red;
    private final float green;
    private final float blue;
    private final float width;
    private final float height;
    private final float speed;
    private final int lifespan;

    public SmashParticleOption(ColorUtil colorUtil, float width, float height, float speed, int lifespan) {
        this(colorUtil.red, colorUtil.green, colorUtil.blue, width, height, speed, lifespan);
    }

    public SmashParticleOption(ColorUtil colorUtil, float width, float height, int lifespan) {
        this(colorUtil, width, height, 0, lifespan);
    }

    public SmashParticleOption(ColorUtil colorUtil, float width, int lifespan) {
        this(colorUtil, width, width * 0.06F, lifespan);
    }

    public SmashParticleOption(float r, float g, float b, float width, float height, float speed, int lifespan) {
        this.red = r;
        this.green = g;
        this.blue = b;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.lifespan = lifespan;
    }

    public SmashParticleOption(float r, float g, float b, float width, float height, int lifespan) {
        this(r, g, b, width, height, 0, lifespan);
    }

    public SmashParticleOption(float r, float g, float b, float width, int lifespan) {
        this(r, g, b, width, width * 0.06F, lifespan);
    }

    public void writeToNetwork(FriendlyByteBuf byteBuf) {
        byteBuf.writeFloat(this.red);
        byteBuf.writeFloat(this.green);
        byteBuf.writeFloat(this.blue);
        byteBuf.writeFloat(this.width);
        byteBuf.writeFloat(this.height);
        byteBuf.writeFloat(this.speed);
        byteBuf.writeInt(this.lifespan);
    }

    public ParticleType<SmashParticleOption> getType() {
        return ModParticleTypes.SMASH.get();
    }

    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %s",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.width, this.height, this.speed, this.lifespan);
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

    public float getSpeed() {
        return this.speed;
    }

    public int getLifespan() {
        return this.lifespan;
    }
}
