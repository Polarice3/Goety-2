package com.Polarice3.Goety.common.magic.spells.nether;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.ModFireball;
import com.Polarice3.Goety.common.magic.Spell;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Learned you could use this method for better projectile accuracy from codes by @Yunus1903
 */
public class FireballSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.FireballCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.FireballDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.BLAZE_SHOOT;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.FireballCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.BURNING.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        Vec3 vector3d = entityLiving.getViewVector( 1.0F);
        ModFireball smallFireballEntity = new ModFireball(worldIn,
                entityLiving.getX() + vector3d.x / 2,
                entityLiving.getEyeY() - 0.2,
                entityLiving.getZ() + vector3d.z / 2,
                vector3d.x,
                vector3d.y,
                vector3d.z);
        smallFireballEntity.setOwner(entityLiving);
        worldIn.addFreshEntity(smallFireballEntity);
        if (rightStaff(staff)) {
            for (int i = 0; i < 2; ++i) {
                ModFireball smallFireballEntity2 = new ModFireball(worldIn,
                        entityLiving.getX() + vector3d.x / 2 + worldIn.random.nextGaussian(),
                        entityLiving.getEyeY() - 0.2,
                        entityLiving.getZ() + vector3d.z / 2 + worldIn.random.nextGaussian(),
                        vector3d.x,
                        vector3d.y,
                        vector3d.z);
                smallFireballEntity2.setOwner(entityLiving);
                worldIn.addFreshEntity(smallFireballEntity2);
            }
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), CastingSound(), this.getSoundSource(), 1.0F, 1.0F);
    }
}
