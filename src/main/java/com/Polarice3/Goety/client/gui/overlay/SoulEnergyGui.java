package com.Polarice3.Goety.client.gui.overlay;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.items.magic.ITotem;
import com.Polarice3.Goety.api.magic.IChargingSpell;
import com.Polarice3.Goety.common.items.magic.FullSpentTotem;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.TotemFinder;
import com.Polarice3.Goety.utils.WandUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class SoulEnergyGui {
    public static final IGuiOverlay OVERLAY = SoulEnergyGui::drawHUD;
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static boolean shouldDisplayBar(){
        return SEHelper.getSoulsContainer(minecraft.player) && MainConfig.SoulGuiShow.get() && (minecraft.gameMode != null && minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR);
    }

    public static Font getFont() {
        return minecraft.font;
    }

    public static void drawHUD(ForgeGui gui, GuiGraphics guiGraphics, float partialTicks, int screenWidth, int screenHeight) {
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
                SoulEnergy = ITotem.currentSouls(stack);
                if (stack.getTag().contains(ITotem.MAX_SOUL_AMOUNT)) {
                    SoulEnergyTotal = ITotem.maximumSouls(stack);
                }
            }
        }

        int i = (screenWidth/2) + (MainConfig.SoulGuiHorizontal.get());
        int energylength = (int)(117 * (SoulEnergy / (double)SoulEnergyTotal));
        int maxenergy = (int) (117 * (MainConfig.MaxSouls.get() / (double)SoulEnergyTotal));

        int height = screenHeight + (MainConfig.SoulGuiVertical.get());

        int offset = (int) ((minecraft.player.tickCount + partialTicks) % 234);

        if (SEHelper.getSEActive(minecraft.player)){
            guiGraphics.blit(Goety.location("textures/gui/soul_energy.png"), i, height - 9, 0, 9, 128, 9, 128, 90);
            guiGraphics.blit(Goety.location("textures/gui/soul_energy.png"), i + 9, height - 9, 9, 18, maxenergy, 9, 128, 90);
        } else {
            int height1 = stack.getItem() instanceof FullSpentTotem ? 36 : 0;
            guiGraphics.blit(Goety.location("textures/gui/soul_energy.png"), i, height - 9, 0, height1, 128, 9, 128, 90);
        }
        RenderSystem.setShaderTexture(0, new ResourceLocation(Goety.MOD_ID, "textures/gui/soul_energy_bar.png"));
        guiGraphics.blit(Goety.location("textures/gui/soul_energy_bar.png"), i + 9, height - 7, offset, 0, energylength, 5, 234, 5);

        if (MobUtil.isSpellCasting(minecraft.player)){
            int useDuration = minecraft.player.getUseItem().getUseDuration();
            float remain = minecraft.player.getUseItemRemainingTicks();
            float useTime0 = (useDuration - remain) / useDuration;
            if (WandUtil.getSpell(minecraft.player) instanceof IChargingSpell spell && spell.defaultCastUp() > 0){
                useDuration = spell.defaultCastUp();
                remain = Math.min(minecraft.player.getUseItem().getUseDuration() - minecraft.player.getUseItemRemainingTicks(), spell.defaultCastUp());
                useTime0 = remain / useDuration;
            }
            int useTime = (int) (117 * useTime0);
            guiGraphics.blit(Goety.location("textures/gui/soul_energy.png"), i + 9, height - 9, 9, 27, useTime, 9, 128, 90);
        }

        if (MainConfig.ShowNum.get()) {
            minecraft.getProfiler().push("soulenergy");
            String s = SoulEnergy + "/" + SoulEnergyTotal;
            int i1 = i + 37;
            int j1 = height - 8;
            guiGraphics.drawString(getFont(), s, i1, j1, 16777215);
            minecraft.getProfiler().pop();
        }
    }
}
