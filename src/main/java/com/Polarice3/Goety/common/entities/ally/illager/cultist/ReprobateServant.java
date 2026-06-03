package com.Polarice3.Goety.common.entities.ally.illager.cultist;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.ai.WitchServantBarterGoal;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.ReprobateCarry;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class ReprobateServant extends CultistServant {
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(ReprobateServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> BARREL = SynchedEntityData.defineId(ReprobateServant.class, EntityDataSerializers.BOOLEAN);
    public static String IDLE = "idle";
    public static String THROW = "throw";
    public static String INCITE = "incite";
    public static String INSPECT = "inspect";
    private int fleeTime;
    private int inciteCool;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState throwAnimationState = new AnimationState();
    public AnimationState inciteAnimationState = new AnimationState();

    public ReprobateServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new WitchServantBarterGoal(this){
            @Override
            public void start() {
                super.start();
                ReprobateServant.this.setAnimationState(INSPECT);
            }

            @Override
            public void stop() {
                super.stop();
                ReprobateServant.this.setAnimationState(IDLE);
            }
        });
        this.goalSelector.addGoal(1, new InciteGoal(this));
        this.goalSelector.addGoal(1, new AvoidTargetGoal<>(this, LivingEntity.class, 12, 1.2D, 1.2D){
            @Override
            public boolean canUse() {
                return ReprobateServant.this.fleeTime > 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(2, new ThrowGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ReprobateHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.ReprobateArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.ReprobateHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.ReprobateArmor.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, 0);
        this.entityData.define(BARREL, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("CarryBarrel")){
            this.setBarrel(compound.getBoolean("CarryBarrel"));
        }
        if (compound.contains("InciteCool")){
            this.inciteCool = compound.getInt("InciteCool");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("CarryBarrel", this.isBarrel());
        compound.putInt("InciteCool", this.inciteCool);
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, IDLE)){
            return 0;
        } else if (Objects.equals(animation, THROW)){
            return 1;
        } else if (Objects.equals(animation, INCITE)){
            return 2;
        } else if (Objects.equals(animation, INSPECT)){
            return 3;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.throwAnimationState);
        animationStates.add(this.inciteAnimationState);
        return animationStates;
    }

    public void stopMostAnimation(AnimationState exception){
        for (AnimationState state : this.getAllAnimations()){
            if (state != exception){
                state.stop();
            }
        }
    }

    public int getCurrentAnimation(){
        return this.entityData.get(ANIM_STATE);
    }

    public boolean isCurrentAnimation(String animation) {
        return this.getCurrentAnimation() == this.getAnimationState(animation);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor)) {
            if (this.level.isClientSide){
                switch (this.entityData.get(ANIM_STATE)){
                    case 0:
                    case 3:
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 1:
                        this.throwAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.throwAnimationState);
                        break;
                    case 2:
                        this.inciteAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.inciteAnimationState);
                        break;
                }
            }
        }
    }

    @Override
    public void setItemSlot(EquipmentSlot p_21416_, ItemStack p_21417_) {
        if (p_21416_ == EquipmentSlot.OFFHAND) {
            this.spawnAtLocation(p_21417_);
        } else {
            super.setItemSlot(p_21416_, p_21417_);
        }
    }

    @Override
    public boolean hasItemInSlot(EquipmentSlot p_21034_) {
        if (p_21034_ == EquipmentSlot.OFFHAND) {
            return true;
        }
        return super.hasItemInSlot(p_21034_);
    }

    @Override
    public boolean isLeftHanded() {
        return true;
    }

    public boolean isBarrel() {
        return this.entityData.get(BARREL);
    }

    public void setBarrel(boolean barrel) {
        this.entityData.set(BARREL, barrel);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITCH_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return SoundEvents.WITCH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITCH_DEATH;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.WITCH_CELEBRATE;
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        if (this.isNatural()){
            return ModEntityType.REPROBATE.get().getDefaultLootTable();
        } else {
            return super.getDefaultLootTable();
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_37856_, DifficultyInstance p_37857_, MobSpawnType p_37858_, @Nullable SpawnGroupData p_37859_, @Nullable CompoundTag p_37860_) {
        SpawnGroupData data = super.finalizeSpawn(p_37856_, p_37857_, p_37858_, p_37859_, p_37860_);
        this.setBarrel(p_37856_.getRandom().nextBoolean());
        return data;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            this.idleAnimationState.animateWhen((this.isCurrentAnimation(IDLE) || this.isCurrentAnimation(INSPECT)) && !this.walkAnimation.isMoving(), this.tickCount);
        } else {
            if (this.fleeTime > 0) {
                --this.fleeTime;
            }
            double d0 = this.getMoveControl().getSpeedModifier();
            if (d0 >= 1.2D) {
                this.setSprinting(true);
                this.level.broadcastEntityEvent(this, (byte) 4);
            } else {
                this.setSprinting(false);
                this.level.broadcastEntityEvent(this, (byte) 5);
            }
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

    public void throwCase(@Nullable LivingEntity target) {
        if (!this.isSilent()) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
        }
        ReprobateCarry reprobateCarry = new ReprobateCarry(this, this.level);
        reprobateCarry.setBarrel(this.isBarrel());
        reprobateCarry.setYRot(this.getYRot());
        if (target != null) {
            Vec3 vec3 = target.getDeltaMovement();
            double d0 = target.getX() + vec3.x - this.getX();
            double d1 = target.getEyeY() - (double)1.1F - this.getY();
            double d2 = target.getZ() + vec3.z - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            reprobateCarry.setXRot(reprobateCarry.getXRot() - 20.0F);
            reprobateCarry.shoot(d0, d1 + d3 * 0.2D, d2, 0.75F, 8.0F);
        } else {
            reprobateCarry.shootFromRotation(this, this.getXRot(), this.getYRot(), -20.0F, 0.75F, 8.0F);
        }
        this.level.addFreshEntity(reprobateCarry);
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4) {
            this.setSprinting(true);
        } else if (p_21375_ == 5) {
            this.setSprinting(false);
        } else {
            super.handleEntityEvent(p_21375_);
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
        /*if (isOwner) {
            return ServantUtil.equipServantArmor(pPlayer, this, itemstack, super.mobInteract(pPlayer, pHand));
        }*/
        return super.mobInteract(pPlayer, pHand);
    }

    public static class ThrowGoal extends Goal {
        public ReprobateServant reprobate;
        public int throwTick;

        public ThrowGoal(ReprobateServant reprobate) {
            this.reprobate = reprobate;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.reprobate.fleeTime > 0) {
                return false;
            }
            LivingEntity target = this.reprobate.getTarget();
            if (target != null && target.isAlive()) {
                if (target.distanceTo(this.reprobate) <= 12.0D && this.reprobate.hasLineOfSight(target)) {
                    this.reprobate.getNavigation().stop();
                    this.reprobate.getMoveControl().strafe(0.0F, 0.0F);
                    return true;
                } else {
                    this.reprobate.getNavigation().moveTo(target, 1.2D);
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.throwTick < 30;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public void start() {
            super.start();
            this.throwTick = 0;
            this.reprobate.setAnimationState(THROW);
        }

        @Override
        public void stop() {
            super.stop();
            this.reprobate.fleeTime = 20;
            this.reprobate.setAnimationState(IDLE);
        }

        public LivingEntity getTarget() {
            return this.reprobate.getTarget();
        }

        @Override
        public void tick() {
            super.tick();
            ++this.throwTick;
            this.reprobate.getNavigation().stop();
            this.reprobate.getMoveControl().strafe(0.0F, 0.0F);
            if (this.getTarget() != null) {
                this.reprobate.getLookControl().setLookAt(this.getTarget(), 100.0F, 100.0F);
                this.reprobate.lookAt(this.getTarget(), 100.0F, 100.0F);
            }
            if (this.throwTick == 12) {
                this.reprobate.throwCase(this.getTarget() == null ? null : this.getTarget());
            }
        }
    }

    public static class InciteGoal extends Goal {
        public ReprobateServant reprobate;
        public int inciteTick;

        public InciteGoal(ReprobateServant reprobate) {
            this.reprobate = reprobate;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.reprobate.inciteCool <= 0) {
                if (this.reprobate.getTarget() != null) {
                    return !this.getList().isEmpty() && this.getList().size() > 3;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.inciteTick < 20;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        public List<RaiderServant> getList() {
            List<RaiderServant> list = this.reprobate.level.getEntitiesOfClass(RaiderServant.class, this.reprobate.getBoundingBox().inflate(8.0D));
            list.removeIf(raider -> raider.getType() == this.reprobate.getType());
            list.removeIf(raider -> !raider.hasLineOfSight(this.reprobate) && MobUtil.areAllies(raider, this.reprobate.getTarget()));
            return list;
        }

        @Override
        public void start() {
            super.start();
            this.inciteTick = 0;
            this.reprobate.getNavigation().stop();
            this.reprobate.getMoveControl().strafe(0.0F, 0.0F);
            this.reprobate.setAnimationState(INCITE);
        }

        @Override
        public void stop() {
            super.stop();
            this.reprobate.inciteCool = MathHelper.secondsToTicks(20);
            this.reprobate.setAnimationState(IDLE);
        }

        @Override
        public void tick() {
            super.tick();
            ++this.inciteTick;
            this.reprobate.getNavigation().stop();
            this.reprobate.getMoveControl().strafe(0.0F, 0.0F);
            if (this.inciteTick == 10) {
                if (!this.getList().isEmpty()) {
                    for (RaiderServant raider : this.getList()) {
                        int i = this.reprobate.getRandom().nextInt(3);
                        MobEffectInstance instance = getInstance(i);
                        raider.addEffect(instance);
                        raider.playSound(SoundEvents.GENERIC_DRINK, 1.0F, 1.0F);
                    }
                }
            }
        }

        @NotNull
        private static MobEffectInstance getInstance(int i) {
            MobEffectInstance instance = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, MathHelper.secondsToTicks(10));
            if (i == 0) {
                instance = new MobEffectInstance(MobEffects.ABSORPTION, MathHelper.secondsToTicks(10));
            } else if (i == 1) {
                instance = new MobEffectInstance(MobEffects.REGENERATION, MathHelper.secondsToTicks(10));
            }
            return instance;
        }
    }
}
