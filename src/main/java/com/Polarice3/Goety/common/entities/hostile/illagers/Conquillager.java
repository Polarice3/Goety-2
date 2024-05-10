package com.Polarice3.Goety.common.entities.hostile.illagers;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.google.common.collect.Maps;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Map;

public class Conquillager extends HuntingIllagerEntity implements CrossbowAttackMob {
    private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(Conquillager.class, EntityDataSerializers.BOOLEAN);

    public Conquillager(EntityType<? extends Conquillager> p_i48556_1_, Level p_i48556_2_) {
        super(p_i48556_1_, p_i48556_2_);
        this.xpReward = 20;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new Raider.HoldGroundAttackGoal(this, 10.0F));
        this.goalSelector.addGoal(3, new IllagerCrossbowGoal<>(this, 1.0D, 16.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.ConquillagerHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.ConquillagerArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.ConquillagerHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.ConquillagerArmor.get());
    }

    public void tick() {
        super.tick();
        for (LivingEntity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(8.0D), field_213690_b)) {
            if (!(entity instanceof PatrollingMonster) && entity.getMobType() != MobType.UNDEAD) {
                if (this.tickCount % 100 == 0 && this.getRandom().nextInt(20) == 0) {
                    if (entity instanceof Player) {
                        if (!((Player) entity).isCreative()) {
                            entity.addEffect(new MobEffectInstance(GoetyEffects.ILLAGUE.get(), 2000, 0, false, false));

                        }
                    } else {
                        entity.addEffect(new MobEffectInstance(GoetyEffects.ILLAGUE.get(), 2000, 0, false, false));
                    }
                }
            }
        }
        if (this.level.isClientSide) {
            if (this.tickCount % 20 == 0){
                for(int i = 0; i < 8; ++i) {
                    this.level.addParticle(ModParticleTypes.PLAGUE_EFFECT.get(), this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.5D, 0.0D);
                }
            }
        }
    }

    public void pickUpItem(ItemEntity pItemEntity) {
        if (!(pItemEntity.getItem().getItem() instanceof TieredItem)){
            super.pickUpItem(pItemEntity);
        }
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return null;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CHARGING_CROSSBOW, false);
    }

    public boolean canFireProjectileWeapon(ProjectileWeaponItem p_230280_1_) {
        return p_230280_1_ == Items.CROSSBOW;
    }

    public boolean isChargingCrossbow() {
        return this.entityData.get(IS_CHARGING_CROSSBOW);
    }

    public void setChargingCrossbow(boolean pIsCharging) {
        this.entityData.set(IS_CHARGING_CROSSBOW, pIsCharging);
    }

    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    public IllagerArmPose getArmPose() {
        if (this.isChargingCrossbow()) {
            return IllagerArmPose.CROSSBOW_CHARGE;
        } else if (this.isHolding(item -> item.getItem() instanceof CrossbowItem)) {
            return IllagerArmPose.CROSSBOW_HOLD;
        } else {
            return this.isAggressive() ? IllagerArmPose.ATTACKING : IllagerArmPose.NEUTRAL;
        }
    }

    public float getWalkTargetValue(BlockPos pPos, LevelReader pLevel) {
        BlockState blockstate = pLevel.getBlockState(pPos.below());
        return !blockstate.is(Blocks.GRASS_BLOCK) && !blockstate.is(Blocks.SAND) ? 0.5F - pLevel.getPathfindingCostFromLightLevels(pPos) : 10.0F;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_33282_, DifficultyInstance p_33283_, MobSpawnType p_33284_, @Nullable SpawnGroupData p_33285_, @Nullable CompoundTag p_33286_) {
        RandomSource randomsource = p_33282_.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, p_33283_);
        this.populateDefaultEquipmentEnchantments(randomsource, p_33283_);
        if (p_33284_ == MobSpawnType.EVENT) {
            if (p_33282_.getLevel().random.nextFloat() <= 0.25F && !this.isPassenger()) {
                Trampler trampler = new Trampler(ModEntityType.TRAMPLER.get(), p_33282_.getLevel());
                trampler.finalizeSpawn(p_33282_, p_33282_.getLevel().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.EVENT, null, null);
                trampler.setPos(this.position());
                p_33282_.getLevel().addFreshEntity(trampler);
                this.startRiding(trampler);
            }
        }
        return super.finalizeSpawn(p_33282_, p_33283_, p_33284_, p_33285_, p_33286_);
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance pDifficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
    }

    protected void enchantSpawnedWeapon(RandomSource randomsource, float p_241844_1_) {
        super.enchantSpawnedWeapon(randomsource, p_241844_1_);
        ItemStack itemstack = this.getMainHandItem();
        if (itemstack.getItem() == Items.CROSSBOW) {
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack);
            map.putIfAbsent(Enchantments.PIERCING, 4);
            EnchantmentHelper.setEnchantments(map, itemstack);
            this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
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

    protected SoundEvent getAmbientSound() {
        return ModSounds.CONQUILLAGER_AMBIENT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.CONQUILLAGER_DEATH.get();
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.CONQUILLAGER_HURT.get();
    }

    public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
        this.performCrossbowAttack(this, 1.6F);
    }

    public void shootCrossbowProjectile(LivingEntity shooter, ItemStack itemStack, Projectile projectileEntity, float p_230284_4_) {
        this.shootCrossbowProjectile(this, shooter, projectileEntity, p_230284_4_, 1.6F);
    }

    public void performCrossbowAttack(LivingEntity shooter, float velocity) {
        InteractionHand hand = ProjectileUtil.getWeaponHoldingHand(shooter, item -> item instanceof CrossbowItem);
        ItemStack itemstack = shooter.getItemInHand(hand);
        if (shooter.isHolding(itemStack -> itemStack.getItem() instanceof CrossbowItem)) {
            CrossbowItem.performShooting(shooter.level, shooter, hand, itemstack, velocity, (float)(14 - shooter.level.getDifficulty().getId() * 4));
        }

        this.onCrossbowAttackPerformed();
    }

    public void shootCrossbowProjectile(LivingEntity shooter, LivingEntity target, Projectile projectileEntity, float p_234279_4_, float velocity) {
        double d0 = target.getX() - shooter.getX();
        double d1 = target.getY(0.5F) - shooter.getY(0.5F);
        double d2 = target.getZ() - shooter.getZ();
        Vector3f vector3f = this.getProjectileShotVector(shooter, new Vec3(d0, d1, d2), p_234279_4_);
        projectileEntity.shoot(vector3f.x(), vector3f.y(), vector3f.z(), velocity, (float)(14 - shooter.level.getDifficulty().getId() * 4));
        shooter.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (shooter.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    public ItemStack getProjectile(ItemStack pShootable) {
        int difficulty = this.level.getCurrentDifficultyAt(this.blockPosition()).getDifficulty().getId();
        return MobUtil.createFirework(difficulty * 2, DyeColor.values());
    }

    public void applyRaidBuffs(int pWave, boolean p_213660_2_) {
        Raid raid = this.getCurrentRaid();
        boolean flag = this.random.nextFloat() <= raid.getEnchantOdds();
        if (flag) {
            ItemStack itemstack = new ItemStack(Items.CROSSBOW);
            Map<Enchantment, Integer> map = Maps.newHashMap();
            if (pWave > raid.getNumGroups(Difficulty.NORMAL)) {
                map.put(Enchantments.QUICK_CHARGE, 3);
            } else if (pWave > raid.getNumGroups(Difficulty.EASY)) {
                map.put(Enchantments.QUICK_CHARGE, 2);
            }

            map.put(Enchantments.MULTISHOT, 1);
            EnchantmentHelper.setEnchantments(map, itemstack);
            this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
        }

    }

    public SoundEvent getCelebrateSound() {
        return ModSounds.CONQUILLAGER_CELEBRATE.get();
    }

    public class IllagerCrossbowGoal<T extends PathfinderMob & RangedAttackMob & CrossbowAttackMob> extends Goal {
        private final T mob;
        private CrossbowState crossbowState = CrossbowState.UNCHARGED;
        private final double speedModifier;
        private final float attackRadiusSqr;
        private int seeTime;
        private int attackDelay;
        private boolean strafingBackwards;
        private int strafingTime = -1;

        public IllagerCrossbowGoal(T p_i50322_1_, double p_i50322_2_, float p_i50322_4_) {
            this.mob = p_i50322_1_;
            this.speedModifier = p_i50322_2_;
            this.attackRadiusSqr = p_i50322_4_ * p_i50322_4_;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            return this.isValidTarget() && this.isHoldingCrossbow();
        }

        private boolean isHoldingCrossbow() {
            return this.mob.isHolding(is -> is.getItem() instanceof CrossbowItem);
        }

        public boolean canContinueToUse() {
            return this.isValidTarget() && (this.canUse() || !this.mob.getNavigation().isDone()) && this.isHoldingCrossbow();
        }

        private boolean isValidTarget() {
            return this.mob.getTarget() != null && this.mob.getTarget().isAlive();
        }

        public void start() {
            super.start();
            this.mob.setAggressive(true);
        }

        public void stop() {
            super.stop();
            this.mob.setAggressive(false);
            this.mob.setTarget((LivingEntity)null);
            this.seeTime = 0;
            if (this.mob.isUsingItem()) {
                this.mob.stopUsingItem();
                this.mob.setChargingCrossbow(false);
                CrossbowItem.setCharged(this.mob.getUseItem(), false);
            }

        }

        public void tick() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null) {
                boolean flag = this.mob.getSensing().hasLineOfSight(livingentity);
                boolean flag1 = this.seeTime > 0;
                boolean noRaiders = livingentity.level.getEntitiesOfClass(Raider.class, livingentity.getBoundingBox().inflate(5.0D), (entity) -> entity != this.mob && !(entity instanceof Tormentor)).isEmpty();
                if (flag != flag1) {
                    this.seeTime = 0;
                }

                if (flag) {
                    ++this.seeTime;
                } else {
                    --this.seeTime;
                }

                double d0 = this.mob.distanceToSqr(livingentity);
                boolean flag2 = (d0 > (double)this.attackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;
                if (d0 < (double)this.attackRadiusSqr && this.seeTime >= 20) {
                    this.mob.getNavigation().stop();
                    ++this.strafingTime;
                } else {
                    this.mob.getNavigation().moveTo(livingentity, this.speedModifier);
                    this.strafingTime = -1;
                }
                if (this.strafingTime >= 20) {
                    this.strafingTime = 0;
                }

                if (this.strafingTime > -1) {
                    this.strafingBackwards = (d0 < (double)(this.attackRadiusSqr * 0.25F));

                    this.mob.getMoveControl().strafe(this.strafingBackwards ? (float) -this.speedModifier : (float) this.speedModifier, 0.0F);
                    this.mob.lookAt(livingentity, 30.0F, 30.0F);
                } else {
                    this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                }

                if (this.crossbowState == CrossbowState.UNCHARGED) {
                    if (!flag2) {
                        this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof CrossbowItem));
                        this.crossbowState = CrossbowState.CHARGING;
                        this.mob.setChargingCrossbow(true);
                    }
                } else if (this.crossbowState == CrossbowState.CHARGING) {
                    if (!this.mob.isUsingItem()) {
                        this.crossbowState = CrossbowState.UNCHARGED;
                    }

                    int i = this.mob.getTicksUsingItem();
                    ItemStack itemstack = this.mob.getUseItem();
                    if (i >= CrossbowItem.getChargeDuration(itemstack)) {
                        this.mob.releaseUsingItem();
                        this.crossbowState = CrossbowState.CHARGED;
                        this.attackDelay = 20 + this.mob.getRandom().nextInt(20);
                        this.mob.setChargingCrossbow(false);
                    }
                } else if (this.crossbowState == CrossbowState.CHARGED) {
                    --this.attackDelay;
                    if (this.attackDelay == 0) {
                        this.crossbowState = CrossbowState.READY_TO_ATTACK;
                    }
                } else if (this.crossbowState == CrossbowState.READY_TO_ATTACK && flag && noRaiders) {
                    this.mob.performRangedAttack(livingentity, 1.0F);
                    ItemStack itemstack1 = this.mob.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof CrossbowItem));
                    CrossbowItem.setCharged(itemstack1, false);
                    this.crossbowState = CrossbowState.UNCHARGED;
                }

            }
        }

        enum CrossbowState {
            UNCHARGED,
            CHARGING,
            CHARGED,
            READY_TO_ATTACK;
        }
    }
}
