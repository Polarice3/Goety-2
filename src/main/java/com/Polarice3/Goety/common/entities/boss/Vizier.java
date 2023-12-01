package com.Polarice3.Goety.common.entities.boss;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MobsConfig;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.hostile.IBoss;
import com.Polarice3.Goety.common.entities.hostile.Irk;
import com.Polarice3.Goety.common.entities.neutral.ICustomAttributes;
import com.Polarice3.Goety.common.entities.projectiles.Spike;
import com.Polarice3.Goety.common.entities.projectiles.SwordProjectile;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SAddBossPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;
import java.util.function.Predicate;

@OnlyIn(
        value = Dist.CLIENT,
        _interface = PowerableMob.class
)
public class Vizier extends SpellcasterIllager implements PowerableMob, ICustomAttributes, IBoss {
    private static final Predicate<Entity> field_213690_b = (p_213685_0_) -> {
        return p_213685_0_.isAlive() && !(p_213685_0_ instanceof Vizier);
    };
    protected static final EntityDataAccessor<Byte> VIZIER_FLAGS = SynchedEntityData.defineId(Vizier.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Integer> CAST_TIMES = SynchedEntityData.defineId(Vizier.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> CASTING = SynchedEntityData.defineId(Vizier.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CONFUSED = SynchedEntityData.defineId(Vizier.class, EntityDataSerializers.INT);
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.PROGRESS);
    private UUID bossInfoUUID = bossInfo.getId();
    public float oBob;
    public float bob;
    public double xCloakO;
    public double yCloakO;
    public double zCloakO;
    public double xCloak;
    public double yCloak;
    public double zCloak;
    public boolean flyWarn;
    public int airBound;
    public int deathTime = 0;

    public Vizier(EntityType<? extends Vizier> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new MobUtil.MinionMoveControl(this);
        this.xpReward = 50;
        if (this.level.isClientSide){
            Goety.PROXY.addBoss(this);
        }
    }

    public void move(MoverType typeIn, Vec3 pos) {
        super.move(typeIn, pos);
        this.checkInsideBlocks();
    }

    public void tick() {
        if (this.getInvulnerableTicks() > 0) {
            this.setDeltaMovement(Vec3.ZERO);
            int j1 = this.getInvulnerableTicks() - 1;
            if (j1 == 20){
                for(int i = 0; i < 5; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    this.level.addParticle(ModParticleTypes.CONFUSED.get(), this.getRandomX(1.0D), this.getRandomY() + 1.0D, this.getRandomZ(1.0D), d0, d1, d2);
                }
                this.playSound(ModSounds.VIZIER_CONFUSE.get(), 1.0F, 1.0F);
            }
            if (j1 <= 0) {
                for(int i = 0; i < 5; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    this.level.addParticle(ParticleTypes.ANGRY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 1.0D, this.getRandomZ(1.0D), d0, d1, d2);
                }
                this.playSound(ModSounds.VIZIER_RAGE.get(), 1.0F, 1.0F);
            }
            this.setInvulnerableTicks(j1);
        }
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
        this.oBob = this.bob;
        float f = Math.min(0.1F, Mth.sqrt((float) getHorizontalDistanceSqr(this.getDeltaMovement())));

        this.bob += (f - this.bob) * 0.4F;
        if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove(RemovalReason.DISCARDED);
        }
        if (!this.isSpellcasting()){
            this.setCasting(this.getCasting() + 1);
        } else {
            if (this.getCastTimes() == 3){
                this.setDeltaMovement(Vec3.ZERO);
            } else {
                Vec3 vector3d = this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D);
                if (!this.level.isClientSide){
                    if (this.getTarget() != null){
                        double d0 = vector3d.y;
                        if (this.getY() < this.getTarget().getY() + 3.0D) {
                            d0 = Math.max(0.0D, d0);
                            d0 = d0 + (0.3D - d0 * (double)0.6F);
                        }

                        vector3d = new Vec3(vector3d.x, d0, vector3d.z);
                        Vec3 vector3d1 = new Vec3(this.getTarget().getX() - this.getX(), 0.0D, this.getTarget().getZ() - this.getZ());
                        if (getHorizontalDistanceSqr(vector3d1) > 9.0D) {
                            Vec3 vector3d2 = vector3d1.normalize();
                            vector3d = vector3d.add(vector3d2.x * 0.3D - vector3d.x * 0.6D, 0.0D, vector3d2.z * 0.3D - vector3d.z * 0.6D);
                        }
                    }
                }
                this.setDeltaMovement(vector3d);
                if (getHorizontalDistanceSqr(vector3d) > 0.05D) {
                    this.setYRot((float)Mth.atan2(vector3d.z, vector3d.x) * (180F / (float)Math.PI) - 90.0F);
                }
            }
        }
        if (this.getCasting() >= 300){
            this.setCasting(0);
        }
        int i = Vizier.this.level.getEntitiesOfClass(Irk.class, Vizier.this.getBoundingBox().inflate(64)).size();
        if (MobsConfig.VizierMinion.get()){
            i = Vizier.this.level.getEntitiesOfClass(Vex.class, Vizier.this.getBoundingBox().inflate(64)).size();
        }
        if (this.getCastTimes() == 1){
            if (i >= 2){
                if (this.level.random.nextBoolean()) {
                    this.setCastTimes(2);
                } else {
                    this.setCastTimes(3);
                }
            } else {
                this.setCastTimes(3);
            }
        }
        this.moveCloak();
        if (this.getTarget() == null){
            for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(32.0F), EntitySelector.NO_CREATIVE_OR_SPECTATOR)){
                if ((livingEntity instanceof Player || livingEntity instanceof AbstractVillager || livingEntity instanceof IronGolem) && canAttack(livingEntity)){
                    this.setTarget(livingEntity);
                }
            }
        } else {
            if (!this.getTarget().isOnGround()){
                ++this.airBound;
            } else {
                this.airBound = 0;
            }
        }
    }

    public static double getHorizontalDistanceSqr(Vec3 pVector) {
        return pVector.x * pVector.x + pVector.z * pVector.z;
    }

    private void moveCloak() {
        this.xCloakO = this.xCloak;
        this.yCloakO = this.yCloak;
        this.zCloakO = this.zCloak;
        double d0 = this.getX() - this.xCloak;
        double d1 = this.getY() - this.yCloak;
        double d2 = this.getZ() - this.zCloak;
        double d3 = 10.0D;
        if (d0 > d3) {
            this.xCloak = this.getX();
            this.xCloakO = this.xCloak;
        }

        if (d2 > d3) {
            this.zCloak = this.getZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 > d3) {
            this.yCloak = this.getY();
            this.yCloakO = this.yCloak;
        }

        if (d0 < -d3) {
            this.xCloak = this.getX();
            this.xCloakO = this.xCloak;
        }

        if (d2 < -d3) {
            this.zCloak = this.getZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 < -d3) {
            this.yCloak = this.getY();
            this.yCloakO = this.yCloak;
        }

        this.xCloak += d0 * 0.25D;
        this.zCloak += d2 * 0.25D;
        this.yCloak += d1 * 0.25D;
    }

    protected SoundEvent getCastingSoundEvent () {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new DoNothingGoal());
        this.goalSelector.addGoal(1, new FangsSpellGoal());
        this.goalSelector.addGoal(1, new HealGoal());
        this.goalSelector.addGoal(1, new SpikesGoal());
        this.goalSelector.addGoal(1, new MoveRandomGoal());
        this.goalSelector.addGoal(4, new ChargeAttackGoal());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.VizierHealth.get())
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.VizierHealth.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VIZIER_FLAGS, (byte)0);
        this.entityData.define(CAST_TIMES, 0);
        this.entityData.define(CASTING, 0);
        this.entityData.define(CONFUSED, 0);
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.VIZIER_AMBIENT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.VIZIER_DEATH.get();
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.VIZIER_HURT.get();
    }

    public int getInvulnerableTicks() {
        return this.entityData.get(CONFUSED);
    }

    public void setInvulnerableTicks(int pTime) {
        this.entityData.set(CONFUSED, pTime);
    }

    public int getCastTimes() {
        return this.entityData.get(CAST_TIMES);
    }

    public void setCastTimes(int pTime) {
        this.entityData.set(CAST_TIMES, pTime);
    }

    public int getCasting() {
        return this.entityData.get(CASTING);
    }

    public void setCasting(int pTime) {
        this.entityData.set(CASTING, pTime);
    }

    public void makeInvulnerable() {
        this.setInvulnerableTicks(40);
    }

    @Override
    public boolean canBeLeader() {
        return false;
    }

    public void die(DamageSource cause) {
        this.playSound(ModSounds.VIZIER_SCREAM.get(), 4.0F, 1.0F);
        if (!MobsConfig.VizierMinion.get()) {
            for (Irk ally : Vizier.this.level.getEntitiesOfClass(Irk.class, Vizier.this.getBoundingBox().inflate(64.0D), field_213690_b)) {
                ally.hurt(DamageSource.STARVE, 200.0F);
            }
        } else {
            for (Vex ally : Vizier.this.level.getEntitiesOfClass(Vex.class, Vizier.this.getBoundingBox().inflate(64.0D), field_213690_b)) {
                ally.hurt(DamageSource.STARVE, 200.0F);
            }
        }

        if (cause.getEntity() != null) {
            if (cause.getEntity() instanceof Player player){
                MobEffectInstance effectinstance = new MobEffectInstance(MobEffects.BAD_OMEN, 120000, 4, false, false, true);
                if (!this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
                    player.addEffect(effectinstance);
                }
            }
        }
        super.die(cause);
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime > 0){
            if (!this.level.isClientSide){
                ServerLevel serverWorld = (ServerLevel) this.level;
                for (int p = 0; p < 8; ++p) {
                    double d0 = (double)this.getX() + this.level.random.nextDouble();
                    double d1 = (double)this.getY() + this.level.random.nextDouble();
                    double d2 = (double)this.getZ() + this.level.random.nextDouble();
                    serverWorld.sendParticles(ModParticleTypes.BULLET_EFFECT.get(), d0, d1, d2, 0, 0.45, 0.45, 0.45, 0.5F);
                }
            }
        }
        if (this.deathTime == 40) {
            this.playSound(SoundEvents.GENERIC_EXPLODE, 2.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F);
            if (!this.level.isClientSide){
                ServerLevel serverWorld = (ServerLevel) this.level;
                serverWorld.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
                for (int p = 0; p < 32; ++p) {
                    double d0 = (double)this.getX() + this.level.random.nextDouble();
                    double d1 = (double)this.getY() + this.level.random.nextDouble();
                    double d2 = (double)this.getZ() + this.level.random.nextDouble();
                    this.level.addParticle(ModParticleTypes.BULLET_EFFECT.get(), d0, d1, d2, 0.45, 0.45, 0.45);
                    serverWorld.sendParticles(ModParticleTypes.BULLET_EFFECT.get(), d0, d1, d2, 0, 0.45, 0.45, 0.45, 0.5F);
                }
            }
            this.remove(RemovalReason.KILLED);
        }
    }

    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public void remove(Entity.RemovalReason removalReason) {
        if (this.level.isClientSide) {
            Goety.PROXY.removeBoss(this);
        }
        super.remove(removalReason);
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        LivingEntity livingEntity = this.getTarget();
        if (this.getInvulnerableTicks() > 0 && pSource != DamageSource.OUT_OF_WORLD){
            return false;
        }
        if (livingEntity != null){
            if (pSource.getEntity() instanceof Irk){
                return false;
            } else {
                if (!MobsConfig.VizierMinion.get()) {
                    int irks = this.level.getEntitiesOfClass(Irk.class, this.getBoundingBox().inflate(32)).size();
                    if ((this.level.random.nextBoolean() || this.getHealth() < this.getMaxHealth()/2) && irks < 16) {
                        Irk irk = new Irk(ModEntityType.IRK.get(), this.level);
                        irk.setPos(this.getX(), this.getY(), this.getZ());
                        irk.setLimitedLife(MobUtil.getSummonLifespan(this.level));
                        irk.setTrueOwner(this);
                        this.level.addFreshEntity(irk);
                    }
                } else {
                    int vexes = this.level.getEntitiesOfClass(Vex.class, this.getBoundingBox().inflate(32)).size();
                    if ((this.level.random.nextBoolean() || this.getHealth() < this.getMaxHealth()/2) && vexes < 16) {
                        if (!this.level.isClientSide) {
                            Vex irk = new Vex(EntityType.VEX, this.level);
                            irk.setPos(this.getX(), this.getY(), this.getZ());
                            irk.setLimitedLife(MobUtil.getSummonLifespan(this.level));
                            irk.setOwner(this);
                            irk.finalizeSpawn((ServerLevelAccessor) this.level, this.level.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                            this.level.addFreshEntity(irk);
                        }
                    }
                }
            }
        }

        if (pAmount > (float) AttributesConfig.VizierDamageCap.get() && pSource != DamageSource.OUT_OF_WORLD){
            return super.hurt(pSource, (float) AttributesConfig.VizierDamageCap.get());
        } else {
            if (this.isSpellcasting() && pSource != DamageSource.OUT_OF_WORLD){
                return super.hurt(pSource, pAmount/2);
            } else {
                return super.hurt(pSource, pAmount);
            }
        }
    }

    @Override
    public void kill() {
        this.setHealth(0.0F);
    }

    public boolean isAlliedTo(Entity pEntity) {
        if (pEntity == this) {
            return true;
        } else if (super.isAlliedTo(pEntity)) {
            return true;
        } else if (pEntity instanceof Irk) {
            return this.isAlliedTo(((Irk)pEntity).getTrueOwner());
        } else if (pEntity instanceof LivingEntity && ((LivingEntity)pEntity).getMobType() == MobType.ILLAGER) {
            return this.getTeam() == null && pEntity.getTeam() == null;
        } else {
            return false;
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Confused", this.getInvulnerableTicks());
        compound.putInt("Casting", this.getCasting());
        compound.putInt("CastTimes", this.getCastTimes());
        compound.putBoolean("FlyWarn", this.flyWarn);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setInvulnerableTicks(compound.getInt("Confused"));
        this.setCasting(compound.getInt("Casting"));
        this.setCastTimes(compound.getInt("CastTimes"));
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
        this.flyWarn = compound.getBoolean("FlyWarn");
        this.setConfigurableAttributes();
    }

    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setVisible(this.getInvulnerableTicks() <= 0);
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        this.bossInfo.addPlayer(pPlayer);
    }

    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossInfo.removePlayer(pPlayer);
    }

    private boolean getVizierFlag(int mask) {
        int i = this.entityData.get(VIZIER_FLAGS);
        return (i & mask) != 0;
    }

    private void setVizierFlag(int mask, boolean value) {
        int i = this.entityData.get(VIZIER_FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(VIZIER_FLAGS, (byte)(i & 255));
    }

    public boolean isCharging() {
        return this.getVizierFlag(1);
    }

    public void setCharging(boolean charging) {
        this.setVizierFlag(1, charging);
    }

    public boolean isSpellcasting(){
        return this.getVizierFlag(2);
    }

    public void setSpellcasting(boolean spellcasting){
        this.setVizierFlag(2, spellcasting);
    }

    public IllagerArmPose getArmPose() {
        if (this.isCharging()) {
            return IllagerArmPose.ATTACKING;
        } else if (this.isSpellcasting()){
            return IllagerArmPose.SPELLCASTING;
        } else {
            return this.isCelebrating() ? IllagerArmPose.CELEBRATING : IllagerArmPose.CROSSED;
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_37856_, DifficultyInstance p_37857_, MobSpawnType p_37858_, @Nullable SpawnGroupData p_37859_, @Nullable CompoundTag p_37860_) {
        this.populateDefaultEquipmentSlots(p_37856_.getRandom(), p_37857_);
        this.populateDefaultEquipmentEnchantments(p_37856_.getRandom(), p_37857_);
        return super.finalizeSpawn(p_37856_, p_37857_, p_37858_, p_37859_, p_37860_);
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {
        super.dropCustomDeathLoot(source, looting, recentlyHitIn);
        ItemEntity itementity = this.spawnAtLocation(ModItems.SOUL_RUBY.get());
        if (itementity != null) {
            itementity.setExtendedLifetime();
        }

    }

    public void applyRaidBuffs(int wave, boolean p_213660_2_) {
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.VIZIER_CELEBRATE.get();
    }

    @Override
    public boolean isPowered() {
        return this.isSpellcasting();
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

    class FangsSpellGoal extends Goal {
        int duration;
        int duration2;

        private FangsSpellGoal() {
        }

        public boolean canUse() {
            return Vizier.this.getCasting() >= 200
                    && Vizier.this.getTarget() != null
                    && !Vizier.this.isCharging()
                    && Vizier.this.getCastTimes() == 0;
        }

        public void start() {
            Vizier.this.playSound(SoundEvents.EVOKER_PREPARE_ATTACK, 1.0F, 1.0F);
            Vizier.this.setSpellcasting(true);
            Vizier.this.airBound = 0;
        }

        public void stop() {
            Vizier.this.setSpellcasting(false);
            Vizier.this.setCasting(0);
            Vizier.this.setCastTimes(1);
            this.duration2 = 0;
            this.duration = 0;
        }

        public void tick() {
            LivingEntity livingentity = Vizier.this.getTarget();
            if (livingentity != null) {
                ++this.duration;
                ++this.duration2;
                if (Vizier.this.airBound > 20){
                    if (!Vizier.this.level.isClientSide) {
                        ServerLevel serverWorld = (ServerLevel) Vizier.this.level;
                        for (int i = 0; i < 5; ++i) {
                            double d0 = serverWorld.random.nextGaussian() * 0.02D;
                            double d1 = serverWorld.random.nextGaussian() * 0.02D;
                            double d2 = serverWorld.random.nextGaussian() * 0.02D;
                            serverWorld.sendParticles(ParticleTypes.ENCHANT, Vizier.this.getRandomX(1.0D), Vizier.this.getRandomY() + 1.0D, Vizier.this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                }
                int time = Vizier.this.getHealth() <= Vizier.this.getMaxHealth() / 2 ? 5 : 10;
                time = Vizier.this.airBound > 20 ? time * 2 : time;
                if (this.duration >= time) {
                    this.duration = 0;
                    if (Vizier.this.airBound > 20 && !Vizier.this.flyWarn){
                        Vizier.this.playSound(ModSounds.VIZIER_CELEBRATE.get(), 1.0F, 1.5F);
                        Vizier.this.flyWarn = true;
                    } else {
                        this.attack(livingentity);
                    }
                }
                if (this.duration2 >= 160) {
                    Vizier.this.setSpellcasting(false);
                    Vizier.this.setCasting(0);
                    Vizier.this.setCastTimes(1);
                    this.duration2 = 0;
                    this.duration = 0;
                }
            } else {
                stop();
            }
        }

        private void attack(LivingEntity livingEntity){
            if (Vizier.this.airBound < 20) {
                float f = (float) Mth.atan2(livingEntity.getZ() - Vizier.this.getZ(), livingEntity.getX() - Vizier.this.getX());
                this.spawnFangs(livingEntity.getX(), livingEntity.getZ(), livingEntity.getY(), livingEntity.getY() + 1.0D, f, 1);
            } else {
                SwordProjectile swordProjectile = new SwordProjectile(Vizier.this, Vizier.this.level, Vizier.this.getMainHandItem());
                double d0 = livingEntity.getX() - Vizier.this.getX();
                double d1 = livingEntity.getY(0.3333333333333333D) - swordProjectile.getY();
                double d2 = livingEntity.getZ() - Vizier.this.getZ();
                double d3 = (double)Mth.sqrt((float) (d0 * d0 + d2 * d2));
                swordProjectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                swordProjectile.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, 1.0F);
                if (!Vizier.this.getSensing().hasLineOfSight(livingEntity)){
                    swordProjectile.setNoPhysics(true);
                }
                Vizier.this.level.addFreshEntity(swordProjectile);
                if (!Vizier.this.isSilent()) {
                    Vizier.this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F);
                }
            }
        }

        private void spawnFangs(double p_190876_1_, double p_190876_3_, double p_190876_5_, double p_190876_7_, float p_190876_9_, int p_190876_10_) {
            BlockPos blockpos = new BlockPos(p_190876_1_, p_190876_7_, p_190876_3_);
            boolean flag = false;
            double d0 = 0.0D;

            do {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = Vizier.this.level.getBlockState(blockpos1);
                if (blockstate.isFaceSturdy(Vizier.this.level, blockpos1, Direction.UP)) {
                    if (!Vizier.this.level.isEmptyBlock(blockpos)) {
                        BlockState blockstate1 = Vizier.this.level.getBlockState(blockpos);
                        VoxelShape voxelshape = blockstate1.getCollisionShape(Vizier.this.level, blockpos);
                        if (!voxelshape.isEmpty()) {
                            d0 = voxelshape.max(Direction.Axis.Y);
                        }
                    }

                    flag = true;
                    break;
                }

                blockpos = blockpos.below();
            } while(blockpos.getY() >= Mth.floor(p_190876_5_) - 1);

            if (flag) {
                Vizier.this.level.addFreshEntity(new EvokerFangs(Vizier.this.level, p_190876_1_, (double)blockpos.getY() + d0, p_190876_3_, p_190876_9_, p_190876_10_, Vizier.this));
            }

        }
    }

    class HealGoal extends Goal {
        int duration3;

        private HealGoal() {
        }

        public boolean canUse(){
            return Vizier.this.getTarget() != null
                    && !Vizier.this.isCharging()
                    && Vizier.this.getCasting() >= 100
                    && Vizier.this.getCastTimes() == 2;
        }

        public void start() {
            int i = 0;
            for (Mob ally : Vizier.this.level.getEntitiesOfClass(Mob.class, Vizier.this.getBoundingBox().inflate(64.0D), field_213690_b)) {
                if (ally instanceof Vex || ally instanceof Irk){
                    ++i;
                }
            }
            if (i >= 2) {
                Vizier.this.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 1.0F, 1.0F);
                Vizier.this.playSound(ModSounds.VIZIER_CELEBRATE.get(), 1.0F, 1.0F);
                Vizier.this.setSpellcasting(true);
            } else {
                Vizier.this.setCastTimes(0);
                Vizier.this.setCasting(0);
                this.duration3 = 0;
            }
        }

        public void stop() {
            Vizier.this.setSpellcasting(false);
            Vizier.this.setCastTimes(0);
            Vizier.this.setCasting(0);
            this.duration3 = 0;
        }

        public void tick() {
            int i = 0;
            ++this.duration3;
            if (this.duration3 >= 60) {
                for (Mob ally : Vizier.this.level.getEntitiesOfClass(Mob.class, Vizier.this.getBoundingBox().inflate(64.0D), field_213690_b)) {
                    if (ally instanceof Vex || ally instanceof Irk){
                        Vizier.this.heal(ally.getHealth());
                        ++i;
                        ally.hurt(DamageSource.STARVE, 200.0F);
                    }
                }
                if (i != 0) {
                    Vizier.this.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);
                }
                this.duration3 = 0;
                Vizier.this.setSpellcasting(false);
                Vizier.this.setCastTimes(0);
                Vizier.this.setCasting(0);
            }
        }

    }

    class ChargeAttackGoal extends Goal {
        public ChargeAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            if (Vizier.this.getTarget() != null
                    && !Vizier.this.getMoveControl().hasWanted()
                    && !Vizier.this.isSpellcasting()
                    && Vizier.this.random.nextInt(7) == 0) {
                return Vizier.this.distanceToSqr(Vizier.this.getTarget()) > 4.0D;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return Vizier.this.getMoveControl().hasWanted()
                    && Vizier.this.isCharging()
                    && !Vizier.this.isSpellcasting()
                    && Vizier.this.getTarget() != null
                    && Vizier.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingentity = Vizier.this.getTarget();
            if (livingentity != null) {
                Vec3 vector3d = livingentity.position();
                Vizier.this.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0D);
                Vizier.this.setCharging(true);
                Vizier.this.playSound(ModSounds.VIZIER_CELEBRATE.get(), 1.0F, 1.0F);
            }
        }

        public void stop() {
            Vizier.this.setCharging(false);
        }

        public void tick() {
            LivingEntity livingentity = Vizier.this.getTarget();
            if (livingentity != null) {
                Vizier.this.getLookControl().setLookAt(livingentity.position());
                if (Vizier.this.getBoundingBox().inflate(1.0D).intersects(livingentity.getBoundingBox())) {
                    Vizier.this.doHurtTarget(livingentity);
                    Vizier.this.setCharging(false);
                } else {
                    double d0 = Vizier.this.distanceToSqr(livingentity);
                    if (d0 < 9.0D) {
                        Vec3 vector3d = livingentity.getEyePosition(1.0F);
                        Vizier.this.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0D);
                    }
                }
            }
        }
    }

    class SpikesGoal extends Goal {
        int duration;

        @Override
        public boolean canUse() {
            return Vizier.this.getTarget() != null
                    && !Vizier.this.isCharging()
                    && Vizier.this.getCasting() >= 100
                    && Vizier.this.getCastTimes() == 3;
        }

        public void start() {
            Vizier.this.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 1.0F, 0.5F);
            Vizier.this.setSpellcasting(true);
        }

        public void stop() {
            Vizier.this.setSpellcasting(false);
            Vizier.this.setCastTimes(0);
            Vizier.this.setCasting(0);
            this.duration = 0;
        }

        public void tick() {
            if (!Vizier.this.level.isClientSide) {
                LivingEntity livingentity = Vizier.this.getTarget();
                if (livingentity != null) {
                    Vizier.this.getLookControl().setLookAt(livingentity, Vizier.this.getMaxHeadXRot(), Vizier.this.getMaxHeadYRot());
                    double d0 = Math.min(livingentity.getY(), Vizier.this.getY());
                    double d1 = Math.max(livingentity.getY(), Vizier.this.getY()) + 1.0D;
                    float f = (float) Mth.atan2(livingentity.getZ() - Vizier.this.getZ(), livingentity.getX() - Vizier.this.getX());
                    ++this.duration;
                    if (Vizier.this.airBound > 20){
                        if (!Vizier.this.level.isClientSide) {
                            ServerLevel serverWorld = (ServerLevel) Vizier.this.level;
                            for (int i = 0; i < 5; ++i) {
                                double d3 = serverWorld.random.nextGaussian() * 0.02D;
                                double d4 = serverWorld.random.nextGaussian() * 0.02D;
                                double d2 = serverWorld.random.nextGaussian() * 0.02D;
                                serverWorld.sendParticles(ParticleTypes.ENCHANT, Vizier.this.getRandomX(1.0D), Vizier.this.getRandomY() + 1.0D, Vizier.this.getRandomZ(1.0D), 0, d3, d4, d2, 0.5F);
                            }
                        }
                    }
                    if (this.duration >= 40) {
                        if (Vizier.this.airBound < 20) {
                            for (int l = 0; l < 16; ++l) {
                                double d2 = 1.25D * (double) (l + 1);
                                this.createSpellEntity(Vizier.this.getX() + (double) Mth.cos(f) * d2, Vizier.this.getZ() + (double) Mth.sin(f) * d2, d0, d1, f, l * 2);
                            }
                        } else {
                            for (int j = 0; j < 3; ++j) {
                                SwordProjectile swordProjectile = new SwordProjectile(Vizier.this, Vizier.this.level, Vizier.this.getMainHandItem());
                                double d4 = livingentity.getX() - Vizier.this.getX();
                                double d5 = livingentity.getY(0.3333333333333333D) - swordProjectile.getY();
                                double d2 = livingentity.getZ() - Vizier.this.getZ();
                                double d3 = Mth.sqrt((float) (d4 * d4 + d2 * d2));
                                swordProjectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                                swordProjectile.shoot(d4 + Vizier.this.random.nextGaussian(), d5 + d3 * (double) 0.2F, d2 + Vizier.this.random.nextGaussian(), 1.6F, 0.6F);
                                if (!Vizier.this.getSensing().hasLineOfSight(livingentity)) {
                                    swordProjectile.setNoPhysics(true);
                                }
                                Vizier.this.level.addFreshEntity(swordProjectile);
                            }
                            if (!Vizier.this.isSilent()) {
                                Vizier.this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F);
                            }
                        }
                        this.duration = 0;
                        Vizier.this.setSpellcasting(false);
                        Vizier.this.setCastTimes(0);
                        Vizier.this.setCasting(0);
                        Vizier.this.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 0.5F);
                    }
                } else {
                    stop();
                }
            }
        }

        private void createSpellEntity(double p_190876_1_, double p_190876_3_, double p_190876_5_, double p_190876_7_, float p_190876_9_, int p_190876_10_) {
            BlockPos blockpos = new BlockPos(p_190876_1_, p_190876_7_, p_190876_3_);
            boolean flag = false;
            double d0 = 0.0D;

            do {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = Vizier.this.level.getBlockState(blockpos1);
                if (blockstate.isFaceSturdy(Vizier.this.level, blockpos1, Direction.UP)) {
                    if (!Vizier.this.level.isEmptyBlock(blockpos)) {
                        BlockState blockstate1 = Vizier.this.level.getBlockState(blockpos);
                        VoxelShape voxelshape = blockstate1.getCollisionShape(Vizier.this.level, blockpos);
                        if (!voxelshape.isEmpty()) {
                            d0 = voxelshape.max(Direction.Axis.Y);
                        }
                    }

                    flag = true;
                    break;
                }

                blockpos = blockpos.below();
            } while(blockpos.getY() >= Mth.floor(p_190876_5_) - 1);

            if (flag) {
                Spike spikeEntity = new Spike(Vizier.this.level, p_190876_1_, (double)blockpos.getY() + d0, p_190876_3_, p_190876_9_, p_190876_10_, Vizier.this);
                Vizier.this.level.addFreshEntity(spikeEntity);
            }

        }
    }

    class MoveRandomGoal extends Goal {
        public MoveRandomGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return !Vizier.this.getMoveControl().hasWanted()
                    && Vizier.this.random.nextInt(7) == 0
                    && !Vizier.this.isCharging()
                    && Vizier.this.getTarget() == null;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = Vizier.this.blockPosition();
            if (Vizier.this.getTarget() != null){
                blockpos = Vizier.this.getTarget().blockPosition();
            }

            for(int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(Vizier.this.random.nextInt(8) - 4, Vizier.this.random.nextInt(6) - 2, Vizier.this.random.nextInt(8) - 4);
                if (Vizier.this.level.isEmptyBlock(blockpos1)) {
                    Vizier.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
                    if (Vizier.this.getTarget() == null) {
                        Vizier.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }

    class DoNothingGoal extends Goal {
        public DoNothingGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return Vizier.this.getInvulnerableTicks() > 0;
        }
    }

    public boolean canChangeDimensions() {
        return false;
    }

}
