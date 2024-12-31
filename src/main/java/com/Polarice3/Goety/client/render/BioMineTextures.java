package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class BioMineTextures {
    public static final Map<Integer, ResourceLocation> TEXTURES = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, location("biomine1.png"));
        map.put(1, location("biomine2.png"));
        map.put(2, location("biomine3.png"));
        map.put(3, location("biomine4.png"));
        map.put(4, location("biomine5.png"));
        map.put(5, location("biomine6.png"));
        map.put(6, location("biomine7.png"));
        map.put(7, location("biomine8.png"));
        map.put(8, location("biomine9.png"));
        map.put(9, location("biomine10.png"));
        map.put(10, location("biomine11.png"));
        map.put(11, location("biomine12.png"));
        map.put(12, location("biomine13.png"));
        map.put(13, location("biomine14.png"));
        map.put(14, location("biomine15.png"));
        map.put(15, location("biomine16.png"));
        map.put(16, location("biomine17.png"));
        map.put(17, location("biomine18.png"));
        map.put(18, location("biomine19.png"));
        map.put(19, location("biomine20.png"));
    });

    public static ResourceLocation location(String path) {
        return Goety.location("textures/entity/projectiles/biomine/" + path);
    }
}
