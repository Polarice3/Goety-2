package com.Polarice3.Goety.common.entities.ally.undead.bound;

import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.spells.frost.IceChunkSpell;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class BoundIceologer extends AbstractBoundIllager{
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(BoundIceologer.class, EntityDataSerializers.INT);
    public int spellCool;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();

    public BoundIceologer(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new AvoidTargetGoal<>(this, LivingEntity.class, 6.0F, 1.0D, 1.0D));
        this.goalSelector.addGoal(4, new ChunkSpellGoal(this));
    }

    public void miscGoal() {
        this.goalSelector.addGoal(8, new RaiderWanderGoal<>(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.FLYING_SPEED, 0.15D)
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.BoundIceologerFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.BoundIceologerArmor.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.BoundIceologerHealth.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.BoundIceologerHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.BoundIceologerArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.BoundIceologerFollowRange.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, 0);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SpellCool", this.spellCool);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SpellCool")) {
            this.spellCool = compound.getInt("SpellCool");
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
        } else if (Objects.equals(animation, "attack")){
            return 2;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> list = new ArrayList<>();
        list.add(this.idleAnimationState);
        list.add(this.attackAnimationState);
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
                        this.attackAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                }
            }
        }
    }

    public SoundEvent getCelebrateSound() {
        return ModSounds.ICEOLOGER_AMBIENT.get();
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.ICEOLOGER_AMBIENT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.ICEOLOGER_DEATH.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_32654_) {
        return ModSounds.ICEOLOGER_HURT.get();
    }

    @Override
    public float getVoicePitch() {
        return 0.45F;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide){
            this.idleAnimationState.animateWhen(this.getCurrentAnimation() != this.getAnimationState("attack") && !this.walkAnimation.isMoving(), this.tickCount);
            for(int i = 0; i < 2; ++i) {
                this.level.addParticle(ParticleTypes.CLOUD, this.getRandomX(0.5D), this.getY() + 0.5D, this.getRandomZ(0.5D), (0.5D - this.random.nextDouble()) * 0.15D, 0.01F, (0.5D - this.random.nextDouble()) * 0.15D);
            }
        }
        if (!this.level.isClientSide) {
            if (this.spellCool > 0) {
                --this.spellCool;
            }
        }
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0){
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    public static class ChunkSpellGoal extends Goal {
        public BoundIceologer iceologer;
        public boolean attack = false;
        public int spellTick;

        public ChunkSpellGoal(BoundIceologer iceologer){
            this.iceologer = iceologer;
            this.setFlags(EnumSet.of(Flag.TARGET, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.iceologer.getTarget() != null){
                return this.iceologer.spellCool <= 0;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.iceologer.getTarget() != null && this.spellTick < 66;
        }

        public void start() {
            super.start();
            this.attack = false;
            this.spellTick = 0;
        }

        public void stop() {
            super.stop();
            this.attack = false;
            this.spellTick = 0;
            this.iceologer.spellCool = MathHelper.secondsToTicks(6);
            this.iceologer.setAnimationState("idle");
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.iceologer.getTarget() == null) {
                this.stop();
                return;
            }
            if (this.iceologer.distanceTo(this.iceologer.getTarget()) <= 7.0D || this.attack){
                if (!this.attack) {
                    this.iceologer.playSound(ModSounds.ICEOLOGER_ATTACK.get(), 1.0F, this.iceologer.getVoicePitch());
                    if (this.iceologer.getTarget() != null) {
                        MobUtil.instaLook(this.iceologer, this.iceologer.getTarget());
                        new IceChunkSpell().mobSpellResult(this.iceologer, new ItemStack(ModItems.FROST_STAFF.get()));
                    }
                    this.iceologer.setAnimationState("attack");
                    this.attack = true;
                }

                MobUtil.instaLook(this.iceologer, this.iceologer.getTarget());
                this.iceologer.getNavigation().stop();
                ++this.spellTick;
            } else {
                this.iceologer.getNavigation().moveTo(this.iceologer.getTarget(), 0.8D);
            }

        }
    }
}
