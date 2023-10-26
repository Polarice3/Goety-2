package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.FloatSwimGoal;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.hostile.Wraith;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public class AbstractWraith extends Summoned {
    private static final EntityDataAccessor<Byte> FLAGS = SynchedEntityData.defineId(AbstractWraith.class, EntityDataSerializers.BYTE);
    public int fireTick;
    public int teleportCooldown;
    public int teleportTime = 20;
    public int teleportTime2;
    public int postTeleportTime;
    public double prevX;
    public double prevY;
    public double prevZ;
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState postTeleportAnimationState = new AnimationState();
    public AnimationState breathingAnimationState = new AnimationState();

    public AbstractWraith(EntityType<? extends Summoned> p_i48553_1_, Level p_i48553_2_) {
        super(p_i48553_1_, p_i48553_2_);
        this.moveControl = new MobUtil.WraithMoveController(this);
        this.fireTick = 0;
        this.teleportTime2 = 0;
        this.teleportCooldown = 0;
        this.postTeleportTime = 0;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatSwimGoal(this));
        this.goalSelector.addGoal(9, new WraithLookGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new WraithLookGoal(this, Mob.class, 8.0F));
        this.goalSelector.addGoal(10, new WraithLookRandomlyGoal(this));
    }

    public void targetSelectGoal(){
        this.targetSelector.addGoal(1, new SummonTargetGoal(this, false, false));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.WraithHealth.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.WraithDamage.get());
    }

    public AttributeSupplier.Builder getConfiguredAttributes(){
        return setCustomAttributes();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLAGS, (byte)0);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("fireTick", this.fireTick);
        pCompound.putInt("teleportTime2", this.teleportTime2);
        pCompound.putInt("teleportCooldown", this.teleportCooldown);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.fireTick = pCompound.getInt("fireTick");
        this.teleportTime2 = pCompound.getInt("teleportTime2");
        this.teleportCooldown = pCompound.getInt("teleportCooldown");
    }

    protected boolean getWraithFlags(int mask) {
        int i = this.entityData.get(FLAGS);
        return (i & mask) != 0;
    }

    protected void setWraithFlags(int mask, boolean value) {
        int i = this.entityData.get(FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(FLAGS, (byte)(i & 255));
    }

    public boolean isFiring() {
        return this.getWraithFlags(1);
    }

    public void setIsFiring(boolean charging) {
        this.setWraithFlags(1, charging);
    }

    public boolean isTeleporting() {
        return this.getWraithFlags(2);
    }

    public void setIsTeleporting(boolean charging) {
        this.setWraithFlags(2, charging);
    }

    public boolean isBreathing() {
        return this.getWraithFlags(4);
    }

    public void setBreathing(boolean flag) {
        this.setWraithFlags(4, flag);
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.WRAITH_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.WRAITH_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.WRAITH_DEATH.get();
    }

    protected SoundEvent getStepSound() {
        return ModSounds.WRAITH_FLY.get();
    }

    protected SoundEvent getAttackSound(){
        return ModSounds.WRAITH_ATTACK.get();
    }

    protected SoundEvent getTeleportInSound(){
        return ModSounds.WRAITH_TELEPORT.get();
    }

    protected SoundEvent getTeleportOutSound(){
        return ModSounds.WRAITH_TELEPORT.get();
    }

    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        if (this.getStepSound() != null) {
            this.playSound(this.getStepSound(), 0.5F, 1.0F);
        }
    }

    @Override
    protected float nextStep() {
        return this.moveDist + 2.0F;
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource damageSource) {
        return false;
    }

    protected boolean isAffectedByFluids() {
        return false;
    }

    public double getFollowRange(){
        return this.getAttributeValue(Attributes.FOLLOW_RANGE);
    }

    public float getFloatFollowRange(){
        return (float) this.getFollowRange();
    }

    public float halfFollowRange(){
        return this.getFloatFollowRange()/2.0F;
    }

    @Override
    public int xpReward() {
        return 10;
    }

    @Override
    public void tick() {
        this.setGravity();
        super.tick();
    }

    public boolean isPostTeleporting(){
        return this.postTeleportTime > 0;
    }

    public void setGravity(){
        this.setNoGravity(this.isUnderWater());
    }

    public void aiStep() {
        super.aiStep();

        boolean flag = this.isSunBurnTick();
        if (flag) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlot.HEAD);
            if (!itemstack.isEmpty()) {
                if (itemstack.isDamageableItem()) {
                    itemstack.setDamageValue(itemstack.getDamageValue() + this.random.nextInt(2));
                    if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                        this.broadcastBreakEvent(EquipmentSlot.HEAD);
                        this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                    }
                }

                flag = false;
            }

            if (flag) {
                this.setSecondsOnFire(8);
            }
        }

        Vec3 vector3d = this.getDeltaMovement();
        if (!this.onGround()&& vector3d.y < 0.0D && !this.isNoGravity()) {
            this.setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
        }

        if (this.teleportCooldown > 0){
            --this.teleportCooldown;
        }

        if (this.postTeleportTime > 0){
            --this.postTeleportTime;
        }

        if (this.isAlive()) {
            this.attackAI();
            this.teleportAI();
        }

    }

    public void teleportAI(){
        if (!this.level.isClientSide){
            if (this.isTeleporting()) {
                --this.teleportTime;
                if (this.teleportTime <= 2){
                    this.prevX = this.getX();
                    this.prevY = this.getY();
                    this.prevZ = this.getZ();
                }
                if (this.teleportTime <= 0){
                    this.teleport();
                }
            } else {
                this.teleportTime = 20;
            }
        } else {
            if (this.isTeleporting()) {
                --this.teleportTime;
                ++this.teleportTime2;
                if (this.teleportTime <= 2){
                    this.prevX = this.getX();
                    this.prevY = this.getY();
                    this.prevZ = this.getZ();
                }
            } else {
                this.teleportTime = 20;
                this.teleportTime2 = 0;
            }
        }
    }

    public void attackAI(){
        if (!this.level.isClientSide) {
            if (this.isPostTeleporting()){
                this.getNavigation().stop();
            }
            if (this.getTarget() != null && !this.isPostTeleporting()) {
                if (!this.isFiring()){
                    this.getLookControl().setLookAt(this.getTarget(), 100.0F, this.getMaxHeadXRot());
                }
                if (this.getSensing().hasLineOfSight(this.getTarget())) {
                    if (((this.getTarget().distanceToSqr(this) >= Mth.square(4.0F) || this.isStaying())
                            && this.getTarget().distanceToSqr(this) < Mth.square(this.halfFollowRange())) || this.isFiring()) {
                        ++this.fireTick;
                        if (this.isFiring()){
                            this.getNavigation().stop();
                            double d2 = this.getTarget().getX() - this.getX();
                            double d1 = this.getTarget().getZ() - this.getZ();
                            this.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                            this.yBodyRot = this.getYRot();
                        }
                        if (this.fireTick > 10) {
                            this.startFiring();
                        } else {
                            this.movement();
                            this.stopFiring();
                        }
                        if (this.fireTick == 20) {
                            this.magicFire(this.getTarget());
                        }
                        if (this.fireTick > 44){
                            this.fireTick = -30;
                        }
                    } else {
                        if (this.fireTick > 0) {
                            this.fireTick = 0;
                        }
                        this.stopFiring();
                        if (!this.isStaying() && this.getTarget().distanceToSqr(this) <= Mth.square(4.0F) && this.teleportCooldown <= 0 && !this.isPostTeleporting()) {
                            this.getNavigation().stop();
                            this.setIsTeleporting(true);
                        } else {
                            this.setIsTeleporting(false);
                            this.movement();
                        }
                    }
                } else {
                    if (MainConfig.WraithAggressiveTeleport.get()) {
                        if (!this.isStaying() && this.teleportCooldown <= 0 && !this.isPostTeleporting()) {
                            this.getNavigation().stop();
                            this.setIsTeleporting(true);
                        }
                    }
                }
            } else {
                this.setIsTeleporting(false);
                this.stopFiring();
            }
        }
    }

    public void magicFire(LivingEntity livingEntity){
        if (this.level.random.nextFloat() <= 0.05F) {
            WandUtil.spawnCrossIceBouquet(this.level, livingEntity.position(), this);
        } else {
            WandUtil.spawnIceBouquet(this.level, livingEntity.position(), this);
        }
    }

    public void movement(){
        if (this.getTarget() != null && !this.isStaying() && !this.isPostTeleporting()) {
            Vec3 vector3d2;
            if (this.getTarget().distanceToSqr(this) > Mth.square(this.halfFollowRange())){
                vector3d2 = this.getTarget().position();
            } else {
                vector3d2 = LandRandomPos.getPos(this, 4, 4);
            }
            if (vector3d2 != null) {
                Path path = this.getNavigation().createPath(vector3d2.x, vector3d2.y, vector3d2.z, 0);
                if (path != null && this.getNavigation().isDone()) {
                    this.getNavigation().moveTo(path, 1.25F);
                }
            }
        }
    }

    protected void teleport() {
        if (!this.level.isClientSide() && this.isAlive() && this.getTarget() != null) {
            if (this.getSensing().hasLineOfSight(this.getTarget())) {
                for (int i = 0; i < 128; ++i) {
                    double d3 = this.getTarget().getX() + (this.getRandom().nextDouble() - 0.5D) * this.getFollowRange();
                    double d4 = this.getTarget().getY();
                    double d5 = this.getTarget().getZ() + (this.getRandom().nextDouble() - 0.5D) * this.getFollowRange();
                    BlockPos blockPos1 = BlockPos.containing(d3, d4, d5);
                    if (!(this.level.canSeeSky(blockPos1) && this.level.isDay()
                            && !(this.fireImmune() || this.hasEffect(MobEffects.FIRE_RESISTANCE)))) {
                        /*Makes it so that the Wraith teleports to a position where they can see its target.*/
                        AbstractWraith wraith = new Wraith(ModEntityType.WRAITH.get(), this.level);
                        wraith.setPos(d3, d4, d5);
                        wraith.getLookControl().setLookAt(this.getTarget(), 100.0F, 100.0F);
                        if (wraith.hasLineOfSight(this.getTarget())) {
                            if (this.randomTeleport(d3, d4, d5, false)) {
                                this.teleportHits();
                                this.setIsTeleporting(false);
                                wraith.discard();
                                MobUtil.instaLook(this, this.getTarget());
                                break;
                            }
                        } else {
                            wraith.discard();
                        }
                    }
                }
            } else {
                this.teleportTowardsEntity(this.getTarget());
            }
        }
    }

    public void teleportTowardsEntity(LivingEntity livingEntity){
        for(int i = 0; i < 128; ++i) {
            Vec3 vector3d = new Vec3(this.getX() - livingEntity.getX(), this.getY(0.5D) - livingEntity.getEyeY(), this.getZ() - livingEntity.getZ());
            vector3d = vector3d.normalize();
            double d0 = 16.0D;
            double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.x * d0;
            double d2 = this.getY() + (double)(this.random.nextInt(16) - 8) - vector3d.y * d0;
            double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.z * d0;
            if (this.randomTeleport(d1, d2, d3, false)) {
                this.teleportHits();
                this.teleportCooldown = 100;
                this.setIsTeleporting(false);
                MobUtil.instaLook(this, livingEntity);
                break;
            }
        }
    }

    public void teleportHits(){
        this.postTeleportTime = 38;
        this.level.broadcastEntityEvent(this, (byte) 6);
        this.level.broadcastEntityEvent(this, (byte) 100);
        this.level.broadcastEntityEvent(this, (byte) 101);
        this.level.gameEvent(GameEvent.TELEPORT, this.position(), GameEvent.Context.of(this));
        if (!this.isSilent()) {
            this.level.playSound(null, this.prevX, this.prevY, this.prevZ, this.getTeleportInSound(), this.getSoundSource(), 1.0F, 1.0F);
            this.playSound(this.getTeleportOutSound(), 1.0F, 1.0F);
        }
    }

    public void startFiring(){
        if (!this.isFiring()) {
            this.setIsFiring(true);
            this.level.broadcastEntityEvent(this, (byte) 4);
            this.level.broadcastEntityEvent(this, (byte) 100);
            if (!this.isSilent()) {
                this.level.playSound((Player) null, this.getX(), this.getY(), this.getZ(), this.getAttackSound(), this.getSoundSource(), 1.0F, 1.0F);
                this.playSound(this.getAttackSound(), 1.0F, 1.0F);
            }
        }
    }

    public void stopFiring(){
        if (this.isFiring()) {
            this.setIsFiring(false);
            this.level.broadcastEntityEvent(this, (byte) 5);
        }
    }

    public ParticleOptions getFireParticles(){
        return ModParticleTypes.WRAITH.get();
    }

    public ParticleOptions getBurstParticles(){
        return ModParticleTypes.WRAITH_BURST.get();
    }

    public void handleEntityEvent(byte pId) {
        super.handleEntityEvent(pId);
        if (pId == 4) {
            this.setIsFiring(true);
            this.attackAnimationState.start(this.tickCount);
        }
        if (pId == 5) {
            this.setIsFiring(false);
            this.attackAnimationState.stop();
        }
        if (pId == 6){
            this.postTeleportAnimationState.start(this.tickCount);
            this.postTeleportTime = 38;
        }
        if (pId == 7){
            this.postTeleportAnimationState.stop();
        }
        if (pId == 100){
            for(int j = 0; j < 6; ++j) {
                double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth() * 2.0D;
                double d2 = this.getY() + (this.random.nextDouble() + 0.5D);
                double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth() * 2.0D;
                this.level.addParticle(this.getFireParticles(), d1, d2, d3, 0.0D, 0.0D, 0.0D);
            }
            if (!this.level.getBlockState(this.blockPosition().below()).isAir()) {
                for (int j = 0; j < 4; ++j) {
                    double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth() * 2.0D;
                    double d2 = this.blockPosition().below().getY() + 0.5F;
                    double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth() * 2.0D;
                    this.level.addParticle(this.getBurstParticles(), d1, d2, d3, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        if (pId == 101){
            if (!this.isSilent()) {
                this.level.playSound(null, this.prevX, this.prevY, this.prevZ, this.getTeleportInSound(), this.getSoundSource(), 1.0F, 1.0F);
                this.playSound(this.getTeleportOutSound(), 1.0F, 1.0F);
            }
        }

    }

    public float getAnimationProgress(float pPartialTicks) {
        if (this.teleportTime <= 12 && this.isAlive()) {
            int i = this.teleportTime - 2;
            return i <= 0 ? 1.0F : 1.0F - ((float) i - pPartialTicks) / 20.0F;
        } else {
            return 0.0F;
        }
    }

    public static class WraithLookGoal extends LookAtPlayerGoal {
        public AbstractWraith wraith;

        public WraithLookGoal(AbstractWraith p_i1631_1_, Class<? extends LivingEntity> p_i1631_2_, float p_i1631_3_) {
            super(p_i1631_1_, p_i1631_2_, p_i1631_3_);
            this.wraith = p_i1631_1_;
        }

        public WraithLookGoal(AbstractWraith p_i1632_1_, Class<? extends LivingEntity> p_i1632_2_, float p_i1632_3_, float p_i1632_4_) {
            super(p_i1632_1_, p_i1632_2_, p_i1632_3_, p_i1632_4_);
            this.wraith = p_i1632_1_;
        }

        public boolean canUse() {
            return super.canUse() && this.wraith.fireTick < 0 && this.wraith.getTarget() == null;
        }
    }

    public static class WraithLookRandomlyGoal extends RandomLookAroundGoal {
        public AbstractWraith wraith;

        public WraithLookRandomlyGoal(AbstractWraith p_i1631_1_) {
            super(p_i1631_1_);
            this.wraith = p_i1631_1_;
        }

        public boolean canUse() {
            return super.canUse() && this.wraith.fireTick < 0 && this.wraith.getTarget() == null;
        }
    }
}
