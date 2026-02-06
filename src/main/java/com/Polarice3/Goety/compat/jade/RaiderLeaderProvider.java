package com.Polarice3.Goety.compat.jade;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.UUID;

public enum RaiderLeaderProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
    INSTANCE;

    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (accessor.getServerData().contains("LeaderName")) {
            String name = accessor.getServerData().getString("LeaderName");
            tooltip.add(Component.translatable("jade.goety.raider_leader", name));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, EntityAccessor accessor) {
        Entity entity = accessor.getEntity();
        UUID leaderUUID = null;
        int leaderID = -1;
        if (entity instanceof RaiderServant ownable) {
            leaderUUID = ownable.getLeaderId();
            leaderID = ownable.getLeaderClientId();
        }
        if (leaderUUID != null) {
            Entity entity1 = EntityFinder.getEntityByUuiD(accessor.getLevel(), leaderUUID);
            if (!(entity1 instanceof RaiderServant)){
                entity1 = accessor.getLevel().getEntity(leaderID);
            }
            if (entity1 instanceof RaiderServant){
                data.putString("LeaderName", entity1.getDisplayName().getString());
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return Goety.location("raider_leader");
    }
}
