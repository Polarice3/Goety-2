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

public class SlamParticleOption implements ParticleOptions {
    public static final Codec<SlamParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("red").forGetter(d -> d.red),
            Codec.FLOAT.fieldOf("green").forGetter(d -> d.green),
            Codec.FLOAT.fieldOf("blue").forGetter(d -> d.blue),
            Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
            Codec.INT.fieldOf("lifespan").forGetter(d -> d.lifespan)
    ).apply(instance, SlamParticleOption::new));
    public static final Deserializer<SlamParticleOption> DESERIALIZER = new Deserializer<>() {
        public SlamParticleOption fromCommand(ParticleType<SlamParticleOption> particleType, StringReader reader) throws CommandSyntaxException {
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
            return new SlamParticleOption(r, g, b, size, lifespan);
        }

        public SlamParticleOption fromNetwork(ParticleType<SlamParticleOption> particleType, FriendlyByteBuf byteBuf) {
            return new SlamParticleOption(byteBuf.readFloat(), byteBuf.readFloat(), byteBuf.readFloat(), byteBuf.readFloat(), byteBuf.readInt());
        }
    };
    private final float red;
    private final float green;
    private final float blue;
    private final float size;
    private final int lifespan;

    public SlamParticleOption(ColorUtil colorUtil, float size, int lifespan) {
        this.red = colorUtil.red;
        this.green = colorUtil.green;
        this.blue = colorUtil.blue;
        this.size = size;
        this.lifespan = lifespan;
    }

    public SlamParticleOption(float r, float g, float b, float size, int lifespan) {
        this.red = r;
        this.green = g;
        this.blue = b;
        this.size = size;
        this.lifespan = lifespan;
    }

    public void writeToNetwork(FriendlyByteBuf byteBuf) {
        byteBuf.writeFloat(this.red);
        byteBuf.writeFloat(this.green);
        byteBuf.writeFloat(this.blue);
        byteBuf.writeFloat(this.size);
        byteBuf.writeInt(this.lifespan);
    }

    public ParticleType<SlamParticleOption> getType() {
        return ModParticleTypes.SLAM.get();
    }

    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %s",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.size, this.lifespan);
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

    public int getLifespan() {
        return this.lifespan;
    }
}
