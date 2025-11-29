package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.client.Timer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

import java.util.Locale;

public class MagicSmokeParticle extends TextureSheetParticle {
    public Timer timer;
    public int colorFrom;
    public int colorTo;

    public MagicSmokeParticle(ClientLevel clientLevel, double x, double y, double z, double xd, double yd, double zd, int colorFrom, int colorTo, int duration, float size, float gravity) {
        super(clientLevel, x, y, z, xd, yd, zd);
        this.friction = 0.96F;
        this.gravity = gravity;
        this.speedUpWhenYMotionIsBlocked = true;
        this.xd = xd == 0.0D ? (this.random.nextDouble() * 2 - 1) / 10 : xd;
        this.yd = yd == 0.0D ? 0.1D + this.random.nextDouble() / 10 : yd;
        this.zd = zd == 0.0D ? (this.random.nextDouble() * 2 - 1) / 10 : zd;
        this.xd *= 0.5F;
        this.zd *= 0.5F;
        ColorUtil colorUtil = new ColorUtil(colorFrom);
        this.rCol = colorUtil.red();
        this.gCol = colorUtil.green();
        this.bCol = colorUtil.blue();
        this.colorFrom = colorFrom;
        this.colorTo = colorTo;
        this.quadSize = size;
        this.lifetime = duration;
        this.timer = new Timer(duration + 1, 0);
        this.hasPhysics = true;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public float getQuadSize(float p_105642_) {
        return this.quadSize * Mth.clamp(((float)this.age + p_105642_) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        ++this.age;
        if (this.age >= this.lifetime) {
            this.remove();
        } else {
            this.yd -= 0.04D * (double)this.gravity;
            this.move(this.xd, this.yd, this.zd);
            if (this.speedUpWhenYMotionIsBlocked && this.y == this.yo) {
                this.xd *= 1.1D;
                this.zd *= 1.1D;
            }

            this.xd *= (double)this.friction;
            this.yd *= (double)this.friction;
            this.zd *= (double)this.friction;
            if (this.onGround) {
                this.xd *= (double)0.7F;
                this.zd *= (double)0.7F;
            }

            this.timer.advanceTime(Util.getMillis());
            float lerp = (this.age + this.timer.partialTick) / this.lifetime;
            int newColor = FastColor.ARGB32.lerp(lerp, this.colorFrom, this.colorTo);
            ColorUtil colorUtil = new ColorUtil(newColor);
            this.rCol = colorUtil.red();
            this.gCol = colorUtil.green();
            this.bCol = colorUtil.blue();
        }
    }

    @Override
    public int getLightColor(float f) {
        return LightTexture.FULL_BRIGHT;
    }

    public static class Provider implements ParticleProvider<Option> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        public Particle createParticle(Option option, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            MagicSmokeParticle trailParticle = new MagicSmokeParticle(clientLevel, d, e, f, g, h, i, option.getColorFrom(), option.getColorTo(), option.getDuration(), option.getSize(), option.getGravity());
            trailParticle.pickSprite(this.sprite);
            return trailParticle;
        }
    }

    public static class Option implements ParticleOptions {
        public static final Codec<Option> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("colorFrom").forGetter(Option::getColorFrom),
                Codec.INT.fieldOf("colorTo").forGetter(Option::getColorTo),
                ExtraCodecs.POSITIVE_INT.fieldOf("duration").forGetter(Option::getDuration),
                Codec.FLOAT.fieldOf("size").forGetter(Option::getSize),
                Codec.FLOAT.fieldOf("gravity").forGetter(Option::getGravity)
        ).apply(instance, Option::new));

        public static final ParticleOptions.Deserializer<Option> DESERIALIZER = new ParticleOptions.Deserializer<>() {
            public Option fromCommand(ParticleType<Option> p_235961_, StringReader p_235962_) throws CommandSyntaxException {
                p_235962_.expect(' ');
                int colorFrom = p_235962_.readInt();
                p_235962_.expect(' ');
                int colorTo = p_235962_.readInt();
                p_235962_.expect(' ');
                int duration = p_235962_.readInt();
                p_235962_.expect(' ');
                float size = p_235962_.readFloat();
                p_235962_.expect(' ');
                float gravity = p_235962_.readFloat();
                return new Option(colorFrom, colorTo, duration, size, gravity);
            }

            public Option fromNetwork(ParticleType<Option> p_235964_, FriendlyByteBuf p_235965_) {
                return new Option(p_235965_.readInt(), p_235965_.readInt(), p_235965_.readInt(), p_235965_.readFloat(), p_235965_.readFloat());
            }
        };
        public int colorFrom;
        public int colorTo;
        public int duration;
        public float size;
        public float gravity;

        public Option(int colorFrom, int colorTo, int duration, float size, float gravity){
            this.colorFrom = colorFrom;
            this.colorTo = colorTo;
            this.duration = duration;
            this.size = size;
            this.gravity = gravity;
        }

        public Option(int colorFrom, int colorTo, int duration, float size){
            this(colorFrom, colorTo, duration, size, -0.1F);
        }

        public ParticleType<Option> getType() {
            return ModParticleTypes.MAGIC_SMOKE.get();
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf p_123732_) {
            p_123732_.writeInt(this.getColorFrom());
            p_123732_.writeInt(this.getColorTo());
            p_123732_.writeInt(this.getDuration());
            p_123732_.writeFloat(this.getSize());
            p_123732_.writeFloat(this.getGravity());
        }

        @Override
        public String writeToString() {
            return String.format(Locale.ROOT, "%s %s %s %s %.2f %.2f",
                    BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.colorFrom, this.colorTo, this.duration, this.size, this.gravity);
        }

        public int getColorFrom() {
            return this.colorFrom;
        }

        public int getColorTo() {
            return this.colorTo;
        }

        public int getDuration() {
            return this.duration;
        }

        public float getSize() {
            return this.size;
        }

        public float getGravity() {
            return this.gravity;
        }
    }
}
