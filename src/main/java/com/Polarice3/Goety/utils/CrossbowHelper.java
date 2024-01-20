package com.Polarice3.Goety.utils;

import com.google.common.collect.Lists;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class CrossbowHelper {

    public static List<ItemStack> getChargedProjectiles(ItemStack p_40942_) {
        List<ItemStack> list = Lists.newArrayList();
        CompoundTag compoundtag = p_40942_.getTag();
        if (compoundtag != null && compoundtag.contains("ChargedProjectiles", 9)) {
            ListTag listtag = compoundtag.getList("ChargedProjectiles", 10);
            if (listtag != null) {
                for(int i = 0; i < listtag.size(); ++i) {
                    CompoundTag compoundtag1 = listtag.getCompound(i);
                    list.add(ItemStack.of(compoundtag1));
                }
            }
        }

        return list;
    }

    public static float[] getShotPitches(RandomSource p_220024_) {
        boolean flag = p_220024_.nextBoolean();
        return new float[]{1.0F, getRandomShotPitch(flag, p_220024_), getRandomShotPitch(!flag, p_220024_)};
    }

    public static float getRandomShotPitch(boolean p_220026_, RandomSource p_220027_) {
        float f = p_220026_ ? 0.63F : 0.43F;
        return 1.0F / (p_220027_.nextFloat() * 0.5F + 1.8F) + f;
    }

    public static void performCustomShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack crossbow, Projectile projectile, float velocity, float p_40893_) {
        performCustomShooting(level, shooter, hand, crossbow, projectile, SoundEvents.CROSSBOW_SHOOT, velocity, p_40893_);
    }

    public static void performCustomShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack crossbow, Projectile projectile, SoundEvent soundEvent, float velocity, float p_40893_) {
        if (shooter instanceof Player player && net.minecraftforge.event.ForgeEventFactory.onArrowLoose(crossbow, shooter.level, player, 1, true) < 0) return;
        List<ItemStack> list = getChargedProjectiles(crossbow);
        float[] afloat = getShotPitches(shooter.getRandom());

        for(int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);
            if (!itemstack.isEmpty()) {
                if (i == 0) {
                    shootCustomProjectile(level, shooter, hand, crossbow, projectile, soundEvent, afloat[i], velocity, p_40893_, 0.0F);
                } else if (i == 1) {
                    shootCustomProjectile(level, shooter, hand, crossbow, projectile, soundEvent, afloat[i], velocity, p_40893_, -10.0F);
                } else if (i == 2) {
                    shootCustomProjectile(level, shooter, hand, crossbow, projectile, soundEvent, afloat[i], velocity, p_40893_, 10.0F);
                }
            }
        }

        onCrossbowShot(level, shooter, crossbow);
    }

    public static void onCrossbowShot(Level p_40906_, LivingEntity p_40907_, ItemStack p_40908_) {
        if (p_40907_ instanceof ServerPlayer serverplayer) {
            if (!p_40906_.isClientSide) {
                CriteriaTriggers.SHOT_CROSSBOW.trigger(serverplayer, p_40908_);
            }

            serverplayer.awardStat(Stats.ITEM_USED.get(p_40908_.getItem()));
        }

        clearChargedProjectiles(p_40908_);
    }

    public static void clearChargedProjectiles(ItemStack p_40944_) {
        CompoundTag compoundtag = p_40944_.getTag();
        if (compoundtag != null) {
            ListTag listtag = compoundtag.getList("ChargedProjectiles", 9);
            listtag.clear();
            compoundtag.put("ChargedProjectiles", listtag);
        }

    }

    public static void shootCustomProjectile(Level level, LivingEntity shooter, InteractionHand hand, ItemStack crossbow, Projectile projectile, SoundEvent soundEvent, float pitch, float velocity, float p_40903_, float p_40904_) {
        if (!level.isClientSide) {
            if (shooter instanceof CrossbowAttackMob crossbowattackmob) {
                if (crossbowattackmob.getTarget() != null) {
                    crossbowattackmob.shootCrossbowProjectile(crossbowattackmob.getTarget(), crossbow, projectile, p_40904_);
                }
            }else {
                Vec3 vec31 = shooter.getUpVector(1.0F);
                Quaternionf quaternionf = (new Quaternionf()).setAngleAxis((double)(p_40904_ * ((float)Math.PI / 180F)), vec31.x, vec31.y, vec31.z);
                Vec3 vec3 = shooter.getViewVector(1.0F);
                Vector3f vector3f = vec3.toVector3f().rotate(quaternionf);
                projectile.shoot((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), velocity, p_40903_);
            }

            crossbow.hurtAndBreak(1, shooter, (p_40858_) -> {
                p_40858_.broadcastBreakEvent(hand);
            });
            level.addFreshEntity(projectile);
            level.playSound((Player)null, shooter.getX(), shooter.getY(), shooter.getZ(), soundEvent, SoundSource.PLAYERS, 1.0F, pitch);
        }
    }
}
