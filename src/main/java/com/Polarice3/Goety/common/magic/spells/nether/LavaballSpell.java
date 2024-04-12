package com.Polarice3.Goety.common.magic.spells.nether;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.Lavaball;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class LavaballSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.LavaballCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.LavaballDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.EVOKER_PREPARE_ATTACK;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.LavaballCoolDown.get();
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
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    @Override
    public ColorUtil particleColors(LivingEntity entityLiving) {
        return new ColorUtil(1.0F, 0.0F, 0.0F);
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        Vec3 vector3d = entityLiving.getViewVector( 1.0F);
        Lavaball fireballEntity = new Lavaball(worldIn,
                entityLiving.getX() + vector3d.x / 2,
                entityLiving.getEyeY() - 0.2,
                entityLiving.getZ() + vector3d.z / 2,
                vector3d.x,
                vector3d.y,
                vector3d.z);
        fireballEntity.setUpgraded(true);
        fireballEntity.setOwner(entityLiving);
        if (isShifting(entityLiving)){
            fireballEntity.setDangerous(false);
        }
        worldIn.addFreshEntity(fireballEntity);
        if (rightStaff(staff)) {
            for (int i = 0; i < 2; ++i) {
                Lavaball lavaballEntity = new Lavaball(worldIn,
                        entityLiving.getX() + vector3d.x / 2 + worldIn.random.nextGaussian(),
                        entityLiving.getEyeY() - 0.2,
                        entityLiving.getZ() + vector3d.z / 2 + worldIn.random.nextGaussian(),
                        vector3d.x,
                        vector3d.y,
                        vector3d.z);
                lavaballEntity.setUpgraded(true);
                lavaballEntity.setOwner(entityLiving);
                if (isShifting(entityLiving)) {
                    lavaballEntity.setDangerous(false);
                }
                worldIn.addFreshEntity(lavaballEntity);
            }
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.GHAST_SHOOT, this.getSoundSource(), 1.0F, 1.0F);
    }
}
