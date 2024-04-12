package com.Polarice3.Goety.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import javax.annotation.Nonnull;

public class LichCuriosLayer<T extends LivingEntity, M extends EntityModel<T>> extends
        RenderLayer<T, M> {
    private final RenderLayerParent<T, M> renderLayerParent;

    public LichCuriosLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
        this.renderLayerParent = renderer;
    }

    @Override
    public void render(@Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource renderTypeBuffer,
                       int light, @Nonnull T livingEntity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStack.pushPose();
        CuriosApi.getCuriosInventory(livingEntity)
                .ifPresent(handler -> handler.getCurios().forEach((id, stacksHandler) -> {
                    IDynamicStackHandler stackHandler = stacksHandler.getStacks();
                    IDynamicStackHandler cosmeticStacksHandler = stacksHandler.getCosmeticStacks();

                    for (int i = 0; i < stackHandler.getSlots(); i++) {
                        ItemStack stack = cosmeticStacksHandler.getStackInSlot(i);
                        boolean cosmetic = true;
                        NonNullList<Boolean> renderStates = stacksHandler.getRenders();
                        boolean renderable = renderStates.size() > i && renderStates.get(i);

                        if (stack.isEmpty() && renderable) {
                            stack = stackHandler.getStackInSlot(i);
                            cosmetic = false;
                        }

                        if (!stack.isEmpty()) {
                            SlotContext slotContext = new SlotContext(id, livingEntity, i, cosmetic, renderable);
                            ItemStack finalStack = stack;
                            CuriosRendererRegistry.getRenderer(stack.getItem()).ifPresent(
                                    renderer -> renderer
                                            .render(finalStack, slotContext, matrixStack, renderLayerParent,
                                                    renderTypeBuffer, light, limbSwing, limbSwingAmount, partialTicks,
                                                    ageInTicks, netHeadYaw, headPitch));
                        }
                    }
                }));
        matrixStack.popPose();
    }
}
