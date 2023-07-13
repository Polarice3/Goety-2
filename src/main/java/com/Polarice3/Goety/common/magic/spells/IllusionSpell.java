package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Doppelganger;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class IllusionSpell extends Spells {
    public int SoulCost() {
        return SpellConfig.IllusionCost.get();
    }

    public int CastDuration() {
        return SpellConfig.IllusionDuration.get();
    }

    public SoundEvent CastingSound() {
        return SoundEvents.ILLUSIONER_PREPARE_MIRROR;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.LICH;
    }

    public void RegularResult(ServerLevel worldIn, LivingEntity entityLiving) {
        for (Entity entity : worldIn.getAllEntities()) {
            if (entity instanceof Doppelganger) {
                if (((Doppelganger) entity).getTrueOwner() == entityLiving) {
                    ((Doppelganger) entity).die(DamageSource.STARVE);
                }
            }
        }
        for (int i1 = 0; i1 < 4; ++i1) {
            Doppelganger summonedentity = new Doppelganger(ModEntityType.DOPPELGANGER.get(), worldIn);
            summonedentity.setTrueOwner(entityLiving);
            summonedentity.moveTo(BlockFinder.SummonRadius(entityLiving, worldIn), 0.0F, 0.0F);
            MobUtil.moveDownToGround(summonedentity);
            summonedentity.setLimitedLife(1200);
            summonedentity.setPersistenceRequired();
            summonedentity.setUpgraded(CuriosFinder.hasIllusionRobe(entityLiving));
            summonedentity.setUndeadClone(CuriosFinder.hasNamelessSet(entityLiving));
            summonedentity.finalizeSpawn((ServerLevelAccessor) worldIn, entityLiving.level.getCurrentDifficultyAt(BlockFinder.SummonRadius(entityLiving, worldIn)), MobSpawnType.MOB_SUMMONED, null, null);
            HitResult rayTraceResult = this.rayTrace(worldIn, entityLiving, 16, 3);
            if (rayTraceResult instanceof EntityHitResult) {
                Entity target = ((EntityHitResult) rayTraceResult).getEntity();
                if (target instanceof LivingEntity){
                    double d2 = target.getX() - summonedentity.getX();
                    double d1 = target.getZ() - summonedentity.getZ();
                    summonedentity.setYRot(-((float)Mth.atan2(d2, d1)) * (180F / (float)Math.PI));
                }
            }
            worldIn.addFreshEntity(summonedentity);
            for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
                worldIn.sendParticles(ParticleTypes.CLOUD, entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ(), 0, 0.0F, 0.0F, 0.0F, 0);
            }
        }
        if (CuriosFinder.hasIllusionRobe(entityLiving)){
            entityLiving.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 1200));
            HitResult rayTraceResult = this.rayTrace(worldIn, entityLiving, 16, 3);
            if (rayTraceResult instanceof EntityHitResult) {
                Entity target = ((EntityHitResult) rayTraceResult).getEntity();
                if (target instanceof LivingEntity){
                    ((LivingEntity) target).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400));
                }
            }
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, this.getSoundSource(), 1.0F, 1.0F);
        for(int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
            worldIn.sendParticles(ParticleTypes.CLOUD, entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ(), 0, 0.0F, 0.0F, 0.0F, 0);
        }
//        this.IncreaseInfamy(SpellConfig.IllusionInfamyChance.get(), (PlayerEntity) entityLiving);
    }

    public void StaffResult(ServerLevel worldIn, LivingEntity entityLiving) {
        for (Entity entity : worldIn.getAllEntities()) {
            if (entity instanceof Doppelganger) {
                if (((Doppelganger) entity).getTrueOwner() == entityLiving) {
                    ((Doppelganger) entity).die(DamageSource.STARVE);
                }
            }
        }
        for (int i1 = 0; i1 < 4 + entityLiving.level.random.nextInt(4); ++i1) {
            Doppelganger summonedentity = new Doppelganger(ModEntityType.DOPPELGANGER.get(), worldIn);
            summonedentity.setTrueOwner(entityLiving);
            summonedentity.moveTo(BlockFinder.SummonRadius(entityLiving, worldIn), 0.0F, 0.0F);
            MobUtil.moveDownToGround(summonedentity);
            summonedentity.setUndeadClone(CuriosFinder.hasNamelessSet(entityLiving));
            summonedentity.setLimitedLife(1200);
            summonedentity.setPersistenceRequired();
            summonedentity.setUpgraded(CuriosFinder.hasIllusionRobe(entityLiving));
            summonedentity.finalizeSpawn((ServerLevelAccessor) worldIn, entityLiving.level.getCurrentDifficultyAt(BlockFinder.SummonRadius(entityLiving, worldIn)), MobSpawnType.MOB_SUMMONED, null, null);
            HitResult rayTraceResult = this.rayTrace(worldIn, entityLiving, 16, 3);
            if (rayTraceResult instanceof EntityHitResult) {
                Entity target = ((EntityHitResult) rayTraceResult).getEntity();
                if (target instanceof LivingEntity){
                    double d2 = target.getX() - summonedentity.getX();
                    double d1 = target.getZ() - summonedentity.getZ();
                    summonedentity.setYRot(-((float)Mth.atan2(d2, d1)) * (180F / (float)Math.PI));
                }
            }
            worldIn.addFreshEntity(summonedentity);
            for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
                worldIn.sendParticles(ParticleTypes.CLOUD, entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ(), 0, 0.0F, 0.0F, 0.0F, 0);
            }
        }
        if (CuriosFinder.hasIllusionRobe(entityLiving)){
            entityLiving.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 1200));
            HitResult rayTraceResult = this.rayTrace(worldIn, entityLiving, 16, 3);
            if (rayTraceResult instanceof EntityHitResult) {
                Entity target = ((EntityHitResult) rayTraceResult).getEntity();
                if (target instanceof LivingEntity){
                    ((LivingEntity) target).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400));
                }
            }
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, this.getSoundSource(), 1.0F, 1.0F);
        for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
            worldIn.sendParticles(ParticleTypes.CLOUD, entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ(), 0, 0.0F, 0.0F, 0.0F, 0);
        }
//        this.IncreaseInfamy(SpellConfig.IllusionInfamyChance.get(), (PlayerEntity) entityLiving);
    }

}
