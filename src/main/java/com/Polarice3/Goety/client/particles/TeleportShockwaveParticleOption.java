package com.Polarice3.Goety.client.particles;

import net.minecraft.core.particles.ParticleType;

public class TeleportShockwaveParticleOption extends ShockwaveParticleOption {

   public TeleportShockwaveParticleOption(int p_235949_) {
      super(p_235949_);
   }

   public ParticleType<ShockwaveParticleOption> getType() {
      return ModParticleTypes.TELEPORT_SHOCKWAVE.get();
   }
}