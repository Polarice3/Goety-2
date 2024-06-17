package com.Polarice3.Goety.common.entities.ally.undead.zombie;

import com.Polarice3.Goety.common.entities.neutral.AbstractZombieVindicator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ZombieVindicatorServant extends AbstractZombieVindicator {
    public ZombieVindicatorServant(EntityType<? extends ZombieServant> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new ModMeleeAttackGoal(this));
        this.goalSelector.addGoal(8, new WanderGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }
}
