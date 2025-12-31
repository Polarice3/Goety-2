package com.Polarice3.Goety.client.audio;

import com.Polarice3.Goety.client.events.ClientEvents;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.ControlledAnimation;
import com.Polarice3.Goety.utils.MiscCapHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;

public class AggroBossLoopMusic extends AbstractTickableSoundInstance {
    protected final Mob mobEntity;
    private final float trueVolume;
    private int ticksExisted = 0;
    private int timeUntilFade;
    ControlledAnimation volumeControl;
    protected SoundEvent postBossMusic;

    public AggroBossLoopMusic(SoundEvent soundEvent, Mob mobEntity) {
        this(soundEvent, mobEntity, 1.0F, 1.0F);
    }

    public AggroBossLoopMusic(SoundEvent soundEvent, Mob mobEntity, float volume, float pitch) {
        this(soundEvent, ModSounds.BOSS_POST.get(), mobEntity, volume, pitch);
    }

    public AggroBossLoopMusic(SoundEvent soundEvent, SoundEvent postBossMusic, Mob mobEntity, float volume, float pitch) {
        super(soundEvent, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.mobEntity = mobEntity;
        this.postBossMusic = postBossMusic;
        this.x = mobEntity.getX();
        this.y = mobEntity.getY();
        this.z = mobEntity.getZ();
        this.looping = true;
        this.delay = 0;
        this.volumeControl = new ControlledAnimation(40);
        this.volumeControl.setTimer(20);
        this.volume = this.volumeControl.getAnimationFraction();
        this.trueVolume = volume;
        this.pitch = pitch;
        this.timeUntilFade = 80;
    }

    public boolean canPlaySound() {
        return ClientEvents.BOSS_MUSIC == this && this.mobEntity.isAggressive();
    }

    public void tick() {
        if (!MainConfig.BossMusic.get()){
            ClientEvents.BOSS_MUSIC = null;
            this.stop();
        }
        boolean target = MiscCapHelper.getMobTarget(this.mobEntity) instanceof Player || (MiscCapHelper.getMobTarget(this.mobEntity) instanceof OwnableEntity ownable && ownable.getOwner() instanceof Player) || this.mobEntity.getType().is(ModTags.EntityTypes.GLOBAL_MUSIC_BOSS);
        if (!target || this.mobEntity.isRemoved()
                || this.mobEntity.isDeadOrDying()
                || !this.mobEntity.isAlive()){
            if (this.mobEntity.isDeadOrDying()){
                this.timeUntilFade = 0;
                if (this.mobEntity.level.isClientSide){
                    Minecraft minecraft = Minecraft.getInstance();
                    SoundManager soundHandler = minecraft.getSoundManager();
                    if (!this.isStopped()){
                        soundHandler.queueTickingSound(new PostBossMusic(this.postBossMusic, mobEntity, this.trueVolume, this.pitch));
                    }
                }
            }
            if (this.timeUntilFade > 0) {
                this.timeUntilFade--;
            } else {
                this.volumeControl.decreaseTimer();
            }
        } else {
            this.volumeControl.increaseTimer();
            this.timeUntilFade = 60;
        }

        this.x = this.mobEntity.getX();
        this.y = this.mobEntity.getY();
        this.z = this.mobEntity.getZ();

        this.volume = this.volumeControl.getAnimationFraction() / this.trueVolume;

        if (this.volumeControl.getAnimationFraction() < 0.025) {
            ClientEvents.BOSS_MUSIC = null;
            this.stop();
        }

        if (this.ticksExisted % 100 == 0) {
            Minecraft.getInstance().getMusicManager().stopPlaying();
        }
        this.ticksExisted++;
    }
}
