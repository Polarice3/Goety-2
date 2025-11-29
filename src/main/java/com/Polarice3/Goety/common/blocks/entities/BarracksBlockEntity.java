package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.api.blocks.entities.IBarrack;
import com.Polarice3.Goety.api.entities.ITrainable;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class BarracksBlockEntity extends OwnedBlockEntity implements GameEventListener, IBarrack {
    public static String TRAIN_LIST = "trainList";
    public static String CLEAR = "Clear";
    public static String RAIN = "Rain";
    public static String STORM = "Storm";
    private final BlockPositionSource blockPosSource = new BlockPositionSource(this.worldPosition);
    public List<UUID> uuids = new ArrayList<>();
    public List<Mob> trainingMobs = new ArrayList<>();
    public String currentWeather = CLEAR;
    public String currentMob = "None";
    public int updateVariant;
    public int mobCountO = 0;
    public int mobCount = 0;
    public boolean showArea;
    public CompoundTag entityTrainTo = new CompoundTag();

    public BarracksBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, BarracksBlockEntity blockEntity) {
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, BarracksBlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel){
            if (serverLevel.isRaining()) {
                if (serverLevel.isThundering()) {
                    if (!Objects.equals(blockEntity.currentWeather, STORM)) {
                        blockEntity.currentWeather = STORM;
                        blockEntity.updateVariant = 5;
                    }
                } else {
                    if (!Objects.equals(blockEntity.currentWeather, RAIN)) {
                        blockEntity.currentWeather = RAIN;
                        blockEntity.updateVariant = 5;
                    }
                }
            } else {
                if (!Objects.equals(blockEntity.currentWeather, CLEAR)) {
                    blockEntity.currentWeather = CLEAR;
                    blockEntity.updateVariant = 5;
                }
            }
            if (blockEntity.updateVariant > 0){
                --blockEntity.updateVariant;
                blockEntity.setVariant(level, blockPos);
            }
            EntityType<? extends Mob> entityType = blockEntity.getTrainedMob(serverLevel, blockPos);
            if (entityType != null) {
                if (!Objects.equals(blockEntity.currentMob, entityType.getDescriptionId())){
                    blockEntity.setCurrentMob(entityType.getDescriptionId());
                }
                if (serverLevel.getGameTime() % 10 == 0) {
                    if (!blockEntity.uuids.isEmpty()) {
                        List<UUID> toRemove = new ArrayList<>();
                        for (UUID uuid : blockEntity.uuids) {
                            if (uuid != null) {
                                Entity entity = serverLevel.getEntity(uuid);
                                if (entity instanceof Mob mob && mob instanceof ITrainable) {
                                    if (!mob.isRemoved()) {
                                        if (!blockEntity.trainingMobs.contains(mob)) {
                                            blockEntity.trainingMobs.add(mob);
                                        }
                                    } else {
                                        toRemove.add(uuid);
                                    }
                                } else {
                                    toRemove.add(uuid);
                                }
                            }
                        }
                        if (!toRemove.isEmpty()){
                            blockEntity.uuids.removeAll(toRemove);
                        }
                    }
                    if (!blockEntity.trainingMobs.isEmpty()) {
                        blockEntity.trainingMobs.removeIf(mob -> {
                            if (!(mob instanceof ITrainable trainable)) {
                                blockEntity.uuids.remove(mob.getUUID());
                                return true;
                            } else if (!mob.isAlive() || mob.level.dimension() != serverLevel.dimension() || trainable.isTrained() || !blockEntity.checkEligibility(mob, serverLevel, blockPos)) {
                                blockEntity.uuids.remove(mob.getUUID());
                                return true;
                            }
                            return false;
                        });
                    }
                }
                blockEntity.trainMobs(level, blockPos);
            } else if (!Objects.equals(blockEntity.currentMob, "None")) {
                blockEntity.setCurrentMob("None");
            }

            blockEntity.mobCountO = blockEntity.amountTraining(level, blockPos);
            if (blockEntity.mobCountO != blockEntity.mobCount) {
                blockEntity.mobCount = blockEntity.mobCountO;
                blockEntity.markUpdated();
            }
        }

        if (blockState.hasProperty(BlockStateProperties.POWERED)){
            level.setBlock(blockPos, blockState.setValue(BlockStateProperties.POWERED, blockEntity.getTrainedMob(level, blockPos) != null), 3);
        }
    }

    public boolean autoMode(){
        return false;
    }

    public void setEntityType(EntityType<?> entityType) {
        if (entityType == null) {
            this.entityTrainTo = new CompoundTag();
        } else {
            ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
            this.entityTrainTo.putString("id", location != null ? location.toString() : "minecraft:pig");
        }
    }

    public void setEntityType(CompoundTag tag){
        if (tag == null) {
            this.entityTrainTo = new CompoundTag();
        } else {
            ResourceLocation resourcelocation = ResourceLocation.tryParse(tag.getString("id"));
            this.entityTrainTo.putString("id", resourcelocation != null ? resourcelocation.toString() : "minecraft:pig");
        }
    }

    public CompoundTag getEntityTrainTo() {
        return this.entityTrainTo;
    }

    public void setVariant(Level level, BlockPos blockPos){
    }

    @Override
    public void addTrainable(Mob mob) {
        if (mob != null) {
            if (this.uuids.size() < this.trainLimit() && !this.uuids.contains(mob.getUUID())) {
                this.uuids.add(mob.getUUID());
            }
        }
    }

    @Override
    public List<Mob> getTrainableList(Level level, BlockPos blockPos) {
        if (this.autoMode()) {
            return this.getMobsInRange(level, blockPos);
        }
        return this.trainingMobs;
    }

    @Override
    @Nullable
    public EntityType<? extends Mob> getTrainedMob(Level level, BlockPos blockPos) {
        if (EntityType.by(this.getEntityTrainTo()).isPresent()) {
            EntityType<?> entityType = EntityType.by(this.getEntityTrainTo()).get();
            Entity entity = entityType.create(level);
            if (entity instanceof Mob) {
                return (EntityType<? extends Mob>) entityType;
            }
        }
        return null;
    }

    @Override
    public int getCurrentAmount() {
        return this.mobCount;
    }

    public void readNetwork(CompoundTag tag) {
        super.readNetwork(tag);
        if (tag.contains("showArea")) {
            this.showArea = tag.getBoolean("showArea");
        }
        if (tag.contains("CurrentWeather")) {
            this.currentWeather = tag.getString("CurrentWeather");
        }
        if (tag.contains("CurrentMob")) {
            this.currentMob = tag.getString("CurrentMob");
        }
        if (tag.contains("MobCount")) {
            this.mobCount = tag.getInt("MobCount");
        }
        if (tag.contains("EntityTrainTo")) {
            this.entityTrainTo = tag.getCompound("EntityTrainTo");
        }
        if (tag.contains(TRAIN_LIST)){
            ListTag list = tag.getList(TRAIN_LIST, 8);
            for(int i = 0; i < list.size(); ++i) {
                this.uuids.add(UUID.fromString(list.getString(i)));
            }
        }
    }

    public CompoundTag writeNetwork(CompoundTag tag) {
        CompoundTag tag1 = super.writeNetwork(tag);
        tag1.putBoolean("showArea", this.showArea);
        tag1.putString("CurrentWeather", this.currentWeather);
        tag1.putString("CurrentMob", this.currentMob);
        tag1.putInt("MobCount", this.getCurrentAmount());
        tag1.put("EntityTrainTo", this.entityTrainTo);
        List<String> list = new ArrayList<>();
        if (tag.contains(TRAIN_LIST)) {
            for (int i = 0; i < tag.getList(TRAIN_LIST, 8).size(); ++i) {
                list.add(tag.getList(TRAIN_LIST, 8).getString(i));
            }
        }
        if (!this.uuids.isEmpty()) {
            for (UUID uuid : this.uuids) {
                if (!list.contains(uuid.toString())) {
                    ListTag nbttaglist = new ListTag();
                    if (tag.contains(TRAIN_LIST)) {
                        nbttaglist = tag.getList(TRAIN_LIST, 8);
                    }

                    nbttaglist.add(StringTag.valueOf(uuid.toString()));
                    tag.put(TRAIN_LIST, nbttaglist);
                }
            }
        }
        return tag1;
    }

    public PositionSource getListenerSource() {
        return this.blockPosSource;
    }

    public GameEventListener.DeliveryMode getDeliveryMode() {
        return GameEventListener.DeliveryMode.BY_DISTANCE;
    }

    public int getListenerRadius() {
        return this.getRange();
    }

    public boolean handleGameEvent(ServerLevel p_222777_, GameEvent p_282184_, GameEvent.Context p_283014_, Vec3 p_282350_) {
        if (!this.isRemoved()) {
            if (p_282184_.is(ModTags.GameEvents.BLOCK_EVENTS)) {
                this.updateVariant = 5;
                return true;
            }

        }
        return false;
    }

    public boolean isShowArea(){
        return this.showArea;
    }

    public void setShowArea(boolean showArea){
        this.showArea = showArea;
        this.markUpdated();
    }

    public String getCurrentMob(){
        return this.currentMob;
    }

    public void setCurrentMob(String string){
        this.currentMob = string;
        this.markUpdated();
    }

    public void markUpdated() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }
}
