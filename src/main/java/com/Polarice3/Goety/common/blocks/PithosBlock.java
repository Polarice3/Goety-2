package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.blocks.entities.PithosBlockEntity;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.hostile.SkullLord;
import com.Polarice3.Goety.common.world.structures.ModStructures;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ModLootTables;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class PithosBlock extends BaseEntityBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public PithosBlock() {
        super(Properties.of()
                .forceSolidOn()
                .pushReaction(PushReaction.BLOCK)
                .mapColor(MapColor.STONE)
                .strength(2.5F, 3600000.0F)
                .sound(SoundType.BONE_BLOCK));
        this.registerDefaultState(this.stateDefinition.any().setValue(OPEN, Boolean.FALSE).setValue(LOCKED, Boolean.TRUE).setValue(TRIGGERED, Boolean.FALSE));
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            if (!pState.getValue(LOCKED)) {
                BlockEntity tileentity = pLevel.getBlockEntity(pPos);
                if (pPlayer.getItemInHand(pHand).is(ModTags.Items.RESPAWN_BOSS) && MainConfig.PithosRespawn.get() && pLevel instanceof ServerLevel serverLevel && BlockFinder.findStructure(serverLevel, pPlayer, ModStructures.CRYPT_KEY)){
                    ItemStack itemStack = pPlayer.getItemInHand(pHand);
                    if (tileentity instanceof PithosBlockEntity pithosBlock) {
                        pithosBlock.setLootTable(ModLootTables.CRYPT_TOMB, pLevel.random.nextLong());
                    }
                    if (pPlayer instanceof ServerPlayer serverPlayer){
                        CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pPos, itemStack);
                    }
                    pLevel.setBlock(pPos, pState.setValue(LOCKED, Boolean.TRUE).setValue(TRIGGERED, Boolean.FALSE), 4);
                    itemStack.shrink(1);
                    pLevel.playSound(null, pPos , SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
                } else if (tileentity instanceof PithosBlockEntity) {
                    pPlayer.openMenu((PithosBlockEntity) tileentity);
                }
            } else {
                if (pLevel.getDifficulty() == Difficulty.PEACEFUL){
                    pLevel.setBlock(pPos, pState.setValue(LOCKED, Boolean.FALSE), 4);
                } else if (!pState.getValue(TRIGGERED)){
                    SkullLord skullLord = ModEntityType.SKULL_LORD.get().create(pLevel);
                    if (skullLord != null){
                        skullLord.setBoundOrigin(pPos);
                        skullLord.setPos(pPos.getX(), pPos.getY() + 1.0F, pPos.getZ());
                        pLevel.playSound(null, pPos , SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0F, 1.0F);
                        pLevel.addFreshEntity(skullLord);
                    }
                    pLevel.setBlock(pPos, pState.setValue(TRIGGERED, Boolean.TRUE), 4);
                }
            }

            return InteractionResult.CONSUME;
        }
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity tileentity = pLevel.getBlockEntity(pPos);
            if (tileentity instanceof Container) {
                Containers.dropContents(pLevel, pPos, (Container)tileentity);
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRand) {
        BlockEntity tileentity = pLevel.getBlockEntity(pPos);
        if (tileentity instanceof PithosBlockEntity) {
            ((PithosBlockEntity)tileentity).recheckOpen();
        }

    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos p_152102_, BlockState p_152103_) {
        return new PithosBlockEntity(p_152102_, p_152103_);
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (pStack.hasCustomHoverName()) {
            BlockEntity tileentity = pLevel.getBlockEntity(pPos);
            if (tileentity instanceof PithosBlockEntity) {
                ((PithosBlockEntity)tileentity).setCustomName(pStack.getHoverName());
            }
        }

    }

    public float getDestroyProgress(BlockState pState, Player pPlayer, BlockGetter pLevel, BlockPos pPos) {
        return !pState.getValue(LOCKED) ? 2.5F : -1.0F;
    }

    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(pLevel.getBlockEntity(pPos));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(OPEN, LOCKED, TRIGGERED);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(LOCKED, Boolean.FALSE);
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRand) {
        if (!pState.getValue(TRIGGERED)) {
            double d0 = (double) pPos.getX() + 0.5D;
            double d1 = (double) pPos.getY() + 0.75D;
            double d2 = (double) pPos.getZ() + 0.5D;
            pLevel.addParticle(ParticleTypes.ENCHANT, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }
}
