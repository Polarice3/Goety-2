package com.Polarice3.Goety.common.entities.hostile.illagers;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningBoltPacket;
import com.Polarice3.Goety.common.network.server.SThunderBoltPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

public class Crusher extends HuntingIllagerEntity{
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Crusher.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(Crusher.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> STORM = SynchedEntityData.defineId(Crusher.class, EntityDataSerializers.BOOLEAN);
    public int attackTick;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState runAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();

    public Crusher(EntityType<? extends HuntingIllagerEntity> p_i48551_1_, Level p_i48551_2_) {
        super(p_i48551_1_, p_i48551_2_);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(4, new AttackGoal(1.0D));
    }

    public void extraGoals(){
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.CrusherHealth.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.CrusherDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.CrusherHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.CrusherDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(ANIM_STATE, 0);
        this.entityData.define(STORM, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("Storm", this.isStorm());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Storm")){
            this.setStorm(pCompound.getBoolean("Storm"));
        }
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return null;
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_) {
        Raid raid = this.getCurrentRaid();
        int i = 0;
        if (p_37844_ > raid.getNumGroups(Difficulty.NORMAL)) {
            i = 1;
        }

        boolean flag = this.random.nextFloat() <= raid.getEnchantOdds();
        if (flag) {
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, i));
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
        } else if (Objects.equals(animation, "walk")){
            return 2;
        } else if (Objects.equals(animation, "run")){
            return 3;
        } else if (Objects.equals(animation, "attack")){
            return 4;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.walkAnimationState);
        animationStates.add(this.runAnimationState);
        animationStates.add(this.attackAnimationState);
        return animationStates;
    }

    public void stopAllAnimations(){
        for (AnimationState animationState : this.getAllAnimations()){
            animationState.stop();
        }
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
                        this.idleAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 2:
                        this.walkAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.walkAnimationState);
                        break;
                    case 3:
                        this.runAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.runAnimationState);
                        break;
                    case 4:
                        this.attackAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                }
            }
        }
    }

    @Override
    public void thunderHit(ServerLevel p_19927_, LightningBolt p_19928_) {
        if (!this.isStorm()) {
            this.setStorm(true);
        } else {
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600));
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600));
        }
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide){
            if (this.isAlive()){
                if (this.getCurrentAnimation() != this.getAnimationState("attack")) {
                    if (!this.isMoving()) {
                        this.setAnimationState("idle");
                    } else {
                        if (!this.isAggressive()) {
                            this.setAnimationState("walk");
                        } else {
                            this.setAnimationState("run");
                        }
                    }
                }
            }
        }
        if (this.isMeleeAttacking()) {
            ++this.attackTick;
        }
    }

    private boolean getFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setFlag(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte)(i & 255));
    }

    public boolean isMeleeAttacking() {
        return this.getFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setFlag(1, attacking);
        this.attackTick = 0;
        this.level.broadcastEntityEvent(this, (byte) 5);
    }

    public boolean isStorm(){
        return this.entityData.get(STORM);
    }

    public void setStorm(boolean storm){
        this.entityData.set(STORM, storm);
    }

    @Override
    public float getVoicePitch() {
        return 0.75F;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.VINDICATOR_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.VINDICATOR_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.VINDICATOR_HURT;
    }

    public boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;
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

    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 5){
            this.attackTick = 0;
        } else if (p_21375_ == 6){
            this.setAggressive(true);
        } else if (p_21375_ == 7){
            this.setAggressive(false);
        } else if (p_21375_ == 8){
            this.setMeleeAttacking(true);
        } else if (p_21375_ == 9){
            this.setMeleeAttacking(false);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.VINDICATOR_CELEBRATE;
    }

    public Vec3 getHorizontalLookAngle() {
        return this.calculateViewVector(0, this.getYRot());
    }

    public double getAttackReachSqr(LivingEntity enemy) {
        return (double)(this.getBbWidth() * 4.0F * this.getBbWidth() * 4.0F + enemy.getBbWidth());
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr){
        double reach = this.getAttackReachSqr(enemy);
        return distToEnemySqr <= reach || this.getBoundingBox().intersects(enemy.getBoundingBox());
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (!this.level.isClientSide && !this.isMeleeAttacking()) {
            this.setMeleeAttacking(true);
            this.level.broadcastEntityEvent(this, (byte) 8);
        }
        return true;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT) && pRecentlyHit) {
            if (this.level.random.nextFloat() < 0.025F + (pLooting * 0.01F)) {
                Item item = ModItems.GREAT_HAMMER.get();
                if (this.isStorm()) {
                    item = ModItems.STORMLANDER.get();
                }
                this.spawnAtLocation(item);
            }
        }
    }

    class AttackGoal extends MeleeAttackGoal {
        private final double moveSpeed;
        private int delayCounter;

        public AttackGoal(double moveSpeed) {
            super(Crusher.this, moveSpeed, true);
            this.moveSpeed = moveSpeed;
        }

        @Override
        public boolean canUse() {
            return Crusher.this.getTarget() != null
                    && Crusher.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            Crusher.this.setAggressive(true);
            Crusher.this.level.broadcastEntityEvent(Crusher.this, (byte) 6);
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            Crusher.this.getNavigation().stop();
            Crusher.this.setAggressive(false);
            Crusher.this.level.broadcastEntityEvent(Crusher.this, (byte) 7);
        }

        @Override
        public void tick() {
            LivingEntity livingentity = Crusher.this.getTarget();
            if (livingentity == null) {
                return;
            }

            Crusher.this.getLookControl().setLookAt(livingentity, Crusher.this.getMaxHeadYRot(), Crusher.this.getMaxHeadXRot());

            if (--this.delayCounter <= 0) {
                this.delayCounter = 10;
                Crusher.this.getNavigation().moveTo(livingentity, this.moveSpeed);
            }

            this.checkAndPerformAttack(livingentity, Crusher.this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(@NotNull LivingEntity enemy, double distToEnemySqr) {
            if (Crusher.this.targetClose(enemy, distToEnemySqr)) {
                Crusher.this.doHurtTarget(enemy);
            }
        }

    }

    class MeleeGoal extends Goal {
        private float yRot;

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return Crusher.this.getTarget() != null
                    && Crusher.this.isMeleeAttacking();
        }

        @Override
        public boolean canContinueToUse() {
            return Crusher.this.attackTick < MathHelper.secondsToTicks(1.3333F);
        }

        @Override
        public void start() {
            Crusher.this.setMeleeAttacking(true);
            Crusher.this.level.broadcastEntityEvent(Crusher.this, (byte) 8);
            if (Crusher.this.getTarget() != null){
                MobUtil.instaLook(Crusher.this, Crusher.this.getTarget());
            }
            this.yRot = Crusher.this.yBodyRot;
        }

        @Override
        public void stop() {
            Crusher.this.setAnimationState("idle");
            Crusher.this.setMeleeAttacking(false);
            Crusher.this.level.broadcastEntityEvent(Crusher.this, (byte) 9);
        }

        @Override
        public void tick() {
            Crusher.this.setYRot(this.yRot);
            Crusher.this.yBodyRot = this.yRot;
            Crusher.this.getNavigation().stop();
            if (Crusher.this.attackTick == 1) {
                Crusher.this.playSound(SoundEvents.VINDICATOR_AMBIENT, 1.0F, Crusher.this.isStorm() ? 0.75F : 1.25F);
                Crusher.this.setAnimationState("attack");
            }
            if (Crusher.this.attackTick == 11){
                Crusher.this.playSound(ModSounds.HAMMER_SWING.get());
            }
            if (Crusher.this.attackTick == 13) {
                AABB aabb = makeAttackRange(Crusher.this.getX() + Crusher.this.getHorizontalLookAngle().x * 2,
                        Crusher.this.getY(),
                        Crusher.this.getZ() + Crusher.this.getHorizontalLookAngle().z * 2, 3, 1, 3);
                for (LivingEntity target : Crusher.this.level.getEntitiesOfClass(LivingEntity.class, aabb)) {
                    if (target != Crusher.this && !target.isAlliedTo(Crusher.this) && !Crusher.this.isAlliedTo(target)) {
                        this.hurtTarget(target);
                    }
                }
                Crusher.this.playSound(ModSounds.HAMMER_IMPACT.get());
                Crusher.this.playSound(ModSounds.DIRT_DEBRIS.get());
                if (Crusher.this.isStorm()){
                    Crusher.this.playSound(ModSounds.THUNDER_STRIKE_FAST.get());
                }
                if (Crusher.this.level instanceof ServerLevel serverLevel){
                    BlockPos blockPos = new BlockPos(Crusher.this.getX() + Crusher.this.getHorizontalLookAngle().x * 2, Crusher.this.getY() - 1.0F, Crusher.this.getZ() + Crusher.this.getHorizontalLookAngle().z * 2);
                    BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, serverLevel.getBlockState(blockPos));
                    for (int i = 0; i < 8; ++i) {
                        ServerParticleUtil.circularParticles(serverLevel, option, Crusher.this.getX() + Crusher.this.getHorizontalLookAngle().x * 2, Crusher.this.getY() + 0.25D, Crusher.this.getZ() + Crusher.this.getHorizontalLookAngle().z * 2, 1.5F);
                    }
                }
            }
        }

        public void hurtTarget(Entity target) {
            float f = (float)Crusher.this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float f1 = (float)Crusher.this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
            DamageSource damageSource = Crusher.this.isStorm() ? ModDamageSource.directShock(Crusher.this) : DamageSource.mobAttack(Crusher.this);
            boolean flag = target.hurt(damageSource, f);
            if (flag) {
                if (f1 > 0.0F && target instanceof LivingEntity livingEntity) {
                    livingEntity.knockback(f1 * 0.5F, Mth.sin(Crusher.this.getYRot() * ((float)Math.PI / 180F)), -Mth.cos(Crusher.this.getYRot() * ((float)Math.PI / 180F)));
                    Crusher.this.setDeltaMovement(Crusher.this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                }

                Crusher.this.doEnchantDamageEffects(Crusher.this, target);
                Crusher.this.setLastHurtMob(target);
            }
            if (Crusher.this.level instanceof ServerLevel serverLevel) {
                if (Crusher.this.isStorm() && target instanceof LivingEntity livingEntity) {
                    BlockHitResult rayTraceResult = this.blockResult(serverLevel, Crusher.this, 16);
                    Optional<BlockPos> lightningRod = BlockFinder.findLightningRod(serverLevel, new BlockPos(rayTraceResult.getLocation()), 16);
                    if (lightningRod.isPresent()) {
                        BlockPos blockPos1 = lightningRod.get();
                        ModNetwork.sendToALL(new SLightningBoltPacket(new Vec3(blockPos1.getX(), blockPos1.getY() + 250, blockPos1.getZ()), new Vec3(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ()), 10));
                        serverLevel.playSound(null, Crusher.this.getX(), Crusher.this.getY(), Crusher.this.getZ(), ModSounds.THUNDERBOLT.get(), Crusher.this.getSoundSource(), 1.0F, 1.0F);
                    } else {
                        Vec3 vec31 = new Vec3(livingEntity.getX(), livingEntity.getY() + livingEntity.getBbHeight() / 2, livingEntity.getZ());
                        ModNetwork.sendToALL(new SLightningBoltPacket(new Vec3(livingEntity.getX(), livingEntity.getY() + 250, livingEntity.getZ()), vec31, 10));
                        chain(livingEntity, Crusher.this);
                        serverLevel.playSound(null, Crusher.this.getX(), Crusher.this.getY(), Crusher.this.getZ(), ModSounds.THUNDERBOLT.get(), Crusher.this.getSoundSource(), 1.0F, 1.0F);
                    }
                }
            }
        }

        public void chain(LivingEntity pTarget, LivingEntity pAttacker) {
            double range = 6;
            Level level = pAttacker.level;
            float oDamage = (float) pAttacker.getAttributeValue(Attributes.ATTACK_DAMAGE);

            List<Entity> harmed = new ArrayList<>();
            Predicate<Entity> selector = entity -> entity instanceof LivingEntity livingEntity && livingEntity != pAttacker && !harmed.contains(livingEntity) && MobUtil.canAttack(Crusher.this, livingEntity);
            LivingEntity prevTarget = pTarget;

            float damage = level.isThundering() ? oDamage : oDamage / 2.0F;

            for (int i = 0; i < 4; i++) {
                AABB aabb = new AABB(Vec3Util.subtract(prevTarget.position(), range), Vec3Util.add(prevTarget.position(), range));
                List<Entity> entities = level.getEntities(prevTarget, aabb, selector);
                if (!entities.isEmpty()) {
                    LivingEntity target = (LivingEntity) entities.get(level.getRandom().nextInt(entities.size()));
                    if (target.hurt(ModDamageSource.directShock(pAttacker), damage)) {
                        if (prevTarget != target) {
                            Vec3 vec3 = prevTarget.getEyePosition();
                            Vec3 vec31 = target.getEyePosition();
                            ModNetwork.sendToALL(new SThunderBoltPacket(vec3, vec31, 8));
                        }
                    }

                    harmed.add(target);
                    prevTarget = target;
                    damage--;
                }
            }
        }

        public BlockHitResult blockResult(Level worldIn, Entity entity, double range) {
            float f = entity.getXRot();
            float f1 = entity.getYRot();
            Vec3 vector3d = entity.getEyePosition(1.0F);
            float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
            float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
            float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
            float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
            float f6 = f3 * f4;
            float f7 = f2 * f4;
            Vec3 vector3d1 = vector3d.add((double)f6 * range, (double)f5 * range, (double)f7 * range);
            return worldIn.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity));
        }

        public static AABB makeAttackRange(double x, double y, double z, double sizeX, double sizeY, double sizeZ) {
            return new AABB(x - (sizeX / 2.0D), y - (sizeY / 2.0D), z - (sizeZ / 2.0D), x + (sizeX / 2.0D), y + (sizeY / 2.0D), z + (sizeZ / 2.0D));
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
