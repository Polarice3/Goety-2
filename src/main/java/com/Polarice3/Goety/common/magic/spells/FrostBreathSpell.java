package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.BreathingSpells;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FrostBreathSpell extends BreathingSpells {
    public float damage = SpellConfig.FrostBreathDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();

    @Override
    public int SoulCost() {
        return SpellConfig.FrostBreathCost.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.SNOW_GOLEM_AMBIENT;
    }

    @Override
    public SpellType getSpellType(){
        return SpellType.FROST;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        float enchantment = 0;
        int duration = 1;
        int range = 0;
        if (entityLiving instanceof Player player){
            if (WandUtil.enchantedFocus(player)){
                enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), player);
                duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), player);
                range = WandUtil.getLevels(ModEnchantments.RANGE.get(), player);
            }
        }
        float damage = this.damage + enchantment;
        if (!worldIn.isClientSide) {
            for (Entity target : getTarget(entityLiving, range + 15)) {
                if (target != null) {
                    if (target instanceof LivingEntity livingTarget) {
                        if (target.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)){
                            damage *= 2.0F;
                        }
                        if (livingTarget.hurt(ModDamageSource.frostBreath(entityLiving, entityLiving), damage)) {
                            livingTarget.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), MathHelper.secondsToTicks(1) * duration));
                        }
                    }
                }
            }
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.PLAYER_BREATH, this.getSoundSource(), worldIn.random.nextFloat() * 0.5F, worldIn.random.nextFloat() * 0.5F);
    }

    @Override
    public ParticleOptions getParticle() {
        return ParticleTypes.POOF;
    }

    @Override
    public void showWandBreath(LivingEntity entityLiving) {
        int range = 0;
        if (entityLiving instanceof Player player){
            if (WandUtil.enchantedFocus(player)){
                range = WandUtil.getLevels(ModEnchantments.RANGE.get(), player);
            }
        }
        this.breathAttack(entityLiving, 0.3F + ((double) range / 10), 5);
    }

    @Override
    public void showStaffBreath(LivingEntity entityLiving) {
    }
}
