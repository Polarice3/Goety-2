package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.ally.TwilightGoat;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;

public class TwilightGoatModel<T extends TwilightGoat> extends QuadrupedModel<T> {

    public TwilightGoatModel(ModelPart p_170578_) {
        super(p_170578_, true, 19.0F, 1.0F, 2.5F, 2.0F, 24);
    }

    public void setupAnim(T p_170587_, float p_170588_, float p_170589_, float p_170590_, float p_170591_, float p_170592_) {
        this.head.getChild("left_horn").visible = p_170587_.hasLeftHorn();
        this.head.getChild("right_horn").visible = p_170587_.hasRightHorn();
        super.setupAnim(p_170587_, p_170588_, p_170589_, p_170590_, p_170591_, p_170592_);
        float f = p_170587_.getRammingXHeadRot();
        if (f != 0.0F) {
            this.head.xRot = f;
        }

    }
}
