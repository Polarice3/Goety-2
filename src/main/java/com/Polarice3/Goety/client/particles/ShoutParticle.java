package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.MathHelper;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class ShoutParticle extends TextureSheetParticle {
   private static final Vector3f ROTATION_VECTOR = Util.make(new Vector3f(0.5F, 0.5F, 0.5F), Vector3f::normalize);
   private static final Vector3f TRANSFORM_VECTOR = new Vector3f(-1.0F, -1.0F, 0.0F);
   private int delay;
   private final PositionSource target;

   public ShoutParticle(ClientLevel p_233976_, double p_233977_, double p_233978_, double p_233979_, int p_233980_, PositionSource p_234109_) {
      super(p_233976_, p_233977_, p_233978_, p_233979_, 0.0D, 0.0D, 0.0D);
      this.quadSize = 0.85F;
      this.delay = p_233980_;
      this.lifetime = 30;
      this.gravity = 0.0F;
      this.target = p_234109_;
   }

   public float getQuadSize(float p_234003_) {
      return this.quadSize * Mth.clamp(((float)this.age + p_234003_) / (float)this.lifetime * 0.75F, 0.0F, 1.0F);
   }

   public void render(VertexConsumer p_233985_, Camera p_233986_, float p_233987_) {
      if (this.delay <= 0) {
         this.alpha = 1.0F - Mth.clamp(((float)this.age + p_233987_) / (float)this.lifetime, 0.0F, 1.0F);
         this.renderRotatedParticle(p_233985_, p_233986_, p_233987_, (p_234005_) -> {
            p_234005_.mul(Vector3f.YP.rotation(0.0F));
            p_234005_.mul(Vector3f.XP.rotation(-MathHelper.modelDegrees(0.0F)));
         });
         this.renderRotatedParticle(p_233985_, p_233986_, p_233987_, (p_234000_) -> {
            p_234000_.mul(Vector3f.YP.rotation(-(float)Math.PI));
            p_234000_.mul(Vector3f.XP.rotation(MathHelper.modelDegrees(0.0F)));
         });
      }
   }

   private void renderRotatedParticle(VertexConsumer p_233989_, Camera p_233990_, float p_233991_, Consumer<Quaternion> p_233992_) {
      Vec3 vec3 = p_233990_.getPosition();
      float f = (float)(Mth.lerp((double)p_233991_, this.xo, this.x) - vec3.x());
      float f1 = (float)(Mth.lerp((double)p_233991_, this.yo, this.y) - vec3.y());
      float f2 = (float)(Mth.lerp((double)p_233991_, this.zo, this.z) - vec3.z());
      Quaternion quaternion = new Quaternion(ROTATION_VECTOR, 0.0F, true);
      p_233992_.accept(quaternion);
      TRANSFORM_VECTOR.transform(quaternion);
      Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
      float f3 = this.getQuadSize(p_233991_);

      for(int i = 0; i < 4; ++i) {
         Vector3f vector3f = avector3f[i];
         vector3f.transform(quaternion);
         vector3f.mul(f3);
         vector3f.add(f, f1, f2);
      }

      int j = this.getLightColor(p_233991_);
      this.makeCornerVertex(p_233989_, avector3f[0], this.getU1(), this.getV1(), j);
      this.makeCornerVertex(p_233989_, avector3f[1], this.getU1(), this.getV0(), j);
      this.makeCornerVertex(p_233989_, avector3f[2], this.getU0(), this.getV0(), j);
      this.makeCornerVertex(p_233989_, avector3f[3], this.getU0(), this.getV1(), j);
   }

   private void makeCornerVertex(VertexConsumer p_233994_, Vector3f p_233995_, float p_233996_, float p_233997_, int p_233998_) {
      p_233994_.vertex((double)p_233995_.x(), (double)p_233995_.y(), (double)p_233995_.z()).uv(p_233996_, p_233997_).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(p_233998_).endVertex();
   }

   public int getLightColor(float p_233983_) {
      return 240;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      if (this.delay > 0) {
         --this.delay;
      } else {
         this.xo = this.x;
         this.yo = this.y;
         this.zo = this.z;
         if (this.age++ >= this.lifetime) {
            this.remove();
         } else {
            Optional<Vec3> optional = this.target.getPosition(this.level);
            if (optional.isEmpty()) {
               this.remove();
            } else {
               int i = this.lifetime - this.age;
               double d0 = 1.0D / (double)i;
               Vec3 vec3 = optional.get();
               this.x = Mth.lerp(d0, this.x, vec3.x());
               this.y = Mth.lerp(d0, this.y, vec3.y());
               this.z = Mth.lerp(d0, this.z, vec3.z());
               this.setPos(this.x, this.y, this.z);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class RedProvider implements ParticleProvider<ShoutParticleOption> {
      private final SpriteSet sprite;

      public RedProvider(SpriteSet p_234008_) {
         this.sprite = p_234008_;
      }

      public Particle createParticle(ShoutParticleOption p_234019_, ClientLevel p_234020_, double p_234021_, double p_234022_, double p_234023_, double p_234024_, double p_234025_, double p_234026_) {
         ShoutParticle shoutParticle = new ShoutParticle(p_234020_, p_234021_, p_234022_, p_234023_, p_234019_.getDelay(), p_234019_.getDestination());
         shoutParticle.pickSprite(this.sprite);
         shoutParticle.setColor(1.0F, 0.0F, 0.0F);
         shoutParticle.setAlpha(1.0F);
         return shoutParticle;
      }
   }
}