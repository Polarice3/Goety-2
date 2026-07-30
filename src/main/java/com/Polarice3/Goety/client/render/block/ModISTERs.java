package com.Polarice3.Goety.client.render.block;

public class ModISTERs {
    private static ModISTER INSTANCE;
    
    public static ModISTER get() {
        if (INSTANCE == null) {
            INSTANCE = new ModISTER();
        }
        return INSTANCE;
    }
}
