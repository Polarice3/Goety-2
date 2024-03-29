package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.ItemConfig;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningPacket;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WindyRobeItem extends SingleStackItem{

    @SubscribeEvent
    public static void PlayerTick(TickEvent.PlayerTickEvent event){
        Player player = event.player;
        Level world = player.level;
        if (CuriosFinder.hasWindyRobes(player) && !player.isSpectator()){
            if (SEHelper.getSoulsAmount(player, ItemConfig.WindRobeSouls.get())
                    || player.isCreative()) {
                Vec3 vector3d = player.getDeltaMovement();
                if (player.hasEffect(MobEffects.SLOW_FALLING)){
                    player.removeEffect(MobEffects.SLOW_FALLING);
                }
                if (!player.isOnGround() && vector3d.y < 0.0D
                        && !player.isNoGravity()
                        && !player.getAbilities().flying
                        && !player.onClimbable()
                        && !player.isInFluidType()
                        && !player.isInWater()
                        && !player.isInLava()
                        && player.fallDistance >= 2.0F) {
                    if (player.tickCount % 20 == 0 && !player.isCreative() && player.fallDistance > 3.0F) {
                        SEHelper.decreaseSouls(player, ItemConfig.WindRobeSouls.get());
                    }
                    if (world instanceof ServerLevel serverLevel){
                        ServerParticleUtil.circularParticles(serverLevel, ParticleTypes.CLOUD, player, 1.0F);
                        if (CuriosFinder.hasCurio(player, ModItems.STORM_ROBE.get())) {
                            if (serverLevel.random.nextInt(20) == 0) {
                                Vec3 vec3 = Vec3.atCenterOf(player.blockPosition());
                                Vec3 vec31 = vec3.add(player.getRandom().nextDouble(), 1.0D, player.getRandom().nextDouble());
                                ModNetwork.sendToALL(new SLightningPacket(vec3, vec31, 2));
                            }
                        }
                    }
                    if (!player.isCrouching() && !player.isShiftKeyDown()) {
                        player.setDeltaMovement(vector3d.multiply(1.0D, 0.875D, 1.0D));
                    } else {
                        player.setDeltaMovement(vector3d.multiply(1.0D, 0.99D, 1.0D));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event){
        if (CuriosFinder.hasWindyRobes(event.getEntity())){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void HurtEvent(LivingHurtEvent event){
        LivingEntity victim = event.getEntity();
        if(CuriosFinder.hasCurio(victim, ModItems.STORM_ROBE.get())){
            if (ModDamageSource.shockAttacks(event.getSource()) || event.getSource() == DamageSource.LIGHTNING_BOLT){
                float resistance = 1.0F - (ItemConfig.StormRobeResistance.get() / 100.0F);
                event.setAmount(event.getAmount() * resistance);
            }
            if (event.getSource() == DamageSource.LIGHTNING_BOLT){
                victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300));
            }
        }
    }
}
