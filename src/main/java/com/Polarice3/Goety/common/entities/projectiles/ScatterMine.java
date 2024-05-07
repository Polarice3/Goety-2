package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.PulsatingCircleParticleOption;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class ScatterMine extends Entity {
    public static final EntityDataAccessor<Boolean> DATA_SPELL = SynchedEntityData.defineId(ScatterMine.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> DATA_EXTRA_DAMAGE = SynchedEntityData.defineId(ScatterMine.class, EntityDataSerializers.FLOAT);
    public float getGlow = 1;
    public float glowAmount = 0.0045F;
    public float size = 3.0F;
    private LivingEntity owner;
    private UUID ownerUUID;
    public int lifeTicks = MathHelper.secondsToTicks(10);

    public ScatterMine(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public ScatterMine(Level pLevel, LivingEntity pOwner, BlockPos blockPos){
        this(ModEntityType.SCATTER_MINE.get(), pLevel);
        if (blockPos != null){
            this.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }
        this.setOwner(pOwner);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_SPELL, false);
        this.entityData.define(DATA_EXTRA_DAMAGE, 0.0F);
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
        if (pCompound.contains("LifeTicks")){
            this.lifeTicks = pCompound.getInt("LifeTicks");
        }
        if (pCompound.contains("CurrentTicks")){
            this.tickCount = pCompound.getInt("CurrentTicks");
        }
        if (pCompound.contains("Spell")){
            this.setSpell(pCompound.getBoolean("Spell"));
        }
        if (pCompound.contains("ExtraDamage")) {
            this.setExtraDamage(pCompound.getFloat("ExtraDamage"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }
        pCompound.putInt("LifeTicks", this.lifeTicks);
        pCompound.putInt("CurrentTicks", this.tickCount);
        pCompound.putBoolean("Spell", this.isSpell());
        pCompound.putFloat("ExtraDamage", this.getExtraDamage());
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

    public boolean isSpell(){
        return this.entityData.get(DATA_SPELL);
    }

    public void setSpell(boolean spell){
        this.entityData.set(DATA_SPELL, spell);
    }

    public void setIsSpell(){
        this.setSpell(true);
        this.level.broadcastEntityEvent(this, (byte) 7);
    }

    public float getExtraDamage() {
        return this.entityData.get(DATA_EXTRA_DAMAGE);
    }

    public void setExtraDamage(float pDamage) {
        this.entityData.set(DATA_EXTRA_DAMAGE, pDamage);
    }

    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.level.addParticle(ModParticleTypes.ELECTRIC.get(), this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), -0.05D + this.random.nextDouble() * 0.05D, -0.05D + this.random.nextDouble() * 0.05D, -0.05D + this.random.nextDouble() * 0.05D);
        } else if (id == 5){
            for(int i = 0; i < 20; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                double d3 = 10.0D;
                this.level.addParticle(ParticleTypes.SMOKE, this.getRandomX(1.0D) - d0 * d3, this.getRandomY() - d1 * d3, this.getRandomZ(1.0D) - d2 * d3, d0, d1, d2);
            }
        } else if (id == 6){
            this.finalizeExplosion();
        } else if (id == 7){
            this.setSpell(true);
        } else {
            super.handleEntityEvent(id);
        }
    }

    public boolean startGlow(){
        return this.lifeTicks < MathHelper.secondsToTicks(10) - 5;
    }

    public boolean startShrink(){
        return this.lifeTicks < MathHelper.secondsToTicks(10) - 2;
    }

    @Override
    public void tick() {
        super.tick();
        --this.lifeTicks;
        if (this.level.isClientSide){
            if (this.startGlow()) {
                this.glow();
            }
            if (this.startShrink()){
                this.shrink();
            }
        }
        if (!this.level.isClientSide) {
            if (!this.isNoGravity()) {
                MobUtil.moveDownToGround(this);
            }
            this.level.broadcastEntityEvent(this, (byte) 4);
            if (this.lifeTicks <= 0){
                if (!this.isSpell()) {
                    this.level.broadcastEntityEvent(this, (byte) 5);
                    this.discard();
                } else {
                    this.trigger();
                }
            } else if (this.tickCount >= 20){
                if (this.tickCount % 10 == 0) {
                    if (this.level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(new PulsatingCircleParticleOption(1.0F), this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0.5F);
                    }
                }
                for (LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.6D, 0.3D, 0.6D))) {
                    if (livingentity.isAlive() && !livingentity.isInvulnerable()) {
                        if (this.getOwner() != null) {
                            if (!MobUtil.areAllies(this.getOwner(), livingentity) && livingentity != this.getOwner()) {
                                this.trigger();
                            }
                        } else {
                            this.trigger();
                        }
                    }
                }
            }
        }
    }

    private void glow(){
        this.getGlow = Mth.clamp(this.getGlow - this.glowAmount, 0, 1);
    }

    private void shrink(){
        this.size = Mth.clamp(this.size - 0.5F, 1.25F, 3.0F);
    }

    public void trigger(){
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte) 6);
            for (LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.6D, 0.3D, 0.6D))) {
                if (livingentity.isAlive() && !livingentity.isInvulnerable()) {
                    if (this.getOwner() != null) {
                        if (!MobUtil.areAllies(this.getOwner(), livingentity) && livingentity != this.getOwner()) {
                            this.explodeDamage(livingentity);
                        }
                    } else {
                        this.explodeDamage(livingentity);
                    }
                }
            }
            this.discard();
        }
    }

    public void explodeDamage(LivingEntity livingEntity) {
        if (!this.level.isClientSide) {
            LivingEntity owner = null;
            float damage = SpellConfig.ScatterMineDamage.get().floatValue();
            if (this.getOwner() instanceof Mob){
                owner = this.getOwner();
                if (this.getOwner().getAttribute(Attributes.ATTACK_DAMAGE) != null && this.getOwner().getAttributeValue(Attributes.ATTACK_DAMAGE) > 0.0F){
                    damage = (float) (this.getOwner().getAttributeValue(Attributes.ATTACK_DAMAGE)/* / 1.666667F*/);
                }
            }
            damage += this.getExtraDamage();
            livingEntity.hurt(DamageSource.explosion(owner), damage);
        }
    }

    private void createParticleBall(double p_106779_, int radius) {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();

        for(int i = -radius; i <= radius; ++i) {
            for(int j = -radius; j <= radius; ++j) {
                for(int k = -radius; k <= radius; ++k) {
                    double d3 = (double)j + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                    double d4 = (double)i + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                    double d5 = (double)k + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                    double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5) / p_106779_ + this.random.nextGaussian() * 0.05D;
                    this.level.addParticle(ModParticleTypes.REDSTONE_EXPLODE.get(), d0, d1, d2, d3 / d6, d4 / d6, d5 / d6);
                    if (i != -radius && i != radius && j != -radius && j != radius) {
                        k += radius * 2 - 1;
                    }
                }
            }
        }

    }

    public void finalizeExplosion() {
        if (this.level.isClientSide) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.REDSTONE_EXPLODE.get(), SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
        }
        this.createParticleBall(0.5D, (int) 2.0F);
        this.level.addParticle(ModParticleTypes.BIG_ELECTRIC.get(), this.getX(), this.getY(), this.getZ(), 1.0D, 0.0D, 0.0D);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
