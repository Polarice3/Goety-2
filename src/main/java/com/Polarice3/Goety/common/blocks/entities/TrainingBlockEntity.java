package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.api.blocks.entities.ITrainingBlock;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public abstract class TrainingBlockEntity extends OwnedBlockEntity implements ITrainingBlock, WorldlyContainer, GameEventListener {
    public static int RANGE = 4;
    private final BlockPositionSource blockPosSource = new BlockPositionSource(this.worldPosition);
    public int trainTime = 0;
    public int trainTimeTotal = 100;
    public int trainAmount;
    public int updateVariant;
    public boolean showArea;
    public boolean sensorSensitive;
    public ItemStack itemStack = ItemStack.EMPTY;
    public CompoundTag entityToSpawn = new CompoundTag();

    public TrainingBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, TrainingBlockEntity blockEntity) {
        blockEntity.tick(level, blockPos, blockState, blockEntity);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, TrainingBlockEntity blockEntity) {
        blockEntity.tick(level, blockPos, blockState, blockEntity);
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState, TrainingBlockEntity blockEntity){
        if (level != null){
            if (blockEntity.updateVariant > 0){
                --blockEntity.updateVariant;
                blockEntity.setVariant(blockEntity.itemStack, level, blockPos);
            }
            if (blockEntity.trainAmount > 0){
                if (blockEntity.trainTime < blockEntity.getMaxTrainTime()){
                    ++blockEntity.trainTime;
                } else {
                    boolean flag;
                    if (this.isSensorSensitive()){
                        flag = hasNearbyTarget();
                    } else {
                        flag = true;
                    }
                    if (flag) {
                        --blockEntity.trainAmount;
                        blockEntity.trainTime = 0;
                        blockEntity.itemStack = ItemStack.EMPTY;
                        if (level instanceof ServerLevel serverLevel) {
                            for (int i = 0; i < 64; ++i) {
                                RandomSource randomsource = level.getRandom();
                                double d0 = (double) blockPos.getX() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double) 4 + 0.5D;
                                double d1 = blockPos.getY() + randomsource.nextInt(3) - 1;
                                double d2 = (double) blockPos.getZ() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double) 4 + 0.5D;
                                BlockPos blockpos = new BlockPos(d0, d1, d2);
                                if (serverLevel.noCollision(blockEntity.getTrainMob().getAABB(d0, d1, d2))) {
                                    Entity entity = blockEntity.getTrainMob().spawn(serverLevel, (CompoundTag) null, (Component) null, (Player) null, blockpos, MobSpawnType.SPAWNER, false, false);
                                    if (entity != null) {
                                        level.gameEvent(entity, GameEvent.ENTITY_PLACE, blockpos);
                                        if (entity instanceof Mob mob) {
                                            mob.spawnAnim();
                                        }
                                        if (entity instanceof IOwned owned && blockEntity.getTrueOwner() != null) {
                                            owned.setTrueOwner(blockEntity.getTrueOwner());
                                        }
                                        blockEntity.playSpawnSound();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                this.markUpdated();
            } else {
                if (blockEntity.trainTime > 0) {
                    blockEntity.trainTime = 0;
                    blockEntity.markUpdated();
                }
            }
        }
    }

    public boolean hasNearbyTarget(){
        if (this.level != null) {
            BlockPos blockPos = this.worldPosition.offset(-RANGE, -RANGE, -RANGE);
            BlockPos blockPos1 = this.worldPosition.offset(RANGE, RANGE, RANGE);
            AABB aabb = new AABB(blockPos, blockPos1);
            List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, aabb);
            for (LivingEntity livingEntity : list) {
                if (EntitySelector.NO_SPECTATORS.test(livingEntity) && EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(livingEntity)) {
                    if (this.getTrueOwner() != null){
                        return predicate().test(livingEntity);
                    } else {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public Predicate<LivingEntity> predicate(){
        if (this.getTrueOwner() instanceof Enemy
                || (this.getTrueOwner() instanceof IOwned owned && owned.isHostile())){
            return (target) -> target instanceof Player player && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player);
        } else {
            return (target) ->
                    (target instanceof Enemy
                            && !(target.getMobType() == MobType.UNDEAD && this.getTrueOwner() != null && LichdomHelper.isLich(this.getTrueOwner()) && MainConfig.LichUndeadFriends.get())
                            && !(target.getMobType() == MobType.UNDEAD && this.getTrueOwner() != null && CuriosFinder.hasUndeadSet(this.getTrueOwner()) && MobsConfig.NecroRobeUndead.get())
                            && !(MobUtil.isWitchType(target) && this.getTrueOwner() != null && CuriosFinder.isWitchFriendly(this.getTrueOwner()))
                            && !(target instanceof Creeper && target.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && MobsConfig.MinionsAttackCreepers.get())
                            && !(target instanceof NeutralMob && ((this.getTrueOwner() != null && ((NeutralMob) target).getTarget() != this.getTrueOwner())))
                            && !(target instanceof IOwned && this.getTrueOwner() != null && ((IOwned) target).getTrueOwner() == this.getTrueOwner()))
                            || (target instanceof IOwned owned && owned.isHostile())
                            || (this.getTrueOwner() instanceof Player player
                            && ((!SEHelper.getGrudgeEntities(player).isEmpty() && SEHelper.getGrudgeEntities(player).contains(target))
                            || (!SEHelper.getGrudgeEntityTypes(player).isEmpty() && SEHelper.getGrudgeEntityTypes(player).contains(target.getType()))));
        }
    }

    public void setEntityType(EntityType<?> p_45463_) {
        ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(p_45463_);
        this.entityToSpawn.putString("id", location != null ? location.toString() : "minecraft:pig");
    }

    public void setEntityType(CompoundTag tag){
        ResourceLocation resourcelocation = ResourceLocation.tryParse(tag.getString("id"));
        this.entityToSpawn.putString("id", resourcelocation != null ? resourcelocation.toString() : "minecraft:pig");
    }

    public CompoundTag getEntityToSpawn() {
        return this.entityToSpawn;
    }

    public void setVariant(ItemStack itemStack, Level level, BlockPos blockPos){
    }

    @Override
    public EntityType<?> getTrainMob() {
        if (EntityType.by(this.getEntityToSpawn()).isPresent()) {
            return EntityType.by(this.getEntityToSpawn()).get();
        } else {
            return EntityType.PIG;
        }
    }

    public void startTraining(int amount, ItemStack itemStack){
        this.setVariant(itemStack, this.level, this.getBlockPos());
        this.trainAmount = Math.min(this.trainAmount + amount, this.maxTrainAmount());
    }

    public void playSpawnSound(){
    }

    @Override
    public int getTrainingTime() {
        return this.trainTime;
    }

    @Override
    public int amountTrainLeft() {
        return this.trainAmount;
    }

    @Override
    public int getMaxTrainTime() {
        return this.trainTimeTotal;
    }

    public boolean isTraining(){
        return this.amountTrainLeft() > 0;
    }

    public boolean isFuel(ItemStack itemStack){
        return true;
    }

    public boolean placeItem(ItemStack pStack) {
        if (this.level != null) {
            if (this.isFuel(pStack) && this.trainAmount < this.maxTrainAmount()) {
                this.itemStack = pStack;
                this.startTraining(1, pStack);
                if (pStack.hasCraftingRemainingItem()){
                    ItemHelper.addItemEntity(this.level, this.getBlockPos().above(), pStack.getCraftingRemainingItem());
                }
                pStack.shrink(1);
                this.markUpdated();
                return true;
            }
        }

        return false;
    }

    public static boolean getStructures(Level pLevel, BlockPos pPos, Predicate<BlockState> pPredicate, int totalCount) {
        int currentCount = 0;

        for (int i = -RANGE; i <= RANGE; ++i) {
            for (int j = -RANGE; j <= RANGE; ++j) {
                for (int k = -RANGE; k <= RANGE; ++k) {
                    BlockPos blockpos1 = pPos.offset(i, j, k);
                    BlockState blockstate = pLevel.getBlockState(blockpos1);
                    if (pPredicate.test(blockstate)){
                        ++currentCount;
                    }
                }
            }
        }

        return currentCount >= totalCount;
    }

    public boolean getBlocks(Predicate<BlockState> pPredicate, int totalCount){
        return getStructures(this.level, this.worldPosition, pPredicate, totalCount);
    }

    public boolean handleEventsImmediately() {
        return true;
    }

    public PositionSource getListenerSource() {
        return this.blockPosSource;
    }

    public int getListenerRadius() {
        return RANGE;
    }

    public boolean handleGameEvent(ServerLevel p_222777_, GameEvent.Message p_222778_) {
        if (!this.isRemoved()) {
            if (p_222778_.gameEvent().is(ModTags.GameEvents.BLOCK_EVENTS)) {
                this.updateVariant = 5;
                return true;
            }

        }
        return false;
    }

    public void readNetwork(CompoundTag tag) {
        super.readNetwork(tag);
        if (tag.contains("TrainTime")) {
            this.trainTime = tag.getInt("TrainTime");
        }
        if (tag.contains("TrainTimeTotal")) {
            this.trainTimeTotal = tag.getInt("TrainTimeTotal");
        }
        if (tag.contains("TrainAmount")) {
            this.trainAmount = tag.getInt("TrainAmount");
        }
        if (tag.contains("Item")) {
            this.itemStack = ItemStack.of(tag.getCompound("Item"));
        }
        if (tag.contains("EntityToSpawn")) {
            this.entityToSpawn = tag.getCompound("EntityToSpawn");
        }
        if (tag.contains("showArea")) {
            this.showArea = tag.getBoolean("showArea");
        }
        if (tag.contains("sensorSensitive")) {
            this.sensorSensitive = tag.getBoolean("sensorSensitive");
        }
    }

    public CompoundTag writeNetwork(CompoundTag tag) {
        CompoundTag tag1 = super.writeNetwork(tag);
        tag1.putInt("TrainTime", this.trainTime);
        tag1.putInt("TrainTimeTotal", this.trainTimeTotal);
        tag1.putInt("TrainAmount", this.trainAmount);
        tag1.put("Item", this.itemStack.save(new CompoundTag()));
        tag1.put("EntityToSpawn", this.entityToSpawn);
        tag1.putBoolean("showArea", this.showArea);
        tag1.putBoolean("sensorSensitive", this.sensorSensitive);
        return tag1;
    }

    public boolean isShowArea(){
        return this.showArea;
    }

    public void setShowArea(boolean showArea){
        this.showArea = showArea;
        this.markUpdated();
    }

    public boolean isSensorSensitive(){
        return this.sensorSensitive;
    }

    public void setSensorSensitive(boolean sensorSensitive){
        this.sensorSensitive = sensorSensitive;
        this.markUpdated();
    }

    public void markUpdated() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public int[] getSlotsForFace(Direction p_19238_) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int p_19235_, ItemStack pItemStack, @Nullable Direction p_19237_) {
        return this.level != null && !this.level.isClientSide && this.placeItem(pItemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int p_19239_, ItemStack p_19240_, Direction p_19241_) {
        return false;
    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int p_18941_) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int p_18942_, int p_18943_) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_18951_) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int p_18944_, ItemStack p_18945_) {
        this.placeItem(p_18945_);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return pPlayer.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clearContent() {
    }
}
