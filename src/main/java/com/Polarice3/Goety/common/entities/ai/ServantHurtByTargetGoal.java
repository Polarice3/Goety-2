package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

public class ServantHurtByTargetGoal extends HurtByTargetGoal {
    public ServantHurtByTargetGoal(PathfinderMob p_26039_, Class<?>... p_26040_) {
        super(p_26039_, p_26040_);
    }

    @Override
    public boolean canUse() {
        LivingEntity attacker = this.mob.getLastHurtByMob();
        if (this.mob instanceof IOwned ownedMob && attacker instanceof IOwned ownedAttacker) {
            if (MobUtil.ownerStack(ownedMob, ownedAttacker)) {
                return false;
            }
            if (ownedMob.isAllyWith(attacker)) {
                return false;
            }
            if (ownedMob.getOwnerId() != null && ownedMob.getOwnerId().equals(ownedAttacker.getOwnerId())) {
                return false;
            }
        }
        return super.canUse();
    }
}
