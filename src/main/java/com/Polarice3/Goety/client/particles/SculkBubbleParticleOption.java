package com.Polarice3.Goety.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

public class SculkBubbleParticleOption implements ParticleOptions {
    public static final Codec<SculkBubbleParticleOption> CODEC = RecordCodecBuilder.create((p_235978_) -> {
        return p_235978_.group(PositionSource.CODEC.fieldOf("destination").forGetter((p_235982_) -> {
            return p_235982_.destination;
        }), Codec.INT.fieldOf("arrival_in_ticks").forGetter((p_235980_) -> {
            return p_235980_.arrivalInTicks;
        })).apply(p_235978_, SculkBubbleParticleOption::new);
    });
    public static final Deserializer<SculkBubbleParticleOption> DESERIALIZER = new Deserializer<SculkBubbleParticleOption>() {
        public SculkBubbleParticleOption fromCommand(ParticleType<SculkBubbleParticleOption> p_175859_, StringReader p_175860_) throws CommandSyntaxException {
            p_175860_.expect(' ');
            float f = (float)p_175860_.readDouble();
            p_175860_.expect(' ');
            float f1 = (float)p_175860_.readDouble();
            p_175860_.expect(' ');
            float f2 = (float)p_175860_.readDouble();
            p_175860_.expect(' ');
            int i = p_175860_.readInt();
            BlockPos blockpos = BlockPos.containing((double)f, (double)f1, (double)f2);
            return new SculkBubbleParticleOption(new BlockPositionSource(blockpos), i);
        }

        public SculkBubbleParticleOption fromNetwork(ParticleType<SculkBubbleParticleOption> p_175862_, FriendlyByteBuf p_175863_) {
            PositionSource positionsource = PositionSourceType.fromNetwork(p_175863_);
            int i = p_175863_.readVarInt();
            return new SculkBubbleParticleOption(positionsource, i);
        }
    };
    private final PositionSource destination;
    private final int arrivalInTicks;

    public SculkBubbleParticleOption(PositionSource p_235975_, int p_235976_) {
        this.destination = p_235975_;
        this.arrivalInTicks = p_235976_;
    }

    public void writeToNetwork(FriendlyByteBuf p_175854_) {
        PositionSourceType.toNetwork(this.destination, p_175854_);
        p_175854_.writeVarInt(this.arrivalInTicks);
    }

    public String writeToString() {
        Vec3 vec3 = this.destination.getPosition((Level)null).get();
        double d0 = vec3.x();
        double d1 = vec3.y();
        double d2 = vec3.z();
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), d0, d1, d2, this.arrivalInTicks);
    }

    public ParticleType<SculkBubbleParticleOption> getType() {
        return ModParticleTypes.SCULK_BUBBLE.get();
    }

    public PositionSource getDestination() {
        return this.destination;
    }

    public int getArrivalInTicks() {
        return this.arrivalInTicks;
    }
}
