package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.ModBlockEntities;
import com.Polarice3.Goety.common.blocks.entities.NightBeaconBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

public class NightBeaconBlock extends BaseEntityBlock implements BeaconBeamBlock{
    public NightBeaconBlock() {
        super(Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
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

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @javax.annotation.Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        BlockEntity tileentity = pLevel.getBlockEntity(pPos);
        if (tileentity instanceof NightBeaconBlockEntity blockEntity){
            blockEntity.setDaylLight(pLevel.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT));
        }

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

    @Override
    public DyeColor getColor() {
        return DyeColor.BLACK;
    }
}
