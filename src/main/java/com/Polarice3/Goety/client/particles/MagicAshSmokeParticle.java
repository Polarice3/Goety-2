package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.client.Timer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BaseAshSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.FastColor;

import java.util.Locale;

public class MagicAshSmokeParticle extends BaseAshSmokeParticle {
    public Timer timer;
    public int colorFrom;
    public int colorTo;
    private final SpriteSet sprites;

    public MagicAshSmokeParticle(ClientLevel clientLevel, double x, double y, double z, double xd, double yd, double zd, int colorFrom, int colorTo, SpriteSet spriteSet) {
        super(clientLevel, x, y, z, 0.1F, 0.1F, 0.1F, xd, yd, zd, 1.0F, spriteSet, 0.3F, 8, -0.1F, true);
        this.sprites = spriteSet;
        ColorUtil colorUtil = new ColorUtil(colorFrom);
        this.rCol = colorUtil.red();
        this.gCol = colorUtil.green();
        this.bCol = colorUtil.blue();
        this.colorFrom = colorFrom;
        this.colorTo = colorTo;
        this.timer = new Timer(this.lifetime + 1, 0);
    }

    @Override
    public void tick() {
        this.setSpriteFromAge(this.sprites);
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

            this.xd *= this.friction;
            this.yd *= this.friction;
            this.zd *= this.friction;
            if (this.onGround) {
                this.xd *= 0.7F;
                this.zd *= 0.7F;
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
            MagicAshSmokeParticle trailParticle = new MagicAshSmokeParticle(clientLevel, d, e, f, g, h, i, option.getColorFrom(), option.getColorTo(), this.sprite);
            trailParticle.pickSprite(this.sprite);
            return trailParticle;
        }
    }

    public static class Option implements ParticleOptions {
        public static final Codec<Option> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("colorFrom").forGetter(Option::getColorFrom),
                Codec.INT.fieldOf("colorTo").forGetter(Option::getColorTo)
        ).apply(instance, Option::new));

        public static final Deserializer<Option> DESERIALIZER = new Deserializer<>() {
            public Option fromCommand(ParticleType<Option> p_235961_, StringReader p_235962_) throws CommandSyntaxException {
                p_235962_.expect(' ');
                int colorFrom = p_235962_.readInt();
                p_235962_.expect(' ');
                int colorTo = p_235962_.readInt();
                return new Option(colorFrom, colorTo);
            }

            public Option fromNetwork(ParticleType<Option> p_235964_, FriendlyByteBuf p_235965_) {
                return new Option(p_235965_.readInt(), p_235965_.readInt());
            }
        };
        public int colorFrom;
        public int colorTo;

        public Option(int colorFrom, int colorTo){
            this.colorFrom = colorFrom;
            this.colorTo = colorTo;
        }

        public Option(int color){
            this.colorFrom = color;
            this.colorTo = color;
        }

        public ParticleType<Option> getType() {
            return ModParticleTypes.MAGIC_ASH_SMOKE.get();
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf p_123732_) {
            p_123732_.writeInt(this.getColorFrom());
            p_123732_.writeInt(this.getColorTo());
        }

        @Override
        public String writeToString() {
            return String.format(Locale.ROOT, "%s %s %s",
                    BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.colorFrom, this.colorTo);
        }

        public int getColorFrom() {
            return this.colorFrom;
        }

        public int getColorTo() {
            return this.colorTo;
        }
    }
}
