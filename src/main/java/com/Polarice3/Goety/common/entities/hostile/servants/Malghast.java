package com.Polarice3.Goety.common.entities.hostile.servants;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.common.entities.neutral.OwnedFlying;
import com.Polarice3.Goety.common.entities.projectiles.GrandLavaball;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class Malghast extends OwnedFlying {
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING = SynchedEntityData.defineId(Malghast.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(Malghast.class, EntityDataSerializers.INT);
    private float explosionPower = 1.0F;
    private int oldSwell;
    private int swell;
    private final int maxSwell = 15;

    public Malghast(EntityType<? extends OwnedFlying> type, Level p_i48578_2_) {
        super(type, p_i48578_2_);
        this.moveControl = new Malghast.MoveHelperController(this);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new FlyingGoal(this));
        this.goalSelector.addGoal(7, new LookAroundGoal(this));
        this.goalSelector.addGoal(7, new FireballAttackGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
    }

    public void tick() {
        if (this.isAlive()) {
            this.oldSwell = this.swell;
            if (this.isCharging()) {
                this.setSwellDir(1);
            } else {
                this.setSwellDir(-1);
            }

            int i = this.getSwellDir();

            this.swell += i;
            if (this.swell < 0) {
                this.swell = 0;
            }

            if (this.swell >= this.maxSwell) {
                this.swell = this.maxSwell;
            }
        }
        super.tick();
    }

    @OnlyIn(Dist.CLIENT)
    public float getSwelling(float pPartialTicks) {
        return Mth.lerp(pPartialTicks, (float)this.oldSwell, (float)this.swell) / (float)(this.maxSwell - 2);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isCharging() {
        return this.entityData.get(DATA_IS_CHARGING);
    }

    public void setCharging(boolean pAttacking) {
        this.entityData.set(DATA_IS_CHARGING, pAttacking);
    }

    public int getSwellDir() {
        return this.entityData.get(DATA_SWELL_DIR);
    }

    public void setSwellDir(int pState) {
        this.entityData.set(DATA_SWELL_DIR, pState);
    }

    public float getExplosionPower() {
        return this.explosionPower;
    }

    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pSize) {
        return 1.3F;
    }

    private static boolean isReflectedFireball(DamageSource p_238408_) {
        return p_238408_.getDirectEntity() instanceof LargeFireball && p_238408_.getEntity() instanceof Player;
    }

    public boolean isInvulnerableTo(DamageSource p_238289_) {
        return !isReflectedFireball(p_238289_) && super.isInvulnerableTo(p_238289_);
    }

    public boolean hurt(DamageSource p_32730_, float p_32731_) {
        if (isReflectedFireball(p_32730_)) {
            super.hurt(p_32730_, 1000.0F);
            return true;
        } else {
            return !this.isInvulnerableTo(p_32730_) && super.hurt(p_32730_, p_32731_);
        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_CHARGING, false);
        this.entityData.define(DATA_SWELL_DIR, -1);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, AttributesConfig.MalghastHealth.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public AttributeSupplier.Builder getConfiguredAttributes(){
        return setCustomAttributes();
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.GHAST_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.GHAST_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.GHAST_DEATH;
    }

    protected float getSoundVolume() {
        return 2.0F;
    }

    public float getVoicePitch() {
        return 0.75F;
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("ExplosionPower", this.explosionPower);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ExplosionPower", 99)) {
            this.explosionPower = pCompound.getFloat("ExplosionPower");
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (this.isNatural()){
            this.setHostile(true);
        }
        this.setBoundOrigin(this.blockPosition());
        return pSpawnData;
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        if (this.isNatural()){
            return EntityType.GHAST.getDefaultLootTable();
        } else {
            return super.getDefaultLootTable();
        }
    }

    static class FireballAttackGoal extends Goal {
        private final Malghast ghast;
        public int chargeTime;
        public boolean shotTimes;

        public FireballAttackGoal(Malghast p_i45837_1_) {
            this.ghast = p_i45837_1_;
        }

        public boolean canUse() {
            return this.ghast.getTarget() != null;
        }

        public void start() {
            this.chargeTime = 0;
            this.shotTimes = true;
        }

        public void stop() {
            this.ghast.setCharging(false);
        }

        public void tick() {
            LivingEntity livingentity = this.ghast.getTarget();
            float d0 = 64.0F;
            if (livingentity.distanceToSqr(this.ghast) < Mth.square(d0) && this.ghast.hasLineOfSight(livingentity)) {
                Level world = this.ghast.level;
                ++this.chargeTime;
                if (this.chargeTime == 10) {
                    this.shotTimes = this.ghast.random.nextFloat() >= 0.25F;
                    if (!this.ghast.isSilent()) {
                        if (this.shotTimes) {
                            this.ghast.playSound(SoundEvents.GHAST_WARN, 5.0F, (this.ghast.random.nextFloat() - this.ghast.random.nextFloat()) * 0.2F + this.ghast.getVoicePitch());
                        } else {
                            this.ghast.playSound(SoundEvents.GHAST_SCREAM, 5.0F, (this.ghast.random.nextFloat() - this.ghast.random.nextFloat()) * 0.2F + this.ghast.getVoicePitch());
                        }
                    }
                }

                if (this.chargeTime == 20) {
                    double d1 = 2.0D;
                    Vec3 vector3d = this.ghast.getViewVector(1.0F);
                    double d2 = livingentity.getX() - (this.ghast.getX() + vector3d.x * d1);
                    double d3 = livingentity.getY(0.5D) - this.ghast.getY(0.5D);
                    double d4 = livingentity.getZ() - (this.ghast.getZ() + vector3d.z * d1);
                    if (!this.ghast.isSilent()) {
                        this.ghast.playSound(SoundEvents.GHAST_SHOOT, 5.0F, (this.ghast.random.nextFloat() - this.ghast.random.nextFloat()) * 0.2F + 1.0F);
                    }

                    int power = (int) (this.ghast.getExplosionPower() + this.ghast.level.getCurrentDifficultyAt(this.ghast.blockPosition()).getSpecialMultiplier());

                    Fireball fireballentity;

                    if (this.shotTimes) {
                        fireballentity = new GrandLavaball(world, this.ghast, d2, d3, d4);
                    } else {
                        fireballentity = new LargeFireball(world, this.ghast, d2, d3, d4, power);
                    }
                    double y = this.ghast.getY() <= livingentity.getEyeY() ? this.ghast.getY(0.5D) : this.ghast.getY();
                    fireballentity.setPos(this.ghast.getX() + vector3d.x * d1, y, fireballentity.getZ() + vector3d.z * d1);
                    world.addFreshEntity(fireballentity);
                    this.ghast.knockback(1.0F, livingentity.getX() - this.ghast.getX(), livingentity.getZ() - this.ghast.getZ());
                    this.chargeTime = -40;
                }
            } else if (this.chargeTime > 0) {
                --this.chargeTime;
            }

            this.ghast.setCharging(this.chargeTime > 10);
        }
    }

    static class LookAroundGoal extends Goal {
        private final Malghast ghast;

        public LookAroundGoal(Malghast p_i45839_1_) {
            this.ghast = p_i45839_1_;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return true;
        }

        public void tick() {
            if (this.ghast.getTarget() == null) {
                Vec3 vector3d = this.ghast.getDeltaMovement();
                this.ghast.setYRot(-((float)Mth.atan2(vector3d.x, vector3d.z)) * (180F / (float)Math.PI));
            } else {
                LivingEntity livingentity = this.ghast.getTarget();
                double d1 = livingentity.getX() - this.ghast.getX();
                double d2 = livingentity.getZ() - this.ghast.getZ();
                this.ghast.getLookControl().setLookAt(livingentity, 10.0F, this.ghast.getMaxHeadXRot());
                this.ghast.setYRot(-((float)Mth.atan2(d1, d2)) * (180F / (float)Math.PI));
            }
            this.ghast.yBodyRot = this.ghast.getYRot();

        }
    }

    static class MoveHelperController extends MoveControl {
        private final Malghast ghast;
        private int floatDuration;

        public MoveHelperController(Malghast p_i45838_1_) {
            super(p_i45838_1_);
            this.ghast = p_i45838_1_;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                if (this.floatDuration-- <= 0) {
                    this.floatDuration += this.ghast.getRandom().nextInt(5) + 2;
                    Vec3 vector3d = new Vec3(this.wantedX - this.ghast.getX(), this.wantedY - this.ghast.getY(), this.wantedZ - this.ghast.getZ());
                    double d0 = vector3d.length();
                    vector3d = vector3d.normalize();
                    if (this.canReach(vector3d, Mth.ceil(d0))) {
                        this.ghast.setDeltaMovement(this.ghast.getDeltaMovement().add(vector3d.scale(0.1D)));
                    } else {
                        this.operation = MoveControl.Operation.WAIT;
                    }
                }

            }
        }

        private boolean canReach(Vec3 p_220673_1_, int p_220673_2_) {
            AABB axisalignedbb = this.ghast.getBoundingBox();

            for(int i = 1; i < p_220673_2_; ++i) {
                axisalignedbb = axisalignedbb.move(p_220673_1_);
                if (!this.ghast.level.noCollision(this.ghast, axisalignedbb)) {
                    return false;
                }
            }

            return true;
        }
    }

    static class FlyingGoal extends Goal {
        private final Malghast ghast;

        public FlyingGoal(Malghast p_i45836_1_) {
            this.ghast = p_i45836_1_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            MoveControl moveControl = this.ghast.getMoveControl();
            if (!moveControl.hasWanted()) {
                return true;
            } else {
                double d0 = moveControl.getWantedX() - this.ghast.getX();
                double d1 = moveControl.getWantedY() - this.ghast.getY();
                double d2 = moveControl.getWantedZ() - this.ghast.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            RandomSource random = this.ghast.getRandom();
            float distance = 16.0F;
            BlockPos blockPos = this.ghast.blockPosition();
            if (this.ghast.getTrueOwner() != null){
                blockPos = this.ghast.getTrueOwner().blockPosition().above(4);
            } else if (this.ghast.getTarget() != null){
                blockPos = this.ghast.getTarget().blockPosition().above(4);
            } else if (this.ghast.getBoundOrigin() != null){
                blockPos = this.ghast.getBoundOrigin();
            }

            if (blockPos != null) {
                if (this.ghast.distanceToSqr(Vec3.atCenterOf(blockPos)) < Mth.square(distance)) {
                    Vec3 vector3d = Vec3.atCenterOf(blockPos);
                    double X = this.ghast.getX() + vector3d.x * distance + (random.nextFloat() * 2.0F - 1.0F) * distance;
                    double Y = this.ghast.getY() + vector3d.y * distance + (random.nextFloat() * 2.0F - 1.0F) * distance;
                    double Z = this.ghast.getZ() + vector3d.z * distance + (random.nextFloat() * 2.0F - 1.0F) * distance;

                    this.ghast.getMoveControl().setWantedPosition(X, Y, Z, 0.25D);
                } else {
                    this.ghast.getMoveControl().setWantedPosition(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5D, 0.25D);
                }
            } else {
                double d0 = this.ghast.getX() + (random.nextFloat() * 2.0F - 1.0F) * distance;
                double d1 = this.ghast.getY() + (random.nextFloat() * 2.0F - 1.0F) * distance;
                double d2 = this.ghast.getZ() + (random.nextFloat() * 2.0F - 1.0F) * distance;
                this.ghast.getMoveControl().setWantedPosition(d0, d1, d2, 0.25D);
            }
        }
    }
}
