package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.core.particles.ParticleType;

public class TeleportInShockwaveParticleOption extends ShockwaveParticleOption {

   public TeleportInShockwaveParticleOption(int p_235949_) {
      super(p_235949_, new ColorUtil(0x9a62e7).red, new ColorUtil(0x9a62e7).green, new ColorUtil(0x9a62e7).blue);
   }

   public ParticleType<ShockwaveParticleOption> getType() {
      return ModParticleTypes.REVERSE_SHOCKWAVE.get();
   }
}