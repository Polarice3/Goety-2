package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntangleVines extends TangleEntity {
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(EntangleVines.class, EntityDataSerializers.INT);
    private boolean isDamaging;
    public AnimationState hiddenAnimationState = new AnimationState();
    public AnimationState holdAnimationState = new AnimationState();
    public AnimationState burstAnimationState = new AnimationState();
    public AnimationState burrowAnimationState = new AnimationState();

    public EntangleVines(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public EntangleVines(Level p_19871_, LivingEntity owner, Entity target) {
        super(ModEntityType.ENTANGLE_VINES.get(), p_19871_, owner, target);
    }

    public EntangleVines(Level p_19871_, LivingEntity owner, BlockPos blockPos) {
        super(ModEntityType.ENTANGLE_VINES.get(), p_19871_, owner, blockPos);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ANIM_STATE, 0);
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, "hold")){
            return 1;
        } else if (Objects.equals(animation, "burst")){
            return 2;
        } else if (Objects.equals(animation, "burrow")){
            return 3;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> list = new ArrayList<>();
        list.add(this.hiddenAnimationState);
        list.add(this.holdAnimationState);
        list.add(this.burstAnimationState);
        list.add(this.burrowAnimationState);
        return list;
    }

    public void stopMostAnimation(AnimationState exception){
        for (AnimationState state : this.getAllAnimations()){
            if (state != exception){
                state.stop();
            }
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor)) {
            if (this.level.isClientSide){
                switch (this.entityData.get(ANIM_STATE)) {
                    case 0 -> {
                        this.hiddenAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.hiddenAnimationState);
                    }
                    case 1 -> {
                        this.holdAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.holdAnimationState);
                    }
                    case 2 -> {
                        this.burstAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.burstAnimationState);
                    }
                    case 3 -> {
                        this.burrowAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.burrowAnimationState);
                    }
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Damaging")) {
            this.isDamaging = pCompound.getBoolean("Damaging");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("Damaging", this.isDamaging);
    }

    public void setDamaging(boolean damaging){
        this.isDamaging = damaging;
    }

    @Override
    public void burst() {
        this.setAnimationState("burst");
        this.playSound(ModSounds.VINE_TRAP_BURST.get(), 2.0F, 1.0F);
        this.playSound(ModSounds.VINE_TRAP_HOLD.get(), 1.0F, 1.0F);
    }

    public void tangleTarget(){
        super.tangleTarget();
        this.setAnimationState("hold");
        if (this.getTarget() != null){
            if (this.isDamaging){
                this.getTarget().hurt(DamageSource.thorns(this), 1.0F);
            }
        }
    }

    @Override
    public void burrow() {
        this.setAnimationState("burrow");
    }
}
