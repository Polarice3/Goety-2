package com.Polarice3.Goety.compat.jade;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.util.PlatformProxy;

import java.util.UUID;

public enum SummonOwnerProvider implements IServerDataProvider<Entity> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, Entity entity, boolean b) {
        UUID ownerUUID = null;
        if (entity instanceof OwnableEntity) {
            ownerUUID = ((OwnableEntity)entity).getOwnerUUID();
        } else if (entity instanceof AbstractHorse) {
            ownerUUID = ((AbstractHorse)entity).getOwnerUUID();
        }

        if (ownerUUID != null) {
            String name = PlatformProxy.getLastKnownUsername(ownerUUID);
            if (name != null) {
                compoundTag.putString("OwnerName", name);
            } else {
                Entity entity1 = EntityFinder.getEntityByUuiD(ownerUUID);
                if (entity1 != null){
                    compoundTag.putString("OwnerName", entity1.getDisplayName().getString());
                }
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return Goety.location("summon_owner");
    }
}
