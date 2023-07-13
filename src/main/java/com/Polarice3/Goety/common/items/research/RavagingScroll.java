package com.Polarice3.Goety.common.items.research;

import com.Polarice3.Goety.common.research.ResearchList;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class RavagingScroll extends ResearchScroll {
    public RavagingScroll(){
        super(ResearchList.RAVAGING);
    }

    @Override
    public Component researchGet() {
        return Component.translatable("info.goety.research.ravager");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("info.goety.items.ravaging_scroll").withStyle(ChatFormatting.GOLD));
    }
}
