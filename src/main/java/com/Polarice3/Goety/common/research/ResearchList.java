package com.Polarice3.Goety.common.research;

import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ResearchList {
    public static Research FORBIDDEN = new Research("forbidden");
    public static Research RAVAGING = new Research("ravaging");
    public static Research FROST = new Research("frost");
    public static Research REPLICATION = new Research("replication");

    public static Research getResearch(ResourceLocation resourceLocation){
        Map<ResourceLocation, Research> researches = Maps.newHashMap();
        researches.put(FORBIDDEN.getLocation(), FORBIDDEN);
        researches.put(RAVAGING.getLocation(), RAVAGING);
        researches.put(FROST.getLocation(), FROST);
        researches.put(REPLICATION.getLocation(), REPLICATION);
        if (researches.containsKey(resourceLocation)){
            return researches.get(resourceLocation);
        }
        return null;
    }

    public static Research getResearch(String id){
        Map<String, Research> researches = Maps.newHashMap();
        researches.put(FORBIDDEN.getId(), FORBIDDEN);
        researches.put(RAVAGING.getId(), RAVAGING);
        researches.put(FROST.getId(), FROST);
        researches.put(REPLICATION.getId(), REPLICATION);
        if (researches.containsKey(id)){
            return researches.get(id);
        }
        return null;
    }
}
