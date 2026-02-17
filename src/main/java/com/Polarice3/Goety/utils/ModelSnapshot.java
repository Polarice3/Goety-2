package com.Polarice3.Goety.utils;

import java.util.Map;

public record ModelSnapshot(float xRot, float yRot, float timestamp, Map<String, ModelPartPose> poses) {
}
