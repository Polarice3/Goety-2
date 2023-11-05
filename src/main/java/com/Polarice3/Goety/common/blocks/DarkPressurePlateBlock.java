package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
      super(p_55254_, ModBlockSetType.HAUNTED);
      this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.FALSE));
   }

   protected int getSignalForState(BlockState p_55270_) {
      return p_55270_.getValue(POWERED) ? 15 : 0;
   }

   protected BlockState setSignalForState(BlockState p_55259_, int p_55260_) {
      return p_55259_.setValue(POWERED, p_55260_ > 0);
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