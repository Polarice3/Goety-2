package com.Polarice3.Goety.compat.jade;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum SummonLifespanProvider implements IEntityComponentProvider, IServerDataProvider<Entity> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (!accessor.getServerData().contains("Lifespan", Tag.TAG_INT)) {
            return;
        }
        if (accessor.getServerData().contains("LimitedLifespan")) {
            if (!accessor.getServerData().getBoolean("LimitedLifespan")) {
                return;
            }
        }
        int time = accessor.getServerData().getInt("Lifespan");
        if (time > 0) {
            tooltip.add(Component.translatable("jade.goety.lifespan", new Object[]{time / 20}));
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, Entity entity, boolean b) {
        int time = -1;
        boolean hasLifespan = false;
        if (entity instanceof Owned owned) {
            time = owned.limitedLifeTicks;
            hasLifespan = owned.limitedLifespan;
        }
        if (time > 0) {
            compoundTag.putInt("Lifespan", time);
        }
        compoundTag.putBoolean("LimitedLifespan", hasLifespan);
    }

    @Override
    public ResourceLocation getUid() {
        return Goety.location("summon_lifespan");
    }
}
