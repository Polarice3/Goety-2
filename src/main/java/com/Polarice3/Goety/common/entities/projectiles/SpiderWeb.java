package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.spider.SpiderServant;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpiderWeb extends TangleEntity {
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(SpiderWeb.class, EntityDataSerializers.INT);
    public AnimationState holdAnimationState = new AnimationState();
    public AnimationState burrowAnimationState = new AnimationState();

    public SpiderWeb(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public SpiderWeb(Level p_19871_, LivingEntity owner, Entity target) {
        super(ModEntityType.SPIDER_WEB.get(), p_19871_, owner, target);
    }

    public SpiderWeb(Level p_19871_, LivingEntity owner, BlockPos blockPos) {
        super(ModEntityType.SPIDER_WEB.get(), p_19871_, owner, blockPos);
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

    public void findTarget(){
        if (this.getTarget() == null) {
            for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                    if (!MobUtil.areAllies(livingEntity, this.getOwner()) && !(livingEntity instanceof Spider) && !(livingEntity instanceof SpiderServant)) {
                        this.setTarget(livingEntity);
                    }
                }
            }
        }
    }

    @Override
    public void burst() {
        this.setAnimationState("hold");
        this.playSound(ModSounds.SPIDER_WEB.get(), 1.5F, 1.0F);
    }

    public void tangleTarget(){
        super.tangleTarget();
        this.setAnimationState("hold");
    }

    @Override
    public void burrow() {
        this.setAnimationState("burrow");
    }
}
