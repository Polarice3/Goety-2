package com.Polarice3.Goety.common.magic.spells.storm;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SThunderBoltPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;

import java.util.Optional;

public class ThunderboltSpell extends Spells {
    @Override
    public int SoulCost() {
        return SpellConfig.ThunderboltCost.get();
    }

    @Override
    public int CastDuration() {
        return SpellConfig.ThunderboltDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.ZAP.get();
    }

    @Override
    public int SpellCooldown() {
        return SpellConfig.ThunderboltCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.STORM;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        Player playerEntity = (Player) entityLiving;
        int range = 16;
        float damage = SpellConfig.ThunderboltDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        int burning = 0;
        if (WandUtil.enchantedFocus(entityLiving)) {
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
            damage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), entityLiving);
        }
        Vec3 vec3 = entityLiving.getEyePosition();
        BlockHitResult rayTraceResult = this.blockResult(worldIn, playerEntity, range);
        Entity target = MobUtil.getNearbyTarget(worldIn, entityLiving, 20.0D, 1.0F);
        Optional<BlockPos> lightningRod = BlockFinder.findLightningRod(worldIn, new BlockPos(rayTraceResult.getLocation()), range);
        if (lightningRod.isPresent() && !rightStaff(staff)){
            BlockPos blockPos = lightningRod.get();
            ModNetwork.sendToALL(new SThunderBoltPacket(vec3, new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), 10));
            worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.THUNDERBOLT.get(), this.getSoundSource(), 1.0F, 1.0F);
        } else {
            if (target instanceof LivingEntity livingEntity && !livingEntity.isDeadOrDying() && ForgeHooks.onLivingAttack(livingEntity, ModDamageSource.directShock(entityLiving), damage)) {
                Vec3 vec31 = new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ());
                ModNetwork.sendToALL(new SThunderBoltPacket(vec3, vec31, 10));
                if (target.hurt(ModDamageSource.directShock(entityLiving), damage)){
                    if (burning > 0){
                        target.setSecondsOnFire(5 * burning);
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
