package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.core.particles.ParticleType;

public class PortalShockwaveParticleOption extends ShockwaveParticleOption {

   public PortalShockwaveParticleOption() {
      super(new ColorUtil(0x7317d2));
   }

   public ParticleType<ShockwaveParticleOption> getType() {
      return ModParticleTypes.SHOCKWAVE.get();
   }
}