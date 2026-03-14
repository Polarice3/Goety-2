package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PatrolSpawner.class)
public class PatrolSpawnerMixin {

    @Inject(method = "spawnPatrolMember", at = @At("HEAD"), cancellable = true)
    public void goetySpawnPatrolMember(ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, boolean captain, CallbackInfoReturnable<Boolean> cir) {
        if (MainConfig.ShriekObeliskPatrol.get()) {
            if (BlockFinder.findIllagerWard(serverLevel, blockPos, Mth.floor(MainConfig.ShriekObeliskCost.get() / 4.0F))) {
                cir.setReturnValue(false);
            }
        }
    }
}
