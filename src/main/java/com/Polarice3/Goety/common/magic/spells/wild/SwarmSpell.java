package com.Polarice3.Goety.common.magic.spells.wild;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.neutral.InsectSwarm;
import com.Polarice3.Goety.common.magic.BreathingSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SwarmSpell extends BreathingSpell {
    public float damage = SpellConfig.SwarmDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();

    @Override
    public int defaultSoulCost() {
        return SpellConfig.SwarmCost.get();
    }

    @Override
    public int defaultCastUp() {
        return SpellConfig.SwarmChargeUp.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return null;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WILD;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        int enchantment = 0;
        int duration = 1;
        int range = 0;
        if (WandUtil.enchantedFocus(entityLiving)) {
            enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving);
            range = WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
        }
        float damage = this.damage + enchantment;
        if (!worldIn.isClientSide) {
            for (Entity target : getBreathTarget(entityLiving, range + 15)) {
                if (target != null) {
                    if (target.hurt(ModDamageSource.swarm(entityLiving, entityLiving), damage)){
                        if (rightStaff(staff)) {
                            if (target instanceof LivingEntity livingTarget) {
                                MobEffect mobEffect = MobEffects.POISON;
                                if (CuriosFinder.hasWildRobe(entityLiving)) {
                                    mobEffect = GoetyEffects.ACID_VENOM.get();
                                }
                                livingTarget.addEffect(new MobEffectInstance(mobEffect, MathHelper.secondsToTicks(5) * duration));
                            }
                        }
                        if (!target.isAlive()){
                            InsectSwarm insectSwarm = new InsectSwarm(worldIn, entityLiving, target.position());
                            insectSwarm.setLimitedLife(200 * duration);
                            if (enchantment > 0){
                                insectSwarm.addEffect(new MobEffectInstance(GoetyEffects.BUFF.get(), EffectsUtil.infiniteEffect(), enchantment - 1, false, false));
                            }
                            worldIn.addFreshEntity(insectSwarm);
                        }
                    }
                }
            }
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.INSECT_SWARM.get(), this.getSoundSource(), worldIn.random.nextFloat(), 1.0F + worldIn.random.nextFloat());
    }

    @Override
    public void showWandBreath(LivingEntity entityLiving) {
        int range = 0;
        if (entityLiving instanceof Player player){
            if (WandUtil.enchantedFocus(player)){
                range = WandUtil.getLevels(ModEnchantments.RANGE.get(), player);
            }
        }
        this.breathAttack(ModParticleTypes.FLY.get(), entityLiving, true, 0.3F + ((double) range / 10), 5);
    }
}
