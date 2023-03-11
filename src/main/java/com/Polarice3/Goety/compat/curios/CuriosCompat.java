package com.Polarice3.Goety.compat.curios;

import com.Polarice3.Goety.Goety;
import com.google.common.collect.ImmutableMap;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class CuriosCompat {
    private static final Map<String, Supplier<ICompatable>> MODULE_TYPES = ImmutableMap.<String, Supplier<ICompatable>>builder()
            .put("curios", CuriosIntegration::new)
            .build();
    private static final Map<String, ICompatable> MODULES = new HashMap<>();

    public static void setup(FMLCommonSetupEvent event) {
        populateModules(ModList.get()::isLoaded);
        MODULES.values().forEach(c -> c.setup(event));
    }

    private static void populateModules(Predicate<String> isLoaded) {
        for (Map.Entry<String, Supplier<ICompatable>> entry : MODULE_TYPES.entrySet()) {
            String id = entry.getKey();
            if (isLoaded.test(id)) {
                MODULES.put(id, entry.getValue().get());
                Goety.LOGGER.info("Loading compat module for mod " + id);
            }
        }
    }
}
