package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class WaystoneItem extends ItemBase {
    public static final String TAG_OWNER = "Owner";
    public static final String TAG_OWNER_NAME = "Owner_Name";
    public static final String TAG_POS = "Pos";
    public static final String TAG_DIRECTION = "Direction";
    public static final String TAG_FACING = "Facing";
    public static final String TAG_DIMENSION = "Dimension";

    public boolean isFoil(ItemStack pStack) {
        return hasBlock(pStack);
    }

    public static boolean hasBlock(ItemStack p_40737_) {
        CompoundTag compoundtag = p_40737_.getTag();
        return compoundtag != null && (compoundtag.contains(TAG_DIMENSION) || compoundtag.contains(TAG_POS));
    }

    private static Optional<ResourceKey<Level>> getBlockDimension(CompoundTag p_40728_) {
        return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, p_40728_.get(TAG_DIMENSION)).result();
    }

    @Nullable
    public static GlobalPos getPosition(ItemStack itemStack) {
        if (itemStack.isEmpty()
                || itemStack.getTag() == null
                || !itemStack.getTag().contains(TAG_POS)
                || !itemStack.getTag().contains(TAG_DIMENSION)) {
            return null;
        }
        CompoundTag tag = itemStack.getOrCreateTag();
        return getPosition(tag);
    }

    @Nullable
    public static GlobalPos getPosition(CompoundTag p_220022_) {
        boolean flag = p_220022_.contains(TAG_POS);
        boolean flag1 = p_220022_.contains(TAG_DIMENSION);
        if (flag && flag1) {
            Optional<ResourceKey<Level>> optional = getBlockDimension(p_220022_);
            if (optional.isPresent()) {
                BlockPos blockpos = NbtUtils.readBlockPos(p_220022_.getCompound(TAG_POS));
                return GlobalPos.of(optional.get(), blockpos);
            }
        }

        return null;
    }

    @Nullable
    public static Direction getDirection(ItemStack itemStack) {
        if (itemStack.isEmpty()
                || itemStack.getTag() == null
                || !itemStack.getTag().contains(TAG_DIRECTION)) {
            return null;
        }
        CompoundTag tag = itemStack.getOrCreateTag();
        return getDirection(tag);
    }

    @Nullable
    public static Direction getDirection(CompoundTag p_220022_){
        boolean flag = p_220022_.contains(TAG_DIRECTION);
        if (flag){
            return Direction.values()[p_220022_.getInt(TAG_DIRECTION)];
        }
        return null;
    }

    @Nullable
    public static Direction getFacing(CompoundTag p_220022_){
        boolean flag = p_220022_.contains(TAG_FACING);
        if (flag){
            return Direction.values()[p_220022_.getInt(TAG_FACING)];
        }
        return null;
    }

    public static boolean isSameDimension(LivingEntity livingEntity, ItemStack stack){
        GlobalPos globalPos = getPosition(stack);
        return globalPos != null && globalPos.dimension() == livingEntity.level.dimension();
    }

    public static boolean isInRange(Vec3 origin, ItemStack stack, int increase){
        GlobalPos globalPos = getPosition(stack);
        if (globalPos != null) {
            double scale = 6.0D * (increase + 1.0F);
            double trueRange = Mth.square(scale) * 2.0D;
            return globalPos.pos().distToCenterSqr(origin) <= Mth.square(trueRange);
        } else {
            return false;
        }
    }

    public static boolean canAffect(LivingEntity livingEntity, ItemStack stack, Vec3 origin, int increase){
        return stack.getItem() instanceof WaystoneItem && WaystoneItem.getPosition(stack) != null && isSameDimension(livingEntity, stack) && isInRange(origin, stack, increase);
    }

    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player player, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        if (!player.level.isClientSide) {
            if (stack.getItem() instanceof WaystoneItem) {
                if (entity instanceof IServant owned) {
                    if (owned.getTrueOwner() == player) {
                        GlobalPos globalPos = getPosition(stack);
                        if (globalPos != null) {
                            if (globalPos.dimension() == player.level.dimension()) {
                                owned.setBoundPos(globalPos.pos());
                                owned.setWandering(false);
                                owned.setStaying(false);
                                player.displayClientMessage(Component.translatable("info.goety.servant.patrol", entity.getDisplayName()), true);
                                player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                                ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        BlockPos blockpos = context.getClickedPos();
        Direction side = context.getClickedFace();
        if (player != null) {
            ItemStack held = player.getItemInHand(hand);
            if (!held.isEmpty() && held.getItem() instanceof WaystoneItem) {
                player.swing(hand);
                CompoundTag nbt = held.getOrCreateTag();
                nbt.putUUID(TAG_OWNER, player.getUUID());
                nbt.putString(TAG_OWNER_NAME, player.getDisplayName().getString());
                addBlockTags(player.level.dimension(), blockpos, nbt);
                nbt.putInt(TAG_DIRECTION, side.ordinal());
                nbt.putInt(TAG_FACING, player.getDirection().ordinal());
                player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                player.level.playLocalSound(blockpos.getX(), blockpos.getY(), blockpos.getZ(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 1.0F, 0.45F, false);
                held.setTag(nbt);
                return InteractionResult.sidedSuccess(player.level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() || player.isCrouching()){
            if (itemstack.getItem() instanceof WaystoneItem){
                if (hasBlock(itemstack) && itemstack.getTag() != null){
                    itemstack.setTag(new CompoundTag());
                }
            }
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }
        return InteractionResultHolder.pass(itemstack);
    }

    private void addBlockTags(ResourceKey<Level> p_40733_, BlockPos p_40734_, CompoundTag p_40735_) {
        p_40735_.put(TAG_POS, NbtUtils.writeBlockPos(p_40734_));
        Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, p_40733_).resultOrPartial(Goety.LOGGER::error).ifPresent((p_40731_) -> {
            p_40735_.put(TAG_DIMENSION, p_40731_);
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        addWaystoneText(stack, tooltip);
    }

    public static void addWaystoneText(ItemStack stack, List<Component> tooltip){
        if (stack.getTag() != null) {
            if (!hasBlock(stack)) {
                tooltip.add(Component.translatable("info.goety.focus.noPos2"));
            } else {
                GlobalPos globalPos = getPosition(stack.getTag());
                if (globalPos != null) {
                    tooltip.add(Component.translatable("info.goety.focus.Pos2").append(" ").append(Component.translatable("info.goety.focus.PosNum", globalPos.pos().getX(), globalPos.pos().getY(), globalPos.pos().getZ())));
                    tooltip.add(Component.translatable("info.goety.focus.PosDim", globalPos.dimension().location().toString()));
                }
                Direction direction = getDirection(stack.getTag());
                if (direction != null){
                    tooltip.add(Component.translatable("info.goety.focus.PosDirection", direction.getName()));
                }
            }
        }
    }
}
