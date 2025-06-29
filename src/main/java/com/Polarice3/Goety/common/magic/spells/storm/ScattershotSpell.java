package com.Polarice3.Goety.common.magic.spells.storm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.MiniElectroOrb;
import com.Polarice3.Goety.common.magic.EverChargeSpell;
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

import java.util.ArrayList;
import java.util.List;

public class ScattershotSpell extends EverChargeSpell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.BombardmentCost.get();
    }

    @Override
    public int defaultCastUp() {
        return SpellConfig.BombardmentChargeUp.get();
    }

    @Override
    public int Cooldown() {
        return SpellConfig.BombardmentDuration.get();
    }

    @Override
    public int shotsNumber(LivingEntity caster, ItemStack staff) {
        int i = 0;
        if (WandUtil.enchantedFocus(caster)) {
            i += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
        }
        return SpellConfig.BombardmentShots.get() + (i * 3);
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.BombardmentCoolDown.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        int potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
        }
        Vec3 vector3d = caster.getViewVector( 1.0F);
        MiniElectroOrb orb = new MiniElectroOrb(
                caster.getX() + vector3d.x / 2,
                caster.getEyeY() - 0.5F,
                caster.getZ() + vector3d.z / 2,
                vector3d.x,
                vector3d.y,
                vector3d.z,
                worldIn);
        orb.setOwner(caster);
        orb.setExtraDamage(potency);
        orb.setStaff(rightStaff(staff));
        worldIn.addFreshEntity(orb);
        this.playSound(worldIn, caster, ModSounds.SHOCK_CAST.get(), 1.0F, this.projPitch(worldIn.getRandom()));
    }
}
