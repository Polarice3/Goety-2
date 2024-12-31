package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.GatherTrailParticle;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.SpellExplosion;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class BouncyBubble extends SpellHurtingProjectile{
    private static final EntityDataAccessor<Integer> BOUNCE_TIMES = SynchedEntityData.defineId(BouncyBubble.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(BouncyBubble.class, EntityDataSerializers.FLOAT);
    public float damage = SpellConfig.BouncyBubbleDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();

    public BouncyBubble(EntityType<? extends AbstractHurtingProjectile> p_36833_, Level p_36834_) {
        super(p_36833_, p_36834_);
    }

    public BouncyBubble(double pX, double pY, double pZ, double pXPower, double pYPower, double pZPower, Level pLevel) {
        super(ModEntityType.BOUNCY_BUBBLE.get(), pX, pY, pZ, pXPower, pYPower, pZPower, pLevel);
    }

    public BouncyBubble(LivingEntity pShooter, double pXPower, double pYPower, double pZPower, Level pLevel) {
        super(ModEntityType.BOUNCY_BUBBLE.get(), pShooter, pXPower, pYPower, pZPower, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BOUNCE_TIMES, 0);
        this.entityData.define(SIZE, 0.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("BounceTimes", this.getBounceTimes());
        compound.putFloat("Size", this.getSize());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setBounceTimes(compound.getInt("BounceTimes"));
        this.setSize(compound.getFloat("Size"));
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33134_) {
        if (SIZE.equals(p_33134_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_33134_);
    }

    public EntityDimensions getDimensions(Pose p_33113_) {
        float i = this.getSize();
        EntityDimensions entitydimensions = super.getDimensions(p_33113_);
        return entitydimensions.scale(i);
    }

    public int getBounceTimes() {
        return this.entityData.get(BOUNCE_TIMES);
    }

    public void setBounceTimes(int bounce) {
        this.entityData.set(BOUNCE_TIMES, bounce);
    }

    public float getSize() {
        return this.entityData.get(SIZE);
    }

    public void setSize(float size) {
        this.entityData.set(SIZE, size);
    }

    @Override
    protected float getInertia() {
        return 0.6F + Math.min(this.boltSpeed, 0.4F);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount >= 100) {
            this.explode();
        }

    }

    @Override
    public float getGravity() {
        if (!this.isInWater()){
            return 0.03F;
        } else {
            return super.getGravity();
        }
    }

    @Override
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

    protected ParticleOptions getTrailParticle() {
        return ModParticleTypes.NONE.get();
    }

    @Override
    protected void onHit(HitResult result) {
        HitResult.Type raytraceresult$type = result.getType();
        Vec3 vec3 = this.getDeltaMovement();
        double motionX = vec3.x();
        double motionY = vec3.y();
        double motionZ = vec3.z();
        if (raytraceresult$type == HitResult.Type.BLOCK) {
            BlockHitResult hitResult = (BlockHitResult) result;
            BlockState blockstate = this.level.getBlockState(hitResult.getBlockPos());
            if (!blockstate.getCollisionShape(this.level, hitResult.getBlockPos()).isEmpty()) {
                Direction face = hitResult.getDirection();
                blockstate.onProjectileHit(this.level, blockstate, hitResult, this);
                if (face.getAxis() == Direction.Axis.X) {
                    motionX = -motionX;
                } else if (face.getAxis() == Direction.Axis.Z) {
                    motionZ = -motionZ;
                } else if (face.getAxis().isVertical()) {
                    motionY = -motionY;
                }
                this.setDeltaMovement(motionX, motionY, motionZ);
                this.xPower = motionX;
                this.yPower = motionY;
                this.zPower = motionZ;
                if (this.getBounceTimes() >= 6) {
                    this.explode();
                } else {
                    this.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    this.setBounceTimes(this.getBounceTimes() + 1);
                }
            }
        } else if (raytraceresult$type == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) result).getEntity();
            if (!this.level.isClientSide) {
                if (canHitEntity(entity)) {
                    if (this.getBounceTimes() >= 6) {
                        this.explode();
                    } else {
                        Vec3 vec31 = entity.getLookAngle();
                        this.setDeltaMovement(vec31.scale(0.1D));
                        this.xPower = vec31.x * 0.1D;
                        this.yPower = vec31.y * 0.1D;
                        this.zPower = vec31.z * 0.1D;
                        entity.hurt(ModDamageSource.indirectDrench(this, this.getOwner() == null ? this : this.getOwner()), this.damage + this.getExtraDamage());
                        this.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                        this.setBounceTimes(this.getBounceTimes() + 1);
                    }
                }
            }
        }
    }

    public void explode(){
        if (!this.level.isClientSide) {
            Entity owner = this.getOwner();
            float radius = 1.0F + this.getSize();
            int radius2 = Mth.floor(radius);
            new SpellExplosion(this.level, this, ModDamageSource.indirectDrench(this, owner == null ? this : owner), this.getX(), this.getY(), this.getZ(), radius, this.damage + this.getExtraDamage());
            if (this.level instanceof ServerLevel serverLevel) {
                for (int i = -radius2; i < radius2; ++i) {
                    for (int j = -radius2; j < radius2; ++j) {
                        for (int k = -radius2; k < radius2; ++k) {
                            Vec3 vec3 = this.position().add(i, j, k);
                            serverLevel.sendParticles(ParticleTypes.SPLASH, vec3.x(), vec3.y() + 0.5F, vec3.z(), 0, 0, 0.04D, 0, 0.5F);
                        }
                    }
                }
                for (int i = 0; i < 8; ++i){
                    ColorUtil colorUtil = new ColorUtil(ChatFormatting.BLUE);
                    if (this.isInWater()){
                        colorUtil = new ColorUtil(ChatFormatting.AQUA);
                    }
                    Vec3 vector3d1 = this.position().offsetRandom(serverLevel.getRandom(), radius * 2.0F);
                    serverLevel.sendParticles(new GatherTrailParticle.Option(colorUtil, vector3d1), this.getX(), this.getY(), this.getZ(), 0, 0.0F, 0.0F, 0.0F, 0.5F);
                }
            }

            this.playSound(SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, 64.0F, 1.0F);
            this.playSound(SoundEvents.GENERIC_SPLASH, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.discard();
        }
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource p_37616_, float p_37617_) {
        return false;
    }

    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
