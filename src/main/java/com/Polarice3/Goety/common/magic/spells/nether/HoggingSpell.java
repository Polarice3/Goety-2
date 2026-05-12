package com.Polarice3.Goety.common.magic.spells.nether;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.BurningHoglin;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class HoggingSpell extends SummonSpell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.HoggingCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.HoggingDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.HoggingCoolDown.get();
    }

    public SoundEvent CastingSound(LivingEntity caster) {
        if (CuriosFinder.hasUnholySet(caster)){
            return ModSounds.APOSTLE_PREPARE_SUMMON.get();
        }
        return ModSounds.PREPARE_SUMMON.get();
    }

    @Override
    public int SummonDownDuration() {
        return SpellConfig.HoggingSummonDown.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof BurningHoglin;
    }

    @Override
    public int summonLimit() {
        return SpellConfig.HoggingLimit.get();
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int duration = spellStat.getDuration();
        float radius = (float) spellStat.getRadius();
        if (WandUtil.enchantedFocus(caster)){
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster) / 2.0F;
        }
        BlockPos left = BlockPos.containing(MobUtil.getLeftPos(caster, 2));
        left = BlockFinder.findGroundBelow(worldIn, left);
        BurningHoglin hoglin = new BurningHoglin(ModEntityType.BURNING_HOGLIN.get(), worldIn);
        hoglin.setTrueOwner(caster);
        hoglin.setPos(Vec3.atBottomCenterOf(left));
        if (this.rightStaff(staff)) {
            hoglin.setWindUpTime(10);
        }
        hoglin.setLimitedLife(100 * duration);
        hoglin.setExplosionPower(hoglin.getExplosionPower() + radius);
        hoglin.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
        this.setTarget(caster, hoglin);
        if (worldIn.addFreshEntity(hoglin)) {
            this.uponSummon(worldIn, caster, staff, hoglin);
        }
        this.summonAdvancement(caster, hoglin);
        if (this.rightStaff(staff)) {
            BlockPos right = BlockPos.containing(MobUtil.getRightPos(caster, 2));
            right = BlockFinder.findGroundBelow(worldIn, right);
            BurningHoglin hoglin2 = new BurningHoglin(ModEntityType.BURNING_HOGLIN.get(), worldIn);
            hoglin2.setTrueOwner(caster);
            hoglin2.setPos(Vec3.atBottomCenterOf(right));
            hoglin2.setWindUpTime(10);
            hoglin2.setLimitedLife(100 * duration);
            hoglin2.setExplosionPower(hoglin2.getExplosionPower() + radius);
            hoglin2.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
            this.setTarget(caster, hoglin2);
            if (worldIn.addFreshEntity(hoglin2)) {
                this.uponSummon(worldIn, caster, staff, hoglin2);
            }
        }
        this.SummonDown(caster);
        worldIn.playSound((Player) null, caster.getX(), caster.getY(), caster.getZ(), ModSounds.SUMMON_SPELL.get(), this.getSoundSource(), 1.0F, 1.0F);
    }
}
