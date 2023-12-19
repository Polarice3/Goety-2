package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.api.entities.hostile.IBoss;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.entities.PithosBlockEntity;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.HauntedSkull;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.HauntedSkullProjectile;
import com.Polarice3.Goety.common.entities.util.SkullLaser;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SAddBossPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SkullLord extends Monster implements ICustomAttributes, IBoss {
    protected static final EntityDataAccessor<Byte> FLAGS = SynchedEntityData.defineId(SkullLord.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Optional<UUID>> BONE_LORD = SynchedEntityData.defineId(SkullLord.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Optional<UUID>> LASER = SynchedEntityData.defineId(SkullLord.class, EntityDataSerializers.OPTIONAL_UUID);
    private final ServerBossEvent bossInfo = (ServerBossEvent) new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(false).setCreateWorldFog(false);
    private UUID bossInfoUUID = bossInfo.getId();
    @Nullable
    private BlockPos boundOrigin;
    private int shootTime;
    private int spawnDelay = 100;
    private int spawnNumber = 0;
    private int chargeTime = 0;
    private int laserTime = 0;
    public float explosionRadius = 1.5F;
    public int boneLordRegen;
    private int hitTimes;
    private int stuckTime = 0;

    public SkullLord(EntityType<? extends SkullLord> p_i50190_1_, Level p_i50190_2_) {
        super(p_i50190_1_, p_i50190_2_);
        this.navigation = this.createNavigation(p_i50190_2_);
        this.shootTime = 0;
        this.moveControl = new MobUtil.MinionMoveControl(this);
        this.hitTimes = 0;
        this.xpReward = 70;
        if (this.level.isClientSide){
            Goety.PROXY.addBoss(this);
        }
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new SoulSkullGoal());
        this.goalSelector.addGoal(4, new ChargeAttackGoal());
        this.goalSelector.addGoal(8, new MoveRandomGoal());
        this.goalSelector.addGoal(9, new SkullLordLookGoal(this));
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
            if (this.isCharging()){
                this.chargeTime = this.chargeTime + 100;
            }
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

    public void tick() {
        super.tick();
        this.setNoGravity(true);
        int delay = switch (this.level.getDifficulty()) {
            case NORMAL -> 200;
            case HARD -> 100;
            default -> 400;
        };
        Vec3 vector3d = this.getDeltaMovement();
        double d0 = this.getX() + vector3d.x;
        double d1 = this.getY() + vector3d.y;
        double d2 = this.getZ() + vector3d.z;
        ParticleOptions particleData = ParticleTypes.SMOKE;
        if (this.isInvulnerable()){
            particleData = ParticleTypes.POOF;
        } else if (this.isCharging() || this.isLasering()){
            particleData = ParticleTypes.SOUL_FIRE_FLAME;
        }
        this.level.addParticle(particleData, d0 + this.random.nextGaussian() * (double)0.3F, d1 + this.random.nextGaussian() * (double)0.3F, d2 + this.random.nextGaussian() * (double)0.3F, 0.0D, 0.0D, 0.0D);
        if (this.shootTime > 0){
            --this.shootTime;
        }
        if (this.isInvulnerable()){
            if (this.tickCount % 20 == 0){
                this.heal(1.0F);
            }
            if (this.getLaser() != null) {
                this.getLaser().discard();
            }
        }
        if (this.isOnFire()){
            if (this.tickCount % 100 == 0 || this.isInvulnerable()){
                this.clearFire();
            }
        }
        if (this.getLaser() != null){
            this.lookControl.setLookAt(this.getLaser(), 90.0F, 90.0F);

            if (!this.level.isClientSide) {
                if (this.tickCount % 20 == 0 && this.random.nextFloat() <= 0.25F && this.isHalfHealth()) {
                    BlockPos blockpos = this.blockPosition().offset(-2 + this.getRandom().nextInt(5), 1, -2 + this.getRandom().nextInt(5));
                    HauntedSkull summonedentity = new HauntedSkull(ModEntityType.HAUNTED_SKULL.get(), this.level);
                    summonedentity.setOwnerId(this.getUUID());
                    summonedentity.moveTo(blockpos, 0.0F, 0.0F);
                    summonedentity.finalizeSpawn((ServerLevelAccessor) this.level, this.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                    summonedentity.setBoundOrigin(blockpos);
                    summonedentity.setLimitedLife(MathHelper.minutesToTicks(1));
                    summonedentity.setTarget(this.getTarget());
                    this.level.addFreshEntity(summonedentity);
                }
                this.drawParticleBeam(this, this.getLaser());
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
            } else {
                Vec3 vector3d1 = this.getTarget().getEyePosition(1.0F);

                if (!this.isInvulnerable()) {
                    if (!this.isLasering()) {
                        int cooldown = this.isHalfHealth() ? 350 : 500;
                        if (this.tickCount % cooldown == 0) {
                            this.setLaserTime(true);
                        }
                    }
                } else {
                    this.setLaserTime(false);
                }

                if (this.isLaserTime()){
                    if (!this.isCharging()) {
                        ++this.laserTime;
                        if (this.distanceTo(this.getTarget()) <= 4.0D) {
                            double nx = this.getTarget().getX() - this.getX();
                            double nz = this.getTarget().getZ() - this.getZ();
                            this.moveControl.setWantedPosition(nx, vector3d1.y, nz, 0.25F);
                        } else {
                            this.moveControl.setWantedPosition(vector3d1.x, vector3d1.y, vector3d1.z, 0.25F);
                        }
                        if (this.laserTime == 20) {
                            this.playSound(ModSounds.SKULL_LORD_LASER_BEGIN.get(), 2.0F, 1.0F);
                            if (this.level.getDifficulty() == Difficulty.HARD){
                                this.spawnMobs();
                            }
                        }
                        if (this.laserTime >= 20){
                            if (!this.level.isClientSide) {
                                ServerLevel serverWorld = (ServerLevel) this.level;
                                ServerParticleUtil.gatheringParticles(ModParticleTypes.LASER_GATHER.get(), this, serverWorld);
                            }
                        }
                        if (this.laserTime >= MathHelper.secondsToTicks(5)) {
                            SkullLaser skullLaser = ModEntityType.LASER.get().create(this.level);
                            if (skullLaser != null) {
                                this.playSound(ModSounds.SKULL_LORD_LASER_START.get(), 3.0F, 1.0F);
                                this.setLaserTime(false);
                                skullLaser.setSkullLord(this);
                                skullLaser.setDuration(200);
                                skullLaser.setPos(this.getX(), this.getY(), this.getZ());
                                skullLaser.setTarget(this.getTarget());
                                this.setLaser(skullLaser);
                                this.level.addFreshEntity(skullLaser);
                            }
                        }
                    }
                } else {
                    if (this.isOnGround() || this.getTarget().getEyeY() > this.getY()) {
                        this.moveControl.setWantedPosition(this.getX(), this.getTarget().getEyeY() + 1, this.getZ(), 1.0F);
                    }
                    this.laserTime = 0;
                }

                boolean flag = false;

                if ((this.xOld == this.getX() && this.yOld == this.getY() && this.zOld == this.getZ()) || this.isInWall()){
                    ++this.stuckTime;
                    if (this.stuckTime >= 100) {
                        flag = true;
                        Explosion.BlockInteraction explosion$mode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
                        if (!this.isInvulnerable() && !this.isLasering() && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                            this.level.explode(this, this.getX(), this.getY(), this.getZ(), 2.0F, explosion$mode);
                        }
                        this.stuckTime = 0;
                    }
                } else {
                    this.stuckTime = 0;
                }

                if (!this.level.getBlockState(this.blockPosition().relative(Direction.WEST)).isAir()
                        && !this.level.getBlockState(this.blockPosition().relative(Direction.NORTH)).isAir()) {
                    flag = true;
                }
                if (!this.level.getBlockState(this.blockPosition().relative(Direction.WEST)).isAir()
                        && !this.level.getBlockState(this.blockPosition().relative(Direction.SOUTH)).isAir()) {
                    flag = true;
                }
                if (!this.level.getBlockState(this.blockPosition().relative(Direction.EAST)).isAir()
                        && !this.level.getBlockState(this.blockPosition().relative(Direction.SOUTH)).isAir()) {
                    flag = true;
                }
                if (!this.level.getBlockState(this.blockPosition().relative(Direction.EAST)).isAir()
                        && !this.level.getBlockState(this.blockPosition().relative(Direction.SOUTH)).isAir()) {
                    flag = true;
                }

                if (flag){
                    if (!this.isInvulnerable() || this.isLaserTime() || this.isLasering()) {
                        if (this.distanceToSqr(this.getTarget()) > 4.0F) {
                            this.moveControl.setWantedPosition(vector3d1.x, vector3d1.y, vector3d1.z, 1.0F);
                        } else {
                            double nx = this.getTarget().getX() - this.getX();
                            double nz = this.getTarget().getZ() - this.getZ();
                            this.moveControl.setWantedPosition(nx, vector3d1.y, nz, 1.0F);
                        }
                    } else {
                        this.moveControl.setWantedPosition(vector3d1.x, vector3d1.y, vector3d1.z, 1.0F);
                    }
                }
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
                        if (this.getLaser() != null){
                            this.getLaser().discard();
                        }
                        this.discard();
                    }
                }
            }
        }
        if (!this.level.isClientSide){
            ServerLevel serverWorld = (ServerLevel) this.level;
            int i = this.blockPosition().getX();
            int j = this.blockPosition().getY();
            int k = this.blockPosition().getZ();
            List<Owned> list = this.level.getEntitiesOfClass(Owned.class, (new AABB(i, j, k, i, j - 4, k)).inflate(8.0D, 8.0D, 8.0D), (owned -> owned.getTrueOwner() == this));
            if (list.size() < 8 && this.getTarget() != null && !this.isLasering()){
                if (this.spawnDelay > 0) {
                    --this.spawnDelay;
                } else {
                    this.spawnDelay = this.level.random.nextInt(delay) + delay;
                    this.spawnMobs();
                }
            }
            if (this.getBoneLord() == null || (this.getBoneLord() != null && this.getBoneLord().isDeadOrDying())){
                if (!this.isLaserTime()){
                    --this.boneLordRegen;
                }
                this.setIsInvulnerable(false);
                this.level.broadcastEntityEvent(this, (byte) 5);
                for (BoneLord boneLord : this.level.getEntitiesOfClass(BoneLord.class, this.getBoundingBox().inflate(32))){
                    if (boneLord.getSkullLord() == this){
                        this.setBoneLord(boneLord);
                    }
                }
                if (this.boneLordRegen <= 0 && !this.isLasering()){
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
                    if (this.getBoneLord().getTarget() != null && this.getTarget() == null){
                        this.setTarget(this.getBoneLord().getTarget());
                    }
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
                if (this.chargeTime >= 200){
                    this.setIsCharging(false);
                }
            } else {
                this.chargeTime = 0;
            }
        }
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

    private void drawParticleBeam(LivingEntity pSource, LivingEntity pTarget) {
        double d0 = pTarget.getX() - pSource.getX();
        double d1 = (pTarget.getY() + (double) pTarget.getBbHeight() * 0.5F) - (pSource.getY() + (double) pSource.getBbHeight() * 0.5D);
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
                serverWorld.sendParticles(ModParticleTypes.LASER_POINT.get(), pSource.getX() + d0 * d4, pSource.getY() + d1 * d4 + (double) pSource.getEyeHeight() * 0.5D, pSource.getZ() + d2 * d4, 1, 0.0D, 0.0D, 0.0D, 0.0D);
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
            for (int i = 0; i < 1 + serverLevel.random.nextInt(2); ++i) {
                double d3 = (double) this.blockPosition().getX() + (serverLevel.random.nextDouble() - serverLevel.random.nextDouble()) * (double) spawnRange + 0.5D;
                double d4 = (double) (this.blockPosition().getY() + serverLevel.random.nextInt(3));
                double d5 = (double) this.blockPosition().getZ() + (serverLevel.random.nextDouble() - serverLevel.random.nextDouble()) * (double) spawnRange + 0.5D;
                Owned owned;
                if (this.isUnderWater()) {
                    owned = ModEntityType.DROWNED_SERVANT.get().create(serverLevel);
                } else {
                    if (random) {
                        owned = ModEntityType.ZOMBIE_SERVANT.get().create(serverLevel);
                    } else {
                        owned = ModEntityType.SKELETON_SERVANT.get().create(serverLevel);
                    }
                }
                if (owned != null) {
                    BlockPos blockPos = new BlockPos(d3, d4, d5);
                    owned.setTrueOwner(this);
                    owned.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 0, false, false));
                    if (!serverLevel.getBlockState(blockPos).isAir()) {
                        owned.moveTo(this.blockPosition().getX(), this.blockPosition().getY(), this.blockPosition().getZ());
                    } else {
                        owned.moveTo(d3, d4, d5);
                    }
                    owned.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(owned.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    serverLevel.addFreshEntityWithPassengers(owned);
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
        if (this.getLaser() != null){
            this.getLaser().discard();
        }
        if (this.getPithos() != null){
            if (this.getPithos() instanceof PithosBlockEntity pithosTile){
                pithosTile.unlock();
            }
        }
    }

    @Override
    public void remove(RemovalReason p_146834_) {
        if (this.level.isClientSide) {
            Goety.PROXY.removeBoss(this);
        }
        super.remove(p_146834_);
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if (this.getBoneLord() != null){
            this.getBoneLord().discard();
        }
        if (this.getLaser() != null){
            this.getLaser().discard();
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

    @Override
    public boolean ignoreExplosion() {
        return true;
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
        this.entityData.define(LASER, Optional.empty());
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
        try {
            UUID uuid = this.getBoneLordUUID();
            if (uuid != null){
                if (EntityFinder.getLivingEntityByUuiD(uuid) instanceof BoneLord){
                    return (BoneLord) EntityFinder.getLivingEntityByUuiD(uuid);
                }
            }
            return null;
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @Nullable
    public UUID getBoneLordUUID() {
        return this.entityData.get(BONE_LORD).orElse(null);
    }

    public void setBoneLordUUID(UUID uuid){
        this.entityData.set(BONE_LORD, Optional.ofNullable(uuid));
    }

    public void setBoneLord(BoneLord boneLord){
        this.setBoneLordUUID(boneLord.getUUID());
    }

    public boolean isLasering(){
        return this.getLaser() != null;
    }

    @Nullable
    public SkullLaser getLaser() {
        try {
            UUID uuid = this.getLaserUUID();
            if (uuid != null){
                if (EntityFinder.getLivingEntityByUuiD(uuid) instanceof SkullLaser){
                    return (SkullLaser) EntityFinder.getLivingEntityByUuiD(uuid);
                }
            }
            return null;
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @Nullable
    public UUID getLaserUUID() {
        return this.entityData.get(LASER).orElse(null);
    }

    public void setLaserUUID(UUID uuid){
        this.entityData.set(LASER, Optional.ofNullable(uuid));
    }

    public void setLaser(SkullLaser laser){
        this.setLaserUUID(laser.getUUID());
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

    public boolean isLaserTime() {
        return this.geFlags(8);
    }

    public void setLaserTime(boolean laserTime) {
        this.setFlags(8, laserTime);
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
        if (pCompound.hasUUID("laser")) {
            uuid = pCompound.getUUID("laser");
        } else {
            String s = pCompound.getString("laser");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.setLaserUUID(uuid);
            } catch (Throwable ignored) {
            }
        }
        this.shootTime = pCompound.getInt("shootTime");
        this.hitTimes = pCompound.getInt("hitTimes");
        this.boneLordRegen = pCompound.getInt("boneLordRegen");
        this.spawnDelay = pCompound.getInt("spawnDelay");
        this.spawnNumber = pCompound.getInt("spawnNumber");
        this.laserTime = pCompound.getInt("laserTime");
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
        if (this.getLaserUUID() != null) {
            pCompound.putUUID("laser", this.getLaserUUID());
        }
        pCompound.putInt("shootTime", this.shootTime);
        pCompound.putInt("hitTimes", this.hitTimes);
        pCompound.putInt("boneLordRegen", this.boneLordRegen);
        pCompound.putInt("spawnDelay", this.spawnDelay);
        pCompound.putInt("spawnNumber", this.spawnNumber);
        pCompound.putInt("laserTime", this.laserTime);
        pCompound.putInt("stuckTime", this.stuckTime);
    }

    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setVisible(MainConfig.SpecialBossBar.get());
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        if (MainConfig.SpecialBossBar.get()) {
            this.bossInfo.addPlayer(pPlayer);
        }
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
    public UUID getBossInfoUUID() {
        return this.bossInfoUUID;
    }

    @Override
    public void setBossInfoUUID(UUID bossInfoUUID) {
        this.bossInfoUUID = bossInfoUUID;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) ModNetwork.INSTANCE.toVanillaPacket(new SAddBossPacket(new ClientboundAddEntityPacket(this), bossInfoUUID), NetworkDirection.PLAY_TO_CLIENT);
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4){
            this.setIsInvulnerable(true);
        } else if (p_21375_ == 5){
            this.setIsInvulnerable(false);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    class SoulSkullGoal extends Goal {
        public SoulSkullGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            if (SkullLord.this.getTarget() != null
                    && !SkullLord.this.getMoveControl().hasWanted()
                    && SkullLord.this.isInvulnerable()) {
                return !SkullLord.this.getTarget().isAlliedTo(SkullLord.this);
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return SkullLord.this.getMoveControl().hasWanted()
                    && SkullLord.this.getTarget() != null
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
                if (SkullLord.this.shootTime == 0) {
                    double d1 = livingentity.getX() - SkullLord.this.getX();
                    double d2 = livingentity.getY(0.5D) - SkullLord.this.getY(0.5D);
                    double d3 = livingentity.getZ() - SkullLord.this.getZ();
                    HauntedSkullProjectile soulSkull = new HauntedSkullProjectile(SkullLord.this, d1, d2, d3, SkullLord.this.level);
                    soulSkull.setPos(soulSkull.getX(), SkullLord.this.getY(0.75D), soulSkull.getZ());
                    soulSkull.setYRot(SkullLord.this.getYRot());
                    soulSkull.setXRot(SkullLord.this.getXRot());
                    if (SkullLord.this.level.random.nextFloat() <= 0.05F){
                        soulSkull.setDangerous(true);
                        SkullLord.this.shootTime = shoot + 40;
                    } else {
                        SkullLord.this.shootTime = shoot;
                    }
                    SkullLord.this.level.addFreshEntity(soulSkull);
                    SkullLord.this.playSound(ModSounds.SKULL_LORD_SHOOT.get(), 1.0F, 1.0F);
                }
                double d2 = SkullLord.this.getTarget().getX() - SkullLord.this.getX();
                double d1 = SkullLord.this.getTarget().getZ() - SkullLord.this.getZ();
                SkullLord.this.setYRot(-((float) MathHelper.atan2(d2, d1)) * (180F / (float)Math.PI));
                SkullLord.this.yBodyRot = SkullLord.this.getYRot();
            }
        }
    }

    class ChargeAttackGoal extends Goal {
        public ChargeAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            if (SkullLord.this.getTarget() != null
                    && !SkullLord.this.getMoveControl().hasWanted()
                    && SkullLord.this.getBoneLord() == null
                    && !SkullLord.this.isLaserTime()
                    && !SkullLord.this.isLasering()) {
                if (!SkullLord.this.isHalfHealth()){
                    return SkullLord.this.random.nextInt(7) == 0;
                } else {
                    return SkullLord.this.random.nextInt(3) == 0;
                }
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return SkullLord.this.getMoveControl().hasWanted()
                    && SkullLord.this.isCharging()
                    && SkullLord.this.getTarget() != null
                    && SkullLord.this.getBoneLord() == null
                    && SkullLord.this.getTarget().isAlive()
                    && !SkullLord.this.isLaserTime()
                    && !SkullLord.this.isLasering();
        }

        public void start() {
            LivingEntity livingentity = SkullLord.this.getTarget();
            if (livingentity != null) {
                Vec3 vector3d = livingentity.getEyePosition(1.0F);
                SkullLord.this.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, SkullLord.this.isHalfHealth() ? 1.25D : 1.0D);
                SkullLord.this.setIsCharging(true);
                SkullLord.this.playSound(ModSounds.SKULL_LORD_CHARGE.get(), 1.0F, 1.0F);
            }
        }

        public void stop() {
            SkullLord.this.setIsCharging(false);
        }

        public void tick() {
            SkullLord skullLord = SkullLord.this;
            LivingEntity livingentity = SkullLord.this.getTarget();
            if (livingentity != null) {
                if (skullLord.getBoundingBox().intersects(livingentity.getBoundingBox())) {
                    if (livingentity.hurt(DamageSource.indirectMagic(skullLord, skullLord), (float) skullLord.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                        if (skullLord.isOnFire()) {
                            livingentity.setSecondsOnFire(5);
                        }
                        skullLord.level.explode(skullLord, skullLord.getX(), skullLord.getY(), skullLord.getZ(), skullLord.explosionRadius, Explosion.BlockInteraction.NONE);
                        skullLord.setIsCharging(false);
                    }
                } else {
                    double d0 = skullLord.distanceToSqr(livingentity);
                    if (d0 < 9.0D) {
                        Vec3 vector3d = livingentity.getEyePosition(1.0F);
                        skullLord.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, SkullLord.this.isHalfHealth() ? 1.25D : 1.0D);
                    }
                }
            }

        }
    }

    class MoveRandomGoal extends Goal {
        public MoveRandomGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return !SkullLord.this.getMoveControl().hasWanted() && SkullLord.this.random.nextInt(7) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = SkullLord.this.blockPosition();
            if (SkullLord.this.getBoundOrigin() != null) {
                blockpos = SkullLord.this.getBoundOrigin();
            } else if (SkullLord.this.getBoneLord() != null){
                blockpos = SkullLord.this.getBoneLord().blockPosition();
            } else if (SkullLord.this.getTarget() != null){
                blockpos = SkullLord.this.getTarget().blockPosition();
            }

            for(int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(SkullLord.this.random.nextInt(15) - 7, SkullLord.this.random.nextInt(11) - 5, SkullLord.this.random.nextInt(15) - 7);
                if (SkullLord.this.level.isEmptyBlock(blockpos1)) {
                    SkullLord.this.playSound(ModSounds.SKULL_LORD_FLY.get());
                    SkullLord.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
                    if (SkullLord.this.getTarget() == null) {
                        SkullLord.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }

    static class SkullLordLookGoal extends LookAtPlayerGoal {
        private final SkullLord skullLord;

        public SkullLordLookGoal(SkullLord p_i1631_1_) {
            super(p_i1631_1_, Player.class, 3.0F, 1.0F);
            this.skullLord = p_i1631_1_;
        }

        public boolean canUse() {
            if (this.skullLord.getLaser() != null){
                return false;
            } else {
                return super.canUse();
            }
        }
    }

}
