package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.entities.SarcophagusBlockEntity;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.MathHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SarcophagusBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
    public static final BooleanProperty CUSHIONED = BooleanProperty.create("cushioned");
    protected static final VoxelShape NORTH_LID_AABB = Block.box(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape NORTH_FOOT_AABB = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D,
                    16.0D, 4.0D, 16.0D),
            Block.box(1.0D, 4.0D, 1.0D,
                    2.0D, 13.0D, 16.0D),
            Block.box(14.0D, 4.0D, 1.0D,
                    15.0D, 13.0D, 16.0D),
            Block.box(2.0D, 4.0D, 1.0D,
                    14.0D, 13.0D, 2.0D));
    protected static final VoxelShape NORTH_AABB = Shapes.or(NORTH_LID_AABB, NORTH_FOOT_AABB);
    private static final Map<Direction, VoxelShape> FOOT_CLOSED_AABB = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH_AABB, Direction.SOUTH, MathHelper.rotateVoxelShape(NORTH_AABB, Direction.SOUTH), Direction.WEST, MathHelper.rotateVoxelShape(NORTH_AABB, Direction.WEST), Direction.EAST, MathHelper.rotateVoxelShape(NORTH_AABB, Direction.EAST)));
    private static final Map<Direction, VoxelShape> FOOT_OPENED_AABB = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH_FOOT_AABB, Direction.SOUTH, MathHelper.rotateVoxelShape(NORTH_FOOT_AABB, Direction.SOUTH), Direction.WEST, MathHelper.rotateVoxelShape(NORTH_FOOT_AABB, Direction.WEST), Direction.EAST, MathHelper.rotateVoxelShape(NORTH_FOOT_AABB, Direction.EAST)));
    private final ResourceLocation texture;

    public SarcophagusBlock(Properties properties, ResourceLocation texture) {
        super(properties.noOcclusion());
        this.texture = texture;
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, BedPart.FOOT).setValue(OCCUPIED, false).setValue(FACING, Direction.NORTH).setValue(CUSHIONED, false));
    }

    public SarcophagusBlock(Properties properties, String material) {
        this(properties, Goety.location("textures/entity/sarcophagus/" + material + ".png"));
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction direction = getConnectedDirection(pState).getOpposite();
        if (pState.getValue(OCCUPIED)) {
            return FOOT_CLOSED_AABB.get(direction);
        } else {
            return FOOT_OPENED_AABB.get(direction);
        }
    }

    public static Direction getConnectedDirection(BlockState pState) {
        Direction direction = pState.getValue(FACING);
        return pState.getValue(PART) == BedPart.HEAD ? direction.getOpposite() : direction;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            BlockPos headPos = pos.relative(state.getValue(FACING));
            level.setBlock(headPos, state.setValue(PART, BedPart.HEAD), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction direction = ctx.getHorizontalDirection();
        BlockPos foot = ctx.getClickedPos();
        BlockPos head = foot.relative(direction);
        Level level = ctx.getLevel();
        if (level.getBlockState(head).canBeReplaced(ctx) && level.getWorldBorder().isWithinBounds(head)) {
            return this.defaultBlockState().setValue(FACING, direction).setValue(PART, BedPart.FOOT);
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
        if (facing == getNeighbourDirection(state.getValue(PART), state.getValue(FACING))) {
            return facingState.is(this) && facingState.getValue(PART) != state.getValue(PART)
                    ? state.setValue(OCCUPIED, facingState.getValue(OCCUPIED)).setValue(CUSHIONED, facingState.getValue(CUSHIONED))
                    : Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    private static Direction getNeighbourDirection(BedPart part, Direction facing) {
        return part == BedPart.FOOT ? facing : facing.getOpposite();
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            BedPart part = state.getValue(PART);
            if (part == BedPart.FOOT) {
                BlockPos headPos = pos.relative(getNeighbourDirection(part, state.getValue(FACING)));
                BlockState headState = level.getBlockState(headPos);
                if (headState.is(this) && headState.getValue(PART) == BedPart.HEAD) {
                    level.setBlock(headPos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, headPos, Block.getId(headState));
                }
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void spawnAfterBreak(BlockState p_222949_, ServerLevel p_222950_, BlockPos p_222951_, ItemStack p_222952_, boolean p_222953_) {
        if (p_222950_.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            if (p_222949_.getValue(CUSHIONED)) {
                SarcophagusBlockEntity blockEntity = null;
                BlockPos blockPos = p_222951_;
                if (p_222949_.getValue(PART) != BedPart.FOOT) {
                    blockPos = p_222951_.relative(p_222949_.getValue(FACING).getOpposite());
                }
                if (p_222950_.getBlockEntity(blockPos) instanceof SarcophagusBlockEntity sarcophagusBlock) {
                    blockEntity = sarcophagusBlock;
                }
                if (blockEntity != null) {
                    ItemStack drop = new ItemStack(ItemHelper.ITEM_BY_DYE.get(blockEntity.getColor()));
                    SarcophagusBlockEntity.dropItemStack(p_222950_, blockPos.getX(), blockPos.getY(), blockPos.getZ(), drop);
                }
            }
        }
        super.spawnAfterBreak(p_222949_, p_222950_, p_222951_, p_222952_, p_222953_);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.CONSUME;
        }

        if (state.getValue(PART) != BedPart.HEAD) {
            pos = pos.relative(state.getValue(FACING));
            state = level.getBlockState(pos);
            if (!state.is(this)) {
                return InteractionResult.CONSUME;
            }
        }

        SarcophagusBlockEntity blockEntity = null;
        BlockPos blockPos = pos;
        if (state.getValue(PART) != BedPart.FOOT) {
            blockPos = pos.relative(state.getValue(FACING).getOpposite());
        }
        if (level.getBlockEntity(blockPos) instanceof SarcophagusBlockEntity sarcophagusBlock) {
            blockEntity = sarcophagusBlock;
        }

        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() instanceof ShearsItem && state.getValue(CUSHIONED)) {
            level.setBlock(pos, state.setValue(CUSHIONED, false), 11);
            level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
                if (blockEntity != null) {
                    ItemStack drop = new ItemStack(ItemHelper.ITEM_BY_DYE.get(blockEntity.getColor()));
                    SarcophagusBlockEntity.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), drop);
                    blockEntity.setColor(DyeColor.WHITE);
                }
            }
            return InteractionResult.SUCCESS;
        } else if (itemStack.is(ItemTags.WOOL) && !state.getValue(CUSHIONED)) {
            if (blockEntity != null) {
                blockEntity.setColor(ItemHelper.DYE_BY_WOOL.get(itemStack.getItem()));
            }
            level.setBlock(pos, state.setValue(CUSHIONED, true), 11);
            level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        } else if (itemStack.getItem() instanceof DyeItem dyeItem
                && state.getValue(CUSHIONED)
                && blockEntity != null
                && blockEntity.getColor() != dyeItem.getDyeColor()) {
            blockEntity.setColor(dyeItem.getDyeColor());
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        } else if (!BedBlock.canSetSpawn(level)) {
            level.removeBlock(pos, false);
            BlockPos blockpos = pos.relative(state.getValue(FACING).getOpposite());
            if (level.getBlockState(blockpos).is(this)) {
                level.removeBlock(blockpos, false);
            }

            Vec3 vec3 = pos.getCenter();
            level.explode(null, level.damageSources().badRespawnPointExplosion(vec3), null, vec3, 5.0F, true, Level.ExplosionInteraction.BLOCK);
            return InteractionResult.SUCCESS;
        } else if (MainConfig.SarcophagusSleep.get()) {
            if (level.isDay()) {
                if (state.getValue(OCCUPIED)) {
                    player.displayClientMessage(Component.translatable("info.goety.sarcophagus.occupied"), true);
                    return InteractionResult.SUCCESS;
                }
                player.startSleepInBed(pos).ifLeft(problem -> {
                    if (problem.getMessage() != null) {
                        player.displayClientMessage(problem.getMessage(), true);
                    }
                });
            } else {
                player.displayClientMessage(Component.translatable("info.goety.sarcophagus.not_day"), true);
            }
        } else {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.setRespawnPosition(serverPlayer.level.dimension(), pos, serverPlayer.getYRot(), false, true);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isBed(BlockState state, BlockGetter level, BlockPos pos, @Nullable Entity player) {
        return true;
    }

    @Override
    public void setBedOccupied(BlockState state, Level level, BlockPos pos, LivingEntity sleeper, boolean occupied) {
        level.setBlock(pos, state.setValue(OCCUPIED, occupied), 11);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    public BlockState rotate(BlockState p_58140_, Rotation p_58141_) {
        return p_58140_.setValue(FACING, p_58141_.rotate(p_58140_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_58137_, Mirror p_58138_) {
        return p_58137_.rotate(p_58138_.getRotation(p_58137_.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, OCCUPIED, CUSHIONED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == BedPart.FOOT ? new SarcophagusBlockEntity(pos, state) : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide && state.getValue(PART) == BedPart.FOOT) {
            return (level1, blockPos, blockState, blockEntity) -> {
                if (blockEntity instanceof SarcophagusBlockEntity blockEntity1) {
                    SarcophagusBlockEntity.clientTick(level1, blockPos, blockState, blockEntity1);
                }
            };
        }
        return null;
    }
}
