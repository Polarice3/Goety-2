package com.Polarice3.Goety.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class LoopSoundPlayer {

    public static void playSound(Entity entity, SoundEvent soundEvent, float volume, float pitch) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getSoundManager().play(new LoopSound(soundEvent, volume, pitch, entity));
    }

    public static void playEffectSound(LivingEntity entity, SoundEvent soundEvent, MobEffect mobEffect, float volume, float pitch) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getSoundManager().play(new EffectLoopSound(soundEvent, volume, pitch, mobEffect, entity));
    }

    public static void playFollowSound(Entity entity, SoundEvent soundEvent, float volume, float pitch, boolean loop) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getSoundManager().play(new FollowSound(entity, soundEvent, volume, pitch, loop));
    }
}
