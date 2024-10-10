package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.common.entities.hostile.cultists.Cultist;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class StrollAroundLeaderGoal extends WaterAvoidingRandomStrollGoal {

   public StrollAroundLeaderGoal(PathfinderMob p_25987_, double p_25988_) {
      super(p_25987_, p_25988_);
   }

   @Nullable
   protected Vec3 getPosition() {
      if (this.mob instanceof Cultist cultist && cultist.getLeader() != null && cultist.getLeader().isAlive()){
         return randomBoundPos();
      } else {
         return super.getPosition();
      }
   }

   public Vec3 randomBoundPos(){
      Vec3 vec3 = null;
      int range = 16;

      if (this.mob instanceof Cultist cultist) {
         if (cultist.getLeader() != null) {
            for (int i = 0; i < 10; ++i) {
               BlockPos blockPos = cultist.getLeader().blockPosition()
                       .offset(cultist.getRandom().nextIntBetweenInclusive(-range, range),
                               cultist.getRandom().nextIntBetweenInclusive(-range, range),
                               cultist.getRandom().nextIntBetweenInclusive(-range, range));
               BlockPos blockPos1 = LandRandomPos.movePosUpOutOfSolid(cultist, blockPos);
               if (blockPos1 != null) {
                  if (cultist.getNavigation().isStableDestination(blockPos1)) {
                     vec3 = Vec3.atBottomCenterOf(blockPos1);
                     break;
                  }
               }
            }
         }
      }

      return vec3;
   }
}