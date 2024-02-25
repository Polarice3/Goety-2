package com.Polarice3.Goety.common.entities.ally.undead.skeleton;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.MobsConfig;
import com.Polarice3.Goety.common.entities.ai.CreatureCrossbowAttackGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.projectiles.Harpoon;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CrossbowHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class SunkenSkeletonServant extends AbstractSkeletonServant implements CrossbowAttackMob {
    private final CreatureCrossbowAttackGoal<SunkenSkeletonServant> crossbowAttackGoal = new CreatureCrossbowAttackGoal<>(this, 1.0D, 8.0F);
    private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(SunkenSkeletonServant.class, EntityDataSerializers.BOOLEAN);
    private boolean searchingForLand;
    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;

    public SunkenSkeletonServant(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new MoveHelperController(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.waterNavigation = new WaterBoundPathNavigation(this, worldIn);
        this.groundNavigation = new GroundPathNavigation(this, worldIn);
    }

    @Override
    public float getStepHeight() {
        return 1.0F;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new GoToWaterGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new FollowOwnerWaterGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(6, new SwimUpGoal(this, 1.0D, this.level.getSeaLevel()));
        this.goalSelector.addGoal(7, new WaterWanderGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.SunkenSkeletonServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SunkenSkeletonServantDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.SunkenSkeletonServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.SunkenSkeletonServantDamage.get());
    }

    public void reassessWeaponGoal() {
        if (!this.level.isClientSide) {
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.crossbowAttackGoal);
            ItemStack itemstack = this.getMainHandItem();
            if (itemstack.getItem() instanceof CrossbowItem) {
                this.goalSelector.addGoal(3, this.crossbowAttackGoal);
            } else {
                this.goalSelector.addGoal(3, this.meleeGoal);
            }

        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CHARGING_CROSSBOW, false);
    }

    protected SoundEvent getAmbientSound() {
        return this.isInWater() ? ModSounds.SUNKEN_SKELETON_AMBIENT.get() : SoundEvents.SKELETON_AMBIENT;
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return this.isInWater() ? ModSounds.SUNKEN_SKELETON_HURT.get() : SoundEvents.SKELETON_HURT;
    }

    protected SoundEvent getDeathSound() {
        return this.isInWater() ? ModSounds.SUNKEN_SKELETON_DEATH.get() : SoundEvents.SKELETON_DEATH;
    }

    @Override
    protected SoundEvent getStepSound() {
        return this.isInWater() ? ModSounds.SUNKEN_SKELETON_STEP.get() : SoundEvents.SKELETON_STEP;
    }

    public boolean isPushedByFluid(FluidType type) {
        return !this.isSwimming();
    }

    public boolean checkSpawnObstruction(LevelReader pLevel) {
        return pLevel.isUnobstructed(this);
    }

    private boolean wantsToSwim() {
        if (this.searchingForLand) {
            return true;
        } else if (this.getTarget() != null) {
            return this.getTarget().isInWater();
        } else {
            return this.getTrueOwner() != null && this.getTrueOwner().isInWater();
        }
    }

    public void travel(@NotNull Vec3 pTravelVector) {
        if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
            this.moveRelative(0.01F, pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(pTravelVector);
        }

    }

    public void updateSwimming() {
        if (!this.level.isClientSide) {
            if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }

    }

    protected boolean closeToNextPos() {
        Path path = this.getNavigation().getPath();
        if (path != null) {
            BlockPos blockpos = path.getTarget();
            double d0 = this.distanceToSqr((double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ());
            return d0 < 4.0D;
        }

        return false;
    }

    public void setSearchingForLand(boolean p_204713_1_) {
        this.searchingForLand = p_204713_1_;
    }

    protected void populateDefaultEquipmentSlots(RandomSource p_219059_, DifficultyInstance p_219060_) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
    }

    @Override
    public boolean isChargingCrossbow() {
        return this.entityData.get(IS_CHARGING_CROSSBOW);
    }

    public void setChargingCrossbow(boolean p_33302_) {
        this.entityData.set(IS_CHARGING_CROSSBOW, p_33302_);
    }

    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    public void performRangedAttack(@NotNull LivingEntity p_33272_, float p_33273_) {
        this.performCrossbowAttack(this, 1.6F);
    }

    public void performCrossbowAttack(@NotNull LivingEntity shooter, float velocity) {
        InteractionHand interactionhand = ProjectileUtil.getWeaponHoldingHand(shooter, item -> item instanceof CrossbowItem);
        ItemStack itemstack = shooter.getItemInHand(interactionhand);
        if (shooter.isHolding(is -> is.getItem() instanceof CrossbowItem)) {
            SoundEvent soundEvent = this.isInWater() ? ModSounds.SUNKEN_SKELETON_SHOOT.get() : SoundEvents.CROSSBOW_SHOOT;
            CrossbowHelper.performCustomShooting(shooter.level, shooter, interactionhand, itemstack, this.getArrow(itemstack, 1.0F), soundEvent, velocity, (float)(14 - shooter.level.getDifficulty().getId() * 4));
        }

        this.onCrossbowAttackPerformed();
    }

    public void shootCrossbowProjectile(@NotNull LivingEntity target, @NotNull ItemStack itemStack, @NotNull Projectile projectile, float v) {
        this.shootCrossbowProjectile(this, target, projectile, v, 1.6F);
    }

    public void shootCrossbowProjectile(LivingEntity p_32323_, LivingEntity p_32324_, Projectile p_32325_, float p_32326_, float p_32327_) {
        double d0 = p_32324_.getX() - p_32323_.getX();
        double d1 = p_32324_.getZ() - p_32323_.getZ();
        double d2 = Math.sqrt(d0 * d0 + d1 * d1);
        double d3 = p_32324_.getY(0.3333333333333333D) - p_32325_.getY() + d2 * (double)0.2F;
        Vector3f vector3f = this.getProjectileShotVector(p_32323_, new Vec3(d0, d3, d1), p_32326_);
        p_32325_.shoot((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), p_32327_, (float)(14 - p_32323_.level.getDifficulty().getId() * 4));
        SoundEvent soundEvent = this.isInWater() ? ModSounds.SUNKEN_SKELETON_SHOOT.get() : SoundEvents.CROSSBOW_SHOOT;
        p_32323_.playSound(soundEvent, 1.0F, 1.0F / (p_32323_.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    public AbstractArrow getArrow(ItemStack pArrowStack, float pDistanceFactor) {
        Harpoon harpoon = new Harpoon(this.level, this);
        harpoon.setEffectsFromItem(pArrowStack);
        harpoon.setEnchantmentEffectsFromEntity(this, pDistanceFactor);
        harpoon.setSoundEvent(SoundEvents.CROSSBOW_HIT);
        harpoon.setShotFromCrossbow(true);
        int i = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.PIERCING, this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof CrossbowItem)));
        if (i > 0) {
            harpoon.setPierceLevel((byte)i);
        }
        harpoon.pickup = Harpoon.Pickup.DISALLOWED;

        return harpoon;
    }

    static class MoveHelperController extends MoveControl {
        private final SunkenSkeletonServant skeletonServant;

        public MoveHelperController(SunkenSkeletonServant p_i48909_1_) {
            super(p_i48909_1_);
            this.skeletonServant = p_i48909_1_;
        }

        public void tick() {
            LivingEntity livingentity = this.skeletonServant.getTarget();
            LivingEntity owner = this.skeletonServant.getTrueOwner();
            if (this.skeletonServant.wantsToSwim() && this.skeletonServant.isInWater()) {
                if (livingentity != null && livingentity.getY() > this.skeletonServant.getY() || this.skeletonServant.searchingForLand) {
                    this.skeletonServant.setDeltaMovement(this.skeletonServant.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                } else if (owner != null && owner.getY() > this.skeletonServant.getY()){
                    this.skeletonServant.setDeltaMovement(this.skeletonServant.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                }

                if (this.operation != MoveControl.Operation.MOVE_TO || this.skeletonServant.getNavigation().isDone()) {
                    this.skeletonServant.setSpeed(0.0F);
                    return;
                }

                double d0 = this.wantedX - this.skeletonServant.getX();
                double d1 = this.wantedY - this.skeletonServant.getY();
                double d2 = this.wantedZ - this.skeletonServant.getZ();
                double d3 = Mth.sqrt((float) (d0 * d0 + d1 * d1 + d2 * d2));
                d1 = d1 / d3;
                float f = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.skeletonServant.setYRot(this.rotlerp(this.skeletonServant.getYRot(), f, 90.0F));
                this.skeletonServant.setYBodyRot(this.skeletonServant.getYRot());
                float f1 = (float)(this.speedModifier * this.skeletonServant.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float f2 = Mth.lerp(0.125F, this.skeletonServant.getSpeed(), f1);
                this.skeletonServant.setSpeed(f2);
                this.skeletonServant.setDeltaMovement(this.skeletonServant.getDeltaMovement().add((double)f2 * d0 * 0.005D, (double)f2 * d1 * 0.1D, (double)f2 * d2 * 0.005D));
            } else {
                if (!this.skeletonServant.onGround) {
                    this.skeletonServant.setDeltaMovement(this.skeletonServant.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
                }

                super.tick();
            }

        }
    }

    static class GoToWaterGoal extends Goal {
        private final SunkenSkeletonServant mob;
        private double wantedX;
        private double wantedY;
        private double wantedZ;
        private final double speedModifier;
        private final Level level;

        public GoToWaterGoal(SunkenSkeletonServant p_i48910_1_, double p_i48910_2_) {
            this.mob = p_i48910_1_;
            this.speedModifier = p_i48910_2_;
            this.level = p_i48910_1_.level;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (this.mob.getTrueOwner() != null){
                if (!this.mob.getTrueOwner().isInWater()){
                    return false;
                }
            } else if (!this.level.isDay()) {
                return false;
            }
            if (this.mob.isInWater()) {
                return false;
            }
            Vec3 vector3d = this.getWaterPos();
            if (vector3d == null) {
                return false;
            } else {
                this.wantedX = vector3d.x;
                this.wantedY = vector3d.y;
                this.wantedZ = vector3d.z;
                return true;
            }
        }

        public boolean canContinueToUse() {
            return !this.mob.getNavigation().isDone();
        }

        public void start() {
            this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        }

        @Nullable
        private Vec3 getWaterPos() {
            RandomSource random = this.mob.getRandom();
            BlockPos blockpos = this.mob.blockPosition();

            for(int i = 0; i < 10; ++i) {
                BlockPos blockpos1 = blockpos.offset(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
                if (this.level.getBlockState(blockpos1).is(Blocks.WATER)) {
                    return Vec3.atBottomCenterOf(blockpos1);
                }
            }

            return null;
        }
    }

    static class SwimUpGoal extends Goal {
        private final SunkenSkeletonServant skeleton;
        private final double speedModifier;
        private final int seaLevel;
        private boolean stuck;

        public SwimUpGoal(SunkenSkeletonServant p_i48908_1_, double p_i48908_2_, int p_i48908_4_) {
            this.skeleton = p_i48908_1_;
            this.speedModifier = p_i48908_2_;
            this.seaLevel = p_i48908_4_;
        }

        public boolean canUse() {
            if (this.skeleton.getTrueOwner() != null){
                if (this.skeleton.getTrueOwner().isUnderWater()){
                    return false;
                }
            } else if (this.skeleton.level.isDay()) {
                return false;
            }
            return this.skeleton.isInWater() && this.skeleton.getY() < (double)(this.seaLevel - 2);
        }

        public boolean canContinueToUse() {
            return this.canUse() && !this.stuck;
        }

        public void tick() {
            if (this.skeleton.getY() < (double)(this.seaLevel - 1) && (this.skeleton.getNavigation().isDone() || this.skeleton.closeToNextPos())) {
                Vec3 vec3 = DefaultRandomPos.getPosTowards(this.skeleton, 4, 8, new Vec3(this.skeleton.getX(), (double)(this.seaLevel - 1), this.skeleton.getZ()), (double)((float)Math.PI / 2F));
                if (vec3 == null) {
                    this.stuck = true;
                    return;
                }

                this.skeleton.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, this.speedModifier);
            }

        }

        public void start() {
            this.skeleton.setSearchingForLand(true);
            this.stuck = false;
        }

        public void stop() {
            this.skeleton.setSearchingForLand(false);
        }
    }

    public static class FollowOwnerWaterGoal extends Goal {
        protected final Summoned summonedEntity;
        private LivingEntity owner;
        private final LevelReader level;
        private final double followSpeed;
        private Path path;
        private double pathedTargetX;
        private double pathedTargetY;
        private double pathedTargetZ;
        private int ticksUntilNextPathRecalculation;
        private final PathNavigation navigation;
        private final float maxDist;
        private final float minDist;

        public FollowOwnerWaterGoal(Summoned summonedEntity, double speed, float minDist, float maxDist) {
            this.summonedEntity = summonedEntity;
            this.level = summonedEntity.level;
            this.followSpeed = speed;
            this.navigation = summonedEntity.getNavigation();
            this.minDist = minDist;
            this.maxDist = maxDist;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.summonedEntity.getTrueOwner();
            if (livingentity == null) {
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } else if (this.summonedEntity.distanceToSqr(livingentity) < (double)(this.minDist * this.minDist)) {
                return false;
            } else if (this.summonedEntity.isWandering() || this.summonedEntity.isStaying()) {
                return false;
            } else if (this.summonedEntity.getTarget() != null) {
                return false;
            } else {
                this.owner = livingentity;
                if (!livingentity.isAlive()) {
                    return false;
                } else {
                    this.path = this.summonedEntity.getNavigation().createPath(livingentity, 0);
                    if (this.path != null) {
                        return true;
                    }
                }
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (this.navigation.isDone()) {
                return false;
            } else if (this.summonedEntity.getTarget() != null){
                return false;
            } else {
                return !(this.summonedEntity.distanceToSqr(this.owner) <= (double)(this.maxDist * this.maxDist));
            }
        }

        public void start() {
            this.summonedEntity.getNavigation().moveTo(this.path, this.followSpeed);
            this.ticksUntilNextPathRecalculation = 0;
        }

        public void stop() {
            this.owner = null;
            this.navigation.stop();
        }

        public void tick() {
            this.summonedEntity.getLookControl().setLookAt(this.owner, 30.0F, 30.0F);
            double d0 = this.summonedEntity.distanceToSqr(this.owner.getX(), this.owner.getY(), this.owner.getZ());
            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
            if (this.ticksUntilNextPathRecalculation == 0 && (this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D || this.owner.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D || this.summonedEntity.getRandom().nextFloat() < 0.05F)) {
                this.pathedTargetX = this.owner.getX();
                this.pathedTargetY = this.owner.getY();
                this.pathedTargetZ = this.owner.getZ();
                this.ticksUntilNextPathRecalculation = 4 + this.summonedEntity.getRandom().nextInt(7);
                if (d0 > 144.0D && MobsConfig.UndeadTeleport.get()){
                    this.tryToTeleportNearEntity();
                }
                if (d0 > 1024.0D) {
                    this.ticksUntilNextPathRecalculation += 10;
                } else if (d0 > 256.0D) {
                    this.ticksUntilNextPathRecalculation += 5;
                }

                if (!this.summonedEntity.getNavigation().moveTo(this.owner, this.followSpeed)) {
                    this.ticksUntilNextPathRecalculation += 15;
                }
            }
        }

        private void tryToTeleportNearEntity() {
            BlockPos blockpos = this.owner.blockPosition();

            for(int i = 0; i < 10; ++i) {
                int j = this.getRandomNumber(-3, 3);
                int k = this.getRandomNumber(-1, 1);
                int l = this.getRandomNumber(-3, 3);
                boolean flag = this.tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
                if (flag) {
                    return;
                }
            }

        }

        private boolean tryToTeleportToLocation(int x, int y, int z) {
            if (Math.abs((double)x - this.owner.getX()) < 2.0D && Math.abs((double)z - this.owner.getZ()) < 2.0D) {
                return false;
            } else if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
                return false;
            } else {
                this.summonedEntity.moveTo((double)x + 0.5D, (double)y, (double)z + 0.5D, this.summonedEntity.getYRot(), this.summonedEntity.getXRot());
                this.navigation.stop();
                return true;
            }
        }

        private boolean isTeleportFriendlyBlock(BlockPos pos) {
            BlockPathTypes pathnodetype = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pos.mutable());
            if (pathnodetype != BlockPathTypes.WALKABLE) {
                return false;
            } else {
                BlockState blockstate = this.level.getBlockState(pos.below());
                if (blockstate.getBlock() instanceof LeavesBlock) {
                    return false;
                } else {
                    BlockPos blockpos = pos.subtract(this.summonedEntity.blockPosition());
                    return this.level.noCollision(this.summonedEntity, this.summonedEntity.getBoundingBox().move(blockpos));
                }
            }
        }

        private int getRandomNumber(int min, int max) {
            return this.summonedEntity.getRandom().nextInt(max - min + 1) + min;
        }
    }

    public class WaterWanderGoal extends RandomStrollGoal {

        public WaterWanderGoal(PathfinderMob p_i47301_1_) {
            super(p_i47301_1_, 1.0D);
        }

        public boolean canUse() {
            if (super.canUse()){
                return !SunkenSkeletonServant.this.isStaying() || SunkenSkeletonServant.this.getTrueOwner() == null;
            } else {
                return false;
            }
        }
    }
}
