package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class ViciousPike extends Entity {
    private LivingEntity owner;
    private UUID ownerUUID;
    private boolean sentSpikeEvent;
    private int lifeTicks = 22;
    public AnimationState mainAnimationState = new AnimationState();

    public ViciousPike(EntityType<? extends Entity> p_i50170_1_, Level p_i50170_2_) {
        super(p_i50170_1_, p_i50170_2_);
    }

    public ViciousPike(Level pLevel, LivingEntity pOwner){
        this(ModEntityType.VICIOUS_PIKE.get(), pLevel);
        this.owner = pOwner;
    }

    public ViciousPike(Level pLevel, double x, double y, double z, LivingEntity pOwner) {
        this(ModEntityType.VICIOUS_PIKE.get(), pLevel);
        this.setOwner(pOwner);
        this.setPos(x, y, z);
    }

    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }
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
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            --this.lifeTicks;
        } else {
            if (this.tickCount == 2) {
                for (LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2D, 0.0D, 0.2D))) {
                    this.dealDamageTo(livingentity);
                }
            }

            if (!this.sentSpikeEvent) {
                this.level.broadcastEntityEvent(this, (byte)4);
                this.sentSpikeEvent = true;
            }

            if (--this.lifeTicks < 0) {
                this.discard();
            }
        }

    }

    @Override
    public void makeStuckInBlock(BlockState p_20006_, Vec3 p_20007_) {
        super.makeStuckInBlock(p_20006_, Vec3.ZERO);
    }

    private void dealDamageTo(LivingEntity target) {
        LivingEntity livingentity = this.getOwner();
        float baseDamage = SpellConfig.FangDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        if (target.isAlive() && !target.isInvulnerable() && MobUtil.validEntity(target) && target != livingentity) {
            if (livingentity != null) {
                if (target.isAlliedTo(livingentity)) {
                    return;
                }
                if (livingentity.isAlliedTo(target)) {
                    return;
                }
                target.hurt(damageSources().indirectMagic(this, livingentity), baseDamage);
            } else {
                target.hurt(damageSources().magic(), baseDamage);
            }
            if (!target.hasImpulse) {
                MobUtil.push(target, 0, 1, 0);
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 4, false, false));
            }
        }
    }

    public void handleEntityEvent(byte p_36935_) {
        super.handleEntityEvent(p_36935_);
        if (p_36935_ == 4) {
            this.mainAnimationState.start(this.tickCount);
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.IMPALE.get(), this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
            }
        }

    }

    public float getAnimationProgress(float p_36937_) {
        int i = this.lifeTicks;
        return i <= 0 ? 1.0F : 1.0F - ((float)i - p_36937_) / 20.0F;
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
