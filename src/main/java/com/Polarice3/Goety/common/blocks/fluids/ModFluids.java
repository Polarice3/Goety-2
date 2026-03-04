package com.Polarice3.Goety.common.blocks.fluids;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.config.MainConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
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

    public static final RegistryObject<FluidType> END_MUD_FLUID_TYPE = FLUID_TYPES.register("end_mud", EndMudFluidType::new);
    public static final RegistryObject<FlowingFluid> END_MUD_FLUID_SOURCE = FLUIDS.register("end_mud", EndMudFluid.Source::new);
    public static final RegistryObject<FlowingFluid> END_MUD_FLUID_FLOWING = FLUIDS.register("end_mud_flowing", EndMudFluid.Flowing::new);

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

        // Mud -> Void = Void Block (Source Void) / End Soil (Flowing Void)
        FluidInteractionRegistry.addInteraction(VOID_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                END_MUD_FLUID_TYPE.get(),
                fluidState -> fluidState.isSource() ? ModBlocks.VOID_BLOCK.get().defaultBlockState() : ModBlocks.END_SOIL.get().defaultBlockState()
        ));

        // Void -> Mud = End Rock (Source Water) / End Soil (Flowing Water)
        FluidInteractionRegistry.addInteraction(END_MUD_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                VOID_FLUID_TYPE.get(),
                fluidState -> fluidState.isSource() ? ModBlocks.END_ROCK.get().defaultBlockState() : ModBlocks.END_SOIL.get().defaultBlockState()
        ));

        // Lava -> Void = End Basalt (Source Void) / End Basalt (Flowing Void)
        FluidInteractionRegistry.addInteraction(VOID_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                ForgeMod.LAVA_TYPE.get(),
                fluidState -> {
                    if (MainConfig.CataclysmVoidStone.get()) {
                        ResourceLocation location = new ResourceLocation("cataclysm", "void_stone");
                        if (ForgeRegistries.BLOCKS.getValue(location) != null) {
                            Block block = ForgeRegistries.BLOCKS.getValue(location);
                            if (block != null) {
                                return fluidState.isSource() ? block.defaultBlockState() : ModBlocks.END_BASALT.get().defaultBlockState();
                            }
                        }
                    }
                    return ModBlocks.END_BASALT.get().defaultBlockState();
                }
        ));

        // Void -> Lava = End Basalt (Source Lava) / End Basalt (Flowing Lava)
        FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                VOID_FLUID_TYPE.get(),
                fluidState -> ModBlocks.END_BASALT.get().defaultBlockState()
        ));

        // Mud -> Water = End Mud
        FluidInteractionRegistry.addInteraction(END_MUD_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                ForgeMod.WATER_TYPE.get(),
                fluidState -> ModBlocks.END_MUD.get().defaultBlockState()
        ));

        // Water -> Mud = End Mud
        FluidInteractionRegistry.addInteraction(ForgeMod.WATER_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                END_MUD_FLUID_TYPE.get(),
                fluidState -> ModBlocks.END_MUD.get().defaultBlockState()
        ));

        // Mud -> Lava = Obsidian (Source Lava) / End Stone (Flowing Lava)
        FluidInteractionRegistry.addInteraction(END_MUD_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                ForgeMod.LAVA_TYPE.get(),
                fluidState -> fluidState.isSource() ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.END_STONE.defaultBlockState()
        ));

        // Lava -> Mud = End Stone Slate (Source Mud) / End Stone (Flowing Mud)
        FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                END_MUD_FLUID_TYPE.get(),
                fluidState -> fluidState.isSource() ? ModBlocks.END_STONE_SLATE_BLOCK.get().defaultBlockState() : Blocks.END_STONE.defaultBlockState()
        ));

        FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(ModBlocks.END_SOIL.get()) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                Blocks.OBSIDIAN.defaultBlockState()
        ));
    }
}
