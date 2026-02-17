package com.Polarice3.Goety.common.magic.spells.abyss;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.BioMine;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BioMineSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.BiomineCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.BiomineDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.ABYSS_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.BiomineCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ABYSS;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int range = spellStat.getRange();
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        double radius = spellStat.getRadius();
        if (WandUtil.enchantedFocus(caster)) {
            range += WandUtil.getRangeLevel(caster);
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
        }
        LivingEntity target = this.getTarget(caster, range);

        int i = this.rightStaff(staff) ? 8 : 4;

        for (int j = 0; j < i; ++j){
            Vec3 vec3 = Vec3.atBottomCenterOf(BlockFinder.SummonWaterAwayRadius(caster.blockPosition(), caster, worldIn, 16, 8));
            if (target != null) {
                vec3 = Vec3.atBottomCenterOf(BlockFinder.SummonWaterAwayRadius(target.blockPosition(), target, worldIn, 16, 8));
            }
            BioMine bioMine = new BioMine(ModEntityType.BIOMINE.get(), worldIn);
            bioMine.setPos(vec3);
            bioMine.setOwner(caster);
            bioMine.setExtraRadius((float) radius);
            bioMine.setExtraDamage(potency);
            bioMine.setLifeTicks(bioMine.getLifeTicks() + MathHelper.secondsToTicks(duration));
            bioMine.setExtraDuration(MathHelper.secondsToTicks(duration));
            if (worldIn.addFreshEntity(bioMine)){
                ColorUtil colorUtil = new ColorUtil(0xffffff);
                ServerParticleUtil.summonedParticles(worldIn, bioMine, colorUtil, 0xffffff, 0xffffff);
                worldIn.playSound(null, vec3.x, vec3.y, vec3.z, ModSounds.BIOMINE_SPAWN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }
}
