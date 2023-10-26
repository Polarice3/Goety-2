package com.Polarice3.Goety.common.entities.hostile.illagers;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.init.ModSounds;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class Inquillager extends HuntingIllagerEntity{
    public int coolDown;
    public int healTimes;

    public Inquillager(EntityType<? extends Inquillager> p_i48556_1_, Level p_i48556_2_) {
        super(p_i48556_1_, p_i48556_2_);
        this.xpReward = 20;
        this.coolDown = 0;
        this.healTimes = 0;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new CastingSpellGoal());
        this.goalSelector.addGoal(2, new HealingSelfSpellGoal());
        this.goalSelector.addGoal(2, new ThrowPotionGoal(this));
        this.goalSelector.addGoal(2, new AttackGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.InquillagerHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.InquillagerDamage.get());
    }

    public AttributeSupplier.Builder getConfiguredAttributes(){
        return setCustomAttributes();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.coolDown = pCompound.getInt("Cooldown");
        this.healTimes = pCompound.getInt("HealTimes");
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Cooldown", this.coolDown);
        pCompound.putInt("HealTimes", this.healTimes);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.coolDown > 0){
            --this.coolDown;
        }
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    public void setCoolDown(int coolDown){
        this.coolDown = coolDown;
    }

    public int getCoolDown() {
        return coolDown;
    }

    public void increaseHealTimes(){
        ++this.healTimes;
    }

    public void setHealTimes(int healTimes){
        this.healTimes = healTimes;
    }

    public int getHealTimes() {
        return healTimes;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData ilivingentitydata = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        RandomSource randomSource = pLevel.getRandom();
        this.populateDefaultEquipmentSlots(randomSource, pDifficulty);
        this.populateDefaultEquipmentEnchantments(randomSource, pDifficulty);
        return ilivingentitydata;
    }

    public boolean hurt(@Nonnull DamageSource source, float amount) {
        if (source.is(DamageTypeTags.WITCH_RESISTANT_TO)){
            return false;
        } else {
            return super.hurt(source, amount);
        }
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance pDifficulty) {
        if (this.getCurrentRaid() == null) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        }
    }

    protected void enchantSpawnedWeapon(RandomSource randomSource, float p_241844_1_) {
        super.enchantSpawnedWeapon(randomSource, p_241844_1_);
        ItemStack itemstack = this.getMainHandItem();
        if (itemstack.getItem() == Items.IRON_SWORD) {
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack);
            map.putIfAbsent(Enchantments.FIRE_ASPECT, 2);
            EnchantmentHelper.setEnchantments(map, itemstack);
            this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
        }
    }

    @Override
        public IllagerArmPose getArmPose() {
        if (this.isCastingSpell()) {
            return IllagerArmPose.SPELLCASTING;
        } else if (this.isAggressive()) {
            return IllagerArmPose.ATTACKING;
        } else {
            return this.isCelebrating() ? IllagerArmPose.CELEBRATING : IllagerArmPose.CROSSED;
        }
    }

    public boolean isAlliedTo(Entity pEntity) {
        if (pEntity == this) {
            return true;
        } else if (super.isAlliedTo(pEntity)) {
            return true;
        } else if (pEntity instanceof Vex) {
            return this.isAlliedTo(((Vex)pEntity).getOwner());
        } else if (pEntity instanceof LivingEntity && ((LivingEntity)pEntity).getMobType() == MobType.ILLAGER) {
            return this.getTeam() == null && pEntity.getTeam() == null;
        } else {
            return false;
        }
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.INQUILLAGER_AMBIENT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.INQUILLAGER_DEATH.get();
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.INQUILLAGER_HURT.get();
    }

    public SoundEvent getCelebrateSound() {
        return ModSounds.INQUILLAGER_CELEBRATE.get();
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean p_213660_2_) {
        ItemStack itemstack = new ItemStack(Items.IRON_SWORD);
        Raid raid = this.getCurrentRaid();
        int i = 2;
        if (pWave > raid.getNumGroups(Difficulty.NORMAL)) {
            i = 4;
        }

        boolean flag = this.random.nextFloat() <= raid.getEnchantOdds();
        if (flag) {
            Map<Enchantment, Integer> map = Maps.newHashMap();
            map.put(Enchantments.SHARPNESS, i);
            EnchantmentHelper.setEnchantments(map, itemstack);
        }

        this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
    }

    class CastingSpellGoal extends SpellcasterCastingSpellGoal {
        private CastingSpellGoal() {
        }

        public void tick() {
            if (Inquillager.this.getTarget() != null) {
                Inquillager.this.getLookControl().setLookAt(Inquillager.this.getTarget(), (float)Inquillager.this.getMaxHeadYRot(), (float)Inquillager.this.getMaxHeadXRot());
            }

        }
    }

    class HealingSelfSpellGoal extends SpellcasterUseSpellGoal {

        private HealingSelfSpellGoal() {
        }

        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else {
                return Inquillager.this.getHealth() < Inquillager.this.getMaxHealth()/2 && Inquillager.this.getCoolDown() <= 0;
            }
        }

        protected int getCastingTime() {
            return 5;
        }

        protected int getCastingInterval() {
            return 20;
        }

        protected void performSpellCasting() {
            Inquillager.this.heal(Inquillager.this.getMaxHealth());
            if (!Inquillager.this.level.isClientSide) {
                ServerLevel serverWorld = (ServerLevel) Inquillager.this.level;
                for (int i = 0; i < 5; ++i) {
                    double d0 = Inquillager.this.random.nextGaussian() * 0.02D;
                    double d1 = Inquillager.this.random.nextGaussian() * 0.02D;
                    double d2 = Inquillager.this.random.nextGaussian() * 0.02D;
                    serverWorld.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), Inquillager.this.getRandomX(1.0D), Inquillager.this.getRandomY() + 1.0D, Inquillager.this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                }
            }
            Inquillager.this.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 1.0F, 2.0F);
            if (Inquillager.this.getHealTimes() > 3){
                Inquillager.this.setHealTimes(0);
                Inquillager.this.setCoolDown(600);
            } else {
                Inquillager.this.increaseHealTimes();
                Inquillager.this.setCoolDown(100);
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        protected IllagerSpell getSpell() {
            return IllagerSpell.SUMMON_VEX;
        }
    }

    static class ThrowPotionGoal extends Goal {
        public int bombTimer;
        public Inquillager inquillager;

        public ThrowPotionGoal(Inquillager inquillager){
            this.inquillager = inquillager;
        }

        @Override
        public boolean canUse() {
            if (this.inquillager.getTarget() != null){
                LivingEntity entity = this.inquillager.getTarget();
                return this.inquillager.distanceTo(entity) > 4.0
                        && this.inquillager.distanceTo(entity) <= 10
                        && this.inquillager.getSensing().hasLineOfSight(entity);
            } else {
                return false;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.inquillager.getTarget() != null && !this.inquillager.getTarget().isDeadOrDying();
        }

        @Override
        public void stop() {
            this.bombTimer = 0;
        }

        @Override
        public void tick() {
            super.tick();
            LivingEntity livingEntity = this.inquillager.getTarget();
            if (livingEntity != null) {
                ++this.bombTimer;
                if (this.bombTimer >= 60) {
                    Vec3 vector3d = livingEntity.getDeltaMovement();
                    double d0 = livingEntity.getX() + vector3d.x - this.inquillager.getX();
                    double d1 = livingEntity.getEyeY() - (double) 1.1F - this.inquillager.getY();
                    double d2 = livingEntity.getZ() + vector3d.z - this.inquillager.getZ();
                    float f = Mth.sqrt((float) (d0 * d0 + d2 * d2));
                    Potion potion;
                    if (livingEntity.isInvertedHealAndHarm()) {
                        potion = Potions.HEALING;
                    } else {
                        potion = Potions.HARMING;
                    }
                    ThrownPotion potionentity = new ThrownPotion(this.inquillager.level, this.inquillager);
                    potionentity.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
                    potionentity.setXRot(potionentity.getXRot() - -20.0F);
                    potionentity.shoot(d0, d1 + (double) (f * 0.2F), d2, 0.75F, 8.0F);
                    if (!this.inquillager.isSilent()) {
                        this.inquillager.level.playSound((Player) null, this.inquillager.getX(), this.inquillager.getY(), this.inquillager.getZ(), SoundEvents.WITCH_THROW, this.inquillager.getSoundSource(), 1.0F, 0.8F + this.inquillager.random.nextFloat() * 0.4F);
                    }
                    this.inquillager.level.addFreshEntity(potionentity);
                    this.bombTimer = 0;
                }
            }
        }
    }

    class AttackGoal extends MeleeAttackGoal {
        public AttackGoal(Inquillager p_i50577_2_) {
            super(p_i50577_2_, 1.0D, false);
        }

        protected double getAttackReachSqr(LivingEntity pAttackTarget) {
            if (this.mob.getVehicle() instanceof Ravager) {
                float f = this.mob.getVehicle().getBbWidth() - 0.1F;
                return (double)(f * 2.0F * f * 2.0F + pAttackTarget.getBbWidth());
            } else {
                return super.getAttackReachSqr(pAttackTarget);
            }
        }
    }
}
