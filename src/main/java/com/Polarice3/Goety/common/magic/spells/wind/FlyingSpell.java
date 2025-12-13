package com.Polarice3.Goety.common.magic.spells.wind;

import com.Polarice3.Goety.api.magic.SpellPoses;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.EverChargeSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class FlyingSpell extends EverChargeSpell {

    public int defaultSoulCost() {
        return SpellConfig.FlyingCost.get();
    }

    @Override
    public int defaultCastUp() {
        return SpellConfig.FlyingChargeUp.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.WIND.get();
    }

    @Override
    public SoundEvent loopSound(LivingEntity caster) {
        return ModSounds.FLIGHT.get();
    }

    @OnlyIn(Dist.CLIENT)
    public HumanoidModel.ArmPose getPose(LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        return SpellPoses.FLIGHT_POSE;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WIND;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        double power = 0.5D;
        if (rightStaff(staff)){
            power = 1.0D;
        }
        int potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus(caster)){
            potency = WandUtil.getPotencyLevel(caster);
        }
        caster.hurtMarked = true;
        if (!worldIn.isClientSide){
            caster.setOnGround(false);
        }
        Vec3 vector3d = caster.getLookAngle();
        double d0 = power + (double) (potency / 2);
        caster.setDeltaMovement(vector3d.x * d0, vector3d.y * d0, vector3d.z * d0);
        caster.hasImpulse = true;
        caster.fallDistance = 0;
        for (int i = 0; i < caster.level.random.nextInt(35) + 10; ++i) {
            worldIn.sendParticles(ParticleTypes.CLOUD, caster.getX(), caster.getY(), caster.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
        }
    }
}
