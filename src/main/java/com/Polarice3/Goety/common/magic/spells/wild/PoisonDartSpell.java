package com.Polarice3.Goety.common.magic.spells.wild;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.PoisonQuill;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class PoisonDartSpell extends Spell {

    public int defaultSoulCost() {
        return SpellConfig.PoisonDartCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.PoisonDartDuration.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.WILD_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.PoisonDartCoolDown.get();
    }

    public SpellType getSpellType() {
        return SpellType.WILD;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        float enchantment = 0;
        int damage = 0;
        int duration = 0;
        if (WandUtil.enchantedFocus(entityLiving)) {
            enchantment = WandUtil.getLevels(ModEnchantments.VELOCITY.get(), entityLiving) / 3.0F;
            damage = WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            duration = WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving);
        }
        PoisonQuill poisonQuill = new PoisonQuill(worldIn, entityLiving);
        poisonQuill.setSpear(rightStaff(staff), damage);
        poisonQuill.shootFromRotation(entityLiving, entityLiving.getXRot(), entityLiving.getYRot(), 0.0F, 1.6F + enchantment, 1.0F);
        poisonQuill.setOwner(entityLiving);
        poisonQuill.setExtraDamage(damage);
        poisonQuill.setDuration(duration);
        if (entityLiving.isUnderWater()){
            poisonQuill.setAqua(true);
        }
        worldIn.addFreshEntity(poisonQuill);
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.POISON_QUILL_VINE_SHOOT.get(), this.getSoundSource(), 1.0F, 1.0F);
    }
}
