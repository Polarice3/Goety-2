package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.SwordProjectile;
import com.Polarice3.Goety.common.magic.Spells;
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

public class SwordSpell extends Spells {

    public int SoulCost() {
        return SpellConfig.SwordCost.get();
    }

    @Override
    public int CastDuration() {
        return SpellConfig.SwordDuration.get();
    }

    public SoundEvent CastingSound() {
        return SoundEvents.DROWNED_SHOOT;
    }

    @Override
    public int SpellCooldown() {
        return SpellConfig.SwordCoolDown.get();
    }

    public SpellType getSpellType() {
        return SpellType.ILL;
    }

    public boolean conditionsMet(ServerLevel worldIn, LivingEntity entityLiving){
        return entityLiving.getMainHandItem().getItem() instanceof SwordItem || entityLiving.getOffhandItem().getItem() instanceof SwordItem;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        float enchantment = 0;
        if (WandUtil.enchantedFocus(entityLiving)) {
            enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving) / 3.0F;
        }
        if (entityLiving.getMainHandItem().getItem() instanceof SwordItem || entityLiving.getOffhandItem().getItem() instanceof SwordItem) {
            ItemStack sword = entityLiving.getMainHandItem().getItem() instanceof SwordItem ? entityLiving.getMainHandItem() : entityLiving.getOffhandItem();
            SwordProjectile swordProjectile = new SwordProjectile(entityLiving, worldIn, sword);
            swordProjectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            swordProjectile.shootFromRotation(entityLiving, entityLiving.getXRot(), entityLiving.getYRot(), 0.0F, 1.6F + enchantment, 1.0F);
            swordProjectile.setOwner(entityLiving);
            if (worldIn.addFreshEntity(swordProjectile) && MobUtil.validEntity(entityLiving)){
                ItemHelper.hurtAndBreak(sword, 10, entityLiving);
            }
            worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), CastingSound(), this.getSoundSource(), 1.0F, 1.0F);
        } else {
            worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.FIRE_EXTINGUISH, this.getSoundSource(), 1.0F, 1.0F);
        }
    }
}
