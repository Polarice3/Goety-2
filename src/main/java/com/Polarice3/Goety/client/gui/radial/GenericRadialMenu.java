package com.Polarice3.Goety.client.gui.radial;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.TriConsumer;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static net.minecraft.client.gui.GuiComponent.blit;

public class GenericRadialMenu {
    public static final float OPEN_ANIMATION_LENGTH = 2.5f;

    public final IRadialMenuHost host;
    private final List<RadialMenuItem> items = Lists.newArrayList();
    private final List<RadialMenuItem> visibleItems = Lists.newArrayList();
    private ItemStack centralItem = ItemStack.EMPTY;
    private final Minecraft minecraft;
    public int backgroundColor = 0x3F000000;
    public int backgroundColorHover = 0x3FFFFFFF;
    public ResourceLocation customBackground;

    public enum State {
        INITIALIZING,
        OPENING,
        NORMAL,
        CLOSING,
        CLOSED
    }

    private State state = State.INITIALIZING;
    public double startAnimation;
    public float animProgress;
    public float radiusIn;
    public float radiusOut;
    public float itemRadius;

    public GenericRadialMenu(Minecraft minecraft, ResourceLocation customBackground, IRadialMenuHost host) {
        this.minecraft = minecraft;
        this.customBackground = customBackground;
        this.host = host;
    }

    public void setCentralItem(ItemStack item) {
        this.centralItem = item;
    }

    public ItemStack getCentralItem() {
        return this.centralItem;
    }

    public int getHovered() {
        for (int i = 0; i < visibleItems.size(); i++) {
            if (visibleItems.get(i).isHovered()) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    public RadialMenuItem getHoveredItem() {
        for (RadialMenuItem item : visibleItems) {
            if (item.isHovered()) {
                return item;
            }
        }
        return null;
    }

    public void setHovered(int which) {
        for (int i = 0; i < visibleItems.size(); i++) {
            visibleItems.get(i).setHovered(i == which);
        }
    }

    public int getVisibleItemCount() {
        return visibleItems.size();
    }

    public void clickItem() {
        if (Objects.requireNonNull(state) == State.NORMAL) {
            RadialMenuItem item = getHoveredItem();
            if (item != null) {
                item.onClick();
                return;
            }
        }
        onClickOutside();
    }

    public void onClickOutside() {
    }

    public boolean isClosed() {
        return state == State.CLOSED;
    }

    public boolean isReady() {
        return state == State.NORMAL;
    }

    public void visibilityChanged(RadialMenuItem item) {
        visibleItems.clear();
        for (RadialMenuItem radialMenuItem : items) {
            if (radialMenuItem.isVisible()) {
                visibleItems.add(radialMenuItem);
            }
        }
    }

    public void add(RadialMenuItem item) {
        items.add(item);
        if (item.isVisible()) {
            visibleItems.add(item);
        }
    }

    public void addAll(Collection<? extends RadialMenuItem> cachedMenuItems) {
        items.addAll(cachedMenuItems);
        for (RadialMenuItem cachedMenuItem : cachedMenuItems) {
            if (cachedMenuItem.isVisible()) {
                visibleItems.add(cachedMenuItem);
            }
        }
    }

    public void clear() {
        items.clear();
        visibleItems.clear();
    }

    public void close() {
        state = State.CLOSING;
        startAnimation = minecraft.level.getGameTime() + (double) minecraft.getFrameTime();
        animProgress = 1.0f;
        setHovered(-1);
    }

    public void tick() {
        if (state == State.INITIALIZING) {
            startAnimation = minecraft.level.getGameTime() + (double) minecraft.getFrameTime();
            state = State.OPENING;
            animProgress = 0;
        }
    }

    public void draw(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        updateAnimationState(partialTicks);

        if (isClosed()) {
            return;
        }

        if (isReady()) {
            processMouse(mouseX, mouseY);
        }

        Screen owner = host.getScreen();
        Font fontRenderer = host.getFontRenderer();
        ItemRenderer itemRenderer = host.getItemRenderer();

        boolean animated = state == State.OPENING || state == State.CLOSING;
        this.radiusIn = animated ? Math.max(0.1f, 30 * this.animProgress) : 30;
        this.radiusOut = this.radiusIn * 2;
        this.itemRadius = (this.radiusIn + this.radiusOut) * 0.5f;
        float animTop = animated ? (1 - this.animProgress) * owner.height / 2.0F : 0;
        float animSide = animated ? (1 - this.animProgress) * owner.width / 2.0F : 0;
        float anim = animated ? this.animProgress : 1;

        int x = owner.width / 2;
        int y = owner.height / 2;
        float z = 0;

        if (this.customBackground != null) {
            matrixStack.pushPose();
            matrixStack.translate(animSide, animTop - 1, 0);
            matrixStack.scale(anim, anim, anim);
/*            if (minecraft.player != null) {
                matrixStack.mulPose(Vector3f.ZP.rotationDegrees(partialTicks + (float) (minecraft.player.tickCount % 720) / 2.0F));
            }*/
            drawBg(matrixStack, owner);

            matrixStack.popPose();
        }

        matrixStack.pushPose();
        matrixStack.translate(0, animTop, 0);

        drawSlices(x, y, z, this.radiusIn, this.radiusOut);

        matrixStack.popPose();

        if (isReady()) {
            matrixStack.pushPose();
            drawItems(matrixStack, x, y, z, owner.width, owner.height, fontRenderer, itemRenderer);
            matrixStack.popPose();

            if (!this.getCentralItem().isEmpty()) {
                int textX = (owner.width - 16) / 2;
                int textY = (owner.height - 16) / 2;
                PoseStack viewModelPose = RenderSystem.getModelViewStack();
                viewModelPose.pushPose();
                viewModelPose.mulPoseMatrix(matrixStack.last().pose());
                viewModelPose.translate(0.0D, 0.0D, 0.0D);
                RenderSystem.applyModelViewMatrix();
                itemRenderer.renderAndDecorateItem(this.getCentralItem(), (int) textX, (int) textY);
                itemRenderer.renderGuiItemDecorations(fontRenderer, this.getCentralItem(), (int) textX, (int) textY);
                viewModelPose.popPose();
                RenderSystem.applyModelViewMatrix();
            }

            matrixStack.pushPose();
            drawTooltips(matrixStack, mouseX, mouseY);
            matrixStack.popPose();
        }
    }

    private void updateAnimationState(float partialTicks) {
        float openAnimation = 0;
        switch (state) {
            case OPENING -> {
                openAnimation = (float) ((minecraft.level.getGameTime() + partialTicks - startAnimation) / OPEN_ANIMATION_LENGTH);
                if (openAnimation >= 1.0 || getVisibleItemCount() == 0) {
                    openAnimation = 1;
                    state = State.NORMAL;
                }
            }
            case CLOSING -> {
                openAnimation = 1 - (float) ((minecraft.level.getGameTime() + partialTicks - startAnimation) / OPEN_ANIMATION_LENGTH);
                if (openAnimation <= 0 || getVisibleItemCount() == 0) {
                    openAnimation = 0;
                    state = State.CLOSED;
                }
            }
        }
        this.animProgress = openAnimation;
    }

    private void drawTooltips(PoseStack matrixStack, int mouseX, int mouseY) {
        Screen owner = host.getScreen();
        Font fontRenderer = host.getFontRenderer();
        ItemRenderer itemRenderer = host.getItemRenderer();
        for (RadialMenuItem item : visibleItems) {
            if (item.isHovered()) {
                DrawingContext context = new DrawingContext(matrixStack, owner.width, owner.height, mouseX, mouseY, 0, fontRenderer, itemRenderer, host);
                item.drawTooltips(context);
            }
        }
    }

    private void drawItems(PoseStack matrixStack, int x, int y, float z, int width, int height, Font font, ItemRenderer itemRenderer) {
        iterateVisible((item, s, e) -> {
            float middle = (s + e) * 0.5f;
            float posX = x + itemRadius * (float) Math.cos(middle);
            float posY = y + itemRadius * (float) Math.sin(middle);

            DrawingContext context = new DrawingContext(matrixStack, width, height, posX, posY, z, font, itemRenderer, host);
            item.draw(context);
        });
    }

    private void iterateVisible(TriConsumer<RadialMenuItem, Float, Float> consumer) {
        int numItems = visibleItems.size();
        for (int i = 0; i < numItems; i++) {
            float s = (float) getAngleFor(i - 0.5, numItems);
            float e = (float) getAngleFor(i + 0.5, numItems);

            RadialMenuItem item = visibleItems.get(i);
            consumer.accept(item, s, e);
        }
    }

    private void drawBg(PoseStack matrixStack, Screen owner) {
        if (visibleItems.size() > 0 && this.customBackground != null) {
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
            int x = (owner.width - 256) / 2;
            int y = (owner.height - 256) / 2;
            RenderSystem.setShaderTexture(0, this.customBackground);
            blit(matrixStack, x, y, 0.0F, 0.0F, 256, 256, 256, 256);
            RenderSystem.disableBlend();
        }
    }

    private void drawSlices(float x, float y, float z, float radiusIn, float radiusOut) {
        if (visibleItems.size() > 0) {
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder buffer = tessellator.getBuilder();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            iterateVisible((item, s, e) -> {
                int color = item.isHovered() ? backgroundColorHover : backgroundColor;
                drawPieArc(buffer, x, y, z, radiusIn, radiusOut, s, e, color);
            });
            tessellator.end();
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }
    }

    private static final float PRECISION = 2.5f / 360.0f;

    private void drawPieArc(BufferBuilder buffer, float x, float y, float z, float radiusIn, float radiusOut, float startAngle, float endAngle, int color) {
        float angle = endAngle - startAngle;
        int sections = Math.max(1, Mth.ceil(angle / PRECISION));

        angle = endAngle - startAngle;

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        int a = (color >> 24) & 0xFF;

        float slice = angle / sections;

        for (int i = 0; i < sections; i++) {
            float angle1 = startAngle + i * slice;
            float angle2 = startAngle + (i + 1) * slice;

            float pos1InX = x + radiusIn * (float) Math.cos(angle1);
            float pos1InY = y + radiusIn * (float) Math.sin(angle1);
            float pos1OutX = x + radiusOut * (float) Math.cos(angle1);
            float pos1OutY = y + radiusOut * (float) Math.sin(angle1);
            float pos2OutX = x + radiusOut * (float) Math.cos(angle2);
            float pos2OutY = y + radiusOut * (float) Math.sin(angle2);
            float pos2InX = x + radiusIn * (float) Math.cos(angle2);
            float pos2InY = y + radiusIn * (float) Math.sin(angle2);

            buffer.vertex(pos1OutX, pos1OutY, z).color(r, g, b, a).endVertex();
            buffer.vertex(pos1InX, pos1InY, z).color(r, g, b, a).endVertex();
            buffer.vertex(pos2InX, pos2InY, z).color(r, g, b, a).endVertex();
            buffer.vertex(pos2OutX, pos2OutY, z).color(r, g, b, a).endVertex();
        }
    }

    public void cyclePrevious() {
        int numItems = getVisibleItemCount();
        int which = getHovered();
        which--;
        if (which < 0) {
            which = numItems - 1;
        }
        setHovered(which);

        moveMouseToItem(which, numItems);
    }

    public void cycleNext() {
        int numItems = getVisibleItemCount();
        int which = getHovered();
        if (which < 0) {
            which = 0;
        } else {
            which++;
            if (which >= numItems)
                which = 0;
        }
        moveMouseToItem(which, numItems);
        setHovered(which);
    }

    private void moveMouseToItem(int which, int numItems) {
        Screen owner = host.getScreen();
        int x = owner.width / 2;
        int y = owner.height / 2;
        float angle = (float) getAngleFor(which, numItems);
        setMousePosition(
                x + itemRadius * Math.cos(angle),
                y + itemRadius * Math.sin(angle)
        );
    }

    private void setMousePosition(double x, double y) {
        Screen owner = host.getScreen();
        Window mainWindow = minecraft.getWindow();
        GLFW.glfwSetCursorPos(mainWindow.getWindow(), (int) (x * mainWindow.getScreenWidth() / owner.width), (int) (y * mainWindow.getScreenHeight() / owner.height));
    }

    private static final double TWO_PI = 2.0 * Math.PI;

    private void processMouse(int mouseX, int mouseY) {
        if (!isReady()) {
            return;
        }

        int numItems = getVisibleItemCount();

        Screen owner = host.getScreen();
        int x = owner.width / 2;
        int y = owner.height / 2;
        double a = Math.atan2(mouseY - y, mouseX - x);
        double d = Math.sqrt(Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2));
        if (numItems > 0) {
            double s0 = getAngleFor(0 - 0.5, numItems);
            double s1 = getAngleFor(numItems - 0.5, numItems);
            while (a < s0) {
                a += TWO_PI;
            }
            while (a >= s1) {
                a -= TWO_PI;
            }
        }

        int hovered = -1;
        for (int i = 0; i < numItems; i++) {
            float s = (float) getAngleFor(i - 0.5, numItems);
            float e = (float) getAngleFor(i + 0.5, numItems);

            if (a >= s && a < e && d >= radiusIn && (d < radiusOut)) {
                hovered = i;
                break;
            }
        }
        setHovered(hovered);

/*        Window mainWindow = minecraft.getWindow();

        int windowWidth = mainWindow.getScreenWidth();
        int windowHeight = mainWindow.getScreenHeight();

        double[] xPos = new double[1];
        double[] yPos = new double[1];
        GLFW.glfwGetCursorPos(mainWindow.getWindow(), xPos, yPos);

        double scaledX = xPos[0] - (windowWidth / 2.0f);
        double scaledY = yPos[0] - (windowHeight / 2.0f);

        double distance = Math.sqrt(scaledX * scaledX + scaledY * scaledY);
        double radius = radiusOut * (windowWidth / (float) owner.width) * 0.975;

        if (distance > radius) {
            double fixedX = scaledX * radius / distance;
            double fixedY = scaledY * radius / distance;

            GLFW.glfwSetCursorPos(mainWindow.getWindow(), (int) (windowWidth / 2 + fixedX), (int) (windowHeight / 2 + fixedY));
        }*/
    }

    private double getAngleFor(double i, int numItems) {
        if (numItems == 0) {
            return 0;
        }
        return ((i / numItems) + 0.25) * TWO_PI + Math.PI;
    }
}
/*
 * Copyright (c) 2015, David Quintana <gigaherz@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the author nor the
 *       names of the contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */