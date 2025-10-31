package com.Polarice3.Goety.api.items;

import com.Polarice3.Goety.Goety;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;

public class IPersistDecorator implements IItemDecorator {
    public static final ResourceLocation BROKEN_OVERLAY = Goety.location("textures/item/broken_overlay.png");

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        if (stack.isEmpty()) {
            return false;
        }
        if (!(stack.getItem() instanceof IPersist persist)) {
            return false;
        } else if (persist.isNotBroken(stack)) {
            return false;
        }

        RenderSystem.disableDepthTest();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 390);

        guiGraphics.blit(BROKEN_OVERLAY, xOffset, yOffset, 16, 16, 0, 0, 16, 16, 16, 16);

        guiGraphics.pose().popPose();
        return true;
    }
}
