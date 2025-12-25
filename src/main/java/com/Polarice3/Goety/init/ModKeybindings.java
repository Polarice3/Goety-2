package com.Polarice3.Goety.init;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;

public class ModKeybindings {
    public static KeyMapping[] keyBindings = new KeyMapping[16];

    public static void init(){
        keyBindings[0] = new KeyMapping("key.goety.wand", GLFW.GLFW_KEY_Z, "key.goety.category");
        keyBindings[1] = new KeyMapping("key.goety.focusCircle", GLFW.GLFW_KEY_X, "key.goety.category");
        keyBindings[2] = new KeyMapping("key.goety.bag", GLFW.GLFW_KEY_C, "key.goety.category");
        keyBindings[3] = new KeyMapping("key.goety.witch.robe", GLFW.GLFW_KEY_V, "key.goety.witch.category");
        keyBindings[4] = new KeyMapping("key.goety.ceaseFire", GLFW.GLFW_KEY_B, "key.goety.category");
        keyBindings[5] = new KeyMapping("key.goety.lich.magnet", GLFW.GLFW_KEY_R, "key.goety.lich.category");
        keyBindings[6] = new KeyMapping("key.goety.lich.nightVision", GLFW.GLFW_KEY_M, "key.goety.lich.category");
        keyBindings[7] = new KeyMapping("key.goety.witch.extractPotions", GLFW.GLFW_KEY_G, "key.goety.witch.category");
        keyBindings[8] = new KeyMapping("key.goety.witch.brewBag", GLFW.GLFW_KEY_H, "key.goety.witch.category");
        keyBindings[9] = new KeyMapping("key.goety.witch.brewCircle", GLFW.GLFW_KEY_J, "key.goety.witch.category");
        keyBindings[10] = new KeyMapping("key.goety.mount.roar", GLFW.GLFW_KEY_R, "key.goety.mount.category");
        keyBindings[11] = new KeyMapping("key.goety.mount.freeRoam", GLFW.GLFW_KEY_H, "key.goety.mount.category");
        keyBindings[12] = new KeyMapping("key.goety.lich.lichForm", GLFW.GLFW_KEY_N, "key.goety.lich.category");
        keyBindings[13] = new KeyMapping("key.goety.lich.laugh", GLFW.GLFW_KEY_K, "key.goety.lich.category");
        keyBindings[14] = new KeyMapping("key.goety.activate_curio", GLFW.GLFW_KEY_G, "key.goety.category");
        keyBindings[15] = new KeyMapping("key.goety.dismiss", GLFW.GLFW_KEY_KP_DECIMAL, "key.goety.category");

        for (KeyMapping keyBinding : keyBindings) {
            Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings, keyBinding);
        }
    }

    public static KeyMapping wandSlot(){
        if (keyBindings[0] != null) {
            return keyBindings[0];
        } else {
            return null;
        }
    }

    public static KeyMapping wandCircle(){
        if (keyBindings[1] != null) {
            return keyBindings[1];
        } else {
            return null;
        }
    }

    public static KeyMapping brewCircle(){
        if (keyBindings[9] != null) {
            return keyBindings[9];
        } else {
            return null;
        }
    }

}
