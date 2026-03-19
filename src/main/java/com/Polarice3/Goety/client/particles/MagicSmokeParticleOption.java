package com.Polarice3.Goety.client.particles;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Locale;

public abstract class MagicSmokeParticleOption implements ParticleOptions {
    /*public static final Codec<MagicSmokeParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("colorFrom").forGetter(MagicSmokeParticleOption::getColorFrom),
            Codec.INT.fieldOf("colorTo").forGetter(MagicSmokeParticleOption::getColorTo),
            ExtraCodecs.POSITIVE_INT.fieldOf("duration").forGetter(MagicSmokeParticleOption::getDuration),
            Codec.FLOAT.fieldOf("size").forGetter(MagicSmokeParticleOption::getSize),
            Codec.FLOAT.fieldOf("gravity").forGetter(MagicSmokeParticleOption::getGravity)
    ).apply(instance, MagicSmokeParticleOption::new));

    public static final ParticleOptions.Deserializer<MagicSmokeParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        public MagicSmokeParticleOption fromCommand(ParticleType<MagicSmokeParticleOption> p_235961_, StringReader p_235962_) throws CommandSyntaxException {
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
            return new MagicSmokeParticleOption(colorFrom, colorTo, duration, size, gravity);
        }

        public MagicSmokeParticleOption fromNetwork(ParticleType<MagicSmokeParticleOption> p_235964_, FriendlyByteBuf p_235965_) {
            return new MagicSmokeParticleOption(p_235965_.readInt(), p_235965_.readInt(), p_235965_.readInt(), p_235965_.readFloat(), p_235965_.readFloat());
        }
    };*/
    public int colorFrom;
    public int colorTo;
    public int duration;
    public float size;
    public float gravity;

    public MagicSmokeParticleOption(int colorFrom, int colorTo, int duration, float size, float gravity){
        this.colorFrom = colorFrom;
        this.colorTo = colorTo;
        this.duration = duration;
        this.size = size;
        this.gravity = gravity;
    }

    public MagicSmokeParticleOption(int colorFrom, int colorTo, int duration, float size){
        this(colorFrom, colorTo, duration, size, -0.1F);
    }

    /*public ParticleType<MagicSmokeParticleOption> getType() {
        return ModParticleTypes.MAGIC_SMOKE.get();
    }*/

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
