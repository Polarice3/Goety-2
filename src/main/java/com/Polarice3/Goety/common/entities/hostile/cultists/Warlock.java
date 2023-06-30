package com.Polarice3.Goety.common.entities.hostile.cultists;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.WitchBarterGoal;
import com.Polarice3.Goety.common.entities.neutral.Wartling;
import com.Polarice3.Goety.common.entities.projectiles.BerserkFungus;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableWitchTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestHealableRaiderTargetGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class Warlock extends Cultist implements RangedAttackMob {
    private int coolDown;
    private int totalCool;
    private NearestHealableRaiderTargetGoal<Raider> healRaidersGoal;
    private NearestAttackableWitchTargetGoal<Player> attackPlayersGoal;

    public Warlock(EntityType<? extends Cultist> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.healRaidersGoal = new NearestHealableRaiderTargetGoal<>(this, Raider.class, true, (target) -> {
            return target != null && this.hasActiveRaid() && target.getType() != EntityType.WITCH && target.getType() != ModEntityType.WARLOCK.get();
        });
        this.attackPlayersGoal = new NearestAttackableWitchTargetGoal<>(this, Player.class, 10, true, false, (Predicate<LivingEntity>)null);
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 20, 40, 10.0F));
        this.goalSelector.addGoal(1, new WitchBarterGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class));
        this.targetSelector.addGoal(2, this.healRaidersGoal);
        this.targetSelector.addGoal(3, this.attackPlayersGoal);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 26.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    public AttributeSupplier.Builder getConfiguredAttributes(){
        return setCustomAttributes();
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
    public void die(DamageSource p_37847_) {
        super.die(p_37847_);
        if (this.isPassenger() && this.getVehicle() != null){
            if (this.getVehicle() instanceof AbstractHorse donkey){
                if (donkey.getOwnerUUID() != null && donkey.getOwnerUUID() == this.getUUID()){
                    donkey.addEffect(new MobEffectInstance(MobEffects.WITHER, MathHelper.minutesToTicks(5), 9));
                    donkey.addEffect(new MobEffectInstance(MobEffects.POISON, MathHelper.minutesToTicks(5), 9));
                    donkey.setSecondsOnFire(300);
                }
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.coolDown > 0){
            --this.coolDown;
        }

        if (!this.level.isClientSide){
            if (this.isAlive()){
                this.healRaidersGoal.decrementCooldown();
                this.attackPlayersGoal.setCanAttack(this.healRaidersGoal.getCooldown() <= 0);
            }
            if (this.random.nextFloat() < 7.5E-4F) {
                this.level.broadcastEntityEvent(this, (byte)15);
            }
            if (this.getTarget() != null) {
                if (!(this.getTarget() instanceof Raider)) {
                    if (this.getTarget().distanceTo(this) < 6.0F && this.coolDown <= this.totalCool / 2) {
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

        if (damageSource.isExplosion()) {
            damage *= 0.15F;
        }

        return damage;
    }

    @Override
    public void performRangedAttack(LivingEntity p_33317_, float p_33318_) {
        if (p_33317_.distanceTo(this) < 6.0F && this.coolDown <= 0 && this.level.getBlockState(this.blockPosition().above(2)).isAir() && !(this.getTarget() instanceof Raider)) {
            this.totalCool = Mth.nextInt(this.random, 6, 10);
            for (int i = 0; i < this.totalCool; ++i) {
                MobUtil.throwSnapFungus(this, level);
            }
            this.coolDown = MathHelper.secondsToTicks(this.totalCool);
            if (!this.isSilent()) {
                this.level.playSound((Player) null, this.getX(), this.getY(), this.getZ(), ModSounds.BLAST_FUNGUS_THROW.get(), this.getSoundSource(), 2.0F, 0.8F + this.random.nextFloat() * 0.4F);
            }
        } else {
            if (this.getTarget() instanceof Raider){
                if (!this.isSilent()) {
                    this.level.playSound((Player) null, this.getX(), this.getY(), this.getZ(), ModSounds.BLAST_FUNGUS_THROW.get(), this.getSoundSource(), 2.0F, 0.8F + this.random.nextFloat() * 0.4F);
                }
            }
            if (this.level instanceof ServerLevel) {
                if (this.getTarget() instanceof Raider raider){
                    Vec3 vec3 = raider.getDeltaMovement();
                    double d0 = raider.getX() + vec3.x - this.getX();
                    double d1 = raider.getEyeY() - (double)1.1F - this.getY();
                    double d2 = raider.getZ() + vec3.z - this.getZ();
                    double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                    BerserkFungus berserkFungus = new BerserkFungus(this, this.level);
                    berserkFungus.setXRot(berserkFungus.getXRot() - 20.0F);
                    berserkFungus.shoot(d0, d1 + d3 * 0.2D, d2, 0.75F, 8.0F);
                    this.level.addFreshEntity(berserkFungus);
                } else {
                    Wartling wartling = new Wartling(ModEntityType.WARTLING.get(), this.level);
                    wartling.setTarget(p_33317_);
                    if (this.isInFluidType()){
                        wartling.setPos(this.getX(), this.getY(0.5F), this.getZ());
                        wartling.setXRot(this.getXRot());
                        wartling.setYRot(this.getYRot());
                        double d0 = p_33317_.getX() - wartling.getX();
                        double d1 = p_33317_.getY(0.3333333333333333D) - wartling.getY();
                        double d2 = p_33317_.getZ() - wartling.getZ();
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
}
