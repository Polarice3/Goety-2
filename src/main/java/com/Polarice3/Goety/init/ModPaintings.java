package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPaintings {
    public static final DeferredRegister<PaintingVariant> PAINTING_VARIANTS = DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, Goety.MOD_ID);

    public static void init(){
        ModPaintings.PAINTING_VARIANTS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<PaintingVariant> APOSTLE = PAINTING_VARIANTS.register("apostle", () -> new PaintingVariant(16, 32));
    public static final RegistryObject<PaintingVariant> MINISTER = PAINTING_VARIANTS.register("minister", () -> new PaintingVariant(48, 32));
    public static final RegistryObject<PaintingVariant> REVELATION = PAINTING_VARIANTS.register("revelation", () -> new PaintingVariant(32, 32));
    public static final RegistryObject<PaintingVariant> WRAITH = PAINTING_VARIANTS.register("wraith", () -> new PaintingVariant(32, 32));

}
