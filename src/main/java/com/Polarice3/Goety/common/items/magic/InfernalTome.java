package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.HellChant;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class InfernalTome extends Item {
    public static String CHANT_TIMES = "Chant Times";

    public InfernalTome() {
        super(new Properties().durability(64).fireResistant().rarity(Rarity.UNCOMMON));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entityIn, int p_41407_, boolean p_41408_) {
        if (stack.getTag() != null) {
            if (!stack.getTag().contains(CHANT_TIMES)) {
                stack.getTag().putInt(CHANT_TIMES, 0);
            }
        }
        super.inventoryTick(stack, level, entityIn, p_41407_, p_41408_);
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        CompoundTag compound = pStack.getOrCreateTag();
        compound.putInt(CHANT_TIMES, 0);
        super.onCraftedBy(pStack, pLevel, pPlayer);
    }

    public static int getChantTimes(ItemStack pStack){
        if (pStack.getTag() != null){
            if (pStack.getTag().contains(CHANT_TIMES)){
                return pStack.getTag().getInt(CHANT_TIMES);
            }
        }
        return 0;
    }

    public static void setChantTimes(ItemStack pStack, int time){
        if (pStack.getTag() != null){
            pStack.getTag().putInt(CHANT_TIMES, time);
        }
    }

    public static void increaseChantTimes(ItemStack pStack){
        setChantTimes(pStack, getChantTimes(pStack) + 1);
    }

    public static boolean isChanting(ItemStack pStack){
        if (pStack.getTag() != null){
            if (pStack.getTag().contains(CHANT_TIMES)){
                return pStack.getTag().getInt(CHANT_TIMES) > 0;
            }
        }
        return false;
    }

    @Override
    public void onUseTick(Level worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
        super.onUseTick(worldIn, livingEntityIn, stack, count);
        if (stack.getItem() instanceof InfernalTome) {
            int CastTime = stack.getUseDuration() - count;
            if (CastTime == 1) {
                worldIn.playSound(null, livingEntityIn.getX(), livingEntityIn.getY(), livingEntityIn.getZ(), ModSounds.HERETIC_CHANT.get(), SoundSource.PLAYERS, 2.0F, 0.5F);
            }
            if (!worldIn.isClientSide) {
                boolean nether = CuriosFinder.hasNetherRobe(livingEntityIn);
                if (!nether){
                    livingEntityIn.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false));
                }
                if (count % 10 == 0) {
                    HellChant hellChant = ModEntityType.HELL_CHANT.get().create(worldIn);
                    if (hellChant != null) {
                        hellChant.setExtraDamage(stack.getEnchantmentLevel(ModEnchantments.POTENCY.get()));
                        hellChant.setBurning(stack.getEnchantmentLevel(ModEnchantments.BURNING.get()));
                        hellChant.chant(livingEntityIn);
                        worldIn.addFreshEntity(hellChant);
                        ItemHelper.hurtAndBreak(stack, 1, livingEntityIn);
                    }
                }
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level level, LivingEntity livingEntity) {
        Player player = livingEntity instanceof Player ? (Player)livingEntity : null;

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (pStack.getItem() instanceof InfernalTome) {
                player.getCooldowns().addCooldown(this, 100);
                if (pStack.getTag() != null) {
                    if (pStack.getTag().getInt(CHANT_TIMES) != 0) {
                        pStack.getTag().putInt(CHANT_TIMES, 0);
                    }
                }
            }
        }
        return pStack;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int useTimeRemaining) {
        if (stack.getItem() instanceof InfernalTome) {
            if (livingEntity instanceof Player player){
                player.getCooldowns().addCooldown(this, 100);
                if (stack.getTag() != null) {
                    if (stack.getTag().getInt(CHANT_TIMES) != 0) {
                        stack.getTag().putInt(CHANT_TIMES, 0);
                    }
                }
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack p_41454_) {
        return 60;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_41452_) {
        return UseAnim.BOW;
    }

    public InteractionResultHolder<ItemStack> use(Level p_40672_, Player p_40673_, InteractionHand p_40674_) {
        ItemStack itemstack = p_40673_.getItemInHand(p_40674_);
        p_40673_.startUsingItem(p_40674_);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == ModEnchantments.POTENCY.get()
                || enchantment == ModEnchantments.BURNING.get()
                || super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
    }

    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.getItem() == Items.PAPER || super.isValidRepairItem(pToRepair, pRepair);
    }
}
