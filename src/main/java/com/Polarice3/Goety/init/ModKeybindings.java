package com.Polarice3.Goety.init;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.ArrayUtils;

public class ModKeybindings {
    public static KeyMapping[] keyBindings;

    public static void init(){
        keyBindings = new KeyMapping[3];

        keyBindings[0] = new KeyMapping("key.goety.wand", 90, "key.goety.category");
        keyBindings[1] = new KeyMapping("key.goety.wandandbag", 88, "key.goety.category");
        keyBindings[2] = new KeyMapping("key.goety.bag", 67, "key.goety.category");

        for (int i = 0; i < keyBindings.length; ++i) {
            Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings, keyBindings[i]);
        }
    }


}
