package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.entities.ally.undead.skeleton.WitherSkeletonServant;
import com.Polarice3.Goety.common.entities.hostile.WitherNecromancer;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Sensor.class)
public class SensorMixin<T extends LivingEntity> {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/sensing/Sensor;doTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;)V", shift = At.Shift.AFTER), method = "tick")
    private void mixinDoTick(ServerLevel serverLevel, T entity, CallbackInfo callbackInfo){
        if (entity instanceof AbstractPiglin piglin){
            Brain<?> brain = piglin.getBrain();

            Optional<Mob> enemyWither = Optional.empty();
            Optional<LivingEntity> zombie = Optional.empty();

            NearestVisibleLivingEntities nearestvisiblelivingentities = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());

            for(LivingEntity livingEntity : nearestvisiblelivingentities.findAll((le) -> true)){
                if (enemyWither.isPresent() || !(livingEntity instanceof WitherSkeletonServant) && !(livingEntity instanceof WitherNecromancer)) {
                    if (zombie.isEmpty() && livingEntity instanceof ZPiglinServant) {
                        zombie = Optional.of(livingEntity);
                    }
                } else {
                    enemyWither = Optional.of((Mob)livingEntity);
                }
            }

            brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, enemyWither);
            brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, zombie);
        }
    }
}
