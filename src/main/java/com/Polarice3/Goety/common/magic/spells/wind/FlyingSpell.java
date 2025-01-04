package com.Polarice3.Goety.common.magic.spells.wind;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.EverChargeSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

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

    public void CommonResult(ServerLevel worldIn, LivingEntity entityLiving, double power){
        if (entityLiving instanceof Player player){
            int potency = 0;
            if (WandUtil.enchantedFocus(player)){
                potency = WandUtil.getLevels(ModEnchantments.POTENCY.get(), player);
            }
            player.hurtMarked = true;
            if (!player.level.isClientSide){
                player.setOnGround(false);
            }
            Vec3 vector3d = player.getLookAngle();
            double d0 = power + (double) (potency / 2);
            player.setDeltaMovement(vector3d.x * d0, vector3d.y * d0, vector3d.z * d0);
            player.hasImpulse = true;
            player.fallDistance = 0;
            for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
                worldIn.sendParticles(ParticleTypes.CLOUD, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
            }
        }
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff){
        double d = 0.5D;
        if (rightStaff(staff)){
            d = 1.0D;
        }
        this.CommonResult(worldIn, caster, d);
    }

    public static HumanoidModel.ArmPose FLIGHT_POSE = HumanoidModel.ArmPose.create("GOETY_FLYING", false, (model, entity, arm) -> {
        float f5 = 1.0F;
        if (arm == HumanoidArm.RIGHT) {
            model.rightArm.xRot = -MathHelper.modelDegrees(105);
            model.rightArm.zRot = Mth.cos(f5 * 0.6662F) * 0.25F;
            model.leftArm.xRot = MathHelper.modelDegrees(25);
        } else {
            model.leftArm.xRot = -MathHelper.modelDegrees(105);
            model.leftArm.zRot = -Mth.cos(f5 * 0.6662F) * 0.25F;
            model.rightArm.xRot = MathHelper.modelDegrees(25);
        }
        model.rightLeg.xRot = MathHelper.modelDegrees(17.5F);
        model.leftLeg.xRot = MathHelper.modelDegrees(17.5F);

        model.rightLeg.xRot += 1.0F * Mth.sin(Minecraft.getInstance().getPartialTick() * 0.067F) * 0.05F;
        model.leftLeg.xRot += -1.0F * Mth.sin(Minecraft.getInstance().getPartialTick() * 0.067F) * 0.05F;
    });

    @Override
    public HumanoidModel.ArmPose spellPose() {
        return FLIGHT_POSE;
    }
}
