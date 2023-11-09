package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.BreathingSpells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FireBreathSpell extends BreathingSpells {
    public float damage = SpellConfig.FireBreathDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();

    @Override
    public int SoulCost() {
        return SpellConfig.FireBreathCost.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.FIRE_BREATH.get();
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        float enchantment = 0;
        int burning = 1;
        int range = 0;
        if (entityLiving instanceof Player player) {
            if (WandUtil.enchantedFocus(player)) {
                enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), player);
                burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), player);
                range = WandUtil.getLevels(ModEnchantments.RANGE.get(), player);
            }
        }
        if (!worldIn.isClientSide) {
            for (Entity target : getTarget(entityLiving, range + 15)) {
                if (target != null) {
                    if (!target.fireImmune()) {
                        if (target.hurt(ModDamageSource.fireBreath(entityLiving, entityLiving), damage + enchantment)){
                            target.setSecondsOnFire(5 * burning);
                        }
                    }
                }
            }
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.FIRE_BREATH.get(), this.getSoundSource(), 0.33F, 1.0F);
    }

    @Override
    public ParticleOptions getParticle() {
        return ParticleTypes.SOUL_FIRE_FLAME;
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
