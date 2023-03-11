package com.Polarice3.Goety.client.inventory.container;

import com.Polarice3.Goety.Goety;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainerType {
    public static DeferredRegister<MenuType<?>> CONTAINER_TYPE = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Goety.MOD_ID);

    public static final RegistryObject<MenuType<SoulItemContainer>> WAND = CONTAINER_TYPE.register("wand",
        () -> IForgeMenuType.create(SoulItemContainer::createContainerClientSide));

    public static final RegistryObject<MenuType<FocusBagContainer>> FOCUS_BAG = CONTAINER_TYPE.register("focus_bag",
            () -> IForgeMenuType.create(FocusBagContainer::createContainerClientSide));

    public static final RegistryObject<MenuType<WandandBagContainer>> WAND_AND_BAG = CONTAINER_TYPE.register("wand_and_bag",
            () -> IForgeMenuType.create(WandandBagContainer::createContainerClientSide));
}
