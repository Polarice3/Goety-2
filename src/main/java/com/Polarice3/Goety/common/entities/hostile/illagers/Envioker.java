package com.Polarice3.Goety.common.entities.hostile.illagers;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.SwordProjectile;
import com.Polarice3.Goety.utils.MobUtil;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

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
        this.goalSelector.addGoal(5, new AttackSpellGoal());
        this.goalSelector.addGoal(6, new ProjectileSpellGoal());
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.EnviokerHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.EnviokerDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.EnviokerHealth.get());
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
        SpawnGroupData ilivingentitydata = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        RandomSource randomSource = pLevel.getRandom();
        this.populateDefaultEquipmentSlots(randomSource, pDifficulty);
        this.populateDefaultEquipmentEnchantments(randomSource, pDifficulty);
        return ilivingentitydata;
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance pDifficulty) {
        if (this.getCurrentRaid() == null) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.TOTEM_OF_UNDYING));
        }
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

    public boolean isAlliedTo(Entity pEntity) {
        if (pEntity == this) {
            return true;
        } else if (super.isAlliedTo(pEntity)) {
            return true;
        } else if (pEntity instanceof Vex) {
            return ((Vex) pEntity).getOwner() != null && this.isAlliedTo(((Vex)pEntity).getOwner());
        } else if (pEntity instanceof LivingEntity && ((LivingEntity)pEntity).getMobType() == MobType.ILLAGER) {
            return this.getTeam() == null && pEntity.getTeam() == null;
        } else {
            return false;
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

    public void applyRaidBuffs(int pWave, boolean p_213660_2_) {
        ItemStack itemstack = new ItemStack(Items.IRON_SWORD);
        Raid raid = this.getCurrentRaid();
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

    class AttackSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
        private AttackSpellGoal() {
        }

        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else {
                return Envioker.this.isMagic();
            }
        }

        protected int getCastingTime() {
            return 40;
        }

        protected int getCastingInterval() {
            return 100;
        }

        protected void performSpellCasting() {
            LivingEntity livingentity = Envioker.this.getTarget();
            double d0 = Math.min(livingentity.getY(), Envioker.this.getY());
            double d1 = Math.max(livingentity.getY(), Envioker.this.getY()) + 1.0D;
            float f = (float) Mth.atan2(livingentity.getZ() - Envioker.this.getZ(), livingentity.getX() - Envioker.this.getX());
            if (Envioker.this.distanceToSqr(livingentity) < 9.0D) {
                for(int i = 0; i < 5; ++i) {
                    float f1 = f + (float)i * (float)Math.PI * 0.4F;
                    this.createSpellEntity(Envioker.this.getX() + (double)Mth.cos(f1) * 1.5D, Envioker.this.getZ() + (double)Mth.sin(f1) * 1.5D, d0, d1, f1, 0);
                }

                for(int k = 0; k < 8; ++k) {
                    float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
                    this.createSpellEntity(Envioker.this.getX() + (double)Mth.cos(f2) * 2.5D, Envioker.this.getZ() + (double)Mth.sin(f2) * 2.5D, d0, d1, f2, 3);
                }

                for(int k = 0; k < 11; ++k) {
                    float f2 = f + (float)k * (float)Math.PI * 4.0F / 16.0F + 2.5133462F;
                    this.createSpellEntity(Envioker.this.getX() + (double)Mth.cos(f2) * 3.5D, Envioker.this.getZ() + (double)Mth.sin(f2) * 3.5D, d0, d1, f2, 6);
                }
            } else {
                float radius = 0.2F;
                for(int l = 0; l < 32; ++l) {
                    double d2 = 1.25D * (double)(l + 1);
                    float fleft = f + radius;
                    float fright = f - radius;
                    this.createSpellEntity(Envioker.this.getX() + (double)Mth.cos(f) * d2, Envioker.this.getZ() + (double)Mth.sin(f) * d2, d0, d1, f, l);
                    this.createSpellEntity(Envioker.this.getX() + (double)Mth.cos(fleft) * d2, Envioker.this.getZ() + (double)Mth.sin(fleft) * d2, d0, d1, fleft, l);
                    this.createSpellEntity(Envioker.this.getX() + (double)Mth.cos(fright) * d2, Envioker.this.getZ() + (double)Mth.sin(fright) * d2, d0, d1, fright, l);
                }
            }

        }

        private void createSpellEntity(double p_190876_1_, double p_190876_3_, double p_190876_5_, double p_190876_7_, float p_190876_9_, int p_190876_10_) {
            BlockPos blockpos = new BlockPos(p_190876_1_, p_190876_7_, p_190876_3_);
            boolean flag = false;
            double d0 = 0.0D;

            do {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = Envioker.this.level.getBlockState(blockpos1);
                if (blockstate.isFaceSturdy(Envioker.this.level, blockpos1, Direction.UP)) {
                    if (!Envioker.this.level.isEmptyBlock(blockpos)) {
                        BlockState blockstate1 = Envioker.this.level.getBlockState(blockpos);
                        VoxelShape voxelshape = blockstate1.getCollisionShape(Envioker.this.level, blockpos);
                        if (!voxelshape.isEmpty()) {
                            d0 = voxelshape.max(Direction.Axis.Y);
                        }
                    }

                    flag = true;
                    break;
                }

                blockpos = blockpos.below();
            } while(blockpos.getY() >= Mth.floor(p_190876_5_) - 1);

            if (flag) {
                Envioker.this.level.addFreshEntity(new EvokerFangs(Envioker.this.level, p_190876_1_, (double)blockpos.getY() + d0, p_190876_3_, p_190876_9_, p_190876_10_, Envioker.this));
            }

        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        protected IllagerSpell getSpell() {
            return IllagerSpell.FANGS;
        }
    }

    class CastingSpellGoal extends SpellcasterCastingSpellGoal {
        private CastingSpellGoal() {
        }

        public void tick() {
            if (Envioker.this.getTarget() != null) {
                Envioker.this.getLookControl().setLookAt(Envioker.this.getTarget(), (float)Envioker.this.getMaxHeadYRot(), (float)Envioker.this.getMaxHeadXRot());
            }

        }
    }

    class ProjectileSpellGoal extends SpellcasterUseSpellGoal {
        private ProjectileSpellGoal() {
        }

        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else {
                return Envioker.this.isMagic() && Envioker.this.getMainHandItem().getItem() instanceof SwordItem;
            }
        }

        protected int getCastingTime() {
            return 10;
        }

        protected int getCastingInterval() {
            return 10;
        }

        protected void performSpellCasting() {
            LivingEntity livingentity = Envioker.this.getTarget();
            if (livingentity != null) {
                if (Envioker.this.getSensing().hasLineOfSight(livingentity)) {
                    SwordProjectile swordProjectile = new SwordProjectile(Envioker.this, Envioker.this.level, Envioker.this.getMainHandItem());
                    double d0 = livingentity.getX() - Envioker.this.getX();
                    double d1 = livingentity.getY(0.3333333333333333D) - swordProjectile.getY();
                    double d2 = livingentity.getZ() - Envioker.this.getZ();
                    double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
                    swordProjectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    swordProjectile.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, 1.0F);
                    Envioker.this.level.addFreshEntity(swordProjectile);
                    if (!Envioker.this.isSilent()) {
                        Envioker.this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F);
                    }
                }
            }

        }

        protected SoundEvent getSpellPrepareSound() {
            return null;
        }

        protected IllagerSpell getSpell() {
            return IllagerSpell.WOLOLO;
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
                return i < 1 && Envioker.this.isMagic();
            }
        }

        protected int getCastingTime() {
            return 100;
        }

        protected int getCastingInterval() {
            return 340;
        }

        protected void performSpellCasting() {
            ServerLevel serverworld = (ServerLevel)Envioker.this.level;

            BlockPos blockpos = Envioker.this.blockPosition().offset(-2 + Envioker.this.random.nextInt(5), 1, -2 + Envioker.this.random.nextInt(5));
            Tormentor tormentorEntity = ModEntityType.TORMENTOR.get().create(Envioker.this.level);
            assert tormentorEntity != null;
            tormentorEntity.moveTo(blockpos, 0.0F, 0.0F);
            tormentorEntity.finalizeSpawn(serverworld, Envioker.this.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, (CompoundTag)null);
            tormentorEntity.setOwner(Envioker.this);
            tormentorEntity.setBoundOrigin(blockpos);
            tormentorEntity.setLimitedLife(20 * (30 + Envioker.this.random.nextInt(90)));
            serverworld.addFreshEntityWithPassengers(tormentorEntity);

        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        protected IllagerSpell getSpell() {
            return IllagerSpell.SUMMON_VEX;
        }
    }

    class AttackGoal extends MeleeAttackGoal {
        public AttackGoal(Envioker p_i50577_2_) {
            super(p_i50577_2_, 1.0D, false);
        }

        @Override
        public boolean canUse() {
            return !Envioker.this.isMagic() && Envioker.this.getTarget() != null;
        }

        protected double getAttackReachSqr(LivingEntity pAttackTarget) {
            if (this.mob.getVehicle() instanceof Ravager) {
                float f = this.mob.getVehicle().getBbWidth() - 0.1F;
                return (double)(f * 2.0F * f * 2.0F + pAttackTarget.getBbWidth());
            } else {
                return super.getAttackReachSqr(pAttackTarget);
            }
        }
    }

}
