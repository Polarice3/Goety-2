/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
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
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class SparkleParticle extends TextureSheetParticle {
    public final int particle = 16;
    private final boolean slowdown = true;
    private final SpriteSet sprite;

    public SparkleParticle(ClientLevel world, double x, double y, double z, float size, float red, float green, float blue, int extraLife, SpriteSet sprite) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.rCol = red;
        this.gCol = green;
        this.bCol = blue;
        this.alpha = 0.75F;
        this.gravity = 0;
        this.xd = this.yd = this.zd = 0;
        this.quadSize = (this.random.nextFloat() * 0.5F + 0.5F) * 0.2F * size;
        this.lifetime = 3 * extraLife;
        this.setSize(0.01F, 0.01F);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.sprite = sprite;
        setSpriteFromAge(sprite);
    }

    @Override
    public float getQuadSize(float partialTicks) {
        return quadSize * (this.lifetime - this.age + 1.0F) / (float) this.lifetime;
    }

    @Override
    public void tick() {
        setSpriteFromAge(sprite);
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            remove();
        }

        this.yd -= 0.04D * this.gravity;

        this.move(this.xd, this.yd, this.zd);

        if (this.slowdown) {
            this.xd *= 0.908000001907348633D;
            this.yd *= 0.908000001907348633D;
            this.zd *= 0.908000001907348633D;

            if (this.onGround) {
                this.xd *= 0.69999998807907104D;
                this.zd *= 0.69999998807907104D;
            }
        }
    }

    @NotNull
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
            return "goety:sparkle";
        }
    };

    public static class Provider implements ParticleProvider<SparkleParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(SparkleParticleOption data, ClientLevel world, double x, double y, double z, double mx, double my, double mz) {
            return new SparkleParticle(world, x, y, z, data.size, data.r, data.g, data.b, data.extraLife, sprite);
        }
    }
}
