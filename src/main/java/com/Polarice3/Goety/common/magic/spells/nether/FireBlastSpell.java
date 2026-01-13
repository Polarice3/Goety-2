package com.Polarice3.Goety.common.magic.spells.nether;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.ShockwaveParticleOption;
import com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class FireBlastSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRadius(3.0D);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.FireBlastCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.FireBlastDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.FireBlastCoolDown.get();
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
        list.add(ModEnchantments.RADIUS.get());
        list.add(ModEnchantments.BURNING.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        float radius = (float) spellStat.getRadius();
        float damage = SpellConfig.FireBlastDamage.get().floatValue() * WandUtil.damageMultiply();
        float maxDamage = SpellConfig.FireBlastMaxDamage.get().floatValue() * WandUtil.damageMultiply();
        int burning = spellStat.getBurning();
        if (WandUtil.enchantedFocus(caster)){
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
            damage += WandUtil.getPotencyLevel(caster) / 2.0F;
            maxDamage += WandUtil.getPotencyLevel(caster) / 2.0F;
            burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), caster);
        }
        damage += spellStat.getPotency();
        maxDamage += spellStat.getPotency();
        ColorUtil colorUtil = new ColorUtil(0xdd9c16);
        worldIn.sendParticles(new ShockwaveParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue()), caster.getX(), caster.getY() + 0.5F, caster.getZ(), 0, 0, 0, 0, 0);
        worldIn.sendParticles(new VerticalCircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), radius, 1), caster.getX(), caster.getY() + 0.5F, caster.getZ(), 1, 0, 0, 0, 0);
        float trueDamage = Mth.clamp(damage + RandomUtil.nextInt(worldIn.getRandom(), (int) (maxDamage - damage)), damage, maxDamage);

        DamageSource damageSource = ModDamageSource.fireBreath(caster, caster);
        if (CuriosFinder.hasNetherRobe(caster)){
            damageSource = ModDamageSource.magicFireBreath(caster, caster);
        }
        if (MobUtil.getOwner(caster) != null){
            if (CuriosFinder.hasNetherRobe(MobUtil.getOwner(caster))){
                damageSource = ModDamageSource.magicFireBreath(caster, caster);
            }
        }
        if (CuriosFinder.hasUnholySet(caster)){
            damageSource = ModDamageSource.hellfire(caster, caster);
        }

        int finalBurning = burning;
        float increase = 0.0F;
        if (rightStaff(staff)) {
            increase = 0.5F;
        }
        new SpellExplosion(worldIn, caster, damageSource, caster.blockPosition(), radius + increase, trueDamage){
            @Override
            public void explodeHurt(Entity target, DamageSource damageSource, double x, double y, double z, double seen, float actualDamage) {
                if (target instanceof LivingEntity target1){
                    super.explodeHurt(target, damageSource, x, y, z, seen, actualDamage);
                    if (!target.fireImmune()) {
                        int i = finalBurning + 1;
                        target1.setSecondsOnFire(5 * i);
                    }
                }
            }
        };
        this.playSound(worldIn, caster, SoundEvents.GENERIC_EXPLODE, 2.0F, 1.0F);
        this.playSound(worldIn, caster, ModSounds.HELL_BLAST_IMPACT.get(), 2.0F, 1.0F);
    }
}
