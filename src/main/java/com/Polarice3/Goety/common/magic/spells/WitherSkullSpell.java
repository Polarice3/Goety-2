package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.entities.projectiles.ModWitherSkull;
import com.Polarice3.Goety.common.magic.Spells;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class WitherSkullSpell extends Spells {

    @Override
    public int SoulCost() {
        return SpellConfig.WitherSkullCost.get();
    }

    @Override
    public int CastDuration() {
        return SpellConfig.WitherSkullDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.WITHER_SHOOT;
    }

    @Override
    public int SpellCooldown() {
        return SpellConfig.WitherSkullCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
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
