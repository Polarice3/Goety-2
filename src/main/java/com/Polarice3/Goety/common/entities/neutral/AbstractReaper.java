package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.SweepingEdgeEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class AbstractReaper extends Summoned {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(AbstractReaper.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(AbstractReaper.class, EntityDataSerializers.INT);
    public int attackTick;
    public int deathTime = 0;
    public float fly;
    public float flySpeed;
    public float oFlySpeed;
    public float oFly;
    private float flying = 1.0F;
    private float nextFly = 1.0F;
    public static String IDLE = "idle";
    public static String ATTACK = "attack";
    public static String DEATH = "death";
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();

    public AbstractReaper(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setNoGravity(true);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new MeleeGoal(this));
        this.goalSelector.addGoal(4, new ReaperAttackGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new FlyTowardsTargetGoal(this));
        this.goalSelector.addGoal(6, new FlyingGoal<>(this));
        this.goalSelector.addGoal(7, new ReaperLookGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ReaperHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.ReaperArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ReaperDamage.get())
                .add(Attributes.MOVEMENT_SPEED, 0.4D)
                .add(Attributes.FLYING_SPEED, 0.4D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.ReaperHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.ReaperArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.ReaperDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(ANIM_STATE, 0);
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof AbstractReaper;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return SpellConfig.ReaperLimit.get();
    }

    @Override
    public int xpReward() {
        return 10;
    }

    protected boolean isSunSensitive() {
        return true;
    }

    protected PathNavigation createNavigation(Level pLevel) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, pLevel) {
            public boolean isStableDestination(BlockPos blockPos) {
                return !this.level.getBlockState(blockPos.below()).isAir();
            }
        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.REAPER_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.REAPER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.REAPER_DEATH.get();
    }

    protected SoundEvent getStepSound() {
        return ModSounds.REAPER_FLY.get();
    }

    protected void playStepSound(BlockPos p_29419_, BlockState p_29420_) {
    }

    private void calculateFlapping() {
        this.oFly = this.fly;
        this.oFlySpeed = this.flySpeed;
        this.flySpeed += (float)(!this.onGround() && !this.isPassenger() ? 4 : -1) * 0.3F;
        this.flySpeed = Mth.clamp(this.flySpeed, 0.0F, 1.0F);
        if (!this.onGround() && this.flying < 1.0F) {
            this.flying = 1.0F;
        }

        this.flying *= 0.9F;
        this.fly += this.flying * 2.0F;
    }

    protected void processFlappingMovement() {
        if (this.isFlapping()) {
            this.onFlap();
            if (this.getMovementEmission().emitsEvents()) {
                this.gameEvent(GameEvent.STEP);
            }
        }

    }

    protected boolean isFlapping() {
        return this.flyDist > this.nextFly;
    }

    protected void onFlap() {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
        this.nextFly = this.flyDist + this.flySpeed / 2.0F;
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
        } else if (Objects.equals(animation, "death")){
            return 3;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> list = new ArrayList<>();
        list.add(this.idleAnimationState);
        list.add(this.attackAnimationState);
        list.add(this.deathAnimationState);
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
                        this.attackAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                    case 3:
                        this.deathAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.deathAnimationState);
                        break;
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    private boolean getReaperFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setReaperFlags(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte)(i & 255));
    }

    public boolean isMeleeAttacking() {
        return this.getReaperFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setReaperFlags(1, attacking);
        this.attackTick = 0;
        this.level.broadcastEntityEvent(this, (byte) 5);
    }

    public void handleEntityEvent(byte pId) {
        if (pId == 5){
            this.attackTick = 0;
        } else {
            super.handleEntityEvent(pId);
        }
    }

    public boolean causeFallDamage(float p_146828_, float p_146829_, DamageSource p_146830_) {
        return false;
    }

    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
    }

    @Override
    public boolean isSteppingCarefully() {
        return true;
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= 30) {
            this.spawnAnim();
            this.remove(RemovalReason.KILLED);
        }
        this.hurtTime = 1;
    }

    public void die(DamageSource p_21014_) {
        this.setAnimationState(DEATH);
        super.die(p_21014_);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide){
            if (this.isMeleeAttacking()) {
                ++this.attackTick;
            } else if (!this.isDeadOrDying()) {
                this.setAnimationState(IDLE);
            }
        }
    }

    public void aiStep() {
        super.aiStep();
        this.calculateFlapping();
    }

    public double getMeleeAttackRangeSqr(LivingEntity p_147273_) {
        return this.getBbWidth() * 4.0F * this.getBbWidth() * 4.0F + p_147273_.getBbWidth();
    }

    public boolean onClimbable() {
        return false;
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (itemstack.is(ModItems.ECTOPLASM.get()) && this.getHealth() < this.getMaxHealth()){
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.playSound(ModSounds.REAPER_AMBIENT.get(), 1.0F, 1.25F);
                this.heal(2.0F);
                if (this.level instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                    }
                }
                pPlayer.swing(pHand);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public static class FlyTowardsTargetGoal extends Goal {
        private final AbstractReaper reaper;

        public FlyTowardsTargetGoal(AbstractReaper reaper) {
            this.reaper = reaper;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return this.reaper.getTarget() != null
                    && !this.reaper.isStaying()
                    && !this.reaper.getMoveControl().hasWanted();
        }

        public boolean canContinueToUse() {
            return this.reaper.getMoveControl().hasWanted()
                    && !this.reaper.isStaying()
                    && this.reaper.getTarget() != null
                    && this.reaper.getTarget().isAlive();
        }

        @Override
        public void start() {
            LivingEntity target = this.reaper.getTarget();
            if (target != null) {
                this.reaper.getNavigation().moveTo(target, 1.0F);
            }
        }

        @Override
        public void tick() {
            LivingEntity target = this.reaper.getTarget();
            if (target != null) {
                double d0 = this.reaper.distanceToSqr(target);
                if (d0 < 16.0D) {
                    this.reaper.getNavigation().moveTo(target, 1.0F);
                }
            }
        }
    }

    static class FlyingGoal<T extends PathfinderMob & IServant> extends WanderGoal<T> {

        public FlyingGoal(T reaper) {
            super(reaper, 0.8D, 20);
        }

        @Nullable
        protected Vec3 getPosition() {
            if (this.summonedEntity.isGuardingArea()){
                return randomBoundPos();
            } else {
                return this.getFlyingPosition();
            }
        }

        protected Vec3 getFlyingPosition() {
            double xd = this.summonedEntity.getX() + (double)((this.summonedEntity.getRandom().nextFloat() * 2.0F - 1.0F) * 16.0F);
            double yd = this.summonedEntity.getY() + (double)((this.summonedEntity.getRandom().nextFloat() * 2.0F - 1.0F) * 16.0F);
            double zd = this.summonedEntity.getZ() + (double)((this.summonedEntity.getRandom().nextFloat() * 2.0F - 1.0F) * 16.0F);
            return new Vec3(xd, yd, zd);
        }
    }

    public static class ReaperLookGoal extends Goal {
        private final AbstractReaper reaper;

        public ReaperLookGoal(AbstractReaper reaper) {
            this.reaper = reaper;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            if (this.reaper.getTarget() == null) {
                this.reaper.setYRot(-((float) Mth.atan2(this.reaper.getDeltaMovement().x(), this.reaper.getDeltaMovement().z())) * (180F / (float) Math.PI));
                this.reaper.setYBodyRot(this.reaper.getYRot());
            } else {
                LivingEntity target = this.reaper.getTarget();
                if (target.distanceToSqr(this.reaper) < Mth.square(64)) {
                    double d1 = target.getX() - this.reaper.getX();
                    double d2 = target.getZ() - this.reaper.getZ();
                    this.reaper.setYRot(-((float) Mth.atan2(d1, d2)) * (180F / (float) Math.PI));
                    this.reaper.setYBodyRot(this.reaper.getYRot());
                }
            }
        }
    }

    static class ReaperAttackGoal extends MeleeAttackGoal {
        private final AbstractReaper reaper;

        public ReaperAttackGoal(AbstractReaper mob, double moveSpeed) {
            super(mob, moveSpeed, true);
            this.reaper = mob;
        }

        @Override
        public boolean canUse() {
            return this.reaper.getTarget() != null && this.reaper.getTarget().isAlive() && this.reaper.isWithinMeleeAttackRange(this.reaper.getTarget());
        }

        @Override
        public void tick() {
            LivingEntity livingentity = this.reaper.getTarget();
            if (livingentity == null) {
                return;
            }
            this.checkAndPerformAttack(livingentity, this.reaper.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (this.reaper.isWithinMeleeAttackRange(enemy) && this.reaper.hasLineOfSight(enemy)) {
                this.reaper.setMeleeAttacking(true);
            }
        }

    }

    static class MeleeGoal extends Goal {
        private final AbstractReaper reaper;

        public MeleeGoal(AbstractReaper reaper) {
            this.reaper = reaper;
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.reaper.getTarget() != null && this.reaper.isMeleeAttacking();
        }

        @Override
        public boolean canContinueToUse() {
            return this.reaper.attackTick < 23;
        }

        @Override
        public void start() {
            this.reaper.setMeleeAttacking(true);
            this.reaper.setAnimationState(ATTACK);
        }

        @Override
        public void stop() {
            this.reaper.setMeleeAttacking(false);
            this.reaper.setAnimationState(IDLE);
        }

        @Override
        public void tick() {
            if (this.reaper.getTarget() != null && this.reaper.getTarget().isAlive()) {
                LivingEntity livingentity = this.reaper.getTarget();
                MobUtil.instaLook(this.reaper, livingentity);
                this.reaper.setYBodyRot(this.reaper.getYHeadRot());
                if (this.reaper.attackTick == 1) {
                    this.reaper.playSound(ModSounds.REAPER_SWING.get(), this.reaper.getSoundVolume(), this.reaper.getVoicePitch());
                    this.reaper.setAnimationState(ATTACK);
                }
                if (this.reaper.attackTick == 12) {
                    this.attackMobs(livingentity, this.reaper);
                }
            }
        }

        public Vec3 getHorizontalLookAngle() {
            return this.reaper.calculateViewVector(0, this.reaper.getYRot());
        }

        public void attackMobs(LivingEntity pTarget, AbstractReaper reaper){
            if (reaper.getTrueOwner() instanceof Player player) {
                SEHelper.increaseSouls(player, ItemConfig.DarkScytheSouls.get() * 5);
            }
            float f = (float)reaper.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float f1 = EnchantmentHelper.getDamageBonus(reaper.getMainHandItem(), pTarget.getMobType());
            float f2 = 1.0F;
            f = f * (0.2F + f2 * f2 * 0.8F);
            f1 = f1 * f2;
            f = f + f1;

            float f3 = 1.0F + SweepingEdgeEnchantment.getSweepingDamageRatio(3) * f;
            int j = EnchantmentHelper.getFireAspect(reaper);
            double area = 2.0D;
            for (LivingEntity livingentity : reaper.level.getEntitiesOfClass(LivingEntity.class, this.reaper.getBoundingBox().move(this.getHorizontalLookAngle().scale(2.0D)).inflate(area, area, area))) {
                if (livingentity != reaper && !MobUtil.areAllies(reaper, livingentity) && (!(livingentity instanceof ArmorStand) || !((ArmorStand) livingentity).isMarker()) && reaper.distanceToSqr(livingentity) < 16.0D && livingentity != reaper.getVehicle()) {
                    livingentity.knockback(0.4F, (double) Mth.sin(reaper.getYRot() * ((float) Math.PI / 180F)), (double) (-Mth.cos(reaper.getYRot() * ((float) Math.PI / 180F))));
                    if (livingentity.hurt(this.reaper.getServantAttack(), f3)) {
                        if (j > 0) {
                            livingentity.setSecondsOnFire(j * 4);
                        }
                        if (reaper.getTrueOwner() instanceof Player player) {
                            if (livingentity instanceof IOwned owned) {
                                if (owned.getTrueOwner() != reaper) {
                                    SEHelper.increaseSouls(player, ItemConfig.DarkScytheSouls.get() * 5);
                                }
                            } else {
                                SEHelper.increaseSouls(player, ItemConfig.DarkScytheSouls.get() * 5);
                            }
                        }
                        EnchantmentHelper.doPostHurtEffects(livingentity, reaper);
                        EnchantmentHelper.doPostDamageEffects(reaper, livingentity);
                    }
                }
            }

            reaper.level.playSound(null, reaper.getX(), reaper.getY(), reaper.getZ(), ModSounds.SCYTHE_SWING.get(), reaper.getSoundSource(), 1.0F, 1.0F);
            double d0 = -Mth.sin(reaper.getYRot() * ((float)Math.PI / 180F));
            double d1 = Mth.cos(reaper.getYRot() * ((float)Math.PI / 180F));
            if (reaper.level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, reaper.getX() + d0, reaper.getY(0.5D), reaper.getZ() + d1, 0, d0, 0.0D, d1, 0.0D);
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
