package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuickGrowingVine extends AbstractVine{
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(QuickGrowingVine.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IDLE_TYPE = SynchedEntityData.defineId(QuickGrowingVine.class, EntityDataSerializers.BOOLEAN);
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState burstAnimationState = new AnimationState();
    public AnimationState burrowAnimationState = new AnimationState();
    public AnimationState holdAnimationState = new AnimationState();

    public QuickGrowingVine(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.QuickGrowingVineHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.QuickGrowingVineArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.QuickGrowingVineHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.QuickGrowingVineArmor.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, 0);
        this.entityData.define(IDLE_TYPE, true);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_31485_) {
        super.addAdditionalSaveData(p_31485_);
        p_31485_.putInt("Animation", this.getCurrentAnimation());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_31474_) {
        super.readAdditionalSaveData(p_31474_);
        if (p_31474_.contains("Animation")){
            this.setAnimationState(p_31474_.getInt("Animation"));
        }
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, "idle")){
            return 1;
        } else if (Objects.equals(animation, "burst")){
            return 2;
        } else if (Objects.equals(animation, "burrow")){
            return 3;
        } else if (Objects.equals(animation, "hold")){
            return 4;
        } else if (Objects.equals(animation, "idle2")){
            return 5;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> list = new ArrayList<>();
        list.add(this.idleAnimationState);
        list.add(this.burstAnimationState);
        list.add(this.burrowAnimationState);
        list.add(this.holdAnimationState);
        return list;
    }

    public void stopMostAnimation(AnimationState exception){
        for (AnimationState state : this.getAllAnimations()){
            if (state != exception){
                state.stop();
            }
        }
    }

    public int getCurrentAnimation(){
        return this.entityData.get(ANIM_STATE);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor)) {
            if (this.level.isClientSide){
                switch (this.entityData.get(ANIM_STATE)){
                    case 0:
                        break;
                    case 1:
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 2:
                        this.burstAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.burstAnimationState);
                        break;
                    case 3:
                        this.burrowAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.burrowAnimationState);
                        break;
                    case 4:
                        this.holdAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.holdAnimationState);
                        break;
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (pReason != MobSpawnType.MOB_SUMMONED){
            this.setPerpetual(true);
        }
        this.setAnimationState("hold");
        return pSpawnData;
    }

    public void initRotate(ServerLevelAccessor pLevel) {
        switch (pLevel.getLevel().random.nextInt(4)){
            case 1 -> {
                this.setYRot(90.0F);
                this.setYBodyRot(90.0F);
                this.setYHeadRot(90.0F);
            }
            case 2 -> {
                this.setYRot(180.0F);
                this.setYBodyRot(180.0F);
                this.setYHeadRot(180.0F);
            }
            case 3 -> {
                this.setYRot(270.0F);
                this.setYBodyRot(270.0F);
                this.setYHeadRot(270.0F);
            }
            default -> {
                this.setYRot(0.0F);
                this.setYBodyRot(0.0F);
                this.setYHeadRot(0.0F);
            }
        }
    }

    @Nullable
    @Override
    public EntityType<?> getVariant(Level level, BlockPos blockPos) {
        if (level.isWaterAt(blockPos) || level.isWaterAt(blockPos.above())){
            return ModEntityType.QUICK_GROWING_KELP.get();
        } else {
            return super.getVariant(level, blockPos);
        }
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.QUICK_GROWING_VINE_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.QUICK_GROWING_VINE_DEATH.get();
    }

    @Override
    protected SoundEvent getBurstSound() {
        return ModSounds.QUICK_GROWING_VINE_BURST.get();
    }

    @Override
    protected SoundEvent getBurrowSound() {
        return ModSounds.QUICK_GROWING_VINE_BURROW.get();
    }

    @Override
    public BlockState getState() {
        return Blocks.VINE.defaultBlockState();
    }

    public int getAgeSpeed(){
        if (!this.isActivate()){
            return 5;
        }
        return 3;
    }

    public void burst(){
        super.burst();
        if (this.activeTick < 15 && this.getCurrentAnimation() != this.getAnimationState("burst")) {
            this.setAnimationState("burst");
            this.playSound(this.getBurstSound(), 2.0F, 1.0F);
        } else if ((this.getCurrentAnimation() < 2 && this.getCurrentAnimation() != this.getAnimationState("hold")) || this.activeTick == 15) {
            this.setAnimationState("idle");
        }
    }

    public void burrow(){
        super.burrow();
        this.setAnimationState("burrow");
        this.playSound(this.getBurrowSound(), 2.0F, 1.0F);
    }

    @Override
    public ItemStack getSeed() {
        return new ItemStack(ModItems.QUICK_GROWING_SEED.get());
    }
}
