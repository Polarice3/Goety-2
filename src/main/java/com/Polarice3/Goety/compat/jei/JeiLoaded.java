package com.Polarice3.Goety.compat.jei;

import net.minecraftforge.fml.ModList;

public enum JeiLoaded {
    JEI("jei");
    private final boolean loaded;

    JeiLoaded(String modid) {
        this.loaded = ModList.get() != null && ModList.get().getModContainerById(modid).isPresent();
    }

    public boolean isLoaded() {
        return this.loaded;
    }
}
