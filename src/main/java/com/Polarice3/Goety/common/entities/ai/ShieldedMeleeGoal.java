package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.api.entities.IShielded;
import com.Polarice3.Goety.client.particles.SmashParticleOption;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;

public class ShieldedMeleeGoal<T extends Mob & IShielded> extends Goal {
    public T mob;
    
    public ShieldedMeleeGoal(T mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.mob.getTarget() != null && this.mob.isMeleeAttacking();
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.getAttackTick() < MathHelper.secondsToTicks(1.42F);
    }

    @Override
    public void start() {
        this.mob.setMeleeAttacking(true);
        this.mob.level.broadcastEntityEvent(this.mob, (byte) 4);
    }

    @Override
    public void stop() {
        this.mob.setMeleeAttacking(false);
    }

    @Override
    public void tick() {
        if (this.mob.getTarget() != null) {
            LivingEntity livingentity = this.mob.getTarget();
            MobUtil.instaLook(this.mob, livingentity);
        }
        if (this.mob.getAttackTick() == 1){
            this.playPreAttack();
        }
        if (this.mob.getAttackTick() == 9){
            this.playSmash();
        }
        if (this.mob.getAttackTick() == 14) {
            double x = this.mob.getX() + this.mob.getHorizontalLookAngle().x * 2;
            double y = this.mob.getY();
            double z = this.mob.getZ() + this.mob.getHorizontalLookAngle().z * 2;
            this.attack(x, y, z);
            this.attackVisual(x, y, z);
        }
    }

    public void playPreAttack() {
    }

    public void playSmash() {
    }

    public void attack(double x, double y, double z) {
        AABB aabb = MobUtil.makeAttackRange(x, y, z, 3, 3, 3);
        AABB boundingBox = this.mob.getBoundingBox();
        for (LivingEntity target : this.mob.level.getEntitiesOfClass(LivingEntity.class, aabb.minmax(boundingBox))) {
            if (target != this.mob) {
                AABB box = target.getBoundingBox();
                if (box.intersects(aabb) || box.intersects(boundingBox)) {
                    if (this.mob.getTarget() == target || (target instanceof Mob mob1 && mob1.getTarget() == this.mob) || !MobUtil.areAllies(this.mob, target)) {
                        this.mob.doHurtTarget(target);
                    }
                }
            }
        }
    }

    public void attackVisual(double x, double y, double z) {
        if (this.mob.level instanceof ServerLevel serverLevel){
            double groundY = BlockFinder.findGroundY(serverLevel, x, y, z);

            BlockPos blockPos = BlockPos.containing(x, groundY - 1.0D, z);
            BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, serverLevel.getBlockState(blockPos));
            for (int i = 0; i < 2; ++i) {
                ServerParticleUtil.circularParticles(serverLevel, option, x, groundY + 0.25D, z, 1.5F);
            }
            ColorUtil colorUtil = new ColorUtil(serverLevel.getBlockState(blockPos).getMapColor(serverLevel, blockPos).col);
            serverLevel.sendParticles(new SmashParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.5F, 10), x, groundY, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
