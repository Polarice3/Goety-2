package com.Polarice3.Goety.common.research;

import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ResearchList {
    public static Map<String, Research> RESEARCH_LIST = Maps.newHashMap();
    public static Research FORBIDDEN = new Research("forbidden");
    public static Research RAVAGING = new Research("ravaging");
    public static Research WARRED = new Research("warred");
    public static Research BURIED = new Research("buried");
    public static Research HAUNTING = new Research("haunting");
    public static Research FRONT = new Research("front");
    public static Research MISTRAL = new Research("mistral");
    public static Research FLORAL = new Research("floral");
    public static Research BYGONE = new Research("bygone");
    public static Research TERMINUS = new Research("terminus");

    public static void registerResearch(String id, Research research){
        RESEARCH_LIST.put(id, research);
    }

    public static Map<String, Research> getResearchList(){
        Map<String, Research> researches = Maps.newHashMap();
        researches.put(FORBIDDEN.getId(), FORBIDDEN);
        researches.put(RAVAGING.getId(), RAVAGING);
        researches.put(WARRED.getId(), WARRED);
        researches.put(BURIED.getId(), BURIED);
        researches.put(HAUNTING.getId(), HAUNTING);
        researches.put(FRONT.getId(), FRONT);
        researches.put(MISTRAL.getId(), MISTRAL);
        researches.put(FLORAL.getId(), FLORAL);
        researches.put(BYGONE.getId(), BYGONE);
        researches.put(TERMINUS.getId(), TERMINUS);
        if (!RESEARCH_LIST.isEmpty()){
            researches.putAll(RESEARCH_LIST);
        }
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
