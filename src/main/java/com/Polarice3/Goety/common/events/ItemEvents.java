package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.client.particles.ShockwaveParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.equipment.DarkScytheItem;
import com.Polarice3.Goety.common.items.equipment.DeathScytheItem;
import com.Polarice3.Goety.common.items.equipment.HuntersBowItem;
import com.Polarice3.Goety.common.items.equipment.RampagingAxeItem;
import com.Polarice3.Goety.common.items.revive.ReviveServantItem;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
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
    public static void HurtEvent(LivingHurtEvent event){
        LivingEntity victim = event.getEntity();
        Entity directEntity = event.getSource().getDirectEntity();
        if (event.getAmount() > 0.0F) {
            if (directEntity instanceof LivingEntity livingAttacker) {
                if (ModDamageSource.physicalAttacks(event.getSource())) {
                    ItemHelper.setItemEffect(livingAttacker.getMainHandItem(), victim);
                    if (livingAttacker.getMainHandItem().getItem() instanceof TieredItem weapon) {
                        if (weapon == ModItems.FANGED_DAGGER.get()){
                            MobEffect effect = MobEffects.POISON;
                            if (CuriosFinder.hasWildRobe(livingAttacker)){
                                effect = GoetyEffects.ACID_VENOM.get();
                            }
                            if (livingAttacker.hasEffect(GoetyEffects.VENOMOUS_HANDS.get())){
                                EffectsUtil.increaseDuration(victim, effect, 600);
                            } else {
                                victim.addEffect(new MobEffectInstance(effect, 200));
                            }
                        }
                        if (weapon == ModItems.HUNGRY_DAGGER.get()){
                            int soulEat = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.SOUL_EATER.get(), livingAttacker) + 1;
                            livingAttacker.heal(event.getAmount() * (0.05F * soulEat));
                        }
                        if (weapon instanceof DarkScytheItem) {
                            victim.playSound(ModSounds.SCYTHE_HIT_MEATY.get());
                        }
                        if (weapon instanceof DeathScytheItem) {
                            if (!victim.hasEffect(GoetyEffects.SAPPED.get())) {
                                victim.addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), 100));
                                victim.playSound(SoundEvents.SHIELD_BREAK, 2.0F, 1.0F);
                            } else {
                                if (victim.level.random.nextFloat() <= 0.2F) {
                                    EffectsUtil.amplifyEffect(victim, GoetyEffects.SAPPED.get(), 100);
                                    victim.playSound(SoundEvents.SHIELD_BREAK, 2.0F, 1.0F);
                                } else {
                                    EffectsUtil.resetDuration(victim, GoetyEffects.SAPPED.get(), 100);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (victim instanceof Player player) {
            if (CuriosFinder.hasCurio(victim, ModItems.SPITEFUL_BELT.get())) {
                int a = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.THORNS, CuriosFinder.findCurio(victim, ModItems.SPITEFUL_BELT.get()));
                if (SEHelper.getSoulsAmount(player, ItemConfig.SpitefulBeltUseAmount.get() * (a + 1))) {
                    if (!event.getSource().is(DamageTypeTags.AVOIDS_GUARDIAN_THORNS) && !event.getSource().is(DamageTypes.THORNS) && event.getSource().getEntity() instanceof LivingEntity livingentity && livingentity != victim) {
                        livingentity.hurt(livingentity.damageSources().thorns(victim), 2.0F + a);
                        SEHelper.decreaseSouls(player, ItemConfig.SpitefulBeltUseAmount.get() * (a + 1));
                    }
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
        if (event.getItemStack().getItem() instanceof IWand) {
            if (event.getTarget() instanceof Villager villager) {
                InteractionResult result = event.getItemStack().interactLivingEntity(event.getEntity(), villager, event.getHand());
                if (result.consumesAction()) {
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }
            }
        }
    }

    @SubscribeEvent
    public static void GeneralInteractEvents(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().is(Items.GLASS_BOTTLE)) {
            InteractionResultHolder<ItemStack> result = ItemHelper.getVoidBottle(event.getEntity(), event.getLevel(), event.getHand());
            if (result.getResult().consumesAction()) {
                event.setCanceled(true);
                event.setCancellationResult(result.getResult());
            }
        }
    }
}
