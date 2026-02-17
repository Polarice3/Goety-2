package com.Polarice3.Goety.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Locale;

public class ShootIndicatorParticleOption implements ParticleOptions {
    public static final Codec<ShootIndicatorParticleOption> CODEC = RecordCodecBuilder.create((instance) -> instance.group(Codec.INT.fieldOf("delay").forGetter((option) -> option.ownerId)).apply(instance, ShootIndicatorParticleOption::new));
    public static final ParticleOptions.Deserializer<ShootIndicatorParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public ShootIndicatorParticleOption fromCommand(ParticleType<ShootIndicatorParticleOption> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            int $$2 = reader.readInt();
            return new ShootIndicatorParticleOption($$2);
        }

        @Override
        public ShootIndicatorParticleOption fromNetwork(ParticleType<ShootIndicatorParticleOption> type, FriendlyByteBuf byteBuf) {
            return new ShootIndicatorParticleOption(byteBuf.readVarInt());
        }
    };
    private final int ownerId;

    public ShootIndicatorParticleOption(int ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf byteBuf) {
        byteBuf.writeVarInt(this.ownerId);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.ownerId);
    }

    @Override
    public ParticleType<ShootIndicatorParticleOption> getType() {
        return ModParticleTypes.SHOOT_INDICATOR.get();
    }

    public int getOwnerId() {
        return this.ownerId;
    }
}