package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.LichdomHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    public PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(at = @At("HEAD"), method = "interactOn(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", cancellable = true)
    public void goetyInteractOn(Entity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Player player = (Player) (Object) this;
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity instanceof AbstractVillager || livingEntity instanceof AbstractHorse) {
                if (itemstack.getDescriptionId().contains(Goety.MOD_ID)) {
                    InteractionResult result = itemstack.interactLivingEntity(player, livingEntity, hand);
                    if (result.consumesAction()) {
                        cir.setReturnValue(result);
                    }
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "playStepSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", cancellable = true)
    public void goetyLichStep(BlockPos p_282121_, BlockState p_282194_, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (LichdomHelper.isLich(player) && LichdomHelper.isInLichMode(player)) {
            if (!this.isInWater()) {
                SoundEvent soundEvent = ModSounds.LICH_FLY.get();
                if (LichdomHelper.hasLegs(player)) {
                    soundEvent = ModSounds.NECROMANCER_STEP.get();
                }
                this.playSound(soundEvent, 0.15F, 1.0F);
                ci.cancel();
            }
        }
    }
}
