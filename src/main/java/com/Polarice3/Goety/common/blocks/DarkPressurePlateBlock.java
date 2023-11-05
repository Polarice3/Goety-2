package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class DarkPressurePlateBlock extends BasePressurePlateBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   public DarkPressurePlateBlock(Properties p_55254_) {
      super(p_55254_);
      this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.FALSE));
   }

   protected int getSignalForState(BlockState p_55270_) {
      return p_55270_.getValue(POWERED) ? 15 : 0;
   }

   protected BlockState setSignalForState(BlockState p_55259_, int p_55260_) {
      return p_55259_.setValue(POWERED, p_55260_ > 0);
   }

   protected void playOnSound(LevelAccessor p_55256_, BlockPos p_55257_) {
      p_55256_.playSound((Player)null, p_55257_, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.8F);
   }

   protected void playOffSound(LevelAccessor p_55267_, BlockPos p_55268_) {
      p_55267_.playSound((Player)null, p_55268_, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.7F);
   }

   protected int getSignalStrength(Level p_55264_, BlockPos p_55265_) {
      AABB aabb = TOUCH_AABB.move(p_55265_);
      List<? extends Entity> list;
      list = p_55264_.getEntitiesOfClass(Player.class, aabb);

      if (!list.isEmpty()) {
         for(Entity entity : list) {
            if (!entity.isIgnoringBlockTriggers()) {
               return 15;
            }
         }
      }

      return 0;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55262_) {
      p_55262_.add(POWERED);
   }
}