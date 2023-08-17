package com.Polarice3.Goety.common.research;

import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ResearchList {
    public static Research FORBIDDEN = new Research("forbidden");
    public static Research RAVAGING = new Research("ravaging");
    public static Research WARRED = new Research("warred");

    public static Map<String, Research> getResearchList(){
        Map<String, Research> researches = Maps.newHashMap();
        researches.put(FORBIDDEN.getId(), FORBIDDEN);
        researches.put(RAVAGING.getId(), RAVAGING);
        researches.put(WARRED.getId(), WARRED);
        return researches;
    }

    public static Map<ResourceLocation, Research> getResearchIdList(){
        Map<ResourceLocation, Research> researches = Maps.newHashMap();
        researches.put(FORBIDDEN.getLocation(), FORBIDDEN);
        researches.put(RAVAGING.getLocation(), RAVAGING);
        researches.put(WARRED.getLocation(), WARRED);
        return researches;
    }

    public static Research getResearch(ResourceLocation resourceLocation){
        if (getResearchIdList().containsKey(resourceLocation)){
            return getResearchIdList().get(resourceLocation);
        }
        return null;
    }

    public static Research getResearch(String id){
        if (getResearchList().containsKey(id)){
            return getResearchList().get(id);
        }
        return null;
    }
}
