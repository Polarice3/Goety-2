package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.NecroVFXPalette;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SoulStar extends SpellEntity {
    public static final EntityDataAccessor<Boolean> DATA_NECRO = SynchedEntityData.defineId(SoulStar.class, EntityDataSerializers.BOOLEAN);
    public static final double MAW_SPEED = 700.0D / 2000.0D;
    public static final int MAW_HOMING_DELAY = 12;
    public static final float MAW_HOMING_MULTIPLIER = 4.5F;
    public static final int MAW_REROUTE_TICKS = 10;
    public static final float MAW_REROUTE_MULTIPLIER = 8.0F;
    public static final double MAW_HOMING_THRESHOLD = 3.5D;
    public static ColorUtil SMASH_COLOR = new ColorUtil(0x2ac9cf);
    public boolean hasTarget = false;
    public double xPower;
    public double yPower;
    public double zPower;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;
    public double speed = 1.0F;
    private int launchTick = -1;
    private boolean homingDone;
    public double badMath = 1.0D;
    public float extraRadius = 0.0F;
    public float baseDamage = 5.0F;
    private HitResult pendingHit;
    public int life;
    public int initTime = MathHelper.secondsToTicks(2);
    public TrailEffect trail = new TrailEffect(0.2F, 3.0F);

    public SoulStar(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public SoulStar(LivingEntity owner, LivingEntity target, Level level) {
        this(ModEntityType.SOUL_STAR.get(), level);
        if (owner != null) {
            this.setOwner(owner);
        }
        if (target != null) {
            this.setTarget(target);
        }
        this.noPhysics = true;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_NECRO, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Necro")) {
            this.setNecro(pCompound.getBoolean("Necro"));
        }
        if (pCompound.contains("BaseDamage")){
            this.baseDamage = pCompound.getFloat("BaseDamage");
        }
        if (pCompound.contains("ExtraRadius")) {
            this.setExtraRadius(pCompound.getFloat("ExtraRadius"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("Necro", this.isNecro());
        pCompound.putFloat("BaseDamage", this.baseDamage);
        pCompound.putFloat("ExtraRadius", this.getExtraRadius());
    }

    public float getExtraRadius(){
        return this.extraRadius;
    }

    public void setExtraRadius(float radius){
        this.extraRadius = radius;
    }

    public boolean isNecro() {
        return this.entityData.get(DATA_NECRO);
    }

    public void setNecro(boolean necro) {
        this.entityData.set(DATA_NECRO, necro);
    }

    public boolean shouldRenderAtSqrDistance(double p_36966_) {
        double d0 = this.getBoundingBox().getSize() * 4.0D;
        if (Double.isNaN(d0)) {
            d0 = 4.0D;
        }

        d0 *= 64.0D;
        return p_36966_ < d0 * d0;
    }

    public void lerpMotion(double p_36984_, double p_36985_, double p_36986_) {
        this.setDeltaMovement(p_36984_, p_36985_, p_36986_);
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = Math.sqrt(p_36984_ * p_36984_ + p_36986_ * p_36986_);
            this.setYRot((float)(Mth.atan2(p_36984_, p_36986_) * (double)(180F / (float)Math.PI)));
            this.setXRot((float)(Mth.atan2(p_36985_, d0) * (double)(180F / (float)Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

    }

    public void signalTo(Vec3 vec3, int initTime){
        double d0 = vec3.x;
        double d1 = vec3.y;
        double d2 = vec3.z;
        double d3 = d0 - this.getX();
        double d4 = d1 - this.getY();
        double d5 = d2 - this.getZ();
        this.xPower = d3 * 0.1D;
        this.yPower = d4 * 0.1D;
        this.zPower = d5 * 0.1D;
        this.initTime = initTime;
        this.life = 0;
    }

    public void setPower(Vec3 vec3, int initTime) {
        this.xPower = vec3.x * 0.1D;
        this.yPower = vec3.y * 0.1D;
        this.zPower = vec3.z * 0.1D;
        this.initTime = initTime;
        this.life = 0;
    }

    public void setBaseDamage(float baseDamage) {
        this.baseDamage = baseDamage;
    }

    public float getInertia() {
        return 0.95F;
    }

    public void tick() {
        super.tick();
        ++this.life;

        if (!this.level.isClientSide) {
            if (this.pendingHit != null) {
                this.onHit(this.pendingHit);
                return;
            }

            this.noPhysics = this.life < this.initTime;
            Vec3 vec3 = this.getDeltaMovement();
            double d3 = vec3.horizontalDistance();
            this.setXRot(MathHelper.lerpRotation(this.xRotO, (float)(Mth.atan2(vec3.y, d3) * (double)(180F / (float)Math.PI))));
            this.setYRot(MathHelper.lerpRotation(this.yRotO, (float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI))));

            if (this.life >= this.initTime && this.level.isLoaded(this.blockPosition())) {
                HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
                if (hitresult.getType() != HitResult.Type.MISS) {
                    Vec3 impact = this.impactPoint(hitresult, vec3);
                    this.setPos(impact.x, impact.y, impact.z);
                    this.setDeltaMovement(Vec3.ZERO);
                    this.pendingHit = hitresult;
                    return;
                }
            }

            this.setPos(this.getX() + vec3.x, this.getY() + vec3.y, this.getZ() + vec3.z);

            if (this.life >= this.initTime) {
                if (!this.hasTarget) {
                    if (this.getTarget() != null) {
                        this.launchAtTarget(this.getTarget());
                    } else if (this.getOwner() != null) {
                        if (this.getOwner() instanceof Mob mob) {
                            if (mob.getTarget() != null) {
                                this.setTarget(mob.getTarget());
                            }
                        } else {
                            List<LivingEntity> list = new ArrayList<>();
                            for (Entity entity1 : this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(16.0F))) {
                                LivingEntity livingEntity = null;
                                if (entity1 instanceof PartEntity<?> partEntity && partEntity.getParent() instanceof LivingEntity living) {
                                    livingEntity = living;
                                } else if (entity1 instanceof LivingEntity living) {
                                    livingEntity = living;
                                }
                                if (livingEntity != null && MobUtil.ownedPredicate(this).test(livingEntity)) {
                                    list.add(livingEntity);
                                }
                            }
                            list.sort(Comparator.comparingDouble(this::distanceTo));
                            if (!list.isEmpty()) {
                                this.setTarget(list.get(0));
                            }
                        }
                    }
                }
            } else if (this.life < 20) {
                this.badMath = Math.max(this.badMath - 0.01D, 0.0D);
                this.xPower *= this.badMath;
                this.yPower *= this.badMath;
                this.zPower *= this.badMath;
            }

            if (this.launchTick < 0) {
                this.setDeltaMovement(this.xPower, this.yPower, this.zPower); // warm-up drift, or no target yet
            } else {
                this.homeInTarget();
            }

            if (this.life >= this.initTime + MathHelper.secondsToTicks(5)) {
                this.discard();
            }
        } else {
            if (this.lerpSteps > 0) {
                double x = this.getX() + (this.lerpX - this.getX()) / this.lerpSteps;
                double y = this.getY() + (this.lerpY - this.getY()) / this.lerpSteps;
                double z = this.getZ() + (this.lerpZ - this.getZ()) / this.lerpSteps;
                this.setYRot(this.getYRot() + (float) Mth.wrapDegrees(this.lerpYRot - this.getYRot()) / this.lerpSteps);
                this.setXRot(this.getXRot() + (float) (this.lerpXRot - this.getXRot()) / this.lerpSteps);
                --this.lerpSteps;
                this.setPos(x, y, z);
            }
            this.trail.update(new Vec3(this.xOld, this.yOld + this.getBbHeight() / 2, this.zOld));
        }

    }

    private void launchAtTarget(LivingEntity target) {
        Vec3 vec3 = target.getBoundingBox().getCenter().subtract(this.position()).normalize();
        this.setDeltaMovement(vec3.scale(this.speed));
        this.launchTick = this.life;
        this.hasTarget = true;
        this.level.broadcastEntityEvent(this, (byte) 4);
    }

    private void homeInTarget() {
        LivingEntity target = this.getTarget();
        int sinceLaunch = this.life - this.launchTick;
        Vec3 velocity = this.getDeltaMovement();
        if (!this.homingDone && target != null && target.isAlive() && sinceLaunch >= MAW_HOMING_DELAY) {
            Vec3 toTarget = target.getBoundingBox().getCenter().subtract(this.position());
            if (toTarget.length() <= MAW_HOMING_THRESHOLD) {
                this.homingDone = true;
            } else {
                float multiplier = sinceLaunch < MAW_HOMING_DELAY + MAW_REROUTE_TICKS ? MAW_REROUTE_MULTIPLIER : MAW_HOMING_MULTIPLIER;
                velocity = velocity.add(toTarget.normalize().scale(this.speed * multiplier * 0.05D));
            }
        }
        double length = velocity.length();
        if (length > 1.0E-6D) {
            velocity = velocity.scale(this.speed / length);
        }
        this.setDeltaMovement(velocity);
    }

    private Vec3 impactPoint(HitResult hit, Vec3 motion) {
        Vec3 from = this.position();
        if (hit instanceof EntityHitResult entityHit) {
            Entity target = entityHit.getEntity();
            return target.getBoundingBox().inflate(this.getBbWidth() * 0.5D)
                    .clip(from, from.add(motion))
                    .orElse(target.getBoundingBox().getCenter());
        }
        return hit.getLocation();
    }

    public void travel(){
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        ProjectileUtil.rotateTowardsMovement(this, 0.2F);
        float f = this.getInertia();
        this.setDeltaMovement(vec3.add(this.xPower, this.yPower, this.zPower).scale(f));
        this.setPos(d0, d1, d2);
    }

    protected void onHit(HitResult pResult) {
        if (this.level instanceof ServerLevel serverLevel){
            if (this.life < this.initTime) {
                return;
            }
            Vec3 vec3 = pResult.getLocation();
            float damage = this.baseDamage + this.getExtraDamage();
            new SpellExplosion(serverLevel, this.getOwner() != null ? this.getOwner() : this, ModDamageSource.indirectSoul(this, this.getOwner()), vec3.x, vec3.y, vec3.z, 2.0F + this.getExtraRadius(), damage) {
                @Override
                public void postHurtEffect(Entity target, DamageSource damageSource, float actualDamage) {
                    super.postHurtEffect(target, damageSource, actualDamage);
                    if (target instanceof LivingEntity livingEntity) {
                        livingEntity.addEffect(new MobEffectInstance(GoetyEffects.SOUL_BURN.get(), MathHelper.secondsToTicks(5)));
                    }
                }
            };
            ColorUtil colorUtil = SMASH_COLOR;
            if (this.isNecro()) {
                colorUtil = new ColorUtil(NecroVFXPalette.BRIGHT_HEX);
            }
            serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 1.5F, 1), vec3.x, BlockFinder.moveDownToGround(this), vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            serverLevel.sendParticles(new SphereExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 3.0F, 1), vec3.x, BlockFinder.moveDownToGround(this), vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            this.playSound(ModSounds.SOUL_ORB_EXPLODE.get(), 1.0F, 0.6F + (serverLevel.getRandom().nextFloat() * 0.4F));
            this.discard();
        }
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    public boolean isAttackable() {
        return false;
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps, boolean teleport) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYRot = yRot;
        this.lerpXRot = xRot;
        this.lerpSteps = Math.min(steps, 1);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(3.0D);
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 4) {
            this.hasTarget = true;
        } else {
            super.handleEntityEvent(pId);
        }
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null){
            if (pEntity == this.getOwner()){
                return false;
            }
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity){
                return true;
            } else {
                if (MobUtil.areAllies(this.getOwner(), pEntity)){
                    return false;
                }
                if (MobUtil.areAllies(this, pEntity)){
                    return false;
                }
                if (pEntity instanceof ItemEntity) {
                    return false;
                }
                if (pEntity instanceof Projectile projectile && projectile.getOwner() == this.getOwner()){
                    return false;
                }
                if (pEntity instanceof SpellEntity spellEntity && spellEntity.getOwner() == this.getOwner()){
                    return false;
                }
                if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1){
                    return !MobUtil.ownerStack(owned0, owned1);
                }
            }
        }
        return true;
    }
}
