package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ApparitionDoorBlock;
import com.Polarice3.Goety.common.blocks.entities.PlinthHooks;
import com.Polarice3.Goety.common.blocks.entities.SealerHooks;
import com.Polarice3.Goety.common.entities.ai.PlinthReturnGoal;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockEvents {
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide && event.getEntity() instanceof Mob mob && PlinthHooks.canRest(mob)) {
            mob.goalSelector.addGoal(1, new PlinthReturnGoal(mob));
        }
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || !PlinthHooks.canRest(mob)) {
            return;
        }
        if (PlinthHooks.isFrozen(mob)) {
            if (mob.level.isClientSide) {
                PlinthHooks.freezeClient(mob);
            }
            event.setCanceled(true);
        } else if (mob.level.isClientSide) {
            PlinthHooks.thawClient(mob);
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || !PlinthHooks.isResting(mob)) {
            return;
        }
        if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            PlinthHooks.onDisturbed(mob);
            return;
        }
        event.setCanceled(true);
    }

    /*@SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getRayTraceResult() instanceof BlockHitResult hit && isGoetySpell(event.getProjectile())) {
            PolarCrystalBlock.hit(event.getProjectile().level, hit.getBlockPos());
        }
    }

    public static void onBeamHit(AbstractBeam beam, HitResult hit) {
        if (hit instanceof BlockHitResult blockHit) {
            PolarCrystalBlock.hit(beam.level, blockHit.getBlockPos());
        }
    }*/

    private static boolean isGoetySpell(Projectile projectile) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(projectile.getType()).getNamespace().equals(Goety.MOD_ID);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (level.isClientSide || !SealerHooks.isSealed(level, event.getPos()) || level.getBlockState(event.getPos()).getBlock() instanceof ApparitionDoorBlock) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        level.playSound(null, event.getPos(), SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
        event.getEntity().displayClientMessage(Component.translatable("info.goety.sealer.sealed"), true);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!event.getPlayer().isCreative() && SealerHooks.isSealed(event.getLevel(), event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().removeIf(pos -> SealerHooks.isSealed(event.getLevel(), pos));
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level && !level.isClientSide) {
            SealerHooks.clear(level);
        }
    }
}
