package com.Polarice3.Goety.common.magic.spells.void_spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class BlinkSpell extends Spells {

    @Override
    public int SoulCost() {
        return SpellConfig.BlinkCost.get();
    }

    @Override
    public int CastDuration() {
        return SpellConfig.BlinkDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.ENDERMAN_TELEPORT;
    }

    @Override
    public int SpellCooldown() {
        return SpellConfig.BlinkCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.VOID;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        if (entityLiving instanceof Player player) {
            int enchantment = 0;
            if (WandUtil.enchantedFocus(player)) {
                enchantment = WandUtil.getLevels(ModEnchantments.RANGE.get(), player);
            }
            Vec3 vec3 = findTeleportLocation(worldIn, player, 32 + enchantment);
            BlockPos blockPos = BlockPos.containing(vec3);
            enderTeleportEvent(entityLiving, worldIn, blockPos);
            worldIn.broadcastEntityEvent(player, (byte) 46);
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), CastingSound(), this.getSoundSource(), 2.0F, 1.0F);
    }

    /**
     * Teleport Location code based of @iron43's codes: <a href="https://github.com/iron431/Irons-Spells-n-Spellbooks/blob/1.19.2/src/main/java/io/redspace/ironsspellbooks/spells/ender/TeleportSpell.java">...</a>
     */
    public static Vec3 findTeleportLocation(ServerLevel level, LivingEntity livingEntity, float maxDistance) {
        BlockHitResult blockHitResult = MobUtil.rayTrace(livingEntity, maxDistance, false);
        BlockPos pos = blockHitResult.getBlockPos();

        Vec3 bbOffset = livingEntity.getForward().normalize().multiply(livingEntity.getBbWidth() / 3.0D, 0.0D, livingEntity.getBbHeight() / 3.0D);
        Vec3 bbImpact = blockHitResult.getLocation().subtract(bbOffset);

        int ledgeY = (int) level.clip(new ClipContext(Vec3.atBottomCenterOf(pos).add(0.0D, 3.0D, 0.0D), Vec3.atBottomCenterOf(pos), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)).getLocation().y;

        Vec3 correctedPos = new Vec3(pos.getX(), ledgeY, pos.getZ());

        boolean isAir = level.getBlockState(BlockPos.containing(correctedPos)).isAir();
        boolean los = level.clip(new ClipContext(bbImpact, bbImpact.add(0.0D, ledgeY - pos.getY(), 0.0D), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, livingEntity)).getType() == HitResult.Type.MISS;

        if (isAir && los && Math.abs(ledgeY - pos.getY()) <= 3) {
            return correctedPos.add(0.5D, 0.076D, 0.5D);
        } else {
            return level.clip(new ClipContext(bbImpact, bbImpact.add(0.0D, -livingEntity.getEyeHeight(), 0.0D), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, livingEntity)).getLocation().add(0.0D, 0.076D, 0.0D);
        }

    }

    public static void enderTeleportEvent(LivingEntity player, Level world, BlockPos target) {
        player.teleportTo(target.getX(), target.getY(), target.getZ());
        player.resetFallDistance();
    }
}
