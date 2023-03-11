package com.Polarice3.Goety.client.audio;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;

public class PostBossMusic extends AbstractTickableSoundInstance {
    protected final Mob mobEntity;

    public PostBossMusic(SoundEvent soundEvent, Mob mobEntity) {
        super(soundEvent, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.mobEntity = mobEntity;
        this.x = (double)((float)mobEntity.getX());
        this.y = (double)((float)mobEntity.getY());
        this.z = (double)((float)mobEntity.getZ());
        this.looping = false;
        this.delay = 0;
        this.volume = 1.0F;
    }

    @Override
    public void tick() {
    }
}
