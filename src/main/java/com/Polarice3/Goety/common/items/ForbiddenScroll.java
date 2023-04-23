package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.ConstantPaths;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ForbiddenScroll extends Item {
    public ForbiddenScroll(){
        super(new Properties()
                .tab(Goety.TAB)
                .rarity(Rarity.EPIC)
                .setNoRepair()
                .stacksTo(1)
                .fireResistant()
        );
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide){
            CompoundTag playerData = playerIn.getPersistentData();
            CompoundTag data;

            if (!playerData.contains(Player.PERSISTED_NBT_TAG)) {
                data = new CompoundTag();
            } else {
                data = playerData.getCompound(Player.PERSISTED_NBT_TAG);
            }

            if (!data.getBoolean(ConstantPaths.readScroll())) {
                data.putBoolean(ConstantPaths.readScroll(), true);
                playerData.put(Player.PERSISTED_NBT_TAG, data);
                CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) playerIn, itemstack);
                playerIn.displayClientMessage(Component.translatable("info.goety.research.lich"), true);
                itemstack.shrink(1);
                return InteractionResultHolder.consume(playerIn.getItemInHand(handIn));
            } else {
                return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
            }
        } else {
            return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("info.goety.items.forbidden_scroll").withStyle(ChatFormatting.DARK_PURPLE));
    }
}
