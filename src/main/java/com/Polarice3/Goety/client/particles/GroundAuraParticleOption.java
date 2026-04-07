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

public class GroundAuraParticleOption implements ParticleOptions {
    public static final Codec<GroundAuraParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("ownerId").forGetter(d -> d.ownerId),
            Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
            Codec.FLOAT.fieldOf("red").forGetter(d -> d.red),
            Codec.FLOAT.fieldOf("green").forGetter(d -> d.green),
            Codec.FLOAT.fieldOf("blue").forGetter(d -> d.blue)
    ).apply(instance, GroundAuraParticleOption::new));
    public static final Deserializer<GroundAuraParticleOption> DESERIALIZER = new Deserializer<GroundAuraParticleOption>() {
        public GroundAuraParticleOption fromCommand(ParticleType<GroundAuraParticleOption> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            int ownerId = reader.readInt();
            reader.expect(' ');
            float size = reader.readFloat();
            reader.expect(' ');
            float red = reader.readFloat();
            reader.expect(' ');
            float green = reader.readFloat();
            reader.expect(' ');
            float blue = reader.readFloat();
            return new GroundAuraParticleOption(ownerId, size, red, green, blue);
        }

        public GroundAuraParticleOption fromNetwork(ParticleType<GroundAuraParticleOption> particleTypeIn, FriendlyByteBuf buffer) {
            return new GroundAuraParticleOption(buffer.readInt(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }
    };
    private final int ownerId;
    private final float size;
    private final float red;
    private final float green;
    private final float blue;

    public GroundAuraParticleOption(int ownerId, float size, ColorUtil color) {
        this.ownerId = ownerId;
        this.size = size;
        this.red = color.red;
        this.green = color.green;
        this.blue = color.blue;
    }

    public GroundAuraParticleOption(int ownerId, float size, float red, float green, float blue) {
        this.ownerId = ownerId;
        this.size = size;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeInt(this.ownerId);
        buffer.writeFloat(this.size);
        buffer.writeFloat(this.red);
        buffer.writeFloat(this.green);
        buffer.writeFloat(this.blue);
    }

    public String writeToString() {
        return String.format(Locale.ROOT, "%s %d %f %f %f %f",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.ownerId, this.size, this.red, this.green, this.blue);
    }

    public ParticleType<GroundAuraParticleOption> getType() {
        return ModParticleTypes.GROUND_AURA.get();
    }

    public int getOwnerId() {
        return this.ownerId;
    }

    public float getSize(){
        return this.size;
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
}
