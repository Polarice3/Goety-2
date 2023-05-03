package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class PendantOfHungerItem extends SingleStackItem implements ICurioItem {
    private static final String ROTTEN_FLESH = "Rotten Flesh Count";

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
            stack.getOrCreateTag().putInt(ROTTEN_FLESH, 0);
        } else {
            if (slotContext.entity() instanceof Player player) {
                if (getRottenFleshAmount(stack) < MainConfig.PendantOfHungerLimit.get()) {
                    if (!ItemHelper.findItem(player, Items.ROTTEN_FLESH).isEmpty()) {
                        increaseRottenFlesh(stack);
                        ItemHelper.findItem(player, Items.ROTTEN_FLESH).shrink(1);
                    }
                }
                if (getRottenFleshAmount(stack) > 0) {
                    if (MobUtil.playerValidity(player, true)) {
                        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 0, false, false));
                    }
                    if (player.getFoodData().needsFood()){
                        player.eat(player.level, new ItemStack(Items.ROTTEN_FLESH));
                        decreaseRottenFlesh(stack);
                    }
                }
            }
        }
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
