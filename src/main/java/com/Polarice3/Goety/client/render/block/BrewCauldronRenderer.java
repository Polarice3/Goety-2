package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.entities.BrewCauldronBlockEntity;
import com.Polarice3.Goety.common.blocks.properties.ModStateProperties;
import com.Polarice3.Goety.utils.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class BrewCauldronRenderer implements BlockEntityRenderer<BrewCauldronBlockEntity> {
    public static final ResourceLocation WATER = new ResourceLocation("block/water_still");
    private static final float[] HEIGHT = {0, 0.25f, 0.4375f, 0.625f};

    public BrewCauldronRenderer(BlockEntityRendererProvider.Context p_i226007_1_) {
    }

    @Override
    public void render(BrewCauldronBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        Level world = entity.getLevel();
        if (world != null) {
            BlockPos pos = entity.getBlockPos();
            int level = entity.getBlockState().getValue(ModStateProperties.LEVEL_BREW);
            if (level > 0) {
                if (entity.isHeated() && !Minecraft.getInstance().isPaused()) {
                    float fluidHeight = 0;
                    float width = 0.35f;
                    switch (entity.getBlockState().getValue(ModStateProperties.LEVEL_BREW)) {
                        case 1 -> fluidHeight = 0.225f;
                        case 2 -> {
                            fluidHeight = 0.425f;
                            width = 0.3f;
                        }
                        case 3 -> fluidHeight = 0.625f;
                    }
                    if (fluidHeight > 0) {
                        double[] rgb = MathHelper.rgbParticle(entity.getColor());
                        if (entity.mode == BrewCauldronBlockEntity.Mode.BREWING) {
                            if (entity.isBrewing){
                                world.addParticle(ParticleTypes.WITCH, pos.getX() + 0.5, pos.getY() + fluidHeight, pos.getZ() + 0.5, 0.0F, 0.0F, 0.0F);
                            } else if (entity.getLevel().getGameTime() % 20 == 0){
                                world.addParticle(ParticleTypes.WITCH, pos.getX() + 0.5, pos.getY() + fluidHeight, pos.getZ() + 0.5, 0.0F, 0.0F, 0.0F);
                            }
                        } else if (entity.mode == BrewCauldronBlockEntity.Mode.COMPLETED){
                            world.addParticle(ParticleTypes.ENTITY_EFFECT, pos.getX() + 0.5, pos.getY() + fluidHeight, pos.getZ() + 0.5, rgb[0], rgb[1], rgb[2]);
                        } else if (entity.mode == BrewCauldronBlockEntity.Mode.FAILED){
                            world.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + fluidHeight, pos.getZ() + 0.5, 0.0F, 0.0F, 0.0F);
                        }
                        world.addParticle(ModParticleTypes.BREW_BUBBLE.get(), pos.getX() + 0.5 + Mth.nextDouble(world.random, -width, width), pos.getY() + fluidHeight, pos.getZ() + 0.5 + Mth.nextDouble(world.random, -width, width), rgb[0], rgb[1], rgb[2]);
                    }
                }
            }
        }
    }
}
