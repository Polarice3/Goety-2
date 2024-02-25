package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @ModifyVariable(at = @At("HEAD"), method = "turn", ordinal = 0, argsOnly = true)
    private double I$XRotate(double value) {
        return this.goety2$playerPerspectiveValue(value);
    }

    @ModifyVariable(at = @At("HEAD"), method = "turn", ordinal = 1, argsOnly = true)
    private double I$YRotate(double value) {
        return this.goety2$playerPerspectiveValue(value);
    }

    @Unique
    private double goety2$playerPerspectiveValue(double value) {
        boolean flag = (Entity) (Object) this instanceof Player player && SEHelper.hasCamera(player);
        return flag ? 0 : value;
    }

}
