package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.SwordProjectile;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SwordSpell extends Spell {
    Predicate<ItemStack> predicate = item -> item.getItem() instanceof SwordItem || item.is(ModTags.Items.MAGIC_SWORD_SHOOTABLE);

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setVelocity(1.6F);
    }

    public int defaultSoulCost() {
        return SpellConfig.SwordCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.SwordDuration.get();
    }

    public SoundEvent CastingSound() {
        return SoundEvents.DROWNED_SHOOT;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.SwordCoolDown.get();
    }

    public SpellType getSpellType() {
        return SpellType.ILL;
    }

    public boolean conditionsMet(ServerLevel worldIn, LivingEntity caster){
        return predicate.test(caster.getMainHandItem()) || predicate.test(caster.getOffhandItem());
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        float velocity = spellStat.getVelocity();
        if (WandUtil.enchantedFocus(caster)) {
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster) / 3.0F;
        }
        if (predicate.test(caster.getMainHandItem()) || predicate.test(caster.getOffhandItem())) {
            ItemStack sword = predicate.test(caster.getMainHandItem()) ? caster.getMainHandItem() : caster.getOffhandItem();
            SwordProjectile swordProjectile = new SwordProjectile(caster, worldIn, sword);
            swordProjectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            swordProjectile.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, velocity, 1.0F);
            swordProjectile.setOwner(caster);
            if (worldIn.addFreshEntity(swordProjectile) && MobUtil.validEntity(caster)){
                ItemHelper.hurtAndBreak(sword, 10, caster);
            }
            this.playSound(worldIn, caster, 1.0F, 1.0F);
        } else {
            this.playSound(worldIn, caster, SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F);
        }
    }
}
