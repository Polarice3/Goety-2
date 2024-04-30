package com.Polarice3.Goety.common.magic.spells.wild;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.EntangleVines;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EntanglingSpell extends Spell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.EntanglingCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.EntanglingDuration.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WILD;
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.WILD_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.EntanglingDuration.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        int range = 16;
        int duration = 0;
        if (WandUtil.enchantedFocus(entityLiving)) {
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving);
        }
        HitResult rayTraceResult = this.rayTrace(worldIn, entityLiving, range, 3);
        if (rightStaff(staff)){
            int i = (int) entityLiving.getX();
            int j = (int) entityLiving.getY();
            int k = (int) entityLiving.getZ();
            int amount = 0;
            List<LivingEntity> list = worldIn.getEntitiesOfClass(LivingEntity.class, (new AABB(i, j, k, i, j - 4, k)).inflate(16));
            if (!list.isEmpty()) {
                for (LivingEntity entity : list) {
                    if (amount < 8) {
                        if (entity != entityLiving && !MobUtil.areAllies(entity, entityLiving)) {
                            EntangleVines entangleVines = new EntangleVines(worldIn, entityLiving, entity);
                            entangleVines.setLifeSpan(entangleVines.getLifeSpan() + MathHelper.secondsToTicks(duration));
                            if (CuriosFinder.hasWildRobe(entityLiving)){
                                entangleVines.setDamaging(true);
                            }
                            if (worldIn.addFreshEntity(entangleVines)) {
                                ++amount;
                            }
                        }
                    }
                }
            } else if (rayTraceResult instanceof BlockHitResult){
                BlockPos blockPos = ((BlockHitResult) rayTraceResult).getBlockPos();
                EntangleVines entangleVines = new EntangleVines(worldIn, entityLiving, blockPos);
                entangleVines.setLifeSpan(entangleVines.getLifeSpan() + MathHelper.secondsToTicks(duration));
                worldIn.addFreshEntity(entangleVines);
            }
        } else {
            LivingEntity target = this.getTarget(entityLiving, range);
            if (target != null){
                EntangleVines entangleVines = new EntangleVines(worldIn, entityLiving, target);
                entangleVines.setLifeSpan(entangleVines.getLifeSpan() + MathHelper.secondsToTicks(duration));
                if (CuriosFinder.hasWildRobe(entityLiving)){
                    entangleVines.setDamaging(true);
                }
                worldIn.addFreshEntity(entangleVines);
            } else if (rayTraceResult instanceof BlockHitResult){
                BlockPos blockPos = ((BlockHitResult) rayTraceResult).getBlockPos();
                EntangleVines entangleVines = new EntangleVines(worldIn, entityLiving, blockPos);
                entangleVines.setLifeSpan(entangleVines.getLifeSpan() + MathHelper.secondsToTicks(duration));
                worldIn.addFreshEntity(entangleVines);
            }
        }
    }
}
