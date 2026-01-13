package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class AoEParticle extends GroundCircleParticle {
   private float initialSize;
   private float growing;
   private float finalSize;
   private int ownerId = -1;
   public final Vec3 origin;
   private final SpriteSet spriteSet;

   AoEParticle(ClientLevel clientLevel, double x, double y, double z, float red, float green, float blue, SpriteSet spriteSet) {
      super(clientLevel, x, y, z, 0.0D, 0.0D, 0.0D);
      this.quadSize = 10.0F;
      this.lifetime = 100;
      this.gravity = 0.0F;
      this.xd = 0.0D;
      this.yd = 0.0D;
      this.zd = 0.0D;
      this.rCol = red;
      this.gCol = green;
      this.bCol = blue;
      this.origin = new Vec3(this.x, this.y, this.z);
      this.spriteSet = spriteSet;
      this.setSpriteFromAge(spriteSet);
   }

   public float getQuadSize(float p_234003_) {
      return this.quadSize;
   }

   public int getLightColor(float p_233983_) {
      return LightTexture.FULL_BRIGHT;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public Vec3 getPosition() {
      Entity owner = this.getEntity();
      return owner != null ? owner.position().add(0, 0.25, 0) : this.origin;
   }

   public Entity getEntity() {
      return this.ownerId == -1 ? null : this.level.getEntity(this.ownerId);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         Vec3 vec3 = this.getPosition();
         this.setPos(vec3.x, vec3.y, vec3.z);
         if (this.growing > 0.0F) {
            if (this.initialSize <= this.finalSize) {
               if (this.quadSize < this.finalSize) {
                  this.quadSize += this.growing;
               }
            } else {
               if (this.quadSize > this.finalSize) {
                  this.quadSize -= this.growing;
               }
            }
         }
         this.setSpriteFromAge(this.spriteSet);
      }
   }

   @Override
   public boolean shouldCull() {
      return false;
   }

   public static class Provider implements ParticleProvider<AoEParticleOption> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_234008_) {
         this.sprite = p_234008_;
      }

      public Particle createParticle(AoEParticleOption p_234019_, ClientLevel p_234020_, double p_234021_, double p_234022_, double p_234023_, double p_234024_, double p_234025_, double p_234026_) {
         AoEParticle shockwaveParticle = new AoEParticle(p_234020_, p_234021_, p_234022_, p_234023_, (float) p_234024_, (float) p_234025_, (float) p_234026_, sprite);
         shockwaveParticle.initialSize = p_234019_.getSize();
         shockwaveParticle.quadSize = p_234019_.getSize();
         shockwaveParticle.growing = p_234019_.getGrowing();
         shockwaveParticle.finalSize = p_234019_.getMaxSize();
         shockwaveParticle.ownerId = p_234019_.getOwnerId();
         shockwaveParticle.setLifetime(p_234019_.getLife());
         shockwaveParticle.setAlpha(0.25F);
         return shockwaveParticle;
      }
   }
}