package com.Polarice3.Goety.common.entities.hostile.ender;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.MagicSmokeParticle;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption;
import com.Polarice3.Goety.common.blocks.entities.VoidFrameBlockEntity;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.ender.WatchlingServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.neutral.ender.AbstractEnderling;
import com.Polarice3.Goety.common.entities.util.CameraShake;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.common.network.server.SInstaLookPacket;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Endersent extends AbstractEnderling implements Enemy {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Endersent.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> EYE_TYPE = SynchedEntityData.defineId(Endersent.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(Endersent.class, EntityDataSerializers.INT);
    private static final UUID TELEPORT_ATTACK_MODIFIER_UUID = UUID.fromString("0134e91f-a8c4-4d38-94d6-7201dae924b7");
    private static final AttributeModifier TELEPORT_ATTACK_MODIFIER = new AttributeModifier(TELEPORT_ATTACK_MODIFIER_UUID, "Teleport Attack Bonus", 1.15D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    private static final UUID DE_ATTACK_MODIFIER_UUID = UUID.fromString("b9788c05-ee1b-4c1d-9701-f681c5d89fd1");
    private static final AttributeModifier DE_ATTACK_MODIFIER = new AttributeModifier(DE_ATTACK_MODIFIER_UUID, "Deadly Escape Attack Bonus", 1.85D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    public static String IDLE = "idle";
    public static String ATTACK = "attack";
    public static String SWIPE = "swipe";
    public static String DEADLY_ESCAPE = "deadly_escape";
    public static String TELEPORT_IN = "teleport_in";
    public static String TELEPORT_OUT = "teleport_out";
    public static String DEATH = "death";
    public static int SEARING_EYE = 1;
    public static int HALLOWED_EYE = 2;
    public static int TWISTED_EYE = 3;
    public static int DREADFUL_EYE = 4;
    public boolean shouldTeleportSmash = false;
    @Nullable
    private BlockPos voidFrame;
    public int idleTime = 0;
    public int teleportHideTime = MathHelper.secondsToTicks(5);
    public int deadlyEscapeCool = 0;
    public int projectileHit = 0;
    public int meleeHit = 0;
    public int recentHitTime = 0;
    public int attackTick;
    public int deathTime = 0;
    private final ModServerBossInfo bossInfo;
    public List<MobEffectInstance> eyeEffects = new ArrayList<>();
    public DamageSource deathBlow = this.damageSources().generic();
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState swipeAnimationState = new AnimationState();
    public AnimationState deadlyEscapeAnimationState = new AnimationState();
    public AnimationState teleportInAnimationState = new AnimationState();
    public AnimationState teleportOutAnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();

    public Endersent(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.PURPLE, false, false);
        this.setHostile(true);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new AttackGoal(1.0F));
        this.goalSelector.addGoal(5, new WanderGoal<>(this, 1.0D){
            @Nullable
            @Override
            protected Vec3 getPosition() {
                if (Endersent.this.getVoidFrame() instanceof VoidFrameBlockEntity voidFrameBlock) {
                    Vec3 vec3 = null;
                    int range = 16;

                    for (int i = 0; i < 10; ++i){
                        BlockPos blockPos = voidFrameBlock.getBlockPos()
                                .offset(this.summonedEntity.getRandom().nextIntBetweenInclusive(-range, range),
                                        this.summonedEntity.getRandom().nextIntBetweenInclusive(-range, range),
                                        this.summonedEntity.getRandom().nextIntBetweenInclusive(-range, range));
                        BlockPos blockPos1 = LandRandomPos.movePosUpOutOfSolid(this.summonedEntity, blockPos);
                        if (blockPos1 != null){
                            vec3 = Vec3.atBottomCenterOf(blockPos1);
                            break;
                        }
                    }

                    return vec3;
                }
                return super.getPosition();
            }
        });
    }

    @SuppressWarnings("removal")
    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.EndersentHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.EndersentDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.EndersentArmor.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public void setConfigurableAttributes() {
        if (!this.hasEye()) {
            MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.EndersentHealth.get());
        }
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.EndersentArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.EndersentDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(EYE_TYPE, 0);
        this.entityData.define(ANIM_STATE, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.voidFrame != null) {
            compound.putInt("FrameX", this.voidFrame.getX());
            compound.putInt("FrameY", this.voidFrame.getY());
            compound.putInt("FrameZ", this.voidFrame.getZ());
        }
        compound.putInt("DeadlyEscapeCool", this.deadlyEscapeCool);
        compound.putInt("ProjectileHit", this.projectileHit);
        compound.putInt("MeleeHit", this.meleeHit);
        compound.putInt("RecentlyHit", this.recentHitTime);
        compound.putInt("EyeType", this.getEyeType());
        if (!this.eyeEffects.isEmpty()) {
            ListTag listtag = new ListTag();

            for(MobEffectInstance mobeffectinstance : this.eyeEffects) {
                listtag.add(mobeffectinstance.save(new CompoundTag()));
            }

            compound.put("EyeEffects", listtag);
        }
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("FrameX")) {
            this.voidFrame = new BlockPos(compound.getInt("FrameX"), compound.getInt("FrameY"), compound.getInt("FrameZ"));
        }
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
        if (compound.contains("DeadlyEscapeCool")) {
            this.deadlyEscapeCool = compound.getInt("DeadlyEscapeCool");
        }
        if (compound.contains("ProjectileHit")) {
            this.projectileHit = compound.getInt("ProjectileHit");
        }
        if (compound.contains("MeleeHit")) {
            this.meleeHit = compound.getInt("MeleeHit");
        }
        if (compound.contains("RecentlyHit")) {
            this.recentHitTime = compound.getInt("RecentlyHit");
        }
        if (compound.contains("EyeType")) {
            this.setEyeType(compound.getInt("EyeType"));
        }
        if (compound.contains("EyeEffects", 9)) {
            ListTag listtag = compound.getList("EyeEffects", 10);

            for(int i = 0; i < listtag.size(); ++i) {
                CompoundTag compoundtag = listtag.getCompound(i);
                MobEffectInstance mobeffectinstance = MobEffectInstance.load(compoundtag);
                if (mobeffectinstance != null) {
                    this.eyeEffects.add(mobeffectinstance);
                }
            }
        }
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, IDLE)){
            return 0;
        } else if (Objects.equals(animation, ATTACK)){
            return 1;
        } else if (Objects.equals(animation, SWIPE)){
            return 2;
        } else if (Objects.equals(animation, DEADLY_ESCAPE)){
            return 3;
        } else if (Objects.equals(animation, TELEPORT_IN)){
            return 4;
        } else if (Objects.equals(animation, TELEPORT_OUT)){
            return 5;
        } else if (Objects.equals(animation, DEATH)){
            return 6;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.swipeAnimationState);
        animationStates.add(this.deadlyEscapeAnimationState);
        animationStates.add(this.teleportInAnimationState);
        animationStates.add(this.teleportOutAnimationState);
        animationStates.add(this.deathAnimationState);
        return animationStates;
    }

    public void stopAllAnimations() {
        for (AnimationState state : this.getAnimations()){
            state.stop();
        }
    }

    public void stopMostAnimation(AnimationState exception) {
        for (AnimationState state : this.getAnimations()) {
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

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_219422_) {
        if (ANIM_STATE.equals(p_219422_)) {
            if (this.level.isClientSide){
                switch (this.entityData.get(ANIM_STATE)){
                    case 0:
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 1:
                        this.stopMostAnimation(this.attackAnimationState);
                        this.attackAnimationState.start(this.tickCount);
                        break;
                    case 2:
                        this.stopMostAnimation(this.swipeAnimationState);
                        this.swipeAnimationState.start(this.tickCount);
                        break;
                    case 3:
                        this.stopMostAnimation(this.deadlyEscapeAnimationState);
                        this.deadlyEscapeAnimationState.start(this.tickCount);
                        break;
                    case 4:
                        this.stopMostAnimation(this.teleportInAnimationState);
                        this.teleportInAnimationState.start(this.tickCount);
                        break;
                    case 5:
                        this.stopMostAnimation(this.teleportOutAnimationState);
                        this.teleportOutAnimationState.start(this.tickCount);
                        break;
                    case 6:
                        this.stopMostAnimation(this.deathAnimationState);
                        this.deathAnimationState.start(this.tickCount);
                        break;
                }
            }
        }

        super.onSyncedDataUpdated(p_219422_);
    }

    private boolean getFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setFlags(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte)(i & 255));
    }

    @Nullable
    public BlockPos getVoidFramePos() {
        return this.voidFrame;
    }

    public void setVoidFramePos(@Nullable BlockPos voidFrame) {
        this.voidFrame = voidFrame;
    }

    public boolean isMeleeAttacking() {
        return this.getFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setFlags(1, attacking);
        this.attackTick = 0;
    }

    public boolean isTeleporting() {
        return this.getFlag(2);
    }

    public void setTeleporting(boolean teleporting) {
        this.setFlags(2, teleporting);
    }

    public boolean isDeadlyEscape() {
        return this.getFlag(4);
    }

    public void setDeadlyEscape(boolean escape) {
        this.setFlags(4, escape);
    }

    public int getEyeType() {
        return this.entityData.get(EYE_TYPE);
    }

    public boolean hasEye() {
        return this.getEyeType() > 0;
    }

    public void setEyeType(int eyeType) {
        this.entityData.set(EYE_TYPE, eyeType);
    }

    public List<MobEffectInstance> getEyeEffects() {
        return this.eyeEffects;
    }

    public void setEyeEffects(List<MobEffectInstance> instances) {
        this.eyeEffects = instances;
    }

    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    protected void checkFallDamage(double p_19911_, boolean p_19912_, BlockState p_19913_, BlockPos p_19914_) {
        if (!this.isHiding()) {
            super.checkFallDamage(p_19911_, p_19912_, p_19913_, p_19914_);
        }
    }

    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
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

    public void makeStuckInBlock(BlockState p_33796_, Vec3 p_33797_) {
    }

    protected boolean canRide(Entity p_219462_) {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    public void die(DamageSource cause) {
        if (this.deathTime > 0) {
            super.die(cause);
        }
    }

    protected void tickDeath() {
        ++this.deathTime;
        this.setAnimationState(DEATH);
        if (this.deathTime < MathHelper.secondsToTicks(2)) {
            if (this.level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 16; ++i) {
                    serverLevel.sendParticles(new MagicSmokeParticle.Option(0, 0, this.level.getRandom().nextIntBetweenInclusive(40, 80), 0.25F), this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0, this.level.getRandom().nextBoolean() ? 0.01D : -0.01D, 0.1D, this.level.getRandom().nextBoolean() ? 0.01D : -0.01D, 0.5F);
                }
            }
        }
        this.move(MoverType.SELF, new Vec3(0.0D, 0.0D, 0.0D));
        if (this.deathTime == 1){
            this.die(this.deathBlow);
        }
        if (this.deathTime >= MathHelper.secondsToTicks(4)) {
            this.remove(RemovalReason.KILLED);
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.ENDERSENT_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.ENDERSENT_HURT.get();
    }

    @Override
    public void stepSound() {
        this.playSound(ModSounds.ENDERSENT_STEP.get(), 0.15F, 1.0F);
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ENDERSENT_DEATH.get();
    }

    @Override
    protected float getSoundVolume() {
        return 2.0F;
    }

    @Nullable
    public BlockEntity getVoidFrame(){
        if (this.getVoidFramePos() != null){
            return this.level.getBlockEntity(this.getVoidFramePos());
        }
        return null;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() != null) {
            if (!MobUtil.areAllies(this, source.getEntity())) {
                float threshold = (this.getMaxHealth() * 0.025F);
                if (!ModDamageSource.physicalAttacks(source)) {
                    amount /= 2.0F;
                    if (amount >= threshold) {
                        this.projectileHit += 1 + Mth.floor(amount / threshold);
                    }
                    ++this.projectileHit;
                    if (this.projectileHit >= 7) {
                        this.projectileHit = 0;
                        this.recentHitTime = MathHelper.secondsToTicks(5);
                    }
                } else {
                    ++this.meleeHit;
                    if (this.level.getRandom().nextBoolean() || this.meleeHit >= 2) {
                        if (this.level.getRandom().nextBoolean() || this.meleeHit >= 3) {
                            this.meleeHit = 0;
                            this.recentHitTime = MathHelper.secondsToTicks(4);
                        }
                    }
                }
            }
        }
        return super.hurt(source, amount);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData data = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (this.getEyeType() > 0) {
            AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
            if (health != null) {
                health.setBaseValue(AttributesConfig.EndersentHealth.get() * 1.15D);
                this.setHealth(this.getMaxHealth());
            }
        }
        return data;
    }

    @Override
    protected float getStandingEyeHeight(Pose p_32517_, EntityDimensions p_32518_) {
        if (this.isHiding()){
            return 0.1F;
        } else {
            return super.getStandingEyeHeight(p_32517_, p_32518_) + 0.5F;
        }
    }

    @Override
    public Component getName() {
        if (this.getEyeType() > 0) {
            return Component.translatable("name.goety.endersent." + this.getEyeType());
        }
        return super.getName();
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            if (this.hasEye()) {
                ItemEntity itementity = this.spawnAtLocation(Items.ENDER_EYE);
                if (itementity != null) {
                    itementity.setExtendedLifetime();
                }
                ItemEntity itementity2 = this.spawnAtLocation(ModItems.VOID_SHARD.get());
                if (itementity2 != null) {
                    itementity2.setGlowingTag(true);
                    itementity2.setExtendedLifetime();
                }
            }
        }
    }

    public void idleTick() {
        if (this.isCurrentAnimation(IDLE) && !this.isHiding() && !this.walkAnimation.isMoving()) {
            ++this.idleTime;
            ColorUtil colorUtil1 = new ColorUtil(0x3c3c3e);
            float size = 2.5F;
            if (this.idleTime == 5) {
                BlockPos blockPos = BlockPos.containing(this.getXRight(), this.getY() - 1.0F, this.getZRight());
                CameraShake.cameraShake(this.level, blockPos.getCenter(), 15.0F, 0.1F, 0, 10);
                BlockState blockState = this.level.getBlockState(blockPos);
                BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
                ColorUtil colorUtil = new ColorUtil(this.level.getBlockState(blockPos).getMapColor(this.level, blockPos).col);
                this.playSound(ModSounds.ENDERSENT_AMBIENT_SMASH.get(), this.getSoundVolume(), this.getVoicePitch());
                this.level.addParticle(new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), size, 1), this.getXRight(), BlockFinder.moveDownToGround(this), this.getZRight(), 0.0D, 0.0D, 0.0D);
                ParticleUtil.circularParticles(this.level, ModParticleTypes.BIG_CULT_SPELL.get(), blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ(), colorUtil1.red(), colorUtil1.green(), colorUtil1.blue(), size);
                ParticleUtil.circularParticles(this.level, option, blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ(), 0.0F, 0.0F, 0.0F, size);
            }
            if (this.idleTime == 63) {
                this.playSound(ModSounds.ENDERSENT_AMBIENT_SMASH.get(), this.getSoundVolume(), this.getVoicePitch());
            }
            if (this.idleTime == 67) {
                BlockPos blockPos = BlockPos.containing(this.getXLeft(), this.getY() - 1.0F, this.getZLeft());
                CameraShake.cameraShake(this.level, blockPos.getCenter(), 15.0F, 0.1F, 0, 10);
                BlockState blockState = this.level.getBlockState(blockPos);
                BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
                ColorUtil colorUtil = new ColorUtil(this.level.getBlockState(blockPos).getMapColor(this.level, blockPos).col);
                this.level.addParticle(new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), size, 1), this.getXLeft(), BlockFinder.moveDownToGround(this), this.getZLeft(), 0.0D, 0.0D, 0.0D);
                ParticleUtil.circularParticles(this.level, ModParticleTypes.BIG_CULT_SPELL.get(), blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ(), colorUtil1.red(), colorUtil1.green(), colorUtil1.blue(), size);
                ParticleUtil.circularParticles(this.level, option, blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ(), 0.0F, 0.0F, 0.0F, size);
            }
            if (this.idleTime >= 119) {
                this.idleTime = 0;
            }
        } else {
            this.idleTime = 0;
        }
    }

    public void eyeTypeEffects() {
        //Custom Eye Effects added through nbt overrides hardcoded effects.
        if (!this.getEyeEffects().isEmpty()) {
            for (MobEffectInstance instance : this.getEyeEffects()) {
                if (instance != null) {
                    this.addEyeEffects(instance.getEffect(), instance.getAmplifier());
                }
            }
        } else if (this.getEyeType() == SEARING_EYE) {
            this.addEyeEffects(MobEffects.FIRE_RESISTANCE);
            this.addEyeEffects(GoetyEffects.FIERY_AURA.get());
            this.addEyeEffects(GoetyEffects.RALLYING.get());
        } else if (this.getEyeType() == HALLOWED_EYE) {
            this.addEyeEffects(GoetyEffects.ALTRUISTIC.get());
            this.addEyeEffects(GoetyEffects.RADIANCE.get());
            this.addEyeEffects(GoetyEffects.SHIELDING.get());
        } else if (this.getEyeType() == TWISTED_EYE) {
            this.addEyeEffects(MobEffects.MOVEMENT_SPEED);
            this.addEyeEffects(GoetyEffects.ELECTRIFIED.get());
            this.addEyeEffects(GoetyEffects.SWIRLING.get());
        } else if (this.getEyeType() == DREADFUL_EYE) {
            this.addEyeEffects(GoetyEffects.GRAVITY_PULSE.get());
            this.addEyeEffects(GoetyEffects.DEFLECTIVE.get());
            this.addEyeEffects(GoetyEffects.FROSTY_AURA.get());
        } else {
            this.addEyeEffects(MobEffects.DAMAGE_BOOST);
            this.addEyeEffects(MobEffects.DAMAGE_RESISTANCE);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 5 == 0) {
            this.bossInfo.update();
        }
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        MiscCapHelper.updateMobTarget(this);
        if (this.level.isClientSide) {
            this.idleAnimationState.animateWhen(!this.walkAnimation.isMoving() && !this.isHiding() && this.isCurrentAnimation(IDLE), this.tickCount);
            if (this.isCurrentAnimation(DEADLY_ESCAPE)) {
                this.stopMostAnimation(this.deadlyEscapeAnimationState);
            }
        }
    }

    public void aiStep() {
        super.aiStep();
        if (!this.isHiding() && !this.isDeadOrDying()) {
            if (this.level.isClientSide) {
                for(int i = 0; i < 2; ++i) {
                    this.level.addParticle(ParticleTypes.PORTAL, this.getRandomX(0.5D), this.getRandomY() - 0.25D, this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
                }
            }
        }
        if (this.deadlyEscapeCool > 0) {
            if (this.isDeadlyEscape()) {
                this.setDeadlyEscape(false);
            }
            --this.deadlyEscapeCool;
        }
        if (this.recentHitTime > 0) {
            --this.recentHitTime;
        }
        this.idleTick();
        if (!this.level.isClientSide) {
            if (!this.isDeadOrDying()) {
                this.setAggressive(this.getTarget() != null);
                if (this.getEyeType() > 0 && !this.isHiding()) {
                    this.eyeTypeEffects();
                }
                if (this.getVoidFrame() instanceof VoidFrameBlockEntity voidFrameBlock) {
                    voidFrameBlock.setCoolTick(0);
                    if (this.getTarget() == null && this.distanceToSqr(voidFrameBlock.getBlockPos().getCenter()) > Mth.square(48)) {
                        if (this.tickCount % 10 == 0) {
                            Vec3 vec3 = voidFrameBlock.getBlockPos().getCenter();
                            this.getNavigation().stop();
                            this.teleportTo(vec3.x, vec3.y + 1, vec3.z);
                        }
                    }
                }
                if (this.isMeleeAttacking()) {
                    ++this.attackTick;
                } else if (this.isAttacking()) {
                    if (this.isDeadlyEscape()) {
                        this.setAnimationState(DEADLY_ESCAPE);
                    } else {
                        this.setAnimationState(IDLE);
                    }
                }
                if (this.isCurrentAnimation(TELEPORT_IN)) {
                    if (this.postTeleportTick <= 0) {
                        this.setAnimationState(IDLE);
                    } else if (this.postTeleportTick == (MathHelper.secondsToTicks(1.92F - 0.44F))) {
                        this.frontSmash();
                    }
                }
                AttributeInstance instance = this.getAttribute(Attributes.ATTACK_DAMAGE);
                if (instance != null) {
                    if (this.isCurrentAnimation(TELEPORT_IN)) {
                        if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                            instance.removeModifier(TELEPORT_ATTACK_MODIFIER);
                            instance.addTransientModifier(TELEPORT_ATTACK_MODIFIER);
                        }
                    } else {
                        if (instance.hasModifier(TELEPORT_ATTACK_MODIFIER)) {
                            instance.removeModifier(TELEPORT_ATTACK_MODIFIER);
                        }
                    }
                    if (this.isCurrentAnimation(DEADLY_ESCAPE)) {
                        if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                            instance.removeModifier(DE_ATTACK_MODIFIER);
                            instance.addTransientModifier(DE_ATTACK_MODIFIER);
                        }
                    } else {
                        if (instance.hasModifier(DE_ATTACK_MODIFIER)) {
                            instance.removeModifier(DE_ATTACK_MODIFIER);
                        }
                    }
                }
                if (!this.isHiding()) {
                    if (this.getTarget() != null) {
                        if (!this.isTeleporting()
                                && !this.isDeadlyEscape()
                                && this.postTeleportTick <= 0) {
                            if (this.getTarget().distanceTo(this) <= 8.0F
                                    && this.level.getRandom().nextBoolean()
                                    && this.recentHitTime > 0
                                    && this.deadlyEscapeCool <= 0
                                    && this.watchlingNumber() < 3) {
                                if (!this.isMeleeAttacking()
                                        && !this.isAttacking()) {
                                    this.setAnimationState(DEADLY_ESCAPE);
                                }
                                this.setDeadlyEscape(true);
                            } else if (!this.isWithinMeleeAttackRange(this.getTarget())
                                    && !this.isMeleeAttacking()
                                    && !this.isAttacking()
                                    && this.recentHitTime > 0
                                    && this.level.getRandom().nextBoolean()
                                    && this.teleportCool <= 0) {
                                this.teleportHideTime = MathHelper.secondsToTicks(this.level.getRandom().nextIntBetweenInclusive(3, 5));
                                this.setTeleporting(true);
                                this.setAnimationState(TELEPORT_OUT);
                            }
                        }
                    }
                    if (this.isDeadlyEscape() && this.isCurrentAnimation(DEADLY_ESCAPE)) {
                        this.setTeleporting(false);
                        this.deadlyEscape();
                    } else if (this.isTeleporting()) {
                        this.setDeadlyEscape(false);
                        this.specialTeleport();
                    }
                }
            }
        }
    }

    public boolean teleportHurt() {
        return true;
    }

    public void addEyeEffects(MobEffect effect) {
        this.addEyeEffects(effect, 0);
    }

    public void addEyeEffects(MobEffect effect, int level) {
        this.addEffect(new MobEffectInstance(effect, 5, level, false, false));
    }

    public int watchlingNumber() {
        return this.level.getEntitiesOfClass(WatchlingServant.class, this.getBoundingBox().inflate(64.0D), predicate -> predicate.getTrueOwner() == this).size();
    }

    public boolean noWatchlings() {
        return this.watchlingNumber() <= 0;
    }

    public boolean isAttacking() {
        return this.isCurrentAnimation(ATTACK) || this.isCurrentAnimation(SWIPE);
    }

    public int getHidingDuration() {
        if (this.isTeleporting()) {
            return this.teleportHideTime;
        }
        return MathHelper.secondsToTicks(5);
    }

    @Override
    public boolean shouldStopHiding() {
        return this.isDeadlyEscape() && this.noWatchlings();
    }

    public void specialTeleport() {
        ++this.preHidingTime;
        this.getNavigation().stop();
        this.getMoveControl().strafe(0.0F, 0.0F);
        if (this.getTarget() != null) {
            MobUtil.instaLook(this, this.getTarget());
        }
        if (this.preHidingTime % 2 == 0) {
            this.playSound(ModSounds.ENDERLING_TELEPORT_OUT.get(), 1.0F, 1.0F);
        }
        if (this.preHidingTime >= MathHelper.secondsToTicks(1.92F)) {
            this.startHide();
            this.preHidingTime = 0;
        }
    }

    public void deadlyEscape() {
        ++this.preHidingTime;
        this.getNavigation().stop();
        this.getMoveControl().strafe(0.0F, 0.0F);
        if (this.getTarget() != null) {
            MobUtil.instaLook(this, this.getTarget());
        }
        if (this.preHidingTime == 1) {
            this.playSound(ModSounds.ENDERSENT_DEADLY_ESCAPE.get(), this.getSoundVolume(), this.getVoicePitch());
        }
        if (this.preHidingTime == MathHelper.secondsToTicks(1.6F)) {
            this.frontSmash();
            this.summonWatchlings();
        }
        if (this.preHidingTime >= MathHelper.secondsToTicks(2)) {
            this.startHide();
            this.preHidingTime = 0;
        }
    }

    public void summonWatchlings() {
        if (this.level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 6; ++i) {
                if (i < 3 || serverLevel.getRandom().nextBoolean()) {
                    WatchlingServant servant = new WatchlingServant(ModEntityType.WATCHLING_SERVANT.get(), serverLevel);
                    BlockPos blockPos = BlockFinder.SummonRadius(this.blockPosition(), servant, serverLevel);
                    servant.setTrueOwner(this);
                    servant.moveTo(blockPos, 0.0F, 0.0F);
                    servant.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    if (serverLevel.addFreshEntity(servant)){
                        ServerParticleUtil.summonedParticles(serverLevel, servant, ColorUtil.WHITE, 0xFFFFFF, 0xFFFFFF);
                    }
                } else {
                    break;
                }
            }
        }
    }

    @Override
    public void teleportAfterHiding() {
        if (!this.isTeleporting()) {
            this.teleport(7.0D);
        } else {
            if (this.getTarget() != null) {
                this.teleportTowards(this.getTarget(), 4.0D);
            } else {
                this.teleportIn();
            }
            this.teleportCool = MathHelper.secondsToTicks(5);
            this.setTeleporting(false);
        }
        if (this.getTarget() != null) {
            if (!this.level.isClientSide) {
                MobUtil.instaLook(this, this.getTarget());
                ModNetwork.sentToTrackingEntity(this, new SInstaLookPacket(this.getId(), this.getTarget().getId()));
            }
        }
        if (this.isDeadlyEscape()) {
            this.deadlyEscapeCool = MathHelper.secondsToTicks(10);
            this.setDeadlyEscape(false);
        }
    }

    @Override
    public void teleportIn() {
        super.teleportIn();
        this.postTeleportTick = MathHelper.secondsToTicks(1.92F);
        if (!this.level.isClientSide) {
            if (this.isTeleporting()) {
                this.setAnimationState(TELEPORT_IN);
                this.playSound(ModSounds.ENDERSENT_TELEPORT_SMASH.get(), this.getSoundVolume(), this.getVoicePitch());
                this.setTeleporting(false);
            } else {
                this.setAnimationState(IDLE);
            }
        }
    }

    protected boolean teleport(double range) {
        if (!this.level.isClientSide() && this.isAlive()) {
            for(int i = 0; i < 128; ++i) {
                boolean flag = true;
                double d3 = this.getX() + (this.getRandom().nextDouble() - 0.5D) * range;
                double d4 = this.getY() + (this.getRandom().nextInt(Mth.floor(range)) - (range / 2.0D));
                if (this.getTarget() != null){
                    d4 = this.getTarget().getY();
                }
                double d5 = this.getZ() + (this.getRandom().nextDouble() - 0.5D) * range;
                BlockPos blockPos = BlockPos.containing(d3, d4, d5);
                if (this.getTarget() != null && i < 64) {
                    flag = BlockFinder.canSeeBlock(this.getTarget(), blockPos);
                }
                net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(this, blockPos.getX(), blockPos.getY(), blockPos.getZ());
                if (event.isCanceled()) {
                    this.teleportIn();
                    return false;
                }
                if (flag) {
                    if (this.ownedTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ())) {
                        return true;
                    } else if (i == 127) {
                        this.teleportIn();
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void teleportTowards(Entity entity, double range) {
        if (!this.level.isClientSide() && this.isAlive()) {
            if (entity == null) {
                this.teleportIn();
                return;
            }
            try {
                for (int i = 0; i < 128; ++i) {
                    int range2 = Mth.floor(range);
                    double d1 = entity.getX() + this.level.getRandom().nextIntBetweenInclusive(-range2, range2);
                    double d2 = entity.getY();
                    double d3 = entity.getZ() + this.level.getRandom().nextIntBetweenInclusive(-range2, range2);
                    net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(this, d1, d2, d3);
                    if (event.isCanceled()) {
                        if (this.getHidingDuration() > 0) {
                            this.teleportIn();
                        } else {
                            this.teleportHits();
                        }
                        break;
                    }
                    if (this.ownedTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ())) {
                        MobUtil.instaLook(this, entity);
                        break;
                    } else if (i == 127) {
                        MobUtil.instaLook(this, entity);
                        if (this.getHidingDuration() > 0) {
                            this.teleportIn();
                        } else {
                            this.teleportHits();
                        }
                        break;
                    }
                }
            } catch (NullPointerException exception) {
                this.teleportIn();
            }
        }
    }

    public double getXLeft(){
        return this.getXFront() + MobUtil.getHorizontalLeftLookAngle(this).x;
    }

    public double getZLeft(){
        return this.getZFront() + MobUtil.getHorizontalLeftLookAngle(this).z;
    }

    public double getXRight(){
        return this.getXFront() + MobUtil.getHorizontalRightLookAngle(this).x;
    }

    public double getZRight(){
        return this.getZFront() + MobUtil.getHorizontalRightLookAngle(this).z;
    }

    public double getXFront(){
        return this.getX() + this.getHorizontalLookAngle().x;
    }

    public double getZFront(){
        return this.getZ() + this.getHorizontalLookAngle().z;
    }

    public Vec3 getHorizontalLookAngle() {
        return this.calculateViewVector(0, this.getYRot());
    }

    @Override
    public double getMeleeAttackRangeSqr(LivingEntity livingEntity) {
        return this.getBbWidth() * 4.0F * this.getBbWidth() * 4.0F + livingEntity.getBbWidth();
    }

    public boolean isWithinMeleeAttackRange(LivingEntity livingentity) {
        double d0 = this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
        return d0 <= this.getMeleeAttackRangeSqr(livingentity);
    }

    public void frontSmash() {
        this.leftSmash();
        this.rightSmash();
        BlockPos blockPos = BlockPos.containing(this.getXFront(), this.getY() - 1.0F, this.getZFront());
        float size = this.isDeadlyEscape() ? 5.0F : 2.5F;
        AABB aabb = new AABB(blockPos).inflate(size);
        for (LivingEntity target : this.level.getEntitiesOfClass(LivingEntity.class, aabb)) {
            if (target != this && !MobUtil.areAllies(target, this)) {
                this.doHurtTarget(target);
            }
        }
        CameraShake.cameraShake(this.level, blockPos.getCenter(), 20, 0.2F, 0, 10);
        if (this.level instanceof ServerLevel serverLevel) {
            if (this.isDeadlyEscape()) {
                ColorUtil colorUtil = new ColorUtil(0xf169e9);
                serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), size, 1), this.getXFront(), BlockFinder.moveDownToGround(this), this.getZFront(), 1, 0.0D, 0.0D, 0.0D, 0);
                serverLevel.sendParticles(new VerticalCircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), size, 1), this.getXFront(), BlockFinder.moveDownToGround(this), this.getZFront(), 1, 0.0D, 0.0D, 0.0D, 0);
                ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.BIG_CULT_SPELL.get(), this.getXFront(), BlockFinder.moveDownToGround(this) + 1.0D, this.getZFront(), colorUtil.red(), colorUtil.green(), colorUtil.blue(), size);
            }
        }
    }

    public void leftSmash() {
        BlockPos blockPos = BlockPos.containing(this.getXLeft(), this.getY() - 1.0F, this.getZLeft());
        float size = 2.5F;
        AABB aabb = new AABB(blockPos).inflate(size);
        for (LivingEntity target : this.level.getEntitiesOfClass(LivingEntity.class, aabb)) {
            if (target != this && !MobUtil.areAllies(target, this)) {
                this.doHurtTarget(target);
            }
        }
        if (this.level instanceof ServerLevel serverLevel) {
            BlockState blockState = this.level.getBlockState(blockPos);
            BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
            ServerParticleUtil.circularParticles(serverLevel, option, blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ(), 0.0F, 0.0F, 0.0F, size);
            if (!this.isDeadlyEscape()) {
                ColorUtil colorUtil = new ColorUtil(serverLevel.getBlockState(blockPos).getMapColor(serverLevel, blockPos).col);
                serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), size, 1), this.getXLeft(), BlockFinder.moveDownToGround(this), this.getZLeft(), 1, 0.0D, 0.0D, 0.0D, 0);
                ColorUtil colorUtil1 = new ColorUtil(0x3c3c3e);
                ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.BIG_CULT_SPELL.get(), blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ(), colorUtil1.red(), colorUtil1.green(), colorUtil1.blue(), size);
            }
        }
    }

    public void rightSmash() {
        BlockPos blockPos = BlockPos.containing(this.getXRight(), this.getY() - 1.0F, this.getZRight());
        float size = 2.5F;
        AABB aabb = new AABB(blockPos).inflate(size);
        for (LivingEntity target : this.level.getEntitiesOfClass(LivingEntity.class, aabb)) {
            if (target != this && !MobUtil.areAllies(target, this)) {
                this.doHurtTarget(target);
            }
        }
        if (this.level instanceof ServerLevel serverLevel) {
            BlockState blockState = this.level.getBlockState(blockPos);
            BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
            ServerParticleUtil.circularParticles(serverLevel, option, blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ(), 0.0F, 0.0F, 0.0F, size);
            if (!this.isDeadlyEscape()) {
                ColorUtil colorUtil = new ColorUtil(serverLevel.getBlockState(blockPos).getMapColor(serverLevel, blockPos).col);
                serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), size, 1), this.getXRight(), BlockFinder.moveDownToGround(this), this.getZRight(), 1, 0.0D, 0.0D, 0.0D, 0);
                ColorUtil colorUtil1 = new ColorUtil(0x3c3c3e);
                ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.BIG_CULT_SPELL.get(), blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ(), colorUtil1.red(), colorUtil1.green(), colorUtil1.blue(), size);
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (this.level.getRandom().nextBoolean() || this.isDeadlyEscape() || this.isCurrentAnimation(TELEPORT_IN)) {
            if (entityIn instanceof Player player && player.isBlocking()) {
                player.disableShield(true);
            }
        }
        if (entityIn instanceof Mob) {
            MobUtil.disableShield(entityIn);
        }
        return flag;
    }

    class AttackGoal extends MeleeAttackGoal {
        private final double moveSpeed;
        private int delayCounter;

        public AttackGoal(double moveSpeed) {
            super(Endersent.this, moveSpeed, true);
            this.moveSpeed = moveSpeed;
        }

        @Override
        public boolean canUse() {
            return Endersent.this.getTarget() != null
                    && Endersent.this.getTarget().isAlive()
                    && !Endersent.this.isHiding()
                    && !Endersent.this.isTeleporting()
                    && !Endersent.this.isDeadlyEscape()
                    && Endersent.this.preHidingTime <= 0
                    && Endersent.this.postTeleportTick <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse()
                    && !Endersent.this.isHiding()
                    && !Endersent.this.isTeleporting()
                    && !Endersent.this.isDeadlyEscape()
                    && Endersent.this.preHidingTime <= 0
                    && Endersent.this.postTeleportTick <= 0;
        }

        @Override
        public void start() {
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            Endersent.this.setMeleeAttacking(false);
            Endersent.this.attackTick = 0;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = Endersent.this.getTarget();
            if (livingentity == null) {
                this.stop();
                return;
            }

            Endersent.this.getLookControl().setLookAt(livingentity, Endersent.this.getMaxHeadYRot(), Endersent.this.getMaxHeadXRot());

            if (--this.delayCounter <= 0) {
                this.delayCounter = 10;
                Endersent.this.getNavigation().moveTo(livingentity, this.moveSpeed);
            }

            this.checkAndPerformAttack(livingentity, Endersent.this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double d0 = this.getAttackReachSqr(enemy);
            boolean smash = Endersent.this.random.nextBoolean();

            if (!Endersent.this.isMeleeAttacking() && distToEnemySqr <= d0){
                Endersent.this.setMeleeAttacking(true);
                if (smash) {
                    Endersent.this.setAnimationState(SWIPE);
                    Endersent.this.playSound(ModSounds.ENDERSENT_SWING.get(), Endersent.this.getSoundVolume(), Endersent.this.getVoicePitch());
                } else {
                    Endersent.this.setAnimationState(ATTACK);
                    Endersent.this.playSound(ModSounds.ENDERSENT_ATTACK.get(), Endersent.this.getSoundVolume(), Endersent.this.getVoicePitch());
                }
            }
            if (Endersent.this.isMeleeAttacking()) {
                float seconds = Endersent.this.isCurrentAnimation(ATTACK) ? 2.44F : 2.84F;
                if (Endersent.this.attackTick < MathHelper.secondsToTicks(seconds)) {
                    MobUtil.instaLook(Endersent.this, enemy);
                    if (Endersent.this.isCurrentAnimation(SWIPE)) {
                        if (Endersent.this.attackTick == MathHelper.secondsToTicks(1)) {
                            this.massiveSweep(Endersent.this, 4.5D, 200.0D);
                        }
                    } else if (Endersent.this.isCurrentAnimation(ATTACK)) {
                        if (Endersent.this.attackTick == MathHelper.secondsToTicks(1)) {
                            double x = Endersent.this.getXFront();
                            double z = Endersent.this.getZFront();
                            float size = 2.5F;
                            BlockPos blockPos = BlockPos.containing(x, Endersent.this.getY() - 1.0F, z);
                            AABB aabb = new AABB(blockPos).inflate(size);
                            for (LivingEntity target : Endersent.this.level.getEntitiesOfClass(LivingEntity.class, aabb)) {
                                if (target != Endersent.this && !MobUtil.areAllies(target, Endersent.this)) {
                                    Endersent.this.doHurtTarget(target);
                                }
                            }
                            CameraShake.cameraShake(Endersent.this.level, blockPos.getCenter(), 15.0F, 0.1F, 0, 10);
                            if (Endersent.this.level instanceof ServerLevel serverLevel) {
                                BlockState blockState = serverLevel.getBlockState(blockPos);
                                BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
                                ServerParticleUtil.circularParticles(serverLevel, option, blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ(), 0.0F, 0.0F, 0.0F, size);
                                ColorUtil colorUtil = new ColorUtil(serverLevel.getBlockState(blockPos).getMapColor(serverLevel, blockPos).col);
                                serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), size, 1), x, BlockFinder.moveDownToGround(Endersent.this), z, 1, 0.0D, 0.0D, 0.0D, 0);
                                ColorUtil colorUtil1 = new ColorUtil(0x3c3c3e);
                                ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.BIG_CULT_SPELL.get(), blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ(), colorUtil1.red(), colorUtil1.green(), colorUtil1.blue(), size);
                            }
                        }
                    }
                } else {
                    if (!Endersent.this.isHiding() && !Endersent.this.isDeadlyEscape()) {
                        if (Endersent.this.level.getRandom().nextFloat() <= 0.3F) {
                            Endersent.this.startHide();
                            if (Endersent.this.level.getRandom().nextBoolean() && Endersent.this.watchlingNumber() <= 3) {
                                Endersent.this.summonWatchlings();
                            }
                        }
                    }
                    Endersent.this.setMeleeAttacking(false);
                }
            }
        }

        public void massiveSweep(LivingEntity source, double range, double arc){
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
                if (hitDistance <= range && (relativeAngle <= arc / 2 && relativeAngle >= -arc / 2) || (relativeAngle >= 360 - arc / 2 || relativeAngle <= -360 + arc / 2)) {
                    Endersent.this.doHurtTarget(target);
                }
            }
        }

        @Override
        protected double getAttackReachSqr(LivingEntity livingEntity) {
            return this.mob.getMeleeAttackRangeSqr(livingEntity);
        }
    }
}
