package com.Polarice3.Goety.common.magic.spells.geomancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.Volcano;
import com.Polarice3.Goety.common.magic.Spell;
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

import java.util.ArrayList;
import java.util.List;

public class EruptionSpell extends Spell {
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
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        int range = 16;
        int radius = 0;
        int potency = 0;
        int duration = 1;
        int burning = 0;
        if (WandUtil.enchantedFocus(entityLiving)) {
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), entityLiving);
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving);
            burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), entityLiving);
        }
        HitResult rayTraceResult = this.rayTrace(worldIn, entityLiving, range, 3);
        LivingEntity target = this.getTarget(entityLiving, range);
        BlockPos blockPos = entityLiving.blockPosition();
        if (target != null){
            blockPos = target.blockPosition();
        } else if (rayTraceResult instanceof BlockHitResult blockHitResult){
            blockPos = blockHitResult.getBlockPos().above();
        }
        Volcano volcano = ModEntityType.VOLCANO.get().create(worldIn);
        if (volcano != null){
            volcano.setTrueOwner(entityLiving);
            volcano.setPos(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5D);
            volcano.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, null, null);
            volcano.setExplosionPower(volcano.getExplosionPower() + (radius / 2.0F));
            volcano.setLifeSpan(6 * duration);
            volcano.setPotency(potency);
            volcano.setFlaming(burning);
            worldIn.addFreshEntity(volcano);
        }
    }
}
