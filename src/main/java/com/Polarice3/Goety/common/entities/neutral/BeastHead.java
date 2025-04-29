package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class BeastHead extends Owned {
    private Vec3 chargePos;
    public int life;
    public float keepYRot;
    public float keepXRot;
    public float alpha = 0.0F;
    private boolean sentSpawnEvent;
    private boolean sentAttackEvent;
    private final List<Entity> hitEntities = new ArrayList<>();
    public AnimationState spawnAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();

    public BeastHead(EntityType<? extends Owned> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public BeastHead(EntityType<? extends Owned> p_19870_, Level p_19871_, LivingEntity target){
        this(p_19870_, p_19871_);
        MobUtil.instaLook(this, target);
        this.setTarget(target);
        this.yRotO = this.getYRot();
        this.yHeadRotO = this.getYRot();
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.life = pCompound.getInt("life");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("life", this.life);
    }

    @Override
    public void makeStuckInBlock(BlockState p_20006_, Vec3 p_20007_) {
        super.makeStuckInBlock(p_20006_, Vec3.ZERO);
    }

    protected void doPush(Entity p_20971_) {
    }

    public boolean canCollideWith(Entity p_20303_) {
        return false;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        return pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public void tick() {
        super.tick();
        this.setYRot(this.getYHeadRot());
        this.yBodyRot = this.getYRot();
        this.noPhysics = true;
        this.setNoGravity(true);

        ++this.life;
        if (!this.level.isClientSide) {
            if (!this.sentSpawnEvent) {
                this.level.broadcastEntityEvent(this, (byte)4);
                this.sentSpawnEvent = true;
            }
            if (this.life < 30) {
                Vec3 vector3d;
                if (this.getTarget() != null) {
                    vector3d = this.getTarget().getEyePosition(1.0F);
                    MobUtil.instaLook(this, this.getTarget());
                } else {
                    vector3d = this.getViewVector(1.0F);
                    MobUtil.instaLook(this, vector3d);
                }
                double dx = this.getX() - vector3d.x();
                double dy = this.getY() - vector3d.y();
                double dz = this.getZ() - vector3d.z();
                double d0 = Math.sqrt(dx * dx + dy * dy + dz * dz);
                double velocity = 2.0D;
                double xPower = -(dx / d0 * velocity * 0.2D);
                double yPower = -(dy / d0 * velocity * 0.2D);
                double zPower = -(dz / d0 * velocity * 0.2D);
                this.chargePos = new Vec3(xPower, yPower, zPower);
                this.keepYRot = this.getYRot();
                this.keepXRot = this.getXRot();
            } else {
                this.setYRot(this.keepYRot);
                this.setXRot(this.keepXRot);
                if (!this.sentAttackEvent) {
                    this.level.broadcastEntityEvent(this, (byte)5);
                    this.sentAttackEvent = true;
                }
                if (this.chargePos != null) {
                    this.setDeltaMovement(this.chargePos);
                }
                if (this.life <= 40) {
                    for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0D))) {
                        if (!this.hitEntities.contains(livingEntity) && !MobUtil.areAllies(this.getOwner() != null ? this.getOwner() : this, livingEntity) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                            DamageSource damageSource = this.damageSources().mobAttack(this);
                            if (this.getOwner() != null){
                                damageSource = ModDamageSource.summonAttack(this, this.getOwner());
                            }
                            if (livingEntity.hurt(damageSource, AttributesConfig.BlackBeastDamage.get().floatValue())) {
                                this.hitEntities.add(livingEntity);
                            }
                        }
                    }
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.25D));
                }
            }
            if (this.life >= 70) {
                this.discard();
            }
        } else {
            if (this.life > 10 && this.life < 50 && this.alpha < 1.0F){
                this.alpha = Mth.clamp(this.alpha + 0.1F, 0.0F, 1.0F);
            }
            if (this.life >= 50){
                this.alpha = Mth.clamp(this.alpha - 0.05F, 0.0F, 1.0F);
            }
        }
    }

    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean attackable() {
        return false;
    }

    public boolean canBeSeenByAnyone() {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance pPotioneffect) {
        return false;
    }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    public boolean displayFireAnimation() {
        return false;
    }

    public void handleEntityEvent(byte p_36935_) {
        if (p_36935_ == 4) {
            this.spawnAnimationState.startIfStopped(this.tickCount);
        } else if (p_36935_ == 5) {
            this.spawnAnimationState.stop();
            this.attackAnimationState.start(this.tickCount);
        } else {
            super.handleEntityEvent(p_36935_);
        }

    }
}
