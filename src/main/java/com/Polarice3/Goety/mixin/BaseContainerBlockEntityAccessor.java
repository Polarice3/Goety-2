package com.Polarice3.Goety.mixin;

import net.minecraft.world.LockCode;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BaseContainerBlockEntity.class)
public interface BaseContainerBlockEntityAccessor {
    @Accessor("lockKey")
    LockCode goety$getLockKey();

    @Accessor("lockKey")
    void goety$setLockKey(LockCode lockKey);
}
