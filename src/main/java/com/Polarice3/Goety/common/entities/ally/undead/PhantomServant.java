package com.Polarice3.Goety.common.entities.ally.undead;

import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.neutral.SummonedFlying;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class PhantomServant extends SummonedFlying {
    public static final float FLAP_DEGREES_PER_TICK = 7.448451F;
    public static final int TICKS_PER_FLAP = Mth.ceil(24.166098F);
    private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(PhantomServant.class, EntityDataSerializers.INT);
    Vec3 moveTargetPoint = Vec3.ZERO;
    BlockPos anchorPoint = BlockPos.ZERO;
    AttackPhase attackPhase = AttackPhase.CIRCLE;

    public PhantomServant(EntityType<? extends SummonedFlying> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new PhantomMoveControl(this);
        this.lookControl = new PhantomLookControl(this);
    }

    public boolean isFlapping() {
        return (this.getUniqueFlapTickOffset() + this.tickCount) % TICKS_PER_FLAP == 0;
    }

    protected BodyRotationControl createBodyControl() {
        return new PhantomBodyRotationControl(this);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new PhantomAttackStrategyGoal());
        this.goalSelector.addGoal(2, new PhantomSweepAttackGoal());
        this.goalSelector.addGoal(3, new PhantomCircleAroundAnchorGoal());
    }

    public void followGoal(){
        this.goalSelector.addGoal(1, new PhantomFollowOwnerGoal());
    }

    public void targetSelectGoal(){
        this.targetSelector.addGoal(1, new PhantomAttackPlayerTargetGoal());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PhantomServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.PhantomServantArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PhantomServantInitialDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.PhantomServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.PhantomServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.PhantomServantInitialDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_SIZE, 0);
    }

    public void setPhantomSize(int p_33109_) {
        this.entityData.set(ID_SIZE, Mth.clamp(p_33109_, 0, 64));
    }

    private void updatePhantomSizeInfo() {
        this.refreshDimensions();
        AttributeInstance instance = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (instance != null){
            double attack = AttributesConfig.PhantomServantSizeDamage.get();
            instance.setBaseValue(attack + this.getPhantomSize());
        }
    }

    public int getPhantomSize() {
        return this.entityData.get(ID_SIZE);
    }

    protected float getStandingEyeHeight(Pose p_33136_, EntityDimensions p_33137_) {
        return p_33137_.height * 0.35F;
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33134_) {
        if (ID_SIZE.equals(p_33134_)) {
            this.updatePhantomSizeInfo();
        }

        super.onSyncedDataUpdated(p_33134_);
    }

    @Override
    public boolean canStay() {
        return false;
    }

    @Override
    public boolean isInvisible() {
        if (this.isUpgraded() && MobsConfig.PhantomServantTranslucent.get()){
            return true;
        }
        return super.isInvisible();
    }

    @Override
    public boolean isInvisibleTo(Player p_20178_) {
        if (this.isUpgraded() && MobsConfig.PhantomServantTranslucent.get()){
            return false;
        }
        return super.isInvisibleTo(p_20178_);
    }

    public int getUniqueFlapTickOffset() {
        return this.getId() * 3;
    }

    public void tick() {
        super.tick();
        if (this.isUpgraded()){
            this.noPhysics = true;
        }
        if (this.level.isClientSide) {
            float f = Mth.cos((float)(this.getUniqueFlapTickOffset() + this.tickCount) * 7.448451F * ((float)Math.PI / 180F) + (float)Math.PI);
            float f1 = Mth.cos((float)(this.getUniqueFlapTickOffset() + this.tickCount + 1) * 7.448451F * ((float)Math.PI / 180F) + (float)Math.PI);
            if (f > 0.0F && f1 <= 0.0F) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.PHANTOM_FLAP, this.getSoundSource(), 0.95F + this.random.nextFloat() * 0.05F, 0.95F + this.random.nextFloat() * 0.05F, false);
            }

            int i = this.getPhantomSize();
            float f2 = Mth.cos(this.getYRot() * ((float)Math.PI / 180F)) * (1.3F + 0.21F * (float)i);
            float f3 = Mth.sin(this.getYRot() * ((float)Math.PI / 180F)) * (1.3F + 0.21F * (float)i);
            float f4 = (0.3F + f * 0.45F) * ((float)i * 0.2F + 1.0F);
            this.level.addParticle(ParticleTypes.MYCELIUM, this.getX() + (double)f2, this.getY() + (double)f4, this.getZ() + (double)f3, 0.0D, 0.0D, 0.0D);
            this.level.addParticle(ParticleTypes.MYCELIUM, this.getX() - (double)f2, this.getY() + (double)f4, this.getZ() - (double)f3, 0.0D, 0.0D, 0.0D);
        }

    }

    @Override
    protected boolean isSunSensitive() {
        return true;
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_33126_, DifficultyInstance p_33127_, MobSpawnType p_33128_, @Nullable SpawnGroupData p_33129_, @Nullable CompoundTag p_33130_) {
        this.anchorPoint = this.blockPosition().above(5);
        this.setPhantomSize(0);
        return super.finalizeSpawn(p_33126_, p_33127_, p_33128_, p_33129_, p_33130_);
    }

    public void setTarget(@Nullable LivingEntity target) {
        this.overrideSetTarget(target);
    }

    public void readAdditionalSaveData(CompoundTag p_33132_) {
        super.readAdditionalSaveData(p_33132_);
        if (p_33132_.contains("AX")) {
            this.anchorPoint = new BlockPos(p_33132_.getInt("AX"), p_33132_.getInt("AY"), p_33132_.getInt("AZ"));
        }
        this.setPhantomSize(p_33132_.getInt("Size"));
    }

    public void addAdditionalSaveData(CompoundTag p_33141_) {
        super.addAdditionalSaveData(p_33141_);
        p_33141_.putInt("AX", this.anchorPoint.getX());
        p_33141_.putInt("AY", this.anchorPoint.getY());
        p_33141_.putInt("AZ", this.anchorPoint.getZ());
        p_33141_.putInt("Size", this.getPhantomSize());
    }

    public boolean shouldRenderAtSqrDistance(double p_33107_) {
        return true;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.PHANTOM_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_33152_) {
        return SoundEvents.PHANTOM_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PHANTOM_DEATH;
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public boolean canAttackType(EntityType<?> p_33111_) {
        return true;
    }

    public EntityDimensions getDimensions(Pose p_33113_) {
        int i = this.getPhantomSize();
        EntityDimensions entitydimensions = super.getDimensions(p_33113_);
        float f = (entitydimensions.width + 0.2F * (float)i) / entitydimensions.width;
        return entitydimensions.scale(f);
    }

    public double getPassengersRidingOffset() {
        return this.getEyeHeight();
    }

    enum AttackPhase {
        CIRCLE,
        SWOOP;
    }

    class PhantomAttackPlayerTargetGoal extends Goal {
        private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0D).selector(SummonTargetGoal.predicate(PhantomServant.this));
        private int nextScanTick = reducedTickDelay(20);

        public boolean canUse() {
            if (this.nextScanTick > 0) {
                --this.nextScanTick;
            } else {
                this.nextScanTick = reducedTickDelay(60);
                List<LivingEntity> list = PhantomServant.this.level.getNearbyEntities(LivingEntity.class, this.attackTargeting, PhantomServant.this, PhantomServant.this.getBoundingBox().inflate(16.0D, 64.0D, 16.0D));
                if (!list.isEmpty()) {
                    list.sort(Comparator.<Entity, Double>comparing(Entity::getY).reversed());

                    for(LivingEntity livingEntity : list) {
                        if (!MobUtil.areAllies(PhantomServant.this, livingEntity)) {
                            PhantomServant.this.setTarget(livingEntity);
                            return true;
                        }
                    }
                }

            }
            return false;
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = PhantomServant.this.getTarget();
            return livingentity != null && livingentity.isAlive();
        }
    }

    class PhantomAttackStrategyGoal extends Goal {
        private int nextSweepTick;

        public boolean canUse() {
            LivingEntity livingentity = PhantomServant.this.getTarget();
            return livingentity != null;
        }

        public void start() {
            this.nextSweepTick = this.adjustedTickDelay(10);
            PhantomServant.this.attackPhase = AttackPhase.CIRCLE;
            this.setAnchorAboveTarget();
        }

        public void stop() {
            if (PhantomServant.this.isGuardingArea()){
                PhantomServant.this.anchorPoint = PhantomServant.this.getBoundPos();
            } else {
                PhantomServant.this.anchorPoint = PhantomServant.this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, PhantomServant.this.anchorPoint).above(10 + PhantomServant.this.random.nextInt(20));
            }
        }

        public void tick() {
            if (PhantomServant.this.attackPhase == AttackPhase.CIRCLE) {
                --this.nextSweepTick;
                if (this.nextSweepTick <= 0) {
                    PhantomServant.this.attackPhase = AttackPhase.SWOOP;
                    this.setAnchorAboveTarget();
                    this.nextSweepTick = this.adjustedTickDelay((8 + PhantomServant.this.random.nextInt(4)) * 20);
                    PhantomServant.this.playSound(SoundEvents.PHANTOM_SWOOP, 10.0F, 0.95F + PhantomServant.this.random.nextFloat() * 0.1F);
                }
            }

        }

        private void setAnchorAboveTarget() {
            if (PhantomServant.this.getTarget() != null) {
                boolean flag = true;
                if (PhantomServant.this.isGuardingArea() && PhantomServant.this.getBoundPos() != null){
                    if (PhantomServant.this.getTarget().distanceToSqr(PhantomServant.this.vec3BoundPos()) > Mth.square(GUARDING_RANGE)){
                        PhantomServant.this.anchorPoint = PhantomServant.this.getBoundPos();
                        flag = false;
                    }
                }
                if (flag) {
                    PhantomServant.this.anchorPoint = PhantomServant.this.getTarget().blockPosition().above(20 + PhantomServant.this.random.nextInt(20));
                    if (PhantomServant.this.anchorPoint.getY() < PhantomServant.this.level.getSeaLevel()) {
                        PhantomServant.this.anchorPoint = new BlockPos(PhantomServant.this.anchorPoint.getX(), PhantomServant.this.level.getSeaLevel() + 1, PhantomServant.this.anchorPoint.getZ());
                    }
                }
            }
        }
    }

    class PhantomBodyRotationControl extends BodyRotationControl {
        public PhantomBodyRotationControl(Mob p_33216_) {
            super(p_33216_);
        }

        public void clientTick() {
            PhantomServant.this.yHeadRot = PhantomServant.this.yBodyRot;
            PhantomServant.this.yBodyRot = PhantomServant.this.getYRot();
        }
    }

    class PhantomFollowOwnerGoal extends Goal{

        public boolean canUse() {
            LivingEntity livingentity = PhantomServant.this.getTrueOwner();
            return livingentity != null && PhantomServant.this.isFollowing() && PhantomServant.this.getTarget() == null && PhantomServant.this.attackPhase != AttackPhase.SWOOP;
        }

        public void start() {
            PhantomServant.this.attackPhase = AttackPhase.CIRCLE;
            this.setAnchorAboveOwner();
        }

        public void stop() {
            if (PhantomServant.this.isGuardingArea()){
                PhantomServant.this.anchorPoint = PhantomServant.this.getBoundPos();
            } else {
                PhantomServant.this.anchorPoint = PhantomServant.this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, PhantomServant.this.anchorPoint).above(10 + PhantomServant.this.random.nextInt(20));
            }
        }

        public void tick() {
            if (PhantomServant.this.attackPhase == AttackPhase.CIRCLE) {
                this.setAnchorAboveOwner();
            }

        }

        private void setAnchorAboveOwner() {
            PhantomServant.this.anchorPoint = PhantomServant.this.getTrueOwner().blockPosition().above(10 + PhantomServant.this.random.nextInt(10));
        }
    }

    class PhantomCircleAroundAnchorGoal extends PhantomMoveTargetGoal {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        public boolean canUse() {
            return PhantomServant.this.getTarget() == null || PhantomServant.this.attackPhase == AttackPhase.CIRCLE;
        }

        public void start() {
            this.distance = 5.0F + PhantomServant.this.random.nextFloat() * 10.0F;
            this.height = -4.0F + PhantomServant.this.random.nextFloat() * 9.0F;
            this.clockwise = PhantomServant.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.selectNext();
        }

        public void tick() {
            if (PhantomServant.this.random.nextInt(this.adjustedTickDelay(350)) == 0) {
                this.height = -4.0F + PhantomServant.this.random.nextFloat() * 9.0F;
            }

            if (PhantomServant.this.random.nextInt(this.adjustedTickDelay(250)) == 0) {
                ++this.distance;
                if (this.distance > 15.0F) {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (PhantomServant.this.random.nextInt(this.adjustedTickDelay(450)) == 0) {
                this.angle = PhantomServant.this.random.nextFloat() * 2.0F * (float)Math.PI;
                this.selectNext();
            }

            if (this.touchingTarget()) {
                this.selectNext();
            }

            if (PhantomServant.this.moveTargetPoint.y < PhantomServant.this.getY() && !PhantomServant.this.level.isEmptyBlock(PhantomServant.this.blockPosition().below(1))) {
                this.height = Math.max(1.0F, this.height);
                this.selectNext();
            }

            if (PhantomServant.this.moveTargetPoint.y > PhantomServant.this.getY() && !PhantomServant.this.level.isEmptyBlock(PhantomServant.this.blockPosition().above(1))) {
                this.height = Math.min(-1.0F, this.height);
                this.selectNext();
            }

        }

        private void selectNext() {
            if (PhantomServant.this.isGuardingArea() && PhantomServant.this.getBoundPos() != null){
                PhantomServant.this.anchorPoint = PhantomServant.this.getBoundPos();
            } else if (BlockPos.ZERO.equals(PhantomServant.this.anchorPoint)) {
                PhantomServant.this.anchorPoint = PhantomServant.this.blockPosition();
            }

            this.angle += this.clockwise * 15.0F * ((float)Math.PI / 180F);
            PhantomServant.this.moveTargetPoint = Vec3.atLowerCornerOf(PhantomServant.this.anchorPoint).add((double)(this.distance * Mth.cos(this.angle)), (double)(-4.0F + this.height), (double)(this.distance * Mth.sin(this.angle)));
        }
    }

    static class PhantomLookControl extends LookControl {
        public PhantomLookControl(Mob p_33235_) {
            super(p_33235_);
        }

        public void tick() {
        }
    }

    class PhantomMoveControl extends MoveControl {
        private float speed = 0.1F;

        public PhantomMoveControl(Mob p_33241_) {
            super(p_33241_);
        }

        public void tick() {
            if (PhantomServant.this.horizontalCollision) {
                PhantomServant.this.setYRot(PhantomServant.this.getYRot() + 180.0F);
                this.speed = 0.1F;
            }

            double d0 = PhantomServant.this.moveTargetPoint.x - PhantomServant.this.getX();
            double d1 = PhantomServant.this.moveTargetPoint.y - PhantomServant.this.getY();
            double d2 = PhantomServant.this.moveTargetPoint.z - PhantomServant.this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            if (Math.abs(d3) > (double)1.0E-5F) {
                double d4 = 1.0D - Math.abs(d1 * (double)0.7F) / d3;
                d0 *= d4;
                d2 *= d4;
                d3 = Math.sqrt(d0 * d0 + d2 * d2);
                double d5 = Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);
                float f = PhantomServant.this.getYRot();
                float f1 = (float)Mth.atan2(d2, d0);
                float f2 = Mth.wrapDegrees(PhantomServant.this.getYRot() + 90.0F);
                float f3 = Mth.wrapDegrees(f1 * (180F / (float)Math.PI));
                PhantomServant.this.setYRot(Mth.approachDegrees(f2, f3, 4.0F) - 90.0F);
                PhantomServant.this.yBodyRot = PhantomServant.this.getYRot();
                if (Mth.degreesDifferenceAbs(f, PhantomServant.this.getYRot()) < 3.0F) {
                    this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
                } else {
                    this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
                }

                float f4 = (float)(-(Mth.atan2(-d1, d3) * (double)(180F / (float)Math.PI)));
                PhantomServant.this.setXRot(f4);
                float f5 = PhantomServant.this.getYRot() + 90.0F;
                double d6 = (double)(this.speed * Mth.cos(f5 * ((float)Math.PI / 180F))) * Math.abs(d0 / d5);
                double d7 = (double)(this.speed * Mth.sin(f5 * ((float)Math.PI / 180F))) * Math.abs(d2 / d5);
                double d8 = (double)(this.speed * Mth.sin(f4 * ((float)Math.PI / 180F))) * Math.abs(d1 / d5);
                Vec3 vec3 = PhantomServant.this.getDeltaMovement();
                PhantomServant.this.setDeltaMovement(vec3.add((new Vec3(d6, d8, d7)).subtract(vec3).scale(0.2D)));
            }

        }
    }

    abstract class PhantomMoveTargetGoal extends Goal {
        public PhantomMoveTargetGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        protected boolean touchingTarget() {
            return PhantomServant.this.moveTargetPoint.distanceToSqr(PhantomServant.this.getX(), PhantomServant.this.getY(), PhantomServant.this.getZ()) < 4.0D;
        }
    }

    class PhantomSweepAttackGoal extends PhantomMoveTargetGoal {
        private static final int CAT_SEARCH_TICK_DELAY = 20;
        private boolean isScaredOfCat;
        private int catSearchTick;

        public boolean canUse() {
            return PhantomServant.this.getTarget() != null && PhantomServant.this.attackPhase == AttackPhase.SWOOP;
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = PhantomServant.this.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else {
                if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
                    return false;
                }

                if (!this.canUse()) {
                    return false;
                } else {
                    if (PhantomServant.this.tickCount > this.catSearchTick) {
                        this.catSearchTick = PhantomServant.this.tickCount + 20;
                        List<Cat> list = PhantomServant.this.level.getEntitiesOfClass(Cat.class, PhantomServant.this.getBoundingBox().inflate(16.0D), EntitySelector.ENTITY_STILL_ALIVE);

                        for(Cat cat : list) {
                            cat.hiss();
                        }

                        this.isScaredOfCat = !list.isEmpty();
                    }

                    return !this.isScaredOfCat;
                }
            }
        }

        public void stop() {
            PhantomServant.this.setTarget(null);
            PhantomServant.this.attackPhase = AttackPhase.CIRCLE;
        }

        public void tick() {
            LivingEntity livingentity = PhantomServant.this.getTarget();
            if (livingentity != null) {
                PhantomServant.this.moveTargetPoint = new Vec3(livingentity.getX(), livingentity.getY(0.5D), livingentity.getZ());
                if (PhantomServant.this.getBoundingBox().inflate((double)0.2F).intersects(livingentity.getBoundingBox())) {
                    PhantomServant.this.doHurtTarget(livingentity);
                    PhantomServant.this.attackPhase = AttackPhase.CIRCLE;
                    if (!PhantomServant.this.isSilent()) {
                        PhantomServant.this.level.levelEvent(1039, PhantomServant.this.blockPosition(), 0);
                    }
                } else if (PhantomServant.this.horizontalCollision || PhantomServant.this.hurtTime > 0) {
                    PhantomServant.this.attackPhase = AttackPhase.CIRCLE;
                }

            }
        }
    }
}
