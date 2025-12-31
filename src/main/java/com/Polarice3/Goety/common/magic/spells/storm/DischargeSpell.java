package com.Polarice3.Goety.common.magic.spells.storm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.ShockwaveParticleOption;
import com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningPacket;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class DischargeSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRadius(3.0D);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.DischargeCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.DischargeDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.DischargeCoolDown.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.STORM;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        int radius = (int) spellStat.getRadius();
        float potency = spellStat.getPotency();
        float damage = SpellConfig.DischargeDamage.get().floatValue() * WandUtil.damageMultiply();
        float maxDamage = SpellConfig.DischargeMaxDamage.get().floatValue() * WandUtil.damageMultiply();
        if (WandUtil.enchantedFocus(caster)){
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
            potency += WandUtil.getPotencyLevel(caster) / 2.0F;
        }
        damage += potency;
        maxDamage += potency;
        ColorUtil colorUtil = new ColorUtil(0xfef597);
        if (staff.is(ModItems.NAMELESS_STAFF.get())) {
            colorUtil = new ColorUtil(0xa7fc3e);
        }
        worldIn.sendParticles(new ShockwaveParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue()), caster.getX(), caster.getY() + 0.5F, caster.getZ(), 0, 0, 0, 0, 0);
        worldIn.sendParticles(new VerticalCircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), radius, 1), caster.getX(), caster.getY() + 0.5F, caster.getZ(), 1, 0, 0, 0, 0);
        for (int i = 0; i < 16; ++i) {
            Vec3 vec3 = caster.position();
            int random1 = worldIn.getRandom().nextIntBetweenInclusive(-4, 4);
            int random2 = worldIn.getRandom().nextIntBetweenInclusive(-4, 4);
            Vec3 vec31 = vec3.add(worldIn.getRandom().nextDouble() * random1, worldIn.getRandom().nextDouble(), worldIn.getRandom().nextDouble() * random2);
            ModNetwork.sendToALL(new SLightningPacket(vec3, vec31, colorUtil, 12));
        }
        float trueDamage = Mth.clamp(damage + RandomUtil.nextInt(worldIn.getRandom(), (int) (maxDamage - damage)), damage, maxDamage);
        new SpellExplosion(worldIn, caster, ModDamageSource.directShock(caster), caster.blockPosition(), radius, trueDamage){
            @Override
            public void explodeHurt(Entity target, DamageSource damageSource, double x, double y, double z, double seen, float actualDamage) {
                if (target instanceof LivingEntity target1){
                    super.explodeHurt(target, damageSource, x, y, z, seen, actualDamage);
                    float chance = rightStaff(staff) ? 0.25F : 0.05F;
                    float chainDamage = actualDamage / 2.0F;
                    if (worldIn.isThundering() && worldIn.isRainingAt(target1.blockPosition())){
                        chance += 0.25F;
                        chainDamage = actualDamage;
                    }
                    if (worldIn.random.nextFloat() <= chance){
                        target1.addEffect(new MobEffectInstance(GoetyEffects.SPASMS.get(), MathHelper.secondsToTicks(5)));
                    }
                    if (rightStaff(staff)){
                        WandUtil.chainLightning(target1, caster, 6.0D, chainDamage);
                    }
                }
            }
        };
        this.playSound(worldIn, caster, ModSounds.REDSTONE_EXPLODE.get(), 2.0F, 1.0F);
    }
}
