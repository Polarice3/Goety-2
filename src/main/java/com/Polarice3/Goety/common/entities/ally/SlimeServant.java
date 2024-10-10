package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class SlimeServant extends Summoned{
    private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(SlimeServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_INTERESTED_ID = SynchedEntityData.defineId(SlimeServant.class, EntityDataSerializers.BOOLEAN);
    private float interestTime;
    public static final int MIN_SIZE = 1;
    public static final int MAX_SIZE = 127;
    public float targetSquish;
    public float squish;
    public float oSquish;
    private boolean wasOnGround;

    public SlimeServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new SlimeServantMoveControl(this);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SlimeFloatGoal(this));
        this.goalSelector.addGoal(2, new SlimeAttackGoal(this));
        this.goalSelector.addGoal(3, new SlimeRandomDirectionGoal(this));
        this.goalSelector.addGoal(5, new SlimeKeepOnJumpingGoal(this));
    }

    public void followGoal(){
        this.goalSelector.addGoal(8, new SlimeFollowGoal(this, 4.0F, 10.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_SIZE, 1);
        this.entityData.define(DATA_INTERESTED_ID, false);
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return EntityType.SLIME.getDefaultLootTable();
    }

    protected LootContext.Builder createLootContext(boolean p_21105_, DamageSource p_21106_) {
        Slime slime = new Slime(EntityType.SLIME, this.level);
        slime.setSize(this.getSize(), true);
        LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel)this.level)).withRandom(this.random).withParameter(LootContextParams.THIS_ENTITY, slime).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.DAMAGE_SOURCE, p_21106_).withOptionalParameter(LootContextParams.KILLER_ENTITY, p_21106_.getEntity()).withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, p_21106_.getDirectEntity());
        if (p_21105_ && this.lastHurtByPlayer != null) {
            lootcontext$builder = lootcontext$builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, this.lastHurtByPlayer).withLuck(this.lastHurtByPlayer.getLuck());
        }

        return lootcontext$builder;
    }

    @Override
    protected boolean shouldDropLoot() {
        return !this.limitedLifespan;
    }

    public void setSize(int p_33594_, boolean p_33595_) {
        int i = Mth.clamp(p_33594_, 1, 127);
        this.entityData.set(ID_SIZE, i);
        this.reapplyPosition();
        this.refreshDimensions();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)(i * i));
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)i));
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double)i);
        if (p_33595_) {
            this.setHealth(this.getMaxHealth());
        }

        this.xpReward = i;
    }

    public int getSize() {
        return this.entityData.get(ID_SIZE);
    }

    public void setIsInterested(boolean pBeg) {
        this.entityData.set(DATA_INTERESTED_ID, pBeg);
    }

    public boolean isInterested() {
        return this.entityData.get(DATA_INTERESTED_ID);
    }

    public void addAdditionalSaveData(CompoundTag p_33619_) {
        super.addAdditionalSaveData(p_33619_);
        p_33619_.putInt("Size", this.getSize() - 1);
        p_33619_.putBoolean("wasOnGround", this.wasOnGround);
    }

    public void readAdditionalSaveData(CompoundTag p_33607_) {
        this.setSize(p_33607_.getInt("Size") + 1, false);
        super.readAdditionalSaveData(p_33607_);
        this.wasOnGround = p_33607_.getBoolean("wasOnGround");
    }

    public boolean isTiny() {
        return this.getSize() <= 1;
    }

    protected ParticleOptions getParticleType() {
        return ParticleTypes.ITEM_SLIME;
    }

    protected boolean shouldDespawnInPeaceful() {
        return this.getSize() > 0 && super.shouldDespawnInPeaceful();
    }

    @Override
    public boolean canUpdateMove() {
        return true;
    }

    public void tick() {
        this.squish += (this.targetSquish - this.squish) * 0.5F;
        this.oSquish = this.squish;
        if (this.isAlive()) {
            if (this.isInterested()) {
                --this.interestTime;
            }
            if (this.interestTime <= 0){
                this.setIsInterested(false);
            }
            if (this.getTarget() != null && this.getTarget().isAlive()){
                AABB aabb;
                if (this.getTarget().isPassenger() && this.getTarget().getVehicle() != null) {
                    aabb = this.getTarget().getBoundingBox().minmax(this.getTarget().getVehicle().getBoundingBox()).inflate(1.0D, 0.0D, 1.0D);
                } else {
                    aabb = this.getTarget().getBoundingBox().inflate(1.0D, 0.5D, 1.0D);
                }

                List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, aabb);

                for (LivingEntity target : list) {
                    if (target == this.getTarget() && this.isDealsDamage()) {
                        this.dealDamage(target);
                    }
                }
            }
        }
        super.tick();
        if (this.onGround && !this.wasOnGround) {
            int i = this.getSize();

            if (spawnCustomParticles()) i = 0;
            for(int j = 0; j < i * 8; ++j) {
                float f = this.random.nextFloat() * ((float)Math.PI * 2F);
                float f1 = this.random.nextFloat() * 0.5F + 0.5F;
                float f2 = Mth.sin(f) * (float)i * 0.5F * f1;
                float f3 = Mth.cos(f) * (float)i * 0.5F * f1;
                this.level.addParticle(this.getParticleType(), this.getX() + (double)f2, this.getY(), this.getZ() + (double)f3, 0.0D, 0.0D, 0.0D);
            }

            this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            this.targetSquish = -0.5F;
        } else if (!this.onGround && this.wasOnGround) {
            this.targetSquish = 1.0F;
        }

        this.wasOnGround = this.onGround;
        this.decreaseSquish();
    }

    @Override
    public void lifeSpanDamage() {
        if (!this.level.isClientSide){
            for(int i = 0; i < this.level.random.nextInt(35) + 10; ++i) {
                ServerParticleUtil.smokeParticles(ParticleTypes.POOF, this.getX(), this.getEyeY(), this.getZ(), this.level);
            }
        }
        this.discard();
    }

    protected void decreaseSquish() {
        this.targetSquish *= 0.6F;
    }

    protected int getJumpDelay() {
        return this.random.nextInt(20) + 10;
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33609_) {
        if (ID_SIZE.equals(p_33609_)) {
            this.refreshDimensions();
            this.setYRot(this.yHeadRot);
            this.yBodyRot = this.yHeadRot;
            if (this.isInWater() && this.random.nextInt(20) == 0) {
                this.doWaterSplashEffect();
            }
        }

        super.onSyncedDataUpdated(p_33609_);
    }

    public EntityType<? extends SlimeServant> getType() {
        return (EntityType<? extends SlimeServant>)super.getType();
    }

    public void remove(RemovalReason p_149847_) {
        int i = this.getSize();
        if (!this.level.isClientSide && i > 1 && this.isDeadOrDying()) {
            Component component = this.getCustomName();
            boolean flag = this.isNoAi();
            float f = (float)i / 4.0F;
            int j = i / 2;
            int k = 2 + this.random.nextInt(3);

            for(int l = 0; l < k; ++l) {
                float f1 = ((float)(l % 2) - 0.5F) * f;
                float f2 = ((float)(l / 2) - 0.5F) * f;
                SlimeServant slime = this.getType().create(this.level);
                if (slime != null) {
                    if (this.isPersistenceRequired()) {
                        slime.setPersistenceRequired();
                    }

                    slime.setCustomName(component);
                    slime.setNoAi(flag);
                    slime.setInvulnerable(this.isInvulnerable());
                    slime.setSize(j, true);
                    if (this.getTrueOwner() != null) {
                        slime.setTrueOwner(this.getTrueOwner());
                    }
                    if (this.limitedLifeTicks > 0) {
                        slime.setLimitedLife(this.limitedLifeTicks);
                    }
                    slime.setHostile(this.isHostile());
                    slime.moveTo(this.getX() + (double) f1, this.getY() + 0.5D, this.getZ() + (double) f2, this.random.nextFloat() * 360.0F, 0.0F);
                    this.level.addFreshEntity(slime);
                }
            }
        }

        super.remove(p_149847_);
    }

    @Override
    public void tryKill(Player player) {
        if (this.limitedLifespan){
            this.lifeSpanDamage();
        } else {
            super.tryKill(player);
        }
    }

    public void push(Entity p_33636_) {
        super.push(p_33636_);
        if (p_33636_ == this.getTarget() && this.isDealsDamage()) {
            this.dealDamage((LivingEntity)p_33636_);
        }

    }

    protected void dealDamage(LivingEntity p_33638_) {
        if (this.isAlive()) {
            int i = this.getSize();
            if (this.distanceToSqr(p_33638_) < 0.6D * (double)i * 0.6D * (double)i && this.hasLineOfSight(p_33638_) && p_33638_.hurt(DamageSource.mobAttack(this), this.getAttackDamage())) {
                this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.doEnchantDamageEffects(this, p_33638_);
            }
        }

    }

    protected float getStandingEyeHeight(Pose p_33614_, EntityDimensions p_33615_) {
        return 0.625F * p_33615_.height;
    }

    protected boolean isDealsDamage() {
        return !this.isTiny() && this.isEffectiveAi();
    }

    protected float getAttackDamage() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    protected SoundEvent getHurtSound(DamageSource p_33631_) {
        return this.isTiny() ? SoundEvents.SLIME_HURT_SMALL : SoundEvents.SLIME_HURT;
    }

    protected SoundEvent getDeathSound() {
        return this.isTiny() ? SoundEvents.SLIME_DEATH_SMALL : SoundEvents.SLIME_DEATH;
    }

    protected SoundEvent getSquishSound() {
        return this.isTiny() ? SoundEvents.SLIME_SQUISH_SMALL : SoundEvents.SLIME_SQUISH;
    }

    protected float getSoundVolume() {
        return 0.4F * (float)this.getSize();
    }

    public int getMaxHeadXRot() {
        return 0;
    }

    protected boolean doPlayJumpSound() {
        return this.getSize() > 0;
    }

    protected void jumpFromGround() {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, (double)this.getJumpPower(), vec3.z);
        this.hasImpulse = true;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_33601_, DifficultyInstance p_33602_, MobSpawnType p_33603_, @Nullable SpawnGroupData p_33604_, @Nullable CompoundTag p_33605_) {
        if (p_33603_ != MobSpawnType.MOB_SUMMONED) {
            RandomSource randomsource = p_33601_.getRandom();
            int i = randomsource.nextInt(3);
            if (i < 2 && randomsource.nextFloat() < 0.5F * p_33602_.getSpecialMultiplier()) {
                ++i;
            }

            int j = 1 << i;
            this.setSize(j, true);
        }
        return super.finalizeSpawn(p_33601_, p_33602_, p_33603_, p_33604_, p_33605_);
    }

    float getSoundPitch() {
        float f = this.isTiny() ? 1.4F : 0.8F;
        return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
    }

    protected SoundEvent getJumpSound() {
        return this.isTiny() ? SoundEvents.SLIME_JUMP_SMALL : SoundEvents.SLIME_JUMP;
    }

    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale(0.255F * (float)this.getSize());
    }

    protected boolean spawnCustomParticles() {
        return false;
    }

    public Item getIncreaseItem(){
        return Items.SLIME_BLOCK;
    }

    public Item getHealItem(){
        return Items.SLIME_BALL;
    }

    public InteractionResult mobInteract(Player p_34394_, InteractionHand p_34395_) {
        ItemStack itemstack = p_34394_.getItemInHand(p_34395_);
        if (itemstack.is(this.getIncreaseItem())) {
            if (this.getSize() < MobsConfig.MaxSlimeSize.get()) {
                if (!p_34394_.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                this.setSize(this.getSize() + 1, true);
                this.playSound(SoundEvents.SLIME_ATTACK);

                return InteractionResult.SUCCESS;
            } else if (this.getHealth() < this.getMaxHealth()) {
                if (!p_34394_.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.heal(20.0F);
                this.playSound(this.getSquishSound());
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        } else if (itemstack.is(this.getHealItem())) {
            if (this.getHealth() < this.getMaxHealth()) {
                if (!p_34394_.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.heal(2.0F);
                this.playSound(this.getSquishSound());
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        } else if (this.getTrueOwner() != null && p_34394_ == this.getTrueOwner()) {
            if (!this.isInterested()) {
                if (p_34395_ == InteractionHand.MAIN_HAND && itemstack.isEmpty() && !p_34394_.isCrouching()) {
                    this.setIsInterested(true);
                    this.interestTime = 40;
                    this.level.broadcastEntityEvent(this, (byte) 102);
                    this.playSound(this.getSquishSound(), 1.0F, 2.0F);
                    this.heal(1.0F);
                    if (this instanceof MagmaCubeServant){
                        if (!p_34394_.fireImmune()){
                            p_34394_.setSecondsOnFire(5);
                            if (p_34394_.hurt(DamageSource.HOT_FLOOR, 2.0F)) {
                                p_34394_.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
                            }
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(p_34394_, p_34395_);
    }

    public void handleEntityEvent(byte pId) {
        super.handleEntityEvent(pId);
        if (pId == 102){
            this.setIsInterested(true);
            this.interestTime = 40;
            this.playSound(SoundEvents.SLIME_SQUISH, 1.0F, 2.0F);
            this.addParticlesAroundSelf(ParticleTypes.HEART);
        }
    }

    protected void addParticlesAroundSelf(ParticleOptions pParticleData) {
        for(int i = 0; i < 5; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(pParticleData, this.getRandomX(1.0D), this.getRandomY() + 1.0D, this.getRandomZ(1.0D), d0, d1, d2);
        }

    }

    static class SlimeAttackGoal extends Goal {
        private final SlimeServant slime;
        private int growTiredTimer;

        public SlimeAttackGoal(SlimeServant p_33648_) {
            this.slime = p_33648_;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.slime.getTarget();
            if (livingentity == null) {
                return false;
            } else {
                return this.slime.canAttack(livingentity) && this.slime.getMoveControl() instanceof SlimeServantMoveControl;
            }
        }

        public void start() {
            this.growTiredTimer = reducedTickDelay(300);
            super.start();
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = this.slime.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!this.slime.canAttack(livingentity)) {
                return false;
            } else {
                return --this.growTiredTimer > 0;
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = this.slime.getTarget();
            if (livingentity != null) {
                this.slime.lookAt(livingentity, 10.0F, 10.0F);
            }

            MoveControl movecontrol = this.slime.getMoveControl();
            if (movecontrol instanceof SlimeServantMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setDirection(this.slime.getYRot(), this.slime.isDealsDamage());
            }
        }
    }

    static class SlimeFollowGoal extends Goal {
        private final SlimeServant slime;
        private LivingEntity owner;
        private final float stopDistance;
        private final float startDistance;

        public SlimeFollowGoal(SlimeServant p_33648_, float startDistance, float stopDistance) {
            this.slime = p_33648_;
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.slime.getTrueOwner();
            if (livingentity == null) {
                return false;
            } else if (!(this.slime.getMoveControl() instanceof SlimeServantMoveControl)){
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } else if (this.slime.distanceToSqr(livingentity) < (double)(Mth.square(this.startDistance))) {
                return false;
            } else if (!this.slime.isFollowing() || this.slime.isCommanded()) {
                return false;
            } else if (this.slime.getTarget() != null) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }

        public void start() {
            super.start();
        }

        public boolean canContinueToUse() {
            if (this.slime.getNavigation().isDone()) {
                return false;
            } else if (this.slime.getTarget() != null){
                return false;
            } else {
                return !(this.slime.distanceToSqr(this.owner) <= (double)(Mth.square(this.stopDistance)));
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.owner != null) {
                this.slime.lookAt(this.owner, 10.0F, 10.0F);
            }

            MoveControl movecontrol = this.slime.getMoveControl();
            if (movecontrol instanceof SlimeServantMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setDirection(this.slime.getYRot(), true);
                slime$slimemovecontrol.setWantedMovement(1.0D);
            }
        }
    }

    static class SlimeFloatGoal extends Goal {
        private final SlimeServant slime;

        public SlimeFloatGoal(SlimeServant p_33655_) {
            this.slime = p_33655_;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
            p_33655_.getNavigation().setCanFloat(true);
        }

        public boolean canUse() {
            return (this.slime.isInWater()
                    || this.slime.isInLava())
                    && this.slime.getMoveControl() instanceof SlimeServantMoveControl;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.slime.getRandom().nextFloat() < 0.8F) {
                this.slime.getJumpControl().jump();
            }

            MoveControl movecontrol = this.slime.getMoveControl();
            if (movecontrol instanceof SlimeServantMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setWantedMovement(1.2D);
            }
        }
    }

    static class SlimeKeepOnJumpingGoal extends Goal {
        private final SlimeServant slime;

        public SlimeKeepOnJumpingGoal(SlimeServant p_33660_) {
            this.slime = p_33660_;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        }

        public boolean canUse() {
            return !this.slime.isPassenger()
                    && !this.slime.isStaying()
                    && (this.slime.getTrueOwner() == null
                    || this.slime.isWandering() || this.slime.getTarget() != null);
        }

        public void tick() {
            MoveControl movecontrol = this.slime.getMoveControl();
            if (movecontrol instanceof SlimeServantMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setWantedMovement(1.0D);
            }
        }
    }

    static class SlimeServantMoveControl extends MoveControl {
        private float yRot;
        private int jumpDelay;
        private final SlimeServant slime;
        private boolean isAggressive;

        public SlimeServantMoveControl(SlimeServant p_33668_) {
            super(p_33668_);
            this.slime = p_33668_;
            this.yRot = 180.0F * p_33668_.getYRot() / (float)Math.PI;
        }

        public void setDirection(float p_33673_, boolean p_33674_) {
            this.yRot = p_33673_;
            this.isAggressive = p_33674_;
        }

        public void setWantedMovement(double p_33671_) {
            this.speedModifier = p_33671_;
            this.operation = Operation.MOVE_TO;
        }

        public void tick() {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
            } else {
                this.operation = Operation.WAIT;
                if (this.mob.isOnGround()) {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = this.slime.getJumpDelay();
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }

                        this.slime.getJumpControl().jump();
                        if (this.slime.doPlayJumpSound()) {
                            this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getSoundPitch());
                        }
                    } else {
                        this.slime.xxa = 0.0F;
                        this.slime.zza = 0.0F;
                        this.mob.setSpeed(0.0F);
                    }
                } else {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }

            }
        }
    }

    static class SlimeRandomDirectionGoal extends Goal {
        private final SlimeServant slime;
        private float chosenDegrees;
        private int nextRandomizeTime;

        public SlimeRandomDirectionGoal(SlimeServant p_33679_) {
            this.slime = p_33679_;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        public boolean canUse() {
            return this.slime.getTarget() == null
                    && (this.slime.getTrueOwner() == null || this.slime.isWandering())
                    && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava() || this.slime.hasEffect(MobEffects.LEVITATION))
                    && this.slime.getMoveControl() instanceof SlimeServantMoveControl;
        }

        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = this.adjustedTickDelay(40 + this.slime.getRandom().nextInt(60));
                this.chosenDegrees = (float)this.slime.getRandom().nextInt(360);
            }

            MoveControl movecontrol = this.slime.getMoveControl();
            if (movecontrol instanceof SlimeServantMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setDirection(this.chosenDegrees, false);
            }
        }
    }
}
