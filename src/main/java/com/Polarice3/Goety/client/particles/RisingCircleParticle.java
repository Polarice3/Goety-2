package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.MathHelper;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class RisingCircleParticle extends TextureSheetParticle {
    private static final Vector3f ROTATION_VECTOR = Util.make(new Vector3f(0.5F, 0.5F, 0.5F), Vector3f::normalize);
    private static final Vector3f TRANSFORM_VECTOR = new Vector3f(-1.0F, -1.0F, 0.0F);
    private int delay;

    public RisingCircleParticle(ClientLevel p_233976_, double p_233977_, double p_233978_, double p_233979_, int p_233980_) {
        super(p_233976_, p_233977_, p_233978_, p_233979_, 0.0D, 0.0D, 0.0D);
        this.quadSize = 1.0F;
        this.delay = p_233980_;
        this.lifetime = 30;
        this.gravity = 0.0F;
        this.xd = 0.0D;
        this.yd = 0.25D;
        this.zd = 0.0D;
    }

    public void render(VertexConsumer p_233985_, Camera p_233986_, float p_233987_) {
        if (this.delay <= 0) {
            this.alpha = 1.0F - Mth.clamp(((float)this.age + p_233987_) / (float)this.lifetime, 0.0F, 1.0F);
            this.renderRotatedParticle(p_233985_, p_233986_, p_233987_, (p_234005_) -> {
                p_234005_.mul(new Quaternionf()).rotationX(-MathHelper.modelDegrees(90));
            });
            this.renderRotatedParticle(p_233985_, p_233986_, p_233987_, (p_234000_) -> {
                p_234000_.mul((new Quaternionf()).rotationYXZ(-(float)Math.PI, MathHelper.modelDegrees(90), 0.0F));
            });
        }
    }

    private void renderRotatedParticle(VertexConsumer p_233989_, Camera p_233990_, float p_233991_, Consumer<Quaternionf> p_233992_) {
        Vec3 vec3 = p_233990_.getPosition();
        float f = (float)(Mth.lerp((double)p_233991_, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp((double)p_233991_, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp((double)p_233991_, this.zo, this.z) - vec3.z());
        Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(0.0F, ROTATION_VECTOR.x(), ROTATION_VECTOR.y(), ROTATION_VECTOR.z());
        p_233992_.accept(quaternionf);
        quaternionf.transform(TRANSFORM_VECTOR);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f3 = this.getQuadSize(p_233991_);

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternionf);
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
            super.tick();
        }
    }

    public static class Provider implements ParticleProvider<RisingCircleParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_234008_) {
            this.sprite = p_234008_;
        }

        public Particle createParticle(RisingCircleParticleOption p_234019_, ClientLevel p_234020_, double p_234021_, double p_234022_, double p_234023_, double p_234024_, double p_234025_, double p_234026_) {
            RisingCircleParticle shriekparticle = new RisingCircleParticle(p_234020_, p_234021_, p_234022_, p_234023_, p_234019_.getDelay());
            shriekparticle.pickSprite(this.sprite);
            shriekparticle.setAlpha(1.0F);
            return shriekparticle;
        }
    }
}
