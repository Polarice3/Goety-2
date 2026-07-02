package com.Polarice3.Goety.client.gui.overlay;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.curios.OminousCharmItem;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class OminousCharmGui {
    public static final IGuiOverlay OVERLAY = OminousCharmGui::drawOverlay;
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static Font getFont() {
        return minecraft.font;
    }

    public static void drawOverlay(ForgeGui gui, GuiGraphics ms, float partialTicks, int screenWidth, int screenHeight) {
        if (!MainConfig.OCGuiShow.get()) {
            return;
        }

        if (minecraft.player != null){
            Player player = minecraft.player;
            ItemStack itemStack = CuriosFinder.findCurio(player, ModItems.OMINOUS_CHARM.get());

            if (!itemStack.isEmpty()) {
                int amount = OminousCharmItem.getOmenAmount(itemStack);
                if (amount > 0) {
                    ms.pose().pushPose();
                    Component component = Component.translatable("info.goety.ominous_charm.level").withStyle(ChatFormatting.DARK_AQUA).append(Component.literal(" " + amount));
                    ms.drawString(getFont(), component, 25 + MainConfig.OCGuiHorizontal.get(), 10 + MainConfig.OCGuiVertical.get(), 16777215);
                    ms.renderFakeItem(itemStack, 5 + MainConfig.OCGuiHorizontal.get(), 5 + MainConfig.OCGuiVertical.get());
                    ms.pose().popPose();
                }
            }
        }
    }
}
