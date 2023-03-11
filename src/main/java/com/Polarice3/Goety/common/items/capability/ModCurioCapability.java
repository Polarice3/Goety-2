package com.Polarice3.Goety.common.items.capability;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModCurioCapability implements ICurio, ICapabilityProvider {
    private final ItemStack stack;

    public ModCurioCapability(ItemStack stack) {
        this.stack = stack;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CuriosCapability.ITEM ? LazyOptional.of(() -> (T)this) : LazyOptional.empty();
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }
}
