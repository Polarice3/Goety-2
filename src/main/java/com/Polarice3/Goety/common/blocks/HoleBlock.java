package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.HoleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class HoleBlock extends BaseEntityBlock {
    public HoleBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE)
                .noCollission()
                .noLootTable()
                .lightLevel((p_50892_) -> {
                    return 10;
                })
                .noOcclusion()
                .strength(-1.0F, 3600000.0F)
                .isValidSpawn(ModBlocks::never));
    }

    public RenderShape getRenderShape(BlockState p_48758_) {
        return RenderShape.INVISIBLE;
    }

    public VoxelShape getShape(BlockState p_48760_, BlockGetter p_48761_, BlockPos p_48762_, CollisionContext p_48763_) {
        return Shapes.empty();
    }

    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {
        return true;
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
