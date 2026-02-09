package com.Polarice3.Goety.common.entities.ally.illager.raider;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.illager.ILooter;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ai.IllagerChestGoal;
import com.Polarice3.Goety.common.entities.ai.IllagerStoreChestGoal;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.client.CPrisonerMinePacket;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.*;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.TierSortingRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class Prisoner extends RaiderServant implements VillagerDataHolder, ILooter {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final EntityDataAccessor<Float> LOOK_ANGLE = SynchedEntityData.defineId(Prisoner.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_TRADER = SynchedEntityData.defineId(Prisoner.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_MINING = SynchedEntityData.defineId(Prisoner.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_LOOK = SynchedEntityData.defineId(Prisoner.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA = SynchedEntityData.defineId(Prisoner.class, EntityDataSerializers.VILLAGER_DATA);
    private final DynamicGameEventListener<GameEventListener> gameEventListener;
    @Nullable
    private Tag gossips;
    @Nullable
    private CompoundTag tradeOffers;
    private int villagerXp;
    public int toMineTick;
    public int noBlockTick;
    public int mineTimes;
    public int updateList;
    public List<BlockPos> blockPosList = new ArrayList<>();
    public List<BlockPos> rareList = new ArrayList<>();
    public boolean isChained;
    @Nullable
    public BlockPos chestPos;
    public String chestDim = Level.OVERWORLD.location().toString();
    @Nullable
    public BlockPos dumpChestPos;
    public String dumpChestDim = Level.OVERWORLD.location().toString();
    private final SimpleContainer inventory = new SimpleContainer(8);
    public AnimationState miningAnimationState = new AnimationState();

    public Prisoner(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.gameEventListener = new DynamicGameEventListener<>(new GameEventListener() {
            public @NotNull PositionSource getListenerSource() {
                return new BlockPositionSource(Prisoner.this.blockPosition());
            }

            public int getListenerRadius() {
                return MobsConfig.PrisonerMiningRange.get() * 2;
            }

            public boolean handleGameEvent(ServerLevel serverLevel, GameEvent p_282184_, GameEvent.Context p_283014_, Vec3 p_282350_) {
                if (!Prisoner.this.isRemoved() && (Prisoner.this.getMainHandItem().is(ItemTags.PICKAXES) || Prisoner.this.getMainHandItem().getItem() instanceof PickaxeItem)) {
                    if (p_282184_.is(ModTags.GameEvents.BLOCK_EVENTS)) {
                        Prisoner.this.updateList = 5;
                        return true;
                    }

                }
                return false;
            }
        });
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new PrisonerPutLootChestGoal<>(this));
        this.goalSelector.addGoal(1, new GiveMinedGoal(this));
        this.goalSelector.addGoal(2, new MiningGoal(this));
        this.goalSelector.addGoal(3, new PrisonerGetPickChestGoal<>(this));
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(6, new FollowOwnerGoal<>(this, 0.6D, 6.0F, 2.0F){
            @Override
            public boolean canUse() {
                if (Prisoner.this.getLeader() != null){
                    LivingEntity livingentity = Prisoner.this.getLeader();
                    if (livingentity == null) {
                        return false;
                    } else if (livingentity.isSpectator()) {
                        return false;
                    } else if (Prisoner.this.distanceToSqr(livingentity) < (double)(Mth.square(this.startDistance))) {
                        return false;
                    } else if (!Prisoner.this.isFollowing() || Prisoner.this.isCommanded()) {
                        return false;
                    } else if (Prisoner.this.getTarget() != null) {
                        return false;
                    } else {
                        this.owner = livingentity;
                        return true;
                    }
                }
                return super.canUse();
            }
        });
    }

    public void miscGoal() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(7, new ReturnToGuardPos<>(this, 0.6D, 2));
        this.goalSelector.addGoal(8, new RaiderWanderGoal<>(this, 0.6D){
            @Override
            public Vec3 randomBoundPos() {
                return this.summonedEntity.vec3BoundPos();
            }
        });
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LOOK_ANGLE, 0.0F);
        this.entityData.define(IS_TRADER, false);
        this.entityData.define(IS_MINING, false);
        this.entityData.define(HAS_LOOK, false);
        this.entityData.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        VillagerData.CODEC.encodeStart(NbtOps.INSTANCE, this.getVillagerData()).resultOrPartial(LOGGER::error).ifPresent((p_204072_) -> {
            compound.put("VillagerData", p_204072_);
        });
        if (this.tradeOffers != null) {
            compound.put("Offers", this.tradeOffers);
        }

        if (this.gossips != null) {
            compound.put("Gossips", this.gossips);
        }

        if (this.hasCustomLook()) {
            compound.putFloat("CustomLookAngle", this.getCustomLook());
        }

        compound.putInt("Xp", this.villagerXp);
        compound.putInt("MineTimes", this.mineTimes);
        compound.putBoolean("Trader", this.isTrader());
        compound.putBoolean("Mining", this.isMining());
        this.saveLooterData(compound);
        this.writeInventoryToTag(compound);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("VillagerData", 10)) {
            DataResult<VillagerData> dataresult = VillagerData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, compound.get("VillagerData")));
            dataresult.resultOrPartial(LOGGER::error).ifPresent(this::setVillagerData);
        }

        if (compound.contains("Offers", 10)) {
            this.tradeOffers = compound.getCompound("Offers");
        }

        if (compound.contains("Gossips", 9)) {
            this.gossips = compound.getList("Gossips", 10);
        }

        if (compound.contains("CustomLookAngle")) {
            this.setCustomLook(compound.getFloat("CustomLookAngle"));
        }

        if (compound.contains("Xp", 3)) {
            this.villagerXp = compound.getInt("Xp");
        }

        if (compound.contains("MineTimes")) {
            this.mineTimes = compound.getInt("MineTimes");
        }

        if (compound.contains("Trader")) {
            this.setIsTrader(compound.getBoolean("Trader"));
        }

        if (compound.contains("Mining")) {
            this.setMining(compound.getBoolean("Mining"));
        }

        this.readLooterData(compound);
        this.readInventoryFromTag(compound);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (IS_MINING.equals(accessor)) {
            if (this.level.isClientSide){
                if (this.isMining()) {
                    this.miningAnimationState.startIfStopped(this.tickCount);
                } else {
                    this.miningAnimationState.stop();
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    public double getMyRidingOffset() {
        return 0.0D;
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public SlotAccess getSlot(int p_149995_) {
        int i = p_149995_ - 300;
        return i >= 0 && i < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, i) : super.getSlot(p_149995_);
    }

    @Nullable
    public BlockPos getChestPos() {
        return this.chestPos;
    }

    public void setChestPos(@Nullable BlockPos chestPos) {
        this.chestPos = chestPos;
    }

    public String getChestDim() {
        return this.chestDim;
    }

    public void setChestDim(String string) {
        this.chestDim = string;
    }

    @Nullable
    public BlockPos getDumpChestPos() {
        return this.dumpChestPos;
    }

    public void setDumpChestPos(@Nullable BlockPos chestPos) {
        this.dumpChestPos = chestPos;
    }

    public String getDumpChestDim() {
        return this.dumpChestDim;
    }

    public void setDumpChestDim(String string) {
        this.dumpChestDim = string;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (pReason != MobSpawnType.CONVERSION) {
            this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome(pLevel.getBiome(this.blockPosition()))));
        }
        return pSpawnData;
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.isTrader()) {
            return SoundEvents.WANDERING_TRADER_NO;
        }
        return SoundEvents.VILLAGER_NO;
    }

    protected SoundEvent getHurtSound(DamageSource p_35498_) {
        if (this.isTrader()) {
            return SoundEvents.WANDERING_TRADER_HURT;
        }
        return SoundEvents.VILLAGER_HURT;
    }

    protected SoundEvent getDeathSound() {
        if (this.isTrader()) {
            return SoundEvents.WANDERING_TRADER_DEATH;
        }
        return SoundEvents.VILLAGER_DEATH;
    }

    public List<ItemStack> itemsInInv(Predicate<ItemStack> predicate){
        List<ItemStack> list = new ArrayList<>();
        SimpleContainer simplecontainer = this.getInventory();
        int i = simplecontainer.getContainerSize();
        for (int j = 0; j < i; ++j) {
            ItemStack itemStack = simplecontainer.getItem(j);
            if (predicate.test(itemStack)){
                list.add(itemStack);
            }
        }
        return list;
    }

    @Nullable
    public Tag getGossips() {
        return this.gossips;
    }

    @Nullable
    public CompoundTag getOffers() {
        return this.tradeOffers;
    }

    @Override
    public void die(DamageSource pCause) {
        if (this.level instanceof ServerLevel serverLevel) {
            Entity entity = pCause.getEntity();
            if (entity instanceof Zombie zombie) {
                if ((zombie.level.getDifficulty() == Difficulty.NORMAL || zombie.level.getDifficulty() == Difficulty.HARD) && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.ZOMBIE_VILLAGER, (timer) -> {})) {
                    if (!(zombie.level.getDifficulty() != Difficulty.HARD && this.random.nextBoolean())) {
                        ZombieVillager zombievillager = this.convertTo(EntityType.ZOMBIE_VILLAGER, false);
                        if (zombievillager != null) {
                            zombievillager.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(zombievillager.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true), (CompoundTag)null);
                            zombievillager.setVillagerData(this.getVillagerData());
                            if (this.getGossips() != null) {
                                zombievillager.setGossips(this.getGossips());
                            }
                            if (this.getOffers() != null) {
                                zombievillager.setTradeOffers(this.getOffers());
                            }
                            zombievillager.setVillagerXp(this.getVillagerXp());
                            net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, zombievillager);
                            if (!this.isSilent()) {
                                serverLevel.levelEvent(null, 1026, this.blockPosition(), 0);
                            }
                        }
                    }
                }
            }
        }
        super.die(pCause);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.miningAnimationState.isStarted()) {
                ++this.toMineTick;
                if (this.toMineTick == 26) {
                    this.playSound(ModSounds.VILLAGER_CHOP.get());
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.VILLAGER_CHOP.get(), this.getSoundSource(), 1.0F, 1.0F, false);
                    ModNetwork.sendToServer(new CPrisonerMinePacket(this.getId()));
                }
                if (this.toMineTick >= 55) {
                    this.toMineTick = 0;
                }
            } else {
                this.toMineTick = 0;
            }
        }
        if (!this.level.isClientSide) {
            if (this.isFollowing()) {
                this.level.broadcastEntityEvent(this, (byte) 4);
            } else {
                this.level.broadcastEntityEvent(this, (byte) 5);
            }
            /*if (this.getTrueOwner() instanceof Raider raider && raider.getCurrentRaid() != null) {
                if (raider.getCurrentRaid().isLoss() && raider.isRemoved()) {
                    this.discard();
                }
            }*/
            if (this.getTrueOwner() instanceof RaiderServant raider) {
                if (raider.getLeader() != null && this.getLeader() != raider.getLeader()) {
                    this.setLeader(raider.getLeader());
                    if (raider.getLeader().getTrueOwner() != null) {
                        this.setTrueOwner(raider.getLeader().getTrueOwner());
                    }
                }
            }
            if (this.getLeader() == null && this.getTrueOwner() instanceof Player player && this.isFollowing()) {
                float f = this.distanceTo(player);
                if (f > 6.0F) {
                    double d0 = (player.getX() - this.getX()) / (double)f;
                    double d1 = (player.getY() - this.getY()) / (double)f;
                    double d2 = (player.getZ() - this.getZ()) / (double)f;
                    this.setDeltaMovement(this.getDeltaMovement().add(Math.copySign(d0 * d0 * 0.4D, d0), Math.copySign(d1 * d1 * 0.4D, d1), Math.copySign(d2 * d2 * 0.4D, d2)));
                    this.checkSlowFallDistance();
                }
            }
            if (this.level instanceof ServerLevel serverLevel) {
                if (this.noBlockTick > 0) {
                    --this.noBlockTick;
                }
                if (this.updateList > 0) {
                    --this.updateList;
                }
                if (this.getMainHandItem().is(ItemTags.PICKAXES) && this.getMainHandItem().getItem() instanceof PickaxeItem pickaxe) {
                    int range = MobsConfig.PrisonerMiningRange.get();
                    if (this.blockPosList.isEmpty() || this.updateList > 0) {
                        if (!this.blockPosList.isEmpty()) {
                            this.blockPosList.clear();
                        }
                        if (!this.rareList.isEmpty()) {
                            this.rareList.clear();
                        }
                        for (int i = -range; i < range; ++i) {
                            for (int j = -range; j < range; ++j) {
                                for (int k = -range; k < range; ++k) {
                                    BlockPos blockPos = this.blockPosition().offset(i, j, k);
                                    BlockState blockState = serverLevel.getBlockState(blockPos);
                                    boolean hasSight = true;
                                    if (MobsConfig.PrisonerMiningSeeBlocks.get()) {
                                        hasSight = false;
                                        for (Direction direction : Direction.values()) {
                                            hasSight = BlockFinder.canSeeBlock(this, blockPos.relative(direction));
                                            if (hasSight) {
                                                break;
                                            }
                                        }
                                    }
                                    if (hasSight) {
                                        if (blockState.is(ModTags.Blocks.PRISONER_MINEABLE) && !blockState.is(ModTags.Blocks.PRISONER_UNMINEABLE)) {
                                            this.blockPosList.add(blockPos);
                                            if (blockState.is(ModTags.Blocks.PRISONER_RARE_ORES)) {
                                                this.rareList.add(blockPos);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!this.blockPosList.isEmpty()) {
                        if (this.mineTimes >= MobsConfig.PrisonerMiningSwings.get()) {
                            BlockPos blockPos = this.blockPosList.get(RandomUtil.nextInt(serverLevel.getRandom(), blockPosList.size()));
                            boolean isRare = false;
                            if (!this.rareList.isEmpty()) {
                                if (this.rareList.contains(blockPos)) {
                                    isRare = true;
                                    if (MobsConfig.PrisonerMiningRareChance.get() != 0 && RandomUtil.nextInt(serverLevel.getRandom(), MobsConfig.PrisonerMiningRareChance.get()) != 0) {
                                        List<BlockPos> newBlockList = new ArrayList<>(blockPosList);
                                        newBlockList.removeIf(this.rareList::contains);
                                        if (newBlockList.size() > 1) {
                                            blockPos = this.blockPosList.get(RandomUtil.nextInt(serverLevel.getRandom(), newBlockList.size()));
                                        } else {
                                            blockPos = null;
                                        }
                                    }
                                }
                            }
                            if (!isRare) {
                                if (MobsConfig.PrisonerMiningChance.get() != 0 && RandomUtil.nextInt(serverLevel.getRandom(), MobsConfig.PrisonerMiningChance.get()) != 0) {
                                    blockPos = null;
                                }
                            }
                            if (blockPos != null) {
                                BlockState blockState = serverLevel.getBlockState(blockPos);
                                if (TierSortingRegistry.isCorrectTierForDrops(pickaxe.getTier(), blockState)) {
                                    for (ItemStack itemStack : Block.getDrops(blockState, serverLevel, blockPos, this.level.getBlockEntity(blockPos), this, this.getMainHandItem())) {
                                        this.getInventory().addItem(itemStack);
                                    }
                                }
                                if (MobsConfig.PrisonerMiningBreakBlocks.get()) {
                                    serverLevel.destroyBlock(blockPos, false, this);
                                }
                            }
                            this.mineTimes = 0;
                            this.updateList = 5;
                        }
                    } else {
                        this.noBlockTick = 100;
                        this.mineTimes = 0;
                    }

                } else {
                    if (!this.blockPosList.isEmpty()) {
                        this.blockPosList.clear();
                    }
                    if (!this.rareList.isEmpty()) {
                        this.rareList.clear();
                    }
                }
            }
        }
    }

    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> p_218348_) {
        if (this.level instanceof ServerLevel serverlevel) {
            p_218348_.accept(this.gameEventListener, serverlevel);
        }
    }

    @Override
    public VillagerData getVillagerData() {
        return this.entityData.get(DATA_VILLAGER_DATA);
    }

    @Override
    public void setVillagerData(VillagerData p_34376_) {
        VillagerData villagerdata = this.getVillagerData();
        if (villagerdata.getProfession() != p_34376_.getProfession()) {
            this.tradeOffers = null;
        }

        this.entityData.set(DATA_VILLAGER_DATA, p_34376_);
    }

    @Override
    public void setHostile(boolean hostile) {
    }

    @Override
    public boolean isHostile() {
        return false;
    }

    @Override
    public void onStopAttack() {
    }

    public boolean isTrader(){
        return this.entityData.get(IS_TRADER);
    }

    public void setIsTrader(boolean isTrader){
        this.entityData.set(IS_TRADER, isTrader);
    }

    public boolean isMining(){
        return this.entityData.get(IS_MINING);
    }

    public void setMining(boolean mining){
        this.entityData.set(IS_MINING, mining);
    }

    public void setTradeOffers(CompoundTag p_34412_) {
        this.tradeOffers = p_34412_;
    }

    public void setGossips(Tag p_34392_) {
        this.gossips = p_34392_;
    }

    public int getVillagerXp() {
        return this.villagerXp;
    }

    public void setVillagerXp(int p_34374_) {
        this.villagerXp = p_34374_;
    }

    public float getCustomLook() {
        return this.entityData.get(LOOK_ANGLE);
    }

    public void setCustomLook(float angle) {
        this.entityData.set(LOOK_ANGLE, angle);
        this.entityData.set(HAS_LOOK, true);
    }

    public boolean hasCustomLook() {
        return this.entityData.get(HAS_LOOK);
    }

    @Override
    public void mobSense() {
    }

    @Override
    public @Nullable LivingEntity getMarked() {
        return null;
    }

    @Override
    public @Nullable BlockPos getRaidPos() {
        return null;
    }

    @Override
    public boolean isRaiding() {
        return false;
    }

    @Override
    public boolean isCapturing() {
        return false;
    }

    public boolean canCelebrate(){
        return false;
    }

    public boolean canJoinPatrol() {
        return false;
    }

    public @NotNull Vec3 getLeashOffset() {
        return new Vec3(0.0D, 0.6F * this.getEyeHeight(), (double)(this.getBbWidth() * 0.4F));
    }

    public boolean isWithinDistance(Entity entity, double distance) {
        if (entity == null){
            return false;
        }
        BlockPos blockpos = entity.blockPosition();
        BlockPos blockpos1 = this.blockPosition();
        return blockpos1.closerThan(blockpos, distance);
    }

    public boolean isWithinThrowingDistance(Entity entity) {
        return this.isWithinDistance(entity, 5.0D);
    }

    @Override
    public boolean canPickUpLoot() {
        return true;
    }

    public boolean wantsToPickUp(ItemStack itemStack) {
        return MobsConfig.PrisonerPickUpPickaxe.get() && itemStack.is(ItemTags.PICKAXES);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (MobsConfig.PrisonerUnshackleDamage.get()) {
            if (source.getEntity() != null) {
                boolean flag = false;
                LivingEntity owner = this.getLeader();
                if (this.getLeader() == null || this.getLeader().distanceTo(this) >= 8.0D) {
                    owner = this.getMasterOwner();
                }
                if (owner != null) {
                    if (!MobUtil.areAllies(source.getEntity(), owner)) {
                        if (this.distanceTo(owner) >= 8.0D) {
                            flag = true;
                        }
                    }
                }
                if (flag) {
                    if ((this.getHealth() - amount) > 0.0F) {
                        if (this.level.getRandom().nextFloat() <= (amount / this.getHealth())) {
                            Player player = null;
                            if (source.getEntity() instanceof Player player1) {
                                player = player1;
                            }
                            this.unshackle(player);
                        }
                    }
                }
            }
        }
        return super.hurt(source, amount);
    }

    public void unshackle(@Nullable Player player) {
        if (this.level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.CHAIN_BREAK, this.getSoundSource(), 1.0F, 2.0F);
            AbstractVillager villager;
            if (this.isTrader()) {
                villager = this.convertTo(EntityType.WANDERING_TRADER, true);
            } else {
                villager = this.convertTo(EntityType.VILLAGER, true);
            }
            if (villager == null) {
                return;
            }
            for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                ItemStack itemstack = this.getItemBySlot(equipmentslot);
                if (!itemstack.isEmpty()) {
                    if (EnchantmentHelper.hasBindingCurse(itemstack)) {
                        villager.getSlot(equipmentslot.getIndex() + 300).set(itemstack);
                    } else {
                        double d0 = this.getEquipmentDropChance(equipmentslot);
                        if (d0 > 1.0D) {
                            this.spawnAtLocation(itemstack);
                        }
                    }
                }
            }
            if (villager instanceof Villager villager1) {
                villager1.setVillagerData(this.getVillagerData());
                if (this.gossips != null) {
                    villager1.setGossips(this.gossips);
                }

                if (this.tradeOffers != null) {
                    villager1.setOffers(new MerchantOffers(this.tradeOffers));
                }
                villager1.setVillagerXp(this.villagerXp);
                villager1.refreshBrain(serverLevel);
                if (player instanceof ServerPlayer
                        && player != this.getMasterOwner()
                        && !MobUtil.areAllies(player, this.getMasterOwner())) {
                    serverLevel.onReputationEvent(ReputationEventType.ZOMBIE_VILLAGER_CURED, player, villager1);
                    villager1.playSound(SoundEvents.VILLAGER_CELEBRATE);
                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.HAPPY_VILLAGER, villager1);
                }
                if (this.getMasterOwner() instanceof Player player1) {
                    villager1.getGossips().add(player1.getUUID(), GossipType.MAJOR_NEGATIVE, 200);
                }
            } else if (villager instanceof WanderingTrader) {
                if (player instanceof ServerPlayer
                        && player != this.getMasterOwner()
                        && !MobUtil.areAllies(player, this.getMasterOwner())) {
                    villager.playSound(SoundEvents.WANDERING_TRADER_YES);
                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.HAPPY_VILLAGER, villager);
                }
                if (this.getMasterOwner() instanceof Player player1) {
                    villager.setLastHurtByPlayer(player1);
                } else if (this.getMasterOwner() != null) {
                    villager.setLastHurtByMob(this.getMasterOwner());
                }
                villager.setCanPickUpLoot(false);
            }
            if (player != null) {
                ItemStack offhand = ItemStack.EMPTY;
                if (this.getOffhandItem().is(ModItems.OMINOUS_SHACKLES.get())) {
                    offhand = this.getOffhandItem().copyAndClear();
                } else if (villager.getOffhandItem().is(ModItems.OMINOUS_SHACKLES.get())) {
                    offhand = villager.getOffhandItem().copyAndClear();
                }
                if (!offhand.isEmpty()) {
                    if (player == this.getMasterOwner() || player == this.getTrueOwner()) {
                        if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                            player.setItemInHand(InteractionHand.MAIN_HAND, offhand);
                        } else if (!player.addItem(offhand)) {
                            this.spawnAtLocation(offhand);
                        }
                    }
                }
            }
            ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.CHAIN.defaultBlockState()), villager);
            villager.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(villager.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData) null, (CompoundTag) null);
            villager.setHealth(this.getHealth());
            net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, villager);
        }
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemInHand = pPlayer.getItemInHand(pHand);
        Item item = itemInHand.getItem();
        ItemStack mainHandItem = this.getMainHandItem();
        if (pPlayer.isCrouching() && !(item.interactLivingEntity(itemInHand, pPlayer, this, pHand).consumesAction() || itemInHand.is(ModTags.Items.GRIMOIRES))) {
            this.unshackle(pPlayer);
            return InteractionResult.SUCCESS;
        } else if (this.getMasterOwner() != null && this.getMasterOwner() == pPlayer) {
            if (MobsConfig.PrisonerMining.get() && (item instanceof PickaxeItem || itemInHand.is(ItemTags.PICKAXES))) {
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                this.setItemSlot(EquipmentSlot.MAINHAND, itemInHand.copyWithCount(1));
                this.dropEquipment(EquipmentSlot.MAINHAND, mainHandItem);
                this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
                if (!pPlayer.getAbilities().instabuild) {
                    itemInHand.shrink(1);
                }
                this.updateList = 5;
                return InteractionResult.SUCCESS;
            } else if (pPlayer.getMainHandItem().is(Items.STICK)) {
                this.dropEquipment(EquipmentSlot.MAINHAND, this.getMainHandItem());
                this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                return InteractionResult.SUCCESS;
            } else if (pPlayer.getMainHandItem().is(ModItems.WAYSTONE.get())) {
                if (WaystoneItem.isSameDimension(this, pPlayer.getMainHandItem())) {
                    if (WaystoneItem.getBlockEntity(pPlayer.getMainHandItem(), this.level) instanceof ChestBlockEntity chestBlock && chestBlock.canOpen(pPlayer)) {
                        if (!this.level.isClientSide) {
                            BlockPos blockPos = WaystoneItem.getBlockPos(pPlayer.getMainHandItem());
                            if (blockPos != null) {
                                this.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                                if (this.level instanceof ServerLevel serverLevel) {
                                    for (int i = 0; i < 7; ++i) {
                                        double d0 = this.random.nextGaussian() * 0.02D;
                                        double d1 = this.random.nextGaussian() * 0.02D;
                                        double d2 = this.random.nextGaussian() * 0.02D;
                                        serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                                    }
                                }
                                if (chestBlock.getBlockState().is(ModTags.Blocks.RAIDING_CHESTS)) {
                                    this.setDumpChestPos(blockPos);
                                    this.setDumpChestDim(this.level.dimension());
                                } else {
                                    this.setChestPos(blockPos);
                                    this.setChestDim(this.level.dimension());
                                }
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }
                return InteractionResult.FAIL;
            } else if (pPlayer.getMainHandItem().isEmpty() && this.isMining()) {
                float f = (float)Mth.floor((Mth.wrapDegrees(pPlayer.getYRot() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                this.setYRot(f);
                this.setYBodyRot(f);
                this.setYHeadRot(f);
                this.setCustomLook(f);
                this.playSound(SoundEvents.VILLAGER_NO, 1.0F, 0.75F);
                return InteractionResult.SUCCESS;
            } else if (this.getLeader() != null) {
                this.setLeader(null);
                this.setFollowing();
                return InteractionResult.SUCCESS;
            } else if (this.getTrueOwner() instanceof OwnableEntity ownable && ownable.getOwner() == pPlayer) {
                this.setTrueOwner(pPlayer);
                this.setFollowing();
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4) {
            this.isChained = true;
        } else if (p_21375_ == 5) {
            this.isChained = false;
        } else {
            super.handleEntityEvent(p_21375_);
        }

    }

    public static class MiningGoal extends Goal {
        public Prisoner prisoner;

        public MiningGoal(Prisoner prisoner) {
            this.prisoner = prisoner;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            if (MobsConfig.PrisonerMining.get()) {
                if (this.prisoner.getMainHandItem().is(ItemTags.PICKAXES)) {
                    if (this.prisoner.isGuardingArea()) {
                        if (this.prisoner.distanceToSqr(this.prisoner.vec3BoundPos()) > Mth.square(2)) {
                            return false;
                        }
                    }
                    return this.prisoner.itemsInInv(itemStack -> !itemStack.isEmpty()).size() < 64
                            && !this.prisoner.isFollowing()
                            && !this.prisoner.isCommanded()
                            && this.prisoner.noBlockTick <= 0
                            && this.prisoner.getTarget() == null
                            && this.prisoner.hurtTime <= 0;
                }
            }
            return false;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            super.start();
            this.prisoner.getNavigation().stop();
            this.prisoner.getMoveControl().strafe(0.0F, 0.0F);
            this.prisoner.setMining(true);
            if (this.prisoner.hasCustomLook()) {
                this.prisoner.setYRot(this.prisoner.getCustomLook());
                this.prisoner.setYBodyRot(this.prisoner.getCustomLook());
                this.prisoner.setYHeadRot(this.prisoner.getCustomLook());
            }
            if (this.prisoner.isGuardingArea()) {
                if (this.prisoner.distanceToSqr(this.prisoner.vec3BoundPos()) <= Mth.square(2.0D)) {
                    BlockPos blockPos = this.prisoner.getBoundPos();
                    if (!BlockFinder.isPassableBlock(this.prisoner.level, blockPos)) {
                        blockPos = blockPos.above();
                    }
                    if (BlockFinder.isPassableBlock(this.prisoner.level, blockPos)) {
                        this.prisoner.moveTo(blockPos, this.prisoner.getYRot(), this.prisoner.getXRot());
                    }
                }
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.prisoner.setMining(false);
        }

        @Override
        public void tick() {
            super.tick();
            if (this.prisoner.level instanceof ServerLevel serverLevel) {
                if (this.prisoner.tickCount % 5 == 0) {
                    ColorUtil colorUtil = new ColorUtil(0xFFFB5A);
                    serverLevel.sendParticles(ModParticleTypes.RISING_SPIRAL.get(), this.prisoner.getRandomX(1.0D), this.prisoner.getEyeY(), this.prisoner.getRandomZ(1.0D), 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.0F);
                }
            }
            this.prisoner.getNavigation().stop();
            this.prisoner.getMoveControl().strafe(0.0F, 0.0F);
        }
    }

    public abstract static class ThrowItemGoal extends Goal {
        public Prisoner prisoner;
        public LivingEntity target;
        public Predicate<ItemStack> predicate = itemStack -> false;
        public Predicate<LivingEntity> targetPredicate = living -> false;
        public int throwTime;

        public ThrowItemGoal(Prisoner prisoner){
            this.prisoner = prisoner;
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.hasItem()){
                this.target = this.getThrowTarget();
                if (this.target != null) {
                    if (this.prisoner.isStaying()) {
                        return this.prisoner.isWithinThrowingDistance(this.target) && this.prisoner.hasLineOfSight(this.target);
                    }
                    return this.target.distanceTo(this.prisoner) <= 8.0D && this.prisoner.hasLineOfSight(this.target);
                }
            }
            return false;
        }

        @Override
        public void stop() {
            this.target = null;
            this.throwTime = 0;
        }

        public boolean isWithinThrowingDistance() {
            if (this.target == null) {
                return false;
            }
            return this.prisoner.isWithinThrowingDistance(this.target);
        }

        public void tick() {
            if (this.target == null){
                this.stop();
            }
            this.prisoner.getLookControl().setLookAt(this.target, 10.0F, (float)this.prisoner.getMaxHeadXRot());
            if (this.isWithinThrowingDistance()){
                this.prisoner.getNavigation().stop();
                ++this.throwTime;
                if (this.throwTime > 20){
                    this.throwItem();
                    this.throwTime = 0;
                }
            } else {
                this.prisoner.getNavigation().moveTo(this.target, 0.6F);
            }
            if (this.target instanceof Mob mob) {
                mob.getLookControl().setLookAt(this.prisoner);
                mob.getNavigation().stop();
            }
        }

        public void throwItem() {
            if (this.target == null){
                this.stop();
            }
            for (ItemStack itemstack : this.prisoner.itemsInInv(this.predicate)) {
                BehaviorUtils.throwItem(this.prisoner, itemstack.copyAndClear(), this.target.position());
            }
        }

        public boolean hasItem(){
            return !this.prisoner.itemsInInv(this.predicate).isEmpty();
        }

        @Nullable
        public LivingEntity getThrowTarget() {
            List<LivingEntity> list = this.prisoner.level.getEntitiesOfClass(LivingEntity.class, this.prisoner.getBoundingBox().inflate(16.0D));
            list.sort(Comparator.comparingDouble(this.prisoner::distanceToSqr));
            LivingEntity target = null;

            for(LivingEntity servant : list) {
                if (servant != this.prisoner
                        && this.targetPredicate.test(servant)
                        && this.prisoner.hasLineOfSight(servant)) {
                    target = servant;
                }
            }

            return target;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public static class GiveMinedGoal extends ThrowItemGoal {

        public GiveMinedGoal(Prisoner prisoner){
            super(prisoner);
            this.predicate = itemStack -> !itemStack.isEmpty();
            this.targetPredicate = living ->
                    (living instanceof AbstractIllagerServant servant1
                            && servant1.getTrueOwner() == prisoner.getTrueOwner()
                            && !servant1.inventoryFull()
                            && !servant1.isBaby()) ||
                            (prisoner.getTrueOwner() instanceof Player
                            && living == prisoner.getTrueOwner());
        }

        @Override
        public boolean canUse() {
            if (this.prisoner.isMining()) {
                return false;
            }
            return super.canUse();
        }

        public boolean isWithinThrowingDistance() {
            if (this.target == null) {
                return false;
            } else if (this.target instanceof InventoryCarrier) {
                return this.prisoner.isWithinDistance(this.target, 2.0D);
            }
            return super.isWithinThrowingDistance();
        }

        public void throwItem() {
            if (this.target == null) {
                this.stop();
            }
            SimpleContainer simpleContainer = this.prisoner.getInventory();
            List<ItemStack> list = new ArrayList<>();
            for (int i = 0; i < simpleContainer.getContainerSize(); ++i){
                ItemStack itemstack1 = simpleContainer.getItem(i);
                if (!itemstack1.isEmpty()) {
                    list.add(itemstack1.copyAndClear());
                }
            }
            if (!list.isEmpty()) {
                for (ItemStack itemStack : list) {
                    if (this.target instanceof InventoryCarrier carrier) {
                        if (carrier.getInventory().canAddItem(itemStack)){
                            carrier.getInventory().addItem(itemStack.copyAndClear());
                            this.target.playSound(SoundEvents.ITEM_PICKUP);
                        }
                    } else {
                        BehaviorUtils.throwItem(this.prisoner, itemStack.copyAndClear(), this.target.position());
                    }
                }
            }
        }
    }

    public static class PrisonerPutLootChestGoal<T extends Prisoner> extends IllagerStoreChestGoal<T> {

        public PrisonerPutLootChestGoal(T prisoner) {
            super(prisoner);
            this.predicate = itemStack -> !itemStack.isEmpty();
            this.chestPredicate = itemStack -> true;
        }

        @Override
        public boolean canUse() {
            if (this.illager.isMining()) {
                return false;
            }
            return super.canUse();
        }

        @Override
        public void chestInteract(Container container) {
            SimpleContainer inventory = this.illager.getInventory();

            for (int invSlot = 0; invSlot < inventory.getContainerSize(); ++invSlot) {
                ItemStack itemStack = inventory.getItem(invSlot);

                if (itemStack.isEmpty()) {
                    continue;
                }

                for (int chestSlot = 0; chestSlot < container.getContainerSize(); ++chestSlot) {
                    ItemStack chestItem = container.getItem(chestSlot);

                    if (chestItem.isEmpty()) {
                        container.setItem(chestSlot, itemStack.copyAndClear());
                        container.setChanged();
                        break;
                    } else if (ItemStack.isSameItemSameTags(chestItem, itemStack)) {
                        int space = Math.min(container.getMaxStackSize(), chestItem.getMaxStackSize()) - chestItem.getCount();
                        if (space > 0) {
                            int amount = Math.min(space, itemStack.getCount());
                            chestItem.grow(amount);
                            itemStack.shrink(amount);
                            container.setChanged();

                            if (itemStack.isEmpty()) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static class PrisonerGetPickChestGoal<T extends Prisoner> extends IllagerChestGoal<T> {

        public PrisonerGetPickChestGoal(T prisoner) {
            super(prisoner);
            this.predicate = itemStack -> true;
            this.chestPredicate = itemStack -> itemStack.is(ItemTags.PICKAXES) || itemStack.getItem() instanceof PickaxeItem;
        }

        @Override
        public boolean canUse() {
            if (this.illager.isMining()) {
                return false;
            }
            if (this.illager.getChestPos() == null) {
                return false;
            }
            if (this.illager.getChestLevel() != this.illager.level.dimension()) {
                return false;
            }
            if (this.illager.getBoundPos() != null){
                if (this.illager.getChestPos() != null){
                    if (!this.illager.isWithinGuard(this.illager.getChestPos())){
                        return false;
                    }
                }
            }
            if (this.getChest(this.illager.level, this.illager.getChestPos()) == null) {
                return false;
            }
            if (!this.illager.getMainHandItem().isEmpty()) {
                return false;
            }
            if (!this.isChestRaidable(this.illager.level, this.illager.getChestPos())){
                return false;
            }
            if (this.illager.level.getEntitiesOfClass(LivingEntity.class, this.illager.getBoundingBox().inflate(16.0F),
                    livingEntity ->
                            ((livingEntity instanceof IOwned owned
                                    && owned.getTrueOwner() == this.illager.getTrueOwner())
                                    || (this.illager.getMasterOwner() == livingEntity))
                            && this.illager.hasLineOfSight(livingEntity)).isEmpty()) {
                return false;
            }
            return super.canUse();
        }

        @Override
        public void chestInteract(Container container) {
            for (ItemStack itemStack : this.getItems(container)) {
                if (this.illager.getMainHandItem().isEmpty()){
                    this.illager.setItemSlot(EquipmentSlot.MAINHAND, itemStack.copyAndClear());
                    container.setChanged();
                }
            }
        }
    }
}
