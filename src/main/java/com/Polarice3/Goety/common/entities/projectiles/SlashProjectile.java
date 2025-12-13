package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ISpellEntity;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Based on BloodSlashProjectile by @iron431: <a href="https://github.com/iron431/irons-spells-n-spellbooks/blob/1.19.2/src/main/java/io/redspace/ironsspellbooks/entity/spells/blood_slash/BloodSlashProjectile.java">...</a>
 **/
public abstract class SlashProjectile extends Projectile implements ISpellEntity {
    public static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(SlashProjectile.class, EntityDataSerializers.FLOAT);
    public int maxLifeSpan;
    public final int animationSeed;
    public float maxRadius;
    public AABB oldBB;
    public float damage = 6.0F;
    public int animationTime;
    private final List<Entity> victims = new ArrayList<>();

    public SlashProjectile(EntityType<? extends SlashProjectile> entityType, Level level) {
        super(entityType, level);
        this.animationSeed = level.getRandom().nextInt(9999);
        this.setRadius(0.6F);
        this.maxRadius = 3.0F;
        this.maxLifeSpan = MathHelper.secondsToTicks(4);
        this.oldBB = this.getBoundingBox();
        this.setNoGravity(true);
    }

    public SlashProjectile(EntityType<? extends SlashProjectile> entityType, Level levelIn, LivingEntity shooter) {
        this(entityType, levelIn);
        this.setOwner(shooter);
        this.setYRot(shooter.getYRot());
        this.setXRot(shooter.getXRot());
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_RADIUS, 0.5F);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Lifespan", this.getMaxLifeSpan());
        pCompound.putFloat("Radius", this.getRadius());
        pCompound.putFloat("MaxRadius", this.getMaxRadius());
        pCompound.putFloat("Damage", this.getDamage());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Lifespan")) {
            this.setMaxLifeSpan(pCompound.getInt("Lifespan"));
        }
        if (pCompound.contains("Radius")) {
            this.setRadius(pCompound.getFloat("Radius"));
        }
        if (pCompound.contains("MaxRadius")) {
            this.setMaxRadius(pCompound.getFloat("MaxRadius"));
        }
        if (pCompound.contains("Damage")) {
            this.setDamage(pCompound.getFloat("Damage"));
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_19729_) {
        if (DATA_RADIUS.equals(p_19729_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_19729_);
    }

    public int getMaxLifeSpan() {
        return this.maxLifeSpan;
    }

    public void setMaxLifeSpan(int lifeSpan) {
        this.maxLifeSpan = lifeSpan;
    }

    public float getDamage() {
        return this.damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getRadius() {
        return this.getEntityData().get(DATA_RADIUS);
    }

    public void setRadius(float radius) {
        this.getEntityData().set(DATA_RADIUS, Mth.clamp(radius, 0.0F, this.getMaxRadius()));
    }

    public float getMaxRadius() {
        return this.maxRadius;
    }

    public void setMaxRadius(float radius) {
        this.maxRadius = radius;
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public EntityDimensions getDimensions(Pose p_19721_) {
        return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
    }

    public void slash(Vec3 rotation, double speed) {
        this.setDeltaMovement(rotation.scale(speed));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > this.getMaxLifeSpan()) {
            this.discard();
            return;
        }
        this.oldBB = this.getBoundingBox();
        this.setRadius(this.getRadius() + 0.12F);

        if (!this.level.isClientSide) {
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitresult.getType() == HitResult.Type.BLOCK) {
                this.onHitBlock((BlockHitResult) hitresult);
            }
            for (Entity entity : this.level.getEntities(this, this.getBoundingBox())) {
                if (this.canHitEntity(entity) && !this.victims.contains(entity)) {
                    this.damageEntity(entity);
                    this.victims.add(entity);
                }
            }
        }

        this.setPos(position().add(this.getDeltaMovement()));
        this.spawnParticles();
    }

    public void damageEntity(Entity entity) {
        entity.hurt(entity.damageSources().indirectMagic(this, this.getOwner()), this.getDamage());
    }

    public ParticleOptions getParticle(){
        return ParticleTypes.CRIT;
    }

    public void spawnParticles() {
        if (this.level instanceof ServerLevel serverLevel) {
            float width = (float) this.getBoundingBox().getXsize();
            float step = 0.25F;
            float radians = Mth.DEG_TO_RAD * getYRot();
            float speed = 0.1F;
            for (int i = 0; i < width / step; i++) {
                double x = getX();
                double y = getY();
                double z = getZ();
                double offset = step * (i - width / step / 2);
                double rotX = offset * Math.cos(radians);
                double rotZ = -offset * Math.sin(radians);

                double dx = Math.random() * speed * 2.0D - speed;
                double dy = Math.random() * speed * 2.0D - speed;
                double dz = Math.random() * speed * 2.0D - speed;
                serverLevel.sendParticles(this.getParticle(), x + rotX + dx, y + dy, z + rotZ + dz, 0, dx, dy, dz, 1.0F);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        if (!this.level.getBlockState(hitResult.getBlockPos()).getCollisionShape(this.level, hitResult.getBlockPos()).isEmpty()) {
            if (!this.level.isClientSide) {
                this.discard();
            }
        }
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null){
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity){
                return super.canHitEntity(pEntity);
            } else {
                if (MobUtil.areAllies(this.getOwner(), pEntity)){
                    return false;
                }
                if (this.getOwner() instanceof Enemy && pEntity instanceof Enemy){
                    return false;
                }
                if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1){
                    return !MobUtil.ownerStack(owned0, owned1);
                }
            }
        }
        return super.canHitEntity(pEntity);
    }
}
