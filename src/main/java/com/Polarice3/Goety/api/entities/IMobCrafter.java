package com.Polarice3.Goety.api.entities;

import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface IMobCrafter {

    default Optional<BlockPos> getFurnacePos() {
        return Optional.empty();
    }

    default void setFurnacePos(@Nullable BlockPos blockPos) {
    }

    default Optional<BlockPos> getCraftTablePos() {
        return Optional.empty();
    }

    default void setCraftTablePos(@Nullable BlockPos blockPos) {
    }

    default void setUsingFurnace(boolean cooking) {
    }

    default boolean isUsingFurnace() {
        return false;
    }

    default boolean isFurnace(BlockState blockState) {
        return false;
    }

    default void setCrafting(boolean crafting) {
    }

    default boolean isCrafting() {
        return false;
    }

    default boolean isCraftTable(BlockState blockState) {
        return false;
    }

    default boolean isFurnaceActuallyCooking() {
        if (this.getFurnacePos().isEmpty()) {
            return false;
        }
        if (this instanceof Entity entity) {
            BlockEntity be = entity.level.getBlockEntity(this.getFurnacePos().get());
            if (be instanceof AbstractFurnaceBlockEntity blockEntity) {
                return blockEntity.litTime > 0;
            }
        }
        return false;
    }

    default void setFurnaceLit(boolean lit) {
        if (this.getFurnacePos().isEmpty()) {
            return;
        }
        if (this instanceof Entity entity) {
            BlockPos blockPos = this.getFurnacePos().get();
            BlockState blockState = entity.level.getBlockState(blockPos);
            if ((this.isFurnace(blockState))
                    && blockState.hasProperty(BlockStateProperties.LIT)
                    && blockState.getValue(BlockStateProperties.LIT) != lit) {
                entity.level.setBlock(blockPos, blockState.setValue(BlockStateProperties.LIT, lit), 3);
            }
        }
    }

    default List<Mob> getNearbyCraftTableUsers(Level level, AABB aabb, BlockPos blockPos) {
        return level.getEntitiesOfClass(Mob.class, aabb, mob -> mob instanceof IMobCrafter crafter && crafter.getCraftTablePos().isPresent()
                && BlockFinder.samePos(crafter.getCraftTablePos().get(), blockPos)
                && crafter.isCrafting()
                && crafter != this);
    }

    default void readCrafterData(CompoundTag compound){
        if (compound.contains("FurnacePos")){
            this.setFurnacePos(NbtUtils.readBlockPos(compound.getCompound("FurnacePos")));
        }
        if (compound.contains("CraftTablePos")){
            this.setCraftTablePos(NbtUtils.readBlockPos(compound.getCompound("CraftTablePos")));
        }
    }

    default void saveCrafterData(CompoundTag compound){
        if (this.getFurnacePos().isPresent()) {
            compound.put("FurnacePos", NbtUtils.writeBlockPos(this.getFurnacePos().get()));
        }
        if (this.getCraftTablePos().isPresent()) {
            compound.put("CraftTablePos", NbtUtils.writeBlockPos(this.getCraftTablePos().get()));
        }
    }
}
