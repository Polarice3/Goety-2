package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CarrionFly extends Summoned {

    public CarrionFly(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setNoGravity(true);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.5D, false));
        this.goalSelector.addGoal(8, new WanderGoal<>(this, 1.0D, 10, 0.0F));
        this.goalSelector.addGoal(9, new FloatGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public void followGoal(){
        this.goalSelector.addGoal(8, new FollowOwnerGoal<>(this, 1.0D, 20.0F, 2.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.CarrionFlyHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.ARMOR, AttributesConfig.CarrionFlyArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.CarrionFlyDamage.get());
    }

    @Override
    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.CarrionFlyHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.CarrionFlyArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.CarrionFlyDamage.get());
    }

    public float getWalkTargetValue(@NotNull BlockPos p_27788_, LevelReader p_27789_) {
        return p_27789_.getBlockState(p_27788_).isAir() ? 10.0F : 0.0F;
    }

    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, pLevel) {
            public boolean isStableDestination(BlockPos blockPos) {
                return !this.level.getBlockState(blockPos.below()).isAir();
            }

            public void tick() {
                super.tick();
            }
        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    public @NotNull MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, @NotNull DamageSource damageSource) {
        return false;
    }

    protected SoundEvent getAmbientSound() {
        return null;
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource p_32615_) {
        return ModSounds.FLY_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.FLY_DEATH.get();
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.SPAWN_EGG){
            this.setHostile(true);
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public boolean isNoGravity() {
        return true;
    }

    private void jumpInLiquidInternal() {
        this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.01D, 0.0D));
    }

    @Override
    public void jumpInFluid(@NotNull FluidType type) {
        this.jumpInLiquidInternal();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.getDifficulty() == Difficulty.HARD){
            if (this.getTarget() != null && this.getTarget().isAlive()){
                this.noPhysics = true;
            }
        }
        if (!this.level.isClientSide) {
            if (this.getTrueOwner() != null) {
                if (this.getTrueOwner() instanceof Mob mobOwner) {
                    if (mobOwner.isRemoved() || mobOwner.isDeadOrDying()) {
                        this.lifeSpanDamage();
                    }
                }
            }
        }
    }

    @Override
    public void lifeSpanDamage() {
        if (!this.level.isClientSide){
            for(int i = 0; i < this.level.random.nextInt(10) + 10; ++i) {
                ServerParticleUtil.smokeParticles(ParticleTypes.SMOKE, this.getX(), this.getEyeY(), this.getZ(), this.level);
            }
        }
        this.discard();
    }

    public void setYBodyRot(float p_32621_) {
        this.setYRot(p_32621_);
        super.setYBodyRot(p_32621_);
    }

    @Override
    public void setUpgraded(boolean upgraded) {
        super.setUpgraded(upgraded);
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (health != null && attack != null) {
            if (upgraded) {
                health.setBaseValue(AttributesConfig.CarrionFlyHealth.get() * 1.33D);
                attack.setBaseValue(AttributesConfig.CarrionFlyDamage.get() * 1.1D);
            } else {
                health.setBaseValue(AttributesConfig.CarrionFlyHealth.get());
                attack.setBaseValue(AttributesConfig.CarrionFlyDamage.get());
            }
        }
        this.setHealth(this.getMaxHealth());
    }

    @Override
    public void uncreditedKill(LivingEntity target) {
        if (!MobUtil.areAllies(this, target)) {
            int total = this.level.getEntitiesOfClass(CarrionMaggot.class, this.getBoundingBox().inflate(16.0F), maggot -> MobUtil.areAllies(this, maggot)).size();
            if (total < SpellConfig.CarrionLimit.get()) {
                int random = this.getRandom().nextIntBetweenInclusive(1, 3);
                if (target.getMaxHealth() < 20.0F) {
                    random = 1;
                }
                for (int i = 0; i < random; ++i) {
                    CarrionMaggot carrionMaggot = new CarrionMaggot(ModEntityType.CARRION_MAGGOT.get(), this.level);
                    carrionMaggot.setTrueOwner(this.getTrueOwner() != null ? this.getTrueOwner() : this);
                    BlockPos blockPos = BlockFinder.SummonRadius(target.blockPosition(), carrionMaggot, this.level, 3);
                    carrionMaggot.moveTo(blockPos.getCenter());
                    if (this.level instanceof ServerLevel serverLevel) {
                        carrionMaggot.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(carrionMaggot.blockPosition()), MobSpawnType.BREEDING, null, null);
                    }
                    carrionMaggot.setNatural(this.isNatural());
                    carrionMaggot.setHostile(this.isHostile());
                    carrionMaggot.setUpgraded(this.isUpgraded());
                    EffectsUtil.copyEffects(this, carrionMaggot);
                    if (this.isLimitedLife()) {
                        carrionMaggot.setLimitedLife(this.getLifespan());
                    }
                    this.level.addFreshEntity(carrionMaggot);
                }
            }
        }
    }

    @Override
    public boolean doHurtTarget(float amount, Entity target) {
        if (target instanceof LivingEntity livingTarget) {
            if (livingTarget.getMobType() == MobType.UNDEAD) {
                amount *= 2;
            }
        }
        return super.doHurtTarget(amount, target);
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
}
