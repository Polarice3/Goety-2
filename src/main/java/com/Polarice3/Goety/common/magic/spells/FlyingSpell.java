package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.ChargingSpells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class FlyingSpell extends ChargingSpells {

    @Override
    public int Cooldown() {
        return 0;
    }

    public int SoulCost() {
        return SpellConfig.FlyingCost.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.WIND.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WIND;
    }

    public void CommonResult(ServerLevel worldIn, LivingEntity entityLiving, double power){
        if (entityLiving instanceof Player player){
            int enchantment = 0;
            if (WandUtil.enchantedFocus(player)){
                enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), player);
            }
            player.hurtMarked = true;
            if (!player.level.isClientSide){
                player.setOnGround(false);
            }
            Vec3 vector3d = player.getLookAngle();
            double d0 = power + (double) (enchantment / 2);
            player.setDeltaMovement(vector3d.x * d0, vector3d.y * d0, vector3d.z * d0);
            player.hasImpulse = true;
            player.fallDistance = 0;
            for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
                worldIn.sendParticles(ParticleTypes.CLOUD, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
            }
        }
    }

    @Override
    public void RegularResult(ServerLevel worldIn, LivingEntity entityLiving) {
        this.CommonResult(worldIn, entityLiving, 0.5D);
    }

    public void StaffResult(ServerLevel worldIn, LivingEntity entityLiving){
        this.CommonResult(worldIn, entityLiving, 1.0D);
    }
}
