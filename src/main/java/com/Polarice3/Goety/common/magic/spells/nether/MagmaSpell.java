package com.Polarice3.Goety.common.magic.spells.nether;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.MagmaBomb;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MagmaSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRadius(3.0D).setVelocity(1.5F);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.MagmaBombCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.MagmaBombDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.MagmaBombCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.RADIUS.get());
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        float radius = (float) spellStat.getRadius();
        float velocity = spellStat.getVelocity() + WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster) / 3.0F;
        float extraBlast = WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster) / 2.0F;
        if (this.rightStaff(staff)){
            radius += 1.0F;
        }
        MagmaBomb blast = new MagmaBomb(caster, worldIn);
        blast.setExtraDamage(spellStat.getPotency() + WandUtil.getPotencyLevel(caster));
        blast.setExplosionPower(extraBlast + radius);
        blast.setDuration(spellStat.getDuration() + WandUtil.getLevels(ModEnchantments.DURATION.get(), caster));
        blast.setStaff(this.rightStaff(staff));
        blast.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, velocity, 1.0F);
        worldIn.addFreshEntity(blast);

        this.playSound(worldIn, caster, ModSounds.CAST_SPELL.get());
    }
}
