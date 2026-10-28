package com.Polarice3.Goety.common.entities.hostile.illagers;

import com.Polarice3.Goety.api.entities.IEnchanter;
import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.ai.enchanter.EnchanterEnchantGoal;
import com.Polarice3.Goety.common.entities.ai.enchanter.EnchanterFleeToAllyGoal;
import com.Polarice3.Goety.common.entities.ai.enchanter.EnchanterMeleeGoal;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Enchanter extends SpellcasterIllager implements IEnchanter {
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(Enchanter.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHOW_ARMS = SynchedEntityData.defineId(Enchanter.class, EntityDataSerializers.BOOLEAN);
    public int enchantCool = 0;
    public int enchantTime = 0;
    public List<UUID> enchantList = new ArrayList<>();
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState enchantAnimationState = new AnimationState();
    public AnimationState noveltyAnimationState = new AnimationState();

    public Enchanter(EntityType<? extends SpellcasterIllager> p_33724_, Level p_33725_) {
        super(p_33724_, p_33725_);
        this.xpReward = 10;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new EnchanterEnchantGoal<>(this));
        this.goalSelector.addGoal(1, new EnchanterMeleeGoal<>(this));
        this.goalSelector.addGoal(2, new EnchanterFleeToAllyGoal<>(this));
        this.goalSelector.addGoal(3, new AvoidTargetGoal<>(this, LivingEntity.class, 7.0F, 1.0D, 1.0D){
            @Override
            public boolean canUse() {
                return super.canUse() && this.toAvoid != null && Enchanter.this.distanceTo(this.toAvoid) > 2.2F;
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && this.toAvoid != null && Enchanter.this.distanceTo(this.toAvoid) > 2.2F;
            }
        });
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.15D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.EnchanterServantDamage.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.EnchanterServantFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.EnchanterServantArmor.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.EnchanterServantHealth.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.EnchanterServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.EnchanterServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.EnchanterServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.EnchanterServantFollowRange.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, 0);
        this.entityData.define(SHOW_ARMS, false);
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_) {
    }

    public void readAdditionalSaveData(CompoundTag p_33732_) {
        super.readAdditionalSaveData(p_33732_);
        this.readEnchanterData(p_33732_);
    }

    public void addAdditionalSaveData(CompoundTag p_33734_) {
        super.addAdditionalSaveData(p_33734_);
        this.addEnchanterData(p_33734_);
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, IDLE)){
            return 0;
        } else if (Objects.equals(animation, ATTACK)){
            return 1;
        } else if (Objects.equals(animation, ENCHANT)){
            return 2;
        } else if (Objects.equals(animation, NOVELTY)){
            return 3;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> list = new ArrayList<>();
        list.add(this.idleAnimationState);
        list.add(this.attackAnimationState);
        list.add(this.enchantAnimationState);
        list.add(this.noveltyAnimationState);
        return list;
    }

    public int getCurrentAnimation(){
        return this.entityData.get(ANIM_STATE);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor)) {
            if (this.level.isClientSide){
                switch (this.entityData.get(ANIM_STATE)){
                    case 0:
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 1:
                        this.attackAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                    case 2:
                        this.enchantAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.enchantAnimationState);
                        break;
                    case 3:
                        this.noveltyAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.noveltyAnimationState);
                        break;
                }
            }
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.ENCHANTER_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.ENCHANTER_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ENCHANTER_DEATH.get();
    }

    @Override
    public @NotNull SoundEvent getCelebrateSound() {
        return ModSounds.ENCHANTER_AMBIENT.get();
    }

    public List<UUID> getEnchantList() {
        return this.enchantList;
    }

    public void setEnchantList(List<UUID> list) {
        this.enchantList = list;
    }

    public int getEnchantCool() {
        return this.enchantCool;
    }

    public void setEnchantCool(int cool) {
        this.enchantCool = cool;
    }

    @Override
    public int getEnchantTime() {
        return this.enchantTime;
    }

    @Override
    public void setEnchantTime(int enchantTime) {
        this.enchantTime = enchantTime;
    }

    @Override
    public void setShowArms(boolean showArms) {
        this.entityData.set(SHOW_ARMS, showArms);
    }

    @Override
    public boolean showArms() {
        return this.entityData.get(SHOW_ARMS);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide){
            this.idleAnimationState.animateWhen(this.isCurrentAnimation(IDLE) && !this.walkAnimation.isMoving(), this.tickCount);
        }
        this.enchanterTick();
    }

    @Override
    protected @NotNull SoundEvent getCastingSoundEvent() {
        return SoundEvents.EMPTY;
    }
}
