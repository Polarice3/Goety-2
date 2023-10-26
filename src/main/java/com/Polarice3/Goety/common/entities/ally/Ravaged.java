package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public class Ravaged extends Summoned {
    private static final UUID SPEED_BOOST_UUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    private static final AttributeModifier SPEED_BOOST = new AttributeModifier(SPEED_BOOST_UUID, "Aggressive speed boost", 0.5D, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(Ravaged.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_CONVERSION_ID = SynchedEntityData.defineId(Ravaged.class, EntityDataSerializers.BOOLEAN);
    private int bitingTick;
    private int excessFood = 0;
    private int conversionTime;

    public Ravaged(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.applyEntityAI();
    }

    protected void applyEntityAI() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.6F));
        this.goalSelector.addGoal(3, new FeedGoal());
        this.targetSelector.addGoal(4, new VillagerAttackGoal<>(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, (double)0.23F)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0D)
                .add(Attributes.ARMOR, 2.0D);
    }

    public AttributeSupplier.Builder getConfiguredAttributes(){
        return setCustomAttributes();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_SIZE, 0);
        this.getEntityData().define(DATA_CONVERSION_ID, false);
    }

    public void setRavagedSize(int p_33109_) {
        this.entityData.set(ID_SIZE, Mth.clamp(p_33109_, 0, 64));
        this.excessFood = 0;
    }

    private void updateSizeInfo() {
        this.refreshDimensions();
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(3.0D + this.getRavagedSize());
    }

    public int getRavagedSize() {
        return this.entityData.get(ID_SIZE);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33134_) {
        if (ID_SIZE.equals(p_33134_)) {
            this.updateSizeInfo();
        }

        super.onSyncedDataUpdated(p_33134_);
    }

    public void addAdditionalSaveData(CompoundTag p_33353_) {
        super.addAdditionalSaveData(p_33353_);
        p_33353_.putInt("BitingTick", this.bitingTick);
        p_33353_.putInt("ExcessFood", this.excessFood);
        p_33353_.putInt("ConversionTime", this.isConverting() ? this.conversionTime : -1);
        p_33353_.putInt("Size", this.getRavagedSize());
    }

    public void readAdditionalSaveData(CompoundTag p_33344_) {
        super.readAdditionalSaveData(p_33344_);
        this.bitingTick = p_33344_.getInt("BitingTick");
        this.excessFood = p_33344_.getInt("ExcessFood");
        if (p_33344_.contains("ConversionTime", 99) && p_33344_.getInt("ConversionTime") > -1) {
            this.startConversion(p_33344_.getInt("StrayConversionTime"));
        }
        this.setRavagedSize(p_33344_.getInt("Size"));
    }

    public boolean isConverting() {
        return this.getEntityData().get(DATA_CONVERSION_ID);
    }

    public SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
    }

    public SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ZOMBIE_VILLAGER_HURT;
    }

    public SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_VILLAGER_DEATH;
    }

    public SoundEvent getStepSound() {
        if (this.getRavagedSize() < 4) {
            return SoundEvents.ZOMBIE_VILLAGER_STEP;
        } else {
            return SoundEvents.POLAR_BEAR_STEP;
        }
    }

    protected void playStepSound(BlockPos p_34316_, BlockState p_34317_) {
        this.playSound(this.getStepSound(), 0.15F + (this.getRavagedSize() / 5.0F), this.getVoicePitch());
    }

    @Override
    protected float getSoundVolume() {
        return Math.min(10, super.getSoundVolume() + (this.getRavagedSize() / 2.0F));
    }

    @Override
    public float getVoicePitch() {
        return Math.max(0.2F, super.getVoicePitch() - (this.getRavagedSize() / 10.0F));
    }

    public int getMaxHeadYRot() {
        return 45;
    }

    public void aiStep() {
        super.aiStep();
        if (this.isAlive()) {
            AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null) {
                if (this.getTarget() != null) {
                    if (!speed.hasModifier(SPEED_BOOST)) {
                        speed.addPermanentModifier(SPEED_BOOST);
                    }
                } else {
                    if (speed.hasModifier(SPEED_BOOST)) {
                        speed.removeModifier(SPEED_BOOST);
                    }
                }
            }
            if (this.bitingTick > 0) {
                --this.bitingTick;
            }

            if (!this.level.isClientSide && this.isAlive() && !this.isNoAi()){
                if (this.isConverting()) {
                    --this.conversionTime;
                    if (this.conversionTime < 0) {
                        this.doRavagerConversion();
                    }
                } else if (this.getAttributeValue(Attributes.MAX_HEALTH) >= 75.0D) {
                    this.startConversion(300);
                }
            }

            if (this.level.isClientSide){
                if (this.getRavagedSize() >= 4) {
                    if (this.getDeltaMovement().horizontalDistanceSqr() > (double) 2.5000003E-7F && this.random.nextInt(5) == 0) {
                        int i = Mth.floor(this.getX());
                        int j = Mth.floor(this.getY() - (double) 0.2F);
                        int k = Mth.floor(this.getZ());
                        BlockPos pos = new BlockPos(i, j, k);
                        BlockState blockstate = this.level.getBlockState(pos);
                        if (!blockstate.isAir()) {
                            this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(pos), this.getX() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getBbWidth(), this.getY() + 0.1D, this.getZ() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getBbWidth(), 4.0D * ((double) this.random.nextFloat() - 0.5D), 0.5D, ((double) this.random.nextFloat() - 0.5D) * 4.0D);
                        }
                    }
                }
            }
        }
    }

    private void startConversion(int p_149831_) {
        this.conversionTime = p_149831_;
        this.entityData.set(DATA_CONVERSION_ID, true);
    }

    public boolean isShaking() {
        return this.isConverting();
    }

    protected void doRavagerConversion() {
        if (this.getTrueOwner() instanceof Player player) {
            MobUtil.convertTo(this, ModEntityType.MOD_RAVAGER.get(), false, player);
        } else {
            this.convertTo(ModEntityType.MOD_RAVAGER.get(), false);
        }
        if (!this.isSilent()) {
            this.level.levelEvent((Player)null, 1027, this.blockPosition(), 0);
        }

    }

    public int getBitingTick() {
        return this.bitingTick;
    }

    public int attackTotalTick(){
        return 10;
    }

    public boolean doHurtTarget(Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            this.bitingTick = attackTotalTick();
            if (entityIn instanceof AbstractVillager){
                this.level.broadcastEntityEvent(this, (byte)11);
                this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            }
            this.level.broadcastEntityEvent(this, (byte)4);
            this.playSound(SoundEvents.PHANTOM_BITE, this.getSoundVolume(), this.getVoicePitch());
            float f = this.level.getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
            if (this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
                entityIn.setSecondsOnFire(2 * (int)f);
            }
        }

        return flag;
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        return spawnDataIn;
    }

    public void handleEntityEvent(byte p_33335_) {
        if (p_33335_ == 4) {
            this.bitingTick = attackTotalTick();
            this.playSound(SoundEvents.PHANTOM_BITE, this.getSoundVolume(), this.getVoicePitch() * 2.0F);
        } else if (p_33335_ == 10) {
            this.bitingTick = attackTotalTick();
            this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
        } else if (p_33335_ == 11) {
            this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
        }

        super.handleEntityEvent(p_33335_);
    }

    public boolean killedEntity(ServerLevel p_219160_, LivingEntity p_219161_) {
        boolean flag = super.killedEntity(p_219160_, p_219161_);
        if (p_219161_ instanceof AbstractVillager){
            this.cannibalize(this.random.nextInt(5) + 1);
        }
        if (p_219161_ instanceof Raider){
            this.cannibalize(this.random.nextInt(2) + 1);
        }
        return flag;
    }

    public boolean hurt(DamageSource p_34288_, float p_34289_) {
        Entity entity = p_34288_.getEntity();
        if (entity instanceof LivingEntity living){
            if (this.canAttack(living, TargetingConditions.DEFAULT)) {
                this.setTarget(living);
            }
        }
        return super.hurt(p_34288_, p_34289_);
    }

    public EntityDimensions getDimensions(Pose p_33113_) {
        int i = this.getRavagedSize();
        EntityDimensions entitydimensions = super.getDimensions(p_33113_);
        float f = (entitydimensions.width + 0.2F * (float)i) / entitydimensions.width;
        return entitydimensions.scale(f);
    }

    public void cannibalize(int amount){
        this.ate();
        float heal = amount * (10.0F / this.getRavagedSize() + 1);
        float excess = Mth.clamp((this.getHealth() + heal) - this.getMaxHealth(), 0.0F, 12.0F);
        if ((this.getHealth() + heal) > (this.getMaxHealth() * 1.25)
                || (excessFood > this.getMaxHealth() && (this.getHealth() + heal) > this.getMaxHealth())){
            AttributeInstance maxHealth = this.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealth != null) {
                if (maxHealth.getValue() < 75.0D) {
                    this.setRavagedSize(this.getRavagedSize() + 1);
                    maxHealth.setBaseValue(Math.min((this.getAttributeValue(Attributes.MAX_HEALTH) + excess), 75.0D));
                }
            }
        } else {
            this.excessFood += excess;
        }
        this.heal(heal);
        this.playSound(SoundEvents.PLAYER_BURP, this.getSoundVolume(), this.getVoicePitch());
    }

    public class FeedGoal extends Goal {
        public ItemEntity food;
        public int feedingTime;

        public FeedGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            for (ItemEntity itemEntity : Ravaged.this.level.getEntitiesOfClass(ItemEntity.class, Ravaged.this.getBoundingBox().inflate(32))){
                if (itemEntity.getItem().is(Items.ROTTEN_FLESH)){
                    this.food = itemEntity;
                }
            }
            return this.food != null && !this.food.isRemoved() && Ravaged.this.hurtTime <= 0;
        }

        public boolean canContinueToUse() {
            return this.food != null
                    && Ravaged.this.hurtTime <= 0
                    && this.feedingTime > 0
                    && !this.food.isRemoved();
        }

        private boolean hasEatenLongEnough() {
            return this.feedingTime <= 0;
        }

        public void start() {
            Ravaged.this.setTarget(null);
            this.feedingTime = 40;
        }

        public void stop() {
            this.feedingTime = 0;
            Ravaged.this.navigation.stop();
        }

        public void tick() {
            if (this.food != null && !this.food.isRemoved()) {
                if (Ravaged.this.getBoundingBox().inflate(0.85D).intersects(this.food.getBoundingBox())) {
                    Ravaged.this.lookControl.setLookAt(this.food);
                    --this.feedingTime;
                    if (this.feedingTime % 5 == 0){
                        Ravaged.this.playSound(SoundEvents.GENERIC_EAT, Ravaged.this.getSoundVolume(), Ravaged.this.getVoicePitch());
                        Ravaged.this.level.broadcastEntityEvent(Ravaged.this, (byte) 10);
                    }
                    if (Ravaged.this.level instanceof ServerLevel serverLevel) {
                        for(int i = 0; i < 5; ++i) {
                            Vec3 vec3 = new Vec3(((double) Ravaged.this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
                            vec3 = vec3.xRot(-Ravaged.this.getXRot() * ((float)Math.PI / 180F));
                            vec3 = vec3.yRot(-Ravaged.this.getYRot() * ((float)Math.PI / 180F));
                            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, this.food.getItem()), this.food.getX(), this.food.getY() + 0.25D, this.food.getZ(), 1, vec3.x, vec3.y + 0.05D, vec3.z, 0);
                        }
                    }
                } else {
                    PathNavigation pathnavigation = Ravaged.this.getNavigation();
                    Path path = pathnavigation.createPath(BlockPos.containing(this.food.position()), 0, 8);
                    Ravaged.this.getNavigation().moveTo(path, 1.5F);
                }
            }
            if (this.hasEatenLongEnough() && this.food != null) {
                Ravaged.this.cannibalize(this.food.getItem().getCount());
                this.food.discard();
            }
        }

    }

    public static class VillagerAttackGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<AbstractVillager> {
        protected Ravaged ravaged;

        public VillagerAttackGoal(Ravaged ravaged) {
            super(ravaged, AbstractVillager.class, true);
            this.ravaged = ravaged;
        }

        public boolean canUse() {
            return super.canUse() && this.ravaged.isNatural() && (this.ravaged.getTrueOwner() == null || this.ravaged.getTrueOwner() instanceof AbstractIllager) && this.target != null && !this.target.isBaby();
        }
    }
}
