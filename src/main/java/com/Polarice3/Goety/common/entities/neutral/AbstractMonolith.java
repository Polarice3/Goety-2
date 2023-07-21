package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.utils.MobUtil;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractMonolith extends Owned{
    protected static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(AbstractMonolith.class, EntityDataSerializers.INT);
    private boolean activate;

    public AbstractMonolith(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AGE, 0);
    }

    public void addAdditionalSaveData(CompoundTag p_31485_) {
        super.addAdditionalSaveData(p_31485_);
        p_31485_.putInt("Age", this.getAge());
        p_31485_.putBoolean("Activate", this.isActivate());
    }

    public void readAdditionalSaveData(CompoundTag p_31474_) {
        super.readAdditionalSaveData(p_31474_);
        this.setAge(p_31474_.getInt("Age"));
        this.setActivate(p_31474_.getBoolean("Activate"));
    }

    public abstract BlockState getState();

    public ParticleOptions getParticles(){
        return new BlockParticleOption(ParticleTypes.BLOCK, this.getState());
    }

    public void setAge(int age){
        this.entityData.set(AGE, age);
    }

    public int getAge(){
        return this.entityData.get(AGE);
    }

    public void setActivate(boolean activate){
        this.activate = activate;
    }

    public boolean isActivate(){
        return this.activate;
    }

    public boolean isInvulnerableTo(DamageSource p_219427_) {
        return this.isEmerging() && !p_219427_.isBypassInvul() || super.isInvulnerableTo(p_219427_);
    }

    public static float getEmergingTime(){
        return 60.0F;
    }

    public boolean isEmerging() {
        return this.getAge() < getEmergingTime() && !this.isActivate();
    }

    public boolean isDescending(){
        return this.getAge() > 0 && this.isActivate();
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    public void push(Entity entityIn) {
    }

    @Override
    public void move(MoverType p_213315_1_, Vec3 p_213315_2_) {
    }

    public int getAgeSpeed(){
        return 1;
    }

    public void aiStep() {
        super.aiStep();
        if (!this.isNoGravity()) {
            MobUtil.moveDownToGround(this);
        }
        if (this.isEmerging()){
            if (!this.level.isClientSide) {
                this.setAge(this.getAge() + this.getAgeSpeed());
                this.level.broadcastEntityEvent(this, (byte) 4);
            }
        }
    }

    public void handleEntityEvent(byte pId) {
        if (pId == 4){
            this.setAge(this.getAge() + this.getAgeSpeed());
        } else {
            super.handleEntityEvent(pId);
        }
    }

    public AbstractMonolith.Crackiness getCrackiness() {
        return AbstractMonolith.Crackiness.byFraction(this.getHealth() / this.getMaxHealth());
    }

    public static enum Crackiness {
        NONE(1.0F),
        LOW(0.75F),
        MEDIUM(0.5F),
        HIGH(0.25F);

        private static final List<AbstractMonolith.Crackiness> BY_DAMAGE = Stream.of(values()).sorted(Comparator.comparingDouble((p_28904_) -> {
            return (double)p_28904_.fraction;
        })).collect(ImmutableList.toImmutableList());
        private final float fraction;

        private Crackiness(float p_28900_) {
            this.fraction = p_28900_;
        }

        public static AbstractMonolith.Crackiness byFraction(float p_28902_) {
            for(AbstractMonolith.Crackiness irongolem$crackiness : BY_DAMAGE) {
                if (p_28902_ < irongolem$crackiness.fraction) {
                    return irongolem$crackiness;
                }
            }

            return NONE;
        }
    }
}
