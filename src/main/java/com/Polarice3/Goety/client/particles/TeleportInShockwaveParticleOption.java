package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;

public class TeleportInShockwaveParticleOption extends ReverseShockwaveParticleOption {

   public TeleportInShockwaveParticleOption() {
      super(new ColorUtil(0x9a62e7));
   }

   public TeleportInShockwaveParticleOption(float size) {
      super(new ColorUtil(0x9a62e7), size);
   }

   public TeleportInShockwaveParticleOption(float originSize, float size) {
      super(new ColorUtil(0x9a62e7), originSize, size);
   }

   public TeleportInShockwaveParticleOption(float originSize, float size, int speed) {
      super(new ColorUtil(0x9a62e7), originSize, size, speed);
   }
}