package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.DustCloudParticleOption;
import com.Polarice3.Goety.client.particles.SparkleParticleOption;
import com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VoidShock extends SpellEntity {
    public boolean hasTarget = false;
    public boolean explode = true;
    public double xPower;
    public double yPower;
    public double zPower;
    public double badMath = 1.0D;
    public float extraRadius = 0.0F;
    public float baseDamage = SpellConfig.VoidShockDamage.get().floatValue() * WandUtil.damageMultiply();
    public int life;
    public int initTime = MathHelper.secondsToTicks(2);
    public TrailEffect trail = new TrailEffect(0.2F, 3.0F);

    public VoidShock(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public VoidShock(LivingEntity owner, LivingEntity target, Level level) {
        this(ModEntityType.VOID_SHOCK.get(), level);
        if (owner != null) {
            this.setOwner(owner);
        }
        if (target != null) {
            this.setTarget(target);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Explode")){
            this.setExplode(pCompound.getBoolean("Explode"));
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
        pCompound.putBoolean("Explode", this.isExplode());
        pCompound.putFloat("BaseDamage", this.baseDamage);
        pCompound.putFloat("ExtraRadius", this.getExtraRadius());
    }

    public float getExtraRadius(){
        return this.extraRadius;
    }

    public void setExtraRadius(float radius){
        this.extraRadius = radius;
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

    public void setExplode(boolean explode) {
        this.explode = explode;
    }

    public boolean isExplode() {
        return this.explode;
    }

    public void tick() {
        super.tick();
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        double d3 = vec3.horizontalDistance();
        this.setXRot(MathHelper.lerpRotation(this.xRotO, (float)(Mth.atan2(vec3.y, d3) * (double)(180F / (float)Math.PI))));
        this.setYRot(MathHelper.lerpRotation(this.yRotO, (float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI))));

        this.level.addParticle(ParticleTypes.PORTAL, d0 - vec3.x * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, d1 - vec3.y * 0.25D - 0.5D, d2 - vec3.z * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, vec3.x, vec3.y, vec3.z);

        ++this.life;

        if (!this.level.isClientSide) {
            this.setPos(d0, d1, d2);
            if (this.life >= this.initTime) {
                if (!this.hasTarget) {
                    if (this.getTarget() != null) {
                        LivingEntity target = this.getTarget();
                        double dx = this.getX() - target.getX();
                        double dy = this.getY() - target.getY();
                        double dz = this.getZ() - target.getZ();
                        double ds = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        double velocity = 5.0D;
                        this.xPower = -(dx / ds * velocity * 0.2D);
                        this.yPower = -(dy / ds * velocity * 0.2D);
                        this.zPower = -(dz / ds * velocity * 0.2D);
                        this.hasTarget = true;
                        this.level.broadcastEntityEvent(this, (byte) 4);
                        if (this.level instanceof ServerLevel serverLevel) {
                            ColorUtil colorUtil = new ColorUtil(0xffffff);
                            Vec3 vec31 = this.position();
                            serverLevel.sendParticles(new SparkleParticleOption(4.0F, colorUtil, 2), vec31.x, vec31.y, vec31.z, 0, 0.0F, 0.0F, 0.0F, 1.0F);
                        }
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
                                if (livingEntity != null) {
                                    if (MobUtil.ownedPredicate(this).test(livingEntity)) {
                                        list.add(livingEntity);
                                    }
                                }
                            }
                            list.sort(Comparator.comparingDouble(this::distanceTo));
                            if (list.stream().findFirst().isPresent()) {
                                LivingEntity livingEntity = list.stream().findFirst().get();
                                this.setTarget(livingEntity);
                            }
                        }
                    }
                }
                if (this.level.isLoaded(this.blockPosition())) {
                    HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
                    if (hitresult.getType() != HitResult.Type.MISS) {
                        this.onHit(hitresult);
                    }
                }
            } else if (this.life <= 20) {
                this.badMath = Math.max(this.badMath - 0.01D, 0.0D);
                this.xPower *= this.badMath;
                this.yPower *= this.badMath;
                this.zPower *= this.badMath;
            }
            this.setDeltaMovement(this.xPower, this.yPower, this.zPower);

            if (this.life >= this.initTime + MathHelper.secondsToTicks(5)) {
                this.discard();
            }
        } else {
            this.setPosRaw(d0, d1, d2);
            if (tickCount > 5 && hasTrail()) {
                Vec3 oldPos = new Vec3(xOld, yOld + getBbHeight() / 2, zOld);
                trail.update(oldPos);
            }
        }

    }

    protected void onHit(HitResult pResult) {
        if (pResult instanceof EntityHitResult entityHitResult && !this.isExplode()){
            this.onHitEntity(entityHitResult);
        }
        if (this.level instanceof ServerLevel serverLevel){
            if (this.isExplode()) {
                Vec3 vec3 = Vec3.atCenterOf(this.blockPosition());
                if (pResult instanceof BlockHitResult blockHitResult) {
                    BlockPos blockpos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
                    if (BlockFinder.canBeReplaced(serverLevel, blockpos)) {
                        vec3 = Vec3.atCenterOf(blockpos);
                    }
                } else if (pResult instanceof EntityHitResult entityHitResult) {
                    Entity entity1 = entityHitResult.getEntity();
                    vec3 = Vec3.atCenterOf(entity1.blockPosition());
                }
                float damage = this.baseDamage + this.getExtraDamage();
                new SpellExplosion(serverLevel, this.getOwner() != null ? this.getOwner() : this, this.damageSources().indirectMagic(this, this.getOwner()), vec3.x, vec3.y, vec3.z, 1.5F + this.getExtraRadius(), damage) {
                    @Override
                    public void explodeHurt(Entity target, DamageSource damageSource, double x, double y, double z, double seen, float actualDamage) {
                        super.explodeHurt(target, damageSource, x, y, z, seen, actualDamage);
                        if (target.level.getRandom().nextFloat() <= 0.25F) {
                            if (target instanceof LivingEntity livingEntity && !livingEntity.hasEffect(GoetyEffects.VOID_TOUCHED.get())) {
                                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.VOID_TOUCHED.get(), MathHelper.secondsToTicks(3), 2, false, true));
                            }
                        }
                    }
                };
                ColorUtil colorUtil = new ColorUtil(0xb103d8);
                serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 1.5F, 1), vec3.x, BlockFinder.moveDownToGround(this), vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 3.0F, 1), vec3.x, BlockFinder.moveDownToGround(this), vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(new VerticalCircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 1.5F, 1), vec3.x, BlockFinder.moveDownToGround(this), vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(new VerticalCircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 3.0F, 1), vec3.x, BlockFinder.moveDownToGround(this), vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                DustCloudParticleOption cloudParticleOptions = new DustCloudParticleOption(new Vector3f(Vec3.fromRGB24(16733695).toVector3f()), 1.0F);
                for (int i = 0; i < 2; ++i) {
                    ServerParticleUtil.circularParticles(serverLevel, cloudParticleOptions, vec3.x, this.getY() + 0.25D, vec3.z, 0, 0.14D, 0, 1.0F);
                }
                this.playSound(ModSounds.FUNGUS_EXPLOSION.get(), 1.0F, 0.6F + (serverLevel.getRandom().nextFloat() * 0.4F));
            }
            this.discard();
        }
    }

    protected void onHitEntity(EntityHitResult pResult) {
        if (!this.level.isClientSide) {
            Entity entity = pResult.getEntity();
            Entity entity1 = this.getOwner();
            float damage = this.baseDamage + this.getExtraDamage();
            entity.hurt(this.damageSources().indirectMagic(this, entity1), damage);
            if (entity1 instanceof LivingEntity living) {
                this.doEnchantDamageEffects(living, entity);
                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(living)) {
                    if (living.level.getRandom().nextFloat() <= 0.25F) {
                        if (!living.hasEffect(GoetyEffects.VOID_TOUCHED.get())) {
                            living.addEffect(new MobEffectInstance(GoetyEffects.VOID_TOUCHED.get(), MathHelper.secondsToTicks(3), 2, false, true));
                        }
                    }
                }
            }
        }
    }

    public boolean hasTrail() {
        return this.hasTarget;
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    public boolean isAttackable() {
        return false;
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
