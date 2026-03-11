package com.Polarice3.Goety.common.entities.ally.golem;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StoneMinistrosity extends RaiderGolemServant {
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(StoneMinistrosity.class, EntityDataSerializers.INT);
    public static String IDLE = "idle";
    public static String ATTACK = "attack";
    public int attackTick;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState sitAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();

    public StoneMinistrosity(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true){
            protected double getAttackReachSqr(LivingEntity p_33825_) {
                return 2.0F + p_33825_.getBbWidth();
            }
        });
    }

    @SuppressWarnings("removal")
    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.StoneMinistrosityHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.StoneMinistrosityDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.StoneMinistrosityArmor.get())
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.StoneMinistrosityFollowRange.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.StoneMinistrosityHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.StoneMinistrosityArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.StoneMinistrosityDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.StoneMinistrosityFollowRange.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, 0);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.MINISTROSITY_STONE_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.MINISTROSITY_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.MINISTROSITY_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(ModSounds.MINISTROSITY_STEP.get(), 0.15F, 1.0F);
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.MINISTROSITY_STONE_IDLE.get();
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return this.isAlive() && !this.isSpectator() && !this.onClimbable();
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
        } else if (Objects.equals(animation, "attack")){
            return 2;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.attackAnimationState);
        return animationStates;
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

    public boolean isCurrentAnimation(String animation) {
        return this.getCurrentAnimation() == this.getAnimationState(animation);
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
                        this.attackAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                }
            }
        }
    }

    public boolean isMeleeAttacking() {
        return this.attackTick > 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            this.idleAnimationState.animateWhen(!this.walkAnimation.isMoving() && this.getCurrentAnimation() == 0 && !this.isStaying(), this.tickCount);
            this.sitAnimationState.animateWhen(!this.walkAnimation.isMoving() && this.getCurrentAnimation() == 0 && this.isStaying(), this.tickCount);
        }
        if (this.isAlive()) {
            if (!this.level.isClientSide) {
                if (this.isMeleeAttacking()) {
                    this.getNavigation().stop();
                    --this.attackTick;
                    if (this.attackTick <= 0){
                        this.setAnimationState(IDLE);
                    }
                }
            }
        }
    }

    @Override
    public void swing(InteractionHand p_21007_) {
        super.swing(p_21007_);
        this.attackTick = 10;
        this.setAnimationState(ATTACK);
        this.playSound(SoundEvents.FOX_BITE, this.getSoundVolume(), this.getVoicePitch() * 0.5F);
    }

    @Override
    public void setUpgraded(boolean upgraded) {
        if (this.getType() == ModEntityType.STONE_MINISTROSITY.get()) {
            if (upgraded) {
                Entity entity = MobUtil.convertTo(this, ModEntityType.REDSTONE_MINISTROSITY.get(), false, this.getTrueOwner() instanceof Player player ? player : null);
                if (entity instanceof StoneMinistrosity ministrosity) {
                    if (this.getTrueOwner() != null) {
                        ministrosity.setTrueOwner(this.getTrueOwner());
                    }
                    if (this.limitedLifeTicks > 0){
                        ministrosity.setLimitedLife(this.limitedLifeTicks);
                    }
                }
            }
        }
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 5){
            this.attackTick = 0;
        } else if (p_21375_ == 6){
            this.setAggressive(true);
        } else if (p_21375_ == 7){
            this.setAggressive(false);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand p_230254_2_) {
        if (!this.level.isClientSide) {
            ItemStack itemstack = pPlayer.getItemInHand(p_230254_2_);
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if (itemstack.is(Tags.Items.STONE) && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.heal(this.getMaxHealth() / 2.0F);
                    this.gameEvent(GameEvent.EAT, this);
                    this.playSound(this.getEatingSound(itemstack), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                    if (this.level instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = serverLevel.random.nextGaussian() * 0.02D;
                            double d1 = serverLevel.random.nextGaussian() * 0.02D;
                            double d2 = serverLevel.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(DustParticleOptions.REDSTONE, this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }
}
