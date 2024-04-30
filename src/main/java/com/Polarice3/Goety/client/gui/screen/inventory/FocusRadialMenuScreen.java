package com.Polarice3.Goety.client.gui.screen.inventory;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.items.magic.IFocus;
import com.Polarice3.Goety.client.events.ClientEvents;
import com.Polarice3.Goety.client.gui.radial.*;
import com.Polarice3.Goety.common.items.handler.FocusBagItemHandler;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.client.focus.CAddFocusToBagPacket;
import com.Polarice3.Goety.common.network.client.focus.CAddFocusToInventoryPacket;
import com.Polarice3.Goety.common.network.client.focus.CSwapFocusPacket;
import com.Polarice3.Goety.common.network.client.focus.CSwapFocusTwoPacket;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModKeybindings;
import com.Polarice3.Goety.utils.TotemFinder;
import com.Polarice3.Goety.utils.WandUtil;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
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
public class FocusRadialMenuScreen extends Screen {
    private ItemStack stackEquipped;
    private IItemHandler focusBagHandler;

/*    private boolean keyCycleBeforeL = false;
    private boolean keyCycleBeforeR = false;*/

    private boolean needsRecheckStacks = true;
    private final List<ItemStackRadialMenuItem> cachedMenuItems = Lists.newArrayList();
    private final TextRadialMenuItem insertMenuItem;
    private final TextRadialMenuItem extractMenuItem;
    private final GenericRadialMenu menu;

    private ItemRenderer getItemRenderer()
    {
        return itemRenderer;
    }

    public FocusRadialMenuScreen() {
        super(Component.literal("FOCUS RADIAL MENU"));

        this.stackEquipped = TotemFinder.findBag(Minecraft.getInstance().player);
        this.focusBagHandler = this.stackEquipped.getCount() > 0 ? FocusBagItemHandler.get(this.stackEquipped) : null;
        this.menu = new GenericRadialMenu(Minecraft.getInstance(), Goety.location("textures/gui/focus_wheel.png"), new IRadialMenuHost() {
            @Override
            public void renderTooltip(PoseStack matrixStack, ItemStack stack, int mouseX, int mouseY) {
                FocusRadialMenuScreen.this.renderTooltip(matrixStack, stack, mouseX, mouseY);
            }

            @Override
            public Screen getScreen() {
                return FocusRadialMenuScreen.this;
            }

            @Override
            public Font getFontRenderer() {
                return font;
            }

            @Override
            public ItemRenderer getItemRenderer() {
                return FocusRadialMenuScreen.this.getItemRenderer();
            }
        }) {
            @Override
            public void onClickOutside() {
                close();
            }
        };
        this.insertMenuItem = new TextRadialMenuItem(menu, Component.translatable("tooltip.goety.focusBag.bag")) {
            @Override
            public boolean onClick() {
                return FocusRadialMenuScreen.this.tryAddFocus();
            }
        };
        this.extractMenuItem = new TextRadialMenuItem(menu, Component.translatable("tooltip.goety.focusBag.extract")) {
            @Override
            public boolean onClick() {
                return FocusRadialMenuScreen.this.tryExtractFocus();
            }
        };
        this.passEvents = MainConfig.WheelGuiMovement.get();
    }

    @SubscribeEvent
    public static void overlayEvent(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() != VanillaGuiOverlay.CROSSHAIR.type())
            return;

        if (Minecraft.getInstance().screen instanceof FocusRadialMenuScreen) {
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
            boolean hasFocusInInv = WandUtil.hasFocusInInv(player);
            boolean hasFocusInBag = this.focusBagHandler != null && TotemFinder.hasFocusInBag(player);
            boolean noFocusInBag = this.focusBagHandler == null || !hasFocusInBag;

            if (this.menu.isClosed() || (noFocusInBag && !hasFocusInInv && WandUtil.findFocus(player).isEmpty())) {
                Minecraft.getInstance().setScreen(null);
                ClientEvents.wipeOpen();
            }

            if (!this.menu.isReady() || (noFocusInBag && !hasFocusInInv && WandUtil.findFocus(player).isEmpty())) {
                return;
            }

            ItemStack stack = TotemFinder.findBag(player);
            if (stack.getCount() <= 0) {
                this.focusBagHandler = null;
                if (hasFocusInInv) {
                    this.stackEquipped = WandUtil.findFocusInInv(player);
                } else {
                    this.stackEquipped = null;
                }
            } else if (this.stackEquipped != stack) {
                this.stackEquipped = stack;
                this.focusBagHandler = FocusBagItemHandler.get(stack);
                this.needsRecheckStacks = true;
            }

            if (!ClientEvents.isKeyDown(ModKeybindings.wandCircle())) {
/*            if (ConfigData.releaseToSwap) {
                processClick();
            } else {
                menu.close();
            }*/
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
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        matrixStack.pushPose();
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        matrixStack.popPose();

        Player player = Minecraft.getInstance().player;

        if (player != null && player.isAlive()) {
            boolean hasFocusInInv = WandUtil.hasFocusInInv(player);
            boolean hasFocusInBag = this.focusBagHandler != null && TotemFinder.hasFocusInBag(player);

            if ((this.focusBagHandler == null || !hasFocusInBag) && !hasFocusInInv && WandUtil.findFocus(player).isEmpty()) {
                return;
            }

            ItemStack inHand = WandUtil.findWand(player);

            if (needsRecheckStacks) {
                this.cachedMenuItems.clear();
                if (this.focusBagHandler != null) {
                    for (int i = 1; i < this.focusBagHandler.getSlots(); i++) {
                        ItemStack inSlot = this.focusBagHandler.getStackInSlot(i);
                        ItemStackRadialMenuItem item = new ItemStackRadialMenuItem(menu, i, inSlot, Component.translatable("tooltip.goety.focusBag.empty")) {
                            @Override
                            public boolean onClick() {
                                return FocusRadialMenuScreen.this.trySwap(getSlot(), getStack());
                            }
                        };
                        item.setVisible(inSlot.getCount() > 0);
                        this.cachedMenuItems.add(item);
                    }
                }
                for (int i = 0; i < player.getInventory().items.size(); i++) {
                    ItemStack inSlot = player.getInventory().getItem(i);
                    if (inSlot.getItem() instanceof IFocus) {
                        ItemStackRadialMenuItem item = new ItemStackRadialMenuItem(menu, i, inSlot, Component.translatable("tooltip.goety.focusBag.empty")) {
                            @Override
                            public boolean onClick() {
                                return FocusRadialMenuScreen.this.trySwapInv(getSlot(), getStack());
                            }
                        };
                        item.setVisible(inSlot.getCount() > 0);
                        this.cachedMenuItems.add(item);
                    }
                }

                this.menu.clear();
                this.menu.addAll(this.cachedMenuItems);
                this.menu.add(this.insertMenuItem);
                this.menu.add(this.extractMenuItem);

                this.needsRecheckStacks = false;
            }

            boolean hasAddButton = !this.cachedMenuItems.stream().allMatch(RadialMenuItem::isVisible) && inHand.getCount() > 0;
            this.insertMenuItem.setVisible(hasAddButton && this.focusBagHandler != null && TotemFinder.hasEmptyBagSpace(player) && !WandUtil.findFocus(player).isEmpty());
            this.extractMenuItem.setVisible(!WandUtil.findFocus(player).isEmpty() && player.getInventory().getFreeSlot() != -1);

            if (!WandUtil.findFocus(player).isEmpty()) {
                this.menu.setCentralItem(WandUtil.findFocus(player));
            } else {
                this.menu.setCentralItem(ItemStack.EMPTY);
            }

//        checkCycleKeybinds();

            this.menu.draw(matrixStack, partialTicks, mouseX, mouseY);
        } else {
            this.menu.close();
        }
    }

    private boolean trySwap(int slotNumber, ItemStack itemMouseOver) {
        ItemStack inHand = WandUtil.findWand(Minecraft.getInstance().player);

        if (inHand.getCount() > 0 || itemMouseOver.getCount() > 0) {
            ModNetwork.sendToServer(new CSwapFocusPacket(slotNumber));
        }

        this.menu.close();
        return true;
    }

    private boolean trySwapInv(int slotNumber, ItemStack itemMouseOver) {
        ItemStack inHand = WandUtil.findWand(Minecraft.getInstance().player);

        if (inHand.getCount() > 0 || itemMouseOver.getCount() > 0) {
            ModNetwork.sendToServer(new CSwapFocusTwoPacket(slotNumber));
        }

        this.menu.close();
        return true;
    }

    private boolean tryAddFocus() {
        ItemStack inHand = WandUtil.findWand(Minecraft.getInstance().player);

        if (inHand.getCount() > 0) {
            ModNetwork.sendToServer(new CAddFocusToBagPacket());
        }

        this.menu.close();
        return true;
    }

    private boolean tryExtractFocus() {
        ItemStack inHand = WandUtil.findFocus(Minecraft.getInstance().player);

        if (inHand.getCount() > 0) {
            ModNetwork.sendToServer(new CAddFocusToInventoryPacket());
        }

        this.menu.close();
        return true;
    }

    private void checkCycleKeybinds() {
/*        if (ClientEvents.isKeyDown(ClientEvents.CYCLE_TOOL_MENU_LEFT_KEYBIND)) {
            if (!this.keyCycleBeforeL) {
                this.menu.cyclePrevious();
            }
            this.keyCycleBeforeL = true;
        } else {
            this.keyCycleBeforeL = false;
        }

        if (ClientEvents.isKeyDown(ClientEvents.CYCLE_TOOL_MENU_RIGHT_KEYBIND)) {
            if (!this.keyCycleBeforeR) {
                this.menu.cycleNext();
            }
            this.keyCycleBeforeR = true;
        } else {
            this.keyCycleBeforeR = false;
        }*/
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