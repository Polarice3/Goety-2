package com.Polarice3.Goety.client.render.layer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;

/**
 * Based on @bagu_chan codes: <a href="https://github.com/baguchan/BagusLib-MultiLoader/blob/1.20.6/common/src/main/java/bagu_chan/bagus_lib/client/layer/IArmor.java">...</a>
 */
public interface HierarchicalArmor {

    void translateToHead(ModelPart part, PoseStack poseStack);

    void translateToChest(ModelPart part, PoseStack poseStack);

    void translateToLeg(ModelPart part, PoseStack poseStack);

    void translateToArms(ModelPart part, PoseStack poseStack);

    default Iterable<ModelPart> rightHandArmors() {
        return ImmutableList.of();
    }

    default Iterable<ModelPart> leftHandArmors() {
        return ImmutableList.of();
    }

    default Iterable<ModelPart> rightLegPartArmors() {
        return ImmutableList.of();
    }

    default Iterable<ModelPart> leftLegPartArmors() {
        return ImmutableList.of();
    }

    default Iterable<ModelPart> bodyPartArmors() {
        return ImmutableList.of();
    }

    default Iterable<ModelPart> headPartArmors() {
        return ImmutableList.of();
    }
}
