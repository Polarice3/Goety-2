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

public class CircleExplodeParticleOption implements ParticleOptions {
   public static final Codec<CircleExplodeParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
           Codec.FLOAT.fieldOf("red").forGetter(d -> d.red),
           Codec.FLOAT.fieldOf("green").forGetter(d -> d.green),
           Codec.FLOAT.fieldOf("blue").forGetter(d -> d.blue),
           Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
           Codec.INT.fieldOf("speed").forGetter(d -> d.speed)
   ).apply(instance, CircleExplodeParticleOption::new));
   public static final Deserializer<CircleExplodeParticleOption> DESERIALIZER = new Deserializer<>() {
      public CircleExplodeParticleOption fromCommand(ParticleType<CircleExplodeParticleOption> p_235961_, StringReader p_235962_) throws CommandSyntaxException {
         p_235962_.expect(' ');
         float r = p_235962_.readFloat();
         p_235962_.expect(' ');
         float g = p_235962_.readFloat();
         p_235962_.expect(' ');
         float b = p_235962_.readFloat();
         p_235962_.expect(' ');
         float s = p_235962_.readFloat();
         p_235962_.expect(' ');
         int s2 = p_235962_.readInt();
         return new CircleExplodeParticleOption(r, g, b, s, s2);
      }

      public CircleExplodeParticleOption fromNetwork(ParticleType<CircleExplodeParticleOption> p_235964_, FriendlyByteBuf p_235965_) {
         return new CircleExplodeParticleOption(p_235965_.readFloat(), p_235965_.readFloat(), p_235965_.readFloat(), p_235965_.readFloat(), p_235965_.readInt());
      }
   };
   private final float red;
   private final float green;
   private final float blue;
   private final float size;
   private final int speed;

   public CircleExplodeParticleOption(float r, float g, float b) {
      this.red = r;
      this.green = g;
      this.blue = b;
      this.size = 10;
      this.speed = 0;
   }

   public CircleExplodeParticleOption(float r, float g, float b, float size, int speed) {
      this.red = r;
      this.green = g;
      this.blue = b;
      this.size = size;
      this.speed = speed;
   }

   public void writeToNetwork(FriendlyByteBuf p_235956_) {
      p_235956_.writeFloat(this.red);
      p_235956_.writeFloat(this.green);
      p_235956_.writeFloat(this.blue);
      p_235956_.writeFloat(this.size);
      p_235956_.writeFloat(this.speed);
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %d %s %s %s %s",
              BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.size, this.speed);
   }

   public ParticleType<CircleExplodeParticleOption> getType() {
      return ModParticleTypes.CIRCLE_EXPLODE.get();
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

   public float getSize(){
      return this.size;
   }

   public int getSpeed(){
      return this.speed;
   }
}