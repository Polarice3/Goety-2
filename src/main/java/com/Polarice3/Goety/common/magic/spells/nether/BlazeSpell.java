package com.Polarice3.Goety.common.magic.spells.nether;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.hostile.servants.Inferno;
import com.Polarice3.Goety.common.entities.neutral.BlazeServant;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BlazeSpell extends SummonSpell {

    public int defaultSoulCost() {
        return SpellConfig.BlazeCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.BlazeDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.BlazeSummonDown.get();
    }

    public SoundEvent CastingSound(LivingEntity caster) {
        if (CuriosFinder.hasUnholySet(caster)){
            return ModSounds.APOSTLE_PREPARE_SUMMON.get();
        }
        return ModSounds.PREPARE_SUMMON.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.BlazeCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof BlazeServant;
    }

    @Override
    public int summonLimit() {
        return SpellConfig.BlazeLimit.get();
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }
        if (!isShifting(caster)) {
            int i = 1;
            if (rightStaff(staff)){
                i = 2 + caster.level.random.nextInt(4);
            }
            for (int i1 = 0; i1 < i; ++i1) {
                BlazeServant blazeServant = new BlazeServant(ModEntityType.BLAZE_SERVANT.get(), worldIn);
                if (CuriosFinder.hasUnholySet(caster)){
                    blazeServant = new Inferno(ModEntityType.INFERNO.get(), worldIn);
                }
                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), blazeServant, worldIn);
                if (caster.isUnderWater()){
                    blockPos = BlockFinder.SummonWaterRadius(caster, worldIn);
                }
                blazeServant.setTrueOwner(caster);
                blazeServant.moveTo(blockPos, 0.0F, 0.0F);
                MobUtil.moveDownToGround(blazeServant);
                blazeServant.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                blazeServant.setPersistenceRequired();
                blazeServant.finalizeSpawn(worldIn, caster.level.getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.MOB_SUMMONED,null,null);
                this.buffSummon(caster, blazeServant, potency);
                this.SummonSap(caster, blazeServant);
                this.setTarget(caster, blazeServant);
                if (worldIn.addFreshEntity(blazeServant)) {
                    this.uponSummon(worldIn, caster, staff, blazeServant);
                }
                this.summonAdvancement(caster, blazeServant);
            }
            this.SummonDown(caster);
            this.playSound(worldIn, caster, ModSounds.SUMMON_SPELL_FIERY.get());
        }
    }

    public void buffSummon(LivingEntity caster, LivingEntity summoned, int potency){
        if (potency > 0 && !this.hasSummonDown(caster)){
            int boost = Mth.clamp(potency - 1, 0, 10);
            summoned.addEffect(new MobEffectInstance(GoetyEffects.BUFF.get(), EffectsUtil.infiniteEffect(), boost, false, false));
            if (summoned instanceof BlazeServant blazeServant){
                blazeServant.setFireBallDamage(blazeServant.getFireBallDamage() + potency);
            }
        }
    }
}
