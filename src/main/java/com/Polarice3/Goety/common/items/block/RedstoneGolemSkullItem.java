package com.Polarice3.Goety.common.items.block;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class RedstoneGolemSkullItem extends StandingAndWallBlockItem {
    
    public RedstoneGolemSkullItem(Properties p_43250_) {
        super(ModBlocks.REDSTONE_GOLEM_SKULL_BLOCK.get(), ModBlocks.WALL_REDSTONE_GOLEM_SKULL_BLOCK.get(), p_43250_, Direction.DOWN);
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return getOwnerID(p_41453_) != null;
    }

    public static void setOwner(@Nullable LivingEntity entity, ItemStack stack) {
        CompoundTag entityTag = stack.getOrCreateTag();
        if (entity != null) {
            entityTag.putUUID("owner", entity.getUUID());
            entityTag.putString("owner_name", entity.getDisplayName().getString());
        }
    }

    public static void setCustomName(String string, ItemStack stack){
        CompoundTag entityTag = stack.getOrCreateTag();
        entityTag.putString("mod_custom_name", string);
    }

    public static String getCustomName(ItemStack stack){
        CompoundTag entityTag = stack.getTag();
        if (entityTag != null){
            if (entityTag.contains("mod_custom_name")) {
                return entityTag.getString("mod_custom_name");
            }
        }
        return null;
    }

    @Nullable
    public static UUID getOwnerID(ItemStack stack){
        CompoundTag entityTag = stack.getTag();
        if (entityTag != null){
            if (entityTag.contains("owner")) {
                return entityTag.getUUID("owner");
            }
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.getTag() != null) {
            if (stack.getTag().contains("owner_name")) {
                tooltip.add(Component.translatable("tooltip.goety.arcaPlayer").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("" + stack.getTag().getString("owner_name")).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
            }
            if (stack.getTag().contains("mod_custom_name")){
                tooltip.add(Component.translatable("tooltip.goety.customName").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("" + stack.getTag().getString("mod_custom_name")).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
            }
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
