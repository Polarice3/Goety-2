package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.entities.neutral.Minion;
import com.Polarice3.Goety.common.entities.projectiles.SoulBullet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class Irk extends Minion implements Enemy {
    private int shootTime;

    public Irk(EntityType<? extends Irk> p_i50190_1_, Level p_i50190_2_) {
        super(p_i50190_1_, p_i50190_2_);
        this.setHostile(true);
        this.shootTime = 0;
        this.navigation = this.createNavigation(p_i50190_2_);
    }

    public int xpReward(){
        return 3;
    }

    protected PathNavigation createNavigation(Level worldIn) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    public void tick() {
        super.tick();
        if (this.shootTime > 0){
            --this.shootTime;
        }
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new Irk.OutofBoundsGoal());
        this.goalSelector.addGoal(2, new Irk.FollowOwnerGoal(this, 0.5D, 6.0f, 3.0f, true));
        this.goalSelector.addGoal(4, new Irk.ChargeAttackGoal());
        this.goalSelector.addGoal(8, new Irk.MoveRandomGoal());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D);
    }

    public void die(DamageSource cause) {
        if (cause.getEntity() instanceof LivingEntity) {
            ((LivingEntity) cause.getEntity()).heal(2.0F);
        }
        super.die(cause);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.shootTime = compound.getInt("shootTime");

    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("shootTime", this.shootTime);

    }

    public boolean hurt(DamageSource source, float amount) {
        Entity entity = source.getEntity();
        if (entity instanceof Irk){
            return false;
        } else {
            return super.hurt(source, amount);
        }
    }

    public boolean isAlliedTo(Entity entityIn) {
        if (entityIn == this) {
            return true;
        } else if (super.isAlliedTo(entityIn)) {
            return true;
        } else if (entityIn instanceof Irk) {
            return this.getTeam() == null && entityIn.getTeam() == null;
        } else if (entityIn instanceof LivingEntity && ((LivingEntity)entityIn).getMobType() == MobType.ILLAGER) {
            return this.getTeam() == null && entityIn.getTeam() == null;
        } else {
            return false;
        }
    }

    private boolean getVexFlag(int mask) {
        int i = this.entityData.get(VEX_FLAGS);
        return (i & mask) != 0;
    }

    private void setVexFlag(int mask, boolean value) {
        int i = this.entityData.get(VEX_FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(VEX_FLAGS, (byte)(i & 255));
    }

    public boolean isCharging() {
        return this.getVexFlag(1);
    }

    public void setIsCharging(boolean charging) {
        this.setVexFlag(1, charging);
    }

    public void setLimitedLife(int limitedLifeTicksIn) {
        this.limitedLifespan = true;
        this.limitedLifeTicks = limitedLifeTicksIn;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.VEX_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.VEX_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.VEX_HURT;
    }
    
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.populateDefaultEquipmentSlots(pLevel.getRandom(), pDifficulty);
        this.populateDefaultEquipmentEnchantments(pLevel.getRandom(), pDifficulty);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    class OutofBoundsGoal extends Goal {
        public OutofBoundsGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return Irk.this.isInWall() && !Irk.this.getMoveControl().hasWanted();
        }

        public boolean canContinueToUse() {
            return Irk.this.isInWall() && !Irk.this.getMoveControl().hasWanted();
        }

        public void tick() {
            BlockPos.MutableBlockPos blockpos$mutable = Irk.this.blockPosition().mutable();
            blockpos$mutable.setY(Irk.this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
            Irk.this.getMoveControl().setWantedPosition(blockpos$mutable.getX(), blockpos$mutable.getY(), blockpos$mutable.getZ(), 1.0F);
        }

    }

    class ChargeAttackGoal extends Goal {
        public ChargeAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            if (Irk.this.getTarget() != null && !Irk.this.getMoveControl().hasWanted()) {
                return !Irk.this.getTarget().isAlliedTo(Irk.this);
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return Irk.this.getMoveControl().hasWanted()
                    && Irk.this.isCharging()
                    && Irk.this.getTarget() != null
                    && Irk.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingentity = Irk.this.getTarget();
            assert livingentity != null;
            Vec3 vector3d = livingentity.getEyePosition(1.0F);
            if (Irk.this.distanceTo(livingentity) > 4.0F) {
                Irk.this.getMoveControl().setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0F);
            }
        }

        public void stop() {
            Irk.this.setIsCharging(false);
        }

        public void tick() {
            LivingEntity livingentity = Irk.this.getTarget();
            if (livingentity != null) {
                if (Irk.this.shootTime == 10) {
                    double d1 = livingentity.getX() - Irk.this.getX();
                    double d2 = livingentity.getY(0.5D) - Irk.this.getY(0.5D);
                    double d3 = livingentity.getZ() - Irk.this.getZ();
                    SoulBullet smallFireballEntity = new SoulBullet(Irk.this.level, Irk.this, d1, d2, d3);
                    smallFireballEntity.setPos(smallFireballEntity.getX(), Irk.this.getY(0.5D), smallFireballEntity.getZ());
                    Irk.this.level.addFreshEntity(smallFireballEntity);
                    Irk.this.playSound(SoundEvents.VEX_CHARGE, 1.0F, 2.0F);
                }
                Irk.this.setIsCharging(Irk.this.shootTime <= 10);
                if (Irk.this.shootTime == 0){
                    Irk.this.shootTime = 20;
                } else {
                    Vec3 vector3d0 = livingentity.getEyePosition(1.0F);
                    Vec3 vector3d = Irk.this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D);
                    double d0 = vector3d.y;
                    if (Irk.this.getY() < vector3d0.y) {
                        d0 = Math.max(0.0D, d0);
                        d0 = d0 + (0.3D - d0 * (double)0.6F);
                    }

                    vector3d = new Vec3(vector3d.x, d0, vector3d.z);
                    Vec3 vector3d1 = new Vec3(vector3d0.x - Irk.this.getX(), 0.0D, vector3d0.z - Irk.this.getZ());
                    if (getHorizontalDistanceSqr(vector3d1) > 9.0D) {
                        Vec3 vector3d2 = vector3d1.normalize();
                        vector3d = vector3d.add(vector3d2.x * 0.3D - vector3d.x * 0.6D, 0.0D, vector3d2.z * 0.3D - vector3d.z * 0.6D);
                    }
                    Irk.this.setDeltaMovement(vector3d);
                }
                double d2 = Irk.this.getTarget().getX() - Irk.this.getX();
                double d1 = Irk.this.getTarget().getZ() - Irk.this.getZ();
                Irk.this.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float)Math.PI));
                Irk.this.yBodyRot = Irk.this.getYRot();
            } else {
                Irk.this.setIsCharging(false);
            }
        }
    }

    public static double getHorizontalDistanceSqr(Vec3 pVector) {
        return pVector.x * pVector.x + pVector.z * pVector.z;
    }

    class MoveRandomGoal extends Goal {
        public MoveRandomGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return !Irk.this.getMoveControl().hasWanted()
                    && Irk.this.random.nextInt(7) == 0
                    && !Irk.this.isCharging();
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = Irk.this.blockPosition();

            for(int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(Irk.this.random.nextInt(8) - 4, Irk.this.random.nextInt(6) - 2, Irk.this.random.nextInt(8) - 4);
                if (Irk.this.level.isEmptyBlock(blockpos1)) {
                    Irk.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
                    if (Irk.this.getTarget() == null) {
                        Irk.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }

    static class FollowOwnerGoal extends Goal {
        private final Irk summonedEntity;
        private LivingEntity owner;
        private final LevelReader level;
        private final double followSpeed;
        private final PathNavigation navigation;
        private int timeToRecalcPath;
        private final float maxDist;
        private final float minDist;
        private float oldWaterCost;
        private final boolean teleportToLeaves;

        public FollowOwnerGoal(Irk summonedEntity, double speed, float minDist, float maxDist, boolean teleportToLeaves) {
            this.summonedEntity = summonedEntity;
            this.level = summonedEntity.level;
            this.followSpeed = speed;
            this.navigation = summonedEntity.getNavigation();
            this.minDist = minDist;
            this.maxDist = maxDist;
            this.teleportToLeaves = teleportToLeaves;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            if (!(summonedEntity.getNavigation() instanceof GroundPathNavigation) && !(summonedEntity.getNavigation() instanceof FlyingPathNavigation)) {
                throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
            }
        }

        public boolean canUse() {
            LivingEntity livingentity = this.summonedEntity.getTrueOwner();
            if (livingentity == null) {
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } else if (this.summonedEntity.distanceToSqr(livingentity) < (double)(this.minDist * this.minDist)) {
                return false;
            } else if (this.summonedEntity.getTarget() != null) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (this.summonedEntity.getTarget() != null) {
                return false;
            } else if (this.navigation.isDone()){
                return false;
            } else {
                return !(this.summonedEntity.distanceToSqr(this.owner) <= (double)(this.maxDist * this.maxDist));
            }
        }

        public void start() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.summonedEntity.getPathfindingMalus(BlockPathTypes.WATER);
            this.summonedEntity.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        }

        public void stop() {
            this.navigation.stop();
            this.summonedEntity.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        }

        public void tick() {
            this.summonedEntity.getLookControl().setLookAt(this.owner, 10.0F, (float)this.summonedEntity.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                if (this.summonedEntity.distanceTo(this.owner) > 8.0D) {
                    double x = Mth.floor(this.owner.getX()) - 2;
                    double y = Mth.floor(this.owner.getBoundingBox().minY);
                    double z = Mth.floor(this.owner.getZ()) - 2;
                    for(int l = 0; l <= 4; ++l) {
                        for(int i1 = 0; i1 <= 4; ++i1) {
                            if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.ValidPosition(new BlockPos(x + l, y + 2, z + i1))){
                                float a = (float) ((x + l) + 0.5F);
                                float b = (float) ((z + i1) + 0.5F);
                                this.summonedEntity.getMoveControl().setWantedPosition(a, y, b, this.followSpeed);
                                this.navigation.stop();
                            }
                        }
                    }
                }
                if (this.summonedEntity.distanceToSqr(this.owner) > 144.0 && SpellConfig.VexTeleport.get()){
                    this.tryToTeleportNearEntity();
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
                if (!this.teleportToLeaves && blockstate.getBlock() instanceof LeavesBlock) {
                    return false;
                } else {
                    BlockPos blockpos = pos.subtract(this.summonedEntity.blockPosition());
                    return this.level.noCollision(this.summonedEntity, this.summonedEntity.getBoundingBox().move(blockpos));
                }
            }
        }

        protected boolean ValidPosition(BlockPos pos) {
            BlockState blockstate = this.level.getBlockState(pos);
            return (blockstate.canSurvive(this.level, pos) && this.level.isEmptyBlock(pos.above()) && this.level.isEmptyBlock(pos.above(2)));
        }

        private int getRandomNumber(int min, int max) {
            return this.summonedEntity.getRandom().nextInt(max - min + 1) + min;
        }
    }

}
