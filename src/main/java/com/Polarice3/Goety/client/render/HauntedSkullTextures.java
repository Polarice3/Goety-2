package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class HauntedSkullTextures {
    public static final Map<Integer, ResourceLocation> TEXTURES = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, location("skull.png"));
        map.put(1, location("skull1.png"));
        map.put(2, location("skull2.png"));
        map.put(3, location("skull3.png"));
        map.put(4, location("skull4.png"));
        map.put(5, location("skull5.png"));
        map.put(6, location("skull6.png"));
        map.put(7, location("skull7.png"));
        map.put(8, location("skull8.png"));
        map.put(9, location("attack/skull1.png"));
        map.put(10, location("attack/skull2.png"));
        map.put(11, location("attack/skull3.png"));
        map.put(12, location("attack/skull4.png"));
        map.put(13, location("attack/skull5.png"));
        map.put(14, location("attack/skull6.png"));
        map.put(15, location("attack/skull7.png"));
        map.put(16, location("attack/skull8.png"));
    });

    public static ResourceLocation location(String path) {
        return Goety.location("textures/entity/servants/haunted_skull/" + path);
    }
}
