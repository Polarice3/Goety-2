package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.ender.AbstractSnareling;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SnarelingGoop extends TangleEntity {
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(SnarelingGoop.class, EntityDataSerializers.INT);
    public AnimationState holdAnimationState = new AnimationState();
    public AnimationState burrowAnimationState = new AnimationState();

    public SnarelingGoop(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public SnarelingGoop(Level p_19871_, LivingEntity owner, Entity target) {
        super(ModEntityType.SNARELING_GOOP.get(), p_19871_, owner, target);
    }

    public SnarelingGoop(Level p_19871_, LivingEntity owner, BlockPos blockPos) {
        super(ModEntityType.SNARELING_GOOP.get(), p_19871_, owner, blockPos);
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
        }if (Objects.equals(animation, "burrow")){
            return 2;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> list = new ArrayList<>();
        list.add(this.holdAnimationState);
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
                    }
                    case 1 -> {
                        this.holdAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.holdAnimationState);
                    }
                    case 2 -> {
                        this.burrowAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.burrowAnimationState);
                    }
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    @Override
    public void burst() {
        this.setAnimationState("hold");
        this.playSound(ModSounds.SNARELING_GOO_IMPACT.get(), 1.5F, 1.0F);
    }

    public void tangleTarget(){
        super.tangleTarget();
        if (this.getTarget() != null) {
            this.getTarget().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, MathHelper.secondsToTicks(5), 0, false, false));
        }
        this.setAnimationState("hold");
    }

    @Override
    public void burrow() {
        this.setAnimationState("burrow");
    }

    @Override
    protected boolean canHitEntity(Entity pEntity) {
        if (pEntity instanceof AbstractSnareling) {
            return false;
        } else {
            return super.canHitEntity(pEntity);
        }
    }
}
