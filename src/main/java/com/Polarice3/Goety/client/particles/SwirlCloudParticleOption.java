package com.Polarice3.Goety.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

public class SwirlCloudParticleOption implements ParticleOptions {
    public static final Codec<SwirlCloudParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("ownerId").forGetter(d -> d.ownerId),
            Codec.BOOL.fieldOf("reversed").forGetter(d -> d.reversed),
            Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
            Codec.DOUBLE.fieldOf("radius").forGetter(d -> d.radius),
            Codec.FLOAT.fieldOf("time").forGetter(d -> d.time),
            Codec.FLOAT.fieldOf("speed").forGetter(d -> d.speed),
            Codec.FLOAT.fieldOf("roll").forGetter(d -> d.speed),
            Vec3.CODEC.fieldOf("offset").forGetter(d -> d.offset)
    ).apply(instance, SwirlCloudParticleOption::new));
    public static final Deserializer<SwirlCloudParticleOption> DESERIALIZER = new Deserializer<SwirlCloudParticleOption>() {
        public SwirlCloudParticleOption fromCommand(ParticleType<SwirlCloudParticleOption> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            int ownerId = reader.readInt();
            reader.expect(' ');
            boolean reversed = reader.readBoolean();
            reader.expect(' ');
            float size = reader.readFloat();
            reader.expect(' ');
            double radius = reader.readDouble();
            reader.expect(' ');
            float time = reader.readFloat();
            reader.expect(' ');
            float speed = reader.readFloat();
            reader.expect(' ');
            float roll = reader.readFloat();
            reader.expect(' ');
            double x = reader.readDouble();
            reader.expect(' ');
            double y = reader.readDouble();
            reader.expect(' ');
            double z = reader.readDouble();
            return new SwirlCloudParticleOption(ownerId, reversed, size, radius, time, speed, roll, new Vec3(x, y, z));
        }

        public SwirlCloudParticleOption fromNetwork(ParticleType<SwirlCloudParticleOption> particleTypeIn, FriendlyByteBuf buffer) {
            return new SwirlCloudParticleOption(buffer.readInt(), buffer.readBoolean(), buffer.readFloat(), buffer.readDouble(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
        }
    };
    private final int ownerId;
    private final boolean reversed;
    private final float size;
    private final double radius;
    private final float time;
    private final float speed;
    private final float roll;
    private final Vec3 offset;

    public SwirlCloudParticleOption(int ownerId, boolean reversed, float size, double radius, float time, float speed, float roll, Vec3 offset) {
        this.ownerId = ownerId;
        this.reversed = reversed;
        this.size = size;
        this.radius = radius;
        this.time = time;
        this.speed = speed;
        this.roll = roll;
        this.offset = offset;
    }

    public SwirlCloudParticleOption(int ownerId, float size, double radius, float time, float speed, Vec3 offset) {
        this(ownerId, false, size, radius, time, speed, (RandomSource.create().nextFloat() - 0.5F) * 0.1F, offset);
    }

    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeInt(this.ownerId);
        buffer.writeBoolean(this.reversed);
        buffer.writeFloat(this.size);
        buffer.writeDouble(this.radius);
        buffer.writeFloat(this.time);
        buffer.writeFloat(this.speed);
        buffer.writeFloat(this.roll);
        buffer.writeDouble(this.offset.x);
        buffer.writeDouble(this.offset.y);
        buffer.writeDouble(this.offset.z);
    }

    public String writeToString() {
        Vec3 vec3 = this.offset;
        double d0 = vec3.x();
        double d1 = vec3.y();
        double d2 = vec3.z();
        return String.format(Locale.ROOT, "%s %b %s %f %.2f %f %f %f %.2f %.2f %.2f",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.ownerId, this.reversed, this.size, this.radius, this.time, this.speed, this.roll, d0, d1, d2);
    }

    public ParticleType<SwirlCloudParticleOption> getType() {
        return ModParticleTypes.SWIRL_CLOUD.get();
    }

    public int getOwnerId() {
        return this.ownerId;
    }

    public boolean isReversed() {
        return this.reversed;
    }

    public float getSize(){
        return this.size;
    }

    public double getRadius() {
        return this.radius;
    }

    public float getTime() {
        return this.time;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getRoll() {
        return this.roll;
    }

    public Vec3 getOffset() {
        return this.offset;
    }
}
