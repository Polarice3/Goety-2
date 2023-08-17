package com.Polarice3.Goety.common.events;

public class SoulRecoilEvents {

/*    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        Player player = event.player;
        if (player.level instanceof ServerLevel serverLevel) {
            float random = (float) SEHelper.getSoulAmountInt(player) / MainConfig.MaxArcaSouls.get();
            int random2 = (int) Math.min(100, random * 100);
            int effect = serverLevel.random.nextInt(random2);
            if (random >= 0.1F) {
                if (player.tickCount % MathHelper.minutesToTicks(2) == 0) {
                    if (serverLevel.random.nextFloat() <= random) {
                        if (effect > 0) {
                            if (effect <= 88) {
                                IllagerSpawner illagerSpawner = new IllagerSpawner();
                                illagerSpawner.forceSpawn(serverLevel, player);
                            }
                        }
                    }
                }
            }
        }
    }*/
}
