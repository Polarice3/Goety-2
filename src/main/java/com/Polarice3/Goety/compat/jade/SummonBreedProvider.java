package com.Polarice3.Goety.compat.jade;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import snownee.jade.api.IServerDataProvider;

public enum SummonBreedProvider implements IServerDataProvider<Entity> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, Entity entity, boolean b) {
        int time = 0;
        if (entity instanceof AnimalSummon animalSummon) {
            time = animalSummon.getAge();
            if (time > 0) {
                compoundTag.putInt("BreedingCD", time);
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return Goety.location("summon_breed");
    }
}
