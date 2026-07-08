package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class UnguentItem extends Item {
    public List<MobEffectInstance> instances;

    public UnguentItem(MobEffectInstance... instances) {
        this(List.of(instances));
    }

    public UnguentItem(List<MobEffectInstance> instances) {
        this(new Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16), instances);
    }

    public UnguentItem(Properties properties, MobEffectInstance... instances) {
        this(properties, List.of(instances));
    }

    public UnguentItem(Properties properties, List<MobEffectInstance> instances) {
        super(properties);
        this.instances = instances;
    }

    public ItemStack finishUsingItem(ItemStack p_41348_, Level p_41349_, LivingEntity p_41350_) {
        super.finishUsingItem(p_41348_, p_41349_, p_41350_);
        if (!(p_41350_ instanceof Player) || !((Player)p_41350_).getAbilities().instabuild) {
            p_41348_.shrink(1);
        }

        if (!p_41349_.isClientSide) {
            if (!this.instances.isEmpty()) {
                for (MobEffectInstance instance : this.instances) {
                    p_41350_.addEffect(instance);
                }
            }
        }

        if (p_41348_.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        } else {
            if (p_41350_ instanceof Player player && !player.getAbilities().instabuild) {
                ItemStack itemstack = new ItemStack(Items.GLASS_BOTTLE);
                if (!player.getInventory().add(itemstack)) {
                    player.drop(itemstack, false);
                }
            }

            return p_41348_;
        }
    }

    public int getUseDuration(ItemStack p_41360_) {
        return 100;
    }

    public UseAnim getUseAnimation(ItemStack p_41358_) {
        return UseAnim.BRUSH;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int useRemain) {
        if (itemStack.is(this)) {
            boolean flag = useRemain >= 0;
            if (flag) {
                int i = this.getUseDuration(itemStack) - useRemain + 1;
                if (i % 10 == 5) {
                    ServerParticleUtil.spawnItemParticles(livingEntity, new ItemStack(Items.SLIME_BALL), 5);
                    livingEntity.playSound(SoundEvents.SLIME_BLOCK_PLACE, 0.5F + 0.5F * (float) level.getRandom().nextInt(2), (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F + 1.0F);
                }
            }
        }
        super.onUseTick(level, livingEntity, itemStack, useRemain);
    }

    public InteractionResultHolder<ItemStack> use(Level p_41352_, Player p_41353_, InteractionHand p_41354_) {
        return ItemUtils.startUsingInstantly(p_41352_, p_41353_, p_41354_);
    }
}
