package com.Polarice3.Goety.client.audio;

import com.Polarice3.Goety.client.events.ClientEvents;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;

public class AggroBossLoopMusic extends AbstractTickableSoundInstance {
    protected final Mob mobEntity;
    protected SoundEvent bossMusic;

    public AggroBossLoopMusic(SoundEvent soundEvent, Mob mobEntity) {
        super(soundEvent, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.mobEntity = mobEntity;
        this.bossMusic = soundEvent;
        this.x = (double)((float)mobEntity.getX());
        this.y = (double)((float)mobEntity.getY());
        this.z = (double)((float)mobEntity.getZ());
        this.looping = true;
        this.delay = 0;
        this.volume = 1.0F;
    }

    public boolean canPlaySound() {
        return ClientEvents.BOSS_MUSIC == this && this.mobEntity != null && this.mobEntity.isAggressive();
    }

    public void tick() {
        if (!MainConfig.BossMusic.get()){
            this.stop();
            ClientEvents.BOSS_MUSIC = null;
        }
        if (this.mobEntity.isRemoved() || this.mobEntity.isDeadOrDying() || !this.mobEntity.isAlive() || !this.mobEntity.isAggressive()){
            if (this.mobEntity.isDeadOrDying()){
                if (this.mobEntity.level.isClientSide){
                    Minecraft minecraft = Minecraft.getInstance();
                    SoundManager soundHandler = minecraft.getSoundManager();
                    if (!this.isStopped()){
                        soundHandler.queueTickingSound(new PostBossMusic(ModSounds.BOSS_POST.get(), mobEntity));
                    }
                }
            }
            this.stop();
            ClientEvents.BOSS_MUSIC = null;
        }
    }
}
