package com.Polarice3.Goety.common.effects.brew;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.DyeItem;

public class BrewColorEffect extends BrewEffect{
    public DyeItem dyeItem;

    public BrewColorEffect(DyeItem dyeItem) {
        super(dyeItem.getDescriptionId(), 0, -1, MobEffectCategory.NEUTRAL, dyeItem.getDyeColor().getTextColor(), true);
        this.dyeItem = dyeItem;
    }

    public String getDescriptionId() {
        return "effect.goety.brew_dye";
    }

}
