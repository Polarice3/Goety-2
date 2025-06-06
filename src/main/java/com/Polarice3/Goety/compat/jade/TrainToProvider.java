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

public enum TrainToProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (!accessor.getServerData().contains("TrainTo", Tag.TAG_STRING)) {
            return;
        }
        String mode = accessor.getServerData().getString("TrainTo");
        if (!mode.isEmpty() && !mode.equals("None")) {
            tooltip.add(Component.translatable("jade.goety.train_to").append(Component.translatable(mode)));
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, EntityAccessor accessor) {
        String mode = null;
        Entity entity = accessor.getEntity();
        if (entity instanceof ITrainable trainable) {
            mode = trainable.getCurrentTrain();
        }
        if (mode != null) {
            tag.putString("TrainTo", mode);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return Goety.location("train_to");
    }
}
