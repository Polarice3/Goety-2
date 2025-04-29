package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.particles.ShockwaveParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.equipment.DeathScytheItem;
import com.Polarice3.Goety.common.items.equipment.HuntersBowItem;
import com.Polarice3.Goety.common.items.equipment.RampagingAxeItem;
import com.Polarice3.Goety.common.items.revive.ReviveServantItem;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemEvents {

    @SubscribeEvent
    public static void FangedHurt(LivingHurtEvent event){
        LivingEntity livingEntity = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (ModDamageSource.physicalAttacks(event.getSource())){
            if (attacker instanceof LivingEntity livingAttacker){
                if (livingAttacker.getMainHandItem().getItem() == ModItems.FANGED_DAGGER.get()){
                    MobEffect effect = MobEffects.POISON;
                    if (CuriosFinder.hasWildRobe(livingAttacker)){
                        effect = GoetyEffects.ACID_VENOM.get();
                    }
                    if (livingAttacker.hasEffect(GoetyEffects.VENOMOUS_HANDS.get())){
                        EffectsUtil.increaseDuration(livingEntity, effect, 600);
                    } else {
                        livingEntity.addEffect(new MobEffectInstance(effect, 200));
                    }
                }
                if (livingAttacker.getMainHandItem().getItem() == ModItems.HUNGRY_DAGGER.get()){
                    int soulEat = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.SOUL_EATER.get(), livingAttacker) + 1;
                    livingAttacker.heal(event.getAmount() * (0.05F * soulEat));
                }
            }
        }
    }

    @SubscribeEvent
    public static void AxeDeath(LivingDeathEvent event){
        LivingEntity killed = event.getEntity();
        Entity killer = event.getSource().getEntity();
        Level world = killed.getCommandSenderWorld();
        if (killer instanceof LivingEntity livingEntity) {
            if (ModDamageSource.physicalAttacks(event.getSource()) && livingEntity.getMainHandItem().getItem() instanceof RampagingAxeItem) {
                MobEffectInstance effectinstance1 = livingEntity.getEffect(GoetyEffects.RAMPAGE.get());
                if (!livingEntity.hasEffect(GoetyEffects.RAMPAGE.get())){
                    livingEntity.addEffect(new MobEffectInstance(GoetyEffects.RAMPAGE.get(), MathHelper.secondsToTicks(ItemConfig.RampagingAxeDuration.get())));
                } else if (effectinstance1 != null){
                    int random = killed.getMaxHealth() >= 20 ? 0 : world.random.nextInt(4);
                    if (effectinstance1.getAmplifier() < 4) {
                        if (random == 0) {
                            EffectsUtil.amplifyEffect(livingEntity, GoetyEffects.RAMPAGE.get(), MathHelper.secondsToTicks(ItemConfig.RampagingAxeDuration.get()));
                        }
                    } else {
                        livingEntity.removeEffect(GoetyEffects.RAMPAGE.get());
                        if (world instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(new ShockwaveParticleOption(), livingEntity.getX(), livingEntity.getY() + 0.5F, livingEntity.getZ(), 0, 0.0D, 0.0D, 0.0D, 0);
                            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 0, 1.0D, 0.0D, 0.0D, 0.5F);
                        }
                        LootingExplosion.Mode lootMode = CuriosFinder.hasWanting(livingEntity) ? LootingExplosion.Mode.LOOT : LootingExplosion.Mode.REGULAR;
                        ExplosionUtil.lootExplode(world, livingEntity, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 3.0F, false, Explosion.BlockInteraction.KEEP, lootMode);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void HunterLoot(LootingLevelEvent event){
        if (event.getDamageSource() != null) {
            if (event.getEntity() != null) {
                if (!event.getEntity().level.isClientSide) {
                    if (event.getDamageSource().getEntity() != null && event.getDamageSource().getEntity() instanceof LivingEntity livingEntity) {
                        if (livingEntity.getMainHandItem().getItem() instanceof HuntersBowItem) {
                            if (event.getDamageSource().getDirectEntity() instanceof AbstractArrow) {
                                if (event.getEntity() instanceof Animal){
                                    event.setLootingLevel(event.getLootingLevel() + 4);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void EmptyClickEvents(PlayerInteractEvent.LeftClickEmpty event){
        DeathScytheItem.emptyClick(event.getItemStack());
    }

    @SubscribeEvent
    public static void PlayerAttackEvents(AttackEntityEvent event){
        DeathScytheItem.entityClick(event.getEntity(), event.getEntity().level);
    }

    @SubscribeEvent
    public static void InteractEntityEvents(PlayerInteractEvent.EntityInteract event){
        if (event.getItemStack().getItem() instanceof ReviveServantItem){
            if (SEHelper.getFocusCoolDown(event.getEntity()).isOnCooldown(event.getItemStack().getItem())){
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
            }
        }
    }
}
