package com.Polarice3.Goety.common.magic.spells.necromancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.undead.HauntedSkull;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class HauntedSkullSpell extends SummonSpell {

    public int defaultSoulCost() {
        return SpellConfig.HauntedSkullCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.HauntedSkullDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.HauntedSkullSummonDown.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SUMMON.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.HauntedSkullCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NECROMANCY;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.BURNING.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    @Override
    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof HauntedSkull;
    }

    @Override
    public int summonLimit() {
        return SpellConfig.SkullLimit.get();
    }

    @Override
    public void commonResultHit(ServerLevel worldIn, LivingEntity caster) {
        for (int i = 0; i < caster.level.random.nextInt(35) + 10; ++i) {
            worldIn.sendParticles(ParticleTypes.POOF, caster.getX(), caster.getEyeY(), caster.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
        }
        this.playSound(worldIn, caster, SoundEvents.EVOKER_CAST_SPELL);
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        int burning = spellStat.getBurning();
        double radius = spellStat.getRadius();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
            burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), caster);
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
        }
        if (!isShifting(caster)) {
            int i = 1;
            if (rightStaff(staff)) {
                i = 3;
            }
            for (int i1 = 0; i1 < i; ++i1) {
                BlockPos blockpos = caster.blockPosition().offset(-2 + caster.getRandom().nextInt(5), 1, -2 + caster.getRandom().nextInt(5));
                HauntedSkull summonedentity = new HauntedSkull(ModEntityType.HAUNTED_SKULL.get(), worldIn);
                summonedentity.setTrueOwner(caster);
                summonedentity.moveTo(blockpos, 0.0F, 0.0F);
                summonedentity.finalizeSpawn(worldIn, caster.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                summonedentity.setBoundOrigin(blockpos);
                summonedentity.setLimitedLife(MathHelper.minutesToTicks(1) * duration);
                this.buffSummon(caster, summonedentity, potency);
                if (radius > 0) {
                    summonedentity.setExplosionPower((float) (1.0F + radius / 4.0F));
                }
                if (burning > 0) {
                    summonedentity.setBurning(burning);
                }
                this.setTarget(caster, summonedentity);
                summonedentity.setUpgraded(this.NecroPower(caster));
                worldIn.addFreshEntity(summonedentity);
                this.summonAdvancement(caster, summonedentity);
            }
            this.playSound(worldIn, caster, SoundEvents.EVOKER_CAST_SPELL);
        }
    }
}
