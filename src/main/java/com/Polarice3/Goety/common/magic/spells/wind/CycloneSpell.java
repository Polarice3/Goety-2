package com.Polarice3.Goety.common.magic.spells.wind;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.Cyclone;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.config.SpellConfig;
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

public class CycloneSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.CycloneCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.CycloneDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.WIND.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.CycloneCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WIND;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RADIUS.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        Vec3 vector3d = entityLiving.getViewVector( 1.0F);
        Cyclone cyclone = new Cyclone(worldIn,
                entityLiving.getX() + vector3d.x / 2,
                entityLiving.getEyeY() - 0.2,
                entityLiving.getZ() + vector3d.z / 2,
                vector3d.x,
                vector3d.y,
                vector3d.z);
        cyclone.setOwnerId(entityLiving.getUUID());
        if (this.getTarget(entityLiving) != null) {
            cyclone.setTarget(this.getTarget(entityLiving));
        } else {
            cyclone.shootFromRotation(entityLiving, entityLiving.getXRot(), entityLiving.getYRot(), 0.0F, 1.0F + WandUtil.getLevels(ModEnchantments.VELOCITY.get(), entityLiving), 1.0F);
        }
        float size = 1.0F;
        float damage = 0.0F;
        if (rightStaff(staff)){
            size += 1.0F;
            damage += 1.0F;
        }
        cyclone.setDamage(damage * (WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving) + 1));
        cyclone.setTotalLife(600 * (WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving) + 1));
        cyclone.setBoltSpeed(WandUtil.getLevels(ModEnchantments.VELOCITY.get(), entityLiving));
        cyclone.setSize(size + (WandUtil.getLevels(ModEnchantments.RADIUS.get(), entityLiving) / 10.0F));
        worldIn.addFreshEntity(cyclone);
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), CastingSound(), this.getSoundSource(), 1.0F, 1.0F);
    }
}
