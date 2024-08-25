package com.Polarice3.Goety.client.audio;

import com.Polarice3.Goety.client.events.ClientEvents;
import com.Polarice3.Goety.config.MainConfig;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;

public class PreBossLoopMusic extends AbstractTickableSoundInstance {
    protected final Mob mobEntity;
    protected SoundEvent preMusic;

    public PreBossLoopMusic(SoundEvent soundEvent, Mob mobEntity) {
        super(soundEvent, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.mobEntity = mobEntity;
        this.preMusic = soundEvent;
        this.x = (double)((float)mobEntity.getX());
        this.y = (double)((float)mobEntity.getY());
        this.z = (double)((float)mobEntity.getZ());
        this.looping = true;
        this.delay = 0;
        this.volume = 1.0F;
    }

    public boolean canPlaySound() {
        return ClientEvents.BOSS_MUSIC == this && this.mobEntity != null && !this.mobEntity.isAggressive();
    }

    public void tick() {
        if (!MainConfig.BossMusic.get()){
            ClientEvents.BOSS_MUSIC = null;
            this.stop();
        }
        if (this.mobEntity.isRemoved() || this.mobEntity.isDeadOrDying() || !this.mobEntity.isAlive() || this.mobEntity.isAggressive()){
            ClientEvents.BOSS_MUSIC = null;
            this.stop();
        }
    }
}
