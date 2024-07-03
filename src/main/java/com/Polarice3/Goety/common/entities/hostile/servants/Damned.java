package com.Polarice3.Goety.common.entities.hostile.servants;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.DustCloudParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.Hellfire;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public class Damned extends Owned implements Enemy {
    private static final EntityDataAccessor<Boolean> DATA_CHARGING_STATE = SynchedEntityData.defineId(Damned.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HUMAN = SynchedEntityData.defineId(Damned.class, EntityDataSerializers.BOOLEAN);
    private Vec3 chargePos;
    private int chargeTime;

    public Damned(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.noPhysics = true;
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CHARGING_STATE, false);
        this.entityData.define(DATA_HUMAN, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Human")){
            this.setHuman(compound.getBoolean("Human"));
        }
        if (compound.contains("ChargePos")){
            this.setCharge(Vec3Util.readVec3(compound.getCompound("ChargePos")));
        }
        if (compound.contains("ChargeTime")){
            this.chargeTime = compound.getInt("ChargeTime");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Human", this.isHuman());
        if (this.chargePos != null) {
            compound.put("ChargePos", Vec3Util.writeVec3(this.chargePos));
        }
        compound.putInt("ChargeTime", this.chargeTime);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @javax.annotation.Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.setDeltaMovement(0.0D, 0.3D, 0.0D);
        if (pLevel.getRandom().nextBoolean()){
            this.setHuman(true);
            pLevel.getLevel().broadcastEntityEvent(this, (byte) 101);
        }
        return pSpawnData;
    }

    public void tick() {
        super.tick();
        this.setYRot(this.getYHeadRot());
        this.yBodyRot = this.getYRot();
        this.setNoGravity(true);

        if (this.isCharging()) {
            if (this.chargePos != null) {
                this.setDeltaMovement(this.chargePos);
            }
            if (!this.level().isClientSide && this.level().isLoaded(this.blockPosition())) {
                HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
                if (hitresult.getType() != HitResult.Type.MISS) {
                    this.onHit(hitresult);
                }
            }
        }

        if (this.getTarget() != null) {
            MobUtil.instaLook(this, this.getTarget());
            ++this.chargeTime;
            if (this.chargeTime == 50) {
                LivingEntity target = this.getTarget();
                double dx = this.getX() - target.getX();
                double dy = this.getY() - target.getY();
                double dz = this.getZ() - target.getZ();
                double d0 = Math.sqrt(dx * dx + dy * dy + dz * dz);
                double velocity = 5.0D;
                double xPower = -(dx / d0 * velocity * 0.2D);
                double yPower = -(dy / d0 * velocity * 0.2D);
                double zPower = -(dz / d0 * velocity * 0.2D);
                this.playSound(ModSounds.DAMNED_SCREAM.get(), 3.0F, this.getVoicePitch());
                this.setCharge(xPower, yPower, zPower);
                if (!this.level.isClientSide) {
                    this.setCharging(true);
                    this.level.broadcastEntityEvent(this, (byte) 100);
                    Vec3 vec3 = this.getDeltaMovement();
                    double mX = this.getX() - vec3.x;
                    double mY = this.getY() - vec3.y;
                    double mZ = this.getZ() - vec3.z;
                    if (this.level instanceof ServerLevel serverLevel){
                        serverLevel.sendParticles(ModParticleTypes.BIG_FIRE.get(), mX, mY + 0.15D, mZ, 1, 0.0D, 0.0D, 0.0D, 0);
                    }
                }
            }
        }
    }

    @Override
    public void lifeSpanDamage() {
        this.discard();
    }

    @Override
    public boolean isOnFire() {
        return this.isCharging();
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if (!pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)){
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    public void setCharge(Vec3 vec3) {
        this.chargePos = vec3;
    }

    public void setCharge(double x, double y, double z) {
        this.chargePos = new Vec3(x, y, z);
    }

    public boolean isCharging() {
        return this.entityData.get(DATA_CHARGING_STATE);
    }

    public void setCharging(boolean charge) {
        this.entityData.set(DATA_CHARGING_STATE, charge);
    }

    public boolean isHuman() {
        return this.entityData.get(DATA_HUMAN);
    }

    public void setHuman(boolean human) {
        this.entityData.set(DATA_HUMAN, human);
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 100){
            this.setCharging(true);
        } else if (p_21375_ == 101){
            this.setHuman(true);
        }
        super.handleEntityEvent(p_21375_);
    }

    protected void onHitEntity(EntityHitResult pResult) {
        if (!this.level.isClientSide) {
            Entity entity = pResult.getEntity();
            Entity entity1 = this.getOwner();
            float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            entity.hurt(ModDamageSource.hellfire(this, entity1), damage);
            if (entity1 instanceof LivingEntity living) {
                this.doEnchantDamageEffects(living, entity);
            }
        }
    }

    protected void onHit(HitResult pResult) {
        if (pResult instanceof EntityHitResult entityHitResult){
            this.onHitEntity(entityHitResult);
        }
        if (!this.level.isClientSide) {
            Entity entity = this.getOwner();
            Vec3 vec3 = Vec3.atCenterOf(this.blockPosition());
            if (pResult instanceof BlockHitResult blockHitResult){
                BlockPos blockpos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
                if (BlockFinder.canBeReplaced(this.level, blockpos)) {
                    vec3 = Vec3.atCenterOf(blockpos);
                }
            } else if (pResult instanceof EntityHitResult entityHitResult){
                Entity entity1 = entityHitResult.getEntity();
                vec3 = Vec3.atCenterOf(entity1.blockPosition());
            }
            LivingEntity livingEntity = entity instanceof LivingEntity living ? living : this;
            Hellfire hellfire = new Hellfire(this.level, vec3, livingEntity);
            if (this.level.addFreshEntity(hellfire)) {
                for (Direction direction : Direction.values()) {
                    if (direction.getAxis().isHorizontal()) {
                        Hellfire hellfire1 = new Hellfire(this.level, Vec3.atCenterOf(hellfire.blockPosition().relative(direction)), livingEntity);
                        this.level.addFreshEntity(hellfire1);
                    }
                }
            }
            MobUtil.explosionDamage(this.level, this.getOwner() != null ? this.getOwner() : this, ModDamageSource.hellfire(this, this.getOwner()), vec3.x, vec3.y, vec3.z, 1.5F, 0);
            if (this.level instanceof ServerLevel serverLevel){
                ServerParticleUtil.addParticlesAroundSelf(serverLevel, ModParticleTypes.BIG_FIRE.get(), this);
                ColorUtil colorUtil = new ColorUtil(0xdd9c16);
                serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 4, 1), vec3.x, BlockFinder.moveDownToGround(this), vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 5, 1), vec3.x, BlockFinder.moveDownToGround(this), vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                DustCloudParticleOption cloudParticleOptions = new DustCloudParticleOption(new Vector3f(Vec3.fromRGB24(0x7a6664).toVector3f()), 1.0F);
                DustCloudParticleOption cloudParticleOptions2 = new DustCloudParticleOption(new Vector3f(Vec3.fromRGB24(0xeca294).toVector3f()), 1.0F);
                for (int i = 0; i < 2; ++i) {
                    ServerParticleUtil.circularParticles(serverLevel, cloudParticleOptions, vec3.x, this.getY() + 0.25D, vec3.z, 0, 0.14D, 0, 3.0F);
                }
                ServerParticleUtil.circularParticles(serverLevel, cloudParticleOptions2, vec3.x, this.getY() + 0.25D, vec3.z, 0, 0.14D, 0, 3.0F);
            }
            this.playSound(SoundEvents.GENERIC_EXPLODE, 4.0F, 1.0F);
            this.playSound(ModSounds.HELL_BLAST_IMPACT.get(), 4.0F, 1.0F);
            this.discard();
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
                if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1){
                    return !MobUtil.ownerStack(owned0, owned1);
                }
            }
        }
        return true;
    }

}
