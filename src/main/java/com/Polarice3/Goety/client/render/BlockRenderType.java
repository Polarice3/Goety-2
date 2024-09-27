package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.OptionalDouble;

/**
 * Stolem from @lothrazar's codes:<a href="https://github.com/Lothrazar/FLib/blob/trunk/1.20.1/src/main/java/com/lothrazar/library/render/type/FakeBlockRenderTypes.java">...</a>
 */
public class BlockRenderType extends RenderType {
    private static final boolean MIPMAP = false;
    private static final boolean BLUR = false;
    private static final boolean SORT = false;
    private static final boolean CRUMBLING = false;
    private static final int BUFFERSIZE = 256;

    public BlockRenderType(String nameIn, VertexFormat formatIn, VertexFormat.Mode drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    public final static ResourceLocation BEAM = Goety.location("textures/particle/block_beam.png");
    public static final RenderType LASER_MAIN_BEAM = create(Goety.MOD_ID + ":block_beam",
            DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, BUFFERSIZE, CRUMBLING, SORT,
            CompositeState.builder()
                    .setTextureState(new TextureStateShard(BEAM, BLUR, MIPMAP)).setShaderState(ShaderStateShard.POSITION_COLOR_TEX_SHADER)
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setCullState(NO_CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false));
    /**
     * used by TESR that render blocks with textures Shape builder, ghostsoundmuffler, render light camo.
     */
    public static final RenderType FAKE_BLOCK = create(Goety.MOD_ID + ":fakeblock",
            DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, BUFFERSIZE, CRUMBLING, SORT,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_SOLID_SHADER) //1.17 was -   BLOCK_SHADER
                    .setLayeringState(POLYGON_OFFSET_LAYERING) // VIEW_OFFSET_Z_LAYERING) //                    .setShadeModelState(SMOOTH_SHADE)
                    .setLightmapState(NO_LIGHTMAP)
                    .setTextureState(BLOCK_SHEET_MIPPED)
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setCullState(CULL)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .createCompositeState(false));
    /**
     * used by EventRender -> RenderWorldLastEvent by most held items that pick locations, such as cyclic:location_data
     */
    public static final RenderType TRANSPARENT_COLOUR = create(Goety.MOD_ID + ":transparent_colour",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            BUFFERSIZE,
            CRUMBLING,
            SORT,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_LINES_SHADER)
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setTextureState(NO_TEXTURE)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setCullState(CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .createCompositeState(false));
    /**
     * used by most blocks that select blocks such as cyclic:forester, cyclic:harvester, cyclic:miner in TESRs
     */
    public static final RenderType SOLID_COLOUR = create(Goety.MOD_ID + ":solidcolour",
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, BUFFERSIZE, CRUMBLING, SORT,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_LINES_SHADER)
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setTextureState(NO_TEXTURE)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setCullState(CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .createCompositeState(false));
    /**
     * Used by cyclic:prospector
     */
    public static final RenderType TOMB_LINES = create(Goety.MOD_ID + ":tomb_lines",
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES, BUFFERSIZE, CRUMBLING, SORT,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_LINES_SHADER)
                    .setLineState(new LineStateShard(OptionalDouble.of(2.5D)))
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .createCompositeState(false));
}
