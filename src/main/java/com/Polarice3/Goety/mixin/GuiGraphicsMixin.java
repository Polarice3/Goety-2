package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.items.magic.DarkWand;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Based from @Iron431's codes "<a href="https://github.com/iron431/Irons-Spells-n-Spellbooks/blob/1.20.1/src/main/java/io/redspace/ironsspellbooks/mixin/ItemRendererMixin.java">...</a>".
 */
@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value = "HEAD"))
    public void renderFocusCooldown(Font font, ItemStack stack, int one, int two, CallbackInfo ci) {
        Item item = stack.getItem();
        LocalPlayer localplayer = Minecraft.getInstance().player;
        if (localplayer != null) {
            if (item instanceof DarkWand && MainConfig.ShowWandCooldown.get()) {
                float f;
                if (DarkWand.getFocus(stack) != null && SEHelper.getFocusCoolDown(localplayer).isOnCooldown(DarkWand.getFocus(stack).getItem())) {
                    Item focus = DarkWand.getFocus(stack).getItem();
                    f = SEHelper.getFocusCoolDown(localplayer).getCooldownPercent(focus);
                } else {
                    f = 0;
                }
                renderFocusCooldown(one, two, f);
            } else {
                float f;
                if (SEHelper.getFocusCoolDown(localplayer).isOnCooldown(item)) {
                    f = SEHelper.getFocusCoolDown(localplayer).getCooldownPercent(item);
                } else {
                    f = 0;
                }
                renderFocusCooldown(one, two, f);
            }
        }
    }


    private void renderFocusCooldown(int one, int two, float f) {
        if (f > 0.0F) {
            GuiGraphics self = (GuiGraphics) (Object) this;
            int i1 = two + Mth.floor(16.0F * (1.0F - f));
            int j1 = i1 + Mth.ceil(16.0F * f);
            self.fill(RenderType.guiOverlay(), one, i1, one + 16, j1, Integer.MAX_VALUE);
        }
    }
}
