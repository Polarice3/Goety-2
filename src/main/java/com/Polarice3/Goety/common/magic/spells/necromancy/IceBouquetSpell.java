package com.Polarice3.Goety.common.magic.spells.necromancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SoundUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IceBouquetSpell extends Spell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.GhostFireCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.GhostFireDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SUMMON.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.GhostFireCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NECROMANCY;
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
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        int range = 16;
        int potency = 0;
        int duration = 0;
        if (WandUtil.enchantedFocus(entityLiving)) {
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving);
        }
        HitResult rayTraceResult = this.rayTrace(worldIn, entityLiving, range, 3);
        LivingEntity target = this.getTarget(entityLiving, range);
        Vec3 vec3 = null;
        if (target != null){
            vec3 = target.position();
        } else if (rayTraceResult instanceof BlockHitResult result){
            vec3 = result.getLocation();
        }
        if (vec3 != null) {
            if (rightStaff(staff)) {
                if (worldIn.random.nextFloat() <= 0.05F) {
                    WandUtil.spawnCrossIceBouquet(worldIn, vec3, entityLiving, potency, MathHelper.secondsToTicks(duration));
                } else {
                    WandUtil.spawnIceBouquet(worldIn, vec3, entityLiving, potency, MathHelper.secondsToTicks(duration));
                }
            } else {
                WandUtil.spawn4x4IceBouquet(worldIn, vec3, entityLiving, potency, MathHelper.secondsToTicks(duration));
            }
            SoundUtil.playNecromancerSummon(entityLiving);
        }
    }
}
