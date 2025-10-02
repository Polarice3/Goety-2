package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.common.events.TimedEvents;
import com.Polarice3.Goety.config.BrewConfig;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ChopTreeBlockEffect extends BrewEffect {
    public ChopTreeBlockEffect() {
        super("chop_tree", BrewConfig.ChopTreeCost.get(), MobEffectCategory.NEUTRAL, 0x6a5227);
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        Item axeItem = Items.STONE_AXE;
        if (pAmplifier == 1){
            axeItem = Items.IRON_AXE;
        } else if (pAmplifier == 2){
            axeItem = Items.DIAMOND_AXE;
        } else if (pAmplifier > 3){
            axeItem = Items.NETHERITE_AXE;
        }
        ItemStack axe = new ItemStack(axeItem);
        if (pLevel instanceof ServerLevel serverLevel) {
            if (pSource instanceof ServerPlayer serverPlayer) {
                for (BlockPos blockPos : this.getSquarePos(pPos, pAreaOfEffect + 3)) {
                    BlockState blockState = serverLevel.getBlockState(blockPos.above());
                    if (this.isTree(serverLevel, blockPos.above(), blockState)){
                        TimedEvents.submitTask("goety:brew_chop_tree", new BlockFinder.ChopTreeTask(serverPlayer.getUUID(), axe, serverLevel, blockPos));
                    }
                }
            }
        }
    }

    private boolean isTree(Level level, BlockPos pos, BlockState state) {
        if (!state.is(BlockTags.LOGS)) {
            return false;
        }
        while (state.is(BlockTags.LOGS)) {
            state = level.getBlockState(pos = pos.above());
        }
        for (BlockPos blockPos : BlockPos.betweenClosed(pos.offset(-2, -2, -2), pos.offset(2, 2, 2))) {
            if (level.getBlockState(blockPos).is(BlockTags.LEAVES) || level.getBlockState(blockPos).is(BlockTags.LOGS)) {
                return true;
            }
        }
        return false;
    }
}
