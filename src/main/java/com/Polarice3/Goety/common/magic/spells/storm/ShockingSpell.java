package com.Polarice3.Goety.common.magic.spells.storm;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.EverChargeSpells;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningPacket;
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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShockingSpell extends EverChargeSpells {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.ShockingCost.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.ZAP.get();
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
        list.add(ModEnchantments.BURNING.get());
        return list;
    }

    public void commonResult(ServerLevel worldIn, LivingEntity entityLiving, int range, boolean staff){
        Player playerEntity = (Player) entityLiving;
        float damage = SpellConfig.ShockingDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        int burning = 0;
        if (WandUtil.enchantedFocus(entityLiving)) {
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
            damage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), entityLiving);
        }
        Vec3 vec3 = entityLiving.getEyePosition();
        BlockHitResult rayTraceResult = this.blockResult(worldIn, playerEntity, range);
        Entity target = MobUtil.getNearbyTarget(worldIn, entityLiving, 20.0D, 1.0F);
        Optional<BlockPos> lightningRod = BlockFinder.findLightningRod(worldIn, BlockPos.containing(rayTraceResult.getLocation()), range);
        if (lightningRod.isPresent() && !staff){
            BlockPos blockPos = lightningRod.get();
            ModNetwork.sendToALL(new SLightningPacket(vec3, new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), 5));
            worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.ZAP.get(), this.getSoundSource(), 1.0F, 1.0F);
        } else {
            if (target instanceof LivingEntity livingEntity && !livingEntity.isDeadOrDying() && ForgeHooks.onLivingAttack(livingEntity, ModDamageSource.directShock(entityLiving), damage)) {
                Vec3 vec31 = new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ());
                ModNetwork.sendToALL(new SLightningPacket(vec3, vec31, 5));
                if (target.hurt(ModDamageSource.directShock(entityLiving), damage)){
                    if (burning > 0){
                        if (worldIn.random.nextFloat() < 0.05F){
                            target.setSecondsOnFire(5 * burning);
                        }
                    }
                }
                worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.ZAP.get(), this.getSoundSource(), 1.0F, 1.0F);
            } else {
                BlockPos blockPos = rayTraceResult.getBlockPos();
                ModNetwork.sendToALL(new SLightningPacket(vec3, new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), 5));
                worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.ZAP.get(), this.getSoundSource(), 1.0F, 1.0F);
            }
        }
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        this.commonResult(worldIn, entityLiving, 16, rightStaff(staff));
    }
}
