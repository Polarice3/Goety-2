package com.Polarice3.Goety.common.magic.spells.storm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.util.MagicLightningTrap;
import com.Polarice3.Goety.common.entities.util.VoidLightningTrap;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ThunderstormSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.ThunderstormCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.ThunderstormDuration.get();
    }

    @Override
    public int castDuration(LivingEntity caster, ItemStack staff) {
        int i = WandUtil.getStats(caster, this).getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            i += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
        }
        return SpellConfig.ThunderstormDuration.get() * (i + 1);
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.ThunderstormCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.STORM;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    @Override
    public void useSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int castTime, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        double radius = spellStat.getRadius();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster);
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
        }
        if (castTime >= 20) {
            int amount = this.rightStaff(staff) ? 2 : 1;
            for (int i = 0; i < amount; ++i) {
                BlockPos blockPos = caster.blockPosition();
                LivingEntity target = this.getTarget(caster);
                if (castTime < 40 && target != null) {
                    blockPos = target.blockPosition();
                }
                BlockPos blockPos1 = blockPos.offset(worldIn.getRandom().nextInt(-16, 16), 0, worldIn.getRandom().nextInt(-16, 16));
                BlockPos blockPos2 = blockPos.offset(worldIn.getRandom().nextInt(-16, 16), 0, worldIn.getRandom().nextInt(-16, 16));
                Vec3 vec3 = Vec3.atBottomCenterOf(blockPos1);
                Vec3 vec32 = Vec3.atBottomCenterOf(blockPos2);
                MagicLightningTrap trap = new MagicLightningTrap(worldIn, vec3.x, vec3.y, vec3.z);
                if (this.typeStaff(staff, SpellType.VOID)) {
                    trap = new VoidLightningTrap(worldIn, vec3.x, vec3.y, vec3.z);
                } else if (staff.is(ModItems.NAMELESS_STAFF.get())) {
                    trap.setColor(0xa7fc3e);
                }
                trap.setOwner(caster);
                trap.setDuration(40);
                trap.setDamage(trap.getDamage() + potency);
                trap.setRadius((float) (trap.radius() + (radius / 2.0F)));
                if (!worldIn.getEntitiesOfClass(MagicLightningTrap.class, new AABB(blockPos1)).isEmpty()) {
                    trap.setPos(vec32.x(), vec32.y(), vec32.z());
                }
                MobUtil.moveDownToGround(trap);
                worldIn.addFreshEntity(trap);
            }
        }
        super.useSpell(worldIn, caster, staff, castTime, spellStat);
    }
}
