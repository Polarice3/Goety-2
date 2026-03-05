package com.Polarice3.Goety.common.entities.ally.illager;

import com.Polarice3.Goety.api.entities.ITrainable;
import com.Polarice3.Goety.api.entities.ally.illager.ILooter;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.entities.OminousPyreBlockEntity;
import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.IllagerLootFoodChestGoal;
import com.Polarice3.Goety.common.entities.ai.IllagerPutFoodChestGoal;
import com.Polarice3.Goety.common.entities.ai.IllagerPutLootChestGoal;
import com.Polarice3.Goety.common.entities.ally.illager.raider.AllyTrampler;
import com.Polarice3.Goety.common.entities.ally.illager.raider.ModRavager;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.entities.ally.illager.raider.Ravaged;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public abstract class AbstractIllagerServant extends RaiderServant implements ITrainable, ILooter {
    protected static final EntityDataAccessor<String> CURRENT_TRAIN = SynchedEntityData.defineId(AbstractIllagerServant.class, EntityDataSerializers.STRING);
    protected static final EntityDataAccessor<Optional<BlockPos>> TRAIN_POS = SynchedEntityData.defineId(AbstractIllagerServant.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    protected static final EntityDataAccessor<Optional<BlockPos>> STORED_TRAIN_POS = SynchedEntityData.defineId(AbstractIllagerServant.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    protected int trainTime;
    protected int trainCheck;
    protected int trainCompleted = 0;
    private int breedCool;
    private int eatCool;
    private int eatenFoodLevel;
    private int foodLevel;
    public int cantDo;
    @Nullable
    public BlockPos chestPos;
    public String chestDim = Level.OVERWORLD.location().toString();
    @Nullable
    public BlockPos dumpChestPos;
    public String dumpChestDim = Level.OVERWORLD.location().toString();
    private final SimpleContainer inventory = new SimpleContainer(8);

    public AbstractIllagerServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.throwGoal();
        this.chestGoal();
        this.goalSelector.addGoal(2, new RaiderOpenDoorGoal(this));
        this.goalSelector.addGoal(7, new MakeLove(this));
    }

    public void targetSelectGoal(){
        super.targetSelectGoal();
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, RaiderServant.class){
            @Override
            public boolean canUse() {
                return super.canUse() && (AbstractIllagerServant.this.isHostile() || AbstractIllagerServant.this.isNatural());
            }
        }).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false, livingEntity -> this.isHostile() && !livingEntity.isBaby()).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false, livingEntity -> this.isHostile()));
    }

    public void throwGoal() {
        this.goalSelector.addGoal(3, new GiveExcessFoodGoal(this));
        this.goalSelector.addGoal(3, new ThrowRottenFleshGoal(this));
        this.goalSelector.addGoal(4, new FeedRottenFleshGoal(this));
        this.goalSelector.addGoal(5, new ThrowLootGoal(this));
    }

    public void chestGoal() {
        this.goalSelector.addGoal(2, new IllagerPutFoodChestGoal<>(this));
        this.goalSelector.addGoal(4, new IllagerPutLootChestGoal<>(this));
        this.goalSelector.addGoal(7, new IllagerLootFoodChestGoal<>(this));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CURRENT_TRAIN, "None");
        this.entityData.define(TRAIN_POS, Optional.empty());
        this.entityData.define(STORED_TRAIN_POS, Optional.empty());
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("BreedCool", this.breedCool);
        compound.putInt("EatCool", this.eatCool);
        compound.putInt("EatenFoodLevel", this.eatenFoodLevel);
        this.saveLooterData(compound);
        this.saveTrainableData(compound);
        this.writeInventoryToTag(compound);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("BreedCool")) {
            this.breedCool = compound.getInt("BreedCool");
        }
        if (compound.contains("EatCool")) {
            this.eatCool = compound.getInt("EatCool");
        }
        if (compound.contains("EatenFoodLevel")) {
            this.eatenFoodLevel = compound.getInt("EatenFoodLevel");
        }
        this.readLooterData(compound);
        this.readTrainableData(compound);
        this.readInventoryFromTag(compound);
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public SlotAccess getSlot(int p_149995_) {
        int i = p_149995_ - 300;
        return i >= 0 && i < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, i) : super.getSlot(p_149995_);
    }

    public MobType getMobType() {
        return MobType.ILLAGER;
    }

    @Nullable
    @Override
    public Team getTeam() {
        if (super.getTeam() != null){
            return super.getTeam();
        } else {
            //To allow Illagers to hurt other vanilla/modded Illagers
            return new DummyTeam();
        }
    }

    public @NotNull Component getDisplayName() {
        return formatNameForTeam(this.getTeam(), this.getName()).withStyle((p_185975_) -> {
            return p_185975_.withHoverEvent(this.createHoverEvent()).withInsertion(this.getStringUUID());
        });
    }

    public static MutableComponent formatNameForTeam(@Nullable Team p_83349_, Component p_83350_) {
        return p_83349_ == null || p_83349_ instanceof DummyTeam ? p_83350_.copy() : p_83349_.getFormattedName(p_83350_);
    }

    @Override
    public boolean canBeLeader() {
        return !this.isTraining();
    }

    @Override
    public boolean canJoinPatrol() {
        return !this.isTraining();
    }

    @Override
    public boolean canCommandToBlock(Level level, BlockPos blockPos) {
        return super.canCommandToBlock(level, blockPos) || level.getBlockEntity(blockPos) instanceof OminousPyreBlockEntity;
    }

    @Override
    public void setCommandPos(BlockPos blockPos) {
        super.setCommandPos(blockPos);
        if (blockPos != null) {
            if (this.level.getBlockEntity(blockPos) instanceof OminousPyreBlockEntity pyre) {
                EntityType<? extends Mob> entityType = pyre.getTrainedMob(this.level, blockPos);
                if (pyre.capacityAvailable(this.level, blockPos)
                        && entityType != null
                        && pyre.trainingRequirements(this.level, blockPos).test(this)
                        && pyre.getTrueOwner() == this.getTrueOwner()) {
                    this.setStoredTrainPos(blockPos);
                }
            }
        }
    }

    @Override
    public int getTotalTrainTime() {
        return MathHelper.secondsToTicks(MobsConfig.IllagerServantTrainTime.get());
    }

    @Override
    public Optional<BlockPos> getTrainPos() {
        return this.entityData.get(TRAIN_POS);
    }

    @Override
    public void setTrainPos(@Nullable BlockPos trainPos) {
        this.entityData.set(TRAIN_POS, Optional.ofNullable(trainPos));
    }

    public Optional<BlockPos> getStoredTrainPos() {
        return this.entityData.get(STORED_TRAIN_POS);
    }

    public void setStoredTrainPos(@Nullable BlockPos blockPos) {
        this.entityData.set(STORED_TRAIN_POS, Optional.ofNullable(blockPos));
    }

    public String getCurrentTrain() {
        return this.entityData.get(CURRENT_TRAIN);
    }

    public void setCurrentTrain(String train) {
        this.entityData.set(CURRENT_TRAIN, train);
    }

    public int getTrainTime() {
        return this.trainTime;
    }

    public void setTrainTime(int time){
        this.trainTime = time;
    }

    public int getTrainCheck() {
        return this.trainCheck;
    }

    public void setTrainCheck(int check){
        this.trainCheck = check;
    }

    @Override
    public boolean canTrain(Level level, BlockPos blockPos, EntityType<? extends Mob> entityType) {
        boolean flag = true;
        if (!MobsConfig.IllagerServantAutoTrain.get()){
            flag = this.getStoredTrainPos().isPresent() && BlockFinder.samePos(this.getStoredTrainPos().get(), blockPos);
        }
        if (flag) {
            this.setTrainCheck(10);
            return true;
        }
        return false;
    }

    public boolean canOpenDoors(){
        return MobsConfig.IllagerServantAllOpenDoors.get();
    }

    protected void customServerAiStep() {
        if (this.canOpenDoors()) {
            if (!this.isNoAi() && GoalUtils.hasGroundPathNavigation(this)) {
                boolean flag = this.isRaiding();
                ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(flag);
            }
        }

        super.customServerAiStep();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getLeader() instanceof AbstractIllagerServant servant) {
            if (!BlockFinder.samePos(this.getDumpChestPos(), servant.getDumpChestPos())) {
                this.setDumpChestPos(servant.getDumpChestPos());
                if (servant.getDumpChestLevel() != null) {
                    this.setDumpChestDim(servant.getDumpChestLevel());
                }
            }
            if (!BlockFinder.samePos(this.getChestPos(), servant.getChestPos())) {
                this.setChestPos(servant.getChestPos());
                if (servant.getChestLevel() != null) {
                    this.setChestDim(servant.getChestLevel());
                }
            }
        }
        if (this.cantDo > 0){
            --this.cantDo;
        }
        if (!this.level.isClientSide) {
            if (this.breedCool > 0) {
                --this.breedCool;
            }
            if (this.eatCool > 0) {
                --this.eatCool;
            }
            if (this.trainCompleted > 0) {
                --this.trainCompleted;
            }
            this.trainTick();
            if (this.eatenFoodLevel > 0) {
                if (this.tickCount % 20 == 0) {
                    --this.eatenFoodLevel;
                    this.heal(1.0F);
                }
            }
        }
    }

    @Override
    public void healServant() {
        super.healServant();
        if (!this.level.isClientSide) {
            if (this.eatenFoodLevel <= 0 && this.getHealth() < this.getMaxHealth()) {
                if (this.countFoodPointsInInventory() != 0) {
                    for (int i = 0; i < this.getInventory().getContainerSize(); ++i) {
                        ItemStack itemstack = this.getInventory().getItem(i);
                        if (!itemstack.isEmpty()) {
                            Integer integer = this.getFoodPoints().get(itemstack.getItem());
                            if (integer != null) {
                                FoodProperties foodProperties = itemstack.getFoodProperties(this);
                                if (foodProperties != null) {
                                    if (!foodProperties.getEffects().isEmpty()) {
                                        for(Pair<MobEffectInstance, Float> pair : foodProperties.getEffects()) {
                                            if (pair.getFirst() != null && this.getRandom().nextFloat() < pair.getSecond()) {
                                                this.addEffect(new MobEffectInstance(pair.getFirst()));
                                            }
                                        }
                                    }
                                }
                                if (!BrewUtils.isEmpty(itemstack)){
                                    if (!PotionUtils.getMobEffects(itemstack).isEmpty()) {
                                        for (MobEffectInstance instance : PotionUtils.getMobEffects(itemstack)) {
                                            if (instance.getEffect().isInstantenous()) {
                                                instance.getEffect().applyInstantenousEffect(this, this, this, instance.getAmplifier(), 1.0D);
                                            } else {
                                                this.addEffect(new MobEffectInstance(instance));
                                            }
                                        }
                                    }
                                    if (!BrewUtils.getBrewEffects(itemstack).isEmpty()) {
                                        for (BrewEffectInstance instance : BrewUtils.getBrewEffects(itemstack)) {
                                            instance.getEffect().drinkBlockEffect(this, this, this, instance.getAmplifier(), BrewUtils.getAreaOfEffect(itemstack));
                                        }
                                    }
                                }
                                this.eatenFoodLevel = integer;
                                this.playSound(SoundEvents.GENERIC_EAT);
                                if (this.level instanceof ServerLevel serverLevel) {
                                    ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, new ItemParticleOption(ParticleTypes.ITEM, itemstack), this);
                                }
                                this.getInventory().removeItem(i, 1);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public double getCommandSpeed() {
        return 1.0D;
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

    public boolean inventoryFull() {
        int i = 0;
        for (int j = 0; j < this.getInventory().getContainerSize(); ++j) {
            ItemStack itemStack = this.getInventory().getItem(j);
            if (itemStack.isEmpty()) {
                ++i;
            }
        }
        return i == 0;
    }

    public int getBreedCool(){
        return this.breedCool;
    }

    public void setBreedCool(int cool) {
        this.breedCool = cool;
    }

    public boolean canBreed() {
        boolean flag = true;
        if (!MobsConfig.IllagerServantAllBreed.get()){
            flag = this.getType() == ModEntityType.NEOLLAGER.get();
        }
        return flag
                && this.foodLevel + this.countFoodPointsInInventory() >= 12
                && !this.isSleeping()
                && this.breedCool <= 0
                && this.hurtTime <= 0
                && this.getTarget() == null
                && !this.isFollowing()
                && !this.isCommanded()
                && !this.isCelebrating()
                && !this.isRaiding()
                && !this.isBaby();
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

    private boolean hungry() {
        return this.foodLevel < 12;
    }

    private void eatUntilFull() {
        if (this.hungry() && this.countFoodPointsInInventory() != 0) {
            for(int i = 0; i < this.getInventory().getContainerSize(); ++i) {
                ItemStack itemstack = this.getInventory().getItem(i);
                if (!itemstack.isEmpty()) {
                    Integer integer = this.getFoodPoints().get(itemstack.getItem());
                    if (integer != null) {
                        int j = itemstack.getCount();

                        for(int k = j; k > 0; --k) {
                            this.foodLevel += integer;
                            this.getInventory().removeItem(i, 1);
                            if (!this.hungry()) {
                                return;
                            }
                        }
                    }
                }
            }

        }
    }

    private void digestFood(int p_35549_) {
        this.foodLevel -= p_35549_;
    }

    public void eatAndDigestFood() {
        this.eatUntilFull();
        this.digestFood(12);
    }

    public int countFoodPointsInInventory() {
        SimpleContainer simplecontainer = this.getInventory();
        return this.getFoodPoints().entrySet().stream().mapToInt((entry) -> {
            return simplecontainer.countItem(entry.getKey()) * entry.getValue();
        }).sum();
    }

    public int countFoodInInventory() {
        SimpleContainer simplecontainer = this.getInventory();
        return this.getFoodPoints().keySet().stream().mapToInt(simplecontainer::countItem).sum();
    }

    public boolean validFood(ItemStack itemStack) {
        FoodProperties foodProperties = itemStack.getFoodProperties(this);
        if (foodProperties == null) {
            return false;
        } else if (foodProperties.isMeat() && foodProperties.getNutrition() <= 3) {
            return false;
        } else {
            return foodProperties.getEffects().isEmpty() || itemStack.is(Items.ROTTEN_FLESH);
        }
    }

    public boolean validLootToStore(ItemStack itemStack) {
        return !itemStack.isEmpty() && !this.validFood(itemStack);
    }

    public boolean canEat(ItemStack itemStack) {
        return !itemStack.is(Items.ROTTEN_FLESH);
    }

    public Map<Item, Integer> getFoodPoints(){
        SimpleContainer simplecontainer = this.getInventory();
        int i = simplecontainer.getContainerSize();
        Map<Item, Integer> foodPoints = new HashMap<>();
        for (int j = 0; j < i; ++j) {
            ItemStack itemStack = simplecontainer.getItem(j);
            if (itemStack.getFoodProperties(this) != null){
                FoodProperties foodProperties = itemStack.getFoodProperties(this);
                if (foodProperties != null && this.validFood(itemStack) && this.canEat(itemStack)){
                    foodPoints.put(itemStack.getItem(), foodProperties.getNutrition());
                }
            }
        }
        return foodPoints;
    }

    public boolean hasExcessFood() {
        return this.countFoodInInventory() > 24;
    }

    public boolean canHaveMoreFood() {
        return this.countFoodPointsInInventory() < 64;
    }

    public boolean wantsMoreFood() {
        return this.countFoodPointsInInventory() < 12;
    }

    protected void addParticlesAroundSelf(ParticleOptions p_35288_) {
        for(int i = 0; i < 5; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(p_35288_, this.getRandomX(1.0D), this.getRandomY() + 1.0D, this.getRandomZ(1.0D), d0, d1, d2);
        }

    }

    public @NotNull Vec3 getLeashOffset() {
        return new Vec3(0.0D, 0.6F * this.getEyeHeight(), (double)(this.getBbWidth() * 0.4F));
    }

    public Vec3 getRopeHoldPosition(float p_35318_) {
        float f = Mth.lerp(p_35318_, this.yBodyRotO, this.yBodyRot) * ((float)Math.PI / 180F);
        Vec3 vec3 = new Vec3(0.0D, this.getBoundingBox().getYsize() - 1.0D, 0.2D);
        return this.getPosition(p_35318_).add(vec3.yRot(-f));
    }

    protected void pickUpItem(ItemEntity itemEntity) {
        ItemStack itemstack = itemEntity.getItem();
        boolean flag = (this.getMarked() != null || this.isRaiding()) && !this.isLeader();
        if (flag && this.getLeader() == null && !this.getLeaderBannerInstance().isEmpty() && ItemHelper.sameBanner(itemstack, this.getBannerPatternInstance())) {
            ItemStack itemstack1 = this.getItemBySlot(EquipmentSlot.HEAD);
            double d0 = this.getEquipmentDropChance(EquipmentSlot.HEAD);
            if (!itemstack1.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1F, 0.0F) < d0) {
                this.spawnAtLocation(itemstack1);
            }

            this.onItemPickup(itemEntity);
            this.setItemSlot(EquipmentSlot.HEAD, itemstack);
            this.setGuaranteedDrop(EquipmentSlot.HEAD);
            this.take(itemEntity, itemstack.getCount());
            itemEntity.discard();
        } else {
            InventoryCarrier.pickUpItem(this, this, itemEntity);
        }
    }

    @Override
    public boolean canPickUpLoot() {
        return MobsConfig.IllagerServantPickUpDrops.get();
    }

    public boolean wantsToPickUp(ItemStack itemStack) {
        return MobsConfig.IllagerServantPickUpDrops.get()
                && this.validFood(itemStack)
                && this.canHaveMoreFood()
                && this.getInventory().canAddItem(itemStack);
    }

    @Nullable
    public AbstractIllagerServant getBreedOffspring(ServerLevel serverLevel, AbstractIllagerServant illager) {
        Neollager neollager = new Neollager(ModEntityType.NEOLLAGER.get(), serverLevel);
        neollager.copyTrueOwner(this);
        ForgeEventFactory.onFinalizeSpawn(neollager, serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.BREEDING, null, null);
        if (illager.isGuardingArea()) {
            neollager.setBoundPos(illager.getBoundPos());
            neollager.setBoundDim(illager.getBoundLevel());
        }
        neollager.setWandering(illager.isWandering());
        neollager.setStaying(illager.isStaying());
        return neollager;
    }

    @Override
    public int trainSpeed(EntityType<? extends Mob> entityType) {
        int i = this.isBaby() ? 2 : 1;
        Mob mob = entityType.create(this.level);
        if (mob instanceof PillagerServant){
            i *= 2;
        }
        List<AbstractIllagerServant> list = this.level.getNearbyEntities(AbstractIllagerServant.class,
                TargetingConditions.forNonCombat().range(8.0D)
                        .ignoreLineOfSight()
                        .ignoreInvisibilityTesting()
                        .selector(livingEntity ->
                                livingEntity instanceof AbstractIllagerServant illager
                                        && illager.getTrueOwner() == this.getTrueOwner()
                                        && illager.getType() == entityType),
                this, this.getBoundingBox().inflate(8.0D));
        i += Mth.clamp(list.size(), 0, MobsConfig.IllagerServantMaxMentors.get());
        return i;
    }

    @Override
    public void train(EntityType<? extends Mob> entityType) {
        if (Objects.equals(this.getCurrentTrain(), entityType.getDescriptionId())) {
            if (this.getTrainTime() < this.getTotalTrainTime()) {
                if (this.getTrainTime() % 1000 == 0) {
                    if (this.level instanceof ServerLevel serverLevel) {
                        ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ParticleTypes.HAPPY_VILLAGER, this);
                    }
                }
                this.setTrainTime(this.getTrainTime() + this.trainSpeed(entityType));
            } else {
                this.completeTraining(entityType);
            }
        } else {
            this.setTrainTime(0);
            this.setCurrentTrain(entityType.getDescriptionId());
            if (this.level instanceof ServerLevel serverLevel) {
                ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ParticleTypes.HAPPY_VILLAGER, this);
            }
        }
        this.setTrainCheck(10);
    }

    @Override
    public boolean isTrained() {
        return this.trainCompleted > 0;
    }

    @Override
    public void completeTraining(EntityType<? extends Mob> entityType) {
        if (!this.isTrained()) {
            if (this.level instanceof ServerLevel serverLevel) {
                ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ParticleTypes.FLAME, this);
            }
            ITrainable.super.completeTraining(entityType);
            this.trainCompleted = 10;
        }
    }

    public void handleEntityEvent(byte p_35391_) {
        if (p_35391_ == 9){
            this.cantDo = 40;
        } else if (p_35391_ == 12) {
            this.addParticlesAroundSelf(ParticleTypes.HEART);
        } else {
            super.handleEntityEvent(p_35391_);
        }
    }

    public IllagerServantArmPose getArmPose() {
        return IllagerServantArmPose.CROSSED;
    }

    protected void dropEquipment() {
        this.dropAllInventory();
    }

    protected void dropAllInventory() {
        for(int i = 0; i < this.getInventory().getContainerSize(); ++i) {
            ItemStack itemstack = this.getInventory().getItem(i);
            if (!itemstack.isEmpty()) {
                if (EnchantmentHelper.hasVanishingCurse(itemstack)) {
                    this.getInventory().removeItemNoUpdate(i);
                } else {
                    this.spawnAtLocation(itemstack);
                }
            }
        }
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

    public void addDrops(Collection<ItemEntity> items) {
        List<ItemStack> drops = items.stream()
                .filter(Objects::nonNull)
                .map(ItemEntity::getItem)
                .filter(itemStack -> !itemStack.isEmpty())
                .toList();
        for (ItemStack itemStack : drops){
            if (this.getInventory().canAddItem(itemStack)) {
                this.getInventory().addItem(itemStack);
            } else {
                this.spawnAtLocation(itemStack);
            }
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (this.validFood(itemstack)) {
                if (this.canHaveMoreFood() && this.getInventory().canAddItem(itemstack)) {
                    this.getInventory().addItem(itemstack.copyWithCount(1));
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                    if (this.level instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            } else if (item instanceof ArmorItem) {
                return ServantUtil.equipServantArmor(pPlayer, this, itemstack, super.mobInteract(pPlayer, pHand));
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
                                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                                    }
                                }
                                if (chestBlock.getBlockState().is(ModTags.Blocks.RAIDING_CHESTS)) {
                                    this.setDumpChestPos(blockPos);
                                    this.setDumpChestDim(this.level.dimension());
                                } else {
                                    this.setChestPos(blockPos);
                                    this.setChestDim(this.level.dimension());
                                }
                                if (this.isLeader()) {
                                    for (RaiderServant servant : this.getNearbyCompanions()) {
                                        if (servant instanceof AbstractIllagerServant servant1) {
                                            if (chestBlock.getBlockState().is(ModTags.Blocks.RAIDING_CHESTS)) {
                                                servant1.setDumpChestPos(blockPos);
                                                servant1.setDumpChestDim(this.level.dimension());
                                            } else {
                                                servant1.setChestPos(blockPos);
                                                servant1.setChestDim(this.level.dimension());
                                            }
                                        }
                                    }
                                }
                                return InteractionResult.SUCCESS;
                            }
                            return InteractionResult.FAIL;
                        }
                    }
                }
            } else if (itemstack.is(Items.TOTEM_OF_UNDYING) && this.getOffhandItem().isEmpty()) {
                this.setItemSlot(EquipmentSlot.OFFHAND, itemstack.copyWithCount(1));
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                if (this.getCelebrateSound() != null) {
                    this.playSound(this.getCelebrateSound(), 1.0F, 1.0F);
                }
                this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                if (this.level instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                    }
                }
                return InteractionResult.SUCCESS;
            } else if (itemstack.is(Items.STICK) && this.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) {
                ItemStack totem = this.getOffhandItem();
                this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                this.dropEquipment(EquipmentSlot.OFFHAND, totem);
                this.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public abstract static class ThrowItemGoal extends Goal {
        public AbstractIllagerServant illager;
        public LivingEntity target;
        public Predicate<ItemStack> predicate = itemStack -> false;
        public Predicate<LivingEntity> targetPredicate = living -> false;
        public int throwTime;

        public ThrowItemGoal(AbstractIllagerServant illager){
            this.illager = illager;
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.illager.getTarget() == null
                    && this.illager.getMarked() == null
                    && !this.illager.isCommanded()
                    && !this.illager.isRaiding()
                    && !this.illager.isCelebrating()
                    && this.hasItem()){
                this.target = this.getThrowTarget();
                if (this.target != null) {
                    if (this.illager.isStaying()) {
                        return this.illager.isWithinThrowingDistance(this.target) && this.illager.hasLineOfSight(this.target);
                    }
                    return this.target.distanceTo(this.illager) <= 8.0D && this.illager.hasLineOfSight(this.target);
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
            return this.illager.isWithinThrowingDistance(this.target);
        }

        public void tick() {
            if (this.target == null){
                this.stop();
            }
            this.illager.getLookControl().setLookAt(this.target, 10.0F, (float)this.illager.getMaxHeadXRot());
            if (this.isWithinThrowingDistance()){
                this.illager.getNavigation().stop();
                ++this.throwTime;
                if (this.throwTime > 20){
                    this.throwItem();
                    this.throwTime = 0;
                }
            } else {
                this.illager.getNavigation().moveTo(this.target, 0.6F);
            }
            if (this.target instanceof Mob mob) {
                mob.getLookControl().setLookAt(this.illager);
                mob.getNavigation().stop();
            }
        }

        public void throwItem() {
            if (this.target == null){
                this.stop();
            }
            for (ItemStack itemstack : this.illager.itemsInInv(this.predicate)) {
                BehaviorUtils.throwItem(this.illager, itemstack.copyAndClear(), this.target.position());
            }
        }

        public boolean hasItem(){
            return !this.illager.itemsInInv(this.predicate).isEmpty();
        }

        @Nullable
        public LivingEntity getThrowTarget() {
            List<LivingEntity> list = this.illager.level.getEntitiesOfClass(LivingEntity.class, this.illager.getBoundingBox().inflate(16.0D));
            list.sort(Comparator.comparingDouble(this.illager::distanceToSqr));
            LivingEntity illagerServant = null;

            for(LivingEntity servant : list) {
                if (servant != this.illager
                        && this.targetPredicate.test(servant)
                        && this.illager.hasLineOfSight(servant)) {
                    illagerServant = servant;
                }
            }

            return illagerServant;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public static class ThrowLootGoal extends ThrowItemGoal {

        public ThrowLootGoal(AbstractIllagerServant illager){
            super(illager);
            this.predicate = this.illager::validLootToStore;
            this.targetPredicate = livingEntity -> illager.getTrueOwner() != null
                    && livingEntity == illager.getTrueOwner();
        }

        @Override
        public boolean canUse() {
            return super.canUse();
        }

        @Nullable
        public LivingEntity getThrowTarget() {
            return this.illager.getTrueOwner();
        }
    }

    public static class GiveExcessFoodGoal extends ThrowItemGoal {

        public GiveExcessFoodGoal(AbstractIllagerServant illager){
            super(illager);
            this.predicate = ItemStack::isEdible;
            this.targetPredicate = living ->
                    living instanceof AbstractIllagerServant servant1
                    && servant1.getTrueOwner() == illager.getTrueOwner()
                    && servant1.wantsMoreFood()
                    && !servant1.isBaby();
        }

        @Override
        public boolean canUse() {
            if (this.illager.hasExcessFood()) {
                return super.canUse();
            }
            return false;
        }

        public boolean isWithinThrowingDistance() {
            if (this.target == null) {
                return false;
            } else if (this.target instanceof InventoryCarrier) {
                return this.illager.isWithinDistance(this.target, 2.0D);
            }
            return super.isWithinThrowingDistance();
        }

        public void throwItem() {
            if (this.target == null){
                this.stop();
            }
            SimpleContainer simpleContainer = this.illager.getInventory();
            List<ItemStack> list = new ArrayList<>();
            for (int i = 0; i < simpleContainer.getContainerSize(); ++i){
                ItemStack itemstack1 = simpleContainer.getItem(i);
                Item item = itemstack1.getItem();
                if (!itemstack1.isEmpty()) {
                    if (item.isEdible()) {
                        int j = 0;
                        if (itemstack1.getCount() > itemstack1.getMaxStackSize() / 2) {
                            j = itemstack1.getCount() / 2;
                        }

                        if (itemstack1.getCount() > 24) {
                            j = itemstack1.getCount() - 24;
                        }

                        if (j > 0) {
                            list.add(itemstack1.split(j));
                        }
                    }
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
                        BehaviorUtils.throwItem(this.illager, itemStack.copyAndClear(), this.target.position());
                    }
                }
            }
        }
    }

    public static class ThrowRottenFleshGoal extends ThrowItemGoal {
        public ThrowRottenFleshGoal(AbstractIllagerServant illager){
            super(illager);
            this.predicate = itemStack -> itemStack.is(Items.ROTTEN_FLESH);
            this.targetPredicate = living -> living instanceof Ravaged ravaged
                    && ravaged.getTrueOwner() == illager.getTrueOwner()
                    && ravaged.hurtTime <= 0
                    && ravaged.getTarget() == null
                    && !ravaged.isConverting();
        }
    }

    public static class FeedRottenFleshGoal extends ThrowItemGoal {

        public FeedRottenFleshGoal(AbstractIllagerServant illager){
            super(illager);
            this.predicate = itemStack -> itemStack.is(Items.ROTTEN_FLESH);
            this.targetPredicate = living -> living instanceof RaiderServant servant
                    && (servant instanceof ModRavager || servant instanceof AllyTrampler)
                    && servant.getTrueOwner() == illager.getTrueOwner()
                    && !servant.isRaiding()
                    && !servant.isCelebrating()
                    && servant.getMarked() == null
                    && servant.getHealth() < servant.getMaxHealth()
                    && servant.getTarget() == null;
        }

        @Override
        public void stop() {
            super.stop();
            if (!(this.illager instanceof PillagerServant)) {
                if (this.illager.getMainHandItem().is(Items.ROTTEN_FLESH)) {
                    this.illager.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                } else if (this.illager.getOffhandItem().is(Items.ROTTEN_FLESH)) {
                    this.illager.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }
            }
        }

        public void tick() {
            if (this.target == null){
                this.stop();
            }
            this.illager.getLookControl().setLookAt(this.target, 10.0F, (float)this.illager.getMaxHeadXRot());
            if (!this.illager.itemsInInv(this.predicate).isEmpty()) {
                Optional<ItemStack> optional = this.illager.itemsInInv(this.predicate).stream().findFirst();
                if (optional.isPresent()) {
                    ItemStack itemStack = optional.get();
                    if (!itemStack.isEmpty()){
                        FoodProperties foodProperties = itemStack.getFoodProperties(this.target);
                        if (foodProperties != null) {
                            if (!(this.illager instanceof PillagerServant)) {
                                if (this.illager.getMainHandItem().isEmpty()) {
                                    this.illager.setItemSlot(EquipmentSlot.MAINHAND, itemStack.copyWithCount(1));
                                } else if (this.illager.getOffhandItem().isEmpty()) {
                                    this.illager.setItemSlot(EquipmentSlot.OFFHAND, itemStack.copyWithCount(1));
                                }
                            }
                            if (this.illager.isWithinThrowingDistance(this.target)){
                                this.illager.getNavigation().stop();
                                ++this.throwTime;
                                if (this.throwTime > 20){
                                    this.target.heal(foodProperties.getNutrition());
                                    this.target.playSound(SoundEvents.GENERIC_EAT, 1.0F, 0.5F);
                                    this.target.gameEvent(GameEvent.EAT, this.target);
                                    this.throwTime = 0;
                                    itemStack.shrink(1);
                                    if (this.target.level instanceof ServerLevel serverLevel) {
                                        for (int i = 0; i < 7; ++i) {
                                            double d0 = this.target.getRandom().nextGaussian() * 0.02D;
                                            double d1 = this.target.getRandom().nextGaussian() * 0.02D;
                                            double d2 = this.target.getRandom().nextGaussian() * 0.02D;
                                            serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.target.getRandomX(1.0D), this.target.getRandomY() + 0.5D, this.target.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                                        }
                                    }
                                }
                            } else {
                                this.illager.getNavigation().moveTo(this.target, 0.6F);
                            }
                        } else {
                            this.stop();
                        }
                    } else {
                        this.stop();
                    }
                } else {
                    this.stop();
                }
            } else {
                this.stop();
            }
        }
    }

    protected class RaiderOpenDoorGoal extends OpenDoorGoal {
        public RaiderOpenDoorGoal(RaiderServant p_32128_) {
            super(p_32128_, false);
        }

        public boolean canUse() {
            return super.canUse() && AbstractIllagerServant.this.isRaiding() && AbstractIllagerServant.this.canOpenDoors();
        }
    }

    public static class MakeLove extends Goal {
        private final AbstractIllagerServant illager;
        @Nullable
        protected AbstractIllagerServant partner;
        private long loveTime;
        private boolean hasBred;

        public MakeLove(AbstractIllagerServant illager){
            this.illager = illager;
        }

        @Override
        public boolean canUse() {
            if (!this.illager.canBreed()){
                return false;
            }
            this.partner = this.getFreePartner();
            return this.partner != null && this.hasVacantBed();
        }

        @Override
        public boolean canContinueToUse() {
            return this.partner != null
                    && this.partner.isAlive()
                    && this.hasVacantBed()
                    && this.illager.level.getGameTime() <= this.loveTime
                    && this.illager.canBreed()
                    && this.partner.canBreed();
        }

        @Override
        public void start() {
            this.hasBred = false;
            int i = 275 + this.illager.level.getRandom().nextInt(50);
            this.loveTime = this.illager.level.getGameTime() + i;
        }

        @Override
        public void stop() {
            this.partner = null;
            this.loveTime = 0;
        }

        public void tick() {
            if (this.partner == null){
                this.stop();
            }
            this.illager.getLookControl().setLookAt(this.partner, 10.0F, (float)this.illager.getMaxHeadXRot());
            this.illager.getNavigation().moveTo(this.partner, 0.5F);
            if (this.illager.level.getGameTime() >= this.loveTime && this.illager.distanceToSqr(this.partner) <= 5.0D) {
                if (!this.hasBred) {
                    this.partner.eatAndDigestFood();
                    this.illager.eatAndDigestFood();
                }
                this.breed();
            } else if (this.illager.level.getRandom().nextInt(35) == 0) {
                this.illager.level.broadcastEntityEvent(this.illager, (byte)12);
                this.illager.level.broadcastEntityEvent(this.partner, (byte)12);
            }
        }

        public void breed(){
            this.illager.breedCool = 6000;
            if (this.partner == null || this.hasBred){
                return;
            }
            this.partner.breedCool = 6000;
            if (this.illager.level instanceof ServerLevel serverLevel) {
                AbstractIllagerServant baby = this.illager.getBreedOffspring(serverLevel, this.partner);
                if (baby != null) {
                    baby.moveTo(this.illager.getX(), this.illager.getY(), this.illager.getZ(), 0.0F, 0.0F);
                    baby.setBaby(true);
                    this.hasBred = true;
                    if (this.illager.level.addFreshEntity(baby)) {
                        this.illager.level.broadcastEntityEvent(baby, (byte) 12);
                    }
                }
            }
        }

        @Nullable
        private AbstractIllagerServant getFreePartner() {
            List<AbstractIllagerServant> list = this.illager.level.getEntitiesOfClass(AbstractIllagerServant.class, this.illager.getBoundingBox().inflate(16.0D));
            list.sort(Comparator.comparingDouble(this.illager::distanceToSqr));
            AbstractIllagerServant illagerServant = null;

            for(AbstractIllagerServant servant : list) {
                if (servant != this.illager && servant.getTrueOwner() == this.illager.getTrueOwner() && servant.canBreed() && this.illager.canBreed() && this.illager.hasLineOfSight(servant)) {
                    illagerServant = servant;
                }
            }

            return illagerServant;
        }

        public boolean hasVacantBed(){
            if (this.illager.level instanceof ServerLevel serverLevel) {
                Optional<BlockPos> optional = this.getVacantBed(serverLevel);
                return optional.isPresent() && this.canReach(optional.get());
            } else {
                return false;
            }
        }

        public Optional<BlockPos> getVacantBed(ServerLevel serverLevel) {
            return serverLevel.getPoiManager().find((holder) -> holder.is(PoiTypes.HOME)
                    , (blockPos -> true), this.illager.blockPosition(), 8, PoiManager.Occupancy.HAS_SPACE);
        }

        public boolean canReach(BlockPos p_217502_) {
            Path path = this.illager.getNavigation().createPath(p_217502_, 1);
            return path != null && path.canReach();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public static enum IllagerServantArmPose {
        CROSSED,
        ATTACKING,
        SPELLCASTING,
        BOW_AND_ARROW,
        CROSSBOW_HOLD,
        CROSSBOW_CHARGE,
        CELEBRATING,
        NEUTRAL;
    }
}
