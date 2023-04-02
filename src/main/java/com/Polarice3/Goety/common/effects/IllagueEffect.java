package com.Polarice3.Goety.common.effects;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class IllagueEffect extends ModEffect {

    public IllagueEffect() {
        super(MobEffectCategory.HARMFUL, 9804699);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }
}
