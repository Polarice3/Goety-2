package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.common.magic.spells.utility.CommandSpell;
import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class CommandFocus extends MagicFocus{
    public static final String TAG_ENTITY = "Servant";

    public CommandFocus() {
        super(new CommandSpell());
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() || player.isCrouching()){
            if (itemstack.getItem() instanceof CommandFocus){
                if (hasServant(itemstack) && itemstack.getTag() != null){
                    itemstack.getTag().remove(TAG_ENTITY);
                }
            }
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }
        return InteractionResultHolder.pass(itemstack);
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return hasServant(p_41453_);
    }

    public static boolean hasServant(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null && getServant(compoundtag) != null;
    }

    public static void setServant(CompoundTag compoundTag, LivingEntity livingEntity){
        if (compoundTag != null) {
            if (livingEntity != null) {
                compoundTag.putUUID(TAG_ENTITY, livingEntity.getUUID());
            }
        }
    }

    public static LivingEntity getServant(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        if (compoundtag != null) {
            return getServant(compoundtag);
        } else {
            return null;
        }
    }

    public static LivingEntity getServant(CompoundTag compoundTag){
        boolean flag = compoundTag.contains(TAG_ENTITY);
        if (flag){
            UUID uuid = compoundTag.getUUID(TAG_ENTITY);
            return EntityFinder.getLivingEntityByUuiD(uuid);
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        addCommandText(stack, tooltip);
    }

    public static void addCommandText(ItemStack stack, List<Component> tooltip){
        if (stack.getTag() != null) {
            if (!hasServant(stack)) {
                tooltip.add(Component.translatable("info.goety.focus.noServant"));
                tooltip.add(Component.translatable("info.goety.focus.noCreative"));
            } else {
                LivingEntity livingEntity = getServant(stack.getTag());
                if (livingEntity != null){
                    tooltip.add(Component.translatable("info.goety.focus.servant").append(" ")
                            .append(livingEntity.getCustomName() != null ? livingEntity.getCustomName() : livingEntity.getDisplayName())
                            .withStyle(ChatFormatting.GREEN));
                }
            }
        }
    }
}
