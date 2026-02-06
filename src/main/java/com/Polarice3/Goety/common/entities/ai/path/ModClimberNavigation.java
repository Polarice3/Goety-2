package com.Polarice3.Goety.common.entities.ai.path;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

//Based on @EEEAB's code: https://github.com/meeprdib/EEEABsMobs/blob/master/src/main/java/com/eeeab/eeeabsmobs/sever/entity/ai/navigate/EMWallClimberNavigation.java
public class ModClimberNavigation extends GroundPathNavigation {

    public ModClimberNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity target = this.mob.getTarget();
        if (this.mob instanceof IServant servant) {
            if (servant instanceof RaiderServant raiderServant) {
                if (raiderServant.getLeader() != null) {
                    target = raiderServant.getLeader();
                }
            }
            if (servant.isFollowing() && servant.getTrueOwner() != null) {
                target = servant.getTrueOwner();
            }
        }
        if (target != null) {
            if ((target.getY() - 3) >= this.mob.getY()) {
                BlockHitResult result = this.level.clip(new ClipContext(this.mob.getEyePosition(), this.mob.getEyePosition().add(this.mob.getViewVector(1.0F).scale(1.0F)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this.mob));
                BlockState blockState = this.level.getBlockState(result.getBlockPos());
                if (blockState.canOcclude() && this.getRequiredState().test(blockState)) {
                    Vec3 vec3 = result.getBlockPos().getCenter();
                    this.mob.getMoveControl().setWantedPosition(vec3.x, this.getGroundY(vec3), vec3.z, this.speedModifier);
                }
            }
        }
    }

    public Predicate<BlockState> getRequiredState() {
        return blockState -> true;
    }
}
