package com.Polarice3.Goety.client.gui.radial;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Quaternionf;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EntityRadialMenuItem extends RadialMenuItem {
    public static final float SLICE_SIZE = 24.0F;
    public static final float SPIN_DEGREES_PER_TICK = 3.0F;
    public static final float TILT_DEGREES = 10.0F;

    private final String key;
    private final CompoundTag servantTag;
    @Nullable
    private LivingEntity preview;
    private boolean triedPreview;
    private boolean next;

    public EntityRadialMenuItem(GenericRadialMenu owner, String key, CompoundTag servantTag) {
        super(owner);
        this.key = key;
        this.servantTag = servantTag;
    }

    public String getKey() {
        return this.key;
    }

    public void setNext(boolean next) {
        this.next = next;
    }

    @Nullable
    public LivingEntity getPreview() {
        if (!this.triedPreview) {
            this.triedPreview = true;
            this.preview = createPreview(this.servantTag);
        }
        return this.preview;
    }

    public Component getName() {
        String json = this.servantTag.getString("CustomName");
        if (!json.isEmpty()) {
            try {
                Component name = Component.Serializer.fromJson(json);
                if (name != null) {
                    return name;
                }
            } catch (Exception ignored) {
            }
        }
        EntityType<?> type = getType(this.servantTag);
        return type != null ? type.getDescription() : Component.translatable("info.goety.servant");
    }

    @Override
    public void draw(DrawingContext context) {
        LivingEntity entity = this.getPreview();
        if (entity != null) {
            renderSpinning(context.guiGraphics(), entity, context.x(), context.y(), SLICE_SIZE * (this.isHovered() ? 1.15F : 1.0F));
        } else {
            context.guiGraphics().drawCenteredString(context.fontRenderer(), this.getName(), (int) context.x(), (int) context.y() - 4, 0xffffff);
        }
        if (this.next) {
            context.guiGraphics().pose().pushPose();
            context.guiGraphics().pose().translate(0.0F, 0.0F, 300.0F);
            context.guiGraphics().drawCenteredString(context.fontRenderer(), "▲", (int) context.x(), (int) (context.y() + SLICE_SIZE * 0.5F), 16777045);
            context.guiGraphics().pose().popPose();
        }
    }

    @Override
    public void drawTooltips(DrawingContext context) {
        List<Component> lines = new ArrayList<>();
        lines.add(this.getName());
        if (this.servantTag.contains("Health")) {
            lines.add(Component.translatable("info.goety.tesseract.health", String.format("%.1f", this.servantTag.getFloat("Health"))).withStyle(ChatFormatting.GRAY));
        }
        lines.add(this.next
                ? Component.translatable("info.goety.tesseract.next").withStyle(ChatFormatting.GOLD)
                : Component.translatable("info.goety.tesseract.select").withStyle(ChatFormatting.DARK_PURPLE));
        context.guiGraphics().renderComponentTooltip(context.fontRenderer(), lines, (int) context.x(), (int) context.y());
    }

    @Nullable
    public static EntityType<?> getType(CompoundTag servantTag) {
        ResourceLocation id = ResourceLocation.tryParse(servantTag.getString("ServantType"));
        return id != null && ForgeRegistries.ENTITY_TYPES.containsKey(id) ? ForgeRegistries.ENTITY_TYPES.getValue(id) : null;
    }

    @Nullable
    public static LivingEntity createPreview(CompoundTag servantTag) {
        Minecraft minecraft = Minecraft.getInstance();
        EntityType<?> type = getType(servantTag);
        if (minecraft.level == null || type == null) {
            return null;
        }
        Entity entity;
        try {
            entity = type.create(minecraft.level);
        } catch (Throwable throwable) {
            return null;
        }
        if (!(entity instanceof LivingEntity living)) {
            return null;
        }
        try {
            living.readAdditionalSaveData(servantTag.copy());
        } catch (Throwable ignored) {
        }
        return living;
    }

    public static void renderSpinning(GuiGraphics guiGraphics, LivingEntity entity, float x, float y, float size) {
        Minecraft minecraft = Minecraft.getInstance();
        float largest = Math.max(entity.getBbWidth(), entity.getBbHeight());
        int scale = Math.max(1, Mth.floor(size / Math.max(largest, 0.25F)));
        float ticks = (minecraft.player != null ? minecraft.player.tickCount : 0) + minecraft.getFrameTime();
        float yaw = 180.0F + ticks * SPIN_DEGREES_PER_TICK;

        float bodyRot = entity.yBodyRot, bodyRotO = entity.yBodyRotO;
        float yRot = entity.getYRot(), yRotO = entity.yRotO;
        float xRot = entity.getXRot(), xRotO = entity.xRotO;
        float headRot = entity.yHeadRot, headRotO = entity.yHeadRotO;

        entity.yBodyRot = entity.yBodyRotO = yaw;
        entity.setYRot(yaw);
        entity.yRotO = yaw;
        entity.setXRot(0.0F);
        entity.xRotO = 0.0F;
        entity.yHeadRot = entity.yHeadRotO = yaw;

        Quaternionf tilt = new Quaternionf().rotateX(TILT_DEGREES * Mth.DEG_TO_RAD);
        Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI).mul(tilt);
        int feetY = Mth.floor(y + entity.getBbHeight() * scale * 0.5F);
        InventoryScreen.renderEntityInInventory(guiGraphics, Mth.floor(x), feetY, scale, pose, new Quaternionf(tilt), entity);

        entity.yBodyRot = bodyRot;
        entity.yBodyRotO = bodyRotO;
        entity.setYRot(yRot);
        entity.yRotO = yRotO;
        entity.setXRot(xRot);
        entity.xRotO = xRotO;
        entity.yHeadRot = headRot;
        entity.yHeadRotO = headRotO;
    }
}