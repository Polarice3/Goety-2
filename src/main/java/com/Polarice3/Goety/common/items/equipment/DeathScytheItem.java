package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.ScytheSlash;
import com.Polarice3.Goety.common.items.ISoulRepair;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.client.CScytheStrikePacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DeathScytheItem extends DarkScytheItem implements ISoulRepair {

    public DeathScytheItem() {
        super(ModTiers.DEATH);
    }

    public static void emptyClick(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof DeathScytheItem){
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CScytheStrikePacket());
        }
    }

    public static void entityClick(Player player, Level world) {
        if (player.getMainHandItem().getItem() instanceof DeathScytheItem) {
            if (!player.level.isClientSide && !player.isSpectator()) {
                strike(world, player);
            }
        }
    }

    public static void strike(Level pLevel, Player pPlayer){
        if (pPlayer.getAttackStrengthScale(0.5F) > 0.9F) {
            pLevel.playSound((Player) null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.NEUTRAL, 2.0F, 0.4F / (pLevel.random.nextFloat() * 0.4F + 0.8F));
            if (!pLevel.isClientSide) {
                Vec3 vector3d = pPlayer.getViewVector(1.0F);
                ScytheSlash scytheSlash = new ScytheSlash(pPlayer.getMainHandItem(),
                        pLevel,
                        pPlayer.getX() + vector3d.x / 2,
                        pPlayer.getEyeY() - 0.2,
                        pPlayer.getZ() + vector3d.z / 2,
                        vector3d.x,
                        vector3d.y,
                        vector3d.z);
                scytheSlash.setOwnerId(pPlayer.getUUID());
                scytheSlash.setDamage(getInitialDamage());
                scytheSlash.setTotallife(300);
                pLevel.addFreshEntity(scytheSlash);
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
}
