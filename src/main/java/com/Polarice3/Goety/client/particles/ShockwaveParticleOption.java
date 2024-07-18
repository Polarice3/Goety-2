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

public class ShockwaveParticleOption implements ParticleOptions {
   public static final Codec<ShockwaveParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
           Codec.FLOAT.fieldOf("red").forGetter(d -> d.red),
           Codec.FLOAT.fieldOf("green").forGetter(d -> d.green),
           Codec.FLOAT.fieldOf("blue").forGetter(d -> d.blue),
           Codec.FLOAT.fieldOf("originSize").forGetter(d -> d.originSize),
           Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
           Codec.INT.fieldOf("speed").forGetter(d -> d.speed),
           Codec.BOOL.fieldOf("fade").forGetter(d -> d.fade)
   ).apply(instance, ShockwaveParticleOption::new));
   public static final Deserializer<ShockwaveParticleOption> DESERIALIZER = new Deserializer<>() {
      public ShockwaveParticleOption fromCommand(ParticleType<ShockwaveParticleOption> p_235961_, StringReader p_235962_) throws CommandSyntaxException {
         p_235962_.expect(' ');
         float r = p_235962_.readFloat();
         p_235962_.expect(' ');
         float g = p_235962_.readFloat();
         p_235962_.expect(' ');
         float b = p_235962_.readFloat();
         p_235962_.expect(' ');
         float s0 = p_235962_.readFloat();
         p_235962_.expect(' ');
         float s = p_235962_.readFloat();
         p_235962_.expect(' ');
         int s2 = p_235962_.readInt();
         p_235962_.expect(' ');
         boolean f = p_235962_.readBoolean();
         return new ShockwaveParticleOption(r, g, b, s0, s, s2, f);
      }

      public ShockwaveParticleOption fromNetwork(ParticleType<ShockwaveParticleOption> p_235964_, FriendlyByteBuf p_235965_) {
         return new ShockwaveParticleOption(p_235965_.readFloat(), p_235965_.readFloat(), p_235965_.readFloat(), p_235965_.readFloat(), p_235965_.readFloat(), p_235965_.readInt(), p_235965_.readBoolean());
      }
   };
   private final float red;
   private final float green;
   private final float blue;
   private final float originSize;
   private final float size;
   private final int speed;
   private final boolean fade;

   public ShockwaveParticleOption() {
      this.red = 1.0F;
      this.green = 1.0F;
      this.blue = 1.0F;
      this.originSize = 20;
      this.size = 10;
      this.speed = 0;
      this.fade = true;
   }

   public ShockwaveParticleOption(float size, int speed) {
      this.red = 1.0F;
      this.green = 1.0F;
      this.blue = 1.0F;
      this.originSize = size * 2;
      this.size = size;
      this.speed = speed;
      this.fade = true;
   }

   public ShockwaveParticleOption(float originSize, float size, int speed) {
      this.red = 1.0F;
      this.green = 1.0F;
      this.blue = 1.0F;
      this.originSize = originSize;
      this.size = size;
      this.speed = speed;
      this.fade = true;
   }

   public ShockwaveParticleOption(float r, float g, float b) {
      this.red = r;
      this.green = g;
      this.blue = b;
      this.size = 10;
      this.originSize = 20;
      this.speed = 0;
      this.fade = true;
   }

   public ShockwaveParticleOption(float r, float g, float b, float size, int speed, boolean fade) {
      this.red = r;
      this.green = g;
      this.blue = b;
      this.originSize = size * 2.0F;
      this.size = size;
      this.speed = speed;
      this.fade = fade;
   }

   public ShockwaveParticleOption(float r, float g, float b, float originSize, float size, int speed, boolean fade) {
      this.red = r;
      this.green = g;
      this.blue = b;
      this.originSize = originSize;
      this.size = size;
      this.speed = speed;
      this.fade = fade;
   }

   public void writeToNetwork(FriendlyByteBuf p_235956_) {
      p_235956_.writeFloat(this.red);
      p_235956_.writeFloat(this.green);
      p_235956_.writeFloat(this.blue);
      p_235956_.writeFloat(this.originSize);
      p_235956_.writeFloat(this.size);
      p_235956_.writeFloat(this.speed);
      p_235956_.writeBoolean(this.fade);
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %s %s",
              Registry.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.originSize, this.size, this.speed, this.fade);
   }

   public ParticleType<ShockwaveParticleOption> getType() {
      return ModParticleTypes.SHOCKWAVE.get();
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

   public float getOriginSize(){
      return this.originSize;
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