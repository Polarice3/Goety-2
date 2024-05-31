package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.world.entity.LivingEntity;

public class SoundUtil {

    public static void playSoulBolt(LivingEntity livingEntity){
        if (!livingEntity.isSilent()) {
            livingEntity.level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), ModSounds.SOUL_BOLT_CAST.get(), livingEntity.getSoundSource(), 1.0F, 1.0F);
            livingEntity.level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), ModSounds.VEX_VAPOR.get(), livingEntity.getSoundSource(), 1.0F, 1.0F);
            livingEntity.level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), ModSounds.CAST_SPELL_TWO.get(), livingEntity.getSoundSource(), 1.0F, 1.0F);
        }
    }

    public static void playNecroBolt(LivingEntity livingEntity){
        if (!livingEntity.isSilent()) {
            livingEntity.level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), ModSounds.SWING.get(), livingEntity.getSoundSource(), 0.75F, 0.6F);
            livingEntity.level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), ModSounds.LICH_TELEPORT_OUT.get(), livingEntity.getSoundSource(), 1.0F, 1.0F);
            livingEntity.level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), ModSounds.PROJECTILE_SPELL.get(), livingEntity.getSoundSource(), 1.0F, 0.8F);
        }
    }

    public static void playNecroBoltIllusion(LivingEntity livingEntity){
        if (!livingEntity.isSilent()) {
            livingEntity.level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), ModSounds.SWING.get(), livingEntity.getSoundSource(), 0.75F, 0.6F);
            livingEntity.level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), ModSounds.LICH_TELEPORT_OUT.get(), livingEntity.getSoundSource(), 1.0F, 1.0F);
            livingEntity.level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), ModSounds.CAST_SPELL.get(), livingEntity.getSoundSource(), 1.0F, 0.8F);
        }
    }

    public static void playNecromancerSummon(LivingEntity livingEntity){
        if (!livingEntity.isSilent()) {
            livingEntity.level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), ModSounds.NECROMANCER_SUMMON.get(), livingEntity.getSoundSource(), 1.4F, 0.7F);
        }
    }
}
