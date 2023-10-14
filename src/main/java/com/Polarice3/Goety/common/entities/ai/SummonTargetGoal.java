package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.entities.neutral.IOwned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;

import java.util.function.Predicate;

public class SummonTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {

    public SummonTargetGoal(Mob ownedEntity) {
        super(ownedEntity, LivingEntity.class, 5, true, false, predicate(ownedEntity));
    }

    public SummonTargetGoal(Mob ownedEntity, boolean pMustSee, boolean pMustReach) {
        super(ownedEntity, LivingEntity.class, 5, pMustSee, pMustReach, predicate(ownedEntity));
    }

    public boolean canUse() {
        boolean flag = super.canUse();
        if (this.mob instanceof Owned owned){
            if (owned.getTrueOwner() != null && this.target == owned.getTrueOwner()){
                return false;
            }
            if (owned.isHostile()){
                return !(this.target instanceof Enemy) && flag;
            }
        }
        return flag;
    }

    public static Predicate<LivingEntity> predicate(LivingEntity attacker){
        if (attacker instanceof IOwned ownedEntity){
            if (ownedEntity.getTrueOwner() instanceof Enemy || ownedEntity instanceof Enemy || (ownedEntity instanceof Owned && ((Owned)ownedEntity).isHostile())){
                return (target) -> target instanceof Player player && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player);
            } else {
                return (target) ->
                        (target instanceof Enemy
                                && !(target instanceof Creeper && target.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && SpellConfig.MinionsAttackCreepers.get())
                                && !(target instanceof NeutralMob && ((ownedEntity.getTrueOwner() != null && ((NeutralMob) target).getTarget() != ownedEntity.getTrueOwner()) || ((NeutralMob) target).getTarget() != ownedEntity))
                                && !(target instanceof IOwned && ownedEntity.getTrueOwner() != null && ((IOwned) target).getTrueOwner() == ownedEntity.getTrueOwner()))
                        || (target instanceof Owned owned
                                && !(ownedEntity instanceof Owned && ((Owned)ownedEntity).isHostile())
                                && owned.isHostile())
                        || (ownedEntity.getTrueOwner() instanceof Player player
                                && ((!SEHelper.getGrudgeEntities(player).isEmpty() && SEHelper.getGrudgeEntities(player).contains(target))
                                || (!SEHelper.getGrudgeEntityTypes(player).isEmpty() && SEHelper.getGrudgeEntityTypes(player).contains(target.getType()))));
            }
        } else {
            return null;
        }
    }
}
