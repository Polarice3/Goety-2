package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;

public class BrewBubbleParticle extends TextureSheetParticle {
   public BrewBubbleParticle(ClientLevel p_105773_, double p_105774_, double p_105775_, double p_105776_, double p_105777_, double p_105778_, double p_105779_) {
      super(p_105773_, p_105774_, p_105775_, p_105776_);
      this.setSize(0.02F, 0.02F);
      this.quadSize *= this.random.nextFloat() * 0.2F + 0.2F;
      this.xd = 0.0D * (double)0.2F + (Math.random() * 2.0D - 1.0D) * (double)0.02F;
      this.yd = 0.0D * (double)0.2F + (Math.random() * 2.0D - 1.0D) * (double)0.02F;
      this.zd = 0.0D * (double)0.2F + (Math.random() * 2.0D - 1.0D) * (double)0.02F;
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.lifetime-- <= 0) {
         this.remove();
      } else {
         this.yd += 0.002D;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= (double)0.85F;
         this.yd *= (double)0.85F;
         this.zd *= (double)0.85F;
         if (!this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).is(ModBlocks.BREWING_CAULDRON.get())) {
            this.remove();
         }

      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_105793_) {
         this.sprite = p_105793_;
      }

      public Particle createParticle(SimpleParticleType p_105804_, ClientLevel p_105805_, double p_105806_, double p_105807_, double p_105808_, double p_105809_, double p_105810_, double p_105811_) {
         BrewBubbleParticle bubbleparticle = new BrewBubbleParticle(p_105805_, p_105806_, p_105807_, p_105808_, p_105809_, p_105810_, p_105811_);
         bubbleparticle.pickSprite(this.sprite);
         bubbleparticle.setColor((float) p_105809_, (float) p_105810_, (float) p_105811_);
         return bubbleparticle;
      }
   }
}