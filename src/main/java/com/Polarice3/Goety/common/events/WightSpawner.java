package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.hostile.Wight;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import java.util.Random;

public class WightSpawner {
    private int nextTick;

    public int tick(ServerLevel pLevel) {
        if (!MobsConfig.WightSpawn.get()) {
            return 0;
        } else {
            RandomSource random = pLevel.random;
            --this.nextTick;
            if (this.nextTick > 0) {
                return 0;
            } else {
                this.nextTick += MobsConfig.WightSpawnFreq.get();
                if (random.nextInt(MobsConfig.WightSpawnChance.get()) != 0) {
                    return 0;
                } else {
                    int j = pLevel.players().size();
                    if (j < 1) {
                        return 0;
                    } else if (!pLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                        return 0;
                    } else {
                        ServerPlayer pPlayer = pLevel.players().get(random.nextInt(j));
                        float rawPercent = (float) SEHelper.getSoulAmountInt(pPlayer) / MainConfig.MaxArcaSouls.get();
                        int sePercent = (int) (rawPercent * 100);
                        if (pPlayer.isSpectator() || pPlayer.isCreative()) {
                            return 0;
                        } else if (!pLevel.getEntitiesOfClass(LivingEntity.class,
                                pPlayer.getBoundingBox().inflate(64.0D),
                                entity -> entity.getType().is(Tags.EntityTypes.BOSSES)
                                    || entity.getType().is(ModTags.EntityTypes.MINI_BOSSES)).isEmpty()) {
                            return 0;
                        } else if (sePercent >= 5 && sePercent < 20) {
                            summonWight(pLevel, pPlayer, sePercent);
                            this.nextTick += MobsConfig.WightSpawnFreq.get();
                            return 1;
                        } else if (sePercent >= 20 && sePercent < 90) {
                            summonWight(pLevel, pPlayer, sePercent);
                            return 1;
                        } else if (sePercent >= 90){
                            int extra = random.nextInt(2);
                            for (int i = 0; i < extra; ++i) {
                                summonWight(pLevel, pPlayer, sePercent);
                            }
                            return 1;
                        }
                    }
                }
            }
        }
        return 0;
    }

    public static void summonWight(ServerLevel serverLevel, Player player, int sePercent){
        Wight wight = new Wight(ModEntityType.WIGHT.get(), serverLevel);
        Random rand = new Random();
        for (int i = 0; i < 16; ++i) {
            Vec3 vec3 = BlockFinder.getRandomSpawnBehindDirection(serverLevel, rand, player.position(), player.getLookAngle());
            BlockPos blockPos = BlockPos.containing(vec3);
            if (BlockFinder.canSeeBlock(player, blockPos) || i == 15) {
                if (!serverLevel.isLoaded(blockPos)){
                    break;
                }
                wight.setPos(vec3);
                wight.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(wight.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                wight.upgradePower(sePercent);
                if (serverLevel.addFreshEntity(wight)) {
                    break;
                }
            }
        }
    }
}
