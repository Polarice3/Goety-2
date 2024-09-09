package com.Polarice3.Goety.common.entities.ally.undead.zombie;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
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
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class BlackguardServant extends ZombieServant{
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(BlackguardServant.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> HAS_SHIELD = SynchedEntityData.defineId(BlackguardServant.class, EntityDataSerializers.BOOLEAN);
    public int attackTick;
    public int shieldHealth = 1;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState standAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();

    public BlackguardServant(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public void attackGoal() {
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(4, new BlackguardAttackGoal());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.BlackguardServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.BlackguardServantDamage.get())
                .add(Attributes.ATTACK_KNOCKBACK, 1.0F)
                .add(Attributes.ARMOR, AttributesConfig.BlackguardServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.BlackguardServantToughness.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.BlackguardServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.BlackguardServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.BlackguardServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS), AttributesConfig.BlackguardServantToughness.get());
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
        if (pCompound.contains("ShieldHeath")){
            this.setShieldHealth(pCompound.getInt("ShieldHeath"));
        }
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("hasShield", this.hasShield());
        pCompound.putInt("ShieldHeath", this.getShieldHealth());
    }

    private boolean getFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setFlags(int mask, boolean value) {
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

    public int getShieldHealth(){
        return this.shieldHealth;
    }

    public void setShieldHealth(int shieldHealth){
        this.shieldHealth = shieldHealth;
    }

    public void destroyShield(){
        if (this.hasShield()) {
            if (this.getShieldHealth() > 1){
                this.setShieldHealth(this.getShieldHealth() - 1);
                this.playSound(SoundEvents.SHIELD_BLOCK);
            } else {
                this.setShieldHealth(0);
                this.setShield(false);
                this.playSound(SoundEvents.SHIELD_BREAK);
                if (this.level instanceof ServerLevel serverLevel){
                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.ANVIL)), this);
                }
            }
        }
    }

    public boolean isMeleeAttacking() {
        return this.getFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setFlags(1, attacking);
        this.attackTick = 0;
        this.level.broadcastEntityEvent(this, (byte) 5);
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    public EntityType<?> getVariant(Level level, BlockPos blockPos){
        return ModEntityType.BLACKGUARD_SERVANT.get();
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.standAnimationState);
        animationStates.add(this.attackAnimationState);
        return animationStates;
    }

    public void stopAllAnimations(){
        for (AnimationState animationState : this.getAnimations()){
            animationState.stop();
        }
    }

    @Override
    public void die(DamageSource pCause) {
        this.playSound(ModSounds.PLATE_DROP.get(), this.getSoundVolume(), this.getVoicePitch());
        super.die(pCause);
    }

    protected void playHurtSound(DamageSource p_21160_) {
        super.playHurtSound(p_21160_);
        this.playSound(ModSounds.PLATE.get(), this.getSoundVolume(), this.getVoicePitch());
    }

    protected SoundEvent getStepSound() {
        return ModSounds.BLACKGUARD_STEP.get();
    }

    @Override
    public void setBaby(boolean pChildZombie) {
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide){
            if (this.isAlive()){
                this.idleAnimationState.animateWhen(!this.isMeleeAttacking() && !this.isStaying() && !this.isMoving(), this.tickCount);
                this.standAnimationState.animateWhen(!this.isMeleeAttacking() && this.isStaying() && !this.isMoving(), this.tickCount);
                if (!this.isMeleeAttacking()) {
                    this.attackAnimationState.stop();
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

    protected double getAttackReachSqr(LivingEntity enemy) {
        return (double)(this.getBbWidth() * 6.0F * this.getBbWidth() * 6.0F + enemy.getBbWidth());
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr){
        return distToEnemySqr <= this.getAttackReachSqr(enemy) || this.getBoundingBox().intersects(enemy.getBoundingBox());
    }

    public Vec3 getHorizontalLookAngle() {
        return this.calculateViewVector(0, this.getYRot());
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand p_230254_2_) {
        ItemStack itemstack = pPlayer.getItemInHand(p_230254_2_);
        Item item = itemstack.getItem();
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (item == Items.ROTTEN_FLESH && this.getHealth() < this.getMaxHealth()) {
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                this.heal(2.0F);
                if (this.level instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            if (!this.hasShield() && itemstack.is(Tags.Items.INGOTS_IRON) && this.getTarget() == null && this.hurtTime <= 0){
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.setShield(true);
                this.setShieldHealth(1);
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    class BlackguardAttackGoal extends MeleeAttackGoal {
        private int delayCounter;
        private static final float SPEED = 1.0F;

        public BlackguardAttackGoal() {
            super(BlackguardServant.this, SPEED, true);
        }

        @Override
        public boolean canUse() {
            return BlackguardServant.this.getTarget() != null
                    && BlackguardServant.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            BlackguardServant.this.setAggressive(true);
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            BlackguardServant.this.getNavigation().stop();
            if (BlackguardServant.this.getTarget() == null) {
                BlackguardServant.this.setAggressive(false);
            }
        }

        @Override
        public void tick() {
            LivingEntity livingentity = BlackguardServant.this.getTarget();
            if (livingentity == null) {
                return;
            }

            BlackguardServant.this.lookControl.setLookAt(livingentity, 30.0F, 30.0F);
            double d0 = BlackguardServant.this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());

            if (--this.delayCounter <= 0 && !BlackguardServant.this.targetClose(livingentity, d0)) {
                this.delayCounter = 10;
                BlackguardServant.this.getNavigation().moveTo(livingentity, SPEED);
            }

            this.checkAndPerformAttack(livingentity, BlackguardServant.this.distanceToSqr(livingentity.getX(), livingentity.getBoundingBox().minY, livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (BlackguardServant.this.targetClose(enemy, distToEnemySqr)) {
                if (!BlackguardServant.this.isMeleeAttacking()) {
                    BlackguardServant.this.setMeleeAttacking(true);
                }
            }
        }
    }

    class MeleeGoal extends Goal {
        public MeleeGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return BlackguardServant.this.getTarget() != null
                    && BlackguardServant.this.isMeleeAttacking();
        }

        @Override
        public boolean canContinueToUse() {
            return BlackguardServant.this.attackTick < MathHelper.secondsToTicks(1.42F);
        }

        @Override
        public void start() {
            BlackguardServant.this.setMeleeAttacking(true);
            BlackguardServant.this.level.broadcastEntityEvent(BlackguardServant.this, (byte) 4);
        }

        @Override
        public void stop() {
            BlackguardServant.this.setMeleeAttacking(false);
        }

        @Override
        public void tick() {
            if (BlackguardServant.this.getTarget() != null) {
                LivingEntity livingentity = BlackguardServant.this.getTarget();
                MobUtil.instaLook(BlackguardServant.this, livingentity);
                BlackguardServant.this.setYBodyRot(BlackguardServant.this.getYHeadRot());
                BlackguardServant.this.setYRot(BlackguardServant.this.getYHeadRot());
            }
            if (BlackguardServant.this.attackTick == 1){
                BlackguardServant.this.playSound(ModSounds.BLACKGUARD_PRE_ATTACK.get(), BlackguardServant.this.getSoundVolume() + 1.0F, BlackguardServant.this.getVoicePitch());
            }
            if (BlackguardServant.this.attackTick == 9){
                BlackguardServant.this.playSound(ModSounds.BLACKGUARD_SMASH.get(), BlackguardServant.this.getSoundVolume() + 1.0F, BlackguardServant.this.getVoicePitch());
            }
            if (BlackguardServant.this.attackTick == 14) {
                double x = BlackguardServant.this.getX() + BlackguardServant.this.getHorizontalLookAngle().x * 2;
                double z = BlackguardServant.this.getZ() + BlackguardServant.this.getHorizontalLookAngle().z * 2;
                AABB aabb = MobUtil.makeAttackRange(x,
                        BlackguardServant.this.getY(),
                        z, 3, 3, 3);
                for (LivingEntity target : BlackguardServant.this.level.getEntitiesOfClass(LivingEntity.class, aabb)) {
                    if (target != BlackguardServant.this && !MobUtil.areAllies(target, BlackguardServant.this)) {
                        BlackguardServant.this.doHurtTarget(target);
                    }
                }
                if (BlackguardServant.this.level instanceof ServerLevel serverLevel){
                    BlockPos blockPos = BlockPos.containing(x, BlackguardServant.this.getY() - 1.0F, z);
                    BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, serverLevel.getBlockState(blockPos));
                    for (int i = 0; i < 2; ++i) {
                        ServerParticleUtil.circularParticles(serverLevel, option, BlackguardServant.this.getX() + BlackguardServant.this.getHorizontalLookAngle().x * 2, BlackguardServant.this.getY() + 0.25D, BlackguardServant.this.getZ() + BlackguardServant.this.getHorizontalLookAngle().z * 2, 1.5F);
                    }
                    ColorUtil colorUtil = new ColorUtil(serverLevel.getBlockState(blockPos).getMapColor(serverLevel, blockPos).col);
                    serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.5F, 1), x, BlockFinder.moveDownToGround(BlackguardServant.this), z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
