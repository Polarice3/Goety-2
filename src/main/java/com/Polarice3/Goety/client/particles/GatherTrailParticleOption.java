package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

public abstract class GatherTrailParticleOption implements ParticleOptions {
    /*public static final Codec<GatherTrailParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("red").forGetter(d -> d.red),
            Codec.FLOAT.fieldOf("green").forGetter(d -> d.green),
            Codec.FLOAT.fieldOf("blue").forGetter(d -> d.blue),
            Codec.FLOAT.fieldOf("endX").forGetter(d -> d.endX),
            Codec.FLOAT.fieldOf("endY").forGetter(d -> d.endY),
            Codec.FLOAT.fieldOf("endZ").forGetter(d -> d.endZ)
    ).apply(instance, GatherTrailParticleOption::new));
    public static final Deserializer<GatherTrailParticleOption> DESERIALIZER = new Deserializer<GatherTrailParticleOption>() {
        public GatherTrailParticleOption fromCommand(ParticleType<GatherTrailParticleOption> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float red = reader.readFloat();
            reader.expect(' ');
            float green = reader.readFloat();
            reader.expect(' ');
            float blue = reader.readFloat();
            reader.expect(' ');
            float endX = reader.readFloat();
            reader.expect(' ');
            float endY = reader.readFloat();
            reader.expect(' ');
            float endZ = reader.readFloat();
            return new GatherTrailParticleOption(red, green, blue, endX, endY, endZ);
        }

        public GatherTrailParticleOption fromNetwork(ParticleType<GatherTrailParticleOption> particleTypeIn, FriendlyByteBuf buffer) {
            return new GatherTrailParticleOption(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }
    };*/
    private final float red;
    private final float green;
    private final float blue;
    private final float endX;
    private final float endY;
    private final float endZ;

    public GatherTrailParticleOption(ColorUtil color, Vec3 end) {
        this.red = color.red();
        this.green = color.green();
        this.blue = color.blue();
        this.endX = (float) end.x;
        this.endY = (float) end.y;
        this.endZ = (float) end.z;
    }

    public GatherTrailParticleOption(float red, float green, float blue, Vec3 end) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.endX = (float) end.x;
        this.endY = (float) end.y;
        this.endZ = (float) end.z;
    }

    public GatherTrailParticleOption(float red, float green, float blue, float endX, float endY, float endZ) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.endX = endX;
        this.endY = endY;
        this.endZ = endZ;
    }

    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.red);
        buffer.writeFloat(this.green);
        buffer.writeFloat(this.blue);
        buffer.writeFloat(this.endX);
        buffer.writeFloat(this.endY);
        buffer.writeFloat(this.endZ);
    }

    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.endX, this.endY, this.endZ);
    }

    /*public ParticleType<GatherTrailParticleOption> getType() {
        return ModParticleTypes.GATHER_TRAIL.get();
    }*/

    public float getRed() {
        return this.red;
    }

    public float getGreen() {
        return this.green;
    }

    public float getBlue() {
        return this.blue;
    }

    public float getEndX() {
        return this.endX;
    }

    public float getEndY() {
        return this.endY;
    }

    public float getEndZ() {
        return this.endZ;
    }
}
