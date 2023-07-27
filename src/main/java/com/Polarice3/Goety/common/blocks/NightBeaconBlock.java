package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.ModBlockEntities;
import com.Polarice3.Goety.common.blocks.entities.NightBeaconBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.jetbrains.annotations.Nullable;

public class NightBeaconBlock extends BaseEntityBlock {
    public NightBeaconBlock() {
        super(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLACK)
                .strength(3.0F)
                .lightLevel((p_152688_) -> {
                    return 15;
                })
                .noOcclusion()
                .isRedstoneConductor(ModBlocks::never));
    }


    public RenderShape getRenderShape(BlockState p_49439_) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new NightBeaconBlockEntity(p_153215_, p_153216_);
    }

    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152160_, BlockState p_152161_, BlockEntityType<T> p_152162_) {
        return createTickerHelper(p_152162_, ModBlockEntities.NIGHT_BEACON.get(), NightBeaconBlockEntity::tick);
    }
}
