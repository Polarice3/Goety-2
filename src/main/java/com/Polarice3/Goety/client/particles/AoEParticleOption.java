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

public class AoEParticleOption implements ParticleOptions {
   public static final Codec<AoEParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
           Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
           Codec.FLOAT.fieldOf("growing").forGetter(d -> d.growing),
           Codec.FLOAT.fieldOf("maxSize").forGetter(d -> d.maxSize),
           Codec.INT.fieldOf("life").forGetter(d -> d.life)
   ).apply(instance, AoEParticleOption::new));
   public static final Deserializer<AoEParticleOption> DESERIALIZER = new Deserializer<>() {
      public AoEParticleOption fromCommand(ParticleType<AoEParticleOption> p_235961_, StringReader p_235962_) throws CommandSyntaxException {
         p_235962_.expect(' ');
         float s = p_235962_.readFloat();
         p_235962_.expect(' ');
         float g = p_235962_.readFloat();
         p_235962_.expect(' ');
         float ms = p_235962_.readFloat();
         p_235962_.expect(' ');
         int l = p_235962_.readInt();
         return new AoEParticleOption(s, g, ms, l);
      }

      public AoEParticleOption fromNetwork(ParticleType<AoEParticleOption> p_235964_, FriendlyByteBuf p_235965_) {
         return new AoEParticleOption(p_235965_.readFloat(), p_235965_.readFloat(), p_235965_.readFloat(), p_235965_.readInt());
      }
   };
   private final float size;
   private final float growing;
   private final float maxSize;
   private final int life;

   public AoEParticleOption(float size, int life) {
      this.size = size;
      this.growing = 0.0F;
      this.maxSize = size;
      this.life = life;
   }

   public AoEParticleOption(float initialSize, float growing, float maxSize, int life) {
      this.size = initialSize;
      this.growing = growing;
      this.maxSize = maxSize;
      this.life = life;
   }

   public void writeToNetwork(FriendlyByteBuf p_235956_) {
      p_235956_.writeFloat(this.size);
      p_235956_.writeFloat(this.growing);
      p_235956_.writeFloat(this.maxSize);
      p_235956_.writeInt(this.life);
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %s",
              BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.size, this.growing, this.maxSize, this.life);
   }

   public ParticleType<AoEParticleOption> getType() {
      return ModParticleTypes.AOE_INDICATOR.get();
   }

   public float getSize(){
      return this.size;
   }

   public float getGrowing(){
      return this.growing;
   }

   public float getMaxSize(){
      return this.maxSize;
   }

   public int getLife(){
      return this.life;
   }
}