package com.Polarice3.Goety.common.items.revive;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ReviveServantItem extends Item {

    public ReviveServantItem(Properties p_41383_) {
        super(p_41383_);
    }

    public static void setSummon(Entity entity, ItemStack stack) {
        entity.stopRiding();
        entity.ejectPassengers();

        CompoundTag entityTag = new CompoundTag();
        ResourceLocation typesKey = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());

        if (typesKey != null) {
            entityTag.putString("entity", typesKey.toString());
            if (entity.hasCustomName()) {
                entityTag.putString("name", Objects.requireNonNull(entity.getCustomName()).getString());
            }
            entity.save(entityTag);
            CompoundTag itemNBT = stack.getOrCreateTag();
            itemNBT.put("entity", entityTag);
        }
    }

    public static Entity getSummon(ItemStack stack, Level level) {
        CompoundTag itemTag = stack.getTag();

        if (itemTag != null) {
            CompoundTag entityTag = itemTag.getCompound("entity");
            EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entityTag.getString("entity")));
            if (entityType != null) {
                Entity entity = entityType.create(level);
                if (level instanceof ServerLevel && entity != null) {
                    entity.load(entityTag);
                }

                return entity;
            }
        }

        return null;
    }

    public static void setOwnerName(@Nullable LivingEntity entity, ItemStack stack) {
        CompoundTag entityTag = stack.getOrCreateTag();
        if (entity != null) {
            entityTag.putString("owner_name", entity.getDisplayName().getString());
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (level != null && getSummon(stack, level) != null)  {
            Entity entity = getSummon(stack, level);

            if (entity == null) {
                return;
            }

            if (stack.getTag() != null) {
                if (stack.getTag().contains("owner_name")) {
                    tooltip.add(Component.translatable("tooltip.goety.arcaPlayer").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("" + stack.getTag().getString("owner_name")).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
                }
            }

            if (entity.getCustomName() != null) {
                tooltip.add(Component.translatable("tooltip.goety.customName").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("").append(entity.getCustomName()).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
            }
        }
    }
}
