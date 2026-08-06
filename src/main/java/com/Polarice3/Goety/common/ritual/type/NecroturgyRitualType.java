package com.Polarice3.Goety.common.ritual.type;

import com.Polarice3.Goety.api.ritual.IRitualType;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import com.Polarice3.Goety.common.ritual.Ritual;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.common.ritual.RitualTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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

    @Override
    public void onFinishRitual(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity, Player castingPlayer, ItemStack activationItem) {
        for (int i = -Ritual.RANGE; i <= Ritual.RANGE; ++i) {
            for (int j = -Ritual.RANGE; j <= Ritual.RANGE; ++j) {
                for (int k = -Ritual.RANGE; k <= Ritual.RANGE; ++k) {
                    BlockPos blockpos1 = darkAltarPos.offset(i, j, k);
                    BlockState blockstate = world.getBlockState(blockpos1);
                    if (blockstate.is(ModBlocks.GRAVE_SOIL.get())){
                        if (world.getRandom().nextBoolean()) {
                            world.levelEvent(null, 2001, blockpos1, Block.getId(blockstate));
                            world.setBlockAndUpdate(blockpos1, ModBlocks.DARK_DIRT.get().defaultBlockState());
                        }
                    }
                }
            }
        }
    }
}
