package com.Polarice3.Goety.compat.jade;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.entities.ITrainable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public enum TrainableProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (!accessor.getServerData().contains("TrainingTime", Tag.TAG_INT)) {
            return;
        }
        if (!accessor.getServerData().contains("TotalTime", Tag.TAG_INT)) {
            return;
        }
        int time = accessor.getServerData().getInt("TrainingTime");
        int totalTime = accessor.getServerData().getInt("TotalTime");
        if (time > 0 && totalTime > 0) {
            tooltip.add(Component.translatable("jade.goety.training", IThemeHelper.get().seconds(time), IThemeHelper.get().seconds(totalTime)));
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, EntityAccessor accessor) {
        int time = -1;
        int totalTime = 0;
        Entity entity = accessor.getEntity();
        if (entity instanceof ITrainable owned) {
            time = owned.getTrainTime();
            totalTime = owned.getTotalTrainTime();
        }
        if (time > 0 && totalTime > 0) {
            tag.putInt("TrainingTime", time);
            tag.putInt("TotalTime", totalTime);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return Goety.location("trainable");
    }
}
