package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SRepositionPacket;
import com.Polarice3.Goety.common.network.server.STentacleRangePacket;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class GulfTentacle extends Owned {
    private static final EntityDataAccessor<Float> RANGE = SynchedEntityData.defineId(GulfTentacle.class, EntityDataSerializers.FLOAT);
    public int life;
    public int offset = 1;
    private boolean sentSpawnEvent;
    public boolean isStaff = false;
    public float keepYRot = 0.0F;
    public float keepXRot = 0.0F;
    public AnimationState spawnAnimationState = new AnimationState();

    public GulfTentacle(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, SpellConfig.WaterWhipDamage.get() * WandUtil.damageMultiply());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), SpellConfig.WaterWhipDamage.get() * WandUtil.damageMultiply());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RANGE, 4.0F);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.isStaff = pCompound.getBoolean("isStaff");
        this.life = pCompound.getInt("life");
        this.offset = pCompound.getInt("offset");
        this.keepYRot = pCompound.getFloat("KeepYRot");
        this.keepXRot = pCompound.getFloat("KeepXRot");
        this.setRange(pCompound.getFloat("Range"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("isStaff", this.isStaff);
        pCompound.putInt("life", this.life);
        pCompound.putInt("offset", this.offset);
        pCompound.putFloat("KeepYRot", this.keepYRot);
        pCompound.putFloat("KeepXRot", this.keepXRot);
        pCompound.putFloat("Range", this.getRange());
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
    public void setDeltaMovement(Vec3 p_20257_) {
    }

    public void setRot(float keepYRot, float keepXRot) {
        this.keepYRot = keepYRot;
        this.keepXRot = keepXRot;
    }

    public void setOffset(int i) {
        this.offset = i;
    }

    public void setStaff(boolean staff) {
        this.isStaff = staff;
    }

    public void setRange(float range) {
        this.entityData.set(RANGE, range);
    }

    public float getRange() {
        return this.entityData.get(RANGE);
    }

    @Override
    public void tick() {
        super.tick();
        this.noPhysics = true;
        this.setNoGravity(true);

        ++this.life;
        if (this.getTrueOwner() != null) {
            this.setYRot(this.getTrueOwner().getYRot());
            this.setXRot(this.getTrueOwner().getXRot());
        } else {
            this.setYRot(this.keepYRot);
            this.setXRot(this.keepXRot);
        }
        this.setYHeadRot(this.getYRot());
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        if (!this.level.isClientSide) {
            if (this.getTrueOwner() != null) {
                float f2 = this.getTrueOwner().yBodyRot * (float) (Math.PI / 180.0);
                double d0 = Mth.sin(f2);
                double d1 = Mth.cos(f2);
                double d2 = this.offset * 0.5D;
                double d3 = 1.0D;
                float f3 = this.getTrueOwner().isCrouching() ? -0.1875F : 0.0F;
                Vec3 vec3 = this.getTrueOwner().getEyePosition().add(-d1 * d2 - d0 * d3, (double)f3 - 0.45F, -d0 * d2 + d1 * d3);
                this.setPos(vec3);
            }
            if (!this.sentSpawnEvent) {
                this.level.broadcastEntityEvent(this, (byte)4);
                this.sentSpawnEvent = true;
                ModNetwork.sentToTrackingEntityAndPlayer(this, new STentacleRangePacket(this.getId(), this.getRange()));
                ModNetwork.sentToTrackingEntityAndPlayer(this, new SRepositionPacket(this.getId(), this.position()));
            }
            if (this.life >= 20) {
                this.discard();
            } else {
                if (this.life >= 8 && this.life < 12) {
                    Set<LivingEntity> entities = new HashSet<>();
                    AABB aabb = new AABB(this.position(), this.position()).inflate(1.0F);
                    double distanceToDestination = beamTraceDistance(this.getRange(), 1.0f, false);
                    double distanceTraveled = 0;
                    while (!(this.position().distanceTo(aabb.getCenter()) > distanceToDestination) && !(this.position().distanceTo(aabb.getCenter()) > this.getRange())) {
                        for (Entity entity : this.level.getEntitiesOfClass(Entity.class, aabb)) {
                            LivingEntity livingEntity = MobUtil.getLivingTarget(entity);
                            if (livingEntity != null && !MobUtil.areAllies(this.getOwner() != null ? this.getOwner() : this, livingEntity) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                                entities.add(livingEntity);
                            }
                        }
                        distanceTraveled += 1.0D;
                        Vec3 viewVector = this.getViewVector(1.0F);
                        Vec3 targetVector = this.position().add(viewVector.x * distanceTraveled, viewVector.y * distanceTraveled, viewVector.z * distanceTraveled);
                        aabb = new AABB(targetVector, targetVector).inflate(1.0F);
                    }
                    this.damageEntities(entities);
                }
            }
        }
    }

    public void damageEntities(Set<LivingEntity> entities){
        for (LivingEntity entity : entities) {
            DamageSource damageSource = ModDamageSource.directDrench(this);
            if (this.getTrueOwner() != null) {
                damageSource = ModDamageSource.indirectDrench(this, this.getTrueOwner());
            }
            boolean flag = entity.hurt(damageSource, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            if (flag) {
                this.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.WHIP_HIT.get(), this.getSoundSource(), 1.0F, this.getVoicePitch());
                if (this.isStaff) {
                    GulfTentacle gulfTentacle = new GulfTentacle(ModEntityType.GULF_TENTACLE.get(), this.level);
                    gulfTentacle.setPos(this.position());
                    if (this.getTrueOwner() != null) {
                        gulfTentacle.setRot(this.getTrueOwner().getYRot(), this.getTrueOwner().getXRot());
                        gulfTentacle.setTrueOwner(this.getTrueOwner());
                    } else {
                        gulfTentacle.setRot(this.getYRot(), this.getXRot());
                    }
                    gulfTentacle.setRange(this.getRange());
                    gulfTentacle.setOffset(this.offset);
                    if (this.hasEffect(GoetyEffects.BUFF.get())) {
                        gulfTentacle.addEffect(new MobEffectInstance(GoetyEffects.BUFF.get(), EffectsUtil.infiniteEffect(), EffectsUtil.getAmplifier(this, GoetyEffects.BUFF.get()), false, false));
                    }
                    gulfTentacle.setStaff(false);
                    this.level.addFreshEntity(gulfTentacle);
                    gulfTentacle.playSound(ModSounds.WHIP_SWING.get());
                }
            }
        }
    }

    public final Vec3 getWorldPosition(float p_242282_1_) {
        double d0 = Mth.lerp(p_242282_1_, this.xo, this.getX());
        double d1 = Mth.lerp(p_242282_1_, this.yo, this.getY());
        double d2 = Mth.lerp(p_242282_1_, this.zo, this.getZ());
        return new Vec3(d0, d1, d2);
    }

    public HitResult beamTraceResult(double distance, float ticks, boolean passesWater) {
        Vec3 vector3d = this.getWorldPosition(ticks);
        Vec3 vector3d1 = this.getViewVector(ticks);
        Vec3 vector3d2 = vector3d.add(vector3d1.x * distance, vector3d1.y * distance, vector3d1.z * distance);
        return level.clip(new ClipContext(vector3d, vector3d2, ClipContext.Block.COLLIDER, passesWater ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, this));
    }

    public double beamTraceDistance(double distance, float ticks, boolean passesWater) {
        HitResult rayTraceResult = beamTraceResult(distance, ticks, passesWater);
        double distanceToDestination = this.getRange();
        if (rayTraceResult instanceof BlockHitResult) {
            BlockPos collision = ((BlockHitResult) rayTraceResult).getBlockPos();
            Vec3 destination = new Vec3(collision.getX(), collision.getY(), collision.getZ());
            distanceToDestination = this.position().distanceTo(destination);
        }
        return distanceToDestination;
    }

    public void updatePositionAndRotation() {
        LivingEntity owner = this.getTrueOwner();
        if (owner != null) {
            Vec3 vec1 = owner.position();
            vec1 = vec1.add(this.getOffsetVector(owner));
            this.setPos(vec1.x, vec1.y, vec1.z);
            this.setYRot(boundDegrees(owner.getYRot()));
            this.setXRot(boundDegrees(owner.getXRot()));
            this.yRotO = boundDegrees(owner.yRotO);
            this.xRotO = boundDegrees(owner.xRotO);
        }
    }

    private float boundDegrees(float v) {
        return (v % 360 + 360) % 360;
    }

    private Vec3 getOffsetVector(LivingEntity living) {
        Vec3 viewVector = this.getViewVector(1.0F);
        return new Vec3(viewVector.x, living.getEyeHeight() * 0.4D, viewVector.z);
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
        return pPotioneffect.getEffect().isBeneficial();
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
        } else {
            super.handleEntityEvent(p_36935_);
        }

    }
}
