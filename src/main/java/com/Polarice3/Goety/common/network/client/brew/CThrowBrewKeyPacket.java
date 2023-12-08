package com.Polarice3.Goety.common.network.client.brew;

import com.Polarice3.Goety.common.entities.projectiles.ThrownBrew;
import com.Polarice3.Goety.common.items.brew.ThrowableBrewItem;
import com.Polarice3.Goety.common.items.handler.BrewBagItemHandler;
import com.Polarice3.Goety.utils.BrewUtils;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CThrowBrewKeyPacket {
    public int chosenBrew;

    public CThrowBrewKeyPacket(int chosenBrew){
        this.chosenBrew = chosenBrew;
    }

    public static void encode(CThrowBrewKeyPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.chosenBrew);
    }

    public static CThrowBrewKeyPacket decode(FriendlyByteBuf buffer) {
        return new CThrowBrewKeyPacket(buffer.readInt());
    }

    public static void consume(CThrowBrewKeyPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> swapFocus(packet.chosenBrew, ctx.get().getSender()));
    }

    public static void swapFocus(int swapSlot, Player player) {
        ItemStack stack = CuriosFinder.findBrewBag(player);
        if (stack.getCount() <= 0) {
            return;
        }

        BrewBagItemHandler bagHandler = BrewBagItemHandler.get(stack);

        ItemStack bagFocus = bagHandler.getStackInSlot(swapSlot);
        if (bagFocus.getItem() instanceof ThrowableBrewItem) {
            if (!player.level.isClientSide) {
                ThrownBrew thrownBrew = new ThrownBrew(player.level, player);
                thrownBrew.setItem(bagFocus);
                float velocity = 0.5F + BrewUtils.getVelocity(bagFocus);
                thrownBrew.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, velocity, 1.0F);
                player.level.addFreshEntity(thrownBrew);
            }

            player.awardStat(Stats.ITEM_USED.get(bagFocus.getItem()));
            if (!player.getAbilities().instabuild) {
                bagFocus.shrink(1);
            }
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSoundPacket(SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, serverPlayer.position().x, serverPlayer.position().y, serverPlayer.position().z, 1.0F, 1.0F, serverPlayer.level.getRandom().nextLong()));
            }
        }
    }
}
