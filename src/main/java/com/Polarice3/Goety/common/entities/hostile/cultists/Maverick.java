package com.Polarice3.Goety.common.entities.hostile.cultists;

import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ModLootTables;
import com.Polarice3.Goety.utils.WitchBarterHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableWitchTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestHealableRaiderTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class Maverick extends Cultist{
    private static final UUID SPEED_MODIFIER_DRINKING_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier SPEED_MODIFIER_DRINKING = new AttributeModifier(SPEED_MODIFIER_DRINKING_UUID, "Drinking speed penalty", -0.25D, AttributeModifier.Operation.ADDITION);
    private static final EntityDataAccessor<Boolean> DATA_USING_ITEM = SynchedEntityData.defineId(Maverick.class, EntityDataSerializers.BOOLEAN);
    private int usingTime;
    private int fleeTime;
    private NearestHealableRaiderTargetGoal<Raider> healRaidersGoal;
    private NearestAttackableWitchTargetGoal<Player> attackPlayersGoal;

    public Maverick(EntityType<? extends Cultist> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.healRaidersGoal = new NearestHealableRaiderTargetGoal<>(this, Raider.class, true, (target) -> {
            return target != null
                    && target.getHealth() > 10.0F
                    && this.hasActiveRaid()
                    && !(target instanceof Witch)
                    && !(target instanceof Cultist);
        });
        this.attackPlayersGoal = new NearestAttackableWitchTargetGoal<>(this, Player.class, 10, true, false, null);
        this.goalSelector.addGoal(1, new MaverickBarterGoal(this));
        this.goalSelector.addGoal(1, new AvoidTargetGoal<>(this, LivingEntity.class, 8, 1.0D, 1.2D){
            @Override
            public boolean canUse() {
                return Maverick.this.fleeTime > 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, true));
        this.targetSelector.addGoal(2, this.healRaidersGoal);
        this.targetSelector.addGoal(3, this.attackPlayersGoal);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 26.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_USING_ITEM, false);
    }

    public void setUsingItem(boolean p_34164_) {
        this.getEntityData().set(DATA_USING_ITEM, p_34164_);
    }

    public boolean isDrinkingPotion() {
        return this.getEntityData().get(DATA_USING_ITEM);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.MAVERICK_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return ModSounds.MAVERICK_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.MAVERICK_DEATH.get();
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.MAVERICK_CELEBRATE.get();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_37856_, DifficultyInstance p_37857_, MobSpawnType p_37858_, @Nullable SpawnGroupData p_37859_, @Nullable CompoundTag p_37860_) {
        SpawnGroupData spawnGroupData = super.finalizeSpawn(p_37856_, p_37857_, p_37858_, p_37859_, p_37860_);
        this.populateDefaultEquipmentSlots(p_37856_.getRandom(), p_37857_);
        return spawnGroupData;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource p_217055_, DifficultyInstance p_217056_) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
    }

    @Override
    public CultistArmPose getArmPose() {
        if (this.isDrinkingPotion() || WitchBarterHelper.getTrader(this) != null){
            return CultistArmPose.ITEM;
        } else if (this.isAggressive()){
            return CultistArmPose.ATTACKING;
        } else if (this.isCelebrating()){
            return CultistArmPose.SPELLCASTING;
        }
        return CultistArmPose.NEUTRAL;
    }

    public void aiStep() {
        if (!this.level.isClientSide && this.isAlive()) {
            this.setAggressive(this.getTarget() != null);
            if (this.fleeTime > 0){
                --this.fleeTime;
            }
            this.healRaidersGoal.decrementCooldown();
            this.attackPlayersGoal.setCanAttack(this.healRaidersGoal.getCooldown() <= 0);
            AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (this.isDrinkingPotion()) {
                int i = this.usingTime;
                if (i % 4 == 0) {
                    if (!this.isSilent()) {
                        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_DRINK, this.getSoundSource(), 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
                    }
                }
                if (this.usingTime-- <= 0) {
                    this.setUsingItem(false);
                    ItemStack itemstack = this.getOffhandItem();
                    this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    if (itemstack.is(Items.POTION)) {
                        List<MobEffectInstance> list = PotionUtils.getMobEffects(itemstack);
                        if (!list.isEmpty()) {
                            for(MobEffectInstance mobeffectinstance : list) {
                                this.addEffect(new MobEffectInstance(mobeffectinstance));
                            }
                        }
                    }

                    if (attributeinstance != null) {
                        attributeinstance.removeModifier(SPEED_MODIFIER_DRINKING);
                    }
                }
            } else {
                Potion potion = null;
                if (this.random.nextFloat() < 0.15F && this.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) && !this.hasEffect(MobEffects.WATER_BREATHING)) {
                    potion = Potions.WATER_BREATHING;
                } else if (this.random.nextFloat() < 0.15F && (this.isOnFire() || this.getLastDamageSource() != null && this.getLastDamageSource().isFire()) && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                    potion = Potions.FIRE_RESISTANCE;
                } else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth() && ((this.getTarget() == null) || (this.getTarget() != null && this.getTarget().distanceTo(this) >= 8.0D))) {
                    if (this.random.nextFloat() <= 0.25F && !this.hasEffect(MobEffects.REGENERATION)){
                        potion = Potions.REGENERATION;
                    } else {
                        potion = Potions.HEALING;
                    }
                } else if (this.random.nextFloat() < 0.5F && this.getTarget() != null && !this.hasEffect(MobEffects.MOVEMENT_SPEED) && this.getTarget().distanceTo(this) >= 16.0D) {
                    potion = Potions.SWIFTNESS;
                }

                if (potion != null) {
                    this.setItemSlot(EquipmentSlot.OFFHAND, PotionUtils.setPotion(new ItemStack(Items.POTION), potion));
                    this.usingTime = this.getOffhandItem().getUseDuration();
                    this.setUsingItem(true);

                    if (attributeinstance != null) {
                        attributeinstance.removeModifier(SPEED_MODIFIER_DRINKING);
                        attributeinstance.addTransientModifier(SPEED_MODIFIER_DRINKING);
                    }
                }
            }
        }

        if (this.random.nextFloat() < 7.5E-4F) {
            this.level.broadcastEntityEvent(this, (byte)15);
        }

        super.aiStep();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        boolean flag = super.hurt(pSource, pAmount);
        if (flag && (this.getHealth() <= this.getMaxHealth() / 2.0F)){
            if (this.fleeTime <= 0) {
                this.fleeTime = MathHelper.secondsToTicks(1);
            }
        }
        return flag;
    }

    @Override
    public boolean doHurtTarget(Entity p_21372_) {
        boolean flag = super.doHurtTarget(p_21372_);
        Potion potion = Potions.HARMING;
        if (flag && p_21372_ instanceof LivingEntity livingEntity) {
            if (livingEntity instanceof Raider raider && this.hasActiveRaid() && raider.getTarget() != this) {
                double attack = this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                if (livingEntity.getHealth() <= attack + 1.0D) {
                    potion = Potions.HEALING;
                } else {
                    potion = Potions.REGENERATION;
                }
                this.setTarget(null);
            } else {
                if (!livingEntity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                    potion = Potions.SLOWNESS;
                } else if (livingEntity.getHealth() >= 8.0F && !livingEntity.hasEffect(MobEffects.POISON) && livingEntity.canBeAffected(new MobEffectInstance(MobEffects.POISON))) {
                    potion = Potions.POISON;
                } else if (!livingEntity.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
                    potion = Potions.WEAKNESS;
                } else if (livingEntity.isInvertedHealAndHarm()) {
                    potion = Potions.HEALING;
                }
            }

            for (MobEffectInstance instance : potion.getEffects()){
                livingEntity.addEffect(new MobEffectInstance(instance));
            }
            if (!livingEntity.isSprinting()) {
                if (this.fleeTime <= 0) {
                    this.fleeTime = MathHelper.secondsToTicks(1);
                }
            }
        }
        return flag;
    }

    protected float getDamageAfterMagicAbsorb(DamageSource damageSource, float damage) {
        damage = super.getDamageAfterMagicAbsorb(damageSource, damage);
        if (damageSource.getEntity() == this) {
            damage = 0.0F;
        }

        if (damageSource.isMagic()) {
            damage *= 0.15F;
        }

        return damage;
    }

    public void handleEntityEvent(byte p_34138_) {
        if (p_34138_ == 15) {
            for(int i = 0; i < this.random.nextInt(35) + 10; ++i) {
                this.level.addParticle(ParticleTypes.WITCH, this.getX() + this.random.nextGaussian() * (double)0.13F, this.getBoundingBox().maxY + 0.5D + this.random.nextGaussian() * (double)0.13F, this.getZ() + this.random.nextGaussian() * (double)0.13F, 0.0D, 0.0D, 0.0D);
            }
        } else {
            super.handleEntityEvent(p_34138_);
        }

    }

    public static class MaverickBarterGoal extends Goal {
        private int progress = 100;
        public Maverick maverick;

        public MaverickBarterGoal(Maverick maverick) {
            this.maverick = maverick;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public void tick() {
            this.maverick.setTarget(null);
            LivingEntity trader = WitchBarterHelper.getTrader(maverick);
            if (--this.progress > 0) {
                this.maverick.getNavigation().stop();
                if (trader != null && this.maverick.distanceTo(trader) <= 16.0F) {
                    this.maverick.getLookControl().setLookAt(trader);
                }
            }
            if (this.progress <= 0) {
                Vec3 vec3 = trader != null ? trader.position() : this.maverick.position();
                if (!this.maverick.level.isClientSide) {
                    if (this.maverick.level.getServer() != null) {
                        float luck = 0.0F;
                        if (this.maverick.getOffhandItem().is(ModTags.Items.WITCH_BETTER_CURRENCY)){
                            luck = 1.0F;
                        }
                        LootTable loottable = this.maverick.level.getServer().getLootTables().get(ModLootTables.MAVERICK_BARTER);
                        List<ItemStack> list = loottable.getRandomItems((new LootContext.Builder((ServerLevel) this.maverick.level)).withParameter(LootContextParams.THIS_ENTITY, this.maverick).withParameter(LootContextParams.ORIGIN, this.maverick.position()).withLuck(luck).withRandom(this.maverick.level.random).create(LootContextParamSets.GIFT));
                        for(ItemStack itemstack : list) {
                            BehaviorUtils.throwItem(this.maverick, itemstack, vec3.add(0.0D, 1.0D, 0.0D));
                        }
                    }
                }
                this.clearTrade();
            }

            if (this.maverick.hurtTime != 0){
                if (this.maverick.getItemInHand(InteractionHand.OFF_HAND).is(ModTags.Items.WITCH_CURRENCY)
                        || this.maverick.getItemInHand(InteractionHand.OFF_HAND).is(ModTags.Items.WITCH_BETTER_CURRENCY)) {
                    this.maverick.spawnAtLocation(this.maverick.getItemInHand(InteractionHand.OFF_HAND));
                    this.clearTrade();
                }
            }
        }

        protected void addParticlesAroundSelf(ParticleOptions p_35288_) {
            if (!this.maverick.level.isClientSide) {
                ServerLevel serverLevel = (ServerLevel) this.maverick.level;
                for (int i = 0; i < 5; ++i) {
                    double d0 = this.maverick.getRandom().nextGaussian() * 0.02D;
                    double d1 = this.maverick.getRandom().nextGaussian() * 0.02D;
                    double d2 = this.maverick.getRandom().nextGaussian() * 0.02D;
                    serverLevel.sendParticles(p_35288_, this.maverick.getRandomX(1.0D), this.maverick.getRandomY() + 1.0D, this.maverick.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                }
            }

        }

        @Override
        public boolean canUse() {
            return this.maverick.getOffhandItem().is(ModTags.Items.WITCH_CURRENCY) || this.maverick.getOffhandItem().is(ModTags.Items.WITCH_BETTER_CURRENCY);
        }

        @Override
        public void start(){
            super.start();
            this.progress = 100;
            WitchBarterHelper.setTimer(this.maverick, 5);
            this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
        }

        public void clearTrade(){
            this.maverick.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            WitchBarterHelper.setTimer(this.maverick, 5);
            WitchBarterHelper.setTrader(this.maverick, null);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
