package com.Polarice3.Goety.common.magic.spells.geomancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.Volcano;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class EruptionSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setDuration(1);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.EruptionCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.EruptionDuration.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.GEOMANCY;
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.EruptionCoolDown.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.RADIUS.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.BURNING.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int range = spellStat.getRange();
        int radius = (int) spellStat.getRadius();
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        int burning = spellStat.getBurning();
        if (WandUtil.enchantedFocus(caster)) {
            range += WandUtil.getRangeLevel(caster);
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
            burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), caster);
        }
        HitResult rayTraceResult = this.rayTrace(worldIn, caster, range, 3);
        LivingEntity target = this.getTarget(caster, range);
        BlockPos blockPos = caster.blockPosition();
        if (target != null){
            blockPos = target.blockPosition();
        } else if (rayTraceResult instanceof BlockHitResult blockHitResult){
            blockPos = blockHitResult.getBlockPos().above();
        }
        Volcano volcano = ModEntityType.VOLCANO.get().create(worldIn);
        if (volcano != null){
            volcano.setTrueOwner(caster);
            volcano.setPos(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5D);
            volcano.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, null, null);
            volcano.setExplosionPower(volcano.getExplosionPower() + (radius / 2.0F));
            volcano.setLifeSpan(6 * duration);
            volcano.setPotency(potency);
            volcano.setFlaming(burning);
            worldIn.addFreshEntity(volcano);
        }
        if (this.rightStaff(staff)) {
            for (int i = 0; i < 2; ++i) {
                blockPos = blockPos.offset(-4 + worldIn.random.nextInt(8), 0, -4 + worldIn.random.nextInt(8));
                Vec3 vec3 = Vec3.atBottomCenterOf(blockPos);
                Volcano volcano2 = new Volcano(ModEntityType.VOLCANO.get(), worldIn);
                volcano2.setTrueOwner(caster);
                volcano2.setPos(vec3.x(), vec3.y(), vec3.z());
                volcano2.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, null, null);
                volcano2.setExplosionPower(volcano2.getExplosionPower() + (radius / 2.0F));
                volcano2.setLifeSpan(6 * duration);
                volcano2.setPotency(potency);
                volcano2.setFlaming(burning);
                worldIn.addFreshEntity(volcano2);
            }
        }
    }
}
