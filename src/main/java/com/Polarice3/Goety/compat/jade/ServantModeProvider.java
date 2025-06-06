package com.Polarice3.Goety.compat.jade;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.entities.ally.IServant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum ServantModeProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (!accessor.getServerData().contains("Movement", Tag.TAG_STRING)) {
            return;
        }
        String mode = accessor.getServerData().getString("Movement");
        if (!mode.isEmpty()) {
            tooltip.add(Component.translatable("jade.goety.movement").append(Component.translatable("jade.goety.movement." + mode)));
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, EntityAccessor accessor) {
        String mode = null;
        Entity entity = accessor.getEntity();
        if (entity instanceof IServant servant && servant.canUpdateMove()) {
            if (servant.getTrueOwner() instanceof Player) {
                if (servant.isFollowing()) {
                    mode = "follow";
                } else if (servant.isGuardingArea()) {
                    mode = "guard";
                } else if (servant.isWandering()) {
                    mode = "wander";
                } else if (servant.isStaying()) {
                    mode = "stand";
                } else if (servant.isCommanded()) {
                    mode = "command";
                }
            }
        }
        if (mode != null) {
            tag.putString("Movement", mode);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return Goety.location("servant_mode");
    }
}
