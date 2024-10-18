package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.events.ArcaTeleporter;
import com.Polarice3.Goety.common.magic.spells.void_spells.CallSpell;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CallFocus extends MagicFocus{
    public static final String TAG_ENTITY = "Summoned";

    public CallFocus() {
        super(new CallSpell());
    }

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
                if (stack.getItem() instanceof CallFocus) {
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
        if (player.isShiftKeyDown() || player.isCrouching()){
            if (itemstack.getItem() instanceof CallFocus){
                if (hasSummon(itemstack) && itemstack.getTag() != null){
                    itemstack.getTag().remove(TAG_ENTITY);
                }
            }
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }
        return InteractionResultHolder.pass(itemstack);
    }

    public static void call(ServerPlayer player, ItemStack stack){
        if (hasSummon(stack) && stack.getTag() != null) {
            CompoundTag compoundTag = stack.getTag();
            LivingEntity livingEntity = getSummon(compoundTag);
            if (player.level instanceof ServerLevel serverLevel) {
                if (livingEntity != null) {
                    List<LivingEntity> list = new ArrayList<>();
                    list.add(livingEntity);
                    if (player.isShiftKeyDown() || player.isCrouching()) {
                        for (Entity entity : serverLevel.getAllEntities()) {
                            if (entity instanceof LivingEntity livingEntity1 && entity.getType() == livingEntity.getType()) {
                                if (livingEntity1 instanceof OwnableEntity ownable){
                                    if (ownable.getOwner() == player){
                                        list.add(livingEntity1);
                                    }
                                }
                            }
                        }
                    }
                    for (LivingEntity livingEntity1 : list) {
                        if (livingEntity1.isPassenger() && livingEntity1.getVehicle() instanceof LivingEntity vehicle) {
                            livingEntity1 = vehicle;
                        }
                        if (!livingEntity1.isDeadOrDying()) {
                            BlockPos blockPos = BlockFinder.SummonRadius(player.blockPosition(), livingEntity, serverLevel);
                            if (player.isShiftKeyDown() || player.isCrouching()) {
                                if (list.size() == 1) {
                                    blockPos = player.blockPosition();
                                }
                            }
                            if (livingEntity1.level.dimension() == player.level.dimension()) {
                                net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(livingEntity1, blockPos.getX(), blockPos.getY(), blockPos.getZ());
                                if (event.isCanceled()) {
                                    break;
                                }
                                livingEntity1.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                                MobUtil.moveDownToGround(livingEntity1);
                                ModNetwork.sendToALL(new SPlayWorldSoundPacket(player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
                                ModNetwork.sendToALL(new SPlayWorldSoundPacket(blockPos, SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
                            } else if (player.getServer() != null) {
                                ServerLevel serverWorld = player.getServer().getLevel(player.level.dimension());
                                if (serverWorld != null) {
                                    Vec3 vec3 = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                                    net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(livingEntity1, blockPos.getX(), blockPos.getY(), blockPos.getZ());
                                    if (event.isCanceled()) {
                                        break;
                                    }
                                    livingEntity1.changeDimension(serverWorld, new ArcaTeleporter(vec3));
                                    livingEntity1.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                                    MobUtil.moveDownToGround(livingEntity1);
                                    ModNetwork.sendToALL(new SPlayWorldSoundPacket(player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
                                }
                            }
                        }
                    }
                }
            }
        }
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
        addCallText(stack, tooltip);
    }

    public static void addCallText(ItemStack stack, List<Component> tooltip){
        if (stack.getTag() != null) {
            if (!hasSummon(stack)) {
                tooltip.add(Component.translatable("info.goety.focus.noSummon"));
            } else {
                LivingEntity livingEntity = getSummon(stack.getTag());
                if (livingEntity != null){
                    tooltip.add(Component.translatable("info.goety.focus.summon").append(" ")
                            .append(livingEntity.getCustomName() != null ? livingEntity.getCustomName() : livingEntity.getDisplayName())
                            .withStyle(ChatFormatting.GREEN));
                }
            }
        }
    }
}
