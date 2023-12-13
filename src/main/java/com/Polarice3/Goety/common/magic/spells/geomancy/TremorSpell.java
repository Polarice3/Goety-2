package com.Polarice3.Goety.common.magic.spells.geomancy;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.common.magic.Tremor;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import com.google.common.collect.Lists;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TremorSpell extends Spells {

    @Override
    public int SoulCost() {
        return 0;
    }

    @Override
    public int CastDuration() {
        return 20;
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.RUMBLE.get();
    }

    @Override
    public int SpellCooldown() {
        return 0;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.GEOMANCY;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        List<Tremor> list = Lists.newArrayList();
        int range = 8;
        int damage = 0;
        if (WandUtil.enchantedFocus(entityLiving)) {
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
            damage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
        }
        Vec3 vec = Vec3.directionFromRotation(0.0F, entityLiving.getYRot());
        int x = Mth.floor(entityLiving.getX() + vec.x * 1.5);
        int y = Mth.floor(entityLiving.getBoundingBox().minY);
        int z = Mth.floor(entityLiving.getZ() + vec.z * 1.5);
        int x1 = Mth.floor(entityLiving.getX() + vec.x * range);
        int z1 = Mth.floor(entityLiving.getZ() + vec.z * range);
        Tremor.createTremors(worldIn, entityLiving, list, damage, x, z, x1, z1, y);
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.WALL_ERUPT.get(), this.getSoundSource(), 0.8F, 0.8F + worldIn.random.nextFloat() * 0.4F);
    }
}
