package com.Polarice3.Goety.common.magic.spells.wild;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.BearServant;
import com.Polarice3.Goety.common.entities.ally.HoglinServant;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class MaulingSpell extends SummonSpell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.MaulingCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.MaulingDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.WILD_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.MaulingCoolDown.get();
    }

    @Override
    public int SummonDownDuration() {
        return SpellConfig.MaulingSummonDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WILD;
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
        return livingEntity -> livingEntity instanceof BearServant;
    }

    @Override
    public int summonLimit() {
        return SpellConfig.MaulingLimit.get();
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }
        if (!isShifting(caster)) {
            int i = 1;
            if (rightStaff(staff)){
                i = 2;
            }
            for (int i1 = 0; i1 < i; ++i1) {
                Summoned summonedentity = new BearServant(ModEntityType.BEAR_SERVANT.get(), worldIn);
                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), summonedentity, worldIn);
                if (typeStaff(staff, SpellType.NETHER) || worldIn.dimension() == Level.NETHER){
                    summonedentity = new HoglinServant(ModEntityType.HOGLIN_SERVANT.get(), worldIn);
                } else if (typeStaff(staff, SpellType.FROST) || worldIn.getBiome(blockPos).is(Tags.Biomes.IS_COLD_OVERWORLD)) {
                    summonedentity = new BearServant(ModEntityType.POLAR_BEAR_SERVANT.get(), worldIn);
                } else if (blockPos.getY() <= 64 && !worldIn.canSeeSky(blockPos) && summonedentity instanceof BearServant bearServant){
                    bearServant.setBearCave();
                }
                summonedentity.setTrueOwner(caster);
                summonedentity.moveTo(blockPos, 0.0F, 0.0F);
                MobUtil.moveDownToGround(summonedentity);
                summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                summonedentity.setPersistenceRequired();
                summonedentity.finalizeSpawn(worldIn, caster.level.getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.MOB_SUMMONED,null,null);
                this.buffSummon(caster, summonedentity, potency);
                this.SummonSap(caster, summonedentity);
                this.setTarget(caster, summonedentity);
                worldIn.addFreshEntity(summonedentity);
                this.summonAdvancement(caster, summonedentity);
            }
            this.SummonDown(caster);
            this.playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
        }
    }
}
