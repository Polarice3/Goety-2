package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.GlacialWall;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
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

public class IceChunk extends SpellEntity {
    private final int distance = 4;
    private boolean isDropping;
    public int hovering = 0;

    public IceChunk(EntityType<? extends Entity> p_i50170_1_, Level p_i50170_2_) {
        super(p_i50170_1_, p_i50170_2_);
    }

    public IceChunk(Level pLevel, LivingEntity pOwner, LivingEntity pTarget){
        this(ModEntityType.ICE_CHUNK.get(), pLevel);
        if (pTarget != null){
            this.setPos(pTarget.getX(), pTarget.getY() + distance, pTarget.getZ());
        }
        this.setOwner(pOwner);
        if (!MobUtil.areAllies(pOwner, pTarget)) {
            this.setTarget(pTarget);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.hovering = pCompound.getInt("hovering");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("hovering", this.hovering);
    }

    private void onHit(HitResult hitResult) {
        if (!this.level.isClientSide()) {
            ServerLevel serverWorld = (ServerLevel) this.level;
            BlockState blockState = Blocks.PACKED_ICE.defaultBlockState();
            double y = this.getY();
            if (hitResult instanceof EntityHitResult entityHitResult){
                Entity entity = entityHitResult.getEntity();
                y = entity.getY();
            }
            this.playSound(ModSounds.ICE_CHUNK_HIT.get(), 2.0F, 1.0F);
            serverWorld.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState), this.getX(), y + (this.getBbHeight()/2.0D), this.getZ(), 256, this.getBbWidth()/2.0D, this.getBbHeight()/2.0D, this.getBbWidth()/2.0D, 1.0D);
            ColorUtil colorUtil = new ColorUtil(0xce5dff);
            serverWorld.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 4.0F, 1), this.getX(), y, this.getZ(), 1, 0, 0, 0, 0.5D);
            for (int i = 0; (float) i < 8; ++i) {
                this.setParticleAura(ParticleTypes.POOF, 1.0F, this.getX(), y, this.getZ());
            }
            if (this.isDropping){
                for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0D, 1.0D, 2.0D), this::canHitEntity)){
                    this.damageTargets(livingEntity);
                }
            }
        }
        this.discard();
    }

    public void damageTargets(LivingEntity livingEntity){
        float damage = SpellConfig.IceChunkDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        damage += this.getExtraDamage();
        if (livingEntity != null) {
            if (livingEntity.hurt(ModDamageSource.indirectFreeze(this, this.getOwner()), damage)) {
                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.STUNNED.get(), MathHelper.secondsToTicks(2)));
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
            ++this.hovering;
            ServerLevel serverWorld = (ServerLevel) this.level;
            HitResult result = ProjectileUtil.getHitResult(this, this::canHitEntity);
            if (result.getType() != HitResult.Type.MISS) {
                if (result.getType() == HitResult.Type.ENTITY){
                    EntityHitResult result1 = (EntityHitResult) result;
                    if (result1.getEntity() instanceof LivingEntity){
                        this.damageTargets((LivingEntity) result1.getEntity());
                    }
                }
                this.onHit(result);
            }
            if (this.isOnGround() || this.isInWall() || this.verticalCollision || this.horizontalCollision){
                this.onHit(result);
            }
            if (!this.isStarting()) {
                if (!this.isDropping) {
                    this.setParticleAura(ParticleTypes.CLOUD, 1.5F, this.getX(), this.getY(), this.getZ());
                }
                BlockPos blockpos = this.blockPosition().below();
                BlockState blockState = Blocks.SNOW_BLOCK.defaultBlockState();
                if (serverWorld.isEmptyBlock(blockpos)) {
                    this.setParticleAura(new BlockParticleOption(ParticleTypes.FALLING_DUST, blockState), 1.5F, this.getX(), this.getY(), this.getZ());
                }
            }
            if (this.hovering == 20){
                for(int k = 0; k < 60; ++k) {
                    float f2 = random.nextFloat() * 4.0F;
                    float f1 = random.nextFloat() * ((float)Math.PI * 2F);
                    double d1 = Mth.cos(f1) * f2;
                    double d2 = 0.01D + random.nextDouble() * 0.5D;
                    double d3 = Mth.sin(f1) * f2;
                    serverWorld.sendParticles(ParticleTypes.POOF, this.getX() + d1 * 0.1D, this.getY() + 0.3D, this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.25F);
                }
            }
        } else {
            ++this.hovering;
        }
        if (this.hovering == 1 && !(this.isInWall() || this.isOnGround())){
            this.playSound(ModSounds.ICE_CHUNK_IDLE.get(), 1.0F, 1.0F);
        }
        int hoverTime = MathHelper.secondsToTicks(5);
        if (this.isStaff()){
            hoverTime = (int) MathHelper.secondsToTicks(1.75F);
        }
        boolean isHovering = !this.isStarting() && this.hovering < hoverTime;
        this.isDropping = this.hovering > hoverTime;
        if (this.hovering == hoverTime - 15){
            this.playSound(ModSounds.ICE_CHUNK_DROP.get(), 1.0F, 1.0F);
        }
        if (isHovering){

            // Ice Chunk Movement code based of @Infamous-Misadventures Dungeon Mobs' Ice Cloud positioning codes.
            float speed = 0.175F;
            if (this.getTarget() != null && this.getTarget().isAlive()){
                this.setDeltaMovement(Vec3.ZERO);
                double d0 = this.getTarget().getX() - this.getX();
                double d1 = (this.getTarget().getY() + this.distance) - this.getY();
                double d2 = this.getTarget().getZ() - this.getZ();
                double d = Math.sqrt((d0 * d0 + d2 * d2));
                double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                if (d > 0.5){
                    this.setDeltaMovement(this.getDeltaMovement().add(d0 / d3, d1 / d3, d2 / d3).scale(speed));
                }
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

    public boolean isStarting(){
        return this.hovering < 20;
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

    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity p_20303_) {
        return super.canCollideWith(p_20303_) && !(p_20303_ instanceof GlacialWall);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    protected boolean canHitEntity(Entity entity) {
        if (!entity.isSpectator() && entity.isAlive() && entity.isPickable() && !entity.noPhysics && !(entity instanceof IceChunk)) {
            Entity owner = this.getOwner();
            return owner == null || !owner.isPassengerOfSameVehicle(entity) && !MobUtil.areAllies(owner, entity);
        } else {
            return false;
        }
    }
}
