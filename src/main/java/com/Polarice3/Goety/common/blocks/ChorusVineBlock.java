package com.Polarice3.Goety.common.blocks;

import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChorusVineBlock extends Block implements net.minecraftforge.common.IForgeShearable {
   public static final BooleanProperty UP = PipeBlock.UP;
   public static final BooleanProperty NORTH = PipeBlock.NORTH;
   public static final BooleanProperty EAST = PipeBlock.EAST;
   public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
   public static final BooleanProperty WEST = PipeBlock.WEST;
   public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((p_57886_) -> {
      return p_57886_.getKey() != Direction.DOWN;
   }).collect(Util.toMap());
   protected static final float AABB_OFFSET = 1.0F;
   private static final VoxelShape UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
   private static final VoxelShape EAST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
   private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
   private final Map<BlockState, VoxelShape> shapesCache;

   public ChorusVineBlock() {
      super(BlockBehaviour.Properties.of()
              .mapColor(MapColor.COLOR_YELLOW)
              .replaceable()
              .noCollission()
              .randomTicks()
              .strength(0.2F)
              .sound(SoundType.CHERRY_LEAVES)
              .pushReaction(PushReaction.DESTROY));
      this.registerDefaultState(this.stateDefinition.any().setValue(UP, false).setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false));
      this.shapesCache = ImmutableMap.copyOf(this.stateDefinition.getPossibleStates().stream().collect(Collectors.toMap(Function.identity(), ChorusVineBlock::calculateShape)));
   }

   private static VoxelShape calculateShape(BlockState p_57906_) {
      VoxelShape voxelshape = Shapes.empty();
      if (p_57906_.getValue(UP)) {
         voxelshape = UP_AABB;
      }

      if (p_57906_.getValue(NORTH)) {
         voxelshape = Shapes.or(voxelshape, NORTH_AABB);
      }

      if (p_57906_.getValue(SOUTH)) {
         voxelshape = Shapes.or(voxelshape, SOUTH_AABB);
      }

      if (p_57906_.getValue(EAST)) {
         voxelshape = Shapes.or(voxelshape, EAST_AABB);
      }

      if (p_57906_.getValue(WEST)) {
         voxelshape = Shapes.or(voxelshape, WEST_AABB);
      }

      return voxelshape.isEmpty() ? Shapes.block() : voxelshape;
   }

   public VoxelShape getShape(BlockState p_57897_, BlockGetter p_57898_, BlockPos p_57899_, CollisionContext p_57900_) {
      return this.shapesCache.get(p_57897_);
   }

   public boolean propagatesSkylightDown(BlockState p_181239_, BlockGetter p_181240_, BlockPos p_181241_) {
      return true;
   }

   public boolean canSurvive(BlockState p_57861_, LevelReader p_57862_, BlockPos p_57863_) {
      return this.hasFaces(this.getUpdatedState(p_57861_, p_57862_, p_57863_));
   }

   private boolean hasFaces(BlockState p_57908_) {
      return this.countFaces(p_57908_) > 0;
   }

   private int countFaces(BlockState p_57910_) {
      int i = 0;

      for(BooleanProperty booleanproperty : PROPERTY_BY_DIRECTION.values()) {
         if (p_57910_.getValue(booleanproperty)) {
            ++i;
         }
      }

      return i;
   }

   private boolean canSupportAtFace(BlockGetter p_57888_, BlockPos p_57889_, Direction p_57890_) {
      if (p_57890_ == Direction.DOWN) {
         return false;
      } else {
         BlockPos blockpos = p_57889_.relative(p_57890_);
         if (isAcceptableNeighbour(p_57888_, blockpos, p_57890_)) {
            return true;
         } else if (p_57890_.getAxis() == Direction.Axis.Y) {
            return false;
         } else {
            BooleanProperty booleanproperty = PROPERTY_BY_DIRECTION.get(p_57890_);
            BlockState blockstate = p_57888_.getBlockState(p_57889_.below());
            return blockstate.is(this) && blockstate.getValue(booleanproperty);
         }
      }
   }

   public static boolean isAcceptableNeighbour(BlockGetter p_57854_, BlockPos p_57855_, Direction p_57856_) {
      return MultifaceBlock.canAttachTo(p_57854_, p_57856_, p_57855_, p_57854_.getBlockState(p_57855_));
   }

   private BlockState getUpdatedState(BlockState p_57902_, BlockGetter p_57903_, BlockPos p_57904_) {
      BlockPos blockpos = p_57904_.below();
      if (p_57902_.getValue(UP)) {
         p_57902_ = p_57902_.setValue(UP, isAcceptableNeighbour(p_57903_, blockpos, Direction.DOWN));
      }

      BlockState blockstate = null;

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BooleanProperty booleanproperty = getPropertyForFace(direction);
         if (p_57902_.getValue(booleanproperty)) {
            boolean flag = this.canSupportAtFace(p_57903_, p_57904_, direction);
            if (!flag) {
               if (blockstate == null) {
                  blockstate = p_57903_.getBlockState(blockpos);
               }

               flag = blockstate.is(this) && blockstate.getValue(booleanproperty);
            }

            p_57902_ = p_57902_.setValue(booleanproperty, flag);
         }
      }

      return p_57902_;
   }

   public BlockState updateShape(BlockState p_57875_, Direction p_57876_, BlockState p_57877_, LevelAccessor p_57878_, BlockPos p_57879_, BlockPos p_57880_) {
      if (p_57876_ == Direction.UP) {
         return super.updateShape(p_57875_, p_57876_, p_57877_, p_57878_, p_57879_, p_57880_);
      } else {
         BlockState blockstate = this.getUpdatedState(p_57875_, p_57878_, p_57879_);
         return !this.hasFaces(blockstate) ? Blocks.AIR.defaultBlockState() : blockstate;
      }
   }

   public void randomTick(BlockState p_222655_, ServerLevel p_222656_, BlockPos p_222657_, RandomSource p_222658_) {
      if (p_222656_.getGameRules().getBoolean(GameRules.RULE_DO_VINES_SPREAD)) {
         if (p_222656_.random.nextInt(4) == 0 && p_222656_.isLoaded(p_222657_)) {
            if (p_222657_.getY() > p_222656_.getMinBuildHeight()) {
               BlockPos blockpos1 = p_222657_.above();
               BlockState blockstate = p_222656_.getBlockState(blockpos1);
               if (blockstate.isAir() || blockstate.is(this)) {
                  BlockState blockstate1 = blockstate.isAir() ? this.defaultBlockState() : blockstate;
                  BlockState blockstate2 = this.copyRandomFaces(p_222655_, blockstate1, p_222658_);
                  if (blockstate1 != blockstate2 && this.hasHorizontalConnection(blockstate2)) {
                     p_222656_.setBlock(blockpos1, blockstate2, 2);
                  }
               }
            }
         }
      }
   }

   private BlockState copyRandomFaces(BlockState p_222651_, BlockState p_222652_, RandomSource p_222653_) {
      for(Direction direction : Direction.Plane.HORIZONTAL) {
         if (p_222653_.nextBoolean()) {
            BooleanProperty booleanproperty = getPropertyForFace(direction);
            if (p_222651_.getValue(booleanproperty)) {
               p_222652_ = p_222652_.setValue(booleanproperty, true);
            }
         }
      }

      return p_222652_;
   }

   private boolean hasHorizontalConnection(BlockState p_57912_) {
      return p_57912_.getValue(NORTH) || p_57912_.getValue(EAST) || p_57912_.getValue(SOUTH) || p_57912_.getValue(WEST);
   }

   public boolean canBeReplaced(BlockState p_57858_, BlockPlaceContext p_57859_) {
      BlockState blockstate = p_57859_.getLevel().getBlockState(p_57859_.getClickedPos());
      if (blockstate.is(this)) {
         return this.countFaces(blockstate) < PROPERTY_BY_DIRECTION.size();
      } else {
         return super.canBeReplaced(p_57858_, p_57859_);
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_57849_) {
      BlockState blockstate = p_57849_.getLevel().getBlockState(p_57849_.getClickedPos());
      boolean flag = blockstate.is(this);
      BlockState blockstate1 = flag ? blockstate : this.defaultBlockState();

      for(Direction direction : p_57849_.getNearestLookingDirections()) {
         if (direction != Direction.DOWN) {
            BooleanProperty booleanproperty = getPropertyForFace(direction);
            boolean flag1 = flag && blockstate.getValue(booleanproperty);
            if (!flag1 && this.canSupportAtFace(p_57849_.getLevel(), p_57849_.getClickedPos(), direction)) {
               return blockstate1.setValue(booleanproperty, true);
            }
         }
      }

      return flag ? blockstate1 : null;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_57882_) {
      p_57882_.add(UP, NORTH, EAST, SOUTH, WEST);
   }

   public BlockState rotate(BlockState p_57868_, Rotation p_57869_) {
       return switch (p_57869_) {
           case CLOCKWISE_180 ->
                   p_57868_.setValue(NORTH, p_57868_.getValue(SOUTH)).setValue(EAST, p_57868_.getValue(WEST)).setValue(SOUTH, p_57868_.getValue(NORTH)).setValue(WEST, p_57868_.getValue(EAST));
           case COUNTERCLOCKWISE_90 ->
                   p_57868_.setValue(NORTH, p_57868_.getValue(EAST)).setValue(EAST, p_57868_.getValue(SOUTH)).setValue(SOUTH, p_57868_.getValue(WEST)).setValue(WEST, p_57868_.getValue(NORTH));
           case CLOCKWISE_90 ->
                   p_57868_.setValue(NORTH, p_57868_.getValue(WEST)).setValue(EAST, p_57868_.getValue(NORTH)).setValue(SOUTH, p_57868_.getValue(EAST)).setValue(WEST, p_57868_.getValue(SOUTH));
           default -> p_57868_;
       };
   }

   public BlockState mirror(BlockState p_57865_, Mirror p_57866_) {
       return switch (p_57866_) {
           case LEFT_RIGHT ->
                   p_57865_.setValue(NORTH, p_57865_.getValue(SOUTH)).setValue(SOUTH, p_57865_.getValue(NORTH));
           case FRONT_BACK -> p_57865_.setValue(EAST, p_57865_.getValue(WEST)).setValue(WEST, p_57865_.getValue(EAST));
           default -> super.mirror(p_57865_, p_57866_);
       };
   }

   public static BooleanProperty getPropertyForFace(Direction p_57884_) {
      return PROPERTY_BY_DIRECTION.get(p_57884_);
   }
}
