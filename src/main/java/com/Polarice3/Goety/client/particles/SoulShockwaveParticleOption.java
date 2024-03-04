package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.core.particles.ParticleType;

public class SoulShockwaveParticleOption extends ShockwaveParticleOption {

   public SoulShockwaveParticleOption(int p_235949_) {
      super(p_235949_, new ColorUtil(0x2ac9cf).red, new ColorUtil(0x2ac9cf).green, new ColorUtil(0x2ac9cf).blue);
   }

   public ParticleType<ShockwaveParticleOption> getType() {
      return ModParticleTypes.SHOCKWAVE.get();
   }
}