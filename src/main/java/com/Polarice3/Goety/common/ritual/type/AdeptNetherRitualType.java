package com.Polarice3.Goety.common.ritual.type;

import com.Polarice3.Goety.api.ritual.IRitualType;
import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.common.ritual.RitualTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class AdeptNetherRitualType implements IRitualType {
    @Override
    public String getName() {
        return RitualTypes.ADEPT_NETHER;
    }

    @Override
    public ItemStack getJeiIcon() {
        return new ItemStack(Items.BLACKSTONE);
    }

    @Override
    public boolean getRequirement(RitualBlockEntity pTileEntity, BlockPos pPos, Level pLevel) {
        return RitualRequirements.getStructures(this.getName(), pPos, pLevel) && (pLevel.dimensionType().ultraWarm() || pLevel.getBiome(pPos).is(BiomeTags.IS_NETHER));
    }
}
