package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.EverChargeSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WardingSpell extends EverChargeSpell {

    @Override
    public int defaultSoulCost() {
        return 0;
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        int potency = 0;
        if (WandUtil.enchantedFocus(entityLiving)){
            potency = WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
        }
        if (entityLiving instanceof Player player){
            SEHelper.setMaxWarding(player, 20);
            if (player.tickCount % 5 == 0) {
                SEHelper.increaseWarding(player, 1 + potency);
            }
            ServerParticleUtil.addParticlesAroundSelf(worldIn, ParticleTypes.ENCHANT, player);        }
    }
}
