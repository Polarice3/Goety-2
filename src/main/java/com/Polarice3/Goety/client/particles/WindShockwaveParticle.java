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
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

public class WindShockwaveParticle extends WindParticle{
    public float increase;

    public WindShockwaveParticle(ClientLevel world, double x, double y, double z, float red, float green, float blue, float width, float height, float increase, float startRot, int ownerId) {
        super(world, x, y, z, red, green, blue, width, height, ownerId);
        this.increase = increase;
        this.initYRot = startRot * 360.0F;
        this.rotateAge = (10.0F + startRot * 10.0F);
    }

    @Override
    public Vec3 getOrbitPosition() {
        Vec3 position = this.getPosition();
        Vec3 vec3 = new Vec3(0.0D, this.height, this.width + (this.age * this.increase)).yRot((float)Math.toRadians(this.initYRot + this.rotateAge * (float)this.age));
        return position.add(vec3);
    }

    @Override
    public float getTrailHeight() {
        return 1.0F;
    }

    public static class Provider implements ParticleProvider<Option> {

        public Provider(SpriteSet p_172490_) {
        }

        public Particle createParticle(Option typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new WindShockwaveParticle(worldIn, x, y, z, typeIn.getRed(), typeIn.getGreen(), typeIn.getBlue(), typeIn.getWidth(), typeIn.getHeight(), typeIn.getIncrease(), typeIn.getStartYRot(), typeIn.getOwnerId());
        }
    }

    public static class Option implements ParticleOptions {
        public static final Codec<Option> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("red").forGetter(d -> d.red),
                Codec.FLOAT.fieldOf("green").forGetter(d -> d.green),
                Codec.FLOAT.fieldOf("blue").forGetter(d -> d.blue),
                Codec.FLOAT.fieldOf("width").forGetter(d -> d.width),
                Codec.FLOAT.fieldOf("height").forGetter(d -> d.height),
                Codec.FLOAT.fieldOf("increase").forGetter(d -> d.increase),
                Codec.FLOAT.fieldOf("startYRot").forGetter(d -> d.startYRot),
                Codec.INT.fieldOf("ownerId").forGetter(d -> d.ownerId)
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
                float width = reader.readFloat();
                reader.expect(' ');
                float height = reader.readFloat();
                reader.expect(' ');
                float increase = reader.readFloat();
                reader.expect(' ');
                float startYRot = reader.readFloat();
                reader.expect(' ');
                int ownerId = reader.readInt();
                return new Option(red, green, blue, width, height, increase, startYRot, ownerId);
            }

            public Option fromNetwork(ParticleType<Option> particleTypeIn, FriendlyByteBuf buffer) {
                return new Option(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt());
            }
        };
        private final float red;
        private final float green;
        private final float blue;
        private final float width;
        private final float height;
        private final float increase;
        private final float startYRot;
        private final int ownerId;

        public Option(ColorUtil color, float width, float height, float increase, float startYRot, int ownerId) {
            this.red = color.red();
            this.green = color.green();
            this.blue = color.blue();
            this.width = width;
            this.height = height;
            this.increase = increase;
            this.startYRot = startYRot;
            this.ownerId = ownerId;
        }

        public Option(float red, float green, float blue, float width, float height, float increase, float startYRot, int ownerId) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.width = width;
            this.height = height;
            this.increase = increase;
            this.startYRot = startYRot;
            this.ownerId = ownerId;
        }

        public void writeToNetwork(FriendlyByteBuf buffer) {
            buffer.writeFloat(this.red);
            buffer.writeFloat(this.green);
            buffer.writeFloat(this.blue);
            buffer.writeFloat(this.width);
            buffer.writeFloat(this.height);
            buffer.writeFloat(this.increase);
            buffer.writeFloat(this.startYRot);
            buffer.writeInt(this.ownerId);
        }

        public String writeToString() {
            return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f %d",
                    BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.width, this.height, this.increase, this.startYRot, this.ownerId);
        }

        public ParticleType<Option> getType() {
            return ModParticleTypes.WIND_SHOCKWAVE.get();
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

        public float getIncrease() {
            return this.increase;
        }

        public float getStartYRot() {
            return this.startYRot;
        }

        public int getOwnerId() {
            return this.ownerId;
        }
    }
}
