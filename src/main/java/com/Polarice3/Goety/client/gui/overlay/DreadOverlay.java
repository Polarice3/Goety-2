package com.Polarice3.Goety.client.gui.overlay;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.hostile.Wight;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class DreadOverlay {
    public static final IGuiOverlay OVERLAY = DreadOverlay::drawOverlay;
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static void drawOverlay(ForgeGui gui, GuiGraphics ms, float partialTicks, int screenWidth, int screenHeight) {
        if (minecraft.player != null){
            Player player = minecraft.player;
            Wight wight = Wight.findWight(player);
            if (wight != null) {
                ResourceLocation overlay;
                int frame = gui.getGuiTicks() % 16;

                overlay = switch (frame) {
                    default -> Goety.location("textures/gui/dread/dread_overlay_0.png");
                    case 4, 5, 6, 7 -> Goety.location("textures/gui/dread/dread_overlay_1.png");
                    case 8, 9, 10, 11 -> Goety.location("textures/gui/dread/dread_overlay_2.png");
                    case 12, 13, 14, 15 -> Goety.location("textures/gui/dread/dread_overlay_3.png");
                };

                gui.setupOverlayRenderState(true, false);
                float alpha = 1.0F - (Math.min(1.0F, wight.distanceTo(player) / 56.0F));
                renderOverlay(overlay, alpha, screenWidth, screenHeight);
            }
        }
    }

    public static void renderOverlay(ResourceLocation location, float alpha, int screenWidth, int screenHeight) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.setShaderTexture(0, location);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(0.0, (double)screenHeight, -90.0).uv(0.0F, 1.0F).endVertex();
        bufferbuilder.vertex((double)screenWidth, (double)screenHeight, -90.0).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex((double)screenWidth, 0.0, -90.0).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(0.0, 0.0, -90.0).uv(0.0F, 0.0F).endVertex();
        tesselator.end();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
