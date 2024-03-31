package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.ModelData;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Shield render codes based on @TeamTwilight's Shield Layer: <a href="https://github.com/TeamTwilight/twilightforest/blob/1.19.x/src/main/java/twilightforest/client/renderer/entity/ShieldLayer.java">...</a>
 */
public class MagicShieldLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public static final ModelResourceLocation SHIELD = new ModelResourceLocation(Goety.location("magic_shield"), "inventory");
    private static final Direction[] DIRECTIONS = ArrayUtils.add(Direction.values(), null);

    public MagicShieldLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int light, T living, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (MiscCapHelper.getShields(living) > 0) {
            renderShields(stack, buffer, living, partialTicks);
        }
    }

    private void renderShields(PoseStack stack, MultiBufferSource buffer, T entity, float partialTicks) {
        float age = entity.tickCount + partialTicks;
        float rotateAngleY = age / 10.0F;

        int count = MiscCapHelper.getShields(entity);
        for (int c = 0; c < count; c++) {
            stack.pushPose();

            stack.translate(-0.5D, 1.0D, -0.5D);

            // invert Y
            stack.scale(1.0F, -1.0F, 1.0F);

            stack.translate(0.5D, 0.5D, 0.5D);
            stack.mulPose(Axis.YP.rotationDegrees(rotateAngleY * (180F / (float) Math.PI) + (c * (360F / count))));
            stack.mulPose(Axis.XP.rotationDegrees(15.0F));
            stack.translate(-0.5D, -0.5D, -0.5D);

            // push the shields outwards from the center of rotation
            stack.translate(0.0D, 0.0D, -1.0D);

            BakedModel model = Minecraft.getInstance().getModelManager().getModel(SHIELD);
            for (Direction dir : DIRECTIONS) {
                Minecraft.getInstance().getItemRenderer().renderQuadList(
                        stack,
                        buffer.getBuffer(Sheets.translucentCullBlockSheet()),
                        model.getQuads(null, dir, entity.getRandom(), ModelData.EMPTY, Sheets.translucentCullBlockSheet()),
                        ItemStack.EMPTY,
                        0xF000F0,
                        OverlayTexture.NO_OVERLAY
                );
            }

            stack.popPose();
        }
    }
}
