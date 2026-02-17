package com.Polarice3.Goety.utils;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class ModelUtil {
    public static Stream<String> getAllPartNames(ModelPart root) {
        return Stream.concat(
                root.children.keySet().stream(),
                root.children.values().stream().flatMap(ModelUtil::getAllPartNames)
        );
    }

    public static Map<String, ModelPartPose> saveModelSnapshot(List<String> allPartNames, Function<String, Optional<ModelPart>> getter) {
        Map<String, ModelPartPose> snapshot = new HashMap<>();
        for (String name : allPartNames) {
            getter.apply(name).ifPresent(part ->
                    snapshot.put(name, new ModelPartPose(
                            part.x, part.y, part.z,
                            part.xRot, part.yRot, part.zRot,
                            part.xScale, part.yScale, part.zScale,
                            part.visible
                    )));
        }
        return snapshot;
    }

    public static void loadPoseFromSnapshot(Map<String, ModelPartPose> snapshot, Function<String, Optional<ModelPart>> getter) {
        snapshot.forEach((name, pose) ->
                getter.apply(name).ifPresent(part -> {
                    part.x = pose.x();
                    part.y = pose.y();
                    part.z = pose.z();
                    part.xRot = pose.xRot();
                    part.yRot = pose.yRot();
                    part.zRot = pose.zRot();
                    part.xScale = pose.xScale();
                    part.yScale = pose.yScale();
                    part.zScale = pose.zScale();
                    part.visible = pose.visible();
                }));
    }
}
