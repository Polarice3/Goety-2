package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class SickleItem extends TieredItem implements Vanishable {
    private static float initialDamage = ItemConfig.SickleBaseDamage.get().floatValue();
    private final Multimap<Attribute, AttributeModifier> sickleAttributes;

    public SickleItem(Tier itemTier) {
        super(itemTier, new Properties().rarity(Rarity.UNCOMMON).durability(itemTier.getUses()));
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        initialDamage = ItemConfig.SickleBaseDamage.get().floatValue() + itemTier.getAttackDamageBonus();
        double attackSpeed = 4.0D - ItemConfig.SickleAttackSpeed.get();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", initialDamage - 1.0D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -attackSpeed, AttributeModifier.Operation.ADDITION));
        this.sickleAttributes = builder.build();
    }

    public SickleItem(){
        this(ModTiers.SPECIAL);
    }

    public static float getInitialDamage() {
        return initialDamage;
    }

    public boolean getMineBlocks(Level pLevel, BlockState pState, BlockPos pPos){
        if (pState.getDestroySpeed(pLevel, pPos) <= -1.0F) {
            return false;
        }
        return pState.is(BlockTags.MINEABLE_WITH_HOE) || pState.is(Blocks.COBWEB) || BlockFinder.isScytheBreak(pState);
    }

    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        if (pState.is(Blocks.COBWEB)) {
            return 15.0F;
        }
        return pState.is(BlockTags.MINEABLE_WITH_HOE) ? 8.0F : 1.0F;
    }

    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        pStack.hurtAndBreak(1, pAttacker, (p_220045_0_) ->
                p_220045_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
            pStack.hurtAndBreak(this.getMineBlocks(pLevel, pState, pPos) ? 1 : 2, pEntityLiving, (p_220044_0_) ->
                    p_220044_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        if (this.getMineBlocks(pLevel, pState, pPos)){
            pLevel.playSound((Player) null, pPos.getX(), pPos.getY(), pPos.getZ(), ModSounds.SCYTHE_HIT.get(), pEntityLiving.getSoundSource(), 1.0F, 1.0F);
            for (BlockPos blockPos : BlockFinder.multiBlockBreak(pEntityLiving, pPos, 1, 1, 1)){
                BlockState blockstate = pLevel.getBlockState(blockPos);
                if (this.getMineBlocks(pLevel, blockstate, blockPos)){
                    if (BlockFinder.breakBlock(pLevel, blockPos, pStack, pEntityLiving)){
                        if (blockstate.getDestroySpeed(pLevel, blockPos) != 0) {
                            pStack.hurtAndBreak(1, pEntityLiving, (p_220044_0_)
                                    -> p_220044_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
    }

    public boolean isCorrectToolForDrops(BlockState pBlock) {
        return pBlock.is(BlockTags.MINEABLE_WITH_HOE) || pBlock.is(Blocks.COBWEB);
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        ResourceLocation enchantmentId = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
        return (enchantment.category == EnchantmentCategory.VANISHABLE
                || enchantment.category == EnchantmentCategory.WEAPON
                || enchantment.category == EnchantmentCategory.BREAKABLE
                || enchantment.category == EnchantmentCategory.DIGGER
                || enchantment == Enchantments.MOB_LOOTING
                || enchantment == Enchantments.BLOCK_FORTUNE
                || Objects.equals(enchantmentId, new ResourceLocation("vanillatweaks", "siphon")));
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? this.sickleAttributes : super.getDefaultAttributeModifiers(equipmentSlot);
    }

    @Override
    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.getItem() == ModItems.PALE_STEEL_INGOT.get() || super.isValidRepairItem(pToRepair, pRepair);
    }
}
