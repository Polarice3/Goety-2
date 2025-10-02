package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

public class GroundAuraParticle extends GroundCircleParticle {
    private final float rotSpeed;
    public final int ownerId;
    public final Vec3 origin;

    protected GroundAuraParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet spriteSet, int ownerId, float size) {
        super(level, x, y, z);
        this.ownerId = ownerId;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.origin = new Vec3(x, y, z);
        this.hasPhysics = false;
        this.quadSize = size;
        this.lifetime = 20;
        this.setSpriteFromAge(spriteSet);
        this.rotSpeed = ((float)Math.random() - 0.5F) * 0.1F;
        this.roll = (float)Math.random() * ((float)Math.PI * 2F);
    }

    public Vec3 getPosition() {
        Entity owner = this.getEntity();
        return owner != null ? new Vec3(owner.getX(), owner.getY() + 0.25F, owner.getZ()) : this.origin;
    }

    public Entity getEntity() {
        return this.ownerId == -1 ? null : this.level.getEntity(this.ownerId);
    }

    public int getLightColor(final float partialTicks) {
        return 240;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        Vec3 vec3 = this.getPosition();
        this.setPos(vec3.x, vec3.y, vec3.z);
        if (++this.age >= this.lifetime || this.getEntity() == null) {
            this.remove();
        }
        this.oRoll = this.roll;
        this.roll += (float)Math.PI * this.rotSpeed * 2.0F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<GroundAuraParticle.Option> {
        private final SpriteSet sprites;

        public Provider(SpriteSet p_i50607_1_) {
            this.sprites = p_i50607_1_;
        }

        public Particle createParticle(GroundAuraParticle.Option pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new GroundAuraParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites, pType.getOwnerId(), pType.getSize());
        }
    }

    public static class Option implements ParticleOptions {
        public static final Codec<GroundAuraParticle.Option> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("ownerId").forGetter(d -> d.ownerId),
                Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
                Codec.FLOAT.fieldOf("red").forGetter(d -> d.red),
                Codec.FLOAT.fieldOf("green").forGetter(d -> d.green),
                Codec.FLOAT.fieldOf("blue").forGetter(d -> d.blue)
        ).apply(instance, GroundAuraParticle.Option::new));
        public static final Deserializer<GroundAuraParticle.Option> DESERIALIZER = new Deserializer<GroundAuraParticle.Option>() {
            public GroundAuraParticle.Option fromCommand(ParticleType<GroundAuraParticle.Option> particleTypeIn, StringReader reader) throws CommandSyntaxException {
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
                return new GroundAuraParticle.Option(ownerId, size, red, green, blue);
            }

            public GroundAuraParticle.Option fromNetwork(ParticleType<GroundAuraParticle.Option> particleTypeIn, FriendlyByteBuf buffer) {
                return new GroundAuraParticle.Option(buffer.readInt(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
            }
        };
        private final int ownerId;
        private final float size;
        private final float red;
        private final float green;
        private final float blue;

        public Option(int ownerId, float size, ColorUtil color) {
            this.ownerId = ownerId;
            this.size = size;
            this.red = color.red;
            this.green = color.green;
            this.blue = color.blue;
        }

        public Option(int ownerId, float size, float red, float green, float blue) {
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

        public ParticleType<GroundAuraParticle.Option> getType() {
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
}
