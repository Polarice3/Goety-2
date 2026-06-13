package com.Polarice3.Goety.common.magic.spells.geomancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.SmackStone;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class SmackStoneSpell extends Spell {
    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setVelocity(1.6F);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.SmackStoneCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.SmackStoneDuration.get();
    }

    @Override
    public SoundEvent CastingSound(LivingEntity caster) {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.SmackStoneCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.GEOMANCY;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.VELOCITY.get());
        list.add(ModEnchantments.BURNING.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        float velocity = spellStat.getVelocity();
        int potency = spellStat.getPotency();
        int burning = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster) / 3.0F;
            potency += WandUtil.getPotencyLevel(caster);
            burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), caster);
        }
        SmackStone smackStone = new SmackStone(caster, worldIn);
        smackStone.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, velocity, 1.0F);
        smackStone.setOwner(caster);
        smackStone.setExtraDamage(potency);
        smackStone.setFiery(burning);
        worldIn.addFreshEntity(smackStone);
        if (rightStaff(staff)) {
            for (int i = 0; i < 2; ++i) {
                SmackStone smackStone1 = new SmackStone(caster, worldIn);
                smackStone1.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, velocity, 16.0F);
                smackStone1.setOwner(caster);
                smackStone1.setExtraDamage(potency);
                smackStone1.setFiery(burning);
                worldIn.addFreshEntity(smackStone1);
            }
        }
        this.playSound(worldIn, caster, SoundEvents.BASALT_PLACE, 1.0F, this.projPitch(worldIn.getRandom()));
    }
}
