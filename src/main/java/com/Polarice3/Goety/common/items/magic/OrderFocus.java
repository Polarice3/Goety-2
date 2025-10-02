package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.magic.spells.utility.CommandSpell;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderFocus extends MagicFocus{
    public static String SERVANT_LIST = "servantList";
    public static String SERVANT_CLIENT_LIST = "servantClientList";

    public OrderFocus() {
        super(new CommandSpell());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isClientSide) {
            ListTag listTag = getServantList(stack, worldIn);
            if (listTag != null && stack.getTag() != null){
                if (listTag.isEmpty()){
                    stack.getTag().remove(SERVANT_LIST);
                    stack.getTag().remove(SERVANT_CLIENT_LIST);
                }
            }
            List<LivingEntity> list = getServants(stack);
            List<LivingEntity> list2 = OrderFocus.getServantsClient(worldIn, stack);
            List<Integer> integerList = getServantIds(stack);
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); ++i) {
                    LivingEntity livingEntity = list.get(i);
                    if (livingEntity != null) {
                        if (livingEntity.isRemoved() || livingEntity.isDeadOrDying()) {
                            removeServant(stack, livingEntity, worldIn);
                            removeServantClient(stack, livingEntity.getId(), worldIn);
                        } else {
                            if (!list2.contains(livingEntity)) {
                                setServantsClient(stack, worldIn, livingEntity);
                            }
                        }
                        if (!integerList.isEmpty()) {
                            if (integerList.size() >= list.size()) {
                                int id = integerList.get(i);
                                Entity entity = worldIn.getEntity(id);
                                if (!(entity instanceof LivingEntity) || entity.isRemoved() || !entity.isAlive() || livingEntity.getId() != id) {
                                    removeServantClient(stack, id, worldIn);
                                }
                            }
                        }
                    }
                }
            } else {
                if (listTag != null && stack.getTag() != null){
                    stack.getTag().remove(SERVANT_LIST);
                    stack.getTag().remove(SERVANT_CLIENT_LIST);
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level.isClientSide) {
            if (entity instanceof LivingEntity livingEntity) {
                if (stack.getItem() instanceof OrderFocus) {
                    if (livingEntity instanceof IServant owned && owned.canBeCommanded()) {
                        if (owned.getTrueOwner() == player) {
                            List<LivingEntity> list = getServants(stack);
                            if (list.isEmpty() || list.size() < 8) {
                                if (!list.contains(livingEntity)) {
                                    setServants(stack, player, livingEntity);
                                    ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() || player.isCrouching()){
            if (itemstack.getItem() instanceof OrderFocus){
                if (itemstack.getTag() != null){
                    itemstack.getTag().remove(SERVANT_LIST);
                    itemstack.getTag().remove(SERVANT_CLIENT_LIST);
                }
            }
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }
        return InteractionResultHolder.pass(itemstack);
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return p_41453_.getTag() != null && p_41453_.getTag().contains(SERVANT_LIST);
    }

    public static ListTag getServantList(ItemStack stack, Level level){
        if (!level.isClientSide) {
            CompoundTag compound = new CompoundTag();
            if (stack.hasTag()) {
                compound = stack.getTag();
            }
            if (compound != null) {
                if (compound.contains(SERVANT_LIST)) {
                    return compound.getList(SERVANT_LIST, 8);
                }
            }
        }
        return null;
    }

    public static void removeServant(ItemStack stack, LivingEntity livingEntity, Level level){
        if (!level.isClientSide) {
            CompoundTag compound = new CompoundTag();
            if (stack.hasTag()) {
                compound = stack.getTag();
            }
            List<String> list = new ArrayList<>();
            if (compound != null) {
                if (compound.contains(SERVANT_LIST)) {
                    for (int i = 0; i < compound.getList(SERVANT_LIST, 8).size(); ++i) {
                        list.add(compound.getList(SERVANT_LIST, 8).getString(i));
                    }
                }

                if (list.contains(livingEntity.getStringUUID())) {
                    ListTag nbttaglist = new ListTag();
                    if (compound.contains(SERVANT_LIST)) {
                        nbttaglist = compound.getList(SERVANT_LIST, 8);
                    }

                    nbttaglist.remove(StringTag.valueOf(livingEntity.getStringUUID()));
                    compound.put(SERVANT_LIST, nbttaglist);
                    stack.setTag(compound);
                }
            }
        }
    }

    public static void removeServantClient(ItemStack stack, int id, Level level){
        if (!level.isClientSide) {
            CompoundTag compound = new CompoundTag();
            if (stack.hasTag()) {
                compound = stack.getTag();
            }
            List<Integer> list = new ArrayList<>();
            if (compound != null) {
                if (compound.contains(SERVANT_CLIENT_LIST)) {
                    for (int i = 0; i < compound.getList(SERVANT_CLIENT_LIST, 3).size(); ++i) {
                        list.add(compound.getList(SERVANT_CLIENT_LIST, 3).getInt(i));
                    }
                }
                if (list.contains(id)) {
                    ListTag nbttaglist = new ListTag();
                    if (compound.contains(SERVANT_CLIENT_LIST)) {
                        nbttaglist = compound.getList(SERVANT_CLIENT_LIST, 3);
                    }

                    nbttaglist.remove(IntTag.valueOf(id));
                    compound.put(SERVANT_CLIENT_LIST, nbttaglist);
                    stack.setTag(compound);
                }
            }
        }
    }

    public static List<LivingEntity> getServants(ItemStack stack){
        CompoundTag compoundtag = stack.getTag();
        if (compoundtag != null) {
            return getServants(compoundtag);
        } else {
            return new ArrayList<>();
        }
    }

    public static List<LivingEntity> getServants(@Nullable CompoundTag compoundTag){
        List<LivingEntity> servants = new ArrayList<>();
        if (compoundTag != null && compoundTag.contains(SERVANT_LIST)){
            ListTag list = compoundTag.getList(SERVANT_LIST, 8);
            for(int i = 0; i < list.size(); ++i) {
                Entity entity = EntityFinder.getLivingEntityByUuiD(UUID.fromString(list.getString(i)));
                if (entity instanceof LivingEntity livingEntity){
                    servants.add(livingEntity);
                }
            }
        }
        return servants;
    }

    public static List<Integer> getServantIds(ItemStack stack){
        CompoundTag compoundtag = stack.getTag();
        if (compoundtag != null) {
            return getServantIds(compoundtag);
        } else {
            return new ArrayList<>();
        }
    }

    public static List<Integer> getServantIds(@Nullable CompoundTag compoundTag){
        List<Integer> integers = new ArrayList<>();
        if (compoundTag != null && compoundTag.contains(SERVANT_CLIENT_LIST)){
            ListTag list = compoundTag.getList(SERVANT_CLIENT_LIST, 3);
            for(int i = 0; i < list.size(); ++i) {
                integers.add(list.getInt(i));
            }
        }
        return integers;
    }

    public static List<LivingEntity> getServantsClient(Level level, ItemStack stack){
        CompoundTag compoundtag = stack.getTag();
        if (compoundtag != null) {
            return getServantsClient(level, compoundtag);
        } else {
            return new ArrayList<>();
        }
    }

    public static List<LivingEntity> getServantsClient(Level level, @Nullable CompoundTag compoundTag){
        List<LivingEntity> servants = new ArrayList<>();
        if (compoundTag != null && compoundTag.contains(SERVANT_CLIENT_LIST)){
            ListTag list = compoundTag.getList(SERVANT_CLIENT_LIST, 3);
            for(int i = 0; i < list.size(); ++i) {
                Entity entity = level.getEntity(list.getInt(i));
                if (entity instanceof LivingEntity livingEntity){
                    servants.add(livingEntity);
                }
            }
        }
        return servants;
    }

    public static void setServants(ItemStack stack, Player player, LivingEntity livingEntity){
        if (!player.level.isClientSide) {
            CompoundTag compound = new CompoundTag();
            if (stack.hasTag()) {
                compound = stack.getTag();
            }
            List<String> list = new ArrayList<>();
            if (compound != null) {
                if (compound.contains(SERVANT_LIST)) {
                    for (int i = 0; i < compound.getList(SERVANT_LIST, 8).size(); ++i) {
                        list.add(compound.getList(SERVANT_LIST, 8).getString(i));
                    }
                }

                if (!list.contains(livingEntity.getStringUUID())) {
                    ListTag nbttaglist = new ListTag();
                    if (compound.contains(SERVANT_LIST)) {
                        nbttaglist = compound.getList(SERVANT_LIST, 8);
                    }

                    nbttaglist.add(StringTag.valueOf(livingEntity.getStringUUID()));
                    compound.put(SERVANT_LIST, nbttaglist);
                    stack.setTag(compound);
                }
            }
        }
    }

    public static void setServantsClient(ItemStack stack, Level level, LivingEntity livingEntity){
        if (!level.isClientSide) {
            CompoundTag compound = new CompoundTag();
            if (stack.hasTag()) {
                compound = stack.getTag();
            }
            List<Integer> list = new ArrayList<>();
            if (compound != null) {
                if (compound.contains(SERVANT_CLIENT_LIST)) {
                    for (int i = 0; i < compound.getList(SERVANT_CLIENT_LIST, 3).size(); ++i) {
                        list.add(compound.getList(SERVANT_CLIENT_LIST, 3).getInt(i));
                    }
                }

                if (!list.contains(livingEntity.getId())) {
                    ListTag nbttaglist = new ListTag();
                    if (compound.contains(SERVANT_CLIENT_LIST)) {
                        nbttaglist = compound.getList(SERVANT_CLIENT_LIST, 3);
                    }

                    nbttaglist.add(IntTag.valueOf(livingEntity.getId()));
                    compound.put(SERVANT_CLIENT_LIST, nbttaglist);
                    stack.setTag(compound);
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        addCommandText(worldIn, stack, tooltip);
    }

    public static void addCommandText(Level level, ItemStack stack, List<Component> tooltip){
        if (stack.getTag() != null) {
            if (!stack.getTag().contains(SERVANT_LIST)) {
                tooltip.add(Component.translatable("info.goety.focus.noServant"));
            } else {
                if (level != null) {
                    for (LivingEntity livingEntity : getServantsClient(level, stack.getTag())) {
                        if (livingEntity != null) {
                            tooltip.add(Component.translatable("info.goety.focus.servant").append(" ")
                                    .append(livingEntity.getCustomName() != null ? livingEntity.getCustomName() : livingEntity.getDisplayName())
                                    .withStyle(ChatFormatting.GREEN));
                        }
                    }
                }
            }
        }
    }
}
