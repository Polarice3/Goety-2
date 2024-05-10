package com.Polarice3.Goety.common.entities.hostile.illagers;

import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class Tormentor extends AbstractIllager implements ICustomAttributes {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Tormentor.class, EntityDataSerializers.BYTE);
    private Mob owner;
    @Nullable
    private BlockPos boundOrigin;
    private boolean hasLimitedLife;
    private int limitedLifeTicks;

    public Tormentor(EntityType<? extends AbstractIllager> p_i50190_1_, Level p_i50190_2_) {
        super(p_i50190_1_, p_i50190_2_);
        this.moveControl = new MobUtil.MinionMoveControl(this);
        this.xpReward = 6;
    }

    public void move(MoverType typeIn, Vec3 pos) {
        super.move(typeIn, pos);
        this.checkInsideBlocks();
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new Tormentor.ChargeAttackGoal());
        this.goalSelector.addGoal(8, new Tormentor.MoveRandomGoal());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, new Tormentor.CopyOwnerTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.TormentorHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.TormentorArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.TormentorDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.TormentorHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.TormentorArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.TormentorDamage.get());
    }

    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
        if (this.hasLimitedLife && --this.limitedLifeTicks <= 0) {
            this.limitedLifeTicks = 20;
            this.hurt(DamageSource.STARVE, 1.0F);
        }
        if ((this.getOwner() == null || this.getOwner().isDeadOrDying()) && this.hasLimitedLife){
            if (this.tickCount % 20 == 0) {
                this.hurt(DamageSource.STARVE, this.getMaxHealth() / 4);
            }
        }
        if (!this.isCharging()){
            this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20, 0, false, false));
        } else {
            this.removeEffect(MobEffects.INVISIBILITY);
        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean p_213660_2_) {

    }

    private boolean getVexFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setVexFlag(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte)(i & 255));
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("BoundX")) {
            this.boundOrigin = new BlockPos(pCompound.getInt("BoundX"), pCompound.getInt("BoundY"), pCompound.getInt("BoundZ"));
        }

        if (pCompound.contains("LifeTicks")) {
            this.setLimitedLife(pCompound.getInt("LifeTicks"));
        }
        this.setConfigurableAttributes();
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.boundOrigin != null) {
            pCompound.putInt("BoundX", this.boundOrigin.getX());
            pCompound.putInt("BoundY", this.boundOrigin.getY());
            pCompound.putInt("BoundZ", this.boundOrigin.getZ());
        }

        if (this.hasLimitedLife) {
            pCompound.putInt("LifeTicks", this.limitedLifeTicks);
        }

    }

    public Mob getOwner() {
        return this.owner;
    }

    @Nullable
    public BlockPos getBoundOrigin() {
        return this.boundOrigin;
    }

    public void setBoundOrigin(@Nullable BlockPos pBoundOrigin) {
        this.boundOrigin = pBoundOrigin;
    }

    public boolean isCharging() {
        return this.getVexFlag(1);
    }

    public void setIsCharging(boolean charging) {
        this.setVexFlag(1, charging);
    }

    public void setOwner(Mob pOwner) {
        this.owner = pOwner;
    }

    public void setLimitedLife(int pLimitedLifeTicks) {
        this.hasLimitedLife = true;
        this.limitedLifeTicks = pLimitedLifeTicks;
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.TORMENTOR_AMBIENT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.TORMENTOR_DEATH.get();
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.TORMENTOR_HURT.get();
    }

    public float getBrightness() {
        return 1.0F;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        RandomSource randomSource = pLevel.getRandom();
        this.populateDefaultEquipmentSlots(randomSource, pDifficulty);
        this.populateDefaultEquipmentEnchantments(randomSource, pDifficulty);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.TORMENTOR_CELEBRATE.get();
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance pDifficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    public boolean canBeLeader() {
        return false;
    }

    class ChargeAttackGoal extends Goal {
        public ChargeAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (Tormentor.this.getTarget() != null && !Tormentor.this.getMoveControl().hasWanted() && Tormentor.this.random.nextInt(7) == 0) {
                return Tormentor.this.distanceToSqr(Tormentor.this.getTarget()) > 4.0D;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return Tormentor.this.getMoveControl().hasWanted() && Tormentor.this.isCharging() && Tormentor.this.getTarget() != null && Tormentor.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingentity = Tormentor.this.getTarget();
            assert livingentity != null;
            Vec3 vector3d = livingentity.position();
            Tormentor.this.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0D);
            Tormentor.this.setIsCharging(true);
            Tormentor.this.playSound(ModSounds.TORMENTOR_CHARGE.get(), 1.0F, 1.0F);
        }

        public void stop() {
            Tormentor.this.setIsCharging(false);
        }

        public void tick() {
            LivingEntity livingentity = Tormentor.this.getTarget();
            assert livingentity != null;
            if (Tormentor.this.getBoundingBox().inflate(1.0F).intersects(livingentity.getBoundingBox())) {
                Tormentor.this.doHurtTarget(livingentity);
                Tormentor.this.setIsCharging(false);
            } else {
                double d0 = Tormentor.this.distanceToSqr(livingentity);
                if (d0 < 9.0D) {
                    Vec3 vector3d = livingentity.getEyePosition(1.0F);
                    Tormentor.this.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0D);
                }
            }

        }
    }

    class CopyOwnerTargetGoal extends TargetGoal {
        private final TargetingConditions copyOwnerTargeting = TargetingConditions.forNonCombat().ignoreLineOfSight().ignoreInvisibilityTesting();

        public CopyOwnerTargetGoal(PathfinderMob p_i47231_2_) {
            super(p_i47231_2_, false);
        }

        public boolean canUse() {
            return Tormentor.this.owner != null && Tormentor.this.owner.getTarget() != null && this.canAttack(Tormentor.this.owner.getTarget(), this.copyOwnerTargeting);
        }

        public void start() {
            Tormentor.this.setTarget(Tormentor.this.owner.getTarget());
            super.start();
        }
    }

    class MoveRandomGoal extends Goal {
        public MoveRandomGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return !Tormentor.this.getMoveControl().hasWanted() && Tormentor.this.random.nextInt(7) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = Tormentor.this.getBoundOrigin();
            if (blockpos == null) {
                blockpos = Tormentor.this.blockPosition();
            }

            for(int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(Tormentor.this.random.nextInt(15) - 7, Tormentor.this.random.nextInt(11) - 5, Tormentor.this.random.nextInt(15) - 7);
                if (Tormentor.this.level.isEmptyBlock(blockpos1)) {
                    Tormentor.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
                    if (Tormentor.this.getTarget() == null) {
                        Tormentor.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }
}
