package com.Polarice3.Goety.common.listeners;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class SoulTakenListener extends SimpleJsonResourceReloadListener {
    public static Map<ResourceLocation, SoulTakenDataType> ENTITY_LIST = new HashMap<>();
    private static final Gson GSON = (new GsonBuilder()).create();

    public SoulTakenListener() {
        super(GSON, "soul_taken");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
        ENTITY_LIST.clear();
        for (int i = 0; i < objectIn.size(); i++) {
            ResourceLocation location = (ResourceLocation) objectIn.keySet().toArray()[i];
            JsonObject object = objectIn.get(location).getAsJsonObject();
            ResourceLocation entityType = null;
            ResourceLocation entityTag = null;
            if (object.has("entity_type")){
                entityType = new ResourceLocation(object.getAsJsonPrimitive("entity_type").getAsString());
            } else if (object.has("tag")){
                entityTag = new ResourceLocation(object.getAsJsonPrimitive("tag").getAsString());
            }
            int soulAmount = object.getAsJsonPrimitive("soul_amount").getAsInt();
            ENTITY_LIST.put(location, new SoulTakenDataType(entityType, entityTag, soulAmount));
        }
    }

    public static int getSoulAmount(LivingEntity victim){
        if (victim != null) {
            if (!ENTITY_LIST.isEmpty()) {
                for (SoulTakenDataType dataType : ENTITY_LIST.values()) {
                    boolean flag = false;
                    if (dataType.entityType != null) {
                        EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(dataType.entityType);
                        if (entityType != null) {
                            flag = victim.getType() == entityType;
                        }
                    } else if (dataType.entityTag != null) {
                        TagKey<EntityType<?>> tagKey = TagKey.create(ForgeRegistries.ENTITY_TYPES.getRegistryKey(), dataType.entityTag);
                        flag = victim.getType().is(tagKey);
                    }
                    if (flag) {
                        return dataType.soulAmount;
                    }
                }
            }
        }
        return 0;
    }

    public static class SoulTakenDataType{
        @Nullable
        public ResourceLocation entityType;
        @Nullable
        public ResourceLocation entityTag;
        public int soulAmount;

        public SoulTakenDataType(@Nullable ResourceLocation entityType, @Nullable ResourceLocation entityTag, int soulAmount) {
            this.entityType = entityType;
            this.entityTag = entityTag;
            this.soulAmount = soulAmount;
        }
    }
}
