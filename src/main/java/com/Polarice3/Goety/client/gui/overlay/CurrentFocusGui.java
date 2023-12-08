package com.Polarice3.Goety.client.gui.overlay;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.client.gui.screen.inventory.FocusRadialMenuScreen;
import com.Polarice3.Goety.utils.WandUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.level.GameType;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class CurrentFocusGui extends GuiComponent {
    public static final IGuiOverlay OVERLAY = CurrentFocusGui::drawHUD;
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static boolean shouldDisplayBar(){
        return !WandUtil.findFocus(minecraft.player).isEmpty() && (minecraft.gameMode != null && minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) && !(minecraft.screen instanceof FocusRadialMenuScreen) && MainConfig.FocusGuiShow.get();
    }

    public static void drawHUD(ForgeGui gui, PoseStack ms, float partialTicks, int screenWidth, int screenHeight) {
        if(!shouldDisplayBar()) {
            return;
        }

        ms.pushPose();
        ms.scale(1.0F, 1.0F, 1.0F);
        if (WandUtil.findFocus(minecraft.player) != null) {
            minecraft.getItemRenderer().renderGuiItem(WandUtil.findFocus(minecraft.player), ((screenWidth - 16) / 2) + MainConfig.FocusGuiHorizontal.get(), (screenHeight - 52) + MainConfig.FocusGuiVertical.get());
            minecraft.getItemRenderer().renderGuiItemDecorations(minecraft.font, WandUtil.findFocus(minecraft.player), ((screenWidth - 16) / 2) + MainConfig.FocusGuiHorizontal.get(), (screenHeight - 52) + MainConfig.FocusGuiVertical.get());
        }
        ms.popPose();
    }
}
