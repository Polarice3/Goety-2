package com.Polarice3.Goety.common.magic.spells.nether;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.util.FirePillar;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FlameStrikeSpell extends Spell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.FlameStrikeCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.FlameStrikeDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.RUMBLE.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.FlameStrikeCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public void startSpell(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        int warmUp = this.castDuration(entityLiving) - 10;
        int duration = 180 * (WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving) + 1);
        int range = 16 + WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
        LivingEntity target = this.getTarget(entityLiving, range);
        if (target != null && !this.isShifting(entityLiving)){
            double radius = 3.0D;
            if (rightStaff(staff)){
                radius *= 2.0D;
            }
            List<Vec3> vec3s = BlockFinder.buildOuterBlockCircle(target.position(), radius);
            for (Vec3 vec3 : vec3s) {
                FirePillar flames = new FirePillar(worldIn, vec3.x, vec3.y, vec3.z);
                flames.setOwner(entityLiving);
                flames.setDuration(duration);
                flames.setWarmUp(warmUp);
                flames.setExtraDamage(WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving));
                MobUtil.moveDownToGround(flames);
                worldIn.addFreshEntity(flames);
            }
        } else {
            Vec3 vector3d = entityLiving.getViewVector(1.0F);
            float f = (float) Mth.atan2(vector3d.z - entityLiving.getZ(), vector3d.x - entityLiving.getX());
            for (int k = 0; k < 8; ++k) {
                float f2 = f + (float) k * (float) Math.PI * 0.25F + 1.0F;
                FirePillar flames = new FirePillar(worldIn, entityLiving.getX() + (double) Mth.cos(f2), entityLiving.getY(), entityLiving.getZ() + (double) Mth.sin(f2));
                flames.setOwner(entityLiving);
                flames.setDuration(duration);
                flames.setWarmUp(warmUp);
                flames.setExtraDamage(WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving));
                MobUtil.moveDownToGround(flames);
                worldIn.addFreshEntity(flames);
            }

            for (int k = 0; k < 8; ++k) {
                float f2 = f + (float) k * (float) Math.PI * 0.25F + 3.0F;
                FirePillar flames = new FirePillar(worldIn, entityLiving.getX() + (double) Mth.cos(f2) * 3.0D, entityLiving.getY(), entityLiving.getZ() + (double) Mth.sin(f2) * 3.0D);
                flames.setOwner(entityLiving);
                flames.setDuration(duration);
                flames.setWarmUp(warmUp);
                flames.setExtraDamage(WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving));
                MobUtil.moveDownToGround(flames);
                worldIn.addFreshEntity(flames);
            }

            if (rightStaff(staff)) {
                for (int k = 0; k < 8; ++k) {
                    float f2 = f + (float) k * (float) Math.PI * 0.25F + 6.0F;
                    FirePillar flames = new FirePillar(worldIn, entityLiving.getX() + (double) Mth.cos(f2) * 6.0D, entityLiving.getY(), entityLiving.getZ() + (double) Mth.sin(f2) * 6.0D);
                    flames.setOwner(entityLiving);
                    flames.setDuration(duration);
                    flames.setWarmUp(warmUp);
                    flames.setExtraDamage(WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving));
                    MobUtil.moveDownToGround(flames);
                    worldIn.addFreshEntity(flames);
                }

                for (int k = 0; k < 16; ++k) {
                    float f2 = f + (float) k * (float) Math.PI * 0.25F + 9.0F;
                    FirePillar flames = new FirePillar(worldIn, entityLiving.getX() + (double) Mth.cos(f2) * 9.0F, entityLiving.getY(), entityLiving.getZ() + (double) Mth.sin(f2) * 9.0F);
                    flames.setOwner(entityLiving);
                    flames.setDuration(duration);
                    flames.setWarmUp(warmUp);
                    flames.setExtraDamage(WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving));
                    MobUtil.moveDownToGround(flames);
                    worldIn.addFreshEntity(flames);
                }
            }
        }
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
    }
}
