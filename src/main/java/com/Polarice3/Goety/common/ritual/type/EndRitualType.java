package com.Polarice3.Goety.common.ritual.type;

import com.Polarice3.Goety.api.ritual.IRitualType;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.common.ritual.RitualTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EndRitualType implements IRitualType {
    @Override
    public String getName() {
        return RitualTypes.END;
    }

    @Override
    public ItemStack getJeiIcon() {
        return new ItemStack(ModBlocks.VOID_BLOCK.get());
    }

    @Override
    public boolean getRequirement(RitualBlockEntity pTileEntity, BlockPos pPos, Level pLevel) {
        return RitualRequirements.getStructures(this.getName(), pPos, pLevel) && (pLevel.dimension() == Level.END || pLevel.getBiome(pPos).is(BiomeTags.IS_END));
    }
}
