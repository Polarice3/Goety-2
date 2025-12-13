package com.Polarice3.Goety.common.magic.spells.abyss;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.BouncyBubble;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BouncyBubbleSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.BouncyBubbleCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.BouncyBubbleDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.GENERIC_SPLASH;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.BouncyBubbleCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ABYSS;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.VELOCITY.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        int velocity = (int) spellStat.getVelocity();
        int radius = (int) spellStat.getRadius();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
        }
        Vec3 vector3d = caster.getLookAngle();
        BouncyBubble bouncyBubble = new BouncyBubble(
                caster.getX() + vector3d.x / 2,
                caster.getEyeY() - 0.2,
                caster.getZ() + vector3d.z / 2,
                vector3d.x,
                vector3d.y,
                vector3d.z, worldIn);
        bouncyBubble.shoot(vector3d);
        bouncyBubble.setOwner(caster);
        bouncyBubble.setExtraDamage(potency);
        bouncyBubble.setBoltSpeed(velocity);
        bouncyBubble.setSize(radius);
        worldIn.addFreshEntity(bouncyBubble);
        if (rightStaff(staff)) {
            for (int i = 0; i < 2; ++i) {
                BouncyBubble bouncyBubble2 = new BouncyBubble(
                        caster.getX() + vector3d.x / 2 + worldIn.random.nextGaussian(),
                        caster.getEyeY() - 0.2,
                        caster.getZ() + vector3d.z / 2 + worldIn.random.nextGaussian(),
                        vector3d.x,
                        vector3d.y,
                        vector3d.z, worldIn);
                bouncyBubble2.shoot(vector3d);
                bouncyBubble2.setOwner(caster);
                bouncyBubble2.setExtraDamage(potency);
                bouncyBubble2.setBoltSpeed(velocity);
                bouncyBubble2.setSize(radius);
                worldIn.addFreshEntity(bouncyBubble2);
            }
        }
    }
}
