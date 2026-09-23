package com.Polarice3.Goety.common.entities.ally.illager;

import com.Polarice3.Goety.api.entities.IShielded;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.HeavyArrow;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class ImperialGuardServant extends AbstractIllagerServant implements IShielded, CrossbowAttackMob {
    public static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(ImperialGuardServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_SHIELD = SynchedEntityData.defineId(ImperialGuardServant.class, EntityDataSerializers.BOOLEAN);
    public int fleeCool;
    public int fleeTime;
    public int attackCoolTick;
    public int shieldHealth = AttributesConfig.ImperialGuardServantShield.get();
    public boolean isFleeing;
    public static String IDLE = "idle";
    public static String PRE_ATTACK = "pre_attack";
    public static String ATTACK = "attack";
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState standAnimationState = new AnimationState();
    public AnimationState preAttackAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public double arrowPower;

    public ImperialGuardServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.arrowPower = 0.0D;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new ImperialGuardRangedGoal(this));
    }

    public void miscGoal() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new RaiderWanderGoal<>(this, 0.6D) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && !ImperialGuardServant.this.isFleeing;
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse()
                        && !ImperialGuardServant.this.isFleeing;
            }
        });
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ImperialGuardServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0F)
                .add(Attributes.ARMOR, AttributesConfig.ImperialGuardServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.ImperialGuardServantToughness.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.ImperialGuardServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.ImperialGuardServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS), AttributesConfig.ImperialGuardServantToughness.get());
    }

    public double getBaseRangeDamage(){
        return AttributesConfig.ImperialGuardServantRangeDamage.get();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, 0);
        this.entityData.define(HAS_SHIELD, true);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.readShieldedData(pCompound);
        this.setArrowPower(pCompound.getInt("arrowPower"));
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        this.addShieldedData(pCompound);
        if (pCompound.contains("arrowPower")){
            pCompound.putDouble("arrowPower", this.arrowPower);
        }
    }

    @Override
    public double getFollowSpeed() {
        return 1.25D;
    }

    @Override
    public boolean canHaveWeapon() {
        return false;
    }

    @Override
    public boolean canWearArmor() {
        return false;
    }

    public double getArrowPower() {
        return arrowPower;
    }

    public void setArrowPower(int arrowPower) {
        this.arrowPower = arrowPower;
    }

    public boolean hasShield(){
        return this.entityData.get(HAS_SHIELD);
    }

    public void setShield(boolean shield){
        this.entityData.set(HAS_SHIELD, shield);
    }

    public int getShieldHealth(){
        return this.shieldHealth;
    }

    public void setShieldHealth(int shieldHealth){
        this.shieldHealth = shieldHealth;
    }

    @Override
    public int getMaxShieldHealth() {
        return AttributesConfig.ImperialGuardServantShield.get();
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
    }

    public boolean wantsToPickUp(ItemStack itemStack) {
        return MobsConfig.IllagerServantPickUpDrops.get()
                && this.isShieldRepair(itemStack)
                && this.getInventory().canAddItem(itemStack)
                || super.wantsToPickUp(itemStack);
    }

    public boolean validLootToStore(ItemStack itemStack) {
        return super.validLootToStore(itemStack) && !this.isShieldRepair(itemStack);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33609_) {
        if (ANIM_STATE.equals(p_33609_)) {
            if (this.level.isClientSide) {
                switch (this.entityData.get(ANIM_STATE)) {
                    case 0:
                        this.stopAllAnimations();
                        break;
                    case 1:
                        this.preAttackAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.preAttackAnimationState);
                        break;
                    case 2:
                        this.attackAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                }
            }
        }

        super.onSyncedDataUpdated(p_33609_);
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, PRE_ATTACK)){
            return 1;
        } else if (Objects.equals(animation, ATTACK)){
            return 2;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.standAnimationState);
        animationStates.add(this.preAttackAnimationState);
        animationStates.add(this.attackAnimationState);
        return animationStates;
    }

    public void stopMostAnimation(AnimationState exception){
        for (AnimationState state : this.getAnimations()){
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

    public void stopAllAnimations(){
        for (AnimationState animationState : this.getAnimations()){
            animationState.stop();
        }
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.PILLAGER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PILLAGER_DEATH;
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource p_34103_) {
        return SoundEvents.PILLAGER_HURT;
    }

    @Override
    public void die(DamageSource pCause) {
        this.playSound(ModSounds.DOUBLE_AXE_IMPACT_SHING.get(), 1.5F * 0.5F * (this.getRandom().nextIntBetweenInclusive(5, 10) * 0.1F), (this.getRandom().nextBoolean() ? 0.65F : 0.6F) * (this.getRandom().nextIntBetweenInclusive(5, 7) * 0.1F));
        this.playSound(ModSounds.DIRT_SHATTER_THREE.get(), 1.5F * 0.25F * (this.getRandom().nextIntBetweenInclusive(7, 10) * 0.1F), (this.getRandom().nextBoolean() ? 0.65F : 0.6F) * (this.getRandom().nextIntBetweenInclusive(7, 10) * 0.1F));
        this.playSound(ModSounds.HAMMER_SHIMMER_IMPACT_FOUR.get(), 1.5F * 0.4F * (this.getRandom().nextIntBetweenInclusive(5, 10) * 0.1F), (this.getRandom().nextBoolean() ? 0.65F : 0.6F) * (this.getRandom().nextIntBetweenInclusive(5, 7) * 0.1F));
        this.playSound(ModSounds.PLATE_DROP.get(), this.getSoundVolume(), this.getVoicePitch());
        super.die(pCause);
    }

    protected void playHurtSound(DamageSource p_21160_) {
        SoundEvent soundevent = this.getHurtSound(p_21160_);
        if (soundevent != null) {
            this.playSound(soundevent, 0.8F, this.getRandom().nextBoolean() ? 0.9F : 0.8F);
        }
        this.ambientSoundTime = -this.getAmbientSoundInterval();
        this.playSound(ModSounds.DOUBLE_AXE_IMPACT_SHING.get(), 0.8F * 0.5F, this.getRandom().nextIntBetweenInclusive(5, 7) * 0.1F);
        this.playSound(ModSounds.DIRT_SHATTER_THREE.get(), 0.8F * 0.25F * (this.getRandom().nextIntBetweenInclusive(7, 10) * 0.1F), this.getRandom().nextIntBetweenInclusive(7, 10) * 0.1F);
        this.playSound(ModSounds.HAMMER_SHIMMER_IMPACT_FOUR.get(), 0.8F * 0.4F * (this.getRandom().nextIntBetweenInclusive(5, 10) * 0.1F), this.getRandom().nextIntBetweenInclusive(5, 7) * 0.1F);
        this.playSound(ModSounds.PLATE.get(), this.getSoundVolume(), this.getVoicePitch());
    }

    public SoundEvent getCelebrateSound() {
        return SoundEvents.PILLAGER_CELEBRATE;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            this.idleAnimationState.animateWhen(!this.walkAnimation.isMoving() && !this.isStaying() && this.getCurrentAnimation() == 0, this.tickCount);
            this.standAnimationState.animateWhen(!this.walkAnimation.isMoving() && this.isStaying() && this.getCurrentAnimation() == 0, this.tickCount);
        } else {
            if (this.fleeCool > 0) {
                --this.fleeCool;
            }

            if (this.fleeCool <= 0) {
                if (this.getTarget() != null) {
                    if (this.getTarget().distanceTo(this) <= 3.0D && !this.hasShield()) {
                        this.isFleeing = true;
                        Vec3 vec3 = DefaultRandomPos.getPosAway(this, 16, 7, this.getTarget().position());
                        if (vec3 != null) {
                            this.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.5F);
                        }
                    }

                    if (this.isFleeing) {
                        ++this.fleeTime;
                        if (this.fleeTime >= MathHelper.secondsToTicks(5) || (this.getTarget().distanceTo(this) > 3.0D && this.getNavigation().isDone())) {
                            this.isFleeing = false;
                            this.fleeTime = 0;
                            this.fleeCool = MathHelper.secondsToTicks(5);
                        }
                    }
                }
            }

            if (this.attackCoolTick > 0) {
                --this.attackCoolTick;
            }
        }
    }

    @Override
    public void healServant() {
        super.healServant();
        if (!this.level.isClientSide) {
            if (this.getShieldHealth() < this.getMaxShieldHealth()) {
                this.itemsInInv(this::isShieldRepair)
                        .stream()
                        .findFirst()
                        .ifPresent(itemStack -> this.repairShield(this, itemStack));
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return this.hurtShielded(source, amount, () -> super.hurt(source, amount));
    }

    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {
        if (!this.hasShield()) {
            super.knockback(p_147241_, p_147242_, p_147243_);
        }
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4 || p_21375_ == 5 || p_21375_ == 6){
            this.handleShieldedEvent(p_21375_);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    @Override
    public void setChargingCrossbow(boolean p_32339_) {

    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    public void performRangedAttack(LivingEntity p_33272_, float p_33273_) {
        HeavyArrow heavyArrow = new HeavyArrow(this.level, this);
        heavyArrow.setBaseDamage(this.getArrowPower() + this.getBaseRangeDamage());
        heavyArrow.setPierceLevel((byte) 4);
        heavyArrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
        heavyArrow.setShotFromCrossbow(true);
        this.shootCrossbowProjectile(p_33272_, ItemStack.EMPTY, heavyArrow, 0.0F);
    }

    public void shootCrossbowProjectile(LivingEntity target, ItemStack stack, Projectile projectile, float angle) {
        float velocity = 4.8F;
        double d3 = target.getX() - this.getX();
        double d4 = (target.getY() + target.getBbHeight() / 2.0D) - this.getEyeY();
        double d5 = target.getZ() - this.getZ();
        projectile.shoot(d3, d4, d5, velocity, 0.0F);
        this.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(projectile);
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (!this.level.isClientSide) {
                if (!this.hasShield() && this.isShieldRepair(itemstack) && this.getTarget() == null && this.hurtTime <= 0) {
                    return this.repairShield(pPlayer, itemstack);
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public static class ImperialGuardRangedGoal extends Goal {
        private final ImperialGuardServant mob;
        @Nullable
        private LivingEntity target;
        private int attackTime = 0;

        public ImperialGuardRangedGoal(ImperialGuardServant mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (this.mob.isFleeing
                    || this.mob.attackCoolTick > 0) {
                return false;
            } else if (livingentity != null
                    && livingentity.isAlive()
                    && this.mob.hasLineOfSight(livingentity)) {
                this.target = livingentity;
                return true;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.target != null
                    && this.target.isAlive()
                    && this.mob.hasLineOfSight(this.target)
                    && !this.mob.isFleeing;
        }

        public void stop() {
            super.stop();
            this.mob.attackCoolTick = 20;
            this.attackTime = 0;
            this.target = null;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.target != null) {
                if (this.mob.isCurrentAnimation(PRE_ATTACK) || this.attackTime > 1) {
                    ++this.attackTime;
                }
                double distanceSq = this.mob.distanceToSqr(this.target);
                double distance = Mth.sqrt((float) distanceSq);
                if (distance <= 14.0F && !this.mob.hasShield()) {
                    if (this.mob.getBlockStateOn().isFaceSturdy(this.mob.level, this.mob.blockPosition(), Direction.UP, SupportType.CENTER)) {
                        this.mob.getMoveControl().strafe(-0.75F, 0);
                    }
                }

                this.mob.getNavigation().stop();
                MobUtil.instaLook(this.mob, this.target);
                if (this.attackTime >= 20) {
                    if (this.mob.getCurrentAnimation() == this.mob.getAnimationState(PRE_ATTACK)) {
                        this.mob.setAnimationState(ATTACK);
                    }
                    if (this.attackTime == 20) {
                        this.mob.performRangedAttack(this.target, 4.8F);
                    }
                    if (this.attackTime >= 40) {
                        this.attackTime = 0;
                    }
                } else {
                    if (!this.mob.isCurrentAnimation(PRE_ATTACK)) {
                        this.mob.setAnimationState(PRE_ATTACK);
                        this.mob.playSound(SoundEvents.CROSSBOW_LOADING_START);
                    }
                }
            }
        }
    }
}
