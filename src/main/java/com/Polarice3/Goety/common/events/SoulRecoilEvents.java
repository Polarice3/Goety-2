package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SoulRecoilEvents {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        Player player = event.player;
        if (player.level instanceof ServerLevel serverLevel) {
            float sePercent = (float) SEHelper.getSoulAmountInt(player) / MainConfig.MaxArcaSouls.get();
            int random = (int) Math.min(100, sePercent * 100);
            int recoilEvent = serverLevel.random.nextInt(random);
            /*if (sePercent >= 0.1F && SEHelper.getRecoil(player) > 0) {
                if (player.tickCount % MathHelper.minutesToTicks(2) == 0) {
                    SEHelper.decreaseRecoil(player, 1);
                    if (serverLevel.random.nextFloat() <= sePercent) {
                        if (recoilEvent > 0) {
                            if (recoilEvent <= 10) {
                                Wight wight = new Wight(ModEntityType.WIGHT.get(), player.level);
                                wight.setPos(wight.spawnLocation(player));
                                wight.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(wight.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                                serverLevel.addFreshEntity(wight);
                            }
                        }
                    }
                }
            }*/
        }
    }
}
