package com.Polarice3.Goety.common.entities.ai.servant;

import com.Polarice3.Goety.api.entities.ally.IServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class ServantNaturalAttackGoal<T extends LivingEntity, M extends PathfinderMob & IServant> extends NearestAttackableTargetGoal<T> {
    protected M summoned;

    public ServantNaturalAttackGoal(M summoned, Class<T> tClass) {
        this(summoned, tClass, 10, true, null);
    }

    public ServantNaturalAttackGoal(M summoned, Class<T> tClass, boolean pMustSee) {
        this(summoned, tClass, 10, pMustSee, null);
    }

    public ServantNaturalAttackGoal(M summoned, Class<T> tClass, boolean pMustSee, @Nullable Predicate<LivingEntity> predicate) {
        this(summoned, tClass, 10, pMustSee, predicate);
    }

    public ServantNaturalAttackGoal(M summoned, Class<T> tClass, int time, boolean pMustSee, @Nullable Predicate<LivingEntity> predicate) {
        this(summoned, tClass, time, pMustSee, false, predicate);
    }

    public ServantNaturalAttackGoal(M summoned, Class<T> tClass, int time, boolean pMustSee, boolean pMustReach, Predicate<LivingEntity> predicate) {
        super(summoned, tClass, time, pMustSee, pMustReach, predicate);
        this.summoned = summoned;
    }

    public boolean canUse() {
        return super.canUse() && this.summoned.isNatural() && this.summoned.getTrueOwner() == null && this.target != null;
    }
}
