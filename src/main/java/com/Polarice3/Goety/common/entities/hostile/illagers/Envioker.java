package com.Polarice3.Goety.common.entities.hostile.illagers;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.Map;

public class Envioker extends HuntingIllagerEntity {

    public Envioker(EntityType<? extends Envioker> p_i50207_1_, Level p_i50207_2_) {
        super(p_i50207_1_, p_i50207_2_);
        this.xpReward = 20;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new CastingSpellGoal());
        this.goalSelector.addGoal(2, new AttackGoal(this));
        this.goalSelector.addGoal(4, new SummonSpellGoal());
        this.goalSelector.addGoal(5, new FangAroundTargetSpellGoal());
        this.goalSelector.addGoal(6, new FangLineSpellGoal());
        this.goalSelector.addGoal(7, new FangSurroundSpellGoal());
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.EnviokerHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.EnviokerArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.EnviokerDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.EnviokerHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.EnviokerArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.EnviokerDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
    }

    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    public boolean isMagic(){
        return this.getOffhandItem().getItem() == Items.TOTEM_OF_UNDYING;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData groupData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (this.getNavigation() instanceof GroundPathNavigation groundNavigation) {
            groundNavigation.setCanOpenDoors(true);
        }
        RandomSource randomSource = pLevel.getRandom();
        this.populateDefaultEquipmentSlots(randomSource, pDifficulty);
        this.populateDefaultEquipmentEnchantments(randomSource, pDifficulty);
        return groupData;
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance pDifficulty) {
        if (this.getCurrentRaid() == null) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        }
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.TOTEM_OF_UNDYING));
    }

    @Override
    public IllagerArmPose getArmPose() {
        if (this.isCastingSpell() && this.isMagic()) {
            return IllagerArmPose.SPELLCASTING;
        } else if (this.isAggressive() && !this.isMagic()) {
            return IllagerArmPose.ATTACKING;
        } else {
            return this.isCelebrating() ? IllagerArmPose.CELEBRATING : IllagerArmPose.CROSSED;
        }
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.EVOKER_HURT;
    }

    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Override
    protected float getDamageAfterMagicAbsorb(DamageSource p_34149_, float p_34150_) {
        p_34150_ = super.getDamageAfterMagicAbsorb(p_34149_, p_34150_);
        if (p_34149_.getEntity() == this) {
            p_34150_ = 0.0F;
        }

        if (p_34149_.is(ModTags.DamageTypes.PHYSICAL)) {
            p_34150_ *= 1.25F;
        }

        if (p_34149_.is(ModTags.DamageTypes.ELEMENTAL)) {
            p_34150_ *= 0.8F;
        }

        return p_34150_;
    }

    public void applyRaidBuffs(int pWave, boolean p_213660_2_) {
        ItemStack itemstack = new ItemStack(Items.IRON_SWORD);
        Raid raid = this.getCurrentRaid();
        if (raid == null) {
            return;
        }
        int i = 1;
        if (pWave > raid.getNumGroups(Difficulty.NORMAL)) {
            i = 3;
        }

        boolean flag = this.random.nextFloat() <= raid.getEnchantOdds();
        if (flag) {
            Map<Enchantment, Integer> map = Maps.newHashMap();
            map.put(Enchantments.SHARPNESS, i);
            map.put(Enchantments.KNOCKBACK, i);
            EnchantmentHelper.setEnchantments(map, itemstack);
        }

        this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
    }

    abstract class FangSpellGoal extends SpellcasterUseSpellGoal {

        public boolean canUse() {
            boolean flag = super.canUse() && Envioker.this.getTarget() != null && Envioker.this.isMagic() && Envioker.this.hasLineOfSight(Envioker.this.getTarget());
            if (flag) {
                LivingEntity livingEntity = Envioker.this.getTarget();
                if (livingEntity != null && livingEntity.isAlive()) {
                    if (this.needsToBeClose()) {
                        if (livingEntity.distanceTo(Envioker.this) > this.distanceToCast()) {
                            Envioker.this.getNavigation().moveTo(livingEntity, 1.0F);
                            flag = false;
                        }
                    }
                }
            }
            return flag;
        }

        @Override
        public void start() {
            Envioker.this.getNavigation().stop();
            super.start();
        }

        public void tick() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
                Envioker.this.playSound(ModSounds.MASTER_FANG_SUMMON.get(), 1.0F, 1.0F);
            }

        }

        @Override
        public void stop() {
            super.stop();
            this.nextAttackTickCount = Envioker.this.tickCount + this.getCastingInterval();
        }

        public boolean needsToBeClose() {
            return true;
        }

        public double distanceToCast() {
            return 8.0D;
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.MASTER_FANG_TELEGRAPH.get();
        }

        protected IllagerSpell getSpell() {
            return IllagerSpell.FANGS;
        }
    }

    class FangLineSpellGoal extends FangSpellGoal {
        private FangLineSpellGoal() {
        }

        @Override
        protected int getCastWarmupTime() {
            return 14;
        }

        protected int getCastingTime() {
            return 30;
        }

        protected int getCastingInterval() {
            return 40;
        }

        protected void performSpellCasting() {
            LivingEntity livingentity = Envioker.this.getTarget();
            if (livingentity != null) {
                WandUtil.spawnFangLine(Envioker.this, livingentity.position());
            }
        }
    }

    class FangAroundTargetSpellGoal extends FangSpellGoal {
        private FangAroundTargetSpellGoal() {
        }

        @Override
        public boolean needsToBeClose() {
            return false;
        }

        @Override
        protected int getCastWarmupTime() {
            return 16;
        }

        protected int getCastingTime() {
            return 46;
        }

        protected int getCastingInterval() {
            return 100;
        }

        protected void performSpellCasting() {
            LivingEntity livingentity = Envioker.this.getTarget();
            if (livingentity != null) {
                WandUtil.spawnFangsAroundTarget(Envioker.this, livingentity.position());
            }
        }
    }

    class FangSurroundSpellGoal extends FangSpellGoal {
        private FangSurroundSpellGoal() {
        }

        protected int getCastingTime() {
            return 60;
        }

        protected int getCastingInterval() {
            return 160;
        }

        protected void performSpellCasting() {
            LivingEntity livingentity = Envioker.this.getTarget();
            if (livingentity != null) {
                WandUtil.spawnFangRound(Envioker.this, livingentity.position());
            }
        }
    }

    class CastingSpellGoal extends SpellcasterCastingSpellGoal {
        private CastingSpellGoal() {
        }

        public void tick() {
            if (Envioker.this.getTarget() != null) {
                Envioker.this.getLookControl().setLookAt(Envioker.this.getTarget(), (float) Envioker.this.getMaxHeadYRot(), (float) Envioker.this.getMaxHeadXRot());
            }

        }
    }

    class SummonSpellGoal extends SpellcasterUseSpellGoal {
        private final TargetingConditions vexCountTargeting = TargetingConditions.forNonCombat().range(16.0D).ignoreLineOfSight().ignoreInvisibilityTesting();

        private SummonSpellGoal() {
        }

        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else {
                int i = Envioker.this.level.getNearbyEntities(Tormentor.class, this.vexCountTargeting, Envioker.this, Envioker.this.getBoundingBox().inflate(16.0D)).size();
                return Envioker.this.getTarget() != null
                        && i < 1 && Envioker.this.isMagic()
                        && Envioker.this.hasLineOfSight(Envioker.this.getTarget());
            }
        }

        protected int getCastingTime() {
            return 100;
        }

        protected int getCastingInterval() {
            return 340;
        }

        protected void performSpellCasting() {
            if (Envioker.this.level instanceof ServerLevel serverLevel) {
                BlockPos blockpos = Envioker.this.blockPosition().offset(-2 + Envioker.this.random.nextInt(5), 1, -2 + Envioker.this.random.nextInt(5));
                Tormentor tormentorEntity = ModEntityType.TORMENTOR.get().create(Envioker.this.level);
                if (tormentorEntity != null) {
                    tormentorEntity.moveTo(blockpos, 0.0F, 0.0F);
                    tormentorEntity.finalizeSpawn(serverLevel, Envioker.this.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, (CompoundTag) null);
                    tormentorEntity.setOwner(Envioker.this);
                    tormentorEntity.setBoundOrigin(blockpos);
                    tormentorEntity.setLimitedLife(20 * (30 + Envioker.this.random.nextInt(90)));
                    serverLevel.addFreshEntityWithPassengers(tormentorEntity);
                }
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        protected IllagerSpell getSpell() {
            return IllagerSpell.SUMMON_VEX;
        }
    }

    class AttackGoal extends ModMeleeAttackGoal {
        public AttackGoal(Envioker p_i50577_2_) {
            super(p_i50577_2_, 1.0D, false);
        }

        @Override
        public boolean canUse() {
            return !Envioker.this.isMagic() && Envioker.this.getTarget() != null;
        }
    }

}