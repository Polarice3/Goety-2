package com.Polarice3.Goety.common.ritual.type;

import com.Polarice3.Goety.api.ritual.IRitualType;
import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.common.ritual.RitualTypes;
import net.minecraft.core.BlockPos;
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
    public boolean getRequirement(RitualBlockEntity pTileEntity, BlockPos pPos, Level pLevel) {
        return RitualRequirements.getStructures(this.getName(), pPos, pLevel) && pLevel.getSkyDarken() >= 4 && pLevel.dimensionType().hasSkyLight();
    }
}
