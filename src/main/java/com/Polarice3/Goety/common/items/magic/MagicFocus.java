package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.Spells;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class MagicFocus extends Item {
    public static final String SOUL_COST = "Soul Cost";
    public Spells spell;
    public int soulCost;

    public MagicFocus(Spells spell){
        super(new Properties()
                .tab(Goety.TAB)
                .rarity(Rarity.UNCOMMON)
                .setNoRepair()
                .stacksTo(1)
        );
        this.spell = spell;
        this.soulCost = spell.SoulCost();
    }

    public boolean isEnchantable(ItemStack pStack) {
        return pStack.getCount() == 1;
    }

    public int getEnchantmentValue() {
        return 1;
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        if (stack.getItem() == ModItems.ICEOLOGY_FOCUS.get()
                || stack.getItem() == ModItems.LIGHTNING_FOCUS.get()){
            return enchantment == ModEnchantments.POTENCY.get()
                    || enchantment == ModEnchantments.RANGE.get();
        }
        if (stack.getItem() == ModItems.SHOCKWAVE_FOCUS.get()){
            return enchantment == ModEnchantments.POTENCY.get()
                    || enchantment == ModEnchantments.RADIUS.get();
        }
        if (stack.getItem() == ModItems.UPDRAFT_FOCUS.get()){
            return enchantment == ModEnchantments.POTENCY.get()
                    || enchantment == ModEnchantments.RADIUS.get()
                    || enchantment == ModEnchantments.RANGE.get();
        }
        if (stack.getItem() == ModItems.BITING_FOCUS.get()) {
            return enchantment == ModEnchantments.POTENCY.get()
                    || enchantment == ModEnchantments.RANGE.get()
                    || enchantment == ModEnchantments.BURNING.get()
                    || enchantment == ModEnchantments.ABSORB.get()
                    || (SpellConfig.FangGainSouls.get() > 0 && enchantment == ModEnchantments.SOUL_EATER.get());
        }
        if (stack.getItem() == ModItems.ROTTING_FOCUS.get()
                || stack.getItem() == ModItems.OSSEOUS_FOCUS.get()
                || stack.getItem() == ModItems.SPOOKY_FOCUS.get()
                || stack.getItem() == ModItems.VEXING_FOCUS.get()
                || stack.getItem() == ModItems.LAUNCH_FOCUS.get()) {
            return enchantment == ModEnchantments.POTENCY.get()
                    || enchantment == ModEnchantments.DURATION.get();
        }
        if (stack.getItem() == ModItems.FEAST_FOCUS.get()){
            return enchantment == ModEnchantments.POTENCY.get()
                    || enchantment == ModEnchantments.BURNING.get()
                    || enchantment == ModEnchantments.ABSORB.get();
        }
        if (stack.getItem() == ModItems.FIREBALL_FOCUS.get()){
            return enchantment == ModEnchantments.POTENCY.get()
                    || enchantment == ModEnchantments.BURNING.get();
        }
        if (stack.getItem() == ModItems.SKULL_FOCUS.get()){
            return enchantment == ModEnchantments.POTENCY.get()
                    || enchantment == ModEnchantments.DURATION.get()
                    || enchantment == ModEnchantments.BURNING.get()
                    || enchantment == ModEnchantments.RADIUS.get();
        }
        if (stack.getItem() == ModItems.LAVABALL_FOCUS.get()){
            return enchantment == ModEnchantments.POTENCY.get()
                    || enchantment == ModEnchantments.RADIUS.get()
                    || enchantment == ModEnchantments.BURNING.get();
        }
        if (stack.getItem() == ModItems.SOUL_BOLT_FOCUS.get()
                || stack.getItem() == ModItems.SWORD_FOCUS.get()
                || stack.getItem() == ModItems.FLYING_FOCUS.get()
                || stack.getItem() == ModItems.SONIC_BOOM_FOCUS.get()){
            return enchantment == ModEnchantments.POTENCY.get();
        }
        return false;
    }

    public Spells getSpell(){
        return this.spell;
    }

    @Override
    public void fillItemCategory(CreativeModeTab pGroup, NonNullList<ItemStack> pItems) {
        if (this.allowedIn(pGroup)){
            ItemStack stack = new ItemStack(this);
            CompoundTag compound = stack.getOrCreateTag();
            compound.putInt(SOUL_COST, soulCost);
            pItems.add(stack);
        }
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        CompoundTag compound = pStack.getOrCreateTag();
        compound.putInt(SOUL_COST, soulCost);
        super.onCraftedBy(pStack, pLevel, pPlayer);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (soulCost != 0) {
            tooltip.add(Component.translatable("info.goety.focus.cost", soulCost));
        } else {
            tooltip.add(Component.translatable("info.goety.focus.cost", 0));
        }
        tooltip.add(Component.translatable("info.goety.focus.spellType", spell.getSpellType().getName()));
    }

}
