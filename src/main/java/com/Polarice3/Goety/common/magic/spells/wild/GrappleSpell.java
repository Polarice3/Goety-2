package com.Polarice3.Goety.common.magic.spells.wild;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.VineHook;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GrappleSpell extends Spell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.GrappleCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.GrappleDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.WILD_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.GrappleCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WILD;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        float velocity = 0;
        if (WandUtil.enchantedFocus(entityLiving)) {
            velocity = WandUtil.getLevels(ModEnchantments.VELOCITY.get(), entityLiving) / 2.0F;
        }
        if (entityLiving instanceof Player player){
            Projectile projectile = SEHelper.getGrappling(player);
            if (projectile != null) {
                projectile.discard();
                SEHelper.setGrappling(player, null);
                worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, this.getSoundSource(), 1.0F, 0.4F / (worldIn.getRandom().nextFloat() * 0.4F + 0.8F));
            } else {
                VineHook vineHook = new VineHook(worldIn, player, 2.5F + velocity);
                vineHook.setStaff(rightStaff(staff));
                worldIn.addFreshEntity(vineHook);
                worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_THROW, this.getSoundSource(), 0.5F, 0.4F / (worldIn.getRandom().nextFloat() * 0.4F + 0.8F));
            }
        }
    }
}
