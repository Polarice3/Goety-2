package com.Polarice3.Goety.common.entities.hostile.illagers;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.FollowMobClassGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class Ripper extends PatrollingMonster {
    private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(Ripper.class, EntityDataSerializers.INT);
    private boolean isWet;
    private boolean isShaking;
    private float shakeAnim;
    private float shakeAnimO;
    private int bitingTick;

    public Ripper(EntityType<? extends PatrollingMonster> p_37839_, Level p_37840_) {
        super(p_37839_, p_37840_);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, -1.0F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, new FollowMobClassGoal(this, 1.0F, 4.0F,32.0F, (p_25278_) -> {
            return p_25278_ instanceof AbstractIllager && !(p_25278_ instanceof Tormentor) && this.getTarget() == null && this.random.nextFloat() <= 0.05F;
        }));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MAX_HEALTH, 16.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0D)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_SIZE, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_37870_) {
        super.addAdditionalSaveData(p_37870_);
        p_37870_.putInt("BitingTick", this.bitingTick);
        p_37870_.putInt("Size", this.getRipperSize());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_37862_) {
        super.readAdditionalSaveData(p_37862_);
        this.bitingTick = p_37862_.getInt("BitingTick");
        this.setRipperSize(p_37862_.getInt("Size"));
    }

    public EntityDimensions getDimensions(Pose p_33113_) {
        float i = this.getRipperSize();
        EntityDimensions entitydimensions = super.getDimensions(p_33113_);
        float f = (entitydimensions.width + (0.2F * i)) / entitydimensions.width;
        return entitydimensions.scale(f);
    }

    public void setRipperSize(int p_33109_) {
        this.entityData.set(ID_SIZE, Mth.clamp(p_33109_, -16, 64));
    }

    private void updateRipperSizeInfo() {
        this.refreshDimensions();
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack != null) {
            if (this.getRipperSize() < -1){
                attack.setBaseValue(0.5F);
            } else {
                attack.setBaseValue(2 + this.getRipperSize());
            }
        }
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) {
            if (this.getRipperSize() < 0){
                health.setBaseValue(16 + (this.getRipperSize() * 4));
            } else {
                health.setBaseValue(16 + (this.getRipperSize() * 2));
            }
        }
    }

    public int getRipperSize() {
        return this.entityData.get(ID_SIZE);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33134_) {
        if (ID_SIZE.equals(p_33134_)) {
            this.updateRipperSizeInfo();
        }

        super.onSyncedDataUpdated(p_33134_);
    }

    protected void playStepSound(BlockPos p_30415_, BlockState p_30416_) {
        this.playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F);
    }

    protected SoundEvent getAmbientSound() {
        if (this.getTarget() != null) {
            return SoundEvents.WOLF_GROWL;
        } else {
            return SoundEvents.WOLF_AMBIENT;
        }
    }

    protected SoundEvent getHurtSound(DamageSource p_30424_) {
        return SoundEvents.WOLF_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.WOLF_HURT;
    }

    protected float getSoundVolume() {
        return 0.4F;
    }

    public float getVoicePitch() {
        return super.getVoicePitch() - 0.25F;
    }

    public void aiStep() {
        super.aiStep();
        if (this.isAlive()){
            if (this.bitingTick > 0) {
                --this.bitingTick;
            }
        }
        if (!this.level.isClientSide && this.isWet && !this.isShaking && !this.isPathFinding() && this.onGround()) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
            this.level.broadcastEntityEvent(this, (byte)8);
        }
    }

    public void tick() {
        super.tick();
        if (this.isAlive()) {
            if (this.isInWaterRainOrBubble()) {
                this.isWet = true;
                if (this.isShaking && !this.level.isClientSide) {
                    this.level.broadcastEntityEvent(this, (byte)56);
                    this.cancelShake();
                }
            } else if ((this.isWet || this.isShaking) && this.isShaking) {
                if (this.shakeAnim == 0.0F) {
                    this.playSound(SoundEvents.WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    this.gameEvent(GameEvent.ENTITY_SHAKE);
                }

                this.shakeAnimO = this.shakeAnim;
                this.shakeAnim += 0.05F;
                if (this.shakeAnimO >= 2.0F) {
                    this.isWet = false;
                    this.isShaking = false;
                    this.shakeAnimO = 0.0F;
                    this.shakeAnim = 0.0F;
                }

                if (this.shakeAnim > 0.4F) {
                    float f = (float)this.getY();
                    int i = (int)(Mth.sin((this.shakeAnim - 0.4F) * (float)Math.PI) * 7.0F);
                    Vec3 vec3 = this.getDeltaMovement();

                    for(int j = 0; j < i; ++j) {
                        float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                        float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                        this.level.addParticle(ParticleTypes.SPLASH, this.getX() + (double)f1, (double)(f + 0.8F), this.getZ() + (double)f2, vec3.x, vec3.y, vec3.z);
                    }
                }
            }

        }
    }

    private void cancelShake() {
        this.isShaking = false;
        this.shakeAnim = 0.0F;
        this.shakeAnimO = 0.0F;
    }

    public void die(DamageSource p_30384_) {
        this.isWet = false;
        this.isShaking = false;
        this.shakeAnimO = 0.0F;
        this.shakeAnim = 0.0F;
        super.die(p_30384_);
    }

    public int getBitingTick() {
        return this.bitingTick;
    }

    public int attackTotalTick(){
        return 10;
    }

    public boolean doHurtTarget(Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            this.bitingTick = attackTotalTick();
            this.level.broadcastEntityEvent(this, (byte)4);
            this.playSound(SoundEvents.FOX_BITE, this.getSoundVolume(), this.getVoicePitch());
            float f = this.level.getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
            if (this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
                entityIn.setSecondsOnFire(2 * (int)f);
            }
        }

        return flag;
    }

    public boolean isWet() {
        return this.isWet;
    }

    public float getWetShade(float p_30447_) {
        return Math.min(0.5F + Mth.lerp(p_30447_, this.shakeAnimO, this.shakeAnim) / 2.0F * 0.5F, 1.0F);
    }

    public float getBodyRollAngle(float p_30433_, float p_30434_) {
        float f = (Mth.lerp(p_30433_, this.shakeAnimO, this.shakeAnim) + p_30434_) / 1.8F;
        if (f < 0.0F) {
            f = 0.0F;
        } else if (f > 1.0F) {
            f = 1.0F;
        }

        return Mth.sin(f * (float)Math.PI) * Mth.sin(f * (float)Math.PI * 11.0F) * 0.15F * (float)Math.PI;
    }

    public float getTailAngle() {
        return 1.5393804F;
    }

    protected float getStandingEyeHeight(Pose p_30409_, EntityDimensions p_30410_) {
        return p_30410_.height * 0.8F;
    }

    public void handleEntityEvent(byte p_30379_) {
        if (p_30379_ == 4) {
            this.bitingTick = attackTotalTick();
            this.playSound(SoundEvents.FOX_BITE, this.getSoundVolume(), this.getVoicePitch() * 2.0F);
        } else if (p_30379_ == 8) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
        } else if (p_30379_ == 56) {
            this.cancelShake();
        } else {
            super.handleEntityEvent(p_30379_);
        }

    }

    public boolean isAlliedTo(Entity pEntity) {
        if (super.isAlliedTo(pEntity)) {
            return true;
        } else if (pEntity instanceof LivingEntity && ((LivingEntity)pEntity).getMobType() == MobType.ILLAGER) {
            return this.getTeam() == null && pEntity.getTeam() == null;
        } else {
            return false;
        }
    }

    public boolean hurt(DamageSource p_34288_, float p_34289_) {
        if (!super.hurt(p_34288_, p_34289_)) {
            return false;
        } else if (!(this.level instanceof ServerLevel serverlevel)) {
            return false;
        } else {
            LivingEntity livingentity = this.getTarget();
            if (livingentity == null && p_34288_.getEntity() instanceof LivingEntity) {
                livingentity = (LivingEntity)p_34288_.getEntity();
            }

            AttributeInstance spawnChance = this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
            int i = Mth.floor(this.getX());
            int j = Mth.floor(this.getY());
            int k = Mth.floor(this.getZ());
            if (spawnChance != null) {
                if (livingentity != null
                        && this.random.nextDouble() < spawnChance.getValue()
                        && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                    Ripper ripper = ModEntityType.RIPPER.get().create(this.level);

                    if (ripper != null) {
                        for (int l = 0; l < 50; ++l) {
                            int i1 = i + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                            int j1 = j + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                            int k1 = k + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                            BlockPos blockpos = new BlockPos(i1, j1, k1);
                            EntityType<?> entitytype = ripper.getType();
                            SpawnPlacements.Type spawnplacements$type = SpawnPlacements.getPlacementType(entitytype);
                            if (NaturalSpawner.isSpawnPositionOk(spawnplacements$type, this.level, blockpos, entitytype) && SpawnPlacements.checkSpawnRules(entitytype, serverlevel, MobSpawnType.REINFORCEMENT, blockpos, this.level.random)) {
                                ripper.setPos((double) i1, (double) j1, (double) k1);
                                if (!this.level.hasNearbyAlivePlayer((double) i1, (double) j1, (double) k1, 7.0D) && this.level.isUnobstructed(ripper) && this.level.noCollision(ripper) && !this.level.containsAnyLiquid(ripper.getBoundingBox())) {
                                    ripper.setTarget(livingentity);
                                    ripper.finalizeSpawn(serverlevel, this.level.getCurrentDifficultyAt(ripper.blockPosition()), MobSpawnType.REINFORCEMENT, (SpawnGroupData) null, (CompoundTag) null);
                                    serverlevel.addFreshEntityWithPassengers(ripper);
                                    spawnChance.addPermanentModifier(new AttributeModifier("Caller charge", (double) -0.05F, AttributeModifier.Operation.ADDITION));
                                    AttributeInstance spawnChance2 = ripper.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
                                    if (spawnChance2 != null) {
                                        spawnChance2.addPermanentModifier(new AttributeModifier("Callee charge", (double) -0.05F, AttributeModifier.Operation.ADDITION));
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            return true;
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34297_, DifficultyInstance p_34298_, MobSpawnType p_34299_, @Nullable SpawnGroupData p_34300_, @Nullable CompoundTag p_34301_) {
        p_34300_ = super.finalizeSpawn(p_34297_, p_34298_, p_34299_, p_34300_, p_34301_);
        float f = p_34298_.getSpecialMultiplier();
        this.handleAttributes(f);
        return p_34300_;
    }

    protected void handleAttributes(float p_34340_) {
        this.randomizeReinforcementsChance();

        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance knockResist = this.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        AttributeInstance spawnChance = this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
        if (speed != null){
            speed.addPermanentModifier(new AttributeModifier("Random spawn speed bonus", this.random.nextDouble() * 0.05D, AttributeModifier.Operation.ADDITION));
        }
        if (knockResist != null){
            knockResist.addPermanentModifier(new AttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05D, AttributeModifier.Operation.ADDITION));
        }

        if (this.random.nextFloat() < p_34340_ * 0.05F) {
            if (spawnChance != null) {
                spawnChance.addPermanentModifier(new AttributeModifier("Leader Ripper bonus", this.random.nextDouble() * 0.25D + 0.5D, AttributeModifier.Operation.ADDITION));
            }
            this.setRipperSize(2);
        } else {
            this.setRipperSize(this.random.nextIntBetweenInclusive(-1, 1));
        }
    }

    protected void randomizeReinforcementsChance() {
        AttributeInstance spawnChance = this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
        if (spawnChance != null) {
            spawnChance.setBaseValue(this.random.nextDouble() * 0.1D);
        }
    }

    @Override
    public boolean canBeLeader() {
        return false;
    }
}
