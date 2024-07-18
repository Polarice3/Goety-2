package com.Polarice3.Goety.compat.jade;

import net.minecraftforge.fml.ModList;

public enum JadeLoaded {
    JADE("jade");
    private final boolean loaded;

    JadeLoaded(String modid) {
        this.loaded = ModList.get() != null && ModList.get().getModContainerById(modid).isPresent();
    }

    public boolean isLoaded() {
        return this.loaded;
    }

}
