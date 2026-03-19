package com.Polarice3.Goety.client.particles;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

public abstract class GatherFrostParticleOption implements ParticleOptions {
    /*public static final Codec<GatherFrostParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("endX").forGetter(d -> d.endX),
            Codec.FLOAT.fieldOf("endY").forGetter(d -> d.endY),
            Codec.FLOAT.fieldOf("endZ").forGetter(d -> d.endZ)
    ).apply(instance, GatherFrostParticleOption::new));
    public static final Deserializer<GatherFrostParticleOption> DESERIALIZER = new Deserializer<GatherFrostParticleOption>() {
        public GatherFrostParticleOption fromCommand(ParticleType<GatherFrostParticleOption> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float endX = reader.readFloat();
            reader.expect(' ');
            float endY = reader.readFloat();
            reader.expect(' ');
            float endZ = reader.readFloat();
            return new GatherFrostParticleOption(endX, endY, endZ);
        }

        public GatherFrostParticleOption fromNetwork(ParticleType<GatherFrostParticleOption> particleTypeIn, FriendlyByteBuf buffer) {
            return new GatherFrostParticleOption(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }
    };*/
    private final float endX;
    private final float endY;
    private final float endZ;

    public GatherFrostParticleOption(Vec3 end) {
        this.endX = (float) end.x;
        this.endY = (float) end.y;
        this.endZ = (float) end.z;
    }

    public GatherFrostParticleOption(float endX, float endY, float endZ) {
        this.endX = endX;
        this.endY = endY;
        this.endZ = endZ;
    }

    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.endX);
        buffer.writeFloat(this.endY);
        buffer.writeFloat(this.endZ);
    }

    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.endX, this.endY, this.endZ);
    }

    /*public ParticleType<GatherFrostParticleOption> getType() {
        return ModParticleTypes.FROST_GATHER.get();
    }*/

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
