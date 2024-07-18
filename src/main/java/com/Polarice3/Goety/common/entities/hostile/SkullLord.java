package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.entities.PithosBlockEntity;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.projectiles.HauntedSkullProjectile;
import com.Polarice3.Goety.common.magic.spells.ShockwaveSpell;
import com.Polarice3.Goety.common.magic.spells.storm.ElectroOrbSpell;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SkullLord extends Monster implements ICustomAttributes {
    protected static final EntityDataAccessor<Byte> FLAGS = SynchedEntityData.defineId(SkullLord.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Optional<UUID>> BONE_LORD = SynchedEntityData.defineId(SkullLord.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> BONE_LORD_CLIENT_ID = SynchedEntityData.defineId(SkullLord.class, EntityDataSerializers.INT);
    private final ModServerBossInfo bossInfo;
    @Nullable
    private BlockPos boundOrigin;
    private int spawnDelay = 100;
    private int spawnNumber = 0;
    private int chargeTime = 0;
    private int shockWaveCool = 0;
    private int oldSwell;
    private int swell;
    public float explosionRadius = 2.0F;
    public int boneLordRegen;
    private int hitTimes;
    private int stuckTime = 0;

    public SkullLord(EntityType<? extends SkullLord> p_i50190_1_, Level p_i50190_2_) {
        super(p_i50190_1_, p_i50190_2_);
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.PURPLE, false, false);
        this.navigation = this.createNavigation(p_i50190_2_);
        this.moveControl = new MoveHelperController(this);
        this.hitTimes = 0;
        this.xpReward = 70;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new SoulSkullGoal());
        this.goalSelector.addGoal(4, new ChargeAttackGoal());
        this.goalSelector.addGoal(4, new ElectroOrbAttackGoal());
        this.goalSelector.addGoal(4, new ShockwaveAttackGoal());
        this.goalSelector.addGoal(8, new MoveRandomGoal());
        this.goalSelector.addGoal(7, new LookAroundGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SkullLordDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.SkullLordHealth.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.SkullLordHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.SkullLordDamage.get());
    }

    public void move(MoverType typeIn, Vec3 pos) {
        super.move(typeIn, pos);
        this.checkInsideBlocks();
    }

    protected PathNavigation createNavigation(Level worldIn) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanPassDoors(false);
        return flyingpathnavigator;
    }

    public BlockEntity getPithos(){
        if (this.getBoundOrigin() != null){
            return this.level.getBlockEntity(this.getBoundOrigin());
        }
        return null;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isInvulnerable()){
            return false;
        } else {
            ++this.hitTimes;
            if (this.hitTimes > 3 || pAmount >= 20){
                pAmount /= 2;
            }
            return super.hurt(pSource, pAmount);
        }
    }

    public void checkDespawn() {
        this.setIsDespawn(true);
        super.checkDespawn();
    }

    @Override
    public boolean fireImmune() {
        return this.isInvulnerable();
    }

    protected boolean isAffectedByFluids() {
        return false;
    }

    static class MoveHelperController extends MoveControl {
        private final SkullLord skullLord;
        private int floatDuration;

        public MoveHelperController(SkullLord p_i45838_1_) {
            super(p_i45838_1_);
            this.skullLord = p_i45838_1_;
        }

        public void tick() {
            if (this.operation == Operation.MOVE_TO) {
                if (this.floatDuration-- <= 0) {
                    this.floatDuration += this.skullLord.getRandom().nextInt(5) + 2;
                    Vec3 vector3d = new Vec3(this.wantedX - this.skullLord.getX(), this.wantedY - this.skullLord.getY(), this.wantedZ - this.skullLord.getZ());
                    double d0 = vector3d.length();
                    vector3d = vector3d.normalize();
                    if (this.canReach(vector3d, Mth.ceil(d0))) {
                        this.skullLord.setDeltaMovement(this.skullLord.getDeltaMovement().add(vector3d.scale(0.1D)));
                    } else {
                        this.operation = Operation.WAIT;
                    }
                }

            }
        }

        private boolean canReach(Vec3 p_220673_1_, int p_220673_2_) {
            AABB axisalignedbb = this.skullLord.getBoundingBox();

            for(int i = 1; i < p_220673_2_; ++i) {
                axisalignedbb = axisalignedbb.move(p_220673_1_);
                if (!this.skullLord.level.noCollision(this.skullLord, axisalignedbb)) {
                    return false;
                }
            }

            return true;
        }
    }

    public void tick() {
        super.tick();
        if (this.tickCount % 5 == 0) {
            this.bossInfo.update();
        }
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        this.setNoGravity(true);
        int delay = switch (this.level.getDifficulty()) {
            case NORMAL -> 300;
            case HARD -> 150;
            default -> 400;
        };
        Vec3 vector3d = this.getDeltaMovement();
        double d0 = this.getX() + vector3d.x;
        double d1 = this.getY() + vector3d.y;
        double d2 = this.getZ() + vector3d.z;
        ParticleOptions particleData = ParticleTypes.SMOKE;
        if (this.isSpawning()){
            particleData = ParticleTypes.FLAME;
        } else if (this.isInvulnerable()){
            particleData = ParticleTypes.POOF;
        } else if (this.isShockWave()){
            particleData = ParticleTypes.SOUL_FIRE_FLAME;
        } else if (this.isElectroOrb()){
            particleData = ModParticleTypes.ELECTRIC.get();
        }
        this.level.addParticle(particleData, d0 + this.random.nextGaussian() * (double)0.3F, d1 + this.random.nextGaussian() * (double)0.3F, d2 + this.random.nextGaussian() * (double)0.3F, 0.0D, 0.0D, 0.0D);
        if (this.isInvulnerable()){
            int healFreq = switch (this.level.getDifficulty()) {
                case NORMAL -> 40;
                case HARD -> 20;
                default -> 60;
            };
            if (this.tickCount % healFreq == 0){
                this.heal(1.0F);
            }
        }
        if (this.isOnFire()){
            if (this.tickCount % 100 == 0 || this.isInvulnerable()){
                this.clearFire();
            }
        }
        if (this.getPithos() != null){
            BlockPos blockPos = this.getPithos().getBlockPos();
            if (this.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ()) > 1024){
                this.moveTo(blockPos, 0, 0);
                if (this.getBoneLord() != null){
                    this.getBoneLord().moveTo(blockPos, 0, 0);
                }
            }
        }
        if (this.getTarget() != null) {
            if (this.getTarget().isDeadOrDying() || this.getTarget().isRemoved()){
                this.setTarget(null);
            }
        } else {
            for (Player player : this.level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(4), EntitySelector.NO_CREATIVE_OR_SPECTATOR)){
                this.setTarget(player);
            }
            if (this.getPithos() != null) {
                if (this.getPithos() instanceof PithosBlockEntity pithosTile) {
                    if (this.tickCount % 100 == 0 && this.level.isLoaded(pithosTile.getBlockPos())) {
                        this.setIsDespawn(true);
                        pithosTile.lock();
                        if (this.getBoneLord() != null) {
                            this.getBoneLord().discard();
                        }
                        this.discard();
                    }
                }
            }
        }
        if (this.isShockWave()){
            this.oldSwell = this.swell;
            this.swell += 1;
        } else {
            this.oldSwell = 0;
            this.swell = 0;
        }
        if (!this.level.isClientSide){
            ServerLevel serverWorld = (ServerLevel) this.level;
            int i = this.blockPosition().getX();
            int j = this.blockPosition().getY();
            int k = this.blockPosition().getZ();
            List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, (new AABB(i, j, k, i, j - 4, k)).inflate(8.0D, 8.0D, 8.0D), (t -> t instanceof IOwned owned && owned.getTrueOwner() == this));
            if (list.size() < 8 && this.getTarget() != null && (this.isNotAbility() || this.isSpawning())){
                int warn = this.isHalfHealth() ? 20 : 40;
                if (this.spawnDelay > 0) {
                    --this.spawnDelay;
                    if (this.spawnDelay == warn){
                        this.playSound(ModSounds.PREPARE_SUMMON.get());
                    }
                    if (this.spawnDelay <= warn){
                        this.setDeltaMovement(Vec3.ZERO);
                        this.setSpawning(true);
                    } else {
                        this.setSpawning(false);
                    }
                } else {
                    this.spawnDelay = this.level.random.nextInt(delay) + delay;
                    this.setSpawning(false);
                    this.spawnMobs();
                }
            }
            if (this.getBoneLord() == null || (this.getBoneLord() != null && this.getBoneLord().isDeadOrDying())){
                --this.boneLordRegen;
                this.setIsInvulnerable(false);
                this.level.broadcastEntityEvent(this, (byte) 5);
                for (BoneLord boneLord : this.level.getEntitiesOfClass(BoneLord.class, this.getBoundingBox().inflate(32))){
                    if (boneLord.getSkullLord() == this){
                        this.setBoneLord(boneLord);
                    }
                }
                if (this.boneLordRegen <= 0 && this.isNotAbility()){
                    BoneLord boneLord = ModEntityType.BONE_LORD.get().create(this.level);
                    if (boneLord != null){
                        boneLord.finalizeSpawn(serverWorld, this.level.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                        boneLord.setPos(this.getX(), this.getY(), this.getZ());
                        boneLord.setSkullLord(this);
                        this.setBoneLord(boneLord);
                        this.level.addFreshEntity(boneLord);
                    }
                }
            } else {
                if (this.getBoneLord() != null){
                    this.drawAttachParticleBeam(this, this.getBoneLord());
                    if (this.distanceToSqr(this.getBoneLord()) > Mth.square(16)) {
                        this.moveTo(this.getBoneLord().position());
                    }
                    this.setIsInvulnerable(true);
                    this.level.broadcastEntityEvent(this, (byte) 4);
                    this.hitTimes = 0;
                    this.boneLordRegen = delay * 2;
                }
            }
            if (this.isCharging()){
                ++this.chargeTime;
                for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0F))){
                    if (!(livingEntity instanceof BoneLord) && livingEntity != this && livingEntity != this.getTarget()) {
                        if (this.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
                            this.level.explode(this, this.getX(), this.getY(), this.getZ(), explosionRadius, Explosion.BlockInteraction.NONE);
                            if (this.random.nextFloat() < 0.25F){
                                this.setIsCharging(false);
                            }
                        }
                    }
                }
                if (this.horizontalCollision || this.verticalCollision){
                    this.level.explode(this, this.getX(), this.getY(), this.getZ(), explosionRadius, Explosion.BlockInteraction.NONE);
                    this.setIsCharging(false);
                }
                if (this.chargeTime >= 100){
                    this.setIsCharging(false);
                }
            } else {
                this.chargeTime = 0;
            }
            if (this.shockWaveCool > 0){
                --this.shockWaveCool;
            }
        }
    }

    public float getSwelling(float p_32321_) {
        return Mth.lerp(p_32321_, (float)this.oldSwell, (float)this.swell) / 28.0F;
    }

    private void drawAttachParticleBeam(LivingEntity pSource, LivingEntity pTarget) {
        double d0 = pTarget.getX() - pSource.getX();
        double d1 = pTarget.getEyeY() - pSource.getY();
        double d2 = pTarget.getZ() - pSource.getZ();
        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        d0 = d0 / d3;
        d1 = d1 / d3;
        d2 = d2 / d3;
        double d4 = pSource.level.random.nextDouble();
        if (!pSource.level.isClientSide) {
            ServerLevel serverWorld = (ServerLevel) pSource.level;
            while (d4 < d3) {
                d4 += 1.0D;
                serverWorld.sendParticles(ModParticleTypes.BONE.get(), pSource.getX() + d0 * d4, pSource.getY() + d1 * d4, pSource.getZ() + d2 * d4, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public void spawnMobs(){
        int spawnRange = 2;
        boolean random = this.level.random.nextBoolean();
        if (this.level instanceof ServerLevel serverLevel) {
            double d0 = (double) this.blockPosition().getX() + this.level.random.nextDouble();
            double d1 = (double) this.blockPosition().getY() + this.level.random.nextDouble();
            double d2 = (double) this.blockPosition().getZ() + this.level.random.nextDouble();
            for (int p = 0; p < 4; ++p) {
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, d0, d1, d2, 1, 0, 0, 0, 0);
                serverLevel.sendParticles(ParticleTypes.SMOKE, d0, d1, d2, 1, 0.0D, 5.0E-4D, 0.0D, 5.0E-4D);
            }
            SoundUtil.playNecromancerSummon(this);
            for (int i = 0; i < 2 + (serverLevel.random.nextInt(2) * serverLevel.random.nextInt(1)); ++i) {
                double d3 = (double) this.blockPosition().getX() + (serverLevel.random.nextDouble() - serverLevel.random.nextDouble()) * (double) spawnRange + 0.5D;
                double d4 = (double) (this.blockPosition().getY() + serverLevel.random.nextInt(3));
                double d5 = (double) this.blockPosition().getZ() + (serverLevel.random.nextDouble() - serverLevel.random.nextDouble()) * (double) spawnRange + 0.5D;
                Summoned summoned;
                if (this.isUnderWater()) {
                    if (random) {
                        summoned = ModEntityType.DROWNED_SERVANT.get().create(serverLevel);
                    } else {
                        summoned = ModEntityType.SUNKEN_SKELETON_SERVANT.get().create(serverLevel);
                    }
                } else {
                    if (random) {
                        if (serverLevel.random.nextFloat() <= 0.8F){
                            summoned = ModEntityType.FROZEN_ZOMBIE_SERVANT.get().create(serverLevel);
                        } else {
                            summoned = ModEntityType.ZOMBIE_SERVANT.get().create(serverLevel);
                        }
                    } else {
                        if (serverLevel.random.nextFloat() <= 0.8F){
                            summoned = ModEntityType.STRAY_SERVANT.get().create(serverLevel);
                        } else {
                            summoned = ModEntityType.SKELETON_SERVANT.get().create(serverLevel);
                        }
                    }
                    if (serverLevel.random.nextFloat() <= 0.15F){
                        summoned = ModEntityType.BORDER_WRAITH_SERVANT.get().create(serverLevel);
                    }
                }
                if (summoned != null) {
                    BlockPos blockPos = new BlockPos(d3, d4, d5);
                    summoned.setTrueOwner(this);
                    summoned.setUpgraded(true);
                    summoned.moveTo(BlockFinder.SummonPosition(summoned, blockPos), this.getYRot(), this.getXRot());
                    summoned.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(summoned.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    serverLevel.addFreshEntityWithPassengers(summoned);
                }
            }
        }
    }

    public void die(DamageSource cause) {
        super.die(cause);
        for(int i = 0; i < this.level.random.nextInt(35) + 10; ++i) {
            float f11 = (this.random.nextFloat() - 0.5F);
            float f13 = (this.random.nextFloat() - 0.5F);
            float f14 = (this.random.nextFloat() - 0.5F);
            this.level.addParticle(ParticleTypes.EXPLOSION, this.getX() + (double)f11, this.getY() + 2.0D + (double)f13, this.getZ() + (double)f14, 0.0D, 0.0D, 0.0D);
        }
        if (this.getBoneLord() != null){
            this.getBoneLord().die(cause);
        }
        if (this.getPithos() != null){
            if (this.getPithos() instanceof PithosBlockEntity pithosTile){
                pithosTile.unlock();
            }
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if (this.getBoneLord() != null){
            this.getBoneLord().discard();
        }
        if (!this.isDespawn()) {
            if (this.getPithos() != null) {
                if (this.getPithos() instanceof PithosBlockEntity pithosTile) {
                    pithosTile.unlock();
                }
            }
        }
    }

    public void makeStuckInBlock(BlockState pState, Vec3 pMotionMultiplier) {
        if (!pState.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(pState, pMotionMultiplier);
        }

    }

    public boolean causeFallDamage(float p_146828_, float p_146829_, DamageSource p_146830_) {
        return false;
    }

    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.SKULL_LORD_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.SKULL_LORD_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.SKULL_LORD_DEATH.get();
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public boolean isAlliedTo(Entity entityIn) {
        if (super.isAlliedTo(entityIn)) {
            return true;
        } else if (entityIn instanceof Monster monster && monster.getMobType() == MobType.UNDEAD) {
            return this.getTeam() == null && entityIn.getTeam() == null;
        } else {
            return false;
        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLAGS, (byte)0);
        this.entityData.define(BONE_LORD, Optional.empty());
        this.entityData.define(BONE_LORD_CLIENT_ID, -1);
    }

    private boolean geFlags(int mask) {
        int i = this.entityData.get(FLAGS);
        return (i & mask) != 0;
    }

    private void setFlags(int mask, boolean value) {
        int i = this.entityData.get(FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(FLAGS, (byte)(i & 255));
    }

    @Nullable
    public BlockPos getBoundOrigin() {
        return this.boundOrigin;
    }

    public void setBoundOrigin(@Nullable BlockPos pBoundOrigin) {
        this.boundOrigin = pBoundOrigin;
    }

    @Nullable
    public BoneLord getBoneLord() {
        if (!this.level.isClientSide){
            UUID uuid = this.getBoneLordUUID();
            return EntityFinder.getLivingEntityByUuiD(uuid) instanceof BoneLord boneLord ? boneLord : null;
        } else {
            int id = this.getBoneLordClientId();
            return id <= -1 ? null : this.level.getEntity(id) instanceof BoneLord boneLord ? boneLord : null;
        }
    }

    @Nullable
    public UUID getBoneLordUUID() {
        return this.entityData.get(BONE_LORD).orElse(null);
    }

    public void setBoneLordUUID(UUID uuid){
        this.entityData.set(BONE_LORD, Optional.ofNullable(uuid));
    }

    public int getBoneLordClientId(){
        return this.entityData.get(BONE_LORD_CLIENT_ID);
    }

    public void setBoneLordClientId(int id){
        this.entityData.set(BONE_LORD_CLIENT_ID, id);
    }

    public void setBoneLord(BoneLord boneLord){
        this.setBoneLordUUID(boneLord.getUUID());
        this.setBoneLordClientId(boneLord.getId());
    }

    public boolean isCharging() {
        return this.geFlags(1);
    }

    public void setIsCharging(boolean charging) {
        this.setFlags(1, charging);
    }

    public boolean isInvulnerable() {
        return this.geFlags(2);
    }

    public void setIsInvulnerable(boolean invulnerable) {
        this.setFlags(2, invulnerable);
    }

    public boolean isDespawn() {
        return this.geFlags(4);
    }

    public void setIsDespawn(boolean despawn) {
        this.setFlags(4, despawn);
    }

    public boolean isElectroOrb() {
        return this.geFlags(8);
    }

    public void setElectroOrb(boolean electroOrb) {
        this.setFlags(8, electroOrb);
    }

    public boolean isSpawning() {
        return this.geFlags(16);
    }

    public void setSpawning(boolean spawning) {
        this.setFlags(16, spawning);
    }

    public boolean isShockWave() {
        return this.geFlags(32);
    }

    public void setShockWave(boolean shockWave) {
        this.setFlags(32, shockWave);
    }

    public boolean isNotAbility(){
        return !this.isCharging() && !this.isElectroOrb() && !this.isSpawning() && !this.isShockWave();
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("BoundX")) {
            this.boundOrigin = new BlockPos(pCompound.getInt("BoundX"), pCompound.getInt("BoundY"), pCompound.getInt("BoundZ"));
        }
        UUID uuid;
        if (pCompound.hasUUID("boneLord")) {
            uuid = pCompound.getUUID("boneLord");
        } else {
            String s = pCompound.getString("boneLord");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.setBoneLordUUID(uuid);
            } catch (Throwable ignored) {
            }
        }

        if (pCompound.contains("BoneLordClient")){
            this.setBoneLordClientId(pCompound.getInt("BoneLordClient"));
        }
        this.hitTimes = pCompound.getInt("hitTimes");
        this.boneLordRegen = pCompound.getInt("boneLordRegen");
        this.spawnDelay = pCompound.getInt("spawnDelay");
        this.spawnNumber = pCompound.getInt("spawnNumber");
        this.shockWaveCool = pCompound.getInt("shockWaveCool");
        this.stuckTime = pCompound.getInt("stuckTime");
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
        this.setConfigurableAttributes();
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.boundOrigin != null) {
            pCompound.putInt("BoundX", this.boundOrigin.getX());
            pCompound.putInt("BoundY", this.boundOrigin.getY());
            pCompound.putInt("BoundZ", this.boundOrigin.getZ());
        }
        if (this.getBoneLordUUID() != null) {
            pCompound.putUUID("boneLord", this.getBoneLordUUID());
        }
        if (this.getBoneLordClientId() > -1) {
            pCompound.putInt("BoneLordClient", this.getBoneLordClientId());
        }
        pCompound.putInt("hitTimes", this.hitTimes);
        pCompound.putInt("boneLordRegen", this.boneLordRegen);
        pCompound.putInt("spawnDelay", this.spawnDelay);
        pCompound.putInt("spawnNumber", this.spawnNumber);
        pCompound.putInt("shockWaveCool", this.shockWaveCool);
        pCompound.putInt("stuckTime", this.stuckTime);
    }

    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setVisible(MainConfig.SpecialBossBar.get());
    }

    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        this.bossInfo.addPlayer(pPlayer);
    }

    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossInfo.removePlayer(pPlayer);
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    public boolean isHalfHealth(){
        return this.getHealth() <= this.getMaxHealth()/2;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.populateDefaultEquipmentSlots(pLevel.getRandom(), pDifficulty);
        this.populateDefaultEquipmentEnchantments(pLevel.getRandom(), pDifficulty);
        BoneLord boneLord = ModEntityType.BONE_LORD.get().create((Level) pLevel);
        if (boneLord != null){
            boneLord.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
            boneLord.setPos(this.getX(), this.getY(), this.getZ());
            boneLord.setSkullLord(this);
            this.setBoneLord(boneLord);
            pLevel.addFreshEntity(boneLord);
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4){
            this.setIsInvulnerable(true);
        } else if (p_21375_ == 5){
            this.setIsInvulnerable(false);
        } else if (p_21375_ == 6){
            this.setShockWave(true);
        } else if (p_21375_ == 7){
            this.setShockWave(false);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    class SoulSkullGoal extends Goal {
        public int shootTime;

        public boolean canUse() {
            if (SkullLord.this.getTarget() != null
                    && SkullLord.this.isNotAbility()
                    && SkullLord.this.hasLineOfSight(SkullLord.this.getTarget())
                    && SkullLord.this.isInvulnerable()) {
                return !MobUtil.areAllies(SkullLord.this.getTarget(),SkullLord.this);
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return SkullLord.this.getTarget() != null
                    && SkullLord.this.isNotAbility()
                    && SkullLord.this.hasLineOfSight(SkullLord.this.getTarget())
                    && SkullLord.this.getTarget().isAlive()
                    && SkullLord.this.getBoneLord() != null;
        }

        public void tick() {
            LivingEntity livingentity = SkullLord.this.getTarget();
            if (livingentity != null) {
                int shoot = 45;
                if (SkullLord.this.isHalfHealth()){
                    shoot = 30;
                }
                if (--this.shootTime <= 0) {
                    double d1 = livingentity.getX() - SkullLord.this.getX();
                    double d2 = livingentity.getY(0.5D) - SkullLord.this.getY(0.5D);
                    double d3 = livingentity.getZ() - SkullLord.this.getZ();
                    HauntedSkullProjectile soulSkull = new HauntedSkullProjectile(SkullLord.this, d1, d2, d3, SkullLord.this.level);
                    soulSkull.setPos(soulSkull.getX(), SkullLord.this.getY(0.75D), soulSkull.getZ());
                    soulSkull.setYRot(SkullLord.this.getYRot());
                    soulSkull.setXRot(SkullLord.this.getXRot());
                    this.shootTime = shoot;
                    SkullLord.this.level.addFreshEntity(soulSkull);
                    SkullLord.this.playSound(ModSounds.SKULL_LORD_SHOOT.get(), 1.0F, 1.0F);
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class ChargeAttackGoal extends Goal {
        private Vec3 chargePos;

        public ChargeAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            if (SkullLord.this.getTarget() != null
                    && SkullLord.this.isNotAbility()
                    && SkullLord.this.hasLineOfSight(SkullLord.this.getTarget())
                    && SkullLord.this.getBoneLord() == null
                    && SkullLord.this.distanceTo(SkullLord.this.getTarget()) <= 8) {
                if (!SkullLord.this.isHalfHealth()){
                    return SkullLord.this.random.nextInt(60) == 0;
                } else {
                    return SkullLord.this.random.nextInt(30) == 0;
                }
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return SkullLord.this.isCharging()
                    && SkullLord.this.getTarget() != null
                    && SkullLord.this.getBoneLord() == null
                    && SkullLord.this.distanceTo(SkullLord.this.getTarget()) <= 8
                    && SkullLord.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingentity = SkullLord.this.getTarget();
            if (livingentity != null) {
                Vec3 vector3d = livingentity.getEyePosition(1.0F);
                double dx = SkullLord.this.getX() - vector3d.x();
                double dy = SkullLord.this.getY() - vector3d.y();
                double dz = SkullLord.this.getZ() - vector3d.z();
                double d0 = Math.sqrt(dx * dx + dy * dy + dz * dz);
                double velocity = 2.0D;
                if (SkullLord.this.isHalfHealth()){
                    velocity = 2.5D;
                }
                double xPower = -(dx / d0 * velocity * 0.2D);
                double yPower = -(dy / d0 * velocity * 0.2D);
                double zPower = -(dz / d0 * velocity * 0.2D);
                this.chargePos = new Vec3(xPower, yPower, zPower);
                SkullLord.this.setIsCharging(true);
                SkullLord.this.playSound(ModSounds.SKULL_LORD_CHARGE.get(), 1.0F, 1.0F);
            }
        }

        public void stop() {
            SkullLord.this.setIsCharging(false);
            SkullLord.this.setDeltaMovement(Vec3.ZERO);
        }

        public void tick() {
            SkullLord skullLord = SkullLord.this;
            LivingEntity livingentity = SkullLord.this.getTarget();
            if (this.chargePos != null){
                SkullLord.this.setDeltaMovement(this.chargePos);
            }
            if (livingentity != null) {
                if (skullLord.getBoundingBox().intersects(livingentity.getBoundingBox())) {
                    if (livingentity.hurt(DamageSource.indirectMagic(skullLord, skullLord), (float) skullLord.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                        if (skullLord.isOnFire()) {
                            livingentity.setSecondsOnFire(5);
                        }
                        if (skullLord.isHalfHealth()){
                            livingentity.addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), 100));
                        }
                        skullLord.level.explode(skullLord, skullLord.getX(), skullLord.getY(), skullLord.getZ(), skullLord.explosionRadius, Explosion.BlockInteraction.NONE);
                        skullLord.setIsCharging(false);
                    }
                }
            }

        }
    }

    class ElectroOrbAttackGoal extends Goal {
        private int electroTime;

        public ElectroOrbAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            if (SkullLord.this.getTarget() != null
                    && SkullLord.this.isNotAbility()
                    && SkullLord.this.getBoneLord() == null
                    && SkullLord.this.hasLineOfSight(SkullLord.this.getTarget())) {
                if (!SkullLord.this.isHalfHealth()){
                    return SkullLord.this.random.nextInt(400) == 0;
                } else {
                    return SkullLord.this.random.nextInt(200) == 0;
                }
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return SkullLord.this.isElectroOrb()
                    && SkullLord.this.getTarget() != null
                    && SkullLord.this.getBoneLord() == null
                    && this.electroTime > 0
                    && SkullLord.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingentity = SkullLord.this.getTarget();
            if (livingentity != null) {
                int time = 100;
                if (SkullLord.this.isHalfHealth()){
                    time = 80;
                }
                this.electroTime = time;
                SkullLord.this.setElectroOrb(true);
                SkullLord.this.playSound(ModSounds.ZAP.get(), 1.0F, 1.0F);
            }
        }

        public void stop() {
            this.electroTime = 0;
            SkullLord.this.setElectroOrb(false);
            SkullLord.this.setDeltaMovement(Vec3.ZERO);
        }

        public void tick() {
            SkullLord skullLord = SkullLord.this;
            LivingEntity livingentity = SkullLord.this.getTarget();
            if (livingentity != null && skullLord.level instanceof ServerLevel serverLevel) {
                if (this.electroTime > 0){
                    --this.electroTime;
                    if (!skullLord.hasLineOfSight(livingentity)){
                        skullLord.setDeltaMovement(0.0D, 0.08D, 0.0D);
                    } else {
                        skullLord.setDeltaMovement(Vec3.ZERO);
                    }
                    double d1 = livingentity.getX() - skullLord.getX();
                    double d2 = livingentity.getZ() - skullLord.getZ();
                    skullLord.getLookControl().setLookAt(livingentity, 10.0F, skullLord.getMaxHeadXRot());
                    skullLord.setYRot(-((float)Mth.atan2(d1, d2)) * (180F / (float)Math.PI));
                    skullLord.yBodyRot = skullLord.getYRot();
                    if (this.electroTime > 60){
                        ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ModParticleTypes.BIG_ELECTRIC.get(), skullLord);
                    }
                    if (this.electroTime <= 60 && this.electroTime % 10 == 0){
                        new ElectroOrbSpell().SpellResult(serverLevel, skullLord, ItemStack.EMPTY);
                    }
                }
            }

        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class ShockwaveAttackGoal extends Goal {
        private int shockWave;

        public ShockwaveAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            if (SkullLord.this.getTarget() != null
                    && SkullLord.this.isNotAbility()
                    && SkullLord.this.getBoneLord() == null
                    && SkullLord.this.getTarget().distanceTo(SkullLord.this) <= 4.0D
                    && SkullLord.this.hasLineOfSight(SkullLord.this.getTarget())) {
                return SkullLord.this.shockWaveCool <= 0;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return SkullLord.this.isShockWave()
                    && SkullLord.this.getTarget() != null
                    && SkullLord.this.getBoneLord() == null
                    && this.shockWave > 0
                    && SkullLord.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingentity = SkullLord.this.getTarget();
            if (livingentity != null) {
                int time = 60;
                if (SkullLord.this.isHalfHealth()){
                    time = 50;
                }
                this.shockWave = time;
                SkullLord.this.setShockWave(true);
                SkullLord.this.level.broadcastEntityEvent(SkullLord.this, (byte) 6);
                SkullLord.this.playSound(ModSounds.BOMB_FUSE.get(), 3.0F, 1.0F);
                SkullLord.this.playSound(ModSounds.SKULL_LORD_CHARGE.get(), 3.0F, 0.25F);
            }
        }

        public void stop() {
            this.shockWave = 0;
            if (SkullLord.this.isHalfHealth()){
                SkullLord.this.shockWaveCool = 100;
            } else {
                SkullLord.this.shockWaveCool = 200;
            }
            SkullLord.this.setShockWave(false);
            SkullLord.this.level.broadcastEntityEvent(SkullLord.this, (byte) 7);
            SkullLord.this.setDeltaMovement(Vec3.ZERO);
        }

        public void tick() {
            SkullLord skullLord = SkullLord.this;
            LivingEntity livingentity = SkullLord.this.getTarget();
            if (livingentity != null && skullLord.level instanceof ServerLevel serverLevel) {
                if (this.shockWave > 0){
                    --this.shockWave;
                    skullLord.setDeltaMovement(Vec3.ZERO);
                    double d1 = livingentity.getX() - skullLord.getX();
                    double d2 = livingentity.getZ() - skullLord.getZ();
                    skullLord.getLookControl().setLookAt(livingentity, 10.0F, skullLord.getMaxHeadXRot());
                    skullLord.setYRot(-((float)Mth.atan2(d1, d2)) * (180F / (float)Math.PI));
                    skullLord.yBodyRot = skullLord.getYRot();
                    ServerParticleUtil.gatheringParticles(ModParticleTypes.LASER_GATHER.get(), skullLord, serverLevel);
                    if (this.shockWave == 10){
                        new ShockwaveSpell().SpellResult(serverLevel, skullLord, ItemStack.EMPTY);
                    }
                }
            }

        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class MoveRandomGoal extends Goal {
        public MoveRandomGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return SkullLord.this.isNotAbility();
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            int distance = 8;
            BlockPos blockPos = null;
            if (SkullLord.this.getBoneLord() != null){
                blockPos = SkullLord.this.getBoneLord().blockPosition().above(2);
            } else if (SkullLord.this.getTarget() != null){
                blockPos = SkullLord.this.getTarget().blockPosition().above(2);
            } else if (SkullLord.this.getBoundOrigin() != null){
                blockPos = SkullLord.this.getBoundOrigin();
            }

            for (int i = 0; i < 64; ++i) {
                if (blockPos != null) {
                    Vec3 vector3d = Vec3.atCenterOf(blockPos);
                    double X = vector3d.x + SkullLord.this.random.nextIntBetweenInclusive(-distance, distance);
                    double Y = vector3d.y + SkullLord.this.random.nextIntBetweenInclusive(-distance, distance);
                    double Z = vector3d.z + SkullLord.this.random.nextIntBetweenInclusive(-distance, distance);

                    BlockPos blockPos1 = new BlockPos(X, Y, Z);
                    if (SkullLord.this.level.isEmptyBlock(blockPos1)) {
                        SkullLord.this.getMoveControl().setWantedPosition(X, Y, Z, 0.05D);
                        break;
                    }
                } else {
                    distance /= 2;
                    double d0 = SkullLord.this.getX() + SkullLord.this.random.nextIntBetweenInclusive(-distance, distance);
                    double d1 = SkullLord.this.getY() + SkullLord.this.random.nextIntBetweenInclusive(-distance, distance);
                    double d2 = SkullLord.this.getZ() + SkullLord.this.random.nextIntBetweenInclusive(-distance, distance);
                    BlockPos blockPos1 = new BlockPos(d0, d1, d2);
                    if (SkullLord.this.level.isEmptyBlock(blockPos1)) {
                        SkullLord.this.getMoveControl().setWantedPosition(d0, d1, d2, 0.05D);
                        break;
                    }
                }
            }
        }
    }

    static class LookAroundGoal extends Goal {
        private final SkullLord skullLord;

        public LookAroundGoal(SkullLord p_i45839_1_) {
            this.skullLord = p_i45839_1_;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        public boolean canUse() {
            return true;
        }

        public void tick() {
            if (this.skullLord.getTarget() == null) {
                Vec3 vector3d = this.skullLord.getDeltaMovement();
                this.skullLord.setYRot(-((float)Mth.atan2(vector3d.x, vector3d.z)) * (180F / (float)Math.PI));
            } else {
                LivingEntity livingentity = this.skullLord.getTarget();
                double d1 = livingentity.getX() - this.skullLord.getX();
                double d2 = livingentity.getZ() - this.skullLord.getZ();
                this.skullLord.getLookControl().setLookAt(livingentity, 10.0F, this.skullLord.getMaxHeadXRot());
                this.skullLord.setYRot(-((float)Mth.atan2(d1, d2)) * (180F / (float)Math.PI));
            }
            this.skullLord.yBodyRot = this.skullLord.getYRot();

        }
    }
}
