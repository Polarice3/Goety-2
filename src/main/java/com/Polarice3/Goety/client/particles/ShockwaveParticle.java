package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.MathHelper;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class ShockwaveParticle extends GroundCircleParticle {
   private float originSize;
   private int speed;
   private boolean fade;
   private final boolean reverse;

   ShockwaveParticle(ClientLevel p_233976_, double p_233977_, double p_233978_, double p_233979_, float red, float green, float blue, boolean reverse) {
      super(p_233976_, p_233977_, p_233978_, p_233979_, 0.0D, 0.0D, 0.0D);
      this.quadSize = 10.0F;
      this.originSize = this.quadSize * 2.0F;
      this.lifetime = 30;
      this.gravity = 0.0F;
      this.xd = 0.0D;
      this.yd = 0.0D;
      this.zd = 0.0D;
      this.rCol = red;
      this.gCol = green;
      this.bCol = blue;
      this.reverse = reverse;
   }

   ShockwaveParticle(ClientLevel p_233976_, double p_233977_, double p_233978_, double p_233979_, float red, float green, float blue){
      this(p_233976_, p_233977_, p_233978_, p_233979_, red, green, blue, false);
   }

   public float getQuadSize(float partialTicks) {
      if (this.reverse){
         return Math.max(this.originSize / (this.age + partialTicks + 1), this.quadSize);
      } else {
         return this.quadSize * Mth.clamp(((float)this.age + partialTicks) / (float)this.lifetime * 0.75F, 0.0F, 2.0F);
      }
   }

   public void render(VertexConsumer p_233985_, Camera p_233986_, float p_233987_) {
      if (this.fade) {
         this.alpha = 1.0F - Mth.clamp(((float) this.age + p_233987_) / (float) this.lifetime, 0.0F, 1.0F);
      }
      this.renderRotatedParticle(p_233985_, p_233986_, p_233987_, (p_234005_) -> {
         p_234005_.mul(new Quaternionf()).rotationX(-MathHelper.modelDegrees(90));
      });
      this.renderRotatedParticle(p_233985_, p_233986_, p_233987_, (p_234000_) -> {
         p_234000_.mul((new Quaternionf()).rotationYXZ(-(float)Math.PI, MathHelper.modelDegrees(90), 0.0F));
      });
   }

   public int getLightColor(float p_233983_) {
      return LightTexture.FULL_BRIGHT;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      this.age += this.speed;
      super.tick();
   }

   public static class Provider implements ParticleProvider<ShockwaveParticleOption> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_234008_) {
         this.sprite = p_234008_;
      }

      public Particle createParticle(ShockwaveParticleOption p_234019_, ClientLevel p_234020_, double p_234021_, double p_234022_, double p_234023_, double p_234024_, double p_234025_, double p_234026_) {
         ShockwaveParticle shockwaveParticle = new ShockwaveParticle(p_234020_, p_234021_, p_234022_, p_234023_, p_234019_.getRed(), p_234019_.getGreen(), p_234019_.getBlue());
         shockwaveParticle.speed = p_234019_.getSpeed();
         shockwaveParticle.fade = p_234019_.isFade();
         shockwaveParticle.originSize = p_234019_.getOriginSize();
         shockwaveParticle.quadSize = p_234019_.getSize();
         shockwaveParticle.pickSprite(this.sprite);
         shockwaveParticle.setAlpha(1.0F);
         return shockwaveParticle;
      }
   }

   public static class ReverseProvider implements ParticleProvider<ShockwaveParticleOption> {
      private final SpriteSet sprite;

      public ReverseProvider(SpriteSet p_234008_) {
         this.sprite = p_234008_;
      }

      public Particle createParticle(ShockwaveParticleOption p_234019_, ClientLevel p_234020_, double p_234021_, double p_234022_, double p_234023_, double p_234024_, double p_234025_, double p_234026_) {
         ShockwaveParticle shockwaveParticle = new ShockwaveParticle(p_234020_, p_234021_, p_234022_, p_234023_, p_234019_.getRed(), p_234019_.getGreen(), p_234019_.getBlue(), true);
         shockwaveParticle.speed = p_234019_.getSpeed();
         shockwaveParticle.fade = p_234019_.isFade();
         shockwaveParticle.originSize = p_234019_.getOriginSize();
         shockwaveParticle.quadSize = p_234019_.getSize();
         shockwaveParticle.pickSprite(this.sprite);
         shockwaveParticle.setAlpha(1.0F);
         return shockwaveParticle;
      }
   }

   /**
    * Modified Vanilla's Firework Particle Ball
    */
   public static class Explosion extends NoRenderParticle {
      private int life;
      private final int radius;
      private final ParticleEngine engine;

      public Explosion(ClientLevel p_106757_, double p_106758_, double p_106759_, double p_106760_, double p_106761_, double p_106762_, double p_106763_, int radius, ParticleEngine p_106764_) {
         super(p_106757_, p_106758_, p_106759_, p_106760_);
         this.xd = p_106761_;
         this.yd = p_106762_;
         this.zd = p_106763_;
         this.engine = p_106764_;
         this.radius = radius;
         this.lifetime = 8;
      }

      public void tick() {
         if (this.life % 2 == 0) {
            this.createParticleBall(0.5D, this.radius);
         }

         ++this.life;
         if (this.life > this.lifetime) {
            this.remove();
         }

      }

      private void createParticle(double p_106768_, double p_106769_, double p_106770_, double p_106771_, double p_106772_, double p_106773_) {
         this.engine.createParticle(ModParticleTypes.SOUL_EXPLODE_BITS.get(), p_106768_, p_106769_, p_106770_, p_106771_, p_106772_, p_106773_);
      }

      private void createParticleBall(double p_106779_, int p_106780_) {
         double d0 = this.x;
         double d1 = this.y;
         double d2 = this.z;

         for(int i = -p_106780_; i <= p_106780_; ++i) {
            for(int j = -p_106780_; j <= p_106780_; ++j) {
               for(int k = -p_106780_; k <= p_106780_; ++k) {
                  double d3 = (double)j + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                  double d4 = (double)i + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                  double d5 = (double)k + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                  double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5) / p_106779_ + this.random.nextGaussian() * 0.05D;
                  this.createParticle(d0, d1, d2, d3 / d6, d4 / d6, d5 / d6);
                  if (i != -p_106780_ && i != p_106780_ && j != -p_106780_ && j != p_106780_) {
                     k += p_106780_ * 2 - 1;
                  }
               }
            }
         }

      }
   }
}