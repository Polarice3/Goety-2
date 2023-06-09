package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.InstantCastSpells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class LaunchSpell extends InstantCastSpells {

    @Override
    public int SoulCost() {
        return SpellConfig.LaunchCost.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.WIND.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WIND;
    }

    @Override
    public void RegularResult(ServerLevel worldIn, LivingEntity entityLiving) {
        if (entityLiving instanceof Player player){
            int enchantment = 0;
            int duration = 1;
            if (WandUtil.enchantedFocus(player)){
                enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), player);
                duration = WandUtil.getLevels(ModEnchantments.DURATION.get(), player) + 1;
            }
            player.hurtMarked = true;
            if (!player.level.isClientSide){
                player.setOnGround(false);
            }
            Vec3 vector3d = player.getLookAngle();
            double d0 = 2.5D + (double) (enchantment/2);
            player.setDeltaMovement(vector3d.x * d0, vector3d.y * d0, vector3d.z * d0);
            player.hasImpulse = true;
            player.fallDistance = 0;
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20 * duration));
//            this.IncreaseInfamy(SpellConfig.LaunchInfamyChance.get(), player);
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), CastingSound(), SoundSource.PLAYERS, 2.0F, 1.0F);
    }

    @Override
    public void StaffResult(ServerLevel worldIn, LivingEntity entityLiving) {
        if (entityLiving instanceof Player player){
            int enchantment = 0;
            int duration = 1;
            if (WandUtil.enchantedFocus(player)){
                enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), player);
                duration = WandUtil.getLevels(ModEnchantments.DURATION.get(), player) + 1;
            }
            player.hurtMarked = true;
            if (!player.level.isClientSide){
                player.setOnGround(false);
            }
            Vec3 vector3d = player.getLookAngle();
            double d0 = 5.0D + (double) (enchantment/2);
            player.setDeltaMovement(vector3d.x * d0, vector3d.y * d0, vector3d.z * d0);
            player.hasImpulse = true;
            player.fallDistance = 0;
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20 * duration));
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), CastingSound(), SoundSource.PLAYERS, 2.0F, 1.0F);
    }
}
