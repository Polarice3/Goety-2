package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.BlackCrystalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BlackCrystalBlock extends EnchanteableBlock {
    public static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D,
            13.0D, 20.0D, 13.0D);

    public BlackCrystalBlock() {
        super(BlockBehaviour.Properties.of()
                .instrument(NoteBlockInstrument.HAT)
                .mapColor(MapColor.COLOR_PURPLE)
                .noOcclusion()
                .requiresCorrectToolForDrops()
                .strength(25.0F, 1200.0F)
                .sound(SoundType.AMETHYST_CLUSTER)
                .pushReaction(PushReaction.BLOCK)
                .isValidSpawn(ModBlocks::never)
                .isSuffocating(ModBlocks::never));
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @javax.annotation.Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        BlockEntity tileentity = pLevel.getBlockEntity(pPos);
        if (pPlacer instanceof Player){
            if (tileentity instanceof BlackCrystalBlockEntity blockEntity){
                blockEntity.setOwner(pPlacer);
            }
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new BlackCrystalBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof BlackCrystalBlockEntity block)
                block.tick();
        };
    }
}
