package com.Polarice3.Goety.client.audio;

import com.Polarice3.Goety.common.entities.hostile.Wight;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;

public class WightLoopSound extends AbstractTickableSoundInstance {
    protected final Wight entity;
    protected final float initVolume;

    public WightLoopSound(float volume, Wight entity) {
        super(ModSounds.WIGHT_LOOP.get(), entity.getSoundSource(), SoundInstance.createUnseededRandom());
        this.entity = entity;
        this.x = (double)((float)entity.getX());
        this.y = (double)((float)entity.getY());
        this.z = (double)((float)entity.getZ());
        this.looping = true;
        this.delay = 0;
        this.volume = volume;
        this.initVolume = volume;
    }

    public WightLoopSound(Wight entity) {
        this(6.0F, entity);
    }

    public void tick() {
        if (this.entity.isRemoved() || !this.entity.isAlive()){
            this.stop();
        }

        if (this.entity.isAggressive() || this.entity.isHiding()){
            this.volume = 0.0F;
        } else {
            this.volume = this.initVolume;
        }

        this.x = this.entity.getX();
        this.y = this.entity.getY();
        this.z = this.entity.getZ();
    }
}
