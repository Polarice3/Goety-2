package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ai.BreathingAttackGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class AbstractBorderWraith extends AbstractWraith implements IBreathing{
    public Vec3 initial;
    public int breathTick;

    public AbstractBorderWraith(EntityType<? extends Summoned> p_i48553_1_, Level p_i48553_2_) {
        super(p_i48553_1_, p_i48553_2_);
        this.breathTick = 0;
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putDouble("targetX", this.initial.x);
        pCompound.putDouble("targetY", this.initial.y);
        pCompound.putDouble("targetZ", this.initial.z);
        pCompound.putInt("breathTick", this.breathTick);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("targetX")){
            this.initial = new Vec3(pCompound.getDouble("targetX"), pCompound.getDouble("targetY"), pCompound.getDouble("targetZ"));
        }
        this.breathTick = pCompound.getInt("breathTick");
    }

    public void teleportAI(){
    }

    public void attackAI(){
        if (!this.level.isClientSide) {
            if (this.getTarget() != null) {
                if (this.getSensing().hasLineOfSight(this.getTarget())) {
                    if ((this.getTarget().distanceToSqr(this) > Mth.square(4.0F) && !this.getNavigation().isInProgress() || this.isStaying())
                            && this.getTarget().distanceToSqr(this) < Mth.square(this.halfFollowRange())) {
                        ++this.fireTick;
                        this.getNavigation().stop();
                        this.getLookControl().setLookAt(this.getTarget(), 100.0F, this.getMaxHeadXRot());
                        double d2 = this.getTarget().getX() - this.getX();
                        double d1 = this.getTarget().getZ() - this.getZ();
                        this.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                        this.yBodyRot = this.getYRot();
                        if (this.fireTick > 10) {
                            this.startFiring();
                        } else {
                            this.movement();
                            this.stopFiring();
                        }
                        if (this.fireTick > 20) {
                            this.fireTick = -20;
                            this.magicFire(this.getTarget());
                        }
                    } else {
                        if (this.fireTick > 0) {
                            this.fireTick = 0;
                        }
                        this.stopFiring();
                        this.movement();
                    }
                    if (this.isBreathing()){
                        this.getNavigation().stop();
                        this.fireTick = -20;
                        ++this.breathTick;
                        if (this.initial != null) {
                            this.lookAt(EntityAnchorArgument.Anchor.EYES, this.initial);
                        }
                        this.setYRot(this.yHeadRot);
                        if (this.breathTick >= 20) {
                            Vec3 vector3d = new Vec3(this.getTarget().getX() - this.initial.x, (this.getTarget().getY() + this.getTarget().getEyeHeight()) - this.initial.y, this.getTarget().getZ() - this.initial.z);
                            vector3d = vector3d.normalize();
                            double speed = 0.25D;

                            this.initial = this.initial.add(vector3d.x * speed, vector3d.y * speed, vector3d.z * speed);

                            this.level.broadcastEntityEvent(this, (byte) 8);
                            Entity entity = MobUtil.getSingleTarget(this.level, this, 8, 3.0F);
                            if (entity != null) {
                                this.doBreathing(entity);
                                this.gameEvent(GameEvent.PROJECTILE_SHOOT);
                            }
                        } else {
                            this.initial = new Vec3(this.getTarget().getX(), this.getTarget().getEyeY(), this.getTarget().getZ());
                        }
                        if (this.breathTick >= MathHelper.secondsToTicks(3.5F) || this.hurtTime > 0){
                            this.stopBreathing();
                        }
                    }
                }
            } else {
                this.stopFiring();
                this.stopBreathing();
            }
        }
    }

    public void movement(){
        if (this.getTarget() != null) {
            if (this.random.nextBoolean() && !this.isBreathing()
                    && this.getTarget().distanceToSqr(this) <= Mth.square(4.0F)
                    && this.hurtTime <= 0 && this.tickCount % 20 == 0) {
                this.startBreathing();
            } else if (!this.getNavigation().isInProgress()){
                super.movement();
            }
        } else {
            super.movement();
        }
    }

    public void magicFire(LivingEntity livingEntity){
        WandUtil.spawn4x4IceBouquet(this.level, livingEntity.position(), this);
    }

    public void startBreathing(){
        if (!this.isBreathing()) {
            this.setBreathing(true);
            this.level.broadcastEntityEvent(this, (byte) 6);
            this.level.broadcastEntityEvent(this, (byte) 100);
            if (!this.isSilent()) {
                this.level.playSound((Player) null, this.getX(), this.getY(), this.getZ(), ModSounds.WRAITH_PUKE.get(), this.getSoundSource(), 1.0F, 1.0F);
                this.playSound(ModSounds.WRAITH_PUKE.get(), 1.0F, 1.0F);
            }
        }
    }

    public void stopBreathing(){
        if (this.isBreathing()) {
            this.setBreathing(false);
            this.breathTick = 0;
            this.level.broadcastEntityEvent(this, (byte) 7);
        }
    }

    public void handleEntityEvent(byte pId) {
        if (pId == 6) {
            this.setBreathing(true);
            this.breathingAnimationState.start(this.tickCount);
        }
        if (pId == 7) {
            this.setBreathing(false);
        }
        if (pId == 8) {
            Vec3 look = this.getLookAngle();

            double dist = 0.9;
            double px = this.getX() + look.x() * dist;
            double py = this.getEyeY() + look.y() * dist;
            double pz = this.getZ() + look.z() * dist;

            for (int i = 0; i < 2; i++) {
                double dx = look.x();
                double dy = look.y();
                double dz = look.z();

                double spread = 10 + this.random.nextDouble() * 5;
                double velocity = 0.3 + this.random.nextDouble() * 0.3;

                dx += this.getRandom().nextGaussian() * 0.0075D * spread;
                dy += this.getRandom().nextGaussian() * 0.0075D * spread;
                dz += this.getRandom().nextGaussian() * 0.0075D * spread;
                dx *= velocity;
                dy *= velocity;
                dz *= velocity;

                this.level.addParticle(ModParticleTypes.WRAITH_FIRE.get(), px, py, pz, dx, dy, dz);
            }
            this.gameEvent(GameEvent.PROJECTILE_SHOOT);
        }
        super.handleEntityEvent(pId);
    }

    @Override
    public void doBreathing(Entity target) {
        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (damage < 1){
            damage = 1.0F;
        }
        if (this instanceof Enemy && target instanceof Enemy) {
            if (this.getTarget() != target) {
                return;
            }
        }
        if (this.getTrueOwner() != null){
            if (target.isAlliedTo(this.getTrueOwner()) || this.getTrueOwner().isAlliedTo(target) || target == this.getTrueOwner()){
                return;
            }
        }
        target.hurt(ModDamageSource.iceBouquet(this, this), damage);
    }

    public static class WraithBreathingGoal<T extends AbstractBorderWraith> extends BreathingAttackGoal<T>{
        public AbstractBorderWraith wraith;

        public WraithBreathingGoal(T pLivingEntity, float pRange, int pSeconds, float pFloatChance) {
            super(pLivingEntity, pRange, pSeconds, pFloatChance);
            this.wraith = pLivingEntity;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !this.wraith.isFiring();
        }

        public void start() {
            super.start();
            this.wraith.fireTick = -30;
            this.wraith.level.broadcastEntityEvent(this.wraith, (byte) 6);
            this.wraith.level.broadcastEntityEvent(this.wraith, (byte) 100);
            if (!this.wraith.isSilent()) {
                this.wraith.level.playSound((Player) null, this.wraith.getX(), this.wraith.getY(), this.wraith.getZ(), ModSounds.WRAITH_PUKE.get(), this.wraith.getSoundSource(), 1.0F, 1.0F);
                this.wraith.playSound(ModSounds.WRAITH_PUKE.get(), 1.0F, 1.0F);
            }
        }

        public void stop() {
            super.stop();
            this.wraith.level.broadcastEntityEvent(this.wraith, (byte) 7);
        }
    }
}
