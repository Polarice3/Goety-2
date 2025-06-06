package com.Polarice3.Goety.api.blocks.entities;

import com.Polarice3.Goety.api.entities.ITrainable;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface IBarrack {

    @Nullable
    EntityType<? extends Mob> getTrainedMob(Level level, BlockPos blockPos);

    default String getCurrentMob(){
        return "None";
    }

    default int getRange(){
        return 8;
    }

    default int trainLimit() {
        return 8;
    }

    default Predicate<Mob> trainingRequirements(Level level, BlockPos blockPos){
        return mob -> false;
    }

    default List<Mob> getMobsInRange(Level level, BlockPos blockPos){
        List<Mob> list = level.getEntitiesOfClass(Mob.class,
                new AABB(blockPos).inflate(this.getRange()),
                this.trainingRequirements(level, blockPos)
                        .and(mob -> mob instanceof ITrainable trainable
                                && this.getTrainedMob(level, blockPos) != null
                                && trainable.canTrain(level, blockPos, this.getTrainedMob(level, blockPos))));
        List<Mob> mobList = new ArrayList<>(this.trainLimit());
        for (Mob mob : list) {
            if (mobList.size() < this.trainLimit()) {
                mobList.add(mob);
            }
        }
        return mobList;
    }

    default boolean checkEligibility(Mob mob, Level level, BlockPos blockPos){
        return this.trainingRequirements(level, blockPos)
                .and(mob1 -> mob1 instanceof ITrainable trainable
                        && this.getTrainedMob(level, blockPos) != null
                        && mob1.distanceToSqr(Vec3.atBottomCenterOf(blockPos)) <= Mth.square(this.getRange() + 2)
                        && trainable.canTrain(level, blockPos, this.getTrainedMob(level, blockPos))).test(mob);
    }

    default List<Mob> getTrainableList(Level level, BlockPos blockPos) {
        return this.getMobsInRange(level, blockPos);
    }

    default void addTrainable(Mob mob, Level level, BlockPos blockPos){
        if (this.checkEligibility(mob, level, blockPos)){
            this.addTrainable(mob);
        }
    }

    default void addTrainable(Mob mob) {
    }

    default int getCurrentAmount() {
        return 0;
    }

    default int amountTraining(Level level, BlockPos blockPos) {
        return this.getTrainableList(level, blockPos).size();
    }

    default boolean capacityAvailable(Level level, BlockPos blockPos) {
        return this.amountTraining(level, blockPos) <= this.trainLimit();
    }

    default void trainMobs(Level level, BlockPos blockPos){
        if (!this.getTrainableList(level, blockPos).isEmpty()) {
            for (Mob mob : this.getTrainableList(level, blockPos)) {
                this.trainMob(mob, level, blockPos);
            }
        }
    }

    default void trainMob(Mob trainMob, Level level, BlockPos blockPos){
        EntityType<? extends Mob> trainedMob = this.getTrainedMob(level, blockPos);
        if (trainedMob != null) {
            if (trainMob instanceof ITrainable trainable) {
                if (trainable.canTrain(level, blockPos, trainedMob)) {
                    if (trainable.getTrainPos().isEmpty() && this.capacityAvailable(level, blockPos)){
                        trainable.setTrainPos(blockPos);
                    } else if (trainable.getTrainPos().isPresent() && BlockFinder.samePos(trainable.getTrainPos().get(), blockPos)) {
                        trainable.train(trainedMob);
                    }
                }
            }
        }
    }

}
