package com.Polarice3.Goety.compat.curios;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.curios.SingleStackItem;
import com.Polarice3.Goety.compat.ICompatable;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;

import java.util.Map;

public class CuriosIntegration implements ICompatable {

    private static final Map<Item, String> TYPES = ImmutableMap.<Item, String>builder()
            .put(ModItems.RING_OF_WANT.get(), "ring")
            .put(ModItems.RING_OF_FORCE.get(), "ring")
            .put(ModItems.RING_OF_THE_FORGE.get(), "ring")
            .put(ModItems.RING_OF_THE_DRAGON.get(), "ring")
            .put(ModItems.DARK_HAT.get(), "head")
            .put(ModItems.GRAND_TURBAN.get(), "head")
            .put(ModItems.FROST_CROWN.get(), "head")
            .put(ModItems.WILD_CROWN.get(), "head")
            .put(ModItems.NETHER_CROWN.get(), "head")
            .put(ModItems.WITCH_HAT.get(), "head")
            .put(ModItems.WITCH_HAT_HEDGE.get(), "head")
            .put(ModItems.CRONE_HAT.get(), "head")
            .put(ModItems.NECRO_CROWN.get(), "head")
            .put(ModItems.NAMELESS_CROWN.get(), "head")
            .put(ModItems.TARGETING_MONOCLE.get(), "head")
            .put(ModItems.AMETHYST_NECKLACE.get(), "necklace")
            .put(ModItems.PENDANT_OF_HUNGER.get(), "necklace")
            .put(ModItems.STAR_AMULET.get(), "necklace")
            .put(ModItems.SEA_AMULET.get(), "necklace")
            .put(ModItems.FELINE_AMULET.get(), "necklace")
            .put(ModItems.DARK_ROBE.get(), "body")
            .put(ModItems.GRAND_ROBE.get(), "body")
            .put(ModItems.FROST_ROBE.get(), "body")
            .put(ModItems.FROST_ROBE_CRYO.get(), "body")
            .put(ModItems.WIND_ROBE.get(), "body")
            .put(ModItems.STORM_ROBE.get(), "body")
            .put(ModItems.WILD_ROBE.get(), "body")
            .put(ModItems.NETHER_ROBE.get(), "body")
            .put(ModItems.NETHER_ROBE_WARPED.get(), "body")
            .put(ModItems.ILLUSION_ROBE.get(), "body")
            .put(ModItems.ILLUSION_ROBE_MIRROR.get(), "body")
            .put(ModItems.WITCH_ROBE.get(), "body")
            .put(ModItems.WITCH_ROBE_HEDGE.get(), "body")
            .put(ModItems.WARLOCK_ROBE.get(), "body")
            .put(ModItems.WARLOCK_ROBE_DARK.get(), "body")
            .put(ModItems.NECRO_CAPE.get(), "back")
            .put(ModItems.NAMELESS_CAPE.get(), "back")
            .put(ModItems.GRAVE_GLOVE.get(), "hands")
            .put(ModItems.THRASH_GLOVE.get(), "hands")
            .put(ModItems.TOTEM_OF_ROOTS.get(), "charm")
            .put(ModItems.TOTEM_OF_SOULS.get(), "charm")
            .put(ModItems.ALARMING_CHARM.get(), "charm")
            .put(ModItems.FOCUS_BAG.get(), "belt")
            .put(ModItems.FOCUS_PACK.get(), "belt")
            .put(ModItems.BREW_BAG.get(), "belt")
            .put(ModItems.WARLOCK_SASH.get(), "belt")
            .put(ModItems.WAYFARERS_BELT.get(), "belt")
            .put(ModItems.SPITEFUL_BELT.get(), "belt")
            .build();

    public void setup(FMLCommonSetupEvent event) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::sendImc);
        MinecraftForge.EVENT_BUS.addListener(this::registerCapabilities);
    }

    private void sendImc(InterModEnqueueEvent event) {
        TYPES.values().stream().distinct().forEach(t -> InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder(t).build()));
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        TYPES.keySet().forEach(entry -> {
            if (entry instanceof SingleStackItem item) {
                CuriosApi.registerCurio(item, new SingleStackItem());
            }
        });
    }

}
