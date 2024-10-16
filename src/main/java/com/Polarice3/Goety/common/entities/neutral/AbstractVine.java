package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.client.particles.DustCloudParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import org.joml.Vector3f;

public abstract class AbstractVine extends AbstractMonolith{
    public static final EntityDataAccessor<Boolean> PERPETUAL = SynchedEntityData.defineId(AbstractVine.class, EntityDataSerializers.BOOLEAN);
    public int warmupDelayTicks;
    public int activeTick = 0;
    public boolean proximity = false;

    public AbstractVine(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PERPETUAL, false);
    }

    public void addAdditionalSaveData(CompoundTag p_31485_) {
        super.addAdditionalSaveData(p_31485_);
        p_31485_.putInt("ActiveTick", this.activeTick);
        p_31485_.putInt("Warmup", this.warmupDelayTicks);
        p_31485_.putBoolean("Proximity", this.proximity);
        p_31485_.putBoolean("Perpetual", this.isPerpetual());
    }

    public void readAdditionalSaveData(CompoundTag p_31474_) {
        super.readAdditionalSaveData(p_31474_);
        if (p_31474_.contains("ActiveTick")) {
            this.activeTick = p_31474_.getInt("ActiveTick");
        }
        if (p_31474_.contains("Warmup")) {
            this.warmupDelayTicks = p_31474_.getInt("Warmup");
        }
        if (p_31474_.contains("Proximity")){
            this.proximity = p_31474_.getBoolean("Proximity");
        }
        if (p_31474_.contains("Perpetual")){
            this.setPerpetual(p_31474_.getBoolean("Perpetual"));
        }
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NATURAL;
    }

    public boolean isPerpetual(){
        return this.entityData.get(PERPETUAL);
    }

    public void setPerpetual(boolean perpetual){
        this.entityData.set(PERPETUAL, perpetual);
    }

    public void setWarmup(int warmupDelayTicks) {
        this.warmupDelayTicks = warmupDelayTicks;
    }

    public void setProximity(boolean proximity){
        this.proximity = proximity;
    }

    @Override
    public boolean isInvisible() {
        return super.isInvisible() || this.activeTick < 1;
    }

    public boolean canHaveEffects(){
        return true;
    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        if (this.isEmerging() || this.isDescending()){
            return false;
        }
        return super.hurt(p_21016_, p_21017_);
    }

    protected AABB getTargetSearchArea() {
        return this.getBoundingBox().inflate(4.0D, 4.0D, 4.0D);
    }

    protected boolean hasTarget() {
        return !this.level.getEntitiesOfClass(LivingEntity.class, this.getTargetSearchArea(), (p_148152_) -> SummonTargetGoal.predicate(this).test(p_148152_)).isEmpty();
    }

    public void playAmbientSound() {
        if (this.activeTick > 0){
            super.playAmbientSound();
        }
    }

    protected abstract SoundEvent getBurstSound();

    protected abstract SoundEvent getBurrowSound();

    public boolean isAquatic(){
        return false;
    }

    protected void handleAirSupply(int p_30344_) {
        if (this.isAlive() && !this.isInWaterOrBubble()) {
            this.setAirSupply(p_30344_ - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(this.damageSources().dryOut(), 2.0F);
            }
        } else {
            this.setAirSupply(300);
        }

    }

    public void baseTick() {
        int i = this.getAirSupply();
        super.baseTick();
        if (this.isAquatic()) {
            this.handleAirSupply(i);
        }
    }

    public boolean canDrownInFluidType(FluidType type) {
        if (this.isAquatic()) {
            return type != ForgeMod.WATER_TYPE.get();
        } else {
            return super.canDrownInFluidType(type);
        }
    }

    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.activeTick > 0;
    }

    public boolean passableEntities(Entity collider){
        return collider instanceof LivingEntity livingEntity
                && ((livingEntity == this.getMasterOwner()
                && CuriosFinder.hasWildRobe(livingEntity))
                || (this.getMasterOwner() != null
                && CuriosFinder.hasWildRobe(this.getMasterOwner())
                && MobUtil.areAllies(livingEntity, this.getMasterOwner())));
    }

    public void aiStep() {
        if (this.activeTick > 0){
            super.aiStep();
        }
        if (!this.level.isClientSide) {
            if (this.warmupDelayTicks > 0){
                --this.warmupDelayTicks;
                if (!this.canSpawn(this.level)){
                    this.discard();
                }
            } else {
                boolean flag;
                if (this.proximity){
                    flag = this.hasTarget();
                } else {
                    flag = true;
                }
                if (flag) {
                    if (this.proximity) {
                        this.proximity = false;
                    }
                    ++this.activeTick;
                    this.level.broadcastEntityEvent(this, (byte) 6);
                    this.burst();
                }
            }
        }
        if (!this.isEmerging()){
            if (!this.isActivate()){
                this.setActivate(true);
            }
            if (!this.isPerpetual()) {
                if (!this.level.isClientSide) {
                    if (this.activeTick == MathHelper.secondsToTicks(this.getLifeSpan())){
                        this.burrow();
                    } else if (this.activeTick >= MathHelper.secondsToTicks(this.getLifeSpan())){
                        this.setAge(this.getAge() - this.getAgeSpeed());
                        this.level.broadcastEntityEvent(this, (byte) 5);
                    }
                    if (this.getAge() <= 0){
                        this.discard();
                    }
                }
            }
            if (!this.level.isClientSide) {
                if (!this.isOnFire() && !this.isDeadOrDying() && (!this.limitedLifespan || this.limitedLifeTicks > 20)) {
                    if (this.getHealth() < this.getMaxHealth()){
                        if (this.getTrueOwner() instanceof Player owner) {
                            boolean curio = false;
                            int soulCost = 0;
                            int healRate = 0;
                            float healAmount = 0;
                            if (MobsConfig.NaturalMinionHeal.get()){
                                curio = CuriosFinder.hasWildRobe(owner);
                                soulCost = MobsConfig.NaturalMinionHealCost.get();
                                healRate = MobsConfig.NaturalMinionHealTime.get();
                                healAmount = MobsConfig.NaturalMinionHealAmount.get().floatValue();
                            }
                            if (curio) {
                                if (SEHelper.getSoulsAmount(owner, soulCost)) {
                                    if (this.tickCount % (MathHelper.secondsToTicks(healRate) + 1) == 0) {
                                        this.heal(healAmount);
                                        Vec3 vector3d = this.getDeltaMovement();
                                        if (this.level instanceof ServerLevel serverWorld) {
                                            SEHelper.decreaseSouls(owner, soulCost);
                                            serverWorld.sendParticles(ParticleTypes.SCULK_SOUL, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0, vector3d.x * -0.2D, 0.1D, vector3d.z * -0.2D, 0.5F);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void burst(){
        if (this.activeTick == 1) {
            this.diggingParticles();
        }
    }

    public void burrow(){
        this.diggingParticles();
    }

    public void diggingParticles(){
        if (this.level instanceof ServerLevel serverLevel) {
            BlockPos blockPos = BlockPos.containing(this.getX(), this.getY() - 1.0F, this.getZ());
            BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, serverLevel.getBlockState(blockPos));
            Vector3f vector3f = new Vector3f(Vec3.fromRGB24(0xcf75af).toVector3f());
            if (this.isAquatic()){
                vector3f = new Vector3f(Vec3.fromRGB24(0x87a6d5).toVector3f());
            }
            DustCloudParticleOption cloudParticleOptions = new DustCloudParticleOption(vector3f, 1.0F);
            for (int i = 0; i < 8; ++i) {
                ServerParticleUtil.circularParticles(serverLevel, option, this.getX(), this.getY() + 0.25D, this.getZ(), 1.0F);
                ServerParticleUtil.circularParticles(serverLevel, cloudParticleOptions, this.getX(), this.getY() + 0.25D, this.getZ(), 0, 0.14D, 0, 1.0F);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 6){
            ++this.activeTick;
        } else {
            super.handleEntityEvent(pId);
        }
    }

    public ItemStack getSeed(){
        return ItemStack.EMPTY;
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand p_230254_2_) {
        if (!this.level.isClientSide){
            ItemStack itemstack = pPlayer.getItemInHand(p_230254_2_);
            Item item = itemstack.getItem();
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if (item == Items.BONE_MEAL){
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    if (this.getHealth() < this.getMaxHealth()) {
                        this.heal(5.0F);
                        this.playSound(SoundEvents.GROWING_PLANT_CROP, this.getSoundVolume(), this.getVoicePitch() + 0.25F);
                        if (this.level instanceof ServerLevel serverLevel) {
                            for (int i = 0; i < 7; ++i) {
                                double d0 = this.random.nextGaussian() * 0.02D;
                                double d1 = this.random.nextGaussian() * 0.02D;
                                double d2 = this.random.nextGaussian() * 0.02D;
                                serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                            }
                        }
                    } else {
                        this.playSound(SoundEvents.BONE_MEAL_USE, this.getSoundVolume(), this.getVoicePitch());
                        if (this.level instanceof ServerLevel serverLevel) {
                            for (int i = 0; i < 7; ++i) {
                                double d0 = this.random.nextGaussian() * 0.02D;
                                double d1 = this.random.nextGaussian() * 0.02D;
                                double d2 = this.random.nextGaussian() * 0.02D;
                                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                            }
                        }
                        if (this.level.random.nextFloat() <= 0.45F){
                            if (!this.getSeed().isEmpty()){
                                ItemHelper.addItemEntity(this.level, this.blockPosition().above(), this.getSeed());
                            }
                        }
                    }
                    pPlayer.swing(p_230254_2_);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(pPlayer, p_230254_2_);
    }
}
