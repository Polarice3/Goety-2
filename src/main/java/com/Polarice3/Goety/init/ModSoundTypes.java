package com.Polarice3.Goety.init;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.util.ForgeSoundType;

public class ModSoundTypes {
    public static final SoundType URN = new ForgeSoundType(1.0F, 1.0F,
            ModSounds.URN_BREAK,
            () -> SoundEvents.GLASS_STEP,
            () -> SoundEvents.GLASS_PLACE,
            () -> SoundEvents.GLASS_HIT,
            () -> SoundEvents.GLASS_FALL);

    public static final SoundType MOD_METAL = new ForgeSoundType(1.0F, 1.0F,
            () -> SoundEvents.METAL_BREAK,
            () -> SoundEvents.SNOW_GOLEM_AMBIENT,
            () -> SoundEvents.METAL_PLACE,
            () -> SoundEvents.METAL_HIT,
            () -> SoundEvents.ZOMBIE_ATTACK_IRON_DOOR);

}
