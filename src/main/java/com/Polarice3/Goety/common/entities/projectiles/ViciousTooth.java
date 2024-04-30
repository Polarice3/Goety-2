package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class ViciousTooth extends Entity {
    private LivingEntity owner;
    private UUID ownerUUID;
    private boolean isDropping;
    public int hovering = 0;
    public float extraDamage = 0.0F;

    public ViciousTooth(EntityType<? extends Entity> p_i50170_1_, Level p_i50170_2_) {
        super(p_i50170_1_, p_i50170_2_);
    }

    public ViciousTooth(Level pLevel, LivingEntity pOwner){
        this(ModEntityType.VICIOUS_TOOTH.get(), pLevel);
        this.owner = pOwner;
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

        this.hovering = pCompound.getInt("hovering");
        this.extraDamage = pCompound.getFloat("extraDamage");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }
        pCompound.putInt("hovering", this.hovering);
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

    public void setExtraDamage(float extraDamage){
        this.extraDamage = extraDamage;
    }

    private void onHit() {
        if (!this.level.isClientSide()) {
            ServerLevel serverWorld = (ServerLevel) this.level;
            BlockState blockState = Blocks.TUFF.defaultBlockState();
            this.playSound(SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, 1.0F, 0.5F);
            serverWorld.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState), this.getX(), this.getY() + (this.getBbHeight()/2.0D), this.getZ(), 256, this.getBbWidth()/2.0D, this.getBbHeight()/2.0D, this.getBbWidth()/2.0D, 1.0D);
            if (this.isDropping){
                for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0D, 1.0D, 2.0D), this::canHitEntity)){
                    this.damageTargets(livingEntity);
                }
            }
            for (Direction direction : Direction.values()){
                if (direction.getAxis().isHorizontal()){
                    BlockPos blockPos = this.blockPosition().relative(direction);
                    ViciousPike impale = new ViciousPike(this.level, this.getOwner() != null ? this.getOwner() : null);
                    impale.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    MobUtil.moveDownToGround(impale);
                    this.level.addFreshEntity(impale);
                }
            }
        }
        this.discard();
    }

    public void damageTargets(LivingEntity livingEntity){
        float damage = SpellConfig.ViciousToothDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        damage += this.extraDamage;
        if (livingEntity != null) {
            if ((this.getOwner() != null && !livingEntity.isAlliedTo(this.getOwner()) && !this.getOwner().isAlliedTo(livingEntity)) || this.getOwner() == null) {
                livingEntity.hurt(DamageSource.indirectMagic(this, this.getOwner()), damage);
            }
        }
    }

    @Override
    public void makeStuckInBlock(BlockState p_20006_, Vec3 p_20007_) {
        super.makeStuckInBlock(p_20006_, Vec3.ZERO);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide){
            ++this.hovering;
            HitResult result = ProjectileUtil.getHitResult(this, this::canHitEntity);
            if (result.getType() != HitResult.Type.MISS) {
                if (result.getType() == HitResult.Type.ENTITY){
                    EntityHitResult result1 = (EntityHitResult) result;
                    if (result1.getEntity() instanceof LivingEntity){
                        this.damageTargets((LivingEntity) result1.getEntity());
                    }
                }
                this.onHit();
            }
            if (this.isOnGround() || this.isInWall() || this.verticalCollision || this.horizontalCollision){
                this.onHit();
            }
        } else {
            ++this.hovering;
        }
        int hoverTime = this.level.getDifficulty() != Difficulty.HARD ? MathHelper.secondsToTicks(3) : MathHelper.secondsToTicks(2);
        this.isDropping = this.hovering > hoverTime;
        if (!this.isDropping){
            this.setDeltaMovement(Vec3.ZERO);
            if (this.level instanceof ServerLevel serverLevel){
                BlockState blockState = Blocks.TUFF.defaultBlockState();
                ServerParticleUtil.circularParticles(serverLevel, new BlockParticleOption(ModParticleTypes.FAST_DUST.get(), blockState), this.getX(), this.getY(), this.getZ(), 0.25F);
            }
        } else {
            this.setDeltaMovement(this.getDeltaMovement().subtract(0.0D, 0.25D, 0.0D));
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
