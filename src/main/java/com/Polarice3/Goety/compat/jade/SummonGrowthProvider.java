package com.Polarice3.Goety.compat.jade;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;

public enum SummonGrowthProvider implements IServerDataProvider<EntityAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag tag, EntityAccessor accessor) {
        int time = -1;
        Entity entity = accessor.getEntity();
        if (entity instanceof AnimalSummon ageable) {
            time = -ageable.getAge();
        }
        if (time > 0) {
            tag.putInt("GrowingTime", time);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return Goety.location("summon_growth");
    }
}
