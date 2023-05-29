package com.Polarice3.Goety.common.magic.spells.summon;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.effects.ModEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.DrownedServant;
import com.Polarice3.Goety.common.entities.ally.HuskServant;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.ZombieServant;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinBruteServant;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.Polarice3.Goety.common.magic.SummonSpells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraftforge.common.Tags;

public class ZombieSpell extends SummonSpells {

    public int SoulCost() {
        return SpellConfig.ZombieCost.get();
    }

    public int CastDuration() {
        return SpellConfig.ZombieDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.ZombieCooldown.get();
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
            }
        }
        if (isShifting(entityLiving)) {
            for (Entity entity : worldIn.getAllEntities()) {
                if (entity instanceof ZombieServant) {
                    if (((ZombieServant) entity).getTrueOwner() == entityLiving) {
                        entity.moveTo(entityLiving.position());
                    }
                }
            }
            for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
                worldIn.sendParticles(ParticleTypes.POOF, entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
            }
            worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.SUMMON_SPELL.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
    }

    public void RegularResult(ServerLevel worldIn, LivingEntity entityLiving) {
        this.commonResult(worldIn, entityLiving);
        if (!isShifting(entityLiving)) {
            Summoned summonedentity;
            BlockPos blockPos = BlockFinder.SummonRadius(entityLiving, worldIn);
            if (entityLiving.isUnderWater()){
                blockPos = BlockFinder.SummonWaterRadius(entityLiving, worldIn);
            }
            if (entityLiving.isUnderWater() && worldIn.isWaterAt(blockPos)){
                summonedentity = new DrownedServant(ModEntityType.DROWNED_SERVANT.get(), worldIn);
            } else if (worldIn.getBiome(blockPos).is(Tags.Biomes.IS_DESERT) && worldIn.canSeeSky(blockPos)){
                summonedentity = new HuskServant(ModEntityType.HUSK_SERVANT.get(), worldIn);
            } else if (worldIn.dimension() == Level.NETHER){
                Summoned summoned = new ZPiglinServant(ModEntityType.ZPIGLIN_SERVANT.get(), worldIn);
                if (worldIn.random.nextFloat() <= 0.25F && BlockFinder.findStructure(worldIn, entityLiving, BuiltinStructures.BASTION_REMNANT)){
                    summoned = new ZPiglinBruteServant(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), worldIn);
                }
                summonedentity = summoned;
            } else {
                summonedentity = new ZombieServant(ModEntityType.ZOMBIE_SERVANT.get(), worldIn);
            }
            summonedentity.setOwnerId(entityLiving.getUUID());
            summonedentity.moveTo(blockPos, 0.0F, 0.0F);
            if (summonedentity.getType() != ModEntityType.DROWNED_SERVANT.get()){
                MobUtil.moveDownToGround(summonedentity);
            }
            summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
            summonedentity.setPersistenceRequired();
            summonedentity.setUpgraded(this.NecroPower(entityLiving));
            summonedentity.finalizeSpawn(worldIn, entityLiving.level.getCurrentDifficultyAt(entityLiving.blockPosition()), MobSpawnType.MOB_SUMMONED,null,null);
            if (enchantment > 0){
                int boost = Mth.clamp(enchantment - 1, 0, 10);
                summonedentity.addEffect(new MobEffectInstance(ModEffects.BUFF.get(), Integer.MAX_VALUE, boost, false, false));
            }
            this.SummonSap(entityLiving, summonedentity);
            this.setTarget(worldIn, entityLiving, summonedentity);
            worldIn.addFreshEntity(summonedentity);
            worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 1.0F, 1.0F);
            for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
                worldIn.sendParticles(ParticleTypes.POOF, summonedentity.getX(), summonedentity.getEyeY(), summonedentity.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
            }
            this.SummonDown(entityLiving);
        }
    }

    public void StaffResult(ServerLevel worldIn, LivingEntity entityLiving) {
        this.commonResult(worldIn, entityLiving);
        if (!isShifting(entityLiving)) {
            for (int i1 = 0; i1 < 2 + entityLiving.level.random.nextInt(4); ++i1) {
                Summoned summonedentity;
                BlockPos blockPos = BlockFinder.SummonRadius(entityLiving, worldIn);
                if (entityLiving.isUnderWater()){
                    blockPos = BlockFinder.SummonWaterRadius(entityLiving, worldIn);
                }
                if (entityLiving.isUnderWater() && worldIn.isWaterAt(blockPos)){
                    summonedentity = new DrownedServant(ModEntityType.DROWNED_SERVANT.get(), worldIn);
                } else if (worldIn.getBiome(blockPos).is(Tags.Biomes.IS_DESERT) && worldIn.canSeeSky(blockPos)){
                    summonedentity = new HuskServant(ModEntityType.HUSK_SERVANT.get(), worldIn);
                } else if (worldIn.dimension() == Level.NETHER){
                    Summoned summoned = new ZPiglinServant(ModEntityType.ZPIGLIN_SERVANT.get(), worldIn);
                    if (worldIn.random.nextFloat() <= 0.25F && BlockFinder.findStructure(worldIn, entityLiving, BuiltinStructures.BASTION_REMNANT)){
                        summoned = new ZPiglinBruteServant(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), worldIn);
                    }
                    summonedentity = summoned;
                } else {
                    summonedentity = new ZombieServant(ModEntityType.ZOMBIE_SERVANT.get(), worldIn);
                }
                summonedentity.setOwnerId(entityLiving.getUUID());
                summonedentity.moveTo(blockPos, 0.0F, 0.0F);
                if (summonedentity.getType() != ModEntityType.DROWNED_SERVANT.get()){
                    MobUtil.moveDownToGround(summonedentity);
                }
                summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                summonedentity.setPersistenceRequired();
                summonedentity.setUpgraded(this.NecroPower(entityLiving));
                summonedentity.finalizeSpawn(worldIn, entityLiving.level.getCurrentDifficultyAt(entityLiving.blockPosition()), MobSpawnType.MOB_SUMMONED,null,null);
                if (enchantment > 0){
                    int boost = Mth.clamp(enchantment - 1, 0, 10);
                    summonedentity.addEffect(new MobEffectInstance(ModEffects.BUFF.get(), Integer.MAX_VALUE, boost, false, false));
                }
                this.SummonSap(entityLiving, summonedentity);
                this.setTarget(worldIn, entityLiving, summonedentity);
                worldIn.addFreshEntity(summonedentity);
                for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
                    worldIn.sendParticles(ParticleTypes.POOF, summonedentity.getX(), summonedentity.getEyeY(), summonedentity.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
                }
            }
            this.SummonDown(entityLiving);
            worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
    }
}
