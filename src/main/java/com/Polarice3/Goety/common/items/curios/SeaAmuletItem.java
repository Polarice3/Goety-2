package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.ItemConfig;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public class SeaAmuletItem extends SingleStackItem{
    private static final String CONDUIT_CHARGES = "Conduit Charges";
    private static final int MAX_POWER = ItemConfig.SeaAmuletMax.get();

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof Player player) {
            boolean flag = true;
            if (!stack.hasTag()) {
                stack.setTag(new CompoundTag());
                stack.getOrCreateTag().putInt(CONDUIT_CHARGES, 0);
            } else {
                if (this.getConduitChargesAmount(stack) > MAX_POWER){
                    this.setConduitCharges(stack, MAX_POWER);
                } else if (this.getConduitChargesAmount(stack) < 0){
                    this.setConduitCharges(stack, 0);
                }
                if (CuriosFinder.hasCurio(player, itemStack -> itemStack == stack)){
                    if (player.isUnderWater()) {
                        BlockEntity blockEntity = BlockFinder.findBlockEntity(BlockEntityType.CONDUIT, worldIn, player.blockPosition(), 8);
                        if (blockEntity instanceof ConduitBlockEntity blockEntity1) {
                            if (blockEntity1.isActive()) {
                                if (this.getConduitChargesAmount(stack) < MAX_POWER) {
                                    if (worldIn instanceof ServerLevel serverLevel) {
                                        ServerParticleUtil.gatheringParticles(ParticleTypes.NAUTILUS, player, serverLevel);
                                    }
                                    if (player.tickCount % 20 == 0) {
                                        this.increaseConduitCharges(stack);
                                    }
                                }
                                flag = false;
                            }
                        }
                        int duration = MathHelper.secondsToTicks(5);
                        if (player.hasEffect(MobEffects.CONDUIT_POWER)){
                            MobEffectInstance mobEffectInstance = player.getEffect(MobEffects.CONDUIT_POWER);
                            flag = mobEffectInstance != null && mobEffectInstance.getDuration() < duration;
                        }
                        if (flag && this.getConduitChargesAmount(stack) > 0 && MobUtil.playerValidity(player, true)) {
                            this.decreaseConduitCharges(stack);
                            player.playSound(SoundEvents.BUBBLE_COLUMN_BUBBLE_POP);
                            player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, duration * 2, 0, false, false));
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
        compound.putInt(CONDUIT_CHARGES, 0);
    }

    public void increaseConduitCharges(ItemStack stack){
        if (stack.getTag() != null && getConduitChargesAmount(stack) < MAX_POWER) {
            stack.getTag().putInt(CONDUIT_CHARGES, Math.min(MAX_POWER, getConduitChargesAmount(stack) + ItemConfig.SeaAmuletChargeConsume.get()));
        }
    }

    public void decreaseConduitCharges(ItemStack stack){
        if (stack.getTag() != null && getConduitChargesAmount(stack) > 0) {
            stack.getTag().putInt(CONDUIT_CHARGES, Math.max(0, getConduitChargesAmount(stack) - ItemConfig.SeaAmuletChargeConsume.get()));
        }
    }

    public void setConduitCharges(ItemStack stack, int charges){
        if (stack.getTag() != null) {
            stack.getTag().putInt(CONDUIT_CHARGES, charges);
        }
    }

    public int getConduitChargesAmount(ItemStack stack) {
        if (stack.getTag() != null) {
            return stack.getTag().getInt(CONDUIT_CHARGES);
        } else {
            return 0;
        }
    }

    public int getBarColor(ItemStack stack) {
        float f = Math.max(0.0F, (float) (1.0F - amountColor(stack))/2.0F);
        return Mth.hsvToRgb(f, 1.0F, f);
    }

    public double amountColor(ItemStack stack){
        if (stack.getTag() != null) {
            int i = stack.getTag().getInt(CONDUIT_CHARGES);
            return 1.0D - (i / (double) MAX_POWER);
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
            int power = stack.getTag().getInt(CONDUIT_CHARGES);
            return Math.round((power * 13.0F / MAX_POWER));
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
            int conduit = stack.getTag().getInt(CONDUIT_CHARGES);
            tooltip.add(Component.translatable("info.goety.sea_amulet.amount", conduit));
        } else {
            tooltip.add(Component.translatable("info.goety.sea_amulet.amount", 0));
        }
    }
}
