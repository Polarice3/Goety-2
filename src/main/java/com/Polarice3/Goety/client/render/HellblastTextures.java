package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class HellblastTextures {
    public static final Map<Integer, ResourceLocation> TEXTURES = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, location("hell_blast1.png"));
        map.put(1, location("hell_blast2.png"));
        map.put(2, location("hell_blast3.png"));
        map.put(3, location("hell_blast4.png"));
        map.put(4, location("hell_blast5.png"));
        map.put(5, location("hell_blast6.png"));
        map.put(6, location("hell_blast7.png"));
        map.put(7, location("hell_blast8.png"));
        map.put(8, location("hell_blast9.png"));
        map.put(9, location("hell_blast10.png"));
        map.put(10, location("hell_blast11.png"));
        map.put(11, location("hell_blast12.png"));
        map.put(12, location("hell_blast13.png"));
        map.put(13, location("hell_blast14.png"));
        map.put(14, location("hell_blast15.png"));
        map.put(15, location("hell_blast16.png"));
        map.put(16, location("hell_blast17.png"));
        map.put(17, location("hell_blast18.png"));
        map.put(18, location("hell_blast19.png"));
        map.put(19, location("hell_blast20.png"));
        map.put(20, location("hell_blast21.png"));
        map.put(21, location("hell_blast22.png"));
        map.put(22, location("hell_blast23.png"));
        map.put(23, location("hell_blast24.png"));
        map.put(24, location("hell_blast25.png"));
        map.put(25, location("hell_blast26.png"));
        map.put(26, location("hell_blast27.png"));
        map.put(27, location("hell_blast28.png"));
        map.put(28, location("hell_blast29.png"));
        map.put(29, location("hell_blast30.png"));
        map.put(30, location("hell_blast31.png"));
        map.put(31, location("hell_blast32.png"));
    });

    public static ResourceLocation location(String path) {
        return Goety.location("textures/entity/projectiles/hell_blast/" + path);
    }
}
