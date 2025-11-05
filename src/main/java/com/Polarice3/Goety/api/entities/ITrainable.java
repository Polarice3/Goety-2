package com.Polarice3.Goety.api.entities;

import com.Polarice3.Goety.api.blocks.entities.IBarrack;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.blocks.entities.OwnedBlockEntity;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public interface ITrainable {

    default int getTotalTrainTime(){
        return MathHelper.minutesToTicks(20);
    }

    default int getTrainTime(){
        return 0;
    }

    default void setTrainTime(int time){
    }

    default int getTrainCheck(){
        return 0;
    }

    default void setTrainCheck(int check){
    }

    default boolean canTrain(Level level, BlockPos blockPos, EntityType<? extends Mob> entityType){
        return true;
    }

    default boolean isTraining(){
        return this.getTrainPos().isPresent();
    }

    default Optional<BlockPos> getTrainPos(){
        return Optional.empty();
    }

    default Optional<BlockPos> getStoredTrainPos(){
        return Optional.empty();
    }

    default Vec3 vec3TrainPos(){
        if (this.getTrainPos().isPresent()) {
            return Vec3.atBottomCenterOf(this.getTrainPos().get());
        } else {
            return null;
        }
    }

    default void setTrainPos(@Nullable BlockPos blockPos){
    }

    default void setStoredTrainPos(@Nullable BlockPos blockPos){
    }

    default String getCurrentTrain() {
        return "None";
    }

    default void setCurrentTrain(String train) {
    }

    default void trainTick(){
        if (this instanceof LivingEntity livingEntity) {
            if (livingEntity.isAlive()) {
                if (livingEntity.tickCount >= 100) {
                    if (this.getTrainCheck() > 0) {
                        if (livingEntity.tickCount % 2 == 0) {
                            this.setTrainCheck(this.getTrainCheck() - 1);
                        }
                    } else if (this.getTrainTime() > 0) {
                        if (livingEntity.tickCount % 2 == 0) {
                            this.setTrainTime(this.getTrainTime() - 1);
                        }
                        if (this.isTraining()) {
                            this.setTrainPos(null);
                        }
                    }
                }
            }
            if (livingEntity instanceof Mob mob) {
                if (this.isTraining() && this.getTrainPos().isPresent()) {
                    int range = 8;
                    if (mob.level.getBlockEntity(this.getTrainPos().get()) instanceof IBarrack barrack){
                        range = barrack.getRange();
                    }
                    try {
                        if (this.vec3TrainPos() != null) {
                            if (mob.distanceToSqr(this.vec3TrainPos()) > Mth.square(range + 2)) {
                                if (mob.level instanceof ServerLevel serverLevel) {
                                    ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ParticleTypes.ANGRY_VILLAGER, mob);
                                }
                                this.setTrainPos(null);
                            }
                            if (mob instanceof IServant owned) {
                                if (!owned.isFollowing() && !owned.isStaying()) {
                                    if (!owned.isCommanded() && mob.distanceToSqr(this.vec3TrainPos()) >= Mth.square(range)) {
                                        mob.setTarget(null);
                                        BlockPos blockPos = this.getTrainPos().get();
                                        mob.getNavigation().moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0F);
                                    }
                                }
                            }
                        }
                    } catch (NullPointerException ignored) {
                    }
                }
                if (this.getStoredTrainPos().isPresent()) {
                    if (mob.level.getBlockEntity(this.getStoredTrainPos().get()) instanceof IBarrack barrack) {
                        if (barrack instanceof OwnedBlockEntity ownedBlock && mob instanceof IServant owned) {
                            if (ownedBlock.getTrueOwner() != owned.getTrueOwner()) {
                                this.setStoredTrainPos(null);
                            }
                        }
                        if (this.getStoredTrainPos().isPresent()) {
                            if (!barrack.getTrainableList(mob.level, this.getStoredTrainPos().get()).contains(this)) {
                                if (barrack.checkEligibility(mob, mob.level, this.getStoredTrainPos().get())) {
                                    barrack.addTrainable(mob, mob.level, this.getStoredTrainPos().get());
                                }
                            } else {
                                if (mob.distanceToSqr(Vec3.atBottomCenterOf(this.getStoredTrainPos().get())) > Mth.square(barrack.getRange() + 2)){
                                    this.setStoredTrainPos(null);
                                }
                            }
                        }
                    } else {
                        this.setStoredTrainPos(null);
                    }
                }
            }
        }
    }

    default int trainSpeed(EntityType<? extends Mob> entityType) {
        return 1;
    }

    default void train(EntityType<? extends Mob> entityType){
        if (Objects.equals(this.getCurrentTrain(), entityType.getDescriptionId())) {
            if (this.getTrainTime() < this.getTotalTrainTime()) {
                this.setTrainTime(this.getTrainTime() + this.trainSpeed(entityType));
            } else {
                this.completeTraining(entityType);
            }
        } else {
            this.setTrainTime(0);
            this.setCurrentTrain(entityType.getDescriptionId());
        }
        this.setTrainCheck(10);
    }

    default boolean isTrained(){
        return false;
    }

    default void completeTraining(EntityType<? extends Mob> entityType){
        if (this instanceof Mob mob){
            if (ForgeEventFactory.canLivingConvert(mob, entityType, (timer) -> {})) {
                Mob converted = mob.convertTo(entityType, true);
                if (converted != null){
                    if (mob instanceof IOwned ownable && converted instanceof IOwned ownable1){
                        ownable1.copyTrueOwner(ownable);
                        if (ownable.getTrueOwner() instanceof Player player) {
                            if (SEHelper.isGrounded(player, mob)){
                                SEHelper.removeGroundedEntity(player, mob);
                                SEHelper.addGroundedEntity(player, converted);
                            }
                        }
                    }
                    if (mob.level instanceof ServerLevel serverLevel) {
                        ForgeEventFactory.onFinalizeSpawn(converted, serverLevel, serverLevel.getCurrentDifficultyAt(converted.blockPosition()), MobSpawnType.CONVERSION, null, null);
                    }
                    if (mob instanceof IServant servant && converted instanceof IServant servant1){
                        if (servant.isWandering() && !servant.isStaying() && !servant.isGuardingArea() && servant1.canWander()) {
                            servant1.setBoundPos(null);
                            servant1.setWandering(true);
                            servant1.setStaying(false);
                        } else if (servant.isStaying() && !servant.isGuardingArea() && servant1.canStay()) {
                            servant1.setBoundPos(null);
                            servant1.setWandering(false);
                            servant1.setStaying(true);
                        } else if (servant.isGuardingArea() && servant1.canGuardArea()){
                            servant1.setBoundPos(servant.getBoundPos());
                            servant1.setWandering(false);
                            servant1.setStaying(false);
                        } else {
                            servant1.setBoundPos(null);
                            servant1.setWandering(false);
                            servant1.setStaying(false);
                        }
                    }
                    converted.playAmbientSound();
                    ForgeEventFactory.onLivingConvert(mob, converted);
                }
            }
        }
    }

    default void readTrainableData(CompoundTag compound){
        if (compound.contains("TrainTime")){
            this.setTrainTime(compound.getInt("TrainTime"));
        }
        if (compound.contains("TrainPos")){
            this.setTrainPos(NbtUtils.readBlockPos(compound.getCompound("TrainPos")));
        }
        if (compound.contains("StoredTrainPos")) {
            this.setStoredTrainPos(NbtUtils.readBlockPos(compound.getCompound("StoredTrainPos")));
        }
        if (compound.contains("CurrentTrain")){
            this.setCurrentTrain(compound.getString("CurrentTrain"));
        }
    }

    default void saveTrainableData(CompoundTag compound){
        compound.putInt("TrainTime", this.getTrainTime());
        if (this.getTrainPos().isPresent()) {
            compound.put("TrainPos", NbtUtils.writeBlockPos(this.getTrainPos().get()));
        }
        if (this.getStoredTrainPos().isPresent()) {
            compound.put("StoredTrainPos", NbtUtils.writeBlockPos(this.getStoredTrainPos().get()));
        }
        compound.putString("CurrentTrain", this.getCurrentTrain());
    }
}
