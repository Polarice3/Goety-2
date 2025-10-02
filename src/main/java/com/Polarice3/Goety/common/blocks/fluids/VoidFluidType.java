package com.Polarice3.Goety.common.blocks.fluids;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.init.ModTags;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class VoidFluidType extends FluidType {
    public static final ResourceLocation FLUID_STILL = Goety.location("block/void_still");
    public static final ResourceLocation FLUID_FLOWING = Goety.location("block/void_flow");
    public static final ResourceLocation OVERLAY = Goety.location("textures/gui/under_void.png");

    public VoidFluidType() {
        super(FluidType.Properties.create()
                .descriptionId("block.goety.void_fluid")
                .canSwim(false)
                .canDrown(false)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                .pathType(BlockPathTypes.LAVA)
                .adjacentPathType(BlockPathTypes.DANGER_OTHER)
                .density(3000)
                .viscosity(6000)
                .temperature(1300));
    }

    @Override
    public double motionScale(Entity entity) {
        return entity.level().dimension() == Level.END ? 0.007D : 0.0023333333333333335D;
    }

    public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
        boolean flag = entity.getDeltaMovement().y <= 0.0D;
        double d8 = entity.getY();
        entity.moveRelative(0.02F, movementVector);
        entity.move(MoverType.SELF, entity.getDeltaMovement());
        if (entity.getFluidTypeHeight(ModFluids.VOID_FLUID_TYPE.get()) <= entity.getFluidJumpThreshold()) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.5D, (double)0.8F, 0.5D));
            Vec3 vec33 = entity.getFluidFallingAdjustedMovement(gravity, flag, entity.getDeltaMovement());
            entity.setDeltaMovement(vec33);
        } else {
            entity.setDeltaMovement(entity.getDeltaMovement().scale(0.5D));
        }

        if (!entity.isNoGravity()) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, -gravity / 4.0D, 0.0D));
        }

        Vec3 vec34 = entity.getDeltaMovement();
        if (entity.horizontalCollision && entity.isFree(vec34.x, vec34.y + (double)0.6F - entity.getY() + d8, vec34.z)) {
            entity.setDeltaMovement(vec34.x, (double)0.3F, vec34.z);
        }
        return true;
    }

    @Override
    public void setItemMovement(ItemEntity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        entity.setDeltaMovement(vec3.x * (double)0.95F, vec3.y + (double)(vec3.y < (double)0.06F ? 5.0E-4F : 0.0F), vec3.z * (double)0.95F);
    }

    @Override
    public boolean canPushEntity(Entity entity) {
        return !entity.getType().is(ModTags.EntityTypes.VOID_TOUCHED_IMMUNE);
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
                return new Vector3f(16.0F / 255.0F, 0.0F, 24.0F / 255.0F);
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
