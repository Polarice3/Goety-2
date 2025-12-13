package com.Polarice3.Goety.common.magic.spells.abyss;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.TidalSurge;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TidalSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.TidalCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.TidalDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.ABYSS_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.TidalCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ABYSS;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        float potency = spellStat.getPotency();
        int range = spellStat.getRange();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster) / 2.0F;
            range += WandUtil.getRangeLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
        }
        HitResult hitResult = this.rayTrace(worldIn, caster, range, 3);
        Entity target = this.getTarget(caster, range);
        int lifespan = 0;
        double y = 0;
        Vec3 vec3 = null;
        if (target != null){
            lifespan = (int) (Math.floor(caster.distanceTo(target))) + 10;
            vec3 = target.position().subtract(caster.position());
            y = target.getY();
        } else if (hitResult instanceof BlockHitResult result) {
            BlockPos blockPos = result.getBlockPos();
            Vec3 vec30 = Vec3.atCenterOf(blockPos);
            lifespan = (int) (Math.floor(Mth.sqrt((float) caster.distanceToSqr(vec30)))) + 10;
            vec3 = vec30.subtract(caster.position());
            y = blockPos.getY() + 1.0D;
        }
        int h = this.rightStaff(staff) ? 1 : 0;
        if (vec3 != null) {
            for (int i = -h; i <= h; i++) {
                TidalSurge tidalSurge = new TidalSurge(worldIn, caster);
                tidalSurge.setPos(caster.getX(), y, caster.getZ());
                tidalSurge.setLifespan(lifespan + duration);
                tidalSurge.setWaveScale(tidalSurge.getWaveScale() + (potency / 2.0F));
                tidalSurge.setYRot(-(float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)) + (i * 10));
                worldIn.addFreshEntity(tidalSurge);
            }
        }
        worldIn.playSound(null, caster.getX(), caster.getY(), caster.getZ(), SoundEvents.PLAYER_SPLASH_HIGH_SPEED, this.getSoundSource(), 1.0F, 1.0F);
    }
}
