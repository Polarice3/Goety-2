package com.Polarice3.Goety.common.magic.spells.nether;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.ModWitherSkull;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.config.SpellConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class WitherSkullSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.WitherSkullCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.WitherSkullDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.WITHER_SHOOT;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.WitherSkullCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.BURNING.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        Vec3 vector3d = entityLiving.getViewVector( 1.0F);
        ModWitherSkull witherSkull = new ModWitherSkull(
                entityLiving.getX() + vector3d.x / 2,
                entityLiving.getEyeY() - 0.2,
                entityLiving.getZ() + vector3d.z / 2,
                vector3d.x,
                vector3d.y,
                vector3d.z, worldIn);
        witherSkull.setOwner(entityLiving);
        if (isShifting(entityLiving)){
            witherSkull.setDangerous(true);
        }
        worldIn.addFreshEntity(witherSkull);
        if (rightStaff(staff)) {
            for (int i = 0; i < 2; ++i) {
                ModWitherSkull witherSkull1 = new ModWitherSkull(
                        entityLiving.getX() + vector3d.x / 2,
                        entityLiving.getEyeY() - 0.2,
                        entityLiving.getZ() + vector3d.z / 2,
                        vector3d.x,
                        vector3d.y,
                        vector3d.z, worldIn);
                witherSkull1.setOwner(entityLiving);
                if (isShifting(entityLiving)) {
                    witherSkull1.setDangerous(true);
                }
                worldIn.addFreshEntity(witherSkull1);
            }
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), this.CastingSound(), this.getSoundSource(), 1.0F, (entityLiving.getRandom().nextFloat() - entityLiving.getRandom().nextFloat()) * 0.2F + 1.0F);
    }
}
