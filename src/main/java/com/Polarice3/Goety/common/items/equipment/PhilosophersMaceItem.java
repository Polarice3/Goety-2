package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.items.ISoulRepair;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.init.ModTags;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class PhilosophersMaceItem extends Item implements Vanishable, ISoulRepair {
    private final Multimap<Attribute, AttributeModifier> maceAttributes;

    public PhilosophersMaceItem() {
        super(new Properties().rarity(Rarity.UNCOMMON).durability(MainConfig.PhilosophersMaceDurability.get()).tab(Goety.TAB).fireResistant());
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", MainConfig.PhilosophersMaceDamage.get() - 1.0D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", (double)-2.4F, AttributeModifier.Operation.ADDITION));
        this.maceAttributes = builder.build();
    }

    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, (entity) -> {
            entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        int i2 = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.MOB_LOOTING, stack);
        target.addEffect(new MobEffectInstance(GoetyEffects.GOLD_TOUCHED.get(), 300, i2));
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
        return material == Material.STONE || material == Material.METAL || material == Material.HEAVY_METAL || material == Material.AMETHYST || pBlock.is(BlockTags.MINEABLE_WITH_PICKAXE) || pBlock.is(BlockTags.MINEABLE_WITH_AXE) || pBlock.is(BlockTags.MINEABLE_WITH_HOE) || pBlock.is(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    public float getDestroySpeed(ItemStack p_41004_, BlockState p_41005_) {
        float blockHard = p_41005_.getBlock().defaultDestroyTime();
        if (p_41005_.is(ModTags.Blocks.PHILOSOPHERS_MACE_HARD)){
            return 1.0F;
        } else if (this.isCorrectToolForDrops(p_41004_, p_41005_) && blockHard >= 1.0F) {
            return 8.0F * blockHard;
        } else {
            return 8.0F;
        }
    }

    public int getEnchantmentValue() {
        return MainConfig.PhilosophersMaceEnchantability.get();
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return (enchantment.category == EnchantmentCategory.DIGGER
                || enchantment.category == EnchantmentCategory.WEAPON
                || enchantment.category == EnchantmentCategory.BREAKABLE
                || enchantment == Enchantments.MOB_LOOTING) 
                && !(enchantment instanceof SweepingEdgeEnchantment);
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot, ItemStack itemStack) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? this.maceAttributes : super.getAttributeModifiers(equipmentSlot, itemStack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
    }

    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.getItem() == ModItems.DARK_METAL_INGOT.get();
    }

}
