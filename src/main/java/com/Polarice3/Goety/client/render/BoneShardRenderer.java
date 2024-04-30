package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.common.items.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

public class BoneShardRenderer<T extends AbstractArrow> extends EntityRenderer<T> {
    private final ItemRenderer itemRenderer;
    private final float scale;

    public BoneShardRenderer(EntityRendererProvider.Context p_i226035_1_, ItemRenderer p_i226035_2_, float p_i226035_3_) {
        super(p_i226035_1_);
        this.itemRenderer = p_i226035_2_;
        this.scale = p_i226035_3_;
    }

    public BoneShardRenderer(EntityRendererProvider.Context p_i50957_1_, ItemRenderer p_i50957_2_) {
        this(p_i50957_1_, p_i50957_2_, 1.0F);
    }

    public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.pushPose();
        pMatrixStack.scale(this.scale, this.scale, this.scale);
        pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
        pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot()) - 45.0F));
        float f9 = (float)pEntity.shakeTime - pPartialTicks;
        if (f9 > 0.0F) {
            float f10 = -Mth.sin(f9 * 5.0F) * f9;
            pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(f10));
        }
        this.itemRenderer.renderStatic(new ItemStack(ModItems.BONE_SHARD.get()), ItemTransforms.TransformType.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pMatrixStack, pBuffer, 0);
        pMatrixStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    public ResourceLocation getTextureLocation(AbstractArrow pEntity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
