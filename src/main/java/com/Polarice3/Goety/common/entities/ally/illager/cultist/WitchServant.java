package com.Polarice3.Goety.common.entities.ally.illager.cultist;

import com.Polarice3.Goety.common.entities.ai.ModRangedAttackGoal;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.ai.WitchServantBarterGoal;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServantUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class WitchServant extends RaiderServant implements RangedAttackMob {
    private static final UUID SPEED_MODIFIER_DRINKING_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier SPEED_MODIFIER_DRINKING = new AttributeModifier(SPEED_MODIFIER_DRINKING_UUID, "Drinking speed penalty", -0.25D, AttributeModifier.Operation.ADDITION);
    private static final EntityDataAccessor<Boolean> DATA_USING_ITEM = SynchedEntityData.defineId(WitchServant.class, EntityDataSerializers.BOOLEAN);
    private LivingEntity shootTarget;
    private int cooldown = 0;
    private int usingTime;

    public WitchServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new WitchServantBarterGoal(this));
        this.goalSelector.addGoal(2, new ModRangedAttackGoal<>(this, 1.0D, 60, 10.0F){
            public boolean canUse() {
                LivingEntity livingentity = WitchServant.this.getShootTarget();
                if (livingentity != null && livingentity.isAlive()) {
                    this.target = livingentity;
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public void targetSelectGoal() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, RaiderServant.class));
        this.targetSelector.addGoal(1, new SummonTargetGoal(this));
    }

    @Override
    public void miscGoal() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(8, new RaiderWanderGoal<>(this, 1.0D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_USING_ITEM, false);
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    public LivingEntity getShootTarget() {
        return this.shootTarget;
    }

    public void setShootTarget(@Nullable LivingEntity target) {
        this.shootTarget = target;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITCH_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_34154_) {
        return SoundEvents.WITCH_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.WITCH_DEATH;
    }

    public void setUsingItem(boolean p_34164_) {
        this.getEntityData().set(DATA_USING_ITEM, p_34164_);
    }

    public boolean isDrinkingPotion() {
        return this.getEntityData().get(DATA_USING_ITEM);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.WitchServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.WitchServantFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.WitchServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.WitchServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.WitchServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.WitchServantFollowRange.get());
    }

    public void aiStep() {
        AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (!this.level.isClientSide && this.isAlive()) {
            --this.cooldown;
            if (this.cooldown <= 0 && this.getRandom().nextBoolean()) {
                this.findTarget();
                this.cooldown = 200;
            }
            if (this.getShootTarget() == null) {
                if (this.getTarget() != null && this.getTarget().isAlive()) {
                    this.setShootTarget(this.getTarget());
                }
            }

            if (this.isDrinkingPotion()) {
                if (this.usingTime-- <= 0) {
                    this.setUsingItem(false);
                    ItemStack itemstack = this.getMainHandItem();
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    if (itemstack.is(Items.POTION)) {
                        List<MobEffectInstance> list = PotionUtils.getMobEffects(itemstack);
                        if (list != null) {
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
                } else if (this.random.nextFloat() < 0.15F && (this.isOnFire() || this.getLastDamageSource() != null && this.getLastDamageSource().is(DamageTypeTags.IS_FIRE)) && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                    potion = Potions.FIRE_RESISTANCE;
                } else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
                    potion = Potions.HEALING;
                } else if (this.random.nextFloat() < 0.5F && this.getTarget() != null && !this.hasEffect(MobEffects.MOVEMENT_SPEED) && this.getTarget().distanceToSqr(this) > 121.0D) {
                    potion = Potions.SWIFTNESS;
                }

                if (potion != null) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, PotionUtils.setPotion(new ItemStack(Items.POTION), potion));
                    this.usingTime = this.getMainHandItem().getUseDuration();
                    this.setUsingItem(true);
                    if (!this.isSilent()) {
                        this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_DRINK, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
                    }

                    if (attributeinstance != null) {
                        attributeinstance.removeModifier(SPEED_MODIFIER_DRINKING);
                        attributeinstance.addTransientModifier(SPEED_MODIFIER_DRINKING);
                    }
                }
            }

            if (this.random.nextFloat() < 7.5E-4F) {
                this.level().broadcastEntityEvent(this, (byte)15);
            }
        }

        super.aiStep();
    }

    protected AABB getTargetSearchArea(double p_26069_) {
        return this.getBoundingBox().inflate(p_26069_, 4.0D, p_26069_);
    }

    public boolean isAlliedTarget(Entity target) {
        if (this.getTrueOwner() != null) {
            return (target == this.getTrueOwner() || MobUtil.getOwner(target) == this.getTrueOwner() || (MobUtil.areAllies(this.getTrueOwner(), target) && MobUtil.notTargetingAlly(target, this)));
        } else {
            return MobUtil.areAllies(this, target);
        }
    }

    protected void findTarget() {
        this.shootTarget = this.level.getNearestEntity(this.level.getEntitiesOfClass(LivingEntity.class, this.getTargetSearchArea(this.getAttributeValue(Attributes.FOLLOW_RANGE)), (p_148152_) -> {
            return true;
        }), TargetingConditions.forNonCombat().range(this.getAttributeValue(Attributes.FOLLOW_RANGE))
                .selector(livingEntity -> this.isAlliedTarget(livingEntity) && livingEntity.getHealth() < livingEntity.getMaxHealth()), this, this.getX(), this.getEyeY(), this.getZ());
    }

    public SoundEvent getCelebrateSound() {
        return SoundEvents.WITCH_CELEBRATE;
    }

    public void handleEntityEvent(byte p_34138_) {
        if (p_34138_ == 15) {
            for(int i = 0; i < this.random.nextInt(35) + 10; ++i) {
                this.level().addParticle(ParticleTypes.WITCH, this.getX() + this.random.nextGaussian() * (double)0.13F, this.getBoundingBox().maxY + 0.5D + this.random.nextGaussian() * (double)0.13F, this.getZ() + this.random.nextGaussian() * (double)0.13F, 0.0D, 0.0D, 0.0D);
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

        if (p_34149_.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            p_34150_ *= 0.15F;
        }

        return p_34150_;
    }

    public void performRangedAttack(LivingEntity target, float velocity) {
        if (!this.isDrinkingPotion()) {
            Vec3 vec3 = target.getDeltaMovement();
            double d0 = target.getX() + vec3.x - this.getX();
            double d1 = target.getEyeY() - (double)1.1F - this.getY();
            double d2 = target.getZ() + vec3.z - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            Potion potion = Potions.HARMING;
            if (target.isInvertedHealAndHarm()) {
                potion = Potions.HEALING;
            }
            if (this.isAlliedTarget(target) && this.getTarget() != target) {
                if (target.isInvertedHealAndHarm()) {
                    potion = Potions.HARMING;
                } else {
                    if (target.getHealth() <= 4.0F) {
                        potion = Potions.HEALING;
                    } else {
                        potion = Potions.REGENERATION;
                    }
                }

                this.setShootTarget(null);
            } else if (d3 >= 8.0D && !target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                potion = Potions.SLOWNESS;
            } else if (target.getHealth() >= 8.0F && target.canBeAffected(new MobEffectInstance(MobEffects.POISON)) && !target.hasEffect(MobEffects.POISON)) {
                potion = Potions.POISON;
            } else if (d3 <= 3.0D && !target.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
                potion = Potions.WEAKNESS;
            }

            ThrownPotion thrownpotion = new ThrownPotion(this.level, this);
            thrownpotion.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
            thrownpotion.setXRot(thrownpotion.getXRot() - -20.0F);
            thrownpotion.shoot(d0, d1 + d3 * 0.2D, d2, 0.75F, 8.0F);
            if (!this.isSilent()) {
                this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
            }

            this.level.addFreshEntity(thrownpotion);
        }
    }

    protected float getStandingEyeHeight(Pose p_34146_, EntityDimensions p_34147_) {
        return 1.62F;
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        boolean isOwner = this.getTrueOwner() != null && pPlayer == this.getTrueOwner();
        boolean isAlly = ((this.getTrueOwner() != null && MobUtil.areAllies(this.getTrueOwner(), pPlayer)) || this.getTrueOwner() == null) && CuriosFinder.isWitchFriendly(pPlayer);
        if (this.getMainHandItem().isEmpty() && pHand == InteractionHand.MAIN_HAND && itemstack.is(ModTags.Items.WITCH_CURRENCY)) {
            if (isOwner || isAlly) {
                if (!this.isAggressive()) {
                    this.playSound(this.getCelebrateSound());
                    ItemStack itemstack1;
                    if (pPlayer.isCreative()) {
                        itemstack1 = itemstack;
                    } else {
                        itemstack1 = itemstack.split(1);
                    }
                    this.setItemSlot(EquipmentSlot.MAINHAND, itemstack1);
                    this.setTrader(pPlayer);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        if (isOwner) {
            return ServantUtil.equipServantArmor(pPlayer, this, itemstack, super.mobInteract(pPlayer, pHand));
        }
        return super.mobInteract(pPlayer, pHand);
    }

}
