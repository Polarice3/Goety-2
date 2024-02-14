package com.Polarice3.Goety.client.audio;

import com.Polarice3.Goety.common.entities.ally.SquallGolem;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class SquallAlertSound extends AbstractTickableSoundInstance {
    protected final SquallGolem squallGolem;

    public SquallAlertSound(SquallGolem squallGolem) {
        super(ModSounds.SQUALL_GOLEM_ALERT.get(), SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.squallGolem = squallGolem;
        this.x = (double)((float)squallGolem.getX());
        this.y = (double)((float)squallGolem.getY());
        this.z = (double)((float)squallGolem.getZ());
        this.delay = 0;
        this.volume = 1.0F;
    }

    @Override
    public void tick() {
        if (this.squallGolem.isRemoved() || !this.squallGolem.isAlive()
        || !this.squallGolem.isNovelty){
            this.stop();
        }
    }
}
