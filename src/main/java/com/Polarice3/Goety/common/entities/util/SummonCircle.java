package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class SummonCircle extends Entity {
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(SummonCircle.class, EntityDataSerializers.OPTIONAL_UUID);
    public Entity entity;
    public boolean preMade;
    public boolean noPos;
    public boolean noSpin = false;
    public boolean noParticles = false;
    public int lifeSpan = 20;

    public SummonCircle(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public SummonCircle(Level pLevel, Vec3 pPos, Entity pEntity, boolean preMade, boolean noPos, LivingEntity pOwner){
        this(ModEntityType.SUMMON_CIRCLE.get(), pLevel);
        this.setPos(pPos.x, pPos.y, pPos.z);
        this.entity = pEntity;
        this.preMade = preMade;
        this.noPos = noPos;
        this.setTrueOwner(pOwner);
    }

    public SummonCircle(Level pLevel, BlockPos pPos, Entity pEntity, boolean preMade, boolean noPos, LivingEntity pOwner){
        this(ModEntityType.SUMMON_CIRCLE.get(), pLevel);
        this.setPos(pPos.getX(), pPos.getY(), pPos.getZ());
        this.entity = pEntity;
        this.preMade = preMade;
        this.noPos = noPos;
        this.setTrueOwner(pOwner);
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        UUID uuid;
        if (pCompound.hasUUID("Owner")) {
            uuid = pCompound.getUUID("Owner");
        } else {
            String s = pCompound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.setOwnerId(uuid);
            } catch (Throwable ignored) {
            }
        }
        this.preMade = pCompound.getBoolean("preMade");
        this.noPos = pCompound.getBoolean("noPos");
        if (pCompound.contains("noSpin")) {
            this.setNoSpin(pCompound.getBoolean("noSpin"));
        }
        if (pCompound.contains("noParticles")) {
            this.noParticles = pCompound.getBoolean("noParticles");
        }
        this.lifeSpan = pCompound.getInt("lifeSpan");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (this.getOwnerId() != null) {
            pCompound.putUUID("Owner", this.getOwnerId());
        }
        pCompound.putBoolean("preMade", this.preMade);
        pCompound.putBoolean("noPos", this.noPos);
        pCompound.putBoolean("noSpin", this.noSpin);
        pCompound.putBoolean("noParticles", this.noParticles);
        pCompound.putInt("lifeSpan", this.lifeSpan);
    }

    public LivingEntity getTrueOwner() {
        try {
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @Nullable
    public UUID getOwnerId() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse((UUID)null);
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    public void setTrueOwner(LivingEntity livingEntity){
        this.setOwnerId(livingEntity.getUUID());
    }

    public void setLifeSpan(int lifeSpan){
        this.lifeSpan = lifeSpan;
    }

    public void setNoSpin(boolean spin){
        this.noSpin = spin;
        if (spin){
            this.level.broadcastEntityEvent(this, (byte) 4);
        } else {
            this.level.broadcastEntityEvent(this, (byte) 5);
        }
    }

    public int getLifeSpan() {
        if (this.lifeSpan == 0){
            return 20;
        }
        return this.lifeSpan;
    }

    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    public void tick() {
        super.tick();
        if (!this.isNoGravity()) {
            MobUtil.moveDownToGround(this);
        }
        if (this.level instanceof ServerLevel serverWorld) {
            float f = 1.5F;
            float f5 = (float) Math.PI * f * f;
            if (this.tickCount == this.getLifeSpan()){
                if (!this.noParticles) {
                    for (int j1 = 0; j1 < 16; ++j1) {
                        for (int k1 = 0; (float) k1 < f5; ++k1) {
                            float f6 = this.random.nextFloat() * ((float) Math.PI * 2F);
                            float f7 = Mth.sqrt(this.random.nextFloat()) * f;
                            float f8 = Mth.cos(f6) * f7;
                            float f9 = Mth.sin(f6) * f7;
                            serverWorld.sendParticles(ParticleTypes.REVERSE_PORTAL, this.getX() + (double) f8, this.getY(), this.getZ() + (double) f9, 0, 0, 0.5D, 0, 0.5F);
                        }
                    }
                }
                if (this.entity != null){
                    if (this.noPos) {
                        this.entity.setPos(this.getX(), this.getY(), this.getZ());
                    }
                    if (this.preMade) {
                        if (this.entity instanceof TamableAnimal && this.getOwnerId() != null) {
                            ((TamableAnimal) this.entity).setOwnerUUID(this.getOwnerId());
                        }
                        if (this.entity instanceof IOwned && this.getTrueOwner() != null) {
                            ((IOwned) this.entity).setTrueOwner(this.getTrueOwner());
                        }
                        if (this.entity instanceof Mob) {
                            ((Mob) this.entity).finalizeSpawn(serverWorld, this.level.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                            if (this.getTrueOwner() != null && this.getTrueOwner() instanceof Mob) {
                                if (((Mob) this.getTrueOwner()).getTarget() != null) {
                                    ((Mob) this.entity).setTarget(((Mob) this.getTrueOwner()).getTarget());
                                }
                            }
                        }
                    }
                    serverWorld.addFreshEntity(entity);
                }
            }
        }
        if (this.tickCount >= this.getLifeSpan()){
            this.playSound(ModSounds.SUMMON_SPELL.get(), 1.0F, 1.0F);
            this.discard();
        }
    }

    @Override
    public void handleEntityEvent(byte p_19882_) {
        if (p_19882_ == 4){
            this.noSpin = true;
        } else if (p_19882_ == 5){
            this.noSpin = false;
        } else {
            super.handleEntityEvent(p_19882_);
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
