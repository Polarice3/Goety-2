package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
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

    public static void playNecromancerSummon(Entity entity){
        if (!entity.isSilent()) {
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.NECROMANCER_SUMMON.get(), entity.getSoundSource(), 1.4F, 0.7F);
        }
    }

    public static void playWraithAttack(LivingEntity livingEntity){
        if (!livingEntity.isSilent()){
            float volume = 2.0F;
            livingEntity.level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), ModSounds.WRAITH_ATTACK.get(), livingEntity.getSoundSource(), volume, 1.0F);
            livingEntity.level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), ModSounds.WRAITH_FIRE.get(), livingEntity.getSoundSource(), volume, 1.7F);
            livingEntity.level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), ModSounds.WRAITH_FLY.get(), livingEntity.getSoundSource(), 0.4F * volume, 1.094967F);
        }
    }

    public static void playRedstoneMineLoad(Entity entity){
        if (!entity.isSilent()) {
            float random1 = Mth.clamp(1.0F + entity.level.getRandom().nextFloat(), 1.2F, 1.5F);
            float random2 = Mth.clamp(1.0F + entity.level.getRandom().nextFloat(), 1.3F, 1.6F);
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.BOMB_LOAD.get(), entity.getSoundSource(), 1.0F, random1);
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.REDSTONE_GOLEM_ELECTRIC_SPARKS.get(), entity.getSoundSource(), 1.0F, 2.0F);
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.BOMB_SPAWN.get(), entity.getSoundSource(), 1.0F, random2);
        }
    }
}
