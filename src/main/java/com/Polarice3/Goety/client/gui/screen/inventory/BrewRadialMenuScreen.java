package com.Polarice3.Goety.client.gui.screen.inventory;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.events.ClientEvents;
import com.Polarice3.Goety.client.gui.radial.GenericRadialMenu;
import com.Polarice3.Goety.client.gui.radial.IRadialMenuHost;
import com.Polarice3.Goety.client.gui.radial.ItemStackRadialMenuItem;
import com.Polarice3.Goety.common.items.handler.BrewBagItemHandler;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.client.brew.CThrowBrewKeyPacket;
import com.Polarice3.Goety.init.ModKeybindings;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

/**
 * Radial Menu as a whole based of @gigaherz Toolbelt codes: <a href="https://github.com/gigaherz/ToolBelt/blob/master/src/main/java/dev/gigaherz/toolbelt/client/RadialMenuScreen.java">...</a>
 * */
@Mod.EventBusSubscriber(Dist.CLIENT)
public class BrewRadialMenuScreen extends Screen {
    private ItemStack stackEquipped;
    private IItemHandler brewBagHandler;

    private boolean needsRecheckStacks = true;
    private final List<ItemStackRadialMenuItem> cachedMenuItems = Lists.newArrayList();
    private final GenericRadialMenu menu;

    public BrewRadialMenuScreen() {
        super(Component.literal("BREW RADIAL MENU"));

        this.stackEquipped = CuriosFinder.findBrewBag(Minecraft.getInstance().player);
        this.brewBagHandler = this.stackEquipped.getCount() > 0 ? BrewBagItemHandler.get(this.stackEquipped) : null;
        this.menu = new GenericRadialMenu(Minecraft.getInstance(), Goety.location("textures/gui/brew_wheel.png"), new IRadialMenuHost() {
            @Override
            public void renderTooltip(GuiGraphics matrixStack, ItemStack stack, int mouseX, int mouseY) {
                matrixStack.renderTooltip(font, stack, mouseX, mouseY);
            }

            @Override
            public Screen getScreen() {
                return BrewRadialMenuScreen.this;
            }

            @Override
            public Font getFontRenderer() {
                return font;
            }

        }) {
            @Override
            public void onClickOutside() {
                close();
            }
        };
    }

    @SubscribeEvent
    public static void overlayEvent(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() != VanillaGuiOverlay.CROSSHAIR.type())
            return;

        if (Minecraft.getInstance().screen instanceof BrewRadialMenuScreen) {
            event.setCanceled(true);
        }
    }

    @Override
    public void removed() {
        super.removed();
        ClientEvents.wipeOpen();
    }

    @Override
    public void tick() {
        super.tick();

        this.menu.tick();

        Player player = Minecraft.getInstance().player;
        if (player != null && player.isAlive()) {
            boolean hasBrewInBag = this.brewBagHandler != null && CuriosFinder.hasBrewInBag(player);
            boolean noBrewInBag = this.brewBagHandler == null || !hasBrewInBag;

            if (this.menu.isClosed() || noBrewInBag) {
                Minecraft.getInstance().setScreen(null);
                ClientEvents.wipeOpen();
            }

            if (!this.menu.isReady() || noBrewInBag) {
                return;
            }

            ItemStack stack = CuriosFinder.findBrewBag(player);
            if (stack.getCount() <= 0) {
                this.brewBagHandler = null;
                this.stackEquipped = null;
            } else if (this.stackEquipped != stack) {
                this.stackEquipped = stack;
                this.brewBagHandler = BrewBagItemHandler.get(stack);
                this.needsRecheckStacks = true;
            }

            if (!ClientEvents.isKeyDown(ModKeybindings.brewCircle())) {
                processClick();
            }
        } else {
            Minecraft.getInstance().setScreen(null);
            ClientEvents.wipeOpen();
        }
    }

    @Override
    public boolean mouseReleased(double p_94722_, double p_94723_, int p_94724_) {
        processClick();
        return super.mouseReleased(p_94722_, p_94723_, p_94724_);
    }

    protected void processClick() {
        this.menu.clickItem();
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        matrixStack.pose().pushPose();
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        matrixStack.pose().popPose();

        Player player = Minecraft.getInstance().player;

        if (player != null && player.isAlive()) {
            boolean hasBrewInBag = this.brewBagHandler != null && CuriosFinder.hasBrewInBag(player);

            if (this.brewBagHandler == null || !hasBrewInBag) {
                return;
            }

            if (needsRecheckStacks) {
                this.cachedMenuItems.clear();
                if (this.brewBagHandler != null) {
                    for (int i = 1; i < this.brewBagHandler.getSlots(); i++) {
                        ItemStack inSlot = this.brewBagHandler.getStackInSlot(i);
                        ItemStackRadialMenuItem item = new ItemStackRadialMenuItem(menu, i, inSlot, Component.translatable("tooltip.goety.focusBag.empty")) {
                            @Override
                            public boolean onClick() {
                                return BrewRadialMenuScreen.this.throwBrew(getSlot(), getStack());
                            }
                        };
                        item.setVisible(inSlot.getCount() > 0);
                        this.cachedMenuItems.add(item);
                    }
                }

                this.menu.clear();
                this.menu.addAll(this.cachedMenuItems);

                this.needsRecheckStacks = false;
            }

            this.menu.setCentralItem(ItemStack.EMPTY);

            this.menu.draw(matrixStack, partialTicks, mouseX, mouseY);
        } else {
            this.menu.close();
        }
    }

    private boolean throwBrew(int slotNumber, ItemStack itemMouseOver){
        if (itemMouseOver.getCount() > 0) {
            ModNetwork.sendToServer(new CThrowBrewKeyPacket(slotNumber));
        }

        this.menu.close();
        return true;
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
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