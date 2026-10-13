package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.ModUUIDUtil;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class MagicHatItem extends SingleStackItem {

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
                                                                        UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
        if (this == ModItems.DARK_CROWN.get()){
            map.put(Attributes.ARMOR, new AttributeModifier(ModUUIDUtil.createUUID("item.goety.dark_crown.armor"), "Dark Crown Armor", 2.0F, AttributeModifier.Operation.ADDITION));
        }
        return map;
    }

}
