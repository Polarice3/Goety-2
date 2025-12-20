package com.Polarice3.Goety.common.ritual.type;

import com.Polarice3.Goety.api.ritual.IRitualType;
import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.common.ritual.RitualTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class OvergrownRitualType implements IRitualType {
    @Override
    public String getName() {
        return RitualTypes.OVERGROWN;
    }

    @Override
    public ItemStack getJeiIcon() {
        return new ItemStack(Items.JUNGLE_LEAVES);
    }

    @Override
    public boolean getRequirement(RitualBlockEntity pTileEntity, Player pPlayer, BlockPos pPos, Level pLevel) {
        return RitualRequirements.overgrownRitual(pPlayer, pPos, pLevel);
    }
}
