package com.Polarice3.Goety.common.magic.spells.nether;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.GhastServant;
import com.Polarice3.Goety.common.entities.ally.MiniGhast;
import com.Polarice3.Goety.common.entities.hostile.servants.Malghast;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class GhastSpell extends SummonSpell {

    public int defaultSoulCost() {
        return SpellConfig.GhastCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.GhastDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.GhastSummonDown.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.GhastCoolDown.get();
    }

    public SoundEvent CastingSound(LivingEntity caster) {
        if (CuriosFinder.hasUnholySet(caster)){
            return ModSounds.APOSTLE_PREPARE_SUMMON.get();
        }
        return ModSounds.PREPARE_SUMMON.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof Malghast;
    }

    @Override
    public int summonLimit() {
        return SpellConfig.GhastLimit.get();
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
            int i = 3;
            if (rightStaff(staff)){
                i = 3 + worldIn.random.nextInt(3);
            }
            if (this.NetherPower(caster)){
                i -= 2;
            }
            for (int i1 = 0; i1 < i; ++i1) {
                BlockPos blockpos = caster.blockPosition().offset(-2 + caster.getRandom().nextInt(5), 1, -2 + caster.getRandom().nextInt(5));
                Malghast ghast = new MiniGhast(ModEntityType.MINI_GHAST.get(), worldIn);
                if (this.NetherPower(caster)){
                    ghast = new GhastServant(ModEntityType.GHAST_SERVANT.get(), worldIn);
                    if (CuriosFinder.hasUnholySet(caster)){
                        ghast = new Malghast(ModEntityType.MALGHAST.get(), worldIn);
                    }
                    blockpos = BlockFinder.SummonFlyingRadius(caster.blockPosition().above(2), ghast, worldIn, 12);
                }
                ghast.setTrueOwner(caster);
                ghast.moveTo(blockpos, caster.getYRot(), 0.0F);
                ghast.finalizeSpawn(worldIn, caster.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                ghast.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                this.SummonSap(caster, ghast);
                int boost = Mth.clamp(potency, 0, 10);
                ghast.setFireBallDamage(boost - EffectsUtil.getAmplifierPlus(caster, MobEffects.WEAKNESS));
                float extraBlast = Mth.clamp(potency, 0, SpellConfig.MaxRadiusLevel.get()) / 2.5F;
                ghast.setExplosionPower(ghast.getExplosionPower() + extraBlast);
                this.setTarget(caster, ghast);
                if (worldIn.addFreshEntity(ghast)) {
                    this.uponSummon(worldIn, caster, staff, ghast);
                }
                this.summonAdvancement(caster, ghast);
            }
            this.SummonDown(caster);
            worldIn.playSound((Player) null, caster.getX(), caster.getY(), caster.getZ(), ModSounds.SUMMON_SPELL.get(), this.getSoundSource(), 1.0F, 1.0F);
        }
    }
}
