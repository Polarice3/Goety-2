package com.Polarice3.Goety.compat.jade;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.entities.ArcaBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum SoulEnergyProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (blockAccessor.getBlockEntity() instanceof ArcaBlockEntity arcaTile){
            if (arcaTile.getPlayer() == blockAccessor.getPlayer() && SEHelper.getSEActive(blockAccessor.getPlayer())) {
                iTooltip.add(Component.translatable("jade.goety.soul_energy_arca", SEHelper.getSESouls(blockAccessor.getPlayer()), MainConfig.MaxArcaSouls.get()));
            }
        } else if (blockAccessor.getBlockEntity() instanceof CursedCageBlockEntity cageBlockEntity) {
            iTooltip.add(Component.translatable("jade.goety.soul_energy", cageBlockEntity.getSouls()));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return Goety.location("soul_energy");
    }

}
