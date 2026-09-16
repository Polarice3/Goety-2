package com.Polarice3.Goety.common.entities.ai.servant;

import com.Polarice3.Goety.api.entities.ally.IServant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ServantWaterWanderGoal<T extends PathfinderMob & IServant> extends RandomStrollGoal {
    public final T summonedEntity;

    public ServantWaterWanderGoal(T entity) {
        this(entity, 1.0D);
    }

    public ServantWaterWanderGoal(T entity, double speedModifier) {
        this(entity, speedModifier, 120);
    }

    public ServantWaterWanderGoal(T entity, double speedModifier, int interval) {
        super(entity, speedModifier, interval, false);
        this.summonedEntity = entity;
    }

    @Nullable
    protected Vec3 getPosition() {
        if (this.summonedEntity.isGuardingArea()){
            return randomBoundPos();
        }
        return super.getPosition();
    }

    public Vec3 randomBoundPos(){
        Vec3 vec3 = null;
        int range = IServant.GUARDING_RANGE / 2;

        for (int i = 0; i < 10; ++i){
            BlockPos blockPos = this.summonedEntity.getBoundPos()
                    .offset(this.summonedEntity.getRandom().nextIntBetweenInclusive(-range, range),
                            this.summonedEntity.getRandom().nextIntBetweenInclusive(-range, range),
                            this.summonedEntity.getRandom().nextIntBetweenInclusive(-range, range));
            if (this.summonedEntity.getNavigation() instanceof WaterBoundPathNavigation){
                if (GoalUtils.isWater(this.summonedEntity, blockPos)){
                    vec3 = Vec3.atBottomCenterOf(blockPos);
                    break;
                }
            } else {
                BlockPos blockPos1 = LandRandomPos.movePosUpOutOfSolid(this.summonedEntity, blockPos);
                if (blockPos1 != null){
                    vec3 = Vec3.atBottomCenterOf(blockPos1);
                    break;
                }
            }
        }

        return vec3;
    }

    public boolean canUse() {
        if (super.canUse()){
            return (!this.summonedEntity.isStaying() && !this.summonedEntity.isCommanded()) || this.summonedEntity.getTrueOwner() == null;
        } else {
            return false;
        }
    }
}
