package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.items.magic.IFocus;
import com.Polarice3.Goety.api.magic.ISpell;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class MagicFocus extends Item implements IFocus {
    public static final String SOUL_COST = "Soul Cost";
    public ISpell spell;
    public int soulCost;

    public MagicFocus(ISpell spell){
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

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (stack.getItem() instanceof IFocus magicFocus){
            if (magicFocus.getSpell() != null){
                if (!magicFocus.getSpell().acceptedEnchantments().isEmpty()){
                    return magicFocus.getSpell().acceptedEnchantments().contains(enchantment);
                }
            }
        }
        return false;
    }

    public ISpell getSpell(){
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
