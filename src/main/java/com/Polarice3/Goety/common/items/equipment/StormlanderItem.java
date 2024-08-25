package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningBoltPacket;
import com.Polarice3.Goety.common.network.server.SThunderBoltPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class StormlanderItem extends HammerItem{

    @Override
    public void smash(ItemStack pStack, LivingEntity pTarget, Player player) {
        player.level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), ModSounds.HAMMER_SWING.get(), player.getSoundSource(), 1.0F, 1.0F);
        player.level.playSound((Player) null, pTarget.getX(), pTarget.getY(), pTarget.getZ(), ModSounds.DIRT_DEBRIS.get(), player.getSoundSource(), 1.0F, 1.0F);
        if (player.level instanceof ServerLevel serverLevel){
            BlockPos blockPos = BlockPos.containing(pTarget.getX(), pTarget.getY() - 1.0F, pTarget.getZ());
            BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, serverLevel.getBlockState(blockPos));
            float area = 1.75F;
            area += pStack.getEnchantmentLevel(ModEnchantments.RADIUS.get());
            for (int i = 0; i < 8; ++i) {
                ServerParticleUtil.circularParticles(serverLevel, option, pTarget.getX(), pTarget.getY() + 0.25D, pTarget.getZ(), area);
            }
            BlockHitResult rayTraceResult = this.blockResult(serverLevel, player, 16);
            Optional<BlockPos> lightningRod = BlockFinder.findLightningRod(serverLevel, BlockPos.containing(rayTraceResult.getLocation()), 16);
            if (lightningRod.isPresent()) {
                BlockPos blockPos1 = lightningRod.get();
                ModNetwork.sendToALL(new SLightningBoltPacket(new Vec3(blockPos1.getX(), blockPos1.getY() + 250, blockPos1.getZ()), new Vec3(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ()), 10));
                serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.THUNDERBOLT.get(), player.getSoundSource(), 1.0F, 1.0F);
            } else {
                Vec3 vec31 = new Vec3(pTarget.getX(), pTarget.getY() + pTarget.getBbHeight() / 2, pTarget.getZ());
                ModNetwork.sendToALL(new SLightningBoltPacket(new Vec3(pTarget.getX(), pTarget.getY() + 250, pTarget.getZ()), vec31, 10));
                chain(pTarget, player);
                serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.THUNDERBOLT.get(), player.getSoundSource(), 1.0F, 1.0F);
            }
        }
    }

    public void attackMobs(LivingEntity pTarget, Player pPlayer, ItemStack pStack){
        float f = (float)pPlayer.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1 = EnchantmentHelper.getDamageBonus(pPlayer.getMainHandItem(), pTarget.getMobType());
        int j = EnchantmentHelper.getFireAspect(pPlayer);
        pPlayer.playSound(ModSounds.THUNDER_STRIKE_FAST.get());
        double area = 1.75D;
        area += pStack.getEnchantmentLevel(ModEnchantments.RADIUS.get());
        for (LivingEntity livingentity : pPlayer.level.getEntitiesOfClass(LivingEntity.class, pTarget.getBoundingBox().inflate(area, 0.25D, area))) {
            if (livingentity != pPlayer && livingentity != pTarget && !pPlayer.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStand) || !((ArmorStand) livingentity).isMarker()) && livingentity != pPlayer.getVehicle()) {
                livingentity.knockback(0.4F, (double) Mth.sin(pPlayer.getYRot() * ((float) Math.PI / 180F)), (double) (-Mth.cos(pPlayer.getYRot() * ((float) Math.PI / 180F))));
                if (livingentity.hurt(ModDamageSource.directShock(pPlayer), f + f1)) {
                    if (j > 0) {
                        livingentity.setSecondsOnFire(j * 4);
                    }
                    EnchantmentHelper.doPostHurtEffects(livingentity, pPlayer);
                    EnchantmentHelper.doPostDamageEffects(pPlayer, livingentity);
                }
            }
        }

        pPlayer.level.playSound((Player) null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), ModSounds.HAMMER_IMPACT.get(), pPlayer.getSoundSource(), 1.0F, 1.0F);
    }

    public void chain(LivingEntity pTarget, LivingEntity pAttacker) {
        double range = 6;
        Level level = pAttacker.level;
        float oDamage = getInitialDamage();

        List<Entity> harmed = new ArrayList<>();
        Predicate<Entity> selector = entity -> entity instanceof LivingEntity livingEntity && livingEntity != pAttacker && !harmed.contains(livingEntity) && MobUtil.canAttack(pAttacker, livingEntity);
        LivingEntity prevTarget = pTarget;

        float damage = level.isThundering() ? oDamage : oDamage / 2.0F;

        for (int i = 0; i < 4; i++) {
            AABB aabb = new AABB(Vec3Util.subtract(prevTarget.position(), range), Vec3Util.add(prevTarget.position(), range));
            List<Entity> entities = level.getEntities(prevTarget, aabb, selector);
            if (!entities.isEmpty()) {
                LivingEntity target = (LivingEntity) entities.get(level.getRandom().nextInt(entities.size()));
                if (target.hurt(ModDamageSource.directShock(pAttacker), damage)) {
                    if (prevTarget != target) {
                        Vec3 vec3 = prevTarget.getEyePosition();
                        Vec3 vec31 = target.getEyePosition();
                        ModNetwork.sendToALL(new SThunderBoltPacket(vec3, vec31, 8));
                    }
                }

                harmed.add(target);
                prevTarget = target;
                damage--;
            }
        }
    }

    public BlockHitResult blockResult(Level worldIn, Entity entity, double range) {
        float f = entity.getXRot();
        float f1 = entity.getYRot();
        Vec3 vector3d = entity.getEyePosition(1.0F);
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3 vector3d1 = vector3d.add((double)f6 * range, (double)f5 * range, (double)f7 * range);
        return worldIn.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity));
    }
}
