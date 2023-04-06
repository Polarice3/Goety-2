package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.entities.projectiles.ModFireball;
import com.Polarice3.Goety.common.magic.InstantCastSpells;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Learned you could use this method for better projectile accuracy from codes by @Yunus1903
 */
public class FireballSpell extends InstantCastSpells {

    @Override
    public int SoulCost() {
        return SpellConfig.FireballCost.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.BLAZE_SHOOT;
    }

    @Override
    public void WandResult(ServerLevel worldIn, LivingEntity entityLiving) {
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
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), CastingSound(), SoundSource.PLAYERS, 1.0F, 1.0F);
//        this.IncreaseInfamy(SpellConfig.FireballInfamyChance.get(), (PlayerEntity) entityLiving);
    }

    @Override
    public void StaffResult(ServerLevel worldIn, LivingEntity entityLiving) {
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
        for(int i = 0; i < 2; ++i) {
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
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), CastingSound(), SoundSource.PLAYERS, 1.0F, 1.0F);
//        this.IncreaseInfamy(SpellConfig.FireballInfamyChance.get(), (PlayerEntity) entityLiving);
    }
}
