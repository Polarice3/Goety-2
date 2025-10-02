package com.Polarice3.Goety.client.audio;

import com.Polarice3.Goety.common.entities.boss.EnderKeeper;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;

public class FollowSound extends AbstractTickableSoundInstance {
    private final Entity entity;
    private int timePlayed;

    public FollowSound(Entity entity, SoundEvent sound, float volume, float pitch, boolean loop) {
        super(sound, entity.getSoundSource(), SoundInstance.createUnseededRandom());
        this.entity = entity;
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.volume = volume;
        this.pitch = pitch;
        this.looping = loop;
    }

    @Override
    public void tick() {
        if (this.entity.isAlive()
                && !this.entity.isRemoved()
                && !this.entity.isSilent()
                && this.additionalPlayConditions()) {
            if (this.additionalPositionConditions()) {
                this.x = this.entity.getX();
                this.y = this.entity.getY();
                this.z = this.entity.getZ();
            }

        } else {
            this.stop();
        }

        this.timePlayed++;
    }

    private boolean additionalPlayConditions() {
        if (this.entity instanceof EnderKeeper enderKeeper) {
            return enderKeeper.isCurrentAnimation(EnderKeeper.CHARGE);
        }
        return true;
    }

    private boolean additionalPositionConditions() {
        return true;
    }
}
