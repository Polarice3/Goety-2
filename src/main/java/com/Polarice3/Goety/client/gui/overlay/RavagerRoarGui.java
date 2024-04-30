package com.Polarice3.Goety.client.gui.overlay;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.ModRavager;
import com.Polarice3.Goety.config.MainConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class RavagerRoarGui extends GuiComponent {
    public static final IGuiOverlay OVERLAY = RavagerRoarGui::drawHUD;
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static boolean shouldDisplayBar(){
        return minecraft.player != null && minecraft.player.getVehicle() instanceof ModRavager ravager && ravager.getRoarCool() > 0;
    }

    public static void drawHUD(ForgeGui gui, PoseStack ms, float partialTicks, int screenWidth, int screenHeight) {
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
        RenderSystem.setShaderTexture(0, new ResourceLocation(Goety.MOD_ID, "textures/gui/ravager_roar_bar.png"));
        blit(ms, i, height - 9, 0, 0, 96,16, 96, 32);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Goety.MOD_ID, "textures/gui/ravager_roar_bar.png"));
        blit(ms, i + 16, height - 9, 16, 16, roarLength,16, 96, 32);
    }
}
