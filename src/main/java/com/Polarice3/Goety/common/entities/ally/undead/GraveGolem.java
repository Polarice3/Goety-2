package com.Polarice3.Goety.common.entities.ally.undead;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.ShockwaveParticleOption;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.golem.AbstractGolemServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.HauntedSkullProjectile;
import com.Polarice3.Goety.common.entities.projectiles.SoulBomb;
import com.Polarice3.Goety.common.entities.util.SummonCircle;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GraveGolem extends AbstractGolemServant {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(GraveGolem.class, EntityDataSerializers.BYTE);
    public static float SUMMON_SECONDS_TIME = 4.7F;
    private int activateTick;
    public int attackTick;
    public int summonTick;
    private int summonCool;
    private int summonCount;
    public int belchCool;
    public int isSittingDown;
    public int isStandingUp;
    public float getGlow;
    public float deathRotation = 0.0F;
    public int deathTime = 0;
    public final SimpleContainer inventory = new SimpleContainer(50);
    public boolean hasInventory;
    public AnimationState activateAnimationState = new AnimationState();
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState summonAnimationState = new AnimationState();
    public AnimationState toSitAnimationState = new AnimationState();
    public AnimationState toStandAnimationState = new AnimationState();
    public AnimationState sitAnimationState = new AnimationState();
    public AnimationState shootAnimationState = new AnimationState();
    public AnimationState belchAnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();

    public GraveGolem(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SummonGoal());
        this.goalSelector.addGoal(2, new MeleeGoal());
//        this.goalSelector.addGoal(3, new GraveGolem.BelchGoal(this));
        this.goalSelector.addGoal(3, new GolemRangedGoal(this, 1.0D, 32.0F));
        this.goalSelector.addGoal(5, new AttackGoal(1.0D));
        this.goalSelector.addGoal(8, new WanderGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.GraveGolemHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.GraveGolemArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 5.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.GraveGolemDamage.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.GraveGolemFollowRange.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.GraveGolemHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.GraveGolemArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.GraveGolemDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.GraveGolemFollowRange.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.hasPose(Pose.EMERGING) ? 1 : 0);
    }

    public void recreateFromPacket(ClientboundAddEntityPacket p_219420_) {
        super.recreateFromPacket(p_219420_);
        if (p_219420_.getData() == 1) {
            this.setPose(Pose.EMERGING);
        }

    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_219422_) {
        if (DATA_POSE.equals(p_219422_)) {
            if (this.getPose() == Pose.EMERGING) {
                this.activateAnimationState.start(this.tickCount);
            }
        }

        super.onSyncedDataUpdated(p_219422_);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("ActivateTick", this.activateTick);
        pCompound.putInt("SummonTick", this.summonTick);
        pCompound.putInt("SummonCount", this.summonCount);
        pCompound.putInt("BelchCool", this.belchCool);
        ListTag listnbt = new ListTag();

        for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (!itemstack.isEmpty()) {
                listnbt.add(itemstack.save(new CompoundTag()));
            }
        }

        pCompound.put("Inventory", listnbt);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ActivateTick")) {
            this.activateTick = pCompound.getInt("ActivateTick");
        }
        if (pCompound.contains("SummonTick")) {
            this.summonTick = pCompound.getInt("SummonTick");
        }
        if (pCompound.contains("SummonCount")) {
            this.summonCount = pCompound.getInt("SummonCount");
        }
        if (pCompound.contains("BelchCool")) {
            this.belchCool = pCompound.getInt("BelchCool");
        }
        if (pCompound.contains("Inventory")) {
            ListTag listnbt = pCompound.getList("Inventory", 10);

            for (int i = 0; i < listnbt.size(); ++i) {
                ItemStack itemstack = ItemStack.of(listnbt.getCompound(i));
                if (!itemstack.isEmpty()) {
                    this.inventory.addItem(itemstack);
                }
            }
        }
    }

    public SlotAccess getSlot(int p_149743_) {
        int i = p_149743_ - 300;
        return i >= 0 && i < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, i) : super.getSlot(p_149743_);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.MOB_SUMMONED){
            this.setPose(Pose.EMERGING);
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public boolean canAnimateMove(){
        return super.canAnimateMove() && !this.isMeleeAttacking();
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.isSummoning() || this.isActivating();
    }

    public boolean hasLineOfSight(Entity p_149755_) {
        return this.summonTick <= 0 && !this.isActivating() && super.hasLineOfSight(p_149755_);
    }

    public boolean isSummoning(){
        return this.summonTick > 0;
    }

    private void glow() {
        this.getGlow = Mth.clamp(this.getGlow - 0.05F, 0, 1);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.GRAVE_GOLEM_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.GRAVE_GOLEM_HURT.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(ModSounds.GRAVE_GOLEM_STEP.get());
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GRAVE_GOLEM_DEATH.get();
    }

    private boolean getGolemFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setGolemFlags(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte)(i & 255));
    }

    public boolean isMeleeAttacking() {
        return this.getGolemFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setGolemFlags(1, attacking);
        this.attackTick = 0;
        this.level.broadcastEntityEvent(this, (byte) 5);
    }

    public void setShooting(boolean shooting){
        this.setGolemFlags(2, shooting);
    }

    public boolean isShooting(){
        return this.getGolemFlag(2);
    }

    public void setBelching(boolean belching){
        this.setGolemFlags(4, belching);
    }

    public boolean isBelching(){
        return this.getGolemFlag(4);
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.activateAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.summonAnimationState);
        animationStates.add(this.belchAnimationState);
        animationStates.add(this.toSitAnimationState);
        animationStates.add(this.toStandAnimationState);
        animationStates.add(this.sitAnimationState);
        animationStates.add(this.shootAnimationState);
        animationStates.add(this.walkAnimationState);
        animationStates.add(this.deathAnimationState);
        return animationStates;
    }

    public void setStaying(boolean staying){
        super.setStaying(staying);
        if (staying){
            this.level.broadcastEntityEvent(this, (byte) 22);
        } else if (this.isFollowing()) {
            this.level.broadcastEntityEvent(this, (byte) 23);
        }
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= MathHelper.secondsToTicks(5)) {
            this.spawnAnim();
            this.dropInventory();
            this.remove(RemovalReason.KILLED);
        }
        this.hurtTime = 1;
        this.setYRot(this.deathRotation);
        this.setYBodyRot(this.deathRotation);
    }

    public void die(DamageSource p_21014_) {
        this.level.broadcastEntityEvent(this, (byte) 7);
        this.deathRotation = this.getYRot();
        super.die(p_21014_);
    }

    public void addDrops(Collection<ItemEntity> items) {
        List<ItemStack> drops = items.stream()
                .filter(Objects::nonNull)
                .map(ItemEntity::getItem)
                .filter(itemStack -> !itemStack.isEmpty())
                .toList();
        for (ItemStack itemStack : drops){
            if (this.inventory.canAddItem(itemStack)) {
                this.inventory.addItem(itemStack);
            } else {
                ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), itemStack);
                itemEntity.setDefaultPickUpDelay();
                float f = this.random.nextFloat() * 0.5F;
                float f1 = this.random.nextFloat() * ((float)Math.PI * 2F);
                itemEntity.setDeltaMovement((double)(-Mth.sin(f1) * f), (double)0.2F, (double)(Mth.cos(f1) * f));
                this.level.addFreshEntity(itemEntity);
            }
        }
    }

    public void dropInventory(){
        this.dropInventory(this.getX(), this.getY(), this.getZ());
    }

    public void dropInventory(BlockPos blockPos){
        this.dropInventory(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public void dropInventory(double x, double y, double z){
        if (!this.inventory.isEmpty()){
            for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
                ItemStack itemstack = this.inventory.getItem(i);
                if (itemstack != ItemStack.EMPTY){
                    ItemEntity itemEntity = new ItemEntity(this.level, x, y, z, itemstack);
                    itemEntity.setDefaultPickUpDelay();
                    if (this.level.addFreshEntity(itemEntity)){
                        this.inventory.setItem(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    public void stopMostAnimations(AnimationState animationState0){
        for (AnimationState animationState : this.getAnimations()){
            if (animationState != animationState0) {
                animationState.stop();
            }
        }
    }

    public void stopAnimations(){
        for (AnimationState animationState : this.getAnimations()){
            animationState.stop();
        }
    }

    private boolean isActivating() {
        return this.hasPose(Pose.EMERGING);
    }

    public void tick() {
        super.tick();
        if (this.isDeadOrDying()){
            this.stopMostAnimations(this.deathAnimationState);
            this.setYRot(this.deathRotation);
            this.setYBodyRot(this.deathRotation);
        }
        if (this.hasPose(Pose.EMERGING)){
            ++this.activateTick;
            if (this.activateTick == 1){
                this.playSound(ModSounds.GRAVE_GOLEM_AWAKEN.get(), 2.0F, 1.0F);
            }
            if (this.activateTick > MathHelper.secondsToTicks(3.25F)){
                this.setPose(Pose.STANDING);
            }
        }
        if (this.level.isClientSide()) {
            if (this.isAlive() && !this.isActivating()) {
                this.glow();
                if (!this.isMeleeAttacking() && !this.isShooting() && !this.isBelching() && !this.isSummoning()) {
                    if (this.isMoving()) {
                        this.stopMostAnimations(this.walkAnimationState);
                        this.walkAnimationState.startIfStopped(this.tickCount);
                    } else {
                        if (this.isStaying()) {
                            if (this.isSittingDown > 0){
                                --this.isSittingDown;
                                this.stopMostAnimations(this.toSitAnimationState);
                                this.toSitAnimationState.startIfStopped(this.tickCount);
                            } else {
                                this.stopMostAnimations(this.sitAnimationState);
                                this.sitAnimationState.startIfStopped(this.tickCount);
                            }
                        } else {
                            if (this.isStandingUp > 0){
                                --this.isStandingUp;
                                this.stopMostAnimations(this.toStandAnimationState);
                                this.toStandAnimationState.startIfStopped(this.tickCount);
                            } else {
                                this.stopMostAnimations(this.idleAnimationState);
                                this.idleAnimationState.startIfStopped(this.tickCount);
                            }
                        }
                    }
                } else {
                    if (this.isStaying()){
                        this.isSittingDown = MathHelper.secondsToTicks(1);
                    } else {
                        this.isSittingDown = 0;
                    }
                    this.isStandingUp = 0;
                    this.sitAnimationState.stop();
                    this.idleAnimationState.stop();
                    this.walkAnimationState.stop();
                    this.toSitAnimationState.stop();
                    this.toStandAnimationState.stop();
                }
                if (this.isMeleeAttacking()) {
                    this.getGlow = 1.0F;
                    ++this.attackTick;
                }
                if (this.isSummoning()) {
                    this.getGlow = 1.0F;
                    this.stopMostAnimations(this.summonAnimationState);
                    if (!this.summonAnimationState.isStarted()){
                        this.summonAnimationState.start(this.tickCount);
                    }
                    --this.summonTick;
                } else {
                    this.summonAnimationState.stop();
                }
                if (this.isShooting()) {
                    this.stopMostAnimations(this.shootAnimationState);
                    if (!this.shootAnimationState.isStarted()){
                        this.shootAnimationState.start(this.tickCount);
                    }
                }
            }
        }
        if (!this.level.isClientSide){
            if (this.isAlive() && !this.isActivating() && !this.isShooting() && !this.isBelching()) {
                if (this.isMeleeAttacking()) {
                    ++this.attackTick;
                }
            }
            if (this.summonTick > 0) {
                --this.summonTick;
            }
            if (this.summonCool > 0) {
                --this.summonCool;
            }
            if (this.belchCool > 0){
                --this.belchCool;
            }
            if (!this.inventory.isEmpty()){
                this.level.broadcastEntityEvent(this, (byte) 19);
            } else {
                this.level.broadcastEntityEvent(this, (byte) 24);
            }
            if (this.isSummoning()) {
                if (this.level instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 5; ++i) {
                        double d0 = serverLevel.random.nextGaussian() * 0.02D;
                        double d1 = serverLevel.random.nextGaussian() * 0.02D;
                        double d2 = serverLevel.random.nextGaussian() * 0.02D;
                        serverLevel.sendParticles(ModParticleTypes.WRAITH.get(), this.getRandomX(0.5D), this.getEyeY() - serverLevel.random.nextInt(2), this.getRandomZ(0.5D), 0, d0, d1, d2, 0.5F);
                    }
                    if (!this.level.getBlockState(this.blockPosition().below()).isAir()) {
                        for (int j = 0; j < 4; ++j) {
                            double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth() * 2.0D;
                            double d2 = this.blockPosition().below().getY() + 0.5F;
                            double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth() * 2.0D;
                            serverLevel.sendParticles(ModParticleTypes.WRAITH_BURST.get(), d1, d2, d3, 0, 0.0D, 0.0D, 0.0D, 0.5F);
                        }
                    }
                }
                if (this.summonTick == MathHelper.secondsToTicks(SUMMON_SECONDS_TIME - 1)){
                    this.playSound(ModSounds.GRAVE_GOLEM_BLAST.get());
                }
                if (this.summonTick <= (MathHelper.secondsToTicks(SUMMON_SECONDS_TIME - 2)) && this.summonCount != 0) {
                    for (int i = 0; i < 6; ++i){
                        BlockPos blockPos = this.blockPosition();
                        blockPos = blockPos.offset(-8 + this.level.random.nextInt(16), 0, -8 + this.level.random.nextInt(16));
                        Summoned summoned = new Haunt(ModEntityType.HAUNT.get(), this.level);
                        summoned.setLimitedLife(MathHelper.secondsToTicks(20));
                        SummonCircle summonCircle = new SummonCircle(this.level, blockPos, summoned, true, true, this);
                        summonCircle.noParticles = true;
                        this.level.addFreshEntity(summonCircle);
                    }
                    this.summonCount = 0;
                }
            }
        }
    }

    public void handleEntityEvent(byte pId) {
        if (pId == 4){
            this.stopAnimations();
            this.setShooting(true);
            this.shootAnimationState.start(this.tickCount);
        } else if (pId == 5){
            this.attackTick = 0;
        } else if (pId == 6){
            this.stopAnimations();
            this.attackAnimationState.start(this.tickCount);
        } else if (pId == 7){
            this.stopAnimations();
            this.deathAnimationState.start(this.tickCount);
            this.deathRotation = this.getYRot();
            this.playSound(ModSounds.GRAVE_GOLEM_DEATH.get(), 1.0F, 1.0F);
        } else if (pId == 8){
            this.stopAnimations();
            this.activateAnimationState.start(this.tickCount);
        } else if (pId == 9){
            this.shootAnimationState.stop();
        } else if (pId == 10){
            this.stopAnimations();
            this.summonAnimationState.start(this.tickCount);
            this.summonTick = (int) (MathHelper.secondsToTicks(SUMMON_SECONDS_TIME));
        } else if (pId == 11){
            this.attackAnimationState.stop();
        } else if (pId == 12){
            this.stopAnimations();
            this.setBelching(true);
            this.belchAnimationState.start(this.tickCount);
        } else if (pId == 13){
            this.belchAnimationState.stop();
        } else if (pId == 14){
            this.setShooting(false);
        } else if (pId == 15){
            this.getGlow = 1.0F;
        } else if (pId == 16){
            this.setBelching(false);
        } else if (pId == 17){
            this.setMeleeAttacking(true);
        } else if (pId == 18){
            this.setMeleeAttacking(false);
        } else if (pId == 19) {
            if (!this.hasInventory) {
                this.hasInventory = true;
            }
        } else if (pId == 24) {
            if (this.hasInventory) {
                this.hasInventory = false;
            }
        } else if (pId == 22) {
            this.isSittingDown = MathHelper.secondsToTicks(1);
        } else if (pId == 23) {
            this.isStandingUp = MathHelper.secondsToTicks(1);
        } else {
            super.handleEntityEvent(pId);
        }
    }

    public Vec3 getHorizontalLookAngle() {
        return this.calculateViewVector(0, this.getYRot());
    }

    public Vec3 getHorizontalRightLookAngle(LivingEntity livingEntity) {
        return MobUtil.calculateViewVector(0, livingEntity.getYRot() + 90);
    }

    public double getAttackReachSqr(LivingEntity enemy) {
        return (double)(this.getBbWidth() * 6.0F + enemy.getBbWidth()) + 1.0D;
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr){
        double reach = this.getAttackReachSqr(enemy);
        return distToEnemySqr <= reach || this.getBoundingBox().intersects(enemy.getBoundingBox());
    }

    public boolean targetClose(LivingEntity enemy){
        return targetClose(enemy, this.distanceToSqr(enemy.getX(), enemy.getY(), enemy.getZ()));
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (!this.level.isClientSide && !this.isMeleeAttacking()) {
            this.setMeleeAttacking(true);
            this.level.broadcastEntityEvent(GraveGolem.this, (byte) 17);
        }
        return true;
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0){
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand p_230254_2_) {
        if (!this.level.isClientSide) {
            ItemStack itemstack = pPlayer.getItemInHand(p_230254_2_);
            Item item = itemstack.getItem();
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if ((item == Items.BONE_BLOCK || item == Items.BONE) && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    if (item == Items.BONE_BLOCK){
                        this.heal(this.getMaxHealth() / 4.0F);
                        this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 0.5F);
                    } else {
                        this.heal((this.getMaxHealth() / 4.0F) / 8.0F);
                        this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 0.25F, 0.75F);
                    }
                    if (this.level instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = serverLevel.random.nextGaussian() * 0.02D;
                            double d1 = serverLevel.random.nextGaussian() * 0.02D;
                            double d2 = serverLevel.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ModParticleTypes.TOTEM_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    return InteractionResult.SUCCESS;
                } else if (item instanceof ShovelItem && !this.inventory.isEmpty()){
                    this.playSound(SoundEvents.SHOVEL_FLATTEN, 1.0F, 1.0F);
                    this.dropInventory(pPlayer.blockPosition());
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    public void shootProjectile(LivingEntity p_33317_) {
        Vec3 vector3d = this.getViewVector( 1.0F);
        double x = this.getHorizontalRightLookAngle(this).x * 2;
        double z = this.getHorizontalRightLookAngle(this).z * 2;
        double d1 = p_33317_.getX() - (this.getX() + x);
        double d2 = p_33317_.getY() - this.getY(0.5D);
        double d3 = p_33317_.getZ() - (this.getZ() + z);
        HauntedSkullProjectile soulSkull = new HauntedSkullProjectile(this, d1, d2, d3, this.level);
        soulSkull.setPos(this.getX() + x + (vector3d.x / 2), this.getY(0.75D), this.getZ() + z + (vector3d.z / 2));
        soulSkull.setYRot(this.getYRot());
        soulSkull.setXRot(this.getXRot());
        soulSkull.setUpgraded(true);
        this.level.addFreshEntity(soulSkull);
        this.playSound(ModSounds.GRAVE_GOLEM_BLAST.get(), 1.0F, 1.0F);
    }

    class AttackGoal extends MeleeAttackGoal {
        private final double moveSpeed;
        private int delayCounter;

        public AttackGoal(double moveSpeed) {
            super(GraveGolem.this, moveSpeed, true);
            this.moveSpeed = moveSpeed;
        }

        @Override
        public boolean canUse() {
            return GraveGolem.this.getTarget() != null && !GraveGolem.this.isSummoning() && !GraveGolem.this.isShooting() && !GraveGolem.this.isBelching() && GraveGolem.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            GraveGolem.this.setAggressive(true);
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            GraveGolem.this.getNavigation().stop();
            if (GraveGolem.this.getTarget() == null) {
                GraveGolem.this.setAggressive(false);
            }
        }

        @Override
        public void tick() {
            LivingEntity livingentity = GraveGolem.this.getTarget();
            if (livingentity == null) {
                return;
            }

            GraveGolem.this.getLookControl().setLookAt(livingentity, GraveGolem.this.getMaxHeadYRot(), GraveGolem.this.getMaxHeadXRot());

            if (--this.delayCounter <= 0) {
                this.delayCounter = 10;
                GraveGolem.this.getNavigation().moveTo(livingentity, this.moveSpeed);
            }

            this.checkAndPerformAttack(livingentity, GraveGolem.this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (GraveGolem.this.targetClose(enemy, distToEnemySqr) && !GraveGolem.this.isShooting()) {
                GraveGolem.this.doHurtTarget(enemy);
            }
        }

    }

    class MeleeGoal extends Goal {
        private float yRot;

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return GraveGolem.this.getTarget() != null && !GraveGolem.this.isShooting() && !GraveGolem.this.isBelching() && GraveGolem.this.isMeleeAttacking();
        }

        @Override
        public boolean canContinueToUse() {
            return GraveGolem.this.attackTick < MathHelper.secondsToTicks(3.0417F) && !GraveGolem.this.isShooting() && !GraveGolem.this.isBelching();
        }

        @Override
        public void start() {
            GraveGolem.this.setMeleeAttacking(true);
            GraveGolem.this.level.broadcastEntityEvent(GraveGolem.this, (byte) 17);
            if (GraveGolem.this.getTarget() != null){
                MobUtil.instaLook(GraveGolem.this, GraveGolem.this.getTarget());
            }
            this.yRot = GraveGolem.this.yBodyRot;
        }

        @Override
        public void stop() {
            GraveGolem.this.setMeleeAttacking(false);
            GraveGolem.this.level.broadcastEntityEvent(GraveGolem.this, (byte) 18);
        }

        @Override
        public void tick() {
            GraveGolem.this.setYRot(this.yRot);
            GraveGolem.this.yBodyRot = this.yRot;
            GraveGolem.this.getNavigation().stop();
            if (GraveGolem.this.attackTick == 1) {
                GraveGolem.this.playSound(ModSounds.GRAVE_GOLEM_GROWL.get(), 5.0F, 1.0F);
                GraveGolem.this.level.broadcastEntityEvent(GraveGolem.this, (byte) 6);
            }
            if (GraveGolem.this.attackTick == 22) {
                GraveGolem.this.playSound(SoundEvents.GENERIC_EXPLODE, 2.0F, 1.0F);
                AABB aabb = makeAttackRange(GraveGolem.this.getX() + GraveGolem.this.getHorizontalLookAngle().x * 2,
                        GraveGolem.this.getY(),
                        GraveGolem.this.getZ() + GraveGolem.this.getHorizontalLookAngle().z * 2, 9, 7, 9);
                for (LivingEntity target : GraveGolem.this.level.getEntitiesOfClass(LivingEntity.class, aabb)) {
                    if (target != GraveGolem.this && !target.isAlliedTo(GraveGolem.this) && !GraveGolem.this.isAlliedTo(target)) {
                        this.hurtTarget(target);
                    }
                }
                if (GraveGolem.this.level instanceof ServerLevel serverLevel){
                    serverLevel.sendParticles(new ShockwaveParticleOption(), GraveGolem.this.getX() + GraveGolem.this.getHorizontalLookAngle().x * 2, GraveGolem.this.getY() + 0.25D, GraveGolem.this.getZ() + GraveGolem.this.getHorizontalLookAngle().z * 2, 0, 0.0D, 0.0D, 0.0D, 0);
                }
            }
        }

        public void hurtTarget(Entity target) {
            float f = (float)GraveGolem.this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float f1 = (float)GraveGolem.this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
            if (target instanceof LivingEntity livingEntity){
                f += (livingEntity.getMaxHealth() * 0.08F);
            }

            DamageSource damageSource = GraveGolem.this.getTrueOwner() != null ? ModDamageSource.summonAttack(GraveGolem.this, GraveGolem.this.getTrueOwner()) : GraveGolem.this.damageSources().mobAttack(GraveGolem.this);
            boolean flag = target.hurt(damageSource, f);
            if (flag) {
                if (f1 > 0.0F && target instanceof LivingEntity livingEntity) {
                    if (livingEntity.getBoundingBox().getSize() > GraveGolem.this.getBoundingBox().getSize()){
                        livingEntity.knockback((double)(f1 * 0.5F), (double) Mth.sin(GraveGolem.this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(GraveGolem.this.getYRot() * ((float)Math.PI / 180F))));
                    } else {
                        MobUtil.forcefulKnockBack(livingEntity, (double)(f1 * 0.5F), (double)Mth.sin(GraveGolem.this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(GraveGolem.this.getYRot() * ((float)Math.PI / 180F))), 0.5D);
                    }
                    GraveGolem.this.setDeltaMovement(GraveGolem.this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                }

                GraveGolem.this.doEnchantDamageEffects(GraveGolem.this, target);
                GraveGolem.this.setLastHurtMob(target);
            }
        }

        /**
         * Based of @Crimson_Steve codes.
         */
        public static AABB makeAttackRange(double x, double y, double z, double sizeX, double sizeY, double sizeZ) {
            return new AABB(x - (sizeX / 2.0D), y - (sizeY / 2.0D), z - (sizeZ / 2.0D), x + (sizeX / 2.0D), y + (sizeY / 2.0D), z + (sizeZ / 2.0D));
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public static class BelchGoal extends Goal{
        private final GraveGolem mob;
        private int attackTime = 0;
        private int seeTime;

        public BelchGoal(GraveGolem mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (this.mob.belchCool <= 0 && livingentity != null && livingentity.isAlive() && !this.mob.targetClose(livingentity) && this.mob.distanceTo(livingentity) <= 8.0D) {
                return !this.mob.isMeleeAttacking() && !this.mob.isShooting() && !this.mob.isSummoning();
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.canUse() || (!this.mob.isMeleeAttacking() && !this.mob.isSummoning() && !this.mob.isShooting() && this.attackTime > 0 && this.mob.belchCool <= 0);
        }

        public void stop() {
            this.mob.setBelching(false);
            this.mob.level.broadcastEntityEvent(this.mob, (byte) 13);
            this.seeTime = 0;
            this.attackTime = 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (!this.mob.isMeleeAttacking() && !this.mob.isSummoning()) {
                if (target != null){
                    double distance = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
                    boolean flag = this.mob.getSensing().hasLineOfSight(target);
                    if (flag) {
                        ++this.seeTime;
                    } else {
                        this.seeTime = 0;
                    }

                    if (distance <= Mth.square(32) && this.seeTime >= 5) {
                        this.mob.getNavigation().stop();
                    } else {
                        this.mob.getNavigation().moveTo(target, 1.0D);
                    }

                    MobUtil.instaLook(this.mob, target);
                }
                ++this.attackTime;
                if (this.attackTime == 1) {
                    this.mob.setBelching(true);
                    this.mob.level.broadcastEntityEvent(this.mob, (byte) 12);
                    this.mob.playSound(ModSounds.GRAVE_GOLEM_GROWL.get());
                } else if (this.attackTime == 17) {
                    Vec3 vec3 = this.mob.position().add(this.mob.getLookAngle().scale(20));
                    this.mob.level.broadcastEntityEvent(this.mob, (byte) 15);
                    for (int i = 0; i < 8; i++) {
                        SoulBomb snowball = new SoulBomb(this.mob, this.mob.level);
                        double d0 = vec3.y - 2.5D;
                        double d1 = vec3.x - this.mob.getX();
                        double d2 = d0 - snowball.getY();
                        double d3 = vec3.z - this.mob.getZ();
                        double d4 = Math.sqrt(d1 * d1 + d3 * d3) * (double) 0.2F;
                        snowball.moveTo(this.mob.getX(), this.mob.getY() + 4.5D, this.mob.getZ());
                        snowball.shoot(d1, d2 + d4, d3, 1.0F, 30);
                        this.mob.level.addFreshEntity(snowball);
                    }
                    this.mob.playSound(ModSounds.GRAVE_GOLEM_BLAST.get());
                } else if (this.attackTime >= 37) {
                    this.mob.setBelching(false);
                    this.mob.level.broadcastEntityEvent(this.mob, (byte) 16);
                    this.attackTime = 0;
                    this.mob.belchCool = 100;
                }
            }
        }
    }

    public static class GolemRangedGoal extends Goal{
        private final GraveGolem mob;
        @Nullable
        private LivingEntity target;
        private int attackTime = 0;
        private final double speedModifier;
        private int seeTime;
        private final float attackRadius;
        private final float attackRadiusSqr;

        public GolemRangedGoal(GraveGolem mob, double speed, float attackRadius) {
            this.mob = mob;
            this.speedModifier = speed;
            this.attackRadius = attackRadius;
            this.attackRadiusSqr = attackRadius * attackRadius;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive() && !this.mob.targetClose(livingentity) && this.mob.distanceTo(livingentity) > 8.0D) {
                this.target = livingentity;
                return !this.mob.isMeleeAttacking() && !this.mob.isSummoning() && !this.mob.isBelching();
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.canUse() || (!this.mob.isMeleeAttacking() && !this.mob.isSummoning() && !this.mob.isBelching() && this.target != null && this.mob.distanceTo(this.target) > 8.0D && !this.mob.targetClose(this.target) && this.target.isAlive() && !this.mob.getNavigation().isDone());
        }

        public void stop() {
            this.mob.setShooting(false);
            this.mob.level.broadcastEntityEvent(this.mob, (byte) 9);
            this.target = null;
            this.seeTime = 0;
            this.attackTime = 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.target != null && !this.mob.isMeleeAttacking() && !this.mob.isSummoning()) {
                double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
                boolean flag = this.mob.getSensing().hasLineOfSight(this.target);
                if (flag) {
                    ++this.seeTime;
                } else {
                    this.seeTime = 0;
                }

                if (d0 <= (double) this.attackRadiusSqr && this.seeTime >= 5) {
                    this.mob.getNavigation().stop();
                } else {
                    this.mob.getNavigation().moveTo(this.target, this.speedModifier);
                }

                MobUtil.instaLook(this.mob, this.target);
                ++this.attackTime;
                if (this.attackTime == 1) {
                    this.mob.setShooting(true);
                    this.mob.level.broadcastEntityEvent(this.mob, (byte) 4);
                } else if (this.attackTime == 10) {
                    if (!flag) {
                        return;
                    }

                    this.mob.level.broadcastEntityEvent(this.mob, (byte) 15);
                    this.mob.shootProjectile(this.target);
                } else if (this.attackTime >= 23) {
                    this.mob.setShooting(false);
                    this.mob.level.broadcastEntityEvent(this.mob, (byte) 14);
                    this.attackTime = 0;
                }
            }
        }
    }

    public class SummonGoal extends Goal{

        @Override
        public boolean canUse() {
            LivingEntity livingentity = GraveGolem.this.getTarget();
            int i = GraveGolem.this.level.getEntitiesOfClass(Haunt.class, GraveGolem.this.getBoundingBox().inflate(32)).size();
            if (livingentity != null && livingentity.isAlive()) {
                return GraveGolem.this.summonCool <= 0 && i < 3 && !GraveGolem.this.isShooting() && !GraveGolem.this.isBelching() && GraveGolem.this.onGround() && GraveGolem.this.distanceTo(livingentity) < 16;
            } else {
                return false;
            }
        }

        @Override
        public void start() {
            super.start();
            GraveGolem.this.playSound(ModSounds.GRAVE_GOLEM_ARM.get(), GraveGolem.this.getSoundVolume(), GraveGolem.this.getVoicePitch());
            GraveGolem.this.level.broadcastEntityEvent(GraveGolem.this, (byte) 10);
            GraveGolem.this.summonTick = (int) (MathHelper.secondsToTicks(SUMMON_SECONDS_TIME));
            GraveGolem.this.summonCool = MathHelper.secondsToTicks(20);
            GraveGolem.this.summonCount = 1;
        }
    }
}
