package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ModMathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class IceChunk extends Entity {
    private LivingEntity owner;
    private UUID ownerUUID;
    private LivingEntity target;
    private UUID targetUUID;
    private final int distance = 4;
    private boolean isDropping;
    public float extraDamage = 0.0F;

    public IceChunk(EntityType<? extends Entity> p_i50170_1_, Level p_i50170_2_) {
        super(p_i50170_1_, p_i50170_2_);
    }

    public IceChunk(Level pLevel, LivingEntity pOwner, LivingEntity pTarget){
        this(ModEntityType.ICE_CHUNK.get(), pLevel);
        if (pTarget != null){
            this.setPos(pTarget.getX(), pTarget.getY() + distance, pTarget.getZ());
        }
        this.owner = pOwner;
        this.target = pTarget;
    }

    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        UUID ownerUUID;
        if (pCompound.hasUUID("Owner")) {
            ownerUUID = pCompound.getUUID("Owner");
        } else {
            String s = pCompound.getString("Owner");
            ownerUUID = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (ownerUUID != null) {
            this.ownerUUID = ownerUUID;
        }

        UUID targetUUID;
        if (pCompound.hasUUID("Target")) {
            targetUUID = pCompound.getUUID("Target");
        } else {
            String s = pCompound.getString("Target");
            targetUUID = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (targetUUID != null) {
            this.targetUUID = targetUUID;
        }
        this.extraDamage = pCompound.getFloat("extraDamage");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }
        if (this.targetUUID != null) {
            pCompound.putUUID("Target", this.targetUUID);
        }
        pCompound.putFloat("extraDamage", this.extraDamage);
    }

    public void setOwner(@Nullable LivingEntity p_190549_1_) {
        this.owner = p_190549_1_;
        this.ownerUUID = p_190549_1_ == null ? null : p_190549_1_.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level instanceof ServerLevel) {
            Entity entity = ((ServerLevel)this.level).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity)entity;
            }
        }

        return this.owner;
    }

    public void setTarget(@Nullable LivingEntity p_190549_1_) {
        this.target = p_190549_1_;
        this.targetUUID = p_190549_1_ == null ? null : p_190549_1_.getUUID();
    }

    @Nullable
    public LivingEntity getTarget() {
        if (this.target == null && this.targetUUID != null && this.level instanceof ServerLevel) {
            Entity entity = ((ServerLevel)this.level).getEntity(this.targetUUID);
            if (entity instanceof LivingEntity) {
                this.target = (LivingEntity)entity;
            }
        }

        return this.target;
    }

    public void setExtraDamage(float extraDamage){
        this.extraDamage = extraDamage;
    }

    private void onHit() {
        if (!this.level.isClientSide()) {
            ServerLevel serverWorld = (ServerLevel) this.level;
            BlockState blockState = Blocks.PACKED_ICE.defaultBlockState();
            this.playSound(ModSounds.ICE_CHUNK_HIT.get(), 2.0F, 1.0F);
            serverWorld.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState), this.getX(), this.getY() + (this.getBbHeight()/2.0D), this.getZ(), 256, this.getBbWidth()/2.0D, this.getBbHeight()/2.0D, this.getBbWidth()/2.0D, 1.0D);
            if (this.isDropping){
                for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0D, 0.0D, 2.0D), this::canHitEntity)){
                    this.damageTargets(livingEntity);
                }
            }
        }
        this.discard();
    }

    public void damageTargets(LivingEntity livingEntity){
        float damage = SpellConfig.IceChunkDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        damage += this.extraDamage;
        if (livingEntity != null) {
            if (livingEntity.hurt(ModDamageSource.indirectFreeze(this, this.getOwner()), damage)) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, ModMathHelper.ticksToSeconds(5), 4));
            }
        }
    }

    public void setParticleAura(ParticleOptions particleAura, float radius, double pX, double pY, double pZ){
        if (!this.level.isClientSide){
            ServerLevel serverWorld = (ServerLevel) this.level;
            float f5 = (float) Math.PI * radius * radius;
            for (int k1 = 0; (float) k1 < f5; ++k1) {
                float f6 = this.random.nextFloat() * ((float) Math.PI * 2F);
                float f7 = Mth.sqrt(this.random.nextFloat()) * radius;
                float f8 = Mth.cos(f6) * f7;
                float f9 = Mth.sin(f6) * f7;
                serverWorld.sendParticles(particleAura, pX + (double) f8, pY, pZ + (double) f9, 1, 0, 0, 0, 0);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide){
            ServerLevel serverWorld = (ServerLevel) this.level;
            HitResult result = ProjectileUtil.getHitResult(this, this::canHitEntity);
            if (result.getType() != HitResult.Type.MISS) {
                if (result.getType() == HitResult.Type.ENTITY){
                    EntityHitResult result1 = (EntityHitResult) result;
                    if (result1.getEntity() instanceof LivingEntity){
                        this.damageTargets((LivingEntity) result1.getEntity());
                    }
                    for (int i = 0; (float) i < 8; ++i) {
                        this.setParticleAura(ParticleTypes.POOF, 3.0F, result1.getEntity().getX(), result1.getEntity().getY(), result1.getEntity().getZ());
                    }
                } else if (result.getType() == HitResult.Type.BLOCK){
                    for (int i = 0; (float) i < 8; ++i) {
                        this.setParticleAura(ParticleTypes.POOF, 3.0F, this.getX(), this.getY(), this.getZ());
                    }
                }
                this.onHit();
            }
            if (this.isOnGround() || this.isInWall() || this.verticalCollision || this.horizontalCollision){
                this.onHit();
            }
            if (!this.isDropping){
                this.setParticleAura(ParticleTypes.CLOUD, 1.5F, this.getX(), this.getY(), this.getZ());
            }
            BlockPos blockpos = this.blockPosition().below();
            BlockState blockState = Blocks.SNOW_BLOCK.defaultBlockState();
            if (serverWorld.isEmptyBlock(blockpos)) {
                this.setParticleAura(new BlockParticleOption(ParticleTypes.FALLING_DUST, blockState), 1.5F, this.getX(), this.getY(), this.getZ());
            }
        }
        if (this.tickCount == 1 && !(this.isInWall() || this.isOnGround())){
            this.playSound(ModSounds.ICE_CHUNK_IDLE.get(), 1.0F, 1.0F);
        }
        int hoverTime = ModMathHelper.ticksToSeconds(5);
        boolean isHovering = this.tickCount < hoverTime;
        this.isDropping = this.tickCount > hoverTime;
        if (isHovering){
            if (this.getTarget() != null){
                this.setPos(this.getTarget().getX(), this.getTarget().getY() + distance, this.getTarget().getZ());
            }
        } else {
            if (!this.isDropping){
                this.setDeltaMovement(Vec3.ZERO);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().subtract(0.0D, 0.25D, 0.0D));
            }
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    public boolean isAttackable() {
        return false;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    protected boolean canHitEntity(Entity entity) {
        if (!entity.isSpectator() && entity.isAlive() && entity.isPickable() && !entity.noPhysics) {
            Entity owner = this.getOwner();
            return owner == null || !owner.isPassengerOfSameVehicle(entity);
        } else {
            return false;
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
