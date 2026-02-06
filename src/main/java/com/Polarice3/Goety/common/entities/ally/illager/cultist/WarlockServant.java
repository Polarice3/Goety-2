package com.Polarice3.Goety.common.entities.ally.illager.cultist;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.ModRangedAttackGoal;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.ai.WitchServantBarterGoal;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.entities.neutral.Wartling;
import com.Polarice3.Goety.common.entities.projectiles.BerserkFungus;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServantUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.*;
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
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class WarlockServant extends CultistServant implements RangedAttackMob {
    private LivingEntity shootTarget;
    private int targetCoolDown = 0;
    private int coolDown;
    private int totalCool;

    public WarlockServant(EntityType<? extends CultistServant> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new WitchServantBarterGoal(this));
        this.goalSelector.addGoal(2, new ModRangedAttackGoal<>(this, 1.0D, 20, 40, 10.0F){
            public boolean canUse() {
                LivingEntity livingentity = WarlockServant.this.getShootTarget();
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

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.WarlockHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.WarlockArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.WarlockHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.WarlockArmor.get());
    }

    public void addAdditionalSaveData(CompoundTag p_33353_) {
        super.addAdditionalSaveData(p_33353_);
        p_33353_.putInt("CoolDown", this.coolDown);
        p_33353_.putInt("TotalCool", this.totalCool);
    }

    public void readAdditionalSaveData(CompoundTag p_33344_) {
        super.readAdditionalSaveData(p_33344_);
        this.coolDown = p_33344_.getInt("CoolDown");
        this.totalCool = p_33344_.getInt("TotalCool");
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
        return ModSounds.WARLOCK_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_34154_) {
        return ModSounds.WARLOCK_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.WARLOCK_DEATH.get();
    }

    public SoundEvent getCelebrateSound() {
        return ModSounds.WARLOCK_CELEBRATE.get();
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.coolDown > 0){
            --this.coolDown;
        }

        if (!this.level.isClientSide){
            if (this.isAlive()){
                if (--this.targetCoolDown <= 0 && this.getRandom().nextBoolean()) {
                    this.findTarget();
                    this.targetCoolDown = 200;
                }
                if (this.getShootTarget() == null) {
                    if (this.getTarget() != null && this.getTarget().isAlive()) {
                        this.setShootTarget(this.getTarget());
                    }
                }
            }
            if (this.random.nextFloat() < 7.5E-4F) {
                this.level.broadcastEntityEvent(this, (byte)15);
            }
            if (this.getShootTarget() != null) {
                if (!this.isAlliedTarget(this.getShootTarget()) || (this.getTarget() != null && this.getTarget() == this.getShootTarget())) {
                    if (this.getShootTarget().distanceTo(this) < 6.0F && this.coolDown <= this.totalCool / 2) {
                        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.SNAP_FUNGUS.get()));
                    } else {
                        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    }
                } else {
                    this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.BERSERK_FUNGUS.get()));
                }
            } else {
                if (this.getMainHandItem().is(ModItems.SNAP_FUNGUS.get()) || this.getMainHandItem().is(ModItems.BERSERK_FUNGUS.get())){
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                }
                boolean flag = false;
                if (!this.isInLava()) {
                    if (this.getHealth() < this.getMaxHealth()) {
                        flag = true;
                    }
                    if (!this.getActiveEffects().isEmpty()) {
                        if (this.getActiveEffects().stream().anyMatch((mobEffectInstance -> mobEffectInstance.getEffect().getCategory() == MobEffectCategory.HARMFUL))){
                            flag = true;
                        }
                    }
                    if (flag){
                        if (this.tickCount % 60 == 0){
                            Wartling wartling = new Wartling(ModEntityType.WARTLING.get(), this.level);
                            wartling.moveTo(this.blockPosition(), this.getYRot(), this.getXRot());
                            this.summonWartlings(wartling);
                        }
                    }
                }
            }

        }
    }

    protected AABB getTargetSearchArea(double p_26069_) {
        return this.getBoundingBox().inflate(p_26069_, 4.0D, p_26069_);
    }

    public boolean isAlliedTarget(Entity target) {
        if (this.getTrueOwner() != null) {
            return (target == this.getTrueOwner() || MobUtil.getOwner(target) == this.getTrueOwner());
        } else {
            return MobUtil.areAllies(this, target);
        }
    }

    protected void findTarget() {
        this.shootTarget = this.level.getNearestEntity(this.level.getEntitiesOfClass(LivingEntity.class, this.getTargetSearchArea(this.getAttributeValue(Attributes.FOLLOW_RANGE)), (p_148152_) -> {
            return true;
        }), TargetingConditions.forNonCombat().range(this.getAttributeValue(Attributes.FOLLOW_RANGE))
                .selector(livingEntity -> this.isAlliedTarget(livingEntity) && (livingEntity instanceof Mob mob && mob.getTarget() != null && !(mob instanceof Wartling))), this, this.getX(), this.getEyeY(), this.getZ());
    }

    public void handleEntityEvent(byte p_34138_) {
        if (p_34138_ == 15) {
            for(int i = 0; i < this.random.nextInt(35) + 10; ++i) {
                this.level.addParticle(ModParticleTypes.WARLOCK.get(), this.getX() + this.random.nextGaussian() * (double)0.13F, this.getBoundingBox().maxY + this.random.nextGaussian() * (double)0.13F, this.getZ() + this.random.nextGaussian() * (double)0.13F, 0.0D, 0.0D, 0.0D);
            }
        } else {
            super.handleEntityEvent(p_34138_);
        }

    }

    protected float getDamageAfterMagicAbsorb(DamageSource damageSource, float damage) {
        damage = super.getDamageAfterMagicAbsorb(damageSource, damage);
        if (damageSource.getEntity() == this) {
            damage = 0.0F;
        }

        if (damageSource.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            damage *= 0.15F;
        }

        return damage;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        if (target.distanceTo(this) < 6.0F && this.coolDown <= 0 && this.level.getBlockState(this.blockPosition().above(2)).isAir() && !(this.getTarget() instanceof Raider)) {
            this.totalCool = Mth.nextInt(this.random, 6, 10);
            for (int i = 0; i < this.totalCool; ++i) {
                MobUtil.throwSnapFungus(this, level);
            }
            this.coolDown = MathHelper.secondsToTicks(this.totalCool);
            if (!this.isSilent()) {
                this.level.playSound((Player) null, this.getX(), this.getY(), this.getZ(), ModSounds.BLAST_FUNGUS_THROW.get(), this.getSoundSource(), 2.0F, 0.8F + this.random.nextFloat() * 0.4F);
            }
        } else {
            if (this.level instanceof ServerLevel) {
                if (this.isAlliedTarget(target) && this.getTarget() != target) {
                    if (!this.isSilent()) {
                        this.level.playSound((Player) null, this.getX(), this.getY(), this.getZ(), ModSounds.BLAST_FUNGUS_THROW.get(), this.getSoundSource(), 2.0F, 0.8F + this.random.nextFloat() * 0.4F);
                    }
                    Vec3 vec3 = target.getDeltaMovement();
                    double d0 = target.getX() + vec3.x - this.getX();
                    double d1 = target.getEyeY() - (double)1.1F - this.getY();
                    double d2 = target.getZ() + vec3.z - this.getZ();
                    double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                    BerserkFungus berserkFungus = new BerserkFungus(this, this.level);
                    berserkFungus.setXRot(berserkFungus.getXRot() - 20.0F);
                    berserkFungus.shoot(d0, d1 + d3 * 0.2D, d2, 0.75F, 8.0F);
                    this.level.addFreshEntity(berserkFungus);
                } else {
                    Wartling wartling = new Wartling(ModEntityType.WARTLING.get(), this.level);
                    wartling.setTarget(target);
                    if (this.isInFluidType()){
                        wartling.setPos(this.getX(), this.getY(0.5F), this.getZ());
                        wartling.setXRot(this.getXRot());
                        wartling.setYRot(this.getYRot());
                        double d0 = target.getX() - wartling.getX();
                        double d1 = target.getY(0.3333333333333333D) - wartling.getY();
                        double d2 = target.getZ() - wartling.getZ();
                        double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
                        MobUtil.shoot(wartling, d0, d1 + d3 * (double)0.2F, d2, 1.6F, 1.0F);
                    } else {
                        wartling.moveTo(this.blockPosition(), this.getYRot(), this.getXRot());
                    }
                    this.summonWartlings(wartling);
                }
            }
        }
    }

    private void summonWartlings(Wartling wartling){
        if (this.level instanceof ServerLevel serverLevel) {
            wartling.setTrueOwner(this);
            wartling.setLimitedLife(MathHelper.secondsToTicks(9));
            this.getActiveEffects().stream().filter(mobEffect -> mobEffect.getEffect().getCategory() == MobEffectCategory.HARMFUL && !mobEffect.getEffect().getCurativeItems().isEmpty()).findFirst().ifPresent(effect -> {
                wartling.setStoredEffect(effect);
                this.removeEffect(effect.getEffect());
            });
            wartling.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
            this.level.addFreshEntity(wartling);
        }
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
