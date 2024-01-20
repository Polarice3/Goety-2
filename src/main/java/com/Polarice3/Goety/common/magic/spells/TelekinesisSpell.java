package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.EverChargeSpells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class TelekinesisSpell extends EverChargeSpells {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.TelekinesisCost.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity entityLiving) {
        if (entityLiving.hurtTime > 0){
            if (entityLiving instanceof Player player){
                player.stopUsingItem();
                SEHelper.addCooldown(player, ModItems.TELEKINESIS_FOCUS.get(), MathHelper.secondsToTicks(5));
                SEHelper.sendSEUpdatePacket(player);
            }
            return false;
        }
        return true;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        double potency = 1.0D;
        int range = 8;
        if (WandUtil.enchantedFocus(entityLiving)){
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving) / 2.0D;
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
        }
        Entity target = MobUtil.getSingleTarget(worldIn, entityLiving, range, 3, EntitySelector.NO_CREATIVE_OR_SPECTATOR);
        if (target != null){
            if ((entityLiving.getBoundingBox().inflate(0.5D).getSize() * potency) >= target.getBoundingBox().getSize()) {
                boolean flag = true;
                if (target instanceof LivingEntity livingTarget){
                    if (livingTarget.getMaxHealth() >= SpellConfig.TelekinesisMaxHealth.get()
                    || MobUtil.hasEntityTypesConfig(SpellConfig.TelekinesisBlackList.get(), livingTarget.getType())){
                        flag = false;
                    }
                }
                if (flag) {
                    Vec3 lookVec = entityLiving.getLookAngle();
                    double x = entityLiving.getX() + lookVec.x * 1.6D - target.getX();
                    double y = entityLiving.getEyeY() + lookVec.y * 1.6D - target.getY();
                    double z = entityLiving.getZ() + lookVec.z * 1.6D - target.getZ();
                    double offset = 0.1D;
                    target.setDeltaMovement(x * offset, y * offset, z * offset);
                    target.move(MoverType.SELF, target.getDeltaMovement());
                    target.resetFallDistance();
                    ServerParticleUtil.addParticlesAroundSelf(worldIn, ParticleTypes.PORTAL, target);
                }
            }
        }
    }
}
