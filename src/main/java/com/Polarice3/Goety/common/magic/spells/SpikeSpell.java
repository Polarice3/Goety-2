package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class SpikeSpell extends Spell {

    public int defaultSoulCost() {
        return SpellConfig.SpikeCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.SpikeDuration.get();
    }

    public SoundEvent CastingSound() {
        return SoundEvents.EVOKER_PREPARE_SUMMON;
    }

    @Override
    public float castingPitch() {
        return 0.5F;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.SpikeCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ILL;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.BURNING.get());
        if (SpellConfig.SpikeGainSouls.get() > 0){
            list.add(ModEnchantments.SOUL_EATER.get());
        }
        return list;
    }

    @Override
    public ColorUtil particleColors(LivingEntity entityLiving) {
        return new ColorUtil(0.4F, 0.3F, 0.35F);
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        Player playerEntity = (Player) entityLiving;
        int range = 16;
        double radius = 2.0D;
        if (WandUtil.enchantedFocus(entityLiving)){
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
        }
        HitResult rayTraceResult = this.rayTrace(worldIn, playerEntity, range, radius);
        Vec3 vector3d = rayTraceResult.getLocation();
        double d0 = Math.min(vector3d.y, entityLiving.getY());
        double d1 = Math.max(vector3d.y, entityLiving.getY()) + 1.0D;
        float f = (float) Mth.atan2(vector3d.z - entityLiving.getZ(), vector3d.x - entityLiving.getX());
        LivingEntity target = this.getTarget(entityLiving, range);
        if (target != null){
            d0 = Math.min(target.getY(), entityLiving.getY());
            d1 = Math.max(target.getY(), entityLiving.getY()) + 1.0D;
            f = (float) Mth.atan2(target.getZ() - entityLiving.getZ(), target.getX() - entityLiving.getX());
        }
        if (!isShifting(entityLiving)) {
            for(int l = 0; l < range; ++l) {
                double d2 = 1.25D * (double)(l + 1);
                WandUtil.spawnSpikes(entityLiving,entityLiving.getX() + (double)Mth.cos(f) * d2, entityLiving.getZ() + (double)Mth.sin(f) * d2, d0, d1, f, l);
            }
        } else {
            for(int i = 0; i < 5; ++i) {
                float f1 = f + (float)i * (float)Math.PI * 0.4F;
                WandUtil.spawnSpikes(entityLiving,entityLiving.getX() + (double)Mth.cos(f1) * 1.5D, entityLiving.getZ() + (double)Mth.sin(f1) * 1.5D, d0, d1, f1, 0);
            }

            for(int k = 0; k < 8; ++k) {
                float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
                WandUtil.spawnSpikes(entityLiving,entityLiving.getX() + (double)Mth.cos(f2) * 2.5D, entityLiving.getZ() + (double)Mth.sin(f2) * 2.5D, d0, d1, f2, 3);
            }

            if (rightStaff(staff)) {
                for(int k = 0; k < 11; ++k) {
                    float f2 = f + (float)k * (float)Math.PI * 4.0F / 16.0F + 2.5133462F;
                    WandUtil.spawnSpikes(entityLiving,entityLiving.getX() + (double)Mth.cos(f2) * 3.5D, entityLiving.getZ() + (double)Mth.sin(f2) * 3.5D, d0, d1, f2, 6);
                }
            }
        }
        worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.EVOKER_CAST_SPELL, this.getSoundSource(), 1.0F, 0.5F);
    }

}
