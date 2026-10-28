package com.Polarice3.Goety.client.gui.screen.inventory;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.events.ClientEvents;
import com.Polarice3.Goety.client.gui.radial.EntityRadialMenuItem;
import com.Polarice3.Goety.client.gui.radial.GenericRadialMenu;
import com.Polarice3.Goety.client.gui.radial.IRadialMenuHost;
import com.Polarice3.Goety.common.items.magic.EsotericTesseract;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.client.CSelectTesseractServantPacket;
import com.Polarice3.Goety.init.ModKeybindings;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TesseractRadialMenuScreen extends Screen {
    private static final float HUB_SIZE = 46.0F;
    private final GenericRadialMenu menu;
    private final List<EntityRadialMenuItem> items = new ArrayList<>();
    private List<String> shownKeys = new ArrayList<>();

    public TesseractRadialMenuScreen() {
        super(Component.literal("TESSERACT RADIAL MENU"));
        this.menu = new GenericRadialMenu(Minecraft.getInstance(), Goety.location("textures/gui/tesseract.png"), new IRadialMenuHost() {
            @Override
            public void renderTooltip(GuiGraphics guiGraphics, ItemStack stack, int mouseX, int mouseY) {
                guiGraphics.renderTooltip(font, stack, mouseX, mouseY);
            }

            @Override
            public Screen getScreen() {
                return TesseractRadialMenuScreen.this;
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
        this.menu.baseRadius = 40.0F;
    }

    public static boolean canOpen(Player player) {
        ItemStack stack = EsotericTesseract.getHeldTesseract(player);
        if (stack.isEmpty() || EsotericTesseract.getServantKeys(stack).isEmpty()) {
            return false;
        }
        return player.getMainHandItem() == stack || WandUtil.findWand(player).isEmpty();
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
        ItemStack stack = player != null && player.isAlive() ? EsotericTesseract.getHeldTesseract(player) : ItemStack.EMPTY;
        if (this.menu.isClosed() || EsotericTesseract.getServantKeys(stack).isEmpty()) {
            Minecraft.getInstance().setScreen(null);
            ClientEvents.wipeOpen();
            return;
        }

        if (this.menu.isReady() && ModKeybindings.wandCircle() != null && !ClientEvents.isKeyDown(ModKeybindings.wandCircle())) {
            this.menu.clickItem();
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.menu.clickItem();
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void refresh(ItemStack stack) {
        List<String> keys = EsotericTesseract.getServantKeys(stack);
        if (!keys.equals(this.shownKeys)) {
            this.shownKeys = keys;
            this.items.clear();
            this.menu.clear();
            CompoundTag tag = stack.getOrCreateTag();
            for (String key : keys) {
                EntityRadialMenuItem item = new EntityRadialMenuItem(this.menu, key, tag.getCompound(key).copy()) {
                    @Override
                    public boolean onClick() {
                        return TesseractRadialMenuScreen.this.select(this.getKey());
                    }
                };
                item.setVisible(true);
                this.items.add(item);
            }
            this.menu.addAll(this.items);
        }
        String next = EsotericTesseract.getNextServantKey(stack);
        for (EntityRadialMenuItem item : this.items) {
            item.setNext(item.getKey().equals(next));
        }
    }

    private boolean select(String key) {
        ModNetwork.sendToServer(new CSelectTesseractServantPacket(key));
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.TOCK.get(), 1.0F));
        this.menu.close();
        return true;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        Player player = Minecraft.getInstance().player;
        ItemStack stack = player != null && player.isAlive() ? EsotericTesseract.getHeldTesseract(player) : ItemStack.EMPTY;
        if (EsotericTesseract.getServantKeys(stack).isEmpty()) {
            this.menu.close();
            return;
        }

        this.refresh(stack);
        this.menu.draw(guiGraphics, partialTicks, mouseX, mouseY);

        if (this.menu.isReady()) {
            EntityRadialMenuItem hub = null;
            for (EntityRadialMenuItem item : this.items) {
                if (item.isHovered()) {
                    hub = item;
                    break;
                }
            }
            if (hub == null) {
                String next = EsotericTesseract.getNextServantKey(stack);
                hub = this.items.stream().filter(item -> item.getKey().equals(next)).findFirst().orElse(null);
            }
            LivingEntity entity = hub != null ? hub.getPreview() : null;
            if (entity != null) {
                EntityRadialMenuItem.renderSpinning(guiGraphics, entity, this.width / 2.0F, this.height / 2.0F, HUB_SIZE);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}