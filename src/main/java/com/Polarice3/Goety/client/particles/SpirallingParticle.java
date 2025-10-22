package com.Polarice3.Goety.client.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class SpirallingParticle extends TextureSheetParticle {
    private final float rotSpeed;

    public SpirallingParticle(ClientLevel p_106610_, double p_106611_, double p_106612_, double p_106613_, float size, float red, float green, float blue, int life, SpriteSet p_106617_) {
        super(p_106610_, p_106611_, p_106612_, p_106613_);
        this.xd = 0.0F;
        this.yd = 0.0F;
        this.zd = 0.0F;
        this.rCol = red;
        this.gCol = green;
        this.bCol = blue;
        this.hasPhysics = false;
        this.quadSize = size;
        this.lifetime = life;
        this.setSpriteFromAge(p_106617_);
        this.rotSpeed = -(1.0F / life);
        this.roll = life;
    }

    @Override
    public float getQuadSize(float partialTicks) {
        return this.quadSize * (this.lifetime - this.age + 1.0F) / (float) this.lifetime;
    }

    public int getLightColor(final float partialTicks) {
        return 240;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (++this.age >= this.lifetime) {
            this.remove();
        }
        this.oRoll = this.roll;
        this.roll += (float)Math.PI * this.rotSpeed * 2.0F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return NORMAL_RENDER;
    }

    private static void beginRenderCommon(BufferBuilder buffer, TextureManager textureManager) {
        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
        AbstractTexture tex = textureManager.getTexture(TextureAtlas.LOCATION_PARTICLES);
        tex.setBlurMipmap(true, false);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
    }

    private static void endRenderCommon() {
        AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_PARTICLES);
        tex.restoreLastBlurMipmap();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }

    public static final ParticleRenderType NORMAL_RENDER = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
            beginRenderCommon(bufferBuilder, textureManager);
        }

        @Override
        public void end(Tesselator tessellator) {
            tessellator.end();
            endRenderCommon();
        }

        @Override
        public String toString() {
            return "goety:spiralling";
        }
    };

    public static class Provider implements ParticleProvider<SpirallingParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_106884_) {
            this.sprite = p_106884_;
        }

        @Nullable
        @Override
        public Particle createParticle(SpirallingParticleOption p_107421_, ClientLevel p_107422_, double p_107423_, double p_107424_, double p_107425_, double p_107426_, double p_107427_, double p_107428_) {
            return new SpirallingParticle(p_107422_, p_107423_, p_107424_, p_107425_, p_107421_.size, p_107421_.r, p_107421_.g, p_107421_.b, p_107421_.life, this.sprite);
        }
    }
}
