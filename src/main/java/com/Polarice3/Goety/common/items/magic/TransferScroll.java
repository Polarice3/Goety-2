package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.items.ItemBase;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class TransferScroll extends ItemBase {
    public static final String TAG_ENTITY = "Servant";

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isClientSide) {
            if (stack.getTag() != null){
                LivingEntity livingEntity = getSummon(stack.getTag());
                if (livingEntity == null || livingEntity.isDeadOrDying()){
                    stack.getTag().remove(TAG_ENTITY);
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level.isClientSide) {
            if (entity instanceof LivingEntity target) {
                if (stack.getItem() == this) {
                    if (entity instanceof IOwned owned) {
                        if (owned.getTrueOwner() == player) {
                            if (!hasSummon(stack)) {
                                CompoundTag compoundTag = new CompoundTag();
                                if (stack.hasTag()) {
                                    compoundTag = stack.getTag();
                                }
                                setSummon(compoundTag, target);
                                stack.setTag(compoundTag);
                                player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                                ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getItem() == this){
            if (hasSummon(itemstack)){
                LivingEntity summon = getSummon(itemstack);
                if (itemstack.getTag() != null) {
                    if (summon instanceof IOwned owned) {
                        if (owned.getTrueOwner() == player) {
                            if (player.isShiftKeyDown() || player.isCrouching()){
                                itemstack.getTag().remove(TAG_ENTITY);
                            }
                        } else if (RitualRequirements.canSummon(level, player, summon.getType())){
                            owned.setTrueOwner(player);
                            player.playSound(SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, 1.0F, 1.0F);
                            if (!level.isClientSide) {
                                ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, 1.0F, 1.0F));
                            }
                            itemstack.shrink(1);
                        }
                        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
                    }
                }
            }
        }
        return InteractionResultHolder.pass(itemstack);
    }

    public static boolean hasSummon(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null && stack.getTag().contains(TAG_ENTITY);
    }

    public static void setSummon(CompoundTag compoundTag, LivingEntity livingEntity){
        if (compoundTag != null) {
            if (livingEntity != null) {
                compoundTag.putUUID(TAG_ENTITY, livingEntity.getUUID());
            }
        }
    }

    public static LivingEntity getSummon(ItemStack stack){
        if (stack.getTag() != null){
            return getSummon(stack.getTag());
        }
        return null;
    }

    public static LivingEntity getSummon(CompoundTag compoundTag){
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
        addTransferText(stack, tooltip);
    }

    public static void addTransferText(ItemStack stack, List<Component> tooltip){
        if (stack.getTag() != null) {
            if (!hasSummon(stack)) {
                tooltip.add(Component.translatable("info.goety.focus.noServant"));
            } else {
                LivingEntity livingEntity = getSummon(stack.getTag());
                if (livingEntity != null){
                    tooltip.add(Component.translatable("info.goety.focus.servant").append(" ")
                            .append(livingEntity.getCustomName() != null ? livingEntity.getCustomName() : livingEntity.getDisplayName())
                            .withStyle(ChatFormatting.GREEN));
                    if (livingEntity instanceof IOwned owned) {
                        if (owned.getMasterOwner() != null) {
                            tooltip.add(Component.translatable("info.goety.focus.owner").append(" ")
                                    .append(owned.getMasterOwner().getCustomName() != null ? owned.getMasterOwner().getCustomName() : owned.getMasterOwner().getDisplayName())
                                    .withStyle(ChatFormatting.GREEN));
                        }
                    }
                }
            }
        }
    }
}
