package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.render.HellfireTextures;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class Hellfire extends GroundProjectile {
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(Hellfire.class, EntityDataSerializers.INT);

    public Hellfire(EntityType<? extends Entity> p_i50170_1_, Level p_i50170_2_) {
        super(p_i50170_1_, p_i50170_2_);
        this.setYRot(0.0F);
        this.lifeTicks = MathHelper.secondsToTicks(7);
    }

    public Hellfire(Level world, double pPosX, double pPosY, double pPosZ, @Nullable LivingEntity owner) {
        this(ModEntityType.HELLFIRE.get(), world);
        this.setOwner(owner);
        this.setPos(pPosX, pPosY, pPosZ);
    }

    public Hellfire(Level world, BlockPos blockPos, @Nullable LivingEntity owner) {
        this(ModEntityType.HELLFIRE.get(), world);
        this.setOwner(owner);
        this.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public Hellfire(Level world, Vec3 vector3d, @Nullable LivingEntity owner) {
        this(ModEntityType.HELLFIRE.get(), world);
        this.setOwner(owner);
        this.setPos(vector3d.x(), vector3d.y(), vector3d.z());
    }

    public ResourceLocation getResourceLocation() {
        return HellfireTextures.TEXTURES.getOrDefault(this.getAnimation(), HellfireTextures.TEXTURES.get(0));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE_ID, 0);
    }

    public int getAnimation() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setAnimation(int pType) {
        this.entityData.set(DATA_TYPE_ID, pType);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setAnimation(pCompound.getInt("Animation"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Animation", this.getAnimation());
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.getAnimation() < HellfireTextures.TEXTURES.size()){
                this.setAnimation(this.getAnimation() + 1);
            } else {
                this.setAnimation(0);
            }
            --this.lifeTicks;
            for(int i = 0; i < 3; ++i) {
                this.level.addParticle(ParticleTypes.SMOKE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
            }
            if (this.level.random.nextInt(24) == 0) {
                this.level.playLocalSound((double)this.blockPosition().getX() + 0.5D, (double)this.blockPosition().getY() + 0.5D, (double)this.blockPosition().getZ() + 0.5D, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + this.level.random.nextFloat(), this.level.random.nextFloat() * 0.7F + 0.3F, false);
            }
        } else {
            if (!this.isNoGravity()) {
                MobUtil.moveDownToGround(this);
            }

            if (this.lifeTicks > 13) {
                for (LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
                    this.dealDamageTo(livingentity);
                }
            }

            if (--this.lifeTicks < 0) {
                this.discard();
            }

            if (this.isInLava()){
                this.level.broadcastEntityEvent(this, (byte)6);
                this.discard();
            }
        }

    }

    public void dealDamageTo(LivingEntity target) {
        LivingEntity owner = this.getOwner();
        float damage = 2.0F;
        if (target.isAlive() && !target.isInvulnerable()) {
            if (owner == null) {
                if (target.hurt(ModDamageSource.HELLFIRE, damage) && !target.fireImmune()) {
                    target.invulnerableTime = 15;
                }
            } else {
                if (target == owner){
                    return;
                }
                if (owner instanceof Mob mobOwner) {
                    if (mobOwner instanceof Enemy && target instanceof Enemy) {
                        if (mobOwner.getTarget() != target) {
                            return;
                        }
                    }
                    if (mobOwner instanceof IOwned owned){
                        if (owned.getTrueOwner() != null){
                            if (MobUtil.areAllies(owned.getTrueOwner(), target)){
                                return;
                            }
                        }
                    }
                }
                if (MobUtil.areAllies(owner, target)){
                    return;
                }
                if (target.hurt(ModDamageSource.hellfire(this, this.getOwner()), damage) && !target.fireImmune()) {
                    target.invulnerableTime = 15;
                }
            }
        }
    }

    public float getAnimationProgress(float pPartialTicks) {
        if (this.lifeTicks <= 24) {
            int i = this.lifeTicks;
            return 1.0F - ((24.0F - i) / 24.0F);
        } else {
            return 1.0F;
        }
    }

    public void handleEntityEvent(byte pId) {
        super.handleEntityEvent(pId);
        if (pId == 6){
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.FIRE_EXTINGUISH, this.getSoundSource(), 1.0F, 1.0F, false);
            }
        }
        if (pId == 7){
            this.lifeTicks = 26;
        }

    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
