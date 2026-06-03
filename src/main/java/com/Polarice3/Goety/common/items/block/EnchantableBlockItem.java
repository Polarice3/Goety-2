package com.Polarice3.Goety.common.items.block;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.ThroneBlock;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.config.MainConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class EnchantableBlockItem extends BlockItemBase {

    public EnchantableBlockItem(Block blockIn) {
        super(blockIn);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (stack.getItem() == ModBlocks.SCULK_DEVOURER.get().asItem()){
            return stack.getCount() == 1
                    && (enchantment == ModEnchantments.SOUL_EATER.get()
                    || enchantment == ModEnchantments.RADIUS.get());
        }
        if (stack.getItem() == ModBlocks.SCULK_CONVERTER.get().asItem()){
            return stack.getCount() == 1 && enchantment == ModEnchantments.POTENCY.get();
        }
        if (stack.getItem() == ModBlocks.SCULK_GROWER.get().asItem()){
            if (MainConfig.SculkGrowerPotency.get()){
                return stack.getCount() == 1 && (enchantment == ModEnchantments.POTENCY.get() || enchantment == ModEnchantments.RADIUS.get());
            } else {
                return stack.getCount() == 1 && enchantment == ModEnchantments.RADIUS.get();
            }
        }
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ThroneBlock) {
            return stack.getCount() == 1 && enchantment == ModEnchantments.ROYALTY.get();
        }
        return stack.getCount() == 1;
    }

    public int getMaxStackSize(ItemStack itemStack){
        return itemStack.isEnchanted() ? 1 : super.getMaxStackSize(itemStack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 25;
    }

    public static void setOwner(@Nullable LivingEntity entity, ItemStack stack) {
        CompoundTag entityTag = stack.getOrCreateTag();
        if (entity != null) {
            entityTag.putUUID("owner", entity.getUUID());
            entityTag.putString("owner_name", entity.getDisplayName().getString());
        }
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
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
