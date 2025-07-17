package com.Polarice3.Goety.client.audio;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public class EffectLoopSound extends AbstractTickableSoundInstance {
    protected final LivingEntity entity;
    protected final MobEffect effect;

    public EffectLoopSound(SoundEvent soundEvent, float volume, float pitch, MobEffect mobEffect, LivingEntity entity) {
        super(soundEvent, entity.getSoundSource(), SoundInstance.createUnseededRandom());
        this.effect = mobEffect;
        this.entity = entity;
        this.x = (double)((float)entity.getX());
        this.y = (double)((float)entity.getY());
        this.z = (double)((float)entity.getZ());
        this.looping = true;
        this.delay = 0;
        this.volume = volume;
        this.pitch = pitch;
    }

    public EffectLoopSound(SoundEvent soundEvent, MobEffect mobEffect, LivingEntity entity) {
        this(soundEvent, 1.0F, 1.0F, mobEffect, entity);
    }

    public void tick() {
        if (this.entity == null || this.entity.isRemoved() || !this.entity.isAlive() || this.effect == null || !this.entity.hasEffect(this.effect)){
            this.stop();
        } else {
            this.x = this.entity.getX();
            this.y = this.entity.getY();
            this.z = this.entity.getZ();
        }
    }
}
