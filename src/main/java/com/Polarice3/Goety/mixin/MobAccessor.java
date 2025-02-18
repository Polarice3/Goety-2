package com.Polarice3.Goety.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

@Mixin(Mob.class)
public interface MobAccessor {

    @Accessor(value = "spawnType", remap = false)
    void setSpawnType(@Nullable MobSpawnType spawnType);
}
