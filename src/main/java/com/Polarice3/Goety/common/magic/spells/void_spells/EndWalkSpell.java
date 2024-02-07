package com.Polarice3.Goety.common.magic.spells.void_spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EndWalkSpell extends Spells {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.EndWalkCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.EndWalkDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.EndWalkCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.VOID;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        int enchantment = 0;
        int duration = 0;
        if (WandUtil.enchantedFocus(entityLiving)){
            enchantment += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            duration = MathHelper.secondsToTicks(5) * WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving);
        }
        if (entityLiving instanceof Player player){
            player.addEffect(new MobEffectInstance(GoetyEffects.SHADOW_WALK.get(), SpellConfig.EndWalkEffectDuration.get() + duration, enchantment, false, false, true));
            for(int i = 0; i < 16; ++i) {
                double d0 = MathHelper.rgbToSpeed(96.0D);
                double d1 = MathHelper.rgbToSpeed(62.0D);
                double d2 = MathHelper.rgbToSpeed(92.0D);
                worldIn.sendParticles(ModParticleTypes.CULT_SPELL.get(), entityLiving.getRandomX(1.0D), entityLiving.getRandomY(), entityLiving.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
            }
            SEHelper.setEndWalk(player, player.blockPosition(), player.level.dimension());
            ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(ModSounds.END_WALK.get(), 1.0F, 1.0F));
        }
    }
}
