package com.Polarice3.Goety.common.research;

import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ResearchList {
    public static Research FORBIDDEN = new Research("forbidden");
    public static Research RAVAGING = new Research("ravaging");
    public static Research WARRED = new Research("warred");
    public static Research BURIED = new Research("buried");
    public static Research HAUNTING = new Research("haunting");
    public static Research FRONT = new Research("front");

    public static Map<String, Research> getResearchList(){
        Map<String, Research> researches = Maps.newHashMap();
        researches.put(FORBIDDEN.getId(), FORBIDDEN);
        researches.put(RAVAGING.getId(), RAVAGING);
        researches.put(WARRED.getId(), WARRED);
        researches.put(BURIED.getId(), BURIED);
        researches.put(HAUNTING.getId(), HAUNTING);
        researches.put(FRONT.getId(), FRONT);
        return researches;
    }

    public static Map<ResourceLocation, Research> getResearchIdList(){
        Map<ResourceLocation, Research> researches = Maps.newHashMap();
        for (Research research : getResearchList().values()){
            researches.put(research.getLocation(), research);
        }
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
