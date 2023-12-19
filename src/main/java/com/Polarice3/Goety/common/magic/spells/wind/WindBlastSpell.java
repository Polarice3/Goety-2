package com.Polarice3.Goety.common.magic.spells.wind;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.projectiles.FireTornado;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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

    @Override
    public int SpellCooldown() {
        return SpellConfig.WindBlastCoolDown.get();
    }

    public SpellType getSpellType(){
        return SpellType.WIND;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        Vec3 srcVec = new Vec3(entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ());
        Vec3 lookVec = entityLiving.getViewVector(1.0F);
        int i0 = rightStaff(staff) ? 16 : 8;
        for(int i = 1; i < i0; ++i) {
            Vec3 vector3d2 = srcVec.add(lookVec.scale((double)i));
            worldIn.sendParticles(ModParticleTypes.WIND_BLAST.get(), vector3d2.x, vector3d2.y, vector3d2.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        int range = rightStaff(staff) ? 16 : 8;
        Vec3 rangeVec = new Vec3(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        List<Entity> entities = entityLiving.level.getEntities(entityLiving, entityLiving.getBoundingBox().expandTowards(rangeVec));
        for (Entity entity : entities){
            if (entityLiving.hasLineOfSight(entity)){
                if (entity instanceof LivingEntity living) {
                    double knock = rightStaff(staff) ? 2.0D : 1.0D;
                    MobUtil.knockBack(living, entityLiving, 2.0D * knock, 0.2D * knock, 2.0D * knock);
                }
                if (entity instanceof FireTornado fireTornado){
                    fireTornado.trueRemove();
                }
            }
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.WIND_BLAST.get(), this.getSoundSource(), 3.0F, 1.0F);
    }
}
