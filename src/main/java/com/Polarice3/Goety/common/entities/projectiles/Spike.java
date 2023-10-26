package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class Spike extends GroundProjectile {

    public Spike(EntityType<? extends Spike> p_i50170_1_, Level p_i50170_2_) {
        super(p_i50170_1_, p_i50170_2_);
        this.lifeTicks = 600;
    }

    public Spike(Level world, double pPosX, double pPosY, double pPosZ, float pYRot, int pWarmUp, LivingEntity owner) {
        this(ModEntityType.SPIKE.get(), world);
        this.warmupDelayTicks = pWarmUp;
        this.setOwner(owner);
        this.setYRot(pYRot * (180F / (float)Math.PI));
        this.setPos(pPosX, pPosY, pPosZ);
    }

    public void push(double p_70024_1_, double p_70024_3_, double p_70024_5_) {
    }

    public void move(MoverType p_213315_1_, Vec3 p_213315_2_) {
    }

    public boolean canCollideWith(Entity p_241849_1_) {
        return (p_241849_1_.canBeCollidedWith() || p_241849_1_.isPushable()) && !this.isPassengerOfSameVehicle(p_241849_1_);
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean isPushable() {
        return false;
    }

    public boolean isAttackable() {
        return false;
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.sentTrapEvent) {
                --this.lifeTicks;
                if (this.animationTicks > 9){
                    --this.animationTicks;
                }
            }
        } else if (--this.warmupDelayTicks < 0) {
            for(LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().move(0.0F, 0.2F, 0.0F).inflate(0.1F, 0.0F, 0.1F))) {
                this.dealDamageTo(livingentity);
            }

            if (!this.playSound){
                this.level.broadcastEntityEvent(this, (byte)5);
                this.playSound = true;
            }

            if (!this.sentTrapEvent) {
                this.level.broadcastEntityEvent(this, (byte)4);
                this.sentTrapEvent = true;
            }

            if (--this.lifeTicks < 0) {
                this.discard();
            }
        }

    }

    private void dealDamageTo(LivingEntity target) {
        LivingEntity livingentity = this.getOwner();
        if (target.isAlive() && !target.isInvulnerable() && target != livingentity) {
            if (livingentity == null) {
                if (target.hurt(ModDamageSource.spike(this, this), 1.0F)){
                    this.level.broadcastEntityEvent(target, (byte) 44);
                }
            } else {
                if (target.isAlliedTo(livingentity)){
                    return;
                }
                if (livingentity.isAlliedTo(target)) {
                    return;
                }
                if (target.hurt(ModDamageSource.spike(this, livingentity), 1.0F)){
                    this.level.broadcastEntityEvent(target, (byte) 44);
                }
            }
        }
    }

    public void handleEntityEvent(byte pId) {
        super.handleEntityEvent(pId);
        if (pId == 5) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.GRINDSTONE_USE, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
