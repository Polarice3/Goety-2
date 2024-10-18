package com.Polarice3.Goety.common.magic.spells.necromancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.ChargingSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class LifeStealSpell extends ChargingSpell {

    @Override
    public int Cooldown() {
        return 0;
    }

    @Override
    public int defaultSoulCost() {
        return 0;
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return null;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NECROMANCY;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        float potency = 1.0F;
        int range = 16;
        if (WandUtil.enchantedFocus(entityLiving)){
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving) / 2.0F;
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
        }
        LivingEntity livingEntity = this.getTarget(entityLiving, range);
        if (livingEntity != null){
            if (livingEntity.hurt(ModDamageSource.soulLeech(entityLiving, entityLiving), potency)) {
                if (this.rightStaff(staff)){
                    if (entityLiving instanceof Player player){
                        int souls = SEHelper.getSoulGiven(livingEntity) < 5 ? SEHelper.getSoulGiven(livingEntity) : SEHelper.getSoulGiven(livingEntity) /5;
                        SEHelper.increaseSouls(player, souls);
                    }
                }
                worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.SOUL_EAT.get(), this.getSoundSource(), 1.0F, 1.0F);
            }
        }
    }
}
