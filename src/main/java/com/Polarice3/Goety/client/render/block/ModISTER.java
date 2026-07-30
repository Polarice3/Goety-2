package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.client.events.ClientEvents;
import com.Polarice3.Goety.common.blocks.*;
import com.Polarice3.Goety.common.blocks.entities.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

/**
 * Chest Item Rendering based of codes from @TeamTwilight
 */
public class ModISTER extends BlockEntityWithoutLevelRenderer {
    private final Map<Block, ModChestBlockEntity> chestEntities = new HashMap<>();
    private final Map<Block, SculpturedStatueBlockEntity> statueEntities = new HashMap<>();
    private final Map<Block, BlackCrystalBlockEntity> crystalEntities = new HashMap<>();

    private ModChestBlockEntity chestEntity(Block block) {
        return this.chestEntities.computeIfAbsent(block, block1 -> new ModChestBlockEntity(BlockPos.ZERO, block1.defaultBlockState()));
    }

    private SculpturedStatueBlockEntity statueEntity(Block block) {
        return this.statueEntities.computeIfAbsent(block, block1 -> new SculpturedStatueBlockEntity(BlockPos.ZERO, block1.defaultBlockState()));
    }

    private BlackCrystalBlockEntity crystalEntity(Block block) {
        return this.crystalEntities.computeIfAbsent(block, block1 -> new BlackCrystalBlockEntity(BlockPos.ZERO, block1.defaultBlockState()));
    }

    public ModISTER() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pCamera, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pLight, int pOverlay) {
        Item item = pStack.getItem();

        if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof TallSkullBlock) {
                if(pCamera == ItemDisplayContext.GUI) {
                    pMatrixStack.pushPose();
                    pMatrixStack.translate(0.5F, 0.5F, 0.5F);
                    pMatrixStack.mulPose(Axis.XP.rotationDegrees(30));
                    pMatrixStack.mulPose(Axis.YN.rotationDegrees(-45));
                    pMatrixStack.translate(-0.5F, -0.5F, -0.5F);
                    pMatrixStack.translate(0.0F, 0.25F, 0.0F);
                    TallSkullBlockEntityRenderer.renderSkull(null, 180.0F, pMatrixStack, pBuffer, pLight);
                    pMatrixStack.popPose();

                } else {
                    TallSkullBlockEntityRenderer.renderSkull(null, 180.0F, pMatrixStack, pBuffer, pLight);
                }
            } else if (block instanceof RedstoneGolemSkullBlock){
                if(pCamera == ItemDisplayContext.GUI) {
                    pMatrixStack.pushPose();
                    pMatrixStack.translate(0.5F, 0.5F, 0.5F);
                    pMatrixStack.mulPose(Axis.XP.rotationDegrees(30));
                    pMatrixStack.mulPose(Axis.YN.rotationDegrees(-45));
                    pMatrixStack.translate(-0.5F, -0.5F, -0.5F);
                    pMatrixStack.translate(0.0F, 0.25F, 0.0F);
                    RedstoneGolemSkullBlockEntityRenderer.renderItemSkull(pStack, null, 180.0F, pMatrixStack, pBuffer, pLight);
                    pMatrixStack.popPose();

                } else {
                    RedstoneGolemSkullBlockEntityRenderer.renderItemSkull(pStack, null, 180.0F, pMatrixStack, pBuffer, pLight);
                }
            } else if (block instanceof GraveGolemSkullBlock){
                if(pCamera == ItemDisplayContext.GUI) {
                    pMatrixStack.pushPose();
                    pMatrixStack.translate(0.5F, 0.5F, 0.5F);
                    pMatrixStack.mulPose(Axis.XP.rotationDegrees(30));
                    pMatrixStack.mulPose(Axis.YN.rotationDegrees(-45));
                    pMatrixStack.translate(-0.5F, -0.5F, -0.5F);
                    pMatrixStack.translate(0.0F, 0.25F, 0.0F);
                    GraveGolemSkullBlockEntityRenderer.renderItemSkull(pStack, null, 180.0F, pMatrixStack, pBuffer, pLight);
                    pMatrixStack.popPose();

                } else {
                    GraveGolemSkullBlockEntityRenderer.renderItemSkull(pStack, null, 180.0F, pMatrixStack, pBuffer, pLight);
                }
            } else if (block instanceof RedstoneMonstrosityHeadBlock){
                if(pCamera == ItemDisplayContext.GUI) {
                    pMatrixStack.pushPose();
                    pMatrixStack.translate(0.5F, 0.5F, 0.5F);
                    pMatrixStack.mulPose(Axis.XP.rotationDegrees(30));
                    pMatrixStack.mulPose(Axis.YN.rotationDegrees(-45));
                    pMatrixStack.translate(-0.5F, -0.5F, -0.5F);
                    pMatrixStack.translate(0.0F, 0.25F, 0.0F);
                    RedstoneMonstrosityHeadBlockEntityRenderer.renderItemSkull(pStack, null, 180.0F, pMatrixStack, pBuffer, pLight);
                    pMatrixStack.popPose();

                } else {
                    RedstoneMonstrosityHeadBlockEntityRenderer.renderItemSkull(pStack, null, 180.0F, pMatrixStack, pBuffer, pLight);
                }
            } else if (block instanceof BlackCrystalBlock) {
                BlackCrystalBlockEntity crystalBlock = crystalEntity(block);
                BlockEntityRenderer<?> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(crystalBlock);
                if (renderer instanceof BlackCrystalRenderer crystalBlockRenderer) {
                    crystalBlockRenderer.render(crystalBlock, ClientEvents.PARTIAL_TICK, pMatrixStack, pBuffer, pLight, pOverlay);
                }
            } else if (block instanceof CryptChestBlock) {
                Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(new CryptChestBlockEntity(BlockPos.ZERO, block.defaultBlockState().setValue(CryptChestBlock.LOCKED, false)), pMatrixStack, pBuffer, pLight, pOverlay);
            } else if (block instanceof LoftyChestBlock) {
                Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(new LoftyChestBlockEntity(BlockPos.ZERO, block.defaultBlockState()), pMatrixStack, pBuffer, pLight, pOverlay);
            } else if (block instanceof ModChestBlock) {
                Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(chestEntity(block), pMatrixStack, pBuffer, pLight, pOverlay);
            } else if (block instanceof PlushieBlock) {
                if(pCamera == ItemDisplayContext.GUI) {
                    pMatrixStack.pushPose();
                    pMatrixStack.translate(0.5F, 0.5F, 0.5F);
                    pMatrixStack.mulPose(Axis.XP.rotationDegrees(30));
                    pMatrixStack.mulPose(Axis.YN.rotationDegrees(-45));
                    pMatrixStack.translate(-0.5F, -0.5F, -0.5F);
                    pMatrixStack.translate(0.0F, 0.25F, 0.0F);
                    PlushieBlockEntityRenderer.renderItemPlushie(pStack, block.defaultBlockState(), 180.0F, pMatrixStack, pBuffer, pLight);
                    pMatrixStack.popPose();
                } else {
                    PlushieBlockEntityRenderer.renderItemPlushie(pStack, block.defaultBlockState(), 180.0F, pMatrixStack, pBuffer, pLight);
                }
            } else if (block instanceof SculpturedStatueBlock) {
                SculpturedStatueBlockEntity statueEntity = statueEntity(block);
                BlockEntityRenderer<?> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(statueEntity);                if (renderer instanceof SculpturedStatueRenderer statueRenderer) {
                    if(pCamera == ItemDisplayContext.GUI) {
                        pMatrixStack.pushPose();
                        pMatrixStack.translate(0.5F, 0.5F, 0.5F);
                        pMatrixStack.mulPose(Axis.XP.rotationDegrees(30));
                        pMatrixStack.mulPose(Axis.YN.rotationDegrees(-45));
                        pMatrixStack.translate(-0.5F, -0.5F, -0.5F);
                        pMatrixStack.translate(0.0F, 0.25F, 0.0F);
                        statueRenderer.renderItem(block.defaultBlockState(), 180.0F, pMatrixStack, pBuffer, pLight);
                        pMatrixStack.popPose();
                    } else {
                        statueRenderer.renderItem(block.defaultBlockState(), 180.0F, pMatrixStack, pBuffer, pLight);
                    }
                }
            }
        }
    }
}
