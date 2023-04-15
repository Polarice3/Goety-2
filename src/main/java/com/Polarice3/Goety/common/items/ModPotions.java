package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.effects.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions {
    public static DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, Goety.MOD_ID);

    public static void init(){
        ModPotions.POTIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<Potion> CLIMBING = POTIONS.register("climbing", () -> new Potion(new MobEffectInstance(ModEffects.CLIMBING.get(), 3600)));
    public static final RegistryObject<Potion> LONG_CLIMBING = POTIONS.register("long_climbing", () -> new Potion("climbing", new MobEffectInstance(ModEffects.CLIMBING.get(), 9600)));
}
