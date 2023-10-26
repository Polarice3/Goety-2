package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class VanguardServant extends AbstractSkeletonServant {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(VanguardServant.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> HAS_SHIELD = SynchedEntityData.defineId(VanguardServant.class, EntityDataSerializers.BOOLEAN);
    public int attackTick;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();

    public VanguardServant(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(4, new VanguardAttackGoal());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.VanguardServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.VanguardServantDamage.get())
                .add(Attributes.ATTACK_KNOCKBACK, 0.5F)
                .add(Attributes.ARMOR, AttributesConfig.VanguardServantArmor.get());
    }

    public AttributeSupplier.Builder getConfiguredAttributes(){
        return setCustomAttributes();
    }

    @Override
    public void reassessWeaponGoal() {
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_SHIELD, true);
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("hasShield")){
            this.setShield(pCompound.getBoolean("hasShield"));
        }
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("hasShield", this.hasShield());
    }

    private boolean getVanguardFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setVanguardFlags(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte)(i & 255));
    }

    public boolean hasShield(){
        return this.entityData.get(HAS_SHIELD);
    }

    public void setShield(boolean shield){
        this.entityData.set(HAS_SHIELD, shield);
    }

    public void destroyShield(){
        if (this.hasShield()) {
            this.setShield(false);
            this.playSound(SoundEvents.SHIELD_BREAK);
            if (this.level instanceof ServerLevel serverLevel){
                ServerParticleUtil.addParticlesAroundSelf(serverLevel, new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SPRUCE_PLANKS)), this);
            }
        }
    }

    public boolean isMeleeAttacking() {
        return this.getVanguardFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setVanguardFlags(1, attacking);
        this.attackTick = 0;
        this.level.broadcastEntityEvent(this, (byte) 5);
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.VANGUARD_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.VANGUARD_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.VANGUARD_DEATH.get();
    }

    @Override
    protected SoundEvent getStepSound() {
        return ModSounds.VANGUARD_STEP.get();
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.walkAnimationState);
        animationStates.add(this.attackAnimationState);
        return animationStates;
    }

    public void stopAllAnimations(){
        for (AnimationState animationState : this.getAnimations()){
            animationState.stop();
        }
    }

    public boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide){
            if (this.isAlive()){
                if (!this.isMeleeAttacking()) {
                    this.attackAnimationState.stop();
                    if (!this.isMoving()) {
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.walkAnimationState.stop();
                    } else {
                        this.idleAnimationState.stop();
                        this.walkAnimationState.startIfStopped(this.tickCount);
                    }
                } else {
                    this.idleAnimationState.stop();
                    this.walkAnimationState.stop();
                }
            }
        }
        if (this.isMeleeAttacking()) {
            ++this.attackTick;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level.isClientSide) {
            if (this.hasShield() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                this.destroyShield();
                return false;
            } else {
                if (this.getTarget() != null) {
                    if (source.getEntity() instanceof LivingEntity livingEntity) {
                        double d0 = this.distanceTo(this.getTarget());
                        double d1 = this.distanceTo(livingEntity);
                        if (MobUtil.ownedCanAttack(this, livingEntity) && livingEntity != this.getTrueOwner()) {
                            if (d0 > d1) {
                                this.setTarget(livingEntity);
                            }
                        }
                    }
                }
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {
        if (!this.hasShield()) {
            super.knockback(p_147241_, p_147242_, p_147243_);
        }
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4){
            this.stopAllAnimations();
            this.attackAnimationState.start(this.tickCount);
        } else if (p_21375_ == 5){
            this.attackTick = 0;
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    public boolean doHurtTarget(Entity p_21372_) {
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1 = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (p_21372_ instanceof LivingEntity) {
            f += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)p_21372_).getMobType());
            f1 += (float)EnchantmentHelper.getKnockbackBonus(this);
        }

        int i = EnchantmentHelper.getFireAspect(this);
        if (i > 0) {
            p_21372_.setSecondsOnFire(i * 4);
        }

        boolean flag = p_21372_.hurt(this.damageSources().mobAttack(this), f);
        if (flag) {
            if (f1 > 0.0F && p_21372_ instanceof LivingEntity living) {
                living.knockback((double)(f1 * 0.5F), (double)Mth.sin(this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(this.getYRot() * ((float)Math.PI / 180F))));
            }

            this.doEnchantDamageEffects(this, p_21372_);
            this.setLastHurtMob(p_21372_);
        }

        return flag;
    }

    protected double getAttackReachSqr(LivingEntity enemy) {
        return (double)(this.getBbWidth() * 6.0F * this.getBbWidth() * 6.0F + enemy.getBbWidth());
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr){
        return distToEnemySqr <= this.getAttackReachSqr(enemy) || this.getBoundingBox().intersects(enemy.getBoundingBox());
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand p_230254_2_) {
        if (!this.level.isClientSide) {
            ItemStack itemstack = pPlayer.getItemInHand(p_230254_2_);
            Item item = itemstack.getItem();
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if (item == Items.BONE && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.playSound(SoundEvents.SKELETON_STEP, 1.0F, 1.25F);
                    this.heal(2.0F);
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        this.level.addParticle(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                    }
                    return InteractionResult.CONSUME;
                }
                if (!this.hasShield() && itemstack.is(ItemTags.PLANKS) && this.getTarget() == null && this.hurtTime <= 0){
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.setShield(true);
                    this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.PASS;
    }

    class VanguardAttackGoal extends MeleeAttackGoal {
        private int delayCounter;
        private static final float SPEED = 1.25F;

        public VanguardAttackGoal() {
            super(VanguardServant.this, SPEED, true);
        }

        @Override
        public boolean canUse() {
            return VanguardServant.this.getTarget() != null && VanguardServant.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            VanguardServant.this.setAggressive(true);
            this.delayCounter = 0;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = VanguardServant.this.getTarget();
            if (livingentity == null) {
                return;
            }

            VanguardServant.this.lookControl.setLookAt(livingentity, 30.0F, 30.0F);
            double d0 = VanguardServant.this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());

            if (--this.delayCounter <= 0 && !VanguardServant.this.targetClose(livingentity, d0)) {
                this.delayCounter = 10;
                VanguardServant.this.getNavigation().moveTo(livingentity, SPEED);
            }

            this.checkAndPerformAttack(livingentity, VanguardServant.this.distanceToSqr(livingentity.getX(), livingentity.getBoundingBox().minY, livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (VanguardServant.this.targetClose(enemy, distToEnemySqr)) {
                if (!VanguardServant.this.isMeleeAttacking()) {
                    VanguardServant.this.setMeleeAttacking(true);
                }
            }
        }

        @Override
        public void stop() {
            VanguardServant.this.getNavigation().stop();
            if (VanguardServant.this.getTarget() == null) {
                VanguardServant.this.setAggressive(false);
            }
        }
    }

    class MeleeGoal extends Goal {
        public MeleeGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return VanguardServant.this.getTarget() != null && VanguardServant.this.isMeleeAttacking();
        }

        @Override
        public boolean canContinueToUse() {
            return VanguardServant.this.attackTick < 20;
        }

        @Override
        public void start() {
            VanguardServant.this.setMeleeAttacking(true);
            VanguardServant.this.level.broadcastEntityEvent(VanguardServant.this, (byte) 4);
        }

        @Override
        public void stop() {
            VanguardServant.this.setMeleeAttacking(false);
        }

        @Override
        public void tick() {
            if (VanguardServant.this.getTarget() != null && VanguardServant.this.getTarget().isAlive()) {
                LivingEntity livingentity = VanguardServant.this.getTarget();
                double d0 = VanguardServant.this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                VanguardServant.this.getLookControl().setLookAt(livingentity, VanguardServant.this.getMaxHeadYRot(), VanguardServant.this.getMaxHeadXRot());
                VanguardServant.this.setYBodyRot(VanguardServant.this.getYHeadRot());
                if (VanguardServant.this.attackTick == 8) {
                    if (VanguardServant.this.targetClose(livingentity, d0)) {
                        if (VanguardServant.this.doHurtTarget(livingentity)){
                            VanguardServant.this.playSound(ModSounds.VANGUARD_SPEAR.get());
                            for (Entity entity : getTargets(VanguardServant.this.level, VanguardServant.this, 3)){
                                if (entity instanceof LivingEntity living){
                                    if (!living.isAlliedTo(VanguardServant.this) && !VanguardServant.this.isAlliedTo(living) && living != livingentity && (!(livingentity instanceof ArmorStand) || !((ArmorStand)livingentity).isMarker()) && VanguardServant.this.canAttack(livingentity)){
                                        VanguardServant.this.doHurtTarget(living);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        public static List<Entity> getTargets(Level level, LivingEntity pSource, double pRange) {
            List<Entity> list = new ArrayList<>();
            Vec3 lookVec = pSource.getViewVector(1.0F);
            double[] lookRange = new double[] {lookVec.x() * pRange, lookVec.y() * pRange, lookVec.z() * pRange};
            List<Entity> possibleList = level.getEntities(pSource, pSource.getBoundingBox().expandTowards(lookRange[0], lookRange[1], lookRange[2]));

            for (Entity hit : possibleList) {
                if (hit.isPickable() && hit != pSource && EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(EntitySelector.LIVING_ENTITY_STILL_ALIVE).test(hit)) {
                    list.add(hit);
                }
            }
            return list;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
