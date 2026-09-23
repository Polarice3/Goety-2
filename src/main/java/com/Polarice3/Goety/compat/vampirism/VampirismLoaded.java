package com.Polarice3.Goety.compat.vampirism;

import net.minecraftforge.fml.ModList;

public enum VampirismLoaded {
    VAMPIRISM("vampirism");
    private final boolean loaded;

    VampirismLoaded(String modid) {
        this.loaded = ModList.get() != null && ModList.get().getModContainerById(modid).isPresent();
    }

    public boolean isLoaded() {
        return this.loaded;
    }

}
