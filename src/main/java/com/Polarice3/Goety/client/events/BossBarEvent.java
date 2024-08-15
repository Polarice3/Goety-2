package com.Polarice3.Goety.client.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.config.MainConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarEvent {

    protected static final ResourceLocation TEXTURE = new ResourceLocation(Goety.MOD_ID, "textures/gui/boss_bar.png");
    protected static final ResourceLocation BOSS_BAR_1 = new ResourceLocation(Goety.MOD_ID, "textures/gui/boss_bar_1.png");
    protected static final ResourceLocation MINI_BOSS_BAR = new ResourceLocation(Goety.MOD_ID, "textures/gui/miniboss_bar.png");
    public static Map<UUID, Mob> BOSS_BARS = new HashMap<>();

    @SubscribeEvent
    public static void renderBossBar(CustomizeGuiOverlayEvent.BossEventProgress event){
        Minecraft minecraft = Minecraft.getInstance();
        if (MainConfig.SpecialBossBar.get()) {
            int i = minecraft.getWindow().getGuiScaledWidth();
            if (BOSS_BARS.containsKey(event.getBossEvent().getId())) {
                Mob boss = BOSS_BARS.get(event.getBossEvent().getId());
                event.setCanceled(true);
                int k = i / 2 - 100;
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                drawBar(event.getGuiGraphics(), k, event.getY(), event.getPartialTick(), boss);
                Component itextcomponent = boss.getDisplayName();
                int l = minecraft.font.width(itextcomponent);
                int i1 = i / 2 - l / 2;
                event.getGuiGraphics().drawString(minecraft.font, itextcomponent, i1, event.getY() - 9, 16777215);
                if (event.getY() >= minecraft.getWindow().getGuiScaledHeight() / 3) {
                    return;
                }
                event.setIncrement(12 + minecraft.font.lineHeight);
            }
        }

    }

    private static void drawBar(GuiGraphics guiGraphics, int pX, int pY, float partialTicks, Mob pEntity) {
        float percent = pEntity.getHealth() / pEntity.getMaxHealth();
        int i = (int) (percent * 182.0F);
        int pX2 = pX + 9;
        int pY2 = pY + 4;
        int offset = (int) ((pEntity.tickCount + partialTicks) % 364);
        if (percent <= 0.25F){
            offset = (int) (((pEntity.tickCount + partialTicks) * 4) % 364);
        } else if (percent <= 0.5F){
            offset = (int) (((pEntity.tickCount + partialTicks) * 2) % 364);
        }
        if (pEntity instanceof Apostle apostleEntity) {
            boolean flag = apostleEntity.isSecondPhase();
            int shake = 0;
            int damage = 36;
            if (i > 0) {
                guiGraphics.blit(BOSS_BAR_1, pX2, pY2, offset, 0, i, 8, 364, 64);
                if (pEntity.hurtTime >= 5) {
                    damage = 32 + pEntity.getRandom().nextInt(pEntity.hurtTime);
                    shake = pEntity.getRandom().nextInt(pEntity.hurtTime);
                    RenderSystem.setShaderTexture(0, TEXTURE);
                    guiGraphics.blit(TEXTURE, pX2, pY2, shake, damage, i, 8, 256, 256);
                }
                if (apostleEntity.isSmited()){
                    float smite = 1.0F - ((float) apostleEntity.getAntiRegen() / apostleEntity.getAntiRegenTotal());
                    guiGraphics.blit(BOSS_BAR_1, pX2, pY2, offset, 16, i, 8, 364, 64);
                    guiGraphics.blit(BOSS_BAR_1, pX2, pY2, offset, 0, (int)(smite * i), 8, 364, 64);
                }
            }
            guiGraphics.blit(TEXTURE, pX, pY, 0, flag ? 16 : 0, 200, 16, 256, 256);
        } else if (pEntity instanceof Vizier) {
            int shake = 0;
            int damage = 100;
            if (i > 0) {
                guiGraphics.blit(BOSS_BAR_1, pX2, pY2, offset, 8, i, 8, 364, 64);
                if (pEntity.hurtTime >= 5) {
                    damage = 64 + pEntity.getRandom().nextInt(pEntity.hurtTime);
                    shake = pEntity.getRandom().nextInt(pEntity.hurtTime);
                    guiGraphics.blit(TEXTURE, pX2, pY2, shake, damage, i, 8, 256, 256);
                }
            }
            RenderSystem.setShaderTexture(0, TEXTURE);
            guiGraphics.blit(TEXTURE, pX, pY, 0, 48, 200, 16, 256, 256);
        } else {
            drawMiniBossBar(guiGraphics, pX, pY, pEntity);
        }
    }

    private static void drawMiniBossBar(GuiGraphics guiGraphics, int pX, int pY, Mob pEntity) {
        float percent = pEntity.getHealth() / pEntity.getMaxHealth();
        int i = (int) (percent * 128.0F);
        int j = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int j1 = j / 2 - 62;
        if (i > 0) {
            if (pEntity.isInvulnerable() && !pEntity.isInvisible()){
                guiGraphics.blit(MINI_BOSS_BAR, j1, pY, 0, 24, i, 8, 128, 128);
            } else {
                guiGraphics.blit(MINI_BOSS_BAR, j1, pY, 0, 8, i, 8, 128, 128);
            }
        }
        guiGraphics.blit(MINI_BOSS_BAR, j1 - 11, pY, 0, 16, 9, 8, 128, 128);
        guiGraphics.blit(MINI_BOSS_BAR, j1, pY, 0, 0, 128, 8, 128, 128);
    }

    public static void addBossBar(UUID id, Mob mob){
        BOSS_BARS.put(id, mob);
    }

    public static void removeBossBar(UUID id, Mob mob){
        BOSS_BARS.remove(id, mob);
    }

}
