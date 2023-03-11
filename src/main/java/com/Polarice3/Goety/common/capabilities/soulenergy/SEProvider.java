package com.Polarice3.Goety.common.capabilities.soulenergy;

import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SEProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
    public static Capability<ISoulEnergy> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    ISoulEnergy instance = new SEImp();

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CAPABILITY ? LazyOptional.of(() -> (T) instance) : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return SEHelper.save(new CompoundTag(), instance);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        SEHelper.load(nbt, instance);
    }
}
