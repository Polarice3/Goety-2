package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.HoleBlockEntity;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class HoleBlock extends BaseEntityBlock {
    public HoleBlock() {
        super(Properties.of()
                .noCollission()
                .noLootTable()
                .lightLevel((p_50892_) -> {
                    return 10;
                })
                .noOcclusion()
                .pushReaction(PushReaction.BLOCK)
                .strength(-1.0F, 3600000.0F)
                .isValidSpawn(ModBlocks::never));
    }

    public RenderShape getRenderShape(BlockState p_48758_) {
        return RenderShape.INVISIBLE;
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (SpellConfig.TunnelHaveBlacklist.get()) {
            if (context instanceof EntityCollisionContext context1) {
                Entity entity = context1.getEntity();
                if (entity != null && entity.getType().is(ModTags.EntityTypes.HOLE_IMMUNE)) {
                    return Shapes.block();
                }
            }
        }
        return Shapes.empty();
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getVisualShape(BlockState p_56684_, BlockGetter p_56685_, BlockPos p_56686_, CollisionContext p_56687_) {
        return Shapes.block();
    }

    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {
        return true;
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState p_56707_, BlockGetter p_56708_, BlockPos p_56709_) {
        return Shapes.block();
    }

    public boolean canBeReplaced(BlockState p_53012_, Fluid p_53013_) {
        return false;
    }

    public ItemStack getCloneItemStack(BlockGetter p_53003_, BlockPos p_53004_, BlockState p_53005_) {
        return ItemStack.EMPTY;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new HoleBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof HoleBlockEntity holeBlockEntity)
                holeBlockEntity.tick();
        };
    }
}
