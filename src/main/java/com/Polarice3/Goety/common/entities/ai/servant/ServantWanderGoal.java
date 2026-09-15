package com.Polarice3.Goety.common.entities.ai.servant;

import com.Polarice3.Goety.api.entities.ally.IServant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ServantWanderGoal<T extends PathfinderMob & IServant> extends RandomStrollGoal {
    public final T summonedEntity;
    protected final float probability;

    public ServantWanderGoal(T entity, double speedModifier) {
        this(entity, speedModifier, 0.001F);
    }

    public ServantWanderGoal(T entity, double speedModifier, float probability) {
        this(entity, speedModifier, 120, probability);
    }

    public ServantWanderGoal(T entity, double speedModifier, int interval, float probability) {
        super(entity, speedModifier, interval, false);
        this.summonedEntity = entity;
        this.probability = probability;
    }

    public boolean canUse() {
        if (super.canUse()){
            return (!this.summonedEntity.isStaying() && !this.summonedEntity.isCommanded() || this.summonedEntity.getTrueOwner() == null) && !(this.summonedEntity.getNavigation() instanceof WaterBoundPathNavigation);
        } else {
            return false;
        }
    }

    @Nullable
    protected Vec3 getPosition() {
        if (this.summonedEntity.isGuardingArea()){
            return randomBoundPos();
        } else if (this.mob.isInWaterOrBubble()) {
            Vec3 vec3 = this.landRandomPos(15, 7);
            return vec3 == null ? this.defaultRandomPos() : vec3;
        } else {
            return this.mob.getRandom().nextFloat() >= this.probability ? this.landRandomPos(10, 7) : this.defaultRandomPos();
        }
    }

    public Vec3 defaultRandomPos(){
        return super.getPosition();
    }

    @Nullable
    public Vec3 landRandomPos(int xz, int y){
        if (this.summonedEntity.getTrueOwner() != null
                && this.summonedEntity.isFollowing()){
            Vec3 vec3 = null;

            for (int i = 0; i < 10; ++i){
                BlockPos blockPos = this.summonedEntity.getTrueOwner().blockPosition()
                        .offset(this.summonedEntity.getRandom().nextIntBetweenInclusive(-xz, xz),
                                this.summonedEntity.getRandom().nextIntBetweenInclusive(-y, y),
                                this.summonedEntity.getRandom().nextIntBetweenInclusive(-xz, xz));
                BlockPos blockPos1 = LandRandomPos.movePosUpOutOfSolid(this.summonedEntity, blockPos);
                if (blockPos1 != null){
                    vec3 = Vec3.atBottomCenterOf(blockPos1);
                    break;
                }
            }

            return vec3;
        }
        return LandRandomPos.getPos(this.mob, xz, y);
    }

    public Vec3 randomBoundPos(){
        Vec3 vec3 = null;
        int range = IServant.GUARDING_RANGE / 2;

        for (int i = 0; i < 10; ++i){
            BlockPos blockPos = this.summonedEntity.getBoundPos()
                    .offset(this.summonedEntity.getRandom().nextIntBetweenInclusive(-range, range),
                            this.summonedEntity.getRandom().nextIntBetweenInclusive(-range, range),
                            this.summonedEntity.getRandom().nextIntBetweenInclusive(-range, range));
            BlockPos blockPos1 = LandRandomPos.movePosUpOutOfSolid(this.summonedEntity, blockPos);
            if (blockPos1 != null){
                vec3 = Vec3.atBottomCenterOf(blockPos1);
                break;
            }
        }

        return vec3;
    }
}
