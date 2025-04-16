package com.Polarice3.Goety.common.items.equipment;

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
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class RevolverCrossbowItem extends CrossbowItem {
    private static final String TAG_CHARGED_SHOTS = "ChargedShots";
    private boolean startSoundPlayed = false;
    private boolean midLoadSoundPlayed = false;

    public RevolverCrossbowItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .durability(465));
    }

    public InteractionResultHolder<ItemStack> use(Level p_40920_, Player p_40921_, InteractionHand p_40922_) {
        ItemStack itemstack = p_40921_.getItemInHand(p_40922_);
        if (isCharged(itemstack)) {
            performShooting(p_40920_, p_40921_, p_40922_, itemstack, getShootingPower(itemstack), 1.0F);
            if (getChargeShots(itemstack) <= 0) {
                setCharged(itemstack, false);
            } else {
                setChargedShots(itemstack, getChargeShots(itemstack) - 1);
            }
            return InteractionResultHolder.consume(itemstack);
        } else if (!p_40921_.getProjectile(itemstack).isEmpty()) {
            if (!isCharged(itemstack)) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
                p_40921_.startUsingItem(p_40922_);
            }

            return InteractionResultHolder.consume(itemstack);
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    public void releaseUsing(ItemStack p_40875_, Level p_40876_, LivingEntity p_40877_, int p_40878_) {
        int i = this.getUseDuration(p_40875_) - p_40878_;
        float f = getPowerForTime(i, p_40875_);
        if (f >= 1.0F && !isCharged(p_40875_) && tryLoadProjectiles(p_40877_, p_40875_)) {
            setCharged(p_40875_, true);
            setChargedShots(p_40875_, 3);
            SoundSource soundsource = p_40877_ instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
            p_40876_.playSound(null, p_40877_.getX(), p_40877_.getY(), p_40877_.getZ(), SoundEvents.CROSSBOW_LOADING_END, soundsource, 1.0F, 1.0F / (p_40876_.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
        }

    }

    private static float getShootingPower(ItemStack p_40946_) {
        return containsChargedProjectile(p_40946_, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
    }

    public static int getChargeShots(ItemStack p_40933_) {
        CompoundTag compoundtag = p_40933_.getTag();
        if (compoundtag != null) {
            return compoundtag.getInt("ChargedShots");
        }
        return 0;
    }

    public static void setChargedShots(ItemStack p_40885_, int p_40886_) {
        CompoundTag compoundtag = p_40885_.getOrCreateTag();
        compoundtag.putInt("ChargedShots", p_40886_);
    }

    private static void addChargedProjectile(ItemStack p_40929_, ItemStack p_40930_) {
        CompoundTag compoundtag = p_40929_.getOrCreateTag();
        ListTag listtag;
        if (compoundtag.contains("ChargedProjectiles", 9)) {
            listtag = compoundtag.getList("ChargedProjectiles", 10);
        } else {
            listtag = new ListTag();
        }

        CompoundTag compoundtag1 = new CompoundTag();
        p_40930_.save(compoundtag1);
        listtag.add(compoundtag1);
        compoundtag.put("ChargedProjectiles", listtag);
    }

    private static List<ItemStack> getChargedProjectiles(ItemStack p_40942_) {
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

    private static void clearChargedProjectiles(ItemStack p_40944_) {
        CompoundTag compoundtag = p_40944_.getTag();
        if (compoundtag != null) {
            ListTag listtag = compoundtag.getList("ChargedProjectiles", 9);
            listtag.clear();
            compoundtag.put("ChargedProjectiles", listtag);
        }

    }

    private static void shootProjectile(Level p_40895_, LivingEntity p_40896_, InteractionHand p_40897_, ItemStack p_40898_, ItemStack p_40899_, float p_40900_, boolean p_40901_, float p_40902_, float p_40903_, float p_40904_) {
        if (!p_40895_.isClientSide) {
            boolean flag = p_40899_.is(Items.FIREWORK_ROCKET);
            Projectile projectile;
            if (flag) {
                projectile = new FireworkRocketEntity(p_40895_, p_40899_, p_40896_, p_40896_.getX(), p_40896_.getEyeY() - (double)0.15F, p_40896_.getZ(), true);
            } else {
                projectile = getArrow(p_40895_, p_40896_, p_40898_, p_40899_);
                if (p_40901_ || p_40904_ != 0.0F) {
                    ((AbstractArrow)projectile).pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
            }

            if (p_40896_ instanceof CrossbowAttackMob crossbowattackmob && crossbowattackmob.getTarget() != null) {
                crossbowattackmob.shootCrossbowProjectile(crossbowattackmob.getTarget(), p_40898_, projectile, p_40904_);
            } else {
                Vec3 vec31 = p_40896_.getUpVector(1.0F);
                Quaternionf quaternionf = (new Quaternionf()).setAngleAxis((double)(p_40904_ * ((float)Math.PI / 180F)), vec31.x, vec31.y, vec31.z);
                Vec3 vec3 = p_40896_.getViewVector(1.0F);
                Vector3f vector3f = vec3.toVector3f().rotate(quaternionf);
                projectile.shoot((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), p_40902_, p_40903_);
            }

            p_40898_.hurtAndBreak(flag ? 3 : 1, p_40896_, (p_40858_) -> {
                p_40858_.broadcastBreakEvent(p_40897_);
            });
            p_40895_.addFreshEntity(projectile);
            p_40895_.playSound(null, p_40896_.getX(), p_40896_.getY(), p_40896_.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, p_40900_);
        }
    }

    private static AbstractArrow getArrow(Level p_40915_, LivingEntity p_40916_, ItemStack p_40917_, ItemStack p_40918_) {
        ArrowItem arrowitem = (ArrowItem)(p_40918_.getItem() instanceof ArrowItem ? p_40918_.getItem() : Items.ARROW);
        AbstractArrow abstractarrow = arrowitem.createArrow(p_40915_, p_40918_, p_40916_);
        if (p_40916_ instanceof Player) {
            abstractarrow.setCritArrow(true);
        }

        abstractarrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
        abstractarrow.setShotFromCrossbow(true);
        int i = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.PIERCING, p_40917_);
        if (i > 0) {
            abstractarrow.setPierceLevel((byte)i);
        }

        return abstractarrow;
    }

    public static void performShooting(Level p_40888_, LivingEntity p_40889_, InteractionHand p_40890_, ItemStack crossbow, float p_40892_, float p_40893_) {
        if (p_40889_ instanceof Player player && net.minecraftforge.event.ForgeEventFactory.onArrowLoose(crossbow, p_40889_.level(), player, 1, true) < 0) return;
        List<ItemStack> list = getChargedProjectiles(crossbow);
        float[] afloat = getShotPitches(p_40889_.getRandom());

        for(int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);
            boolean flag = p_40889_ instanceof Player && ((Player)p_40889_).getAbilities().instabuild;
            if (!itemstack.isEmpty()) {
                if (i == 0) {
                    shootProjectile(p_40888_, p_40889_, p_40890_, crossbow, itemstack, afloat[i], flag, p_40892_, p_40893_, 0.0F);
                } else if (i == 1) {
                    shootProjectile(p_40888_, p_40889_, p_40890_, crossbow, itemstack, afloat[i], flag, p_40892_, p_40893_, -10.0F);
                } else if (i == 2) {
                    shootProjectile(p_40888_, p_40889_, p_40890_, crossbow, itemstack, afloat[i], flag, p_40892_, p_40893_, 10.0F);
                }
            }
        }

        onCrossbowShot(p_40888_, p_40889_, crossbow);
    }

    private static float[] getShotPitches(RandomSource p_220024_) {
        boolean flag = p_220024_.nextBoolean();
        return new float[]{1.0F, getRandomShotPitch(flag, p_220024_), getRandomShotPitch(!flag, p_220024_)};
    }

    private static float getRandomShotPitch(boolean p_220026_, RandomSource p_220027_) {
        float f = p_220026_ ? 0.63F : 0.43F;
        return 1.0F / (p_220027_.nextFloat() * 0.5F + 1.8F) + f;
    }

    private static void onCrossbowShot(Level p_40906_, LivingEntity p_40907_, ItemStack crossbow) {
        if (p_40907_ instanceof ServerPlayer serverplayer) {
            if (!p_40906_.isClientSide) {
                CriteriaTriggers.SHOT_CROSSBOW.trigger(serverplayer, crossbow);
            }

            serverplayer.awardStat(Stats.ITEM_USED.get(crossbow.getItem()));
        }

        if (getChargeShots(crossbow) <= 0) {
            clearChargedProjectiles(crossbow);
        }
    }

    public void onUseTick(Level p_40910_, LivingEntity p_40911_, ItemStack p_40912_, int p_40913_) {
        if (!p_40910_.isClientSide) {
            int i = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.QUICK_CHARGE, p_40912_);
            SoundEvent soundevent = this.getStartSound(i);
            SoundEvent soundevent1 = i == 0 ? SoundEvents.CROSSBOW_LOADING_MIDDLE : null;
            float f = (float)(p_40912_.getUseDuration() - p_40913_) / (float)getChargeDuration(p_40912_);
            if (f < 0.2F) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
            }

            if (f >= 0.2F && !this.startSoundPlayed) {
                this.startSoundPlayed = true;
                p_40910_.playSound((Player)null, p_40911_.getX(), p_40911_.getY(), p_40911_.getZ(), soundevent, SoundSource.PLAYERS, 0.5F, 1.0F);
            }

            if (f >= 0.5F && soundevent1 != null && !this.midLoadSoundPlayed) {
                this.midLoadSoundPlayed = true;
                p_40910_.playSound((Player)null, p_40911_.getX(), p_40911_.getY(), p_40911_.getZ(), soundevent1, SoundSource.PLAYERS, 0.5F, 1.0F);
            }
        }

    }

    private SoundEvent getStartSound(int p_40852_) {
        return switch (p_40852_) {
            case 1 -> SoundEvents.CROSSBOW_QUICK_CHARGE_1;
            case 2 -> SoundEvents.CROSSBOW_QUICK_CHARGE_2;
            case 3 -> SoundEvents.CROSSBOW_QUICK_CHARGE_3;
            default -> SoundEvents.CROSSBOW_LOADING_START;
        };
    }

    private static boolean tryLoadProjectiles(LivingEntity shooter, ItemStack crossbow) {
        int i = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.MULTISHOT, crossbow);
        int j = i == 0 ? 3 : 9;
        boolean flag = shooter instanceof Player player && player.getAbilities().instabuild;
        ItemStack itemstack = shooter.getProjectile(crossbow);
        ItemStack itemstack1 = itemstack.copy();

        for(int k = 0; k < j; ++k) {
            if (k > 0) {
                itemstack = itemstack1.copy();
            }

            if (itemstack.isEmpty() && flag) {
                itemstack = new ItemStack(Items.ARROW);
                itemstack1 = itemstack.copy();
            }

            if (!loadProjectile(shooter, crossbow, itemstack, k > 0, flag)) {
                return false;
            }
        }

        return true;
    }

    private static boolean loadProjectile(LivingEntity shooter, ItemStack crossbow, ItemStack arrow, boolean hasArrow, boolean creative) {
        if (arrow.isEmpty()) {
            return false;
        } else {
            boolean flag = creative && arrow.getItem() instanceof ArrowItem;
            ItemStack itemstack;
            if (!flag && !creative && !hasArrow) {
                itemstack = arrow.split(1);
                if (arrow.isEmpty() && shooter instanceof Player player) {
                    player.getInventory().removeItem(arrow);
                }
            } else {
                itemstack = arrow.copy();
            }

            addChargedProjectile(crossbow, itemstack);
            return true;
        }
    }

    private static float getPowerForTime(int p_40854_, ItemStack p_40855_) {
        float f = (float)p_40854_ / (float)getChargeDuration(p_40855_);
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }
}
