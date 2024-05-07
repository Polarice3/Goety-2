package com.Polarice3.Goety.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBase extends Item {
    public ItemBase(){
        super(new Properties());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        ChatFormatting main = ChatFormatting.DARK_PURPLE;
        ChatFormatting secondary = ChatFormatting.BLUE;

        if (stack.getItem() instanceof ItemBase) {
            if (stack.is(ModItems.OMINOUS_SADDLE.get())) {
                tooltip.add(Component.translatable("info.goety.ominous_saddle").withStyle(main));
            }
        }
    }
}
