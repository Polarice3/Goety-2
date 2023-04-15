package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.ModMathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class Wartling extends Summoned {
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(Wartling.class, EntityDataSerializers.BYTE);
    private int searchTime;
    private MobEffectInstance effect;

    public Wartling(EntityType<? extends Summoned> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    public void tick() {
        if (!this.level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) this.level;
            this.setClimbing(this.horizontalCollision);
            if (this.getStoredEffect() != null){
                int i = getColor(this.getStoredEffect());
                if (i > 0) {
                    boolean flag;
                    if (this.isInvisible()) {
                        flag = this.random.nextInt(15) == 0;
                    } else {
                        flag = this.random.nextBoolean();
                    }

                    if (flag) {
                        double d0 = (double)(i >> 16 & 255) / 255.0D;
                        double d1 = (double)(i >> 8 & 255) / 255.0D;
                        double d2 = (double)(i >> 0 & 255) / 255.0D;
                        serverLevel.sendParticles(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0, d0, d1, d2, 0.5F);
                    }
                }
            }
        }
        super.tick();
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new AvoidOwnerGoal(this, 8.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, (double)0.3F)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    public int xpReward() {
        return 1;
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new WallClimberNavigation(this, worldIn);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CLIMBING, (byte)0);
    }

    public void addAdditionalSaveData(CompoundTag p_21145_) {
        super.addAdditionalSaveData(p_21145_);
        if (this.getStoredEffect() != null){
            p_21145_.put("StoredEffect", this.getStoredEffect().save(new CompoundTag()));
        }
        p_21145_.putInt("SearchTime", this.searchTime);
    }

    public void readAdditionalSaveData(CompoundTag p_21096_) {
        super.readAdditionalSaveData(p_21096_);
        if (p_21096_.contains("StoredEffect")){
            this.setStoredEffect(MobEffectInstance.load(p_21096_.getCompound("StoredEffect")));
        }
        this.searchTime = p_21096_.getInt("SearchTime");
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    public float getVoicePitch() {
        return super.getVoicePitch() * 1.5F;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.SPIDER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
    }

    public boolean onClimbable() {
        return this.isBesideClimbableBlock();
    }

    public void makeStuckInBlock(BlockState state, Vec3 motionMultiplierIn) {
        if (!state.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(state, motionMultiplierIn);
        }

    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Override
    public void die(DamageSource pCause) {
        super.die(pCause);
        if (!this.level.isClientSide) {
            if (this.getStoredEffect() != null) {
                AreaEffectCloud areaEffectCloud = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
                areaEffectCloud.setRadius(1.0F);
                areaEffectCloud.setRadiusOnUse(-0.5F);
                areaEffectCloud.setWaitTime(10);
                areaEffectCloud.setDuration(areaEffectCloud.getDuration() / 2);
                areaEffectCloud.setRadiusPerTick(-areaEffectCloud.getRadius() / (float) areaEffectCloud.getDuration());
                areaEffectCloud.addEffect(this.getStoredEffect());
                this.level.addFreshEntity(areaEffectCloud);
            }
        }
    }

    public void lifeSpanDamage(){
        if (this.getTrueOwner() != null
                && this.getTrueOwner().isAlive()
                && !this.getTrueOwner().isDeadOrDying()
                && (this.getStoredEffect() == null || this.getStoredEffect().getEffect().isBeneficial())){
            this.getNavigation().moveTo(this.getTrueOwner(), 1.25F);
            this.setTarget(null);
            ++this.searchTime;
            if (this.getBoundingBox().intersects(this.getTrueOwner().getBoundingBox())){
                this.getTrueOwner().heal(this.getHealth());
                for (MobEffectInstance mobEffectInstance : this.getActiveEffects()){
                    if (mobEffectInstance.getEffect().isBeneficial()){
                        this.getTrueOwner().addEffect(mobEffectInstance);
                    }
                }
                if (this.getStoredEffect() != null && this.getStoredEffect().getEffect().isBeneficial()){
                    this.getTrueOwner().addEffect(this.getStoredEffect());
                }
                this.playSound(SoundEvents.SCULK_BLOCK_SPREAD, 1.0F, 1.0F);
                if (!this.level.isClientSide) {
                    this.level.broadcastEntityEvent(this, (byte) 15);
                }
                this.discard();
            } else if (this.searchTime >= ModMathHelper.ticksToSeconds(5)){
                this.hurt(DamageSource.STARVE, this.getMaxHealth());
            }
        } else {
            this.limitedLifeTicks = 20;
            this.hurt(DamageSource.STARVE, this.getMaxHealth());
        }
    }

    public boolean canBeAffected(MobEffectInstance potionEffectIn) {
        if (potionEffectIn.getEffect() == MobEffects.POISON) {
            net.minecraftforge.event.entity.living.MobEffectEvent.Applicable event = new net.minecraftforge.event.entity.living.MobEffectEvent.Applicable(this, potionEffectIn);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
            return event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW;
        }
        return potionEffectIn.getEffect() != this.getStoredEffect().getEffect() && super.canBeAffected(potionEffectIn);
    }

    public void setStoredEffect(MobEffectInstance effect){
        this.effect = effect;
    }

    public MobEffectInstance getStoredEffect() {
        return this.effect;
    }

    public static int getColor(MobEffectInstance mobEffectInstance) {
        if (mobEffectInstance == null) {
            return 3694022;
        } else {
            float f = 0.0F;
            float f1 = 0.0F;
            float f2 = 0.0F;
            int j = 0;

            int k = mobEffectInstance.getEffect().getColor();
            int l = mobEffectInstance.getAmplifier() + 1;
            f += (float)(l * (k >> 16 & 255)) / 255.0F;
            f1 += (float)(l * (k >> 8 & 255)) / 255.0F;
            f2 += (float)(l * (k >> 0 & 255)) / 255.0F;
            j += l;

            if (j == 0) {
                return 0;
            } else {
                f = f / (float)j * 255.0F;
                f1 = f1 / (float)j * 255.0F;
                f2 = f2 / (float)j * 255.0F;
                return (int)f << 16 | (int)f1 << 8 | (int)f2;
            }
        }
    }

    public boolean isBesideClimbableBlock() {
        return (this.entityData.get(CLIMBING) & 1) != 0;
    }

    public void setClimbing(boolean climbing) {
        byte b0 = this.entityData.get(CLIMBING);
        if (climbing) {
            b0 = (byte)(b0 | 1);
        } else {
            b0 = (byte)(b0 & -2);
        }

        this.entityData.set(CLIMBING, b0);
    }

    public void handleEntityEvent(byte p_34138_) {
        if (p_34138_ == 15) {
            for(int i = 0; i < this.random.nextInt(35) + 10; ++i) {
                this.level.addParticle(ModParticleTypes.CULT_SPELL.get(), this.getX() + this.random.nextGaussian() * (double)0.13F, this.getY() + this.random.nextGaussian() * (double)0.13F, this.getZ() + this.random.nextGaussian() * (double)0.13F, 1.0D, 1.0D, 1.0D);
            }
        } else {
            super.handleEntityEvent(p_34138_);
        }

    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        return spawnDataIn;
    }

    public static class AvoidOwnerGoal extends Goal {
        protected final Wartling mob;
        private final double walkSpeedModifier;
        private final double sprintSpeedModifier;
        protected final float maxDist;
        @Nullable
        protected Path path;
        protected final PathNavigation pathNav;

        public AvoidOwnerGoal(Wartling p_25040_, float p_25043_, double p_25044_, double p_25045_) {
            this.mob = p_25040_;
            this.maxDist = p_25043_;
            this.walkSpeedModifier = p_25044_;
            this.sprintSpeedModifier = p_25045_;
            this.pathNav = p_25040_.getNavigation();
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (this.mob.getTrueOwner() == null || this.mob.getTrueOwner().isDeadOrDying()) {
                return false;
            } else {
                Vec3 vec3 = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.mob.getTrueOwner().position());
                if (vec3 == null) {
                    return false;
                } else if (this.mob.getTrueOwner().distanceToSqr(vec3.x, vec3.y, vec3.z) < this.mob.getTrueOwner().distanceToSqr(this.mob)) {
                    return false;
                } else {
                    this.path = this.pathNav.createPath(vec3.x, vec3.y, vec3.z, 0);
                    return this.path != null
                            && this.mob.getStoredEffect() != null
                            && this.mob.getStoredEffect().getEffect().getCategory() != MobEffectCategory.BENEFICIAL
                            && this.mob.getTarget() == null;
                }
            }
        }

        public boolean canContinueToUse() {
            return !this.pathNav.isDone() && this.mob.getTrueOwner() != null && this.mob.getTrueOwner().isAlive();
        }

        public void start() {
            this.pathNav.moveTo(this.path, this.walkSpeedModifier);
        }

        public void stop() {
            this.pathNav.stop();
        }

        public void tick() {
            if (this.mob.getTrueOwner() != null) {
                if (this.mob.distanceToSqr(this.mob.getTrueOwner()) < 49.0D) {
                    this.mob.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
                } else {
                    this.mob.getNavigation().setSpeedModifier(this.walkSpeedModifier);
                }
            }
        }
    }
}
