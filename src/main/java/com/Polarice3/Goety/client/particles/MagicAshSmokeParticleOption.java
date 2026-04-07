package com.Polarice3.Goety.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Locale;

public class MagicAshSmokeParticleOption implements ParticleOptions {
    public static final Codec<MagicAshSmokeParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("colorFrom").forGetter(MagicAshSmokeParticleOption::getColorFrom),
            Codec.INT.fieldOf("colorTo").forGetter(MagicAshSmokeParticleOption::getColorTo)
    ).apply(instance, MagicAshSmokeParticleOption::new));

    public static final Deserializer<MagicAshSmokeParticleOption> DESERIALIZER = new Deserializer<>() {
        public MagicAshSmokeParticleOption fromCommand(ParticleType<MagicAshSmokeParticleOption> p_235961_, StringReader p_235962_) throws CommandSyntaxException {
            p_235962_.expect(' ');
            int colorFrom = p_235962_.readInt();
            p_235962_.expect(' ');
            int colorTo = p_235962_.readInt();
            return new MagicAshSmokeParticleOption(colorFrom, colorTo);
        }

        public MagicAshSmokeParticleOption fromNetwork(ParticleType<MagicAshSmokeParticleOption> p_235964_, FriendlyByteBuf p_235965_) {
            return new MagicAshSmokeParticleOption(p_235965_.readInt(), p_235965_.readInt());
        }
    };
    public int colorFrom;
    public int colorTo;

    public MagicAshSmokeParticleOption(int colorFrom, int colorTo){
        this.colorFrom = colorFrom;
        this.colorTo = colorTo;
    }

    public MagicAshSmokeParticleOption(int color){
        this.colorFrom = color;
        this.colorTo = color;
    }

    public ParticleType<MagicAshSmokeParticleOption> getType() {
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
