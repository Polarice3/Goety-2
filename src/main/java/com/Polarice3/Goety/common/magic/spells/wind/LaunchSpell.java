package com.Polarice3.Goety.common.magic.spells.wind;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class LaunchSpell extends Spells {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.LaunchCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.LaunchDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.WIND.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.LaunchCoolDown.get();
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

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
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
            double power = rightStaff(staff) ? 2.5D : 1.5D;
            double d0 = power + (double) (enchantment / 4);
            player.setDeltaMovement(vector3d.x * d0, vector3d.y * d0, vector3d.z * d0);
            player.hasImpulse = true;
            player.fallDistance = 0;
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), CastingSound(), this.getSoundSource(), 2.0F, 1.0F);
    }
}
