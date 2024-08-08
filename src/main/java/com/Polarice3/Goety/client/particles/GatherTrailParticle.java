package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class GatherTrailParticle extends WindTrailParticle {
    public final Vec3 origin;
    public final Vec3 end;

    protected GatherTrailParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, float red, float green, float blue, Vec3 end) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D, red, green, blue);
        this.origin = new Vec3(x, y, z);
        this.end = end;
        this.xo = this.origin.x();
        this.yo = this.origin.y();
        this.zo = this.origin.z();
        this.x = this.xo;
        this.y = this.yo;
        this.z = this.zo;
        this.xd = this.end.x - this.origin.x;
        this.yd = this.end.y - this.origin.y;
        this.zd = this.end.z - this.origin.z;
        this.hasPhysics = false;
        this.lifetime = (int)(Math.random() * 10.0D) + 30;
    }

    public void tick() {
        this.trail();
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.xd *= 0.5D;
        this.yd *= 0.5D;
        this.zd *= 0.5D;
        if (this.age++ >= this.lifetime || this.getPos().distanceTo(this.end) < 0.25D) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
        }
    }

    public @NotNull Vec3 getPos() {
        return new Vec3(this.x, this.y, this.z);
    }

    public int sampleSize() {
        return 4;
    }

    public static class Provider implements ParticleProvider<Option> {

        public Provider(SpriteSet p_i50607_1_) {
        }

        public Particle createParticle(Option pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new GatherTrailParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType.red, pType.green, pType.blue, new Vec3(pType.endX, pType.endY, pType.endZ));
        }
    }

    public static class Option implements ParticleOptions {
        public static final Codec<Option> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("red").forGetter(d -> d.red),
                Codec.FLOAT.fieldOf("green").forGetter(d -> d.green),
                Codec.FLOAT.fieldOf("blue").forGetter(d -> d.blue),
                Codec.FLOAT.fieldOf("endX").forGetter(d -> d.endX),
                Codec.FLOAT.fieldOf("endY").forGetter(d -> d.endY),
                Codec.FLOAT.fieldOf("endZ").forGetter(d -> d.endZ)
        ).apply(instance, Option::new));
        public static final Deserializer<Option> DESERIALIZER = new Deserializer<Option>() {
            public Option fromCommand(ParticleType<Option> particleTypeIn, StringReader reader) throws CommandSyntaxException {
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
                return new Option(red, green, blue, endX, endY, endZ);
            }

            public Option fromNetwork(ParticleType<Option> particleTypeIn, FriendlyByteBuf buffer) {
                return new Option(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
            }
        };
        private final float red;
        private final float green;
        private final float blue;
        private final float endX;
        private final float endY;
        private final float endZ;

        public Option(ColorUtil color, Vec3 end) {
            this.red = color.red();
            this.green = color.green();
            this.blue = color.blue();
            this.endX = (float) end.x;
            this.endY = (float) end.y;
            this.endZ = (float) end.z;
        }

        public Option(float red, float green, float blue, Vec3 end) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.endX = (float) end.x;
            this.endY = (float) end.y;
            this.endZ = (float) end.z;
        }

        public Option(float red, float green, float blue, float endX, float endY, float endZ) {
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
                    Registry.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.endX, this.endY, this.endZ);
        }

        public ParticleType<Option> getType() {
            return ModParticleTypes.GATHER_TRAIL.get();
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
}
