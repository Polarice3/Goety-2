package com.Polarice3.Goety.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

public class ShoutParticleOption implements ParticleOptions {
   public static final Codec<ShoutParticleOption> CODEC = RecordCodecBuilder.create((p_235952_) -> {
      return p_235952_.group(PositionSource.CODEC.fieldOf("destination").forGetter((p_235982_) -> {
         return p_235982_.destination;
      }), Codec.INT.fieldOf("delay").forGetter((p_235954_) -> {
         return p_235954_.delay;
      })).apply(p_235952_, ShoutParticleOption::new);
   });
   public static final Deserializer<ShoutParticleOption> DESERIALIZER = new Deserializer<ShoutParticleOption>() {
      public ShoutParticleOption fromCommand(ParticleType<ShoutParticleOption> p_235961_, StringReader p_235962_) throws CommandSyntaxException {
         p_235962_.expect(' ');
         float f = (float)p_235962_.readDouble();
         p_235962_.expect(' ');
         float f1 = (float)p_235962_.readDouble();
         p_235962_.expect(' ');
         float f2 = (float)p_235962_.readDouble();
         p_235962_.expect(' ');
         BlockPos blockpos = new BlockPos((double)f, (double)f1, (double)f2);
         int i = p_235962_.readInt();
         return new ShoutParticleOption(new BlockPositionSource(blockpos), i);
      }

      public ShoutParticleOption fromNetwork(ParticleType<ShoutParticleOption> p_235964_, FriendlyByteBuf p_235965_) {
         PositionSource positionsource = PositionSourceType.fromNetwork(p_235965_);
         return new ShoutParticleOption(positionsource, p_235965_.readVarInt());
      }
   };
   private final PositionSource destination;
   private final int delay;

   public ShoutParticleOption(PositionSource p_235975_, int p_235949_) {
      this.destination = p_235975_;
      this.delay = p_235949_;
   }

   public void writeToNetwork(FriendlyByteBuf p_235956_) {
      PositionSourceType.toNetwork(this.destination, p_235956_);
      p_235956_.writeVarInt(this.delay);
   }

   public String writeToString() {
      Vec3 vec3 = this.destination.getPosition((Level)null).get();
      double d0 = vec3.x();
      double d1 = vec3.y();
      double d2 = vec3.z();
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %d", Registry.PARTICLE_TYPE.getKey(this.getType()), d0, d1, d2, this.delay);
   }

   public ParticleType<ShoutParticleOption> getType() {
      return ModParticleTypes.SHOUT.get();
   }

   public int getDelay() {
      return this.delay;
   }

   public PositionSource getDestination() {
      return this.destination;
   }
}