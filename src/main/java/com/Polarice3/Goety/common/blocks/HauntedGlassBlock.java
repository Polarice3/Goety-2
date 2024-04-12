package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HauntedGlassBlock extends AbstractGlassBlock {
    public boolean isPlayerOnly;
    public boolean isTinted;

    public HauntedGlassBlock(Properties properties, boolean isPlayerOnly, boolean isTinted) {
        super(properties);
        this.isPlayerOnly = isPlayerOnly;
        this.isTinted = isTinted;
    }

    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        if (this.isTinted){
            return false;
        } else {
            return super.propagatesSkylightDown(blockState, blockGetter, blockPos);
        }
    }

    public int getLightBlock(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        if (this.isTinted) {
            return blockGetter.getMaxLightLevel();
        } else {
            return super.getLightBlock(blockState, blockGetter, blockPos);
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext context1){
            if (context1.getEntity() instanceof Player){
                if (this.isPlayerOnly){
                    return Shapes.empty();
                }
            } else {
                if (!this.isPlayerOnly){
                    return Shapes.empty();
                }
            }
        }
        return state.getShape(world, pos);
    }
}
