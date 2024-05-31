package com.Polarice3.Goety.common.items.research;

import com.Polarice3.Goety.common.research.Research;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public abstract class ResearchScroll extends Item {
    public Research research;

    public ResearchScroll(Properties properties, Research research){
        super(properties);
        this.research = research;
    }

    public ResearchScroll(Research research){
        this(scrollProperties(),
                research
        );
    }

    public static Item.Properties scrollProperties(){
        return new Properties()
                .rarity(Rarity.EPIC)
                .setNoRepair()
                .stacksTo(1);
    }

    public static Item.Properties fireResistant(){
        return new Properties()
                .rarity(Rarity.EPIC)
                .setNoRepair()
                .fireResistant()
                .stacksTo(1);
    }

    public abstract Component researchGet();

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide){
            if (!SEHelper.hasResearch(playerIn, this.research)) {
                if (SEHelper.addResearch(playerIn, this.research)) {
                    CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) playerIn, itemstack);
                    if (researchGet() != null) {
                        playerIn.displayClientMessage(researchGet(), true);
                    }
                    itemstack.shrink(1);
                    return InteractionResultHolder.consume(playerIn.getItemInHand(handIn));
                }
            } else {
                playerIn.displayClientMessage(Component.translatable("info.goety.research.already"), true);
            }
        }
        return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
    }
}
