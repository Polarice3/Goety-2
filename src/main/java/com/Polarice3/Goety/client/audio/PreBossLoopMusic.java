package com.Polarice3.Goety.client.audio;

import com.Polarice3.Goety.Goety;
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
import net.minecraft.world.entity.player.Player;

public class PreBossLoopMusic extends AbstractTickableSoundInstance {
    protected final Mob mobEntity;
    private final float trueVolume;
    protected SoundEvent preMusic;
    protected SoundEvent postBossMusic;
    protected int withinRange;

    public PreBossLoopMusic(SoundEvent soundEvent, Mob mobEntity, float volume, float pitch, int withinRange) {
        this(soundEvent, ModSounds.BOSS_POST.get(), mobEntity, volume, pitch, withinRange);
    }

    public PreBossLoopMusic(SoundEvent soundEvent, SoundEvent postBossMusic, Mob mobEntity, float volume, float pitch, int withinRange) {
        super(soundEvent, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.mobEntity = mobEntity;
        this.postBossMusic = postBossMusic;
        this.preMusic = soundEvent;
        this.x = (double)((float)mobEntity.getX());
        this.y = (double)((float)mobEntity.getY());
        this.z = (double)((float)mobEntity.getZ());
        this.looping = true;
        this.delay = 0;
        this.volume = volume;
        this.trueVolume = volume;
        this.pitch = pitch;
        this.withinRange = withinRange;
    }

    @Override
    public boolean canPlaySound() {
        if (this.withinRange > 0) {
            Player player = Goety.PROXY.getPlayer();
            if (player != null) {
                float range = 1.0F - (player.distanceTo(this.mobEntity) / (this.withinRange + 8));
                if (range <= 0.0F) {
                    if (ClientEvents.PRE_BOSS_MUSIC == this) {
                        ClientEvents.PRE_BOSS_MUSIC = null;
                    }
                    return false;
                }
            }
        }
        if (ClientEvents.PRE_BOSS_MUSIC == this) {
            if (!(ClientEvents.BOSS_MUSIC == null || ClientEvents.BOSS_MUSIC.isStopped())) {
                ClientEvents.PRE_BOSS_MUSIC = null;
            }
        }
        return ClientEvents.PRE_BOSS_MUSIC == this && (ClientEvents.BOSS_MUSIC == null || ClientEvents.BOSS_MUSIC.isStopped());
    }

    public void tick() {
        if (!MainConfig.BossMusic.get()){
            ClientEvents.PRE_BOSS_MUSIC = null;
            this.stop();
        }

        if (this.mobEntity.isAggressive() || this.mobEntity.isRemoved() || this.mobEntity.isDeadOrDying() || !this.mobEntity.isAlive()){
            if (this.mobEntity.isDeadOrDying()){
                if (this.mobEntity.level.isClientSide){
                    Minecraft minecraft = Minecraft.getInstance();
                    SoundManager soundHandler = minecraft.getSoundManager();
                    if (!this.isStopped()){
                        soundHandler.queueTickingSound(new PostBossMusic(this.postBossMusic, mobEntity, this.trueVolume, this.pitch));
                    }
                }
            }
            ClientEvents.PRE_BOSS_MUSIC = null;
            this.stop();
        }

        if (this.withinRange > 0) {
            Player player = Goety.PROXY.getPlayer();
            if (player != null) {
                this.volume = 1.0F - (player.distanceTo(this.mobEntity) / (this.withinRange + 8));
            }
        } else {
            this.volume = 1.0F;
        }

        this.x = this.mobEntity.getX();
        this.y = this.mobEntity.getY();
        this.z = this.mobEntity.getZ();
    }
}
