package com.Polarice3.Goety.client.gui.overlay;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.items.magic.TotemOfSouls;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.TotemFinder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class SoulEnergyGui extends GuiComponent {
    public static final IGuiOverlay OVERLAY = SoulEnergyGui::drawHUD;
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static boolean shouldDisplayBar(){
        return SEHelper.getSoulsContainer(minecraft.player) && MainConfig.SoulGuiShow.get();
    }

    public static Font getFont() {
        return minecraft.font;
    }

    public static void drawHUD(ForgeGui gui, PoseStack ms, float partialTicks, int screenWidth, int screenHeight) {
        if(!shouldDisplayBar()) {
            return;
        }

        ItemStack stack = TotemFinder.FindTotem(minecraft.player);
        int SoulEnergy = 0;
        int SoulEnergyTotal = MainConfig.MaxSouls.get();
        if (SEHelper.getSEActive(minecraft.player)){
            SoulEnergy = SEHelper.getSESouls(minecraft.player);
            SoulEnergyTotal = MainConfig.MaxArcaSouls.get();
        } else if (!stack.isEmpty()) {
            if (stack.getTag() != null) {
                SoulEnergy = TotemOfSouls.currentSouls(stack);
            }
        }

        int i = (screenWidth/2) + (MainConfig.SoulGuiHorizontal.get());
        int energylength = 117;
        energylength = (int)((energylength) * (SoulEnergy / (double)SoulEnergyTotal));
        int maxenergy = (int)(117 * (MainConfig.MaxSouls.get() / (double)SoulEnergyTotal));

        int height = screenHeight + (MainConfig.SoulGuiVertical.get());

        int offset = (int) ((minecraft.player.tickCount + partialTicks) % 234);

        if (SEHelper.getSEActive(minecraft.player)){
            RenderSystem.setShaderTexture(0, new ResourceLocation(Goety.MOD_ID, "textures/gui/soulenergyborder2.png"));
            blit(ms,i, height - 9, 0, 0, 128,9, 128, 9);
            RenderSystem.setShaderTexture(0, new ResourceLocation(Goety.MOD_ID, "textures/gui/soulenergy_revive.png"));
            blit(ms,i + 9, height - 7, 0, 0, maxenergy,5, 117, 5);
        } else {
            RenderSystem.setShaderTexture(0, new ResourceLocation(Goety.MOD_ID, "textures/gui/soulenergyborder.png"));
            blit(ms,i, height - 9, 0, 0, 128,9, 128, 9);
        }
        RenderSystem.setShaderTexture(0, new ResourceLocation(Goety.MOD_ID, "textures/gui/soulenergy.png"));
        blit(ms,i + 9, height - 7, offset, 0, energylength,5, 234, 5);
        if (MainConfig.ShowNum.get()) {
            minecraft.getProfiler().push("soulenergy");
            String s = "" + SoulEnergy + "/" + "" + SoulEnergyTotal;
            int i1 = i + 36;
            int j1 = height - 8;
            getFont().draw(ms, s, (float) i1, (float) j1, 16777215);
            minecraft.getProfiler().pop();
        }
    }
}
