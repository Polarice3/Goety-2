package com.Polarice3.Goety.common.capabilities.misc;

import com.Polarice3.Goety.utils.MiscCapHelper;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MiscProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
    public static Capability<IMisc> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    IMisc instance = new MiscImp();

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CAPABILITY ? LazyOptional.of(() -> (T) instance) : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return MiscCapHelper.save(new CompoundTag(), instance);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        MiscCapHelper.load(nbt, instance);
    }
}
