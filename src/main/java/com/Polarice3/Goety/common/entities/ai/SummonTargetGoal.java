package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.entities.neutral.IOwned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;

import java.util.function.Predicate;

public class SummonTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<LivingEntity> {

    public SummonTargetGoal(Mob ownedEntity) {
        super(ownedEntity, LivingEntity.class, 5, true, false, predicate(ownedEntity));
    }

    public SummonTargetGoal(Mob ownedEntity, boolean pMustSee, boolean pMustReach) {
        super(ownedEntity, LivingEntity.class, 5, pMustSee, pMustReach, predicate(ownedEntity));
    }

    public boolean canUse() {
        boolean flag = super.canUse();
        if (this.mob instanceof Owned && ((Owned) this.mob).isHostile()){
            return !(this.target instanceof Enemy) && flag;
        }
        return flag;
    }

    public static Predicate<LivingEntity> predicate(LivingEntity livingEntity){
        if (livingEntity instanceof IOwned){
            IOwned ownedEntity = (IOwned) livingEntity;
            if (ownedEntity.getTrueOwner() instanceof Enemy || ownedEntity instanceof Enemy || (ownedEntity instanceof Owned && ((Owned)ownedEntity).isHostile())){
                return (entity) -> entity instanceof Player;
            } else {
                return (entity) ->
                        entity instanceof Enemy
                                && !(entity instanceof Creeper && entity.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && SpellConfig.MinionsAttackCreepers.get())
                                && !(entity instanceof NeutralMob && ((ownedEntity.getTrueOwner() != null && ((NeutralMob) entity).getTarget() != ownedEntity.getTrueOwner()) || ((NeutralMob) entity).getTarget() != ownedEntity))
                                && !(entity instanceof IOwned && ((IOwned) entity).getTrueOwner() == ownedEntity.getTrueOwner() && ownedEntity.getTrueOwner() != null);
            }
        } else {
            return null;
        }
    }
}
