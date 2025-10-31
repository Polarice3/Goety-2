package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModBanners {
    public static final DeferredRegister<BannerPattern> BANNER_PATTERNS = DeferredRegister.create(Registries.BANNER_PATTERN, Goety.MOD_ID);

    public static void init(){
        BANNER_PATTERNS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<BannerPattern> CROSS = create("cross");
    public static final RegistryObject<BannerPattern> GALE = create("gale");
    public static final RegistryObject<BannerPattern> MOON = create("moon");

    private static RegistryObject<BannerPattern> create(String name) {
        return BANNER_PATTERNS.register(name, () -> new BannerPattern(name));
    }
}