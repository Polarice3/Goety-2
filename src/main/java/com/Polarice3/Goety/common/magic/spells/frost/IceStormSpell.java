package com.Polarice3.Goety.common.magic.spells.frost;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.IceStorm;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class IceStormSpell extends Spells {

    public int defaultSoulCost() {
        return SpellConfig.IceStormCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.IceStormDuration.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.FROST_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.IceStormCoolDown.get();
    }

    public SpellType getSpellType() {
        return SpellType.FROST;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        Vec3 vector3d = entityLiving.getViewVector( 1.0F);
        IceStorm iceStorm = new IceStorm(
                entityLiving.getX() + vector3d.x / 2,
                entityLiving.getEyeY() - 0.2,
                entityLiving.getZ() + vector3d.z / 2,
                vector3d.x,
                vector3d.y,
                vector3d.z, worldIn);
        if (WandUtil.enchantedFocus(entityLiving)){
            iceStorm.setExtraDamage(WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving));
            iceStorm.setDuration(WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving));
            iceStorm.setRange(WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving));
            iceStorm.setBoltSpeed(WandUtil.getLevels(ModEnchantments.VELOCITY.get(), entityLiving));
        }
        iceStorm.setOwner(entityLiving);
        worldIn.addFreshEntity(iceStorm);
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.WIND_BLAST.get(), this.getSoundSource(), 1.0F, 0.75F);
    }
}
