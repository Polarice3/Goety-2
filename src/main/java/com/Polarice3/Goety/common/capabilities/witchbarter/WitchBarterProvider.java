package com.Polarice3.Goety.common.capabilities.witchbarter;

import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WitchBarterProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
    public static Capability<IWitchBarter> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    IWitchBarter instance = new WitchBarterImp();

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CAPABILITY ? LazyOptional.of(() -> (T) instance) : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return save(new CompoundTag(), instance);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        load(nbt, instance);
    }

    public static CompoundTag save(CompoundTag tag, IWitchBarter witchBarter) {
        tag.putInt("barterTimer", witchBarter.getTimer());
        if (witchBarter.getTrader() != null) {
            tag.putUUID("barterTrader", witchBarter.getTrader().getUUID());
        }
        return tag;
    }

    public static IWitchBarter load(CompoundTag tag, IWitchBarter witchBarter) {
        witchBarter.setTimer(tag.getInt("barterTimer"));
        if (tag.hasUUID("barterTrader")) {
            witchBarter.setTrader(EntityFinder.getLivingEntityByUuiD(tag.getUUID("barterTrader")));
        }
        return witchBarter;
    }
}
