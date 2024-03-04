package com.Polarice3.Goety.common.magic.spells.storm;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.ShockwaveParticleOption;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class DischargeSpell extends Spells {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.DischargeCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.DischargeDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.DischargeCoolDown.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.STORM;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        int radius = 3;
        float damage = SpellConfig.DischargeDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        float maxDamage = SpellConfig.DischargeMaxDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        if (WandUtil.enchantedFocus(entityLiving)){
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), entityLiving);
            damage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            maxDamage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
        }
        for (int i = -radius; i < radius; ++i){
            for (int k = -radius; k < radius; ++k){
                BlockPos blockPos = entityLiving.blockPosition().offset(i, 0, k);
                worldIn.sendParticles(ModParticleTypes.ELECTRIC.get(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0, 0, 0.04D, 0, 0.5F);
            }
        }
        worldIn.sendParticles(new ShockwaveParticleOption(0), entityLiving.getX(), entityLiving.getY() + 0.5F, entityLiving.getZ(), 0, 0, 0, 0, 0);
        float trueDamage = Mth.clamp(damage + worldIn.random.nextInt((int) (maxDamage - damage)), damage, maxDamage);
        MobUtil.explosionDamage(worldIn, entityLiving, ModDamageSource.indirectShock(entityLiving, entityLiving), entityLiving.blockPosition(), radius, trueDamage);
        for (Entity entity : MobUtil.explosionRangeEntities(worldIn, entityLiving, entityLiving.blockPosition(), radius)){
            if (entity instanceof LivingEntity target){
                float chance = rightStaff(staff) ? 0.25F : 0.05F;
                if (worldIn.isThundering() && worldIn.isRainingAt(target.blockPosition())){
                    if (worldIn.random.nextFloat() <= chance){
                        LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, worldIn);
                        lightningBolt.setPos(target.position());
                        if (entityLiving instanceof ServerPlayer serverPlayer) {
                            lightningBolt.setCause(serverPlayer);
                        }
                        worldIn.addFreshEntity(lightningBolt);
                    }
                }
            }
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.REDSTONE_EXPLODE.get(), this.getSoundSource(), 1.0F, 1.0F);
    }
}
