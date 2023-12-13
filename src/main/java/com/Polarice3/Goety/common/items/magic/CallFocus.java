package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.events.ArcaTeleporter;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.spells.void_spells.CallSpell;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayEntitySoundPacket;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class CallFocus extends MagicFocus{
    public static final String TAG_ENTITY = "Summoned";

    public CallFocus() {
        super(new CallSpell());
    }

    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity entity, InteractionHand hand) {
        if (stack.getItem() instanceof CallFocus) {
            if (entity instanceof Owned owned){
                if (owned.getTrueOwner() == playerIn){
                    if (!hasSummon(stack)) {
                        ItemStack itemStack = new ItemStack(ModItems.CALL_FOCUS.get());
                        CompoundTag compoundTag = itemStack.getOrCreateTag();
                        setSummon(compoundTag, owned);
                        itemStack.setTag(compoundTag);
                        playerIn.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                        ModNetwork.sendToALL(new SPlayEntitySoundPacket(playerIn.getUUID(), SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                        ItemHelper.addAndConsumeItem(playerIn, hand, itemStack, true);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.interactLivingEntity(stack, playerIn, entity, hand);
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

    public static boolean call(ServerPlayer player, ItemStack stack){
        if (hasSummon(stack) && stack.getTag() != null) {
            CompoundTag compoundTag = stack.getTag();
            LivingEntity livingEntity = getSummon(compoundTag);
            if (livingEntity != null){
                if (livingEntity.isPassenger() && livingEntity.getVehicle() instanceof LivingEntity vehicle){
                    livingEntity = vehicle;
                }
                if (!livingEntity.isDeadOrDying()) {
                    BlockPos blockPos = BlockFinder.SummonRadius(player, player.level);
                    if (livingEntity.level.dimension() == player.level.dimension()) {
                        livingEntity.teleportTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                        MobUtil.moveDownToGround(livingEntity);
                        ModNetwork.INSTANCE.send(PacketDistributor.ALL.noArg(), new SPlayWorldSoundPacket(player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
                        ModNetwork.INSTANCE.send(PacketDistributor.ALL.noArg(), new SPlayWorldSoundPacket(blockPos, SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
                        return true;
                    } else if (player.getServer() != null) {
                        ServerLevel serverWorld = player.getServer().getLevel(player.level.dimension());
                        if (serverWorld != null) {
                            Vec3 vec3 = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                            livingEntity.changeDimension(serverWorld, new ArcaTeleporter(vec3));
                            livingEntity.teleportTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                            MobUtil.moveDownToGround(livingEntity);
                            ModNetwork.INSTANCE.send(PacketDistributor.ALL.noArg(), new SPlayWorldSoundPacket(player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean hasSummon(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null && getSummon(compoundtag) != null;
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
