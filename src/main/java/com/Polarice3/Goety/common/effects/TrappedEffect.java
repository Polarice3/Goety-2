package com.Polarice3.Goety.common.effects;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TrappedEffect extends GoetyBaseEffect {
    public TrappedEffect() {
        super(MobEffectCategory.HARMFUL, 5064781);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, "7ad88b67-392d-4d87-a6a5-08c08c2f86eb", (double)-2.0F, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }
}
