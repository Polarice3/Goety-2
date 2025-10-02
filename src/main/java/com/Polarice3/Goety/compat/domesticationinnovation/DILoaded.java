package com.Polarice3.Goety.compat.domesticationinnovation;

import net.minecraftforge.fml.ModList;

public enum DILoaded {
    DOMESTICATION_INNOVATION("domesticationinnovation");
    private final boolean loaded;

    DILoaded(String modid) {
        this.loaded = ModList.get() != null && ModList.get().getModContainerById(modid).isPresent();
    }

    public boolean isLoaded() {
        return this.loaded;
    }
}
