package com.Polarice3.Goety.common.entities.hostile.illagers;

import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;

public abstract class HostileGolem extends Raider {
    public HostileGolem(EntityType<? extends Raider> type, Level worldIn) {
        super(type, worldIn);
    }

    public boolean causeFallDamage(float p_148711_, float p_148712_, DamageSource p_148713_) {
        return false;
    }

    public int getAmbientSoundInterval() {
        return 120;
    }

    protected int decreaseAirSupply(int p_28882_) {
        return p_28882_;
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    protected void doPush(Entity p_28839_) {
        if (p_28839_ instanceof LivingEntity livingEntity && !(livingEntity instanceof Enemy) && this.canAttack(livingEntity) && MobUtil.areNotFullAllies(this, livingEntity) && this.getRandom().nextInt(20) == 0) {
            this.setTarget(livingEntity);
        }

        super.doPush(p_28839_);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    public boolean removeWhenFarAway(double p_27519_) {
        return false;
    }

    public boolean canAnimateMove(){
        return !this.isImmobile();
    }

    public boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;
    }

    public abstract double getAttackReachSqr(LivingEntity enemy);

    public boolean canAttack(LivingEntity p_186270_) {
        return (!(p_186270_ instanceof AbstractVillager) || !p_186270_.isBaby()) && super.canAttack(p_186270_);
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr){
        double reach = this.getAttackReachSqr(enemy);
        return distToEnemySqr <= reach || this.getBoundingBox().intersects(enemy.getBoundingBox());
    }
}
