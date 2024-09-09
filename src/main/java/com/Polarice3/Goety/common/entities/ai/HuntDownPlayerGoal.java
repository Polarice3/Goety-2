package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class HuntDownPlayerGoal<T extends PathfinderMob> extends Goal {
    private final T mob;
    private final TargetingConditions target = TargetingConditions.forNonCombat().range(64.0D).selector(MobUtil.NO_CREATIVE_OR_SPECTATOR);

    public HuntDownPlayerGoal(T mob){
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            List<Player> list = this.mob.level.getNearbyEntities(Player.class, this.target, this.mob, this.mob.getBoundingBox().inflate(64.0D, 32.0D, 64.0D));
            for (Player player : list) {
                if (SEHelper.getSoulsAmount(player, MobsConfig.IllagerAssaultSEThreshold.get())) {
                    this.mob.setTarget(player);
                }
            }
        } else {
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)){
                this.mob.setTarget(null);
                return false;
            }
            return this.mob.distanceTo(livingEntity) > 32.0D;
        }
        return false;
    }

    public void tick(){
        LivingEntity livingEntity = this.mob.getTarget();
        if (!this.mob.isPathFinding() && livingEntity != null) {
            Vec3 vector3d = livingEntity.position();
            this.mob.getNavigation().moveTo(vector3d.x, vector3d.y, vector3d.z, 1.0D);
        }
    }
}
