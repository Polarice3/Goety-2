package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class HellfireTextures {
    public static final Map<Integer, ResourceLocation> TEXTURES = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, location("fire1.png"));
        map.put(1, location("fire2.png"));
        map.put(2, location("fire3.png"));
        map.put(3, location("fire4.png"));
        map.put(4, location("fire5.png"));
        map.put(5, location("fire6.png"));
        map.put(6, location("fire7.png"));
        map.put(7, location("fire8.png"));
        map.put(8, location("fire9.png"));
        map.put(9, location("fire10.png"));
        map.put(10, location("fire11.png"));
        map.put(11, location("fire12.png"));
        map.put(12, location("fire13.png"));
        map.put(13, location("fire14.png"));
        map.put(14, location("fire15.png"));
        map.put(15, location("fire16.png"));
        map.put(16, location("fire17.png"));
        map.put(17, location("fire18.png"));
        map.put(18, location("fire19.png"));
        map.put(19, location("fire20.png"));
        map.put(20, location("fire21.png"));
        map.put(21, location("fire22.png"));
        map.put(22, location("fire23.png"));
        map.put(23, location("fire24.png"));
        map.put(24, location("fire25.png"));
        map.put(25, location("fire26.png"));
        map.put(26, location("fire27.png"));
        map.put(27, location("fire28.png"));
        map.put(28, location("fire29.png"));
        map.put(29, location("fire30.png"));
        map.put(30, location("fire31.png"));
        map.put(31, location("fire32.png"));
    });

    public static ResourceLocation location(String path) {
        return Goety.location("textures/entity/projectiles/hellfire/" + path);
    }
}
