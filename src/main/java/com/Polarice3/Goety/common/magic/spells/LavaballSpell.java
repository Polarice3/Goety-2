package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.entities.projectiles.Lavaball;
import com.Polarice3.Goety.common.magic.Spells;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class LavaballSpell extends Spells {

    @Override
    public int SoulCost() {
        return SpellConfig.LavaballCost.get();
    }

    public int CastDuration() {
        return SpellConfig.LavaballDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.EVOKER_PREPARE_ATTACK;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public void RegularResult(ServerLevel worldIn, LivingEntity entityLiving) {
        Vec3 vector3d = entityLiving.getViewVector( 1.0F);
        Lavaball fireballEntity = new Lavaball(worldIn,
                entityLiving.getX() + vector3d.x / 2,
                entityLiving.getEyeY() - 0.2,
                entityLiving.getZ() + vector3d.z / 2,
                vector3d.x,
                vector3d.y,
                vector3d.z);
        fireballEntity.setOwner(entityLiving);
        if (isShifting(entityLiving)){
            fireballEntity.setDangerous(false);
        }
        worldIn.addFreshEntity(fireballEntity);
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.GHAST_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
//        this.IncreaseInfamy(SpellConfig.LavaballInfamyChance.get(), (PlayerEntity) entityLiving);
    }

    @Override
    public void StaffResult(ServerLevel worldIn, LivingEntity entityLiving) {
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
        for(int i = 0; i < 2; ++i) {
            Lavaball lavaballEntity = new Lavaball(worldIn,
                    entityLiving.getX() + vector3d.x / 2 + worldIn.random.nextGaussian(),
                    entityLiving.getEyeY() - 0.2,
                    entityLiving.getZ() + vector3d.z / 2 + worldIn.random.nextGaussian(),
                    vector3d.x,
                    vector3d.y,
                    vector3d.z);
            lavaballEntity.setUpgraded(true);
            lavaballEntity.setOwner(entityLiving);
            if (isShifting(entityLiving)){
                lavaballEntity.setDangerous(false);
            }
            worldIn.addFreshEntity(lavaballEntity);
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.GHAST_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
//        this.IncreaseInfamy(SpellConfig.LavaballInfamyChance.get(), (PlayerEntity) entityLiving);
    }
}
