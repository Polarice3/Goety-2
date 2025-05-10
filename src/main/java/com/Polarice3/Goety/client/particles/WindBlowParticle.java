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
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Locale;

public class WindBlowParticle extends WindTrailParticle {
    public final int width;
    public final float height;

    public WindBlowParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, float red, float green, float blue, int width, float height, int life) {
        super(world, x, y, z, 0, 0, 0, red, green, blue);
        this.gravity = 0.0F;
        this.xd *= (double)0.1F;
        this.yd *= (double)0.1F;
        this.zd *= (double)0.1F;
        this.xd += xd;
        this.yd += yd;
        this.zd += zd;
        if (life <= 0){
            this.lifetime = 20 + this.random.nextInt(20);
        } else {
            this.lifetime = life;
        }
        this.width = width;
        this.height = height;
    }

    public void tick() {
        super.tick();
        this.trailA = 1.0F - (float) this.age / (float) this.lifetime;
    }

    public float getTrailHeight() {
        return this.height;
    }

    public int sampleSize() {
        return this.width;
    }

    public int getLightColor(float pPartialTick) {
        return LightTexture.FULL_BLOCK;
    }

    public static class Provider implements ParticleProvider<Option> {

        public Provider(SpriteSet p_172490_) {
        }

        public Particle createParticle(Option typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new WindBlowParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getRed(), typeIn.getGreen(), typeIn.getBlue(), typeIn.getWidth(), typeIn.getHeight(), typeIn.getLife());
        }
    }

    public static class Option implements ParticleOptions {
        public static final Codec<Option> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("red").forGetter(d -> d.red),
                Codec.FLOAT.fieldOf("green").forGetter(d -> d.green),
                Codec.FLOAT.fieldOf("blue").forGetter(d -> d.blue),
                Codec.INT.fieldOf("width").forGetter(d -> d.width),
                Codec.FLOAT.fieldOf("height").forGetter(d -> d.height),
                Codec.INT.fieldOf("life").forGetter(d -> d.life)
        ).apply(instance, Option::new));
        public static final Deserializer<Option> DESERIALIZER = new Deserializer<>() {
            public Option fromCommand(ParticleType<Option> particleTypeIn, StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                float red = reader.readFloat();
                reader.expect(' ');
                float green = reader.readFloat();
                reader.expect(' ');
                float blue = reader.readFloat();
                reader.expect(' ');
                int width = reader.readInt();
                reader.expect(' ');
                float height = reader.readFloat();
                reader.expect(' ');
                int life = reader.readInt();
                return new Option(red, green, blue, width, height, life);
            }

            public Option fromNetwork(ParticleType<Option> particleTypeIn, FriendlyByteBuf buffer) {
                return new Option(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt(), buffer.readFloat(), buffer.readInt());
            }
        };
        private final float red;
        private final float green;
        private final float blue;
        private final int width;
        private final float height;
        private final int life;

        public Option(ColorUtil color, int width, float height) {
            this.red = color.red();
            this.green = color.green();
            this.blue = color.blue();
            this.width = width;
            this.height = height;
            this.life = 0;
        }

        public Option(ColorUtil color, int width, float height, int life) {
            this.red = color.red();
            this.green = color.green();
            this.blue = color.blue();
            this.width = width;
            this.height = height;
            this.life = life;
        }

        public Option(float red, float green, float blue, int width, float height, int life) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.width = width;
            this.height = height;
            this.life = life;
        }

        public Option(float red, float green, float blue, int width, float height) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.width = width;
            this.height = height;
            this.life = 0;
        }

        public void writeToNetwork(FriendlyByteBuf buffer) {
            buffer.writeFloat(this.red);
            buffer.writeFloat(this.green);
            buffer.writeFloat(this.blue);
            buffer.writeInt(this.width);
            buffer.writeFloat(this.height);
            buffer.writeInt(this.life);
        }

        public String writeToString() {
            return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %d %.2f %d",
                    BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.width, this.height, this.life);
        }

        public ParticleType<Option> getType() {
            return ModParticleTypes.WIND_BLOW.get();
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

        public int getWidth() {
            return this.width;
        }

        public float getHeight() {
            return this.height;
        }

        public int getLife() {
            return this.life;
        }
    }
}
