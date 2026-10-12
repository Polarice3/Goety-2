package com.Polarice3.Goety.client.audio;

import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MiscCapHelper;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.entity.LivingEntity;

public class EnchantBeamLoopSound extends AbstractTickableSoundInstance {
    protected final LivingEntity entity;
    protected final float initVolume;

    public EnchantBeamLoopSound(float volume, LivingEntity entity) {
        super(ModSounds.ENCHANTER_BEAM_LOOP.get(), entity.getSoundSource(), SoundInstance.createUnseededRandom());
        this.entity = entity;
        this.x = (double)((float)entity.getX());
        this.y = (double)((float)entity.getY());
        this.z = (double)((float)entity.getZ());
        this.looping = true;
        this.delay = 0;
        this.volume = volume;
        this.initVolume = volume;
    }

    public EnchantBeamLoopSound(LivingEntity entity) {
        this(0.25F, entity);
    }

    public void tick() {
        if (this.entity.isRemoved() || !this.entity.isAlive()){
            this.stop();
        }

        if (!MiscCapHelper.isEnchanter(this.entity)){
            this.volume = 0.0F;
        } else {
            this.volume = this.initVolume;
        }

        this.x = this.entity.getX();
        this.y = this.entity.getY();
        this.z = this.entity.getZ();
    }
}
