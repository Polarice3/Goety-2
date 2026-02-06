package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.common.entities.ally.illager.raider.AllyIrk;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;

public class Irk extends AllyIrk implements Enemy {

    public Irk(EntityType<? extends Irk> p_i50190_1_, Level p_i50190_2_) {
        super(p_i50190_1_, p_i50190_2_);
        this.setHostile(true);
    }

    public int xpReward(){
        return 3;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
    }

    public boolean hurt(DamageSource source, float amount) {
        Entity entity = source.getEntity();
        if (entity instanceof Irk){
            return false;
        } else {
            return super.hurt(source, amount);
        }
    }

    public boolean isAlliedTo(Entity pEntity) {
        return MobUtil.illagerAllies(this, pEntity);
    }
}
