package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class PendantOfHungerItem extends SingleStackItem {
    private static final String ROTTEN_FLESH = "Rotten Flesh Count";

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof Player player) {
            if (!stack.hasTag()) {
                stack.setTag(new CompoundTag());
                stack.getOrCreateTag().putInt(ROTTEN_FLESH, 0);
            } else if (CuriosFinder.hasCurio(player, itemStack -> itemStack == stack)){
                if (getRottenFleshAmount(stack) < MainConfig.PendantOfHungerLimit.get()) {
                    if (!ItemHelper.findItem(player, Items.ROTTEN_FLESH).isEmpty()) {
                        increaseRottenFlesh(stack);
                        ItemHelper.findItem(player, Items.ROTTEN_FLESH).shrink(1);
                    }
                }
                if (getRottenFleshAmount(stack) > 0) {
                    if (MobUtil.playerValidity(player, true)) {
                        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 0, false, false));
                        if (player.getFoodData().needsFood()) {
                            player.eat(player.level, new ItemStack(Items.ROTTEN_FLESH));
                            decreaseRottenFlesh(stack);
                        }
                    }
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        CompoundTag compound = pStack.getOrCreateTag();
        compound.putInt(ROTTEN_FLESH, 0);
    }

    public void increaseRottenFlesh(ItemStack stack){
        if (stack.getTag() != null) {
            stack.getOrCreateTag().putInt(ROTTEN_FLESH, getRottenFleshAmount(stack) + 1);
        }
    }

    public void decreaseRottenFlesh(ItemStack stack){
        if (stack.getTag() != null) {
            stack.getOrCreateTag().putInt(ROTTEN_FLESH, getRottenFleshAmount(stack) - 1);
        }
    }

    public int getRottenFleshAmount(ItemStack stack) {
        if (stack.getTag() != null) {
            return stack.getOrCreateTag().getInt(ROTTEN_FLESH);
        } else {
            return 0;
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    public int getBarColor(ItemStack stack) {
        float f = Math.max(0.0F, (float) (1.0F - amountColor(stack))/2.0F);
        return Mth.hsvToRgb(1.0F, f, f);
    }

    public double amountColor(ItemStack stack){
        if (stack.getTag() != null) {
            int i = stack.getTag().getInt(ROTTEN_FLESH);
            return 1.0D - (i / (double) MainConfig.PendantOfHungerLimit.get());
        } else {
            return 1.0D;
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getTag() != null;
    }

    @Override
    public int getBarWidth(ItemStack stack){
        if (stack.getTag() != null) {
            int power = stack.getTag().getInt(ROTTEN_FLESH);
            return Math.round((power * 13.0F / MainConfig.PendantOfHungerLimit.get()));
        } else {
            return 0;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (stack.getTag() != null) {
            int rottenFlesh = stack.getTag().getInt(ROTTEN_FLESH);
            tooltip.add(Component.translatable("info.goety.hunger_pendent.amount", rottenFlesh));
        } else {
            tooltip.add(Component.translatable("info.goety.hunger_pendent.amount", 0));
        }
    }

}
