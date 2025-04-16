package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.core.particles.ParticleType;

public class LichShockwaveParticleOption extends ShockwaveParticleOption {

   public LichShockwaveParticleOption(ColorUtil colorUtil, float originSize, float size) {
      super(colorUtil.red, colorUtil.green, colorUtil.blue, originSize, size, 0, true);
   }

   public LichShockwaveParticleOption(ColorUtil colorUtil, float originSize, float size, int speed, int life) {
      super(colorUtil.red, colorUtil.green, colorUtil.blue, originSize, size, speed, life, true);
   }

   public LichShockwaveParticleOption(ColorUtil colorUtil, float originSize, float size, int life) {
      super(colorUtil.red, colorUtil.green, colorUtil.blue, originSize, size, 0, life, true);
   }

   public ParticleType<ShockwaveParticleOption> getType() {
      return ModParticleTypes.LICH_DEATH.get();
   }
}