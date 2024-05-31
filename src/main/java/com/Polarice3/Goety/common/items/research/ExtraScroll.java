package com.Polarice3.Goety.common.items.research;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.research.Research;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ExtraScroll extends ResearchScroll{
    public Research requirement;

    public ExtraScroll(Properties properties, Research research, Research requirement) {
        super(properties, research);
        this.requirement = requirement;
    }

    public ExtraScroll(Research research, Research requirement) {
        this(scrollProperties(), research, requirement);
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide){
            if (!SEHelper.hasResearch(playerIn, this.research)) {
                if (SEHelper.hasResearch(playerIn, this.requirement)) {
                    if (SEHelper.addResearch(playerIn, this.research)) {
                        CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) playerIn, itemstack);
                        if (researchGet() != null) {
                            playerIn.displayClientMessage(researchGet(), true);
                        }
                        itemstack.shrink(1);
                        return InteractionResultHolder.consume(playerIn.getItemInHand(handIn));
                    }
                } else {
                    playerIn.displayClientMessage(Component.translatable("info.goety.research.extra", this.requirement.getId()), true);
                }
            } else {
                playerIn.displayClientMessage(Component.translatable("info.goety.research.already"), true);
            }
        }
        return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
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
            } else if (SEHelper.hasResearch(Goety.PROXY.getPlayer(), this.requirement)) {
                tooltip.add(Component.translatable("info.goety.items.scroll").withStyle(ChatFormatting.AQUA));
            } else {
                tooltip.add(Component.translatable("info.goety.research.extra", this.requirement.getId()).withStyle(ChatFormatting.RED));
            }
        }
    }
}
