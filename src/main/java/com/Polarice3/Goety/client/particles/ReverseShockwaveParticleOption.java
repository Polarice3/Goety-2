package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.core.particles.ParticleType;

public class ReverseShockwaveParticleOption extends ShockwaveParticleOption {

   public ReverseShockwaveParticleOption(ColorUtil colorUtil) {
      super(colorUtil);
   }

   public ReverseShockwaveParticleOption(ColorUtil colorUtil, float size) {
      super(colorUtil.red, colorUtil.green, colorUtil.blue, size, 0, true);
   }

   public ReverseShockwaveParticleOption(ColorUtil colorUtil, float originSize, float size) {
      super(colorUtil.red, colorUtil.green, colorUtil.blue, originSize, size, 0, true);
   }

   public ReverseShockwaveParticleOption(ColorUtil colorUtil, float originSize, float size, int speed) {
      super(colorUtil.red, colorUtil.green, colorUtil.blue, originSize, size, speed, 30, true);
   }

   public ReverseShockwaveParticleOption(ColorUtil colorUtil, float originSize, float size, int speed, int life) {
      super(colorUtil.red, colorUtil.green, colorUtil.blue, originSize, size, speed, life, true);
   }

   public ReverseShockwaveParticleOption(ColorUtil colorUtil, float originSize, float size, int speed, boolean fade) {
      super(colorUtil.red, colorUtil.green, colorUtil.blue, originSize, size, speed, fade);
   }

   public ParticleType<ShockwaveParticleOption> getType() {
      return ModParticleTypes.REVERSE_SHOCKWAVE.get();
   }
}