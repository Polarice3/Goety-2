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

public class ModShriekParticleOption implements ParticleOptions {
   public static final Codec<ModShriekParticleOption> CODEC = RecordCodecBuilder.create((p_235952_) -> {
      return p_235952_.group(Codec.INT.fieldOf("delay").forGetter((p_235954_) -> {
         return p_235954_.delay;
      })).apply(p_235952_, ModShriekParticleOption::new);
   });
   public static final Deserializer<ModShriekParticleOption> DESERIALIZER = new Deserializer<>() {
      public ModShriekParticleOption fromCommand(ParticleType<ModShriekParticleOption> p_235961_, StringReader p_235962_) throws CommandSyntaxException {
         p_235962_.expect(' ');
         int i = p_235962_.readInt();
         return new ModShriekParticleOption(i);
      }

      public ModShriekParticleOption fromNetwork(ParticleType<ModShriekParticleOption> p_235964_, FriendlyByteBuf p_235965_) {
         return new ModShriekParticleOption(p_235965_.readVarInt());
      }
   };
   private final int delay;

   public ModShriekParticleOption(int p_235949_) {
      this.delay = p_235949_;
   }

   public void writeToNetwork(FriendlyByteBuf p_235956_) {
      p_235956_.writeVarInt(this.delay);
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.delay);
   }

   public ParticleType<ModShriekParticleOption> getType() {
      return ModParticleTypes.MOD_SHRIEK.get();
   }

   public int getDelay() {
      return this.delay;
   }
}