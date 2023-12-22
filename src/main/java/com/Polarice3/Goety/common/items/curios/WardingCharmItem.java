package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.spells.IronHideSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class WardingCharmItem extends SingleStackItem{
    private static final String SOULUSE = "Soul Use";

    public boolean SoulDiscount(LivingEntity entityLiving){
        return CuriosFinder.hasCurio(entityLiving, itemStack -> itemStack.getItem() instanceof MagicRobeItem);
    }

    public boolean SoulCostUp(LivingEntity entityLiving){
        return entityLiving.hasEffect(GoetyEffects.SUMMON_DOWN.get());
    }

    public int SoulCalculation(LivingEntity entityLiving){
        if (SoulCostUp(entityLiving)){
            int amp = Objects.requireNonNull(entityLiving.getEffect(GoetyEffects.SUMMON_DOWN.get())).getAmplifier() + 2;
            return new IronHideSpell().defaultSoulCost() * amp;
        } else if (SoulDiscount(entityLiving)){
            return new IronHideSpell().defaultSoulCost() / 2;
        } else {
            return new IronHideSpell().defaultSoulCost();
        }
    }

    public int SoulUse(LivingEntity entityLiving, ItemStack stack){
        if (stack.isEnchanted()){
            return (int) (SoulCalculation(entityLiving) * 2 * SEHelper.soulDiscount(entityLiving));
        } else {
            return (int) (SoulCalculation(entityLiving) * SEHelper.soulDiscount(entityLiving));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof LivingEntity livingEntity) {
            CompoundTag compound = stack.getOrCreateTag();
            compound.putInt(SOULUSE, SoulUse(livingEntity, stack));
            if (!worldIn.isClientSide) {
                if (livingEntity instanceof Player player) {
                    if (!player.hasEffect(GoetyEffects.SOUL_ARMOR.get())) {
                        if (SEHelper.getSoulsAmount(player, SoulUse(player, stack))) {
                            List<Mob> mobs = worldIn.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(64, 16, 64));
                            Mob hostile = mobs.stream().filter(mob -> mob.getTarget() == player).findFirst().orElse(null);
                            if (hostile != null || player.hurtTime > 0) {
                                SEHelper.decreaseSouls(player, SoulUse(player, stack));
                                SEHelper.sendSEUpdatePacket(player);
                                int enchantment = 0;
                                int duration = 1;
                                if (stack.isEnchanted()) {
                                    enchantment = this.getEnchantmentLevel(stack, ModEnchantments.POTENCY.get());
                                    duration += this.getEnchantmentLevel(stack, ModEnchantments.DURATION.get());
                                }
                                player.addEffect(new MobEffectInstance(GoetyEffects.SOUL_ARMOR.get(), MathHelper.minutesToTicks(duration), enchantment, false, false, true));
                                worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.IRON_HIDE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isEnchantable(ItemStack pStack) {
        return pStack.getCount() == 1;
    }

    public int getEnchantmentValue() {
        return 1;
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == ModEnchantments.POTENCY.get()
                || enchantment == ModEnchantments.DURATION.get();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (stack.getTag() != null) {
            int SoulUse = stack.getTag().getInt(SOULUSE);
            tooltip.add(Component.translatable("info.goety.wand.cost", SoulUse));
        }
    }
}
