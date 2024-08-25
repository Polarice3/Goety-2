package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieServant;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

import javax.annotation.Nullable;
import java.util.UUID;

public class ZPiglinServant extends ZombieServant {
    private static final UUID SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", 0.05D, AttributeModifier.Operation.ADDITION);
    private int playFirstAngerSoundIn;

    public ZPiglinServant(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(ZombifiedPiglin.class));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ZPiglinServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ZPiglinServantDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.ZPiglinServantArmor.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.ZPiglinServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.ZPiglinServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.ZPiglinServantDamage.get());
    }

    public double getMyRidingOffset() {
        return this.isBaby() ? -0.05D : -0.45D;
    }

    protected float getStandingEyeHeight(Pose p_259553_, EntityDimensions p_259614_) {
        return this.isBaby() ? 0.96999997F : 1.79F;
    }

    protected boolean convertsInWater() {
        return false;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        RandomSource randomSource = pLevel.getRandom();
        this.populateDefaultEquipmentSlots(randomSource, pDifficulty);
        this.populateDefaultEquipmentEnchantments(randomSource, pDifficulty);
        if (this.getTrueOwner() instanceof Enemy || this.isHostile()){
            this.setWandering(true);
        }
        for(EquipmentSlot equipmentslottype : EquipmentSlot.values()) {
            this.setDropChance(equipmentslottype, 0.0F);
        }

        return pSpawnData;
    }

    protected void customServerAiStep() {
        AttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (this.isAggressive()) {
            if (!modifiableattributeinstance.hasModifier(SPEED_MODIFIER_ATTACKING)) {
                modifiableattributeinstance.addTransientModifier(SPEED_MODIFIER_ATTACKING);
            }
            this.maybePlayFirstAngerSound();
        } else if (modifiableattributeinstance.hasModifier(SPEED_MODIFIER_ATTACKING)) {
            modifiableattributeinstance.removeModifier(SPEED_MODIFIER_ATTACKING);
        }

        super.customServerAiStep();
    }

    private void maybePlayFirstAngerSound() {
        if (this.playFirstAngerSoundIn > 0) {
            --this.playFirstAngerSoundIn;
            if (this.playFirstAngerSoundIn == 0) {
                this.playAngerSound();
            }
        }

    }

    private void playAngerSound() {
        this.playSound(SoundEvents.ZOMBIFIED_PIGLIN_ANGRY, this.getSoundVolume() * 2.0F, this.getVoicePitch() * 1.8F);
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.getEntity() != null){
            if (pSource.getEntity() instanceof LivingEntity livingEntity){
                if (!(livingEntity instanceof ZombifiedPiglin) && !livingEntity.isAlliedTo(this)){
                    for (ZombifiedPiglin zombifiedPiglin : this.level.getEntitiesOfClass(ZombifiedPiglin.class, this.getBoundingBox().inflate(10))){
                        if (zombifiedPiglin.getTarget() != livingEntity) {
                            if (zombifiedPiglin.canAttack(livingEntity)) {
                                zombifiedPiglin.startPersistentAngerTimer();
                                zombifiedPiglin.setPersistentAngerTarget(livingEntity.getUUID());
                                zombifiedPiglin.setTarget(livingEntity);
                            }
                        }
                    }
                }
            }
        }
        return !this.isInvulnerableTo(pSource) && super.hurt(pSource, pAmount);
    }

    protected SoundEvent getAmbientSound() {
        return this.isAggressive() ? SoundEvents.ZOMBIFIED_PIGLIN_ANGRY : SoundEvents.ZOMBIFIED_PIGLIN_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.ZOMBIFIED_PIGLIN_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIFIED_PIGLIN_DEATH;
    }

    public SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_STEP;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance pDifficulty) {
        if (this.canSpawnArmor()){
            super.populateDefaultEquipmentSlots(randomSource, pDifficulty);
        }
        this.populateWeapon();
    }

    protected void populateWeapon(){
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
    }
}
