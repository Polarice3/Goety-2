package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.api.entities.IAutoRideable;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.hoglin.HoglinBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class HoglinServant extends AnimalSummon implements HoglinBase, PlayerRideable, IAutoRideable {
    private static final EntityDataAccessor<Boolean> DATA_IMMUNE_TO_ZOMBIFICATION = SynchedEntityData.defineId(HoglinServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AUTO_MODE = SynchedEntityData.defineId(HoglinServant.class, EntityDataSerializers.BOOLEAN);
    private int attackAnimationRemainingTicks;
    private int timeInOverworld;
    private int retreatTime;
    private int avoidTime;
    public BlockPos nearestRepellent;
    private static final UniformInt RETREAT_DURATION = TimeUtil.rangeOfSeconds(5, 20);
    @Nullable
    private LivingEntity toAvoid;

    public HoglinServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new HoglinMeleeAttackGoal());
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new WanderGoal<>(this, 0.4D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.HoglinServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, (double)0.3F)
                .add(Attributes.KNOCKBACK_RESISTANCE, (double)0.6F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.ARMOR, AttributesConfig.HoglinServantArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.HoglinServantDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.HoglinServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.HoglinServantArmor.get());
        if (this.isBaby()) {
            MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), 0.5D);
        } else {
            MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.HoglinServantDamage.get());
        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IMMUNE_TO_ZOMBIFICATION, false);
        this.entityData.define(AUTO_MODE, false);
    }

    public void addAdditionalSaveData(CompoundTag p_33353_) {
        super.addAdditionalSaveData(p_33353_);
        p_33353_.putBoolean("IsImmuneToZombification", this.isImmuneToZombification());
        p_33353_.putBoolean("AutoMode", this.isAutonomous());
        p_33353_.putInt("TimeInOverworld", this.timeInOverworld);
    }

    public void readAdditionalSaveData(CompoundTag p_33344_) {
        super.readAdditionalSaveData(p_33344_);
        if (p_33344_.contains("IsImmuneToZombification")) {
            this.setImmuneToZombification(p_33344_.getBoolean("IsImmuneToZombification"));
        }
        if (p_33344_.contains("TimeInOverworld")) {
            this.timeInOverworld = p_33344_.getInt("TimeInOverworld");
        }
        if (p_33344_.contains("AutoMode")) {
            this.setAutonomous(p_33344_.getBoolean("AutoMode"));
        }
    }

    protected SoundEvent getAmbientSound() {
        return isRetreating(this) ? SoundEvents.HOGLIN_RETREAT : this.isAggressive() ? SoundEvents.HOGLIN_ANGRY : SoundEvents.HOGLIN_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_34548_) {
        return SoundEvents.HOGLIN_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.HOGLIN_DEATH;
    }

    protected void playStepSound(BlockPos p_34526_, BlockState p_34527_) {
        this.playSound(SoundEvents.HOGLIN_STEP, 0.15F, 1.0F);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34508_, DifficultyInstance p_34509_, MobSpawnType p_34510_, @javax.annotation.Nullable SpawnGroupData p_34511_, @javax.annotation.Nullable CompoundTag p_34512_) {
        if (p_34508_.getRandom().nextFloat() < 0.2F) {
            this.setBaby(true);
        }

        return super.finalizeSpawn(p_34508_, p_34509_, p_34510_, p_34511_, p_34512_);
    }

    @Override
    public boolean hurt(DamageSource p_27567_, float p_27568_) {
        boolean flag = super.hurt(p_27567_, p_27568_);
        if (this.isBaby()) {
            if (p_27567_.getEntity() instanceof LivingEntity attacker) {
                if (!MobUtil.areAllies(this, attacker)) {
                    this.toAvoid = attacker;
                    this.getNavigation().stop();
                    this.avoidTime = RETREAT_DURATION.sample(this.random);
                }
            }
        }
        return flag;
    }

    public boolean doHurtTarget(Entity p_34491_) {
        if (!(p_34491_ instanceof LivingEntity)) {
            return false;
        } else {
            this.attackAnimationRemainingTicks = 10;
            this.level.broadcastEntityEvent(this, (byte)4);
            this.playSound(SoundEvents.HOGLIN_ATTACK, 1.0F, this.getVoicePitch());
            return HoglinBase.hurtAndThrowTarget(this, (LivingEntity)p_34491_);
        }
    }

    protected void blockedByShield(LivingEntity p_34550_) {
        if (!this.isBaby()) {
            HoglinBase.throwTarget(this, p_34550_);
        }
    }

    protected void ageBoundaryReached() {
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack != null) {
            if (this.isBaby()) {
                attack.setBaseValue(0.5D);
            } else {
                attack.setBaseValue(AttributesConfig.HoglinServantDamage.get());
            }
        }

    }

    @Override
    public MobType getMobType() {
        return ModMobType.NATURAL;
    }

    public void setAutonomous(boolean autonomous) {
        this.entityData.set(AUTO_MODE, autonomous);
        if (autonomous) {
            this.playSound(SoundEvents.ARROW_HIT_PLAYER);
            if (!this.isWandering()) {
                this.setWandering(true);
                this.setStaying(false);
            }
        }
    }

    public boolean isAutonomous() {
        return this.entityData.get(AUTO_MODE);
    }

    public void setImmuneToZombification(boolean p_34565_) {
        this.getEntityData().set(DATA_IMMUNE_TO_ZOMBIFICATION, p_34565_);
    }

    private boolean isImmuneToZombification() {
        return this.getEntityData().get(DATA_IMMUNE_TO_ZOMBIFICATION);
    }

    public boolean isConverting() {
        return !this.level.dimensionType().piglinSafe()
                && !this.isImmuneToZombification()
                && !this.isNoAi()
                && !(this.getTrueOwner() != null && CuriosFinder.hasNetherRobe(this.getTrueOwner()));
    }

    private void finishConversion() {
        Zoglin zoglin = this.convertTo(EntityType.ZOGLIN, true);
        if (zoglin != null) {
            zoglin.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
            net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, zoglin);
        }

    }

    @Nullable
    @Override
    public AnimalSummon getBreedOffspring(ServerLevel p_146743_, AnimalSummon p_146744_) {
        HoglinServant hoglin = ModEntityType.HOGLIN_SERVANT.get().create(p_146743_);
        if (hoglin != null) {
            hoglin.setPersistenceRequired();
            hoglin.setImmuneToZombification(this.isImmuneToZombification());
        }

        return hoglin;
    }

    public boolean canFallInLove() {
        return !this.isAggressive() && super.canFallInLove();
    }

    public void handleEntityEvent(byte p_34496_) {
        if (p_34496_ == 4) {
            this.attackAnimationRemainingTicks = 10;
            this.playSound(SoundEvents.HOGLIN_ATTACK, 1.0F, this.getVoicePitch());
        } else {
            super.handleEntityEvent(p_34496_);
        }

    }

    public int getAttackAnimationRemainingTicks() {
        return this.attackAnimationRemainingTicks;
    }

    public double getPassengersRidingOffset() {
        return (double)this.getBbHeight() - (this.isBaby() ? 0.2D : 0.15D);
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        if (!this.isNoAi()) {
            Entity entity = this.getFirstPassenger();
            if (entity instanceof Mob mob){
                return mob;
            } else if (entity instanceof LivingEntity
                    && !this.isAutonomous()) {
                return (LivingEntity)entity;
            }
        }

        return null;
    }

    public boolean isControlledByLocalInstance() {
        return this.isEffectiveAi();
    }

    protected void doPlayerRide(Player player) {
        if (!this.level.isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
        }
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.isConverting()) {
            ++this.timeInOverworld;
            if (this.timeInOverworld > 300 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.ZOGLIN, (timer) -> this.timeInOverworld = timer)) {
                this.playSound(SoundEvents.HOGLIN_CONVERTED_TO_ZOMBIFIED);
                this.finishConversion();
            }
        } else {
            this.timeInOverworld = 0;
        }

    }

    public void aiStep() {
        if (this.attackAnimationRemainingTicks > 0) {
            --this.attackAnimationRemainingTicks;
        }
        if (this.level instanceof ServerLevel serverLevel){
            if (this.findNearestRepellent(serverLevel, this).isPresent()){
                if (this.nearestRepellent == null){
                    this.nearestRepellent = this.findNearestRepellent(serverLevel, this).get();
                    this.getNavigation().stop();
                    this.retreatTime = 200;
                }
            } else {
                this.nearestRepellent = null;
            }

            if (this.isBaby()){
                if (this.toAvoid != null && !this.toAvoid.isDeadOrDying()){
                    Vec3 vec31 = this.toAvoid.position();
                    if (this.position().closerThan(vec31, 8)) {
                        if (this.getNavigation().isDone()) {
                            for (int i = 0; i < 10; ++i) {
                                Vec3 vector3d2 = DefaultRandomPos.getPosAway(this, 16, 7, vec31);
                                if (vector3d2 != null) {
                                    Path path = this.getNavigation().createPath(vector3d2.x, vector3d2.y, vector3d2.z, 0);
                                    if (path != null && path.canReach()) {
                                        this.getNavigation().moveTo(path, 1.0F);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                this.toAvoid = null;
            }

            if (this.retreatTime > 0){
                --this.retreatTime;
                this.setTarget(null);
            }

            if (this.avoidTime > 0){
                --this.avoidTime;
                this.setTarget(null);
            } else {
                this.toAvoid = null;
            }

            if (this.nearestRepellent != null){
                Vec3 vec31 = this.nearestRepellent.getCenter();
                if (this.position().closerThan(vec31, 8)) {
                    if (this.getNavigation().isDone()) {
                        for (int i = 0; i < 10; ++i) {
                            Vec3 vector3d2 = LandRandomPos.getPosAway(this, 16, 7, vec31);
                            if (vector3d2 != null) {
                                Path path = this.getNavigation().createPath(vector3d2.x, vector3d2.y, vector3d2.z, 0);
                                if (path != null && path.canReach()) {
                                    this.getNavigation().moveTo(path, 1.0F);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        super.aiStep();
    }

    public void travel(@NotNull Vec3 pTravelVector) {
        if (this.isAlive()) {
            LivingEntity rider = this.getControllingPassenger();
            if (this.isVehicle() && rider instanceof Player player && !this.isAutonomous()) {
                this.setYRot(rider.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(rider.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;
                float speed = this.getRiddenSpeed(player);
                float f = rider.xxa * speed;
                float f1 = rider.zza * speed;
                if (f1 <= 0.0F) {
                    f1 *= 0.25F;
                }

                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                super.travel(new Vec3(f, pTravelVector.y, f1));
                this.lerpSteps = 0;

                this.calculateEntityAnimation(false);
            } else {
                super.travel(pTravelVector);
            }
        }
    }

    public boolean isFood(ItemStack p_27600_) {
        return p_27600_.is(Items.CRIMSON_FUNGUS);
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if ((itemstack.is(Items.CRIMSON_ROOTS) || itemstack.is(Items.WEEPING_VINES)) && this.getHealth() < this.getMaxHealth()) {
                this.heal(2.0F);
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                this.gameEvent(GameEvent.EAT, this);
                this.eat(this.level, itemstack);
                if (this.level instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                    }
                }
                pPlayer.swing(pHand);
                return InteractionResult.SUCCESS;
            } else if (!pPlayer.isCrouching()) {
                if (!this.isBaby()) {
                    if (this.getFirstPassenger() != null && this.getFirstPassenger() != pPlayer) {
                        this.getFirstPassenger().stopRiding();
                        return InteractionResult.SUCCESS;
                    } else if (!(pPlayer.getItemInHand(pHand).getItem() instanceof IWand)) {
                        this.doPlayerRide(pPlayer);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    private static boolean isRetreating(HoglinServant p_34633_) {
        return p_34633_.nearestRepellent != null || p_34633_.toAvoid != null;
    }

    public float getWalkTargetValue(BlockPos p_34516_, LevelReader p_34517_) {
        if (isPosNearNearestRepellent(this, p_34516_)) {
            return -1.0F;
        } else {
            return p_34517_.getBlockState(p_34516_.below()).is(Blocks.CRIMSON_NYLIUM) ? 10.0F : 0.0F;
        }
    }

    static boolean isPosNearNearestRepellent(HoglinServant p_34586_, BlockPos p_34587_) {
        return p_34586_.nearestRepellent != null && p_34586_.nearestRepellent.closerThan(p_34587_, 8.0D);
    }

    private Optional<BlockPos> findNearestRepellent(ServerLevel p_26665_, HoglinServant p_26666_) {
        return BlockPos.findClosestMatch(p_26666_.blockPosition(), 8, 4, (p_186148_) -> {
            return p_26665_.getBlockState(p_186148_).is(BlockTags.HOGLIN_REPELLENTS);
        });
    }

    class HoglinMeleeAttackGoal extends ModMeleeAttackGoal {
        public HoglinMeleeAttackGoal() {
            super(HoglinServant.this, 1.0D, true);
        }

        protected int getAttackInterval() {
            return this.adjustedTickDelay(40);
        }
    }
}
