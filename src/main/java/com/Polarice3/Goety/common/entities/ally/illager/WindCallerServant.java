package com.Polarice3.Goety.common.entities.ally.illager;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.ally.undead.bound.BoundWindCaller;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.spells.wind.UpdraftSpell;
import com.Polarice3.Goety.common.magic.spells.wind.WindBlastSpell;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class WindCallerServant extends SpellcasterIllagerServant{
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(WindCallerServant.class, EntityDataSerializers.INT);
    public static String IDLE = "idle";
    public static String BLAST = "blast";
    public static String UPDRAFT = "updraft";
    public int blastCool = 0;
    public int updraftCool = 0;
    public ItemStack staff = new ItemStack(ModItems.WIND_STAFF.get());
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState blastAnimationState = new AnimationState();
    public AnimationState updraftAnimationState = new AnimationState();

    public WindCallerServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new AvoidTargetGoal<>(this, LivingEntity.class, 3.0F, 1.0D, 1.6D));
        this.goalSelector.addGoal(3, new BlastGoal(this));
        this.goalSelector.addGoal(4, new UpdraftGoal(this));
    }

    public void miscGoal() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new RaiderWanderGoal<>(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    @SuppressWarnings("removal")
    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0D)
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.WindCallerServantFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.WindCallerServantArmor.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.WindCallerServantHealth.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.WindCallerServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.WindCallerServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.WindCallerServantFollowRange.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, 0);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("BlastCool", this.blastCool);
        compound.putInt("UpdraftCool", this.updraftCool);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("BlastCool")) {
            this.blastCool = compound.getInt("BlastCool");
        }
        if (compound.contains("UpdraftCool")) {
            this.updraftCool = compound.getInt("UpdraftCool");
        }
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, IDLE)){
            return 1;
        } else if (Objects.equals(animation, BLAST)){
            return 2;
        } else if (Objects.equals(animation, UPDRAFT)){
            return 3;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> list = new ArrayList<>();
        list.add(this.idleAnimationState);
        list.add(this.blastAnimationState);
        list.add(this.updraftAnimationState);
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
                        this.blastAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.blastAnimationState);
                        break;
                    case 3:
                        this.updraftAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.updraftAnimationState);
                        break;
                }
            }
        }
    }

    public boolean isAttacking(){
        return this.getCurrentAnimation() == this.getAnimationState(BLAST) || this.getCurrentAnimation() == this.getAnimationState(UPDRAFT);
    }

    @Override
    public void die(DamageSource pCause) {
        if (!this.level.isClientSide) {
            if (this.getIdol() == null) {
                if (this.getTrueOwner() != null) {
                    if (CuriosFinder.hasNamelessSet(this.getTrueOwner())){
                        BoundWindCaller servant = this.convertTo(ModEntityType.BOUND_WIND_CALLER.get(), true);
                        if (servant != null) {
                            servant.setTrueOwner(this.getTrueOwner());
                            net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, servant);
                            if (!this.isSilent()) {
                                this.level.levelEvent((Player)null, 1026, this.blockPosition(), 0);
                            }
                        }
                    }
                }
            }
        }
        super.die(pCause);
    }

    @Override
    public int xpReward() {
        return 10;
    }

    public SoundEvent getCelebrateSound() {
        return ModSounds.WIND_CALLER_AMBIENT.get();
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.WIND_CALLER_AMBIENT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.WIND_CALLER_DEATH.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_32654_) {
        return ModSounds.WIND_CALLER_HURT.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
    }

    @Override
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return false;
    }

    protected void checkFallDamage(double p_20809_, boolean p_20810_, BlockState p_20811_, BlockPos p_20812_) {
    }

    @Override
    public void tick() {
        if (this.level.isClientSide()) {
            this.idleAnimationState.animateWhen(!this.isAttacking() && !this.walkAnimation.isMoving(), this.tickCount);
        }
        super.tick();
        if (this.level instanceof ServerLevel serverLevel){
            if (this.blastCool > 0) {
                --this.blastCool;
            }
            if (this.updraftCool > 0) {
                --this.updraftCool;
            }
            if (this.isAlive()) {
                ServerParticleUtil.windParticle(serverLevel, ColorUtil.WHITE, 0.5F + serverLevel.random.nextFloat() * 0.5F, 0.0F, this.getId(), this.position());
                ColorUtil colorUtil = new ColorUtil(0x8d837d);
                ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.STATION_CULT_SPELL.get(), this, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 0.5F);
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        Vec3 vector3d = this.getDeltaMovement();
        if (!this.onGround() && vector3d.y < 0.0D && !this.isNoGravity()) {
            this.setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
        }
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return null;
    }

    public static class BlastGoal extends Goal {
        public WindCallerServant windCaller;
        public boolean attack = false;
        public int spellTick;

        public BlastGoal(WindCallerServant windCaller){
            this.windCaller = windCaller;
            this.setFlags(EnumSet.of(Flag.TARGET, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.windCaller.getTarget() != null
                    && this.windCaller.distanceTo(this.windCaller.getTarget()) <= 6.75F){
                return !this.windCaller.isAttacking() && this.windCaller.blastCool <= 0;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.spellTick <= MathHelper.secondsToTicks(2.55F) && this.windCaller.getTarget() != null;
        }

        public void start() {
            super.start();
            this.windCaller.setAnimationState(BLAST);
            this.windCaller.playSound(ModSounds.WIND_CALLER_BLAST.get(), 1.2F, 1.0F);
            this.spellTick = 0;
        }

        public void stop() {
            super.stop();
            this.attack = false;
            this.spellTick = 0;
            this.windCaller.blastCool = MathHelper.secondsToTicks(3);
            this.windCaller.setAnimationState(IDLE);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.windCaller.getTarget() == null) {
                this.stop();
                return;
            }

            MobUtil.instaLook(this.windCaller, this.windCaller.getTarget());
            this.windCaller.getNavigation().stop();
            ++this.spellTick;

            if (this.spellTick >= MathHelper.secondsToTicks(0.325F)){
                if (!this.attack) {
                    new WindBlastSpell().mobSpellResult(this.windCaller, this.windCaller.staff);
                    this.attack = true;
                }
            }
        }
    }

    public static class UpdraftGoal extends Goal {
        public WindCallerServant windCaller;
        public boolean attack = false;
        public boolean attack2 = false;
        public int spellTick;

        public UpdraftGoal(WindCallerServant windCaller){
            this.windCaller = windCaller;
            this.setFlags(EnumSet.of(Flag.TARGET, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.windCaller.getTarget() != null){
                return !this.windCaller.isAttacking() && this.windCaller.updraftCool <= 0;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.spellTick <= MathHelper.secondsToTicks(3.25F) && this.windCaller.getTarget() != null;
        }

        public void start() {
            super.start();
            this.attack = false;
            this.attack2 = false;
            this.spellTick = 0;
        }

        public void stop() {
            super.stop();
            this.attack = false;
            this.attack2 = false;
            this.spellTick = 0;
            this.windCaller.updraftCool = MathHelper.secondsToTicks(3);
            this.windCaller.setAnimationState(IDLE);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.windCaller.getTarget() == null) {
                this.stop();
                return;
            }

            if (this.windCaller.distanceTo(this.windCaller.getTarget()) <= 13.0D || this.attack) {
                if (!this.attack) {
                    this.windCaller.setAnimationState(UPDRAFT);
                    this.windCaller.playSound(ModSounds.WIND_CALLER_UPDRAFT.get(), 1.2F, 1.0F);
                    this.attack = true;
                }

                if (this.spellTick >= MathHelper.secondsToTicks(0.85F)){
                    if (!this.attack2){
                        new UpdraftSpell().mobSpellResult(this.windCaller, this.windCaller.staff);
                        this.attack2 = true;
                    }
                }
                MobUtil.instaLook(this.windCaller, this.windCaller.getTarget());
                this.windCaller.getNavigation().stop();
                ++this.spellTick;
            } else {
                this.windCaller.getNavigation().moveTo(this.windCaller.getTarget(), 1.1D);
            }
        }
    }
}
