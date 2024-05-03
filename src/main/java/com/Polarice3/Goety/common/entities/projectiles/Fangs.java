package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class Fangs extends Entity {
    private static final EntityDataAccessor<Boolean> ABSORBING = SynchedEntityData.defineId(Fangs.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TOTEM = SynchedEntityData.defineId(Fangs.class, EntityDataSerializers.BOOLEAN);
    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks = 22;
    private boolean clientSideAttackStarted;
    private int damage = 0;
    private int burning = 0;
    private int soulEater = 0;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    public Fangs(EntityType<? extends Fangs> p_36923_, Level p_36924_) {
        super(p_36923_, p_36924_);
    }

    public Fangs(Level p_36926_, double p_36927_, double p_36928_, double p_36929_, float p_36930_, int p_36931_, LivingEntity p_36932_) {
        this(ModEntityType.FANG.get(), p_36926_);
        this.warmupDelayTicks = p_36931_;
        this.setOwner(p_36932_);
        this.setYRot(p_36930_ * (180F / (float)Math.PI));
        this.setPos(p_36927_, p_36928_, p_36929_);
    }

    public Fangs(Level world, double pPosX, double pPosY, double pPosZ, float pYRot, int pWarmUp, int damage, int burning, int soulEater, LivingEntity owner) {
        this(ModEntityType.FANG.get(), world);
        this.warmupDelayTicks = pWarmUp;
        this.setOwner(owner);
        this.setYRot(pYRot * (180F / (float)Math.PI));
        this.damage = damage;
        this.burning = burning;
        this.soulEater = soulEater;
        this.setTotemSpawned(true);
        this.setPos(pPosX, pPosY, pPosZ);
    }

    protected void defineSynchedData() {
        this.entityData.define(ABSORBING, false);
        this.entityData.define(TOTEM, false);
    }

    public boolean isAbsorbing() {
        return this.entityData.get(ABSORBING);
    }

    public void setAbsorbing(boolean absorbing) {
        this.entityData.set(ABSORBING, absorbing);
    }

    public boolean isTotemSpawned() {
        return this.entityData.get(TOTEM);
    }

    public void setTotemSpawned(boolean totemSpawned) {
        this.entityData.set(TOTEM, totemSpawned);
    }

    public int getSoulEater(){
        return this.soulEater;
    }

    public void setOwner(@Nullable LivingEntity p_36939_) {
        this.owner = p_36939_;
        this.ownerUUID = p_36939_ == null ? null : p_36939_.getUUID();
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

    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.warmupDelayTicks = pCompound.getInt("Warmup");
        if (pCompound.contains("Damage")){
            this.damage = pCompound.getInt("Damage");
        }
        if (pCompound.contains("Burning")){
            this.burning = pCompound.getInt("Burning");
        }
        if (pCompound.contains("SoulEater")){
            this.soulEater = pCompound.getInt("SoulEater");
        }
        if (pCompound.contains("Absorbing")){
            this.setAbsorbing(pCompound.getBoolean("Absorbing"));
        }
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
        }

    }

    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("Warmup", this.warmupDelayTicks);
        if (this.isTotemSpawned()){
            pCompound.putInt("Damage", this.damage);
            pCompound.putInt("Burning", this.burning);
        }
        if (this.soulEater > 0){
            pCompound.putInt("SoulEater", this.soulEater);
        }
        if (this.isAbsorbing()){
            pCompound.putBoolean("Absorbing", this.isAbsorbing());
        }
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }

    }
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.clientSideAttackStarted) {
                --this.lifeTicks;
                if (this.lifeTicks == 14) {
                    for(int i = 0; i < 12; ++i) {
                        double d0 = this.getX() + (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.getBbWidth() * 0.5D;
                        double d1 = this.getY() + 0.05D + this.random.nextDouble();
                        double d2 = this.getZ() + (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.getBbWidth() * 0.5D;
                        double d3 = (this.random.nextDouble() * 2.0D - 1.0D) * 0.3D;
                        double d4 = 0.3D + this.random.nextDouble() * 0.3D;
                        double d5 = (this.random.nextDouble() * 2.0D - 1.0D) * 0.3D;
                        this.level.addParticle(ParticleTypes.CRIT, d0, d1 + 1.0D, d2, d3, d4, d5);
                    }
                }
            }
        } else if (--this.warmupDelayTicks < 0) {
            if (this.warmupDelayTicks == -8) {
                for(LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2D, 0.0D, 0.2D))) {
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

    private void dealDamageTo(LivingEntity target) {
        LivingEntity livingentity = this.getOwner();
        float baseDamage = SpellConfig.FangDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        if (target.isAlive() && !target.isInvulnerable() && target != livingentity) {
            if (livingentity == null) {
                target.hurt(DamageSource.MAGIC, baseDamage);
            } else {
                if (target.isAlliedTo(livingentity)){
                    return;
                }
                if (livingentity.isAlliedTo(target)) {
                    return;
                }
                if (livingentity instanceof Player player){
                    if (this.isTotemSpawned()){
                        target.hurt(DamageSource.indirectMagic(this, livingentity), baseDamage + damage);
                        if (burning > 0){
                            target.setSecondsOnFire(5 * burning);
                        }
                    } else {
                        float enchantment = 0;
                        int burning = 0;
                        if (WandUtil.enchantedFocus(player)) {
                            enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), player);
                            burning = WandUtil.getLevels(ModEnchantments.BURNING.get(), player);
                        }
                        if (target.hurt(DamageSource.indirectMagic(this, livingentity), baseDamage + enchantment)){
                            int soulEater = Mth.clamp(this.getSoulEater(), 0, 10);
                            SEHelper.increaseSouls(player, SpellConfig.FangGainSouls.get() * soulEater);
                            if (burning > 0){
                                target.setSecondsOnFire(5 * burning);
                            }
                        }
                    }
                } else {
                    target.hurt(DamageSource.indirectMagic(this, livingentity), baseDamage);
                }
            }
        }
    }

    public void handleEntityEvent(byte p_36935_) {
        super.handleEntityEvent(p_36935_);
        if (p_36935_ == 4) {
            this.clientSideAttackStarted = true;
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.EVOKER_FANGS_ATTACK, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
            }
        }

    }

    public float getAnimationProgress(float p_36937_) {
        if (!this.clientSideAttackStarted) {
            return 0.0F;
        } else {
            int i = this.lifeTicks - 2;
            return i <= 0 ? 1.0F : 1.0F - ((float)i - p_36937_) / 20.0F;
        }
    }

    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
