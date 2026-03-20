package com.Polarice3.Goety.common.items.curios;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class WayfarersBeltItem extends SingleStackItem {

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
                                                                        UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
        map.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(UUID.fromString("f46dd333-63a3-4c3b-a5d3-065de1e226cd"), "Wayfarer Speed bonus", 0.15F, AttributeModifier.Operation.MULTIPLY_TOTAL));
        map.put(ForgeMod.STEP_HEIGHT_ADDITION.get(), new AttributeModifier(UUID.fromString("532a87ef-73fc-40c3-a950-ee26bbbcd2d7"), "Wayfarer Step Height bonus", 1.0625F, AttributeModifier.Operation.ADDITION));
        map.put(ForgeMod.SWIM_SPEED.get(), new AttributeModifier(UUID.fromString("b2e95923-1ce4-49c1-b110-5ceb2f428df8"), "Wayfarer Swim bonus", 0.0175F, AttributeModifier.Operation.ADDITION));
        return map;
    }
}
