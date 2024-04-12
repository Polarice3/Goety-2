package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.ChargingSpell;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class FeastSpell extends ChargingSpell {

    public int defaultSoulCost() {
        return SpellConfig.FeastCost.get();
    }

    @Override
    public int Cooldown() {
        return SpellConfig.FeastDuration.get();
    }

    public SoundEvent CastingSound() {
        return SoundEvents.EVOKER_PREPARE_ATTACK;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ILL;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RADIUS.get());
        list.add(ModEnchantments.BURNING.get());
        list.add(ModEnchantments.ABSORB.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        int i = (int) entityLiving.getX();
        int j = (int) entityLiving.getY();
        int k = (int) entityLiving.getZ();
        double radius = 16.0D;
        if (WandUtil.enchantedFocus(entityLiving)){
            radius *= (WandUtil.getLevels(ModEnchantments.RADIUS.get(), entityLiving) / 2.0D) + 1.0D;
        }
        for (LivingEntity entity : worldIn.getEntitiesOfClass(LivingEntity.class, (new AABB(i, j, k, i, j - 4, k)).inflate(radius))) {
            float f = (float) Mth.atan2(entity.getZ() - entityLiving.getZ(), entity.getX() - entityLiving.getX());
            if (entity != entityLiving && !MobUtil.areAllies(entity, entityLiving)){
                WandUtil.spawnFangs(entityLiving, entity.getX(), entity.getZ(), entity.getY(), entity.getY() + 1.0D, f, 1);
                if (rightStaff(staff)) {
                    for (int i1 = 0; i1 < 5; ++i1) {
                        float f1 = f + (float) i1 * (float) Math.PI * 0.4F;
                        WandUtil.spawnFangs(entityLiving, entity.getX() + (double) Mth.cos(f1) * 1.5D, entity.getZ() + (double) Mth.sin(f1) * 1.5D, entity.getY(), entity.getY() + 1.0D, f1, 0);
                    }
                }
            }
        }
        for(int i1 = 0; i1 < entityLiving.level.random.nextInt(35) + 10; ++i1) {
            worldIn.sendParticles(ParticleTypes.POOF, entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
        }
    }

}
