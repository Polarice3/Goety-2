package com.Polarice3.Goety.common.magic.spells.geomancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.ScatterMine;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class ScatterSpell extends Spell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.ScatterCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.ScatterDuration.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.GEOMANCY;
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.ScatterCoolDown.get();
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        for (int i = 0; i < 3; ++i) {
            BlockPos blockPos = entityLiving.blockPosition();
            blockPos = blockPos.offset(-4 + worldIn.random.nextInt(8), 0, -4 + worldIn.random.nextInt(8));
            BlockPos blockPos2 = entityLiving.blockPosition().offset(-4 + worldIn.random.nextInt(8), 0, -4 + worldIn.random.nextInt(8));
            ScatterMine scatterMine = new ScatterMine(worldIn, entityLiving, blockPos);
            scatterMine.setIsSpell();
            scatterMine.setExtraDamage(WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving));
            scatterMine.lifeTicks = MathHelper.secondsToTicks(10 + WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving));
            if (!worldIn.getEntitiesOfClass(ScatterMine.class, new AABB(blockPos)).isEmpty()) {
                scatterMine.setPos(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
            }
            if (worldIn.addFreshEntity(scatterMine)) {
                scatterMine.playSound(ModSounds.REDSTONE_GOLEM_MINE_SPAWN.get());
            }
        }
    }
}
