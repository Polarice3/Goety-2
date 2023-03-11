package com.Polarice3.Goety.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ModBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    public ModBlockEntityRenderer(BlockEntityRendererProvider.Context p_i226007_1_) {
    }

    @Override
    public void render(T p_112307_, float p_112308_, PoseStack p_112309_, MultiBufferSource p_112310_, int p_112311_, int p_112312_) {

    }
}
