package com.Polarice3.Goety.init;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.ArrayUtils;

public class ModKeybindings {
    public static KeyMapping[] keyBindings;

    public static void init(){
        keyBindings = new KeyMapping[12];

        keyBindings[0] = new KeyMapping("key.goety.wand", 90, "key.goety.category");
        keyBindings[1] = new KeyMapping("key.goety.wandandbag", 88, "key.goety.category");
        keyBindings[2] = new KeyMapping("key.goety.bag", 67, "key.goety.category");
        keyBindings[3] = new KeyMapping("key.goety.witch.robe", 86, "key.goety.witch.category");
        keyBindings[4] = new KeyMapping("key.goety.ceaseFire", 66, "key.goety.category");
        keyBindings[5] = new KeyMapping("key.goety.lich.lichKiss", 82, "key.goety.lich.category");
        keyBindings[6] = new KeyMapping("key.goety.lich.magnet", 77, "key.goety.lich.category");
        keyBindings[7] = new KeyMapping("key.goety.witch.addFuel", 74, "key.goety.witch.category");
        keyBindings[8] = new KeyMapping("key.goety.witch.addCatalyst", 72, "key.goety.witch.category");
        keyBindings[9] = new KeyMapping("key.goety.witch.extractPotions", 71, "key.goety.witch.category");
        keyBindings[10] = new KeyMapping("key.goety.ravager.roar", 82, "key.goety.ravager.category");
        keyBindings[11] = new KeyMapping("key.goety.ravager.freeRoam", 89, "key.goety.ravager.category");

        for (int i = 0; i < keyBindings.length; ++i) {
            Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings, keyBindings[i]);
        }
    }


}
