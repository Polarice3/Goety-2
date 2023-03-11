package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.magic.ChargingSpells;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public class FeastSpell extends ChargingSpells {

    public int SoulCost() {
        return SpellConfig.FeastCost.get();
    }

    @Override
    public int Cooldown() {
        return SpellConfig.FeastDuration.get();
    }

    public SoundEvent CastingSound() {
        return SoundEvents.EVOKER_PREPARE_ATTACK;
    }

    public void WandResult(ServerLevel worldIn, LivingEntity entityLiving){
        int i = (int) entityLiving.getX();
        int j = (int) entityLiving.getY();
        int k = (int) entityLiving.getZ();
        for (LivingEntity entity : worldIn.getEntitiesOfClass(LivingEntity.class, (new AABB(i, j, k, i, j - 4, k)).inflate(16.0D))) {
            float f = (float) Mth.atan2(entity.getZ() - entityLiving.getZ(), entity.getX() - entityLiving.getX());
            if (entity != entityLiving){
                WandUtil.spawnFangs(entityLiving, entity.getX(), entity.getZ(), entity.getY(), entity.getY() + 1.0D, f, 1);
            }
        }
        for(int i1 = 0; i1 < entityLiving.level.random.nextInt(35) + 10; ++i1) {
            worldIn.sendParticles(ParticleTypes.POOF, entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
        }
//        this.IncreaseInfamy(SpellConfig.FeastInfamyChance.get(), (PlayerEntity) entityLiving);
    }

    public void StaffResult(ServerLevel worldIn, LivingEntity entityLiving){
        int i = (int) entityLiving.getX();
        int j = (int) entityLiving.getY();
        int k = (int) entityLiving.getZ();
        for (LivingEntity entity : worldIn.getEntitiesOfClass(LivingEntity.class, (new AABB(i, j, k, i, j - 4, k)).inflate(32.0D))) {
            float f = (float) Mth.atan2(entity.getZ() - entityLiving.getZ(), entity.getX() - entityLiving.getX());
            if (entity != entityLiving){
                WandUtil.spawnFangs(entityLiving, entity.getX(), entity.getZ(), entity.getY(), entity.getY() + 1.0D, f, 1);
                for(int i1 = 0; i1 < 5; ++i1) {
                    float f1 = f + (float)i1 * (float)Math.PI * 0.4F;
                    WandUtil.spawnFangs(entityLiving,entity.getX() + (double)Mth.cos(f1) * 1.5D, entity.getZ() + (double)Mth.sin(f1) * 1.5D, entity.getY(), entity.getY() + 1.0D, f1, 0);
                }
                for(int k1 = 0; k1 < 8; ++k1) {
                    float f2 = f + (float)k1 * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
                    WandUtil.spawnFangs(entityLiving,entity.getX() + (double)Mth.cos(f2) * 2.5D, entity.getZ() + (double)Mth.sin(f2) * 2.5D, entity.getY(), entity.getY() + 1.0D, f2, 3);
                }
            }
        }
        for(int i1 = 0; i1 < entityLiving.level.random.nextInt(35) + 10; ++i1) {
            worldIn.sendParticles(ParticleTypes.POOF, entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
        }
//        this.IncreaseInfamy(SpellConfig.FeastInfamyChance.get(), (PlayerEntity) entityLiving);
    }

}
