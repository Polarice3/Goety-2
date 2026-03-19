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

public class SphereExplodeParticleOption implements ParticleOptions {
    public static final Codec<SphereExplodeParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("red").forGetter(d -> d.red),
            Codec.FLOAT.fieldOf("green").forGetter(d -> d.green),
            Codec.FLOAT.fieldOf("blue").forGetter(d -> d.blue),
            Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
            Codec.INT.fieldOf("speed").forGetter(d -> d.speed)
    ).apply(instance, SphereExplodeParticleOption::new));
    public static final Deserializer<SphereExplodeParticleOption> DESERIALIZER = new Deserializer<>() {
        public SphereExplodeParticleOption fromCommand(ParticleType<SphereExplodeParticleOption> particleType, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            float size = reader.readFloat();
            reader.expect(' ');
            int lifespan = reader.readInt();
            return new SphereExplodeParticleOption(r, g, b, size, lifespan);
        }

        public SphereExplodeParticleOption fromNetwork(ParticleType<SphereExplodeParticleOption> particleType, FriendlyByteBuf byteBuf) {
            return new SphereExplodeParticleOption(byteBuf.readFloat(), byteBuf.readFloat(), byteBuf.readFloat(), byteBuf.readFloat(), byteBuf.readInt());
        }
    };
    private final float red;
    private final float green;
    private final float blue;
    private final float size;
    private final int speed;

    public SphereExplodeParticleOption(ColorUtil colorUtil, float size, int speed) {
        this.red = colorUtil.red;
        this.green = colorUtil.green;
        this.blue = colorUtil.blue;
        this.size = size;
        this.speed = speed;
    }

    public SphereExplodeParticleOption(float r, float g, float b, float size, int speed) {
        this.red = r;
        this.green = g;
        this.blue = b;
        this.size = size;
        this.speed = speed;
    }

    public void writeToNetwork(FriendlyByteBuf byteBuf) {
        byteBuf.writeFloat(this.red);
        byteBuf.writeFloat(this.green);
        byteBuf.writeFloat(this.blue);
        byteBuf.writeFloat(this.size);
        byteBuf.writeInt(this.speed);
    }

    public ParticleType<SphereExplodeParticleOption> getType() {
        return ModParticleTypes.SPHERE_EXPLODE.get();
    }

    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %s",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.size, this.speed);
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

    public float getSize() {
        return this.size;
    }

    public int getSpeed() {
        return this.speed;
    }
}
