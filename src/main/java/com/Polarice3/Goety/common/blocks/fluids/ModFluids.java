package com.Polarice3.Goety.common.blocks.fluids;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidInteractionRegistry;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Goety.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Goety.MOD_ID);

    public static void init(){
        ModFluids.FLUID_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModFluids.FLUIDS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<FluidType> VOID_FLUID_TYPE = FLUID_TYPES.register("void", VoidFluidType::new);
    public static final RegistryObject<FlowingFluid> VOID_FLUID_SOURCE = FLUIDS.register("void", VoidFluid.Source::new);
    public static final RegistryObject<FlowingFluid> VOID_FLUID_FLOWING = FLUIDS.register("void_flowing", VoidFluid.Flowing::new);

    public static void interactionInit() {
        // Water -> Void = Void Block (Source Void) / End Soil (Flowing Void)
        FluidInteractionRegistry.addInteraction(VOID_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                ForgeMod.WATER_TYPE.get(),
                fluidState -> fluidState.isSource() ? ModBlocks.VOID_BLOCK.get().defaultBlockState() : ModBlocks.END_SOIL.get().defaultBlockState()
        ));

        // Void -> Water = End Rock (Source Water) / End Soil (Flowing Water)
        FluidInteractionRegistry.addInteraction(ForgeMod.WATER_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                VOID_FLUID_TYPE.get(),
                fluidState -> fluidState.isSource() ? ModBlocks.END_ROCK.get().defaultBlockState() : ModBlocks.END_SOIL.get().defaultBlockState()
        ));

        // Void -> Lava = End Basalt (Source Void) / End Basalt (Flowing Void)
        FluidInteractionRegistry.addInteraction(VOID_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                ForgeMod.LAVA_TYPE.get(),
                fluidState -> ModBlocks.END_BASALT.get().defaultBlockState()
        ));

        // Lava -> Void = End Basalt (Source Lava) / End Basalt (Flowing Lava)
        FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                VOID_FLUID_TYPE.get(),
                fluidState -> ModBlocks.END_BASALT.get().defaultBlockState()
        ));

        FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(ModBlocks.END_SOIL.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                Blocks.OBSIDIAN.defaultBlockState()
        ));
    }
}
