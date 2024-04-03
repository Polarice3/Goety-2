package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.client.particles.FoggyCloudParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.ai.LookAtTargelGoal;
import com.Polarice3.Goety.common.entities.neutral.AbstractMonolith;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.EntangleVines;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class Whisperer extends Summoned{
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(Whisperer.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Whisperer.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> DATA_WAVE_CONVERSION_ID = SynchedEntityData.defineId(Whisperer.class, EntityDataSerializers.BOOLEAN);
    public static String IDLE = "idle";
    public static String WALK = "walk";
    public static String ATTACK = "attack";
    public static String SUMMON = "summon";
    public static String SUMMON_POISON = "summonPoison";
    public static String SUMMON_THORNS = "summonThorns";
    public int attackTick;
    public int summonTick = 0;
    public int summonCool = 0;
    private int inWaterTime;
    private int conversionTime;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState summonAnimationState = new AnimationState();
    public AnimationState summonPoisonAnimationState = new AnimationState();
    public AnimationState summonThornsAnimationState = new AnimationState();

    public Whisperer(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this){
            @Override
            public boolean canUse() {
                return super.canUse() && !(Whisperer.this.getNavigation() instanceof WaterBoundPathNavigation);
            }
        });
        this.goalSelector.addGoal(0, new AttackGoal(this));
        this.goalSelector.addGoal(1, new SummonGoal(this));
        this.goalSelector.addGoal(1, AvoidTargetGoal.AvoidRadiusGoal.newGoal(this, 4.0F, 8, 1.0F, 1.2F));
        this.goalSelector.addGoal(2, new LookAtTargelGoal(this, 15.0F));
        this.goalSelector.addGoal(8, new WanderGoal(this, 1.0D){
            @Override
            public boolean canUse() {
                return super.canUse() && Whisperer.this.getTarget() == null;
            }
        });
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.WhispererHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.WhispererDamage.get())
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 15.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.WhispererHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.WhispererDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(ANIM_STATE, 0);
        this.getEntityData().define(DATA_WAVE_CONVERSION_ID, false);
    }

    protected void dropFromLootTable(DamageSource p_21021_, boolean p_21022_) {
        if (!this.limitedLifespan){
            super.dropFromLootTable(p_21021_, p_21022_);
        }
    }

    public boolean isUnderWaterConverting() {
        return this.getEntityData().get(DATA_WAVE_CONVERSION_ID);
    }

    private void startUnderWaterConversion(int p_204704_1_) {
        this.conversionTime = p_204704_1_;
        this.getEntityData().set(DATA_WAVE_CONVERSION_ID, true);
    }

    protected void doUnderWaterConversion() {
        this.convertToWave(ModEntityType.WAVEWHISPERER.get());
        if (!this.isSilent()) {
            this.level.levelEvent((Player) null, 1040, this.blockPosition(), 0);
        }

    }

    protected void convertToWave(EntityType<? extends Wavewhisperer> p_234341_1_) {
        Wavewhisperer wavewhisperer = this.convertTo(p_234341_1_, true);
        if (wavewhisperer != null) {
            if (this.getTrueOwner() != null) {
                wavewhisperer.setTrueOwner(this.getTrueOwner());
            }
            net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, wavewhisperer);
        }

    }

    @Override
    public MobType getMobType() {
        return ModMobType.NATURAL;
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, IDLE)){
            return 1;
        } else if (Objects.equals(animation, WALK)){
            return 2;
        } else if (Objects.equals(animation, ATTACK)){
            return 3;
        } else if (Objects.equals(animation, SUMMON)){
            return 4;
        } else if (Objects.equals(animation, SUMMON_POISON)){
            return 5;
        } else if (Objects.equals(animation, SUMMON_THORNS)){
            return 6;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.walkAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.summonAnimationState);
        animationStates.add(this.summonPoisonAnimationState);
        animationStates.add(this.summonThornsAnimationState);
        return animationStates;
    }

    public void stopAllAnimations(){
        for (AnimationState animationState : this.getAllAnimations()){
            animationState.stop();
        }
    }

    public void stopMostAnimation(AnimationState exception){
        for (AnimationState state : this.getAllAnimations()){
            if (state != exception){
                state.stop();
            }
        }
    }

    public int getCurrentAnimation(){
        return this.entityData.get(ANIM_STATE);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor)) {
            if (this.level.isClientSide){
                switch (this.entityData.get(ANIM_STATE)){
                    case 0:
                        break;
                    case 1:
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 2:
                        this.walkAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.walkAnimationState);
                        break;
                    case 3:
                        this.attackAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                    case 4:
                        this.summonAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.summonAnimationState);
                        break;
                    case 5:
                        this.summonPoisonAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.summonPoisonAnimationState);
                        break;
                    case 6:
                        this.summonThornsAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.summonThornsAnimationState);
                        break;
                }
            }
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.WHISPERER_AMBIENT.get();
    }

    @Override
    public void playAmbientSound() {
        if (this.getCurrentAnimation() == this.getAnimationState(IDLE)) {
            super.playAmbientSound();
        }
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.WHISPERER_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.WHISPERER_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(ModSounds.WHISPERER_STEP.get(), 0.15F, 1.0F);
    }

    protected SoundEvent getAttackSound(){
        return ModSounds.WHISPERER_ATTACK.get();
    }

    protected SoundEvent getSummonSound(){
        return ModSounds.WHISPERER_SUMMON.get();
    }

    protected SoundEvent getSummonPoisonSound(){
        return ModSounds.WHISPERER_SUMMON_POISON.get();
    }

    protected SoundEvent getSummonThornsSound(){
        return ModSounds.WHISPERER_SUMMON_THORNS.get();
    }

    protected EntityType<? extends AbstractMonolith> getVines(){
        return ModEntityType.QUICK_GROWING_VINE.get();
    }

    protected EntityType<? extends AbstractMonolith> getPoison(){
        return ModEntityType.POISON_QUILL_VINE.get();
    }

    private boolean getFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setFlag(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte)(i & 255));
    }

    public boolean isSummonCool(){
        return this.summonCool > 0;
    }

    public boolean isSummoning(){
        return this.getFlag(2);
    }

    public void setSummoning(boolean summon){
        this.setFlag(2, summon);
    }

    public boolean isMeleeAttacking() {
        return this.getFlag(4);
    }

    public void setMeleeAttacking(boolean attack) {
        this.setFlag(4, attack);
        this.attackTick = 0;
        this.level.broadcastEntityEvent(this, (byte) 5);
    }

    public boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;
    }

    public Vec3 getHorizontalLeftLookAngle() {
        return MobUtil.calculateViewVector(0, this.getYRot() - 90);
    }

    public Vec3 getHorizontalRightLookAngle() {
        return MobUtil.calculateViewVector(0, this.getYRot() + 90);
    }

    protected boolean convertsInWater() {
        return true;
    }

    public EntityType<?> getVariant(Level level, BlockPos blockPos){
        EntityType<?> entityType = ModEntityType.WHISPERER.get();
        if (level.isWaterAt(blockPos)) {
            entityType = ModEntityType.WAVEWHISPERER.get();
        }
        return entityType;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive()){
            if (!this.level.isClientSide) {
                if (this.isAlive() && !this.isNoAi()) {
                    if (this.isUnderWaterConverting()) {
                        --this.conversionTime;

                        if (this.conversionTime < 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, ModEntityType.ZOMBIE_SERVANT.get(), (timer) -> this.conversionTime = timer)) {
                            this.doUnderWaterConversion();
                        }
                    } else if (this.convertsInWater()) {
                        if (this.isEyeInFluidType(ForgeMod.WATER_TYPE.get())) {
                            ++this.inWaterTime;
                            if (this.inWaterTime >= 600) {
                                this.startUnderWaterConversion(300);
                            }
                        } else {
                            this.inWaterTime = -1;
                        }
                    }
                }
                if (!this.isMeleeAttacking() && !this.isSummoning()) {
                    if (!this.isMoving()) {
                        this.setAnimationState(IDLE);
                    } else {
                        this.setAnimationState(WALK);
                    }
                }
                if (this.isMeleeAttacking()) {
                    ++this.attackTick;
                    if (this.attackTick >= MathHelper.secondsToTicks(1.3333F)){
                        this.setMeleeAttacking(false);
                        this.level.broadcastEntityEvent(this, (byte) 9);
                    }
                }
                if (this.isSummoning()){
                    ++this.summonTick;
                }
                if (this.summonCool > 0){
                    --this.summonCool;
                }
                if (this.level instanceof ServerLevel serverLevel) {
                    if (this.getCurrentAnimation() == this.getAnimationState(SUMMON)) {
                        if (this.summonTick > 5 && this.summonTick <= 20) {
                            ColorUtil colorUtil = new ColorUtil(0xfcd9f7);
                            float f = this.yBodyRot * ((float) Math.PI / 180F) + Mth.cos((float) this.tickCount * 0.6662F) * 0.25F;
                            float f1 = Mth.cos(f);
                            float f2 = Mth.sin(f);
                            serverLevel.sendParticles(ModParticleTypes.SPELL_SQUARE.get(), this.getX() + (double) f1 * 0.6D, this.getY() + 3.0D, this.getZ() + (double) f2 * 0.6D, 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 0.5F);
                            serverLevel.sendParticles(ModParticleTypes.SPELL_SQUARE.get(), this.getX() - (double) f1 * 0.6D, this.getY() + 3.0D, this.getZ() - (double) f2 * 0.6D, 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 0.5F);
                            serverLevel.sendParticles(new FoggyCloudParticleOption(new ColorUtil(0xcf75af), 0.25F, 6), this.getX(), this.getY() + 2.5D, this.getZ(), 1, 0, 0, 0, 0);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void lifeSpanDamage() {
        this.hurt(this.damageSources().starve(), this.getMaxHealth() * 2);
    }

    protected boolean shouldDropLoot() {
        return !this.limitedLifespan;
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 5){
            this.attackTick = 0;
        } else if (p_21375_ == 6){
            this.setAggressive(true);
        } else if (p_21375_ == 7){
            this.setAggressive(false);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand p_230254_2_) {
        if (!this.level.isClientSide){
            ItemStack itemstack = pPlayer.getItemInHand(p_230254_2_);
            Item item = itemstack.getItem();
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if (item == Items.BONE_MEAL && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    if (this.getType() == ModEntityType.WAVEWHISPERER.get()){
                        this.playSound(ModSounds.WAVEWHISPERER_AMBIENT.get(), 1.0F, 1.25F);
                    } else {
                        this.playSound(ModSounds.WHISPERER_AMBIENT.get(), 1.0F, 1.25F);
                    }
                    this.heal(2.0F);
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        this.level.addParticle(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                    }
                    pPlayer.swing(p_230254_2_);
                    return InteractionResult.CONSUME;
                }
            }
        }
        return super.mobInteract(pPlayer, p_230254_2_);
    }

    static class AttackGoal extends Goal {
        public Whisperer whisperer;
        public LivingEntity target;
        private int ticksUntilNextAttack;

        public AttackGoal(Whisperer whisperer) {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            this.whisperer = whisperer;
            this.target = whisperer.getTarget();
        }

        @Override
        public boolean canUse() {
            this.target = this.whisperer.getTarget();
            return this.target != null
                    && this.target.isAlive()
                    && !this.whisperer.isSummoning()
                    && this.whisperer.hasLineOfSight(this.target)
                    && this.target.distanceTo(this.whisperer) < 2.5F;
        }

        @Override
        public boolean canContinueToUse() {
            return this.whisperer.isMeleeAttacking() && this.target != null;
        }

        @Override
        public void start() {
            this.ticksUntilNextAttack = 0;
            this.whisperer.setMeleeAttacking(true);
            this.whisperer.setAggressive(true);
            this.whisperer.level.broadcastEntityEvent(this.whisperer, (byte) 6);
        }

        @Override
        public void stop() {
            this.whisperer.setMeleeAttacking(false);
            this.whisperer.setAggressive(false);
            this.whisperer.level.broadcastEntityEvent(this.whisperer, (byte) 7);
        }

        @Override
        public void tick() {
            if (this.target == null){
                return;
            }

            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);

            if (this.ticksUntilNextAttack == 0) {
                MobUtil.instaLook(this.whisperer, this.target);

                this.whisperer.getNavigation().stop();

                if (this.whisperer.getCurrentAnimation() != this.whisperer.getAnimationState(ATTACK)) {
                    this.whisperer.setAnimationState(ATTACK);
                    this.whisperer.playSound(this.whisperer.getAttackSound(), this.whisperer.getSoundVolume(), this.whisperer.getVoicePitch());
                }

                if (this.whisperer.attackTick == 14) {
                    if (this.target.distanceTo(this.whisperer) < 2.5F) {
                        this.resetAttackCooldown();
                        this.whisperer.swing(InteractionHand.MAIN_HAND);
                        this.whisperer.doHurtTarget(this.target);
                    }
                }
            }
        }

        protected void resetAttackCooldown() {
            this.ticksUntilNextAttack = this.adjustedTickDelay(10);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

    }

    static class SummonGoal extends Goal{
        public Whisperer whisperer;
        public LivingEntity target;
        public int type = 0;

        public SummonGoal(Whisperer whisperer) {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            this.whisperer = whisperer;
            this.target = whisperer.getTarget();
        }

        @Override
        public boolean canUse() {
            this.target = this.whisperer.getTarget();
            return !this.whisperer.isMeleeAttacking()
                    && !this.whisperer.isSummoning()
                    && !this.whisperer.isSummonCool()
                    && this.target != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.whisperer.isSummoning();
        }

        @Override
        public void stop() {
            super.stop();
            this.type = 0;
            this.whisperer.summonTick = 0;
            this.whisperer.setSummoning(false);
        }

        @Override
        public void tick() {
            super.tick();

            if (this.target != null){
                if (this.target.distanceTo(this.whisperer) > 13.0F){
                    this.whisperer.navigation.moveTo(this.target, 1.0F);
                } else {
                    MobUtil.instaLook(this.whisperer, this.target);
                    this.whisperer.navigation.stop();
                    boolean first = this.whisperer.getRandom().nextBoolean();
                    float chance = this.whisperer.getRandom().nextFloat();
                    int spellTime = 0;
                    int cooldown = MathHelper.secondsToTicks(1);
                    if (!this.whisperer.isSummoning()) {
                        if (first) {
                            if (chance >= 0.5F) {
                                this.type = 1;
                            } else if (chance >= 0.35F) {
                                this.type = 3;
                            } else if (chance >= 0.15F) {
                                this.type = 2;
                            }
                        } else {
                            if (chance >= 0.75F) {
                                this.type = 3;
                            } else if (chance >= 0.15F) {
                                this.type = 1;
                            } else if (chance >= 0.1F) {
                                this.type = 2;
                            }
                        }
                        this.whisperer.setSummoning(true);
                    } else {
                        if (this.type == 1){
                            spellTime = 34;
                            if (this.whisperer.getCurrentAnimation() != this.whisperer.getAnimationState(SUMMON)){
                                this.whisperer.setAnimationState(SUMMON);
                                this.whisperer.playSound(this.whisperer.getSummonSound(), this.whisperer.getSoundVolume(), this.whisperer.getVoicePitch());
                            }
                            if (this.whisperer.summonTick == 20){
                                int random = this.whisperer.random.nextInt(5);
                                Direction direction = Direction.fromYRot(this.target.getYHeadRot());
                                if (random == 0) {
                                    WandUtil.summonMinorSquareTrap(this.whisperer, this.target, this.whisperer.getVines(), direction, 0);
                                } else if (random == 1) {
                                    WandUtil.summonHallTrap(this.whisperer, this.target, this.whisperer.getVines(), 0);
                                } else if (random == 2) {
                                    WandUtil.summonCubeTrap(this.whisperer, this.target, this.whisperer.getVines(), 0);
                                } else if (random == 3) {
                                    WandUtil.summonCircleTrap(this.whisperer, this.target, this.whisperer.getVines(), direction, 0);
                                } else {
                                    WandUtil.summonSurroundTrap(this.whisperer, this.target, this.whisperer.getVines(), 0);
                                }
                            }
                            cooldown = spellTime + MathHelper.secondsToTicks(1);
                        } else if (this.type == 2){
                            spellTime = 63;
                            if (this.whisperer.getCurrentAnimation() != this.whisperer.getAnimationState(SUMMON_POISON)){
                                this.whisperer.setAnimationState(SUMMON_POISON);
                                this.whisperer.playSound(this.whisperer.getSummonPoisonSound(), this.whisperer.getSoundVolume(), this.whisperer.getVoicePitch());
                            }
                            if (this.whisperer.summonTick == 20){
                                int x = (int) (this.whisperer.getHorizontalLeftLookAngle().x * 4);
                                int z = (int) (this.whisperer.getHorizontalLeftLookAngle().z * 4);
                                BlockPos left = new BlockPos(this.whisperer.blockPosition().offset(x, 0, z));
                                WandUtil.summonTurret(this.whisperer, BlockFinder.SummonPosition(this.whisperer, left), this.whisperer.getPoison(), target, 0, 0);
                            }
                            if (this.whisperer.summonTick == 50){
                                int x = (int) (this.whisperer.getHorizontalRightLookAngle().x * 4);
                                int z = (int) (this.whisperer.getHorizontalRightLookAngle().z * 4);
                                BlockPos right = new BlockPos(this.whisperer.blockPosition().offset(x, 0, z));
                                WandUtil.summonTurret(this.whisperer, BlockFinder.SummonPosition(this.whisperer, right), this.whisperer.getPoison(), target, 0, 0);
                            }
                            cooldown = (int) (spellTime + MathHelper.secondsToTicks(1.4F));
                        } else if (this.type == 3){
                            spellTime = 74;
                            if (this.whisperer.getCurrentAnimation() != this.whisperer.getAnimationState(SUMMON_THORNS)){
                                this.whisperer.setAnimationState(SUMMON_THORNS);
                                this.whisperer.playSound(ModSounds.WHISPERER_CAST_THORNS.get(), this.whisperer.getSoundVolume(), this.whisperer.getVoicePitch());
                            }
                            if (this.whisperer.summonTick == 44){
                                this.whisperer.playSound(this.whisperer.getSummonThornsSound(), this.whisperer.getSoundVolume(), this.whisperer.getVoicePitch());
                            }
                            if (this.whisperer.summonTick == 46){
                                EntangleVines entangleVines = new EntangleVines(this.whisperer.level, this.whisperer, this.target);
                                entangleVines.setDamaging(CuriosFinder.hasWildRobe(this.whisperer.getTrueOwner()));
                                this.whisperer.level.addFreshEntity(entangleVines);
                            }
                            cooldown = (int) (spellTime + MathHelper.secondsToTicks(0.1F));
                        }
                        if (this.type < 1 || (spellTime > 0 && this.whisperer.summonTick >= spellTime)){
                            this.whisperer.setSummoning(false);
                            this.whisperer.summonTick = 0;
                            this.whisperer.summonCool = cooldown;
                        }
                    }
                }
            } else {
                this.whisperer.setSummoning(false);
                this.whisperer.summonTick = 0;
            }
        }
    }
}
