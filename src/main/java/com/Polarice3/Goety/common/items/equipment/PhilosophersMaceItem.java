package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.effects.ModEffects;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.SEHelper;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class PhilosophersMaceItem extends Item implements Vanishable {
    private final Multimap<Attribute, AttributeModifier> maceAttributes;

    public PhilosophersMaceItem() {
        super(new Properties().rarity(Rarity.UNCOMMON).durability(MainConfig.PhilosophersMaceDurability.get()).tab(Goety.TAB).fireResistant());
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", MainConfig.PhilosophersMaceDamage.get() - 1.0D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", (double)-2.4F, AttributeModifier.Operation.ADDITION));
        this.maceAttributes = builder.build();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (MainConfig.SoulRepair.get()) {
            if (entityIn instanceof Player player) {
                if (!(player.swinging && isSelected)) {
                    if (stack.isDamaged()) {
                        if (SEHelper.getSoulsContainer(player)){
                            if (SEHelper.getSoulsAmount(player, MainConfig.ItemsRepairAmount.get())){
                                if (player.tickCount % 20 == 0) {
                                    stack.setDamageValue(stack.getDamageValue() - 1);
                                    SEHelper.decreaseSouls(player, MainConfig.ItemsRepairAmount.get());
                                }
                            }
                        }
                    }
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, (entity) -> {
            entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        int i2 = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.MOB_LOOTING, stack);
        target.addEffect(new MobEffectInstance(ModEffects.GOLD_TOUCHED.get(), 300, i2));
        return true;
    }

    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (!pLevel.isClientSide && pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
            pStack.hurtAndBreak(1, pEntityLiving, (p_220038_0_) -> {
                p_220038_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }

        return true;
    }

    public boolean isCorrectToolForDrops(BlockState pBlock) {
        Material material = pBlock.getMaterial();
        return material == Material.STONE || material == Material.METAL || material == Material.HEAVY_METAL || pBlock.is(Blocks.SNOW) || pBlock.is(Blocks.SNOW_BLOCK);
    }

    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        return 8.0F;
    }

    public int getEnchantmentValue() {
        return 15;
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        return (enchantment.category == EnchantmentCategory.DIGGER
                || enchantment.category == EnchantmentCategory.WEAPON
                || enchantment.category == EnchantmentCategory.BREAKABLE
                || enchantment == Enchantments.MOB_LOOTING) 
                && !(enchantment instanceof SweepingEdgeEnchantment);
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? this.maceAttributes : super.getDefaultAttributeModifiers(equipmentSlot);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
    }

    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.getItem() == ModItems.CURSED_INGOT.get();
    }

}
