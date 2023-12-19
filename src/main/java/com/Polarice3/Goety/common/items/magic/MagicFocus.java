package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.items.magic.IFocus;
import com.Polarice3.Goety.api.magic.ISpell;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class MagicFocus extends Item implements IFocus {
    public ISpell spell;
    public int soulCost;

    public MagicFocus(ISpell spell){
        super(new Properties()
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
        if (stack.getItem() instanceof MagicFocus magicFocus){
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
