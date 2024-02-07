package com.Polarice3.Goety.common.entities.boss;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.MobsConfig;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.hostile.cultists.Cultist;
import com.Polarice3.Goety.common.entities.hostile.cultists.SpellCastingCultist;
import com.Polarice3.Goety.common.entities.hostile.servants.Malghast;
import com.Polarice3.Goety.common.entities.hostile.servants.ObsidianMonolith;
import com.Polarice3.Goety.common.entities.hostile.servants.SkeletonVillagerServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinBruteServant;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.Polarice3.Goety.common.entities.projectiles.*;
import com.Polarice3.Goety.common.entities.util.*;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.common.network.server.SApostleSmitePacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModLootTables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;
import java.util.function.Predicate;

public class Apostle extends SpellCastingCultist implements RangedAttackMob {
    private int f;
    private int hitTimes;
    private int coolDown;
    private int tornadoCoolDown;
    private int monolithCoolDown;
    private int spellCycle;
    private int titleNumber;
    private final Predicate<Entity> ALIVE = Entity::isAlive;
    private boolean roarParticles;
    private boolean fireArrows;
    private boolean regen;
    private MobEffect arrowEffect;
    private static final UUID SPEED_MODIFIER_CASTING_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier SPEED_MODIFIER_CASTING = new AttributeModifier(SPEED_MODIFIER_CASTING_UUID, "Casting speed penalty", -1.0D, AttributeModifier.Operation.ADDITION);
    protected static final EntityDataAccessor<Byte> BOSS_FLAGS = SynchedEntityData.defineId(Apostle.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Float> SPIN = SynchedEntityData.defineId(Apostle.class, EntityDataSerializers.FLOAT);
    private final ModServerBossInfo bossInfo = new ModServerBossInfo(this.getUUID(), this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(true).setCreateWorldFog(true);
    public Predicate<Owned> ZOMBIE_MINIONS = (owned) -> {
        return owned instanceof ZPiglinServant && owned.getTrueOwner() == this;
    };
    private final Predicate<LivingEntity> MONOLITHS = (livingEntity) -> {
        return livingEntity instanceof ObsidianMonolith monolith && monolith.getTrueOwner() == this;
    };
    private final Predicate<Owned> SKELETON_MINIONS = (owned) -> {
        return (owned instanceof SkeletonVillagerServant || owned instanceof Malghast) && owned.getTrueOwner() == this;
    };
    private final Predicate<Entity> OWNED_TRAPS = (entity) -> {
        return entity instanceof AbstractTrap abstractTrap && abstractTrap.getOwner() == this;
    };
    public int antiRegen;
    public int antiRegenTotal;
    public int deathTime = 0;
    public int moddedInvul = 0;
    public DamageSource deathBlow = this.damageSources().generic();

    public Apostle(EntityType<? extends SpellCastingCultist> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.xpReward = 1000;
        this.f = 0;
        this.coolDown = 100;
        this.spellCycle = 0;
        this.hitTimes = 0;
        this.antiRegen = 0;
        if (this.level.isClientSide){
            Goety.PROXY.addBoss(this);
        }
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SecondPhaseIndicator());
        this.goalSelector.addGoal(2, new ApostleBowGoal<>(this, 30.0F));
        this.goalSelector.addGoal(3, new CastingSpellGoal());
        this.goalSelector.addGoal(3, new FireballSpellGoal());
        this.goalSelector.addGoal(3, new ZombieSpellGoal());
        this.goalSelector.addGoal(3, new FireRainSpellGoal());
        this.goalSelector.addGoal(3, new SkeletonSpellGoal());
        this.goalSelector.addGoal(3, new FireTornadoSpellGoal());
        this.goalSelector.addGoal(3, new RoarSpellGoal());
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void extraGoal() {
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ApostleHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ARMOR, AttributesConfig.ApostleArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.ApostleToughness.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0F)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.ApostleHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.ApostleArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS), AttributesConfig.ApostleToughness.get());
    }

    protected PathNavigation createNavigation(Level p_33913_) {
        return new ApostlePathNavigation(this, p_33913_);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BOSS_FLAGS, (byte)0);
        this.entityData.define(SPIN, 0.0F);
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

    public boolean canStandOnFluid(FluidState p_204067_) {
        return p_204067_.is(FluidTags.LAVA);
    }

    private void floatApostle() {
        if (this.isInLava()) {
            CollisionContext collisioncontext = CollisionContext.of(this);
            if (collisioncontext.isAbove(LiquidBlock.STABLE_SHAPE, this.blockPosition(), true) && !this.level.getFluidState(this.blockPosition().above()).is(FluidTags.LAVA)) {
                this.setOnGround(true);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D).add(0.0D, 0.05D, 0.0D));
            }
        }

    }

    public float getWalkTargetValue(BlockPos p_33895_, LevelReader p_33896_) {
        if (p_33896_.getBlockState(p_33895_).getFluidState().is(FluidTags.LAVA)) {
            return 10.0F;
        } else {
            return this.isInLava() ? Float.NEGATIVE_INFINITY : 0.0F;
        }
    }

    public int getAmbientSoundInterval() {
        return 200;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getTarget() != null || this.isAggressive()) {
            return ModSounds.APOSTLE_AMBIENT.get();
        } else {
            return null;
        }
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
        return pPotioneffect.getEffect() != GoetyEffects.BURN_HEX.get() && pPotioneffect.getEffect() != MobEffects.WITHER && super.canBeAffected(pPotioneffect);
    }

    public int getExperienceReward() {
        if (this.level.dimension() == Level.NETHER){
            return this.xpReward * 12;
        } else {
            return this.xpReward;
        }
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("firing", this.f);
        pCompound.putInt("coolDown", this.coolDown);
        pCompound.putInt("tornadoCoolDown", this.tornadoCoolDown);
        pCompound.putInt("monolithCoolDown", this.monolithCoolDown);
        pCompound.putInt("spellCycle", this.spellCycle);
        pCompound.putInt("hitTimes", this.hitTimes);
        pCompound.putInt("antiRegen", this.antiRegen);
        pCompound.putInt("antiRegenTotal", this.antiRegenTotal);
        pCompound.putInt("titleNumber", this.titleNumber);
        pCompound.putInt("moddedInvul", this.moddedInvul);
        pCompound.putBoolean("fireArrows", this.fireArrows);
        pCompound.putBoolean("secondPhase", this.isSecondPhase());
        pCompound.putBoolean("settingSecondPhase", this.isSettingUpSecond());
        pCompound.putBoolean("regen", this.regen);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.f = pCompound.getInt("firing");
        this.coolDown = pCompound.getInt("coolDown");
        this.tornadoCoolDown = pCompound.getInt("tornadoCoolDown");
        this.monolithCoolDown = pCompound.getInt("monolithCoolDown");
        this.spellCycle = pCompound.getInt("spellCycle");
        this.hitTimes = pCompound.getInt("hitTimes");
        this.antiRegen = pCompound.getInt("antiRegen");
        this.antiRegenTotal = pCompound.getInt("antiRegenTotal");
        this.titleNumber = pCompound.getInt("titleNumber");
        this.moddedInvul = pCompound.getInt("moddedInvul");
        this.fireArrows = pCompound.getBoolean("fireArrows");
        this.regen = pCompound.getBoolean("regen");
        this.setTitleNumber(this.titleNumber);
        this.TitleEffect(this.titleNumber);
        this.setSecondPhase(pCompound.getBoolean("secondPhase"));
        this.setSettingUpSecond(pCompound.getBoolean("settingSecondPhase"));
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
            this.antiRegen = 0;
            this.antiRegenTotal = 0;
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
        if (MobsConfig.FancierApostleDeath.get() || this.level.dimension() == Level.NETHER) {
            this.setNoGravity(true);
            if (this.getKillCredit() instanceof Player){
                this.lastHurtByPlayerTime = 100;
            }
            if (this.deathTime < 180) {
                if (this.deathTime > 20) {
                    this.move(MoverType.SELF, new Vec3(0.0D, 0.1D, 0.0D));
                }
                this.level.explode(this, this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), 0.0F, Level.ExplosionInteraction.NONE);
            } else if (this.deathTime != 200) {
                this.move(MoverType.SELF, new Vec3(0.0D, 0.0D, 0.0D));
            }
            if (this.deathTime >= 200) {
                this.move(MoverType.SELF, new Vec3(0.0D, -4.0D, 0.0D));
                if (this.onGround() || this.getY() <= 0) {
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
    public void remove(RemovalReason p_146834_) {
        if (this.level.isClientSide) {
            Goety.PROXY.removeBoss(this);
        } else {
            ServerLevel ServerLevel = (ServerLevel) this.level;
            if (ServerLevel.getLevelData().isThundering()) {
                ServerLevel.setWeatherParameters(6000, 0, false, false);
            }
        }
        super.remove(p_146834_);
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        if (this.level.dimension() == Level.NETHER){
            return ModLootTables.APOSTLE_HARD;
        } else {
            return super.getDefaultLootTable();
        }
    }

    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        ItemEntity itementity = this.spawnAtLocation(ModItems.UNHOLY_BLOOD.get());
        if (itementity != null) {
            itementity.setExtendedLifetime();
        }
        if (this.level.dimension() == Level.NETHER){
            if (MainConfig.EnableNightBeacon.get()){
                ItemEntity itementity2 = this.spawnAtLocation(ModBlocks.NIGHT_BEACON.get().asItem());
                if (itementity2 != null) {
                    itementity2.setExtendedLifetime();
                }
            }
        }

    }

    protected float getDamageAfterMagicAbsorb(DamageSource source, float damage) {
        damage = super.getDamageAfterMagicAbsorb(source, damage);
        if (source.getEntity() == this) {
            damage = 0.0F;
        }

        if (source.getDirectEntity() instanceof ExplosiveProjectile || source.getDirectEntity() instanceof Fireball) {
            damage = (float)((double)damage * 0.15D);
        }

        if (this.level.getDifficulty() == Difficulty.HARD){
            if (source.is(DamageTypeTags.WITCH_RESISTANT_TO)){
                damage = (float)((double)damage * 0.15D);
            }
        }

        return damage;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.populateDefaultEquipmentSlots(worldIn.getRandom(), difficultyIn);
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
            this.setCustomName(Component.translatable(component.getString() + component1.getString()));
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public void setTitleNumber(Integer integer){
        this.titleNumber = integer;
    }

    public void TitleEffect(Integer integer){
        switch (integer) {
            case 0 -> this.setRegen(true);
            case 1 -> this.setArrowEffect(MobEffects.HARM);
            case 2 -> this.setArrowEffect(MobEffects.POISON);
            case 3 -> this.setArrowEffect(MobEffects.WITHER);
            case 4 -> this.setArrowEffect(MobEffects.DARKNESS);
            case 5 -> this.setArrowEffect(MobEffects.WEAKNESS);
            case 6 -> {
                this.setFireArrow(true);
                this.setArrowEffect(GoetyEffects.BURN_HEX.get());
            }
            case 7 -> this.setArrowEffect(MobEffects.HUNGER);
            case 8 -> this.setArrowEffect(MobEffects.MOVEMENT_SLOWDOWN);
            case 9 ->
                    this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, false), this);
            case 10 ->
                    this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 0, false, false), this);
            case 11 -> this.setArrowEffect(GoetyEffects.SAPPED.get());
        }
    }

    public boolean addEffect(MobEffectInstance p_182397_, @Nullable Entity p_182398_) {
        if (p_182398_ == this) {
            return super.addEffect(p_182397_, p_182398_);
        } else {
            return p_182397_.getEffect().isBeneficial();
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

    public void setSettingUpSecond(boolean settingupSecond){
        this.setBossFlag(2, settingupSecond);
    }

    public boolean isSettingUpSecond(){
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

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    public CultistArmPose getArmPose() {
        if (this.isDeadOrDying()){
            return CultistArmPose.DYING;
        } else if (this.getMainHandItem().getItem() instanceof BowItem) {
            if (this.isAggressive() && !this.isSpellcasting() && !this.isSettingUpSecond()){
                return CultistArmPose.BOW_AND_ARROW;
            } else if (this.isSpellcasting()){
                return CultistArmPose.SPELL_AND_WEAPON;
            } else if (this.isSettingUpSecond()){
                return CultistArmPose.SPELL_AND_WEAPON;
            } else {
                return CultistArmPose.CROSSED;
            }
        } else {
            return CultistArmPose.CROSSED;
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

    public void increaseHitTime(){
        ++this.hitTimes;
    }

    public int getHitTimes(){
        return this.hitTimes;
    }

    public void resetCoolDown(){
        this.setCoolDown(0);
    }

    public void setCoolDown(int coolDown){
        this.coolDown = coolDown;
    }

    public int getCoolDown(){
        return this.coolDown;
    }

    public void setTornadoCoolDown(int coolDown){
        this.tornadoCoolDown = coolDown;
    }

    public int getTornadoCoolDown(){
        return this.tornadoCoolDown;
    }

    public void setMonolithCoolDown(int coolDown){
        this.monolithCoolDown = coolDown;
    }

    public int getMonolithCoolDown(){
        return this.monolithCoolDown;
    }

    public int getAntiRegen(){
        return this.antiRegen;
    }

    public int getAntiRegenTotal(){
        return this.antiRegenTotal;
    }

    public boolean isSmited(){
        return this.antiRegen > 0;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        LivingEntity livingEntity = this.getTarget();
        if (!this.level.isClientSide) {
            if (livingEntity != null && pSource.getEntity() instanceof LivingEntity) {
                this.increaseHitTime();
            }
        }

        if (pSource.getDirectEntity() instanceof LivingEntity living){
            int smite = EnchantmentHelper.getEnchantmentLevel(Enchantments.SMITE, living);
            if (smite > 0){
                int smite2 = Mth.clamp(smite, 1, 5);
                int duration = MathHelper.secondsToTicks(smite2);
                if (this.Regen()){
                    duration /= 2;
                }
                this.antiRegenTotal = duration;
                this.antiRegen = duration;
                if (this.level instanceof ServerLevel){
                    ModNetwork.sendToALL(new SApostleSmitePacket(this.getId(), duration));
                    if (this.getCoolDown() < this.coolDownLimit()){
                        this.coolDown += 10;
                    }
                }
            }
        }

        if (pSource.is(DamageTypes.LIGHTNING_BOLT) || pSource.is(DamageTypes.FALL) || pSource.is(DamageTypes.IN_WALL)){
            return false;
        }

        if (pSource.getDirectEntity() instanceof AbstractArrow && ((AbstractArrow) pSource.getDirectEntity()).getOwner() == this){
            return false;
        }

        if (this.isSettingUpSecond()){
            return false;
        }

        if (pSource.getDirectEntity() instanceof NetherMeteor || pSource.getEntity() instanceof NetherMeteor){
            return false;
        }

        if (this.moddedInvul > 0){
            return false;
        }

        float trueAmount = this.level.dimension() == Level.NETHER ? pAmount / 2 : pAmount;

        if (pSource.is(DamageTypes.FELL_OUT_OF_WORLD) && pSource.getEntity() == null){
            this.discard();
        }

        if (this.getHitTimes() >= this.hitTimeTeleport()){
            trueAmount = trueAmount/2;
            this.teleport();
        }

        if (!this.level.getNearbyPlayers(TargetingConditions.forCombat().selector(MobUtil.NO_CREATIVE_OR_SPECTATOR), this, this.getBoundingBox().inflate(32)).isEmpty()){
            if (!(pSource.getEntity() instanceof Player)){
                trueAmount = trueAmount / 2;
            }
        }

        if (this.isDeadOrDying()) {
            this.deathBlow = pSource;
        }

        return super.hurt(pSource, Math.min(trueAmount, (float) AttributesConfig.ApostleDamageCap.get()));
    }

    protected void actuallyHurt(DamageSource p_21240_, float p_21241_) {
        if (p_21240_.is(DamageTypeTags.BYPASSES_COOLDOWN) || (p_21240_.is(DamageTypes.FELL_OUT_OF_WORLD) && p_21240_.getEntity() != null)) {
            this.moddedInvul = 20;
        }
        super.actuallyHurt(p_21240_, p_21241_);
    }

    @Override
    public void kill() {
        this.remove(RemovalReason.KILLED);
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource damageSource) {
        return false;
    }

    protected void teleport() {
        if (!this.level.isClientSide() && this.isAlive() && !this.isSettingUpSecond() && !this.isCasting()) {
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
        if (!this.level.isClientSide() && this.isAlive() && !this.isSettingUpSecond()) {
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
        this.level.gameEvent(GameEvent.TELEPORT, this.position(), GameEvent.Context.of(this));
        if (!this.isSilent()) {
            this.level.playSound((Player) null, this.xo, this.yo, this.zo, ModSounds.APOSTLE_TELEPORT.get(), this.getSoundSource(), 1.0F, 1.0F);
            this.playSound(ModSounds.APOSTLE_TELEPORT.get(), 1.0F, 1.0F);
        }
    }

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

    public void setSpellCycle(int spellCycle){
        this.spellCycle = spellCycle;
    }

    public int getSpellCycle(){
        return this.spellCycle;
    }

    protected boolean canRide(Entity pEntity) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        this.floatApostle();
    }

    public void aiStep() {
        super.aiStep();
        if (this.level.getDifficulty() == Difficulty.PEACEFUL){
            this.remove(RemovalReason.DISCARDED);
        }
        if (this.moddedInvul > 0){
            --this.moddedInvul;
        }
        if (this.level.isClientSide){
            if (this.isAlive()) {
                if (this.getSpin() < 3.14F) {
                    this.setSpin(this.getSpin() + 0.01F);
                } else {
                    this.setSpin(-3.14F);
                }
            }
            if (this.isSettingUpSecond()){
                for(int i = 0; i < 40; ++i) {
                    double d0 = this.random.nextGaussian() * 0.2D;
                    double d1 = this.random.nextGaussian() * 0.2D;
                    double d2 = this.random.nextGaussian() * 0.2D;
                    this.level.addAlwaysVisibleParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), d0, d1, d2);
                }
            }
        }
        if (!this.level.isClientSide){
            AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attributeinstance != null) {
                if (attributeinstance.hasModifier(SPEED_MODIFIER_CASTING)){
                    attributeinstance.removeModifier(SPEED_MODIFIER_CASTING);
                }
                if (this.isCasting()) {
                    attributeinstance.addTransientModifier(SPEED_MODIFIER_CASTING);
                }
            }
        }
        if (this.isSettingUpSecond()){
            this.setFiring(false);
            this.f = 0;
            this.heal(1.0F);
            for (Entity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3.0D), ALIVE)) {
                if (!entity.isAlliedTo(this)) {
                    this.barrier(entity, this);
                }
            }

            if (!this.level.isClientSide){
                this.resetHitTime();
            }
            if (this.getHealth() >= this.getMaxHealth()){
                if (!this.level.isClientSide){
                    ServerLevel serverLevel = (ServerLevel) this.level;
                    if (!serverLevel.isThundering()) {
                        serverLevel.setWeatherParameters(0, 6000, true, true);
                    }
                    for(int k = 0; k < 60; ++k) {
                        float f2 = random.nextFloat() * 4.0F;
                        float f1 = random.nextFloat() * ((float)Math.PI * 2F);
                        double d1 = Mth.cos(f1) * f2;
                        double d2 = 0.01D + random.nextDouble() * 0.5D;
                        double d3 = Mth.sin(f1) * f2;
                        serverLevel.sendParticles(ParticleTypes.FLAME, this.getX() + d1 * 0.1D, this.getY() + 0.3D, this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.25F);
                    }
                }
                this.setSettingUpSecond(false);
                this.setSecondPhase(true);
            }
        }
        LivingEntity target = this.getTarget();
        if (this.getMainHandItem().isEmpty() && this.isAlive()){
            this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOW));
        }
        if (this.isSmited()){
            --this.antiRegen;
        }
        if (this.getTornadoCoolDown() > 0){
            --this.tornadoCoolDown;
        }
        if (this.getMonolithCoolDown() > 0){
            --this.monolithCoolDown;
        }
        if (this.isSecondPhase()) {
            if (!this.isSmited()) {
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
            }
            if (this.tickCount % 100 == 0 && !this.isDeadOrDying()) {
                if (!this.level.isClientSide){
                    ServerLevel ServerLevel = (ServerLevel) this.level;
                    ServerLevel.setWeatherParameters(0, 6000, true, true);
                }
            }
            if (!this.level.isClientSide) {
                if (this.getCoolDown() < this.coolDownLimit()) {
                    ++this.coolDown;
                } else {
                    this.setSpellCycle(1);
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
                        fireball.setDangerous(ForgeEventFactory.getMobGriefingEvent(this.level, this) && MobsConfig.ApocalypseMode.get());
                        fireball.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                        this.level.addFreshEntity(fireball);
                    }
                }
            }
        } else {
            if (!this.isSmited()) {
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
            }
            if (!this.level.isClientSide) {
                if (this.getCoolDown() < this.coolDownLimit()) {
                    ++this.coolDown;
                } else {
                    this.setSpellCycle(0);
                }
            }
        }
        if (!this.level.isClientSide) {
            if (this.getSpellCycle() < 2) {
                if (this.level.random.nextBoolean()) {
                    this.setSpellCycle(2);
                } else {
                    this.setSpellCycle(3);
                }
            }
        }
        for (LivingEntity living : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(32))){
            if (!(living instanceof Cultist) && !(living instanceof Witch) && !(living instanceof IOwned && ((Owned) living).getTrueOwner() == this)){
                if (living.isInWater()){
                    living.hurt(living.damageSources().hotFloor(), 1.0F);
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
            for (Mob mob : this.level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(this.getAttributeValue(Attributes.FOLLOW_RANGE)), EntitySelector.LIVING_ENTITY_STILL_ALIVE)){
                if (mob.getTarget() == this && this.getTarget() == null){
                    this.setTarget(mob);
                }
            }
        } else {
            if (target instanceof IOwned owned && owned.getTrueOwner() != null){
                if (this.canAttack(owned.getTrueOwner()) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(owned.getTrueOwner())) {
                    this.setTarget(owned.getTrueOwner());
                }
            }
            int i = Apostle.this.level.getEntitiesOfClass(Owned.class, Apostle.this.getBoundingBox().inflate(64.0D), ZOMBIE_MINIONS).size();
            if (this.tickCount % 100 == 0 && i < 16 && this.level.random.nextFloat() <= 0.25F && !this.isSettingUpSecond()){
                if (!this.level.isClientSide){
                    ServerLevel ServerLevel = (ServerLevel) this.level;
                    RandomSource r = this.level.random;
                    int numbers = this.isSecondPhase() ? 4 : 2;
                    if (this.level.dimension() == Level.NETHER) {
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
                            SummonCircle summonCircle = new SummonCircle(this.level, blockpos$mutable, summonedentity, false, true, this);
                            this.level.addFreshEntity(summonCircle);
                        }
                    }
                }
            }
            if (this.isSecondPhase()) {
                if (this.getHealth() <= this.getMaxHealth()/8){
                    if (this.tickCount % 100 == 0){
                        this.teleport();
                    }
                }
                if (MobUtil.isInRain(target)) {
                    int count = 100 * (this.random.nextInt(5) + 1);
                    if (this.tickCount % count == 0) {
                        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(target.getX(), target.getY(), target.getZ());

                        while (blockpos$mutable.getY() > 0 && !this.level.getBlockState(blockpos$mutable).blocksMotion()) {
                            blockpos$mutable.move(Direction.DOWN);
                        }

                        LightningTrap lightningTrap = new LightningTrap(this.level, blockpos$mutable.getX(), blockpos$mutable.getY() + 1, blockpos$mutable.getZ());
                        lightningTrap.setOwner(this);
                        lightningTrap.setDuration(50);
                        this.level.addFreshEntity(lightningTrap);
                    }
                }
            }
            if ((target.distanceToSqr(this) > 1024 || !this.getSensing().hasLineOfSight(target)) && target.onGround() && !this.isSettingUpSecond()){
                this.teleportTowards(target);
            }
        }
        if (this.isFiring() && !this.isSettingUpSecond()) {
            ++this.f;
            if (this.f % 2 == 0 && this.f < 10) {
                for (Entity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D), ALIVE)) {
                    if (!(entity instanceof Cultist) && !(entity instanceof Witch) && !(entity instanceof IOwned && ((IOwned) entity).getTrueOwner() == this)) {
                        entity.hurt(this.damageSources().mobAttack(this), AttributesConfig.ApostleMagicDamage.get().floatValue());
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
                target.addEffect(new MobEffectInstance(GoetyEffects.BURN_HEX.get(), 100));
            }
        }
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.isSettingUpSecond();
    }

    public boolean hasLineOfSight(Entity p_149755_) {
        return !this.isSettingUpSecond() && super.hasLineOfSight(p_149755_);
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
        DeathArrow deathArrow = new DeathArrow(this.level, this);
        deathArrow.setEffectsFromItem(pArrowStack);
        deathArrow.setEnchantmentEffectsFromEntity(this, pDistanceFactor);
        if (this.getArrowEffect() != null){
            MobEffect mobEffect = this.getArrowEffect();
            int amp;
            if (this.isSecondPhase() && this.getArrowEffect() != MobEffects.HARM){
                amp = 1;
            } else {
                amp = 0;
            }
            if (this.getTarget() != null && this.getArrowEffect() == MobEffects.HARM && this.getTarget().isInvertedHealAndHarm()){
                mobEffect = MobEffects.HEAL;
            }
            if (this.isSecondPhase() && this.getArrowEffect() == MobEffects.DARKNESS){
                mobEffect = MobEffects.BLINDNESS;
            }
            deathArrow.addEffect(new MobEffectInstance(mobEffect, mobEffect.isInstantenous() ? 1 : 200, amp));
        }
        if (this.getFireArrow()){
            deathArrow.setRemainingFireTicks(100);
        }
        if (this.level.dimension() == Level.NETHER){
            deathArrow.setPierceLevel((byte) 5);
            deathArrow.setCritArrow(true);
        } else {
            float critChance = 0.05F;
            if (this.level.getDifficulty() == Difficulty.HARD){
                critChance += 0.25F;
            }
            if (this.isSecondPhase()){
                critChance += 0.1F;
            }
            if (this.isSecondPhase() && this.getHealth() <= this.getMaxHealth() / 4){
                critChance += 0.25F;
            }
            if (this.level.random.nextFloat() <= critChance){
                deathArrow.setCritArrow(true);
            }
        }
        return deathArrow;
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
        return this.isSecondPhase() && this.getHealth() <= this.getMaxHealth() /4 ? 12 : this.isSecondPhase() ? 20 : 40;
    }

    public int coolDownLimit(){
        return this.isSecondPhase() && this.getHealth() <= this.getMaxHealth() /4 ? 20 : this.isSecondPhase() ? 25 : 50;
    }

    public void postSpellCast(){
        if (this.teleportChance()) {
            this.teleport();
        }
        this.resetCoolDown();
        this.setSpellCycle(0);
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

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && !Apostle.this.isSettingUpSecond();
        }

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
                return Apostle.this.getSpellCycle() == 0
                        && !Apostle.this.isSettingUpSecond()
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
                double d1 = livingentity.getX() - Apostle.this.getX();
                double d2 = livingentity.getY(0.5D) - Apostle.this.getY(0.5D);
                double d3 = livingentity.getZ() - Apostle.this.getZ();
                ExplosiveProjectile fireballEntity;
                if (Apostle.this.level.getDifficulty() != Difficulty.EASY){
                    fireballEntity = new GrandLavaball(Apostle.this.level, Apostle.this, d1, d2, d3);
                } else {
                    fireballEntity = new Lavaball(Apostle.this.level, Apostle.this, d1, d2, d3);
                }
                fireballEntity.setDangerous(ForgeEventFactory.getMobGriefingEvent(Apostle.this.level, Apostle.this));
                fireballEntity.setPos(fireballEntity.getX(), Apostle.this.getY(0.5), fireballEntity.getZ());
                Apostle.this.level.addFreshEntity(fireballEntity);
                if (!Apostle.this.isSilent()) {
                    Apostle.this.level.levelEvent(null, 1016, Apostle.this.blockPosition(), 0);
                }
                if (Apostle.this.teleportChance()) {
                    Apostle.this.teleport();
                }
                Apostle.this.resetCoolDown();
                Apostle.this.setSpellCycle(1);
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
            int i = Apostle.this.level.getEntitiesOfClass(Owned.class, Apostle.this.getBoundingBox().inflate(64.0D), ZOMBIE_MINIONS).size();
            int j = Apostle.this.level.getEntitiesOfClass(Owned.class, Apostle.this.getBoundingBox().inflate(64.0D), MONOLITHS).size();
            if (!super.canUse()) {
                return false;
            }  else {
                int cool = Apostle.this.spellStart();
                return Apostle.this.getCoolDown() >= cool
                        && Apostle.this.getSpellCycle() == 2
                        && Apostle.this.getMonolithCoolDown() <= 0
                        && !Apostle.this.isSettingUpSecond()
                        && i < 4 && j < 4;
            }
        }

        protected int getCastingTime() {
            return 20;
        }

        public void castSpell() {
            if (!Apostle.this.level.isClientSide) {
                ServerLevel serverLevel = (ServerLevel) Apostle.this.level;
                LivingEntity livingentity = Apostle.this.getTarget();
                RandomSource randomSource = serverLevel.random;
                if (livingentity != null) {
                    BlockPos blockpos = Apostle.this.blockPosition();
                    if (randomSource.nextBoolean()) {
                        ZPiglinServant summonedentity = new ZPiglinServant(ModEntityType.ZPIGLIN_SERVANT.get(), Apostle.this.level);
                        if (Apostle.this.isSecondPhase()){
                            summonedentity = new ZPiglinBruteServant(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), Apostle.this.level);
                        }
                        summonedentity.moveTo(blockpos, 0.0F, 0.0F);
                        summonedentity.setTrueOwner(Apostle.this);
                        summonedentity.setLimitedLife(60 * (90 + serverLevel.random.nextInt(180)));
                        for(EquipmentSlot equipmentslottype : EquipmentSlot.values()) {
                            if (equipmentslottype.getType() == EquipmentSlot.Type.ARMOR) {
                                ItemStack itemstack = summonedentity.getItemBySlot(equipmentslottype);
                                if (itemstack.isEmpty()) {
                                    Item item = getEquipmentForSlot(equipmentslottype, 1);
                                    if (serverLevel.getDifficulty() == Difficulty.HARD && Apostle.this.isSecondPhase()){
                                        item = netheriteArmor(equipmentslottype);
                                    }
                                    if (item != null) {
                                        summonedentity.setItemSlot(equipmentslottype, new ItemStack(item));
                                    }
                                }
                            }
                            if (equipmentslottype == EquipmentSlot.MAINHAND){
                                if (serverLevel.getDifficulty() == Difficulty.HARD && Apostle.this.isSecondPhase()){
                                    summonedentity.setItemSlot(equipmentslottype, new ItemStack(Items.NETHERITE_AXE));
                                }
                            }
                            summonedentity.setDropChance(equipmentslottype, 0.0F);
                        }
                        summonedentity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Integer.MAX_VALUE, 1, false, false));
                        summonedentity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                        summonedentity.setTarget(livingentity);
                        SummonCircle summonCircle = new SummonCircle(serverLevel, blockpos, summonedentity, false, true, Apostle.this);
                        serverLevel.addFreshEntity(summonCircle);
                    } else {
                        if (serverLevel.dimension() == Level.NETHER) {
                            for (int p = 0; p < 3 + randomSource.nextInt(6); ++p) {
                                int k = (12 + randomSource.nextInt(12)) * (randomSource.nextBoolean() ? -1 : 1);
                                int l = (12 + randomSource.nextInt(12)) * (randomSource.nextBoolean() ? -1 : 1);
                                BlockPos.MutableBlockPos blockpos$mutable = Apostle.this.blockPosition().mutable().move(k, 0, l);
                                blockpos$mutable.setX(blockpos$mutable.getX() + randomSource.nextInt(5) - randomSource.nextInt(5));
                                blockpos$mutable.setY((int) BlockFinder.moveDownToGround(Apostle.this));
                                blockpos$mutable.setZ(blockpos$mutable.getZ() + randomSource.nextInt(5) - randomSource.nextInt(5));
                                ZPiglinServant summonedentity = new ZPiglinServant(ModEntityType.ZPIGLIN_SERVANT.get(), Apostle.this.level);
                                if (Apostle.this.isSecondPhase()){
                                    summonedentity = new ZPiglinBruteServant(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), Apostle.this.level);
                                }
                                summonedentity.moveTo(blockpos$mutable, 0.0F, 0.0F);
                                summonedentity.setTrueOwner(Apostle.this);
                                summonedentity.setLimitedLife(60 * (90 + serverLevel.random.nextInt(180)));
                                summonedentity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockpos$mutable), MobSpawnType.MOB_SUMMONED, null, null);
                                summonedentity.setTarget(livingentity);
                                SummonCircle summonCircle = new SummonCircle(serverLevel, blockpos$mutable, summonedentity, false, true, Apostle.this);
                                serverLevel.addFreshEntity(summonCircle);
                            }
                        } else {
                            int k = (12 + randomSource.nextInt(12)) * (randomSource.nextBoolean() ? -1 : 1);
                            int l = (12 + randomSource.nextInt(12)) * (randomSource.nextBoolean() ? -1 : 1);
                            BlockPos.MutableBlockPos blockpos$mutable = Apostle.this.blockPosition().mutable().move(k, 0, l);
                            blockpos$mutable.setX(blockpos$mutable.getX() + randomSource.nextInt(5) - randomSource.nextInt(5));
                            blockpos$mutable.setY((int) BlockFinder.moveDownToGround(Apostle.this));
                            blockpos$mutable.setZ(blockpos$mutable.getZ() + randomSource.nextInt(5) - randomSource.nextInt(5));
                            ObsidianMonolith summonedentity = new ObsidianMonolith(ModEntityType.OBSIDIAN_MONOLITH.get(), Apostle.this.level);
                            summonedentity.moveTo(blockpos$mutable, 0.0F, 0.0F);
                            summonedentity.setTrueOwner(Apostle.this);
                            summonedentity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockpos$mutable), MobSpawnType.MOB_SUMMONED, null, null);
                            serverLevel.addFreshEntity(summonedentity);
                        }
                    }
                    Apostle.this.postSpellCast();
                }
            }
        }

        @Nullable
        public Item netheriteArmor(EquipmentSlot pSlot) {
            return switch (pSlot) {
                case HEAD -> Items.NETHERITE_HELMET;
                case CHEST -> Items.NETHERITE_CHESTPLATE;
                case LEGS -> Items.NETHERITE_LEGGINGS;
                case FEET -> Items.NETHERITE_BOOTS;
                default -> null;
            };
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
            int i = Apostle.this.level.getEntitiesOfClass(Owned.class, Apostle.this.getBoundingBox().inflate(64.0D), ZOMBIE_MINIONS).size();
            int i2 = Apostle.this.level.getEntitiesOfClass(AbstractTrap.class, Apostle.this.getBoundingBox().inflate(64.0D), OWNED_TRAPS).size();
            if (!super.canUse()) {
                return false;
            } else {
                int cool = Apostle.this.spellStart();
                return Apostle.this.getCoolDown() >= cool
                        && Apostle.this.getSpellCycle() == 2
                        && !Apostle.this.isSettingUpSecond()
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

                    while (blockpos$mutable.getY() > 0 && !Apostle.this.level.getBlockState(blockpos$mutable).blocksMotion()) {
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
            int i = Apostle.this.level.getEntitiesOfClass(Owned.class, Apostle.this.getBoundingBox().inflate(64.0D), SKELETON_MINIONS).size();
            if (!super.canUse()) {
                return false;
            } else {
                int cool = Apostle.this.spellStart();
                return Apostle.this.getCoolDown() >= cool
                        && Apostle.this.getSpellCycle() == 3
                        && !Apostle.this.isSettingUpSecond()
                        && i < 2;
            }
        }

        protected int getCastingTime() {
            return 60;
        }

        public void castSpell() {
            if (!Apostle.this.level.isClientSide) {
                ServerLevel serverLevel = (ServerLevel) Apostle.this.level;
                LivingEntity livingentity = Apostle.this.getTarget();
                RandomSource r = Apostle.this.random;
                if (livingentity != null) {
                    if (r.nextBoolean()) {
                        if (Apostle.this.isSecondPhase()) {
                            int k = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                            int l = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                            BlockPos.MutableBlockPos blockpos$mutable = Apostle.this.blockPosition().mutable().move(k, 0, l);
                            blockpos$mutable.setX(blockpos$mutable.getX() + r.nextInt(5) - r.nextInt(5));
                            blockpos$mutable.setY((int) BlockFinder.moveDownToGround(Apostle.this));
                            blockpos$mutable.setZ(blockpos$mutable.getZ() + r.nextInt(5) - r.nextInt(5));
                            Malghast summonedentity = new Malghast(ModEntityType.MALGHAST.get(), Apostle.this.level);
                            if (serverLevel.noCollision(summonedentity, summonedentity.getBoundingBox().move(blockpos$mutable.above(2)).inflate(0.25F))){
                                summonedentity.setPos(blockpos$mutable.getX(), blockpos$mutable.getY() + 2, blockpos$mutable.getZ());
                            } else {
                                blockpos$mutable = Apostle.this.blockPosition().mutable();
                                summonedentity.setPos(blockpos$mutable.getX(), blockpos$mutable.getY() + 2, blockpos$mutable.getZ());
                            }
                            summonedentity.setTrueOwner(Apostle.this);
                            summonedentity.setLimitedLife(60 * (90 + Apostle.this.level.random.nextInt(180)));
                            summonedentity.finalizeSpawn(serverLevel, Apostle.this.level.getCurrentDifficultyAt(blockpos$mutable.above(2)), MobSpawnType.MOB_SUMMONED, null, null);
                            summonedentity.setTarget(livingentity);
                            SummonCircle summonCircle = new SummonCircle(Apostle.this.level, blockpos$mutable, summonedentity, false, false, Apostle.this);
                            Apostle.this.level.addFreshEntity(summonCircle);
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
                            summonedentity.finalizeSpawn(serverLevel, Apostle.this.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                            summonedentity.setTarget(livingentity);
                            SummonCircle summonCircle = new SummonCircle(Apostle.this.level, blockpos, summonedentity, false, true, Apostle.this);
                            Apostle.this.level.addFreshEntity(summonCircle);
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
                            summonedentity.finalizeSpawn(serverLevel, Apostle.this.level.getCurrentDifficultyAt(blockpos$mutable), MobSpawnType.MOB_SUMMONED, null, null);
                            summonedentity.setTarget(livingentity);
                            SummonCircle summonCircle = new SummonCircle(Apostle.this.level, blockpos$mutable, summonedentity, false, true, Apostle.this);
                            Apostle.this.level.addFreshEntity(summonCircle);
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
            int i = Apostle.this.level.getEntitiesOfClass(Owned.class, Apostle.this.getBoundingBox().inflate(64.0D), SKELETON_MINIONS).size();
            int i2 = Apostle.this.level.getEntitiesOfClass(FireTornado.class, Apostle.this.getBoundingBox().inflate(64.0D)).size();
            int cool = Apostle.this.spellStart();
            if (!super.canUse()) {
                return false;
            } else if (Apostle.this.isSettingUpSecond()){
                return false;
            } else if (Apostle.this.getHitTimes() >= 6){
                return i2 < 1;
            } else {
                return Apostle.this.getCoolDown() >= cool
                        && Apostle.this.getTornadoCoolDown() <= 0
                        && Apostle.this.getSpellCycle() == 3
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

                    while (blockpos$mutable.getY() > 0 && !Apostle.this.level.getBlockState(blockpos$mutable).blocksMotion()) {
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
                    && !Apostle.this.isSettingUpSecond();
        }

        protected int getCastingTime() {
            return 200;
        }

        protected int getCastingInterval() {
            return 120;
        }

        public void castSpell() {
            Apostle apostle = Apostle.this;
            apostle.resetHitTime();
            if (!apostle.isSecondPhase()){
                apostle.setFiring(true);
                apostle.coolDown = 0;
                apostle.playSound(ModSounds.ROAR_SPELL.get(), 1.0F, 1.0F);
            } else {
                double d0 = Math.min(apostle.getTarget().getY(), apostle.getY());
                double d1 = Math.max(apostle.getTarget().getY(), apostle.getY()) + 1.0D;
                for(int i = 0; i < 5; ++i) {
                    float f1 = f + (float)i * (float)Math.PI * 0.4F;
                    spawnBlasts(apostle,apostle.getX() + (double)Mth.cos(f1) * 1.5D, apostle.getZ() + (double)Mth.sin(f1) * 1.5D, d0, d1);
                }

                for(int k = 0; k < 8; ++k) {
                    float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
                    spawnBlasts(apostle,apostle.getX() + (double)Mth.cos(f2) * 2.5D, apostle.getZ() + (double)Mth.sin(f2) * 2.5D, d0, d1);
                }
                if (apostle.getHealth() < apostle.getMaxHealth()/2){
                    for(int k = 0; k < 11; ++k) {
                        float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
                        spawnBlasts(apostle,apostle.getX() + (double)Mth.cos(f2) * 2.5D, apostle.getZ() + (double)Mth.sin(f2) * 2.5D, d0, d1);
                    }
                }

                if (apostle.getHealth() < apostle.getMaxHealth()/4){
                    for(int k = 0; k < 14; ++k) {
                        float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
                        spawnBlasts(apostle,apostle.getX() + (double)Mth.cos(f2) * 2.5D, apostle.getZ() + (double)Mth.sin(f2) * 2.5D, d0, d1);
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
            BlockPos blockpos = BlockPos.containing(pPosX, pOPosY, pPosZ);
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
                    && !Apostle.this.isSettingUpSecond();
        }

        @Override
        public void tick() {
            Apostle.this.setSettingUpSecond(true);
        }
    }

    static class ApostleBowGoal<T extends Apostle> extends Goal{
        private final T mob;
        private final float attackRadiusSqr;
        private int attackTime = -1;
        private int seeTime;
        private boolean strafingClockwise;
        private boolean strafingBackwards;
        private int strafingTime = -1;

        public ApostleBowGoal(T p_25792_, float p_25795_) {
            this.mob = p_25792_;
            this.attackRadiusSqr = p_25795_ * p_25795_;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null && this.HaveBow() && !this.mob.isSettingUpSecond();
        }

        public boolean canContinueToUse() {
            return (this.canUse() || !this.mob.getNavigation().isDone()) && this.HaveBow() && !this.mob.isCasting();
        }

        public void start() {
            super.start();
            this.mob.setAggressive(true);
        }

        public void stop() {
            super.stop();
            this.mob.setAggressive(false);
            this.seeTime = 0;
            this.attackTime = -1;
            this.mob.stopUsingItem();
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null) {
                double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                boolean flag = this.mob.getSensing().hasLineOfSight(livingentity);
                boolean flag1 = this.seeTime > 0;
                if (flag != flag1) {
                    this.seeTime = 0;
                }

                if (flag) {
                    ++this.seeTime;
                } else {
                    --this.seeTime;
                }

                if (!(d0 > (double)this.attackRadiusSqr) && this.seeTime >= 20) {
                    this.mob.getNavigation().stop();
                    ++this.strafingTime;
                } else {
                    this.mob.getNavigation().moveTo(livingentity, 1.0F);
                    this.strafingTime = -1;
                }

                if (this.strafingTime >= 20) {
                    if ((double)this.mob.getRandom().nextFloat() < 0.3D) {
                        this.strafingClockwise = !this.strafingClockwise;
                    }

                    if ((double)this.mob.getRandom().nextFloat() < 0.3D) {
                        this.strafingBackwards = !this.strafingBackwards;
                    }

                    this.strafingTime = 0;
                }

                if (this.strafingTime > -1) {
                    if (d0 > (double)(this.attackRadiusSqr * 0.75F)) {
                        this.strafingBackwards = false;
                    } else if (d0 < (double)(this.attackRadiusSqr * 0.25F)) {
                        this.strafingBackwards = true;
                    }

                    this.mob.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                    this.mob.lookAt(livingentity, 30.0F, 30.0F);
                } else {
                    this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                }

                if (this.mob.isUsingItem()) {
                    if (!flag && this.seeTime < -60) {
                        this.mob.stopUsingItem();
                    } else if (flag) {
                        int i = this.mob.getTicksUsingItem();
                        if (i >= 20) {
                            this.mob.stopUsingItem();
                            this.mob.performRangedAttack(livingentity, BowItem.getPowerForTime(i));
                            Difficulty difficulty = this.mob.level.getDifficulty();
                            int attackIntervalMin = difficulty != Difficulty.HARD ? 10 : 5;
                            if (this.mob.isSecondPhase() || this.mob.level.dimension() == Level.NETHER) {
                                attackIntervalMin = 0;
                            }
                            this.attackTime = attackIntervalMin;
                        }
                    }
                } else if (--this.attackTime <= 0 && this.seeTime >= -60) {
                    this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof BowItem));
                }

            }
        }

        protected boolean HaveBow() {
            return this.mob.isHolding(item -> item.getItem() instanceof BowItem);
        }
    }

    static class ApostlePathNavigation extends GroundPathNavigation {
        ApostlePathNavigation(Apostle p_33969_, Level p_33970_) {
            super(p_33969_, p_33970_);
        }

        protected PathFinder createPathFinder(int p_33972_) {
            this.nodeEvaluator = new WalkNodeEvaluator();
            this.nodeEvaluator.setCanPassDoors(true);
            return new PathFinder(this.nodeEvaluator, p_33972_);
        }

        protected boolean hasValidPathType(BlockPathTypes p_33974_) {
            return p_33974_ == BlockPathTypes.LAVA || p_33974_ == BlockPathTypes.DAMAGE_FIRE || p_33974_ == BlockPathTypes.DANGER_FIRE || super.hasValidPathType(p_33974_);
        }

        public boolean isStableDestination(BlockPos p_33976_) {
            return this.level.getBlockState(p_33976_).is(Blocks.LAVA) || super.isStableDestination(p_33976_);
        }
    }

    public boolean canChangeDimensions() {
        return false;
    }

}
