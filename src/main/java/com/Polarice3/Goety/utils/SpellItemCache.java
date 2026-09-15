package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.api.items.magic.ISpellHolder;
import com.Polarice3.Goety.api.magic.ISpell;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class SpellItemCache {
    private static Map<ISpell, List<Item>> SPELL_TO_ITEMS = null;

    public static void build() {
        Map<ISpell, List<Item>> map = new IdentityHashMap<>();
        for (Item item : ForgeRegistries.ITEMS) {
            if (item instanceof ISpellHolder holder) {
                ISpell spell = holder.getSpell();
                if (spell != null) {
                    map.computeIfAbsent(spell, s -> new ArrayList<>()).add(item);
                }
            }
        }
        SPELL_TO_ITEMS = map;
    }

    public static List<Item> itemsFor(ISpell spell) {
        if (SPELL_TO_ITEMS == null) {
            build();
        }
        return SPELL_TO_ITEMS.getOrDefault(spell, List.of());
    }
}
