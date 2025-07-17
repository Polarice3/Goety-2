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
import net.minecraft.util.Mth;

import java.util.Locale;

public class MagicSmokeParticle extends TextureSheetParticle {
    public Timer timer = new Timer(20.0F, 0);
    public int colorFrom;
    public int colorTo;

    public MagicSmokeParticle(ClientLevel clientLevel, double x, double y, double z, double xd, double yd, double zd, int colorFrom, int colorTo, float size) {
        super(clientLevel, x, y, z, xd, yd, zd);
        this.friction = 0.96F;
        this.gravity = -0.1F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.xd = xd == 0.0D ? (this.random.nextDouble() * 2 - 1) / 10 : xd;
        this.yd = yd == 0.0D ? 0.1D + this.random.nextDouble() / 10 : yd;
        this.zd = zd == 0.0D ? (this.random.nextDouble() * 2 - 1) / 10 : zd;
        this.xd *= 0.5F;
        this.zd *= 0.5F;
        this.colorFrom = colorFrom;
        this.colorTo = colorTo;
        this.quadSize = size;
        this.lifetime = (int)((double)8 / ((double)clientLevel.random.nextFloat() * 0.8D + 0.2D) * (double)0.3F);
        this.lifetime = Math.max(this.lifetime, 1);
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
        super.tick();
        this.timer.advanceTime(Util.getMillis());
        int newColor = ColorUtil.ARGB.lerp(this.timer.partialTick, this.colorFrom, this.colorTo);
        this.rCol = (float)ColorUtil.ARGB.red(newColor) / 255.0F;
        this.gCol = (float)ColorUtil.ARGB.green(newColor) / 255.0F;
        this.bCol = (float)ColorUtil.ARGB.blue(newColor) / 255.0F;
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
            MagicSmokeParticle trailParticle = new MagicSmokeParticle(clientLevel, d, e, f, g, h, i, option.colorFrom(), option.colorTo(), option.size());
            trailParticle.pickSprite(this.sprite);
            trailParticle.setLifetime(option.duration());
            return trailParticle;
        }
    }

    public record Option(int colorFrom, int colorTo, int duration, float size) implements ParticleOptions {
        public static final Codec<Option> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("colorFrom").forGetter(Option::colorFrom),
                Codec.INT.fieldOf("colorTo").forGetter(Option::colorTo),
                ExtraCodecs.POSITIVE_INT.fieldOf("duration").forGetter(Option::duration),
                Codec.FLOAT.fieldOf("size").forGetter(Option::size)
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
                return new Option(colorFrom, colorTo, duration, size);
            }

            public Option fromNetwork(ParticleType<Option> p_235964_, FriendlyByteBuf p_235965_) {
                return new Option(p_235965_.readInt(), p_235965_.readInt(), p_235965_.readInt(), p_235965_.readFloat());
            }
        };

        public ParticleType<Option> getType() {
            return ModParticleTypes.MAGIC_SMOKE.get();
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf p_123732_) {
            p_123732_.writeInt(this.colorFrom());
            p_123732_.writeInt(this.colorTo());
            p_123732_.writeInt(this.duration());
            p_123732_.writeFloat(this.size());
        }

        @Override
        public String writeToString() {
            return String.format(Locale.ROOT, "%s %s %s %s %.2f",
                    BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.colorFrom, this.colorTo, this.duration, this.size);
        }
    }
}
