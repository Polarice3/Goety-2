package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class FollowFireParticle extends TextureSheetParticle {
   public final int ownerId;
   public final boolean isEntity;
   private final SpriteSet sprites;

   protected FollowFireParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites, int ownerId) {
      super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
      this.ownerId = ownerId;
      this.isEntity = ownerId > 0;
      this.sprites = pSprites;
      this.friction = 0.96F;
      this.xd = this.xd * (double)0.01F + pXSpeed;
      this.yd = this.yd * (double)0.01F + pYSpeed;
      this.zd = this.zd * (double)0.01F + pZSpeed;
      this.quadSize *= 3.0F;
      this.lifetime = 8;
      this.hasPhysics = false;
      this.setSpriteFromAge(pSprites);
   }

   public Entity getEntity() {
      return this.ownerId == -1 ? null : this.level.getEntity(this.ownerId);
   }

   public void tick() {
      super.tick();
      if (this.isEntity){
         if (this.getEntity() != null) {
            Vec3 vec3 = this.getEntity().position();
            this.x = vec3.x;
            this.y = vec3.y + (this.getEntity().getBbHeight() / 2.0F);
            this.z = vec3.z;
         } else {
            this.remove();
         }
      }
      if (this.age < this.lifetime) {
         this.setSpriteFromAge(this.sprites);
      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public int getLightColor(float p_106821_) {
      return LightTexture.FULL_BRIGHT;
   }

   public static class Provider implements ParticleProvider<FollowFireParticleOption> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_106827_) {
         this.sprite = p_106827_;
      }

      public Particle createParticle(FollowFireParticleOption p_106838_, ClientLevel p_106839_, double p_106840_, double p_106841_, double p_106842_, double p_106843_, double p_106844_, double p_106845_) {
         FollowFireParticle fireParticle = new FollowFireParticle(p_106839_, p_106840_, p_106841_, p_106842_, 0.0D, 0.0D, 0.0D, this.sprite, p_106838_.getOwnerId());
         fireParticle.setColor((float) p_106843_, (float) p_106844_, (float) p_106845_);
         return fireParticle;
      }
   }
}