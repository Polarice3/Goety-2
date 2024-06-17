package com.Polarice3.Goety.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;

public class LoopSoundPlayer {

    public static void playSound(Entity entity, SoundEvent soundEvent, float volume) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getSoundManager().play(new LoopSound(soundEvent, volume, entity));
    }
}
