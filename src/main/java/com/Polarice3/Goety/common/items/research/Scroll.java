package com.Polarice3.Goety.common.items.research;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.research.Research;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class Scroll extends ResearchScroll{
    public Scroll(Properties properties, Research research) {
        super(properties, research);
    }

    public Scroll(Research research) {
        super(research);
    }

    @Override
    public Component researchGet() {
        return Component.translatable("info.goety.research." + research.getId());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("info.goety.items." + research.getId()).withStyle(ChatFormatting.GOLD));
        if (worldIn != null && worldIn.isClientSide){
            if (SEHelper.hasResearch(Goety.PROXY.getPlayer(), this.research)){
                tooltip.add(Component.translatable("info.goety.research.learned").withStyle(ChatFormatting.BLUE));
            } else {
                tooltip.add(Component.translatable("info.goety.items.scroll").withStyle(ChatFormatting.AQUA));
            }
        }
    }
}
