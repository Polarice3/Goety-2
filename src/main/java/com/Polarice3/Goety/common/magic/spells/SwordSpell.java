package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.entities.projectiles.SwordProjectile;
import com.Polarice3.Goety.common.magic.InstantCastSpells;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

public class SwordSpell extends InstantCastSpells {

    public int SoulCost() {
        return SpellConfig.SwordCost.get();
    }

    public SoundEvent CastingSound() {
        return SoundEvents.DROWNED_SHOOT;
    }

    public SpellType getSpellType() {
        return SpellType.ILL;
    }

    @Override
    public void RegularResult(ServerLevel worldIn, LivingEntity entityLiving) {
        if (entityLiving.getMainHandItem().getItem() instanceof SwordItem || entityLiving.getOffhandItem().getItem() instanceof SwordItem) {
            ItemStack sword = entityLiving.getMainHandItem().getItem() instanceof SwordItem ? entityLiving.getMainHandItem() : entityLiving.getOffhandItem();
            SwordProjectile swordProjectile = new SwordProjectile(entityLiving, worldIn, sword);
            swordProjectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            swordProjectile.shootFromRotation(entityLiving, entityLiving.getXRot(), entityLiving.getYRot(), 0.0F, 1.6F, 1.0F);
            swordProjectile.setOwner(entityLiving);
            if (worldIn.addFreshEntity(swordProjectile) && MobUtil.validEntity(entityLiving)){
                ItemHelper.hurtAndBreak(sword, 5, entityLiving);
            }
            worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), CastingSound(), SoundSource.PLAYERS, 1.0F, 1.0F);
        } else {
            if (entityLiving instanceof Player player) {
                SEHelper.increaseSouls(player, this.SoulCost());
            }
            worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
    }
}
