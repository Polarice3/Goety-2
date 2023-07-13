package com.Polarice3.Goety.common.items.research;

import com.Polarice3.Goety.common.research.ResearchList;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ReplicationScroll extends ResearchScroll {
    public ReplicationScroll(){
        super(ResearchList.REPLICATION);
    }

    @Override
    public Component researchGet() {
        return Component.translatable("info.goety.research.replication");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("info.goety.items.replication_scroll").withStyle(ChatFormatting.GOLD));
    }
}
