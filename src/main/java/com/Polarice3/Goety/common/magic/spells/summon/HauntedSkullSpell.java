package com.Polarice3.Goety.common.magic.spells.summon;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.effects.ModEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.HauntedSkull;
import com.Polarice3.Goety.common.magic.SummonSpells;
import com.Polarice3.Goety.utils.ModMathHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;

public class HauntedSkullSpell extends SummonSpells {
    public int burning = 0;
    public int radius = 0;

    public int SoulCost() {
        return SpellConfig.HauntedSkullCost.get();
    }

    public int CastDuration() {
        return SpellConfig.HauntedSkullDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.HauntedSkullCooldown.get();
    }

    public SoundEvent CastingSound() {
        return SoundEvents.EVOKER_PREPARE_SUMMON;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NECROMANCY;
    }

    public void commonResult(ServerLevel worldIn, LivingEntity entityLiving){
        if (entityLiving instanceof Player player){
            if (WandUtil.enchantedFocus(player)){
                enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), player);
                duration = WandUtil.getLevels(ModEnchantments.DURATION.get(), player) + 1;
                burning = WandUtil.getLevels(ModEnchantments.BURNING.get(), player);
                radius = WandUtil.getLevels(ModEnchantments.RADIUS.get(), player);
            }
        }
        if (isShifting(entityLiving)) {
            for (Entity entity : worldIn.getAllEntities()) {
                if (entity instanceof HauntedSkull) {
                    if (((HauntedSkull) entity).getTrueOwner() == entityLiving) {
                        entity.moveTo(entityLiving.position());
                    }
                }
            }
            for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
                worldIn.sendParticles(ParticleTypes.POOF, entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
            }
            worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
    }

    public void RegularResult(ServerLevel worldIn, LivingEntity entityLiving) {
        this.commonResult(worldIn, entityLiving);
        if (!isShifting(entityLiving)) {
            BlockPos blockpos = entityLiving.blockPosition().offset(-2 + entityLiving.getRandom().nextInt(5), 1, -2 + entityLiving.getRandom().nextInt(5));
            HauntedSkull summonedentity = new HauntedSkull(ModEntityType.HAUNTED_SKULL.get(), worldIn);
            summonedentity.setOwnerId(entityLiving.getUUID());
            summonedentity.moveTo(blockpos, 0.0F, 0.0F);
            summonedentity.finalizeSpawn(worldIn, entityLiving.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
            summonedentity.setBoundOrigin(blockpos);
            summonedentity.setLimitedLife(ModMathHelper.ticksToSeconds(10) * duration);
            if (enchantment > 0){
                int boost = Mth.clamp(enchantment - 1, 0, 10);
                summonedentity.addEffect(new MobEffectInstance(ModEffects.BUFF.get(), Integer.MAX_VALUE, boost));
            }
            if (radius > 0){
                summonedentity.setExplosionPower(1.0F + radius/4.0F);
            }
            if (burning > 0){
                summonedentity.setBurning(burning);
            }
            this.setTarget(worldIn, entityLiving, summonedentity);
            summonedentity.setUpgraded(this.NecroPower(entityLiving));
            worldIn.addFreshEntity(summonedentity);
            worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 1.0F, 1.0F);
            for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
                worldIn.sendParticles(ParticleTypes.POOF, summonedentity.getX(), summonedentity.getEyeY(), summonedentity.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
            }
        }
    }

    public void StaffResult(ServerLevel worldIn, LivingEntity entityLiving) {
        this.commonResult(worldIn, entityLiving);
        if (!isShifting(entityLiving)) {
            for (int i1 = 0; i1 < 3; ++i1) {
                BlockPos blockpos = entityLiving.blockPosition().offset(-2 + entityLiving.getRandom().nextInt(5), 1, -2 + entityLiving.getRandom().nextInt(5));
                HauntedSkull summonedentity = new HauntedSkull(ModEntityType.HAUNTED_SKULL.get(), worldIn);
                summonedentity.setOwnerId(entityLiving.getUUID());
                summonedentity.moveTo(blockpos, 0.0F, 0.0F);
                summonedentity.finalizeSpawn(worldIn, entityLiving.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                summonedentity.setBoundOrigin(blockpos);
                summonedentity.setLimitedLife(ModMathHelper.ticksToSeconds(10) * duration);
                if (enchantment > 0){
                    int boost = Mth.clamp(enchantment - 1, 0, 10);
                    summonedentity.addEffect(new MobEffectInstance(ModEffects.BUFF.get(), Integer.MAX_VALUE, boost));
                }
                if (radius > 0){
                    summonedentity.setExplosionPower(1.0F + radius/4.0F);
                }
                if (burning > 0){
                    summonedentity.setBurning(burning);
                }
                this.setTarget(worldIn, entityLiving, summonedentity);
                summonedentity.setUpgraded(this.NecroPower(entityLiving));
                worldIn.addFreshEntity(summonedentity);
                for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
                    worldIn.sendParticles(ParticleTypes.POOF, summonedentity.getX(), summonedentity.getEyeY(), summonedentity.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
                }
            }
            worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
    }
}
