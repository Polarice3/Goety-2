package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class TaglockKit extends Item {
    public static final String TAG_ENTITY = "Tagged";

    public TaglockKit() {
        super(new Properties()
                .rarity(Rarity.UNCOMMON)
                .setNoRepair());
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, Level worldIn, @NotNull Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isClientSide) {
            if (stack.getTag() != null){
                LivingEntity livingEntity = getEntity(stack.getTag());
                if (livingEntity == null || livingEntity.isDeadOrDying()){
                    stack.getTag().remove(TAG_ENTITY);
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack p_41453_) {
        return hasEntity(p_41453_);
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level.isClientSide) {
            if (entity instanceof LivingEntity target) {
                if (stack.getItem() instanceof TaglockKit) {
                    if (!hasEntity(stack)) {
                        if (entity instanceof Player player1 && !sneakATag(player, player1)){
                            player.displayClientMessage(Component.translatable("info.goety.taglock.failed").withStyle(ChatFormatting.RED), true);
                            player1.displayClientMessage(Component.translatable("info.goety.taglock.discover").withStyle(ChatFormatting.GOLD), true);
                            player.level.playSound(player, player, SoundEvents.NOTE_BLOCK_SNARE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                        } else {
                            CompoundTag compoundTag = new CompoundTag();
                            if (stack.hasTag()) {
                                compoundTag = stack.getTag();
                            }
                            setEntity(compoundTag, target);
                            stack.setTag(compoundTag);
                            ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                        }
                        return true;
                    }
                }
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        Player player = pContext.getPlayer();
        ItemStack stack = pContext.getItemInHand();
        BlockState blockState = level.getBlockState(blockpos);
        if (player != null) {
            if (!blockState.isAir()) {
                taglockFromBed(player, stack, level, blockpos);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return super.useOn(pContext);
    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() || player.isCrouching()){
            if (itemstack.getItem() instanceof TaglockKit){
                if (hasEntity(itemstack) && itemstack.getTag() != null){
                    itemstack.getTag().remove(TAG_ENTITY);
                }
            }
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }
        return InteractionResultHolder.pass(itemstack);
    }

    private void taglockFromBed(Player player, ItemStack stack, Level level, BlockPos blockPos) {
        if (!level.isClientSide) {
            String boundName = "";
            LivingEntity boundEntity = getEntity(stack);
            if (boundEntity instanceof Player player1) {
                boundName = player1.getScoreboardName();
            }

            List<Player> players = new ArrayList<>();

            for (Player found : level.players()) {
                if (found instanceof ServerPlayer serverPlayer) {
                    BlockPos respawn = serverPlayer.getRespawnPosition();
                    if (respawn != null && respawn.equals(blockPos)) {
                        players.add(found);
                    }
                }
            }

            Comparator<Player> comparator = Comparator.comparing(Player::getScoreboardName);
            players.sort(comparator);
            if (players.size() > 0) {
                if (boundName.isEmpty()) {
                    setNewTaglock(player, stack, players.get(0));
                } else {
                    boolean bind = false;

                    for (int i = 0; i < players.size() && !bind; ++i) {
                        if (players.get(i).getScoreboardName().equals(boundName)) {
                            if (i == players.size() - 1) {
                                setNewTaglock(player, stack, players.get(0));
                            } else {
                                setNewTaglock(player, stack, players.get(i + 1));
                            }

                            bind = true;
                        }
                    }

                    if (!bind) {
                        setNewTaglock(player, stack, players.get(0));
                    }
                }
            }

        }
    }

    public static boolean hasEntity(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null && stack.getTag().contains(TAG_ENTITY);
    }

    public static void setNewTaglock(Player player, ItemStack itemStack, LivingEntity livingEntity){
        if (livingEntity != null) {
            CompoundTag compoundTag = new CompoundTag();
            if (itemStack.hasTag()) {
                compoundTag = itemStack.getTag();
            }
            setEntity(compoundTag, livingEntity);
            itemStack.setTag(compoundTag);
            player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
            ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
        }
    }

    public static void setEntity(CompoundTag compoundTag, LivingEntity livingEntity){
        if (compoundTag != null) {
            if (livingEntity != null) {
                compoundTag.putUUID(TAG_ENTITY, livingEntity.getUUID());
            }
        }
    }

    public static boolean isSameDimension(LivingEntity livingEntity, ItemStack stack){
        LivingEntity tagged = getEntity(stack);
        return tagged != null && tagged.level.dimension() == livingEntity.level.dimension();
    }

    public static boolean isInRange(Vec3 origin, ItemStack stack, int increase){
        LivingEntity tagged = getEntity(stack);
        if (tagged != null){
            double scale = 6.0D * (increase + 1.0F);
            double trueRange = Mth.square(scale) * 2.0D;
            return tagged.distanceToSqr(origin) <= Mth.square(trueRange);
        } else {
            return false;
        }
    }

    public static boolean canAffect(LivingEntity livingEntity, ItemStack stack, Vec3 origin, int increase){
        return stack.getItem() instanceof TaglockKit && TaglockKit.hasEntity(stack) && isSameDimension(livingEntity, stack) && isInRange(origin, stack, increase);
    }

    public static LivingEntity getEntity(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        if (compoundtag != null) {
            return getEntity(compoundtag);
        } else {
            return null;
        }
    }

    public static LivingEntity getEntity(CompoundTag compoundTag){
        boolean flag = compoundTag.contains(TAG_ENTITY);
        if (flag){
            UUID uuid = compoundTag.getUUID(TAG_ENTITY);
            return EntityFinder.getLivingEntityByUuiD(uuid);
        }
        return null;
    }

    private boolean sneakATag(Player source, LivingEntity target) {
        if (isNotAllowed(source, target)) {
            return false;
        } else {
            double sourceFacing = (source.getYHeadRot() + 90.0F) % 360.0F;
            if(sourceFacing < 0.0D) {
                sourceFacing += 360.0D;
            }

            double targetFacing = (target.getYHeadRot() + 90.0F) % 360.0F;
            if(targetFacing < 0.0D) {
                targetFacing += 360.0D;
            }

            double arc = 45.0D;
            double diff = Math.abs(targetFacing - sourceFacing);
            double chance;
            if (((360.0D - diff) % 360.0D) >= arc && (diff % 360.0D) >= arc) {
                chance = source.isCrouching() ? 0.1D : 0.01D;
            } else {
                chance = source.isCrouching() ? 0.6D : 0.3D;
            }
            if(source.isInvisible()) {
                chance += 0.1D;
            }

            return source.getRandom().nextDouble() < chance;
        }
    }

    public static boolean isNotAllowed(Player collector, LivingEntity target) {
        if(target instanceof Player && collector != target) {
            if (collector.level.getServer() != null) {
                if (!collector.level.getServer().isPvpAllowed()) {
                    return true;
                } else {
                    Player targetPlayer = (Player) target;
                    return targetPlayer.canUseGameMasterBlocks();
                }
            }
        }
        return false;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        addTaggedText(stack, tooltip);
    }

    public static void addTaggedText(ItemStack stack, List<Component> tooltip){
        if (stack.getTag() != null) {
            if (hasEntity(stack)) {
                LivingEntity livingEntity = getEntity(stack.getTag());
                if (livingEntity != null){
                    tooltip.add(Component.translatable("info.goety.taglock.tagged").append(" ")
                            .append(livingEntity.getCustomName() != null ? livingEntity.getCustomName() : livingEntity.getDisplayName())
                            .withStyle(ChatFormatting.GRAY));
                }
            }
        }
    }
}
