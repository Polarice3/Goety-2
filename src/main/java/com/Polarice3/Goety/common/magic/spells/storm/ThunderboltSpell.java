package com.Polarice3.Goety.common.magic.spells.storm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SThunderBoltPacket;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ThunderboltSpell extends Spell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.ThunderboltCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.ThunderboltDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.ZAP.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.ThunderboltCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.STORM;
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
        Player playerEntity = (Player) entityLiving;
        int range = 16;
        float damage = SpellConfig.ThunderboltDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        if (WandUtil.enchantedFocus(entityLiving)) {
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
            damage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
        }
        Vec3 vec3 = entityLiving.getEyePosition();
        BlockHitResult rayTraceResult = this.blockResult(worldIn, playerEntity, range);
        Entity target = MobUtil.getNearbyTarget(worldIn, entityLiving, 20.0D, 1.0F);
        Optional<BlockPos> lightningRod = BlockFinder.findLightningRod(worldIn, BlockPos.containing(rayTraceResult.getLocation()), range);
        if (lightningRod.isPresent() && !rightStaff(staff)){
            BlockPos blockPos = lightningRod.get();
            ModNetwork.sendToALL(new SThunderBoltPacket(vec3, new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), 10));
            worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.THUNDERBOLT.get(), this.getSoundSource(), 1.0F, 1.0F);
        } else {
            if (target instanceof LivingEntity livingEntity && !livingEntity.isDeadOrDying() && ForgeHooks.onLivingAttack(livingEntity, ModDamageSource.directShock(entityLiving), damage)) {
                Vec3 vec31 = new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ());
                ModNetwork.sendToALL(new SThunderBoltPacket(vec3, vec31, 10));
                if (target.hurt(ModDamageSource.directShock(entityLiving), damage)){
                    float chance = rightStaff(staff) ? 0.25F : 0.05F;
                    if (worldIn.isThundering() && worldIn.isRainingAt(target.blockPosition())){
                        chance += 0.25F;
                    }
                    if (worldIn.random.nextFloat() <= chance){
                        livingEntity.addEffect(new MobEffectInstance(GoetyEffects.SPASMS.get(), MathHelper.secondsToTicks(5)));
                    }
                }
                worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.THUNDERBOLT.get(), this.getSoundSource(), 1.0F, 1.0F);
            } else {
                BlockPos blockPos = rayTraceResult.getBlockPos();
                ModNetwork.sendToALL(new SThunderBoltPacket(vec3, new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), 10));
                worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.THUNDERBOLT.get(), this.getSoundSource(), 1.0F, 1.0F);
            }
        }
    }
}
