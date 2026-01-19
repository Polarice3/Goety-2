package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;

import javax.annotation.Nullable;

public class Snapper extends AnimalSummon{

    public Snapper(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new SnapperMoveControl(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.4F, false));
        this.goalSelector.addGoal(3, new WaterWanderGoal<>(this));
        this.goalSelector.addGoal(4, new TryFindWaterGoal(this));
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(2, new FollowOwnerWaterGoal(this, 1.0D, 10.0F, 2.0F));
    }

    @Override
    public void targetSelectGoal() {
        super.targetSelectGoal();
        this.targetSelector.addGoal(5, new NaturalAttackGoal<>(this, AbstractFish.class, false));
        this.targetSelector.addGoal(6, new NaturalAttackGoal<>(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
        this.targetSelector.addGoal(7, new NaturalAttackGoal<>(this, AbstractSkeleton.class, false));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(ForgeMod.SWIM_SPEED.get(), 0.7D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.SnapperHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.SnapperArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SnapperDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.SnapperHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.SnapperArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.SnapperDamage.get());
    }

    public MobType getMobType() {
        return MobType.WATER;
    }

    public boolean canDrownInFluidType(FluidType type){
        return false;
    }

    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    public boolean checkSpawnObstruction(LevelReader p_32829_) {
        return p_32829_.isUnobstructed(this);
    }

    protected PathNavigation createNavigation(Level world) {
        return new WaterBoundPathNavigation(this, world);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (pReason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() != null){
            ServerParticleUtil.addParticlesAroundMiddleSelf(pLevel.getLevel(), ParticleTypes.LARGE_SMOKE, this);
            ColorUtil color = new ColorUtil(0);
            ServerParticleUtil.windParticle(pLevel.getLevel(), color, 1.0F, 0.0F, this.getId(), this.position());
        }
        return pSpawnData;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.SALMON_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.SALMON_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource p_29795_) {
        return SoundEvents.SALMON_HURT;
    }

    protected SoundEvent getFlopSound() {
        return SoundEvents.SALMON_FLOP;
    }

    protected void handleAirSupply(int p_30344_) {
        if (this.isAlive() && !this.isInWaterOrBubble()) {
            this.setAirSupply(p_30344_ - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(this.damageSources().drown(), 2.0F);
            }
        } else {
            this.setAirSupply(300);
        }

    }

    public void baseTick() {
        int i = this.getAirSupply();
        super.baseTick();
        this.handleAirSupply(i);
    }

    public void aiStep() {
        if (!this.isInWater() && this.onGround() && this.verticalCollision) {
            this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F, (double)0.4F, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F)));
            this.setOnGround(false);
            this.hasImpulse = true;
            this.playSound(this.getFlopSound(), this.getSoundVolume(), this.getVoicePitch());
        }

        super.aiStep();
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

    public boolean isFood(ItemStack p_30440_) {
        Item item = p_30440_.getItem();
        return item.isEdible() && p_30440_.getFoodProperties(this).isMeat();
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                FoodProperties foodProperties = itemstack.getFoodProperties(this);
                if (foodProperties != null){
                    this.heal((float)foodProperties.getNutrition());
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
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public boolean canFallInLove() {
        return false;
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        this.playSound(SoundEvents.FOX_BITE, 1.0F, 1.0F);
        return super.doHurtTarget(entityIn);
    }

    @Override
    public void setUpgraded(boolean upgraded) {
        super.setUpgraded(upgraded);
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (health != null && armor != null && attack != null) {
            if (upgraded) {
                health.setBaseValue(AttributesConfig.SnapperHealth.get() * 1.5D);
                armor.setBaseValue(AttributesConfig.SnapperArmor.get() + 1.0D);
                attack.setBaseValue(AttributesConfig.SnapperDamage.get() + 1.0D);
            } else {
                health.setBaseValue(AttributesConfig.SnapperHealth.get());
                armor.setBaseValue(AttributesConfig.SnapperArmor.get());
                attack.setBaseValue(AttributesConfig.SnapperDamage.get());
            }
        }
        this.setHealth(this.getMaxHealth());
    }

    public void travel(Vec3 vec3) {
        if (this.isControlledByLocalInstance()) {
            double d0 = this.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
            boolean flag = this.getDeltaMovement().y <= 0.0D;

            FluidState fluidstate = this.level().getFluidState(this.blockPosition());
            if ((this.isInWater() || (this.isInFluidType(fluidstate) && fluidstate.getFluidType() != ForgeMod.LAVA_TYPE.get())) && this.isAffectedByFluids() && !this.canStandOnFluid(fluidstate)) {
                if (this.isInWater() || (this.isInFluidType(fluidstate) && !this.moveInFluid(fluidstate, vec3, d0))) {
                    double d9 = this.getY();
                    float f4 = 0.96F;
                    float f5 = 0.02F;
                    float f6 = (float) EnchantmentHelper.getDepthStrider(this);
                    if (f6 > 3.0F) {
                        f6 = 3.0F;
                    }

                    if (!this.onGround()) {
                        f6 *= 0.5F;
                    }

                    if (f6 > 0.0F) {
                        f5 += (this.getSpeed() - f5) * f6 / 3.0F;
                    }

                    f5 *= (float)this.getAttributeValue(ForgeMod.SWIM_SPEED.get());
                    this.moveRelative(f5, vec3);
                    this.move(MoverType.SELF, this.getDeltaMovement());
                    Vec3 vec36 = this.getDeltaMovement();
                    if (this.horizontalCollision && this.onClimbable()) {
                        vec36 = new Vec3(vec36.x, 0.2D, vec36.z);
                    }

                    this.setDeltaMovement(vec36.multiply(f4, 0.8F, f4));
                    Vec3 vec32 = this.getFluidFallingAdjustedMovement(d0, flag, this.getDeltaMovement());
                    this.setDeltaMovement(vec32);
                    if (this.horizontalCollision && this.isFree(vec32.x, vec32.y + (double)0.6F - this.getY() + d9, vec32.z)) {
                        this.setDeltaMovement(vec32.x, 0.3F, vec32.z);
                    }
                } else {
                    super.travel(vec3);
                }
            } else {
                super.travel(vec3);
            }
        } else {
            super.travel(vec3);
        }

    }

    public static class SnapperMoveControl extends MoveControl {

        public SnapperMoveControl(Mob p_24983_) {
            super(p_24983_);
        }

        public void tick() {
            if (this.mob.isInWater()) {
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0, 0.005, 0.0));
            }

            if (this.operation == Operation.MOVE_TO && !this.mob.getNavigation().isDone()) {
                double dx = this.wantedX - this.mob.getX();
                double dy = this.wantedY - this.mob.getY();
                double dz = this.wantedZ - this.mob.getZ();
                float f = (float)(Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
                float f1 = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f, 10.0F));
                this.mob.yBodyRot = this.mob.getYRot();
                this.mob.yHeadRot = this.mob.getYRot();
                if (this.mob.isInWater()) {
                    this.mob.setSpeed((float)this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    float f2 = -((float)(Mth.atan2(dy, (float)Math.sqrt(dx * dx + dz * dz)) * (180.0D / Math.PI)));
                    f2 = Mth.clamp(Mth.wrapDegrees(f2), -85.0F, 85.0F);
                    this.mob.setXRot(this.rotlerp(this.mob.getXRot(), f2, 5.0F));
                    float f3 = Mth.cos(this.mob.getXRot() * ((float)Math.PI / 180F));
                    this.mob.setZza(f3 * f1);
                    this.mob.setYya((float)(f1 * dy));
                } else {
                    this.mob.setSpeed(f1 * 0.05F);
                }
            } else {
                this.mob.setSpeed(0.0F);
                this.mob.setYya(0.0F);
                this.mob.setZza(0.0F);
            }

        }
    }
}
