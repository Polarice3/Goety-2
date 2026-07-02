package com.Polarice3.Goety.client.gui.overlay;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.curios.EternalCauldronItem;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class EternalCauldronGui {
    public static final IGuiOverlay OVERLAY = EternalCauldronGui::drawOverlay;
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static Font getFont() {
        return minecraft.font;
    }

    public static void drawOverlay(ForgeGui gui, GuiGraphics ms, float partialTicks, int screenWidth, int screenHeight) {
        if (!MainConfig.ECGuiShow.get()) {
            return;
        }

        if (minecraft.player != null){
            Player player = minecraft.player;
            ItemStack itemStack = CuriosFinder.findCurio(player, ModItems.ETERNAL_CAULDRON.get());

            if (!itemStack.isEmpty()) {
                if (!EternalCauldronItem.getBottle(itemStack).isEmpty()) {
                    ItemStack show = itemStack;
                    if (SEHelper.isOnCooldown(player, itemStack)) {
                        show = new ItemStack(ModItems.ETERNAL_CAULDRON.get());
                    }
                    ms.pose().pushPose();
                    int i = screenWidth / 2;
                    int x = i - 91 - 29 - 29;
                    int y = screenHeight - 16 - 3;
                    x += MainConfig.ECGuiHorizontal.get();
                    y += MainConfig.ECGuiVertical.get();
                    ms.renderFakeItem(show, x, y);
                    ms.renderItemDecorations(minecraft.font, show, x, y);
                    ms.pose().popPose();
                }
            }
        }
    }
}
