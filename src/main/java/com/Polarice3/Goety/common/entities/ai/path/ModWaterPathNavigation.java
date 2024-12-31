package com.Polarice3.Goety.common.entities.ai.path;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

//Based on @l_ender's codes:https://github.com/lender544/new1.20.1/blob/master/src/main/java/com/github/L_Ender/cataclysm/entity/etc/path/SemiAquaticPathNavigator.java
public class ModWaterPathNavigation extends WaterBoundPathNavigation {
    public ModWaterPathNavigation(Mob mob, Level worldIn) {
        super(mob, worldIn);
    }

    protected @NotNull PathFinder createPathFinder(int p_179679_1_) {
        this.nodeEvaluator = new AmphibiousNodeEvaluator(true);
        return new PathFinder(this.nodeEvaluator, p_179679_1_);
    }

    protected boolean canUpdatePath() {
        return true;
    }

    protected boolean canMoveDirectly(Vec3 posVec31, Vec3 posVec32) {
        Vec3 vector3d = new Vec3(posVec32.x, posVec32.y + (double)this.mob.getBbHeight() * 0.5, posVec32.z);
        return this.level.clip(new ClipContext(posVec31, vector3d, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob)).getType() == HitResult.Type.MISS;
    }

    public boolean isStableDestination(BlockPos pos) {
        return !this.level.getBlockState(pos.below()).isAir();
    }
}
