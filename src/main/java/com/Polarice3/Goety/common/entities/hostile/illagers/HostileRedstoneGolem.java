package com.Polarice3.Goety.common.entities.hostile.illagers;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.ScatterMine;
import com.Polarice3.Goety.common.entities.util.CameraShake;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class HostileRedstoneGolem extends HostileGolem {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(HostileRedstoneGolem.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(HostileRedstoneGolem.class, EntityDataSerializers.INT);
    public static String IDLE = "idle";
    public static String ATTACK = "attack";
    public static String SUMMON = "summon";
    public static String NOVELTY = "novelty";
    public static String DEATH = "death";
    public static float SUMMON_SECONDS_TIME = 5.15F;
    private int activateTick;
    private int idleTime;
    public int summonTick;
    private int summonCool;
    private int mineCount;
    public int attackTick;
    public int postAttackTick;
    public float getGlow;
    public float glowAmount = 0.03F;
    public int noveltyTick;
    public int deathTime = 0;
    public float deathRotation = 0.0F;
    public boolean isNovelty = false;
    public boolean isPostAttack = false;
    public boolean isFlash = false;
    private final ModServerBossInfo bossInfo;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState noveltyAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState summonAnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();

    public HostileRedstoneGolem(EntityType<? extends HostileGolem> p_37839_, Level p_37840_) {
        super(p_37839_, p_37840_);
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.RED, false, false);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.xpReward = 99;
    }

    public void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SummonMinesGoal());
        this.goalSelector.addGoal(2, new MeleeGoal());
        this.goalSelector.addGoal(5, new AttackGoal(1.2D));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.RedstoneGolemHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.RedstoneGolemArmor.get())
                .add(Attributes.MOVEMENT_SPEED, (double)0.3F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 3.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.RedstoneGolemDamage.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.RedstoneGolemFollowRange.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.RedstoneGolemHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.RedstoneGolemArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.RedstoneGolemDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.RedstoneGolemFollowRange.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(ANIM_STATE, 0);
    }

    protected Component getTypeName() {
        return ModEntityType.REDSTONE_GOLEM.get().getDescription();
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, "idle")){
            return 1;
        } else if (Objects.equals(animation, "attack")){
            return 2;
        } else if (Objects.equals(animation, "summon")){
            return 3;
        } else if (Objects.equals(animation, "novelty")){
            return 4;
        } else if (Objects.equals(animation, "death")){
            return 5;
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

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_219422_) {
        if (ANIM_STATE.equals(p_219422_)) {
            if (this.level.isClientSide){
                switch (this.entityData.get(ANIM_STATE)){
                    case 0:
                        break;
                    case 1:
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 2:
                        this.attackAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                    case 3:
                        this.summonAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.summonAnimationState);
                        break;
                    case 4:
                        this.noveltyAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.noveltyAnimationState);
                        break;
                    case 5:
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
        pCompound.putInt("CoolDown", this.summonCool);
        pCompound.putInt("MineCount", this.mineCount);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ActivateTick")) {
            this.activateTick = pCompound.getInt("ActivateTick");
        }
        if (pCompound.contains("SummonTick")) {
            this.summonTick = pCompound.getInt("SummonTick");
        }
        if (pCompound.contains("CoolDown")){
            this.summonCool = pCompound.getInt("CoolDown");
        }
        if (pCompound.contains("MineCount")){
            this.mineCount = pCompound.getInt("MineCount");
        }
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    public boolean canAnimateMove(){
        return super.canAnimateMove() && this.getCurrentAnimation() == this.getAnimationState(IDLE);
    }

    public boolean isAlliedTo(Entity pEntity) {
        if (pEntity == this) {
            return true;
        } else if (super.isAlliedTo(pEntity)) {
            return true;
        } else if (pEntity instanceof Vex vex && vex.getOwner() != null) {
            return this.isAlliedTo(vex.getOwner());
        } else if (pEntity instanceof LivingEntity && ((LivingEntity)pEntity).getMobType() == MobType.ILLAGER) {
            return this.getTeam() == null && pEntity.getTeam() == null;
        } else {
            return false;
        }
    }

    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setVisible(MainConfig.SpecialBossBar.get());
    }

    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        if (MainConfig.SpecialBossBar.get()) {
            this.bossInfo.addPlayer(pPlayer);
        }
    }

    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossInfo.removePlayer(pPlayer);
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_) {
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.REDSTONE_GOLEM_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.REDSTONE_GOLEM_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.REDSTONE_GOLEM_HURT.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(ModSounds.REDSTONE_GOLEM_STEP.get());
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.REDSTONE_GOLEM_DEATH.get();
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

    protected boolean isImmobile() {
        return super.isImmobile() || this.isSummoning();
    }

    public boolean hasLineOfSight(Entity p_149755_) {
        return this.summonTick <= 0 && super.hasLineOfSight(p_149755_);
    }

    public boolean isSummoning(){
        return this.summonTick > 0;
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.noveltyAnimationState);
        animationStates.add(this.summonAnimationState);
        animationStates.add(this.deathAnimationState);
        return animationStates;
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= 30) {
            this.spawnAnim();
            ItemStack itemStack = new ItemStack(ModBlocks.REDSTONE_GOLEM_SKULL_ITEM.get());
            if (this.level.random.nextFloat() <= 0.11F){
                this.spawnAtLocation(itemStack);
            }
            this.remove(RemovalReason.KILLED);
        }
        this.hurtTime = 1;
        this.setYRot(this.deathRotation);
        this.setYBodyRot(this.deathRotation);
    }

    public void die(DamageSource p_21014_) {
        this.setAnimationState(DEATH);
        this.deathRotation = this.getYRot();
        super.die(p_21014_);
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

    public void tick() {
        super.tick();
        if (this.tickCount % 5 == 0) {
            this.bossInfo.update();
        }
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        if (this.isDeadOrDying()){
            this.stopMostAnimations(this.deathAnimationState);
            this.setYRot(this.deathRotation);
            this.setYBodyRot(this.deathRotation);
        }
        if (this.level.isClientSide()) {
            if (this.isAlive()) {
                if (!this.isSummoning()){
                    this.glow();
                }
                if (this.summonTick > 0) {
                    this.isFlash = this.summonTick < 60 && this.summonTick > 55 || this.summonTick < 52 && this.summonTick > 47 || this.summonTick < 45 && this.summonTick > 40 || this.summonTick < 38 && this.summonTick > 20;
                    --this.summonTick;
                } else {
                    this.isFlash = false;
                }
            }
        }
        if (!this.level.isClientSide){
            if (!this.isDeadOrDying()) {
                if (!this.isMeleeAttacking() && !this.isSummoning()) {
                    if (!this.isPostAttack && !this.isNovelty) {
                        this.setAnimationState(IDLE);
                    }
                }
                if (this.isAlive()) {
                    if (this.isNovelty) {
                        this.jumping = false;
                        this.xxa = 0.0F;
                        this.zza = 0.0F;
                        ++this.noveltyTick;
                        if (this.noveltyTick == 8 || this.noveltyTick == 13 || this.noveltyTick == 18 || this.noveltyTick == 23 || this.noveltyTick == 28 || this.noveltyTick == 33) {
                            this.playSound(ModSounds.REDSTONE_GOLEM_CHEST.get());
                        }
                        if (this.noveltyTick == 42) {
                            this.playSound(ModSounds.REDSTONE_GOLEM_GROWL.get());
                            this.gameEvent(GameEvent.ENTITY_ROAR, this);
                        }
                        if (this.noveltyTick >= 92 || this.getTarget() != null || this.hurtTime > 0) {
                            this.isNovelty = false;
                        }
                    } else {
                        this.noveltyTick = 0;
                    }
                    if (!this.isMeleeAttacking() && !this.isSummoning() && !this.isMoving()) {
                        ++this.idleTime;
                        if (this.level.random.nextFloat() <= 0.05F && this.hurtTime <= 0 && (this.getTarget() == null || this.getTarget().isDeadOrDying()) && !this.isNovelty && this.idleTime >= MathHelper.minutesToTicks(1)) {
                            this.idleTime = 0;
                            this.isNovelty = true;
                            this.setAnimationState(NOVELTY);
                        }
                    } else {
                        this.idleTime = 0;
                        this.isNovelty = false;
                    }
                    if (this.summonTick > 0) {
                        --this.summonTick;
                    }
                    if (this.summonCool > 0) {
                        --this.summonCool;
                    }
                    if (this.isMeleeAttacking()) {
                        ++this.attackTick;
                    }
                    if (this.isPostAttack) {
                        ++this.postAttackTick;
                    }
                    if (this.postAttackTick >= 15) {
                        if (!this.isSummoning()) {
                            this.setAnimationState(IDLE);
                        }
                        this.postAttackTick = 0;
                        this.isPostAttack = false;
                    }
                    if (this.isSummoning()) {
                        if (this.level instanceof ServerLevel serverLevel) {
                            for (int i = 0; i < 5; ++i) {
                                double d0 = serverLevel.random.nextGaussian() * 0.02D;
                                double d1 = serverLevel.random.nextGaussian() * 0.02D;
                                double d2 = serverLevel.random.nextGaussian() * 0.02D;
                                serverLevel.sendParticles(ModParticleTypes.BIG_ELECTRIC.get(), this.getRandomX(0.5D), this.getEyeY() - serverLevel.random.nextInt(5), this.getRandomZ(0.5D), 0, d0, d1, d2, 0.5F);
                            }
                        }
                        if (this.summonTick == MathHelper.secondsToTicks(SUMMON_SECONDS_TIME - 1)) {
                            CameraShake.cameraShake(this.level, this.position(), 10.0F, 0.1F, 0, 20);
                        }
                        if (this.summonTick <= (MathHelper.secondsToTicks(SUMMON_SECONDS_TIME - 1)) && this.mineCount > 0) {
                            int time = (int) (MathHelper.secondsToTicks(SUMMON_SECONDS_TIME - 1) / 14);
                            if (this.tickCount % time == 0 && this.onGround()) {
                                BlockPos blockPos = this.blockPosition();
                                blockPos = blockPos.offset(-8 + this.level.random.nextInt(16), 0, -8 + this.level.random.nextInt(16));
                                BlockPos blockPos2 = this.blockPosition().offset(-8 + this.level.random.nextInt(16), 0, -8 + this.level.random.nextInt(16));
                                Vec3 vec3 = Vec3.atBottomCenterOf(blockPos);
                                Vec3 vec32 = Vec3.atBottomCenterOf(blockPos2);
                                ScatterMine scatterMine = new ScatterMine(this.level, this, vec3);
                                if (!this.level.getEntitiesOfClass(ScatterMine.class, new AABB(blockPos)).isEmpty()) {
                                    scatterMine.setPos(vec32.x(), vec32.y(), vec32.z());
                                }
                                if (this.level.addFreshEntity(scatterMine)) {
                                    if (this.level.random.nextBoolean()) {
                                        scatterMine.playSound(ModSounds.REDSTONE_GOLEM_MINE_SPAWN.get());
                                    }
                                    --this.mineCount;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void glow() {
        this.getGlow = Mth.clamp(this.getGlow + this.glowAmount, 0, 1);
        if (this.getGlow == 0 || this.getGlow == 1) {
            this.glowAmount *= -1;
        }
    }

    public void handleEntityEvent(byte pId) {
        if (pId == 4){
            this.playSound(ModSounds.REDSTONE_GOLEM_SUMMON.get());
            this.summonTick = (int) (MathHelper.secondsToTicks(SUMMON_SECONDS_TIME) + 5);
            this.getGlow = 1.0F;
        } else if (pId == 5){
            this.attackTick = 0;
        } else if (pId == 6){
            this.playSound(ModSounds.REDSTONE_GOLEM_ATTACK.get());
        } else if (pId == 7){
            this.deathRotation = this.getYRot();
            this.playSound(ModSounds.REDSTONE_GOLEM_DEATH.get(), 1.0F, 1.0F);
        } else {
            super.handleEntityEvent(pId);
        }
    }

    public double getAttackReachSqr(LivingEntity enemy) {
        return (double)(this.getBbWidth() * 6.0F + enemy.getBbWidth()) + 1.0D;
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr){
        double reach = this.getAttackReachSqr(enemy);
        return distToEnemySqr <= reach || HostileRedstoneGolem.this.getBoundingBox().intersects(enemy.getBoundingBox());
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (!this.level.isClientSide && !this.isMeleeAttacking()) {
            this.playSound(ModSounds.REDSTONE_GOLEM_PRE_ATTACK.get(), 1.5F, 1.0F);
            this.setMeleeAttacking(true);
        }
        return true;
    }

    /**
     * Attack and Melee Goals based of @Infamous-Misadventures codes to make animation sync up: <a href="https://github.com/Infamous-Misadventures/Dungeons-Mobs/blob/1.19/src/main/java/com/infamous/dungeons_mobs/entities/redstone/RedstoneGolemEntity.java#L92">...</a>
     */
    class AttackGoal extends MeleeAttackGoal {
        private final double moveSpeed;
        private int delayCounter;

        public AttackGoal(double moveSpeed) {
            super(HostileRedstoneGolem.this, moveSpeed, true);
            this.moveSpeed = moveSpeed;
        }

        @Override
        public boolean canUse() {
            return HostileRedstoneGolem.this.getTarget() != null && HostileRedstoneGolem.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            HostileRedstoneGolem.this.setAggressive(true);
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            HostileRedstoneGolem.this.getNavigation().stop();
            if (HostileRedstoneGolem.this.getTarget() == null) {
                HostileRedstoneGolem.this.setAggressive(false);
            }
        }

        @Override
        public void tick() {
            LivingEntity livingentity = HostileRedstoneGolem.this.getTarget();
            if (livingentity == null) {
                return;
            }

            HostileRedstoneGolem.this.getLookControl().setLookAt(livingentity, HostileRedstoneGolem.this.getMaxHeadYRot(), HostileRedstoneGolem.this.getMaxHeadXRot());

            if (--this.delayCounter <= 0) {
                this.delayCounter = 10;
                HostileRedstoneGolem.this.getNavigation().moveTo(livingentity, this.moveSpeed);
            }

            this.checkAndPerformAttack(livingentity, HostileRedstoneGolem.this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (HostileRedstoneGolem.this.targetClose(enemy, distToEnemySqr) && !HostileRedstoneGolem.this.isPostAttack) {
                HostileRedstoneGolem.this.doHurtTarget(enemy);
            }
        }

    }

    class MeleeGoal extends Goal {
        public MeleeGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return HostileRedstoneGolem.this.getTarget() != null && HostileRedstoneGolem.this.isMeleeAttacking();
        }

        /**
         * Is short so that the Redstone Golem can immediately move after attacking, else it would stay in place until animation is finished.
         */
        @Override
        public boolean canContinueToUse() {
            return HostileRedstoneGolem.this.attackTick < 5;
        }

        @Override
        public void start() {
            HostileRedstoneGolem.this.setMeleeAttacking(true);
        }

        /**
         * Using Post Attack Tick to ensure that attack animation is stopped properly.
         */
        @Override
        public void stop() {
            HostileRedstoneGolem.this.setMeleeAttacking(false);
            HostileRedstoneGolem.this.isPostAttack = true;
        }

        @Override
        public void tick() {
            if (HostileRedstoneGolem.this.getTarget() != null && HostileRedstoneGolem.this.getTarget().isAlive()) {
                LivingEntity livingentity = HostileRedstoneGolem.this.getTarget();
                double d0 = HostileRedstoneGolem.this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                HostileRedstoneGolem.this.getLookControl().setLookAt(livingentity, HostileRedstoneGolem.this.getMaxHeadYRot(), HostileRedstoneGolem.this.getMaxHeadXRot());
                if (HostileRedstoneGolem.this.targetClose(livingentity, d0)){
                    HostileRedstoneGolem.this.setYBodyRot(HostileRedstoneGolem.this.getYHeadRot());
                    if (HostileRedstoneGolem.this.attackTick == 1) {
                        HostileRedstoneGolem.this.playSound(ModSounds.REDSTONE_GOLEM_ATTACK.get());
                        HostileRedstoneGolem.this.setAnimationState(ATTACK);
                    }
                    if (HostileRedstoneGolem.this.attackTick == 3) {
                        if (HostileRedstoneGolem.this.targetClose(livingentity, d0)) {
                            this.hurtTarget(livingentity);
                            this.massiveSweep(HostileRedstoneGolem.this, livingentity, 3.0D, 100.0D);
                        }
                    }
                }
            }
        }

        public void hurtTarget(Entity target) {
            float f = (float)HostileRedstoneGolem.this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float f1 = (float)HostileRedstoneGolem.this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
            boolean flag = target.hurt(HostileRedstoneGolem.this.damageSources().mobAttack(HostileRedstoneGolem.this), f);
            if (flag) {
                if (f1 > 0.0F && target instanceof LivingEntity livingEntity) {
                    if (livingEntity.getBoundingBox().getSize() > HostileRedstoneGolem.this.getBoundingBox().getSize()){
                        livingEntity.knockback((double)(f1 * 0.5F), (double)Mth.sin(HostileRedstoneGolem.this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(HostileRedstoneGolem.this.getYRot() * ((float)Math.PI / 180F))));
                    } else {
                        MobUtil.forcefulKnockBack(livingEntity, (double)(f1 * 0.5F), (double)Mth.sin(HostileRedstoneGolem.this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(HostileRedstoneGolem.this.getYRot() * ((float)Math.PI / 180F))), 0.5D);
                    }
                    HostileRedstoneGolem.this.setDeltaMovement(HostileRedstoneGolem.this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                }

                HostileRedstoneGolem.this.doEnchantDamageEffects(HostileRedstoneGolem.this, target);
                HostileRedstoneGolem.this.setLastHurtMob(target);
            }
        }

        public void massiveSweep(LivingEntity source, LivingEntity exempt, double range, double arc){
            List<LivingEntity> hits = MobUtil.getAttackableLivingEntitiesNearby(source, range, 1.0F, range, range);
            for (LivingEntity target : hits) {
                float targetAngle = (float) ((Math.atan2(target.getZ() - source.getZ(), target.getX() - source.getX()) * (180 / Math.PI) - 90) % 360);
                float attackAngle = source.yBodyRot % 360;
                if (targetAngle < 0) {
                    targetAngle += 360;
                }
                if (attackAngle < 0) {
                    attackAngle += 360;
                }
                float relativeAngle = targetAngle - attackAngle;
                float hitDistance = (float) Math.sqrt((target.getZ() - source.getZ()) * (target.getZ() - source.getZ()) + (target.getX() - source.getX()) * (target.getX() - source.getX())) - target.getBbWidth() / 2f;
                if (target != exempt) {
                    if (hitDistance <= range && (relativeAngle <= arc / 2 && relativeAngle >= -arc / 2) || (relativeAngle >= 360 - arc / 2 || relativeAngle <= -360 + arc / 2)) {
                        this.hurtTarget(target);
                    }
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public class SummonMinesGoal extends Goal{

        @Override
        public boolean canUse() {
            LivingEntity livingentity = HostileRedstoneGolem.this.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                double d0 = HostileRedstoneGolem.this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                return HostileRedstoneGolem.this.summonCool <= 0 && HostileRedstoneGolem.this.onGround() && HostileRedstoneGolem.this.targetClose(livingentity, d0);
            } else {
                return false;
            }
        }

        @Override
        public void start() {
            super.start();
            HostileRedstoneGolem.this.playSound(ModSounds.REDSTONE_GOLEM_SUMMON.get(), HostileRedstoneGolem.this.getSoundVolume(), HostileRedstoneGolem.this.getVoicePitch());
            HostileRedstoneGolem.this.setAnimationState(SUMMON);
            HostileRedstoneGolem.this.summonTick = (int) (MathHelper.secondsToTicks(SUMMON_SECONDS_TIME));
            HostileRedstoneGolem.this.summonCool = (int) MathHelper.secondsToTicks(10 + SUMMON_SECONDS_TIME);
            HostileRedstoneGolem.this.mineCount = 14;
        }
    }
}
