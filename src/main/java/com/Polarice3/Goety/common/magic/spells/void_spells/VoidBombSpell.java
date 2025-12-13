package com.Polarice3.Goety.common.magic.spells.void_spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.VoidShockBomb;
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

public class VoidBombSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setVelocity(1.0F);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.VoidBombCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.VoidBombDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.VOID_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.VoidBombCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.VOID;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        float velocity = spellStat.getVelocity();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster);
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster) / 3.0F;
        }
        Vec3 vec3 = caster.position().add(0, caster.getBbHeight() + 1.0F, 0);
        VoidShockBomb bomb = new VoidShockBomb(caster, worldIn);
        bomb.setPos(vec3);
        bomb.setExtraDamage(potency);
        bomb.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, velocity, 1.0F);
        worldIn.addFreshEntity(bomb);
        this.playSound(worldIn, caster, ModSounds.HEAVY_WOOSH.get(), 3.0F, caster.getVoicePitch());
        this.playSound(worldIn, caster, ModSounds.TELEPORT_ORB_THROW.get(), 3.0F, caster.getVoicePitch() - 0.5F);
        if (this.rightStaff(staff)) {
            for (int i = 0; i < 2; ++i){
                VoidShockBomb bomb2 = new VoidShockBomb(caster, worldIn);
                bomb2.setPos(vec3);
                bomb2.setExtraDamage(potency);
                bomb2.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, (velocity / 2.0F) + worldIn.getRandom().nextFloat(), 8.0F);
                worldIn.addFreshEntity(bomb2);
            }
        }
    }
}
