package com.Polarice3.Goety.common.ritual.type;

import com.Polarice3.Goety.api.ritual.IRitualType;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import com.Polarice3.Goety.common.ritual.Ritual;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.common.ritual.RitualTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DivinationRitualType implements IRitualType {
    @Override
    public String getName() {
        return RitualTypes.DIVINATION;
    }

    @Override
    public ItemStack getJeiIcon() {
        return new ItemStack(ModBlocks.CRYSTAL_BALL.get());
    }

    @Override
    public boolean getRequirement(RitualBlockEntity pTileEntity, Player pPlayer, BlockPos pPos, Level pLevel) {
        return RitualRequirements.getStructures(this.getName(), pPlayer, pPos, pLevel);
    }

    @Override
    public void onFinishRitual(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity, Player castingPlayer, ItemStack activationItem) {
        if (activationItem.is(Items.MAP)) {
            world.playSound(null, darkAltarPos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
        }
        for (int i = -Ritual.RANGE; i <= Ritual.RANGE; ++i) {
            for (int j = -Ritual.RANGE; j <= Ritual.RANGE; ++j) {
                for (int k = -Ritual.RANGE; k <= Ritual.RANGE; ++k) {
                    BlockPos blockpos1 = darkAltarPos.offset(i, j, k);
                    BlockState blockstate = world.getBlockState(blockpos1);
                    if (blockstate.is(BlockTags.CANDLES)
                            && blockstate.hasProperty(CandleBlock.LIT)
                            && blockstate.getValue(CandleBlock.LIT)){
                        AbstractCandleBlock.extinguish(castingPlayer, blockstate, world, blockpos1);
                    }
                }
            }
        }
    }
}
