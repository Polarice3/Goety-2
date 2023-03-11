package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.SculkDevourerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEventListener;

import javax.annotation.Nullable;

public class SculkDevourerBlock extends BaseEntityBlock {
    public static final BooleanProperty PULSE = BlockStateProperties.BLOOM;

    public SculkDevourerBlock() {
        super(ModBlocks.ShadeStoneProperties());
        this.registerDefaultState(this.stateDefinition.any().setValue(PULSE, Boolean.valueOf(false)));
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        BlockEntity tileentity = pLevel.getBlockEntity(pPos);
        if (pPlacer instanceof Player){
            if (tileentity instanceof SculkDevourerBlockEntity blockEntity){
                blockEntity.setOwnerId(pPlacer.getUUID());
            }
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_222115_) {
        p_222115_.add(PULSE);
    }

    public void tick(BlockState p_222104_, ServerLevel p_222105_, BlockPos p_222106_, RandomSource p_222107_) {
        if (p_222104_.getValue(PULSE)) {
            p_222105_.setBlock(p_222106_, p_222104_.setValue(PULSE, Boolean.valueOf(false)), 3);
        }

    }

    public int getSignal(BlockState p_57402_, BlockGetter p_57403_, BlockPos p_57404_, Direction p_57405_) {
        return p_57402_.getValue(PULSE) ? 15 : 0;
    }

    public boolean isSignalSource(BlockState p_57418_) {
        return true;
    }

    public static void bloom(ServerLevel p_222095_, BlockPos p_222096_, BlockState p_222097_, RandomSource p_222098_) {
        p_222095_.setBlock(p_222096_, p_222097_.setValue(PULSE, Boolean.valueOf(true)), 3);
        p_222095_.scheduleTick(p_222096_, p_222097_.getBlock(), 8);
        p_222095_.sendParticles(ParticleTypes.SCULK_SOUL, (double)p_222096_.getX() + 0.5D, (double)p_222096_.getY() + 1.15D, (double)p_222096_.getZ() + 0.5D, 2, 0.2D, 0.0D, 0.2D, 0.0D);
        p_222095_.playSound((Player)null, p_222096_, SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.BLOCKS, 2.0F, 0.6F + p_222098_.nextFloat() * 0.4F);
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos p_222117_, BlockState p_222118_) {
        return new SculkDevourerBlockEntity(p_222117_, p_222118_);
    }

    @Nullable
    public <T extends BlockEntity> GameEventListener getListener(ServerLevel p_222092_, T p_222093_) {
        return p_222093_ instanceof SculkDevourerBlockEntity ? (SculkDevourerBlockEntity)p_222093_ : null;
    }

    public RenderShape getRenderShape(BlockState p_222120_) {
        return RenderShape.MODEL;
    }
}
