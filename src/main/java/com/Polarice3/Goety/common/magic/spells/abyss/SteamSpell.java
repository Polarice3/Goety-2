package com.Polarice3.Goety.common.magic.spells.abyss;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.SpellHurtingProjectile;
import com.Polarice3.Goety.common.entities.projectiles.SteamMissile;
import com.Polarice3.Goety.common.magic.ChargingSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class SteamSpell extends ChargingSpell {

    public int defaultSoulCost() {
        return SpellConfig.SteamingCost.get();
    }

    @Override
    public int defaultCastUp() {
        return SpellConfig.SteamingDuration.get();
    }

    @Override
    public int Cooldown() {
        return 2;
    }

    @Override
    public int Cooldown(LivingEntity caster, ItemStack staff, int shots) {
        if (shots % 5 == 0){
            return 8;
        } else {
            return super.Cooldown(caster, staff, shots);
        }
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.SteamingCoolDown.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.CAST_STEAM.get();
    }

    @Override
    public float castingVolume() {
        return 1.0F;
    }

    @Override
    public int shotsNumber(LivingEntity caster, ItemStack staff) {
        if (this.rightStaff(staff)){
            return 20;
        } else {
            return 5;
        }
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ABYSS;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        float velocity = spellStat.getVelocity();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
        }
        Vec3 vector3d = caster.getViewVector(1.0F);
        double accuracy = 8.0D;
        Vec3 vec3 = (new Vec3(vector3d.x, vector3d.y, vector3d.z)).normalize().add(worldIn.random.triangle(0.0D, 0.0172275D * (double) accuracy), 0.0D, worldIn.random.triangle(0.0D, 0.0172275D * (double) accuracy));
        SpellHurtingProjectile steamMissile = new SteamMissile(
                caster.getX() + vector3d.x / 2,
                caster.getEyeY() - 0.2,
                caster.getZ() + vector3d.z / 2,
                vec3.x,
                vec3.y,
                vec3.z, worldIn);
        steamMissile.setExtraDamage(potency);
        steamMissile.setBoltSpeed((int) velocity);
        steamMissile.setOwner(caster);
        worldIn.addFreshEntity(steamMissile);
    }
}
