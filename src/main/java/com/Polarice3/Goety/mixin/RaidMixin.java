package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raid.class)
public abstract class RaidMixin {
    @Shadow
    @Final
    private ServerLevel level;

    @Shadow public abstract int getBadOmenLevel();

    @Shadow public abstract void stop();

    @Shadow private BlockPos center;

    @Shadow public abstract boolean isStopped();

    @Shadow public abstract boolean isVictory();

    @ModifyVariable(at = @At(value = "STORE", ordinal = 0), method = "spawnGroup")
    private Raider spawnCustomRaider(Raider raider, BlockPos blockPos) {
        if (MobsConfig.ArmoredRavagerRaid.get()){
            if (this.level.random.nextFloat() < (0.25F + this.level.getCurrentDifficultyAt(raider.blockPosition()).getSpecialMultiplier())) {
                if (raider.getType() == EntityType.RAVAGER){
                    raider = ModEntityType.ARMORED_RAVAGER.get().create(this.level);
                    if (raider != null){
                        return raider;
                    }
                }
            }
        }
        return raider;
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (MainConfig.ShriekObeliskRaid.get()) {
            if (this.level.getGameTime() % 20 == 0) {
                if (!this.isStopped() && !this.isVictory()) {
                    int cost = MainConfig.ShriekObeliskCost.get() * this.getBadOmenLevel();
                    if (BlockFinder.findIllagerWard(this.level, this.center, cost)) {
                        this.stop();
                    }
                }
            }
        }
    }
}
