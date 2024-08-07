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
    public static final RegistryObject<PaintingVariant> MOVIE = PAINTING_VARIANTS.register("movie", () -> new PaintingVariant(16, 32));
    public static final RegistryObject<PaintingVariant> KNUCKLES = PAINTING_VARIANTS.register("knuckles", () -> new PaintingVariant(16, 32));
    public static final RegistryObject<PaintingVariant> SATURN = PAINTING_VARIANTS.register("saturn", () -> new PaintingVariant(16, 32));
    public static final RegistryObject<PaintingVariant> WICKER = PAINTING_VARIANTS.register("wicker", () -> new PaintingVariant(16, 32));
    public static final RegistryObject<PaintingVariant> BEGGING = PAINTING_VARIANTS.register("begging", () -> new PaintingVariant(16, 32));
    public static final RegistryObject<PaintingVariant> ELDRITCH = PAINTING_VARIANTS.register("eldritch", () -> new PaintingVariant(16, 32));
    public static final RegistryObject<PaintingVariant> CRYPT = PAINTING_VARIANTS.register("crypt", () -> new PaintingVariant(32, 16));
    public static final RegistryObject<PaintingVariant> STONEDRAKE = PAINTING_VARIANTS.register("stonedrake", () -> new PaintingVariant(32, 16));
    public static final RegistryObject<PaintingVariant> LIZARDCALM = PAINTING_VARIANTS.register("lizardcalm", () -> new PaintingVariant(32, 16));
    public static final RegistryObject<PaintingVariant> MINISTER = PAINTING_VARIANTS.register("minister", () -> new PaintingVariant(48, 32));
    public static final RegistryObject<PaintingVariant> FENG = PAINTING_VARIANTS.register("feng", () -> new PaintingVariant(48, 32));
    public static final RegistryObject<PaintingVariant> FALLEN_KINGDOM = PAINTING_VARIANTS.register("fallen_kingdom", () -> new PaintingVariant(48, 32));
    public static final RegistryObject<PaintingVariant> LADIES_OF_THE_WOOD = PAINTING_VARIANTS.register("ladies_of_the_wood", () -> new PaintingVariant(48, 32));
    public static final RegistryObject<PaintingVariant> CAPSTONE = PAINTING_VARIANTS.register("capstone", () -> new PaintingVariant(48, 32));
    public static final RegistryObject<PaintingVariant> MRAEG_JOEY = PAINTING_VARIANTS.register("mraeg_joey", () -> new PaintingVariant(32, 32));
    public static final RegistryObject<PaintingVariant> MRAEG_WALLY = PAINTING_VARIANTS.register("mraeg_wally", () -> new PaintingVariant(32, 32));
    public static final RegistryObject<PaintingVariant> REVELATION = PAINTING_VARIANTS.register("revelation", () -> new PaintingVariant(32, 32));
    public static final RegistryObject<PaintingVariant> THEPANTS = PAINTING_VARIANTS.register("thepants", () -> new PaintingVariant(32, 32));
    public static final RegistryObject<PaintingVariant> MANSION = PAINTING_VARIANTS.register("mansion", () -> new PaintingVariant(32, 32));
    public static final RegistryObject<PaintingVariant> RUBY = PAINTING_VARIANTS.register("ruby", () -> new PaintingVariant(32, 32));
    public static final RegistryObject<PaintingVariant> HOUND = PAINTING_VARIANTS.register("hound", () -> new PaintingVariant(32, 32));
    public static final RegistryObject<PaintingVariant> WRAITH = PAINTING_VARIANTS.register("wraith", () -> new PaintingVariant(16, 16));
    public static final RegistryObject<PaintingVariant> BOBBY = PAINTING_VARIANTS.register("bobby", () -> new PaintingVariant(16, 16));
    public static final RegistryObject<PaintingVariant> HEART = PAINTING_VARIANTS.register("heart", () -> new PaintingVariant(16, 16));
    public static final RegistryObject<PaintingVariant> TORMENT = PAINTING_VARIANTS.register("torment", () -> new PaintingVariant(16, 16));
}
