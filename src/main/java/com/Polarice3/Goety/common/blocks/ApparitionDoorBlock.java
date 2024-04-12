package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Based on @TeamTwilight codes: <a href="https://github.com/TeamTwilight/twilightforest/blob/1.19.x/src/main/java/twilightforest/block/CastleDoorBlock.java">...</a>
 */
public class ApparitionDoorBlock extends Block {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty VANISHED = BooleanProperty.create("vanish");
    private static final VoxelShape VANISHED_SHAPE = Shapes.create(new AABB(0.375F, 0.375F, 0.375F, 0.625F, 0.625F, 0.625F));

    public ApparitionDoorBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.getStateDefinition().any().setValue(ACTIVE, false).setValue(VANISHED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(ACTIVE, VANISHED);
    }

    @Override
    public boolean skipRendering(BlockState pState, BlockState otherState, Direction direction) {
        return otherState.getBlock() instanceof ApparitionDoorBlock && otherState.getValue(VANISHED) == pState.getValue(VANISHED);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.getValue(VANISHED) || !pState.getValue(ACTIVE) ? Shapes.empty() : super.getOcclusionShape(pState, pLevel, pPos);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(VANISHED) ? Shapes.empty() : super.getCollisionShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(VANISHED) ? VANISHED_SHAPE : super.getShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        return this.onActivation(pLevel, pPos, pState);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!(block instanceof ApparitionDoorBlock) && pLevel.hasNeighborSignal(pPos)) {
            this.onActivation(pLevel, pPos, pState);
        }
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState pState) {
        return PushReaction.BLOCK;
    }

    private InteractionResult onActivation(Level pLevel, BlockPos pPos, BlockState pState) {
        if (pState.getValue(VANISHED) || pState.getValue(ACTIVE)) {
            return InteractionResult.FAIL;
        }

        this.changeToActiveBlock(pLevel, pPos, pState);
        return InteractionResult.SUCCESS;
    }

    private void changeToActiveBlock(Level pLevel, BlockPos pPos, BlockState originState) {
        if (originState.getBlock() instanceof ApparitionDoorBlock) {
            pLevel.setBlockAndUpdate(pPos, originState.setValue(ACTIVE, true));
        }
        pLevel.scheduleTick(pPos, originState.getBlock(), 2 + pLevel.getRandom().nextInt(5));
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(VANISHED)) {
            if (pState.getValue(ACTIVE)) {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(VANISHED, false).setValue(ACTIVE, false));
            } else {
                this.changeToActiveBlock(pLevel, pPos, pState);
            }
            this.playReappearSound(pLevel, pPos);
        } else {
            if (pState.getValue(ACTIVE)) {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(VANISHED, true).setValue(ACTIVE, false));
                pLevel.scheduleTick(pPos, this, 80);
                this.playVanishSound(pLevel, pPos);
                this.vanishParticles(pLevel, pPos);
                for (Direction direction : Direction.values()) {
                    this.checkAndActivateCastleDoor(pLevel, pPos.relative(direction));
                }
            }
        }
    }

    private void playVanishSound(Level pLevel, BlockPos pPos) {
        pLevel.playSound(null, pPos, ModSounds.WRAITH_TELEPORT.get(), SoundSource.BLOCKS, 0.125F, pLevel.getRandom().nextFloat() * 0.25F + 1.75F);
    }

    private void playReappearSound(Level pLevel, BlockPos pPos) {
        pLevel.playSound(null, pPos, ModSounds.WRAITH_TELEPORT.get(), SoundSource.BLOCKS, 0.125F, pLevel.getRandom().nextFloat() * 0.25F + 1.25F);
    }

    public void checkAndActivateCastleDoor(Level pLevel, BlockPos pPos) {
        BlockState state = pLevel.getBlockState(pPos);

        if (state.getBlock() instanceof ApparitionDoorBlock && !state.getValue(VANISHED) && !state.getValue(ACTIVE)) {
            this.changeToActiveBlock(pLevel, pPos, state);
        }
    }

    private void vanishParticles(Level pLevel, BlockPos pPos) {
        if (pLevel instanceof ServerLevel serverLevel) {
            for (int dx = 0; dx < 4; ++dx) {
                for (int dy = 0; dy < 4; ++dy) {
                    for (int dz = 0; dz < 4; ++dz) {
                        double x = pPos.getX() + (dx + 0.5D) / 4.0D;
                        double y = pPos.getY() + (dy + 0.5D) / 4.0D;
                        double z = pPos.getZ() + (dz + 0.5D) / 4.0D;
                        ColorUtil colorUtil = new ColorUtil(0x8deaf9);

                        serverLevel.sendParticles(ModParticleTypes.SPELL_SQUARE.get(), x, y, z, 0, colorUtil.red, colorUtil.green, colorUtil.blue, 0.5F);
                    }
                }
            }
        }
    }
}
