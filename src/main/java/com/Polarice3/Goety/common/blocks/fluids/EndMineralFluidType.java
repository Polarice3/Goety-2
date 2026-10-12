package com.Polarice3.Goety.common.blocks.fluids;

import com.Polarice3.Goety.Goety;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class EndMineralFluidType extends FluidType {
    public static final ResourceLocation FLUID_STILL = Goety.location("block/end_mineral_still");
    public static final ResourceLocation FLUID_FLOWING = Goety.location("block/end_mineral_flow");
    public static final ResourceLocation OVERLAY = Goety.location("textures/gui/under_end_mineral.png");
    private static final float MIN_ALPHA = 0.45F;

    public EndMineralFluidType() {
        super(Properties.create()
                .descriptionId("block.goety.end_mineral_fluid")
                .canSwim(true)
                .canDrown(true)
                .canConvertToSource(true)
                .supportsBoating(true)
                .canExtinguish(true)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .pathType(BlockPathTypes.OPEN)
                .adjacentPathType(BlockPathTypes.OPEN)
                .fallDistanceModifier(0.0F)
                .density(15)
                .viscosity(5));
    }

    private static int depthTint(FluidState state, BlockAndTintGetter level, BlockPos pos) {
        float total = level.getFluidState(pos.above()).getType().isSame(state.getType()) ? 1.0F : 0.5F;
        float weights = 1.0F;
        float fade = Mth.clamp(total / weights, 0.0F, 1.0F);
        int alpha = Mth.floor(Mth.lerp(fade, MIN_ALPHA, 1.0F) * 255.0F + 0.5F);
        return alpha << 24 | 0xFFFFFF;
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return FLUID_STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return FLUID_FLOWING;
            }

            @Override
            public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                return depthTint(state, getter, pos);
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                return OVERLAY;
            }

            @Override
            public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                return new Vector3f(0.55F, 1.0F, 0.0F);
            }

            @Override
            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
                nearDistance = 0.0F;
                farDistance = 3.0F;
                RenderSystem.setShaderFogStart(nearDistance);
                RenderSystem.setShaderFogEnd(farDistance);
                RenderSystem.setShaderFogShape(shape);
            }
        });
    }
}
