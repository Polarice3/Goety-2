package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;

public class GrowTreeBlockEffect extends BrewEffect {
    public Block block;
    public AbstractTreeGrower treeGrower;

    public GrowTreeBlockEffect(Block block, AbstractTreeGrower treeGrower) {
        super(block.getDescriptionId(), MobEffectCategory.NEUTRAL, 0x5a3f1e, true);
        this.block = block;
        this.treeGrower = treeGrower;
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        if (pLevel instanceof ServerLevel serverLevel) {
            BlockState blockState = serverLevel.getBlockState(pPos.above());
            if (this.treeGrower != null) {
                if (this.block.defaultBlockState().canSurvive(pLevel, pPos.above()) || BlockFinder.canBeReplaced(pLevel, pPos)) {
                    this.treeGrower.growTree(serverLevel, serverLevel.getChunkSource().getGenerator(), pPos.above(), blockState, serverLevel.random);
                }
            }
        }
    }

    public String getDescriptionId() {
        return "effect.goety.grow_tree";
    }

    public MutableComponent getDisplayName() {
        return Component.translatable(this.getDescriptionId()).append(" - ").append(Component.translatable(this.block.getDescriptionId()));
    }

}
