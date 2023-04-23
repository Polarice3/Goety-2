package com.Polarice3.Goety.common.items.capability;

import com.Polarice3.Goety.common.items.handler.FocusBagItemHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FocusBagItemCapability implements ICapabilitySerializable<Tag> {
    private final ItemStack stack;
    private final LazyOptional<IItemHandler> holder = LazyOptional.of(this::getHandler);
    private FocusBagItemHandler handler;

    public FocusBagItemCapability(ItemStack stack) {
        this.stack = stack;
    }

    @Nonnull
    private FocusBagItemHandler getHandler() {
        if (handler == null) {
            handler = new FocusBagItemHandler(stack);
        }
        return handler;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, holder);
    }

    public Tag serializeNBT() {
        return getHandler().serializeNBT();
    }

    public void deserializeNBT(Tag nbt) {
        this.getHandler().deserializeNBT((CompoundTag) nbt);
    }
}
