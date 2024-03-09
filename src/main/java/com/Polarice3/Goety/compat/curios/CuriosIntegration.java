package com.Polarice3.Goety.compat.curios;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.compat.ICompatable;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class CuriosIntegration implements ICompatable {

    private static final Map<Item, String> TYPES = ImmutableMap.<Item, String>builder()
            .put(ModItems.RING_OF_WANT.get(), "ring")
            .put(ModItems.RING_OF_FORCE.get(), "ring")
            .put(ModItems.RING_OF_THE_DRAGON.get(), "ring")
            .put(ModItems.DARK_HAT.get(), "head")
            .put(ModItems.GRAND_TURBAN.get(), "head")
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
            .put(ModItems.ILLUSION_ROBE.get(), "body")
            .put(ModItems.ILLUSION_ROBE_MIRROR.get(), "body")
            .put(ModItems.WITCH_ROBE.get(), "body")
            .put(ModItems.WITCH_ROBE_HEDGE.get(), "body")
            .put(ModItems.WARLOCK_ROBE.get(), "body")
            .put(ModItems.WARLOCK_ROBE_DARK.get(), "body")
            .put(ModItems.NECRO_CAPE.get(), "back")
            .put(ModItems.NAMELESS_CAPE.get(), "back")
            .put(ModItems.GRAVE_GLOVE.get(), "hands")
            .put(ModItems.TOTEM_OF_ROOTS.get(), "charm")
            .put(ModItems.TOTEM_OF_SOULS.get(), "charm")
            .put(ModItems.FOCUS_BAG.get(), "belt")
            .put(ModItems.BREW_BAG.get(), "belt")
            .put(ModItems.WARLOCK_SASH.get(), "belt")
            .put(ModItems.WAYFARERS_BELT.get(), "belt")
            .put(ModItems.SPITEFUL_BELT.get(), "belt")
            .build();

    public void setup(FMLCommonSetupEvent event) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::sendImc);
        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, this::onCapabilitiesAttach);
    }

    private void sendImc(InterModEnqueueEvent event) {
        TYPES.values().stream().distinct().forEach(t -> InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder(t).build()));
    }

    private void onCapabilitiesAttach(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (TYPES.containsKey(stack.getItem())) {
            event.addCapability(new ResourceLocation(Goety.MOD_ID, "curios"), new ICapabilityProvider() {
                private final LazyOptional<ICurio> curio = LazyOptional.of(() -> new ICurio() {
                    @Override
                    public void curioTick(SlotContext slotContext) {
                        stack.getItem().inventoryTick(stack, slotContext.entity().level, slotContext.entity(), -1, false);

                    }

                    @Override
                    public ItemStack getStack() {
                        return stack;
                    }

                    @Override
                    public boolean canEquipFromUse(SlotContext slotContext) {
                        return true;
                    }

                    @Override
                    public boolean canSync(SlotContext slotContext) {
                        return true;
                    }
                });

                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                    if (cap != CuriosCapability.ITEM)
                        return LazyOptional.empty();
                    return this.curio.cast();
                }
            });
        }
    }

}
