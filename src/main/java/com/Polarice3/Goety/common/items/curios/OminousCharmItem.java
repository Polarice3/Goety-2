package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.api.items.curios.IActivatable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

public class OminousCharmItem extends SingleStackItem implements IActivatable {
    private static final String OMEN_LEVEL = "Omen Level";

    @Override
    public void activate(Level level, Player player, ItemStack itemStack) {
        if (itemStack.is(this)) {
            if (player.hasEffect(MobEffects.BAD_OMEN)) {
                MobEffectInstance instance = player.getEffect(MobEffects.BAD_OMEN);
                if (instance != null) {
                    increaseOmenLevel(itemStack, instance.getAmplifier() + 1);
                    level.playSound(null, player, SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.PLAYERS, 1.0F, 1.0F);
                    player.removeEffect(MobEffects.BAD_OMEN);
                }
            } else if (hasOmen(itemStack)) {
                MobEffectInstance mobeffectinstance = new MobEffectInstance(MobEffects.BAD_OMEN, 120000, getOmenAmount(itemStack) - 1, false, false, true);
                player.addEffect(mobeffectinstance);
                level.playSound(null, player, SoundEvents.RESPAWN_ANCHOR_DEPLETE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                setOmenLevel(itemStack, 0);
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.is(this)) {
            if (player.hasEffect(MobEffects.BAD_OMEN)) {
                MobEffectInstance instance = player.getEffect(MobEffects.BAD_OMEN);
                if (instance != null) {
                    increaseOmenLevel(itemstack, instance.getAmplifier() + 1);
                    level.playSound(null, player, SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.PLAYERS, 1.0F, 1.0F);
                    player.removeEffect(MobEffects.BAD_OMEN);
                    return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
                }
            } else if (hasOmen(itemstack)) {
                MobEffectInstance mobeffectinstance = new MobEffectInstance(MobEffects.BAD_OMEN, 120000, getOmenAmount(itemstack) - 1, false, false, true);
                player.addEffect(mobeffectinstance);
                level.playSound(null, player, SoundEvents.RESPAWN_ANCHOR_DEPLETE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                setOmenLevel(itemstack, 0);
                return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
            }
        }
        return super.use(level, player, hand);
    }

    public static void increaseOmenLevel(ItemStack stack, int amount){
        setOmenLevel(stack, getOmenAmount(stack) + amount);
    }

    public static void setOmenLevel(ItemStack stack, int amount){
        if (stack.getTag() != null) {
            stack.getTag().putInt(OMEN_LEVEL, Math.min(5, amount));
        } else {
            CompoundTag compound = stack.getOrCreateTag();
            compound.putInt(OMEN_LEVEL, Math.min(5, amount));
        }
    }

    public static int getOmenAmount(ItemStack stack) {
        if (stack.getTag() != null) {
            return stack.getTag().getInt(OMEN_LEVEL);
        } else {
            return 0;
        }
    }

    public static boolean hasOmen(ItemStack stack) {
        return getOmenAmount(stack) > 0;
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        CompoundTag compound = pStack.getOrCreateTag();
        compound.putInt(OMEN_LEVEL, 0);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }
}
