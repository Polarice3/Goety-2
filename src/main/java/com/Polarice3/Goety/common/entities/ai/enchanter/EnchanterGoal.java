package com.Polarice3.Goety.common.entities.ai.enchanter;

import com.Polarice3.Goety.api.entities.IEnchanter;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Enemy;

import java.util.ArrayList;
import java.util.List;

public abstract class EnchanterGoal<T extends PathfinderMob & IEnchanter> extends Goal {
    public T enchanter;
    public List<LivingEntity> cachedAllies = new ArrayList<>();
    public long nextAllyScan = 0L;

    public EnchanterGoal(T enchanter) {
        this.enchanter = enchanter;
    }

    public boolean isAlliedTarget(Entity target) {
        if (this.enchanter instanceof OwnableEntity ownable && ownable.getOwner() != null) {
            return (target == ownable.getOwner() || MobUtil.getOwner(target) == ownable.getOwner() || (MobUtil.areAllies(ownable.getOwner(), target)));
        } else if ((this.enchanter instanceof Enemy || this.enchanter instanceof IOwned owned && owned.isHostile()) && target instanceof Enemy) {
            if (target instanceof Mob mob) {
                return mob.getTarget() != this.enchanter && this.enchanter.getTarget() != mob && !MobUtil.areAllies(this.enchanter, mob.getTarget());
            }
        }
        return MobUtil.areAllies(this.enchanter, target);
    }

    public List<LivingEntity> getAllies() {
        long now = this.enchanter.level.getGameTime();
        if (now < this.nextAllyScan) {
            this.cachedAllies = this.enchanter.level.getEntitiesOfClass(
                    LivingEntity.class,
                    this.enchanter.getBoundingBox().inflate(13),
                    ally -> !(ally instanceof IEnchanter)
                            && !ally.getType().is(ModTags.EntityTypes.UNENCHANTABLE)
                            && !ally.isDeadOrDying()
                            && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(ally)
                            && !ally.hasEffect(GoetyEffects.ENCHANTED.get())
                            && ally.getAttribute(Attributes.ATTACK_DAMAGE) != null
                            && this.isAlliedTarget(ally));
        }
        this.nextAllyScan = now + 20L;
        return this.cachedAllies;
    }
}
