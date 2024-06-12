package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public abstract class AbstractHauntedArmor extends Summoned {
    private static final UUID SPEED_MODIFIER_HOSTILE_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier SPEED_MODIFIER_HOSTILE = new AttributeModifier(SPEED_MODIFIER_HOSTILE_UUID, "Aggression Speed", 0.5D, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final EntityDataAccessor<Byte> FLAGS = SynchedEntityData.defineId(AbstractHauntedArmor.class, EntityDataSerializers.BYTE);
    private int blockTime;
    private int coolTime;
    private int breakShield;

    public AbstractHauntedArmor(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new GuardingGoal(this, 0.75D, 20));
        this.goalSelector.addGoal(2, new AttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(8, new WanderGoal(this, 1.0D, 10));
    }

    @Override
    public float getStepHeight() {
        return 1.0F;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.HauntedArmorHealth.get())
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.HauntedArmorHealth.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLAGS, (byte)0);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("BlockTime")) {
            this.blockTime = compound.getInt("BlockTime");
        }
        if (compound.contains("CoolTime")) {
            this.coolTime = compound.getInt("CoolTime");
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("BlockTime", this.blockTime);
        compound.putInt("CoolTime", this.coolTime);
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.populateDefaultEquipmentSlots(worldIn.getRandom(), worldIn.getCurrentDifficultyAt(this.blockPosition()));
        this.populateDefaultEquipmentEnchantments(worldIn.getRandom(), difficultyIn);
        return spawnDataIn;
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.HEAD, ModItems.CURSED_KNIGHT_HELMET.get().getDefaultInstance());
        this.setItemSlot(EquipmentSlot.CHEST, ModItems.CURSED_KNIGHT_CHESTPLATE.get().getDefaultInstance());
        this.setItemSlot(EquipmentSlot.LEGS, ModItems.CURSED_KNIGHT_LEGGINGS.get().getDefaultInstance());
        this.setItemSlot(EquipmentSlot.FEET, ModItems.CURSED_KNIGHT_BOOTS.get().getDefaultInstance());
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()){
            if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR){
                this.setDropChance(equipmentSlot, 0.05F);
            }
        }
        if (difficulty.getDifficulty() == Difficulty.EASY){
            this.setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_SWORD.getDefaultInstance());
        } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, ModItems.FELL_BLADE.get().getDefaultInstance());
            this.setDropChance(EquipmentSlot.MAINHAND, 0.025F);
        }
        this.setItemSlot(EquipmentSlot.OFFHAND, Items.SHIELD.getDefaultInstance());
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    private boolean getFlags(int mask) {
        int i = this.entityData.get(FLAGS);
        return (i & mask) != 0;
    }

    private void setFlags(int mask, boolean value) {
        int i = this.entityData.get(FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(FLAGS, (byte)(i & 255));
    }

    public void setGuarding(boolean guarding){
        this.setFlags(1, guarding);
    }

    public boolean isGuarding() {
        return this.getFlags(1);
    }

    public HauntedArmPose getArmPose() {
        if (this.isGuarding()){
            return HauntedArmPose.GUARD;
        } else if (this.isAggressive()){
            return HauntedArmPose.ATTACK;
        } else {
            return HauntedArmPose.IDLE;
        }
    }

    @Override
    public int xpReward() {
        return 10;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.HAUNTED_ARMOR_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        if (!this.isDamageSourceBlocked(p_21239_)) {
            if (this.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem armorItem){
                if (armorItem.getMaterial().getEquipSound() == SoundEvents.ARMOR_EQUIP_LEATHER
                || armorItem.getMaterial().getEquipSound() == SoundEvents.ARMOR_EQUIP_ELYTRA
                || armorItem.getMaterial().getEquipSound() == SoundEvents.ARMOR_EQUIP_TURTLE
                || armorItem.getMaterial().getEquipSound() == SoundEvents.ARMOR_EQUIP_GENERIC){
                    return SoundEvents.GENERIC_HURT;
                }
            } else {
                return SoundEvents.GENERIC_HURT;
            }
            return ModSounds.HAUNTED_ARMOR_HURT.get();
        } else {
            return SoundEvents.SHIELD_BLOCK;
        }
    }

    protected void playHurtSound(DamageSource p_21160_) {
        if (this.breakShield <= 0){
            super.playHurtSound(p_21160_);
        }
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.HAUNTED_ARMOR_DEATH.get();
    }

    @Nullable
    protected SoundEvent getStepSound() {
        if (this.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof ArmorItem armorItem){
            if (armorItem.getMaterial().getEquipSound() == SoundEvents.ARMOR_EQUIP_LEATHER
                    || armorItem.getMaterial().getEquipSound() == SoundEvents.ARMOR_EQUIP_ELYTRA
                    || armorItem.getMaterial().getEquipSound() == SoundEvents.ARMOR_EQUIP_TURTLE
                    || armorItem.getMaterial().getEquipSound() == SoundEvents.ARMOR_EQUIP_GENERIC){
                return null;
            }
        }
        return ModSounds.HAUNTED_ARMOR_STEP.get();
    }

    protected void playStepSound(BlockPos p_34316_, BlockState p_34317_) {
        if (this.getStepSound() != null) {
            this.playSound(this.getStepSound(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
        }
    }

    @Override
    public boolean canBeAffected(MobEffectInstance pPotioneffect) {
        return pPotioneffect.getEffect().getAttributeModifiers().containsKey(Attributes.ARMOR);
    }

    @Override
    public void die(DamageSource pCause) {
        super.die(pCause);
        if (this.level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 20; ++i) {
                ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.SMOKE, this);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide){
            AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attributeinstance != null) {
                if ((this.getTarget() != null || this.isAggressive())
                        && !attributeinstance.hasModifier(SPEED_MODIFIER_HOSTILE)) {
                    attributeinstance.addTransientModifier(SPEED_MODIFIER_HOSTILE);
                } else if (attributeinstance.hasModifier(SPEED_MODIFIER_HOSTILE)){
                    attributeinstance.removeModifier(SPEED_MODIFIER_HOSTILE);
                }
            }
        }
        if (this.isGuarding()){
            ++this.blockTime;
            int total = MathHelper.secondsToTicks(3 + this.level.random.nextInt(3));
            if (this.blockTime > total){
                this.coolTime = total * 2;
                this.setGuarding(false);
            }
        } else {
            this.blockTime = 0;
            if (this.coolTime > 0){
                --this.coolTime;
            }
        }

        if (!this.isInLava() && !this.isInFluidType(ForgeMod.LAVA_TYPE.get())){
            this.clearFire();
        }

        if (this.breakShield > 0){
            --this.breakShield;
        }

        if (this.isEffectiveAi()) {
            if (!this.hasItemInSlot(EquipmentSlot.HEAD) && !this.hasItemInSlot(EquipmentSlot.CHEST) && !this.hasItemInSlot(EquipmentSlot.LEGS) && !this.hasItemInSlot(EquipmentSlot.FEET)) {
                this.kill();
            }
        }

        if (this.level.isClientSide) {
            for(int i = 0; i < 2; ++i) {
                this.level.addParticle(ParticleTypes.SMOKE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean flag = false;
        if (amount > 0.0F && this.isDamageSourceBlocked(source)) {
            net.minecraftforge.event.entity.living.ShieldBlockEvent ev = net.minecraftforge.common.ForgeHooks.onShieldBlock(this, source, amount);
            if(!ev.isCanceled()) {
                if(ev.shieldTakesDamage()) {
                    this.hurtCurrentlyUsedShield(amount);
                }
                amount -= ev.getBlockedDamage();
                if (!source.is(DamageTypeTags.IS_PROJECTILE)) {
                    Entity entity = source.getDirectEntity();
                    if (entity instanceof LivingEntity) {
                        this.blockUsingShield((LivingEntity)entity);
                    }
                }

                flag = true;
            }
        }
        if (source.is(DamageTypeTags.IS_FIRE) && !source.is(DamageTypes.LAVA)){
            return false;
        }
        if (flag) {
            this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.8F + this.level.random.nextFloat() * 0.4F);
            this.level.broadcastEntityEvent(this, (byte)29);
            if (amount <= 1.0F){
                return false;
            }
        }
        if (!this.isGuarding() && this.coolTime > 0 && !this.canDisableShield(source) && !source.is(DamageTypeTags.BYPASSES_ARMOR)){
            this.coolTime -= (int) (amount * 10);
        }
        if (this.level instanceof ServerLevel serverLevel && !this.isDamageSourceBlocked(source)){
            if (!this.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
                int j = (int) Math.min(amount, 10);
                for (int i = 0; i < j; ++i) {
                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, new ItemParticleOption(ParticleTypes.ITEM, this.getItemBySlot(EquipmentSlot.CHEST)), this);
                }
            }
        }
        return super.hurt(source, amount);
    }

    public boolean canDisableShield(DamageSource damageSource){
        if (damageSource.getDirectEntity() instanceof LivingEntity livingEntity){
            return livingEntity.getMainHandItem().canDisableShield(this.useItem, this, livingEntity);
        }
        return false;
    }

    protected void blockUsingShield(LivingEntity p_36295_) {
        super.blockUsingShield(p_36295_);
        if (p_36295_.getMainHandItem().canDisableShield(this.useItem, this, p_36295_)) {
            this.disableShield(true);
        }

    }

    public void disableShield(boolean p_36385_) {
        float f = 0.25F + (float) EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
        if (p_36385_) {
            f += 0.75F;
        }

        if (this.random.nextFloat() < f) {
            this.coolTime = MathHelper.secondsToTicks(15);
            this.breakShield = 10;
            this.setGuarding(false);
            this.stopUsingItem();
            this.swing(InteractionHand.OFF_HAND);
            this.playSound(SoundEvents.SHIELD_BREAK, 2.0F, 0.8F + this.level.random.nextFloat() * 0.4F);
            this.level.broadcastEntityEvent(this, (byte)30);
        }

    }

    protected void hurtCurrentlyUsedShield(float p_36383_) {
        if (this.useItem.canPerformAction(net.minecraftforge.common.ToolActions.SHIELD_BLOCK)) {
            if (p_36383_ >= 3.0F) {
                int i = 1 + Mth.floor(p_36383_);
                InteractionHand interactionhand = this.getUsedItemHand();
                this.useItem.hurtAndBreak(i, this, (p_219739_) -> {
                    p_219739_.broadcastBreakEvent(interactionhand);
                });
                if (this.useItem.isEmpty()) {
                    if (interactionhand == InteractionHand.MAIN_HAND) {
                        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    } else {
                        this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }

                    this.useItem = ItemStack.EMPTY;
                    this.playSound(SoundEvents.SHIELD_BREAK, 2.0F, 0.8F + this.level.random.nextFloat() * 0.4F);
                }
            }

        }
    }

    public void handleEntityEvent(byte p_20975_) {
        if (p_20975_ == 29){
            this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.8F + this.level.random.nextFloat() * 0.4F);
        } else if (p_20975_ == 30){
            this.playSound(SoundEvents.SHIELD_BREAK, 2.0F, 0.8F + this.level.random.nextFloat() * 0.4F);
            this.breakShield = 10;
        }
        super.handleEntityEvent(p_20975_);
    }

    public static class GuardingGoal extends Goal {
        private final AbstractHauntedArmor mob;
        private final double speedModifier;
        private final float attackRadiusSqr;
        private int seeTime;
        private boolean strafingClockwise;
        private boolean strafingBackwards;
        private int strafingTime = -1;
        private int ticksUntilNextAttack;

        public GuardingGoal(AbstractHauntedArmor p_25792_, double speed, float radius) {
            this.mob = p_25792_;
            this.speedModifier = speed;
            this.attackRadiusSqr = radius * radius;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            return this.mob.getTarget() != null
                    && this.mob.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ShieldItem
                    && this.mob.coolTime <= 0;
        }

        public boolean canContinueToUse() {
            return (this.canUse() || !this.mob.getNavigation().isDone());
        }

        public void start() {
            super.start();
            this.ticksUntilNextAttack = 0;
            this.mob.startUsingItem(InteractionHand.OFF_HAND);
            this.mob.setGuarding(true);
        }

        public void stop() {
            super.stop();
            this.mob.setGuarding(false);
            this.mob.stopUsingItem();
            this.mob.getNavigation().stop();
            this.mob.setZza(0.0F);
            this.mob.setXxa(0.0F);
            this.seeTime = 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null) {
                double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                boolean flag = this.mob.getSensing().hasLineOfSight(livingentity);
                boolean flag1 = this.seeTime > 0;
                if (flag != flag1) {
                    this.seeTime = 0;
                }

                if (flag) {
                    ++this.seeTime;
                } else {
                    --this.seeTime;
                }

                if (!(d0 > (double)this.attackRadiusSqr) && this.seeTime >= 20) {
                    this.mob.getNavigation().stop();
                    ++this.strafingTime;
                } else {
                    this.mob.getNavigation().moveTo(livingentity, this.speedModifier);
                    this.strafingTime = -1;
                }

                if (this.strafingTime >= 20) {
                    if ((double)this.mob.getRandom().nextFloat() < 0.3D) {
                        this.strafingClockwise = !this.strafingClockwise;
                    }

                    if ((double)this.mob.getRandom().nextFloat() < 0.3D) {
                        this.strafingBackwards = !this.strafingBackwards;
                    }

                    this.strafingTime = 0;
                }

                if (this.strafingTime > -1) {
                    if (d0 > (double)(this.attackRadiusSqr * 0.75F)) {
                        this.strafingBackwards = false;
                    } else if (d0 < (double)(this.attackRadiusSqr * 0.25F)) {
                        this.strafingBackwards = true;
                    }

                    this.mob.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                    this.mob.lookAt(livingentity, 30.0F, 30.0F);
                } else {
                    this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                }
                this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
                this.checkAndPerformAttack(livingentity, d0);
            }
        }

        protected void checkAndPerformAttack(LivingEntity p_25557_, double p_25558_) {
            double d0 = this.getAttackReachSqr(p_25557_);
            if (p_25558_ <= d0 && this.ticksUntilNextAttack <= 0) {
                this.resetAttackCooldown();
                this.mob.swing(InteractionHand.MAIN_HAND);
                this.mob.doHurtTarget(p_25557_);
            }
        }

        protected void resetAttackCooldown() {
            this.ticksUntilNextAttack = this.adjustedTickDelay(20);
        }

        protected double getAttackReachSqr(LivingEntity p_25556_) {
            return (double)(this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + p_25556_.getBbWidth());
        }
    }

    public static class AttackGoal extends MeleeAttackGoal{
        private final AbstractHauntedArmor mob;

        public AttackGoal(AbstractHauntedArmor p_25552_, double speed, boolean needSight) {
            super(p_25552_, speed, needSight);
            this.mob = p_25552_;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !this.mob.isGuarding();
        }

        protected double getAttackReachSqr(LivingEntity p_25556_) {
            return (double)(this.mob.getBbWidth() * 2.5F * this.mob.getBbWidth() * 2.5F + p_25556_.getBbWidth());
        }
    }

    public enum HauntedArmPose {
        IDLE,
        GUARD,
        ATTACK;
    }
}
