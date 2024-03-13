package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.MathHelper;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;

public abstract class GroundCircleParticle extends TextureSheetParticle {
    public static final Vector3f ROTATION_VECTOR = Util.make(new Vector3f(0.5F, 0.5F, 0.5F), Vector3f::normalize);
    public static final Vector3f TRANSFORM_VECTOR = new Vector3f(-1.0F, -1.0F, 0.0F);

    protected GroundCircleParticle(ClientLevel p_108323_, double p_108324_, double p_108325_, double p_108326_) {
        super(p_108323_, p_108324_, p_108325_, p_108326_);
    }

    protected GroundCircleParticle(ClientLevel p_108328_, double p_108329_, double p_108330_, double p_108331_, double p_108332_, double p_108333_, double p_108334_) {
        super(p_108328_, p_108329_, p_108330_, p_108331_, p_108332_, p_108333_, p_108334_);
    }

    public void render(VertexConsumer p_233985_, Camera p_233986_, float p_233987_) {
        this.renderRotatedParticle(p_233985_, p_233986_, p_233987_, (p_234005_) -> {
            p_234005_.mul(new Quaternionf()).rotationX(-MathHelper.modelDegrees(90));
        });
        this.renderRotatedParticle(p_233985_, p_233986_, p_233987_, (p_234000_) -> {
            p_234000_.mul((new Quaternionf()).rotationYXZ(-(float)Math.PI, MathHelper.modelDegrees(90), 0.0F));
        });
    }

    public void renderRotatedParticle(VertexConsumer p_233989_, Camera p_233990_, float p_233991_, Consumer<Quaternionf> p_233992_) {
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

    public void makeCornerVertex(VertexConsumer p_233994_, Vector3f p_233995_, float p_233996_, float p_233997_, int p_233998_) {
        p_233994_.vertex((double)p_233995_.x(), (double)p_233995_.y(), (double)p_233995_.z()).uv(p_233996_, p_233997_).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(p_233998_).endVertex();
    }
}
