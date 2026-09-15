package com.Polarice3.Goety.api.items;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPosHolder {
    String TAG_OWNER = "Owner";
    String TAG_OWNER_NAME = "Owner_Name";
    String TAG_POS = "Pos";
    String TAG_DIRECTION = "Direction";
    String TAG_FACING = "Facing";
    String TAG_DIMENSION = "Dimension";

    static boolean hasBlock(ItemStack p_40737_) {
        CompoundTag compoundtag = p_40737_.getTag();
        return compoundtag != null && (compoundtag.contains(TAG_DIMENSION) || compoundtag.contains(TAG_POS));
    }

    static Optional<ResourceKey<Level>> getBlockDimension(CompoundTag p_40728_) {
        return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, p_40728_.get(TAG_DIMENSION)).result();
    }

    @Nullable
    static GlobalPos getPosition(ItemStack itemStack) {
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
    static GlobalPos getPosition(CompoundTag p_220022_) {
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
    static BlockPos getBlockPos(ItemStack itemStack){
        GlobalPos globalPos = getPosition(itemStack);
        if (globalPos != null){
            return globalPos.pos();
        } else {
            return null;
        }
    }

    @Nullable
    static BlockEntity getBlockEntity(ItemStack itemStack, Level level) {
        BlockPos blockPos = getBlockPos(itemStack);
        if (blockPos == null) {
            return null;
        }
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            if (level.getBlockEntity(blockPos) == null && blockState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
                return level.getBlockEntity(blockPos.below());
            }
        }
        return level.getBlockEntity(blockPos);
    }

    @Nullable
    static Direction getDirection(ItemStack itemStack) {
        if (itemStack.isEmpty()
                || itemStack.getTag() == null
                || !itemStack.getTag().contains(TAG_DIRECTION)) {
            return null;
        }
        CompoundTag tag = itemStack.getOrCreateTag();
        return getDirection(tag);
    }

    @Nullable
    static Direction getDirection(CompoundTag p_220022_){
        boolean flag = p_220022_.contains(TAG_DIRECTION);
        if (flag){
            return Direction.values()[p_220022_.getInt(TAG_DIRECTION)];
        }
        return null;
    }

    @Nullable
    static Direction getFacing(CompoundTag p_220022_){
        boolean flag = p_220022_.contains(TAG_FACING);
        if (flag){
            return Direction.values()[p_220022_.getInt(TAG_FACING)];
        }
        return null;
    }

    static boolean isSameDimension(LivingEntity livingEntity, ItemStack stack){
        GlobalPos globalPos = getPosition(stack);
        return globalPos != null && globalPos.dimension() == livingEntity.level.dimension();
    }

    static boolean isSameDimension(BlockEntity blockEntity, ItemStack stack){
        GlobalPos globalPos = getPosition(stack);
        return globalPos != null && blockEntity.getLevel() != null && globalPos.dimension() == blockEntity.getLevel().dimension();
    }

    static boolean isInRange(Vec3 origin, ItemStack stack, int increase){
        GlobalPos globalPos = getPosition(stack);
        if (globalPos != null) {
            double scale = 6.0D * (increase + 1.0F);
            double trueRange = Mth.square(scale) * 2.0D;
            return globalPos.pos().distToCenterSqr(origin) <= Mth.square(trueRange);
        } else {
            return false;
        }
    }

    static boolean canAffect(LivingEntity livingEntity, ItemStack stack, Vec3 origin, int increase){
        return stack.getItem() instanceof IPosHolder && IPosHolder.getPosition(stack) != null && isSameDimension(livingEntity, stack) && isInRange(origin, stack, increase);
    }

    static UUID getOwner(ItemStack stack) {
        if (stack.getTag() != null) {
            if (stack.getTag().contains(TAG_OWNER)) {
                return stack.getTag().getUUID(TAG_OWNER);
            }
        }
        return null;
    }

    default void addBlockTags(ResourceKey<Level> p_40733_, BlockPos p_40734_, CompoundTag p_40735_) {
        p_40735_.put(TAG_POS, NbtUtils.writeBlockPos(p_40734_));
        Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, p_40733_).resultOrPartial(Goety.LOGGER::error).ifPresent((p_40731_) -> {
            p_40735_.put(TAG_DIMENSION, p_40731_);
        });
    }

    default void addBlockEntityTags(ItemStack itemStack, LivingEntity livingEntity, ResourceKey<Level> resourceKey, BlockPos blockPos, @Nullable Direction direction) {
        CompoundTag nbt = itemStack.getOrCreateTag();
        if (livingEntity != null) {
            nbt.putUUID(TAG_OWNER, livingEntity.getUUID());
            nbt.putString(TAG_OWNER_NAME, livingEntity.getDisplayName().getString());
        }
        addBlockTags(resourceKey, blockPos, nbt);
        if (direction != null) {
            int i = direction.ordinal();
            if (livingEntity != null) {
                i = livingEntity.getDirection().ordinal();
            }
            nbt.putInt(TAG_DIRECTION, direction.ordinal());
            nbt.putInt(TAG_FACING, i);
        }
        itemStack.setTag(nbt);
    }

    static void addHolderText(ItemStack stack, List<Component> tooltip){
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
