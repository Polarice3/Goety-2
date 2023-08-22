package com.Polarice3.Goety.common.entities.deco;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.level.Level;

public class ModPainting extends Painting {
    public ModPainting(EntityType<? extends Painting> p_31904_, Level p_31905_) {
        super(p_31904_, p_31905_);
    }

/*    public ModPainting(Level level, BlockPos blockPos, Direction direction, Holder<PaintingVariant> holder) {
        super(ModEntityType.MOD_PAINTING.get(), level);
        this.setVariant(holder);
        this.setDirection(direction);
        this.pos = blockPos;
    }*/

    private void setVariant(Holder<PaintingVariant> p_218892_) {
        this.entityData.set(DATA_PAINTING_VARIANT_ID, p_218892_);
    }
}
