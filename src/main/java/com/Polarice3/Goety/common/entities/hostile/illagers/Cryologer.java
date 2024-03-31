package com.Polarice3.Goety.common.entities.hostile.illagers;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.MobsConfig;
import com.Polarice3.Goety.api.entities.IBreathing;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.ai.BreathingAttackGoal;
import com.Polarice3.Goety.common.entities.neutral.AbstractMonolith;
import com.Polarice3.Goety.common.entities.projectiles.HailCloud;
import com.Polarice3.Goety.common.entities.projectiles.IceChunk;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cryologer extends HuntingIllagerEntity implements IBreathing {
    private static final EntityDataAccessor<Byte> IS_CASTING_SPELL = SynchedEntityData.defineId(Cryologer.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(Cryologer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> BREATHING = SynchedEntityData.defineId(Cryologer.class, EntityDataSerializers.BOOLEAN);
    protected int castingTime;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState breathAnimationState = new AnimationState();
    public AnimationState cloudAnimationState = new AnimationState();
    public AnimationState wallAnimationState = new AnimationState();
    public AnimationState chunkAnimationState = new AnimationState();

    public Cryologer(EntityType<? extends HuntingIllagerEntity> p_i48551_1_, Level p_i48551_2_) {
        super(p_i48551_1_, p_i48551_2_);
        this.xpReward = 10;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new CastingSpellGoal());
        this.goalSelector.addGoal(1, new WallSpellGoal());
        this.goalSelector.addGoal(2, new HailSpellGoal());
        this.goalSelector.addGoal(2, new ChunkSpellGoal());
        this.goalSelector.addGoal(3, new BreathGoal());
        this.goalSelector.addGoal(4, new AvoidTargetGoal<>(this, LivingEntity.class, 8.0F, 0.6D, 1.0D){
            @Override
            public boolean canUse() {
                return super.canUse() && Cryologer.this.getCurrentAnimation() < 2;
            }
        });
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.CryologerHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.CryologerDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.CryologerHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.CryologerDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CASTING_SPELL, (byte)0);
        this.entityData.define(ANIM_STATE, 0);
        this.entityData.define(BREATHING, false);
    }

    public void readAdditionalSaveData(CompoundTag p_33732_) {
        super.readAdditionalSaveData(p_33732_);
        this.castingTime = p_33732_.getInt("FrostSpellTicks");
    }

    public void addAdditionalSaveData(CompoundTag p_33734_) {
        super.addAdditionalSaveData(p_33734_);
        p_33734_.putInt("FrostSpellTicks", this.castingTime);
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
        } else if (Objects.equals(animation, "breath")){
            return 2;
        } else if (Objects.equals(animation, "cloud")){
            return 3;
        } else if (Objects.equals(animation, "wall")){
            return 4;
        } else if (Objects.equals(animation, "chunk")){
            return 5;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> list = new ArrayList<>();
        list.add(this.idleAnimationState);
        list.add(this.breathAnimationState);
        list.add(this.cloudAnimationState);
        list.add(this.wallAnimationState);
        list.add(this.chunkAnimationState);
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
                        this.breathAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.breathAnimationState);
                        break;
                    case 3:
                        this.cloudAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.cloudAnimationState);
                        break;
                    case 4:
                        this.wallAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.wallAnimationState);
                        break;
                    case 5:
                        this.chunkAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.chunkAnimationState);
                        break;
                }
            }
        }
    }

    public boolean isCastingSpell() {
        if (this.level.isClientSide) {
            return this.entityData.get(IS_CASTING_SPELL) > 0;
        } else {
            return this.castingTime > 0;
        }
    }

    public void setIsCastingSpell(int id) {
        this.entityData.set(IS_CASTING_SPELL, (byte)id);
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.castingTime > 0) {
            --this.castingTime;
        }

    }

    protected int getSpellCastingTime() {
        return this.castingTime;
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.CRYOLOGER_AMBIENT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.CRYOLOGER_DEATH.get();
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.CRYOLOGER_HURT.get();
    }

    public boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;
    }

    protected float getDamageAfterMagicAbsorb(DamageSource p_34149_, float p_34150_) {
        p_34150_ = super.getDamageAfterMagicAbsorb(p_34149_, p_34150_);
        if (p_34149_.getEntity() == this) {
            p_34150_ = 0.0F;
        }

        if (ModDamageSource.freezeAttacks(p_34149_) || p_34149_ == DamageSource.FREEZE) {
            p_34150_ *= 0.15F;
        }

        return p_34150_;
    }

    public void makeStuckInBlock(BlockState p_33796_, Vec3 p_33797_) {
        if (!p_33796_.is(Blocks.POWDER_SNOW)) {
            super.makeStuckInBlock(p_33796_, p_33797_);
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide){
            if (this.isAlive()){
                if (this.getCurrentAnimation() < 2 && this.getCurrentAnimation() != 1){
                    this.setAnimationState("idle");
                }

                if (this.isBreathing()){
                    Vec3 look = this.getLookAngle();

                    double dist = 0.9D;
                    double px = this.getX() + look.x() * dist;
                    double py = this.getEyeY() + look.y() * dist;
                    double pz = this.getZ() + look.z() * dist;

                    for (int i = 0; i < 2; i++) {
                        double spread = 5.0D + this.getRandom().nextDouble() * 2.5D;
                        double velocity = 0.15D + this.getRandom().nextDouble() * 0.15D;

                        Vec3 vecSpread = new Vec3(
                                this.getRandom().nextGaussian() * 0.0075D * spread,
                                this.getRandom().nextGaussian() * 0.0075D * spread,
                                this.getRandom().nextGaussian() * 0.0075D * spread);
                        Vec3 vec3 = look.add(vecSpread).multiply(velocity, velocity, velocity);

                        this.level.addAlwaysVisibleParticle(ParticleTypes.CLOUD, px, py, pz, vec3.x, vec3.y, vec3.z);
                    }
                }
            }
        }
    }

    public boolean isAlliedTo(Entity pEntity) {
        if (pEntity == this) {
            return true;
        } else if (super.isAlliedTo(pEntity)) {
            return true;
        } else if (pEntity instanceof Vex vex && vex.getOwner() != null) {
            return this.isAlliedTo(vex.getOwner());
        } else if (pEntity instanceof LivingEntity && ((LivingEntity)pEntity).getMobType() == MobType.ILLAGER) {
            return this.getTeam() == null && pEntity.getTeam() == null;
        } else {
            return false;
        }
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.PLAYER_HURT_FREEZE;
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_) {

    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.CRYOLOGER_CELEBRATE.get();
    }

    @Override
    public boolean isBreathing() {
        return this.entityData.get(BREATHING);
    }

    @Override
    public void setBreathing(boolean flag) {
        this.entityData.set(BREATHING, flag);
    }

    @Override
    public void doBreathing(Entity target) {
        this.playSound(SoundEvents.PLAYER_BREATH, this.random.nextFloat() * 0.5F, this.random.nextFloat() * 0.5F);
        float damage = 1.0F;
        if (target.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)){
            damage *= 2.0F;
        }
        if (target.hurt(ModDamageSource.frostBreath(this, this), damage)) {
            if (target instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), MathHelper.secondsToTicks(1)));
            }
        }
    }

    class BreathGoal extends BreathingAttackGoal<Cryologer> {
        protected int nextAttackTickCount;
        protected int breathTime;

        public BreathGoal() {
            super(Cryologer.this, 8, 20, 1.0F);
        }

        public boolean noWall(){
            return MobUtil.getTargets(Cryologer.this.level, Cryologer.this, 16, 3, EntitySelector.NO_CREATIVE_OR_SPECTATOR).stream().noneMatch(entity -> entity instanceof AbstractMonolith);
        }

        @Override
        public boolean canUse() {
            return super.canUse()
                    && !Cryologer.this.isCastingSpell()
                    && Cryologer.this.tickCount >= this.nextAttackTickCount
                    && noWall();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && noWall();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            if (this.attackTarget != null){
                this.spewX = this.attackTarget.getX();
                this.spewY = this.attackTarget.getY() + this.attackTarget.getEyeHeight();
                this.spewZ = this.attackTarget.getZ();
            }
            this.durationLeft = this.maxDuration;
            this.attacker.navigation.stop();
            this.attacker.setAnimationState("breath");
            this.nextAttackTickCount = Cryologer.this.tickCount + 100;
            this.breathTime = 10;
        }

        @Override
        public void stop() {
            super.stop();
            this.attacker.setAnimationState("idle");
            this.breathTime = 0;
        }

        @Override
        public void tick() {
            if (this.breathTime > 0){
                --this.breathTime;
                this.attacker.getLookControl().setLookAt(spewX, spewY, spewZ, 500.0F, 500.0F);
                this.rotateAttacker(spewX, spewY, spewZ, 500.0F, 500.0F);
            } else {
                super.tick();
                if (!this.attacker.isBreathing()) {
                    this.attacker.setBreathing(true);
                }
            }
        }
    }

    class CastingSpellGoal extends Goal {
        private CastingSpellGoal() {
        }

        public boolean canUse() {
            return Cryologer.this.getSpellCastingTime() > 0;
        }

        public void start() {
            super.start();
            Cryologer.this.navigation.stop();
        }

        public void stop() {
            super.stop();
            Cryologer.this.setIsCastingSpell(0);
            Cryologer.this.setAnimationState("idle");
        }

        public void tick() {
            if (Cryologer.this.getTarget() != null) {
                Cryologer.this.getLookControl().setLookAt(Cryologer.this.getTarget(), (float)Cryologer.this.getMaxHeadYRot(), (float)Cryologer.this.getMaxHeadXRot());
            }

        }
    }

    protected abstract class CryologerUseSpellGoal extends Goal {
        protected int attackWarmupDelay;
        protected int nextAttackTickCount;

        public boolean canUse() {
            LivingEntity livingentity = Cryologer.this.getTarget();
            if (livingentity != null && livingentity.isAlive() && Cryologer.this.hasLineOfSight(livingentity) && Cryologer.this.getCurrentAnimation() != Cryologer.this.getAnimationState("breath")) {
                if (Cryologer.this.isCastingSpell()) {
                    return false;
                } else {
                    return Cryologer.this.tickCount >= this.nextAttackTickCount;
                }
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = Cryologer.this.getTarget();
            return livingentity != null && livingentity.isAlive() && Cryologer.this.hasLineOfSight(livingentity) && this.attackWarmupDelay > 0;
        }

        public void start() {
            this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
            Cryologer.this.castingTime = this.getCastingTime();
            this.nextAttackTickCount = Cryologer.this.tickCount + this.getCastingInterval();
            SoundEvent soundevent = this.getSpellPrepareSound();
            if (soundevent != null) {
                Cryologer.this.playSound(soundevent, 1.0F, 1.0F);
            }
        }

        @Override
        public void stop() {
            super.stop();
            Cryologer.this.setAnimationState("idle");
        }

        public void tick() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
                Cryologer.this.playSound(Cryologer.this.getCastingSoundEvent(), 1.0F, 1.0F);
            }

        }

        protected abstract void performSpellCasting();

        protected int getCastWarmupTime() {
            return 20;
        }

        protected abstract int getCastingTime();

        protected abstract int getCastingInterval();

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class HailSpellGoal extends CryologerUseSpellGoal {

        public void start() {
            super.start();
            Cryologer.this.setAnimationState("cloud");
        }

        @Override
        protected void performSpellCasting() {
            if (Cryologer.this.getTarget() != null){
                LivingEntity target = Cryologer.this.getTarget();
                HailCloud hailCloud = new HailCloud(Cryologer.this.level, Cryologer.this, target);
                Cryologer.this.level.addFreshEntity(hailCloud);
            }
        }

        @Override
        protected int getCastingTime() {
            return 22;
        }

        @Override
        protected int getCastingInterval() {
            return 100;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.CRYOLOGER_HAIL.get();
        }
    }

    class WallSpellGoal extends CryologerUseSpellGoal {

        public void start() {
            super.start();
            Cryologer.this.setAnimationState("wall");
        }

        @Override
        protected void performSpellCasting() {
            if (Cryologer.this.getTarget() != null){
                LivingEntity target = Cryologer.this.getTarget();
                int random = Cryologer.this.random.nextInt(3);
                if (random == 0) {
                    int[] rowToRemove = Util.getRandom(WandUtil.CONFIG_1_ROWS, Cryologer.this.getRandom());
                    Direction direction = Direction.fromYRot(target.getYHeadRot());
                    switch (direction){
                        case NORTH -> rowToRemove = WandUtil.CONFIG_1_NORTH_ROW;
                        case SOUTH -> rowToRemove = WandUtil.CONFIG_1_SOUTH_ROW;
                        case WEST -> rowToRemove = WandUtil.CONFIG_1_WEST_ROW;
                        case EAST -> rowToRemove = WandUtil.CONFIG_1_EAST_ROW;
                    }
                    WandUtil.summonLesserSquareTrap(Cryologer.this, target.blockPosition(), ModEntityType.GLACIAL_WALL.get(), rowToRemove, 1);
                } else if (random == 1){
                    WandUtil.summonWallTrap(Cryologer.this, target, ModEntityType.GLACIAL_WALL.get(), 3, 1);
                } else {
                    WandUtil.summonRandomPillarsTrap(Cryologer.this, target, ModEntityType.GLACIAL_WALL.get(), 6, 1);
                }
            }
        }

        @Override
        protected int getCastingTime() {
            return 20;
        }

        @Override
        protected int getCastingInterval() {
            return 80;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.CRYOLOGER_WALL.get();
        }
    }

    class ChunkSpellGoal extends CryologerUseSpellGoal {

        @Override
        public boolean canUse() {
            return super.canUse() && Cryologer.this.level.getDifficulty() == Difficulty.HARD && MobsConfig.CryologerIceChunk.get();
        }

        public void start() {
            super.start();
            Cryologer.this.setAnimationState("chunk");
            if (Cryologer.this.getTarget() != null){
                LivingEntity target = Cryologer.this.getTarget();
                IceChunk iceChunk = new IceChunk(Cryologer.this.level, Cryologer.this, target);
                iceChunk.playSound(ModSounds.ICE_CHUNK_SUMMON.get(), 1.0F, 1.0F);
                Cryologer.this.level.addFreshEntity(iceChunk);
            }
        }

        @Override
        protected void performSpellCasting() {
        }

        @Override
        protected int getCastWarmupTime() {
            return 50;
        }

        @Override
        protected int getCastingTime() {
            return 50;
        }

        @Override
        protected int getCastingInterval() {
            return 100;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.CRYOLOGER_CHUNK.get();
        }
    }
}
