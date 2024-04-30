package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.common.entities.projectiles.HauntedSkullProjectile;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BoneLord extends AbstractSkeleton implements ICustomAttributes {
    private static final EntityDataAccessor<Optional<UUID>> SKULL_LORD = SynchedEntityData.defineId(BoneLord.class, EntityDataSerializers.OPTIONAL_UUID);

    public BoneLord(EntityType<? extends AbstractSkeleton> type, Level p_i48555_2_) {
        super(type, p_i48555_2_);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(8, new FollowHeadGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.BoneLordHealth.get())
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.BoneLordDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.BoneLordHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.BoneLordDamage.get());
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance pDifficulty) {
        super.populateDefaultEquipmentSlots(randomSource, pDifficulty);
        if (pDifficulty.isHarderThan(Difficulty.EASY.ordinal())){
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.FROZEN_BLADE.get()));
        } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
        }
        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.CURSED_PALADIN_CHESTPLATE.get()));
        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.CURSED_PALADIN_LEGGINGS.get()));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.CURSED_PALADIN_BOOTS.get()));
        this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
    }

    protected void populateDefaultEquipmentEnchantments(DifficultyInstance pDifficulty) {
        if (pDifficulty.getDifficulty() != Difficulty.PEACEFUL && pDifficulty.getDifficulty() != Difficulty.EASY) {
            for (EquipmentSlot equipmentslottype : EquipmentSlot.values()) {
                if (equipmentslottype.getType() == EquipmentSlot.Type.ARMOR) {
                    ItemStack itemstack = this.getItemBySlot(equipmentslottype);
                    if (!itemstack.isEmpty()) {
                        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack);
                        switch (pDifficulty.getDifficulty()) {
                            case NORMAL:
                                map.putIfAbsent(Enchantments.ALL_DAMAGE_PROTECTION, 2);
                            case HARD:
                                map.putIfAbsent(Enchantments.ALL_DAMAGE_PROTECTION, 3);
                        }
                        EnchantmentHelper.setEnchantments(map, itemstack);
                        this.setItemSlot(equipmentslottype, itemstack);
                    }
                }
            }
            if (pDifficulty.getDifficulty() == Difficulty.HARD){
                ItemStack itemstack = this.getItemBySlot(EquipmentSlot.MAINHAND);
                if (!itemstack.isEmpty()){
                    this.setItemSlot(EquipmentSlot.MAINHAND, EnchantmentHelper.enchantItem(this.random, this.getMainHandItem(), 30, false));
                }
            }
        }
    }

    public boolean causeFallDamage(float p_148711_, float p_148712_, DamageSource p_148713_) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (this.getSkullLord() == null || this.getSkullLord().isDeadOrDying()) {
                if (this.tickCount % 20 == 0) {
                    this.discard();
                }
            } else {
                AttributeInstance knockResist = this.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
                if (this.getSkullLord().isHalfHealth()) {
                    this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 1, false, false));
                    if (knockResist != null) {
                        knockResist.setBaseValue(1.0D);
                    }
                } else {
                    if (knockResist != null) {
                        if (knockResist.getBaseValue() > 0.0D) {
                            knockResist.setBaseValue(0.0D);
                        }
                    }
                }
                if (this.isInWall()) {
                    this.moveTo(this.getSkullLord().position());
                }
                if (this.getSkullLord().getTarget() != null) {
                    this.setTarget(this.getSkullLord().getTarget());
                }
            }
        }
    }

    protected boolean isSunBurnTick() {
        return false;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.getEntity() == this.getSkullLord() && pSource.getDirectEntity() instanceof HauntedSkullProjectile){
            return false;
        } else {
            return super.hurt(pSource, pAmount);
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.populateDefaultEquipmentSlots(pLevel.getRandom(), pDifficulty);
        this.populateDefaultEquipmentEnchantments(pDifficulty);
        this.setCanPickUpLoot(false);
        for(EquipmentSlot equipmentslottype : EquipmentSlot.values()) {
            this.setDropChance(equipmentslottype, 0.0F);
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public boolean isAlliedTo(Entity entityIn) {
        if (super.isAlliedTo(entityIn)) {
            return true;
        } else if (entityIn instanceof Monster && ((Monster) entityIn).getMobType() == MobType.UNDEAD) {
            return this.getTeam() == null && entityIn.getTeam() == null;
        } else {
            return false;
        }
    }

    public void makeStuckInBlock(BlockState pState, Vec3 pMotionMultiplier) {
        if (!pState.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(pState, pMotionMultiplier);
        }

    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.SKELETON_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.SKELETON_STEP;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SKULL_LORD, Optional.empty());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        UUID uuid;
        if (pCompound.hasUUID("skullLord")) {
            uuid = pCompound.getUUID("skullLord");
        } else {
            String s = pCompound.getString("skullLord");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.setSkullLordUUID(uuid);
            } catch (Throwable ignored) {
            }
        }
        this.setConfigurableAttributes();
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.getSkullLordUUID() != null) {
            pCompound.putUUID("skullLord", this.getSkullLordUUID());
        }
    }

    @Nullable
    public SkullLord getSkullLord() {
        try {
            UUID uuid = this.getSkullLordUUID();
            if (uuid != null){
                if (EntityFinder.getLivingEntityByUuiD(uuid) instanceof SkullLord){
                    return (SkullLord) EntityFinder.getLivingEntityByUuiD(uuid);
                }
            }
            return null;
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @Nullable
    public UUID getSkullLordUUID() {
        return this.entityData.get(SKULL_LORD).orElse(null);
    }

    public void setSkullLordUUID(UUID uuid){
        this.entityData.set(SKULL_LORD, Optional.ofNullable(uuid));
    }

    public void setSkullLord(SkullLord skullLord){
        this.setSkullLordUUID(skullLord.getUUID());
    }

    public static class FollowHeadGoal extends Goal {
        private final BoneLord boneLord;
        private LivingEntity owner;
        private final PathNavigation navigation;
        private int timeToRecalcPath;
        private float oldWaterCost;

        public FollowHeadGoal(BoneLord boneLord) {
            this.boneLord = boneLord;
            this.navigation = boneLord.getNavigation();
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.boneLord.getSkullLord();
            if (livingentity == null) {
                return false;
            } else if (this.boneLord.distanceToSqr(livingentity) < (double)(100)) {
                return false;
            } else if (this.boneLord.isAggressive()) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (this.navigation.isDone()) {
                return false;
            } else if (this.boneLord.isAggressive()){
                return false;
            } else {
                return !(this.boneLord.distanceToSqr(this.owner) <= (double)(4));
            }
        }

        public void start() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.boneLord.getPathfindingMalus(BlockPathTypes.WATER);
            this.boneLord.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        }

        public void stop() {
            this.owner = null;
            this.navigation.stop();
            this.boneLord.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        }

        public void tick() {
            this.boneLord.getLookControl().setLookAt(this.owner, 10.0F, (float)this.boneLord.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                if (!this.boneLord.isPassenger()) {
                    this.navigation.moveTo(this.owner, 1.0F);
                }
            }
        }

    }
}
