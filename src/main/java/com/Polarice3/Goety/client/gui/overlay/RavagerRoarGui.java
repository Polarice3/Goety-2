package com.Polarice3.Goety.client.gui.overlay;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.entities.ally.ModRavager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class RavagerRoarGui {
    public static final IGuiOverlay OVERLAY = RavagerRoarGui::drawHUD;
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static boolean shouldDisplayBar(){
        return minecraft.player != null && minecraft.player.getVehicle() instanceof ModRavager ravager && ravager.getRoarCool() > 0;
    }

    public static void drawHUD(ForgeGui gui, GuiGraphics guiGraphics, float partialTicks, int screenWidth, int screenHeight) {
        if(!shouldDisplayBar()) {
            return;
        }
        int i = (screenWidth/2) + (MainConfig.SoulGuiHorizontal.get());
        int RoarCool = 0;
        if (minecraft.player.getVehicle() instanceof ModRavager ravager){
            RoarCool = ravager.getRoarCool();
        }
        int RoarCoolTotal = ModRavager.getRoarCoolMax();
        int roarLength = 80;
        roarLength *= (RoarCool / (double)RoarCoolTotal);
        int height = screenHeight + (MainConfig.SoulGuiVertical.get() - 20);
        guiGraphics.blit(Goety.location("textures/gui/ravager_roar_bar.png"), i, height - 9, 0, 0, 96,16, 96, 32);
        guiGraphics.blit(Goety.location("textures/gui/ravager_roar_bar.png"), i + 16, height - 9, 16, 16, roarLength,16, 96, 32);
    }
}
