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

public class ShockwaveParticleOption implements ParticleOptions {
   public static final Codec<ShockwaveParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
           Codec.INT.fieldOf("delay").forGetter(d -> d.delay),
           Codec.FLOAT.fieldOf("red").forGetter(d -> d.red),
           Codec.FLOAT.fieldOf("green").forGetter(d -> d.green),
           Codec.FLOAT.fieldOf("blue").forGetter(d -> d.blue),
           Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
           Codec.INT.fieldOf("speed").forGetter(d -> d.speed),
           Codec.BOOL.fieldOf("fade").forGetter(d -> d.fade)
   ).apply(instance, ShockwaveParticleOption::new));
   public static final Deserializer<ShockwaveParticleOption> DESERIALIZER = new Deserializer<>() {
      public ShockwaveParticleOption fromCommand(ParticleType<ShockwaveParticleOption> p_235961_, StringReader p_235962_) throws CommandSyntaxException {
         p_235962_.expect(' ');
         int i = p_235962_.readInt();
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
         p_235962_.expect(' ');
         boolean f = p_235962_.readBoolean();
         return new ShockwaveParticleOption(i, r, g, b, s, s2, f);
      }

      public ShockwaveParticleOption fromNetwork(ParticleType<ShockwaveParticleOption> p_235964_, FriendlyByteBuf p_235965_) {
         return new ShockwaveParticleOption(p_235965_.readVarInt(), p_235965_.readFloat(), p_235965_.readFloat(), p_235965_.readFloat(), p_235965_.readFloat(), p_235965_.readInt(), p_235965_.readBoolean());
      }
   };
   private final int delay;
   private final float red;
   private final float green;
   private final float blue;
   private final float size;
   private final int speed;
   private final boolean fade;

   public ShockwaveParticleOption(int p_235949_) {
      this.delay = p_235949_;
      this.red = 1.0F;
      this.green = 1.0F;
      this.blue = 1.0F;
      this.size = 10;
      this.speed = 0;
      this.fade = true;
   }

   public ShockwaveParticleOption(int p_235949_, float size, int speed) {
      this.delay = p_235949_;
      this.red = 1.0F;
      this.green = 1.0F;
      this.blue = 1.0F;
      this.size = size;
      this.speed = speed;
      this.fade = true;
   }

   public ShockwaveParticleOption(int p_235949_, float r, float g, float b) {
      this.delay = p_235949_;
      this.red = r;
      this.green = g;
      this.blue = b;
      this.size = 10;
      this.speed = 0;
      this.fade = true;
   }

   public ShockwaveParticleOption(int p_235949_, float r, float g, float b, float size, int speed, boolean fade) {
      this.delay = p_235949_;
      this.red = r;
      this.green = g;
      this.blue = b;
      this.size = size;
      this.speed = speed;
      this.fade = fade;
   }

   public void writeToNetwork(FriendlyByteBuf p_235956_) {
      p_235956_.writeVarInt(this.delay);
      p_235956_.writeFloat(this.red);
      p_235956_.writeFloat(this.green);
      p_235956_.writeFloat(this.blue);
      p_235956_.writeFloat(this.size);
      p_235956_.writeFloat(this.speed);
      p_235956_.writeBoolean(this.fade);
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f %d %s %s %s %s %s %s",
              BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.delay, this.size, this.speed, this.fade);
   }

   public ParticleType<ShockwaveParticleOption> getType() {
      return ModParticleTypes.SHOCKWAVE.get();
   }

   public int getDelay() {
      return this.delay;
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

   public boolean isFade() {
      return this.fade;
   }
}