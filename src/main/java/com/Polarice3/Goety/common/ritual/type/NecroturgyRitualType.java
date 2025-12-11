package com.Polarice3.Goety.common.ritual.type;

import com.Polarice3.Goety.api.ritual.IRitualType;
import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.common.ritual.RitualTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class NecroturgyRitualType implements IRitualType {
    @Override
    public String getName() {
        return RitualTypes.NECROTURGY;
    }

    @Override
    public ItemStack getJeiIcon() {
        return new ItemStack(Items.SCULK);
    }

    @Override
    public boolean getRequirement(RitualBlockEntity pTileEntity, Player pPlayer, BlockPos pPos, Level pLevel) {
        if (!(pLevel.getSkyDarken() >= 4 && pLevel.dimensionType().hasSkyLight())) {
            if (pPlayer != null) {
                pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.night"), true);
            }
            return false;
        }
        return RitualRequirements.getStructures(this.getName(), pPlayer, pPos, pLevel);
    }
}
