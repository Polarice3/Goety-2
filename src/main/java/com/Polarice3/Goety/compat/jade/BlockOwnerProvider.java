package com.Polarice3.Goety.compat.jade;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.blocks.entities.IOwnedBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.util.CommonProxy;

import java.util.UUID;

public enum BlockOwnerProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        String name;
        if (blockAccessor.getServerData().contains("OwnerName")) {
            name = blockAccessor.getServerData().getString("OwnerName");
        } else {
            BlockEntity blockEntity = blockAccessor.getBlockEntity();
            UUID ownerUUID = null;
            if (blockEntity instanceof IOwnedBlock ownedBlock) {
                ownerUUID = ownedBlock.getPlayer().getUUID();
            }

            if (ownerUUID == null) {
                return;
            }

            name = CommonProxy.getLastKnownUsername(ownerUUID);
            if (name == null) {
                name = "???";
            }
        }

        iTooltip.add(Component.translatable("jade.owner", new Object[]{name}));
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity().getLevel() == null){
            return;
        }
        BlockEntity blockEntity = blockAccessor.getBlockEntity();
        UUID ownerUUID = null;
        if (blockEntity instanceof IOwnedBlock ownedBlock) {
            ownerUUID = ownedBlock.getPlayer().getUUID();
        }
        if (ownerUUID != null) {
            String name = CommonProxy.getLastKnownUsername(ownerUUID);
            if (name != null) {
                compoundTag.putString("OwnerName", name);
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return Goety.location("block_owner");
    }
}
