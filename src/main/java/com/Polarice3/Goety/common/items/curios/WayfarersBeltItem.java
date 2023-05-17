package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WayfarersBeltItem extends SingleStackItem implements ICurioItem {

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event){
        if (event.getEntity() instanceof Player player) {
            boolean flag = CuriosFinder.hasCurio(player, itemStack -> itemStack.getItem() instanceof WayfarersBeltItem);

            AttributeInstance movement = player.getAttribute(Attributes.MOVEMENT_SPEED);
            float increaseSpeed = 0.15F;
            AttributeModifier attributeModifier = new AttributeModifier(UUID.fromString("f46dd333-63a3-4c3b-a5d3-065de1e226cd"), "Wayfarer Speed bonus", increaseSpeed, AttributeModifier.Operation.MULTIPLY_TOTAL);

            if (movement != null){
                if (flag){
                    if (!movement.hasModifier(attributeModifier)){
                        movement.addPermanentModifier(attributeModifier);
                    }
                } else {
                    if (movement.hasModifier(attributeModifier)){
                        movement.removeModifier(attributeModifier);
                    }
                }
            }

            AttributeInstance stepHeight = player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
            float increaseStepHeight = 1.0625F;
            AttributeModifier attributemodifier1 = new AttributeModifier(UUID.fromString("532a87ef-73fc-40c3-a950-ee26bbbcd2d7"), "Wayfarer Step Height bonus", increaseStepHeight, AttributeModifier.Operation.ADDITION);

            if (stepHeight != null){
                if (flag){
                    if (!stepHeight.hasModifier(attributemodifier1)){
                        stepHeight.addPermanentModifier(attributemodifier1);
                    }
                } else {
                    if (stepHeight.hasModifier(attributemodifier1)){
                        stepHeight.removeModifier(attributemodifier1);
                    }
                }
            }

            if (flag){
                FluidState fluidstate = player.level.getFluidState(player.blockPosition());
                if (player.isInWater() && player.isAffectedByFluids() && !player.canStandOnFluid(fluidstate) && !player.hasEffect(MobEffects.DOLPHINS_GRACE)){
                    player.setDeltaMovement(player.getDeltaMovement().x * 1.0175, player.getDeltaMovement().y, player.getDeltaMovement().z * 1.0175);
                }
            }
        }
    }

    @SubscribeEvent
    public static void OnLivingJump(LivingEvent.LivingJumpEvent event){
        if (event.getEntity() instanceof Player player) {
            if (CuriosFinder.hasCurio(player, itemStack -> itemStack.getItem() instanceof WayfarersBeltItem)){
                float f = 0.625F;
                if (player.hasEffect(MobEffects.JUMP)){
                    f += 0.1F * (float)(player.getEffect(MobEffects.JUMP).getAmplifier() + 1);
                }
                Vec3 vector3d = player.getDeltaMovement();
                player.setDeltaMovement(vector3d.x, f, vector3d.z);
            }
        }

    }

    @SubscribeEvent
    public static void OnLivingFall(LivingFallEvent event){
        if (event.getEntity() instanceof Player player) {
            if (CuriosFinder.hasCurio(player, itemStack -> itemStack.getItem() instanceof WayfarersBeltItem)){
                event.setDistance(event.getDistance()/2);
            }
        }
    }
}
