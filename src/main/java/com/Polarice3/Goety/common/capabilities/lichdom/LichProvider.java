package com.Polarice3.Goety.common.capabilities.lichdom;

import com.Polarice3.Goety.utils.LichdomHelper;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LichProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
    public static Capability<ILichdom> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    ILichdom instance = new LichImp();

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CAPABILITY ? LazyOptional.of(() -> (T) instance) : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return LichdomHelper.save(new CompoundTag(), instance);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        LichdomHelper.load(nbt, instance);
    }
}
