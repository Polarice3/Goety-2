package com.Polarice3.Goety.common.magic.spells.wind;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.EverChargeSpells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class FlyingSpell extends EverChargeSpells {

    public int defaultSoulCost() {
        return SpellConfig.FlyingCost.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.WIND.get();
    }

    @Override
    public SoundEvent loopSound() {
        return ModSounds.FLIGHT.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WIND;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        return list;
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

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        double d = 0.5D;
        if (rightStaff(staff)){
            d = 1.0D;
        }
        this.CommonResult(worldIn, entityLiving, d);
    }
}
