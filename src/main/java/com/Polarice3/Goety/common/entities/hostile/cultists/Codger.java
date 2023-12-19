package com.Polarice3.Goety.common.entities.hostile.cultists;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.api.entities.hostile.IBoss;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.WitchBarterGoal;
import com.Polarice3.Goety.common.entities.neutral.Wartling;
import com.Polarice3.Goety.common.entities.projectiles.BerserkFungus;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SAddBossPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableWitchTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestHealableRaiderTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Predicate;

public class Codger extends Cultist implements RangedAttackMob, IBoss {
    private int coolDown;
    private int totalCool;
    private int hitTimes;
    private int lastHitTime;
    private int overwhelmed;
    private boolean isSpecial;
    private boolean isShaking;
    private float shakeAnim;
    private float shakeAnimO;
    private final ServerBossEvent bossInfo = (ServerBossEvent) new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(false).setCreateWorldFog(false);
    private UUID bossInfoUUID = bossInfo.getId();
    private NearestHealableRaiderTargetGoal<Raider> healRaidersGoal;
    private NearestAttackableWitchTargetGoal<Player> attackPlayersGoal;

    public Codger(EntityType<? extends Cultist> type, Level worldIn) {
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
            return target != null && this.hasActiveRaid() && target.getType() != EntityType.WITCH && target.getType() != ModEntityType.WARLOCK.get();
        });
        this.attackPlayersGoal = new NearestAttackableWitchTargetGoal<>(this, Player.class, 10, true, false, (Predicate<LivingEntity>)null);
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 20, 40, 10.0F));
        this.goalSelector.addGoal(1, new WitchBarterGoal(this));
        this.goalSelector.addGoal(1, new CodgerTeleportGoal(this));
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

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.CroneHealth.get());
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

    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setVisible(MainConfig.SpecialBossBar.get());
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

    @Override
    public void remove(RemovalReason p_146834_) {
        if (this.level.isClientSide) {
            Goety.PROXY.removeBoss(this);
        }
        super.remove(p_146834_);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level.isClientSide && this.isSpecial && !this.isShaking && !this.isPathFinding() && this.onGround()) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
            this.level.broadcastEntityEvent(this, (byte)8);
        }

        if (this.coolDown > 0){
            --this.coolDown;
        }

        if (this.getTarget() != null) {
            if ((this.isSpecial || this.isShaking) && this.isShaking) {
                if (this.shakeAnim == 0.0F) {
                    this.playSound(SoundEvents.WART_BLOCK_STEP, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    this.gameEvent(GameEvent.ENTITY_SHAKE);
                }

                this.performRangedAttack(this.getTarget(), 1.0F);

                this.shakeAnimO = this.shakeAnim;
                this.shakeAnim += 0.05F;
                if (this.shakeAnimO >= 2.0F) {
                    this.isSpecial = false;
                    this.isShaking = false;
                    this.shakeAnimO = 0.0F;
                    this.shakeAnim = 0.0F;
                }

                if (this.shakeAnim > 0.4F) {
                    float f = (float) this.getY();
                    int i = (int) (Mth.sin((this.shakeAnim - 0.4F) * (float) Math.PI) * 7.0F);
                    Vec3 vec3 = this.getDeltaMovement();

                    for (int j = 0; j < i; ++j) {
                        float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                        float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                        this.level.addParticle(ParticleTypes.CRIMSON_SPORE, this.getX() + (double) f1, (double) (f + 0.8F), this.getZ() + (double) f2, vec3.x, vec3.y, vec3.z);
                    }
                }
            }
        } else {
            this.isSpecial = false;
            this.isShaking = false;
            this.shakeAnimO = 0.0F;
            this.shakeAnim = 0.0F;
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
                        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.BLAST_FUNGUS.get()));
                    } else {
                        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    }
                    if (this.getHealth() <= this.getMaxHealth() / 4 && this.tickCount % 10 == 0 && this.random.nextBoolean()){
                        MobUtil.throwBlastFungus(this, level);
                    }
                    if (this.tickCount % 100 == 0 && this.random.nextFloat() <= 0.25F && !this.isSpecial && !this.isShaking){
                        this.isSpecial = true;
                        this.level.broadcastEntityEvent(this, (byte)7);
                    }
                } else {
                    this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.BERSERK_FUNGUS.get()));
                }
            } else {
                if (this.getMainHandItem().is(ModItems.BLAST_FUNGUS.get()) || this.getMainHandItem().is(ModItems.BERSERK_FUNGUS.get())){
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

    private void cancelShake() {
        this.isShaking = false;
        this.shakeAnim = 0.0F;
        this.shakeAnimO = 0.0F;
    }

    public float getBodyRollAngle(float p_30433_, float p_30434_) {
        float f = (Mth.lerp(p_30433_, this.shakeAnimO, this.shakeAnim) + p_30434_) / 1.8F;
        if (f < 0.0F) {
            f = 0.0F;
        } else if (f > 1.0F) {
            f = 1.0F;
        }

        return Mth.sin(f * (float)Math.PI) * Mth.sin(f * (float)Math.PI * 11.0F) * 0.15F * (float)Math.PI;
    }

    public void handleEntityEvent(byte p_34138_) {
        if (p_34138_ == 7){
            this.isSpecial = true;
        } else if (p_34138_ == 8) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
        } else if (p_34138_ == 56) {
            this.cancelShake();
        } else if (p_34138_ == 15) {
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

        if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
            damage *= 0.15F;
        }

        return damage;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        this.lastHitTime = MathHelper.secondsToTicks(15);

        if (pAmount >= 15){
            this.overwhelmed = MathHelper.secondsToTicks(15);
        }

        if (this.getHealth() <= 10.0F){
            if (pSource.is(DamageTypeTags.IS_EXPLOSION)){
                return false;
            }
        }

        if (!pSource.is(DamageTypeTags.AVOIDS_GUARDIAN_THORNS) && !pSource.is(DamageTypes.THORNS) && pSource.getEntity() instanceof LivingEntity livingentity && livingentity != this) {
            float thorn = 2.0F;
            if (this.level.getDifficulty() == Difficulty.HARD){
                thorn *= 2.0F;
            }
            livingentity.hurt(this.damageSources().thorns(this), thorn);
        }

        return super.hurt(pSource, pAmount);
    }

    @Override
    public void performRangedAttack(LivingEntity p_33317_, float p_33318_) {
        if (p_33317_.distanceTo(this) < 6.0F && this.coolDown <= 0 && this.level.getBlockState(this.blockPosition().above(2)).isAir() && !(this.getTarget() instanceof Raider)) {
            this.totalCool = Mth.nextInt(this.random, 6, 10);
            for (int i = 0; i < this.totalCool; ++i) {
                MobUtil.throwBlastFungus(this, level);
            }
            if (this.getHealth() <= this.getMaxHealth() / 2){
                this.coolDown = MathHelper.secondsToTicks(this.totalCool / 2);
            } else {
                this.coolDown = MathHelper.secondsToTicks(this.totalCool);
            }
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
            wartling.setMega();
            this.level.addFreshEntity(wartling);
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

    @Override
    public UUID getBossInfoUUID() {
        return this.bossInfoUUID;
    }

    @Override
    public void setBossInfoUUID(UUID bossInfoUUID) {
        this.bossInfoUUID = bossInfoUUID;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) ModNetwork.INSTANCE.toVanillaPacket(new SAddBossPacket(new ClientboundAddEntityPacket(this), bossInfoUUID), NetworkDirection.PLAY_TO_CLIENT);
    }

    static class CodgerTeleportGoal extends Goal {
        private final Codger codger;
        private int teleportTime;

        public CodgerTeleportGoal(Codger p_32573_) {
            this.codger = p_32573_;
        }

        public boolean canUse() {
            return this.codger.getTarget() != null;
        }

        public void start() {
            super.start();
            this.teleportTime = 0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.codger.getTarget() != null;
        }

        public void tick() {
            super.tick();
            if (this.codger.getTarget() != null && !this.codger.isPassenger()) {
                if ((this.codger.getTarget().distanceToSqr(this.codger) > 256 || !MobUtil.hasVisualLineOfSight(this.codger, this.codger.getTarget())) && this.teleportTime++ >= this.adjustedTickDelay(30) && this.codger.teleportTowards(this.codger.getTarget())) {
                    this.teleportTime = 0;
                }
            }
        }
    }
}
