package com.Polarice3.Goety.api.entities.ally.illager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface ILooter extends InventoryCarrier {

    default List<ItemStack> itemsInInv(Predicate<ItemStack> predicate){
        List<ItemStack> list = new ArrayList<>();
        SimpleContainer simplecontainer = this.getInventory();
        int i = simplecontainer.getContainerSize();
        for (int j = 0; j < i; ++j) {
            ItemStack itemStack = simplecontainer.getItem(j);
            if (predicate.test(itemStack)){
                list.add(itemStack);
            }
        }
        return list;
    }

    default int getItemAmount(Predicate<ItemStack> predicate) {
        if (this.itemsInInv(predicate).isEmpty()) {
            return 0;
        }
        return this.itemsInInv(predicate).size();
    }

    default boolean hasItem(Predicate<ItemStack> predicate) {
        return this.getItemAmount(predicate) > 0;
    }

    @Nullable
    default BlockPos getChestPos() {
        return null;
    }

    default void setChestPos(@Nullable BlockPos chestPos) {
    }

    default ResourceKey<Level> getChestLevel() {
        ResourceLocation resourcelocation = new ResourceLocation(this.getChestDim());
        return ResourceKey.create(Registries.DIMENSION, resourcelocation);
    }

    default String getChestDim() {
        return Level.OVERWORLD.toString();
    }

    default void setChestDim(String string) {
    }

    default void setChestDim(ResourceKey<Level> resourceKey) {
        this.setChestDim(resourceKey.location().toString());
    }

    @Nullable
    default BlockPos getDumpChestPos() {
        return null;
    }

    default void setDumpChestPos(@Nullable BlockPos chestPos) {
    }

    default ResourceKey<Level> getDumpChestLevel() {
        ResourceLocation resourcelocation = new ResourceLocation(this.getDumpChestDim());
        return ResourceKey.create(Registries.DIMENSION, resourcelocation);
    }

    default String getDumpChestDim() {
        return Level.OVERWORLD.toString();
    }

    default void setDumpChestDim(String string) {
    }

    default void setDumpChestDim(ResourceKey<Level> resourceKey) {
        this.setDumpChestDim(resourceKey.location().toString());
    }

    default void readLooterData(CompoundTag compound){
        if (compound.contains("ChestPos")){
            this.setChestPos(NbtUtils.readBlockPos(compound.getCompound("ChestPos")));
            if (compound.contains("ChestDim")){
                this.setChestDim(compound.getString("ChestDim"));
            }
        }
        if (compound.contains("DumpChestPos")){
            this.setDumpChestPos(NbtUtils.readBlockPos(compound.getCompound("DumpChestPos")));
            if (compound.contains("DumpChestDim")){
                this.setDumpChestDim(compound.getString("DumpChestDim"));
            }
        }
    }

    default void saveLooterData(CompoundTag compound){
        if (this.getChestPos() != null) {
            compound.put("ChestPos", NbtUtils.writeBlockPos(this.getChestPos()));
            compound.putString("ChestDim", this.getChestDim());
        }
        if (this.getDumpChestPos() != null) {
            compound.put("DumpChestPos", NbtUtils.writeBlockPos(this.getDumpChestPos()));
            compound.putString("DumpChestDim", this.getDumpChestDim());
        }
    }
}
