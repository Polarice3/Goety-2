package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.VoidFrameBlockEntity;
import com.Polarice3.Goety.common.blocks.properties.ModStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class VoidFrameBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty TYPE = ModStateProperties.TYPE;
    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
    public static int SEARING_EYE = 0;
    public static int HALLOWED_EYE = 1;
    public static int TWISTED_EYE = 2;
    public static int DREADFUL_EYE = 3;
    public static int RANDOM = 4;
    public static int NONE = 5;
    protected static final VoxelShape BASE_SHAPE = box(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);
    protected static final VoxelShape EYE_SHAPE = box(4.0, 13.0, 4.0, 12.0, 16.0, 12.0);
    protected static final VoxelShape FULL_SHAPE = Shapes.or(BASE_SHAPE, EYE_SHAPE);

    public VoidFrameBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .sound(SoundType.GLASS)
                .lightLevel((p_50847_) -> {
                    return 1;
                })
                .strength(-1.0F, 3600000.0F)
                .noLootTable());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LOCKED, Boolean.TRUE)
                .setValue(TYPE, 0));
    }

    public boolean useShapeForLightOcclusion(BlockState p_53079_) {
        return true;
    }

    public VoxelShape getShape(BlockState p_53073_, BlockGetter p_53074_, BlockPos p_53075_, CollisionContext p_53076_) {
        return FULL_SHAPE;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(LOCKED, Boolean.TRUE)
                .setValue(TYPE, context.getPlayer() != null ? context.getLevel().getRandom().nextIntBetweenInclusive(0, 3) : 0);
    }

    public boolean hasAnalogOutputSignal(BlockState p_53054_) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState p_53061_, Level p_53062_, BlockPos p_53063_) {
        return p_53061_.getValue(LOCKED) ? 15 : 0;
    }

    public BlockState rotate(BlockState p_53068_, Rotation p_53069_) {
        return p_53068_.setValue(FACING, p_53069_.rotate(p_53068_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_53065_, Mirror p_53066_) {
        return p_53065_.rotate(p_53066_.getRotation(p_53065_.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53071_) {
        p_53071_.add(FACING, LOCKED, TYPE);
    }

    public RenderShape getRenderShape(BlockState p_49753_) {
        return RenderShape.MODEL;
    }

    public boolean isPathfindable(BlockState p_53056_, BlockGetter p_53057_, BlockPos p_53058_, PathComputationType p_53059_) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new VoidFrameBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof VoidFrameBlockEntity blockEntity1)
                blockEntity1.tick();
        };
    }
}
