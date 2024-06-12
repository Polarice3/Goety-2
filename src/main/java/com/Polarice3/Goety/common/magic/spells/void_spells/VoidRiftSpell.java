package com.Polarice3.Goety.common.magic.spells.void_spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.util.VoidRift;
import com.Polarice3.Goety.common.magic.Spell;
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

public class VoidRiftSpell extends Spell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.RuptureCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.RuptureDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.VOID_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.RuptureCoolDown.get();
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
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public void startSpell(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        int warmUp = this.castDuration(entityLiving) - 10;
        int duration = 300 * (WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving) + 1);
        int range = 16 + WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
        Vec3 vec3 = this.rayTrace(worldIn, entityLiving, range, 3).getLocation();
        VoidRift voidRift = new VoidRift(worldIn, vec3.x, vec3.y, vec3.z);
        voidRift.setOwner(entityLiving);
        voidRift.setDuration(duration);
        voidRift.setWarmUp(warmUp);
        voidRift.setSize(WandUtil.getLevels(ModEnchantments.RADIUS.get(), entityLiving));
        voidRift.setExtraDamage(WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving));
        worldIn.addFreshEntity(voidRift);
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
    }
}
