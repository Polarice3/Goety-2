package com.Polarice3.Goety.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Locale;

public record PulsatingCircleParticleOption(float size) implements ParticleOptions {
   public static final Codec<PulsatingCircleParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
           Codec.FLOAT.fieldOf("size").forGetter(d -> d.size)
   ).apply(instance, PulsatingCircleParticleOption::new));
   public static final Deserializer<PulsatingCircleParticleOption> DESERIALIZER = new Deserializer<>() {
      public PulsatingCircleParticleOption fromCommand(ParticleType<PulsatingCircleParticleOption> p_235961_, StringReader p_235962_) throws CommandSyntaxException {
         p_235962_.expect(' ');
         float s = p_235962_.readFloat();
         return new PulsatingCircleParticleOption(s);
      }

      public PulsatingCircleParticleOption fromNetwork(ParticleType<PulsatingCircleParticleOption> p_235964_, FriendlyByteBuf p_235965_) {
         return new PulsatingCircleParticleOption(p_235965_.readFloat());
      }
   };

   public void writeToNetwork(FriendlyByteBuf p_235956_) {
      p_235956_.writeFloat(this.size);
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %.2f",
              Registry.PARTICLE_TYPE.getKey(this.getType()), this.size);
   }

   public ParticleType<PulsatingCircleParticleOption> getType() {
      return ModParticleTypes.MINE_PULSE.get();
   }
}