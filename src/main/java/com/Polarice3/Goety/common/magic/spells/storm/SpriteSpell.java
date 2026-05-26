package com.Polarice3.Goety.common.magic.spells.storm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.SpriteMob;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SpriteSpell extends SummonSpell {

    public int defaultSoulCost() {
        return SpellConfig.SpriteCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.SpriteDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.SpriteSummonDown.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.SpriteCoolDown.get();
    }

    public SoundEvent CastingSound(LivingEntity caster) {
        return ModSounds.PREPARE_SUMMON.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.STORM;
    }

    @Override
    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof SpriteMob;
    }

    @Override
    public int summonLimit() {
        return SpellConfig.SpriteLimit.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
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
                i = 2 + caster.level.getRandom().nextInt(4);
            }
            for (int i1 = 0; i1 < i; ++i1) {
                BlockPos blockpos = caster.blockPosition().offset(-2 + caster.getRandom().nextInt(5), 1, -2 + caster.getRandom().nextInt(5));
                SpriteMob spriteMob = new SpriteMob(ModEntityType.SPRITE.get(), worldIn);
                spriteMob.setTrueOwner(caster);
                spriteMob.moveTo(blockpos, caster.getYRot(), 0.0F);
                spriteMob.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                spriteMob.finalizeSpawn(worldIn, caster.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                this.buffSummon(caster, spriteMob, potency);
                this.SummonSap(caster, spriteMob);
                this.setTarget(caster, spriteMob);
                if (worldIn.addFreshEntity(spriteMob)) {
                    this.uponSummon(worldIn, caster, staff, spriteMob);
                }
                this.summonAdvancement(caster, spriteMob);
            }
            this.SummonDown(caster);
            this.playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
        }
    }
}
