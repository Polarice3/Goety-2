package com.Polarice3.Goety.client.audio;

import com.Polarice3.Goety.common.entities.ally.Leapleaf;
import com.Polarice3.Goety.common.entities.ally.golem.SquallGolem;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

public class SummonNoveltySound extends AbstractTickableSoundInstance {
    protected final LivingEntity entity;

    public SummonNoveltySound(LivingEntity summon, SoundEvent soundEvent) {
        super(soundEvent, SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.entity = summon;
        this.x = (double)((float)summon.getX());
        this.y = (double)((float)summon.getY());
        this.z = (double)((float)summon.getZ());
        this.delay = 0;
        this.volume = 1.0F;
    }

    @Override
    public void tick() {
        if (this.entity.isRemoved() || !this.entity.isAlive()){
            if (this.entity instanceof SquallGolem squallGolem){
                if (!squallGolem.isNovelty){
                    this.stop();
                }
            }
            if (this.entity instanceof Leapleaf leapleaf){
                if (!leapleaf.isNovelty){
                    this.stop();
                }
            }
            this.stop();
        }
    }
}
