package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class BarricadeSpell extends Spells {
    @Override
    public int SoulCost() {
        return SpellConfig.BarricadeCost.get();
    }

    @Override
    public int CastDuration() {
        return SpellConfig.BarricadeDuration.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.GEOMANCY;
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.EVOKER_PREPARE_ATTACK;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        int range = 16;
        int potency = 0;
        int duration = 1;
        float chance = 0.05F;
        if (WandUtil.enchantedFocus(entityLiving)) {
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving);
        }
        if (GeoPower(entityLiving)){
            chance += 0.2F;
        }
        HitResult rayTraceResult = this.rayTrace(worldIn, entityLiving, range, 3);
        if (rayTraceResult instanceof EntityHitResult){
            Entity target = ((EntityHitResult) rayTraceResult).getEntity();
            if (this.isShifting(entityLiving)){
                if (worldIn.random.nextFloat() <= chance){
                    WandUtil.summonQuadOffensiveTrap(entityLiving, target, ModEntityType.TOTEMIC_BOMB.get(), potency);
                } else {
                    int xShift = worldIn.getRandom().nextInt(-1, 1);
                    int zShift = worldIn.getRandom().nextInt(-1, 1);
                    WandUtil.summonMonolith(entityLiving, target, ModEntityType.TOTEMIC_BOMB.get(), xShift, zShift, potency);
                }
            } else {
                int random = worldIn.random.nextInt(3);
                if (random == 0) {
                    int[] rowToRemove = Util.getRandom(WandUtil.CONFIG_1_ROWS, entityLiving.getRandom());
                    Direction direction = Direction.fromYRot(target.getYHeadRot());
                    switch (direction){
                        case NORTH -> rowToRemove = WandUtil.CONFIG_1_NORTH_ROW;
                        case SOUTH -> rowToRemove = WandUtil.CONFIG_1_SOUTH_ROW;
                        case WEST -> rowToRemove = WandUtil.CONFIG_1_WEST_ROW;
                        case EAST -> rowToRemove = WandUtil.CONFIG_1_EAST_ROW;
                    }
                    WandUtil.summonSquareTrap(entityLiving, target, ModEntityType.TOTEMIC_WALL.get(), rowToRemove, duration);
                } else if (random == 1){
                    WandUtil.summonWallTrap(entityLiving, target, ModEntityType.TOTEMIC_WALL.get(), duration);
                } else {
                    WandUtil.summonRandomPillarsTrap(entityLiving, target, ModEntityType.TOTEMIC_WALL.get(), duration);
                }
            }
        } else if (rayTraceResult instanceof BlockHitResult){
            BlockPos blockPos = ((BlockHitResult) rayTraceResult).getBlockPos();
            if (this.isShifting(entityLiving)){
                if (worldIn.random.nextFloat() <= chance){
                    WandUtil.summonQuadOffensiveTrap(entityLiving, blockPos, ModEntityType.TOTEMIC_BOMB.get(), potency);
                } else {
                    WandUtil.summonMonolith(entityLiving, blockPos, ModEntityType.TOTEMIC_BOMB.get(), 0, 0, potency);
                }
            } else {
                WandUtil.summonWallTrap(entityLiving, blockPos, ModEntityType.TOTEMIC_WALL.get(), duration);
            }
        }
    }
}
