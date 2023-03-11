package com.Polarice3.Goety.common.entities.boss;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.effects.ModEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.hostile.cultists.Cultist;
import com.Polarice3.Goety.common.entities.hostile.cultists.SpellCastingCultist;
import com.Polarice3.Goety.common.entities.hostile.servants.Malghast;
import com.Polarice3.Goety.common.entities.hostile.servants.SkeletonVillagerServant;
import com.Polarice3.Goety.common.entities.hostile.servants.ZombieVillagerServant;
import com.Polarice3.Goety.common.entities.neutral.IOwned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinBruteServant;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.Polarice3.Goety.common.entities.projectiles.ExplosiveProjectile;
import com.Polarice3.Goety.common.entities.projectiles.FireTornado;
import com.Polarice3.Goety.common.entities.projectiles.NetherMeteor;
import com.Polarice3.Goety.common.entities.util.*;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class Apostle extends SpellCastingCultist implements RangedAttackMob {
    private int f;
    private int hitTimes;
    private int coolDown;
    private int spellCycle;
    private int titleNumber;
    private final Predicate<Entity> ALIVE = Entity::isAlive;
    private boolean roarParticles;
    private boolean fireArrows;
    private boolean regen;
    private MobEffect arrowEffect;
    protected static final EntityDataAccessor<Byte> BOSS_FLAGS = SynchedEntityData.defineId(Apostle.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Float> SPIN = SynchedEntityData.defineId(Apostle.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> HAT = SynchedEntityData.defineId(Apostle.class, EntityDataSerializers.BOOLEAN);
    private final ModServerBossInfo bossInfo = new ModServerBossInfo(this.getUUID(), this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(true).setCreateWorldFog(true);
    private final Predicate<LivingEntity> ZOMBIE_MINIONS = (livingEntity) -> {
        return livingEntity instanceof ZombieVillagerServant || livingEntity instanceof ZPiglinServant;
    };
    private final Predicate<LivingEntity> SKELETON_MINIONS = (livingEntity) -> {
        return livingEntity instanceof SkeletonVillagerServant || livingEntity instanceof Malghast;
    };
    public int deathTime = 0;
    public DamageSource deathBlow = DamageSource.GENERIC;
    private final TargetingConditions zombieCount = TargetingConditions.forCombat().range(64.0D).ignoreInvisibilityTesting().selector(ZOMBIE_MINIONS);
    private final TargetingConditions skeletonCount = TargetingConditions.forCombat().range(64.0D).ignoreInvisibilityTesting().selector(SKELETON_MINIONS);

    public Apostle(EntityType<? extends SpellCastingCultist> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.maxUpStep = 1.0F;
        this.xpReward = 200;
        this.f = 0;
        this.coolDown = 100;
        this.spellCycle = 0;
        this.hitTimes = 0;
        if (this.level.isClientSide){
            Goety.PROXY.addBoss(this);
        }
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SecondPhaseIndicator());
        this.goalSelector.addGoal(2, new BowAttackGoal<>(this));
        this.goalSelector.addGoal(2, new FasterBowAttackGoal<>(this));
        this.goalSelector.addGoal(2, new FastestBowAttackGoal<>(this));
        this.goalSelector.addGoal(3, new CastingSpellGoal());
        this.goalSelector.addGoal(3, new FireballSpellGoal());
        this.goalSelector.addGoal(3, new ZombieSpellGoal());
        this.goalSelector.addGoal(3, new FireRainSpellGoal());
        this.goalSelector.addGoal(3, new SkeletonSpellGoal());
        this.goalSelector.addGoal(3, new FireTornadoSpellGoal());
        this.goalSelector.addGoal(3, new RoarSpellGoal());
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ApostleHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    public AttributeSupplier.Builder getConfiguredAttributes(){
        return setCustomAttributes();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BOSS_FLAGS, (byte)0);
        this.entityData.define(SPIN, 0.0F);
        this.entityData.define(HAT, true);
    }

    private boolean getBossFlag(int mask) {
        int i = this.entityData.get(BOSS_FLAGS);
        return (i & mask) != 0;
    }

    private void setBossFlag(int mask, boolean value) {
        int i = this.entityData.get(BOSS_FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(BOSS_FLAGS, (byte)(i & 255));
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.APOSTLE_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.APOSTLE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.APOSTLE_PREDEATH.get();
    }

    protected SoundEvent getTrueDeathSound() {
        return ModSounds.APOSTLE_DEATH.get();
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public boolean canBeAffected(MobEffectInstance pPotioneffect) {
        return pPotioneffect.getEffect() != ModEffects.BURN_HEX.get() && pPotioneffect.getEffect() != MobEffects.WITHER && super.canBeAffected(pPotioneffect);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("firing", this.f);
        pCompound.putInt("coolDown", this.coolDown);
        pCompound.putInt("spellCycle", this.spellCycle);
        pCompound.putInt("hitTimes", this.hitTimes);
        pCompound.putInt("titleNumber", this.titleNumber);
        pCompound.putBoolean("fireArrows", this.fireArrows);
        pCompound.putBoolean("secondPhase", this.isSecondPhase());
        pCompound.putBoolean("settingSecondPhase", this.isSettingupSecond());
        pCompound.putBoolean("regen", this.regen);
        pCompound.putBoolean("hat", this.hasHat());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.f = pCompound.getInt("firing");
        this.coolDown = pCompound.getInt("coolDown");
        this.spellCycle = pCompound.getInt("spellCycle");
        this.hitTimes = pCompound.getInt("hitTimes");
        this.titleNumber = pCompound.getInt("titleNumber");
        this.fireArrows = pCompound.getBoolean("fireArrows");
        this.regen = pCompound.getBoolean("regen");
        this.setHat(pCompound.getBoolean("hat"));
        this.setTitleNumber(this.titleNumber);
        this.TitleEffect(this.titleNumber);
        this.setSecondPhase(pCompound.getBoolean("secondPhase"));
        this.setSettingupSecond(pCompound.getBoolean("settingSecondPhase"));
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
        this.bossInfo.setId(this.getUUID());
    }

    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
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

    protected boolean isAffectedByFluids() {
        return false;
    }

    public boolean isAlliedTo(Entity entityIn) {
        if (entityIn instanceof WitherBoss) {
            return this.getTeam() == null && entityIn.getTeam() == null;
        } else return super.isAlliedTo(entityIn);
    }

    public void die(DamageSource cause) {
        if (this.deathTime > 0) {
            super.die(cause);
        }
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 1){
            for (AbstractTrap trapEntity : this.level.getEntitiesOfClass(AbstractTrap.class, this.getBoundingBox().inflate(64))) {
                if (trapEntity.getOwner() == this) {
                    trapEntity.discard();
                }
            }
            for (FireTornado fireTornadoEntity : this.level.getEntitiesOfClass(FireTornado.class, this.getBoundingBox().inflate(64))) {
                fireTornadoEntity.discard();
            }
            this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
        if (MainConfig.FancierApostleDeath.get()) {
            this.setNoGravity(true);
            if (this.deathTime < 180) {
                if (this.deathTime > 20) {
                    this.move(MoverType.SELF, new Vec3(0.0D, 0.1D, 0.0D));
                }
                this.level.explode(this, this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), 0.0F, Explosion.BlockInteraction.NONE);
            } else if (this.deathTime != 200) {
                this.move(MoverType.SELF, new Vec3(0.0D, 0.0D, 0.0D));
            }
            if (this.deathTime >= 200) {
                this.move(MoverType.SELF, new Vec3(0.0D, -4.0D, 0.0D));
                if (this.isOnGround() || this.getY() <= 0) {
                    if (!this.level.isClientSide) {
                        ServerLevel ServerLevel = (ServerLevel) this.level;
                        if (ServerLevel.getLevelData().isThundering()) {
                            ServerLevel.setWeatherParameters(6000, 0, false, false);
                        }
                        for (int k = 0; k < 200; ++k) {
                            float f2 = random.nextFloat() * 4.0F;
                            float f1 = random.nextFloat() * ((float) Math.PI * 2F);
                            double d1 = Mth.cos(f1) * f2;
                            double d2 = 0.01D + random.nextDouble() * 0.5D;
                            double d3 = Mth.sin(f1) * f2;
                            ServerLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX() + d1 * 0.1D, this.getY() + 0.3D, this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.5F);
                            ServerLevel.sendParticles(ParticleTypes.FLAME, this.getX() + d1 * 0.1D, this.getY() + 0.3D, this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.5F);
                        }
                        ServerLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 0, 1.0F, 0.0F, 0.0F, 0.5F);
                    }
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F);
                    this.playSound(this.getTrueDeathSound(), 5.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    this.die(this.deathBlow);
                    this.remove(RemovalReason.KILLED);
                }
            }
        } else {
            this.move(MoverType.SELF, new Vec3(0.0D, 0.0D, 0.0D));
            if (this.deathTime == 1){
                if (!this.level.isClientSide) {
                    ServerLevel ServerLevel = (ServerLevel) this.level;
                    if (ServerLevel.getLevelData().isThundering()) {
                        ServerLevel.setWeatherParameters(6000, 0, false, false);
                    }
                    for (int k = 0; k < 200; ++k) {
                        float f2 = random.nextFloat() * 4.0F;
                        float f1 = random.nextFloat() * ((float) Math.PI * 2F);
                        double d1 = Mth.cos(f1) * f2;
                        double d2 = 0.01D + random.nextDouble() * 0.5D;
                        double d3 = Mth.sin(f1) * f2;
                        ServerLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX() + d1 * 0.1D, this.getY() + 0.3D, this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.5F);
                        ServerLevel.sendParticles(ParticleTypes.FLAME, this.getX() + d1 * 0.1D, this.getY() + 0.3D, this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.5F);
                    }
                    for (int l = 0; l < 16; ++l){
                        ServerLevel.sendParticles(ParticleTypes.FLAME, this.getRandomX(1.0F), this.getRandomY() - 0.25F, this.getRandomZ(1.0F), 1, 0, 0, 0, 0);
                    }
                }
                this.die(this.deathBlow);
                this.playSound(this.getTrueDeathSound(), 5.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }
            if (this.deathTime >= 30) {
                this.remove(RemovalReason.KILLED);
            }
        }

    }

    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public void remove(Entity.RemovalReason p_146834_) {
        if (this.level.isClientSide) {
            Goety.PROXY.removeBoss(this);
        }
        super.remove(p_146834_);
    }

    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        ItemEntity itementity = this.spawnAtLocation(ModItems.UNHOLY_BLOOD.get());
        if (itementity != null) {
            itementity.setExtendedLifetime();
        }

    }

    protected float getDamageAfterMagicAbsorb(DamageSource source, float damage) {
        damage = super.getDamageAfterMagicAbsorb(source, damage);
        if (source.getEntity() == this) {
            damage = 0.0F;
        }

        if (source.getDirectEntity() instanceof ExplosiveProjectile) {
            damage = (float)((double)damage * 0.15D);
        }

        if (this.level.getDifficulty() == Difficulty.HARD){
            if (source.isMagic()){
                damage = (float)((double)damage * 0.15D);
            }
        }

        return damage;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.populateDefaultEquipmentSlots(difficultyIn);
        this.populateDefaultEquipmentEnchantments(worldIn.getRandom(), difficultyIn);
        if (!this.hasCustomName()){
            int random = this.random.nextInt(18);
            int random2 = this.random.nextInt(12);
            this.setTitleNumber(random2);
            this.TitleEffect(random2);
            Component component = Component.translatable("name.goety.apostle." + random);
            Component component1 = Component.translatable("title.goety." + random2);
            if (random2 == 1){
                this.setHealth(this.getMaxHealth());
            }
            this.setCustomName(Component.translatable(component.getString() + " " + "The" + " " + component1.getString()));
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public void setTitleNumber(Integer integer){
        this.titleNumber = integer;
    }

    public void TitleEffect(Integer integer){
        switch (integer){
            case 0:
                this.setRegen(true);
                break;
            case 1:
                this.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, Integer.MAX_VALUE, 1, false, false));
                break;
            case 2:
                this.setArrowEffect(MobEffects.POISON);
                break;
            case 3:
                this.setArrowEffect(MobEffects.WITHER);
                break;
            case 4:
                this.setArrowEffect(MobEffects.BLINDNESS);
                break;
            case 5:
                this.setArrowEffect(MobEffects.WEAKNESS);
                break;
            case 6:
                this.setFireArrow(true);
                break;
            case 7:
                this.setArrowEffect(MobEffects.HUNGER);
                break;
            case 8:
                this.setArrowEffect(MobEffects.MOVEMENT_SLOWDOWN);
                break;
            case 9:
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Integer.MAX_VALUE, 1, false, false));
                break;
            case 10:
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false, false));
                break;
            case 11:
                this.setArrowEffect(ModEffects.SAPPED.get());
                break;
        }
    }

    public void setRegen(boolean regen){
        this.regen = regen;
    }

    public boolean Regen(){
        return this.regen;
    }

    public void setFireArrow(boolean fireArrow){
        this.fireArrows = fireArrow;
    }

    public boolean getFireArrow(){
        return this.fireArrows;
    }

    public void setArrowEffect(MobEffect effect){
        this.arrowEffect = effect;
    }

    public MobEffect getArrowEffect(){
        return this.arrowEffect;
    }

    public void setSecondPhase(boolean secondPhase){
        this.setBossFlag(1, secondPhase);
    }

    public boolean isSecondPhase(){
        return this.getBossFlag(1);
    }

    public void setSettingupSecond(boolean settingupSecond){
        this.setBossFlag(2, settingupSecond);
    }

    public boolean isSettingupSecond(){
        return this.getBossFlag(2);
    }

    public void setCasting(boolean casting){
        this.setBossFlag(4, casting);
    }

    public boolean isCasting(){
        return this.getBossFlag(4);
    }

    public void setSpin(float spin){
        this.entityData.set(SPIN, spin);
    }

    public float getSpin(){
        return this.entityData.get(SPIN);
    }

    public void setHat(boolean hat){
        this.entityData.set(HAT, hat);
    }

    public boolean hasHat(){
        return this.entityData.get(HAT);
    }

    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @OnlyIn(Dist.CLIENT)
    public ArmPose getArmPose() {
        if (this.isDeadOrDying()){
            return ArmPose.DYING;
        } else if (this.getMainHandItem().getItem() instanceof BowItem) {
            if (this.isAggressive() && !this.isSpellcasting() && !this.isSettingupSecond()){
                return ArmPose.BOW_AND_ARROW;
            } else if (this.isSpellcasting()){
                return ArmPose.SPELL_AND_WEAPON;
            } else if (this.isSettingupSecond()){
                return ArmPose.SPELL_AND_WEAPON;
            } else {
                return ArmPose.CROSSED;
            }
        } else {
            return ArmPose.CROSSED;
        }
    }

    public boolean isFiring(){
        return this.roarParticles;
    }

    public void setFiring(boolean firing){
        this.roarParticles = firing;
    }

    public int hitTimeTeleport(){
        return this.isSecondPhase() ? 2 : 4;
    }

    public void resetHitTime(){
        this.hitTimes = 0;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        LivingEntity livingEntity = this.getTarget();
        if (!this.level.isClientSide) {
            if (livingEntity != null && pSource.getEntity() instanceof LivingEntity) {
                ++this.hitTimes;
            }
        }

        if (pSource == DamageSource.LIGHTNING_BOLT || pSource == DamageSource.FALL || pSource == DamageSource.IN_WALL){
            return false;
        }

        if (pSource.getDirectEntity() instanceof AbstractArrow && ((AbstractArrow) pSource.getDirectEntity()).getOwner() == this){
            return false;
        }

        if (this.isSettingupSecond()){
            return false;
        }

        if (pSource.getDirectEntity() instanceof NetherMeteor || pSource.getEntity() instanceof NetherMeteor){
            return false;
        }

        float trueAmount = this.level.dimension() == Level.NETHER ? pAmount/4 : pAmount;

        if (pSource == DamageSource.OUT_OF_WORLD){
            this.discard();
        }

        if (this.hitTimes >= this.hitTimeTeleport()){
            trueAmount = trueAmount/2;
            this.teleport();
        }

        if (!this.level.getNearbyPlayers(TargetingConditions.forCombat().selector(MobUtil.NO_CREATIVE_OR_SPECTATOR), this, this.getBoundingBox().inflate(32)).isEmpty()){
            if (!(pSource.getEntity() instanceof Player)){
                trueAmount = trueAmount/2;
            }
        }
        if (this.isDeadOrDying()) {
            this.deathBlow = pSource;
        }

        return super.hurt(pSource, Math.min(trueAmount, (float) AttributesConfig.ApostleDamageCap.get()));
    }

    @Override
    public void kill() {
        this.remove(RemovalReason.KILLED);
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource damageSource) {
        return false;
    }

    protected void teleport() {
        if (!this.level.isClientSide() && this.isAlive() && !this.isSettingupSecond() && !this.isCasting()) {
            for(int i = 0; i < 128; ++i) {
                double d3 = this.getX() + (this.getRandom().nextDouble() - 0.5D) * 32.0D;
                double d4 = this.getY();
                if (this.getTarget() != null){
                    d4 = this.getTarget().getY();
                }
                double d5 = this.getZ() + (this.getRandom().nextDouble() - 0.5D) * 32.0D;
                if (this.randomTeleport(d3, d4, d5, false)) {
                    this.teleportHits();
                    this.resetHitTime();
                    break;
                }
            }
        }
    }

    private void teleportTowards(Entity entity) {
        if (!this.level.isClientSide() && this.isAlive() && !this.isSettingupSecond()) {
            for(int i = 0; i < 128; ++i) {
                Vec3 vector3d = new Vec3(this.getX() - entity.getX(), this.getY(0.5D) - entity.getEyeY(), this.getZ() - entity.getZ());
                vector3d = vector3d.normalize();
                double d0 = 16.0D;
                double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.x * d0;
                double d2 = this.getY() + (double)(this.random.nextInt(16) - 8) - vector3d.y * d0;
                double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.z * d0;
                if (this.randomTeleport(d1, d2, d3, false)) {
                    this.teleportHits();
                    break;
                }
            }
        }
    }

    public void teleportHits(){
        this.level.broadcastEntityEvent(this, (byte) 100);
        if (!this.isSilent()) {
            this.level.playSound((Player) null, this.xo, this.yo, this.zo, ModSounds.APOSTLE_TELEPORT.get(), this.getSoundSource(), 1.0F, 1.0F);
            this.playSound(ModSounds.APOSTLE_TELEPORT.get(), 1.0F, 1.0F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte pId) {
        if (pId == 100){
            int i = 128;

            for(int j = 0; j < i; ++j) {
                double d0 = (double)j / (i - 1);
                float f = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (this.random.nextFloat() - 0.5F) * 0.2F;
                double d1 = Mth.lerp(d0, this.xo, this.getX()) + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth() * 2.0D;
                double d2 = Mth.lerp(d0, this.yo, this.getY()) + this.random.nextDouble() * (double)this.getBbHeight();
                double d3 = Mth.lerp(d0, this.zo, this.getZ()) + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth() * 2.0D;
                this.level.addParticle(ParticleTypes.SMOKE, d1, d2, d3, (double)f, (double)f1, (double)f2);
            }
        } else {
            super.handleEntityEvent(pId);
        }
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return ModSounds.APOSTLE_CAST_SPELL.get();
    }

    protected boolean canRide(Entity pEntity) {
        return false;
    }

    public void aiStep() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL){
            this.remove(RemovalReason.DISCARDED);
        }
        if (this.level.isClientSide){
            if (this.isAlive()) {
                if (this.getSpin() < 3.14F) {
                    this.setSpin(this.getSpin() + 0.01F);
                } else {
                    this.setSpin(-3.14F);
                }
            }
            if (this.isSecondPhase() && this.getHealth() < this.getMaxHealth()/2 && this.hasHat()){
                this.setHat(false);
            }
        }
/*        if (this.tickCount % 100 == 0){
            MobUtil.secretConversion(this);
        }*/
        if (!this.level.isClientSide){
            ServerLevel ServerLevel = (ServerLevel) this.level;
/*            if (MainConfig.WitchConversion.get()) {
                for (WitchEntity witch : this.level.getEntitiesOfClass(WitchEntity.class, this.getBoundingBox().inflate(8.0D))) {
                    BeldamEntity beldam = witch.convertTo(ModEntityType.BELDAM.get(), true);
                    if (beldam != null) {
                        beldam.finalizeSpawn(ServerLevel, ServerLevel.getCurrentDifficultyAt(witch.blockPosition()), MobSpawnType.CONVERSION, null, null);
                        ForgeEventFactory.onLivingConvert(witch, beldam);
                    }
                }
            }*/
        }
        if (this.isSettingupSecond()){
            this.heal(1.0F);
            for (Entity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3.0D), ALIVE)) {
                if (!entity.isAlliedTo(this)) {
                    this.barrier(entity, this);
                }
            }

            if (!this.level.isClientSide){
                this.serverAiStep();
                this.resetHitTime();
                ServerLevel ServerLevel = (ServerLevel) this.level;
                for(int i = 0; i < 40; ++i) {
                    double d0 = this.random.nextGaussian() * 0.2D;
                    double d1 = this.random.nextGaussian() * 0.2D;
                    double d2 = this.random.nextGaussian() * 0.2D;
                    ServerLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0, d0, d1, d2, 0.5F);
                }
            }
            if (this.getHealth() >= this.getMaxHealth()){
                if (!this.level.isClientSide){
                    ServerLevel ServerLevel = (ServerLevel) this.level;
                    if (!ServerLevel.isThundering()) {
                        ServerLevel.setWeatherParameters(0, 6000, true, true);
                    }
                    for(int k = 0; k < 60; ++k) {
                        float f2 = random.nextFloat() * 4.0F;
                        float f1 = random.nextFloat() * ((float)Math.PI * 2F);
                        double d1 = Mth.cos(f1) * f2;
                        double d2 = 0.01D + random.nextDouble() * 0.5D;
                        double d3 = Mth.sin(f1) * f2;
                        ServerLevel.sendParticles(ParticleTypes.FLAME, this.getX() + d1 * 0.1D, this.getY() + 0.3D, this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.25F);
                    }
                }
                this.setSettingupSecond(false);
                this.setSecondPhase(true);
            }
        } else {
            super.aiStep();
        }
        LivingEntity target = this.getTarget();
        if (this.getMainHandItem().isEmpty() && this.isAlive()){
            this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOW));
        }
        if (this.isSecondPhase()) {
            if (this.Regen()) {
                if (this.tickCount % 10 == 0) {
                    if (this.getHealth() < this.getMaxHealth()) {
                        this.heal(1.0F);
                    }
                }
            } else {
                if (this.tickCount % 20 == 0) {
                    if (this.getHealth() < this.getMaxHealth()) {
                        this.heal(1.0F);
                    }
                }
            }
            if (this.tickCount % 100 == 0 && !this.isDeadOrDying()) {
                if (!this.level.isClientSide){
                    ServerLevel ServerLevel = (ServerLevel) this.level;
                    ServerLevel.setWeatherParameters(0, 6000, true, true);
                }
            }
            if (this.coolDown < this.coolDownLimit()) {
                ++this.coolDown;
            } else {
                this.spellCycle = 1;
            }
            if (!this.level.isClientSide) {
                if (this.getHealth() <= this.getMaxHealth()/8){
                    if (this.tickCount % 100 == 0){
                        this.teleport();
                    }
                }
            }
            if (this.level.dimension() != Level.NETHER){
                if (this.tickCount % 20 == 0) {
                    BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(this.getRandomX(0.2), this.getY(), this.getRandomZ(0.2));

                    while (blockPos.getY() < this.getY() + 64.0D && !this.level.getBlockState(blockPos).isSolidRender(this.level, blockPos)) {
                        blockPos.move(Direction.UP);
                    }
                    if (blockPos.getY() > this.getY() + 32.0D) {
                        int range = this.getHealth() < this.getMaxHealth()/2 ? 450 : 900;
                        int trueRange = this.getHealth() < this.getHealth()/4 ? range/2 : range;
                        RandomSource random = this.level.random;
                        double d = (random.nextBoolean() ? 1 : -1);
                        double e = (random.nextBoolean() ? 1 : -1);
                        double d2 = (random.nextInt(trueRange) * d);
                        double d3 = -900.0D;
                        double d4 = (random.nextInt(trueRange) * e);
                        NetherMeteor fireball = new NetherMeteor(this.level, this, d2, d3, d4);
                        fireball.setDangerous(ForgeEventFactory.getMobGriefingEvent(this.level, this) && MainConfig.ApocalypseMode.get());
                        fireball.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                        this.level.addFreshEntity(fireball);
                    }
                }
            }
        } else {
            if (this.Regen()) {
                if (this.tickCount % 20 == 0) {
                    if (this.getHealth() < this.getMaxHealth()) {
                        this.heal(1.0F);
                    }
                }
            } else {
                if (this.tickCount % 40 == 0) {
                    if (this.getHealth() < this.getMaxHealth()) {
                        this.heal(1.0F);
                    }
                }
            }
            if (this.coolDown < this.coolDownLimit()) {
                ++this.coolDown;
            } else {
                this.spellCycle = 0;
            }
        }
        if (this.spellCycle == 1){
            if (this.tickCount % 100 == 0) {
                if (this.level.random.nextBoolean()) {
                    this.spellCycle = 2;
                } else {
                    this.spellCycle = 3;
                }
            }
        }
        for (LivingEntity living : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(32))){
            if (!(living instanceof Cultist) && !(living instanceof Owned && ((Owned) living).getTrueOwner() == this)){
                if (living.isInWater()){
                    living.hurt(DamageSource.HOT_FLOOR, 1.0F);
                }
            }
            if (living instanceof Player player){
                player.getAbilities().flying &= player.isCreative();
            }
        }
        if (target == null){
            this.resetHitTime();
            for (Player player : this.level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(64), EntitySelector.NO_CREATIVE_OR_SPECTATOR)){
                this.setTarget(player);
            }
        } else {
            if (target instanceof IOwned && ((IOwned) target).getTrueOwner() != null){
                IOwned owned = (IOwned) target;
                this.setTarget(owned.getTrueOwner());
            }
            int i = this.level.getNearbyEntities(Owned.class, this.zombieCount, this, this.getBoundingBox().inflate(64.0D)).size();
            if (this.tickCount % 100 == 0 && i < 16 && this.level.random.nextFloat() <= 0.25F){
                if (!this.level.isClientSide){
                    ServerLevel ServerLevel = (ServerLevel) this.level;
                    RandomSource r = this.level.random;
                    int numbers = this.isSecondPhase() ? 4 : 2;
                    if (this.level.dimension() != Level.NETHER) {
                        for (int p = 0; p < r.nextInt(numbers) + 1; ++p) {
                            int k = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                            int l = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                            BlockPos.MutableBlockPos blockpos$mutable = this.blockPosition().mutable().move(k, 0, l);
                            blockpos$mutable.setX(blockpos$mutable.getX() + r.nextInt(5) - r.nextInt(5));
                            blockpos$mutable.setY((int) BlockFinder.moveDownToGround(this));
                            blockpos$mutable.setZ(blockpos$mutable.getZ() + r.nextInt(5) - r.nextInt(5));
                            Owned summonedentity;
                            if (!this.isSecondPhase()) {
                                summonedentity = new ZombieVillagerServant(ModEntityType.ZOMBIE_VILLAGER_SERVANT.get(), this.level);
                            } else {
                                summonedentity = new ZPiglinServant(ModEntityType.ZPIGLIN_SERVANT.get(), this.level);
                            }
                            summonedentity.moveTo(blockpos$mutable, 0.0F, 0.0F);
                            summonedentity.setTrueOwner(this);
                            summonedentity.setLimitedLife(60 * (90 + this.level.random.nextInt(180)));
                            summonedentity.finalizeSpawn(ServerLevel, this.level.getCurrentDifficultyAt(blockpos$mutable), MobSpawnType.MOB_SUMMONED, null, null);
                            summonedentity.setTarget(this.getTarget());
                            SummonCircleEntity summonCircleEntity = new SummonCircleEntity(this.level, blockpos$mutable, summonedentity, false, this);
                            this.level.addFreshEntity(summonCircleEntity);
                        }
                    } else {
                        for (ZombifiedPiglin zombifiedPiglin : this.level.getEntitiesOfClass(ZombifiedPiglin.class, this.getBoundingBox().inflate(16))){
                            if (zombifiedPiglin.getTarget() != this.getTarget()){
                                zombifiedPiglin.setTarget(this.getTarget());
                            }
                        }
                        for (int p = 0; p < r.nextInt(numbers) + 1; ++p) {
                            int k = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                            int l = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                            BlockPos.MutableBlockPos blockpos$mutable = this.blockPosition().mutable().move(k, 0, l);
                            blockpos$mutable.setX(blockpos$mutable.getX() + r.nextInt(5) - r.nextInt(5));
                            blockpos$mutable.setY((int) BlockFinder.moveDownToGround(this));
                            blockpos$mutable.setZ(blockpos$mutable.getZ() + r.nextInt(5) - r.nextInt(5));
                            ZPiglinServant summonedentity = new ZPiglinServant(ModEntityType.ZPIGLIN_SERVANT.get(), this.level);
                            summonedentity.moveTo(blockpos$mutable, 0.0F, 0.0F);
                            summonedentity.setTrueOwner(this);
                            summonedentity.setLimitedLife(60 * (90 + this.level.random.nextInt(180)));
                            summonedentity.finalizeSpawn(ServerLevel, this.level.getCurrentDifficultyAt(blockpos$mutable), MobSpawnType.MOB_SUMMONED, null, null);
                            summonedentity.setTarget(this.getTarget());
                            SummonCircleEntity summonCircleEntity = new SummonCircleEntity(this.level, blockpos$mutable, summonedentity, false, this);
                            this.level.addFreshEntity(summonCircleEntity);
                        }
                    }
                }
            }
            if (this.isSecondPhase()) {
                if (MobUtil.isInRain(target)) {
                    int count = 100 * (this.random.nextInt(5) + 1);
                    if (this.tickCount % count == 0) {
                        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(target.getX(), target.getY(), target.getZ());

                        while (blockpos$mutable.getY() > 0 && !this.level.getBlockState(blockpos$mutable).getMaterial().blocksMotion()) {
                            blockpos$mutable.move(Direction.DOWN);
                        }

                        LightningTrap lightningTrap = new LightningTrap(this.level, blockpos$mutable.getX(), blockpos$mutable.getY() + 1, blockpos$mutable.getZ());
                        lightningTrap.setOwner(this);
                        lightningTrap.setDuration(50);
                        this.level.addFreshEntity(lightningTrap);
                    }
                }
            }
            if ((target.distanceToSqr(this) > 1024 || !this.getSensing().hasLineOfSight(target)) && target.isOnGround() && !this.isSettingupSecond()){
                this.teleportTowards(target);
            }
        }
        if (this.isFiring()) {
            ++this.f;
            if (this.f % 2 == 0 && this.f < 10) {
                for (Entity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D), ALIVE)) {
                    if (!(entity instanceof Cultist) && !(entity instanceof Owned && ((Owned) entity).getTrueOwner() == this)) {
                        entity.hurt(DamageSource.mobAttack(this), 6.0F);
                        this.launch(entity, this);
                    }
                }
                if (!this.level.isClientSide){
                    this.serverRoarParticles();
                }

            }
            if (this.f >= 10){
                if (this.teleportChance()) {
                    this.teleport();
                }
                this.setFiring(false);
                this.f = 0;
            }
        }
        if (this.isInWater() || this.isInLava() || this.isInWall()){
            this.teleport();
        }
        if (this.level.dimension() == Level.NETHER){
            if (target != null){
                target.addEffect(new MobEffectInstance(ModEffects.BURN_HEX.get(), 100));
            }
        }
    }

    private void serverRoarParticles(){
        ServerLevel ServerLevel = (ServerLevel) this.level;
        ServerLevel.sendParticles(ParticleTypes.FLASH, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
        Vec3 vector3d = this.getBoundingBox().getCenter();
        for(int i = 0; i < 40; ++i) {
            double d0 = this.random.nextGaussian() * 0.2D;
            double d1 = this.random.nextGaussian() * 0.2D;
            double d2 = this.random.nextGaussian() * 0.2D;
            ServerLevel.sendParticles(ParticleTypes.POOF, vector3d.x, vector3d.y, vector3d.z, 0, d0, d1, d2, 0.5F);
        }
    }

    private void launch(Entity p_213688_1_, LivingEntity livingEntity) {
        double d0 = p_213688_1_.getX() - livingEntity.getX();
        double d1 = p_213688_1_.getZ() - livingEntity.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
        MobUtil.push(p_213688_1_, d0 / d2 * 6.0D, 0.4D, d1 / d2 * 6.0D);
    }

    private void barrier(Entity p_213688_1_, LivingEntity livingEntity) {
        double d0 = p_213688_1_.getX() - livingEntity.getX();
        double d1 = p_213688_1_.getZ() - livingEntity.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
        MobUtil.push(p_213688_1_, d0 / d2 * 2.0D, 0.1D, d1 / d2 * 2.0D);
    }

    public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
        ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof BowItem)));
        AbstractArrow abstractarrowentity = this.getArrow(itemstack, pDistanceFactor * AttributesConfig.ApostleBowDamage.get());
        if (this.getMainHandItem().getItem() instanceof BowItem) {
            abstractarrowentity = ((BowItem) this.getMainHandItem().getItem()).customArrow(abstractarrowentity);
        }
        double d0 = pTarget.getX() - this.getX();
        double d1 = pTarget.getY(0.3333333333333333D) - abstractarrowentity.getY();
        double d2 = pTarget.getZ() - this.getZ();
        double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
        abstractarrowentity.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(abstractarrowentity);
    }

    public AbstractArrow getArrow(ItemStack pArrowStack, float pDistanceFactor) {
        AbstractArrow abstractarrowentity = ProjectileUtil.getMobArrow(this, pArrowStack, pDistanceFactor);
        if (this.getArrowEffect() != null){
            int amp;
            if (this.isSecondPhase()){
                amp = 1;
            } else {
                amp = 0;
            }
            ((Arrow)abstractarrowentity).addEffect(new MobEffectInstance(this.getArrowEffect(), 200, amp));
        }
        if (this.getFireArrow()){
            abstractarrowentity.setRemainingFireTicks(100);
        }
        return abstractarrowentity;
    }

    public boolean canFireProjectileWeapon(ProjectileWeaponItem p_230280_1_) {
        return p_230280_1_ instanceof BowItem;
    }

    public ItemStack getProjectile(ItemStack shootable) {
        if (shootable.getItem() instanceof ProjectileWeaponItem) {
            Predicate<ItemStack> predicate = ((ProjectileWeaponItem)shootable.getItem()).getSupportedHeldProjectiles();
            ItemStack itemstack = ProjectileWeaponItem.getHeldProjectile(this, predicate);
            return itemstack.isEmpty() ? new ItemStack(Items.ARROW) : itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public boolean teleportChance(){
        return this.level.random.nextFloat() <= 0.25F;
    }

    public int spellStart(){
        return this.isSecondPhase() ? 20 : 45;
    }

    public int coolDownLimit(){
        return this.isSecondPhase() ? 25 : 50;
    }

    public void postSpellCast(){
        if (this.teleportChance()) {
            this.teleport();
        }
        this.coolDown = 0;
        this.spellCycle = 0;
    }

    class CastingSpellGoal extends CastingASpellGoal {
        private CastingSpellGoal() {
        }

        public void tick() {
            if (Apostle.this.getTarget() != null) {
                Apostle.this.getLookControl().setLookAt(Apostle.this.getTarget(), (float) Apostle.this.getMaxHeadYRot(), (float) Apostle.this.getMaxHeadXRot());
            }
        }
    }

    abstract class CastingGoal extends UseSpellGoal {

        public void start() {
            super.start();
            Apostle.this.setCasting(true);
        }

        public void stop() {
            super.stop();
            Apostle.this.setCasting(false);
        }

        protected int getCastingInterval() {
            return 0;
        }

    }

    class FireballSpellGoal extends CastingGoal {
        private FireballSpellGoal() {
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = Apostle.this.getTarget();
            if (!super.canUse()) {
                return false;
            } else if (livingentity == null) {
                return false;
            } else {
                return Apostle.this.spellCycle == 0
                        && !Apostle.this.isSettingupSecond()
                        && !Apostle.this.isSecondPhase()
                        && Apostle.this.getSensing().hasLineOfSight(livingentity);
            }
        }

        protected int getCastingTime() {
            return 20;
        }

        public void castSpell() {
            LivingEntity livingentity = Apostle.this.getTarget();
            if (livingentity != null) {
                double d0 = Apostle.this.distanceToSqr(livingentity);
                float f = Mth.sqrt(Mth.sqrt((float) d0)) * 0.5F;
                double d1 = livingentity.getX() - Apostle.this.getX();
                double d2 = livingentity.getY(0.5D) - Apostle.this.getY(0.5D);
                double d3 = livingentity.getZ() - Apostle.this.getZ();
                LargeFireball fireballEntity = new LargeFireball(Apostle.this.level, Apostle.this, d1, d2, d3, 1);
                fireballEntity.setPos(fireballEntity.getX(), Apostle.this.getY(0.5), fireballEntity.getZ());
                Apostle.this.level.addFreshEntity(fireballEntity);
                if (!Apostle.this.isSilent()) {
                    Apostle.this.level.levelEvent(null, 1016, Apostle.this.blockPosition(), 0);
                }
                if (Apostle.this.teleportChance()) {
                    Apostle.this.teleport();
                }
                Apostle.this.coolDown = 0;
                ++Apostle.this.spellCycle;
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.APOSTLE_PREPARE_SPELL.get();
        }

        @Override
        protected SpellType getSpellType() {
            return SpellType.FIRE;
        }
    }

    class ZombieSpellGoal extends CastingGoal {
        private ZombieSpellGoal() {
        }

        @Override
        public boolean canUse() {
            int i = Apostle.this.level.getNearbyEntities(Owned.class, Apostle.this.zombieCount, Apostle.this, Apostle.this.getBoundingBox().inflate(64.0D)).size();
            if (!super.canUse()) {
                return false;
            }  else {
                int cool = Apostle.this.spellStart();
                return Apostle.this.coolDown >= cool
                        && Apostle.this.spellCycle == 2
                        && !Apostle.this.isSettingupSecond()
                        && i < 4;
            }
        }

        protected int getCastingTime() {
            return 20;
        }

        public void castSpell() {
            if (!Apostle.this.level.isClientSide) {
                ServerLevel ServerLevel = (ServerLevel) Apostle.this.level;
                LivingEntity livingentity = Apostle.this.getTarget();
                RandomSource r = Apostle.this.random;
                if (livingentity != null) {
                    BlockPos blockpos = Apostle.this.blockPosition();
                    if (!Apostle.this.isSecondPhase()){
                        if (r.nextBoolean()) {
                            ZPiglinServant summonedentity = new ZPiglinServant(ModEntityType.ZPIGLIN_SERVANT.get(), Apostle.this.level);
                            summonedentity.moveTo(blockpos, 0.0F, 0.0F);
                            summonedentity.setOwnerId(Apostle.this.getUUID());
                            summonedentity.setLimitedLife(60 * (90 + Apostle.this.level.random.nextInt(180)));
                            for(EquipmentSlot equipmentslottype : EquipmentSlot.values()) {
                                if (equipmentslottype.getType() == EquipmentSlot.Type.ARMOR) {
                                    ItemStack itemstack = summonedentity.getItemBySlot(equipmentslottype);
                                    if (itemstack.isEmpty()) {
                                        Item item = getEquipmentForSlot(equipmentslottype, 1);
                                        if (item != null) {
                                            summonedentity.setItemSlot(equipmentslottype, new ItemStack(item));
                                        }
                                    }
                                }
                                summonedentity.setDropChance(equipmentslottype, 0.0F);
                            }
                            summonedentity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Integer.MAX_VALUE, 1, false, false));
                            summonedentity.finalizeSpawn(ServerLevel, Apostle.this.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                            summonedentity.setTarget(livingentity);
                            SummonCircleEntity summonCircleEntity = new SummonCircleEntity(Apostle.this.level, blockpos, summonedentity, false, Apostle.this);
                            Apostle.this.level.addFreshEntity(summonCircleEntity);
                        } else {
                            for (int p = 0; p < 3 + r.nextInt(6); ++p) {
                                int k = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                                int l = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                                BlockPos.MutableBlockPos blockpos$mutable = Apostle.this.blockPosition().mutable().move(k, 0, l);
                                blockpos$mutable.setX(blockpos$mutable.getX() + r.nextInt(5) - r.nextInt(5));
                                blockpos$mutable.setY((int) BlockFinder.moveDownToGround(Apostle.this));
                                blockpos$mutable.setZ(blockpos$mutable.getZ() + r.nextInt(5) - r.nextInt(5));
                                ZPiglinServant summonedentity = new ZPiglinServant(ModEntityType.ZPIGLIN_SERVANT.get(), Apostle.this.level);
                                summonedentity.moveTo(blockpos$mutable, 0.0F, 0.0F);
                                summonedentity.setTrueOwner(Apostle.this);
                                summonedentity.setLimitedLife(60 * (90 + Apostle.this.level.random.nextInt(180)));
                                summonedentity.finalizeSpawn(ServerLevel, Apostle.this.level.getCurrentDifficultyAt(blockpos$mutable), MobSpawnType.MOB_SUMMONED, null, null);
                                summonedentity.setTarget(livingentity);
                                SummonCircleEntity summonCircleEntity = new SummonCircleEntity(Apostle.this.level, blockpos$mutable, summonedentity, false, Apostle.this);
                                Apostle.this.level.addFreshEntity(summonCircleEntity);
                            }
                        }
                    } else {
                        if (r.nextBoolean()) {
                            ZPiglinBruteServant summonedentity = new ZPiglinBruteServant(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), Apostle.this.level);
                            summonedentity.moveTo(blockpos, 0.0F, 0.0F);
                            summonedentity.setTrueOwner(Apostle.this);
                            summonedentity.setLimitedLife(60 * (90 + Apostle.this.level.random.nextInt(180)));
                            summonedentity.finalizeSpawn(ServerLevel, Apostle.this.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                            for(EquipmentSlot equipmentslottype : EquipmentSlot.values()) {
                                if (equipmentslottype.getType() == EquipmentSlot.Type.ARMOR) {
                                    ItemStack itemstack = summonedentity.getItemBySlot(equipmentslottype);
                                    if (itemstack.isEmpty()) {
                                        Item item = getEquipmentForSlot(equipmentslottype, 1);
                                        if (Apostle.this.level.getDifficulty() == Difficulty.HARD){
                                            item = netheriteArmor(equipmentslottype);
                                        }
                                        if (item != null) {
                                            summonedentity.setItemSlot(equipmentslottype, new ItemStack(item));
                                        }
                                    }
                                }
                                if (equipmentslottype == EquipmentSlot.MAINHAND){
                                    if (Apostle.this.level.getDifficulty() == Difficulty.HARD){
                                        summonedentity.setItemSlot(equipmentslottype, new ItemStack(Items.NETHERITE_AXE));
                                    }
                                }
                                summonedentity.setDropChance(equipmentslottype, 0.0F);
                            }
                            summonedentity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Integer.MAX_VALUE, 1, false, false));
                            summonedentity.setTarget(livingentity);
                            SummonCircleEntity summonCircleEntity = new SummonCircleEntity(Apostle.this.level, blockpos, summonedentity, false, Apostle.this);
                            Apostle.this.level.addFreshEntity(summonCircleEntity);
                        } else {
                            for (int p = 0; p < 2 + r.nextInt(Apostle.this.level.getDifficulty().getId()); ++p) {
                                int k = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                                int l = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                                BlockPos.MutableBlockPos blockpos$mutable = Apostle.this.blockPosition().mutable().move(k, 0, l);
                                blockpos$mutable.setX(blockpos$mutable.getX() + r.nextInt(5) - r.nextInt(5));
                                blockpos$mutable.setY((int) BlockFinder.moveDownToGround(Apostle.this));
                                blockpos$mutable.setZ(blockpos$mutable.getZ() + r.nextInt(5) - r.nextInt(5));
                                ZPiglinBruteServant summonedentity = new ZPiglinBruteServant(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), Apostle.this.level);
                                summonedentity.moveTo(blockpos$mutable, 0.0F, 0.0F);
                                summonedentity.setTrueOwner(Apostle.this);
                                summonedentity.setLimitedLife(60 * (90 + Apostle.this.level.random.nextInt(180)));
                                summonedentity.finalizeSpawn(ServerLevel, Apostle.this.level.getCurrentDifficultyAt(blockpos$mutable), MobSpawnType.MOB_SUMMONED, null, null);
                                summonedentity.setTarget(livingentity);
                                SummonCircleEntity summonCircleEntity = new SummonCircleEntity(Apostle.this.level, blockpos$mutable, summonedentity, false, Apostle.this);
                                Apostle.this.level.addFreshEntity(summonCircleEntity);
                            }
                        }
                    }
                    Apostle.this.postSpellCast();
                }
            }
        }

        @Nullable
        public Item netheriteArmor(EquipmentSlot pSlot) {
            switch(pSlot) {
                case HEAD:
                    return Items.NETHERITE_HELMET;
                case CHEST:
                    return Items.NETHERITE_CHESTPLATE;
                case LEGS:
                    return Items.NETHERITE_LEGGINGS;
                case FEET:
                    return Items.NETHERITE_BOOTS;
                default:
                    return null;
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.APOSTLE_PREPARE_SUMMON.get();
        }

        @Override
        protected SpellType getSpellType() {
            return SpellType.ZOMBIE;
        }
    }

    class FireRainSpellGoal extends CastingGoal {
        private FireRainSpellGoal() {
        }

        @Override
        public boolean canUse() {
            int i = Apostle.this.level.getNearbyEntities(Owned.class, Apostle.this.zombieCount, Apostle.this, Apostle.this.getBoundingBox().inflate(64.0D)).size();
            int i2 = Apostle.this.level.getEntitiesOfClass(AbstractTrap.class, Apostle.this.getBoundingBox().inflate(64.0D)).size();
            if (!super.canUse()) {
                return false;
            } else {
                int cool = Apostle.this.spellStart();
                return Apostle.this.coolDown >= cool
                        && Apostle.this.spellCycle == 2
                        && !Apostle.this.isSettingupSecond()
                        && i > 4
                        && i2 < 2;
            }
        }

        protected int getCastingTime() {
            return 20;
        }

        public void castSpell() {
            if (!Apostle.this.level.isClientSide) {
                LivingEntity livingentity = Apostle.this.getTarget();
                if (livingentity != null) {
                    BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(livingentity.getX(), livingentity.getY(), livingentity.getZ());

                    while (blockpos$mutable.getY() > 0 && !Apostle.this.level.getBlockState(blockpos$mutable).getMaterial().blocksMotion()) {
                        blockpos$mutable.move(Direction.DOWN);
                    }
                    if (Apostle.this.isSecondPhase()){
                        ArrowRainTrap fireRainTrap = new ArrowRainTrap(Apostle.this.level, blockpos$mutable.getX(), blockpos$mutable.getY() + 1, blockpos$mutable.getZ());
                        fireRainTrap.setOwner(Apostle.this);
                        fireRainTrap.setDuration(100);
                        Apostle.this.level.addFreshEntity(fireRainTrap);
                    } else {
                        FireRainTrap fireRainTrap = new FireRainTrap(Apostle.this.level, blockpos$mutable.getX(), blockpos$mutable.getY() + 1, blockpos$mutable.getZ());
                        fireRainTrap.setOwner(Apostle.this);
                        fireRainTrap.setDuration(1200);
                        Apostle.this.level.addFreshEntity(fireRainTrap);
                    }
                    Apostle.this.postSpellCast();
                }
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.APOSTLE_PREPARE_SUMMON.get();
        }

        @Override
        protected SpellType getSpellType() {
            return SpellType.FIRE;
        }
    }

    class SkeletonSpellGoal extends CastingGoal {
        private SkeletonSpellGoal() {
        }

        @Override
        public boolean canUse() {
            int i = Apostle.this.level.getNearbyEntities(Owned.class, Apostle.this.skeletonCount, Apostle.this, Apostle.this.getBoundingBox().inflate(64.0D)).size();
            if (!super.canUse()) {
                return false;
            } else {
                int cool = Apostle.this.spellStart();
                return Apostle.this.coolDown >= cool
                        && Apostle.this.spellCycle == 3
                        && !Apostle.this.isSettingupSecond()
                        && i < 2;
            }
        }

        protected int getCastingTime() {
            return 60;
        }

        public void castSpell() {
            if (!Apostle.this.level.isClientSide) {
                ServerLevel ServerLevel = (ServerLevel) Apostle.this.level;
                LivingEntity livingentity = Apostle.this.getTarget();
                RandomSource r = Apostle.this.random;
                if (livingentity != null) {
                    if (r.nextBoolean()) {
                        if (Apostle.this.isSecondPhase()) {
                            int k = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                            int l = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                            BlockPos.MutableBlockPos blockpos$mutable = Apostle.this.blockPosition().mutable().move(k, 0, l);
                            blockpos$mutable.setX(blockpos$mutable.getX() + r.nextInt(5) - r.nextInt(5));
                            blockpos$mutable.setY((int) BlockFinder.moveDownToGround(Apostle.this) + 3);
                            blockpos$mutable.setZ(blockpos$mutable.getZ() + r.nextInt(5) - r.nextInt(5));
                            Malghast summonedentity = new Malghast(ModEntityType.MALGHAST.get(), Apostle.this.level);
                            if (ServerLevel.noCollision(summonedentity, summonedentity.getBoundingBox().move(blockpos$mutable).inflate(0.5F)) && summonedentity.distanceToSqr(Apostle.this) <= Mth.square(32.0F)){
                                summonedentity.moveTo(blockpos$mutable, 0.0F, 0.0F);
                            } else {
                                blockpos$mutable = Apostle.this.blockPosition().mutable();
                                summonedentity.moveTo(blockpos$mutable, 0.0F, 0.0F);
                            }
                            summonedentity.setTrueOwner(Apostle.this);
                            summonedentity.setLimitedLife(60 * (90 + Apostle.this.level.random.nextInt(180)));
                            summonedentity.finalizeSpawn(ServerLevel, Apostle.this.level.getCurrentDifficultyAt(blockpos$mutable), MobSpawnType.MOB_SUMMONED, null, null);
                            summonedentity.setTarget(livingentity);
                            SummonCircleEntity summonCircleEntity = new SummonCircleEntity(Apostle.this.level, blockpos$mutable, summonedentity, false, Apostle.this);
                            Apostle.this.level.addFreshEntity(summonCircleEntity);
                        } else {
                            BlockPos blockpos = Apostle.this.blockPosition();
                            SkeletonVillagerServant summonedentity = new SkeletonVillagerServant(ModEntityType.SKELETON_VILLAGER_SERVANT.get(), Apostle.this.level);
                            summonedentity.moveTo(blockpos, 0.0F, 0.0F);
                            summonedentity.setTrueOwner(Apostle.this);
                            summonedentity.setLimitedLife(60 * (90 + Apostle.this.level.random.nextInt(180)));
                            for (EquipmentSlot equipmentslottype : EquipmentSlot.values()) {
                                if (equipmentslottype.getType() == EquipmentSlot.Type.ARMOR) {
                                    ItemStack itemstack = summonedentity.getItemBySlot(equipmentslottype);
                                    if (itemstack.isEmpty()) {
                                        Item item = getEquipmentForSlot(equipmentslottype, 3);
                                        if (item != null) {
                                            summonedentity.setItemSlot(equipmentslottype, new ItemStack(item));
                                        }
                                    }
                                }
                                summonedentity.setDropChance(equipmentslottype, 0.0F);
                            }
                            summonedentity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Integer.MAX_VALUE, 1, false, false));
                            summonedentity.finalizeSpawn(ServerLevel, Apostle.this.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                            summonedentity.setTarget(livingentity);
                            SummonCircleEntity summonCircleEntity = new SummonCircleEntity(Apostle.this.level, blockpos, summonedentity, false, Apostle.this);
                            Apostle.this.level.addFreshEntity(summonCircleEntity);
                        }
                    } else {
                        for (int p = 0; p < 1 + r.nextInt(2); ++p) {
                            int k = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                            int l = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                            BlockPos.MutableBlockPos blockpos$mutable = Apostle.this.blockPosition().mutable().move(k, 0, l);
                            blockpos$mutable.setX(blockpos$mutable.getX() + r.nextInt(5) - r.nextInt(5));
                            blockpos$mutable.setY((int) BlockFinder.moveDownToGround(Apostle.this));
                            blockpos$mutable.setZ(blockpos$mutable.getZ() + r.nextInt(5) - r.nextInt(5));
                            SkeletonVillagerServant summonedentity = new SkeletonVillagerServant(ModEntityType.SKELETON_VILLAGER_SERVANT.get(), Apostle.this.level);
                            summonedentity.moveTo(blockpos$mutable, 0.0F, 0.0F);
                            summonedentity.setTrueOwner(Apostle.this);
                            summonedentity.setLimitedLife(60 * (90 + Apostle.this.level.random.nextInt(180)));
                            summonedentity.finalizeSpawn(ServerLevel, Apostle.this.level.getCurrentDifficultyAt(blockpos$mutable), MobSpawnType.MOB_SUMMONED, null, null);
                            summonedentity.setTarget(livingentity);
                            SummonCircleEntity summonCircleEntity = new SummonCircleEntity(Apostle.this.level, blockpos$mutable, summonedentity, false, Apostle.this);
                            Apostle.this.level.addFreshEntity(summonCircleEntity);
                        }
                    }
                    Apostle.this.postSpellCast();
                }
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.APOSTLE_PREPARE_SUMMON.get();
        }

        @Override
        protected SpellType getSpellType() {
            return SpellType.SKELETON;
        }
    }

    class FireTornadoSpellGoal extends CastingGoal {
        private FireTornadoSpellGoal() {
        }

        @Override
        public boolean canUse() {
            int i = Apostle.this.level.getNearbyEntities(Owned.class, Apostle.this.skeletonCount, Apostle.this, Apostle.this.getBoundingBox().inflate(64.0D)).size();
            int i2 = Apostle.this.level.getEntitiesOfClass(FireTornado.class, Apostle.this.getBoundingBox().inflate(64.0D)).size();
            int cool = Apostle.this.spellStart();
            if (!super.canUse()) {
                return false;
            } else if (Apostle.this.isSettingupSecond()){
                return false;
            } else if (Apostle.this.hitTimes >= 6){
                return i2 < 1;
            } else {
                return Apostle.this.coolDown >= cool
                        && Apostle.this.spellCycle == 3
                        && i >= 2
                        && i2 < 1;
            }
        }

        protected int getCastingTime() {
            return 60;
        }

        public void castSpell() {
            if (!Apostle.this.level.isClientSide) {
                LivingEntity livingentity = Apostle.this.getTarget();
                if (livingentity != null) {
                    BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(livingentity.getX(), livingentity.getY(), livingentity.getZ());

                    while (blockpos$mutable.getY() > 0 && !Apostle.this.level.getBlockState(blockpos$mutable).getMaterial().blocksMotion()) {
                        blockpos$mutable.move(Direction.DOWN);
                    }

                    FireTornadoTrap fireTornadoTrapEntity = new FireTornadoTrap(ModEntityType.FIRE_TORNADO_TRAP.get(), Apostle.this.level);
                    fireTornadoTrapEntity.setPos(blockpos$mutable.getX(), blockpos$mutable.getY() + 1, blockpos$mutable.getZ());
                    fireTornadoTrapEntity.setOwner(Apostle.this);
                    fireTornadoTrapEntity.setDuration(60);
                    Apostle.this.level.addFreshEntity(fireTornadoTrapEntity);
                    Apostle.this.postSpellCast();
                }
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.APOSTLE_PREPARE_SUMMON.get();
        }

        @Override
        protected SpellType getSpellType() {
            return SpellType.TORNADO;
        }
    }

    class RoarSpellGoal extends CastingGoal {
        private RoarSpellGoal() {
        }

        @Override
        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else if (Apostle.this.getTarget() == null) {
                return false;
            } else return Apostle.this.distanceTo(Apostle.this.getTarget()) < 4.0F
                    && !Apostle.this.isSettingupSecond();
        }

        protected int getCastingTime() {
            return 200;
        }

        protected int getCastingInterval() {
            return 120;
        }

        public void castSpell() {
            Apostle Apostle = Apostle.this;
            Apostle.hitTimes = 0;
            if (!Apostle.isSecondPhase()){
                Apostle.setFiring(true);
                Apostle.coolDown = 0;
                Apostle.playSound(ModSounds.ROAR_SPELL.get(), 1.0F, 1.0F);
            } else {
                double d0 = Math.min(Apostle.getTarget().getY(), Apostle.getY());
                double d1 = Math.max(Apostle.getTarget().getY(), Apostle.getY()) + 1.0D;
                for(int i = 0; i < 5; ++i) {
                    float f1 = f + (float)i * (float)Math.PI * 0.4F;
                    spawnBlasts(Apostle,Apostle.getX() + (double)Mth.cos(f1) * 1.5D, Apostle.getZ() + (double)Mth.sin(f1) * 1.5D, d0, d1);
                }

                for(int k = 0; k < 8; ++k) {
                    float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
                    spawnBlasts(Apostle,Apostle.getX() + (double)Mth.cos(f2) * 2.5D, Apostle.getZ() + (double)Mth.sin(f2) * 2.5D, d0, d1);
                }
                if (Apostle.getHealth() < Apostle.getMaxHealth()/2){
                    for(int k = 0; k < 11; ++k) {
                        float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
                        spawnBlasts(Apostle,Apostle.getX() + (double)Mth.cos(f2) * 2.5D, Apostle.getZ() + (double)Mth.sin(f2) * 2.5D, d0, d1);
                    }
                }

                if (Apostle.getHealth() < Apostle.getMaxHealth()/4){
                    for(int k = 0; k < 14; ++k) {
                        float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
                        spawnBlasts(Apostle,Apostle.getX() + (double)Mth.cos(f2) * 2.5D, Apostle.getZ() + (double)Mth.sin(f2) * 2.5D, d0, d1);
                    }
                }
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.APOSTLE_PREPARE_SPELL.get();
        }

        @Override
        protected SpellType getSpellType() {
            return SpellType.ROAR;
        }

        public void spawnBlasts(LivingEntity livingEntity, double pPosX, double pPosZ, double PPPosY, double pOPosY) {
            BlockPos blockpos = new BlockPos(pPosX, pOPosY, pPosZ);
            boolean flag = false;
            double d0 = 0.0D;

            do {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = livingEntity.level.getBlockState(blockpos1);
                if (blockstate.isFaceSturdy(livingEntity.level, blockpos1, Direction.UP)) {
                    if (!livingEntity.level.isEmptyBlock(blockpos)) {
                        BlockState blockstate1 = livingEntity.level.getBlockState(blockpos);
                        VoxelShape voxelshape = blockstate1.getCollisionShape(livingEntity.level, blockpos);
                        if (!voxelshape.isEmpty()) {
                            d0 = voxelshape.max(Direction.Axis.Y);
                        }
                    }

                    flag = true;
                    break;
                }

                blockpos = blockpos.below();
            } while(blockpos.getY() >= Mth.floor(PPPosY) - 1);

            if (flag) {
                FireBlastTrap fireBlastTrap = new FireBlastTrap(livingEntity.level, pPosX, (double)blockpos.getY() + d0, pPosZ);
                fireBlastTrap.setOwner(livingEntity);
                livingEntity.level.addFreshEntity(fireBlastTrap);
            }

        }
    }

    class SecondPhaseIndicator extends Goal {
        private SecondPhaseIndicator() {
        }

        @Override
        public boolean canUse() {
            return Apostle.this.getHealth() <= Apostle.this.getMaxHealth()/2
                    && Apostle.this.getTarget() != null
                    && !Apostle.this.isSecondPhase()
                    && !Apostle.this.isSettingupSecond();
        }

        @Override
        public void tick() {
            Apostle.this.setSettingupSecond(true);
        }
    }

    static class BowAttackGoal<T extends Apostle & RangedAttackMob> extends RangedBowAttackGoal<T> {
        private final T mob;

        public BowAttackGoal(T p_i47515_1_) {
            super(p_i47515_1_, 1.0D, 40, 30.0F);
            this.mob = p_i47515_1_;
        }

        @Override
        public boolean canUse() {
            return !this.mob.isSecondPhase() && this.mob.getTarget() != null && this.HaveBow() && !this.mob.isSettingupSecond();
        }

        protected boolean HaveBow() {
            return this.mob.isHolding(item -> item.getItem() instanceof BowItem);
        }
    }

    static class FasterBowAttackGoal<T extends Apostle & RangedAttackMob> extends RangedBowAttackGoal<T> {
        private final T mob;

        public FasterBowAttackGoal(T p_i47515_1_) {
            super(p_i47515_1_, 1.0D, 20, 30.0F);
            this.mob = p_i47515_1_;
        }

        @Override
        public boolean canUse() {
            return this.mob.isSecondPhase() && this.mob.getHealth() > this.mob.getMaxHealth()/4 && this.mob.getTarget() != null && this.HaveBow() && !this.mob.isSettingupSecond();
        }

        protected boolean HaveBow() {
            return this.mob.isHolding(item -> item.getItem() instanceof BowItem);
        }
    }

    static class FastestBowAttackGoal<T extends Apostle & RangedAttackMob> extends RangedBowAttackGoal<T> {
        private final T mob;

        public FastestBowAttackGoal(T p_i47515_1_) {
            super(p_i47515_1_, 1.0D, 5, 30.0F);
            this.mob = p_i47515_1_;
        }

        @Override
        public boolean canUse() {
            return this.mob.isSecondPhase() && this.mob.getHealth() <= this.mob.getMaxHealth()/4 && this.mob.getTarget() != null && this.HaveBow() && !this.mob.isSettingupSecond();
        }

        protected boolean HaveBow() {
            return this.mob.isHolding(item -> item.getItem() instanceof BowItem);
        }
    }

    public boolean canChangeDimensions() {
        return false;
    }

}
