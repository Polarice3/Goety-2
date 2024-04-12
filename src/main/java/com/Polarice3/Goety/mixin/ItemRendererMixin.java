package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.utils.SEHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Based from @Iron431's codes "<a href="https://github.com/iron431/Irons-Spells-n-Spellbooks/blob/1.19.2/src/main/java/io/redspace/ironsspellbooks/mixin/ItemRendererMixin.java">...</a>".
 */
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Shadow
    private void fillRect(BufferBuilder p_115153_, int p_115154_, int p_115155_, int p_115156_, int p_115157_, int p_115158_, int p_115159_, int p_115160_, int p_115161_) {
    }

    @Inject(method = "renderGuiItemDecorations*", at = @At(value = "TAIL"))
    public void renderFocusCooldown(Font font, ItemStack stack, int one, int two, CallbackInfo ci) {
        Item item = stack.getItem();
        LocalPlayer localplayer = Minecraft.getInstance().player;
        if (localplayer != null) {
            if (item instanceof IWand && MainConfig.ShowWandCooldown.get()) {
                float f;
                if (IWand.getFocus(stack) != null && SEHelper.getFocusCoolDown(localplayer).isOnCooldown(IWand.getFocus(stack).getItem())) {
                    Item focus = IWand.getFocus(stack).getItem();
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
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            fillRect(bufferbuilder, one, two + Mth.floor(16.0F * (1.0F - f)), 16, Mth.ceil(16.0F * f), 255, 255, 255, 127);
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
    }
}
