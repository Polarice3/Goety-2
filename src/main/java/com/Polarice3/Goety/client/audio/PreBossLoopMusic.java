package com.Polarice3.Goety.client.audio;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.events.ClientEvents;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
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
    protected boolean mustSee;
    protected boolean canSee;

    public PreBossLoopMusic(SoundEvent soundEvent, Mob mobEntity, float volume, float pitch, int withinRange) {
        this(soundEvent, ModSounds.BOSS_POST.get(), mobEntity, volume, pitch, withinRange);
    }

    public PreBossLoopMusic(SoundEvent soundEvent, SoundEvent postBossMusic, Mob mobEntity, float volume, float pitch, int withinRange) {
        this(soundEvent, postBossMusic, mobEntity, volume, pitch, withinRange, false);
    }

    public PreBossLoopMusic(SoundEvent soundEvent, SoundEvent postBossMusic, Mob mobEntity, float volume, float pitch, int withinRange, boolean mustSee) {
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
        this.mustSee = mustSee;
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

    public static boolean canPlayFor(Mob mob) {
        return mob.isAlive() && !mob.isRemoved() && !mob.isDeadOrDying() && !mob.isAggressive();
    }

    public boolean isStale() {
        Minecraft minecraft = Minecraft.getInstance();
        return this.isStopped() || minecraft.level == null || this.mobEntity.level() != minecraft.level;
    }

    private void clear() {
        if (ClientEvents.PRE_BOSS_MUSIC == this) {
            ClientEvents.PRE_BOSS_MUSIC = null;
        }
        this.stop();
    }

    public void tick() {
        if (!MainConfig.BossMusic.get() || this.isStale()){
            this.clear();
            return;
        }

        if (!canPlayFor(this.mobEntity)){
            boolean died = this.mobEntity.isDeadOrDying() && !this.isStopped();
            this.clear();
            if (died) {
                ClientEvents.queuePostBossMusic(new PostBossMusic(this.postBossMusic, this.mobEntity, this.trueVolume, this.pitch));
            }
            return;
        }

        if (this.mustSee) {
            Player player = Goety.PROXY.getPlayer();
            if (player != null) {
                this.canSee = MobUtil.hasVisualLineOfSight(player, this.mobEntity);
            } else {
                this.canSee = false;
            }
        } else {
            this.canSee = true;
        }

        if (this.withinRange > 0) {
            Player player = Goety.PROXY.getPlayer();
            if (player != null) {
                if (this.canSee) {
                    this.volume = 1.0F - (player.distanceTo(this.mobEntity) / (this.withinRange + 8));
                } else {
                    this.volume -= 0.025F;
                }
            }
        } else {
            if (this.canSee) {
                this.volume = 1.0F;
            } else {
                this.volume -= 0.025F;
            }
        }

        this.x = this.mobEntity.getX();
        this.y = this.mobEntity.getY();
        this.z = this.mobEntity.getZ();

        if (this.volume <= 0.0F && !this.canSee) {
            this.clear();
        }
    }
}
