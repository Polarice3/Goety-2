package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.projectiles.FireTornado;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class WindBlastSpell extends Spells {

    public int SoulCost() {
        return SpellConfig.WindBlastCost.get();
    }

    public int CastDuration() {
        return SpellConfig.WindBlastDuration.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    public SpellType getSpellType(){
        return SpellType.WIND;
    }

    @Override
    public void RegularResult(ServerLevel worldIn, LivingEntity entityLiving) {
        Vec3 srcVec = new Vec3(entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ());
        Vec3 lookVec = entityLiving.getViewVector(1.0F);
        for(int i = 1; i < 3; ++i) {
            Vec3 vector3d2 = srcVec.add(lookVec.scale((double)i));
            worldIn.sendParticles(ModParticleTypes.WIND_BLAST.get(), vector3d2.x, vector3d2.y, vector3d2.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        Vec3 rangeVec = new Vec3(lookVec.x * 4, lookVec.y * 4, lookVec.z * 4);
        List<Entity> entities = entityLiving.level.getEntities(entityLiving, entityLiving.getBoundingBox().expandTowards(rangeVec));
        for (Entity entity : entities){
            if (entityLiving.hasLineOfSight(entity)){
                double d0 = entity.getX() - entityLiving.getX();
                double d1 = entity.getZ() - entityLiving.getZ();
                double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                MobUtil.push(entity, d0 / d2 * 4.0D, 0.2D, d1 / d2 * 4.0D);
                if (entity instanceof FireTornado fireTornado){
                    fireTornado.trueRemove();
                }
            }
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.WIND_BLAST.get(), this.getSoundSource(), 3.0F, 1.0F);
    }

    @Override
    public void StaffResult(ServerLevel worldIn, LivingEntity entityLiving) {
        Vec3 srcVec = new Vec3(entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ());
        Vec3 lookVec = entityLiving.getViewVector(1.0F);
        for(int i = 1; i < 6; ++i) {
            Vec3 vector3d2 = srcVec.add(lookVec.scale((double)i));
            worldIn.sendParticles(ModParticleTypes.WIND_BLAST.get(), vector3d2.x, vector3d2.y, vector3d2.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        Vec3 rangeVec = new Vec3(lookVec.x * 8, lookVec.y * 8, lookVec.z * 8);
        List<Entity> entities = entityLiving.level.getEntities(entityLiving, entityLiving.getBoundingBox().expandTowards(rangeVec));
        for (Entity entity : entities){
            if (entityLiving.hasLineOfSight(entity)){
                double d0 = entity.getX() - entityLiving.getX();
                double d1 = entity.getZ() - entityLiving.getZ();
                double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                MobUtil.push(entity, d0 / d2 * 8.0D, 0.4D, d1 / d2 * 8.0D);
                if (entity instanceof FireTornado fireTornado){
                    fireTornado.trueRemove();
                }
            }
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.WIND_BLAST.get(), this.getSoundSource(), 3.0F, 1.0F);
    }
}
