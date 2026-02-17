package com.Polarice3.Goety.common.magic.spells.geomancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.AbstractMonolith;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModAttributes;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

public class BarricadeSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.BarricadeCost.get();
    }

    @Override
    public int defaultCastDuration() {
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

    @Override
    public boolean hasCustomCooldown(LivingEntity caster, ItemStack staff, ItemStack focus, int initialCooldown) {
        return true;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.BarricadeCoolDown.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        int range = spellStat.getRange();
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        float chance = 0.05F;
        int cooldown = this.defaultSpellCooldown();
        if (WandUtil.enchantedFocus(caster)) {
            range += WandUtil.getRangeLevel(caster);
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
        }
        if (this.rightStaff(staff)){
            chance += 0.2F;
        }
        HitResult rayTraceResult = this.rayTrace(worldIn, caster, range, 3);
        LivingEntity target = this.getTarget(caster, range);
        EntityType<? extends AbstractMonolith> entityType = ModEntityType.TOTEMIC_WALL.get();
        if (this.typeStaff(staff, SpellType.FROST)){
            entityType = ModEntityType.GLACIAL_WALL.get();
        }
        if (target != null){
            if (this.isShifting(caster)){
                if (worldIn.getRandom().nextFloat() <= chance){
                    WandUtil.summonQuadOffensiveTrap(caster, target, ModEntityType.TOTEMIC_BOMB.get(), potency);
                    cooldown += MathHelper.secondsToTicks(3);
                } else {
                    int xShift = worldIn.getRandom().nextInt(-1, 1);
                    int zShift = worldIn.getRandom().nextInt(-1, 1);
                    WandUtil.summonMonolith(caster, target, ModEntityType.TOTEMIC_BOMB.get(), xShift, zShift, potency);
                    cooldown += MathHelper.secondsToTicks(2);
                }
            } else {
                int random = worldIn.random.nextInt(3);
                if (random == 0) {
                    int[] rowToRemove = Util.getRandom(WandUtil.CONFIG_1_ROWS, caster.getRandom());
                    Direction direction = Direction.fromYRot(target.getYHeadRot());
                    switch (direction){
                        case NORTH -> rowToRemove = WandUtil.CONFIG_1_NORTH_ROW;
                        case SOUTH -> rowToRemove = WandUtil.CONFIG_1_SOUTH_ROW;
                        case WEST -> rowToRemove = WandUtil.CONFIG_1_WEST_ROW;
                        case EAST -> rowToRemove = WandUtil.CONFIG_1_EAST_ROW;
                    }
                    WandUtil.summonSquareTrap(caster, target, entityType, rowToRemove, duration);
                } else if (random == 1){
                    WandUtil.summonWallTrap(caster, target, entityType, duration);
                } else {
                    WandUtil.summonRandomPillarsTrap(caster, target, entityType, duration);
                }
            }
        } else if (rayTraceResult instanceof BlockHitResult){
            BlockPos blockPos = ((BlockHitResult) rayTraceResult).getBlockPos();
            if (this.isShifting(caster)){
                if (worldIn.random.nextFloat() <= chance){
                    WandUtil.summonQuadOffensiveTrap(caster, blockPos, ModEntityType.TOTEMIC_BOMB.get(), potency);
                    cooldown += MathHelper.secondsToTicks(3);
                } else {
                    WandUtil.summonMonolith(caster, blockPos, ModEntityType.TOTEMIC_BOMB.get(), 0, 0, potency);
                    cooldown += MathHelper.secondsToTicks(2);
                }
            } else {
                WandUtil.summonWallTrap(caster, blockPos, entityType, duration);
            }
        }
        if (caster instanceof Player player) {
            cooldown *= (int) ModAttributes.getCooldownDiscount(caster);
            SEHelper.addCooldown(player, WandUtil.findFocus(caster).getItem(), cooldown);
        }
    }
}
