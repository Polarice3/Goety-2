package com.Polarice3.Goety.common.entities.ally.golem;

import com.Polarice3.Goety.api.entities.IAnimated;
import com.Polarice3.Goety.api.entities.IBreathing;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.GatherSquareParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ai.BreathingAttackGoal;
import com.Polarice3.Goety.common.entities.ai.servant.ServantWanderGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class FlameConstruct extends AbstractGolemServant implements IBreathing, IAnimated {
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(FlameConstruct.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HUNKERED = SynchedEntityData.defineId(FlameConstruct.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BREATHING = SynchedEntityData.defineId(FlameConstruct.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> LOOK_ANGLE = SynchedEntityData.defineId(FlameConstruct.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> HAS_LOOK = SynchedEntityData.defineId(FlameConstruct.class, EntityDataSerializers.BOOLEAN);
    public static String IDLE = "idle";
    public static String BREATH = "breath";
    public static String BLAST = "blast";
    public static String HUNKER_DOWN = "hunker_down";
    public static String BORED = "bored";
    public static String PEEK = "peek";
    public static String STAND_UP = "stand_up";
    public static final float BREATH_RANGE = 5.0F;
    public float getGlow;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState breathAnimationState = new AnimationState();
    public AnimationState blastAnimationState = new AnimationState();
    public AnimationState hunkerDownAnimationState = new AnimationState();
    public AnimationState boredAnimationState = new AnimationState();
    public AnimationState peekAnimationState = new AnimationState();
    public AnimationState standUpAnimationState = new AnimationState();

    public FlameConstruct(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FireBreathGoal(this));
        this.goalSelector.addGoal(2, new BlastGoal(this));
        this.goalSelector.addGoal(3, new StandUpGoal(this));
        this.goalSelector.addGoal(4, new HunkerDownGoal(this));
        this.goalSelector.addGoal(5, new NoveltyGoal(this));
        this.goalSelector.addGoal(8, new ServantWanderGoal<>(this, 1.0D, 10));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !FlameConstruct.this.isHunkered();
            }
        });
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !FlameConstruct.this.isHunkered();
            }
        });
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.FlameConstructHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.FlameConstructDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.FlameConstructArmor.get())
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2D)
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.FlameConstructFollowRange.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.FlameConstructHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.FlameConstructArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.FlameConstructDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.FlameConstructFollowRange.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LOOK_ANGLE, 0.0F);
        this.entityData.define(HUNKERED, false);
        this.entityData.define(BREATHING, false);
        this.entityData.define(HAS_LOOK, false);
        this.entityData.define(ANIM_STATE, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Hunkered")) {
            this.setHunkered(compound.getBoolean("Hunkered"));
        }
        if (compound.contains("CustomLookAngle")) {
            this.setCustomLook(compound.getFloat("CustomLookAngle"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Hunkered", this.isHunkered());
        if (this.hasCustomLook()) {
            compound.putFloat("CustomLookAngle", this.getCustomLook());
        }
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> list = new ArrayList<>();
        list.add(this.idleAnimationState);
        list.add(this.breathAnimationState);
        list.add(this.blastAnimationState);
        list.add(this.hunkerDownAnimationState);
        list.add(this.boredAnimationState);
        list.add(this.peekAnimationState);
        list.add(this.standUpAnimationState);
        return list;
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, IDLE)){
            return 0;
        } else if (Objects.equals(animation, BREATH)){
            return 1;
        } else if (Objects.equals(animation, BLAST)){
            return 2;
        } else if (Objects.equals(animation, HUNKER_DOWN)){
            return 3;
        } else if (Objects.equals(animation, BORED)){
            return 4;
        } else if (Objects.equals(animation, PEEK)){
            return 5;
        } else if (Objects.equals(animation, STAND_UP)){
            return 6;
        } else {
            return 0;
        }
    }

    @Override
    public int getCurrentAnimation() {
        return this.entityData.get(ANIM_STATE);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_219422_) {
        if (ANIM_STATE.equals(p_219422_)) {
            if (this.level.isClientSide){
                switch (this.entityData.get(ANIM_STATE)){
                    case 0:
                        if (!this.walkAnimation.isMoving()) {
                            this.idleAnimationState.startIfStopped(this.tickCount);
                        }
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 1:
                        this.breathAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.breathAnimationState);
                        break;
                    case 2:
                        this.blastAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.blastAnimationState);
                        break;
                    case 3:
                        this.hunkerDownAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.hunkerDownAnimationState);
                        break;
                    case 4:
                        this.boredAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.boredAnimationState);
                        break;
                    case 5:
                        this.peekAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.peekAnimationState);
                        break;
                    case 6:
                        this.standUpAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.standUpAnimationState);
                        break;
                }
            }
        }

        super.onSyncedDataUpdated(p_219422_);
    }

    public boolean canBeSeenByAnyone() {
        return super.canBeSeenByAnyone() && !this.isHunkered();
    }

    @Override
    public void playAmbientSound() {
        if (!this.isHunkered()) {
            super.playAmbientSound();
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.FLAME_CONSTRUCT_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.FLAME_CONSTRUCT_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.FLAME_CONSTRUCT_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(ModSounds.FLAME_CONSTRUCT_STEP.get(), 0.15F, 1.0F);
    }

    public boolean isHunkered() {
        return this.entityData.get(HUNKERED);
    }

    public void setHunkered(boolean hunkered) {
        this.entityData.set(HUNKERED, hunkered);
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
        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.1F;
        if (target.hurt(ModDamageSource.fireBreath(this, this), damage)) {
            target.invulnerableTime = 10;
            target.setSecondsOnFire(4);
        }
    }

    public void setCustomLook(float angle) {
        this.entityData.set(LOOK_ANGLE, angle);
        this.entityData.set(HAS_LOOK, true);
    }

    public float getCustomLook() {
        return this.entityData.get(LOOK_ANGLE);
    }

    public boolean hasCustomLook() {
        return this.entityData.get(HAS_LOOK);
    }

    @Override
    public boolean altStaying() {
        return this.isHunkered();
    }

    private void glow() {
        this.getGlow = Mth.clamp(this.getGlow - 0.05F, 0, 1);
    }

    @Override
    protected float getStandingEyeHeight(Pose p_21131_, EntityDimensions p_21132_) {
        return p_21132_.height * 0.5F;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (!this.isDeadOrDying()) {
                if (this.tickCount % 2 == 0) {
                    double d0 = ((this.getRandom().nextDouble() * this.getRandom().nextIntBetweenInclusive(-1, 1)) * 0.25F);
                    double d1 = ((this.getRandom().nextDouble() * this.getRandom().nextIntBetweenInclusive(-1, 1)) * 0.25F);
                    this.level.addParticle(ParticleTypes.SMOKE, this.getX() + d0, this.getY() + (this.getRandom().nextDouble() * 0.3D) + 0.7D, this.getZ() + d1, 0.0D, 0.0D, 0.0D);
                }
            }
            this.glow();
            this.idleAnimationState.animateWhen(this.isCurrentAnimation(IDLE) && !this.walkAnimation.isMoving(), this.tickCount);
        }

        if (this.isHunkered()) {
            this.getNavigation().stop();
            this.getMoveControl().strafe(0, 0);
        }
    }

    @Override
    public void mobSense() {
        if (!this.isHunkered()) {
            super.mobSense();
        }
    }

    @Override
    protected float getDamageAfterMagicAbsorb(DamageSource p_34149_, float p_34150_) {
        p_34150_ = super.getDamageAfterMagicAbsorb(p_34149_, p_34150_);
        if (p_34149_.getEntity() == this) {
            p_34150_ = 0.0F;
        }

        if (p_34149_.is(DamageTypeTags.IS_FREEZING)) {
            p_34150_ *= 2.0F;
        }

        if (this.isHunkered()) {
            p_34150_ *= 0.2F;
        }

        return p_34150_;
    }

    public void handleEntityEvent(byte pId) {
        if (pId == 15){
            this.getGlow = 1.0F;
        } else {
            super.handleEntityEvent(pId);
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!this.level.isClientSide) {
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                int fuel = ForgeHooks.getBurnTime(itemstack, null);
                if (this.getHealth() < this.getMaxHealth() && (itemstack.is(Items.MAGMA_BLOCK) || fuel > 0)) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    if (itemstack.is(Items.MAGMA_BLOCK)) {
                        this.heal(this.getMaxHealth() / 4.0F);
                        this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 1.25F);
                    } else {
                        this.heal((fuel / 20.0F) / 5.0F);
                        this.playSound(SoundEvents.FIRECHARGE_USE, 1.0F, 0.75F);
                    }
                    if (this.level instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = serverLevel.random.nextGaussian() * 0.02D;
                            double d1 = serverLevel.random.nextGaussian() * 0.02D;
                            double d2 = serverLevel.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    return InteractionResult.SUCCESS;
                } else if (itemstack.isEmpty() && this.isHunkered()) {
                    if (pPlayer.isShiftKeyDown()) {
                        Vec3 vec3 = Vec3.atBottomCenterOf(this.blockPosition());
                        this.moveTo(vec3);
                    } else {
                        float f = (float)Mth.floor((Mth.wrapDegrees(pPlayer.getYRot() - 180.0F) + 45.0F) / 90.0F) * 90.0F;
                        this.setYRot(f);
                        this.setYBodyRot(f);
                        this.setYHeadRot(f);
                        this.setCustomLook(f);
                    }
                    this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 0.5F);
                    return InteractionResult.SUCCESS;
                } else {
                    if (pPlayer.getMainHandItem().is(ModItems.REPAIR_PUTTY.get()) && pPlayer.getOffhandItem().is(ModTags.Items.HAMMERS)) {
                        return InteractionResult.PASS;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    static class FireBreathGoal extends BreathingAttackGoal<FlameConstruct> {
        protected FlameConstruct construct;
        protected int soundTick;
        protected int nextAttackTickCount;

        public FireBreathGoal(FlameConstruct construct) {
            super(construct, 3, 106, 1.0F);
            this.construct = construct;
        }

        @Override
        public boolean canUse() {
            boolean flag = super.canUse();
            if (!this.construct.isHunkered()) {
                if (this.construct.tickCount >= this.nextAttackTickCount) {
                    if (this.attackTarget != null) {
                        if (this.attacker.distanceTo(this.attackTarget) > this.spewingRange) {
                            this.construct.getNavigation().moveTo(this.attackTarget, 1.0D);
                        }
                    }
                    return flag;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.durationLeft > 0;
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            super.start();
            this.soundTick = 0;
            this.attacker.setAnimationState(BREATH);
            this.attacker.playSound(ModSounds.FLAME_CONSTRUCT_PRE_BREATH.get());
            this.attacker.playSound(ModSounds.FLAME_CONSTRUCT_BREATH_STAND.get());
        }

        @Override
        public void stop() {
            super.stop();
            this.soundTick = 0;
            this.attacker.setAnimationState(IDLE);
            this.nextAttackTickCount = this.construct.tickCount + MathHelper.secondsToTicks(4);
        }

        @Override
        public void breathOut() {
            this.construct.getNavigation().stop();
            this.construct.getMoveControl().strafe(0, 0);
            int duration = this.maxDuration - this.durationLeft;
            if (duration < 40) {
                this.construct.level.broadcastEntityEvent(this.construct, (byte) 15);
            }
            if (duration >= 27 && duration < 68) {
                ++this.soundTick;
                if (this.soundTick == 0 || this.soundTick % 20 == 0) {
                    this.attacker.playSound(ModSounds.FLAME_CONSTRUCT_BREATH.get());
                }
                Vec3 source = this.construct.position().add(0, this.construct.getBbHeight() * 0.5F, 0);
                Vec3 direction = this.construct.calculateViewVector(0, this.construct.getViewYRot(1.0F));
                FlamethrowerVFX.flamethrower(this.construct, direction, duration - 27, BREATH_RANGE);
                for (Entity entity : MobUtil.getTargets(this.attacker.level, this.attacker, source, direction, BREATH_RANGE, 1.5D)) {
                    if (entity != null) {
                        this.attacker.doBreathing(entity);
                    }
                }
            }
            if (duration == 69) {
                this.attacker.playSound(ModSounds.FLAME_CONSTRUCT_BREATH_END.get());
            }
        }
    }

    static class BlastGoal extends Goal {
        protected static ColorUtil GATHER = new ColorUtil(0xffcb7d);
        protected static ColorUtil FIRE_BLAST_CIRCLE = new ColorUtil(0xffea93);
        protected static ColorUtil SMOKE = new ColorUtil(0xac3819);
        protected static ColorUtil SMOKE_UP = new ColorUtil(0xe8612d);
        protected FlameConstruct construct;
        protected int nextAttackTickCount;
        public int blastTime;

        public BlastGoal(FlameConstruct construct) {
            this.construct = construct;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            if (!this.construct.isHunkered()) {
                LivingEntity target = this.construct.getTarget();
                if (this.construct.tickCount >= this.nextAttackTickCount) {
                    if (target != null) {
                        if (this.construct.distanceTo(target) > 3.0D) {
                            this.construct.getNavigation().moveTo(target, 1.0D);
                        } else {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.blastTime < 64;
        }

        @Override
        public void start() {
            super.start();
            this.blastTime = 0;
            this.construct.playSound(ModSounds.FLAME_CONSTRUCT_PRE_BLAST.get());
            this.construct.getNavigation().stop();
            this.construct.getMoveControl().strafe(0, 0);
            this.construct.setAnimationState(BLAST);
        }

        @Override
        public void stop() {
            super.stop();
            this.construct.setAnimationState(IDLE);
            this.blastTime = 0;
            this.nextAttackTickCount = this.construct.tickCount + MathHelper.secondsToTicks(5);
        }

        @Override
        public void tick() {
            super.tick();
            ++this.blastTime;
            if (this.construct.getTarget() != null) {
                MobUtil.instaLook(this.construct, this.construct.getTarget());
            }
            if (this.blastTime < 26) {
                if (this.construct.level instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 16; ++i) {
                        ServerParticleUtil.gatherHorizontalParticles(new GatherSquareParticleOption(GATHER, this.construct.position().add(0, this.construct.getBbHeight() * 0.5F, 0)), this.construct, serverLevel, 2);
                    }
                }
            }
            if (this.blastTime == 27) {
                this.construct.playSound(ModSounds.FLAME_CONSTRUCT_BLAST_JUMP.get());
            }
            if (this.blastTime < 40) {
                this.construct.level.broadcastEntityEvent(this.construct, (byte) 15);
                this.construct.getNavigation().stop();
                this.construct.getMoveControl().strafe(0, 0);
            }
            if (this.blastTime == 42 && !this.construct.isStaying()) {
                Vec3 movement = this.construct.getDeltaMovement();
                Vec3 vector3d = this.construct.getViewVector( 1.0F);
                double distance0 = Math.sqrt(vector3d.x * vector3d.x + vector3d.y * vector3d.y + vector3d.z * vector3d.z);
                double power = 5.0D;
                double x = vector3d.x / distance0 * power * 0.2D;
                double z = vector3d.z / distance0 * power * 0.2D;
                this.construct.setDeltaMovement(x, movement.y(), z);
            }
            if (this.blastTime == 39 || this.blastTime == 55) {
                this.construct.playSound(ModSounds.FLAME_CONSTRUCT_BLAST.get());
                this.fireBlast();
            }
        }

        public void fireBlast() {
            if (this.construct.level instanceof ServerLevel serverLevel) {
                new SpellExplosion(serverLevel, this.construct, this.construct.getServantAttack(), this.construct.blockPosition(), 5.0F, (float) this.construct.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0F) {
                    @Override
                    public void postHurtEffect(Entity target, DamageSource damageSource, float actualDamage) {
                        super.postHurtEffect(target, damageSource, actualDamage);
                        target.invulnerableTime = 10;
                        target.setSecondsOnFire(4);
                    }
                };
                serverLevel.sendParticles(new CircleExplodeParticleOption(FIRE_BLAST_CIRCLE, 5, 1), this.construct.getX(), this.construct.getY() + 0.25F, this.construct.getZ(), 1, 0, 0, 0, 0);
                ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.BIG_CULT_SPELL.get(), this.construct.getX(), this.construct.getY() + 0.25F, this.construct.getZ(), SMOKE.red(), SMOKE.green(), SMOKE.blue(), 5.0F);
                ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.BIG_FIRE.get(), this.construct.getX(), this.construct.getY() + 0.25F, this.construct.getZ(), 0, 0, 0, 5.0F);
                ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.SMALL_FIRE_GROUND.get(), this.construct.getX(), this.construct.getY() + 0.25F, this.construct.getZ(), 0, 0, 0, 5.0F);
                ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.BIG_CULT_SPELL.get(), this.construct.getX(), this.construct.getY() + 1.5F, this.construct.getZ(), SMOKE_UP.red(), SMOKE_UP.green(), SMOKE_UP.blue(), 2.0F);
                ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.BIG_FIRE.get(), this.construct.getX(), this.construct.getY() + 1.5F, this.construct.getZ(), 0, 0, 0, 2.0F);
            }
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    static class HunkerDownGoal extends Goal {
        protected FlameConstruct construct;
        protected int toHunker;

        public HunkerDownGoal(FlameConstruct construct) {
            this.construct = construct;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET, Flag.JUMP, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!this.construct.isHunkered()) {
                if (this.construct.hurtTime <= 0) {
                    if (this.construct.getTarget() == null) {
                        if (!this.construct.isPatrolling() && !this.construct.isCommanded()) {
                            if (this.construct.isGuardingArea()) {
                                return this.construct.isWithinGuard(this.construct.blockPosition());
                            }
                            return this.construct.isStaying();
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public void start() {
            this.construct.playSound(ModSounds.FLAME_CONSTRUCT_HUNKER_DOWN_VOCAL.get());
            this.toHunker = 0;
            this.construct.setAnimationState(HUNKER_DOWN);
        }

        @Override
        public void stop() {
            this.toHunker = 0;
            this.construct.setAnimationState(IDLE);
        }

        @Override
        public void tick() {
            super.tick();
            ++this.toHunker;
            if (this.construct.hasCustomLook()) {
                this.construct.setYRot(this.construct.getCustomLook());
                this.construct.setYBodyRot(this.construct.getCustomLook());
                this.construct.setYHeadRot(this.construct.getCustomLook());
            } else {
                float f = Mth.floor(this.construct.getYRot() / 90.0D + 0.5D) & 3;
                this.construct.setYRot(f);
                this.construct.setYBodyRot(f);
                this.construct.setYHeadRot(f);
            }
            if (this.toHunker == 5) {
                this.construct.playSound(ModSounds.FLAME_CONSTRUCT_HUNKER_DOWN.get());
            }
            if (this.toHunker == 13 || this.toHunker == 18 || this.toHunker == 22 || this.toHunker == 26) {
                this.construct.playSound(ModSounds.FLAME_CONSTRUCT_LEG_TUCK.get());
            }
            if (this.toHunker >= 36) {
                this.construct.setHunkered(true);
                this.stop();
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    static class NoveltyGoal extends Goal {
        protected static int PEEK_TYPE = 1;
        protected static int BORED_TYPE = 2;
        protected FlameConstruct construct;
        protected int noveltyType = 0;
        protected int noveltyTick;

        public NoveltyGoal(FlameConstruct construct) {
            this.construct = construct;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.construct.isHunkered() && this.construct.hurtTime <= 0 && this.construct.getTarget() == null && (this.construct.isStaying() || (this.construct.isGuardingArea() && this.construct.isWithinGuard(this.construct.blockPosition()))) && !this.construct.isPatrolling() && !this.construct.isCommanded()) {
                if (this.construct.isCurrentAnimation(IDLE)) {
                    int chance = this.construct.getRandom().nextInt(108);
                    if (chance == 3) {
                        this.noveltyType = BORED_TYPE;
                    } else if (chance == 5) {
                        this.noveltyType = PEEK_TYPE;
                    }

                    return this.noveltyType > 0;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.construct.isHunkered() && this.construct.hurtTime <= 0 && this.construct.getTarget() == null && (this.construct.isStaying() || (this.construct.isGuardingArea() && this.construct.isWithinGuard(this.construct.blockPosition()))) && !this.construct.isPatrolling() && !this.construct.isCommanded()) {
                if (this.noveltyType == PEEK_TYPE) {
                    return this.noveltyTick < 63;
                } else if (this.noveltyType == BORED_TYPE) {
                    return this.noveltyTick < 40;
                }
            }
            return false;
        }

        @Override
        public void start() {
            super.start();
            this.noveltyTick = 0;
            if (this.noveltyType == PEEK_TYPE) {
                this.construct.setAnimationState(PEEK);
            } else {
                this.construct.setAnimationState(BORED);
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.noveltyTick = 0;
            this.noveltyType = 0;
            this.construct.setAnimationState(IDLE);
        }

        @Override
        public void tick() {
            super.tick();
            ++this.noveltyTick;
            if (this.noveltyType == PEEK_TYPE) {
                if (this.noveltyTick == 20 || this.noveltyTick == 37) {
                    this.construct.playSound(ModSounds.FLAME_CONSTRUCT_TWIST.get());
                }
                if (this.noveltyTick == 62) {
                    this.construct.playSound(ModSounds.FLAME_CONSTRUCT_CLOSE.get());
                }
            } else if (this.noveltyType == BORED_TYPE) {
                if (this.noveltyTick == 4 || this.noveltyTick == 24) {
                    this.construct.playSound(ModSounds.FLAME_CONSTRUCT_BORED_SIDE.get());
                }
                if (this.noveltyTick == 13 || this.noveltyTick == 33) {
                    this.construct.playSound(ModSounds.FLAME_CONSTRUCT_BORED_DOWN.get());
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    static class StandUpGoal extends Goal {
        protected FlameConstruct construct;
        protected int toStand;

        public StandUpGoal(FlameConstruct construct) {
            this.construct = construct;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET, Flag.JUMP, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!this.construct.isHunkered()) {
                return false;
            } else {
                if (this.construct.isCommanded()) {
                    return true;
                }
                if (!this.construct.isStaying() && !(this.construct.isGuardingArea() && this.construct.isWithinGuard(this.construct.blockPosition()))) {
                    return true;
                }
                if (this.construct.getTarget() != null) {
                    return true;
                }
                return this.construct.hurtTime > 0;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.toStand < 23;
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public void start() {
            this.toStand = 0;
            this.construct.setAnimationState(STAND_UP);
        }

        @Override
        public void stop() {
            this.toStand = 0;
            this.construct.setHunkered(false);
            this.construct.setAnimationState(IDLE);
        }

        @Override
        public void tick() {
            super.tick();
            ++this.toStand;
            if (this.toStand == 2) {
                this.construct.playSound(ModSounds.FLAME_CONSTRUCT_STAND_UP.get());
            }
            if (this.toStand == 12) {
                this.construct.playSound(ModSounds.FLAME_CONSTRUCT_STAND_UP.get());
            }
            this.construct.getNavigation().stop();
            this.construct.getMoveControl().strafe(0, 0);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
