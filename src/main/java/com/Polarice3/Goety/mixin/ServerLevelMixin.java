package com.Polarice3.Goety.mixin;


import com.Polarice3.Goety.common.effects.GoetyEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraftforge.registries.ForgeRegistries;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

//Stolen from @baguchi:https://github.com/baguchi/EnchantWithMob-Forge/blob/1.20.1/src/main/java/baguchan/enchantwithmob/mixin/ServerLevelMixin.java
@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
    protected ServerLevelMixin(WritableLevelData p_270739_, ResourceKey<Level> p_270683_, RegistryAccess p_270200_, Holder<DimensionType> p_270240_, Supplier<ProfilerFiller> p_270692_, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_) {
        super(p_270739_, p_270683_, p_270200_, p_270240_, p_270692_, p_270904_, p_270470_, p_270248_, p_270466_);
    }

    @Inject(method = "tickNonPassenger", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;tickCount:I", opcode = Opcodes.PUTFIELD), cancellable = true)
    public void tickNonPassenger(Entity instance, CallbackInfo ci) {
        if (instance instanceof Mob mob) {
            if (mob.hasEffect(GoetyEffects.ENCHANTED.get())) {
                ProfilerFiller profilerfiller = this.getProfiler();
                instance.tickCount += 1;
                this.getProfiler().push(() -> {
                    ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(instance.getType());
                    if (location != null) {
                        return location.toString();
                    }
                    return instance.getType().getDescriptionId();
                });
                profilerfiller.incrementCounter("tickNonPassenger");
                instance.tick();
                this.getProfiler().pop();

                for (Entity entity : instance.getPassengers()) {
                    this.tickPassenger(instance, entity);
                }
                ci.cancel();
            }
        }
    }

    @Shadow
    private void tickPassenger(Entity p_8663_, Entity p_8664_) {

    }
}
