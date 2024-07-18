package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.core.particles.ParticleType;

public class ReverseShockwaveParticleOption extends ShockwaveParticleOption {

   public ReverseShockwaveParticleOption(ColorUtil colorUtil) {
      super(colorUtil.red, colorUtil.green, colorUtil.blue);
   }

   public ReverseShockwaveParticleOption(ColorUtil colorUtil, float size) {
      super(colorUtil.red, colorUtil.green, colorUtil.blue, size, 0, true);
   }

   public ReverseShockwaveParticleOption(ColorUtil colorUtil, float originSize, float size) {
      super(colorUtil.red, colorUtil.green, colorUtil.blue, originSize, size, 0, true);
   }

   public ReverseShockwaveParticleOption(ColorUtil colorUtil, float size, int speed) {
      super(colorUtil.red, colorUtil.green, colorUtil.blue, size, speed, true);
   }

   public ReverseShockwaveParticleOption(ColorUtil colorUtil, float originSize, float size, int speed) {
      super(colorUtil.red, colorUtil.green, colorUtil.blue, originSize, size, speed, true);
   }

   public ParticleType<ShockwaveParticleOption> getType() {
      return ModParticleTypes.REVERSE_SHOCKWAVE.get();
   }
}