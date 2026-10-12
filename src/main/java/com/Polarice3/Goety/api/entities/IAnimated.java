package com.Polarice3.Goety.api.entities;

import net.minecraft.world.entity.AnimationState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface IAnimated {

    default void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    default void setAnimationState(int id) {
    }

    default int getAnimationState(String animation) {
        return 0;
    }

    default List<AnimationState> getAllAnimations(){
        return new ArrayList<>();
    }

    default void stopMostAnimation(AnimationState... exception){
        for (AnimationState state : this.getAllAnimations()){
            if (Arrays.stream(exception).noneMatch(animationState -> animationState == state)){
                state.stop();
            }
        }
    }

    default void stopMostAnimation(AnimationState exception){
        for (AnimationState state : this.getAllAnimations()){
            if (state != exception){
                state.stop();
            }
        }
    }

    default int getCurrentAnimation(){
        return 0;
    }

    default boolean isCurrentAnimation(String animation) {
        return this.getCurrentAnimation() == this.getAnimationState(animation);
    }
}
