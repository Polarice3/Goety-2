package com.Polarice3.Goety.common.entities.hostile.illagers;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.entities.hostile.IBoss;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.projectiles.IllBomb;
import com.Polarice3.Goety.common.entities.projectiles.MagicBolt;
import com.Polarice3.Goety.common.entities.projectiles.ViciousTooth;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SAddBossPacket;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class Minister extends HuntingIllagerEntity implements RangedAttackMob, IBoss {
    private static final EntityDataAccessor<Boolean> HAS_STAFF = SynchedEntityData.defineId(Minister.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(Minister.class, EntityDataSerializers.INT);
    public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, Goety.location("textures/entity/illagers/minister/minister.png"));
        map.put(1, Goety.location("textures/entity/illagers/minister/minister_2.png"));
        map.put(2, Goety.location("textures/entity/illagers/minister/minister_3.png"));
    });
    private final ServerBossEvent bossInfo = (ServerBossEvent) new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(false).setCreateWorldFog(false);
    private UUID bossInfoUUID = bossInfo.getId();
    public float staffDamage;
    public int coolDown;
    public int deathTime = 0;
    public float deathRotation = 0.0F;
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState castAnimationState = new AnimationState();
    public AnimationState laughAnimationState = new AnimationState();
    public AnimationState laughTargetAnimationState = new AnimationState();
    public AnimationState commandAnimationState = new AnimationState();
    public AnimationState blockAnimationState = new AnimationState();
    public AnimationState smashedAnimationState = new AnimationState();
    public AnimationState speechAnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();

    public Minister(EntityType<? extends HuntingIllagerEntity> p_i48551_1_, Level p_i48551_2_) {
        super(p_i48551_1_, p_i48551_2_);
        this.xpReward = 99;
        if (this.level.isClientSide){
            Goety.PROXY.addBoss(this);
        }
    }

    public ResourceLocation getResourceLocation() {
        return TEXTURE_BY_TYPE.getOrDefault(this.getOutfitType(), TEXTURE_BY_TYPE.get(0));
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new CastingSpellGoal());
        this.goalSelector.addGoal(3, new SpeechGoal());
        this.goalSelector.addGoal(4, new LaughTargetGoal());
        this.goalSelector.addGoal(5, new CommandGoal());
        this.goalSelector.addGoal(6, new TeethSpellGoal());
        this.goalSelector.addGoal(7, AvoidTargetGoal.newGoal(this, 4.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(8, new MinisterRangedGoal(this, 1.0D, 20, 16.0F){
            @Override
            public boolean canUse() {
                return super.canUse() && !Minister.this.hasNearbyIllagers();
            }
        });
        this.goalSelector.addGoal(8, new MinisterRangedGoal(this, 1.0D, 20, 60, 16.0F){
            @Override
            public boolean canUse() {
                return super.canUse() && Minister.this.hasNearbyIllagers();
            }
        });
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.MinisterHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.MinisterDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.MinisterHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.MinisterDamage.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE_ID, 1);
        this.entityData.define(HAS_STAFF, true);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("HasStaff", this.hasStaff());
        pCompound.putFloat("StaffDamage", this.staffDamage);
        pCompound.putInt("Outfit", this.getOutfitType());
        pCompound.putInt("CoolDown", this.coolDown);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("HasStaff")) {
            this.setHasStaff(pCompound.getBoolean("HasStaff"));
        }
        if (pCompound.contains("StaffDamage")) {
            this.staffDamage = pCompound.getFloat("StaffDamage");
        }
        if (pCompound.contains("Outfit")){
            this.setOutfitType(pCompound.getInt("Outfit"));
        }
        if (pCompound.contains("CoolDown")){
            this.coolDown = pCompound.getInt("CoolDown");
        }
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    public int getOutfitType() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setOutfitType(int pType) {
        if (pType < 0 || pType >= this.OutfitTypeNumber() + 1) {
            pType = this.random.nextInt(this.OutfitTypeNumber());
        }

        this.entityData.set(DATA_TYPE_ID, pType);
    }

    public int OutfitTypeNumber(){
        return TEXTURE_BY_TYPE.size();
    }

    public boolean hasStaff(){
        return this.entityData.get(HAS_STAFF);
    }

    public void setHasStaff(boolean staff){
        this.entityData.set(HAS_STAFF, staff);
    }

    public static final Predicate<AbstractIllager> NOT_THEMSELVES = (p_20434_) -> {
        return !(p_20434_ instanceof Minister) && !(p_20434_.getTarget() instanceof Minister);
    };

    public List<AbstractIllager> getNearbyIllagers(){
        return this.level.getEntitiesOfClass(AbstractIllager.class, this.getBoundingBox().inflate(32.0D, 16.0D, 32.0D), NOT_THEMSELVES);
    }

    public boolean hasNearbyIllagers(){
        return !this.getNearbyIllagers().isEmpty();
    }

    @Override
    public IllagerArmPose getArmPose() {
        if (this.isAggressive() || this.isCelebrating() || this.isDeadOrDying() || this.isCasting()) {
            return IllagerArmPose.NEUTRAL;
        } else {
            return IllagerArmPose.CROSSED;
        }
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.castAnimationState);
        animationStates.add(this.laughAnimationState);
        animationStates.add(this.laughTargetAnimationState);
        animationStates.add(this.commandAnimationState);
        animationStates.add(this.blockAnimationState);
        animationStates.add(this.smashedAnimationState);
        animationStates.add(this.speechAnimationState);
        animationStates.add(this.deathAnimationState);
        return animationStates;
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

    protected SoundEvent getAmbientSound() {
        return ModSounds.MINISTER_AMBIENT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.MINISTER_DEATH.get();
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.MINISTER_HURT.get();
    }

    public SoundEvent getCelebrateSound() {
        return ModSounds.MINISTER_CELEBRATE.get();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_37856_, DifficultyInstance p_37857_, MobSpawnType p_37858_, @org.jetbrains.annotations.Nullable SpawnGroupData p_37859_, @org.jetbrains.annotations.Nullable CompoundTag p_37860_) {
        this.setOutfitType(this.random.nextInt(this.OutfitTypeNumber()));
        return super.finalizeSpawn(p_37856_, p_37857_, p_37858_, p_37859_, p_37860_);
    }

    @Override
    public boolean isLeftHanded() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource p_37849_, float p_37850_) {
        if (this.hasStaff() && this.isAggressive() && !this.isCasting() && this.coolDown <= 10){
            if (this.staffDamage >= 64){
                this.setHasStaff(false);
                this.level.broadcastEntityEvent(this, (byte) 13);
                if (this.level instanceof ServerLevel serverLevel){
                    for(int i = 0; i < 20; ++i) {
                        ServerParticleUtil.addParticlesAroundSelf(serverLevel, new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ModItems.DARK_FABRIC.get())), this);
                    }
                }
                if (p_37849_.getEntity() != null){
                    MobUtil.knockBack(this, p_37849_.getEntity(), 4.0D, 0.2D, 4.0D);
                }
                this.playSound(SoundEvents.ITEM_BREAK, 4.0F, 1.0F);
                return false;
            } else if (!p_37849_.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                    && !p_37849_.is(DamageTypeTags.BYPASSES_EFFECTS)
                    && !p_37849_.is(DamageTypeTags.BYPASSES_ENCHANTMENTS)
                    && !p_37849_.is(DamageTypeTags.IS_EXPLOSION)
                    && !p_37849_.isCreativePlayer()
                    && p_37849_.getEntity() != null){
                Vec3 vec32 = p_37849_.getSourcePosition();
                if (vec32 != null) {
                    MobUtil.instaLook(Minister.this, vec32);
                    if (ModDamageSource.toolAttack(p_37849_, item -> item instanceof AxeItem)){
                        p_37850_ *= 2.0F;
                    }
                    this.staffDamage += p_37850_;
                    this.level.broadcastEntityEvent(this, (byte) 8);
                    this.playSound(SoundEvents.SHIELD_BLOCK);
                    if (this.level instanceof ServerLevel serverLevel){
                        for(int i = 0; i < 5; ++i) {
                            ServerParticleUtil.addParticlesAroundSelf(serverLevel, new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ModItems.DARK_FABRIC.get())), this);
                        }
                    }
                    if (this.level.random.nextFloat() <= 0.05F){
                        this.playSound(ModSounds.MINISTER_LAUGH.get());
                    }
                }
                return false;
            }
        }
        return super.hurt(p_37849_, p_37850_);
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 40) {
            this.spawnAnim();
            this.remove(RemovalReason.KILLED);
        }
        this.setYRot(this.deathRotation);
        this.setYBodyRot(this.deathRotation);
    }

    public void die(DamageSource p_21014_) {
        this.level.broadcastEntityEvent(this, (byte) 10);
        this.deathRotation = this.getYRot();
        if (this.level instanceof ServerLevel serverLevel){
            for (Player player : serverLevel.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(32.0F))){
                SEHelper.setRestPeriod(player, MathHelper.minecraftDayToTicks(3));
            }
        }
        super.die(p_21014_);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource p_21385_, int p_21386_, boolean p_21387_) {
        super.dropCustomDeathLoot(p_21385_, p_21386_, p_21387_);
        if (this.hasStaff()){
            ItemStack itemStack = ModItems.OMINOUS_ORB.get().getDefaultInstance();
            ItemEntity itementity = this.spawnAtLocation(itemStack);
            if (itementity != null) {
                itementity.setExtendedLifetime();
            }
        }
    }

    @Nullable
    @Override
    public ItemEntity spawnAtLocation(ItemStack itemStack, float p_19986_) {
        if (itemStack.isEmpty()) {
            return null;
        } else if (this.level.isClientSide) {
            return null;
        } else {
            ItemEntity itementity = new ItemEntity(this.level, this.getX(), this.getY() + (double)p_19986_, this.getZ(), itemStack
                    , this.random.nextDouble() * 0.4D - 0.2D, 0.4D, this.random.nextDouble() * 0.4D - 0.2D);
            itementity.setDefaultPickUpDelay();
            if (this.captureDrops() != null) {
                this.captureDrops().add(itementity);
            } else {
                this.level.addFreshEntity(itementity);
            }
            return itementity;
        }
    }

    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setVisible(MainConfig.SpecialBossBar.get());
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
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
    public void remove(RemovalReason p_146834_) {
        if (this.level.isClientSide) {
            Goety.PROXY.removeBoss(this);
        }
        super.remove(p_146834_);
    }

    public void tick() {
        super.tick();
        if (this.isCelebrating()){
            if (this.tickCount % 100 == 0 && this.hurtTime <= 0){
                this.laughAnimationState.start(this.tickCount);
                this.level.broadcastEntityEvent(this, (byte) 6);
            }
        }
        if (this.isDeadOrDying()){
            for (AnimationState animationState : this.getAnimations()){
                if (animationState != deathAnimationState){
                    animationState.stop();
                }
            }
            this.setYRot(this.deathRotation);
            this.setYBodyRot(this.deathRotation);
        }
        if (this.level instanceof ServerLevel serverLevel){
            if (this.hasStaff()) {
                if (!this.getMainHandItem().isEmpty()) {
                    if (this.getOffhandItem().isEmpty()) {
                        this.setItemSlot(EquipmentSlot.OFFHAND, this.getOffhandItem());
                    } else {
                        this.spawnAtLocation(this.getMainHandItem());
                    }
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                }
            }
            ServerParticleUtil.addAuraParticles(serverLevel, ParticleTypes.ENCHANT, this, 8.0F);
            for (LivingEntity living : serverLevel.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(8.0F, 4.0F, 8.0F))) {
                if (living.getMobType() == MobType.ILLAGER && living != this) {
                    living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 0, false, false));
                }
            }
            if (this.coolDown > 0){
                --this.coolDown;
            }
            if (this.getTarget() != null){
                serverLevel.broadcastEntityEvent(this, (byte) 14);
            } else {
                serverLevel.broadcastEntityEvent(this, (byte) 15);
            }
            this.setAggressive(this.getTarget() != null);
        }
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_) {

    }

    @Override
    public void performRangedAttack(LivingEntity p_33317_, float p_33318_) {
        if (this.hasStaff()) {
            Vec3 vector3d = this.getViewVector(1.0F);
            double d1 = p_33317_.getX() - this.getX();
            double d2 = p_33317_.getY(0.5D) - this.getY(0.5D);
            double d3 = p_33317_.getZ() - this.getZ();
            MagicBolt magicBolt = new MagicBolt(this.level, this, d1, d2, d3);
            magicBolt.setYRot(this.getYRot());
            magicBolt.setXRot(this.getXRot());
            magicBolt.setPos(this.getX() + vector3d.x / 2, this.getEyeY() - 0.2, this.getZ() + vector3d.z / 2);
            this.playSound(ModSounds.CAST_SPELL.get(), 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            this.level.addFreshEntity(magicBolt);
        } else {
            IllBomb snowball = new IllBomb(this, this.level);
            double d0 = p_33317_.getEyeY() - (double)1.1F;
            double d1 = p_33317_.getX() - this.getX();
            double d2 = d0 - snowball.getY();
            double d3 = p_33317_.getZ() - this.getZ();
            double d4 = Math.sqrt(d1 * d1 + d3 * d3) * (double)0.2F;
            float velocity = p_33317_.distanceTo(this) >= 10.0F ? 1.0F : 0.5F;
            snowball.shoot(d1, d2 + d4, d3, velocity, 0.5F);
            this.playSound(SoundEvents.WITCH_THROW, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            this.level.addFreshEntity(snowball);
        }
    }

    public void handleEntityEvent(byte pId) {
        if (pId == 4) {
            this.attackAnimationState.start(this.tickCount);
        } else if (pId == 5) {
            this.castAnimationState.start(this.tickCount);
        } else if (pId == 6) {
            this.laughAnimationState.start(this.tickCount);
        } else if (pId == 7){
            this.laughTargetAnimationState.start(this.tickCount);
        } else if (pId == 8){
            this.blockAnimationState.start(this.tickCount);
        } else if (pId == 10){
            this.deathAnimationState.start(this.tickCount);
            this.deathRotation = this.getYRot();
            this.playSound(ModSounds.MINISTER_DEATH.get(), 4.0F, 1.0F);
        } else if (pId == 11){
            this.speechAnimationState.start(this.tickCount);
        } else if (pId == 12){
            this.commandAnimationState.start(this.tickCount);
        } else if (pId == 13){
            this.smashedAnimationState.start(this.tickCount);
            this.setHasStaff(false);
        } else if (pId == 14){
            this.setAggressive(true);
        } else if (pId == 15){
            this.setAggressive(false);
        } else {
            super.handleEntityEvent(pId);
        }
    }

    @Override
    public UUID getBossInfoUUID() {
        return this.bossInfoUUID;
    }

    @Override
    public void setBossInfoUUID(UUID bossInfoUUID) {
        this.bossInfoUUID = bossInfoUUID;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) ModNetwork.INSTANCE.toVanillaPacket(new SAddBossPacket(new ClientboundAddEntityPacket(this), bossInfoUUID), NetworkDirection.PLAY_TO_CLIENT);
    }

    class CastingSpellGoal extends SpellcasterCastingSpellGoal {
        private CastingSpellGoal() {
        }

        public void tick() {
            if (Minister.this.getTarget() != null) {
                MobUtil.instaLook(Minister.this, Minister.this.getTarget());
                Minister.this.getLookControl().setLookAt(Minister.this.getTarget(), 500.0F, (float)Minister.this.getMaxHeadXRot());
            }
        }
    }

    abstract class CastingGoal extends SpellcasterUseSpellGoal {
        public boolean hasCastSound;

        @Override
        public boolean canUse() {
            return super.canUse() && !Minister.this.isCasting() && Minister.this.coolDown <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && (Minister.this.isCasting() || Minister.this.coolDown > 0);
        }

        public void start() {
            super.start();
            Minister.this.setCasting(true);
            Minister.this.coolDown = 20;
        }

        public void stop() {
            super.stop();
            Minister.this.setCasting(false);
            Minister.this.coolDown = 20;
        }

        public void tick() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
                if (this.hasCastSound) {
                    Minister.this.playSound(Minister.this.getCastingSoundEvent(), 1.0F, 1.0F);
                }
            }

        }

    }

    class TeethSpellGoal extends CastingGoal {
        public int teethAmount;

        private TeethSpellGoal() {
            this.hasCastSound = true;
        }

        @Override
        public void start() {
            super.start();
            Minister.this.castAnimationState.start(Minister.this.tickCount);
            Minister.this.level.broadcastEntityEvent(Minister.this, (byte) 5);
        }

        protected int getCastingTime() {
            return 30;
        }

        protected int getCastingInterval() {
            if (Minister.this.hasNearbyIllagers()){
                return 360;
            } else {
                return 120;
            }
        }

        protected void performSpellCasting() {
            if (Minister.this.getTarget() != null) {
                BlockPos blockPos = Minister.this.getTarget().blockPosition();
                if (Minister.this.getTarget().distanceTo(Minister.this) <= 4.0F){
                    this.surroundTeeth();
                } else {
                    for (int length = 0; length < 16; length++) {
                        blockPos = blockPos.offset(-2 + Minister.this.getRandom().nextInt(4), 0, -2 + Minister.this.getRandom().nextInt(4));
                        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());

                        while(blockpos$mutable.getY() < blockPos.getY() + 8.0D && !Minister.this.level.getBlockState(blockpos$mutable).blocksMotion()) {
                            blockpos$mutable.move(Direction.UP);
                        }

                        if (Minister.this.level.noCollision(new AABB(blockpos$mutable))){
                            ++this.teethAmount;
                            ViciousTooth viciousTooth = new ViciousTooth(ModEntityType.VICIOUS_TOOTH.get(), Minister.this.level);
                            viciousTooth.setPos(Vec3.atCenterOf(blockpos$mutable));
                            viciousTooth.setOwner(Minister.this);
                            if (Minister.this.level.addFreshEntity(viciousTooth)) {
                                viciousTooth.playSound(ModSounds.TOOTH_SPAWN.get());
                            }
                        }
                    }
                }
                if (this.teethAmount <= 0){
                    this.surroundTeeth();
                }
            }
        }

        public void surroundTeeth(){
            if (Minister.this.getTarget() != null) {
                BlockPos blockPos = Minister.this.blockPosition();
                BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());

                while (blockpos$mutable.getY() < blockPos.getY() + 8.0D && !Minister.this.level.getBlockState(blockpos$mutable).blocksMotion()) {
                    blockpos$mutable.move(Direction.UP);
                }

                float f = (float) Mth.atan2(Minister.this.getTarget().getZ() - blockPos.getZ(), Minister.this.getTarget().getX() - blockPos.getX());
                for (int i = 0; i < 5; ++i) {
                    float f1 = f + (float) i * (float) Math.PI * 0.4F;
                    ViciousTooth viciousTooth = new ViciousTooth(ModEntityType.VICIOUS_TOOTH.get(), Minister.this.level);
                    viciousTooth.setPos(blockPos.getX() + (double) Mth.cos(f1) * 1.5D, blockpos$mutable.getY(), blockPos.getZ() + (double) Mth.cos(f1) * 1.5D);
                    viciousTooth.setOwner(Minister.this);
                    if (Minister.this.level.addFreshEntity(viciousTooth)) {
                        viciousTooth.playSound(ModSounds.TOOTH_SPAWN.get());
                    }
                }
                for (int k = 0; k < 8; ++k) {
                    float f2 = f + (float) k * (float) Math.PI * 2.0F / 8.0F + 1.2566371F;
                    ViciousTooth viciousTooth = new ViciousTooth(ModEntityType.VICIOUS_TOOTH.get(), Minister.this.level);
                    viciousTooth.setPos(blockPos.getX() + (double) Mth.cos(f2) * 2.5D, blockpos$mutable.getY(), blockPos.getZ() + (double) Mth.sin(f2) * 2.5D);
                    viciousTooth.setOwner(Minister.this);
                    if (Minister.this.level.addFreshEntity(viciousTooth)) {
                        viciousTooth.playSound(ModSounds.TOOTH_SPAWN.get());
                    }
                }
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.MINISTER_CAST.get();
        }

        protected IllagerSpell getSpell() {
            return IllagerSpell.FANGS;
        }
    }

    class SpeechGoal extends CastingGoal{
        private SpeechGoal(){
            this.hasCastSound = false;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && Minister.this.hasNearbyIllagers();
        }

        @Override
        public void start() {
            super.start();
            Minister.this.speechAnimationState.start(Minister.this.tickCount);
            Minister.this.level.broadcastEntityEvent(Minister.this, (byte) 11);
        }

        protected int getCastingTime() {
            return MathHelper.secondsToTicks(3);
        }

        protected int getCastingInterval() {
            return MathHelper.secondsToTicks(10);
        }

        @Override
        public void tick() {
            super.tick();
            for (AbstractIllager abstractIllager : Minister.this.getNearbyIllagers()){
                if (abstractIllager.isAlive() && abstractIllager.getMaxHealth() < Minister.this.getMaxHealth() && abstractIllager.getTarget() != Minister.this && (abstractIllager.getLastHurtByMob() == null || !abstractIllager.isAlliedTo(abstractIllager.getLastHurtByMob()))){
                    abstractIllager.setTarget(null);
                    abstractIllager.setAggressive(false);
                    abstractIllager.getNavigation().stop();
                    MobUtil.instaLook(abstractIllager, Minister.this);
                    abstractIllager.getLookControl().setLookAt(Minister.this, 500.0F, abstractIllager.getMaxHeadXRot());
                }
            }
        }

        protected void performSpellCasting() {
            if (Minister.this.hasNearbyIllagers()){
                for (AbstractIllager abstractIllager : Minister.this.getNearbyIllagers()){
                    if (abstractIllager.isAlive() && abstractIllager.getTarget() != Minister.this && abstractIllager.getMaxHealth() < Minister.this.getMaxHealth()){
                        abstractIllager.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, MathHelper.secondsToTicks(30)));
                    }
                }
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.MINISTER_SPEECH.get();
        }

        protected IllagerSpell getSpell() {
            return IllagerSpell.BLINDNESS;
        }
    }

    class CommandGoal extends CastingGoal{
        private CommandGoal(){
            this.hasCastSound = false;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && Minister.this.hasNearbyIllagers() && Minister.this.getLastHurtByMob() != null
                    && Minister.this.getTarget() == Minister.this.getLastHurtByMob();
        }

        @Override
        public void start() {
            super.start();
            Minister.this.commandAnimationState.start(Minister.this.tickCount);
            Minister.this.level.broadcastEntityEvent(Minister.this, (byte) 12);
        }

        protected int getCastingTime() {
            return MathHelper.secondsToTicks(1);
        }

        protected int getCastingInterval() {
            return 100;
        }

        protected void performSpellCasting() {
            if (Minister.this.hasNearbyIllagers()){
                for (AbstractIllager abstractIllager : Minister.this.getNearbyIllagers()){
                    if (abstractIllager.getMaxHealth() < Minister.this.getMaxHealth() && Minister.this.getTarget() != null && abstractIllager.getTarget() != Minister.this.getTarget()){
                        abstractIllager.setTarget(Minister.this.getTarget());
                    }
                }
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.MINISTER_COMMAND.get();
        }

        protected IllagerSpell getSpell() {
            return IllagerSpell.SUMMON_VEX;
        }
    }

    class LaughTargetGoal extends CastingGoal{
        private LaughTargetGoal(){
            this.hasCastSound = false;
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = Minister.this.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                return !livingentity.hasEffect(MobEffects.WEAKNESS) && livingentity.canBeAffected(new MobEffectInstance(MobEffects.WEAKNESS)) && livingentity.distanceTo(Minister.this) <= 16.0F && super.canUse();
            }
            return false;
        }

        @Override
        public void start() {
            super.start();
            Minister.this.level.broadcastEntityEvent(Minister.this, (byte) 7);
        }

        @Override
        protected int getCastingTime() {
            return 20;
        }

        protected int getCastingInterval() {
            return MathHelper.secondsToTicks(15);
        }

        @Override
        protected void performSpellCasting() {
            LivingEntity livingentity = Minister.this.getTarget();
            if (livingentity != null) {
                livingentity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, MathHelper.secondsToTicks(30)), Minister.this);
            }
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.MINISTER_LAUGH.get();
        }

        @Override
        protected IllagerSpell getSpell() {
            return IllagerSpell.WOLOLO;
        }
    }

    public class MinisterRangedGoal extends Goal{
        private final Minister mob;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private int seeTime;
        private final int attackIntervalMin;
        private final int attackIntervalMax;
        private final float attackRadius;
        private final float attackRadiusSqr;

        public MinisterRangedGoal(Minister p_25768_, double speed, int attackInterval, float attackRadius) {
            this(p_25768_, speed, attackInterval, attackInterval, attackRadius);
        }

        public MinisterRangedGoal(Minister mob, double speed, int attackMin, int attackMax, float attackRadius) {
            this.mob = mob;
            this.speedModifier = speed;
            this.attackIntervalMin = attackMin;
            this.attackIntervalMax = attackMax;
            this.attackRadius = attackRadius;
            this.attackRadiusSqr = attackRadius * attackRadius;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return true;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.canUse() || (this.target != null && this.target.isAlive() && !this.mob.getNavigation().isDone());
        }

        public void stop() {
            this.target = null;
            this.seeTime = 0;
            this.attackTime = -1;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.target != null) {
                double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
                boolean flag = this.mob.getSensing().hasLineOfSight(this.target);
                if (flag) {
                    ++this.seeTime;
                } else {
                    this.seeTime = 0;
                }

                if (!(d0 > (double) this.attackRadiusSqr) && this.seeTime >= 5) {
                    this.mob.getNavigation().stop();
                } else {
                    this.mob.getNavigation().moveTo(this.target, this.speedModifier);
                }

                this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                --this.attackTime;
                if (this.attackTime == 5) {
                    this.mob.attackAnimationState.start(Minister.this.tickCount);
                    this.mob.level.broadcastEntityEvent(Minister.this, (byte) 4);
                } else if (this.attackTime == 0) {
                    if (!flag) {
                        return;
                    }

                    float f = (float) Math.sqrt(d0) / this.attackRadius;
                    float f1 = Mth.clamp(f, 0.1F, 1.0F);
                    this.mob.performRangedAttack(this.target, f1);
                    this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
                } else if (this.attackTime < 0) {
                    this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius, (double) this.attackIntervalMin, (double) this.attackIntervalMax));
                }
            }
        }
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }
}
