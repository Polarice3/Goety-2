package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Locale;

public class FoggyCloudParticleOption implements ParticleOptions {
    public static final Codec<FoggyCloudParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("red").forGetter(d -> d.red),
            Codec.FLOAT.fieldOf("green").forGetter(d -> d.green),
            Codec.FLOAT.fieldOf("blue").forGetter(d -> d.blue),
            Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
            Codec.INT.fieldOf("speed").forGetter(d -> d.speed),
            Codec.BOOL.fieldOf("gravity").forGetter(d -> d.gravity)
    ).apply(instance, FoggyCloudParticleOption::new));
    public static final Deserializer<FoggyCloudParticleOption> DESERIALIZER = new Deserializer<>() {
        public FoggyCloudParticleOption fromCommand(ParticleType<FoggyCloudParticleOption> p_235961_, StringReader p_235962_) throws CommandSyntaxException {
            p_235962_.expect(' ');
            float r = p_235962_.readFloat();
            p_235962_.expect(' ');
            float g = p_235962_.readFloat();
            p_235962_.expect(' ');
            float b = p_235962_.readFloat();
            p_235962_.expect(' ');
            float s = p_235962_.readFloat();
            p_235962_.expect(' ');
            int s2 = p_235962_.readInt();
            p_235962_.expect(' ');
            boolean g2 = p_235962_.readBoolean();
            return new FoggyCloudParticleOption(r, g, b, s, s2, g2);
        }

        public FoggyCloudParticleOption fromNetwork(ParticleType<FoggyCloudParticleOption> p_235964_, FriendlyByteBuf p_235965_) {
            return new FoggyCloudParticleOption(p_235965_.readFloat(), p_235965_.readFloat(), p_235965_.readFloat(), p_235965_.readFloat(), p_235965_.readInt(), p_235965_.readBoolean());
        }
    };
    private final float red;
    private final float green;
    private final float blue;
    private final float size;
    private final int speed;
    private final boolean gravity;

    public FoggyCloudParticleOption(ColorUtil colorUtil, float size, int speed) {
        this(colorUtil, size, speed, true);
    }

    public FoggyCloudParticleOption(ColorUtil colorUtil, float size, int speed, boolean gravity) {
        this.red = colorUtil.red;
        this.green = colorUtil.green;
        this.blue = colorUtil.blue;
        this.size = size;
        this.speed = speed;
        this.gravity = gravity;
    }

    public FoggyCloudParticleOption(float r, float g, float b, float size, int speed) {
        this(r, g, b, size, speed, true);
    }

    public FoggyCloudParticleOption(float r, float g, float b, float size, int speed, boolean gravity) {
        this.red = r;
        this.green = g;
        this.blue = b;
        this.size = size;
        this.speed = speed;
        this.gravity = gravity;
    }

    public void writeToNetwork(FriendlyByteBuf p_235956_) {
        p_235956_.writeFloat(this.red);
        p_235956_.writeFloat(this.green);
        p_235956_.writeFloat(this.blue);
        p_235956_.writeFloat(this.size);
        p_235956_.writeInt(this.speed);
        p_235956_.writeBoolean(this.gravity);
    }

    public ParticleType<FoggyCloudParticleOption> getType() {
        return ModParticleTypes.FOG_CLOUD.get();
    }

    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %s %s %s %s %s %s",
                Registry.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.size, this.speed, this.gravity);
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

    public boolean hasGravity() {
        return this.gravity;
    }
}
