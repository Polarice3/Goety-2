package com.Polarice3.Goety.common.blocks.fluids;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class EndMudFluidType extends FluidType {
    public static final ResourceLocation FLUID_STILL = Goety.location("block/end_mud_still");
    public static final ResourceLocation FLUID_FLOWING = Goety.location("block/end_mud_flow");
    public static final ResourceLocation OVERLAY = Goety.location("textures/gui/under_end_mud.png");

    public EndMudFluidType() {
        super(FluidType.Properties.create()
                .descriptionId("block.goety.end_mud_fluid")
                .canSwim(true)
                .canDrown(true)
                .canConvertToSource(true)
                .supportsBoating(true)
                .canExtinguish(true)
                .motionScale(0.005F)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .pathType(BlockPathTypes.OPEN)
                .adjacentPathType(BlockPathTypes.OPEN)
                .fallDistanceModifier(0.0F)
                .viscosity(1400));
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
            public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                return OVERLAY;
            }

            @Override
            public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                return new Vector3f(140.0F / 255.0F, 140.0F / 255.0F, 140.0F / 255.0F);
            }

            @Override
            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
                Entity entity = camera.getEntity();
                if (entity.isSpectator()) {
                    nearDistance = -8.0F;
                    farDistance = renderDistance * 0.5F;
                } else if (entity instanceof LivingEntity livingEntity && CuriosFinder.hasVoidRobe(livingEntity)) {
                    nearDistance = 0.0F;
                    farDistance = 3.0F;
                } else {
                    nearDistance = 0.25F;
                    farDistance = 1.0F;
                }
                RenderSystem.setShaderFogStart(nearDistance);
                RenderSystem.setShaderFogEnd(farDistance);
                RenderSystem.setShaderFogShape(shape);
            }
        });
    }
}
