package com.Polarice3.Goety.client.gui.screen.inventory;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.inventory.container.FocusBagContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FocusBagScreen extends AbstractContainerScreen<FocusBagContainer> {
    private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Goety.MOD_ID, "textures/gui/container/focusbag.png");

    public FocusBagScreen(FocusBagContainer p_i51097_1_, Inventory p_i51097_2_, Component p_i51097_3_) {
        super(p_i51097_1_, p_i51097_2_, p_i51097_3_);
    }

    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.minecraft != null;
        RenderSystem.setShaderTexture(0, GUI_TEXTURES);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}
