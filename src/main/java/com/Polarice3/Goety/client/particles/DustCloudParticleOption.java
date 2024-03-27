package com.Polarice3.Goety.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.DustParticleOptionsBase;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

public class DustCloudParticleOption extends DustParticleOptionsBase {
   public static final Codec<DustCloudParticleOption> CODEC = RecordCodecBuilder.create((p_175793_) -> {
      return p_175793_.group(Vector3f.CODEC.fieldOf("color").forGetter((p_175797_) -> {
         return p_175797_.color;
      }), Codec.FLOAT.fieldOf("scale").forGetter((p_175795_) -> {
         return p_175795_.scale;
      })).apply(p_175793_, DustCloudParticleOption::new);
   });
   public static final Deserializer<DustCloudParticleOption> DESERIALIZER = new Deserializer<DustCloudParticleOption>() {
      public DustCloudParticleOption fromCommand(ParticleType<DustCloudParticleOption> p_123689_, StringReader p_123690_) throws CommandSyntaxException {
         Vector3f vector3f = DustParticleOptionsBase.readVector3f(p_123690_);
         p_123690_.expect(' ');
         float f = p_123690_.readFloat();
         return new DustCloudParticleOption(vector3f, f);
      }

      public DustCloudParticleOption fromNetwork(ParticleType<DustCloudParticleOption> p_123692_, FriendlyByteBuf p_123693_) {
         return new DustCloudParticleOption(DustParticleOptionsBase.readVector3f(p_123693_), p_123693_.readFloat());
      }
   };

   public DustCloudParticleOption(Vector3f p_175790_, float p_175791_) {
      super(p_175790_, p_175791_);
   }

   public ParticleType<DustCloudParticleOption> getType() {
      return ModParticleTypes.DUST_CLOUD.get();
   }
}