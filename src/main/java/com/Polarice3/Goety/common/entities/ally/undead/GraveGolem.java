package com.Polarice3.Goety.common.entities.ally.undead;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.SmashParticleOption;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.golem.AbstractGolemServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.HauntedSkullProjectile;
import com.Polarice3.Goety.common.entities.util.CameraShake;
import com.Polarice3.Goety.common.entities.util.SummonCircle;
import com.Polarice3.Goety.common.items.block.GraveGolemSkullItem;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GraveGolem extends AbstractGolemServant {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(GraveGolem.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(GraveGolem.class, EntityDataSerializers.INT);
    public static String ACTIVATE = "activate";
    public static String IDLE = "idle";
    public static String ATTACK = "attack";
    public static String SUMMON = "summon";
    public static String TO_SIT = "to_sit";
    public static String TO_STAND = "to_stand";
    public static String SIT = "sit";
    public static String SHOOT = "shoot";
    public static String DEATH = "death";
    public static float SUMMON_SECONDS_TIME = 4.6F;
    public static int SUMMON_COOL = MathHelper.secondsToTicks(20);
    private int activateTick;
    public int attackTick;
    public int summonTick;
    private int summonCool;
    private int summonCount;
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
    public AnimationState summonAnimationState = new AnimationState();
    public AnimationState toSitAnimationState = new AnimationState();
    public AnimationState toStandAnimationState = new AnimationState();
    public AnimationState sitAnimationState = new AnimationState();
    public AnimationState shootAnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();

    public GraveGolem(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SummonGoal());
        this.goalSelector.addGoal(2, new MeleeGoal());
        this.goalSelector.addGoal(3, new GolemRangedGoal(this));
        this.goalSelector.addGoal(5, new AttackGoal(1.0D));
        this.goalSelector.addGoal(8, new WanderGoal<>(this, 1.0D, 10));
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
        this.entityData.define(ANIM_STATE, 0);
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

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, ACTIVATE)){
            return 1;
        } else if (Objects.equals(animation, IDLE)){
            return 2;
        } else if (Objects.equals(animation, ATTACK)){
            return 3;
        } else if (Objects.equals(animation, SUMMON)){
            return 4;
        } else if (Objects.equals(animation, TO_SIT)){
            return 5;
        } else if (Objects.equals(animation, TO_STAND)){
            return 6;
        } else if (Objects.equals(animation, SIT)){
            return 7;
        } else if (Objects.equals(animation, SHOOT)){
            return 8;
        } else if (Objects.equals(animation, DEATH)){
            return 9;
        } else {
            return 0;
        }
    }

    public void stopMostAnimation(AnimationState exception){
        for (AnimationState state : this.getAnimations()){
            if (state != exception){
                state.stop();
            }
        }
    }

    public int getCurrentAnimation(){
        return this.entityData.get(ANIM_STATE);
    }

    public boolean isCurrentAnimation(String animation) {
        return this.getCurrentAnimation() == this.getAnimationState(animation);
    }

    public void resetToIdle() {
        this.setAnimationState(IDLE);
        if (this.isStaying()) {
            this.isSittingDown = MathHelper.secondsToTicks(1);
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_219422_) {
        if (ANIM_STATE.equals(p_219422_)) {
            if (this.level.isClientSide){
                switch (this.entityData.get(ANIM_STATE)){
                    case 0:
                        break;
                    case 1:
                        this.activateAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.activateAnimationState);
                        break;
                    case 2:
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 3:
                        this.attackAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                    case 4:
                        this.stopMostAnimation(this.summonAnimationState);
                        this.summonAnimationState.start(this.tickCount);
                        break;
                    case 5:
                        this.stopMostAnimation(this.toSitAnimationState);
                        this.toSitAnimationState.startIfStopped(this.tickCount);
                        break;
                    case 6:
                        this.stopMostAnimation(this.toStandAnimationState);
                        this.toStandAnimationState.startIfStopped(this.tickCount);
                        break;
                    case 7:
                        this.stopMostAnimation(this.sitAnimationState);
                        break;
                    case 8:
                        this.shootAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.shootAnimationState);
                        break;
                    case 9:
                        this.deathAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.deathAnimationState);
                        break;
                }
            }
        }

        super.onSyncedDataUpdated(p_219422_);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("ActivateTick", this.activateTick);
        pCompound.putInt("SummonTick", this.summonTick);
        pCompound.putInt("SummonCount", this.summonCount);

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

    @Override
    public void summonParticles(ServerLevel pLevel, MobSpawnType pReason) {
    }

    public boolean canAnimateMove(){
        return super.canAnimateMove() && !this.isMeleeAttacking() && !this.isShooting();
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
    }

    public void setShooting(boolean shooting){
        this.setGolemFlags(2, shooting);
    }

    public boolean isShooting(){
        return this.getGolemFlag(2);
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.activateAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.summonAnimationState);
        animationStates.add(this.toSitAnimationState);
        animationStates.add(this.toStandAnimationState);
        animationStates.add(this.sitAnimationState);
        animationStates.add(this.shootAnimationState);
        animationStates.add(this.deathAnimationState);
        return animationStates;
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= MathHelper.secondsToTicks(5)) {
            this.spawnAnim();
            ItemStack itemStack = new ItemStack(ModBlocks.GRAVE_GOLEM_SKULL_ITEM.get());
            if (this.getTrueOwner() != null){
                GraveGolemSkullItem.setOwner(this.getTrueOwner(), itemStack);
                if (this.getCustomName() != null){
                    GraveGolemSkullItem.setCustomName(this.getCustomName().getString(), itemStack);
                }
                ItemEntity itemEntity = this.spawnAtLocation(itemStack);
                if (itemEntity != null){
                    itemEntity.setExtendedLifetime();
                }
            } else if (this.level.getRandom().nextFloat() <= 0.11F){
                this.spawnAtLocation(itemStack);
            }
            this.dropInventory();
            this.remove(RemovalReason.KILLED);
        }
        this.hurtTime = 1;
        this.setYRot(this.deathRotation);
        this.setYBodyRot(this.deathRotation);
    }

    @Override
    public void die(DamageSource p_21014_) {
        this.setAnimationState(DEATH);
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

    private boolean isActivating() {
        return this.hasPose(Pose.EMERGING);
    }

    public void tick() {
        super.tick();
        if (this.isDeadOrDying()){
            this.setYRot(this.deathRotation);
            this.setYBodyRot(this.deathRotation);
        }
        if (this.hasPose(Pose.EMERGING)){
            ++this.activateTick;
            this.setAnimationState(ACTIVATE);
            if (this.activateTick == 1){
                this.playSound(ModSounds.GRAVE_GOLEM_AWAKEN.get(), 2.0F, 1.0F);
            }
            if (this.activateTick > 20) {
                if (this.level.isClientSide()) {
                    this.getGlow = 1.0F;
                }
            }
            if (this.activateTick > MathHelper.secondsToTicks(3.25F)){
                this.setPose(Pose.STANDING);
            }
        }
        if (this.level.isClientSide()) {
            if (this.isAlive() && !this.isActivating()) {
                this.idleAnimationState.animateWhen(!this.walkAnimation.isMoving() && this.isCurrentAnimation(IDLE), this.tickCount);
                this.sitAnimationState.animateWhen(!this.walkAnimation.isMoving() && this.isCurrentAnimation(SIT), this.tickCount);
                this.glow();
                if (this.isMeleeAttacking()) {
                    this.getGlow = 1.0F;
                }
                if (this.isSummoning()) {
                    this.getGlow = 1.0F;
                }
            }
        }
        if (!this.level.isClientSide){
            if (!this.isDeadOrDying()) {
                if (!this.isActivating() && !this.isMeleeAttacking() && !this.isSummoning() && !this.isShooting()) {
                    if (this.isStaying()) {
                        this.isStandingUp = MathHelper.secondsToTicks(1);
                        if (this.isSittingDown > 0) {
                            --this.isSittingDown;
                            this.setAnimationState(TO_SIT);
                        } else {
                            this.setAnimationState(SIT);
                        }
                    } else {
                        this.isSittingDown = MathHelper.secondsToTicks(1);
                        if (this.isStandingUp > 0) {
                            --this.isStandingUp;
                            this.setAnimationState(TO_STAND);
                        } else {
                            this.setAnimationState(IDLE);
                        }
                    }
                }
            }
            if (this.isAlive() && !this.isActivating() && !this.isSummoning() && !this.isShooting()) {
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
            if (!this.inventory.isEmpty()){
                this.level.broadcastEntityEvent(this, (byte) 19);
            } else {
                this.level.broadcastEntityEvent(this, (byte) 24);
            }
            if (this.isSummoning()) {
                this.level.broadcastEntityEvent(this, (byte) 14);
                this.level.broadcastEntityEvent(this, (byte) 15);
                if (this.level instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 5; ++i) {
                        double d0 = serverLevel.getRandom().nextGaussian() * 0.02D;
                        double d1 = serverLevel.getRandom().nextGaussian() * 0.02D;
                        double d2 = serverLevel.getRandom().nextGaussian() * 0.02D;
                        serverLevel.sendParticles(ModParticleTypes.WRAITH.get(), this.getRandomX(0.5D), this.getEyeY() - serverLevel.getRandom().nextInt(2), this.getRandomZ(0.5D), 0, d0, d1, d2, 0.5F);
                    }
                }
                if (this.summonTick == MathHelper.secondsToTicks(SUMMON_SECONDS_TIME) - 36){
                    this.playSound(ModSounds.GRAVE_GOLEM_ROAR.get(), 2.0F, 1.0F);
                    this.gameEvent(GameEvent.ENTITY_ROAR, this);
                }
                if (this.summonTick <= (MathHelper.secondsToTicks(SUMMON_SECONDS_TIME - 2)) && this.summonCount != 0) {
                    int j = 0;
                    for (int i = 0; i < 32; ++i){
                        if (j < 6) {
                            BlockPos blockPos = this.blockPosition();
                            blockPos = blockPos.offset(-8 + this.level.getRandom().nextInt(16), 0, -8 + this.level.getRandom().nextInt(16));
                            Vec3 vec3 = Vec3.atBottomCenterOf(blockPos);
                            Summoned summoned = new Haunt(ModEntityType.HAUNT.get(), this.level);
                            summoned.setLimitedLife(SUMMON_COOL);
                            if (this.level.noCollision(summoned, summoned.getBoundingBox().move(vec3))) {
                                SummonCircle summonCircle = new SummonCircle(this.level, blockPos, summoned, true, true, this);
                                summonCircle.noParticles = true;
                                if (this.level.addFreshEntity(summonCircle)) {
                                    ++j;
                                }
                            }
                        } else {
                            break;
                        }
                    }
                    this.summonCool = SUMMON_COOL;
                    this.summonCount = 0;
                }
            } else {
                if (this.isCurrentAnimation(SUMMON)){
                    this.resetToIdle();
                }
            }
        }
    }

    public void handleEntityEvent(byte pId) {
        if (pId == 13){
            this.stopMostAnimation(this.shootAnimationState);
            if (!this.shootAnimationState.isStarted()) {
                this.shootAnimationState.start(this.tickCount);
            }
        } else if (pId == 14){
            this.stopMostAnimation(this.summonAnimationState);
            if (!this.summonAnimationState.isStarted()) {
                this.summonAnimationState.start(this.tickCount);
            }
        } else if (pId == 15){
            this.getGlow = 1.0F;
        } else if (pId == 19) {
            if (!this.hasInventory) {
                this.hasInventory = true;
            }
        } else if (pId == 24) {
            if (this.hasInventory) {
                this.hasInventory = false;
            }
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

    public boolean targetClose(LivingEntity enemy){
        return targetClose(enemy, this.distanceToSqr(enemy.getX(), enemy.getY(), enemy.getZ()));
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (!this.level.isClientSide && !this.isMeleeAttacking()) {
            this.setMeleeAttacking(true);
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
                            double d0 = serverLevel.getRandom().nextGaussian() * 0.02D;
                            double d1 = serverLevel.getRandom().nextGaussian() * 0.02D;
                            double d2 = serverLevel.getRandom().nextGaussian() * 0.02D;
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

    public void shootProjectile(@Nullable LivingEntity target) {
        Vec3 vector3d = this.getViewVector( 1.0F);
        double rightOffset = 1.5D;
        double x = this.getHorizontalRightLookAngle(this).x * rightOffset;
        double z = this.getHorizontalRightLookAngle(this).z * rightOffset;
        double forwardOffset = 2.0D;
        Vec3 vec3 = vector3d;
        if (target != null) {
            vec3 = target.position();
        }
        double spawnX = this.getX() + x + (vector3d.x * forwardOffset);
        double spawnY = this.getY(0.65D);
        double spawnZ = this.getZ() + z + (vector3d.z * forwardOffset);
        double d1 = vec3.x() - (this.getX() + x);
        double d2 = vec3.y() - this.getY(0.5D);
        double d3 = vec3.z() - (this.getZ() + z);
        HauntedSkullProjectile soulSkull = new HauntedSkullProjectile(this, d1, d2, d3, this.level);
        soulSkull.setPos(spawnX, spawnY, spawnZ);
        soulSkull.setYRot(this.getYRot());
        soulSkull.setXRot(this.getXRot());
        soulSkull.setDamage(this.getAttributeValue(Attributes.ATTACK_DAMAGE));
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
            return GraveGolem.this.getTarget() != null && !GraveGolem.this.isSummoning() && !GraveGolem.this.isShooting() && GraveGolem.this.getTarget().isAlive();
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

    public static ColorUtil SMASH_COLOR = new ColorUtil(0x2ac9cf);

    class MeleeGoal extends Goal {
        private float yRot;

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return GraveGolem.this.getTarget() != null && !GraveGolem.this.isShooting() && GraveGolem.this.isMeleeAttacking();
        }

        @Override
        public boolean canContinueToUse() {
            return GraveGolem.this.attackTick < MathHelper.secondsToTicks(3) && !GraveGolem.this.isShooting();
        }

        @Override
        public void start() {
            GraveGolem.this.setMeleeAttacking(true);
            if (GraveGolem.this.getTarget() != null){
                MobUtil.instaLook(GraveGolem.this, GraveGolem.this.getTarget());
            }
            this.yRot = GraveGolem.this.yBodyRot;
        }

        @Override
        public void stop() {
            GraveGolem.this.setMeleeAttacking(false);
            GraveGolem.this.resetToIdle();
        }

        @Override
        public void tick() {
            GraveGolem.this.setYRot(this.yRot);
            GraveGolem.this.yBodyRot = this.yRot;
            GraveGolem.this.getNavigation().stop();
            if (GraveGolem.this.attackTick == 1) {
                GraveGolem.this.playSound(ModSounds.GRAVE_GOLEM_GROWL.get(), 5.0F, 1.0F);
                GraveGolem.this.setAnimationState(ATTACK);
            }
            if (GraveGolem.this.attackTick == 24) {
                GraveGolem.this.playSound(ModSounds.REDSTONE_MONSTROSITY_SMASH.get(), 2.0F, 0.2F);
                GraveGolem.this.playSound(SoundEvents.GENERIC_EXPLODE, 0.5F, 0.9F);
                AABB aabb = MobUtil.makeAttackRange(GraveGolem.this.getX() + GraveGolem.this.getHorizontalLookAngle().x * 2,
                        GraveGolem.this.getY(),
                        GraveGolem.this.getZ() + GraveGolem.this.getHorizontalLookAngle().z * 2, 7, 7, 7);
                for (LivingEntity target : GraveGolem.this.level.getEntitiesOfClass(LivingEntity.class, aabb)) {
                    if (target != GraveGolem.this && !target.isAlliedTo(GraveGolem.this) && !GraveGolem.this.isAlliedTo(target)) {
                        this.hurtTarget(target);
                    }
                }
                CameraShake.cameraShake(GraveGolem.this.level, GraveGolem.this.position(), 18.0F, 0.3F, 0, 20);
                if (GraveGolem.this.level instanceof ServerLevel serverLevel){
                    Vec3 vec31 = new Vec3(GraveGolem.this.getX() + GraveGolem.this.getHorizontalLookAngle().x * 2, GraveGolem.this.getY() + 0.25D, GraveGolem.this.getZ() + GraveGolem.this.getHorizontalLookAngle().z * 2);
                    serverLevel.sendParticles(new SmashParticleOption(SMASH_COLOR, 7, 10), vec31.x, vec31.y, vec31.z, 0, 0, 0, 0, 0);
                    serverLevel.sendParticles(new SphereExplodeParticleOption(SMASH_COLOR, 7, 1), vec31.x, vec31.y, vec31.z, 1, 0, 0, 0, 0);
                    BlockPos blockPos = BlockPos.containing(vec31);
                    BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, serverLevel.getBlockState(blockPos));
                    for (int i = 0; i < 8; ++i) {
                        ServerParticleUtil.circularParticles(serverLevel, option, vec31.x, vec31.y, vec31.z, 3.0F);
                    }
                }
            }
        }

        public void hurtTarget(Entity target) {
            float f = (float)GraveGolem.this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float f1 = (float)GraveGolem.this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
            if (target instanceof LivingEntity livingEntity){
                f += (livingEntity.getMaxHealth() * 0.08F);
            }

            boolean flag = target.hurt(GraveGolem.this.getServantAttack(), f);
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

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public static class GolemRangedGoal extends Goal{
        private final GraveGolem mob;
        @Nullable
        private LivingEntity target;
        private int attackTime = 0;

        public GolemRangedGoal(GraveGolem mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null
                    && livingentity.isAlive()
                    && this.mob.hasLineOfSight(livingentity)
                    && !this.mob.targetClose(livingentity)
                    && (this.mob.distanceTo(livingentity) > 8.0D || this.mob.isStaying())) {
                this.target = livingentity;
                return !this.mob.isMeleeAttacking()
                        && !this.mob.isShooting()
                        && !this.mob.isSummoning();
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            if (this.mob.isShooting()) {
                return true;
            }
            if (this.target == null) {
                return false;
            }
            if (!this.target.isAlive()) {
                return false;
            }
            if (!this.mob.hasLineOfSight(this.target)) {
                return false;
            }
            if (this.mob.isMeleeAttacking() || this.mob.isSummoning()) {
                return false;
            }
            return this.mob.distanceTo(this.target) > 8.0D && !this.mob.targetClose(this.target);
        }

        public void stop() {
            this.mob.setShooting(false);
            this.target = null;
            if (this.mob.isCurrentAnimation(SHOOT)) {
                this.mob.resetToIdle();
            }
            this.attackTime = -1;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.target != null) {
                MobUtil.instaLook(this.mob, this.target);
            }
            ++this.attackTime;
            if (this.attackTime == 1) {
                this.mob.setShooting(true);
                this.mob.setAnimationState(SHOOT);
                this.mob.level.broadcastEntityEvent(this.mob, (byte) 13);
            } else if (this.attackTime == 10) {
                this.mob.level.broadcastEntityEvent(this.mob, (byte) 15);
                this.mob.shootProjectile(this.target != null ? this.target : null);
            } else if (this.attackTime >= 23) {
                this.mob.setShooting(false);
                this.mob.resetToIdle();
                this.attackTime = 0;
            }
        }
    }

    public class SummonGoal extends Goal{

        @Override
        public boolean canUse() {
            LivingEntity livingentity = GraveGolem.this.getTarget();
            int i = GraveGolem.this.level.getEntitiesOfClass(Haunt.class, GraveGolem.this.getBoundingBox().inflate(32), haunt -> haunt.getTrueOwner() == GraveGolem.this).size();
            if (livingentity != null && livingentity.isAlive()) {
                return GraveGolem.this.summonCool <= 0
                        && i < 3
                        && !GraveGolem.this.isShooting()
                        && !GraveGolem.this.isCurrentAnimation(SHOOT)
                        && !GraveGolem.this.isMeleeAttacking()
                        && GraveGolem.this.onGround()
                        && GraveGolem.this.distanceTo(livingentity) <= 16;
            } else {
                return false;
            }
        }

        @Override
        public void start() {
            super.start();
            GraveGolem.this.level.broadcastEntityEvent(GraveGolem.this, (byte) 14);
            GraveGolem.this.summonTick = MathHelper.secondsToTicks(SUMMON_SECONDS_TIME);
            GraveGolem.this.setAnimationState(SUMMON);
            GraveGolem.this.playSound(ModSounds.GRAVE_GOLEM_ARM.get(), GraveGolem.this.getSoundVolume(), GraveGolem.this.getVoicePitch());
            GraveGolem.this.summonCount = 1;
            if (GraveGolem.this.getTarget() != null) {
                MobUtil.instaLook(GraveGolem.this, GraveGolem.this.getTarget());
            }
        }
    }
}
