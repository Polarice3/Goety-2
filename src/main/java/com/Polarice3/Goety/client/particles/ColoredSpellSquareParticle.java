package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;

public class ColoredSpellSquareParticle extends TextureSheetParticle {
   private static final float ACCEL = 0.01F / 400.0F;
   private static final float DT = 0.05F;
   private static final float[] DRAG_CURVE = {
           0.000F, 0.146F, 0.283F, 0.406F, 0.508F, 0.586F, 0.634F, 0.672F, 0.707F, 0.740F, 0.770F, 0.798F, 0.824F,
           0.848F, 0.869F, 0.889F, 0.907F, 0.923F, 0.937F, 0.949F, 0.960F, 0.969F, 0.977F, 0.984F, 0.989F, 0.993F,
           0.997F, 0.999F, 1.000F};
   private static final float DRAG = 2.0F;
   private static final float CURL = 500.0F;
   private static final float CURL_FREQ = 2.5F;
   private final boolean flame;
   private final float baseSize;
   private final float baseR, baseG, baseB;
   private final float lift;
   private final float seed;
   private final SpriteSet sprites;

   public ColoredSpellSquareParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, float size, float red, float green, float blue, int life, SpriteSet spriteSet) {
      this(level, x, y, z, xd, yd, zd, size, red, green, blue, life, false, spriteSet);
   }

   public ColoredSpellSquareParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, float size, float red, float green, float blue, int life, boolean flame, SpriteSet spriteSet) {
      super(level, x, y, z, xd, yd, zd);
      this.sprites = spriteSet;
      this.rCol = red;
      this.gCol = green;
      this.bCol = blue;
      this.hasPhysics = false;
      this.quadSize = size;
      this.lifetime = life;
      this.flame = flame;
      this.baseSize = size;
      this.baseR = red;
      this.baseG = green;
      this.baseB = blue;
      this.lift = 200.0F + this.random.nextFloat() * 400.0F;
      this.seed = this.random.nextFloat() * 100.0F;
      if (flame) {
         this.xd = xd;
         this.yd = yd;
         this.zd = zd;
         this.friction = 1.0F;
         this.gravity = 0.0F;
         this.updateFlame(0.0F);
      }
      this.setSpriteFromAge(spriteSet);
   }

   public int getLightColor(float p_233983_) {
      return LightTexture.FULL_BRIGHT;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      if (this.flame) {
         this.tickFlame();
      } else {
         super.tick();
      }
      this.setSpriteFromAge(this.sprites);
   }

   private void tickFlame() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
         return;
      }
      float t = Mth.clamp((float) this.age / (float) this.lifetime, 0.0F, 1.0F);

      double ay = this.lift * Math.min(1.0F, t / 0.7F);
      double curl = CURL * (1.0F - Mth.cos(Mth.TWO_PI * t)) * 0.5F;
      double px = this.x * CURL_FREQ + this.seed, py = this.y * CURL_FREQ + this.seed * 0.7F, pz = this.z * CURL_FREQ + this.seed * 1.3F;
      this.xd += curl * Math.sin(py) * Math.cos(pz * 1.3D) * ACCEL;
      this.yd += (ay + curl * Math.sin(pz) * Math.cos(px * 1.3D)) * ACCEL;
      this.zd += curl * Math.sin(px) * Math.cos(py * 1.3D) * ACCEL;

      double damp = 1.0D / (1.0D + DRAG * sample(DRAG_CURVE, t) * DT);
      this.xd *= damp;
      this.yd *= damp;
      this.zd *= damp;
      this.move(this.xd, this.yd, this.zd);

      this.updateFlame(t);
   }

   private void updateFlame(float t) {
      float scale = t < 0.1F ? t / 0.1F : (t < 0.7F ? 1.0F : 1.0F - (float) Math.pow((t - 0.7F) / 0.3F, 2.3F));
      this.quadSize = this.baseSize * Math.max(scale, 0.0F);
      float heat = 1.0F + 99.0F * (Mth.cos(Mth.PI * t) + 1.0F) * 0.5F;
      this.rCol = tonemap(this.baseR * heat);
      this.gCol = tonemap(this.baseG * heat);
      this.bCol = tonemap(this.baseB * heat);
   }

   private static float tonemap(float v) {
      return v / (1.0F + v);
   }

   private static float sample(float[] curve, float t) {
      float x = t * (curve.length - 1);
      int i = Math.min((int) x, curve.length - 2);
      return Mth.lerp(x - i, curve[i], curve[i + 1]);
   }

   public static class Provider implements ParticleProvider<ColoredSpellSquareParticleOption> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_107826_) {
         this.sprite = p_107826_;
      }

      public Particle createParticle(ColoredSpellSquareParticleOption option, ClientLevel p_107838_, double p_107839_, double p_107840_, double p_107841_, double p_107842_, double p_107843_, double p_107844_) {
          return new ColoredSpellSquareParticle(p_107838_, p_107839_, p_107840_, p_107841_, p_107842_, p_107843_, p_107844_, option.size, option.r, option.g, option.b, option.life, option.flame, this.sprite);
      }
   }
}
