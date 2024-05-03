package com.Polarice3.Goety.common.entities.hostile.servants;

import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.common.entities.hostile.Irk;
import com.Polarice3.Goety.common.entities.projectiles.SwordProjectile;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class VizierClone extends SpellcasterIllager {
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(VizierClone.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> OWNER_CLIENT_ID = SynchedEntityData.defineId(VizierClone.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> POSITION = SynchedEntityData.defineId(VizierClone.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Byte> VIZIER_FLAGS = SynchedEntityData.defineId(VizierClone.class, EntityDataSerializers.BYTE);
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public int spellCool = 200;

    public VizierClone(EntityType<? extends SpellcasterIllager> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
        this.moveControl = new MobUtil.MinionMoveControl(this);
    }

    public void move(MoverType typeIn, Vec3 pos) {
        super.move(typeIn, pos);
        this.checkInsideBlocks();
    }

    public void tick() {
        if (this.isEffectiveAi()) {
            if (this.getOwner() == null || this.getOwner().isDeadOrDying()) {
                if (!this.level.isClientSide) {
                    for (int i = 0; i < this.level.random.nextInt(35) + 10; ++i) {
                        ServerParticleUtil.smokeParticles(ParticleTypes.POOF, this.getX(), this.getEyeY(), this.getZ(), this.level);
                    }
                }
                this.discard();
            } else {
                if (this.distanceTo(this.getOwner()) > 32.0F){
                    if (this.getTarget() != null && this.distanceTo(this.getTarget()) > 8.0F){
                        this.moveTo(Vec3.atCenterOf(BlockFinder.SummonPosition(this, this.getOwner().blockPosition())));
                    } else if (this.getTarget() == null){
                        this.moveTo(Vec3.atCenterOf(BlockFinder.SummonPosition(this, this.getOwner().blockPosition())));
                    }
                }
                if (this.isSpellcasting()) {
                    double x;
                    double z;
                    if (this.getPositionType() == 0){
                        x = MobUtil.getHorizontalLeftLookAngle(this.getOwner()).x * 4;
                        z = MobUtil.getHorizontalLeftLookAngle(this.getOwner()).z * 4;
                    } else {
                        x = MobUtil.getHorizontalRightLookAngle(this.getOwner()).x * 4;
                        z = MobUtil.getHorizontalRightLookAngle(this.getOwner()).z * 4;
                    }
                    Vec3 vector3d = this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D);
                    if (!this.level.isClientSide){
                        double d0 = vector3d.y;
                        if (this.getY() < this.getOwner().getY() + 1.0D) {
                            d0 = Math.max(0.0D, d0);
                            d0 = d0 + (0.3D - d0 * (double)0.6F);
                        }
                        vector3d = new Vec3(vector3d.x, d0, vector3d.z);
                        Vec3 vector3d1 = new Vec3((this.getOwner().getX() + x) - this.getX(), 0.0D, (this.getOwner().getZ() + z) - this.getZ());
                        if (getHorizontalDistanceSqr(vector3d1) > 9.0D) {
                            Vec3 vector3d2 = vector3d1.normalize();
                            vector3d = vector3d.add(vector3d2.x * 0.3D - vector3d.x * 0.6D, 0.0D, vector3d2.z * 0.3D - vector3d.z * 0.6D);
                        }
                    }
                    this.setDeltaMovement(vector3d);
                    if (this.getTarget() != null) {
                        MobUtil.instaLook(this, this.getTarget());
                    }
                }
            }
        }
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
        if (this.spellCool > 0){
            --this.spellCool;
        }
    }

    public static double getHorizontalDistanceSqr(Vec3 pVector) {
        return pVector.x * pVector.x + pVector.z * pVector.z;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new ShootSpellGoal());
        this.goalSelector.addGoal(1, new MoveRandomGoal());
        this.goalSelector.addGoal(4, new ChargeAttackGoal());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F){
            @Override
            public boolean canUse() {
                return super.canUse() && VizierClone.this.getTarget() == null;
            }
        });
        this.targetSelector.addGoal(0, new CopyOwnerTargetGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.VizierHealth.get())
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.VizierHealth.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
        this.entityData.define(OWNER_CLIENT_ID, -1);
        this.entityData.define(POSITION, 0);
        this.entityData.define(VIZIER_FLAGS, (byte)0);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        UUID uuid;
        if (compound.hasUUID("Owner")) {
            uuid = compound.getUUID("Owner");
        } else {
            String s = compound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.setOwnerId(uuid);
            } catch (Throwable ignored) {
            }
        }

        if (compound.contains("OwnerClient")){
            this.setOwnerClientId(compound.getInt("OwnerClient"));
        }

        if (compound.contains("Position")){
            this.setPositionType(compound.getInt("Position"));
        }

        if (compound.contains("SpellCool")){
            this.spellCool = compound.getInt("SpellCool");
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getOwnerId() != null) {
            compound.putUUID("Owner", this.getOwnerId());
        }
        if (this.getOwnerClientId() > -1) {
            compound.putInt("OwnerClient", this.getOwnerClientId());
        }
        if (this.getPositionType() > -1 && this.getPositionType() < 2) {
            compound.putInt("Position", this.getPositionType());
        }
        compound.putInt("SpellCool", this.spellCool);
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.VIZIER_AMBIENT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.VIZIER_DEATH.get();
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.VIZIER_HURT.get();
    }

    @Override
    public boolean canBeLeader() {
        return false;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (!this.isEffectiveAi()){
            return super.hurt(source, amount);
        }
        return source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    public boolean isAlliedTo(Entity pEntity) {
        if (pEntity == this) {
            return true;
        } else if (super.isAlliedTo(pEntity)) {
            return true;
        } else if (pEntity instanceof Irk irk && irk.getTrueOwner() != null) {
            return this.isAlliedTo(irk.getTrueOwner());
        } else if (pEntity instanceof LivingEntity && ((LivingEntity)pEntity).getMobType() == MobType.ILLAGER) {
            return this.getTeam() == null && pEntity.getTeam() == null;
        } else {
            return false;
        }
    }

    @Nullable
    public Vizier getOwner() {
        if (!this.level.isClientSide){
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid) instanceof Vizier vizier ? vizier : null;
        } else {
            return this.level.getEntity(this.getOwnerClientId()) instanceof Vizier vizier ? vizier : null;
        }
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.getOwnerId();
    }

    @Nullable
    public UUID getOwnerId() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse((UUID)null);
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    public int getOwnerClientId(){
        return this.entityData.get(OWNER_CLIENT_ID);
    }

    public void setOwnerClientId(int id){
        this.entityData.set(OWNER_CLIENT_ID, id);
    }

    public void setOwner(LivingEntity livingEntity){
        this.setOwnerId(livingEntity.getUUID());
        this.setOwnerClientId(livingEntity.getId());
    }

    public int getPositionType(){
        return this.entityData.get(POSITION);
    }

    public void setPositionType(int type){
        this.entityData.set(POSITION, type);
    }

    private boolean getVizierFlag(int mask) {
        int i = this.entityData.get(VIZIER_FLAGS);
        return (i & mask) != 0;
    }

    private void setVizierFlag(int mask, boolean value) {
        int i = this.entityData.get(VIZIER_FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(VIZIER_FLAGS, (byte)(i & 255));
    }

    public boolean isCharging() {
        return this.getVizierFlag(1);
    }

    public void setCharging(boolean charging) {
        this.setVizierFlag(1, charging);
    }

    public boolean isSpellcasting(){
        return this.getVizierFlag(2);
    }

    public void setSpellcasting(boolean spellcasting){
        this.setVizierFlag(2, spellcasting);
    }

    public IllagerArmPose getArmPose() {
        if (this.isCharging()) {
            return IllagerArmPose.ATTACKING;
        } else if (this.isSpellcasting()){
            return IllagerArmPose.SPELLCASTING;
        } else {
            return this.isCelebrating() ? IllagerArmPose.CELEBRATING : IllagerArmPose.NEUTRAL;
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_37856_, DifficultyInstance p_37857_, MobSpawnType p_37858_, @Nullable SpawnGroupData p_37859_, @Nullable CompoundTag p_37860_) {
        this.populateDefaultEquipmentSlots(p_37856_.getRandom(), p_37857_);
        this.populateDefaultEquipmentEnchantments(p_37856_.getRandom(), p_37857_);
        return super.finalizeSpawn(p_37856_, p_37857_, p_37858_, p_37859_, p_37860_);
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    public void applyRaidBuffs(int wave, boolean p_213660_2_) {
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.VIZIER_CELEBRATE.get();
    }

    class CopyOwnerTargetGoal extends TargetGoal {
        private final TargetingConditions copyOwnerTargeting = TargetingConditions.forNonCombat().ignoreLineOfSight().ignoreInvisibilityTesting();

        public CopyOwnerTargetGoal(PathfinderMob p_34056_) {
            super(p_34056_, false);
        }

        public boolean canUse() {
            return VizierClone.this.getOwner() != null
                    && VizierClone.this.getOwner().getTarget() != null
                    && this.canAttack(VizierClone.this.getOwner().getTarget(), this.copyOwnerTargeting);
        }

        public void start() {
            if (VizierClone.this.getOwner() != null
                    && VizierClone.this.getOwner().getTarget() != null
                    && this.canAttack(VizierClone.this.getOwner().getTarget(), this.copyOwnerTargeting)) {
                VizierClone.this.setTarget(VizierClone.this.getOwner().getTarget());
            }
            super.start();
        }
    }

    class ChargeAttackGoal extends Goal {
        public ChargeAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            if (VizierClone.this.getTarget() != null
                    && !VizierClone.this.getMoveControl().hasWanted()
                    && !VizierClone.this.isSpellcasting()) {
                return VizierClone.this.distanceToSqr(VizierClone.this.getTarget()) > 8.0D || VizierClone.this.random.nextInt(reducedTickDelay(100)) == 0;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return VizierClone.this.getMoveControl().hasWanted()
                    && VizierClone.this.isCharging()
                    && !VizierClone.this.isSpellcasting()
                    && VizierClone.this.getTarget() != null
                    && VizierClone.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingentity = VizierClone.this.getTarget();
            if (livingentity != null) {
                Vec3 vector3d = livingentity.position();
                VizierClone.this.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0D);
                VizierClone.this.setCharging(true);
                VizierClone.this.playSound(ModSounds.VIZIER_CELEBRATE.get(), 1.0F, 1.0F);
            }
        }

        public void stop() {
            VizierClone.this.setCharging(false);
        }

        public void tick() {
            LivingEntity livingentity = VizierClone.this.getTarget();
            if (livingentity != null) {
                VizierClone.this.getLookControl().setLookAt(livingentity.position());
                if (VizierClone.this.getBoundingBox().inflate(1.0D).intersects(livingentity.getBoundingBox())) {
                    VizierClone.this.doHurtTarget(livingentity);
                    VizierClone.this.setCharging(false);
                } else {
                    double d0 = VizierClone.this.distanceToSqr(livingentity);
                    if (d0 < 9.0D) {
                        Vec3 vector3d = livingentity.getEyePosition();
                        VizierClone.this.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0D);
                    }
                }
            }
        }
    }

    class ShootSpellGoal extends Goal {
        int duration;
        int duration2;

        private ShootSpellGoal() {
        }

        public boolean canUse() {
            return VizierClone.this.getOwner() != null
                    && VizierClone.this.getTarget() != null
                    && VizierClone.this.spellCool <= 0
                    && !VizierClone.this.isCharging();
        }

        public void start() {
            VizierClone.this.playSound(SoundEvents.EVOKER_PREPARE_ATTACK, 1.0F, 1.0F);
            VizierClone.this.setSpellcasting(true);
        }

        public void stop() {
            VizierClone.this.setSpellcasting(false);
            VizierClone.this.spellCool = 100 + VizierClone.this.random.nextInt(100);
            this.duration2 = 0;
            this.duration = 0;
        }

        public void tick() {
            LivingEntity livingentity = VizierClone.this.getTarget();
            if (livingentity != null) {
                ++this.duration;
                ++this.duration2;
                if (!VizierClone.this.level.isClientSide) {
                    ServerLevel serverWorld = (ServerLevel) VizierClone.this.level;
                    for (int i = 0; i < 5; ++i) {
                        double d0 = serverWorld.random.nextGaussian() * 0.02D;
                        double d1 = serverWorld.random.nextGaussian() * 0.02D;
                        double d2 = serverWorld.random.nextGaussian() * 0.02D;
                        serverWorld.sendParticles(ParticleTypes.ENCHANT, VizierClone.this.getRandomX(1.0D), VizierClone.this.getRandomY() + 1.0D, VizierClone.this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                    }
                }
                int time = 20;
                if (this.duration >= time) {
                    this.duration = 0;
                    this.attack(livingentity);
                }
                if (this.duration2 >= 160) {
                    VizierClone.this.setSpellcasting(false);
                    VizierClone.this.spellCool = 100 + VizierClone.this.random.nextInt(100);
                    this.duration2 = 0;
                    this.duration = 0;
                }
            } else {
                stop();
            }
        }

        private void attack(LivingEntity livingEntity){
            SwordProjectile swordProjectile = new SwordProjectile(VizierClone.this, VizierClone.this.level, VizierClone.this.getMainHandItem());
            double d0 = livingEntity.getX() - VizierClone.this.getX();
            double d1 = livingEntity.getY(0.3333333333333333D) - swordProjectile.getY();
            double d2 = livingEntity.getZ() - VizierClone.this.getZ();
            double d3 = (double)Mth.sqrt((float) (d0 * d0 + d2 * d2));
            swordProjectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            swordProjectile.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - VizierClone.this.level.getDifficulty().getId() * 4));
            if (!VizierClone.this.getSensing().hasLineOfSight(livingEntity)){
                swordProjectile.setNoPhysics(true);
            }
            VizierClone.this.level.addFreshEntity(swordProjectile);
            if (!VizierClone.this.isSilent()) {
                VizierClone.this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F);
            }
        }

        private void spawnFangs(double p_190876_1_, double p_190876_3_, double p_190876_5_, double p_190876_7_, float p_190876_9_, int p_190876_10_) {
            BlockPos blockpos = BlockPos.containing(p_190876_1_, p_190876_7_, p_190876_3_);
            boolean flag = false;
            double d0 = 0.0D;

            do {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = VizierClone.this.level.getBlockState(blockpos1);
                if (blockstate.isFaceSturdy(VizierClone.this.level, blockpos1, Direction.UP)) {
                    if (!VizierClone.this.level.isEmptyBlock(blockpos)) {
                        BlockState blockstate1 = VizierClone.this.level.getBlockState(blockpos);
                        VoxelShape voxelshape = blockstate1.getCollisionShape(VizierClone.this.level, blockpos);
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
                VizierClone.this.level.addFreshEntity(new EvokerFangs(VizierClone.this.level, p_190876_1_, (double)blockpos.getY() + d0, p_190876_3_, p_190876_9_, p_190876_10_, VizierClone.this));
            }

        }
    }

    class MoveRandomGoal extends Goal {
        public MoveRandomGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return !VizierClone.this.getMoveControl().hasWanted()
                    && VizierClone.this.random.nextInt(7) == 0
                    && !VizierClone.this.isCharging();
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            double speed = 0.25D;
            BlockPos blockpos = VizierClone.this.blockPosition();
            if (VizierClone.this.getTarget() != null){
                speed = 1.0D;
                blockpos = VizierClone.this.getTarget().blockPosition();
            }

            for(int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(VizierClone.this.random.nextInt(8) - 4, VizierClone.this.random.nextInt(6) - 2, VizierClone.this.random.nextInt(8) - 4);
                if (VizierClone.this.level.isEmptyBlock(blockpos1)) {
                    VizierClone.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, speed);
                    if (VizierClone.this.getTarget() == null) {
                        VizierClone.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }

    public boolean canChangeDimensions() {
        return false;
    }

}
