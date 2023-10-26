package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.client.inventory.container.DarkAnvilMenu;
import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class DarkAnvilBlock extends FallingBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final VoxelShape BASE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
   private static final VoxelShape X_LEG1 = Block.box(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D);
   private static final VoxelShape X_LEG2 = Block.box(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D);
   private static final VoxelShape X_TOP = Block.box(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D);
   private static final VoxelShape Z_LEG1 = Block.box(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D);
   private static final VoxelShape Z_LEG2 = Block.box(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D);
   private static final VoxelShape Z_TOP = Block.box(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D);
   private static final VoxelShape X_AXIS_AABB = Shapes.or(BASE, X_LEG1, X_LEG2, X_TOP);
   private static final VoxelShape Z_AXIS_AABB = Shapes.or(BASE, Z_LEG1, Z_LEG2, Z_TOP);
   private static final Component CONTAINER_TITLE = Component.translatable("container.repair");

   public DarkAnvilBlock() {
      super(Properties.of()
              .mapColor(MapColor.COLOR_BLACK)
              .requiresCorrectToolForDrops()
              .randomTicks()
              .strength(5.0F, 1200.0F)
              .sound(SoundType.ANVIL)
              .pushReaction(PushReaction.BLOCK));
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
   }

   public boolean isRandomlyTicking(BlockState p_51780_) {
      return !p_51780_.is(ModBlocks.DARK_ANVIL.get());
   }

   public void randomTick(BlockState p_221000_, ServerLevel p_221001_, BlockPos p_221002_, RandomSource p_221003_) {
      if (!p_221000_.is(ModBlocks.DARK_ANVIL.get())) {
         if (p_221001_.getBlockEntity(p_221002_.below()) instanceof CursedCageBlockEntity cageBlockEntity) {
            if (cageBlockEntity.getSouls() >= MainConfig.DarkAnvilSoulCost.get()) {
               cageBlockEntity.decreaseSouls(MainConfig.DarkAnvilSoulCost.get());
               ModNetwork.sendToALL(new SPlayWorldSoundPacket(p_221002_, SoundEvents.FIRE_AMBIENT, 1.0F + p_221001_.random.nextFloat(), p_221001_.random.nextFloat() * 0.7F + 0.3F));
               DarkAnvilBlock.generateManyParticles(p_221001_, p_221002_);
               BlockState repaired = DarkAnvilBlock.repair(p_221000_);
               p_221001_.setBlock(p_221002_, repaired, 2);
            }
         }
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_48781_) {
      return this.defaultBlockState().setValue(FACING, p_48781_.getHorizontalDirection().getClockWise());
   }

   public InteractionResult use(BlockState p_48804_, Level p_48805_, BlockPos p_48806_, Player p_48807_, InteractionHand p_48808_, BlockHitResult p_48809_) {
      if (p_48805_.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         p_48807_.openMenu(p_48804_.getMenuProvider(p_48805_, p_48806_));
         p_48807_.awardStat(Stats.INTERACT_WITH_ANVIL);
         return InteractionResult.CONSUME;
      }
   }

   @Nullable
   public MenuProvider getMenuProvider(BlockState p_48821_, Level p_48822_, BlockPos p_48823_) {
      return new SimpleMenuProvider((p_48785_, p_48786_, p_48787_) -> {
         return new DarkAnvilMenu(p_48785_, p_48786_, ContainerLevelAccess.create(p_48822_, p_48823_));
      }, CONTAINER_TITLE);
   }

   public VoxelShape getShape(BlockState p_48816_, BlockGetter p_48817_, BlockPos p_48818_, CollisionContext p_48819_) {
      Direction direction = p_48816_.getValue(FACING);
      return direction.getAxis() == Direction.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
   }

   protected void falling(FallingBlockEntity p_48779_) {
      p_48779_.setHurtsEntities(2.0F, 40);
   }

   public void onLand(Level p_48793_, BlockPos p_48794_, BlockState p_48795_, BlockState p_48796_, FallingBlockEntity p_48797_) {
      if (!p_48797_.isSilent()) {
         p_48793_.levelEvent(1031, p_48794_, 0);
      }

   }

   public void onBrokenAfterFall(Level p_152053_, BlockPos p_152054_, FallingBlockEntity p_152055_) {
      if (!p_152055_.isSilent()) {
         p_152053_.levelEvent(1029, p_152054_, 0);
      }

   }

   public DamageSource getFallDamageSource(Entity p_254036_) {
      return p_254036_.damageSources().anvil(p_254036_);
   }

   @Nullable
   public static BlockState damage(BlockState p_48825_) {
      if (p_48825_.is(ModBlocks.DARK_ANVIL.get())) {
         return ModBlocks.CHIPPED_DARK_ANVIL.get().defaultBlockState().setValue(FACING, p_48825_.getValue(FACING));
      } else {
         return p_48825_.is(ModBlocks.CHIPPED_DARK_ANVIL.get()) ? ModBlocks.DAMAGED_DARK_ANVIL.get().defaultBlockState().setValue(FACING, p_48825_.getValue(FACING)) : null;
      }
   }

   public static BlockState repair(BlockState blockState){
      if (blockState.is(ModBlocks.DAMAGED_DARK_ANVIL.get())) {
         return ModBlocks.CHIPPED_DARK_ANVIL.get().defaultBlockState().setValue(FACING, blockState.getValue(FACING));
      } else {
         return ModBlocks.DARK_ANVIL.get().defaultBlockState().setValue(FACING, blockState.getValue(FACING));
      }
   }

   public BlockState rotate(BlockState p_48811_, Rotation p_48812_) {
      return p_48811_.setValue(FACING, p_48812_.rotate(p_48811_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_48814_) {
      p_48814_.add(FACING);
   }

   public boolean isPathfindable(BlockState p_48799_, BlockGetter p_48800_, BlockPos p_48801_, PathComputationType p_48802_) {
      return false;
   }

   public int getDustColor(BlockState p_48827_, BlockGetter p_48828_, BlockPos p_48829_) {
      return p_48827_.getMapColor(p_48828_, p_48829_).col;
   }

   public static void generateManyParticles(ServerLevel serverLevel, BlockPos blockPos){
      for(int k = 0; k < 20; ++k) {
         double d9 = (double)blockPos.getX() + 0.5D + (serverLevel.random.nextDouble() - 0.5D) * 2.0D;
         double d13 = (double)blockPos.getY() + 0.5D + (serverLevel.random.nextDouble() - 0.5D) * 2.0D;
         double d19 = (double)blockPos.getZ() + 0.5D + (serverLevel.random.nextDouble() - 0.5D) * 2.0D;
         serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, d9, d13, d19, 1, 0.0D, 0.0D, 0.0D, 0);
      }
   }
}