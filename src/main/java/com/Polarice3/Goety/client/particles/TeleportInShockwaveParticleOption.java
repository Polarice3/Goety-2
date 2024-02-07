package com.Polarice3.Goety.client.particles;

import net.minecraft.core.particles.ParticleType;

public class TeleportInShockwaveParticleOption extends ShockwaveParticleOption {

   public TeleportInShockwaveParticleOption(int p_235949_) {
      super(p_235949_);
   }

   public ParticleType<ShockwaveParticleOption> getType() {
      return ModParticleTypes.TELEPORT_IN_SHOCKWAVE.get();
   }
}