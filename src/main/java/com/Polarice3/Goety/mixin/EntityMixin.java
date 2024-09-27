package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.neutral.AbstractVine;
import com.Polarice3.Goety.init.ModSoundTypes;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(
            method = {"canCollideWith(Lnet/minecraft/world/entity/Entity;)Z"},
            at = @At(value = "HEAD"),
            cancellable = true
    )
    protected void canCollideWith(Entity other, CallbackInfoReturnable<Boolean> cir) {
        if(other instanceof AbstractVine vine && vine.passableEntities((Entity)(Object)this)){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isSwimming", at = @At("HEAD"), cancellable = true)
    public void isSwimming(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if ((Entity) (Object) this instanceof LivingEntity livingEntity && livingEntity.hasEffect(GoetyEffects.PLUNGE.get())) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @Inject(method = "playStepSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", at = @At(value = "TAIL"))
    protected void playStepSound(BlockPos blockPos, BlockState blockState, CallbackInfo callbackInfo) {
        Entity entity = (Entity) (Object) this;
        SoundType soundtype = blockState.getSoundType(entity.level, blockPos, entity);
        if (soundtype == ModSoundTypes.MOD_METAL){
            entity.playSound(SoundEvents.STONE_STEP, (soundtype.getVolume() * 0.15F) * Mth.randomBetween(entity.level.random, 0.8F, 1.0F), Mth.randomBetween(entity.level.random, 0.8F, 1.0F));
            entity.playSound(SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, (soundtype.getVolume() * 0.15F) * Mth.randomBetween(entity.level.random, 0.8F, 1.0F), Mth.randomBetween(entity.level.random, 0.8F, 1.0F));
        }
    }

    @Inject(method = "makeStuckInBlock(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;)V", at = @At(value = "HEAD"), cancellable = true)
    public void makeStuckInBlock(BlockState blockState, Vec3 vec3, CallbackInfo callbackInfo) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity){
            if (blockState.getBlock() instanceof WebBlock) {
                if (livingEntity.hasEffect(GoetyEffects.CLIMBING.get())) {
                    callbackInfo.cancel();
                }
            }
            if (blockState.getBlock() instanceof PowderSnowBlock){
                if (CuriosFinder.hasFrostRobes(livingEntity)){
                    callbackInfo.cancel();
                }
            }
        }
    }
}
