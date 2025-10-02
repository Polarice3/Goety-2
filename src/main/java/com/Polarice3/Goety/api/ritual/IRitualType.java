package com.Polarice3.Goety.api.ritual;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public interface IRitualType {

    String getName();

    default ItemStack getJeiIcon(){
        return new ItemStack(Items.OBSIDIAN);
    }

    default boolean getRequirement(RitualBlockEntity pTileEntity, BlockPos pPos, Level pLevel){
        return false;
    }

    default void onFinishRitual(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                                Player castingPlayer, ItemStack activationItem){
    }
}
