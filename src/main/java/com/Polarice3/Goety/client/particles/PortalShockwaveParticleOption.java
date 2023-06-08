package com.Polarice3.Goety.client.particles;

import net.minecraft.core.particles.ParticleType;

public class PortalShockwaveParticleOption extends ShockwaveParticleOption {

   public PortalShockwaveParticleOption(int p_235949_) {
      super(p_235949_);
   }

   public ParticleType<ShockwaveParticleOption> getType() {
      return ModParticleTypes.PORTAL_SHOCKWAVE.get();
   }
}