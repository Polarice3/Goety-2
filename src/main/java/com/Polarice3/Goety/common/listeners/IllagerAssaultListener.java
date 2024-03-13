package com.Polarice3.Goety.common.listeners;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.events.IllagerSpawner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class IllagerAssaultListener extends SimpleJsonResourceReloadListener {
    public static Map<ResourceLocation, IllagerSpawner.IllagerDataType> ILLAGER_LIST = new HashMap<>();
    private static final Gson GSON = (new GsonBuilder()).create();

    public IllagerAssaultListener() {
        super(GSON, "illager_assault");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
        ILLAGER_LIST.clear();
        for (int i = 0; i < objectIn.size(); i++) {
            ResourceLocation location = (ResourceLocation) objectIn.keySet().toArray()[i];
            JsonObject object = objectIn.get(location).getAsJsonObject();
            String name = object.getAsJsonPrimitive("entity_type").getAsString();
            ResourceLocation resourceLocation = new ResourceLocation(name);
            if (!ForgeRegistries.ENTITY_TYPES.containsKey(resourceLocation)) {
                continue;
            }
            if (ILLAGER_LIST.containsKey(resourceLocation)) {
                Goety.LOGGER.info("entity with registry name: " + name + " already has an entry. Overwriting.");
            }
            JsonObject data = object.getAsJsonObject("registry");
            float thresholdTimes = data.getAsJsonPrimitive("threshold_times").getAsFloat();
            int max = data.getAsJsonPrimitive("max").getAsInt();
            int extra = data.getAsJsonPrimitive("extra").getAsInt();
            float chance = data.getAsJsonPrimitive("chance").getAsFloat();
            ILLAGER_LIST.put(resourceLocation, new IllagerSpawner.IllagerDataType(thresholdTimes, max, extra, chance));
        }
    }
}
