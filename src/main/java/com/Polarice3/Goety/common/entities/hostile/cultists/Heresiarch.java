package com.Polarice3.Goety.common.entities.hostile.cultists;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.WitchBarterGoal;
import com.Polarice3.Goety.common.entities.hostile.servants.ObsidianMonolith;
import com.Polarice3.Goety.common.entities.neutral.AbstractObsidianMonolith;
import com.Polarice3.Goety.common.entities.neutral.BurningHoglin;
import com.Polarice3.Goety.common.entities.projectiles.HellBolt;
import com.Polarice3.Goety.common.entities.projectiles.SurgingOrb;
import com.Polarice3.Goety.common.entities.util.EffectBlastTrap;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.common.network.server.SRepositionPacket;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableWitchTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class Heresiarch extends Cultist {
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(Heresiarch.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> START_TELEPORTING = SynchedEntityData.defineId(Heresiarch.class, EntityDataSerializers.BOOLEAN);
    public static String IDLE = "idle";
    public static String BLESS = "bless";
    public static String SUMMON = "summon";
    public static String BLAST = "blast";
    public static String SHOOT = "shoot";
    public static String BARRAGE = "barrage";
    public static String MELEE = "melee";
    public static String INSPECT = "inspect";
    public static String CHANT = "chant";
    private AbstractObsidianMonolith monolith;
    public double prevX;
    public double prevY;
    public double prevZ;
    public int spellType = 0;
    public int hysteriaCool;
    public int meleeCool;
    public int fightTick;
    public int aboutToTeleport = 0;
    public float runeScale, prevRuneScale;
    private final ModServerBossInfo bossInfo;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState blessAnimationState = new AnimationState();
    public AnimationState summonAnimationState = new AnimationState();
    public AnimationState blastAnimationState = new AnimationState();
    public AnimationState shootAnimationState = new AnimationState();
    public AnimationState barrageAnimationState = new AnimationState();
    public AnimationState meleeAnimationState = new AnimationState();
    public AnimationState chantAnimationState = new AnimationState();

    public final List<Pair<Vec3, ModelSnapshot>> trailSnapshots = new ArrayList<>(50);
    public float lastTrailTick = 0;

    public boolean shouldAddTrailSnapshot() {
        if (!MobsConfig.HeresiarchAfterImage.get()) {
            return false;
        }
        return Mth.degreesDifferenceAbs(getYRot(), yBodyRot) < 45
                && Mth.degreesDifferenceAbs(getYRot(), yBodyRotO) < 45
                && Mth.degreesDifferenceAbs(yBodyRot, yBodyRotO) < 45
                && this.isStartTeleporting();
    }

    public Heresiarch(EntityType<? extends Cultist> type, Level worldIn) {
        super(type, worldIn);
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.RED, false, false);
        this.xpReward = 99;
        this.noCulling = true;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new WitchBarterGoal(this){
            @Override
            public void start() {
                super.start();
                Heresiarch.this.setAnimationState(INSPECT);
            }

            @Override
            public void stop() {
                super.stop();
                Heresiarch.this.setAnimationState(IDLE);
            }
        });
        this.goalSelector.addGoal(0, new ChantGoal(this));
        this.goalSelector.addGoal(0, new MeleeGoal(this));
        this.goalSelector.addGoal(1, new StareGoal(this));
        this.goalSelector.addGoal(2, new HysteriaGoal(this));
        this.goalSelector.addGoal(2, new SummonGoal(this));
        this.goalSelector.addGoal(2, new BlastGoal(this));
        this.goalSelector.addGoal(2, new ShootGoal(this));
        this.goalSelector.addGoal(2, new BarrageGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableWitchTargetGoal<>(this, Player.class, 10, true, false, null));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.HeresiarchHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.HeresiarchArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.HeresiarchDamage.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.25D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.HeresiarchHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.HeresiarchArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.HeresiarchDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, 0);
        this.entityData.define(START_TELEPORTING, false);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("HysteriaCool", this.hysteriaCool);
        pCompound.putInt("MeleeCool", this.meleeCool);
        pCompound.putInt("SpellType", this.spellType);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("HysteriaCool")) {
            this.hysteriaCool = pCompound.getInt("HysteriaCool");
        }
        if (pCompound.contains("MeleeCool")) {
            this.meleeCool = pCompound.getInt("MeleeCool");
        }
        if (pCompound.contains("SpellType")) {
            this.spellType = pCompound.getInt("SpellType");
        }
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.HERESIARCH_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return ModSounds.HERESIARCH_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.HERESIARCH_DEATH.get();
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.HERESIARCH_AMBIENT.get();
    }

    @Override
    public void die(DamageSource source) {
        this.playSound(ModSounds.DEAD_MOAN.get(), 2.0F, this.getVoicePitch() * 0.75F);
        if (source.getEntity() != null) {
            Entity entity = source.getEntity();
            LivingEntity target = null;
            if (entity instanceof LivingEntity livingEntity && !MobUtil.areAllies(this, livingEntity) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                target = livingEntity;
            }
            if (this.getTarget() != null) {
                target = this.getTarget();
            }
            for (Cultist cultist : this.getList()) {
                if (this.level instanceof ServerLevel serverLevel) {
                    cultist.addEffect(new MobEffectInstance(GoetyEffects.HYSTERIA.get(), MathHelper.secondsToTicks(45), 0, false, false));
                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.ANGRY_VILLAGER, cultist);
                    if (target != null) {
                        cultist.setTarget(target);
                    }
                }
            }
        }
        super.die(source);
    }

    public List<Cultist> getList() {
        List<Cultist> list = this.level.getEntitiesOfClass(Cultist.class, this.getBoundingBox().inflate(16.0D));
        list.removeIf(cultist -> cultist.getType().is(Tags.EntityTypes.BOSSES) || cultist.getType().is(ModTags.EntityTypes.MINI_BOSSES));
        list.removeIf(cultist -> !cultist.hasLineOfSight(this) && MobUtil.areAllies(cultist, this.getTarget()));
        return list;
    }

    public void setMonolith(@Nullable AbstractObsidianMonolith monolith) {
        this.monolith = monolith;
    }

    @Nullable
    public AbstractObsidianMonolith getMonolith() {
        return this.monolith;
    }

    public void setStartTeleporting(boolean startTeleporting) {
        this.entityData.set(START_TELEPORTING, startTeleporting);
    }

    public boolean isStartTeleporting() {
        return this.entityData.get(START_TELEPORTING);
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
        } else if (Objects.equals(animation, BLESS)){
            return 1;
        } else if (Objects.equals(animation, SUMMON)){
            return 2;
        } else if (Objects.equals(animation, BLAST)){
            return 3;
        } else if (Objects.equals(animation, SHOOT)){
            return 4;
        } else if (Objects.equals(animation, BARRAGE)){
            return 5;
        } else if (Objects.equals(animation, MELEE)){
            return 6;
        } else if (Objects.equals(animation, INSPECT)){
            return 7;
        } else if (Objects.equals(animation, CHANT)){
            return 8;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.blessAnimationState);
        animationStates.add(this.summonAnimationState);
        animationStates.add(this.blastAnimationState);
        animationStates.add(this.shootAnimationState);
        animationStates.add(this.barrageAnimationState);
        animationStates.add(this.meleeAnimationState);
        animationStates.add(this.chantAnimationState);
        return animationStates;
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

    public boolean isCurrentAnimation(String animation) {
        return this.getCurrentAnimation() == this.getAnimationState(animation);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor)) {
            if (this.level.isClientSide){
                switch (this.entityData.get(ANIM_STATE)){
                    case 0:
                    case 7:
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 1:
                        this.blessAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.blessAnimationState);
                        break;
                    case 2:
                        this.summonAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.summonAnimationState);
                        break;
                    case 3:
                        this.blastAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.blastAnimationState);
                        break;
                    case 4:
                        this.shootAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.shootAnimationState);
                        break;
                    case 5:
                        this.barrageAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.barrageAnimationState);
                        break;
                    case 6:
                        this.meleeAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.meleeAnimationState);
                        break;
                    case 8:
                        this.chantAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.chantAnimationState);
                        break;
                }
            }
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        this.populateDefaultEquipmentSlots(worldIn.getRandom(), difficultyIn);
        this.populateDefaultEquipmentEnchantments(worldIn.getRandom(), difficultyIn);
        if (!this.hasCustomName()){
            int random = this.random.nextInt(4);
            int random2 = this.random.nextInt(12);
            Component component = Component.translatable("title.goety.heresiarch." + random);
            Component component1 = Component.translatable("name.goety.heresiarch." + random2);
            this.setCustomName(Component.translatable(component.getString() +  component1.getString()));
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource p_217055_, DifficultyInstance p_217056_) {
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

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 5 == 0) {
            this.bossInfo.update();
        }
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        this.bossInfo.setVisible(!this.isCurrentAnimation(CHANT));
        if (this.level.isClientSide) {
            this.prevRuneScale = this.runeScale;
            if (this.isCurrentAnimation(Heresiarch.BARRAGE)) {
                this.runeScale += 0.3F;
            } else {
                this.runeScale -= 0.2F;
            }
            this.runeScale = Mth.clamp(this.runeScale, 0, 1);
            this.idleAnimationState.animateWhen((this.isCurrentAnimation(IDLE) || this.isCurrentAnimation(INSPECT)) && !this.walkAnimation.isMoving(), this.tickCount);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            if (this.hysteriaCool > 0) {
                --this.hysteriaCool;
            }
            if (this.meleeCool > 0) {
                --this.meleeCool;
            }
            if (this.fightTick > 0) {
                if (this.getTarget() == null) {
                    --this.fightTick;
                }
            }
            if (this.getMonolith() == null){
                if (this.getLeader() instanceof ObsidianMonolith obsidianMonolith){
                    this.setMonolith(obsidianMonolith);
                } else {
                    for (ObsidianMonolith monolith1 : this.level.getEntitiesOfClass(ObsidianMonolith.class, this.getBoundingBox().inflate(64.0D, 8.0D, 64.0D))){
                        if (monolith1.getTrueOwner() == null || monolith1.getTrueOwner() instanceof Cultist){
                            this.setMonolith(monolith1);
                        }
                    }
                }
            } else if (this.getMonolith() != null){
                if (this.getLeader() != this.getMonolith()) {
                    this.setLeader(this.getMonolith());
                }
                this.getMonolith().empowered = 10;
                this.getMonolith().spawnMiniBossCool = MathHelper.minecraftDayToTicks(5);
                try {
                    if (this.getTarget() != null) {
                        if (this.tickCount % 20 == 0) {
                            if (this.level.getRandom().nextInt(64) == 0) {
                                int i = this.level.getEntitiesOfClass(Maverick.class, this.getMonolith().getBoundingBox().inflate(24.0D, 16.0D, 24.0D), livingEntity -> livingEntity.isAlive() && MobUtil.areAllies(livingEntity, this)).size();
                                if (i < 8) {
                                    this.getMonolith().summonMavericks();
                                }
                            }
                            if (this.level.getRandom().nextInt(256) == 0) {
                                int i = this.level.getEntitiesOfClass(Heretic.class, this.getMonolith().getBoundingBox().inflate(24.0D, 16.0D, 24.0D), livingEntity -> livingEntity.isAlive() && MobUtil.areAllies(livingEntity, this)).size();
                                if (i < 2) {
                                    this.getMonolith().summonHeretics();
                                }
                            }
                        }
                    }
                } catch (NullPointerException exception) {
                    this.setMonolith(null);
                }
            }
            if (this.isStartTeleporting()) {
                ++this.aboutToTeleport;
                int time = MobUtil.healthIsHalved(this) ? 30 : 60;
                if (this.aboutToTeleport >= time && !this.isCurrentAnimation(MELEE)) {
                    if (this.getTarget() != null) {
                        this.teleport();
                    }
                    this.setStartTeleporting(false);
                }
            } else {
                if (this.aboutToTeleport > 0) {
                    this.aboutToTeleport = 0;
                }
            }
        }
    }

    protected float getDamageAfterMagicAbsorb(DamageSource damageSource, float damage) {
        damage = super.getDamageAfterMagicAbsorb(damageSource, damage);
        if (damageSource.getEntity() == this) {
            damage = 0.0F;
        }

        if (damageSource.is(DamageTypeTags.WITCH_RESISTANT_TO) || damageSource.is(DamageTypeTags.IS_FIRE)) {
            damage *= 0.15F;
        }

        return damage;
    }

    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        ItemEntity itementity = this.spawnAtLocation(ModItems.MALEFIC_HELM.get());
        if (itementity != null) {
            itementity.setExtendedLifetime();
        }

    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 2.25F;
    }

    protected void teleport() {
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
        if (!this.level.isClientSide() && this.isAlive()) {
            Vec3 vec3 = this.position();
            if (this.getMonolith() != null) {
                vec3 = this.getMonolith().position();
            }
            for (int i = 0; i < 128; ++i) {
                double d3 = vec3.x + (this.level.getRandom().nextDouble() - 0.5D) * 32.0D;
                double d4 = this.getTarget() != null ? this.getTarget().getY() : vec3.y;
                double d5 = vec3.z + (this.level.getRandom().nextDouble() - 0.5D) * 32.0D;
                BlockPos blockPos = BlockPos.containing(d3, d4, d5);
                boolean flag = this.getTarget() == null || i >= 126 || BlockFinder.canSeeBlock(this.getTarget(), blockPos);
                net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(this, d3, d4, d5);
                if (event.isCanceled()) {
                    break;
                }
                Vec3 newPos = new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                if (this.getTarget() != null && i < 126) {
                    if (newPos.distanceTo(this.getTarget().position()) > this.getAttributeValue(Attributes.FOLLOW_RANGE)) {
                        flag = false;
                    }
                }
                if (flag && this.randomTeleport(newPos.x, newPos.y, newPos.z, false)) {
                    ModNetwork.sendToALL(new SRepositionPacket(this.getId(), this.getX(), this.getY(), this.getZ()));
                    this.teleportHits();
                    break;
                }
            }
        }
    }

    public void teleportHits(){
        if (this.level instanceof ServerLevel serverLevel) {
            int i = 16;

            for(int j = 0; j < i; ++j) {
                double d0 = (double)j / (i - 1);
                float f = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (this.random.nextFloat() - 0.5F) * 0.2F;
                double d1 = Mth.lerp(d0, this.prevX, this.getX()) + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth() * 2.0D;
                double d2 = Mth.lerp(d0, this.prevY, this.getY()) + this.random.nextDouble() * (double)this.getBbHeight();
                double d3 = Mth.lerp(d0, this.prevZ, this.getZ()) + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth() * 2.0D;
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, d1, d2, d3, 0, (double)f, (double)f1, (double)f2, 1.0F);
            }
        }
        this.level.gameEvent(GameEvent.TELEPORT, this.position(), GameEvent.Context.of(this));
        if (!this.isSilent()) {
            this.level.playSound((Player) null, this.prevX, this.prevY, this.prevZ, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
    }

    static class ChantGoal extends Goal {
        public Heresiarch heresiarch;

        public ChantGoal(Heresiarch heresiarch) {
            this.heresiarch = heresiarch;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE, Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            return this.heresiarch.getMonolith() != null
                    && this.heresiarch.getMonolith().isAlive()
                    && (this.heresiarch.getTarget() == null || this.heresiarch.getTarget().distanceTo(this.heresiarch) > 6.0D || !this.heresiarch.hasLineOfSight(this.heresiarch.getTarget()))
                    && this.heresiarch.getLastHurtByMobTimestamp() <= 0
                    && this.heresiarch.fightTick <= 0
                    && this.heresiarch.getHealth() == this.heresiarch.getMaxHealth();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void stop() {
            super.stop();
            this.heresiarch.fightTick = MathHelper.secondsToTicks(10);
            this.heresiarch.setAnimationState(IDLE);
        }

        @Override
        public void tick() {
            if (this.heresiarch.getMonolith() != null) {
                if (this.heresiarch.distanceTo(this.heresiarch.getMonolith()) <= 8.0D && this.heresiarch.hasLineOfSight(this.heresiarch.getMonolith())) {
                    if (!this.heresiarch.isCurrentAnimation(CHANT)) {
                        this.heresiarch.setAnimationState(CHANT);
                    }
                    MobUtil.instaLook(this.heresiarch, this.heresiarch.getMonolith());
                    this.drawParticleBeam(this.heresiarch, this.heresiarch.getMonolith());
                    if (this.heresiarch.tickCount % 20 == 0){
                        this.heresiarch.getMonolith().heal(2.0F);
                    }
                    this.heresiarch.getNavigation().stop();
                    this.heresiarch.getMoveControl().strafe(0, 0);
                    this.heresiarch.noActionTime = 0;
                } else {
                    this.heresiarch.setAnimationState(IDLE);
                    Vec3 vec3 = this.heresiarch.getMonolith().position();
                    this.heresiarch.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.0D);
                }
            }
        }

        private void drawParticleBeam(LivingEntity pSource, LivingEntity pTarget) {
            double d0 = pTarget.getX() - pSource.getX();
            double d1 = (pTarget.getY() + (double) pTarget.getBbHeight() * 0.5F) - (pSource.getY() + (double) pSource.getBbHeight() * 0.5D);
            double d2 = pTarget.getZ() - pSource.getZ();
            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            d0 = d0 / d3;
            d1 = d1 / d3;
            d2 = d2 / d3;
            double d4 = pSource.level.random.nextDouble();
            if (pSource.level instanceof ServerLevel serverWorld) {
                while (d4 < d3) {
                    d4 += 0.5D;
                    serverWorld.sendParticles(ModParticleTypes.CHANT.get(), pSource.getX() + d0 * d4, pSource.getY() + d1 * d4 + (double) pSource.getEyeHeight(), pSource.getZ() + d2 * d4, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    static abstract class HeresiarchGoal extends Goal {
        public Heresiarch heresiarch;

        public HeresiarchGoal(Heresiarch heresiarch) {
            this.heresiarch = heresiarch;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE, Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            return this.getTarget() != null && this.heresiarch.hasLineOfSight(this.getTarget()) && !this.heresiarch.isStartTeleporting() && this.heresiarch.isCurrentAnimation(IDLE);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Nullable
        public LivingEntity getTarget() {
            return this.heresiarch.getTarget();
        }

        public boolean halfHealth() {
            return MobUtil.healthIsHalved(this.heresiarch);
        }

        public void stopAndStare() {
            this.heresiarch.getNavigation().stop();
            this.heresiarch.getMoveControl().strafe(0.0F, 0.0F);
            if (this.getTarget() != null) {
                this.heresiarch.getLookControl().setLookAt(this.getTarget(), 100.0F, 100.0F);
                this.heresiarch.lookAt(this.getTarget(), 100.0F, 100.0F);
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.heresiarch.setAnimationState(IDLE);
            this.heresiarch.setStartTeleporting(true);
        }
    }

    static class StareGoal extends HeresiarchGoal {

        public StareGoal(Heresiarch heresiarch) {
            super(heresiarch);
        }

        @Override
        public boolean canUse() {
            if (this.heresiarch.meleeCool <= 0) {
                if (this.getTarget() != null) {
                    if (this.heresiarch.isWithinMeleeAttackRange(this.getTarget())) {
                        return false;
                    }
                }
            }
            return this.heresiarch.isStartTeleporting()
                    && this.heresiarch.aboutToTeleport > 0
                    && this.heresiarch.isCurrentAnimation(IDLE);
        }

        @Override
        public void start() {
            super.start();
            this.stopAndStare();
        }

        @Override
        public void tick() {
            super.tick();
            this.stopAndStare();
        }

        @Override
        public void stop() {
        }
    }

    static class MeleeGoal extends HeresiarchGoal {
        public int meleeTick;

        public MeleeGoal(Heresiarch heresiarch) {
            super(heresiarch);
        }

        @Override
        public boolean canUse() {
            if (this.heresiarch.isStartTeleporting() || this.heresiarch.isCurrentAnimation(IDLE)) {
                if (this.heresiarch.meleeCool <= 0) {
                    if (this.getTarget() != null) {
                        return this.heresiarch.isWithinMeleeAttackRange(this.getTarget());
                    }
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.meleeTick > 0;
        }

        @Override
        public void start() {
            super.start();
            this.heresiarch.setAnimationState(MELEE);
            this.meleeTick = 25;
            this.stopAndStare();
        }

        @Override
        public void tick() {
            super.tick();
            --this.meleeTick;
            this.stopAndStare();

            if (this.meleeTick == 22) {
                this.heresiarch.playSound(ModSounds.SWORD_SHING.get(), 1.0F, this.heresiarch.getVoicePitch());
            }

            if (this.meleeTick == 13) {
                this.heresiarch.playSound(ModSounds.SOUL_KNIFE_NO_SOUL_SWING.get(), 1.0F, this.heresiarch.getVoicePitch());
                float damage = (float) this.heresiarch.getAttributeValue(Attributes.ATTACK_DAMAGE) + 4.0F;
                DamageSource damageSource = this.heresiarch.damageSources().mobAttack(this.heresiarch);
                MobUtil.areaAttack(this.heresiarch, 4.0F, 0.25F, 90, damage, 0, 0, damageSource, true);
                if (this.getTarget() != null) {
                    if (this.heresiarch.isWithinMeleeAttackRange(this.getTarget())) {
                        this.getTarget().hurt(damageSource, damage);
                    }
                }
            }
        }

        @Override
        public void stop() {
            this.meleeTick = 0;
            this.heresiarch.meleeCool = MathHelper.secondsToTicks(5);
            this.heresiarch.setAnimationState(IDLE);
            this.heresiarch.setStartTeleporting(true);
        }
    }

    static class HysteriaGoal extends HeresiarchGoal {
        public int blessTick;

        public HysteriaGoal(Heresiarch heresiarch) {
            super(heresiarch);
        }

        @Override
        public boolean canUse() {
            if (super.canUse()) {
                if (this.heresiarch.hysteriaCool <= 0) {
                    return !this.heresiarch.getList().isEmpty();
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.blessTick > 0;
        }

        @Override
        public void start() {
            super.start();
            this.heresiarch.setAnimationState(BLESS);
            this.blessTick = 50;
            this.stopAndStare();
        }

        @Override
        public void tick() {
            super.tick();
            --this.blessTick;
            this.stopAndStare();

            if (this.blessTick == 30) {
                if (!this.heresiarch.getList().isEmpty()) {
                    for (Cultist cultist : this.heresiarch.getList()) {
                        if (this.heresiarch.level instanceof ServerLevel serverLevel) {
                            cultist.addEffect(new MobEffectInstance(GoetyEffects.HYSTERIA.get(), MathHelper.secondsToTicks(45), 0, false, false));
                            cultist.playSound(SoundEvents.FIRECHARGE_USE, 1.0F, 1.0F);
                            ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.ANGRY_VILLAGER, cultist);
                            cultist.setTarget(this.getTarget());
                        }
                    }
                }
            }
        }

        @Override
        public void stop() {
            this.blessTick = 0;
            this.heresiarch.hysteriaCool = MathHelper.secondsToTicks(60);
            super.stop();
        }
    }

    static class SummonGoal extends HeresiarchGoal {
        public int summonTick;

        public SummonGoal(Heresiarch heresiarch) {
            super(heresiarch);
        }

        @Override
        public boolean canUse() {
            if (super.canUse()) {
                return this.heresiarch.spellType == 0;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.summonTick > 0;
        }

        @Override
        public void start() {
            super.start();
            this.heresiarch.setAnimationState(SUMMON);
            this.summonTick = 40;
            this.stopAndStare();
        }

        @Override
        public void tick() {
            super.tick();
            --this.summonTick;
            this.stopAndStare();
            if (this.summonTick == 20) {
                int x = (int) (MobUtil.getHorizontalLeftLookAngle(this.heresiarch).x * 2);
                int z = (int) (MobUtil.getHorizontalLeftLookAngle(this.heresiarch).z * 2);
                BlockPos left = new BlockPos(this.heresiarch.blockPosition().offset(x, 3, z));
                left = BlockFinder.findGroundBelow(this.heresiarch.level, left);
                BurningHoglin hoglin = new BurningHoglin(ModEntityType.BURNING_HOGLIN.get(), this.heresiarch.level);
                hoglin.setTrueOwner(this.heresiarch);
                hoglin.setPos(Vec3.atBottomCenterOf(left));
                if (this.halfHealth()) {
                    hoglin.setWindUpTime(10);
                }
                hoglin.setLimitedLife(100);
                if (this.heresiarch.level instanceof ServerLevel serverLevel) {
                    ForgeEventFactory.onFinalizeSpawn(hoglin, serverLevel, serverLevel.getCurrentDifficultyAt(this.heresiarch.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    ServerParticleUtil.summonUndeadParticles(serverLevel, hoglin, new ColorUtil(0xffa300), 0xffa300, 0xffff6e);
                }
                if (this.getTarget() != null) {
                    hoglin.setTarget(this.getTarget());
                }
                this.heresiarch.level.addFreshEntity(hoglin);
            }
        }

        @Override
        public void stop() {
            this.summonTick = 0;
            this.heresiarch.spellType = 1;
            super.stop();
        }
    }

    static class BlastGoal extends HeresiarchGoal {
        public int blastTick;

        public BlastGoal(Heresiarch heresiarch) {
            super(heresiarch);
        }

        public boolean canUse() {
            if (super.canUse()) {
                return this.heresiarch.spellType == 1;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.blastTick > 0;
        }

        @Override
        public void start() {
            super.start();
            this.heresiarch.setAnimationState(BLAST);
            this.blastTick = 35;
            this.stopAndStare();
        }

        @Override
        public void tick() {
            super.tick();
            --this.blastTick;
            this.stopAndStare();
            if (this.blastTick == 10) {
                if (this.getTarget() != null) {
                    int amount = this.halfHealth() ? 4 : 3;
                    this.heresiarch.level.playSound(null, this.getTarget().getX(), this.getTarget().getY(), this.getTarget().getZ(), ModSounds.TOWER_WRAITH_ACID.get(), this.heresiarch.getSoundSource(), 2.0F, 0.5F);
                    for (int i = 0; i < amount; ++i) {
                        Vec3 vec3 = MobUtil.getFrontPos(this.getTarget(), 4.0D);
                        if (i == 1) {
                            vec3 = MobUtil.getRightPos(this.getTarget(), 4.0D);
                        } else if (i == 2) {
                            vec3 = MobUtil.getLeftPos(this.getTarget(), 4.0D);
                        } else if (i == 3) {
                            vec3 = MobUtil.getBackPos(this.getTarget(), 4.0D);
                        }
                        EffectBlastTrap effectBlastTrap = new EffectBlastTrap(this.heresiarch.level, vec3);
                        effectBlastTrap.setOwner(this.heresiarch);
                        effectBlastTrap.setAreaOfEffect(1.5F);
                        effectBlastTrap.playSound = false;
                        if (i == 0) {
                            effectBlastTrap.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1800));
                        } else if (i == 1) {
                            effectBlastTrap.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1800));
                        } else if (i == 2) {
                            effectBlastTrap.addEffect(new MobEffectInstance(GoetyEffects.ACID_VENOM.get(), 900));
                        } else {
                            effectBlastTrap.addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), 1800, 1));
                        }
                        MobUtil.moveDownToGround(effectBlastTrap);
                        this.heresiarch.level.addFreshEntity(effectBlastTrap);
                    }
                }
            }
        }

        @Override
        public void stop() {
            this.blastTick = 0;
            this.heresiarch.spellType = 2;
            super.stop();
        }
    }

    static class ShootGoal extends HeresiarchGoal {
        public int shootTick;

        public ShootGoal(Heresiarch heresiarch) {
            super(heresiarch);
        }

        public boolean canUse() {
            if (super.canUse()) {
                return this.heresiarch.spellType == 2;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.shootTick > 0;
        }

        @Override
        public void start() {
            super.start();
            this.heresiarch.setAnimationState(SHOOT);
            this.shootTick = 20;
            this.stopAndStare();
        }

        @Override
        public void tick() {
            super.tick();
            --this.shootTick;
            this.stopAndStare();
            if (this.shootTick == 5) {
                this.heresiarch.playSound(ModSounds.HELL_BOLT_SHOOT.get(), 2.0F, (this.heresiarch.getRandom().nextFloat() - this.heresiarch.getRandom().nextFloat()) * 0.2F + 1.0F);
                vecFromCenterToFrontOfFace(0.0F);
                vecFromCenterToFrontOfFace(-10.0F);
                vecFromCenterToFrontOfFace(10.0F);
                if (this.halfHealth()) {
                    vecFromCenterToFrontOfFace(-20.0F);
                    vecFromCenterToFrontOfFace(20.0F);
                }
            }
            if (this.halfHealth()) {
                if (this.shootTick == 9) {
                    this.heresiarch.playSound(ModSounds.HELL_BOLT_SHOOT.get(), 2.0F, (this.heresiarch.getRandom().nextFloat() - this.heresiarch.getRandom().nextFloat()) * 0.2F + 1.0F);
                    vecFromCenterToFrontOfFace(0.0F);
                    vecFromCenterToFrontOfFace(-10.0F);
                    vecFromCenterToFrontOfFace(10.0F);
                }
            }
        }

        //Based on @hexnowloading code: https://github.com/hexnowloading/DungeonNowLoading/blob/3.0-1.20.1/common/src/main/java/dev/hexnowloading/dungeonnowloading/entity/ai/chaos_spawner/ChaosSpawnerShootGhostBulletGoal.java
        private void vecFromCenterToFrontOfFace(float angle) {
            double viewDistance = 2.0F;
            Vec3 viewVector = this.heresiarch.getViewVector(1.0F);
            if (angle != 0.0F) {
                float offset = (float) Math.toRadians(angle);
                viewVector = viewVector.yRot(offset);
            }
            double d0 = viewVector.x * viewDistance;
            double d1 = viewVector.y * viewDistance;
            double d2 = viewVector.z * viewDistance;
            HellBolt hellBolt = new HellBolt(this.heresiarch, d0, d1, d2, this.heresiarch.level);
            hellBolt.setPos(hellBolt.getX() + d0, this.heresiarch.getEyeY() + d1, hellBolt.getZ() + d2);
            this.heresiarch.level.addFreshEntity(hellBolt);
        }

        @Override
        public void stop() {
            this.shootTick = 0;
            this.heresiarch.spellType = 3;
            super.stop();
        }
    }

    static class BarrageGoal extends HeresiarchGoal {
        public int barrageTick;

        public BarrageGoal(Heresiarch heresiarch) {
            super(heresiarch);
        }

        public boolean canUse() {
            if (super.canUse()) {
                return this.heresiarch.spellType == 3;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.barrageTick > 0;
        }

        @Override
        public void start() {
            super.start();
            this.heresiarch.setAnimationState(BARRAGE);
            this.barrageTick = 80;
            this.stopAndStare();
        }

        @Override
        public void tick() {
            super.tick();
            --this.barrageTick;
            this.stopAndStare();
            int speed = this.halfHealth() ? 3 : 6;
            if (this.barrageTick <= 70 && this.barrageTick > 10 && this.barrageTick % speed == 0) {
                Vec3 vector3d = this.heresiarch.getViewVector( 1.0F);
                Vec3 vec3 = vector3d;
                if (this.getTarget() != null) {
                    Vec3 pos = this.getTarget().position().add(0, 0.25, 0);
                    vec3 = pos.subtract(this.heresiarch.position().add(0, (this.heresiarch.getBbHeight() * 1.5F), 0));
                }
                SurgingOrb orb = new SurgingOrb(
                        this.heresiarch.getX() + vector3d.x / 2,
                        this.heresiarch.getY() + (this.heresiarch.getBbHeight() * 1.5F),
                        this.heresiarch.getZ() + vector3d.z / 2,
                        vec3.x,
                        vec3.y,
                        vec3.z,
                        this.heresiarch.level);
                orb.setOwner(this.heresiarch);
                orb.setOrange(true);
                orb.setExtraDamage((float) this.heresiarch.getAttributeValue(Attributes.ATTACK_DAMAGE));
                int i = 0;
                if (this.halfHealth()) {
                    ++i;
                }
                if (this.heresiarch.level.getDifficulty() == Difficulty.HARD) {
                    ++i;
                }
                orb.setBoltSpeed(i);
                this.heresiarch.level.addFreshEntity(orb);
                this.heresiarch.playSound(ModSounds.SHOCK_CAST.get(), 1.0F, (this.heresiarch.getRandom().nextFloat() - this.heresiarch.getRandom().nextFloat()) * 0.2F + 1.0F);
            }
        }

        @Override
        public void stop() {
            this.barrageTick = 0;
            this.heresiarch.spellType = 0;
            super.stop();
        }
    }
}
