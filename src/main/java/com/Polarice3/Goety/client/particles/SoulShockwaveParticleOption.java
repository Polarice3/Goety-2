package com.Polarice3.Goety.client.particles;

import net.minecraft.core.particles.ParticleType;

public class SoulShockwaveParticleOption extends ShockwaveParticleOption {

   public SoulShockwaveParticleOption(int p_235949_) {
      super(p_235949_);
   }

   public ParticleType<ShockwaveParticleOption> getType() {
      return ModParticleTypes.SOUL_SHOCKWAVE.get();
   }
}