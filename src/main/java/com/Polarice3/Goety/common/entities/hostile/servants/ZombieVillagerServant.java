package com.Polarice3.Goety.common.entities.hostile.servants;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ai.NeutralZombieAttackGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

public class ZombieVillagerServant extends Owned {
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(ZombieVillagerServant.class, EntityDataSerializers.INT);
    public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, Goety.location("textures/entity/servants/zombie/zombie_0.png"));
        map.put(1, Goety.location("textures/entity/servants/zombie/zombie_1.png"));
        map.put(2, Goety.location("textures/entity/servants/zombie/zombie_2.png"));
        map.put(3, Goety.location("textures/entity/servants/zombie/zombie_3.png"));
        map.put(4, Goety.location("textures/entity/servants/zombie/zombie_4.png"));
        map.put(5, Goety.location("textures/entity/servants/zombie/zombie_5.png"));
    });

    public ZombieVillagerServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    public ResourceLocation getResourceLocation() {
        return TEXTURE_BY_TYPE.getOrDefault(this.getOutfitType(), TEXTURE_BY_TYPE.get(0));
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.applyEntityAI();
    }

    protected void applyEntityAI() {
        this.goalSelector.addGoal(2, new NeutralZombieAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(ZombieVillagerServant.class));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ZombieVillagerServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, (double)0.23F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ZombieVillagerServantDamage.get())
                .add(Attributes.ARMOR, 2.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.ZombieVillagerServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.ZombieVillagerServantDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE_ID, 1);
    }

    public int getOutfitType() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setOutfitType(int pType) {
        if (pType < 0 || pType >= this.OutfitTypeNumber() + 1) {
            pType = this.random.nextInt(this.OutfitTypeNumber());
        }

        this.entityData.set(DATA_TYPE_ID, pType);
    }

    public int OutfitTypeNumber(){
        return TEXTURE_BY_TYPE.size();
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setOutfitType(compound.getInt("Outfit"));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Outfit", this.getOutfitType());
    }

    public boolean doHurtTarget(Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            float f = this.level.getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
            if (this.getMainHandItem().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
                entityIn.setSecondsOnFire(2 * (int)f);
            }
        }

        return flag;
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
        return SoundEvents.ZOMBIE_VILLAGER_STEP;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    protected void populateDefaultEquipmentSlots(RandomSource p_219165_, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(p_219165_, difficulty);
        if (this.random.nextFloat() < (this.level.getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
            int i = this.random.nextInt(3);
            if (i == 0) {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            } else {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
            }
        }

    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        float f = difficultyIn.getSpecialMultiplier();
        for(EquipmentSlot equipmentslottype : EquipmentSlot.values()) {
            this.setDropChance(equipmentslottype, 0.0F);
        }
        this.handleAttributes(f);
        this.setOutfitType(this.random.nextInt(this.OutfitTypeNumber()));
        return spawnDataIn;
    }

    protected void handleAttributes(float difficulty) {
        Objects.requireNonNull(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE)).addPermanentModifier(new AttributeModifier("random spawn bonus", this.random.nextDouble() * (double)0.05F, AttributeModifier.Operation.ADDITION));
        double d0 = this.random.nextDouble() * 1.5D * (double)difficulty;
        if (d0 > 1.0D) {
            Objects.requireNonNull(this.getAttribute(Attributes.FOLLOW_RANGE)).addPermanentModifier(new AttributeModifier("random zombie-spawn bonus", d0, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }

    }
}
