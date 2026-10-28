package com.Polarice3.Goety.common.entities.ai.path;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class LargeMobNavigation extends GroundPathNavigation {

    public LargeMobNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        this.nodeEvaluator = new LargeWalkNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
    }

    public static class LargeWalkNodeEvaluator extends WalkNodeEvaluator {

        @Override
        public Node getStart() {
            Node start = super.getStart();
            if (start == null || this.entityWidth <= 1) {
                return start;
            }
            double half = this.entityWidth / 2.0D;
            int baseX = Mth.floor(this.mob.getX() - half + 0.5D);
            int baseZ = Mth.floor(this.mob.getZ() - half + 0.5D);
            int bestX = 0, bestZ = 0;
            double bestDist = Double.MAX_VALUE;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    int x = baseX + dx;
                    int z = baseZ + dz;
                    if (this.fits(x, start.y, z)) {
                        double cx = x + half - this.mob.getX();
                        double cz = z + half - this.mob.getZ();
                        double dist = cx * cx + cz * cz;
                        if (dist < bestDist) {
                            bestDist = dist;
                            bestX = x;
                            bestZ = z;
                        }
                    }
                }
            }
            return bestDist < Double.MAX_VALUE ? this.getStartNode(new BlockPos(bestX, start.y, bestZ)) : start;
        }

        private boolean fits(int x, int y, int z) {
            BlockPathTypes type = this.getBlockPathType(this.level, x, y, z, this.mob);
            return type != BlockPathTypes.OPEN && this.mob.getPathfindingMalus(type) >= 0.0F;
        }
    }
}