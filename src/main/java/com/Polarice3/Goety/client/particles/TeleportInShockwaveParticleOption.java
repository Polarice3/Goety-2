package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;

public class TeleportInShockwaveParticleOption extends ReverseShockwaveParticleOption {

   public TeleportInShockwaveParticleOption() {
      super(new ColorUtil(0x9a62e7), 4, 1, 0, 10);
   }

   public TeleportInShockwaveParticleOption(float size) {
      super(new ColorUtil(0x9a62e7), size);
   }

   public TeleportInShockwaveParticleOption(float originSize, float size) {
      super(new ColorUtil(0x9a62e7), originSize, size);
   }

   public TeleportInShockwaveParticleOption(float originSize, float size, int life) {
      super(new ColorUtil(0x9a62e7), originSize, size, 0, life);
   }

   public TeleportInShockwaveParticleOption(float originSize, float size, int speed, int life) {
      super(new ColorUtil(0x9a62e7), originSize, size, speed, life);
   }
}