package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.init.ModShaders;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.function.Function;

public class ModRenderType {
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_EYES_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEyesShader);
    protected static final RenderStateShard.TextureStateShard BLOCK_SHEET = new RenderStateShard.TextureStateShard(InventoryMenu.BLOCK_ATLAS, false, false);
    protected static final RenderStateShard.TransparencyStateShard NO_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("no_transparency", RenderSystem::disableBlend, () -> {
    });
    protected static final RenderStateShard.TransparencyStateShard ADDITIVE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderStateShard.TransparencyStateShard TRANSLUCENT_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderStateShard.CullStateShard NO_CULL = new RenderStateShard.CullStateShard(false);
    protected static final RenderStateShard.LightmapStateShard LIGHTMAP = new RenderStateShard.LightmapStateShard(true);
    protected static final RenderStateShard.OverlayStateShard OVERLAY = new RenderStateShard.OverlayStateShard(true);
    protected static final RenderStateShard.WriteMaskStateShard COLOR_WRITE = new RenderStateShard.WriteMaskStateShard(true, false);
    private static final Function<ResourceLocation, RenderType> WRAITH = Util.memoize((p_173253_) -> {
        RenderStateShard.TextureStateShard renderstateshard$texturestateshard = new RenderStateShard.TextureStateShard(p_173253_, false, false);
        return RenderType.create(source("wraith"), DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_EYES_SHADER).setTextureState(renderstateshard$texturestateshard).setTransparencyState(ADDITIVE_TRANSPARENCY).setCullState(NO_CULL).setOverlayState(OVERLAY).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
    });

    public static RenderType wraith(ResourceLocation p_110459_) {
        return WRAITH.apply(p_110459_);
    }

    /**
     * Beam render Types and StateShards based on MyRenderType on @Thelnfamous1's Dungeon Gears
     */
    protected static final RenderStateShard.LayeringStateShard VIEW_OFFSET_Z_LAYERING = new RenderStateShard.LayeringStateShard("view_offset_z_layering", () -> {
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.scale(0.99975586F, 0.99975586F, 0.99975586F);
        RenderSystem.applyModelViewMatrix();
    }, () -> {
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
    });
    protected static final RenderStateShard.DepthTestStateShard NO_DEPTH_TEST = new RenderStateShard.DepthTestStateShard("always", 519);
    protected static final RenderStateShard.LightmapStateShard NO_LIGHTMAP = new RenderStateShard.LightmapStateShard(false);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENERGY_SWIRL_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEnergySwirlShader);

    private static final Function<ResourceLocation, RenderType> BEACON_BEAM = Util.memoize((p_173253_) -> {
        RenderStateShard.TextureStateShard renderstateshard$texturestateshard = new RenderStateShard.TextureStateShard(p_173253_, false, false);
        return RenderType.create(source("magic_beam"),
                DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, false, false,
                RenderType.CompositeState.builder().setTextureState(renderstateshard$texturestateshard)
                        .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(NO_DEPTH_TEST)
                        .setCullState(NO_CULL)
                        .setLightmapState(NO_LIGHTMAP)
                        .setWriteMaskState(COLOR_WRITE)
                        .setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
                        .createCompositeState(false));
    });

    public static RenderType magicBeam(ResourceLocation p_110459_) {
        return BEACON_BEAM.apply(p_110459_);
    }

    protected static final RenderStateShard.TransparencyStateShard LIGHTNING_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("lightning_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderStateShard.ShaderStateShard POSITION_COLOR_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader);
    protected static final RenderType.CompositeState LIGHTNING_STATE = RenderType.CompositeState.builder()
            .setShaderState(POSITION_COLOR_SHADER)
            .setTransparencyState(LIGHTNING_TRANSPARENCY)
            .createCompositeState(false);
    public static final RenderType LIGHTNING = RenderType.create(source("lightning"), DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, true, LIGHTNING_STATE);
    protected static final RenderStateShard.ShaderStateShard HOLE_SHADER = new RenderStateShard.ShaderStateShard(ModShaders::getHoleShader);

    private static final RenderType HOLE = RenderType.create(source("hole"),
            DefaultVertexFormat.POSITION,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(HOLE_SHADER)
                    .setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                            .add(TheEndPortalRenderer.END_SKY_LOCATION,
                                    false, false)
                            .add(TheEndPortalRenderer.END_PORTAL_LOCATION,
                                    false, false).build())
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .createCompositeState(false));

    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_LIGHTNING_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeLightningShader);

    public static final RenderType DRAGON_RAYS_QUADS = RenderType.create(source("dragon_rays_quads"), DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, RenderType.TRANSIENT_BUFFER_SIZE, true, true, RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
            .setTransparencyState(LIGHTNING_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .setWriteMaskState(COLOR_WRITE)
            .createCompositeState(true));

    public static RenderType hole() {
        return HOLE;
    }

    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityTranslucentShader);

    public static final Function<ResourceLocation, RenderType> ENTITY_TRANSLUCENT_NO_DEPTH = Util.memoize(location ->
            RenderType.create(source("entity_translucent_no_depth"), DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RenderType.TRANSIENT_BUFFER_SIZE, true, true, RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_WRITE)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true)));

    public static RenderType entityTranslucentNoDepth(ResourceLocation location) {
        return ENTITY_TRANSLUCENT_NO_DEPTH.apply(location);
    }

    protected static final RenderStateShard.DepthTestStateShard LEQUAL_DEPTH_TEST = new RenderStateShard.DepthTestStateShard("<=", 515);

    public static RenderType getWaterStream(ResourceLocation resourceLocation) {
        return RenderType.create(source("water_stream"), DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER).setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, true)).setLightmapState(LIGHTMAP).setCullState(NO_CULL).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDepthTestState(LEQUAL_DEPTH_TEST).createCompositeState(true));
    }

    private static final Function<ResourceLocation, RenderType> ORB_CENTER = Util.memoize((p_286170_) -> {
        RenderStateShard.TextureStateShard renderstateshard$texturestateshard = new RenderStateShard.TextureStateShard(p_286170_, false, false);
        return RenderType.create(source("orb_center"), DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_EYES_SHADER).setTextureState(renderstateshard$texturestateshard).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
    });

    public static RenderType orbCenter(ResourceLocation p_110489_) {
        return ORB_CENTER.apply(p_110489_);
    }

    private static final Function<ResourceLocation, RenderType> EYES = Util.memoize((p_286170_) -> {
        RenderStateShard.TextureStateShard renderstateshard$texturestateshard = new RenderStateShard.TextureStateShard(p_286170_, false, false);
        return RenderType.create(source("eyes"), DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_EYES_SHADER).setTextureState(renderstateshard$texturestateshard).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
    });

    public static RenderType eyes(ResourceLocation p_110489_) {
        return EYES.apply(p_110489_);
    }

    /**
     * Based/Stolen from @RCXcrafter's Embers Rekindled codes: <a href="https://github.com/RCXcrafter/EmbersRekindled/blob/rekindled/src/main/java/com/rekindled/embers/render/EmbersRenderTypes.java#L49">...</a>
     */
    public static ParticleRenderType PARTICLE_ADDITIVE = new ParticleRenderType() {
        public void begin(BufferBuilder p_107455_, TextureManager p_107456_) {
            RenderSystem.enableDepthTest();
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
            RenderSystem.depthMask(false);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            p_107455_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        public void end(Tesselator p_107458_) {
            p_107458_.end();
        }

        public String toString() {
            return source("additive");
        }
    };

    private static String source(String name) {
        return Goety.MOD_ID + ":" + name;
    }
}
