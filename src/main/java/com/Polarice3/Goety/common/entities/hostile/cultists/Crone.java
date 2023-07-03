package com.Polarice3.Goety.common.entities.hostile.cultists;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.effects.brew.*;
import com.Polarice3.Goety.common.effects.brew.block.HarvestBlockEffect;
import com.Polarice3.Goety.common.effects.brew.block.SweetBerriedEffect;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.WitchBarterGoal;
import com.Polarice3.Goety.common.entities.neutral.VampireBat;
import com.Polarice3.Goety.common.entities.projectiles.ThrownBrew;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BrewUtils;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableWitchTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestHealableRaiderTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class Crone extends Cultist implements RangedAttackMob {
    private static final UUID SPEED_MODIFIER_DRINKING_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier SPEED_MODIFIER_DRINKING = new AttributeModifier(SPEED_MODIFIER_DRINKING_UUID, "Drinking speed penalty", -0.25D, AttributeModifier.Operation.ADDITION);
    private static final EntityDataAccessor<Boolean> DATA_USING_ITEM = SynchedEntityData.defineId(Crone.class, EntityDataSerializers.BOOLEAN);
    private int usingTime;
    private int hitTimes;
    private int lastHitTime;
    private int overwhelmed;
    private final ModServerBossInfo bossInfo = new ModServerBossInfo(this.getUUID(), this.getDisplayName(), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(false).setCreateWorldFog(false);
    private NearestHealableRaiderTargetGoal<Raider> healRaidersGoal;
    private NearestAttackableWitchTargetGoal<Player> attackPlayersGoal;

    public Crone(EntityType<? extends Cultist> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = 99;
        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        if (this.level.isClientSide){
            Goety.PROXY.addBoss(this);
        }
    }

    protected void registerGoals() {
        super.registerGoals();
        this.healRaidersGoal = new NearestHealableRaiderTargetGoal<>(this, Raider.class, true, (target) -> {
            return target != null && this.hasActiveRaid() && target.getType() != EntityType.WITCH && target.getType() != ModEntityType.CRONE.get() && target.getType() != ModEntityType.WARLOCK.get();
        });
        this.attackPlayersGoal = new NearestAttackableWitchTargetGoal<>(this, Player.class, 10, true, false, (Predicate<LivingEntity>)null);
        this.goalSelector.addGoal(2, new BrewThrowsGoal(this));
        this.goalSelector.addGoal(2, new FastBrewThrowsGoal(this));
        this.goalSelector.addGoal(1, new WitchBarterGoal(this));
        this.goalSelector.addGoal(1, new CroneTeleportGoal(this));
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class));
        this.targetSelector.addGoal(2, this.healRaidersGoal);
        this.targetSelector.addGoal(3, this.attackPlayersGoal);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.CroneHealth.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    public AttributeSupplier.Builder getConfiguredAttributes(){
        return setCustomAttributes();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_USING_ITEM, false);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("hitTimes", this.hitTimes);
        pCompound.putInt("lastHitTime", this.lastHitTime);
        pCompound.putInt("overwhelmed", this.overwhelmed);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.hitTimes = pCompound.getInt("hitTimes");
        this.lastHitTime = pCompound.getInt("lastHitTime");
        this.overwhelmed = pCompound.getInt("overwhelmed");
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
        this.bossInfo.setVisible(this.getTarget() != null);
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

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.populateDefaultEquipmentSlots(worldIn.getRandom(), difficultyIn);
        this.populateDefaultEquipmentEnchantments(worldIn.getRandom(), difficultyIn);
        if (!this.hasCustomName()){
            if (this.getTarget() == null){
                this.bossInfo.setVisible(false);
            }
            int random = this.random.nextInt(4);
            int random2;
            if (random == 0){
                random2 = 12 + this.random.nextInt(6);
            } else {
                random2 = this.random.nextInt(12);
            }
            Component component = Component.translatable("title.goety.crone." + random);
            Component component1 = Component.translatable("name.goety.crone." + random2);
            this.setCustomName(Component.translatable(component.getString() + " " +  component1.getString()));
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.CRONE_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_34154_) {
        return SoundEvents.WITCH_HURT;
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.CRONE_DEATH.get();
    }

    public void setUsingItem(boolean p_34164_) {
        this.getEntityData().set(DATA_USING_ITEM, p_34164_);
    }

    public boolean isDrinkingPotion() {
        return this.getEntityData().get(DATA_USING_ITEM);
    }

    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        ItemEntity itementity = this.spawnAtLocation(ModItems.CRONE_HAT.get());
        if (itementity != null) {
            itementity.setExtendedLifetime();
        }

    }

    @Override
    public void remove(Entity.RemovalReason p_146834_) {
        if (this.level.isClientSide) {
            Goety.PROXY.removeBoss(this);
        }
        super.remove(p_146834_);
    }

    public void aiStep() {
        if (!this.level.isClientSide && this.isAlive()) {
            if (this.lastHitTime > 0){
                --this.lastHitTime;
            }
            this.healRaidersGoal.decrementCooldown();
            this.attackPlayersGoal.setCanAttack(this.healRaidersGoal.getCooldown() <= 0);

            if (this.isDrinkingPotion()) {
                if (this.usingTime-- <= 0) {
                    this.setUsingItem(false);
                    ItemStack itemstack = this.getMainHandItem();
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    if (itemstack.is(ModItems.BREW.get())) {
                        List<MobEffectInstance> list = PotionUtils.getMobEffects(itemstack);
                        for (MobEffectInstance mobeffectinstance : list) {
                            this.addEffect(new MobEffectInstance(mobeffectinstance));
                        }
                        List<BrewEffectInstance> list1 = BrewUtils.getBrewEffects(itemstack);
                        for (BrewEffectInstance brewEffectInstance : list1) {
                            brewEffectInstance.getEffect().drinkBlockEffect(this, this, this, brewEffectInstance.getAmplifier(), BrewUtils.getAreaOfEffect(itemstack));
                        }
                    }

                    this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_MODIFIER_DRINKING);
                }
            } else {
                int amp = 0;
                if (this.level.random.nextFloat() <= 0.05F && this.level.getDifficulty() == Difficulty.NORMAL){
                    amp = 2;
                } else if (this.level.random.nextFloat() <= 0.25F){
                    amp = 1;
                }
                List<MobEffectInstance> mobEffectInstance = new ArrayList<>();
                List<BrewEffectInstance> brewEffectInstance = new ArrayList<>();
                if (this.random.nextFloat() < 0.15F && (this.isInWall() || (this.getLastDamageSource() != null && this.getLastDamageSource() == DamageSource.IN_WALL))){
                    brewEffectInstance.add(new BrewEffectInstance(new BlindJumpBrewEffect(0), 1, amp));
                } else if (this.random.nextFloat() < 0.15F && this.getLastDamageSource() != null && (this.getLastDamageSource() == DamageSource.CACTUS || this.getLastDamageSource() == DamageSource.SWEET_BERRY_BUSH)){
                    brewEffectInstance.add(new BrewEffectInstance(new HarvestBlockEffect()));
                } else if (this.random.nextFloat() < 0.15F && this.getHealth() < this.getMaxHealth() && (this.getTarget() == null || this.lastHitTime == 0)) {
                    mobEffectInstance.add(new MobEffectInstance(MobEffects.HEAL, 1, amp));
                } else if (this.random.nextFloat() < 0.15F && this.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) && !this.hasEffect(MobEffects.WATER_BREATHING)) {
                    mobEffectInstance.add(new MobEffectInstance(MobEffects.WATER_BREATHING, 3600));
                    mobEffectInstance.add(new MobEffectInstance(GoetyEffects.SWIFT_SWIM.get(), 3600));
                } else if (this.random.nextFloat() < 0.15F && (this.isOnFire() || this.getLastDamageSource() != null && this.getLastDamageSource().isFire()) && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                    mobEffectInstance.add(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600));
                } else if (this.random.nextFloat() < 0.15F && this.getLastDamageSource() != null && this.getLastDamageSource().isFall() && !this.hasEffect(MobEffects.SLOW_FALLING)){
                    mobEffectInstance.add(new MobEffectInstance(MobEffects.SLOW_FALLING, 3600));
                } else if (this.random.nextFloat() < 0.15F && this.getLastDamageSource() != null && ModDamageSource.physicalAttacks(this.getLastDamageSource()) && !this.hasEffect(GoetyEffects.REPULSIVE.get())){
                    mobEffectInstance.add(new MobEffectInstance(GoetyEffects.REPULSIVE.get(), 1800 / (amp + 1), amp));
                } else if (this.random.nextFloat() < 0.15F && this.getTarget() != null) {
                    if ((this.random.nextFloat() <= 0.15F && this.getTarget().distanceTo(this) <= 4.0F) || this.getHealth() <= 15.0F){
                        brewEffectInstance.add(new BrewEffectInstance(new BlindJumpBrewEffect(0), 1, amp));
                    } else if (this.random.nextFloat() <= 0.15F && !this.hasEffect(MobEffects.REGENERATION)){
                        mobEffectInstance.add(new MobEffectInstance(MobEffects.REGENERATION, 900 / (amp + 1), amp));
                        if (this.random.nextFloat() < 0.25F && MobUtil.isInSunlight(this) && !this.hasEffect(GoetyEffects.PHOTOSYNTHESIS.get())){
                            mobEffectInstance.add(new MobEffectInstance(GoetyEffects.PHOTOSYNTHESIS.get(), 1800));
                        }
                    } else if (this.random.nextFloat() < 0.05F && !this.hasEffect(MobEffects.DAMAGE_RESISTANCE)){
                        mobEffectInstance.add(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1800));
                    } else if (!this.hasEffect(GoetyEffects.FIERY_AURA.get()) && !this.getTarget().hasEffect(GoetyEffects.FREEZING.get()) && !this.getTarget().hasEffect(MobEffects.FIRE_RESISTANCE) && !this.getTarget().fireImmune() && this.random.nextFloat() < 0.05F) {
                        mobEffectInstance.add(new MobEffectInstance(GoetyEffects.FIERY_AURA.get(), 1800 / (amp + 1), amp));
                    }
                } else if (this.random.nextFloat() <= 0.15F && MobUtil.isInWeb(this) && !this.hasEffect(GoetyEffects.CLIMBING.get())){
                    mobEffectInstance.add(new MobEffectInstance(GoetyEffects.CLIMBING.get(), 3600));
                } else if (this.random.nextFloat() < 0.05F && MobUtil.hasLongNegativeEffects(this)) {
                    brewEffectInstance.add(new BrewEffectInstance(new PurifyBrewEffect("purify_debuff", 0, 0, MobEffectCategory.BENEFICIAL, 0x385858, true)));
                }

                if (!mobEffectInstance.isEmpty() || !brewEffectInstance.isEmpty()) {
                    ItemStack brew = BrewUtils.setCustomEffects(new ItemStack(ModItems.BREW.get()), mobEffectInstance, brewEffectInstance);
                    BrewUtils.setAreaOfEffect(brew, this.level.random.nextInt(amp + 1));
                    brew.getOrCreateTag().putInt("CustomPotionColor", BrewUtils.getColor(mobEffectInstance, brewEffectInstance));
                    this.setItemSlot(EquipmentSlot.MAINHAND, brew);
                    this.usingTime = this.overwhelmed > 0 ? this.getMainHandItem().getUseDuration() / 2 : this.getMainHandItem().getUseDuration();
                    this.setUsingItem(true);
                    if (!this.isSilent()) {
                        this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_DRINK, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
                    }

                    AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
                    attributeinstance.removeModifier(SPEED_MODIFIER_DRINKING);
                    attributeinstance.addTransientModifier(SPEED_MODIFIER_DRINKING);
                }
            }

            if (this.random.nextFloat() < 7.5E-4F) {
                this.level.broadcastEntityEvent(this, (byte)15);
            }
        }

        super.aiStep();
    }

    public SoundEvent getCelebrateSound() {
        return ModSounds.CRONE_LAUGH.get();
    }

    public void handleEntityEvent(byte p_34138_) {
        if (p_34138_ == 15) {
            for(int i = 0; i < this.random.nextInt(35) + 10; ++i) {
                this.level.addParticle(ParticleTypes.WITCH, this.getX() + this.random.nextGaussian() * (double)0.13F, this.getBoundingBox().maxY + 0.5D + this.random.nextGaussian() * (double)0.13F, this.getZ() + this.random.nextGaussian() * (double)0.13F, 0.0D, 0.0D, 0.0D);
            }
        } else if (p_34138_ == 46){
            int i = 128;

            for(int j = 0; j < i; ++j) {
                double d0 = (double)j / 127.0D;
                float f = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (this.random.nextFloat() - 0.5F) * 0.2F;
                double d1 = Mth.lerp(d0, this.xo, this.getX()) + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth() * 2.0D;
                double d2 = Mth.lerp(d0, this.yo, this.getY()) + this.random.nextDouble() * (double)this.getBbHeight();
                double d3 = Mth.lerp(d0, this.zo, this.getZ()) + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth() * 2.0D;
                this.level.addParticle(ParticleTypes.PORTAL, d1, d2, d3, (double)f, (double)f1, (double)f2);
            }
        } else {
            super.handleEntityEvent(p_34138_);
        }
    }

    protected float getDamageAfterMagicAbsorb(DamageSource p_34149_, float p_34150_) {
        p_34150_ = super.getDamageAfterMagicAbsorb(p_34149_, p_34150_);
        if (p_34149_.getEntity() == this) {
            p_34150_ = 0.0F;
        }

        if (p_34149_.isMagic()) {
            p_34150_ *= 0.15F;
        }

        return p_34150_;
    }

    public void performRangedAttack(LivingEntity target, float p_34144_) {
        if (!this.isDrinkingPotion()) {
            Vec3 vec3 = target.getDeltaMovement();
            double d0 = target.getX() + vec3.x - this.getX();
            double d1 = target.getEyeY() - (double)1.1F - this.getY();
            double d2 = target.getZ() + vec3.z - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            int amp = 0;
            if (this.level.random.nextFloat() <= 0.05F && this.level.getDifficulty() == Difficulty.NORMAL){
                amp = 2;
            } else if (this.level.random.nextFloat() <= 0.25F){
                amp = 1;
            }
            List<MobEffectInstance> mobEffectInstance = new ArrayList<>();
            List<BrewEffectInstance> brewEffectInstance = new ArrayList<>();
            if (target instanceof Raider) {
                if (target.getHealth() <= 4.0F) {
                    mobEffectInstance.add(new MobEffectInstance(MobEffects.HEAL, 1));
                } else {
                    mobEffectInstance.add(new MobEffectInstance(MobEffects.REGENERATION, 900));
                    if (this.random.nextFloat() <= 0.05F) {
                        mobEffectInstance.add(new MobEffectInstance(MobEffects.ABSORPTION, 1800));
                    }
                }
                this.setTarget(null);
            } else if (d3 >= 8.0D && !target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                mobEffectInstance.add(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1800 / (amp + 1), amp));
                if (this.random.nextFloat() <= 0.25F && target.isOnGround()){
                    brewEffectInstance.add(new BrewEffectInstance(new SweetBerriedEffect(), 1, amp));
                }
                if (this.random.nextFloat() <= 0.25F && this.noBrewMinions(target)){
                    brewEffectInstance.add(new BrewEffectInstance(new WebbedBrewEffect(0, 0), 1, amp));
                } else if (this.random.nextFloat() <= 0.5F){
                    if (this.random.nextBoolean()) {
                        brewEffectInstance.add(new BrewEffectInstance(new TransposeBrewEffect()));
                    }
                }
            } else if (target.getHealth() >= 8.0F && !target.hasEffect(MobEffects.POISON)) {
                mobEffectInstance.add(new MobEffectInstance(MobEffects.POISON, 900 / (amp + 1), amp));
                if (this.random.nextFloat() <= 0.25F && !this.hasEffect(GoetyEffects.FIERY_AURA.get()) && !target.hasEffect(GoetyEffects.FREEZING.get())){
                    mobEffectInstance.add(new MobEffectInstance(GoetyEffects.FREEZING.get(), 900 / (amp + 1), amp));
                } else if (this.random.nextFloat() <= 0.5F && !target.hasEffect(GoetyEffects.TRIPPING.get())){
                    mobEffectInstance.add(new MobEffectInstance(GoetyEffects.TRIPPING.get(), 1800 / (amp + 1), amp));
                }
            } else if (this.getLastDamageSource() != null
                    && ModDamageSource.physicalAttacks(this.getLastDamageSource())
                    && !target.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
                mobEffectInstance.add(new MobEffectInstance(MobEffects.WEAKNESS, 1800 / (amp + 1), amp));
                if (this.random.nextFloat() <= 0.25F){
                    brewEffectInstance.add(new BrewEffectInstance(new TransposeBrewEffect(), 1, amp));
                } else if (this.random.nextFloat() <= 0.5F
                        && !target.fireImmune()
                        && MobUtil.isInSunlight(target)
                        && !target.hasEffect(MobEffects.FIRE_RESISTANCE)
                        && !target.hasEffect(GoetyEffects.SUN_ALLERGY.get())){
                    mobEffectInstance.add(new MobEffectInstance(GoetyEffects.SUN_ALLERGY.get(), 3600 / (amp + 1), amp));
                } else if (this.random.nextFloat() <= 0.75F
                        && target.level.getLightLevelDependentMagicValue(target.blockPosition()) < 0.1
                        && !target.hasEffect(GoetyEffects.NYCTOPHOBIA.get())){
                    mobEffectInstance.add(new MobEffectInstance(GoetyEffects.NYCTOPHOBIA.get(), 1800 / (amp + 1), amp));
                } else if (!target.hasEffect(GoetyEffects.SAPPED.get())) {
                    mobEffectInstance.add(new MobEffectInstance(GoetyEffects.SAPPED.get(), 1800 / (amp + 1), amp));
                }
            } else {
                if (this.random.nextFloat() <= 0.05F){
                    brewEffectInstance.add(new BrewEffectInstance(new StripBrewEffect(0, 0)));
                } else if (this.random.nextFloat() <= 0.25F){
                    if (target.isOnGround()) {
                        brewEffectInstance.add(new BrewEffectInstance(new ThornTrapBrewEffect(0), 1, amp));
                    } else if (!target.hasEffect(MobEffects.LEVITATION)){
                        mobEffectInstance.add(new MobEffectInstance(GoetyEffects.PLUNGE.get(), 900 / (amp + 1), amp));
                    }
                } else if (this.random.nextFloat() <= 0.35F && !target.hasEffect(MobEffects.BLINDNESS) && !MobUtil.isInWeb(target) && target.getMaxHealth() > 10.0F && this.noBrewMinions(target)){
                    mobEffectInstance.add(new MobEffectInstance(MobEffects.BLINDNESS, MathHelper.secondsToTicks(5)));
                    brewEffectInstance.add(new BrewEffectInstance(new BatsBrewEffect(0, 0)));
                } else if (this.random.nextFloat() <= 0.55F){
                    brewEffectInstance.add(new BrewEffectInstance(new LaunchBrewEffect(), 1));
                } else if (this.random.nextFloat() <= 0.75F && !MobUtil.isInWeb(target) && target.getMaxHealth() > 10.0F && this.noBrewMinions(target)){
                    brewEffectInstance.add(new BrewEffectInstance(new BeesBrewEffect(0, 0)));
                } else {
                    mobEffectInstance.add(new MobEffectInstance(MobEffects.HARM, 1, amp));
                }
            }

            if (!mobEffectInstance.isEmpty() || !brewEffectInstance.isEmpty()) {
                ThrownBrew thrownBrew = new ThrownBrew(this.level, this);
                ItemStack brew = BrewUtils.setCustomEffects(new ItemStack(ModItems.SPLASH_BREW.get()), mobEffectInstance, brewEffectInstance);
                BrewUtils.setAreaOfEffect(brew, this.level.random.nextInt(amp + 1));
                brew.getOrCreateTag().putInt("CustomPotionColor", BrewUtils.getColor(mobEffectInstance, brewEffectInstance));
                thrownBrew.setItem(brew);
                float velocity = 0.75F;
                if (target.distanceTo(this) >= 4.0F) {
                    thrownBrew.setXRot(thrownBrew.getXRot() + 20.0F);
                } else {
                    thrownBrew.setXRot(thrownBrew.getXRot());
                    velocity = 1.0F;
                }
                thrownBrew.shoot(d0, d1 + d3 * 0.2D, d2, velocity, 8.0F);
                if (!this.isSilent()) {
                    this.level.playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
                }

                this.level.addFreshEntity(thrownBrew);
            }
        }
    }

    public boolean noBrewMinions(LivingEntity livingEntity){
        return this.level.getEntitiesOfClass(VampireBat.class, livingEntity.getBoundingBox().inflate(2.0D)).isEmpty()
                && this.level.getEntitiesOfClass(Bee.class, livingEntity.getBoundingBox().inflate(2.0D)).isEmpty();
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        this.lastHitTime = MathHelper.secondsToTicks(15);

        if (pAmount >= 15){
            this.overwhelmed = MathHelper.secondsToTicks(15);
        }

        if (this.getHealth() <= 10.0F){
            if (pSource == DamageSource.CACTUS || pSource == DamageSource.SWEET_BERRY_BUSH || pSource.isMagic()){
                return false;
            }
        }

        if (!pSource.isExplosion() && !pSource.isMagic() && pSource.getEntity() instanceof LivingEntity livingentity && livingentity != this) {
            float thorn = 2.0F;
            if (this.level.getDifficulty() == Difficulty.HARD){
                thorn *= 2.0F;
            }
            livingentity.hurt(DamageSource.thorns(this), thorn);
        }

        return super.hurt(pSource, pAmount);
    }

    protected void teleport() {
        if (!this.level.isClientSide() && this.isAlive()) {
            for(int i = 0; i < 128; ++i) {
                double d3 = this.getX() + (this.getRandom().nextDouble() - 0.5D) * 32.0D;
                double d4 = this.getY();
                if (this.getTarget() != null){
                    d4 = this.getTarget().getY();
                }
                double d5 = this.getZ() + (this.getRandom().nextDouble() - 0.5D) * 32.0D;
                if (this.getHealth() <= 0.0F){
                    break;
                }
                if (this.randomTeleport(d3, d4, d5, false)) {
                    this.teleportHits();
                    break;
                }
            }
        }
    }

    private boolean teleportTowards(Entity entity) {
        if (!this.level.isClientSide() && this.isAlive()) {
            for(int i = 0; i < 128; ++i) {
                Vec3 vector3d = new Vec3(this.getX() - entity.getX(), this.getY(0.5D) - entity.getEyeY(), this.getZ() - entity.getZ());
                vector3d = vector3d.normalize();
                double d0 = 16.0D;
                double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.x * d0;
                double d2 = this.getY() + (double)(this.random.nextInt(16) - 8) - vector3d.y * d0;
                double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.z * d0;
                if (this.getHealth() <= 0.0F){
                    return false;
                }
                if (this.randomTeleport(d1, d2, d3, true)) {
                    this.teleportHits();
                    return true;
                }
            }
        }
        return false;
    }

    public void teleportHits(){
        this.level.broadcastEntityEvent(this, (byte) 46);
        this.level.gameEvent(GameEvent.TELEPORT, this.position(), GameEvent.Context.of(this));
        if (!this.isSilent()) {
            this.level.playSound((Player) null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 0.75F);
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 0.75F);
        }
    }

    static class CroneTeleportGoal extends Goal {
        private final Crone crone;
        private int teleportTime;

        public CroneTeleportGoal(Crone p_32573_) {
            this.crone = p_32573_;
        }

        public boolean canUse() {
            return this.crone.getTarget() != null;
        }

        public void start() {
            super.start();
            this.teleportTime = 0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.crone.getTarget() != null;
        }

        public void tick() {
            super.tick();
            if (this.crone.getTarget() != null && !this.crone.isPassenger()) {
                if ((this.crone.getTarget().distanceToSqr(this.crone) > 256 || !MobUtil.hasVisualLineOfSight(this.crone, this.crone.getTarget())) && this.teleportTime++ >= this.adjustedTickDelay(30) && this.crone.teleportTowards(this.crone.getTarget())) {
                    this.teleportTime = 0;
                }
            }
        }
    }

    static class BrewThrowsGoal extends RangedAttackGoal{
        public Crone crone;

        public BrewThrowsGoal(Crone p_25773_) {
            super(p_25773_, 1.0D, 20, 40, 10.0F);
            this.crone = p_25773_;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && (this.crone.getHealth() >= (this.crone.getMaxHealth() / 4) || (this.crone.getTarget() instanceof Raider && this.crone.hasActiveRaid()));
        }
    }

    static class FastBrewThrowsGoal extends RangedAttackGoal{
        public Crone crone;

        public FastBrewThrowsGoal(Crone p_25773_) {
            super(p_25773_, 1.0D, 15, 30, 10.0F);
            this.crone = p_25773_;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.crone.getHealth() < (this.crone.getMaxHealth() / 4) && !(this.crone.getTarget() instanceof Raider && this.crone.hasActiveRaid());
        }
    }
}
