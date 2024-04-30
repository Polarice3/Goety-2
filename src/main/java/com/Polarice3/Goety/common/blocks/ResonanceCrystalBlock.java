package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.ResonanceCrystalBlockEntity;
import com.Polarice3.Goety.common.entities.ally.golem.SquallGolem;
import com.Polarice3.Goety.common.items.block.ResonanceBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ResonanceCrystalBlock extends BaseEntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ResonanceCrystalBlock() {
        super(ModBlocks.JadeStoneProperties());
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.FALSE));
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @javax.annotation.Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        BlockEntity tileentity = pLevel.getBlockEntity(pPos);
        if (pPlacer instanceof Player){
            if (pStack.getItem() instanceof ResonanceBlockItem) {
                if (tileentity instanceof ResonanceCrystalBlockEntity blockEntity) {
                    if (!ResonanceBlockItem.getGolems(pStack, pLevel).isEmpty()){
                        for (SquallGolem squallGolem : ResonanceBlockItem.getGolems(pStack, pLevel)){
                            blockEntity.addSquallGolem(squallGolem);
                        }
                    }
                }
            }
        }
    }

    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @javax.annotation.Nullable BlockEntity pTe, ItemStack pStack) {
        ItemStack itemStack = new ItemStack(this);
        if (pTe instanceof ResonanceCrystalBlockEntity blockEntity) {
            this.setItemStackTags(itemStack, blockEntity);
        }
        popResource(pLevel, pPos, itemStack);
        super.playerDestroy(pLevel, pPlayer, pPos, pState, pTe, pStack);
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        ItemStack itemStack = new ItemStack(this);
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof ResonanceCrystalBlockEntity blockEntity) {
            this.setItemStackTags(itemStack, blockEntity);
        }
        return itemStack;
    }

    public void setItemStackTags(ItemStack itemStack, ResonanceCrystalBlockEntity tileEntity){
        if (!tileEntity.getUuids().isEmpty()){
            for (UUID uuid : tileEntity.getUuids()) {
                ResonanceBlockItem.setUUIDs(itemStack, uuid);
            }
        }
    }

    public boolean isSignalSource(BlockState p_55213_) {
        return true;
    }

    public int getSignal(BlockState p_55208_, BlockGetter p_55209_, BlockPos p_55210_, Direction p_55211_) {
        return p_55208_.getValue(POWERED) ? 15 : 0;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(POWERED, Boolean.FALSE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new ResonanceCrystalBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof ResonanceCrystalBlockEntity blockEntity1)
                blockEntity1.tick();
        };
    }
}
