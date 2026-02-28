package com.Polarice3.Goety.common.entities.ally.illager.raider;

import com.Polarice3.Goety.api.entities.ITrainable;
import com.Polarice3.Goety.common.advancements.ModCriteriaTriggers;
import com.Polarice3.Goety.common.blocks.entities.OminousIdolBlockEntity;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.WitchServant;
import com.Polarice3.Goety.common.entities.ally.undead.bound.AbstractBoundIllager;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.Hellfire;
import com.Polarice3.Goety.common.entities.projectiles.SpellEntity;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.common.items.magic.TaglockKit;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public abstract class RaiderServant extends Summoned {
    protected static final EntityDataAccessor<Boolean> IS_CELEBRATING = SynchedEntityData.defineId(RaiderServant.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> CAPTURE_MODE = SynchedEntityData.defineId(RaiderServant.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Optional<UUID>> MARKED_ID = SynchedEntityData.defineId(RaiderServant.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Optional<UUID>> LEADER_ID = SynchedEntityData.defineId(RaiderServant.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> LEADER_CLIENT_ID = SynchedEntityData.defineId(RaiderServant.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Optional<BlockPos>> RAID_POS = SynchedEntityData.defineId(RaiderServant.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    protected static final EntityDataAccessor<String> RAID_DIM = SynchedEntityData.defineId(RaiderServant.class, EntityDataSerializers.STRING);
    @Nullable
    public BlockPos revivePos;
    public String reviveDim = Level.OVERWORLD.location().toString();
    public int traderId;
    private int celebrationTime;
    private int raidTime;
    private boolean missionComplete = false;

    public RaiderServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.miscGoal();
        this.goalSelector.addGoal(1, new ObtainLeaderBannerGoal<>(this));
        this.goalSelector.addGoal(3, new PathfindToRaidGoal<>(this));
        this.goalSelector.addGoal(4, new RaiderMoveThroughVillageGoal(this, 1.05D, 1));
        this.goalSelector.addGoal(5, new CelebrateGoal(this));
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(6, new FollowOwnerGoal<>(this, 1.0D, 10.0F, 2.0F){
            @Override
            public boolean canUse() {
                if (RaiderServant.this.isRaiding()){
                    return false;
                }
                if (RaiderServant.this instanceof ITrainable trainable) {
                    if (trainable.isTraining()) {
                        return false;
                    }
                }
                if (RaiderServant.this.getLeader() != null){
                    LivingEntity livingentity = RaiderServant.this.getLeader();
                    if (livingentity == null) {
                        return false;
                    } else if (livingentity.isSpectator()) {
                        return false;
                    } else if (RaiderServant.this.distanceToSqr(livingentity) < (double)(Mth.square(this.startDistance))) {
                        return false;
                    } else if (!RaiderServant.this.isFollowing() || RaiderServant.this.isCommanded()) {
                        return false;
                    } else if (RaiderServant.this.getTarget() != null) {
                        return false;
                    } else if (RaiderServant.this.isWoundedOrCrippled()) {
                        return false;
                    } else {
                        this.owner = livingentity;
                        return true;
                    }
                }
                return super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                if (RaiderServant.this instanceof ITrainable trainable) {
                    if (trainable.isTraining()) {
                        return false;
                    }
                }
                return super.canContinueToUse();
            }
        });
    }

    @Override
    public void targetSelectGoal() {
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false, livingEntity -> this.isRaiding() && !livingEntity.isBaby()));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, false, livingEntity -> this.isRaiding() && !livingEntity.isBaby() && livingEntity.getType().is(ModTags.EntityTypes.VILLAGE_GUARDS)));
        super.targetSelectGoal();
    }

    public void miscGoal() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new RaiderWanderGoal<>(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CELEBRATING, false);
        this.entityData.define(CAPTURE_MODE, false);
        this.entityData.define(MARKED_ID, Optional.empty());
        this.entityData.define(LEADER_ID, Optional.empty());
        this.entityData.define(LEADER_CLIENT_ID, -1);
        this.entityData.define(RAID_POS, Optional.empty());
        this.entityData.define(RAID_DIM, Level.OVERWORLD.location().toString());
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getMarkedId() != null) {
            compound.putUUID("Marked", this.getMarkedId());
        }
        if (this.getLeaderId() != null) {
            compound.putUUID("Leader", this.getLeaderId());
        }
        if (this.getLeaderClientId() > -1) {
            compound.putInt("LeaderClient", this.getLeaderClientId());
        }
        if (this.getRaidPos() != null) {
            compound.put("RaidPos", NbtUtils.writeBlockPos(this.getRaidPos()));
            compound.putString("RaidDim", this.getRaidDim());
        }
        compound.putInt("CelebrationTime", this.celebrationTime);
        compound.putInt("RaidTime", this.raidTime);
        compound.putInt("TraderId", this.traderId);
        compound.putBoolean("MissionComplete", this.missionComplete);
        compound.putBoolean("Capturing", this.isCapturing());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Marked")) {
            this.setMarkedId(compound.getUUID("Marked"));
        }
        if (compound.contains("Leader")) {
            this.setLeaderId(compound.getUUID("Leader"));
        }
        if (compound.contains("LeaderClient")) {
            this.setLeaderClientId(compound.getInt("LeaderClient"));
        }
        if (compound.contains("RaidPos")) {
            this.setRaidPos(NbtUtils.readBlockPos(compound.getCompound("RaidPos")));
            this.setRaidDim(compound.getString("RaidDim"));
        }
        if (compound.contains("CelebrationTime")) {
            this.celebrationTime = compound.getInt("CelebrationTime");
        }
        if (compound.contains("RaidTime")) {
            this.raidTime = compound.getInt("RaidTime");
        }
        if (compound.contains("TraderId")) {
            this.traderId = compound.getInt("TraderId");
        }
        if (compound.contains("MissionComplete")) {
            this.missionComplete = compound.getBoolean("MissionComplete");
        }
        if (compound.contains("Capturing")) {
            this.setCaptureMode(compound.getBoolean("Capturing"));
        }
    }

    @Nullable
    public LivingEntity getMarked() {
        UUID uuid = this.getMarkedId();
        return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
    }

    @Nullable
    public UUID getMarkedId() {
        return this.entityData.get(MARKED_ID).orElse(null);
    }

    public void setMarkedId(@Nullable UUID p_184754_1_) {
        this.entityData.set(MARKED_ID, Optional.ofNullable(p_184754_1_));
    }

    public void setMarked(@Nullable LivingEntity livingEntity){
        if (livingEntity != null) {
            this.setMarkedId(livingEntity.getUUID());
        } else {
            this.setMarkedId(null);
        }
    }

    @Nullable
    public RaiderServant getLeader() {
        if (!this.level.isClientSide) {
            UUID uuid = this.getLeaderId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid) instanceof RaiderServant servant && servant.isAlive() && servant.canBeLeader() && servant != this ? servant : null;
        } else {
            int id = this.getLeaderClientId();
            return id <= -1 ? null : this.level.getEntity(this.getLeaderClientId()) instanceof RaiderServant servant && servant.isAlive() && servant.canBeLeader() && servant != this ? servant : null;
        }
    }

    @Nullable
    public UUID getLeaderId() {
        return this.entityData.get(LEADER_ID).orElse(null);
    }

    public void setLeaderId(@Nullable UUID p_184754_1_) {
        this.entityData.set(LEADER_ID, Optional.ofNullable(p_184754_1_));
    }

    public int getLeaderClientId() {
        return this.entityData.get(LEADER_CLIENT_ID);
    }

    public void setLeaderClientId(int p_184754_1_) {
        this.entityData.set(LEADER_CLIENT_ID, p_184754_1_);
    }

    public void setLeader(@Nullable RaiderServant livingEntity){
        if (livingEntity != null && livingEntity != this) {
            this.setLeaderId(livingEntity.getUUID());
            this.setLeaderClientId(livingEntity.getId());
            this.getNavigation().stop();
        } else {
            this.setLeaderId(null);
            this.setLeaderClientId(-1);
        }
    }

    public boolean isLeader(){
        if (this.canBeLeader()) {
            if (this.getLeaderBannerInstance().isEmpty()) {
                return this.getItemBySlot(EquipmentSlot.HEAD).is(ItemTags.BANNERS);
            } else {
                return ItemStack.matches(this.getItemBySlot(EquipmentSlot.HEAD), this.getLeaderBannerInstance());
            }
        }
        return false;
    }

    public boolean isFollower(){
        return this.getLeader() != null;
    }

    public int getTraderId(){
        return this.traderId;
    }

    public void setTraderId(int id) {
        this.traderId = id;
    }

    @Nullable
    public LivingEntity getTrader() {
        Entity entity = this.level.getEntity(this.getTraderId());
        if (entity instanceof LivingEntity livingEntity) {
            return livingEntity;
        }
        return null;
    }

    public void setTrader(@Nullable LivingEntity livingEntity) {
        if (livingEntity != null) {
            this.setTraderId(livingEntity.getId());
        } else {
            this.setTraderId(-1);
        }
    }

    @Nullable
    public BlockPos getRaidPos(){
        return this.entityData.get(RAID_POS).orElse(null);
    }

    public void setRaidPos(@Nullable BlockPos blockPos) {
        this.entityData.set(RAID_POS, Optional.ofNullable(blockPos));
    }

    public ResourceKey<Level> getRaidLevel() {
        ResourceLocation resourcelocation = new ResourceLocation(this.getRaidDim());
        return ResourceKey.create(Registries.DIMENSION, resourcelocation);
    }

    public String getRaidDim(){
        return this.entityData.get(RAID_DIM);
    }

    public void setRaidDim(ResourceKey<Level> resourceKey) {
        this.setRaidDim(resourceKey.location().toString());
    }

    public void setRaidDim(String string) {
        this.entityData.set(RAID_DIM, string);
    }

    public boolean isRaiding() {
        return this.getRaidPos() != null;
    }

    public void setCaptureMode(boolean captureMode) {
        this.entityData.set(CAPTURE_MODE, captureMode);
    }

    public boolean isCapturing() {
        return this.entityData.get(CAPTURE_MODE);
    }

    public double getMyRidingOffset() {
        return -0.45D;
    }

    @Nullable
    public BlockPos findRandomSpawnPos(int p_37708_, int p_37709_) {
        if (this.level instanceof ServerLevel serverLevel) {
            if (this.getRaidPos() != null) {
                int i = p_37708_ == 0 ? 2 : 2 - p_37708_;
                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                for (int i1 = 0; i1 < p_37709_; ++i1) {
                    float f = this.level.random.nextFloat() * ((float) Math.PI * 2F);
                    int j = this.getRaidPos().getX() + Mth.floor(Mth.cos(f) * 32.0F * (float) i) + this.level.random.nextInt(5);
                    int l = this.getRaidPos().getZ() + Mth.floor(Mth.sin(f) * 32.0F * (float) i) + this.level.random.nextInt(5);
                    int k = this.level.getHeight(Heightmap.Types.WORLD_SURFACE, j, l);
                    blockpos$mutableblockpos.set(j, k, l);
                    if (!serverLevel.isVillage(blockpos$mutableblockpos) || p_37708_ >= 2) {
                        int j1 = 10;
                        if (this.level.hasChunksAt(blockpos$mutableblockpos.getX() - j1, blockpos$mutableblockpos.getZ() - j1, blockpos$mutableblockpos.getX() + j1, blockpos$mutableblockpos.getZ() + j1) && serverLevel.isPositionEntityTicking(blockpos$mutableblockpos) && (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, this.level, blockpos$mutableblockpos, EntityType.RAVAGER) || this.level.getBlockState(blockpos$mutableblockpos.below()).is(Blocks.SNOW) && this.level.getBlockState(blockpos$mutableblockpos).isAir())) {
                            return blockpos$mutableblockpos;
                        }
                    }
                }
            }
        }

        return null;
    }

    public boolean isCelebrating() {
        return this.entityData.get(IS_CELEBRATING);
    }

    public void setCelebrating(boolean p_37900_) {
        this.entityData.set(IS_CELEBRATING, p_37900_);
    }

    public SoundEvent getCelebrateSound() {
        return SoundEvents.EMPTY;
    }

    @Override
    public void updateMoveMode(Player player) {
        if (this.getLeader() != null){
            player.displayClientMessage(Component.translatable("info.goety.servant.removeLeader", this.getDisplayName(), this.getLeader().getDisplayName()), true);
            this.setLeader(null);
            this.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED, 1.0f, 1.0f);
        } else {
            super.updateMoveMode(player);
        }
    }

    public void setCommandPosEntityOrder(LivingEntity living){
        if (living instanceof RaiderServant servant){
            RaiderServant leader = null;
            if (servant != this) {
                if (servant.isLeader()) {
                    leader = servant;
                } else if (servant.isFollower()) {
                    leader = servant.getLeader();
                }
            }
            if (leader != null && this.canBeFollower()) {
                this.setLeader(leader);
                this.setFollowing();
                if (this.getTrueOwner() instanceof Player player) {
                    player.displayClientMessage(Component.translatable("info.goety.servant.setLeaderGroup", leader.getDisplayName()), true);
                }
            } else {
                super.setCommandPosEntity(living);
            }
        } else {
            super.setCommandPosEntity(living);
        }
    }

    public void setCommandPosEntity(LivingEntity living){
        if (living instanceof RaiderServant servant){
            RaiderServant leader = null;
            if (servant != this) {
                if (servant.isLeader()) {
                    leader = servant;
                } else if (servant.isFollower()) {
                    leader = servant.getLeader();
                }
            }
            if (leader != null && this.canBeFollower()) {
                this.setLeader(leader);
                this.setFollowing();
                if (this.getTrueOwner() instanceof Player player) {
                    player.displayClientMessage(Component.translatable("info.goety.servant.setLeader", this.getDisplayName(), leader.getDisplayName()), true);
                }
            } else {
                super.setCommandPosEntity(living);
            }
        } else {
            super.setCommandPosEntity(living);
        }
    }

    @Override
    public void die(DamageSource pCause) {
        if (this.getMarked() != null){
            this.setMarked(null);
        }
        if (this.getRaidPos() != null){
            this.setRaidPos(null);
        }
        super.die(pCause);
    }

    @Override
    public void onCeaseFire(ServerPlayer player) {
        if (player.getMainHandItem().is(ItemTags.BANNERS) || player.getOffhandItem().is(ItemTags.BANNERS)){
            if (this.getRaidPos() != null) {
                if (this.isLeader()) {
                    player.displayClientMessage(Component.translatable("info.goety.servant.stopRaid", this.getRaidPos().getX(), this.getRaidPos().getY(), this.getRaidPos().getZ()), false);
                }
                this.setRaidPos(null);
            }
        }
    }

    public boolean canCelebrate(){
        return true;
    }

    @Override
    public boolean shouldChunkLoad() {
        boolean flag = super.shouldChunkLoad();
        if (!flag) {
            if (this.getMarked() != null) {
                flag = MobsConfig.IllagerServantChunkLoadMark.get();
            }
            if (this.isRaiding()) {
                flag = MobsConfig.IllagerServantChunkLoadRaid.get();
            }
            if (this.isCelebrating()) {
                flag = MobsConfig.IllagerServantChunkLoadMark.get() || MobsConfig.IllagerServantChunkLoadRaid.get();
            }
        }
        return flag;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (this.celebrationTime > 0) {
                --this.celebrationTime;
            } else {
                if (this.missionComplete) {
                    if (this.getTrueOwner() != null
                            && this.getTrueOwner().distanceTo(this) > 32.0D
                            && MobUtil.sameDimension(this, this.getTrueOwner())) {
                        this.teleportTowards(this.getTrueOwner());
                    }
                    this.missionComplete = false;
                }
            }
            if (this.getLeader() != null) {
                if ((this.getLeader().tickCount >= 100 && !this.getLeader().isLeader()) || this.getLeader().isDeadOrDying() || this.isLeader()) {
                    this.setLeader(null);
                } else {
                    if (this.getLeader().tickCount < 20) {
                        Entity entity = this.level.getEntity(this.getOwnerClientId());
                        if (entity instanceof RaiderServant raiderServant) {
                            if (raiderServant != this.getLeader()) {
                                this.setLeaderClientId(this.getLeader().getId());
                            }
                        } else {
                            this.setLeaderClientId(this.getLeader().getId());
                        }
                    }
                    if (this.canLinkToIdol()) {
                        if (this.getLeader().getIdol() != null) {
                            OminousIdolBlockEntity idol = this.getLeader().getIdol();
                            if (idol != null) {
                                if (this.getIdol() == null || this.getIdol() != idol) {
                                    if (idol.hasSpace()) {
                                        if (this.getIdol() != null) {
                                            this.getIdol().removeIllager(this);
                                        }
                                        idol.addIllager(this);
                                        this.setRevivePos(idol.getBlockPos());
                                        if (idol.getLevel() != null) {
                                            this.setReviveDim(idol.getLevel().dimension());
                                        } else {
                                            this.setReviveDim(this.getLeader().getReviveDim());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!(this instanceof ITrainable trainable && trainable.isTraining())
                            && !this.isWoundedOrCrippled()) {
                        if (this.getLeader().getMarked() != null && this.getLeader().getMarked() != this.getMarked()) {
                            this.setMarked(this.getLeader().getMarked());
                        }
                        if (this.getLeader().getRaidPos() != null && !BlockFinder.samePos(this.getLeader().getRaidPos(), this.getRaidPos())) {
                            this.setRaidPos(this.getLeader().getRaidPos());
                        } else if (this.getLeader().getTarget() != null && this.getPriorityTarget() == null && this.getTarget() == null) {
                            this.setTarget(this.getLeader().getTarget());
                        }
                    }
                    this.setCaptureMode(this.getLeader().isCapturing());
                    if (this.getLeader().getTrueOwner() != null) {
                        if (this.getTrueOwner() == null || this.getTrueOwner() != this.getLeader().getTrueOwner()) {
                            this.setTrueOwner(this.getLeader().getTrueOwner());
                        }
                    }
                }
            }
            this.updateIdol();
            this.markedTick();
            this.raidTick();
        }
    }

    public void updateIdol() {
        if (this.getIdol() != null) {
            if (!this.getIdol().getIllagers().contains(this) && this.getIdol().hasSpace()) {
                this.getIdol().addIllager(this);
            }
        }
    }

    public void markedTick(){
        if (this.getMarked() != null) {
            try {
                if (this.getTarget() == null) {
                    if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(this.getMarked())
                            && MobUtil.sameDimension(this, this.getMarked())
                            && this.level.isLoaded(this.getMarked().blockPosition())) {
                        this.setTarget(this.getMarked());
                        if (this.getControlledVehicle() instanceof Mob mob) {
                            mob.setTarget(this.getMarked());
                        }
                    }
                } else if (this.getTarget() == this.getMarked()){
                    if (this.getTarget().distanceTo(this) > 64.0D
                            && MobUtil.sameDimension(this, this.getMarked())){
                        this.teleportTowards(this.getMarked());
                    }
                }
                if (this.getTrueOwner() != null) {
                    if (MobUtil.areAllies(this.getMarked(), this.getTrueOwner())) {
                        if (this.getTrueOwner().distanceTo(this) > 32.0D
                                && MobUtil.sameDimension(this, this.getTrueOwner())) {
                            this.teleportTowards(this.getTrueOwner());
                        }
                        if (this.getMarked() != null){
                            this.setMarked(null);
                        }
                    }
                }
                if (this.getMarked().isDeadOrDying()) {
                    this.celebrationTime = 600;
                    this.missionComplete = true;
                    if (this.getMarked() != null){
                        this.setMarked(null);
                    }
                } else {
                    this.chunkLoadTarget(this.getMarked().blockPosition());
                    if (this.isLeader()) {
                        for (RaiderServant servant : this.getNearbyCompanions()) {
                            if (servant.getLeader() == null && servant.getMarked() == this.getMarked()) {
                                servant.setLeader(this);
                                servant.setFollowing();
                            }
                        }
                    }
                }
            } catch (NullPointerException exception) {
                this.celebrationTime = 600;
                this.missionComplete = true;
                this.setMarked(null);
            }
        }
    }

    public void raidTick(){
        if (this.level instanceof ServerLevel serverLevel) {
            if (this.getRaidPos() != null) {
                if (this.level.dimension() != this.getRaidLevel()){
                    this.setRaidPos(null);
                    if (this.getTrueOwner() != null
                            && this.getTrueOwner().distanceTo(this) > 32.0D
                            && MobUtil.sameDimension(this, this.getTrueOwner())) {
                        this.teleportTowards(this.getTrueOwner());
                    }
                }

                if (!serverLevel.isVillage(this.getRaidPos())) {
                    this.moveRaidCenterToNearbyVillageSection();
                }

                if (!serverLevel.isVillage(this.getRaidPos()) || (this.getRaidVillagers().isEmpty() && this.raidTime >= 100)) {
                    this.celebrationTime = 600;
                    this.missionComplete = true;
                    this.setRaidPos(null);
                    if (this.getTrueOwner() instanceof ServerPlayer serverPlayer){
                        ModCriteriaTriggers.SERVANT_RAID_VICTORY.trigger(serverPlayer);
                    }
                }
                ++this.raidTime;

                this.chunkLoadTarget(this.getRaidPos());

                if (this.raidTime >= 48000){
                    this.setRaidPos(null);
                    if (this.getTrueOwner() != null
                            && this.getTrueOwner().distanceTo(this) > 32.0D
                            && MobUtil.sameDimension(this, this.getTrueOwner())) {
                        this.teleportTowards(this.getTrueOwner());
                    }
                }
                boolean raidFlag = this.isAllyWithType(EntityType.VILLAGER);
                if (raidFlag) {
                    this.setRaidPos(null);
                    if (this.getTrueOwner() != null) {
                        if (this.getTrueOwner().distanceTo(this) > 32.0D
                                && MobUtil.sameDimension(this, this.getTrueOwner())) {
                            this.teleportTowards(this.getTrueOwner());
                        }
                    }
                }
                if (this.isLeader()) {
                    for (RaiderServant servant : this.getNearbyCompanions()) {
                        if (servant.getLeader() == null && servant.getRaidPos() == this.getRaidPos()) {
                            servant.setLeader(this);
                            servant.setFollowing();
                        }
                    }
                }
            } else {
                if (this.raidTime > 0){
                    this.raidTime = 0;
                }
            }
        }
    }

    @Override
    public void teleportTowards(Entity entity) {
        if (this.getControlledVehicle() instanceof LivingEntity livingEntity && !(livingEntity instanceof RaiderServant)){
            if (!this.level.isClientSide() && livingEntity.isAlive()) {
                for (int i = 0; i < 128; ++i) {
                    Vec3 vector3d = new Vec3(livingEntity.getX() - entity.getX(), livingEntity.getY(0.5D) - entity.getEyeY(), livingEntity.getZ() - entity.getZ());
                    vector3d = vector3d.normalize();
                    double d0 = 16.0D;
                    double d1 = livingEntity.getX() + (livingEntity.getRandom().nextDouble() - 0.5D) * 8.0D - vector3d.x * d0;
                    double d2 = livingEntity.getY() + (double) (livingEntity.getRandom().nextInt(16) - 8) - vector3d.y * d0;
                    double d3 = livingEntity.getZ() + (livingEntity.getRandom().nextDouble() - 0.5D) * 8.0D - vector3d.z * d0;
                    net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(livingEntity, d1, d2, d3);
                    if (event.isCanceled()) {
                        break;
                    }
                    if (livingEntity.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), false)) {
                        this.teleportHits();
                        break;
                    }
                }
            }
        } else {
            super.teleportTowards(entity);
        }
    }

    public void teleportHits(){
    }

    private void moveRaidCenterToNearbyVillageSection() {
        if (this.level instanceof ServerLevel serverLevel) {
            if (this.getRaidPos() != null) {
                Stream<SectionPos> stream = SectionPos.cube(SectionPos.of(this.getRaidPos()), 2);
                stream.filter(serverLevel::isVillage).map(SectionPos::center).min(Comparator.comparingDouble((p_37766_) -> {
                    return p_37766_.distSqr(this.getRaidPos());
                })).ifPresent(this::setRaidPos);
            }
        }
    }

    private List<Villager> getRaidVillagers() {
        List<Villager> list = new ArrayList<>();
        if (this.level instanceof ServerLevel serverLevel) {
            if (this.getRaidPos() != null) {
                AABB aabb = new AABB(this.getRaidPos()).inflate(256.0D);
                list = serverLevel.getEntitiesOfClass(Villager.class, aabb, villager -> !villager.isBaby());
            }
        }
        return list;
    }

    public boolean canBeLeader(){
        return false;
    }

    public boolean canBeFollower(){
        return true;
    }

    public boolean canJoinPatrol() {
        if (this.getTrueOwner() instanceof Player player && SEHelper.isGrounded(player, this)) {
            return false;
        }
        if (this.isWoundedOrCrippled()) {
            return false;
        }
        return this.canBeFollower();
    }

    public boolean isWoundedOrCrippled() {
        if (this.hasEffect(GoetyEffects.CRIPPLED.get())) {
            return true;
        }
        if (this.hasEffect(GoetyEffects.WOUNDED.get())) {
            return this.getIdol() != null;
        }
        return false;
    }

    public ItemStack getBannerPatternInstance() {
        if (this.getTrueOwner() instanceof Player player) {
            ListTag listTag = SEHelper.getBannerPattern(player);
            if (listTag != null) {
                ItemStack itemstack = new ItemStack(BannerBlock.byColor(SEHelper.getBannerBaseColor(player)));
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.put("Patterns", listTag);
                BlockItem.setBlockEntityData(itemstack, BlockEntityType.BANNER, compoundtag);
                return itemstack;
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getLeaderBannerInstance() {
        if (this.getTrueOwner() instanceof Player player) {
            ListTag listTag = SEHelper.getBannerPattern(player);
            if (listTag != null) {
                ItemStack itemstack = this.getBannerPatternInstance();
                itemstack.hideTooltipPart(ItemStack.TooltipPart.ADDITIONAL);
                itemstack.setHoverName(Component.translatable("block.goety.player_banner", player.getDisplayName()).withStyle(ChatFormatting.GOLD));
                return itemstack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    @Override
    public <T extends Mob> T convertTo(EntityType<T> p_21407_, boolean p_21408_) {
        T convert = super.convertTo(p_21407_, p_21408_);
        if (convert instanceof RaiderServant servant) {
            if (this.getLeader() != null) {
                servant.setLeader(this.getLeader());
            }
        }
        return convert;
    }

    @Nullable
    public OminousIdolBlockEntity getIdol() {
        if (this.getServer() != null) {
            if (this.revivePos != null) {
                for (Level level1 : this.getServer().getAllLevels()) {
                    if (level1.dimension() == this.getReviveLevel()) {
                        BlockEntity blockEntity = level1.getBlockEntity(this.revivePos);
                        if (blockEntity instanceof OminousIdolBlockEntity idol) {
                            if (idol.getTrueOwner() == this.getTrueOwner()) {
                                return idol;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0 && this.getIdol() != null){
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    @Override
    public boolean canRevive(DamageSource damageSource) {
        if (!damageSource.is(ModDamageSource.DISMISSED)) {
            if (MainConfig.OminousIdolRevive.get()) {
                if (!this.hasEffect(GoetyEffects.WOUNDED.get())) {
                    if (this.getIdol() != null && this.level.dimension() == this.getReviveLevel()) {
                        if (this.getIdol().getSoulEnergy() >= MainConfig.OminousIdolReviveCost.get()) {
                            return this.getIdol().getIllagers().contains(this);
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void reviveOwned() {
        super.reviveOwned();
        if (this.isLeader()) {
            this.spawnAtLocation(this.getItemBySlot(EquipmentSlot.HEAD));
            this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        }
        this.setTarget(null);
        this.setMarked(null);
        this.setRaidPos(null);
        this.level.broadcastEntityEvent(this, (byte) 35);
        this.addEffect(new MobEffectInstance(GoetyEffects.WOUNDED.get(), MathHelper.minecraftDayToTicks(1)));
        this.addEffect(new MobEffectInstance(GoetyEffects.CRIPPLED.get(), MathHelper.minutesToTicks(5)));
        for (Entity entity : this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(16))) {
            if (entity instanceof Hellfire || entity instanceof SpellEntity) {
                if (!MobUtil.areAllies(this, entity)) {
                    entity.discard();
                }
            }
        }
        if (this.getIdol() != null) {
            this.getIdol().siphonSoulEnergy(MainConfig.OminousIdolReviveCost.get());
        }
    }

    @Override
    public void reviveTick() {
        super.reviveTick();
        this.getActiveEffects().removeIf(instance -> instance.getEffect() != GoetyEffects.WOUNDED.get() && instance.getEffect() != GoetyEffects.CRIPPLED.get());
    }

    @Nullable
    @Override
    public BlockPos getRevivePos() {
        return this.revivePos;
    }

    @Override
    public void setRevivePos(BlockPos revivePos) {
        this.revivePos = revivePos;
    }

    @Override
    public String getReviveDim() {
        return this.reviveDim;
    }

    @Override
    public void setReviveDim(String reviveDim) {
        this.reviveDim = reviveDim;
    }

    public void spawnArmor(RandomSource randomSource){
        if (MobsConfig.RaiderServantWearArmor.get()) {
            super.spawnArmor(randomSource);
        }
    }

    public boolean canLinkToIdol() {
        return this instanceof AbstractIllagerServant
                || this instanceof AbstractBoundIllager
                || this instanceof WitchServant
                || this instanceof AllyTrampler
                || this instanceof ModRavager;
    }

    public List<RaiderServant> getNearbyCompanions() {
        return this.level.getEntitiesOfClass(RaiderServant.class, this.getBoundingBox().inflate(8.0D), (illager) ->
                illager != this && illager.getTrueOwner() == this.getTrueOwner() && illager.canJoinPatrol() && (illager.getLeader() == null || illager.getLeader() == this));
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (this.canBeLeader()
                    && !this.getLeaderBannerInstance().isEmpty()
                    && ItemHelper.sameBanner(itemstack, this.getBannerPatternInstance())
                    && !ItemStack.matches(this.getItemBySlot(EquipmentSlot.HEAD), this.getLeaderBannerInstance())) {
                ItemStack helmet = this.getItemBySlot(EquipmentSlot.HEAD);
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                this.playSound(this.getCelebrateSound(), 1.0F, this.getVoicePitch());
                this.dropEquipment(EquipmentSlot.HEAD, helmet);
                this.setItemSlot(EquipmentSlot.HEAD, this.getLeaderBannerInstance());
                this.setGuaranteedDrop(EquipmentSlot.HEAD);
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                for (int i = 0; i < 7; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                }
                return InteractionResult.SUCCESS;
            } else if (pPlayer.getMainHandItem().getItem() instanceof TaglockKit
                    && TaglockKit.hasEntity(pPlayer.getMainHandItem())
                    && !MobUtil.areAllies(pPlayer, TaglockKit.getEntity(pPlayer.getMainHandItem()))
                    && this.isLeader()){
                this.setMarked(TaglockKit.getEntity(pPlayer.getMainHandItem()));
                this.setTarget(this.getMarked());
                if (!pPlayer.getAbilities().instabuild) {
                    TaglockKit.removeEntity(pPlayer.getMainHandItem());
                }
                if (!this.level.isClientSide) {
                    if (this.getMarked() instanceof ServerPlayer player) {
                        if (CuriosFinder.hasCurio(player, ModItems.ALARMING_CHARM.get())) {
                            player.displayClientMessage(Component.translatable("info.goety.summon.hunt").withStyle(ChatFormatting.RED), true);
                            ModNetwork.sendToClient(player, new SPlayPlayerSoundPacket(SoundEvents.RAID_HORN.get(), 64.0F, 1.0F));
                        }
                    }
                }

                if (this.isStaying()){
                    this.setStaying(false);
                }
                if (this.isGuardingArea()){
                    this.setBoundPos(null);
                }
                for (RaiderServant servant : this.getNearbyCompanions()) {
                    servant.setMarked(this.getMarked());
                    servant.setTarget(this.getMarked());
                    if (servant.getLeader() == null){
                        servant.setLeader(this);
                        servant.setFollowing();
                    }
                    if (MobUtil.sameDimension(servant, this.getMarked())) {
                        servant.teleportTowards(this.getMarked());
                    }
                }
                if (MobUtil.sameDimension(this, this.getMarked())) {
                    this.teleportTowards(this.getMarked());
                }
                return InteractionResult.SUCCESS;
            } else if (pPlayer.getMainHandItem().is(ModItems.WAYSTONE.get())
                    && WaystoneItem.hasBlock(pPlayer.getMainHandItem())
                    && WaystoneItem.isSameDimension(this, pPlayer.getMainHandItem())
                    && (pPlayer.getOffhandItem().is(Items.GOAT_HORN) || pPlayer.getOffhandItem().is(ModItems.RAIDING_HORN.get()))
                    && !SEHelper.getFocusCoolDown(pPlayer).isOnCooldown(ModItems.WAYSTONE.get())
                    && this.isLeader()){
                if (this.level instanceof ServerLevel serverLevel) {
                    GlobalPos globalPos = WaystoneItem.getPosition(pPlayer.getMainHandItem());
                    if (globalPos != null){
                        BlockPos blockPos = globalPos.pos();
                        if (!serverLevel.isLoaded(blockPos)){
                            pPlayer.displayClientMessage(Component.translatable("info.goety.waystone.unloaded"), true);
                            return InteractionResult.FAIL;
                        }
                        if (serverLevel.isVillage(blockPos)){
                            if (this.isAllyWithType(EntityType.VILLAGER)) {
                                return InteractionResult.FAIL;
                            }
                            this.setRaidPos(blockPos);
                            this.setRaidDim(serverLevel.dimension());
                            if (this.isStaying()){
                                this.setStaying(false);
                            }
                            if (this.isGuardingArea()){
                                this.setBoundPos(null);
                            }
                            boolean flag = this.position().distanceToSqr(blockPos.getCenter()) <= Mth.square(32);
                            if (!flag) {
                                int k = 0;
                                BlockPos blockPos1 = this.findRandomSpawnPos(k, 20);
                                if (blockPos1 != null) {
                                    for (RaiderServant servant : this.getNearbyCompanions()) {
                                        servant.setRaidPos(blockPos);
                                        servant.setRaidDim(serverLevel.dimension());
                                        if (servant.getLeader() == null) {
                                            servant.setLeader(this);
                                            servant.setFollowing();
                                        }
                                        servant.moveTo(blockPos1, 0.0F, 0.0F);
                                    }
                                    this.moveTo(blockPos1, 0.0F, 0.0F);
                                    flag = true;
                                } else {
                                    ++k;
                                }
                            }
                            if (flag) {
                                long j = serverLevel.getRandom().nextLong();
                                for(ServerPlayer serverplayer : serverLevel.players()) {
                                    Vec3 vec3 = serverplayer.position();
                                    Vec3 vec31 = this.position();
                                    double d0 = Math.sqrt((vec31.x - vec3.x) * (vec31.x - vec3.x) + (vec31.z - vec3.z) * (vec31.z - vec3.z));
                                    double d1 = vec3.x + 13.0D / d0 * (vec31.x - vec3.x);
                                    double d2 = vec3.z + 13.0D / d0 * (vec31.z - vec3.z);
                                    if (d0 <= 64.0D) {
                                        serverplayer.connection.send(new ClientboundSoundPacket(SoundEvents.RAID_HORN, SoundSource.NEUTRAL, d1, serverplayer.getY(), d2, 64.0F, 1.0F, j));
                                    }
                                }
                                if (this.getTrueOwner() instanceof Player player) {
                                    SEHelper.addCooldown(player, ModItems.WAYSTONE.get(), 300);
                                }
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }
                return InteractionResult.CONSUME;
            } else if (pPlayer.getMainHandItem().is(ModItems.OMINOUS_SHACKLES.get())
                    && !this.isCapturing()
                    && !this.isFollower()){
                if (!this.level.isClientSide) {
                    this.setCaptureMode(true);
                    this.playSound(SoundEvents.CHAIN_PLACE, 1.0F, 1.0F);
                }

                return InteractionResult.SUCCESS;
            } else if (pPlayer.getMainHandItem().is(Tags.Items.SHEARS)
                    && this.isCapturing()
                    && !this.isFollower()){
                if (!this.level.isClientSide) {
                    this.setCaptureMode(false);
                    this.playSound(SoundEvents.CHAIN_BREAK, 1.0F, 0.5F);
                }

                return InteractionResult.SUCCESS;
            } else if (pPlayer.getMainHandItem().isEmpty() && this.isLeader() && !this.getBindPrisoners().isEmpty()){
                for (Prisoner prisoner : this.getBindPrisoners()) {
                    prisoner.setLeader(this);
                }
                this.playSound(SoundEvents.CHAIN_PLACE, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            } else if (pPlayer.getMainHandItem().isEmpty() && this.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof BannerItem){
                ItemStack helmet = this.getItemBySlot(EquipmentSlot.HEAD);
                this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                this.dropEquipment(EquipmentSlot.HEAD, helmet);
                return InteractionResult.SUCCESS;
            } else if (this.canLinkToIdol()) {
                return this.linkToIdol(pPlayer, pHand);
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public List<Prisoner> getBindPrisoners() {
        return this.level.getEntitiesOfClass(Prisoner.class, this.getBoundingBox().inflate(10.0D), prisoner -> prisoner.isFollowing() && prisoner.getMasterOwner() == this.getMasterOwner());
    }

    public InteractionResult linkToIdol(Player pPlayer, InteractionHand pHand) {
        if (pPlayer.getMainHandItem().is(ModItems.WAYSTONE.get())) {
            if (WaystoneItem.isSameDimension(this, pPlayer.getMainHandItem())) {
                if (WaystoneItem.getBlockEntity(pPlayer.getMainHandItem(), this.level) instanceof OminousIdolBlockEntity idol
                        && idol.getTrueOwner() == this.getTrueOwner()
                        && idol.hasSpace()) {
                    if (!this.level.isClientSide) {
                        if (this.getIdol() != null) {
                            this.getIdol().removeIllager(this);
                            this.getIdol().markUpdated();
                        }
                        BlockPos blockPos = idol.getBlockPos();
                        idol.addIllager(this);
                        this.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                        if (this.level instanceof ServerLevel serverLevel) {
                            for (int i = 0; i < 7; ++i) {
                                double d0 = this.random.nextGaussian() * 0.02D;
                                double d1 = this.random.nextGaussian() * 0.02D;
                                double d2 = this.random.nextGaussian() * 0.02D;
                                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                            }
                        }
                        this.setRevivePos(blockPos);
                        this.setReviveDim(this.level.dimension());
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public static class RaiderWanderGoal<T extends RaiderServant> extends WanderGoal<T> {

        public RaiderWanderGoal(T entity, double speedModifier) {
            super(entity, speedModifier);
        }

        public RaiderWanderGoal(T entity, double speedModifier, float probability) {
            super(entity, speedModifier, probability);
        }

        public RaiderWanderGoal(T entity, double speedModifier, int interval, float probability) {
            super(entity, speedModifier, interval, probability);
        }

        @Nullable
        @Override
        protected Vec3 getPosition() {
            if (this.mob.isInWaterOrBubble()) {
                Vec3 vec3 = this.landRandomPos(15, 7);
                return vec3 == null ? this.defaultRandomPos() : vec3;
            } else if (this.summonedEntity instanceof ITrainable trainable && trainable.isTraining()) {
                if (trainable.getTrainPos().isPresent()) {
                    Vec3 vec3 = null;
                    int range = 4;

                    for (int i = 0; i < 10; ++i) {
                        BlockPos blockPos = trainable.getTrainPos().get()
                                .offset(this.summonedEntity.getRandom().nextIntBetweenInclusive(-range, range),
                                        this.summonedEntity.getRandom().nextIntBetweenInclusive(-range, range),
                                        this.summonedEntity.getRandom().nextIntBetweenInclusive(-range, range));
                        BlockPos blockPos1 = LandRandomPos.movePosUpOutOfSolid(this.summonedEntity, blockPos);
                        if (blockPos1 != null) {
                            vec3 = Vec3.atBottomCenterOf(blockPos1);
                            break;
                        }
                    }

                    return vec3;
                }
            } else if (this.summonedEntity.getLeader() != null){
                Vec3 vec3 = null;
                int xz = 8;
                int y = 4;

                for (int i = 0; i < 10; ++i){
                    BlockPos blockPos = this.summonedEntity.getLeader().blockPosition()
                            .offset(this.summonedEntity.getRandom().nextIntBetweenInclusive(-xz, xz),
                                    this.summonedEntity.getRandom().nextIntBetweenInclusive(-y, y),
                                    this.summonedEntity.getRandom().nextIntBetweenInclusive(-xz, xz));
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
    }

    public static class ObtainLeaderBannerGoal<T extends RaiderServant> extends Goal {
        private final T mob;

        public ObtainLeaderBannerGoal(T p_37917_) {
            this.mob = p_37917_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if ((this.mob.getMarked() != null || this.mob.isRaiding()) && this.mob.canBeLeader() && !this.mob.getLeaderBannerInstance().isEmpty() && !ItemStack.matches(this.mob.getItemBySlot(EquipmentSlot.HEAD), this.mob.getLeaderBannerInstance())) {
                RaiderServant raider = this.mob.getLeader();
                if (raider == null || !raider.isAlive()) {
                    List<ItemEntity> list = this.mob.level.getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(16.0D, 8.0D, 16.0D), itemEntity -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive() && ItemHelper.sameBanner(itemEntity.getItem(), this.mob.getBannerPatternInstance()));
                    if (!list.isEmpty()) {
                        Optional<ItemEntity> optional = list.stream().findFirst();
                        if (optional.isPresent()) {
                            return this.mob.getNavigation().moveTo(optional.get(), 1.15D);
                        }
                    }
                }
            }
            return false;
        }

        public void tick() {
            List<ItemEntity> list = this.mob.level.getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(4.0D, 4.0D, 4.0D), itemEntity -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive() && ItemHelper.sameBanner(itemEntity.getItem(), this.mob.getBannerPatternInstance()));
            if (!list.isEmpty()) {
                Optional<ItemEntity> optional = list.stream().findFirst();
                if (optional.isPresent()) {
                    this.mob.pickUpItem(optional.get());
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public static class PathfindToRaidGoal<T extends RaiderServant> extends Goal {
        private final T mob;

        public PathfindToRaidGoal(T p_25706_) {
            this.mob = p_25706_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return this.mob.getTarget() == null && !this.mob.isVehicle() && this.mob.isRaiding() && this.mob.level instanceof ServerLevel serverLevel && !serverLevel.isVillage(this.mob.blockPosition());
        }

        public boolean canContinueToUse() {
            return this.mob.isRaiding() && this.mob.level instanceof ServerLevel serverLevel && !serverLevel.isVillage(this.mob.blockPosition());
        }

        public void tick() {
            if (this.mob.getRaidPos() != null) {
                if (!this.mob.isPathFinding()) {
                    Vec3 vec3 = DefaultRandomPos.getPosTowards(this.mob, 15, 4, Vec3.atBottomCenterOf(this.mob.getRaidPos()), (double)((float)Math.PI / 2F));
                    if (vec3 != null) {
                        this.mob.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.0D);
                    }
                }
            }

        }
    }

    static class RaiderMoveThroughVillageGoal extends Goal {
        private final RaiderServant raider;
        private final double speedModifier;
        private BlockPos poiPos;
        private final List<BlockPos> visited = Lists.newArrayList();
        private final int distanceToPoi;
        private boolean stuck;

        public RaiderMoveThroughVillageGoal(RaiderServant p_37936_, double p_37937_, int p_37938_) {
            this.raider = p_37936_;
            this.speedModifier = p_37937_;
            this.distanceToPoi = p_37938_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            this.updateVisited();
            return this.raider.isRaiding() && this.hasSuitablePoi() && this.raider.getTarget() == null;
        }

        public boolean canContinueToUse() {
            if (this.raider.getNavigation().isDone()) {
                return false;
            } else {
                return this.raider.getTarget() == null && !this.poiPos.closerToCenterThan(this.raider.position(), this.raider.getBbWidth() + (float)this.distanceToPoi) && !this.stuck;
            }
        }

        private boolean hasSuitablePoi() {
            ServerLevel serverlevel = (ServerLevel)this.raider.level();
            BlockPos blockpos = this.raider.blockPosition();
            Optional<BlockPos> optional = serverlevel.getPoiManager().getRandom((p_219843_) -> {
                return p_219843_.is(PoiTypes.HOME);
            }, this::hasNotVisited, PoiManager.Occupancy.ANY, blockpos, 48, this.raider.random);
            if (optional.isEmpty()) {
                return false;
            } else {
                this.poiPos = optional.get().immutable();
                return true;
            }
        }

        public void stop() {
            if (this.poiPos.closerToCenterThan(this.raider.position(), (double)this.distanceToPoi)) {
                this.visited.add(this.poiPos);
            }

        }

        public void start() {
            super.start();
            this.raider.setNoActionTime(0);
            this.raider.getNavigation().moveTo((double)this.poiPos.getX(), (double)this.poiPos.getY(), (double)this.poiPos.getZ(), this.speedModifier);
            this.stuck = false;
        }

        public void tick() {
            if (this.raider.getNavigation().isDone()) {
                Vec3 vec3 = Vec3.atBottomCenterOf(this.poiPos);
                Vec3 vec31 = DefaultRandomPos.getPosTowards(this.raider, 16, 7, vec3, (double)((float)Math.PI / 10F));
                if (vec31 == null) {
                    vec31 = DefaultRandomPos.getPosTowards(this.raider, 8, 7, vec3, (double)((float)Math.PI / 2F));
                }

                if (vec31 == null) {
                    this.stuck = true;
                    return;
                }

                this.raider.getNavigation().moveTo(vec31.x, vec31.y, vec31.z, this.speedModifier);
            }

        }

        private boolean hasNotVisited(BlockPos p_37943_) {
            for(BlockPos blockpos : this.visited) {
                if (Objects.equals(p_37943_, blockpos)) {
                    return false;
                }
            }

            return true;
        }

        private void updateVisited() {
            if (this.visited.size() > 2) {
                this.visited.remove(0);
            }

        }
    }

    public static class CelebrateGoal extends Goal {
        private final RaiderServant mob;

        public CelebrateGoal(RaiderServant p_37924_) {
            this.mob = p_37924_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return this.mob.isAlive() && this.mob.getTarget() == null && this.mob.canCelebrate() && this.mob.celebrationTime > 0;
        }

        public void start() {
            this.mob.setCelebrating(true);
            super.start();
        }

        public void stop() {
            this.mob.setCelebrating(false);
            super.stop();
        }

        public void tick() {
            if (!this.mob.isSilent() && this.mob.random.nextInt(this.adjustedTickDelay(100)) == 0) {
                this.mob.playSound(this.mob.getCelebrateSound(), this.mob.getSoundVolume(), this.mob.getVoicePitch());
            }

            if (!this.mob.isPassenger() && this.mob.random.nextInt(this.adjustedTickDelay(50)) == 0) {
                this.mob.getJumpControl().jump();
            }

            super.tick();
        }
    }
}
