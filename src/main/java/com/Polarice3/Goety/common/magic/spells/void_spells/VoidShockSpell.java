package com.Polarice3.Goety.common.magic.spells.void_spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.VoidShock;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VoidShockSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.VoidShockCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.VoidShockDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.VOID_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.VoidShockCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.VOID;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public void useSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int castTime, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
        }
        int tickRate = this.rightStaff(staff) ? 2 : 5;
        if (castTime >= 10 && castTime % tickRate == 0) {
            VoidShock voidShock = new VoidShock(caster, this.getTarget(caster), worldIn);
            voidShock.setPos(caster.position().add(worldIn.getRandom().nextInt(-3, 3), 4.0D, worldIn.getRandom().nextInt(-3, 3)));
            voidShock.setPower(Vec3.ZERO, 10);
            voidShock.setExtraDamage(potency);
            worldIn.addFreshEntity(voidShock);
            this.playSound(worldIn, voidShock, ModSounds.TELEPORT_ORB_THROW.get(), 2.0F, caster.getVoicePitch());
        }
        super.useSpell(worldIn, caster, staff, castTime, spellStat);
    }
}
