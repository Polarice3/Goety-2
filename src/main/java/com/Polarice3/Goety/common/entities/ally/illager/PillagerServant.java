package com.Polarice3.Goety.common.entities.ally.illager;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.BackawayCrossbowGoal;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonPillagerServant;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.Map;

public class PillagerServant extends AbstractIllagerServant implements CrossbowAttackMob {
    private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(PillagerServant.class, EntityDataSerializers.BOOLEAN);
    private final BackawayCrossbowGoal<PillagerServant> crossBowGoal = new BackawayCrossbowGoal<>(this, 1.0D, 8.0F);
    private final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.2D, false) {

        public void stop() {
            super.stop();
            PillagerServant.this.setAggressive(false);
        }

        public void start() {
            super.start();
            PillagerServant.this.setAggressive(true);
        }
    };
    public double arrowPower;

    public PillagerServant(EntityType<? extends PillagerServant> p_33262_, Level p_33263_) {
        super(p_33262_, p_33263_);
        this.reassessWeaponGoal();
        this.arrowPower = 0.0D;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new BackawayCrossbowGoal<>(this, 1.0D, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.MAX_HEALTH, AttributesConfig.PillagerServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.PillagerServantArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PillagerServantDamage.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.PillagerServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.PillagerServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.PillagerServantDamage.get());
    }

    public double getBaseRangeDamage(){
        return AttributesConfig.PillagerServantRangeDamage.get();
    }

    public void reassessWeaponGoal() {
        if (!this.level.isClientSide) {
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.crossBowGoal);
            ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof ProjectileWeaponItem));
            if (itemstack.getItem() == Items.CROSSBOW){
                this.goalSelector.addGoal(3, this.crossBowGoal);
            } else {
                this.goalSelector.addGoal(3, this.meleeGoal);
            }

        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CHARGING_CROSSBOW, false);
    }

    public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {
        super.setItemSlot(pSlot, pStack);
        if (!this.level.isClientSide) {
            if (pSlot.getType() == EquipmentSlot.Type.HAND) {
                this.reassessWeaponGoal();
            }
        }

    }

    public boolean canFireProjectileWeapon(ProjectileWeaponItem p_33280_) {
        return p_33280_ == Items.CROSSBOW;
    }

    public boolean isChargingCrossbow() {
        return this.entityData.get(IS_CHARGING_CROSSBOW);
    }

    public void setChargingCrossbow(boolean p_33302_) {
        this.entityData.set(IS_CHARGING_CROSSBOW, p_33302_);
    }

    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setArrowPower(pCompound.getInt("arrowPower"));
        this.reassessWeaponGoal();
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (pCompound.contains("arrowPower")){
            pCompound.putDouble("arrowPower", this.arrowPower);
        }
    }

    public double getArrowPower() {
        return arrowPower;
    }

    public void setArrowPower(int arrowPower) {
        this.arrowPower = arrowPower;
    }

    public IllagerServantArmPose getArmPose() {
        if (this.isChargingCrossbow()) {
            return IllagerServantArmPose.CROSSBOW_CHARGE;
        } else if (this.isHolding(is -> is.getItem() instanceof CrossbowItem || is.is(ModTags.Items.PILLAGER_WEAPONS))) {
            if (this.isAggressive()) {
                return IllagerServantArmPose.CROSSBOW_HOLD;
            } else {
                return this.isCelebrating() ? IllagerServantArmPose.CELEBRATING : IllagerServantArmPose.NEUTRAL;
            }
        } else if (this.isCelebrating()) {
            return IllagerServantArmPose.CELEBRATING;
        } else {
            return this.isAggressive() ? IllagerServantArmPose.ATTACKING : IllagerServantArmPose.NEUTRAL;
        }
    }

    public float getWalkTargetValue(BlockPos p_33288_, LevelReader p_33289_) {
        return 0.0F;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_33282_, DifficultyInstance p_33283_, MobSpawnType p_33284_, @Nullable SpawnGroupData p_33285_, @Nullable CompoundTag p_33286_) {
        RandomSource randomsource = p_33282_.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, p_33283_);
        this.populateDefaultEquipmentEnchantments(randomsource, p_33283_);
        this.reassessWeaponGoal();
        return super.finalizeSpawn(p_33282_, p_33283_, p_33284_, p_33285_, p_33286_);
    }

    protected void populateDefaultEquipmentSlots(RandomSource p_219059_, DifficultyInstance p_219060_) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    protected void enchantSpawnedWeapon(RandomSource p_219056_, float p_219057_) {
        super.enchantSpawnedWeapon(p_219056_, p_219057_);
        if (p_219056_.nextInt(300) == 0) {
            ItemStack itemstack = this.getMainHandItem();
            if (itemstack.is(Items.CROSSBOW)) {
                Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack);
                map.putIfAbsent(Enchantments.PIERCING, 1);
                EnchantmentHelper.setEnchantments(map, itemstack);
                this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
            }
        }

    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.PILLAGER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PILLAGER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource p_33306_) {
        return SoundEvents.PILLAGER_HURT;
    }

    public void performRangedAttack(LivingEntity p_33272_, float p_33273_) {
        this.performCrossbowAttack(this, 1.6F);
    }

    public void shootCrossbowProjectile(LivingEntity p_33275_, ItemStack p_33276_, Projectile p_33277_, float p_33278_) {
        if (p_33277_ instanceof AbstractArrow arrow){
            arrow.setBaseDamage(arrow.getBaseDamage() + this.getArrowPower() + this.getBaseRangeDamage());
        }
        this.shootCrossbowProjectile(this, p_33275_, p_33277_, p_33278_, 1.6F);
    }

    public SoundEvent getCelebrateSound() {
        return SoundEvents.PILLAGER_CELEBRATE;
    }

    @Override
    public void die(DamageSource pCause) {
        if (!this.level.isClientSide) {
            if (this.getIdol() == null) {
                if (this.getTrueOwner() != null) {
                    if (CuriosFinder.hasNamelessSet(this.getTrueOwner())){
                        SkeletonPillagerServant servant = this.convertTo(ModEntityType.SKELETON_PILLAGER_SERVANT.get(), true);
                        if (servant != null) {
                            servant.setTrueOwner(this.getTrueOwner());
                            net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, servant);
                            if (!this.isSilent()) {
                                this.level.levelEvent((Player)null, 1026, this.blockPosition(), 0);
                            }
                        }
                    }
                }
            }
        }
        super.die(pCause);
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        ItemStack itemstack2 = this.getMainHandItem();
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (!(pPlayer.getOffhandItem().getItem() instanceof IWand)) {
                if (item instanceof CrossbowItem || itemstack.is(ModTags.Items.PILLAGER_WEAPONS)) {
                    this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                    this.dropEquipment(EquipmentSlot.MAINHAND, itemstack2.copyAndClear());
                    this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copyWithCount(1));
                    this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
                    if (this.level instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                } else if (item instanceof ArrowItem) {
                    this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                    this.dropEquipment(EquipmentSlot.OFFHAND, this.getOffhandItem().copyAndClear());
                    this.setItemSlot(EquipmentSlot.OFFHAND, itemstack.split(64));
                    this.setGuaranteedDrop(EquipmentSlot.OFFHAND);
                    if (this.level instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }
}
